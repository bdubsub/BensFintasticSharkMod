package tfar.bensfintasticsharks.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CaribbeanReefOctopusEntity extends BfsAquaticEntity<CaribbeanReefOctopusEntity> {

    private int playerProxCooldown;
    private int inkCooldown;
    private int hideTicks;
    private int hideCheckTimer;

    protected CaribbeanReefOctopusEntity(EntityType<CaribbeanReefOctopusEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.8F);
    }

    @Override
    protected float wanderRadiusXZ() { return 14f; }
    @Override
    protected float wanderRadiusY() { return 4f; }
    @Override
    protected int wanderWeight() { return 7; }
    @Override
    protected int idleWeight() { return 3; }
    // Pitch unlocked so the smooth-swim move control can tilt toward vertical targets.
    @Override
    protected float swimSpeedMultiplier() { return 0.10f; }
    @Override
    protected float maxHorizontalSpeed() { return 0.18f; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean took = super.hurt(source, amount);
        if (took && !level().isClientSide && level() instanceof ServerLevel sl) {
            emitInk(sl);
        }
        return took;
    }

    public boolean isHiding() { return hideTicks > 0; }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (inkCooldown > 0) inkCooldown--;
        if (hideTicks > 0) {
            hideTicks--;
            setDeltaMovement(getDeltaMovement().scale(0.6));
        }
        if (playerProxCooldown > 0) { playerProxCooldown--; return; }
        AABB area = getBoundingBox().inflate(5.0);
        List<Player> nearby = level().getEntitiesOfClass(Player.class, area,
                p -> !p.isCreative() && !p.isSpectator() && p.isInWater());
        if (!nearby.isEmpty() && inkCooldown == 0 && level() instanceof ServerLevel sl) {
            emitInk(sl);
            Player p = nearby.get(0);
            Vec3 away = position().subtract(p.position()).normalize().scale(0.4);
            setDeltaMovement(getDeltaMovement().add(away));
            inkCooldown = 120;
            hideTicks = 0;
        }
        playerProxCooldown = 20;
        if (hideTicks == 0 && hideCheckTimer-- <= 0) {
            hideCheckTimer = 600;
            if (nearby.isEmpty() && getRandom().nextFloat() < 0.35f) {
                hideTicks = 200 + getRandom().nextInt(301);
            }
        }
    }

    protected void emitInk(ServerLevel level) {
        for (int i = 0; i < 20; i++) {
            double dx = (random.nextDouble() - 0.5) * 0.3;
            double dy = (random.nextDouble() - 0.5) * 0.3;
            double dz = (random.nextDouble() - 0.5) * 0.3;
            level.sendParticles(ParticleTypes.SQUID_INK, getX(), getY(), getZ(), 1, dx, dy, dz, 0.1);
        }
        level.playSound(null, blockPosition(), SoundEvents.SQUID_SQUIRT, SoundSource.NEUTRAL, 1.0F, 1.1F);
    }
}
