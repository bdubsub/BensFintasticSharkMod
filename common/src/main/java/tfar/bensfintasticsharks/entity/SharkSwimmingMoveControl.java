package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.phys.Vec3;

/** Shark route steering with the existing dolphin controller retained for other swimmers. */
public class SharkSwimmingMoveControl extends SmoothSwimmingMoveControl {

    private final boolean trackPitch;
    private final PitchSwimmingMoveControl pitchControl;
    private double smoothedVerticalVelocity;

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier) {
        this(mob, inWaterSpeedModifier, true);
    }

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier, boolean trackPitch) {
        super(mob, trackPitch ? 85 : 0, 10, inWaterSpeedModifier, 0, false);
        this.trackPitch = trackPitch;
        pitchControl = mob instanceof AbstractSharkEntity<?> shark && shark.usesPitchDrivenVerticalMovement()
                ? new PitchSwimmingMoveControl(mob, inWaterSpeedModifier, false,
                shark.hardUpwardPitchLimitDegrees(), shark.hardDownwardPitchLimitDegrees()) : null;
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        super.setWantedPosition(x, y, z, speed);
        if (pitchControl != null) pitchControl.setWantedPosition(x, y, z, speed);
    }

    @Override
    public boolean hasWanted() {
        return pitchControl == null ? super.hasWanted() : pitchControl.hasWanted();
    }

    public String routeState() {
        return pitchControl == null ? "reference" : pitchControl.routeState();
    }

    public PitchSwimmingMoveControl.Snapshot snapshot() {
        return pitchControl == null ? null : pitchControl.snapshot();
    }

    public void travel(double acceleration, double friction, double horizontalCap, double speedFloor,
                        Vec3 input) {
        pitchControl.travel(acceleration, friction, horizontalCap, speedFloor, input);
    }

    @Override
    public void tick() {
        if (pitchControl != null) {
            pitchControl.tick();
            return;
        }
        boolean moving = operation == Operation.MOVE_TO && !mob.getNavigation().isDone() && mob.isInWater();
        double dx = wantedX - mob.getX();
        double dy = wantedY - mob.getY();
        double dz = wantedZ - mob.getZ();
        float previousPitch = mob.getXRot();
        float previousYaw = mob.getYRot();
        super.tick();
        if (!trackPitch) return;
        double targetVertical = moving ? AquaticMovement.affectedVerticalVelocity(mob.getSpeed(), dx, dy, dz) : 0;
        smoothedVerticalVelocity = AquaticMovement.smoothAndLimitVerticalVelocity(
                smoothedVerticalVelocity, targetVertical, mob.getSpeed());
        Vec3 delta = mob.getDeltaMovement();
        mob.setDeltaMovement(delta.x, smoothedVerticalVelocity, delta.z);
        if (!moving) {
            if (mob.isInWater()) mob.setXRot(rotlerp(previousPitch, 0,
                    AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK));
            mob.setZza(0);
            mob.setYya(0);
            return;
        }
        mob.setYya(0);
        mob.setZza(1);
        mob.setXRot(rotlerp(previousPitch, AquaticMovement.affectedPitch(dx, dy, dz),
                AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK));
        if (dx * dx + dz * dz <= 1.0e-6) {
            mob.setYRot(previousYaw);
            mob.yBodyRot = previousYaw;
            mob.yHeadRot = previousYaw;
            mob.setXxa(0);
            mob.setZza(0);
            mob.setDeltaMovement(0, smoothedVerticalVelocity, 0);
        }
    }
}
