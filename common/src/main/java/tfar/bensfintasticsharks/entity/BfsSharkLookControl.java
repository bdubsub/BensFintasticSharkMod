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
        float movementPitch = mob.getXRot();
        super.tick();
        mob.setXRot(movementPitch);
    }
}
