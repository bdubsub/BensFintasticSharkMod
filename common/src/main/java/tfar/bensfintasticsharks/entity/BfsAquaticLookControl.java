package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;

/** Keeps the movement controller's nose pitch while retaining smooth head tracking. */
final class BfsAquaticLookControl extends SmoothSwimmingLookControl {

    private final Mob mob;

    BfsAquaticLookControl(Mob mob) {
        super(mob, 10);
        this.mob = mob;
    }

    @Override
    public void tick() {
        if (MovementIntentOverrides.active(mob)) {
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
