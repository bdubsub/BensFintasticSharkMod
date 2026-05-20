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
        if (sensMult <= 0.0) return;

        if (tfar.bensfintasticsharks.config.BfsConfig.COMMON.disturbanceParticlesEnabled.get()) {
            spawnFeedback(level, event);
        }

        double radius = switch (event.getType()) {
            case BLOOD -> 56.0;
            case HEAVY -> 32.0;
            case LIGHT -> 24.0;
        };

        DisturbanceType ct = switch (event.getType()) {
            case LIGHT -> DisturbanceType.LIGHT;
            case HEAVY -> DisturbanceType.HEAVY;
            case BLOOD -> DisturbanceType.BLOOD;
        };

        AABB area = new AABB(event.getSource()).inflate(radius);
        LivingEntity sourceLiving = event.getSourceEntity() instanceof LivingEntity le ? le : null;

        // 1) New-style sharks react via state machine
        List<AbstractSharkEntity> sharks = level.getEntitiesOfClass(AbstractSharkEntity.class, area,
                shark -> shark.isAlive() && shark.isInWaterOrBubble());
        for (AbstractSharkEntity shark : sharks) {
            shark.reactToDisturbance(event.getSource(), ct, sourceLiving);
        }

        // 2) Legacy alpha sharks react via target/walk-target nudges
        List<WaterAnimal> legacySharks = level.getEntitiesOfClass(WaterAnimal.class, area,
                e -> e.isAlive() && e.isInWaterOrBubble() && e.getType().is(ModTags.EntityTypes.SHARKS)
                        && !(e instanceof AbstractSharkEntity));
        for (WaterAnimal sharkLike : legacySharks) {
            switch (ct) {
                case BLOOD -> {
                    if (sourceLiving != null && sourceLiving != sharkLike && sharkLike.getTarget() == null) {
                        sharkLike.setTarget(sourceLiving);
                    }
                }
                case LIGHT, HEAVY -> {
                    float chance = ct == DisturbanceType.LIGHT ? 0.15f : 0.60f;
                    if (sharkLike.getTarget() == null && sharkLike.getRandom().nextFloat() < chance) {
                        BrainUtils.setMemory(sharkLike.getBrain(),
                                MemoryModuleType.WALK_TARGET,
                                new WalkTarget(Vec3.atCenterOf(event.getSource()), 1.0f, 1));
                    }
                }
            }
        }
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
