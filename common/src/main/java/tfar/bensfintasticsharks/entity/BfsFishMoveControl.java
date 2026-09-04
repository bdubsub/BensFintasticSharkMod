package tfar.bensfintasticsharks.entity;

import net.minecraft.tags.FluidTags;
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
     * Mirrors AbstractFish water travel without its unconditional idle sink. Vertical velocity
     * is supplied by this controller, so a fish can only rise or dive while its nose is moving
     * toward that same target vector.
     */
    static void travel(AbstractFish fish, Vec3 movementInput) {
        fish.moveRelative(0.01F, new Vec3(movementInput.x, 0.0, movementInput.z));
        fish.move(MoverType.SELF, fish.getDeltaMovement());
        fish.setDeltaMovement(fish.getDeltaMovement().scale(0.9));
    }

    @Override
    public void tick() {
        double targetVerticalImpulse = 0.0;
        float previousPitch = fish.getXRot();
        if (this.operation == Operation.MOVE_TO) {
            float targetSpeed = (float) (this.speedModifier
                    * fish.getAttributeValue(Attributes.MOVEMENT_SPEED));
            fish.setSpeed(Mth.lerp(0.125F, fish.getSpeed(), targetSpeed));

            double dx = this.wantedX - fish.getX();
            double dy = this.wantedY - fish.getY();
            double dz = this.wantedZ - fish.getZ();
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (distance > 0.5) {
                targetVerticalImpulse = AquaticMovement.affectedVerticalVelocity(
                        fish.getSpeed(), dx, dy, dz);

                if (Math.abs(dx) > 1.0e-8 || Math.abs(dz) > 1.0e-8) {
                    float desiredYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
                    fish.setYRot(this.rotlerp(fish.getYRot(), desiredYaw, 90.0F));
                    fish.yBodyRot = fish.getYRot();
                    fish.yHeadRot = fish.getYRot();
                }

                fish.setXxa(0.0F);
                double horizontalDistanceSqr = dx * dx + dz * dz;
                fish.setZza(horizontalDistanceSqr > 0.25 ? 1.0F : 0.0F);

                fish.setXRot(this.rotlerp(previousPitch,
                        AquaticMovement.affectedPitch(dx, dy, dz, upwardPitchLimit, downwardPitchLimit),
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

        if (fish.isEyeInFluid(FluidTags.WATER)) {
            double verticalSpeedLimit = fish.getSpeed() * AquaticMovement.VERTICAL_SPEED_RATIO;
            double verticalVelocity = AquaticMovement.smoothVerticalVelocity(
                    fish.getDeltaMovement().y, targetVerticalImpulse);
            verticalVelocity = Mth.clamp(verticalVelocity, -verticalSpeedLimit, verticalSpeedLimit);
            fish.setDeltaMovement(fish.getDeltaMovement().x, verticalVelocity,
                    fish.getDeltaMovement().z);
        }
    }
}
