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

public class CaribbeanReefOctopusEntity extends BfsAquaticEntity<CaribbeanReefOctopusEntity>
        implements OctopusCamouflageHost {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> DATA_CAMO_TARGET =
            net.minecraft.network.syncher.SynchedEntityData.defineId(CaribbeanReefOctopusEntity.class,
                    net.minecraft.network.syncher.EntityDataSerializers.INT);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Float> DATA_CAMO_WEIGHT =
            net.minecraft.network.syncher.SynchedEntityData.defineId(CaribbeanReefOctopusEntity.class,
                    net.minecraft.network.syncher.EntityDataSerializers.FLOAT);
    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> DATA_INK_TICKS =
            net.minecraft.network.syncher.SynchedEntityData.defineId(CaribbeanReefOctopusEntity.class,
                    net.minecraft.network.syncher.EntityDataSerializers.INT);

    private int camouflageColor = 0x6b5c4e;
    private float camouflageWeight;

    private int playerProxCooldown;
    private int inkCooldown;
    private int hideTicks;
    private int hideCheckTimer;

    /**
     * Squid-style visual body pitch in degrees: 0 = upright, -90 = level with
     * horizontal travel (vanilla Squid's xBodyRot convention, measured from the
     * vertical axis). Computed from synced velocity on both sides; the renderer
     * applies it so the octopus leans into its swim instead of drifting upright.
     */
    public float xBodyRot;
    public float xBodyRotO;

    protected CaribbeanReefOctopusEntity(EntityType<CaribbeanReefOctopusEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_CAMO_TARGET, 0x6b5c4e);
        entityData.define(DATA_CAMO_WEIGHT, 0.0f);
        entityData.define(DATA_INK_TICKS, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 6)
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
    protected float swimSpeedMultiplier() { return 0.14f; }
    @Override
    protected float maxHorizontalSpeed() { return 0.22f; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean took = super.hurt(source, amount);
        if (took && !level().isClientSide && level() instanceof ServerLevel sl) {
            emitInk(sl);
            // "Aw, you made me ink!" — only the player who provoked the ink earns it.
            if (source.getEntity() instanceof net.minecraft.server.level.ServerPlayer provoker) {
                tfar.bensfintasticsharks.BensFintasticSharks.OCTOPUS_INKED.trigger(provoker, this);
            }
        }
        return took;
    }

    public boolean isHiding() { return hideTicks > 0; }

    @Override
    public void tick() {
        super.tick();
        updateBodyPitch();
        OctopusCamouflage.tick(this);
        if (level().isClientSide) return;
        if (level() instanceof ServerLevel sl) {
            OctopusInkCloudRegistry.tick(sl, getUUID());
            entityData.set(DATA_INK_TICKS, OctopusInkCloudRegistry.active(sl, getUUID())
                    ? Math.max(0, entityData.get(DATA_INK_TICKS) - 1) : 0);
        }
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
            if (emitInk(sl)) {
                Player p = nearby.get(0);
                startJetAway(p);
                inkCooldown = 120;
                hideTicks = 0;
            }
        }
        playerProxCooldown = 20;
        if (hideTicks == 0 && hideCheckTimer-- <= 0) {
            hideCheckTimer = 600;
            if (nearby.isEmpty() && getRandom().nextFloat() < 0.35f) {
                hideTicks = 200 + getRandom().nextInt(301);
            }
        }
    }

    /** Vanilla-Squid-style lean toward the swim direction; eases back upright when resting. */
    private void updateBodyPitch() {
        this.xBodyRotO = this.xBodyRot;
        Vec3 vel = getDeltaMovement();
        float target = 0f;
        if (isInWater() && !onGround() && vel.lengthSqr() > 1.0e-4) {
            // atan2(horizontal speed, vertical speed): 0 when rising, -90 when level.
            target = (float) (-(net.minecraft.util.Mth.atan2(vel.horizontalDistance(), vel.y) * (180F / (float) Math.PI)));
            // Cap the lean at -65 (≈25° off horizontal) so horizontal travel keeps a visible
            // slant instead of lying dead-flat (Ben's feedback), and sinking tilts without nose-diving.
            target = net.minecraft.util.Mth.clamp(target, -65f, 0f);
        }
        this.xBodyRot += (target - this.xBodyRot) * 0.1f;
    }

    protected boolean emitInk(ServerLevel level) {
        if (!isSubmerged() || !OctopusInkCloudRegistry.tryCreate(level, this)) return false;
        for (int i = 0; i < 32; i++) {
            double dx = (random.nextDouble() - 0.5) * 0.3;
            double dy = (random.nextDouble() - 0.5) * 0.3;
            double dz = (random.nextDouble() - 0.5) * 0.3;
            level.sendParticles(ParticleTypes.SQUID_INK, getX(), getY(), getZ(), 1, dx, dy, dz, 0.1);
        }
        level.playSound(null, blockPosition(), SoundEvents.SQUID_SQUIRT, SoundSource.NEUTRAL, 1.0F, 1.1F);
        entityData.set(DATA_INK_TICKS, OctopusInkCloudRegistry.LIFETIME_TICKS);
        return true;
    }

    private void startJetAway(Player threat) {
        Vec3 away = position().subtract(threat.position());
        if (away.lengthSqr() < 1.0e-4) away = new Vec3(1, 0, 0);
        away = away.normalize();
        Vec3 candidate = position().add(away.scale(4.0));
        if (!level().getFluidState(net.minecraft.core.BlockPos.containing(candidate))
                .is(net.minecraft.tags.FluidTags.WATER)) return;
        setDeltaMovement(getDeltaMovement().add(away.scale(0.18)));
    }

    private boolean isSubmerged() {
        return isInWaterOrBubble()
                || level().getFluidState(blockPosition()).is(net.minecraft.tags.FluidTags.WATER)
                || level().getFluidState(blockPosition().below()).is(net.minecraft.tags.FluidTags.WATER);
    }

    public boolean isInkCloudActive() {
        return entityData.get(DATA_INK_TICKS) > 0;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (level() instanceof ServerLevel sl) OctopusInkCloudRegistry.remove(sl, getUUID());
        super.remove(reason);
    }

    @Override public net.minecraft.world.entity.Entity camouflageEntity() { return this; }
    @Override public int camouflageTargetColor() { return entityData.get(DATA_CAMO_TARGET); }
    @Override public float camouflageTargetWeight() { return entityData.get(DATA_CAMO_WEIGHT); }
    @Override public void setCamouflageTarget(int color, float weight) {
        entityData.set(DATA_CAMO_TARGET, color & 0xffffff);
        entityData.set(DATA_CAMO_WEIGHT, net.minecraft.util.Mth.clamp(weight, 0.0f, 1.0f));
    }
    @Override public int camouflageColor() { return camouflageColor; }
    @Override public float camouflageWeight() { return camouflageWeight; }
    @Override public void setCamouflageCurrent(int color, float weight) {
        camouflageColor = color & 0xffffff;
        camouflageWeight = net.minecraft.util.Mth.clamp(weight, 0.0f, 1.0f);
    }

    @Override public float bfsScaleMin() { return 0.4f; }
    @Override public float bfsScaleMax() { return 0.85f; }
}
