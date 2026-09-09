package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

/** Fish movement control with the vanilla fish response and an explicit three dimensional pitch. */
public final class BfsFishMoveControl extends MoveControl {

    private final AbstractFish fish;
    private final float upwardPitchLimit;
    private final float downwardPitchLimit;

    public BfsFishMoveControl(AbstractFish fish) {
        this(fish, AquaticMovement.DEFAULT_UPWARD_PITCH_LIMIT,
                AquaticMovement.DEFAULT_DOWNWARD_PITCH_LIMIT);
    }

    public BfsFishMoveControl(AbstractFish fish, float upwardPitchLimit, float downwardPitchLimit) {
        super(fish);
        this.fish = fish;
        this.upwardPitchLimit = upwardPitchLimit;
        this.downwardPitchLimit = downwardPitchLimit;
    }

    /**
     * Mirrors AbstractFish water travel without its unconditional idle sink. The local forward
     * input is rotated through the current body pitch before travel, so vertical movement is
     * always the vertical projection of forward propulsion rather than a second independent
     * impulse.
     */
    static void travel(AbstractFish fish, Vec3 movementInput) {
        Vec3 planarInput = new Vec3(movementInput.x, 0.0D, movementInput.z);
        Vec3 bodyAlignedInput = AquaticMovement.bodyAlignedInput(planarInput, fish.getXRot());
        fish.moveRelative(0.01F, bodyAlignedInput);
        fish.move(MoverType.SELF, fish.getDeltaMovement());
        Vec3 velocity = fish.getDeltaMovement().scale(0.9);
        double verticalLimit = Math.abs(fish.getSpeed()) * AquaticMovement.VERTICAL_SPEED_RATIO;
        if (Math.abs(velocity.y) > verticalLimit) {
            velocity = new Vec3(velocity.x, Math.copySign(verticalLimit, velocity.y), velocity.z);
        }
        if (movementInput.z <= 0.0D) {
            velocity = new Vec3(velocity.x,
                    AquaticMovement.smoothVerticalVelocity(velocity.y, 0.0D), velocity.z);
        }
        fish.setDeltaMovement(velocity);
    }

    @Override
    public void tick() {
        float previousPitch = fish.getXRot();
        if (this.operation == Operation.MOVE_TO) {
            float targetSpeed = (float) (this.speedModifier
                    * fish.getAttributeValue(Attributes.MOVEMENT_SPEED));
            fish.setSpeed(Mth.lerp(0.125F, fish.getSpeed(), targetSpeed));

            double dx = this.wantedX - fish.getX();
            double dy = this.wantedY - fish.getY();
            double dz = this.wantedZ - fish.getZ();
            BlockPos routeTargetPos = fish.getNavigation().getTargetPos();
            double routeDy = dy;
            if (routeTargetPos != null) {
                routeDy = Vec3.atCenterOf(routeTargetPos).y - fish.getY();
            }
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0.5) {
                if (Math.abs(dx) > 1.0e-8 || Math.abs(dz) > 1.0e-8) {
                    float desiredYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
                    fish.setYRot(this.rotlerp(fish.getYRot(), desiredYaw, 90.0F));
                    fish.yBodyRot = fish.getYRot();
                    fish.yHeadRot = fish.getYRot();
                }

                fish.setXxa(0.0F);
                // Even a direct-above or direct-below destination needs forward propulsion while
                // the body eases into its matching vertical pose. Stopping forward input here
                // would create stationary pitch acquisition and an independent vertical impulse.
                fish.setZza(1.0F);

                fish.setXRot(this.rotlerp(previousPitch,
                        AquaticMovement.affectedPitch(dx, routeDy, dz, upwardPitchLimit, downwardPitchLimit),
                        AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK));
            } else {
                this.operation = Operation.WAIT;
                fish.setSpeed(0.0F);
                fish.setXxa(0.0F);
                fish.setZza(0.0F);
                fish.setXRot(this.rotlerp(previousPitch, 0.0F,
                        AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK));
            }
        } else {
            fish.setSpeed(0.0F);
            fish.setXxa(0.0F);
            fish.setZza(0.0F);
            fish.setXRot(this.rotlerp(previousPitch, 0.0F,
                    AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK));
        }

        // Travel owns the projected vertical component. The controller only selects the route
        // and eases the body attitude, avoiding a second writer that could move a level fish.
    }
}
