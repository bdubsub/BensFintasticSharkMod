package tfar.bensfintasticsharks.entity;

import net.minecraft.util.Mth;

/** Shared movement math for aquatic entities whose vertical thrust follows the approved oracle. */
public final class AquaticMovement {

    /** Affected fish and shark vertical thrust compared with the Bottlenose Dolphin reference. */
    public static final double VERTICAL_SPEED_RATIO = 0.10d;

    /** Conservative routine powered pitch profiles from the DEC-006 engineering envelope. */
    public static final float COD_PITCH_LIMIT = 8.0f;
    public static final float SALMON_PITCH_LIMIT = 10.0f;
    public static final float OCEANIC_WHITETIP_PITCH_LIMIT = 10.0f;
    public static final float TIGER_UPWARD_PITCH_LIMIT = 14.0f;
    public static final float TIGER_DOWNWARD_PITCH_LIMIT = 5.0f;
    public static final float DEFAULT_UPWARD_PITCH_LIMIT = 10.0f;
    public static final float DEFAULT_DOWNWARD_PITCH_LIMIT = 10.0f;

    /** Six degrees per second at the nominal 20 tick server rate. */
    public static final float MAX_PITCH_STEP_DEGREES_PER_TICK = 0.30f;

    private AquaticMovement() {
    }

    /**
     * Returns the nose pitch for the actual affected-entity travel vector. The vertical component
     * is scaled before the angle is derived, so the body never points along a steeper path than
     * the motion it is actually making.
     */
    public static float affectedPitch(double dx, double dy, double dz) {
        return affectedPitch(dx, dy, dz, DEFAULT_UPWARD_PITCH_LIMIT, DEFAULT_DOWNWARD_PITCH_LIMIT);
    }

    /**
     * Returns a bounded nose pitch for the supplied target vector. A target directly above or
     * below has no horizontal direction from which to derive an attitude, so use the shallow
     * angle implied by the approved vertical ratio instead of ever pointing the model straight
     * up or down.
     */
    public static float affectedPitch(double dx, double dy, double dz,
                                      float upwardLimit, float downwardLimit) {
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDistance <= 1.0e-8 && Math.abs(dy) <= 1.0e-8) {
            return 0.0f;
        }
        double shallowAngle = Math.atan(VERTICAL_SPEED_RATIO) * Mth.RAD_TO_DEG;
        if (horizontalDistance <= 1.0e-8) {
            float limit = dy > 0.0 ? upwardLimit : downwardLimit;
            float pitch = (float) (dy > 0.0 ? -shallowAngle : shallowAngle);
            return Mth.clamp(pitch, -Math.abs(limit), Math.abs(limit));
        }
        float pitch = (float) -(Math.atan2(dy * VERTICAL_SPEED_RATIO, horizontalDistance) * Mth.RAD_TO_DEG);
        return Mth.clamp(pitch, -Math.abs(upwardLimit), Math.abs(downwardLimit));
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
