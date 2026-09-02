package tfar.bensfintasticsharks.entity;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
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

import java.util.function.IntFunction;

public class BlacktipReefSharkEntity extends AbstractSharkEntity<BlacktipReefSharkEntity>
        implements BfsVariantHolder, SharkGrabber {

    private static final int LATCH_DURATION_TICKS = 30;

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_LATCH_TIMER =
            SynchedEntityData.defineId(BlacktipReefSharkEntity.class, EntityDataSerializers.INT);

    private static final SharkParams PARAMS = new SharkParams(
            /* detectionRadius      */ 20.0f,
            /* bloodDetectionRadius */ 30.0f,
            /* aggressionLevel      */ 55,
            /* aggroSpeedMult       */ 1.1f,
            /* disengageDistance    */ 64.0f,
            /* disengageTimeoutTicks*/ 300,
            /* biteCooldownTicks    */ 20,
            /* biteDamage           */ 2.5f
    );

    protected BlacktipReefSharkEntity(EntityType<BlacktipReefSharkEntity> type, Level level) {
        super(type, level, PARAMS);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createSharkAttributes(60, 1.1, 2.5);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_LATCH_TIMER, 0);
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
    protected float wanderRadiusXZ() { return 40f; }
    @Override
    protected float wanderRadiusY() { return 10f; }

    @Override
    public boolean canJoinPlayerFeedingFrenzy() { return true; }

    @Override
    protected boolean alertsPackmatesWhenAttacked() { return true; }

    // Feedback singled blacktips out as visually too fast once a school enters a frenzy.
    // Keep their normal cruise untouched, but trim the chase burst/floor and terminal cap.
    @Override
    protected float chaseAccelBoost() { return 1.55f; }
    @Override
    protected float chaseSpeedFloor() { return 0.27f; }
    @Override
    protected float maxHorizontalSpeed() {
        return getTarget() != null ? 0.52f : super.maxHorizontalSpeed();
    }

    public enum Variant implements StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2"),
        DEFAULT_3(2, "default_3"),
        DEFAULT_4(3, "default_4");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private static final SimpleWeightedRandomList<Variant> NATURAL_VARIANTS = SimpleWeightedRandomList.<Variant>builder()
                .add(DEFAULT_1, 25).add(DEFAULT_2, 25).add(DEFAULT_3, 25).add(DEFAULT_4, 25).build();

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

    // 0.18 — Ben: the bite animation seemed to lag ~1s behind the damage. The 1.125s
    // bite clip snaps shut around 0.5-0.65s in (plus the controller's 0.25s blend-in),
    // but the default 5-tick impact delay landed the damage at 0.25s — well before the
    // visible chomp. 12 ticks (0.6s) puts the hit on the animation's impact frame.
    @Override protected int biteImpactDelayTicks() { return 12; }

    @Override
    protected double biteRangeAgainst(net.minecraft.world.entity.LivingEntity target) {
        return closeBiteRangeAgainst(target, 0.3);
    }

    protected void latchMob(LivingEntity target) {
        if (target != getTarget() || target.isPassenger() || !isInWaterOrBubble()) return;
        if (!target.startRiding(this, true)) return;
        setGrabTimer(LATCH_DURATION_TICKS);
        if (target instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetPassengersPacket(this));
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        Vec3 look = getLookAngle();
        double distance = Math.max(0.65, getBbWidth() * 0.45);
        double y = getY() + getBbHeight() * 0.45 - passenger.getBbHeight() * 0.5;
        moveFunction.accept(passenger, getX() + look.x * distance, y, getZ() + look.z * distance);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        int timer = getGrabTimer();
        if (timer <= 0) return;

        int next = timer - 1;
        boolean livingPassenger = getPassengers().stream()
                .anyMatch(passenger -> passenger instanceof LivingEntity living && living.isAlive());
        if (next <= 0 || !isAlive() || !isInWaterOrBubble() || !livingPassenger) {
            setGrabTimer(0);
            ejectPassengers();
        } else {
            setGrabTimer(next);
        }
    }

    private void setGrabTimer(int timer) {
        entityData.set(DATA_LATCH_TIMER, Math.max(0, timer));
    }

    @Override
    public int getGrabTimer() {
        return entityData.get(DATA_LATCH_TIMER);
    }

    @Override
    public void remove(RemovalReason reason) {
        setGrabTimer(0);
        ejectPassengers();
        super.remove(reason);
    }

    @Override
    protected net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> preyTag() {
        return tfar.bensfintasticsharks.init.ModTags.EntityTypes.BLACKTIP_REEF_SHARK_PREY;
    }

    /**
     * Blacktips are skittish. A lone one that gets hit bolts instead of fighting back;
     * with a group of its own kind nearby it gets bold and retaliates (and the base
     * hurt() help-call then drags the rest of the school into the fight).
     */
    @Override
    protected boolean retaliatesAgainst(net.minecraft.world.entity.LivingEntity attacker) {
        int buddies = level().getEntitiesOfClass(BlacktipReefSharkEntity.class,
                getBoundingBox().inflate(16.0), s -> s != this && s.isAlive() && s.isInWater()).size();
        return buddies >= 1;
    }
}
