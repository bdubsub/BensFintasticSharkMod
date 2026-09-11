package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;

/** Keeps shark nose pitch owned by the movement controller while retaining smooth head yaw. */
final class BfsSharkLookControl extends SmoothSwimmingLookControl {

    private final Mob mob;

    BfsSharkLookControl(Mob mob) {
        super(mob, 10);
        this.mob = mob;
    }

    @Override
    public void tick() {
        if (mob.getMoveControl() instanceof SharkSwimmingMoveControl moveControl && mob.isInWater()
                && moveControl.hasWanted()) {
            // Keep navigation as the single body yaw writer during powered swimming. A separate
            // look target must not steer the body sideways and turn a depth leg into an orbit.
            float movementYaw = mob.getYRot();
            mob.yBodyRot = movementYaw;
            mob.yHeadRot = movementYaw;
            return;
        }
        float movementPitch = mob.getXRot();
        super.tick();
        mob.setXRot(movementPitch);
    }
}
