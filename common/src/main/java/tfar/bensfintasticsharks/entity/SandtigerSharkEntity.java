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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tfar.bensfintasticsharks.disturbance.DisturbanceType;

import java.util.function.IntFunction;

public class SandtigerSharkEntity extends AbstractSharkEntity<SandtigerSharkEntity> implements BfsVariantHolder {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(SandtigerSharkEntity.class, EntityDataSerializers.INT);

    private static final SharkParams PARAMS = new SharkParams(
            /* detectionRadius      */ 18.0f,
            /* bloodDetectionRadius */ 32.0f,
            /* aggressionLevel      */ 30,
            /* aggroSpeedMult       */ 0.9f,
            /* disengageDistance    */ 48.0f,
            /* disengageTimeoutTicks*/ 200,
            /* biteCooldownTicks    */ 25,
            /* biteDamage           */ 4.0f
    );

    private int hoverTicksRemaining;
    private int hoverCheckCooldown;
    private boolean playedTailWhipForCuriousEntry;
    private SharkState lastSeenState = SharkState.IDLE;

    protected SandtigerSharkEntity(EntityType<SandtigerSharkEntity> type, Level level) {
        super(type, level, PARAMS);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSharkAttributes(80, 0.9, 4);
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
    protected float wanderRadiusXZ() { return 28f; }
    @Override
    protected float wanderRadiusY() { return 10f; }

    public boolean isHovering() { return hoverTicksRemaining > 0; }
    public boolean justEnteredCurious() { return playedTailWhipForCuriousEntry; }

    @Override
    protected void onSharkTick() {
        super.onSharkTick();
        if (level().isClientSide) return;
        // 0.20 — while fleeing a bigger shark, drop the hover (it pins the shark in place with
        // a fixed upward velocity) and skip the item/curious logic so the flee actually runs.
        if (isFleeing()) {
            hoverTicksRemaining = 0;
            return;
        }

        SharkState now = getSharkState();
        if (now != lastSeenState) {
            if (lastSeenState == SharkState.IDLE && now == SharkState.CURIOUS && getRandom().nextFloat() < 0.15f) {
                playedTailWhipForCuriousEntry = true;
            } else {
                playedTailWhipForCuriousEntry = false;
            }
            lastSeenState = now;
        }

        if (hoverTicksRemaining > 0) {
            setDeltaMovement(new Vec3(0, 0.02, 0));
            hoverTicksRemaining--;
            return;
        }
        if (hoverCheckCooldown > 0) {
            hoverCheckCooldown--;
            return;
        }
        if (now != SharkState.IDLE) return;
        if (!isInWater()) return;
        if (getRandom().nextFloat() < 0.10f) {
            hoverTicksRemaining = 100 + getRandom().nextInt(101);
        }
        hoverCheckCooldown = 1200;
    }

    @Override
    protected void onPostDisturbance(BlockPos source, DisturbanceType type, @Nullable LivingEntity sourceEntity) {
        super.onPostDisturbance(source, type, sourceEntity);
        if (type != DisturbanceType.LIGHT) hoverTicksRemaining = 0;
    }

    public enum Variant implements StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2"),
        DEFAULT_3(2, "default_3");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private static final SimpleWeightedRandomList<Variant> NATURAL_VARIANTS = SimpleWeightedRandomList.<Variant>builder()
                .add(DEFAULT_1, 1).add(DEFAULT_2, 1).add(DEFAULT_3, 1).build();

        private final int id;
        private final String name;
        Variant(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public @NotNull String getSerializedName() { return name; }
        public static Variant byId(int id) { return BY_ID.apply(id); }
        public static Variant getSpawnVariant(RandomSource random) { return NATURAL_VARIANTS.getRandomValue(random).orElseThrow(); }
    }

    // Suggestion 2: wider size variety. Baseline model is ~3.24m long at scale 1.0, so
    // 0.72 → ~2.33m juveniles and 1.45 → ~4.70m largest adults — a noticeable range that
    // stays under the 5m target with margin.
    @Override public float bfsScaleMin() { return 0.72f; }
    @Override public float bfsScaleMax() { return 1.45f; }

    // Bite-sync (same recipe as Blacktip's 0.18 fix): animation.sandtigershark.bite opens
    // the jaw at 0.125s, peaks at 0.25s and snaps shut at 0.375s (7.5t). Default 5t hit early.
    @Override protected int biteImpactDelayTicks() { return 8; }

    @Override
    protected double biteRangeAgainst(net.minecraft.world.entity.LivingEntity target) {
        return closeBiteRangeAgainst(target, 0.35);
    }

    @Override
    protected net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> preyTag() {
        return tfar.bensfintasticsharks.init.ModTags.EntityTypes.SANDTIGER_SHARK_PREY;
    }
}
