package tfar.bensfintasticsharks.audit;

import org.junit.jupiter.api.Test;
import tfar.bensfintasticsharks.entity.AquaticDepthGuidance;

import static org.junit.jupiter.api.Assertions.*;

class AquaticDepthGuidanceTest {

    @Test
    void depthCurveTurnsUpThenReservesALevelExit() {
        assertTrue(AquaticDepthGuidance.approach(12, 4, 0).curvature() > 0);
        assertTrue(AquaticDepthGuidance.approach(4, 0.4, -20).curvature() < 0);
        assertTrue(AquaticDepthGuidance.approach(12, -4, 0).curvature() < 0);
        assertTrue(AquaticDepthGuidance.approach(4, -0.4, 20).curvature() > 0);
    }

    @Test
    void levelCruiseHasNoPitchThrottleAndShortReversalsRemainFinite() {
        assertEquals(Double.POSITIVE_INFINITY, AquaticDepthGuidance.approach(12, 0, 0).speedLimit());
        var turn = AquaticDepthGuidance.approach(4.6, 3.8, 1.2f);
        assertTrue(turn.speedLimit() > 0.003);
        for (int pitch = -90; pitch <= 90; pitch++) {
            var endpoint = AquaticDepthGuidance.approach(0, 4, pitch);
            assertTrue(Double.isFinite(endpoint.curvature()));
            assertTrue(Double.isFinite(endpoint.speedLimit()));
            assertTrue(endpoint.speedLimit() > 0);
        }
    }
}
