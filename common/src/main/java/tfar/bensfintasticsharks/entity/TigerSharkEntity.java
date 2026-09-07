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
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.pathfinder.Path;
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
    private ItemEntity recentlyInvestigatedItem;
    private Vec3 investigatedApproachPoint;
    private int investigationTicks;
    private int approachRefreshCooldown;
    private int recentItemCooldown;
    private int biteFlash;

    protected TigerSharkEntity(EntityType<TigerSharkEntity> type, Level level) {
        super(type, level, PARAMS);
    }

    public static AttributeSupplier.Builder createAttributes() {
        // Bug 10: tiger sharks are large, fast cruising apex predators — bump MOVEMENT_SPEED
        // from 1.0 to 1.2 (parity with the Great White / Common Thresher tier) so they no
        // longer lag behind the other big sharks while wandering.
        return createSharkAttributes(130, 1.2, 4);
    }

    @Override
    protected float upwardPitchLimitDegrees() { return AquaticMovement.TIGER_UPWARD_PITCH_LIMIT; }

    @Override
    protected float downwardPitchLimitDegrees() { return AquaticMovement.TIGER_DOWNWARD_PITCH_LIMIT; }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    /** Bug 5: true when name-tagged "Sandy" — the vanilla jeb_/Toast-style easter egg. */
    public boolean isSandy() {
        return hasCustomName() && getCustomName().getString().equals("Sandy");
    }

    public Variant getVariant() {
        // A "Sandy"-named tiger shark displays variant 4 (the sandy skin) while named, and
        // reverts to its natural stored variant if renamed. Overriding the getter (rather than
        // mutating DATA_VARIANT) matches vanilla jeb_/Toast: no tick churn, auto-revert.
        if (isSandy()) return Variant.SANDY;
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }
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
        // Persist the raw stored variant, NOT getVariant() — otherwise a "Sandy"-named shark
        // would save Variant=3 and keep the sandy skin after the name is removed.
        tag.putInt("Variant", this.entityData.get(DATA_VARIANT));
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
    // Keep random idle targets within a normally navigable ocean pocket. A 64 block
    // search regularly selected a target outside a small loaded water volume, leaving
    // the shark with no path and making it appear stationary until the next roll.
    protected float wanderRadiusXZ() { return 32f; }
    @Override
    protected float wanderRadiusY() { return 16f; }

    @Override
    protected void onSharkTick() {
        super.onSharkTick();
        if (level().isClientSide) return;
        if (biteFlash > 0) biteFlash--;
        if (recentItemCooldown > 0 && --recentItemCooldown == 0) {
            recentlyInvestigatedItem = null;
        }
        // 0.20 — don't investigate/steer toward dropped items while fleeing a bigger shark.
        if (isFleeing()) {
            if (investigatedItem != null) {
                stopInvestigatingItem(false);
            }
            return;
        }

        // Live prey always wins over curiosity. Keeping an old item waypoint here let this
        // hook fight the shared pursuit waypoint every tick, producing alternating loops.
        if (getTarget() != null) {
            if (investigatedItem != null) {
                stopInvestigatingItem(false);
            }
            return;
        }

        if (investigatedItem != null) {
            if (!investigatedItem.isAlive() || !investigatedItem.isInWater()) {
                stopInvestigatingItem(true);
            } else if (--investigationTicks <= 0) {
                stopInvestigatingItem(true);
            } else if (investigationTicks < 190 && getNavigation().isDone()
                    && distanceToSqr(investigatedApproachPoint) >= 5.0625) {
                // A reachable intercept should keep a path active until the shark arrives.
                // If navigation has finished while the shark is still outside bite distance,
                // treat that as a failed path instead of waiting out the curiosity timeout.
                stopInvestigatingItem(true);
            } else if (distanceToSqr(investigatedApproachPoint) < 5.0625) {
                this.swing(this.getUsedItemHand());
                biteFlash = 8;
                stopInvestigatingItem(true);
                setStateTimer(60);
            } else {
                if (approachRefreshCooldown > 0) approachRefreshCooldown--;
                if (approachRefreshCooldown == 0 && itemMovedAwayFromApproach()) {
                    investigatedApproachPoint = createItemApproachPoint(investigatedItem);
                    approachRefreshCooldown = 20;
                }
            }
            if (investigatedItem != null && (tickCount + getId()) % 10 == 0) {
                BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET,
                        new WalkTarget(investigatedApproachPoint, 0.75f, 2));
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
        List<ItemEntity> items = level().getEntitiesOfClass(ItemEntity.class, area,
                item -> item.isInWater()
                        && item.getItem().isEdible()
                        && item != recentlyInvestigatedItem
                        && isReachableItem(item));
        if (items.isEmpty()) {
            itemScanCooldown = 100;
            return;
        }
        ItemEntity closest = items.stream()
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElseThrow();
        investigatedItem = closest;
        investigatedApproachPoint = createItemApproachPoint(closest);
        investigationTicks = 200;
        approachRefreshCooldown = 20;
        BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET,
                new WalkTarget(investigatedApproachPoint, 0.75f, 2));
        setSharkState(SharkState.CURIOUS);
        setStateTimer(200);
        itemScanCooldown = 200;
    }

    private Vec3 createItemApproachPoint(ItemEntity item) {
        double depth = Math.max(1.0, getBbHeight() * 0.6);
        return new Vec3(item.getX(), item.getY() - depth, item.getZ());
    }

    private boolean isReachableItem(ItemEntity item) {
        Vec3 approach = createItemApproachPoint(item);
        net.minecraft.core.BlockPos approachPos = net.minecraft.core.BlockPos.containing(approach);
        if (!level().getFluidState(approachPos).is(net.minecraft.tags.FluidTags.WATER)) return false;
        Path path = getNavigation().createPath(approachPos, 0);
        return path != null && path.canReach();
    }

    private boolean itemMovedAwayFromApproach() {
        if (investigatedApproachPoint == null) return true;
        double depth = Math.max(1.0, getBbHeight() * 0.6);
        Vec3 expectedItemPosition = investigatedApproachPoint.add(0, depth, 0);
        return investigatedItem.position().distanceToSqr(expectedItemPosition) > 4.0;
    }

    private void stopInvestigatingItem(boolean rememberItem) {
        if (rememberItem && investigatedItem != null) {
            recentlyInvestigatedItem = investigatedItem;
            recentItemCooldown = 600;
        }
        investigatedItem = null;
        investigatedApproachPoint = null;
        investigationTicks = 0;
        approachRefreshCooldown = 0;
        BrainUtils.clearMemory(getBrain(), MemoryModuleType.WALK_TARGET);
        getNavigation().stop();
        if (getTarget() == null && !isFleeing()) {
            setSharkState(SharkState.IDLE);
        }
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

    @Override public float bfsScaleMin() { return 0.85f; }
    @Override public float bfsScaleMax() { return 1.0f; }

    // Bite-sync (same recipe as Blacktip's 0.18 fix): animation.tigershark.bite opens the
    // jaw at 0.125s, peaks at 0.25s and snaps shut at 0.375s (7.5t). Default 5t hit early.
    @Override protected int biteImpactDelayTicks() { return 8; }

    @Override
    protected net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> preyTag() {
        return tfar.bensfintasticsharks.init.ModTags.EntityTypes.TIGER_SHARK_PREY;
    }
}
