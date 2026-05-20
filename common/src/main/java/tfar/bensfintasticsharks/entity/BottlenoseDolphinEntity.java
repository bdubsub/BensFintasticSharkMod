package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.BrainUtils;

import java.util.Comparator;
import java.util.List;

public class BottlenoseDolphinEntity extends BfsAquaticEntity<BottlenoseDolphinEntity> implements BfsVariantHolder {

    private static final net.minecraft.network.syncher.EntityDataAccessor<Integer> DATA_VARIANT =
            net.minecraft.network.syncher.SynchedEntityData.defineId(BottlenoseDolphinEntity.class, net.minecraft.network.syncher.EntityDataSerializers.INT);

    private int airTicks;
    private int breachCooldown;
    private int graceCooldown;
    private boolean queuedBreach;
    private int boatScanCooldown;
    private int itemPlayCooldown;
    private int boatFollowTicksLeft;

    protected BottlenoseDolphinEntity(EntityType<BottlenoseDolphinEntity> type, Level level) {
        super(type, level);
        this.airTicks = 600 + level.getRandom().nextInt(601);
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
                .add(Attributes.MAX_HEALTH, 22)
                .add(Attributes.MOVEMENT_SPEED, 1.2F)
                .add(Attributes.ATTACK_DAMAGE, 2);
    }

    @Override
    protected float wanderRadiusXZ() { return 56f; }
    @Override
    protected float wanderRadiusY() { return 24f; }
    @Override
    protected int wanderWeight() { return 9; }
    @Override
    protected int idleWeight() { return 1; }

    public boolean isBreaching() {
        return queuedBreach;
    }

    /** True for the Apex-of-Apex advancement: dolphin has been actively boat-following for ≥30s. */
    public boolean isFollowingBoat() {
        return boatFollowTicksLeft > 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (level() instanceof ServerLevel sl) {
            tickAirAndSurface(sl);
            tickRandomBreach(sl);
            tickDolphinsGrace();
            tickBoatFollow();
            tickItemPlay();
        }
    }

    private void tickBoatFollow() {
        if (boatFollowTicksLeft > 0) boatFollowTicksLeft--;
        if (boatScanCooldown-- > 0) return;
        boatScanCooldown = 20;
        AABB area = getBoundingBox().inflate(16.0);
        List<Boat> nearby = level().getEntitiesOfClass(Boat.class, area,
                boat -> boat.getControllingPassenger() instanceof Player
                        && boat.getDeltaMovement().lengthSqr() > 0.001);
        if (nearby.isEmpty()) return;
        Boat closest = nearby.stream()
                .min(Comparator.comparingDouble(b -> b.distanceToSqr(this)))
                .orElseThrow();
        // Follow at 1.3× speed, target 4 blocks behind the boat for that "play" feel.
        Vec3 boatForward = closest.getViewVector(1.0f).normalize().scale(-3.0);
        Vec3 target = closest.position().add(boatForward);
        BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET,
                new WalkTarget(target, 1.3f, 1));
        boatFollowTicksLeft = Math.max(boatFollowTicksLeft, 600);
    }

    private void tickItemPlay() {
        if (itemPlayCooldown-- > 0) return;
        itemPlayCooldown = 40;
        AABB area = getBoundingBox().inflate(8.0);
        List<ItemEntity> items = level().getEntitiesOfClass(ItemEntity.class, area, ItemEntity::isInWater);
        if (items.isEmpty()) return;
        ItemEntity closest = items.stream()
                .min(Comparator.comparingDouble(this::distanceToSqr))
                .orElseThrow();
        if (distanceToSqr(closest) < 2.5) {
            // Nudge the item: small upward + random horizontal velocity. Item NOT consumed.
            closest.setDeltaMovement(
                    (getRandom().nextDouble() - 0.5) * 0.4,
                    0.3,
                    (getRandom().nextDouble() - 0.5) * 0.4);
            itemPlayCooldown = 120;
        } else {
            BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET,
                    new WalkTarget(closest.position(), 1.1f, 1));
        }
    }

    private void tickAirAndSurface(ServerLevel sl) {
        if (airTicks > 0) { airTicks--; return; }
        // When out of air: swim upward and emit splash + reset air.
        BlockPos here = blockPosition();
        int surface = sl.getHeight(Heightmap.Types.WORLD_SURFACE_WG, here.getX(), here.getZ());
        if (here.getY() < surface - 1 && isInWater()) {
            Vec3 v = getDeltaMovement();
            setDeltaMovement(v.x, Math.max(v.y, 0.18), v.z);
        }
        if (!isInWater() || here.getY() >= surface - 1) {
            sl.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, getX(), getY() + 1, getZ(),
                    4, 0.2, 0.1, 0.2, 0.0);
            sl.playSound(null, here, SoundEvents.DOLPHIN_SPLASH, SoundSource.NEUTRAL, 0.8f, 1.0f);
            airTicks = 600 + getRandom().nextInt(601);
        }
    }

    private void tickRandomBreach(ServerLevel sl) {
        if (breachCooldown > 0) { breachCooldown--; queuedBreach = false; return; }
        if (!isInWater()) return;
        BlockPos here = blockPosition();
        int surface = sl.getHeight(Heightmap.Types.WORLD_SURFACE_WG, here.getX(), here.getZ());
        if (here.getY() < surface - 4) { queuedBreach = false; return; }
        if (tickCount % 50 != 0) return;
        if (getRandom().nextFloat() < 0.05f) {
            setDeltaMovement(getDeltaMovement().x, 1.2, getDeltaMovement().z);
            sl.playSound(null, here, SoundEvents.DOLPHIN_JUMP, SoundSource.NEUTRAL, 1.0f, 1.0f);
            queuedBreach = true;
            breachCooldown = 200;
        }
    }

    private void tickDolphinsGrace() {
        if (graceCooldown > 0) { graceCooldown--; return; }
        AABB inner = getBoundingBox().inflate(9.0);
        List<Player> nearby = level().getEntitiesOfClass(Player.class, inner,
                p -> p.isSwimming() && p.getFluidHeight(FluidTags.WATER) > 0
                        && !p.isCreative() && !p.isSpectator());
        for (Player p : nearby) {
            p.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 100, 0, false, false, true));
        }
        graceCooldown = 40;
    }

    public enum Variant implements net.minecraft.util.StringRepresentable {
        DEFAULT_1(0,             "default_1"),
        VANILLA(1,               "vanilla"),
        WHITEBELLY(2,            "whitebelly"),
        BLACKTIP(3,              "blacktip"),
        BLACKTIP_WHITEBELLY(4,   "blacktip_whitebelly");

        private static final java.util.function.IntFunction<Variant> BY_ID = net.minecraft.util.ByIdMap.continuous(Variant::getId, values(), net.minecraft.util.ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final com.mojang.serialization.Codec<Variant> CODEC = net.minecraft.util.StringRepresentable.fromEnum(Variant::values);

        private static final net.minecraft.util.random.SimpleWeightedRandomList<Variant> NATURAL_VARIANTS =
                net.minecraft.util.random.SimpleWeightedRandomList.<Variant>builder()
                        .add(DEFAULT_1, 1).add(VANILLA, 1).add(WHITEBELLY, 1)
                        .add(BLACKTIP, 1).add(BLACKTIP_WHITEBELLY, 1).build();

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
