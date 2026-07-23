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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.IntFunction;

public class AmericanLobsterEntity extends BfsAquaticEntity<AmericanLobsterEntity> implements BfsVariantHolder {

    private static final EntityDataAccessor<Integer> DATA_VARIANT =
            SynchedEntityData.defineId(AmericanLobsterEntity.class, EntityDataSerializers.INT);
    // Synced so the client-side animation controller can pick IDLE vs CRAWL; restTicks is server-only.
    private static final EntityDataAccessor<Boolean> DATA_RESTING =
            SynchedEntityData.defineId(AmericanLobsterEntity.class, EntityDataSerializers.BOOLEAN);

    private int snipCooldown;
    private int restTicks;
    private int restCheckTimer;
    /** 0.18 — scheduled pinch: damage lands at the snip clip's impact frame, not on trigger. */
    private LivingEntity pendingSnipTarget;
    private int snipImpactTicks;

    protected AmericanLobsterEntity(EntityType<AmericanLobsterEntity> type, Level level) {
        super(type, level);
        // The lobster showed the same close-waypoint orbit as sharks. Reuse their
        // turn-aware arrival braking, but keep pitch disabled for a seafloor crawler.
        this.moveControl = new SharkSwimmingMoveControl(this, 1f / 8f, false);
        this.setMaxUpStep(0.6f);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10)
                .add(Attributes.MOVEMENT_SPEED, 0.6F)
                .add(Attributes.ATTACK_DAMAGE, 1);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
        this.entityData.define(DATA_RESTING, false);
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
    protected float wanderRadiusXZ() { return 12f; }
    // Suggestion 1: tight vertical wander so SetRandomSwimTarget stops picking mid-water
    // destinations that drag the lobster off the seafloor (mirrors the stingray's setRadius(8,1)).
    @Override
    protected float wanderRadiusY() { return 1f; }
    @Override
    protected boolean lockHorizontalPitch() { return true; }
    @Override
    protected float swimSpeedMultiplier() { return 0.10f; }
    @Override
    protected float maxHorizontalSpeed() { return 0.18f; }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    public void travel(@NotNull Vec3 movementInput) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed() * swimSpeedMultiplier(), movementInput);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.85));
            Vec3 dm = this.getDeltaMovement();
            double horiz = Math.sqrt(dm.x * dm.x + dm.z * dm.z);
            float cap = maxHorizontalSpeed();
            if (horiz > cap) {
                double s = cap / horiz;
                this.setDeltaMovement(dm.x * s, dm.y, dm.z * s);
            }
            // Suggestion 1: persistent sink so the lobster settles on the sea floor instead of
            // floating (matches the bottom-dwelling Common Stingray's -0.04 bias). -0.005 was too
            // weak to counter the buoyant yya the move control generates toward wander targets.
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (snipCooldown > 0) snipCooldown--;
        if (restTicks > 0) {
            restTicks--;
            setDeltaMovement(getDeltaMovement().scale(0.4));
        }
        if (restCheckTimer-- <= 0) {
            restCheckTimer = 60 + getRandom().nextInt(61);
            if (restTicks == 0 && getRandom().nextFloat() < 0.5f) {
                restTicks = 100 + getRandom().nextInt(301);
            }
        }
        // 0.18 — land the scheduled snip at the claw clip's impact frame. Ben reported the
        // animation trailing the damage: the old code hurt the player the same tick the snip
        // (and its 0.5s clip) was queued, so the pinch always preceded the visible snap.
        if (pendingSnipTarget != null && --snipImpactTicks <= 0) {
            if (pendingSnipTarget.isAlive()
                    && getBoundingBox().inflate(1.5).intersects(pendingSnipTarget.getBoundingBox())) {
                pendingSnipTarget.hurt(damageSources().mobAttack(this), 1.0f);
            }
            pendingSnipTarget = null;
        }
        if (snipCooldown <= 0 && tickCount % 10 == 0) {
            AABB area = getBoundingBox().inflate(1.0);
            List<Player> nearby = level().getEntitiesOfClass(Player.class, area,
                    p -> !p.isCreative() && !p.isSpectator());
            if (!nearby.isEmpty()) {
                pendingSnipTarget = nearby.get(0);
                snipImpactTicks = 7; // snip clip peaks ~0.3s in, +1 tick of trigger latency
                snipCooldown = 30;
                restTicks = 0;
            }
        }
        this.entityData.set(DATA_RESTING, restTicks > 0);
    }

    public boolean isSnipping() { return snipCooldown > 20; }
    public boolean isResting() { return this.entityData.get(DATA_RESTING); }

    public enum Variant implements StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2"),
        DEFAULT_3(2, "default_3"),
        BLUE(3,      "blue"),
        RED(4,       "red");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        // Common colors get most of the spawns, blue/red are rare.
        private static final SimpleWeightedRandomList<Variant> NATURAL_VARIANTS = SimpleWeightedRandomList.<Variant>builder()
                .add(DEFAULT_1, 50)
                .add(DEFAULT_2, 50)
                .add(DEFAULT_3, 50)
                .add(BLUE, 2)
                .add(RED, 5)
                .build();

        private final int id;
        private final String name;

        Variant(int id, String name) { this.id = id; this.name = name; }
        public int getId() { return id; }
        public String getName() { return name; }
        @Override public @NotNull String getSerializedName() { return name; }
        public static Variant byId(int id) { return BY_ID.apply(id); }
        public static Variant getSpawnVariant(RandomSource random) { return NATURAL_VARIANTS.getRandomValue(random).orElseThrow(); }
    }

    @Override public float bfsScaleMin() { return 0.5f; }
    @Override public float bfsScaleMax() { return 1.0f; }
}
