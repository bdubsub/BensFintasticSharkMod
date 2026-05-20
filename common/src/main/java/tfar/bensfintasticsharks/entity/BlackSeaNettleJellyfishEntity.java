package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlackSeaNettleJellyfishEntity extends BfsAquaticEntity<BlackSeaNettleJellyfishEntity> {

    private Vec3 driftDirection;
    private final int driftOffsetTicks;

    protected BlackSeaNettleJellyfishEntity(EntityType<BlackSeaNettleJellyfishEntity> type, Level level) {
        super(type, level);
        this.driftDirection = randomDriftDirection();
        this.driftOffsetTicks = level.getRandom().nextInt(200);
    }

    private Vec3 randomDriftDirection() {
        return new Vec3(
                (level().getRandom().nextDouble() - 0.5) * 2,
                0,
                (level().getRandom().nextDouble() - 0.5) * 2
        ).normalize();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (!this.isInWater()) {
            super.travel(travelVector);
            return;
        }
        // Periodically shift drift direction so blooms eventually move around.
        if (this.tickCount % 600 == 0 && !level().isClientSide) {
            this.driftDirection = randomDriftDirection();
        }

        // Vertical: pulse-rise toward surface, then plateau just below it.
        double bobPhase = (this.tickCount + this.driftOffsetTicks) % 80;
        double pulse = Math.sin(bobPhase / 80.0 * Math.PI * 2) * 0.04;
        // Light upward bias — jellyfish drift up when below sea level, down when above.
        double riseBias = 0.005;
        if (level() instanceof ServerLevel sl) {
            int surface = sl.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockPosition().getX(), blockPosition().getZ());
            int targetY = surface - 4; // hover ~4 blocks below surface
            if (this.getY() > targetY) riseBias = -0.005;
        }
        double y = pulse + riseBias;

        // Horizontal: slow drift.
        double driftX = this.driftDirection.x * 0.025;
        double driftZ = this.driftDirection.z * 0.025;

        this.setDeltaMovement(driftX, y, driftZ);
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;
        if (this.tickCount % 10 == 0) {
            AABB tentacleBox = this.getBoundingBox().inflate(0.6, 0.6, 0.6).expandTowards(0, -3, 0);
            List<LivingEntity> touching = this.level().getEntitiesOfClass(LivingEntity.class, tentacleBox);
            for (LivingEntity target : touching) {
                if (target == this) continue;
                if (target instanceof BlackSeaNettleJellyfishEntity) continue;
                DamageSource src = this.damageSources().mobAttack(this);
                if (target.hurt(src, 1.0f * globalJellyfishDamageMult)) {
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 160, 0));
                }
            }
        }
    }
}
