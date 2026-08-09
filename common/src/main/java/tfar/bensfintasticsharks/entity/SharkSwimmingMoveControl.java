package tfar.bensfintasticsharks.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.phys.Vec3;

/**
 * Swim steering tuned for long-bodied, fast-moving sharks.
 *
 * <p>Vanilla's in-water smooth controller keeps applying full thrust while the mob is
 * making even a 180-degree turn. That is fine for small fish, but a shark carries enough
 * momentum to overshoot its path node and orbit it indefinitely. This controller keeps
 * vanilla pathing and pitch calculation, then scales thrust and existing horizontal
 * momentum while the shark is badly misaligned or arriving at a close path node.</p>
 */
public class SharkSwimmingMoveControl extends SmoothSwimmingMoveControl {

    private static final double ARRIVAL_BRAKE_DISTANCE = 2.75;
    private final boolean trackPitch;

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier) {
        this(mob, inWaterSpeedModifier, true);
    }

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier, boolean trackPitch) {
        // Sharks may pitch steeply, and turn at 16 degrees/tick rather than vanilla's 10.
        // Turn-aware thrust below keeps the quicker yaw from looking like a pivot in place.
        super(mob, trackPitch ? 55 : 0, 16, inWaterSpeedModifier, 0, false);
        this.trackPitch = trackPitch;
    }

    @Override
    public void tick() {
        boolean moving = this.operation == MoveControl.Operation.MOVE_TO
                && !this.mob.getNavigation().isDone();
        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY(0.5);
        double dz = this.wantedZ - this.mob.getZ();

        super.tick();

        if (!moving || !this.mob.isInWater()) return;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 1.0e-4) return;

        float desiredYaw = (float)(Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0f;
        float yawError = Math.abs(Mth.wrapDegrees(desiredYaw - this.mob.getYRot()));

        // Full thrust within 20 degrees; progressively coast down to 15% while turning
        // around. Bleeding existing momentum at the same time collapses the old wide orbit.
        float turnFactor = 1.0f - Mth.clamp((yawError - 20.0f) / 75.0f, 0.0f, 0.85f);
        float arrivalFactor = (float)Mth.clamp(distance / ARRIVAL_BRAKE_DISTANCE, 0.18, 1.0);
        float thrustFactor = Math.min(turnFactor, arrivalFactor);
        this.mob.zza *= thrustFactor;

        this.mob.yya *= Math.max(0.55f, arrivalFactor);

        if (yawError > 35.0f || distance < ARRIVAL_BRAKE_DISTANCE) {
            Vec3 movement = this.mob.getDeltaMovement();
            double momentumFactor = yawError > 95.0f ? 0.55
                    : yawError > 55.0f ? 0.72
                    : Math.max(0.72, arrivalFactor);
            this.mob.setDeltaMovement(movement.x * momentumFactor, movement.y, movement.z * momentumFactor);
        }

        if (trackPitch && Math.abs(dy) > 0.35) {
            float desiredPitch = (float)(-(Mth.atan2(dy, Math.max(0.25, horizontalDistance)) * Mth.RAD_TO_DEG));
            desiredPitch = Mth.clamp(desiredPitch, -40.0f, 40.0f);
            this.mob.setXRot(this.rotlerp(this.mob.getXRot(), desiredPitch, 7.5f));
        }
    }
}
