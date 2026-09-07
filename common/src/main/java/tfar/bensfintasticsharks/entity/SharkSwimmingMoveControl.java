package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.phys.Vec3;

/**
 * Shark swim steering using the same smooth target following as bottlenose dolphins.
 *
 * <p>Direct vertical routes retain their entry yaw while the body eases into pitch-driven
 * propulsion. Once the target depth is crossed, thrust stops instead of starting a new
 * horizontal orbit.</p>
 */
public class SharkSwimmingMoveControl extends SmoothSwimmingMoveControl {

    private static final double VERTICAL_TARGET_EPSILON = 1.0e-6;
    private final boolean trackPitch;
    private double smoothedVerticalVelocity;
    private double lastWantedX = Double.NaN;
    private double lastWantedY = Double.NaN;
    private double lastWantedZ = Double.NaN;
    private float verticalRouteYaw;
    private int verticalRouteDirection;
    private double verticalRouteGoalY;
    private boolean verticalRouteSettled;
    private boolean verticalRouteProfileInitialized;
    private boolean verticalRoute;

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier) {
        this(mob, inWaterSpeedModifier, true);
    }

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier, boolean trackPitch) {
        super(mob, trackPitch ? 85 : 0, 10, inWaterSpeedModifier, 0, false);
        this.trackPitch = trackPitch;
    }

    @Override
    public void tick() {
        boolean pitchDrivenTravel = this.mob instanceof AbstractSharkEntity<?> shark
                && shark.usesPitchDrivenVerticalMovement();
        boolean hasMoveTarget = this.operation == MoveControl.Operation.MOVE_TO;
        if (!hasMoveTarget) {
            lastWantedX = Double.NaN;
            lastWantedY = Double.NaN;
            lastWantedZ = Double.NaN;
            verticalRouteSettled = false;
            verticalRouteProfileInitialized = false;
            verticalRoute = false;
        } else if (pitchDrivenTravel) {
            if (!verticalRouteProfileInitialized) {
                BlockPos navigationTarget = this.mob.getNavigation().getTargetPos();
                verticalRouteYaw = this.mob.getYRot();
                verticalRouteGoalY = navigationTarget == null ? this.wantedY : navigationTarget.getY() + 0.5D;
                double initialDx = navigationTarget == null
                        ? this.wantedX - this.mob.getX()
                        : navigationTarget.getX() + 0.5D - this.mob.getX();
                double initialDz = navigationTarget == null
                        ? this.wantedZ - this.mob.getZ()
                        : navigationTarget.getZ() + 0.5D - this.mob.getZ();
                verticalRoute = initialDx * initialDx + initialDz * initialDz
                        <= VERTICAL_TARGET_EPSILON;
                verticalRouteDirection = Integer.signum(Double.compare(
                        verticalRouteGoalY, this.mob.getY()));
                verticalRouteSettled = false;
                verticalRouteProfileInitialized = true;
            }
        } else if (!Double.isFinite(lastWantedX)
                || Math.abs(lastWantedX - this.wantedX) > VERTICAL_TARGET_EPSILON
                || Math.abs(lastWantedY - this.wantedY) > VERTICAL_TARGET_EPSILON
                || Math.abs(lastWantedZ - this.wantedZ) > VERTICAL_TARGET_EPSILON) {
            lastWantedX = this.wantedX;
            lastWantedY = this.wantedY;
            lastWantedZ = this.wantedZ;
            verticalRouteYaw = this.mob.getYRot();
            verticalRouteDirection = Integer.signum(Double.compare(this.wantedY, this.mob.getY()));
            verticalRouteSettled = false;
        }
        boolean moving = this.operation == MoveControl.Operation.MOVE_TO
                && !this.mob.getNavigation().isDone()
                && this.mob.isInWater();
        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY();
        double dz = this.wantedZ - this.mob.getZ();
        double routeDy = pitchDrivenTravel && verticalRoute ? verticalRouteGoalY - this.mob.getY() : dy;
        double pitchDx = pitchDrivenTravel && verticalRoute ? 0.0D : dx;
        double pitchDz = pitchDrivenTravel && verticalRoute ? 0.0D : dz;
        if (pitchDrivenTravel && verticalRouteDirection == 0 && Math.abs(routeDy) > VERTICAL_TARGET_EPSILON) {
            verticalRouteDirection = Integer.signum(Double.compare(routeDy, 0.0D));
        }
        boolean verticalOnly = moving && (pitchDrivenTravel
                ? verticalRoute
                : dx * dx + dz * dz <= VERTICAL_TARGET_EPSILON);
        if (pitchDrivenTravel && verticalOnly && verticalRouteDirection != 0
                && (routeDy * verticalRouteDirection <= 0.0D
                || Math.abs(routeDy) <= this.mob.getBbHeight())) {
            verticalRouteSettled = true;
        }
        boolean motionActive = moving && !(pitchDrivenTravel && verticalOnly && verticalRouteSettled);
        float yaw = this.mob.getYRot();
        float previousPitch = this.mob.getXRot();

        super.tick();

        if (trackPitch) {
            // Non pitch driven routes continue to follow smoothed vertical waypoints. Pitch
            // driven routes use the latched final target above so refreshed path nodes cannot
            // reverse the body attitude during a direct ascent or descent.
            if (!pitchDrivenTravel) {
                double targetVerticalVelocity = moving
                        ? AquaticMovement.affectedVerticalVelocity(this.mob.getSpeed(), dx, dy, dz)
                        : 0.0D;
                smoothedVerticalVelocity = AquaticMovement.smoothAndLimitVerticalVelocity(
                        smoothedVerticalVelocity, targetVerticalVelocity, this.mob.getSpeed());
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().x,
                        smoothedVerticalVelocity, this.mob.getDeltaMovement().z);
            }
            if (!motionActive) {
                if (this.mob.isInWater()) {
                    this.mob.setXRot(this.rotlerp(previousPitch, 0.0F,
                            AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK));
                }
                if (pitchDrivenTravel && verticalOnly) {
                    this.mob.setYRot(verticalRouteYaw);
                    this.mob.yBodyRot = verticalRouteYaw;
                    this.mob.yHeadRot = verticalRouteYaw;
                }
                this.mob.setZza(0.0F);
                this.mob.setYya(0.0F);
                return;
            }

            this.mob.setYya(0.0F);
            // A direct-above or direct-below target still needs forward propulsion while the
            // body eases through its entry arc. The pitch-aligned travel step supplies the
            // vertical component; zero forward input would create stationary pitch acquisition.
            this.mob.setZza(1.0F);
            this.mob.setXRot(this.rotlerp(previousPitch,
                    AquaticMovement.affectedPitch(pitchDx, routeDy, pitchDz,
                            pitchUpLimit(), pitchDownLimit()),
                    AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK));

            if (verticalOnly) {
                float routeYaw = pitchDrivenTravel ? verticalRouteYaw : yaw;
                this.mob.setYRot(routeYaw);
                this.mob.yBodyRot = routeYaw;
                this.mob.yHeadRot = routeYaw;
                if (!pitchDrivenTravel) {
                    this.mob.setZza(0.0F);
                    this.mob.setXxa(0.0F);
                    Vec3 delta = this.mob.getDeltaMovement();
                    this.mob.setDeltaMovement(0.0D, delta.y, 0.0D);
                }
            }
        }
    }

    private float pitchUpLimit() {
        return this.mob instanceof AbstractSharkEntity<?> shark
                ? shark.upwardPitchLimitDegrees() : AquaticMovement.DEFAULT_UPWARD_PITCH_LIMIT;
    }

    private float pitchDownLimit() {
        return this.mob instanceof AbstractSharkEntity<?> shark
                ? shark.downwardPitchLimitDegrees() : AquaticMovement.DEFAULT_DOWNWARD_PITCH_LIMIT;
    }
}
