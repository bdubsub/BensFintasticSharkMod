package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AtlanticSalmonEntity extends Salmon {

    protected AtlanticSalmonEntity(EntityType<? extends Salmon> type, Level level) {
        super(type, level);
        this.moveControl = new BfsFishMoveControl(this,
                AquaticMovement.SALMON_PITCH_LIMIT, AquaticMovement.SALMON_PITCH_LIMIT);
        this.lookControl = new BfsFishLookControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes();
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
