package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Uses the route controller deadline while a deliberately slow depth approach is active. */
public class PitchSwimmingNavigation extends WaterBoundPathNavigation {

    public PitchSwimmingNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected void doStuckDetection(Vec3 position) {
        PitchSwimmingMoveControl.Snapshot state = null;
        if (mob.getMoveControl() instanceof PitchSwimmingMoveControl control) state = control.snapshot();
        else if (mob.getMoveControl() instanceof SharkSwimmingMoveControl control) state = control.snapshot();
        if (state != null && (state.state().equals("clearance") || state.state().equals("approach"))) {
            lastStuckCheck = tick;
            lastStuckCheckPos = position;
            timeoutTimer = 0;
            return;
        }
        super.doStuckDetection(position);
    }
}
