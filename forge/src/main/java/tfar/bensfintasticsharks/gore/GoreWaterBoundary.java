package tfar.bensfintasticsharks.gore;

public final class GoreWaterBoundary {
    private GoreWaterBoundary() {
    }

    public static double centerBelowSurface(double centerY, double surfaceY, float halfSize) {
        return Math.min(centerY, surfaceY - halfSize * Math.sqrt(2.0D) - 0.01D);
    }
}
