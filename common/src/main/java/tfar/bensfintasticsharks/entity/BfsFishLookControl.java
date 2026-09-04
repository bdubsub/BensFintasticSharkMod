package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.animal.AbstractFish;

/** Keeps the fish pitch supplied by the swim controller while still turning its head. */
final class BfsFishLookControl extends LookControl {

    BfsFishLookControl(AbstractFish fish) {
        super(fish);
    }

    @Override
    protected boolean resetXRotOnTick() {
        return false;
    }
}
