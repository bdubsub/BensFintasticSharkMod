package tfar.bensfintasticsharks.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TurtleEggBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GreenSeaTurtleEntity extends BfsAquaticEntity<GreenSeaTurtleEntity> implements BfsVariantHolder {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> DATA_VARIANT =
            net.minecraft.network.syncher.SynchedEntityData.defineId(GreenSeaTurtleEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);

    private long lastEggLayDay = -1;
    private int layingTicks;
    private int basksTicks;
    private int basksCheckTimer;

    protected GreenSeaTurtleEntity(EntityType<GreenSeaTurtleEntity> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1);
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
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.8F);
    }

    @Override
    protected float wanderRadiusXZ() { return 40f; }
    @Override
    protected float wanderRadiusY() { return 12f; }
    @Override
    protected int wanderWeight() { return 8; }
    @Override
    protected int idleWeight() { return 2; }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    public void travel(@NotNull Vec3 movementInput) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), movementInput);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.7));
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.lastEggLayDay = tag.getLong("LastEggLayDay");
        setVariant(Variant.byId(tag.getInt("Variant")));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putLong("LastEggLayDay", lastEggLayDay);
        tag.putInt("Variant", getVariant().getId());
    }

    @Override
    public void tick() {
        super.tick();
        // Egg-laying behaviour intentionally disabled — feedback was that a beached
        // turtle dropping eggs felt unintentional. Use vanilla turtles for breeding;
        // BFS turtles are a wild encounter species only.
    }

    private boolean isOnBeachSand(ServerLevel sl) {
        Holder<Biome> biome = sl.getBiome(blockPosition());
        if (!(biome.is(Biomes.BEACH) || biome.is(Biomes.SNOWY_BEACH))) return false;
        BlockState below = sl.getBlockState(blockPosition().below());
        return below.is(BlockTags.SAND);
    }

    public boolean isLayingEggs() {
        return layingTicks > 0;
    }

    public boolean isBasking() {
        return basksTicks > 0;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        if (basksTicks > 0) {
            basksTicks--;
            setDeltaMovement(getDeltaMovement().scale(0.5));
        }
        if (basksCheckTimer-- <= 0) {
            basksCheckTimer = 400;
            // 30% chance per 20s to rest 60-200t (3-10s).
            if (basksTicks == 0 && layingTicks == 0 && getRandom().nextFloat() < 0.3f) {
                basksTicks = 60 + getRandom().nextInt(141);
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
}
