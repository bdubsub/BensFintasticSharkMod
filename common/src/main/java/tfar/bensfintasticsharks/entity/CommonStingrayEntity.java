package tfar.bensfintasticsharks.entity;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomSwimTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public class CommonStingrayEntity extends SmartWaterAnimal<CommonStingrayEntity>
        implements BfsVariantHolder {

    @Override public int bfsVariantCount() { return Variant.values().length; }
    @Override public void setBfsVariantId(int id) {
        int n = bfsVariantCount();
        setVariant(Variant.byId(((id % n) + n) % n));
    }

    private static final Predicate<LivingEntity> DANGEROUS = living -> {
        if (living instanceof Player player && player.isCreative()) {
            return false;
        } else {
            return !(living instanceof CommonStingrayEntity);
        }
    };

    static final TargetingConditions targetingConditions = TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight().selector(DANGEROUS);


    protected boolean stinging;

    protected CommonStingrayEntity(EntityType<CommonStingrayEntity> $$0, Level $$1) {
        super($$0, $$1);

        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 1 / 10f, 0, false);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(CommonStingrayEntity.class, EntityDataSerializers.INT);

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10).add(Attributes.MOVEMENT_SPEED, 1.2F).add(Attributes.ATTACK_DAMAGE, 2);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT, 0);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        RandomSource randomsource = pLevel.getRandom();
        this.setVariant(Variant.getSpawnVariant(randomsource));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
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
    public List<? extends ExtendedSensor<? extends CommonStingrayEntity>> getSensors() {
        return List.of();
    }

    @Override
    public BrainActivityGroup<CommonStingrayEntity> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),                      // Have the entity turn to face and look at its current look target
                new MoveToWalkTarget<>());
    }

    @Override
    public BrainActivityGroup<CommonStingrayEntity> getIdleTasks() {
        // Stingrays hug the seafloor — small horizontal wander, tiny vertical range,
        // and lots of idling so they basically rest on the bottom.
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()),
                new OneRandomBehaviour<>(
                        com.mojang.datafixers.util.Pair.of(new SetRandomSwimTarget<CommonStingrayEntity>().setRadius(8, 1), 3),
                        com.mojang.datafixers.util.Pair.of(new Idle<CommonStingrayEntity>().runFor(entity -> entity.getRandom().nextInt(120, 240)), 7)));
    }

    /**
     * Stingrays sink to the seafloor. Heavy downward bias counteracts the buoyancy
     * other water mobs have. They effectively settle on the ocean floor unless
     * actively walking to a target via brain.
     */
    @Override
    public void travel(@org.jetbrains.annotations.NotNull net.minecraft.world.phys.Vec3 movementInput) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed() * 0.5f, movementInput);
            this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.6));
            // Persistent sink toward the seafloor.
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        } else {
            super.travel(movementInput);
        }
    }


    public Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_VARIANT));
    }

    public void setVariant(Variant pVariant) {
        this.entityData.set(DATA_VARIANT, pVariant.getId());
    }

    //. Default Skin 1 (Common Spawn Rate)
    //ii. Default Skin 2 (Common Spawn Rate)
    //iii. Default Skin 3 (Common Spawn Rate)
    //iv. Melanistic (Extremely Rare Spawn Rate)
    //v. Albino (Rarest Spawn Rate)

    public enum Variant implements StringRepresentable {
        DEFAULT_1(0, "default_1"),
        DEFAULT_2(1, "default_2"),
        DEFAULT_3(2, "default_3"),
        DEFAULT_4(3, "default_4");
        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final Codec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);

        private static final SimpleWeightedRandomList<Variant> NATURAL_VARIANTS = SimpleWeightedRandomList.<Variant>builder()
                .add(DEFAULT_1, 1000)
                .add(DEFAULT_2, 1000)
                .add(DEFAULT_3, 1000)
                .add(DEFAULT_4, 1000)
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

    public void setStinging(boolean stinging) {
        this.stinging = stinging;
    }

    public boolean isStinging() {
        return stinging;
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

    private static final int STING_DURATION_TICKS = 40;

    public void aiStep() {
        super.aiStep();
        // Real stingrays don't need provocation — anything that steps on them gets stung.
        // The old gate (only sting while still wounded by a recent attacker) called a
        // method that returns null on this entity, so stings effectively never fired.
        // Now we sting any mob/player that physically intersects our hitbox.
        stinging = true;
        if (this.isAlive() && !level().isClientSide) {
            this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(0.2D),
                    mob -> targetingConditions.test(this, mob))
                    .stream().filter(LivingEntity::isAlive).forEach(this::touch);
        }
    }

    private void touch(Mob pMob) {
        int i = 2;
        if (pMob.hurt(this.damageSources().mobAttack(this), (float) (1 + i))) {
            pMob.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * i, 0), this);
            this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0F, 1.0F);
        }
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void playerTouch(Player pEntity) {
        if (pEntity.isCreative() || pEntity.isSpectator()) return;
        int i = 2;
        if (pEntity instanceof ServerPlayer && pEntity.hurt(this.damageSources().mobAttack(this), 1 + i)) {
            if (!this.isSilent()) {
                ((ServerPlayer) pEntity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0F));
            }
            pEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * i, 0), this);
        }
    }

}
