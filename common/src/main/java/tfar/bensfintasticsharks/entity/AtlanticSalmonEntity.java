package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AtlanticSalmonEntity extends Salmon {

    private static final EntityDataAccessor<Boolean> DATA_FAST_SWIM =
            SynchedEntityData.defineId(AtlanticSalmonEntity.class, EntityDataSerializers.BOOLEAN);

    protected AtlanticSalmonEntity(EntityType<? extends Salmon> type, Level level) {
        super(type, level);
        this.moveControl = new BfsFishMoveControl(this,
                AquaticMovement.SALMON_PITCH_LIMIT, AquaticMovement.SALMON_PITCH_LIMIT);
        this.lookControl = new BfsFishLookControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes();
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

    public boolean isNamedSpin() {
        return hasCustomName() && "Spin".equals(getCustomName().getString());
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
