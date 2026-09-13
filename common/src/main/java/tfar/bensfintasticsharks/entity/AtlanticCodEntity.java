package tfar.bensfintasticsharks.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AtlanticCodEntity extends Cod {

    private static final EntityDataAccessor<Boolean> DATA_FAST_SWIM =
            SynchedEntityData.defineId(AtlanticCodEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DATA_BFS_SCALE =
            SynchedEntityData.defineId(AtlanticCodEntity.class, EntityDataSerializers.FLOAT);

    protected AtlanticCodEntity(EntityType<? extends Cod> type, Level level) {
        super(type, level);
        this.moveControl = new BfsFishMoveControl(this,
                AquaticMovement.COD_PITCH_LIMIT, AquaticMovement.COD_PITCH_LIMIT,
                AquaticMovement.FISH_HARD_UPWARD_PITCH_LIMIT,
                AquaticMovement.FISH_HARD_DOWNWARD_PITCH_LIMIT);
        this.lookControl = new BfsFishLookControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes();
    }

    @Override
    protected net.minecraft.world.entity.ai.navigation.PathNavigation createNavigation(Level level) {
        return new PitchSwimmingNavigation(this, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_FAST_SWIM, false);
        entityData.define(DATA_BFS_SCALE, 1.0F);
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance diff,
                                        @NotNull MobSpawnType reason, @Nullable SpawnGroupData data,
                                        @Nullable CompoundTag tag) {
        setBfsScale(BfsScaleUtil.roll(this, getRandom(), 1.0F, 1.0F));
        return super.finalizeSpawn(level, diff, reason, data, tag);
    }

    public float getBfsScale() { return entityData.get(DATA_BFS_SCALE); }
    public void setBfsScale(float value) { entityData.set(DATA_BFS_SCALE, Math.max(0.25F, Math.min(2.0F, value))); }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("BfsScale", getBfsScale());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BfsScale")) setBfsScale(tag.getFloat("BfsScale"));
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return BfsScaleUtil.scale(super.getDimensions(pose), getBfsScale());
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            entityData.set(DATA_FAST_SWIM,
                    isInWaterOrBubble() && getDeltaMovement().lengthSqr() > 0.0225D);
            SpeciesBehaviorEngine.tickFish(this);
        }
    }

    public boolean isFastSwim() {
        return entityData.get(DATA_FAST_SWIM);
    }

    @Override
    public void travel(Vec3 movementInput) {
        if (MovementIntentOverrides.active(this) || (isEffectiveAi() && isInWater())) {
            BfsFishMoveControl.travel(this, movementInput);
        } else {
            super.travel(movementInput);
        }
    }
}
