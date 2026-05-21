package tfar.bensfintasticsharks.entity;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.IntFunction;

public class OceanicWhitetipSharkEntity extends AbstractSharkEntity<OceanicWhitetipSharkEntity>
        implements BfsVariantHolder {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(OceanicWhitetipSharkEntity.class, EntityDataSerializers.INT);

    private static final SharkParams PARAMS = new SharkParams(
            /* detectionRadius      */ 26.0f,
            /* bloodDetectionRadius */ 48.0f,
            /* aggressionLevel      */ 55,
            /* aggroSpeedMult       */ 1.1f,
            /* disengageDistance    */ 80.0f,
            /* disengageTimeoutTicks*/ 600,
            /* biteCooldownTicks    */ 40,
            /* biteDamage           */ 3.5f
    );

    protected OceanicWhitetipSharkEntity(EntityType<OceanicWhitetipSharkEntity> type, Level level) {
        super(type, level, PARAMS);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSharkAttributes(70, 1.0, 3.5);
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

    @Override
    protected float wanderRadiusXZ() { return 80f; }
    @Override
    protected float wanderRadiusY() { return 16f; }

    public enum Variant implements StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2"),
        DEFAULT_3(2, "default_3"),
        DEFAULT_4(3, "default_4"),
        DEFAULT_5(4, "default_5"),
        DEFAULT_6(5, "default_6");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private static final SimpleWeightedRandomList<Variant> NATURAL_VARIANTS = SimpleWeightedRandomList.<Variant>builder()
                .add(DEFAULT_1, 1).add(DEFAULT_2, 1).add(DEFAULT_3, 1)
                .add(DEFAULT_4, 1).add(DEFAULT_5, 1).add(DEFAULT_6, 1).build();

        private final int id;
        private final String name;
        Variant(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public @NotNull String getSerializedName() { return name; }
        public static Variant byId(int id) { return BY_ID.apply(id); }
        public static Variant getSpawnVariant(RandomSource random) { return NATURAL_VARIANTS.getRandomValue(random).orElseThrow(); }
    }

    @Override public float bfsScaleMin() { return 0.9f; }
    @Override public float bfsScaleMax() { return 1.05f; }
}
