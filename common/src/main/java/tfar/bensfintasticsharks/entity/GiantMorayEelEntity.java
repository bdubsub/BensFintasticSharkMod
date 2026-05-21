package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class GiantMorayEelEntity extends BfsAquaticEntity<GiantMorayEelEntity> implements BfsVariantHolder {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> DATA_VARIANT =
            net.minecraft.network.syncher.SynchedEntityData.defineId(GiantMorayEelEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);

    private int lungeCooldown;
    private boolean queueLunge;
    private int hideTicks;
    private int hideCheckTimer;

    protected GiantMorayEelEntity(EntityType<GiantMorayEelEntity> type, Level level) {
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
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.9F)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    // Eels stick close to their hiding spot — small radius, more idling.
    @Override
    protected float wanderRadiusXZ() { return 10f; }
    @Override
    protected float wanderRadiusY() { return 3f; }
    @Override
    protected int wanderWeight() { return 5; }
    @Override
    protected int idleWeight() { return 5; }
    @Override
    protected int idleMinTicks() { return 80; }
    @Override
    protected int idleMaxTicks() { return 160; }

    public boolean isLunging() {
        return queueLunge;
    }

    public boolean isHiding() {
        return hideTicks > 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        // Eels anchor in place between lunges — long hide intervals interrupted by player proximity.
        if (hideTicks > 0) {
            hideTicks--;
            setDeltaMovement(getDeltaMovement().scale(0.3));
        }
        if (lungeCooldown > 0) { lungeCooldown--; queueLunge = false; return; }
        if (tickCount % 10 != 0) return;
        // Lunge if a player is within 2 blocks.
        AABB area = getBoundingBox().inflate(2.0);
        List<Player> nearby = level().getEntitiesOfClass(Player.class, area,
                p -> !p.isCreative() && !p.isSpectator());
        if (!nearby.isEmpty()) {
            nearby.get(0).hurt(damageSources().mobAttack(this), 2.0f);
            queueLunge = true;
            lungeCooldown = 40;
            hideTicks = 0;
            return;
        }
        // Most of the time, eels hide motionless. Periodically pick a new hide duration.
        if (hideTicks == 0 && hideCheckTimer-- <= 0) {
            hideCheckTimer = 800;
            if (getRandom().nextFloat() < 0.7f) {
                hideTicks = 400 + getRandom().nextInt(401);
            }
        }
    }

    public enum Variant implements net.minecraft.util.StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2"),
        DEFAULT_3(2, "default_3");

        private static final java.util.function.IntFunction<Variant> BY_ID = net.minecraft.util.ByIdMap.continuous(Variant::getId, values(), net.minecraft.util.ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final com.mojang.serialization.Codec<Variant> CODEC = net.minecraft.util.StringRepresentable.fromEnum(Variant::values);

        private static final net.minecraft.util.random.SimpleWeightedRandomList<Variant> NATURAL_VARIANTS =
                net.minecraft.util.random.SimpleWeightedRandomList.<Variant>builder()
                        .add(DEFAULT_1, 1).add(DEFAULT_2, 1).add(DEFAULT_3, 1).build();

        private final int id;
        private final String name;
        Variant(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public @org.jetbrains.annotations.NotNull String getSerializedName() { return name; }
        public static Variant byId(int id) { return BY_ID.apply(id); }
        public static Variant getSpawnVariant(net.minecraft.util.RandomSource random) { return NATURAL_VARIANTS.getRandomValue(random).orElseThrow(); }
    }

    @Override public float bfsScaleMin() { return 0.55f; }
    @Override public float bfsScaleMax() { return 1.0f; }
}
