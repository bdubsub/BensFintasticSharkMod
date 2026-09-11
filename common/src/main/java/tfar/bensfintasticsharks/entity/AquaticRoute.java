package tfar.bensfintasticsharks.entity;

import net.minecraft.world.phys.Vec3;

/** A finite approach with a separate clearance leg for steep destinations. */
public final class AquaticRoute {

    private final Vec3 destination;
    private final double arrivalRadius;
    private Vec3 approach;

    public AquaticRoute(Vec3 destination, double arrivalRadius, Vec3 approach) {
        this.destination = destination;
        this.arrivalRadius = arrivalRadius;
        this.approach = approach;
    }

    public static Vec3 clearancePoint(Vec3 position, Vec3 destination, float yaw,
                                      float pitchLimit, double clearance) {
        Vec3 delta = destination.subtract(position);
        double horizontal = delta.horizontalDistance();
        double minimumRun = 1.5 * Math.abs(delta.y)
                / Math.tan(Math.toRadians(Math.min(90, Math.abs(pitchLimit))));
        if (Math.abs(delta.y) <= 0.35 || horizontal >= Math.max(0.35, minimumRun)) return null;
        double required = minimumRun + clearance;
        if (horizontal <= clearance) {
            // A truly vertical target has no destination bearing. Take one bounded
            // forward clearance leg in the current heading, then turn once toward
            // the target. Choosing the leg from the current position keeps it in
            // the swimmer's existing water volume instead of driving backward into
            // a wall and falling back to an unbounded straight run.
            Vec3 bearing = AquaticMovement.forwardVector(yaw, 0);
            Vec3 leg = position.add(bearing.scale(required));
            return new Vec3(leg.x, position.y, leg.z);
        }
        Vec3 bearing = new Vec3(delta.x / horizontal, 0, delta.z / horizontal);
        return new Vec3(destination.x - bearing.x * required, position.y,
                destination.z - bearing.z * required);
    }

    /** Returns the mirrored finite entry leg when the preferred heading is blocked. */
    public static Vec3 oppositeClearancePoint(Vec3 position, Vec3 destination, float yaw,
                                              float pitchLimit, double clearance) {
        Vec3 delta = destination.subtract(position);
        double horizontal = delta.horizontalDistance();
        double minimumRun = 1.5 * Math.abs(delta.y)
                / Math.tan(Math.toRadians(Math.min(90, Math.abs(pitchLimit))));
        if (Math.abs(delta.y) <= 0.35 || horizontal >= Math.max(0.35, minimumRun)) return null;
        double required = minimumRun + clearance;
        if (horizontal <= clearance) {
            Vec3 bearing = AquaticMovement.forwardVector(yaw, 0);
            Vec3 leg = position.subtract(bearing.scale(required));
            return new Vec3(leg.x, position.y, leg.z);
        }
        Vec3 bearing = new Vec3(delta.x / horizontal, 0, delta.z / horizontal);
        return new Vec3(destination.x + bearing.x * required, position.y,
                destination.z + bearing.z * required);
    }

    public Vec3 target(Vec3 position) {
        if (approach != null && position.distanceToSqr(approach) <= arrivalRadius * arrivalRadius) {
            approach = null;
        }
        return approach == null ? destination : approach;
    }

    public boolean arrived(Vec3 position) {
        return approach == null && position.distanceToSqr(destination) <= arrivalRadius * arrivalRadius;
    }

    public Vec3 destination() {
        return destination;
    }

    public boolean isApproaching() {
        return approach != null;
    }
}
