package tfar.bensfintasticsharks.entity;

import net.minecraft.util.Mth;

/** Shared movement math for aquatic entities whose vertical thrust follows the approved oracle. */
public final class AquaticMovement {

    /** Affected fish and shark vertical thrust compared with the Bottlenose Dolphin reference. */
    public static final double VERTICAL_SPEED_RATIO = 0.10d;

    private AquaticMovement() {
    }

    /**
     * Returns the nose pitch for the actual affected-entity travel vector. The vertical component
     * is scaled before the angle is derived, so the body never points along a steeper path than
     * the motion it is actually making.
     */
    public static float affectedPitch(double dx, double dy, double dz) {
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDistance <= 1.0e-8 && Math.abs(dy) <= 1.0e-8) {
            return 0.0f;
        }
        return (float) -(Math.atan2(dy * VERTICAL_SPEED_RATIO, horizontalDistance) * Mth.RAD_TO_DEG);
    }

    /**
     * Returns the affected entity's vertical control component for one target vector. Keeping
     * this beside {@link #affectedPitch(double, double, double)} makes the pitch and vertical
     * movement use the same scaled three dimensional direction.
     */
    public static double affectedVerticalVelocity(double speed, double dx, double dy, double dz) {
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance <= 1.0e-8) {
            return 0.0d;
        }
        return speed * dy / distance * VERTICAL_SPEED_RATIO;
    }

    /**
     * Eases a vertical control value toward its target using the same one-eighth response already
     * used by the vanilla fish speed controller. Keeping the response shared prevents a target
     * reversal from becoming an instantaneous vertical impulse on one aquatic species only.
     */
    public static double smoothVerticalVelocity(double current, double target) {
        return Mth.lerp(0.125d, current, target);
    }
}
