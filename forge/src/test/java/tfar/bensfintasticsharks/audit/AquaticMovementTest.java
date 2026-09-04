package tfar.bensfintasticsharks.audit;

import org.junit.jupiter.api.Test;
import tfar.bensfintasticsharks.entity.AquaticMovement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AquaticMovementTest {

    @Test
    void affectedPitchUsesTheScaledThreeDimensionalVector() {
        float pitch = AquaticMovement.affectedPitch(10.0, 10.0, 0.0);
        assertEquals(-Math.toDegrees(Math.atan2(0.1, 1.0)), pitch, 0.00001);
        assertEquals(-pitch, AquaticMovement.affectedPitch(10.0, -10.0, 0.0), 0.00001);
    }

    @Test
    void verticalOnlyAndLevelVectorsRemainFiniteAndDirectional() {
        double shallowAngle = Math.toDegrees(Math.atan(AquaticMovement.VERTICAL_SPEED_RATIO));
        assertEquals(-shallowAngle, AquaticMovement.affectedPitch(0.0, 10.0, 0.0), 0.00001);
        assertEquals(shallowAngle, AquaticMovement.affectedPitch(0.0, -10.0, 0.0), 0.00001);
        assertEquals(0.0, AquaticMovement.affectedPitch(10.0, 0.0, 0.0), 0.00001);
        assertTrue(Float.isFinite(AquaticMovement.affectedPitch(0.0, 0.0, 0.0)));
    }

    @Test
    void profilePitchLimitsRejectUprightAndOversteepRoutes() {
        assertEquals(-8.0, AquaticMovement.affectedPitch(0.001, 100.0, 0.0, 8.0f, 8.0f), 0.00001);
        assertEquals(5.0, AquaticMovement.affectedPitch(0.001, -100.0, 0.0, 14.0f, 5.0f), 0.00001);
        assertTrue(Math.abs(AquaticMovement.affectedPitch(0.001, 100.0, 0.0, 10.0f, 10.0f)) <= 10.0f);
        assertEquals(0.30f, AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK, 0.00001f);
    }

    @Test
    void affectedVerticalVelocityUsesTheApprovedRatio() {
        assertEquals(1.0, AquaticMovement.affectedVerticalVelocity(10.0, 0.0, 10.0, 0.0), 0.00001);
        assertEquals(-1.0, AquaticMovement.affectedVerticalVelocity(10.0, 0.0, -10.0, 0.0), 0.00001);
        assertEquals(0.0, AquaticMovement.affectedVerticalVelocity(10.0, 10.0, 0.0, 0.0), 0.00001);
        assertEquals(0.0, AquaticMovement.affectedVerticalVelocity(10.0, 0.0, 0.0, 0.0), 0.00001);
    }

    @Test
    void verticalControlEasesTowardTargetAndThroughReversal() {
        double first = AquaticMovement.smoothVerticalVelocity(0.0, 1.0);
        double second = AquaticMovement.smoothVerticalVelocity(first, 1.0);
        double reversal = AquaticMovement.smoothVerticalVelocity(second, -1.0);

        assertTrue(first > 0.0 && first < 1.0);
        assertTrue(second > first && second < 1.0);
        assertTrue(reversal < second && reversal > -1.0);
    }
}
