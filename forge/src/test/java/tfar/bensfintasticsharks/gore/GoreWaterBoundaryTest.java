package tfar.bensfintasticsharks.gore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoreWaterBoundaryTest {
    @Test
    void rotatedBillboardCornersStayBelowSurface() {
        for (float size : new float[] {0.02F, 0.5F, 4.0F, 12.0F}) {
            double center = GoreWaterBoundary.centerBelowSurface(63.99D, 64.0D, size);
            for (int degrees = 0; degrees < 360; degrees++) {
                double angle = Math.toRadians(degrees);
                double topCorner = center + size * (Math.abs(Math.sin(angle)) + Math.abs(Math.cos(angle)));
                assertTrue(topCorner < 64.0D);
            }
        }
    }

    @Test
    void deepCloudIsNotPulledTowardSurface() {
        assertEquals(20.0D, GoreWaterBoundary.centerBelowSurface(20.0D, 64.0D, 0.5F));
    }
}
