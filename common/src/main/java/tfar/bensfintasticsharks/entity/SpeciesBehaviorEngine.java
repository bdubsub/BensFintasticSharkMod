package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

import static tfar.bensfintasticsharks.init.ModTags.EntityTypes.APEX_PREDATOR;

/**
 * Bounded server-side policy shared by the non-combat species.
 *
 * <p>Sharks keep their dedicated combat state machine. This engine owns only one low-priority
 * intent at a time for the other species: escape, breathe, feed, shelter, social travel or
 * profile-specific bottom holding. Existing actions remain authoritative when a walk target is
 * already present, so this cannot become a second movement writer.</p>
 */
public final class SpeciesBehaviorEngine {

    private static final int SCAN_INTERVAL_TICKS = 20;
    private static final int MAX_CANDIDATES = 32;
    private static final int MAX_NEIGHBORS = 8;
    private static final int ESCAPE_DISTANCE = 8;

    private SpeciesBehaviorEngine() {}

    /**
     * Vanilla fish do not use SmartBrainLib, so their policy has a small movement-control adapter
     * rather than a second brain. It only owns the bounded threat and school destinations; the
     * vanilla fish movement integrator still applies the final motion and pitch.
     */
    public static void tickFish(AbstractFish fish) {
        if (fish.level().isClientSide || !fish.isAlive()) return;
        // Let a newly created fish settle and remain discoverable before its first policy scan.
        // This also prevents a school route or threat response from racing the spawn and
        // encounter triggers that observe the entity on the same server tick.
        if (fish.tickCount < SCAN_INTERVAL_TICKS) return;
        SpeciesBehaviorProfile.Profile profile = SpeciesBehaviorProfile.forEntity(fish);
        if (profile == null || fish.tickCount % SCAN_INTERVAL_TICKS != Math.floorMod(fish.getId(), SCAN_INTERVAL_TICKS)) return;

        List<LivingEntity> threats = boundedLiving(fish, fish.getBoundingBox().inflate(profile.scanRadius()),
                other -> other != fish && other.isAlive() && other.getType().is(APEX_PREDATOR)
                        && !(other instanceof Player player && (player.isCreative() || player.isSpectator())));
        LivingEntity threat = threats.stream().min(Comparator.comparingDouble(fish::distanceToSqr)).orElse(null);
        if (threat != null) {
            Vec3 away = fish.position().subtract(threat.position());
            if (away.lengthSqr() < 1.0e-4) away = new Vec3(1, 0, 0);
            away = away.normalize().scale(Math.min(8.0, profile.scanRadius()));
            Vec3 target = fish.position().add(away);
            fish.getNavigation().moveTo(target.x, target.y, target.z, 1.25D);
            return;
        }
        if (!profile.social() || !fish.getNavigation().isDone()) return;
        List<LivingEntity> school = boundedLiving(fish, fish.getBoundingBox().inflate(profile.scanRadius()),
                other -> other != fish && other.getType() == fish.getType() && other.isAlive())
                .stream().limit(MAX_NEIGHBORS).toList();
        if (!school.isEmpty()) {
            Vec3 center = school.stream().map(Entity::position).reduce(Vec3.ZERO, Vec3::add)
                    .scale(1.0 / school.size());
            Vec3 separation = fish.position().subtract(center);
            Vec3 target = center.add(separation.lengthSqr() < 1.0 ? new Vec3(1.5, 0, 0) : separation.normalize().scale(2.5));
            fish.getNavigation().moveTo(target.x, target.y, target.z, 0.8D);
        }
    }

    public static void tick(SmartWaterAnimal<?> entity) {
        if (entity.level().isClientSide || !entity.isAlive()) return;
        SpeciesBehaviorProfile.Profile profile = SpeciesBehaviorProfile.forEntity(entity);
        if (profile == null || profile.family() == SpeciesBehaviorProfile.Family.SHARK) return;

        entity.tickBfsBehaviorAction();
        entity.tickBfsBehaviorMemory();
        if (entity.hasBfsBehaviorAction()
                && (entity.hasLostBfsBehaviorTarget() || entity.hasExpiredBfsBehaviorMemory())) {
            clearOwnedRoute(entity);
        }
        if (entity.hasExpiredBfsBehaviorAction()) {
            BrainUtils.clearMemory(entity.getBrain(), MemoryModuleType.WALK_TARGET);
            entity.clearBfsBehaviorAction();
        }
        if (entity.getBfsBehaviorScanCooldown() > 0) {
            entity.setBfsBehaviorScanCooldown(entity.getBfsBehaviorScanCooldown() - 1);
            return;
        }
        int stagger = Math.floorMod(entity.getId(), SCAN_INTERVAL_TICKS);
        entity.setBfsBehaviorScanCooldown(SCAN_INTERVAL_TICKS + stagger);
        if (!entity.isInWaterOrBubble() && profile.family() != SpeciesBehaviorProfile.Family.TURTLE
                && profile.family() != SpeciesBehaviorProfile.Family.MAMMAL) {
            clearOwnedRoute(entity);
            return;
        }

        LivingEntity threat = findThreat(entity, profile);
        if (threat != null && profile.threatResponse() != SpeciesBehaviorProfile.ThreatResponse.NONE) {
            Vec3 escape = findEscape(entity, threat, profile.scanRadius());
            if (escape != null && claimRoute(entity, "escape", profile.actionTimeoutTicks(), escape,
                    1.35f, threat, profile.memoryTicks())) return;
        }

        if (profile.needsSurface() && entity.getAirSupply() < 120) {
            Vec3 surface = findSurface(entity);
            if (surface != null && claimRoute(entity, "breathe", profile.actionTimeoutTicks(), surface,
                    1.0f, null, 0)) return;
        }

        if (profile.social() && claimSocialRoute(entity, profile)) return;

        if (profile.foodMode() != SpeciesBehaviorProfile.FoodMode.PASSIVE
                && profile.foodMode() != SpeciesBehaviorProfile.FoodMode.PLANKTON) {
            TargetRoute food = findFood(entity, profile);
            if (food != null && claimRoute(entity, "feed", profile.actionTimeoutTicks(), food.position(),
                    0.9f, food.target(), profile.memoryTicks())) return;
        }

        if (profile.habitat() == SpeciesBehaviorProfile.Habitat.SEAFLOOR
                && (profile.locomotion() == SpeciesBehaviorProfile.Locomotion.BOTTOM_WALK
                || profile.locomotion() == SpeciesBehaviorProfile.Locomotion.BOTTOM_GLIDE
                || profile.locomotion() == SpeciesBehaviorProfile.Locomotion.BOTTOM_GRAZE)) {
            Vec3 floor = findFloorRoute(entity);
            if (floor != null) claimRoute(entity, "habitat", profile.actionTimeoutTicks(), floor,
                    0.65f, null, 0);
        }
    }

    private static boolean claimSocialRoute(SmartWaterAnimal<?> entity, SpeciesBehaviorProfile.Profile profile) {
        if (hasAnyWalkTarget(entity)) return false;
        List<LivingEntity> neighbors = nearby(entity, profile.scanRadius(),
                other -> other.getType() == entity.getType() && other != entity && other.isAlive())
                .stream().limit(MAX_NEIGHBORS).toList();
        if (neighbors.isEmpty()) return false;
        Vec3 center = neighbors.stream().map(Entity::position).reduce(Vec3.ZERO, Vec3::add)
                .scale(1.0 / neighbors.size());
        Vec3 separation = entity.position().subtract(center);
        if (separation.lengthSqr() < 1.0) {
            separation = entity.position().subtract(neighbors.get(0).position());
        }
        Vec3 target = center.add(separation.normalize().scale(2.5));
        return claimRoute(entity, "social", profile.actionTimeoutTicks(), target, 0.85f,
                neighbors.get(0), profile.memoryTicks());
    }

    @Nullable
    private static LivingEntity findThreat(SmartWaterAnimal<?> entity, SpeciesBehaviorProfile.Profile profile) {
        if (profile.threatResponse() == SpeciesBehaviorProfile.ThreatResponse.NONE) return null;
        return nearby(entity, profile.scanRadius(), other -> other != entity && other.isAlive()
                && other.getType().is(APEX_PREDATOR)
                && !(other instanceof Player player && (player.isCreative() || player.isSpectator()))
                && !visualThreatHidden(entity, other))
                .stream().min(Comparator.comparingDouble(entity::distanceToSqr)).orElse(null);
    }

    private static boolean visualThreatHidden(SmartWaterAnimal<?> entity, LivingEntity threat) {
        if (!(entity instanceof OctopusCamouflageHost)
                || !(entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return false;
        }
        return OctopusInkCloudRegistry.obscuresVisualRay(serverLevel, entity.getUUID(),
                threat.getEyePosition());
    }

    @Nullable
    private static TargetRoute findFood(SmartWaterAnimal<?> entity, SpeciesBehaviorProfile.Profile profile) {
        List<LivingEntity> candidates = nearby(entity, profile.scanRadius(), other ->
                other != entity && other.isAlive() && isFood(entity, other, profile));
        LivingEntity prey = candidates.stream().min(Comparator.comparingDouble(entity::distanceToSqr)).orElse(null);
        if (prey != null) return new TargetRoute(prey.position(), prey);
        if (profile.foodMode() == SpeciesBehaviorProfile.FoodMode.BENTHIC_INVERTEBRATE
                || profile.foodMode() == SpeciesBehaviorProfile.FoodMode.ALGAE) {
            BlockPos block = findFoodBlock(entity, profile);
            return block == null ? null : new TargetRoute(Vec3.atCenterOf(block), null);
        }
        return findNearbyFoodItem(entity);
    }

    private static boolean isFood(SmartWaterAnimal<?> hunter, LivingEntity candidate,
                                  SpeciesBehaviorProfile.Profile profile) {
        if (candidate instanceof Player || candidate.getType().is(APEX_PREDATOR)) return false;
        if (candidate.getType() == hunter.getType()) return false;
        String id = candidate.getType().builtInRegistryHolder().key().location().getPath();
        return switch (profile.foodMode()) {
            case SMALL_FISH -> id.contains("cod") || id.contains("salmon") || id.contains("squid");
            case FISH_AND_INVERTEBRATE -> id.contains("cod") || id.contains("salmon") || id.contains("squid")
                    || id.contains("stingray") || id.contains("lobster");
            case BENTHIC_INVERTEBRATE -> id.contains("lobster") || id.contains("squid")
                    || id.contains("jellyfish") || id.contains("stingray");
            default -> false;
        };
    }

    @Nullable
    private static TargetRoute findNearbyFoodItem(SmartWaterAnimal<?> entity) {
        List<ItemEntity> items = new ArrayList<>(MAX_CANDIDATES);
        entity.level().getEntities(EntityTypeTest.forClass(ItemEntity.class),
                entity.getBoundingBox().inflate(8.0), ItemEntity::isAlive, items, MAX_CANDIDATES);
        ItemEntity item = items.stream().findFirst().orElse(null);
        return item == null ? null : new TargetRoute(item.position(), item);
    }

    @Nullable
    private static BlockPos findFoodBlock(SmartWaterAnimal<?> entity, SpeciesBehaviorProfile.Profile profile) {
        BlockPos origin = entity.blockPosition();
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                for (int dy = -3; dy <= 1; dy++) {
                    BlockPos candidate = origin.offset(dx, dy, dz);
                    BlockState state = entity.level().getBlockState(candidate);
                    boolean algae = state.is(tfar.bensfintasticsharks.init.ModTags.Blocks.ALGAE)
                            || state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS);
                    if (profile.foodMode() == SpeciesBehaviorProfile.FoodMode.ALGAE && algae) return candidate;
                    if (profile.foodMode() == SpeciesBehaviorProfile.FoodMode.BENTHIC_INVERTEBRATE
                            && state.isSolidRender(entity.level(), candidate)
                            && entity.level().getFluidState(candidate.above()).is(FluidTags.WATER)) return candidate.above();
                }
            }
        }
        return null;
    }

    @Nullable
    private static Vec3 findEscape(SmartWaterAnimal<?> entity, LivingEntity threat, int radius) {
        Vec3 away = entity.position().subtract(threat.position());
        Vec3 horizontal = new Vec3(away.x, 0, away.z);
        if (horizontal.lengthSqr() < 1.0e-4) horizontal = new Vec3(1, 0, 0);
        horizontal = horizontal.normalize();
        double vertical = Math.max(-2.0, Math.min(2.0, away.y * 0.25));
        double[] turns = {0, Math.PI / 4, -Math.PI / 4, Math.PI / 2, -Math.PI / 2};
        for (double turn : turns) {
            Vec3 direction = new Vec3(horizontal.x * Math.cos(turn) - horizontal.z * Math.sin(turn),
                    0, horizontal.x * Math.sin(turn) + horizontal.z * Math.cos(turn));
            Vec3 candidate = entity.position().add(direction.scale(Math.min(ESCAPE_DISTANCE, radius))).add(0, vertical, 0);
            if (entity.level().getFluidState(BlockPos.containing(candidate)).is(FluidTags.WATER)
                    && reachable(entity, candidate)) return candidate;
        }
        return null;
    }

    @Nullable
    private static Vec3 findSurface(SmartWaterAnimal<?> entity) {
        int top = Math.min(entity.level().getSeaLevel(), entity.level().getMaxBuildHeight() - 2);
        for (int y = entity.blockPosition().getY(); y <= top; y++) {
            BlockPos pos = new BlockPos(entity.blockPosition().getX(), y, entity.blockPosition().getZ());
            if (!entity.level().getFluidState(pos).is(FluidTags.WATER)
                    && entity.level().getFluidState(pos.below()).is(FluidTags.WATER)
                    && reachable(entity, Vec3.atCenterOf(pos))) return Vec3.atCenterOf(pos);
        }
        return null;
    }

    @Nullable
    private static Vec3 findFloorRoute(SmartWaterAnimal<?> entity) {
        BlockPos origin = entity.blockPosition();
        for (int dy = 0; dy >= -8; dy--) {
            BlockPos pos = origin.offset(0, dy, 0);
            if (entity.level().getFluidState(pos).is(FluidTags.WATER)
                    && entity.level().getBlockState(pos.below()).isSolidRender(entity.level(), pos.below())) {
                return Vec3.atCenterOf(pos);
            }
        }
        return null;
    }

    private static boolean claimRoute(SmartWaterAnimal<?> entity, String action, int timeout,
                                      Vec3 target, float speed, @Nullable Entity trackedTarget,
                                      int memoryTicks) {
        if (hasAnyWalkTarget(entity)) return false;
        BrainUtils.setMemory(entity.getBrain(), MemoryModuleType.WALK_TARGET, new WalkTarget(target, speed, 1));
        entity.beginBfsBehaviorAction(action, timeout);
        entity.rememberBfsBehaviorTarget(trackedTarget, memoryTicks);
        return true;
    }

    private static boolean hasAnyWalkTarget(SmartWaterAnimal<?> entity) {
        return BrainUtils.hasMemory(entity.getBrain(), MemoryModuleType.WALK_TARGET)
                && !entity.hasExpiredBfsBehaviorAction();
    }

    private static void clearOwnedRoute(SmartWaterAnimal<?> entity) {
        if (entity.hasBfsBehaviorAction() || entity.hasBfsBehaviorTarget()) {
            BrainUtils.clearMemory(entity.getBrain(), MemoryModuleType.WALK_TARGET);
            entity.clearBfsBehaviorAction();
        }
    }

    private static boolean reachable(SmartWaterAnimal<?> entity, Vec3 target) {
        var path = entity.getNavigation().createPath(BlockPos.containing(target), 0);
        return path != null && path.canReach();
    }

    private static List<LivingEntity> nearby(SmartWaterAnimal<?> entity, int radius,
                                             java.util.function.Predicate<LivingEntity> filter) {
        return boundedLiving(entity, entity.getBoundingBox().inflate(radius), filter);
    }

    private static List<LivingEntity> boundedLiving(Entity source, AABB area,
                                                    java.util.function.Predicate<LivingEntity> filter) {
        List<LivingEntity> result = new ArrayList<>(MAX_CANDIDATES);
        source.level().getEntities(EntityTypeTest.forClass(LivingEntity.class), area, filter,
                result, MAX_CANDIDATES);
        return List.copyOf(result);
    }

    private record TargetRoute(Vec3 position, @Nullable Entity target) {
    }
}
