package tfar.bensfintasticsharks.entity;

import com.mojang.serialization.Codec;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
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
import tfar.bensfintasticsharks.BensFintasticSharks;

import java.util.function.IntFunction;

public class OceanicWhitetipSharkEntity extends AbstractSharkEntity<OceanicWhitetipSharkEntity>
        implements BfsVariantHolder, SharkGrabber {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(OceanicWhitetipSharkEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_GRAB_TIMER =
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
        return createSharkAttributes(110, 1.0, 3.5);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_GRAB_TIMER, 0);
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

    public void grabMob(LivingEntity target) {
        if (target != this.getTarget() || target.isPassenger() || !this.isInWaterOrBubble()) return;
        if (!target.startRiding(this, true)) return;
        if (target instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetPassengersPacket(this));
        }
        setGrabTimer(BensFintasticSharks.GRAB_TIMER);
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        Vec3 look = getLookAngle();
        float distance = Math.max(1.15f, getBbWidth() * 0.65f);
        int elapsed = BensFintasticSharks.GRAB_TIMER - getGrabTimer();
        float angle = 30 * Mth.sin((elapsed % 40) * 18 * Mth.DEG_TO_RAD);
        double sin = Mth.sin(angle * Mth.DEG_TO_RAD);
        double cos = Mth.cos(angle * Mth.DEG_TO_RAD);
        double offsetX = distance * (look.x * cos - look.z * sin);
        double offsetZ = distance * (look.x * sin + look.z * cos);
        double y = getY() + getBbHeight() * 0.5 - passenger.getBbHeight() * 0.5;
        moveFunction.accept(passenger, getX() + offsetX, y, getZ() + offsetZ);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        int timer = getGrabTimer();
        if (timer <= 0) return;
        int next = timer - 1;
        boolean livingPassenger = getPassengers().stream()
                .anyMatch(passenger -> passenger instanceof LivingEntity living && living.isAlive());
        boolean release = next == 0 || !isAlive() || !isInWaterOrBubble() || !livingPassenger;
        if (!release && next % 10 == 0) {
            for (Entity passenger : getPassengers()) {
                if (passenger instanceof LivingEntity living && living.isAlive()) {
                    living.hurt(this.damageSources().mobAttack(this), 2.0f);
                }
            }
        }
        setGrabTimer(release ? 0 : next);
        if (release) ejectPassengers();
    }

    private void setGrabTimer(int timer) {
        entityData.set(DATA_GRAB_TIMER, Math.max(0, timer));
    }

    @Override
    public void remove(RemovalReason reason) {
        setGrabTimer(0);
        ejectPassengers();
        super.remove(reason);
    }

    @Override
    public int getGrabTimer() {
        return entityData.get(DATA_GRAB_TIMER);
    }

    @Override protected int biteImpactDelayTicks() { return 6; }

    @Override
    protected net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> preyTag() {
        return tfar.bensfintasticsharks.init.ModTags.EntityTypes.OCEANIC_WHITETIP_SHARK_PREY;
    }
}
