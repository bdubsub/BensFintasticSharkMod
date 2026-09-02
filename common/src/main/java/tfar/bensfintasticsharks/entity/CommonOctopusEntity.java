package tfar.bensfintasticsharks.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.IntFunction;

public class CommonOctopusEntity extends BfsAquaticEntity<CommonOctopusEntity> implements BfsVariantHolder {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(CommonOctopusEntity.class, EntityDataSerializers.INT);

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

    protected CommonOctopusEntity(EntityType<CommonOctopusEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    public Variant getVariant() { return Variant.byId(this.entityData.get(DATA_VARIANT)); }
    public void setVariant(Variant v) { this.entityData.set(DATA_VARIANT, v.getId()); }

    @Override
    public int bfsVariantCount() { return Variant.values().length; }
    @Override
    public void setBfsVariantId(int id) {
        int n = bfsVariantCount();
        setVariant(Variant.byId(((id % n) + n) % n));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(Variant.byId(tag.getInt("Variant")));
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant().getId());
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance diff,
                                        @NotNull MobSpawnType reason, @Nullable SpawnGroupData data,
                                        @Nullable CompoundTag tag) {
        this.setVariant(Variant.getSpawnVariant(level.getRandom()));
        return super.finalizeSpawn(level, diff, reason, data, tag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12)
                .add(Attributes.MOVEMENT_SPEED, 0.8F);
    }

    @Override
    protected float wanderRadiusXZ() { return 14f; }
    @Override
    protected float wanderRadiusY() { return 8f; }
    @Override
    protected int wanderWeight() { return 7; }
    @Override
    protected int idleWeight() { return 3; }
    // Pitch is no longer locked — locking it stopped the smooth-swim move control from
    // ever tilting the octopus toward a target above or below it, which made the
    // entity refuse to swim vertically. Letting the model tilt naturally fixes that.
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
        if (level().isClientSide) return;
        if (inkCooldown > 0) inkCooldown--;
        // Cosmetic hide state — slows movement and switches anim. Cancels on threat.
        if (hideTicks > 0) {
            hideTicks--;
            setDeltaMovement(getDeltaMovement().scale(0.6));
        }
        if (playerProxCooldown > 0) { playerProxCooldown--; return; }
        // Defensive ink + flee when a player gets too close.
        AABB area = getBoundingBox().inflate(5.0);
        List<Player> nearby = level().getEntitiesOfClass(Player.class, area,
                p -> !p.isCreative() && !p.isSpectator() && p.isInWater());
        if (!nearby.isEmpty() && inkCooldown == 0 && level() instanceof ServerLevel sl) {
            emitInk(sl);
            // Push away from the nearest player.
            Player p = nearby.get(0);
            Vec3 away = position().subtract(p.position()).normalize().scale(0.4);
            setDeltaMovement(getDeltaMovement().add(away));
            inkCooldown = 120;
            hideTicks = 0;
        }
        playerProxCooldown = 20;
        // 35% chance every 600t to "hide" (rest in place) for 200-500t.
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

    protected void emitInk(ServerLevel level) {
        for (int i = 0; i < 30; i++) {
            double dx = (random.nextDouble() - 0.5) * 0.4;
            double dy = (random.nextDouble() - 0.5) * 0.4;
            double dz = (random.nextDouble() - 0.5) * 0.4;
            level.sendParticles(ParticleTypes.SQUID_INK, getX(), getY(), getZ(), 1, dx, dy, dz, 0.1);
        }
        level.playSound(null, blockPosition(), SoundEvents.SQUID_SQUIRT, SoundSource.NEUTRAL, 1.0F, 1.0F);
    }

    public enum Variant implements StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2"),
        DEFAULT_3(2, "default_3"),
        DEFAULT_4(3, "default_4");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private static final SimpleWeightedRandomList<Variant> NATURAL_VARIANTS = SimpleWeightedRandomList.<Variant>builder()
                .add(DEFAULT_1, 1).add(DEFAULT_2, 1).add(DEFAULT_3, 1).add(DEFAULT_4, 1).build();

        private final int id;
        private final String name;
        Variant(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public @NotNull String getSerializedName() { return name; }
        public static Variant byId(int id) { return BY_ID.apply(id); }
        public static Variant getSpawnVariant(RandomSource random) { return NATURAL_VARIANTS.getRandomValue(random).orElseThrow(); }
    }

    @Override public float bfsScaleMin() { return 0.45f; }
    @Override public float bfsScaleMax() { return 0.9f; }
}
