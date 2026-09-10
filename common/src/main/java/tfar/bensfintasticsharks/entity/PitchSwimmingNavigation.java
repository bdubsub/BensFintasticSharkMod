package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Uses the route controller deadline while a deliberately slow depth approach is active. */
public class PitchSwimmingNavigation extends WaterBoundPathNavigation {

    private Vec3 requestedDestination;

    public PitchSwimmingNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    public boolean moveTo(double x, double y, double z, double speed) {
        requestedDestination = new Vec3(x, y, z);
        boolean accepted = super.moveTo(x, y, z, speed);
        // Water pathfinding can reject a steep vertical destination because it has no
        // horizontal node path. Keep the exact endpoint in the pitch controller so the
        // finite clearance route can still take over instead of leaving the swimmer idle.
        if (mob.getMoveControl() instanceof PitchSwimmingMoveControl control) {
            control.setWantedPosition(x, y, z, speed);
        } else if (mob.getMoveControl() instanceof SharkSwimmingMoveControl control) {
            control.setWantedPosition(x, y, z, speed);
        }
        return accepted;
    }

    @Override
    public void stop() {
        super.stop();
    }

    public void clearRequestedDestination() {
        requestedDestination = null;
    }

    public Vec3 requestedDestination() {
        return requestedDestination;
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
