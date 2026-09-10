package tfar.bensfintasticsharks.audit;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;
import tfar.bensfintasticsharks.entity.AquaticMovement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AquaticMovementTest {

    @Test
    void pitchBrakesBeforeItsEndpointAndReversesWithBoundedAcceleration() {
        float pitch = 0;
        float rate = 0;
        for (int tick = 0; tick < 600; tick++) {
            var step = AquaticMovement.stepPitch(pitch, rate, tick < 250 ? -45 : 60);
            assertTrue(Math.abs(step.rate() - rate) <= AquaticMovement.MAX_PITCH_ACCELERATION_PER_TICK + 0.00001);
            assertTrue(Math.abs(step.rate()) <= AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK + 0.00001);
            assertTrue(step.pitch() >= -45.001 && step.pitch() <= 60.001);
            pitch = step.pitch();
            rate = step.rate();
        }
    }

    @Test
    void affectedPitchUsesTheScaledThreeDimensionalVector() {
        float pitch = AquaticMovement.affectedPitch(10.0, 10.0, 0.0);
        assertEquals(-Math.toDegrees(Math.atan2(0.1, 1.0)), pitch, 0.00001);
        assertEquals(-pitch, AquaticMovement.affectedPitch(10.0, -10.0, 0.0), 0.00001);
    }

    @Test
    void verticalOnlyAndLevelVectorsRemainFiniteAndDirectional() {
        assertEquals(AquaticMovement.VERTICAL_UPWARD_PITCH,
                AquaticMovement.affectedPitch(0.0, 10.0, 0.0), 0.00001);
        assertEquals(AquaticMovement.VERTICAL_DOWNWARD_PITCH,
                AquaticMovement.affectedPitch(0.0, -10.0, 0.0), 0.00001);
        assertEquals(0.0, AquaticMovement.affectedPitch(10.0, 0.0, 0.0), 0.00001);
        assertTrue(Float.isFinite(AquaticMovement.affectedPitch(0.0, 0.0, 0.0)));
    }

    @Test
    void profilePitchLimitsRejectUprightAndOversteepRoutes() {
        assertEquals(-8.0, AquaticMovement.affectedPitch(0.001, 100.0, 0.0, 8.0f, 8.0f), 0.00001);
        assertEquals(5.0, AquaticMovement.affectedPitch(0.001, -100.0, 0.0, 14.0f, 5.0f), 0.00001);
        assertTrue(Math.abs(AquaticMovement.affectedPitch(0.001, 100.0, 0.0, 10.0f, 10.0f)) <= 10.0f);
        assertEquals(-AquaticMovement.FISH_HARD_UPWARD_PITCH_LIMIT,
                AquaticMovement.affectedPitch(0.0, 100.0, 0.0, 8.0f, 8.0f,
                        AquaticMovement.FISH_HARD_UPWARD_PITCH_LIMIT,
                        AquaticMovement.FISH_HARD_DOWNWARD_PITCH_LIMIT), 0.00001);
        assertEquals(AquaticMovement.FISH_HARD_DOWNWARD_PITCH_LIMIT,
                AquaticMovement.affectedPitch(0.0, -100.0, 0.0, 8.0f, 8.0f,
                        AquaticMovement.FISH_HARD_UPWARD_PITCH_LIMIT,
                        AquaticMovement.FISH_HARD_DOWNWARD_PITCH_LIMIT), 0.00001);
        assertEquals(AquaticMovement.SHARK_HARD_DOWNWARD_PITCH_LIMIT,
                AquaticMovement.affectedPitch(0.0, -100.0, 0.0, 14.0f, 5.0f,
                        AquaticMovement.SHARK_HARD_UPWARD_PITCH_LIMIT,
                        AquaticMovement.SHARK_HARD_DOWNWARD_PITCH_LIMIT), 0.00001);
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
    void fishAndSharkProfilesUseTheirOwnCurrentVerticalRatiosExactlyOnce() {
        assertEquals(0.20d, AquaticMovement.FISH_VERTICAL_SPEED_RATIO, 0.00001d);
        assertEquals(0.25d, AquaticMovement.SHARK_VERTICAL_SPEED_RATIO, 0.00001d);
        assertEquals(-2.0d, AquaticMovement.affectedVerticalVelocity(10.0d, 0.0d, -10.0d, 0.0d,
                AquaticMovement.FISH_VERTICAL_SPEED_RATIO), 0.00001d);
        assertEquals(-2.5d, AquaticMovement.affectedVerticalVelocity(10.0d, 0.0d, -10.0d, 0.0d,
                AquaticMovement.SHARK_VERTICAL_SPEED_RATIO), 0.00001d);
        assertEquals(0.8d, AquaticMovement.smoothAndLimitVerticalVelocity(4.0d, 0.8d, 4.0d,
                AquaticMovement.FISH_VERTICAL_SPEED_RATIO), 0.00001d);
        assertEquals(1.0d, AquaticMovement.smoothAndLimitVerticalVelocity(4.0d, 1.0d, 4.0d,
                AquaticMovement.SHARK_VERTICAL_SPEED_RATIO), 0.00001d);
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

    @Test
    void verticalControlCapsAStaleFullImpulseToTheApprovedFraction() {
        double limited = AquaticMovement.smoothAndLimitVerticalVelocity(4.0, 0.4, 4.0);

        assertEquals(0.4, limited, 0.00001);
        assertEquals(-0.4,
                AquaticMovement.smoothAndLimitVerticalVelocity(-4.0, -0.4, 4.0), 0.00001);
    }

    @Test
    void bodyAlignedInputDerivesVerticalThrustFromPitchOnly() {
        Vec3 level = AquaticMovement.bodyAlignedInput(new Vec3(0.25, 0.8, 1.0), 0.0f);
        assertEquals(0.25, level.x, 0.00001);
        assertEquals(0.0, level.y, 0.00001);
        assertEquals(1.0, level.z, 0.00001);

        Vec3 upright = AquaticMovement.bodyAlignedInput(new Vec3(0.0, 0.0, 1.0), -90.0f);
        assertEquals(0.0, upright.x, 0.00001);
        assertEquals(1.0, upright.y, 0.00001);
        assertEquals(0.0, upright.z, 0.00001);
    }

    @Test
    void poweredVelocityCapsVerticalComponentWithoutChangingDirection() {
        Vec3 forward = AquaticMovement.forwardVector(0.0f, -45.0f);
        Vec3 limited = AquaticMovement.limitPoweredVelocity(forward.scale(2.0), forward, 0.5, 0.1);

        assertEquals(0.1 / Math.abs(forward.y), limited.dot(forward), 0.00001);
        assertEquals(forward.x / forward.z, limited.x / limited.z, 0.00001);
        assertTrue(Math.abs(limited.y) <= 0.1 + 0.00001);
    }

    @Test
    void unalignedVerticalSlipIsRemovedWhileLateralDriftRemains() {
        Vec3 velocity = AquaticMovement.removeUnalignedVerticalSlip(
                new Vec3(0.25, 0.4, 1.0), new Vec3(0.0, 0.0, 1.0));

        assertEquals(0.25, velocity.x, 0.00001);
        assertEquals(0.0, velocity.y, 0.00001);
        assertEquals(1.0, velocity.z, 0.00001);
    }
}
