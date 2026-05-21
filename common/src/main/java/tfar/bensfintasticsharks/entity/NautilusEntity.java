package tfar.bensfintasticsharks.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NautilusEntity extends BfsAquaticEntity<NautilusEntity> implements BfsVariantHolder {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> DATA_VARIANT =
            net.minecraft.network.syncher.SynchedEntityData.defineId(NautilusEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);

    private int restTicks;
    private int restCheckTimer;

    protected NautilusEntity(EntityType<NautilusEntity> type, Level level) {
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
    public void readAdditionalSaveData(@org.jetbrains.annotations.NotNull net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(Variant.byId(tag.getInt("Variant")));
    }

    @Override
    public void addAdditionalSaveData(@org.jetbrains.annotations.NotNull net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant().getId());
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(@org.jetbrains.annotations.NotNull net.minecraft.world.level.ServerLevelAccessor level,
                                                                   @org.jetbrains.annotations.NotNull net.minecraft.world.DifficultyInstance diff,
                                                                   @org.jetbrains.annotations.NotNull net.minecraft.world.entity.MobSpawnType reason,
                                                                   @org.jetbrains.annotations.Nullable net.minecraft.world.entity.SpawnGroupData data,
                                                                   @org.jetbrains.annotations.Nullable net.minecraft.nbt.CompoundTag tag) {
        this.setVariant(Variant.getSpawnVariant(level.getRandom()));
        return super.finalizeSpawn(level, diff, reason, data, tag);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.5F);
    }

    public boolean isResting() {
        return restTicks > 0;
    }

    // Nautilus drifts slowly — keep wander tight but don't freeze it.
    @Override
    protected float wanderRadiusXZ() { return 14f; }
    @Override
    protected float wanderRadiusY() { return 6f; }
    @Override
    protected int wanderWeight() { return 6; }
    @Override
    protected int idleWeight() { return 4; }
    @Override
    protected int idleMinTicks() { return 60; }
    @Override
    protected int idleMaxTicks() { return 120; }
    // Pitch unlocked — locking it kept the smooth-swim move control from ever tilting
    // the nautilus toward vertical targets, so it'd refuse to swim up/down.
    @Override
    protected float swimSpeedMultiplier() { return 0.08f; }
    @Override
    protected float maxHorizontalSpeed() { return 0.12f; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean took = super.hurt(source, amount);
        if (took && !level().isClientSide && isInWater()) {
            // Sink down slowly when attacked — no flee, just descent.
            Vec3 v = getDeltaMovement();
            setDeltaMovement(v.x * 0.3, Math.min(v.y, -0.05), v.z * 0.3);
        }
        return took;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (restTicks > 0) {
            restTicks--;
            // Slow movement to a glide — don't hard-stop it.
            setDeltaMovement(getDeltaMovement().scale(0.85));
            return;
        }
        if (restCheckTimer > 0) { restCheckTimer--; return; }
        // 20% chance per 400-tick window (20s) to rest for 5-10s.
        if (getRandom().nextFloat() < 0.20f) {
            restTicks = 100 + getRandom().nextInt(101);
        }
        restCheckTimer = 400;
    }

    public enum Variant implements net.minecraft.util.StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2");

        private static final java.util.function.IntFunction<Variant> BY_ID = net.minecraft.util.ByIdMap.continuous(Variant::getId, values(), net.minecraft.util.ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final com.mojang.serialization.Codec<Variant> CODEC = net.minecraft.util.StringRepresentable.fromEnum(Variant::values);

        private static final net.minecraft.util.random.SimpleWeightedRandomList<Variant> NATURAL_VARIANTS =
                net.minecraft.util.random.SimpleWeightedRandomList.<Variant>builder()
                        .add(DEFAULT_1, 1).add(DEFAULT_2, 1).build();

        private final int id;
        private final String name;
        Variant(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public @org.jetbrains.annotations.NotNull String getSerializedName() { return name; }
        public static Variant byId(int id) { return BY_ID.apply(id); }
        public static Variant getSpawnVariant(net.minecraft.util.RandomSource random) { return NATURAL_VARIANTS.getRandomValue(random).orElseThrow(); }
    }

    @Override public float bfsScaleMin() { return 0.85f; }
    @Override public float bfsScaleMax() { return 1.1f; }
}
