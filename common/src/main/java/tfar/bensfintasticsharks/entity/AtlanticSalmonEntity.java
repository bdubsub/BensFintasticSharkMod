package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.level.Level;

public class AtlanticSalmonEntity extends Salmon {

    protected AtlanticSalmonEntity(EntityType<? extends Salmon> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes();
    }

    public boolean isNamedSpin() {
        return hasCustomName() && "Spin".equals(getCustomName().getString());
    }

}
