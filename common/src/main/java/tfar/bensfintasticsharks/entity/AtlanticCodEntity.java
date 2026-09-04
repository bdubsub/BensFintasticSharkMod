package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AtlanticCodEntity extends Cod {

    protected AtlanticCodEntity(EntityType<? extends Cod> type, Level level) {
        super(type, level);
        this.moveControl = new BfsFishMoveControl(this);
        this.lookControl = new BfsFishLookControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes();
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
