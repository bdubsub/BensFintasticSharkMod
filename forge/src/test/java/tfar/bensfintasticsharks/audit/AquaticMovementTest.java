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
        assertEquals(-90.0, AquaticMovement.affectedPitch(0.0, 10.0, 0.0), 0.00001);
        assertEquals(90.0, AquaticMovement.affectedPitch(0.0, -10.0, 0.0), 0.00001);
        assertEquals(0.0, AquaticMovement.affectedPitch(10.0, 0.0, 0.0), 0.00001);
        assertTrue(Float.isFinite(AquaticMovement.affectedPitch(0.0, 0.0, 0.0)));
    }
}
