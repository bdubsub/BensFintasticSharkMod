package tfar.bensfintasticsharks.entity;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
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
import net.minecraft.world.entity.animal.WaterAnimal;
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
import tfar.bensfintasticsharks.init.ModTags;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntFunction;

public class CommonThresherSharkEntity extends AbstractSharkEntity<CommonThresherSharkEntity>
        implements ConditionalGlowing, BfsVariantHolder {

    @Override public int bfsVariantCount() { return Variant.values().length; }
    @Override public void setBfsVariantId(int id) {
        int n = bfsVariantCount();
        setVariant(Variant.byId(((id % n) + n) % n));
    }

    private static final SharkParams THRESHER_PARAMS = new SharkParams(
            /* detectionRadius      */ 22.0f,
            /* bloodDetectionRadius */ 32.0f,
            /* aggressionLevel      */ 55,
            /* aggroSpeedMult       */ 1.1f,
            /* disengageDistance    */ 64.0f,
            /* disengageTimeoutTicks*/ 300,
            /* biteCooldownTicks    */ 30,
            /* biteDamage           */ 4.0f
    );

    protected CommonThresherSharkEntity(EntityType<CommonThresherSharkEntity> $$0, Level $$1) {
        super($$0, $$1, THRESHER_PARAMS);

        this.moveControl = new SharkSwimmingMoveControl(this, 1/10f);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(CommonThresherSharkEntity.class, EntityDataSerializers.INT);

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 130).add(Attributes.MOVEMENT_SPEED, 1.2F).add(Attributes.ATTACK_DAMAGE, 6);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    // 0.19 — Part II hunger spec: delegate to the shared predicate (per-species prey tag +
    // hunger gate). The old form targeted ANY mob under 50% health with no cooldown check.
    public boolean canTarget(LivingEntity target) {
        return canHuntTarget(target);
    }

    @Override
    protected net.minecraft.tags.TagKey<net.minecraft.world.entity.EntityType<?>> preyTag() {
        return ModTags.EntityTypes.COMMON_THRESHER_SHARK_PREY;
    }

    // A thresher's tail makes it look faster than its collision body is actually moving.
    // Lower only pursuit acceleration/cap; ordinary swimming retains its existing tuning.
    @Override
    protected float chaseAccelBoost() { return 1.6f; }
    @Override
    protected float chaseSpeedFloor() { return 0.28f; }
    @Override
    protected float maxHorizontalSpeed() {
        return getTarget() != null ? 0.55f : super.maxHorizontalSpeed();
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity pEntity) {
        float reach = this.getBbWidth() + pEntity.getBbWidth();
        return (reach * reach) / 4f;
    }

    @Override
    public double getPerceivedTargetDistanceSquareForMeleeAttack(LivingEntity pEntity) {
        double v = getBiteAttackPosition().distanceToSqr(pEntity.position());
        return v;
        //return Math.max(this.distanceToSqr(pEntity.getMeleeAttackReferencePosition()), this.distanceToSqr(pEntity.position()));
    }

    @Override
    public List<? extends ExtendedSensor<CommonThresherSharkEntity>> getSensors() {
        NearbyLivingEntitySensor<CommonThresherSharkEntity> nearbyLivingEntitySensor = new NearbyLivingEntitySensor<>();
        nearbyLivingEntitySensor.setPredicate((target, entity) -> canTarget(target));
        return List.of(nearbyLivingEntitySensor, // This tracks nearby entities
                new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<CommonThresherSharkEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<CommonThresherSharkEntity> getIdleTasks() {
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
                        new SetRandomSwimTarget<>(),          // Set a random walk target to a nearby position
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60)))); // Do nothing for 1.5->3 seconds
    }

    //this starts at bottom center
    protected Vec3 getBiteAttackPosition() {
        Vec3 bottomCenter = position();
        Vec3 center = bottomCenter.add(0,getBbHeight() / 2,0);
        Vec3 look = getLookAngle();
        float scale = 2;
        return center.add(look.x * scale,0,look.z * scale);
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (this.tickCount % 10 == 0)
            this.refreshDimensions();

        if (isEffectiveAi() && this.isInWater()) {
            // 0.19 — shared swim step: adds the chase-acceleration burst, in-range brake and
            // backslide damping that this bespoke override was missing.
            swimInWater(movementInput, 0.65, 0.005, false);
        } else
            super.travel(movementInput);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        this.setVariant(Variant.getSpawnVariant(randomsource));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public void thunderHit(ServerLevel $$0, LightningBolt $$1) {
        setVariant(Variant.ZIPPY);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setVariant(Variant.byId(tag.getInt("Variant")));
    }


    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", getVariant().getId());
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        return new WaterBoundPathNavigation(this, pLevel) {
            @Override
            protected boolean canUpdatePath() {
                return true;
            }

            @Override
            protected PathFinder createPathFinder(int pMaxVisitedNodes) {
                nodeEvaluator = new AmphibiousNodeEvaluator(true);
                nodeEvaluator.setCanOpenDoors(false);
                return new PathFinder(this.nodeEvaluator, pMaxVisitedNodes);
            }
        };
    }


    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(Variant pVariant) {
        this.entityData.set(DATA_VARIANT, pVariant.getId());
    }

    public boolean isZippy() {
        return getVariant() == Variant.ZIPPY;
    }

    @Override
    public boolean hasGlowingLayer() {
        return isZippy() && level().getMaxLocalRawBrightness(blockPosition()) < 8;
    }

    //i. Default Skin 1 (Common Spawn Rate)
    //ii. Melanistic (Extremely Rare Spawn Rate)
    //iii. Albino Variant (Rarest Spawn Rate)
    //4
    //iv. Zippy (Easter Egg) Requires Shark Trident and Thunderstorm
    //{Glows in the dark}

    public enum Variant implements StringRepresentable {
        DEFAULT_1(0, "default_1"),
        MELANISTIC(1, "melanistic"),
        ALBINO(2, "albino"),
        ZIPPY(3,"zippy");

        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private final int id;
        private final String name;

        private static final SimpleWeightedRandomList<Variant> NATURAL_VARIANTS = SimpleWeightedRandomList.<Variant>builder()
                .add(DEFAULT_1,1000)
                .add(MELANISTIC,10)
                .add(ALBINO,5)
                .build();

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

        public String getSerializedName() {
            return this.name;
        }

        public static Variant byId(int pId) {
            return BY_ID.apply(pId);
        }


        private static Variant getSpawnVariant(RandomSource pRandom) {
            return NATURAL_VARIANTS.getRandomValue(pRandom).orElseThrow();
        }
    }

    @Override public float bfsScaleMin() { return 0.9f; }
    @Override public float bfsScaleMax() { return 1.05f; }
}
