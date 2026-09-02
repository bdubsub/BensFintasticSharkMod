package tfar.bensfintasticsharks.entity;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
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
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomSwimTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor;
import org.jetbrains.annotations.NotNull;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModTags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntFunction;

public class ShortfinMakoSharkEntity extends AbstractSharkEntity<ShortfinMakoSharkEntity>
        implements BfsVariantHolder, SharkGrabber {

    @Override public int bfsVariantCount() { return Variant.values().length; }
    @Override public void setBfsVariantId(int id) {
        int n = bfsVariantCount();
        setVariant(Variant.byId(((id % n) + n) % n));
    }


    private static final SharkParams MAKO_PARAMS = new SharkParams(
            /* detectionRadius      */ 28.0f,
            /* bloodDetectionRadius */ 42.0f,
            /* aggressionLevel      */ 85,
            // Aggro speed multiplier dialled back so a mako doesn't laser-track the player.
            // Still the fastest shark in the mod via the lower MOVEMENT_SPEED base + chase mult.
            /* aggroSpeedMult       */ 1.3f,
            /* disengageDistance    */ 80.0f,
            /* disengageTimeoutTicks*/ 400,
            /* biteCooldownTicks    */ 14,
            /* biteDamage           */ 4.0f
    );

    protected ShortfinMakoSharkEntity(EntityType<ShortfinMakoSharkEntity> $$0, Level $$1) {
        super($$0, $$1, MAKO_PARAMS);


        this.moveControl = new SharkSwimmingMoveControl(this, 1 / 8f);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(ShortfinMakoSharkEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_GRAB_TIMER = SynchedEntityData.defineId(ShortfinMakoSharkEntity.class, EntityDataSerializers.INT);


    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 130).add(Attributes.MOVEMENT_SPEED, 0.80F).add(Attributes.ATTACK_DAMAGE, 6);
    }

    public void grabMob(LivingEntity entity) {
        if (entity != this.getTarget() || entity.isPassenger() || !this.isInWaterOrBubble()) return;
        if (!entity.startRiding(this, true)) return;
        if (entity instanceof ServerPlayer serverPlayer)
            serverPlayer.connection.send(new ClientboundSetPassengersPacket(this));
        setGrabTimer(BensFintasticSharks.GRAB_TIMER);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_VARIANT, 0);
        entityData.define(DATA_GRAB_TIMER, 0);
    }


    @Override
    protected void positionRider(Entity entity, MoveFunction function) {
        Vec3 look = getLookAngle();
        // Pull the held victim right up against the front of the shark's bounding box so
        // it looks like it's actually being chomped, not floating two blocks away.
        float dist = 1.1f;


        double angle = computeGrabAngle();

        double rotatedx = look.x * Mth.cos((float) (angle * Math.PI / 180)) - look.z * Mth.sin((float) (angle * Math.PI / 180));
        double rotatedz = look.x * Mth.sin((float) (angle * Math.PI / 180)) + look.z * Mth.cos((float) (angle * Math.PI / 180));

        double offsetX = dist * rotatedx;
        double offsetZ = dist * rotatedz;

        double thisHeight = getDimensions(getPose()).height;
        double height = entity.getDimensions(entity.getPose()).height;

        function.accept(entity, getX() + offsetX, getY() + thisHeight / 2 - height / 2, getZ() + offsetZ);
    }

    @Override
    public List<? extends ExtendedSensor<ShortfinMakoSharkEntity>> getSensors() {
        NearbyLivingEntitySensor<ShortfinMakoSharkEntity> nearbyLivingEntitySensor = new NearbyLivingEntitySensor<>();
        nearbyLivingEntitySensor.setPredicate((target, entity) -> canTarget(target));
        return List.of(nearbyLivingEntitySensor, // This tracks nearby entities
                new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<? extends ShortfinMakoSharkEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());
    }


    float computeGrabAngle() {
        int grabTimer = BensFintasticSharks.GRAB_TIMER - getGrabTimer();

        int mod = (grabTimer + 3) % 40;

        float degrees = mod * 24;
        float angle = 35 * Mth.sin((float) (degrees * Math.PI / 180));
        return angle;

        //.25 - .1 full wave

    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        int grabCountdown = getGrabTimer();
        if (grabCountdown > 0) {
            int next = grabCountdown - 1;
            boolean livingPassenger = getPassengers().stream()
                    .anyMatch(passenger -> passenger instanceof LivingEntity living && living.isAlive());
            if (next <= 0 || !isAlive() || !isInWaterOrBubble() || !livingPassenger) {
                setGrabTimer(0);
                ejectPassengers();
                return;
            }
            // Thrash damage: while a victim is being held, deal a tick of damage every 10t.
            // Without this the grab is purely visual and the prey just hitches a ride home.
            if (next % 10 == 0) {
                for (net.minecraft.world.entity.Entity p : getPassengers()) {
                    if (p instanceof net.minecraft.world.entity.LivingEntity le && le.isAlive()) {
                        le.hurt(this.damageSources().mobAttack(this), 2.0f);
                    }
                }
            }
            setGrabTimer(next);
        }
    }

    void setGrabTimer(int timer) {
        entityData.set(DATA_GRAB_TIMER, Math.max(0, timer));
    }

    @Override
    public int getGrabTimer() {
        return entityData.get(DATA_GRAB_TIMER);
    }

    @Override
    public void remove(RemovalReason reason) {
        setGrabTimer(0);
        ejectPassengers();
        super.remove(reason);
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity pEntity) {
        float reach = this.getBbWidth() + pEntity.getBbWidth();
        return (reach * reach) / 4f;
    }

    @Override
    public double getPerceivedTargetDistanceSquareForMeleeAttack(LivingEntity pEntity) {
        return getAttackPosition().distanceToSqr(pEntity.position());
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level pLevel) {
        return new WaterBoundPathNavigation(this, pLevel) {
            @Override
            protected boolean canUpdatePath() {
                return true;
            }

            @Override
            protected @NotNull PathFinder createPathFinder(int pMaxVisitedNodes) {
                nodeEvaluator = new AmphibiousNodeEvaluator(true);
                nodeEvaluator.setCanOpenDoors(false);
                return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
            }
        };
    }

    @Override
    public BrainActivityGroup<ShortfinMakoSharkEntity> getIdleTasks() {
        // These are the tasks that run when the mob isn't doing anything else (usually)
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(      // Run only one of the below behaviours, trying each one in order. Include the generic type because JavaC is silly
                        new TargetOrRetaliate<>()
                                // Base hurt() owns retaliation; this sensor only acquires
                                // hunger-gated prey and must not enlist nearby player blood.
                                .attackablePredicate(entity -> this.isInWaterOrBubble()
                                        && entity.isAlive() && canHuntTarget(entity)),
                        new SetPlayerLookTarget<>(),          // Set the look target for the nearest player
                        new SetRandomLookTarget<>()),         // Set a random look target
                new OneRandomBehaviour<>(                 // Run a random task from the below options
                        new SetRandomSwimTarget<>().setRadius(30, 21),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60)))); // Do nothing for 1.5->3 seconds
    }


    protected Vec3 getAttackPosition() {
        Vec3 bottomCenter = position();
        Vec3 center = bottomCenter.add(0, getBbHeight() / 2, 0);
        Vec3 look = getLookAngle();
        float scale = 2;
        return center.add(look.x * scale, 0, look.z * scale);
    }

    // 0.19 — Part II hunger spec: delegate to the shared predicate. Note this drops the
    // 0.11-era "never attack other sharks" blanket rule on purpose — Ben's prey list puts
    // blacktip reef sharks on the mako's menu (same-species is still excluded).
    public boolean canTarget(LivingEntity target) {
        return canHuntTarget(target);
    }

    @Override
    protected net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> preyTag() {
        return ModTags.EntityTypes.SHORTFIN_MAKO_SHARK_PREY;
    }

    @Override
    public void travel(@NotNull Vec3 movementInput) {
        if (this.tickCount % 10 == 0)
            this.refreshDimensions();

        if (isEffectiveAi() && this.isInWater()) {
            // 0.19 — shared swim step: adds the chase-acceleration burst, in-range brake and
            // backslide damping that this bespoke override was missing.
            swimInWater(movementInput, 0.65, 0.005, false);
        } else
            super.travel(movementInput);
    }

    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(ShortfinMakoSharkEntity.Variant.byId(tag.getInt("Variant")));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant().getId());
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        this.setVariant(ShortfinMakoSharkEntity.Variant.getSpawnVariant(randomsource));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    protected float getStandingEyeHeight(Pose $$0, EntityDimensions $$1) {
        return $$1.height * 0.65F;
    }


    public ShortfinMakoSharkEntity.Variant getVariant() {

        return ShortfinMakoSharkEntity.Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(ShortfinMakoSharkEntity.Variant pVariant) {
        this.entityData.set(DATA_VARIANT, pVariant.getId());
    }

    public enum Variant implements StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2"),
        DEFAULT_3(2, "default_3"),
        MELANISTIC(3, "melanistic"),
        ALBINO(4, "albino");

        private static final IntFunction<ShortfinMakoSharkEntity.Variant> BY_ID = ByIdMap.continuous(ShortfinMakoSharkEntity.Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<ShortfinMakoSharkEntity.Variant> CODEC = StringRepresentable.fromEnum(ShortfinMakoSharkEntity.Variant::values);

        private static final SimpleWeightedRandomList<ShortfinMakoSharkEntity.Variant> NATURAL_VARIANTS = SimpleWeightedRandomList.<ShortfinMakoSharkEntity.Variant>builder()
                .add(DEFAULT_1,1000)
                .add(DEFAULT_2,1000)
                .add(DEFAULT_3,1000)
                .add(MELANISTIC,10)
                .add(ALBINO,5)
                .build();

        private final int id;
        private final String name;

        Variant(int pId, String pName) {
            this.id = pId;
            this.name = pName;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public static ShortfinMakoSharkEntity.Variant byId(int pId) {
            return BY_ID.apply(pId);
        }

        private static ShortfinMakoSharkEntity.Variant getSpawnVariant(RandomSource pRandom) {
            return NATURAL_VARIANTS.getRandomValue(pRandom).orElseThrow();
        }
    }

    // 0.18 — Ben: base model ≈4 m; spawn sizes should span 3 m to 5 m.
    @Override public float bfsScaleMin() { return 0.75f; }
    @Override public float bfsScaleMax() { return 1.25f; }

    // Fastest shark in the ocean — highest chase floor in the mod (~8.4 m/s vs the
    // player's ~5.6 m/s sprint-swim). Its MOVEMENT_SPEED attribute is deliberately
    // low for calm cruising, so without this it would be one of the SLOWEST chasers.
    @Override protected float chaseSpeedFloor() { return 0.42f; }

    // Bite-sync (same recipe as Blacktip's 0.18 fix): attack.bite holds the jaw neutral
    // until 0.25s, opens at 0.375s and snaps shut at 0.5s (10t); with the controller's
    // 5-tick blend-in the visible chomp lands ~0.55s in. Default 5t hit way too early.
    @Override protected int biteImpactDelayTicks() { return 11; }
}
