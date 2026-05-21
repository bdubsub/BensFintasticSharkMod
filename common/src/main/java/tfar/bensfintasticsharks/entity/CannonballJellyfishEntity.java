package tfar.bensfintasticsharks.entity;

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

public class CannonballJellyfishEntity extends BfsAquaticEntity<CannonballJellyfishEntity> {

    private Vec3 driftDirection;
    private final int driftOffsetTicks;

    protected CannonballJellyfishEntity(EntityType<CannonballJellyfishEntity> type, Level level) {
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
                .add(Attributes.MAX_HEALTH, 3)
                .add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (!this.isInWater()) {
            super.travel(travelVector);
            return;
        }
        if (this.tickCount % 600 == 0 && !level().isClientSide) {
            this.driftDirection = randomDriftDirection();
        }

        double bobPhase = (this.tickCount + this.driftOffsetTicks) % 80;
        double pulse = Math.sin(bobPhase / 80.0 * Math.PI * 2) * 0.04;
        double riseBias = 0.005;
        if (level() instanceof ServerLevel sl) {
            int surface = sl.getHeight(Heightmap.Types.WORLD_SURFACE_WG, blockPosition().getX(), blockPosition().getZ());
            int targetY = surface - 4;
            if (this.getY() > targetY) riseBias = -0.005;
        }
        double y = pulse + riseBias;

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
            AABB tentacleBox = this.getBoundingBox().inflate(0.5);
            List<LivingEntity> touching = this.level().getEntitiesOfClass(LivingEntity.class, tentacleBox);
            for (LivingEntity target : touching) {
                if (target == this) continue;
                if (target instanceof CannonballJellyfishEntity) continue;
                DamageSource src = this.damageSources().mobAttack(this);
                if (target.hurt(src, 0.5f * globalJellyfishDamageMult)) {
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 0));
                }
            }
        }
    }

    @Override public float bfsScaleMin() { return 0.6f; }
    @Override public float bfsScaleMax() { return 0.85f; }
}
