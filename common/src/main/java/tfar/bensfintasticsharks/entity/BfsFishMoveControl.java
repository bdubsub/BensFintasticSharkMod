package tfar.bensfintasticsharks.entity;

import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.animal.AbstractFish;

/**
 * Fish movement control retaining vanilla's gentle vertical thrust while adding smooth pitch.
 * Vanilla fish already use a ten percent vertical component, but only rotate their yaw. This
 * control keeps that movement contract and points the whole model along the same scaled vector.
 */
public final class BfsFishMoveControl extends MoveControl {

    private final AbstractFish fish;
    private double smoothedVerticalImpulse;

    public BfsFishMoveControl(AbstractFish fish) {
        super(fish);
        this.fish = fish;
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
                if (Math.abs(dy) > 1.0e-8) {
                    targetVerticalImpulse = fish.getSpeed() * dy / distance
                            * AquaticMovement.VERTICAL_SPEED_RATIO;
                }

                if (Math.abs(dx) > 1.0e-8 || Math.abs(dz) > 1.0e-8) {
                    float desiredYaw = (float) (Mth.atan2(dz, dx) * Mth.RAD_TO_DEG) - 90.0F;
                    fish.setYRot(this.rotlerp(fish.getYRot(), desiredYaw, 90.0F));
                    fish.yBodyRot = fish.getYRot();
                    fish.yHeadRot = fish.getYRot();
                }

                fish.setXRot(this.rotlerp(previousPitch,
                        AquaticMovement.affectedPitch(dx, dy, dz), 5.0F));
            } else {
                this.operation = Operation.WAIT;
                fish.setSpeed(0.0F);
                fish.setXRot(this.rotlerp(previousPitch, 0.0F, 5.0F));
            }
        } else {
            fish.setSpeed(0.0F);
            fish.setXRot(this.rotlerp(previousPitch, 0.0F, 5.0F));
        }

        if (fish.isEyeInFluid(FluidTags.WATER)) {
            smoothedVerticalImpulse = AquaticMovement.smoothVerticalVelocity(
                    smoothedVerticalImpulse, targetVerticalImpulse);
            fish.setDeltaMovement(fish.getDeltaMovement().add(
                    0.0, 0.005 + smoothedVerticalImpulse, 0.0));
        } else {
            smoothedVerticalImpulse = 0.0;
        }
    }
}
