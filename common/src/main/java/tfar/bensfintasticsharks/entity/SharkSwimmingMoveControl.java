package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * Shark swim steering using the same smooth target following as bottlenose dolphins.
 *
 * <p>The vertical-only guard keeps the vanilla controller from assigning a meaningless
 * yaw when a path target is directly above or below the shark. This preserves smooth
 * pitch and vertical thrust without introducing circular horizontal movement.</p>
 */
public class SharkSwimmingMoveControl extends SmoothSwimmingMoveControl {

    private static final double VERTICAL_TARGET_EPSILON = 1.0e-6;
    private static final double VERTICAL_ROUTE_HORIZONTAL_TOLERANCE_SQR = 0.25;
    private final boolean trackPitch;
    private double smoothedVerticalVelocity;

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier) {
        this(mob, inWaterSpeedModifier, true);
    }

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier, boolean trackPitch) {
        super(mob, trackPitch ? 85 : 0, 10, inWaterSpeedModifier, 0, false);
        this.trackPitch = trackPitch;
    }

    @Override
    public void tick() {
        boolean moving = this.operation == MoveControl.Operation.MOVE_TO
                && !this.mob.getNavigation().isDone()
                && this.mob.isInWater();
        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY();
        double dz = this.wantedZ - this.mob.getZ();
        BlockPos routeTargetPos = this.mob.getNavigation().getTargetPos();
        double routeDx = dx;
        double routeDy = dy;
        double routeDz = dz;
        if (routeTargetPos != null) {
            Vec3 routeTarget = Vec3.atCenterOf(routeTargetPos);
            routeDx = routeTarget.x - this.mob.getX();
            routeDy = routeTarget.y - this.mob.getY();
            routeDz = routeTarget.z - this.mob.getZ();
        }
        boolean verticalOnly = moving
                && (dx * dx + dz * dz <= VERTICAL_TARGET_EPSILON
                || (routeTargetPos != null
                && routeDx * routeDx + routeDz * routeDz <= VERTICAL_ROUTE_HORIZONTAL_TOLERANCE_SQR));
        float yaw = this.mob.getYRot();
        float previousPitch = this.mob.getXRot();

        super.tick();

        if (trackPitch) {
            double verticalDx = verticalOnly ? routeDx : dx;
            double verticalDy = verticalOnly ? routeDy : dy;
            double verticalDz = verticalOnly ? routeDz : dz;
            double targetVerticalVelocity = moving
                    ? AquaticMovement.affectedVerticalVelocity(
                            this.mob.getSpeed(), verticalDx, verticalDy, verticalDz)
                    : 0.0D;
            smoothedVerticalVelocity = AquaticMovement.smoothVerticalVelocity(
                    this.mob.getDeltaMovement().y, targetVerticalVelocity);
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().x,
                    smoothedVerticalVelocity, this.mob.getDeltaMovement().z);

            if (!moving) {
                if (this.mob.isInWater()) {
                    this.mob.setXRot(this.rotlerp(previousPitch, 0.0F,
                            AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK));
                }
                this.mob.setYya(0.0F);
                return;
            }

            this.mob.setYya(0.0F);
            this.mob.setXRot(this.rotlerp(previousPitch,
                    AquaticMovement.affectedPitch(verticalDx, verticalDy, verticalDz,
                            pitchUpLimit(), pitchDownLimit()),
                    AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK));
        }

        if (verticalOnly) {
            this.mob.setYRot(yaw);
            this.mob.yBodyRot = yaw;
            this.mob.yHeadRot = yaw;
            this.mob.setZza(0.0f);
            this.mob.setXxa(0.0f);
            Vec3 delta = this.mob.getDeltaMovement();
            this.mob.setDeltaMovement(0.0, delta.y, 0.0);
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
