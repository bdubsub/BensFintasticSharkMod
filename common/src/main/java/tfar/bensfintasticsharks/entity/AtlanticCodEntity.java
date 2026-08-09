package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.level.Level;

public class AtlanticCodEntity extends Cod {

    protected AtlanticCodEntity(EntityType<? extends Cod> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractFish.createAttributes();
    }
}
