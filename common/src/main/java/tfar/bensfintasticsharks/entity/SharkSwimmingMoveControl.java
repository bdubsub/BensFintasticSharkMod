package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;

/**
 * Shark swim steering using the same smooth target following as bottlenose dolphins.
 *
 * <p>The vertical-only guard keeps the vanilla controller from assigning a meaningless
 * yaw when a path target is directly above or below the shark. This preserves smooth
 * pitch and vertical thrust without introducing circular horizontal movement.</p>
 */
public class SharkSwimmingMoveControl extends SmoothSwimmingMoveControl {

    private static final double VERTICAL_TARGET_EPSILON = 1.0e-6;

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier) {
        this(mob, inWaterSpeedModifier, true);
    }

    public SharkSwimmingMoveControl(Mob mob, float inWaterSpeedModifier, boolean trackPitch) {
        super(mob, trackPitch ? 85 : 0, 10, inWaterSpeedModifier, 0, false);
    }

    @Override
    public void tick() {
        boolean moving = this.operation == MoveControl.Operation.MOVE_TO
                && !this.mob.getNavigation().isDone()
                && this.mob.isInWater();
        double dx = this.wantedX - this.mob.getX();
        double dz = this.wantedZ - this.mob.getZ();
        boolean verticalOnly = moving && dx * dx + dz * dz <= VERTICAL_TARGET_EPSILON;
        float yaw = this.mob.getYRot();

        super.tick();

        if (verticalOnly) {
            this.mob.setYRot(yaw);
            this.mob.yBodyRot = yaw;
            this.mob.yHeadRot = yaw;
            this.mob.setZza(0.0f);
            this.mob.setXxa(0.0f);
        }
    }
}
