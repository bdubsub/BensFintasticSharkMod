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
    private AquaticDepthGuidance.Step depthGuidance;
    private boolean settling;
    private boolean committedHeading;
    private float committedYaw;
    private double remainingVerticalDistance;
    private boolean routeStartedWithOpposingPitch;

    @Override
    public boolean hasWanted() {
        return super.hasWanted() || settling;
    }

    @Override
    public void setWantedPosition(double x, double y, double z, double speed) {
        boolean navigationStillOwnsDestination = mob.getNavigation() instanceof PitchSwimmingNavigation pitchNavigation
                && pitchNavigation.requestedDestination() != null;
        if (navigationOwned && mob.getNavigation().getPath() == null && !navigationStillOwnsDestination) {
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
                verticalReferenceSpeed, verticalReferenceSpeed * verticalSpeedRatio, settling,
                pitchRate, routeSpeedCap, depthGuidance == null ? 0 : depthGuidance.curvature());
    }

    public record Snapshot(long attempt, String state, Vec3 destination, Vec3 waypoint, float desiredPitch,
                           double remainingDistance, int stalledTicks, Vec3 poweredVelocity, Vec3 externalVelocity,
                           double verticalSpeedRatio, double verticalReferenceSpeed, double verticalSpeedCeiling,
                           boolean settling, float pitchRate, double routeSpeedCap, double depthCurvature) {}

    @Override
    public void tick() {
        if (!mob.isInWater()) {
            poweredCarry = Vec3.ZERO;
            stopInputs("idle");
            return;
        }
        if (operation != Operation.MOVE_TO) {
            prepareSettling();
            return;
        }
        var path = mob.getNavigation().getPath();
        Vec3 navigationDestination = mob.getNavigation() instanceof PitchSwimmingNavigation pitchNavigation
                ? pitchNavigation.requestedDestination() : null;
        // Navigation may finish its internal node path while the finite pitch route is still
        // swimming toward the captured destination. Only cancel when no custom route remains;
        // otherwise the pathfinder would restart the route and accumulate a horizontal orbit.
        if (navigationOwned && path == null && route == null && navigationDestination == null) {
            route = null;
            requestedGoal = null;
            navigationOwned = false;
            stopInputs("cancelled");
            return;
        }
        // Path#getTarget is the pathfinder's current node in this water navigation, not the
        // destination requested by the brain. A vertical path therefore exposed a different
        // depth node every tick and made the body repeatedly choose a new horizontal bearing.
        // Use the navigation target as the stable route destination and keep the path nodes
        // internal to navigation.
        BlockPos navigationTarget = path == null ? null : mob.getNavigation().getTargetPos();
        Vec3 goal = navigationDestination != null ? navigationDestination
                : navigationTarget == null ? new Vec3(wantedX, wantedY, wantedZ)
                : Vec3.atCenterOf(navigationTarget);
        if (blockedGoal != null && blockedGoal.distanceToSqr(goal) < 1.0e-6
                && mob.tickCount < retryAfterTick) {
            stopInputs("blocked");
            return;
        }
        if (requestedGoal == null || requestedGoal.distanceToSqr(goal) > 1.0e-6) {
            requestedGoal = goal;
            routeAttempt++;
            navigationOwned = path != null;
            routeStartedWithOpposingPitch = isOpposingPitch(
                    AquaticMovement.forwardVector(mob.getYRot(), mob.getXRot()), goal.y - mob.getY());
            route = createRoute(goal);
            if (route == null && path == null && !clearSegment(mob.position(), goal)) {
                blockedGoal = goal;
                retryAfterTick = mob.tickCount + 100;
                stopInputs("blocked");
                return;
            }
            progressWaypoint = null;
            stalledTicks = 0;
            committedHeading = false;
        }
        if (route != null && route.arrived(mob.position())) {
            stopInputs("arrived");
            return;
        }

        boolean approachingBeforeTarget = route != null && route.isApproaching();
        // A missing custom route is still governed by the stable destination. Falling back to
        // wantedX/Y/Z here would reintroduce the pathfinder's transient node and undo the
        // destination capture above.
        Vec3 target = route == null ? goal : route.target(mob.position());
        boolean approachingAfterTarget = route != null && route.isApproaching();
        settling = false;
        selectedWaypoint = target;
        Vec3 delta = target.subtract(mob.position());
        remainingVerticalDistance = delta.y;
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
        // A finite entry leg can carry the body a fraction past its destination before
        // the next server tick observes the arrival radius. Release the old heading as
        // soon as that crossing is detected so the swimmer makes one corrective turn
        // back to the endpoint instead of continuing a full horizontal orbit.
        if (bestWaypointDistance < 2.0 && distance > bestWaypointDistance + 0.15) {
            committedHeading = false;
            bestWaypointDistance = distance;
            stalledTicks = 0;
        }
        routeState = route == null ? "path" : route.isApproaching() ? "clearance" : "approach";
        float targetSpeed = (float) (speedModifier * mob.getAttributeValue(Attributes.MOVEMENT_SPEED))
                * waterSpeedMultiplier;
        mob.setSpeed(easeSpeed ? Mth.lerp(0.125F, mob.getSpeed(), targetSpeed) : targetSpeed);
        // Near a vertical target, tiny horizontal residuals are numerical noise. Turning toward
        // each changing residual makes the body sweep left and right while it is climbing or
        // descending. Preserve the established bearing until a body-sized horizontal offset is
        // available for a meaningful turn.
        double horizontalHeadingThreshold = Math.max(0.1, mob.getBbWidth() * 0.5);
        float bearingYaw = delta.horizontalDistance() > horizontalHeadingThreshold
                ? (float) Math.toDegrees(Math.atan2(delta.z, delta.x)) - 90 : mob.getYRot();
        if (!committedHeading || (approachingBeforeTarget && !approachingAfterTarget)) {
            committedYaw = bearingYaw;
            committedHeading = true;
        }
        float desiredYaw = committedHeading ? committedYaw : mob.getYRot();
        boolean stillTurning = Math.abs(Mth.wrapDegrees(desiredYaw - mob.getYRot()))
                > AquaticMovement.MAX_YAW_STEP_DEGREES_PER_TICK;
        mob.setYRot(rotlerp(mob.getYRot(), desiredYaw, AquaticMovement.MAX_YAW_STEP_DEGREES_PER_TICK));
        mob.yBodyRot = mob.getYRot();
        mob.yHeadRot = mob.getYRot();

        float desiredPitch = Mth.clamp((float) -Math.toDegrees(Math.atan2(delta.y, delta.horizontalDistance())),
                -upwardLimit, downwardLimit);
        targetPitch = desiredPitch;
        depthGuidance = AquaticDepthGuidance.approach(delta.horizontalDistance(), delta.y, mob.getXRot());

        double yawError = Math.toRadians(Mth.wrapDegrees(desiredYaw - mob.getYRot()));
        double headingThrottle = Math.max(0.05, Math.cos(yawError));
        mob.setXxa(0);
        mob.setYya(0);
        mob.setZza((float) headingThrottle);
        // Curvature is an angular steering demand, not a reason to stop the animal. The old
        // speed limit divided the pitch step by the whole cubic curve curvature, which reduced
        // a steep but valid route to a nearly stationary nose-up pose. Keep the existing curve
        // telemetry and pitch-rate ceiling, but retain enough scalar propulsion to make the
        // approved class vertical component attainable. The travel integrator applies the same
        // ceiling again to the actual body-forward vector.
        Vec3 routeForward = AquaticMovement.forwardVector(mob.getYRot(), mob.getXRot());
        boolean opposingPitch = routeStartedWithOpposingPitch && isOpposingPitch(routeForward)
                && (route == null || route.isApproaching() || !navigationOwned);
        // Keep scalar propulsion at cruise speed while an already pitched body reorients. The
        // established vertical floor remains in force for ordinary routes and final approach.
        double forwardTravelFloor = Math.abs(mob.getSpeed())
                * (opposingPitch ? 1.0 : verticalSpeedRatio);
        routeSpeedCap = Math.min(distance * 0.1,
                Math.max(depthGuidance.speedLimit(), forwardTravelFloor));
        routeSpeedCap *= Math.max(0.25, headingThrottle);

        if (distance < bestWaypointDistance - 1.0e-4) {
            bestWaypointDistance = distance;
            stalledTicks = 0;
        } else if (stillTurning) {
            // Heading alignment is an intentional part of a finite route. Do not classify the
            // low forward projection during that turn as a stalled depth approach.
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
            Vec3 opposite = AquaticRoute.oppositeClearancePoint(mob.position(), goal, mob.getYRot(), limit,
                    Math.max(1.0, mob.getBbWidth() * 2.0));
            if (opposite != null && clearSegment(mob.position(), opposite) && clearSegment(opposite, goal)) {
                return new AquaticRoute(goal, radius, opposite);
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
        if (mob.getNavigation() instanceof PitchSwimmingNavigation pitchNavigation) {
            pitchNavigation.clearRequestedDestination();
        }
        mob.setXxa(0);
        mob.setYya(0);
        mob.setZza(0);
        routeSpeedCap = 0;
        depthGuidance = null;
        remainingVerticalDistance = 0;
        prepareSettling();
    }

    private void prepareSettling() {
        boolean reserved = settling;
        settling = false;
        mob.setZza(0);
        routeSpeedCap = 0;
        if (Math.abs(mob.getXRot()) < 0.05 && Math.abs(pitchRate) < 0.015) return;
        if (mob.getSpeed() <= 0) {
            mob.setSpeed((float) mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * waterSpeedMultiplier);
        }
        // Leveling is powered swimming, not a stationary pose correction. Keep the existing
        // species vertical travel class as the conservative floor for the final pose exit.
        double exitSpeed = Math.abs(mob.getSpeed()) * verticalSpeedRatio;
        if (!reserved) {
            // A full-speed level exit can be valid in open water but exceed a bounded fixture or
            // a real reef corridor. Reduce only as far as the swept clearance requires. This
            // keeps the exit translating without trading a safe pose for a wall collision.
            for (int attempt = 0; attempt < 8 && !clearExit(exitSpeed); attempt++) {
                exitSpeed *= 0.5;
            }
            if (exitSpeed <= 1.0e-4 || !clearExit(exitSpeed)) return;
        }
        settling = true;
        targetPitch = 0;
        routeSpeedCap = exitSpeed;
        mob.setZza(1);
    }

    private boolean clearExit(double speed) {
        Vec3 position = mob.position();
        Vec3 checked = position;
        float pitch = mob.getXRot();
        float rate = pitchRate;
        for (int tick = 0; tick < 400; tick++) {
            var step = AquaticMovement.stepPitch(pitch, rate, 0);
            pitch = step.pitch();
            rate = step.rate();
            position = position.add(AquaticMovement.forwardVector(mob.getYRot(), pitch).scale(speed));
            boolean finished = Math.abs(pitch) < 0.05 && Math.abs(rate) < 0.015;
            if (tick % 8 == 7 || finished) {
                if (!clearSegment(checked, position)) return false;
                checked = position;
            }
            if (finished) return true;
        }
        return false;
    }

    /** External velocity is the change since the last owned carry, not an orthogonal guess. */
    public void travel(double acceleration, double friction, double horizontalCap, double speedFloor,
                        Vec3 input) {
        Vec3 delta = mob.getDeltaMovement();
        Vec3 external = new Vec3(externalComponent(delta.x, poweredCarry.x),
                externalComponent(delta.y, poweredCarry.y), externalComponent(delta.z, poweredCarry.z));
        // AI movement controls set zza before the entity travel callback, but a few vanilla water
        // paths can still deliver a zero travel vector on the first tick of a new route. If the
        // controller has a live destination, that zero would combine with an empty carry and
        // permanently bootstrap at rest while the nose continues pitching. Own the forward input
        // for that state so a wanted route always has a finite forward propulsion source.
        double propulsionInput = input.z;
        if (propulsionInput <= 0.0 && (operation == Operation.MOVE_TO || settling)) {
            propulsionInput = 1.0;
        }
        double speed = Math.max(0, poweredCarry.length() + acceleration * Math.max(0, propulsionInput));
        if (propulsionInput > 0) speed = Math.max(speed, speedFloor);
        Vec3 forward = AquaticMovement.forwardVector(mob.getYRot(), mob.getXRot());
        boolean meaningfulVerticalError = Math.abs(remainingVerticalDistance) > 0.05;
        boolean opposingPitch = routeStartedWithOpposingPitch && isOpposingPitch(forward)
                && (route == null || route.isApproaching() || !navigationOwned);
        double speedCap = Math.min(horizontalCap / Math.max(1.0e-8, forward.horizontalDistance()), routeSpeedCap);
        if (Math.abs(forward.y) > 1.0e-8 && !opposingPitch) {
            speedCap = Math.min(speedCap,
                    Math.abs(mob.getSpeed()) * verticalSpeedRatio / Math.abs(forward.y));
            if (meaningfulVerticalError) {
                boolean correctingVerticalDirection = remainingVerticalDistance * forward.y < -1.0e-6;
                if (correctingVerticalDirection) {
                    double levelingCap = Math.abs(mob.getSpeed()) * verticalSpeedRatio
                            * (route == null || route.isApproaching() ? 1.0 : 0.125);
                    speedCap = Math.min(speedCap, Math.max(0.01, levelingCap));
                } else {
                    speedCap = Math.min(speedCap, Math.abs(remainingVerticalDistance) / Math.abs(forward.y));
                }
            }
        }
        if (propulsionInput > 0) speed = Math.min(speed, speedCap);
        if (propulsionInput > 0 && (depthGuidance != null || settling)) {
            boolean correctingVerticalDirection = meaningfulVerticalError
                    && forward.y * remainingVerticalDistance < -1.0e-6;
            float desiredRate = settling
                    ? AquaticMovement.stepPitch(mob.getXRot(), pitchRate, 0).rate()
                    : correctingVerticalDirection
                    ? AquaticMovement.stepPitch(mob.getXRot(), pitchRate, targetPitch).rate()
                    : (float) -Math.toDegrees(depthGuidance.curvature() * speed);
            desiredRate = Mth.clamp(desiredRate, -AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK,
                    AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK);
            // Start braking before the hard pitch envelope. Waiting until the
            // final fractional degree would force the position clamp to discard
            // more angular rate than one server tick allows.
            float boundaryBuffer = Math.min(1.0F, AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK * 4.0F);
            float pitchAcceleration = AquaticMovement.MAX_PITCH_ACCELERATION_PER_TICK - 0.0001F;
            double upwardStoppingDistance = pitchRate < 0 ? pitchRate * pitchRate / (2 * pitchAcceleration) : 0;
            double downwardStoppingDistance = pitchRate > 0 ? pitchRate * pitchRate / (2 * pitchAcceleration) : 0;
            if (desiredRate < 0 && (mob.getXRot() <= -upwardLimit + boundaryBuffer
                    || mob.getXRot() + upwardLimit <= upwardStoppingDistance + 0.05)) desiredRate = 0;
            if (desiredRate > 0 && (mob.getXRot() >= downwardLimit - boundaryBuffer
                    || downwardLimit - mob.getXRot() <= downwardStoppingDistance + 0.05)) desiredRate = 0;
            float nextRate = Mth.approach(pitchRate, desiredRate, AquaticMovement.MAX_PITCH_ACCELERATION_PER_TICK);
            nextRate = Mth.clamp(nextRate,
                    AquaticMovement.stepPitch(mob.getXRot(), pitchRate, -upwardLimit).rate(),
                    AquaticMovement.stepPitch(mob.getXRot(), pitchRate, downwardLimit).rate());
            float accelerationMargin = AquaticMovement.MAX_PITCH_ACCELERATION_PER_TICK - 0.0001F;
            nextRate = Mth.clamp(nextRate, pitchRate - accelerationMargin, pitchRate + accelerationMargin);
            float nextPitch = Mth.clamp(mob.getXRot() + nextRate, -upwardLimit, downwardLimit);
            Vec3 nextForward = AquaticMovement.forwardVector(mob.getYRot(), nextPitch);
            if (meaningfulVerticalError && Math.abs(nextForward.y) > 1.0e-8) {
                speed = Math.min(speed, Math.abs(remainingVerticalDistance) / Math.abs(nextForward.y));
            }
            Vec3 nextPowered = opposingPitch
                    ? limitVerticalTravel(nextForward.scale(speed)) : nextForward.scale(speed);
            if (clearSegment(mob.position(), mob.position().add(nextPowered))) {
                pitchRate = nextPitch - mob.getXRot();
                mob.setXRot(nextPitch);
                forward = nextForward;
            }
        }
        Vec3 powered = opposingPitch ? limitVerticalTravel(forward.scale(speed)) : forward.scale(speed);
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

    private Vec3 limitVerticalTravel(Vec3 powered) {
        double verticalCeiling = Math.abs(mob.getSpeed()) * verticalSpeedRatio;
        if (Math.abs(powered.y) <= verticalCeiling) return powered;
        return new Vec3(powered.x, Math.copySign(verticalCeiling, powered.y), powered.z);
    }

    private boolean isOpposingPitch(Vec3 forward) {
        return isOpposingPitch(forward, remainingVerticalDistance);
    }

    private boolean isOpposingPitch(Vec3 forward, double verticalDistance) {
        return Math.abs(verticalDistance) > 0.05
                && Math.abs(mob.getXRot()) > 0.05F
                && verticalDistance * forward.y < -1.0e-6;
    }
}
