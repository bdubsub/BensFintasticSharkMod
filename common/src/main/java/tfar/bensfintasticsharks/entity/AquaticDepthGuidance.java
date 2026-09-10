package tfar.bensfintasticsharks.entity;

/** A cubic depth approach starts at the current tangent and ends level. */
public final class AquaticDepthGuidance {

    private AquaticDepthGuidance() {}

    public record Step(double curvature, double maximumCurvature) {
        public double speedLimit() {
            return maximumCurvature < 1.0e-8 ? Double.POSITIVE_INFINITY
                    : Math.toRadians(AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK) / maximumCurvature;
        }
    }

    public static Step approach(double horizontalDistance, double height, float pitch) {
        double run = Math.max(0.35, horizontalDistance);
        double handle = run / 3;
        double x1 = handle * Math.cos(Math.toRadians(pitch));
        double y1 = -handle * Math.sin(Math.toRadians(pitch));
        double x2 = run - handle;
        double curvature = curvature(0, x1, y1, x2, run, height);
        double maximum = Math.abs(curvature);
        for (int sample = 1; sample <= 16; sample++) {
            double bend = curvature(sample / 16.0, x1, y1, x2, run, height);
            maximum = Math.max(maximum, Math.abs(bend));
        }
        return new Step(curvature, maximum);
    }

    private static double curvature(double t, double x1, double y1, double x2, double run, double height) {
        double u = 1 - t;
        double dx = 3 * (u * u * x1 + 2 * u * t * (x2 - x1) + t * t * (run - x2));
        double dy = 3 * (u * u * y1 + 2 * u * t * (height - y1));
        double ddx = 6 * (u * (x2 - 2 * x1) + t * (run - 2 * x2 + x1));
        double ddy = 6 * (u * (height - 2 * y1) + t * (y1 - height));
        return (dx * ddy - dy * ddx) / Math.pow(dx * dx + dy * dy, 1.5);
    }
}
