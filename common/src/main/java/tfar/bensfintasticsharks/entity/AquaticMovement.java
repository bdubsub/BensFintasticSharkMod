package tfar.bensfintasticsharks.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

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
    /** Profile-permitted steep maneuver endpoints, not routine cruise limits. */
    public static final float FISH_HARD_UPWARD_PITCH_LIMIT = 45.0f;
    public static final float FISH_HARD_DOWNWARD_PITCH_LIMIT = 90.0f;
    public static final float SHARK_HARD_UPWARD_PITCH_LIMIT = 45.0f;
    public static final float SHARK_HARD_DOWNWARD_PITCH_LIMIT = 60.0f;
    public static final float DEFAULT_UPWARD_PITCH_LIMIT = 10.0f;
    public static final float DEFAULT_DOWNWARD_PITCH_LIMIT = 10.0f;

    /** Six degrees per second at the nominal 20 tick server rate. */
    public static final float MAX_PITCH_STEP_DEGREES_PER_TICK = 0.30f;
    public static final float MAX_PITCH_ACCELERATION_PER_TICK = 0.015f;
    public static final float MAX_YAW_STEP_DEGREES_PER_TICK = 10.0f;

    /** A directly vertical route requires the body to reach a true sky or ground pose. */
    public static final float VERTICAL_UPWARD_PITCH = -90.0f;
    public static final float VERTICAL_DOWNWARD_PITCH = 90.0f;

    private AquaticMovement() {
    }

    public static PitchStep stepPitch(float pitch, float rate, float target) {
        float error = target - pitch;
        float acceleration = MAX_PITCH_ACCELERATION_PER_TICK;
        float brakingRate = (float) (Math.sqrt(2 * acceleration * Math.abs(error)
                + acceleration * acceleration * 0.25) - acceleration * 0.5);
        float desiredRate = Math.copySign(Math.min(MAX_PITCH_STEP_DEGREES_PER_TICK, brakingRate), error);
        float nextRate = Mth.approach(rate, desiredRate, acceleration);
        return new PitchStep(pitch + nextRate, nextRate);
    }

    public record PitchStep(float pitch, float rate) {}

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
     * below has no horizontal direction from which to derive an attitude. A direct vertical route
     * therefore uses the full sky or ground pose, while every route with a horizontal component
     * remains bounded by its species routine profile.
     */
    public static float affectedPitch(double dx, double dy, double dz,
                                      float upwardLimit, float downwardLimit) {
        return affectedPitch(dx, dy, dz, upwardLimit, downwardLimit,
                VERTICAL_UPWARD_PITCH, VERTICAL_DOWNWARD_PITCH);
    }

    /**
     * Returns a bounded pitch using separate routine and steep-route envelopes. A direct vertical
     * route is allowed to use its species profile endpoint only after the caller has selected that
     * route; ordinary routes remain inside their shallow cruise limits.
     */
    public static float affectedPitch(double dx, double dy, double dz,
                                      float upwardLimit, float downwardLimit,
                                      float verticalUpwardLimit, float verticalDownwardLimit) {
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        if (horizontalDistance <= 1.0e-8 && Math.abs(dy) <= 1.0e-8) {
            return 0.0f;
        }
        if (horizontalDistance <= 1.0e-8) {
            return dy > 0.0 ? -Math.abs(verticalUpwardLimit) : Math.abs(verticalDownwardLimit);
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

    /**
     * Eases a controlled vertical value without allowing a stale or externally supplied impulse
     * to exceed the approved fraction of the entity speed.
     */
    public static double smoothAndLimitVerticalVelocity(double current, double target, double speed) {
        double eased = smoothVerticalVelocity(current, target);
        double limit = Math.abs(speed) * VERTICAL_SPEED_RATIO;
        return Mth.clamp(eased, -limit, limit);
    }

    /**
     * Returns the engine forward vector for the entity's yaw and pitch. Minecraft's positive
     * pitch points the nose down, so a negative pitch produces positive vertical travel.
     */
    public static Vec3 forwardVector(float yawDegrees, float pitchDegrees) {
        double yaw = Math.toRadians(yawDegrees);
        double pitch = Math.toRadians(pitchDegrees);
        double horizontal = Math.cos(pitch);
        return new Vec3(-Math.sin(yaw) * horizontal, -Math.sin(pitch),
                Math.cos(yaw) * horizontal);
    }

    /**
     * Couples forward propulsion to body pitch while leaving lateral steering horizontal. A
     * shark cannot request independent vertical input through this path.
     */
    public static Vec3 bodyAlignedInput(Vec3 movementInput, float pitchDegrees) {
        double pitch = Math.toRadians(pitchDegrees);
        double forwardInput = movementInput.z;
        return new Vec3(movementInput.x, -Math.sin(pitch) * forwardInput,
                Math.cos(pitch) * forwardInput);
    }

    /**
     * Limits the powered component of a velocity without changing its direction. Orthogonal
     * velocity is retained for separately classified external forces.
     */
    public static Vec3 limitPoweredVelocity(Vec3 velocity, Vec3 forward,
                                             double horizontalSpeedCap, double verticalSpeedCap) {
        double poweredSpeed = velocity.dot(forward);
        if (poweredSpeed <= 0.0D) return velocity;
        double cap = poweredSpeed;
        double horizontalProjection = Math.sqrt(forward.x * forward.x + forward.z * forward.z);
        if (horizontalProjection > 1.0e-8) {
            cap = Math.min(cap, Math.abs(horizontalSpeedCap) / horizontalProjection);
        }
        if (Math.abs(forward.y) > 1.0e-8) {
            cap = Math.min(cap, Math.abs(verticalSpeedCap) / Math.abs(forward.y));
        }
        if (cap >= poweredSpeed) return velocity;
        Vec3 powered = forward.scale(poweredSpeed);
        return velocity.subtract(powered).add(forward.scale(cap));
    }

    /**
     * Removes stale vertical slip left over from the previous body pose while retaining
     * horizontal orthogonal drift for collision and external-force handling.
     */
    public static Vec3 removeUnalignedVerticalSlip(Vec3 velocity, Vec3 forward) {
        double poweredSpeed = velocity.dot(forward);
        if (poweredSpeed <= 0.0D) return velocity;
        Vec3 orthogonal = velocity.subtract(forward.scale(poweredSpeed));
        return new Vec3(orthogonal.x, 0.0D, orthogonal.z).add(forward.scale(poweredSpeed));
    }
}
