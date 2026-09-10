package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.phys.Vec3;

/** Fish swimming with finite destination approaches and pitch aligned propulsion. */
public final class BfsFishMoveControl extends PitchSwimmingMoveControl {

    public BfsFishMoveControl(AbstractFish fish) {
        this(fish, AquaticMovement.DEFAULT_UPWARD_PITCH_LIMIT,
                AquaticMovement.DEFAULT_DOWNWARD_PITCH_LIMIT);
    }

    public BfsFishMoveControl(AbstractFish fish, float upwardPitchLimit, float downwardPitchLimit) {
        this(fish, upwardPitchLimit, downwardPitchLimit,
                AquaticMovement.FISH_HARD_UPWARD_PITCH_LIMIT, AquaticMovement.FISH_HARD_DOWNWARD_PITCH_LIMIT);
    }

    public BfsFishMoveControl(AbstractFish fish, float upwardPitchLimit, float downwardPitchLimit,
                               float verticalUpwardPitchLimit, float verticalDownwardPitchLimit) {
        super(fish, 1.0F, true, verticalUpwardPitchLimit, verticalDownwardPitchLimit,
                AquaticMovement.FISH_VERTICAL_SPEED_RATIO);
    }

    static void travel(AbstractFish fish, Vec3 movementInput) {
        if (fish.getMoveControl() instanceof BfsFishMoveControl control) {
            control.travel(0.01, 0.9, Math.abs(fish.getSpeed()), 0, movementInput);
        }
    }
}
