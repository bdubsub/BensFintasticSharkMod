package tfar.bensfintasticsharks.fishing;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.Mth;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.debug.BfsDebugManager;
import tfar.bensfintasticsharks.debug.FishingCatchTrace;

import java.util.List;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class FishingCatchDelivery {

    private static final String DELIVERED_TAG = "bfs_fishing_delivery_complete";
    private static final String ATTEMPTED_TAG = "bfs_fishing_delivery_attempted";
    private static final AtomicBoolean FAILURE_REPORTED = new AtomicBoolean();
    private static final Map<FishingHook, AttemptContext> ATTEMPTS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<Entity, ReelPath> ACTIVE_REELS = new IdentityHashMap<>();
    private static final int MIN_REEL_TICKS = 8;
    private static final int MAX_REEL_TICKS = 24;

    private FishingCatchDelivery() {
    }

    public static void rememberAttempt(FishingHook hook, ItemStack rod, boolean live, boolean replace,
                                       String originalItem, String lootTable) {
        if (hook.getPlayerOwner() instanceof ServerPlayer player
                && hook.level() instanceof ServerLevel level && isCurrentCatch(player, hook, level)) {
            ATTEMPTS.put(hook, new AttemptContext(rod.copy(), rod, live, replace, originalItem, lootTable));
        }
    }

    public static void onItemFished(ItemFishedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(event.getHookEntity().level() instanceof ServerLevel level)) {
            return;
        }

        FishingHook hook = event.getHookEntity();
        AttemptContext context = ATTEMPTS.remove(hook);
        boolean live = context == null ? BfsConfig.COMMON.fishEntities.get() : context.live();
        FishingCatchTrace trace = BfsDebugManager.beginFishing(event,
                context == null ? fallbackRod(player) : context.actualRod(),
                context == null ? "unavailable:no_loot_context" : context.originalItem(),
                context == null ? "unavailable:no_loot_context" : context.lootTable(),
                context == null ? BfsConfig.COMMON.replaceVanillaMobs.get() : context.replace(), live);
        try {
            deliver(event, player, level, hook, context, live, trace);
        } finally {
            if (trace != null) {
                trace.close();
            }
        }
    }

    private static void deliver(ItemFishedEvent event, ServerPlayer player, ServerLevel level, FishingHook hook,
                                AttemptContext context, boolean live, FishingCatchTrace trace) {
        ItemStack caughtFish = FishingCatchPolicy.onlySupportedFishingFish(event.getDrops());
        if (caughtFish == null) {
            if (trace != null) trace.outcome("unmodified_loot_passthrough");
            return;
        }
        boolean previouslyCancelled = event.isCanceled();
        event.setCanceled(true);
        if (hook.getPersistentData().getBoolean(ATTEMPTED_TAG)
                || hook.getPersistentData().getBoolean(DELIVERED_TAG)) {
            if (trace != null) trace.outcome("attempt_already_reserved");
            return;
        }

        hook.getPersistentData().putBoolean(ATTEMPTED_TAG, true);
        if (previouslyCancelled || !isCurrentCatch(player, hook, level)) {
            if (trace != null) trace.outcome(previouslyCancelled ? "previously_cancelled" : "invalid_hook_or_owner");
            return;
        }
        EntityType<? extends Mob> fishType = FishingCatchPolicy.entityTypeFor(caughtFish);
        if (fishType == null) {
            if (trace != null) trace.outcome("missing_entity_mapping");
            reportFailure(caughtFish, "no entity mapping");
            return;
        }

        Entity delivery;
        if (live) {
            Mob fish = fishType.create(level);
            if (fish == null) {
                if (trace != null) trace.outcome("entity_creation_failed");
                reportFailure(caughtFish, "entity construction failed");
                return;
            }
            fish.moveTo(hook.getX(), hook.getY(), hook.getZ(), player.getYRot(), player.getXRot());
            delivery = fish;
        } else {
            delivery = new ItemEntity(level, hook.getX(), hook.getY(), hook.getZ(), caughtFish.copy());
        }
        Vec3 initialImpulse = reelImpulse(player, hook.position());
        delivery.setDeltaMovement(initialImpulse);
        boolean accepted = level.addFreshEntity(delivery);
        if (trace != null) trace.delivery(delivery, live, accepted);
        if (!accepted) {
            if (trace != null) trace.outcome("insertion_rejected");
            reportFailure(caughtFish, "delivery insertion was rejected");
            return;
        }
        if (!isCurrentCatch(player, hook, level)) {
            delivery.discard();
            if (trace != null) trace.outcome("hook_invalidated_after_insertion");
            return;
        }

        beginReel(delivery, player);

        hook.getPersistentData().putBoolean(DELIVERED_TAG, true);
        ItemStack rod = context != null ? context.rod() : fallbackRod(player);
        CriteriaTriggers.FISHING_ROD_HOOKED.trigger(player, rod, hook,
                List.of(caughtFish.copy()));
        level.broadcastEntityEvent(hook, (byte) 31);
        int experience = level.getRandom().nextInt(6) + 1;
        boolean xpAccepted = level.addFreshEntity(new ExperienceOrb(level, player.getX(), player.getY() + 0.5D,
                player.getZ() + 0.5D, experience));
        player.awardStat(Stats.FISH_CAUGHT, 1);
        if (trace != null) trace.committed(experience, xpAccepted);
    }

    /**
     * Owns the short return path after a catch. Vanilla's one-frame impulse can stop a live
     * fish at a wall, so the committed delivery follows an eased arc to the angler's feet and
     * restores ordinary physics when it arrives.
     */
    private static void beginReel(Entity delivery, ServerPlayer player) {
        Vec3 start = delivery.position();
        Vec3 target = findLandingTarget(delivery, player);
        double horizontalDistance = Math.hypot(target.x - start.x, target.z - start.z);
        int duration = Mth.clamp((int) Math.ceil(horizontalDistance * 1.5D),
                MIN_REEL_TICKS, MAX_REEL_TICKS);
        double arcHeight = Mth.clamp(1.0D + horizontalDistance * 0.35D, 1.5D, 5.0D);
        ReelPath path = new ReelPath(player, start, target, arcHeight, duration,
                delivery.isNoGravity(), delivery.noPhysics);
        ACTIVE_REELS.put(delivery, path);
        delivery.setNoGravity(true);
        delivery.noPhysics = true;
    }

    private static Vec3 findLandingTarget(Entity delivery, ServerPlayer player) {
        Vec3 feet = player.position().add(0.0D, 0.1D, 0.0D);
        double[][] offsets = {
                {0.0D, 0.0D}, {0.45D, 0.0D}, {-0.45D, 0.0D},
                {0.0D, 0.45D}, {0.0D, -0.45D}, {0.32D, 0.32D},
                {-0.32D, 0.32D}, {0.32D, -0.32D}, {-0.32D, -0.32D}
        };
        for (double[] offset : offsets) {
            Vec3 candidate = feet.add(offset[0], 0.0D, offset[1]);
            AABB movedBox = delivery.getBoundingBox().move(candidate.subtract(delivery.position()));
            if (player.level().noCollision(delivery, movedBox)) {
                return candidate;
            }
        }
        return feet;
    }

    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || ACTIVE_REELS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Entity, ReelPath>> iterator = ACTIVE_REELS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Entity, ReelPath> entry = iterator.next();
            Entity delivery = entry.getKey();
            ReelPath path = entry.getValue();
            ServerPlayer player = path.player;
            if (delivery.isRemoved() || player.isRemoved() || !player.isAlive()
                    || delivery.level() != player.level()) {
                restorePhysics(delivery, path);
                iterator.remove();
                continue;
            }

            int nextTick = ++path.age;
            double progress = Math.min(1.0D, nextTick / (double) path.durationTicks);
            double eased = progress * progress * (3.0D - 2.0D * progress);
            Vec3 linear = path.start.lerp(path.target, eased);
            Vec3 next = new Vec3(linear.x, linear.y + Math.sin(Math.PI * progress) * path.arcHeight,
                    linear.z);
            orientAlong(delivery, next.subtract(delivery.position()));
            delivery.setPos(next.x, next.y, next.z);
            delivery.setDeltaMovement(Vec3.ZERO);

            if (progress >= 1.0D) {
                delivery.setPos(path.target.x, path.target.y, path.target.z);
                delivery.setDeltaMovement(Vec3.ZERO);
                restorePhysics(delivery, path);
                iterator.remove();
            }
        }
    }

    public static void onServerStopping(ServerStoppingEvent event) {
        ACTIVE_REELS.forEach(FishingCatchDelivery::restorePhysics);
        ACTIVE_REELS.clear();
    }

    private static void restorePhysics(Entity delivery, ReelPath path) {
        delivery.setNoGravity(path.previousNoGravity);
        delivery.noPhysics = path.previousNoPhysics;
    }

    private static void orientAlong(Entity delivery, Vec3 movement) {
        if (movement.lengthSqr() < 1.0E-8D) {
            return;
        }
        float yaw = (float) (Mth.atan2(movement.z, movement.x) * Mth.RAD_TO_DEG) - 90.0F;
        float pitch = (float) (Mth.atan2(-movement.y, Math.hypot(movement.x, movement.z)) * Mth.RAD_TO_DEG);
        delivery.setYRot(yaw);
        delivery.setXRot(Mth.clamp(pitch, -90.0F, 90.0F));
    }

    private static ItemStack fallbackRod(ServerPlayer player) {
        return player.getMainHandItem().canPerformAction(net.minecraftforge.common.ToolActions.FISHING_ROD_CAST)
                ? player.getMainHandItem() : player.getOffhandItem();
    }

    private record AttemptContext(ItemStack rod, ItemStack actualRod, boolean live, boolean replace,
                                  String originalItem, String lootTable) {
    }

    private static boolean isCurrentCatch(ServerPlayer player, FishingHook hook, ServerLevel level) {
        return !hook.isRemoved() && !player.isRemoved() && player.isAlive()
                && player.fishing == hook && hook.getPlayerOwner() == player && player.level() == level
                && Double.isFinite(hook.getX()) && Double.isFinite(hook.getY())
                && Double.isFinite(hook.getZ()) && level.hasChunkAt(hook.blockPosition());
    }

    private static Vec3 reelImpulse(ServerPlayer player, Vec3 hookPosition) {
        double x = player.getX() - hookPosition.x;
        double y = player.getY() - hookPosition.y;
        double z = player.getZ() - hookPosition.z;
        double distance = Math.sqrt(x * x + y * y + z * z);
        return new Vec3(x * 0.1D, y * 0.1D + Math.sqrt(distance) * 0.08D, z * 0.1D);
    }

    private static final class ReelPath {
        private final ServerPlayer player;
        private final Vec3 start;
        private final Vec3 target;
        private final double arcHeight;
        private final int durationTicks;
        private final boolean previousNoGravity;
        private final boolean previousNoPhysics;
        private int age;

        private ReelPath(ServerPlayer player, Vec3 start, Vec3 target, double arcHeight, int durationTicks,
                         boolean previousNoGravity, boolean previousNoPhysics) {
            this.player = player;
            this.start = start;
            this.target = target;
            this.arcHeight = arcHeight;
            this.durationTicks = durationTicks;
            this.previousNoGravity = previousNoGravity;
            this.previousNoPhysics = previousNoPhysics;
        }
    }

    private static void reportFailure(ItemStack caughtFish, String reason) {
        if (FAILURE_REPORTED.compareAndSet(false, true)) {
            BensFintasticSharks.LOG.warn("BFS fishing delivery failed for {} because {}.", caughtFish, reason);
        }
    }
}
