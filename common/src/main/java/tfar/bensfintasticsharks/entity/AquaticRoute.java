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
        double minimumRun = Math.abs(delta.y) / Math.tan(Math.toRadians(Math.min(90, Math.abs(pitchLimit))));
        if (Math.abs(delta.y) <= 0.35 || horizontal >= Math.max(0.35, minimumRun)) return null;
        double required = minimumRun + clearance;
        Vec3 bearing = horizontal > clearance
                ? new Vec3(delta.x / horizontal, 0, delta.z / horizontal)
                : AquaticMovement.forwardVector(yaw, 0).scale(-1);
        return new Vec3(destination.x - bearing.x * required, position.y,
                destination.z - bearing.z * required);
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
