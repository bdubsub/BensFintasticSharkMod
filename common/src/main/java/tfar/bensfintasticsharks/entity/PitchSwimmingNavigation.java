package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
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
    public boolean moveTo(Entity entity, double speed) {
        // PathNavigation's entity overload builds a path directly and bypasses the coordinate
        // overload below. Follow leases use this overload, so route the endpoint through the
        // pitch handoff or a vertical or rejected water path only changes the mob's gaze.
        return moveTo(entity.getX(), entity.getY(), entity.getZ(), speed);
    }

    @Override
    public boolean moveTo(double x, double y, double z, double speed) {
        Vec3 destination = new Vec3(x, y, z);
        boolean accepted = super.moveTo(x, y, z, speed);
        // WaterBoundPathNavigation may call stop() while replacing or rejecting a destination
        // that has no node path. The finite pitch route still owns this exact endpoint, so
        // restore the handoff after the superclass call while keeping explicit stop() able to
        // clear it later.
        requestedDestination = destination;
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
        boolean hadRequestedDestination = requestedDestination != null;
        super.stop();
        // PathNavigation.stop() clears the path but does not clear the destination that the
        // pitch controller captured for a rejected or vertical path. Drop that handoff too so
        // a released follow lease cannot leave the swimmer looking at the old owner forever.
        requestedDestination = null;
        if (hadRequestedDestination) {
            if (mob.getMoveControl() instanceof SharkSwimmingMoveControl control) {
                control.clearNavigationRequest();
            } else if (mob.getMoveControl() instanceof PitchSwimmingMoveControl control) {
                control.clearNavigationRequest();
            }
        }
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
