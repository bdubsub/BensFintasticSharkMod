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
}
