package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.animal.AbstractFish;

/** Keeps the fish pitch supplied by the swim controller while still turning its head. */
final class BfsFishLookControl extends LookControl {

    private final AbstractFish fish;

    BfsFishLookControl(AbstractFish fish) {
        super(fish);
        this.fish = fish;
    }

    @Override
    public void tick() {
        float movementPitch = fish.getXRot();
        super.tick();
        fish.setXRot(movementPitch);
    }

    @Override
    protected boolean resetXRotOnTick() {
        return false;
    }
}
