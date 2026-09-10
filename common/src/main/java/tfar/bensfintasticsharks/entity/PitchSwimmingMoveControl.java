package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Shared destination steering and explicitly owned propulsion for fish and sharks. */
public class PitchSwimmingMoveControl extends MoveControl {

    private final float waterSpeedMultiplier;
    private final boolean easeSpeed;
    private final float upwardLimit;
    private final float downwardLimit;
    private final double verticalSpeedRatio;
    private AquaticRoute route;
    private Vec3 requestedGoal;
    private Vec3 progressWaypoint;
    private double bestWaypointDistance;
    private int stalledTicks;
    private boolean navigationOwned;
    private float pitchRate;
    private double routeSpeedCap = Double.POSITIVE_INFINITY;
    private Vec3 poweredCarry = Vec3.ZERO;
    private String routeState = "idle";
    private long routeAttempt;
    private Vec3 selectedWaypoint;
    private float targetPitch;
    private Vec3 blockedGoal;
    private int retryAfterTick;

    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        if (navigationOwned && mob.getNavigation().getPath() == null) {
            navigationOwned = false;
            requestedGoal = null;
            route = null;
        }
        super.setWantedPosition(x, y, z, speed);
    }

    protected PitchSwimmingMoveControl(Mob mob, float waterSpeedMultiplier, boolean easeSpeed,
                                        float upwardLimit, float downwardLimit, double verticalSpeedRatio) {
        super(mob);
        this.waterSpeedMultiplier = waterSpeedMultiplier;
        this.easeSpeed = easeSpeed;
        this.upwardLimit = Math.abs(upwardLimit);
        this.downwardLimit = Math.abs(downwardLimit);
        this.verticalSpeedRatio = verticalSpeedRatio;
    }

    public String routeState() {
        return routeState;
    }

    public Snapshot snapshot() {
        double verticalReferenceSpeed = Math.abs(mob.getSpeed());
        return new Snapshot(routeAttempt, routeState, requestedGoal, selectedWaypoint, targetPitch,
                requestedGoal == null ? 0 : mob.position().distanceTo(requestedGoal), stalledTicks,
                poweredCarry, mob.getDeltaMovement().subtract(poweredCarry), verticalSpeedRatio,
                verticalReferenceSpeed, verticalReferenceSpeed * verticalSpeedRatio);
    }

    public record Snapshot(long attempt, String state, Vec3 destination, Vec3 waypoint, float desiredPitch,
                           double remainingDistance, int stalledTicks, Vec3 poweredVelocity, Vec3 externalVelocity,
                           double verticalSpeedRatio, double verticalReferenceSpeed, double verticalSpeedCeiling) {}

    @Override
    public void tick() {
        if (!mob.isInWater()) {
            poweredCarry = Vec3.ZERO;
            stopInputs("idle");
            return;
        }
        if (operation != Operation.MOVE_TO) return;
        var path = mob.getNavigation().getPath();
        if (navigationOwned && path == null) {
            route = null;
            requestedGoal = null;
            navigationOwned = false;
            stopInputs("cancelled");
            return;
        }
        Vec3 goal = path == null ? new Vec3(wantedX, wantedY, wantedZ)
                : Vec3.atCenterOf(path.getTarget());
        if (blockedGoal != null && blockedGoal.distanceToSqr(goal) < 1.0e-6
                && mob.tickCount < retryAfterTick) {
            stopInputs("blocked");
            return;
        }
        if (requestedGoal == null || requestedGoal.distanceToSqr(goal) > 1.0e-6) {
            requestedGoal = goal;
            routeAttempt++;
            navigationOwned = path != null;
            route = createRoute(goal);
            progressWaypoint = null;
            stalledTicks = 0;
        }
        if (route != null && route.arrived(mob.position())) {
            stopInputs("arrived");
            return;
        }

        Vec3 target = route == null ? new Vec3(wantedX, wantedY, wantedZ) : route.target(mob.position());
        selectedWaypoint = target;
        Vec3 delta = target.subtract(mob.position());
        double distance = delta.length();
        if (progressWaypoint == null || progressWaypoint.distanceToSqr(target) > 1.0e-6) {
            progressWaypoint = target;
            bestWaypointDistance = distance;
            stalledTicks = 0;
        }
        if (distance <= 0.35) {
            stopInputs(route == null ? "waypoint" : "arrived");
            return;
        }
        routeState = route == null ? "path" : route.isApproaching() ? "clearance" : "approach";
        float targetSpeed = (float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED))
                * waterSpeedMultiplier;
        mob.setSpeed(easeSpeed ? Mth.lerp(0.125F, mob.getSpeed(), targetSpeed) : targetSpeed);
        float desiredYaw = delta.horizontalDistance() > 0.1
                ? (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90 : mob.getYRot();
        mob.setYRot(rotlerp(mob.getYRot(), desiredYaw, AquaticMovement.MAX_YAW_STEP_DEGREES_PER_TICK));
        mob.yBodyRot = mob.getYRot();
        mob.yHeadRot = mob.getYRot();

        float desiredPitch = Mth.clamp((float) -Math.toDegrees(Math.atan2(delta.y, delta.horizontalDistance())),
                -upwardLimit, downwardLimit);
        targetPitch = desiredPitch;
        float pitchError = Mth.wrapDegrees(desiredPitch - mob.getXRot());
        AquaticMovement.PitchStep pitchStep = AquaticMovement.stepPitch(
                Mth.wrapDegrees(mob.getXRot()), pitchRate, desiredPitch);
        pitchRate = pitchStep.rate();
        mob.setXRot(pitchStep.pitch());

        double yawError = Math.toRadians(Mth.wrapDegrees(desiredYaw - mob.getYRot()));
        double headingThrottle = Math.max(0.05, Math.cos(yawError));
        mob.setXxa(0);
        mob.setYya(0);
        mob.setZza((float) headingThrottle);
        routeSpeedCap = distance * 0.1;
        if (Math.abs(pitchError) > AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK) {
            routeSpeedCap = Math.min(routeSpeedCap, distance
                    * AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK / Math.abs(pitchError) * 0.5);
            if (route != null && !route.isApproaching() && Math.abs(delta.y) > 0.35) {
                double limit = delta.y > 0 ? upwardLimit : downwardLimit;
                double margin = delta.horizontalDistance()
                        - Math.abs(delta.y) / Math.tan(Math.toRadians(limit));
                double pitchRemaining = Math.max(1, limit - Math.abs(Mth.wrapDegrees(mob.getXRot())));
                routeSpeedCap = Math.min(routeSpeedCap, Math.max(0, margin)
                        * AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK / pitchRemaining * 0.5);
            }
        }
        routeSpeedCap *= headingThrottle;

        if (distance < bestWaypointDistance - 0.1) {
            bestWaypointDistance = distance;
            stalledTicks = 0;
        } else if (++stalledTicks >= 80) {
            blockedGoal = requestedGoal;
            retryAfterTick = mob.tickCount + 100;
            mob.getNavigation().stop();
            mob.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
            route = null;
            requestedGoal = null;
            navigationOwned = false;
            stopInputs("blocked");
        }
    }

    private AquaticRoute createRoute(Vec3 goal) {
        double radius = Math.max(0.35, Math.min(0.65, mob.getBbWidth() * 0.5));
        float limit = goal.y > mob.getY() ? upwardLimit : downwardLimit;
        Vec3 approach = AquaticRoute.clearancePoint(mob.position(), goal, mob.getYRot(), limit,
                Math.max(1.0, mob.getBbWidth() * 2.0));
        if (approach != null) {
            if (clearSegment(mob.position(), approach) && clearSegment(approach, goal)) {
                return new AquaticRoute(goal, radius, approach);
            }
            return null;
        }
        return clearSegment(mob.position(), goal) ? new AquaticRoute(goal, radius, null) : null;
    }

    private boolean clearSegment(Vec3 from, Vec3 to) {
        Vec3 displacement = to.subtract(from);
        int samples = (int) Math.ceil(displacement.length() * 2);
        if (samples > 96) return false;
        AABB bounds = mob.getBoundingBox().move(from.subtract(mob.position())).deflate(0.01);
        for (int i = 0; i <= samples; i++) {
            Vec3 offset = displacement.scale(i / (double) Math.max(1, samples));
            AABB sample = bounds.move(offset);
            if (!mob.level().noCollision(mob, sample)
                    || !mob.level().getFluidState(BlockPos.containing(sample.getCenter())).is(FluidTags.WATER)) {
                return false;
            }
        }
        return true;
    }

    private void stopInputs(String state) {
        routeState = state;
        operation = Operation.WAIT;
        mob.setXxa(0);
        mob.setYya(0);
        mob.setZza(0);
        routeSpeedCap = 0;
        pitchRate = Mth.approach(pitchRate, 0, AquaticMovement.MAX_PITCH_ACCELERATION_PER_TICK);
    }

    /** External velocity is the change since the last owned carry, not an orthogonal guess. */
    public void travel(double acceleration, double friction, double horizontalCap, double speedFloor,
                        Vec3 input) {
        Vec3 delta = mob.getDeltaMovement();
        Vec3 external = new Vec3(externalComponent(delta.x, poweredCarry.x),
                externalComponent(delta.y, poweredCarry.y), externalComponent(delta.z, poweredCarry.z));
        Vec3 forward = AquaticMovement.forwardVector(mob.getYRot(), mob.getXRot());
        double speed = Math.max(0, poweredCarry.length() + acceleration * Math.max(0, input.z));
        if (input.z > 0) speed = Math.max(speed, speedFloor);
        double speedCap = Math.min(horizontalCap / Math.max(1.0e-8, forward.horizontalDistance()), routeSpeedCap);
        if (Math.abs(forward.y) > 1.0e-8) {
            speedCap = Math.min(speedCap,
                    Math.abs(mob.getSpeed()) * verticalSpeedRatio / Math.abs(forward.y));
        }
        if (input.z > 0) speed = Math.min(speed, speedCap);
        Vec3 powered = forward.scale(speed);
        mob.setDeltaMovement(external.add(powered));
        mob.move(MoverType.SELF, mob.getDeltaMovement());
        Vec3 afterCollision = mob.getDeltaMovement();
        powered = new Vec3(afterCollision.x == 0 ? 0 : powered.x,
                afterCollision.y == 0 ? 0 : powered.y, afterCollision.z == 0 ? 0 : powered.z);
        mob.setDeltaMovement(afterCollision.scale(friction));
        poweredCarry = powered.scale(friction);
    }

    private static double externalComponent(double observed, double owned) {
        return observed == 0 && Math.abs(owned) < 0.003 ? 0 : observed - owned;
    }
}
