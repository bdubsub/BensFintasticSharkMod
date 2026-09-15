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
import tfar.bensfintasticsharks.init.ModTags;

import java.util.List;

public class WaterDisturbanceHandler {

    @SubscribeEvent
    public void onDisturbance(WaterDisturbanceEvent event) {
        if (event.getLevel().isClientSide) return;
        ServerLevel level = (ServerLevel) event.getLevel();

        // Config gate — if sensitivity is 0 for this type, skip entirely.
        double sensMult = switch (event.getType()) {
            case LIGHT -> tfar.bensfintasticsharks.config.BfsConfig.COMMON.lightSensitivityMult.get();
            case HEAVY -> tfar.bensfintasticsharks.config.BfsConfig.COMMON.heavySensitivityMult.get();
            case BLOOD -> tfar.bensfintasticsharks.config.BfsConfig.COMMON.bloodSensitivityMult.get();
        };
        if (sensMult <= 0.0) {
            tfar.bensfintasticsharks.debug.BfsDebugManager.recordDisturbanceDecision(level, event,
                    "ignored", "sensitivity_disabled", 0,
                    WaterDisturbanceListeners.sourceKeyCount(level));
            return;
        }

        if (tfar.bensfintasticsharks.config.BfsConfig.COMMON.disturbanceParticlesEnabled.get()) {
            spawnFeedback(level, event);
        }

        double radius = switch (event.getType()) {
            // Tightened from 56/32/24. The old 56-block BLOOD radius pulled in
            // every shark of every species in chunk-load range and turned a
            // single hurt mob into a cross-species feeding frenzy. Players
            // reported "they all form a pack and target everything together"
            // — caused by every nearby shark calling reactToDisturbance(BLOOD,
            // sourceEntity) and locking onto the same victim.
            case BLOOD -> 24.0;
            case HEAVY -> 16.0;
            case LIGHT -> 12.0;
        };

        DisturbanceType ct = disturbanceType(event);

        AABB area = new AABB(event.getSource()).inflate(radius);
        LivingEntity sourceLiving = event.getSourceEntity() instanceof LivingEntity le ? le : null;

        int[] inspected = {0};
        List<LivingEntity> sharks = level.getEntitiesOfClass(LivingEntity.class, area, entity -> {
            if (!entity.isAlive() || !entity.isInWaterOrBubble()
                    || (!(entity instanceof AbstractSharkEntity)
                    && !(entity instanceof WaterAnimal waterAnimal && waterAnimal.getType().is(ModTags.EntityTypes.SHARKS)))) {
                return false;
            }
            if (inspected[0] >= 64) return false;
            inspected[0]++;
            return true;
        });
        for (LivingEntity sharkLike : sharks) {
            if (sharkLike instanceof AbstractSharkEntity shark) {
                shark.reactToDisturbance(event.getSource(), ct, sourceLiving);
                continue;
            }
            if (!(sharkLike instanceof WaterAnimal waterAnimal)) continue;
            switch (ct) {
                case BLOOD -> {
                    if (sourceLiving != null && sourceLiving != waterAnimal && waterAnimal.getTarget() == null) {
                        waterAnimal.setTarget(sourceLiving);
                    }
                }
                case LIGHT, HEAVY -> {
                    float chance = ct == DisturbanceType.LIGHT ? 0.15f : 0.60f;
                    if (waterAnimal.getTarget() == null && waterAnimal.getRandom().nextFloat() < chance) {
                        BrainUtils.setMemory(waterAnimal.getBrain(),
                                MemoryModuleType.WALK_TARGET,
                                new WalkTarget(Vec3.atCenterOf(event.getSource()), 1.0f, 1));
                    }
                }
            }
        }
        tfar.bensfintasticsharks.debug.BfsDebugManager.recordDisturbanceDecision(level, event,
                sharks.isEmpty() ? "ignored" : "alert", sharks.isEmpty() ? "no_eligible_sharks" : "eligible_sharks",
                inspected[0], WaterDisturbanceListeners.sourceKeyCount(level));
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
}
