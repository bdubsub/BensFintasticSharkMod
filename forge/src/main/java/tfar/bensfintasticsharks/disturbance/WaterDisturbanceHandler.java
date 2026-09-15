package tfar.bensfintasticsharks.disturbance;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tslat.smartbrainlib.util.BrainUtils;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.entity.SpeciesSettingsService;
import tfar.bensfintasticsharks.init.ModTags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/** Applies one resolved disturbance policy to each eligible nearby shark. */
public class WaterDisturbanceHandler {

    private static final double MAX_QUERY_RADIUS = 256.0D;
    private static final int MAX_CANDIDATES = 64;
    private static final ConcurrentMap<ReactionKey, Long> LAST_REACTION = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onDisturbance(WaterDisturbanceEvent event) {
        if (event.getLevel().isClientSide || !(event.getLevel() instanceof ServerLevel level)) return;
        long tick = level.getGameTime();

        String sourceFailure = sourceFailure(event, level);
        if (sourceFailure != null) {
            record(level, event, "ignored", sourceFailure, "unavailable", 0, 0,
                    0.0D, 0.0D, 0.0D, 0, 0, false);
            return;
        }

        if (tfar.bensfintasticsharks.config.BfsConfig.COMMON.disturbanceParticlesEnabled.get()) {
            spawnFeedback(level, event);
        }

        AABB area = new AABB(event.getSource()).inflate(MAX_QUERY_RADIUS);
        int[] inspected = {0};
        List<LivingEntity> candidates = new ArrayList<>(level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> {
                    boolean inWater = entity instanceof AbstractSharkEntity shark
                            ? shark.isInWater() : entity.isInWaterOrBubble();
                    boolean eligible = entity instanceof AbstractSharkEntity
                            || entity instanceof WaterAnimal waterAnimal
                            && waterAnimal.getType().is(ModTags.EntityTypes.SHARKS);
                    if (inspected[0] >= MAX_CANDIDATES || !entity.isAlive() || !inWater || !eligible) {
                        return false;
                    }
                    inspected[0]++;
                    return true;
                }));
        candidates.sort(Comparator.<LivingEntity>comparingDouble(entity -> entity.distanceToSqr(Vec3.atCenterOf(event.getSource())))
                .thenComparing(entity -> entity.getUUID().toString()));
        int candidateCount = candidates.size();
        int sourceKeyCount = WaterDisturbanceListeners.sourceKeyCount(level);

        for (int index = 0; index < candidateCount; index++) {
            LivingEntity sharkLike = candidates.get(index);
            DisturbanceSettings.Effective settings = DisturbanceSettings.resolve(sharkLike, event.getSourceKind());
            ReactionKey key = new ReactionKey(level.dimension(), sharkLike.getUUID(), event.getSourceKind());
            Long previous = LAST_REACTION.get(key);
            DisturbanceSettings.Decision decision = DisturbanceSettings.evaluate(settings,
                    event.getStrength(), Math.sqrt(sharkLike.distanceToSqr(Vec3.atCenterOf(event.getSource()))), tick, previous);
            if (decision.accepted()) {
                LAST_REACTION.put(key, tick);
                applyReaction(sharkLike, event, settings);
            }
            record(level, event, decision.outcome(), decision.reason(),
                    speciesOf(sharkLike), candidateCount, sourceKeyCount, settings.radius(),
                    settings.sensitivity(), settings.strength(), settings.effectiveIntervalTicks(),
                    settings.alertTicks(), decision.thresholdAccepted(), settings.revision(),
                    decision.effectiveStrength());
        }
        if (candidates.isEmpty()) {
            record(level, event, "ignored", "no_eligible_sharks", "unavailable", 0, sourceKeyCount,
                    0.0D, 0.0D, 0.0D, 0, 0, false);
        }
    }

    private static String sourceFailure(WaterDisturbanceEvent event, ServerLevel level) {
        if (event.getSourceEntity() != null && event.getSourceEntity().level() != level) {
            return "wrong_dimension";
        }
        if (event.getSourceKind() == WaterDisturbanceEvent.SourceKind.OCCUPIED_BOAT) {
            if (event.getBoat() == null || event.getRider() == null) return "empty_boat";
            if (event.getBoat().level() != level || event.getRider().level() != level) {
                return "wrong_dimension";
            }
            if (!event.getBoat().getPassengers().contains(event.getRider())) return "empty_boat";
            if (event.getStrength() <= 0.0D) return "stationary_boat";
        }
        return null;
    }

    private static void applyReaction(LivingEntity sharkLike, WaterDisturbanceEvent event,
                                      DisturbanceSettings.Effective settings) {
        DisturbanceType type = disturbanceType(event);
        LivingEntity sourceLiving = event.getSourceEntity() instanceof LivingEntity le ? le : null;
        if (sharkLike instanceof AbstractSharkEntity shark) {
            shark.reactToDisturbance(event.getSource(), type, sourceLiving);
            shark.setStateTimer(settings.alertTicks());
            if (settings.reaction() == DisturbanceSettings.Reaction.INVESTIGATE) {
                BrainUtils.setMemory(shark.getBrain(), MemoryModuleType.WALK_TARGET,
                        new WalkTarget(Vec3.atCenterOf(event.getSource()), 1.0f, 2));
                if (shark.getSharkState() == AbstractSharkEntity.SharkState.IDLE) {
                    shark.setSharkState(AbstractSharkEntity.SharkState.CURIOUS);
                }
            }
            return;
        }
        if (!(sharkLike instanceof WaterAnimal waterAnimal)) return;
        if (type == DisturbanceType.BLOOD && sourceLiving != null
                && sourceLiving != waterAnimal && waterAnimal.getTarget() == null) {
            waterAnimal.setTarget(sourceLiving);
        } else if (waterAnimal.getTarget() == null) {
            BrainUtils.setMemory(waterAnimal.getBrain(), MemoryModuleType.WALK_TARGET,
                    new WalkTarget(Vec3.atCenterOf(event.getSource()), 1.0f, 1));
        }
    }

    private static void record(ServerLevel level, WaterDisturbanceEvent event, String outcome,
                               String reason, String species, int candidateCount, int sourceKeyCount,
                               double radius, double sensitivity, double sourceStrength,
                               int sourceInterval, int alertTicks, boolean threshold) {
        record(level, event, outcome, reason, species, candidateCount, sourceKeyCount, radius,
                sensitivity, sourceStrength, sourceInterval, alertTicks, threshold,
                SpeciesSettingsService.revision(), event.getStrength() * sourceStrength * sensitivity);
    }

    private static void record(ServerLevel level, WaterDisturbanceEvent event, String outcome,
                               String reason, String species, int candidateCount, int sourceKeyCount,
                               double radius, double sensitivity, double sourceStrength,
                               int sourceInterval, int alertTicks, boolean threshold,
                               long settingsRevision, double effectiveStrength) {
        tfar.bensfintasticsharks.debug.BfsDebugManager.recordDisturbanceDecision(level, event,
                outcome, reason, candidateCount, sourceKeyCount, species, settingsRevision,
                radius, sensitivity, sourceStrength, sourceInterval, alertTicks, threshold,
                effectiveStrength);
    }

    private static String speciesOf(LivingEntity entity) {
        var profile = tfar.bensfintasticsharks.entity.SpeciesBehaviorProfile.forEntity(entity);
        return profile == null ? "unregistered" : profile.id();
    }

    private static DisturbanceType disturbanceType(WaterDisturbanceEvent event) {
        return switch (event.getSourceKind()) {
            case DAMAGE -> DisturbanceType.BLOOD;
            case ATTACK, BLOCK_BREAK, FALL, WATER_JUMP -> DisturbanceType.HEAVY;
            case SWIM_SPRINT, PROJECTILE, WATER_ENTRY, OCCUPIED_BOAT -> DisturbanceType.LIGHT;
        };
    }

    private void spawnFeedback(ServerLevel level, WaterDisturbanceEvent event) {
        double x = event.getSource().getX() + 0.5;
        double y = event.getSource().getY() + 0.5;
        double z = event.getSource().getZ() + 0.5;
        switch (event.getType()) {
            case LIGHT -> level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, x, y, z, 3, 0.2, 0.2, 0.2, 0.0);
            case HEAVY -> {
                level.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, x, y, z, 6, 0.3, 0.3, 0.3, 0.0);
                level.sendParticles(ParticleTypes.SPLASH, x, y, z, 3, 0.3, 0.1, 0.3, 0.0);
            }
            case BLOOD -> {
                level.sendParticles(ParticleTypes.DAMAGE_INDICATOR, x, y, z, 8, 0.3, 0.3, 0.3, 0.0);
                level.sendParticles(ParticleTypes.SPLASH, x, y, z, 3, 0.3, 0.1, 0.3, 0.0);
                if (tfar.bensfintasticsharks.config.BfsConfig.COMMON.disturbanceAudioEnabled.get()) {
                    level.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GENERIC_SPLASH,
                            SoundSource.NEUTRAL, 0.3f, 0.7f);
                }
            }
        }
    }

    static void clearEntity(ServerLevel level, UUID entity) {
        LAST_REACTION.keySet().removeIf(key -> key.dimension().equals(level.dimension())
                && key.shark().equals(entity));
    }

    static void clearLevel(ServerLevel level) {
        LAST_REACTION.keySet().removeIf(key -> key.dimension().equals(level.dimension()));
    }

    private record ReactionKey(net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimension,
                               UUID shark, WaterDisturbanceEvent.SourceKind sourceKind) {
    }
}
