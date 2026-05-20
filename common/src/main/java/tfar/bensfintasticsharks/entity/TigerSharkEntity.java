package tfar.bensfintasticsharks.entity;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.function.IntFunction;

public class TigerSharkEntity extends AbstractSharkEntity<TigerSharkEntity> implements BfsVariantHolder {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(TigerSharkEntity.class, EntityDataSerializers.INT);

    private static final SharkParams PARAMS = new SharkParams(
            /* detectionRadius      */ 26.0f,
            /* bloodDetectionRadius */ 36.0f,
            /* aggressionLevel      */ 60,
            /* aggroSpeedMult       */ 1.05f,
            /* disengageDistance    */ 64.0f,
            /* disengageTimeoutTicks*/ 300,
            /* biteCooldownTicks    */ 20,
            /* biteDamage           */ 4.0f
    );

    private int itemScanCooldown;
    private ItemEntity investigatedItem;
    private int biteFlash;

    protected TigerSharkEntity(EntityType<TigerSharkEntity> type, Level level) {
        super(type, level, PARAMS);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSharkAttributes(80, 1.0, 4);
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

    public boolean justBitItem() { return biteFlash > 0; }

    @Override
    protected float wanderRadiusXZ() { return 64f; }
    @Override
    protected float wanderRadiusY() { return 16f; }

    @Override
    protected void onSharkTick() {
        super.onSharkTick();
        if (level().isClientSide) return;
        if (biteFlash > 0) biteFlash--;

        if (investigatedItem != null) {
            if (!investigatedItem.isAlive() || !investigatedItem.isInWater()) {
                investigatedItem = null;
            } else if (distanceToSqr(investigatedItem) < 4.0) {
                this.swing(this.getUsedItemHand());
                biteFlash = 8;
                investigatedItem = null;
                setStateTimer(60);
            }
        }

        if (itemScanCooldown > 0) {
            itemScanCooldown--;
            return;
        }
        if (getSharkState() != SharkState.IDLE && getSharkState() != SharkState.CURIOUS) return;
        if (getTarget() != null) return;
        if (investigatedItem != null) return;

        AABB area = getBoundingBox().inflate(12.0);
        List<ItemEntity> items = level().getEntitiesOfClass(ItemEntity.class, area, ItemEntity::isInWater);
        if (items.isEmpty()) {
            itemScanCooldown = 100;
            return;
        }
        ItemEntity closest = items.stream()
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElseThrow();
        investigatedItem = closest;
        BlockPos pos = closest.blockPosition();
        BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET,
                new WalkTarget(Vec3.atCenterOf(pos), 1.0f, 1));
        setSharkState(SharkState.CURIOUS);
        setStateTimer(160);
        itemScanCooldown = 200;
    }

    public enum Variant implements StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2"),
        DEFAULT_3(2, "default_3"),
        SANDY(3,     "sandy");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        // Sandy is the rare variant.
        private static final SimpleWeightedRandomList<Variant> NATURAL_VARIANTS = SimpleWeightedRandomList.<Variant>builder()
                .add(DEFAULT_1, 33).add(DEFAULT_2, 33).add(DEFAULT_3, 33).add(SANDY, 6).build();

        private final int id;
        private final String name;
        Variant(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public @NotNull String getSerializedName() { return name; }
        public static Variant byId(int id) { return BY_ID.apply(id); }
        public static Variant getSpawnVariant(RandomSource random) { return NATURAL_VARIANTS.getRandomValue(random).orElseThrow(); }
    }
}
