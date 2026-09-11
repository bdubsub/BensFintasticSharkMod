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
        if (fish.getMoveControl() instanceof BfsFishMoveControl moveControl && fish.isInWater()
                && moveControl.hasWanted()) {
            // Movement owns the body bearing while a navigation route is active. Letting the
            // generic look target overwrite it adds a second yaw writer and makes a fish hunt
            // left and right around an otherwise stable path.
            float movementYaw = fish.getYRot();
            fish.yBodyRot = movementYaw;
            fish.yHeadRot = movementYaw;
            return;
        }
        float movementPitch = fish.getXRot();
        super.tick();
        fish.setXRot(movementPitch);
    }

    @Override
    protected boolean resetXRotOnTick() {
        return false;
    }
}
