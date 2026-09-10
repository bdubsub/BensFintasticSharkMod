package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AtlanticCodEntity extends Cod {

    private static final EntityDataAccessor<Boolean> DATA_FAST_SWIM =
            SynchedEntityData.defineId(AtlanticCodEntity.class, EntityDataSerializers.BOOLEAN);

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
        if (isEffectiveAi() && isInWater()) {
            BfsFishMoveControl.travel(this, movementInput);
        } else {
            super.travel(movementInput);
        }
    }
}
