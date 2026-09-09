package tfar.bensfintasticsharks.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OceanicPresentationStateTest {
    @Test
    void locomotionYieldsToOnlyAValidServerGrab() {
        assertTrue(OceanicPresentationState.hasActiveGrab(1, true));
        assertFalse(OceanicPresentationState.hasActiveGrab(0, true));
        assertFalse(OceanicPresentationState.hasActiveGrab(1, false));
        assertFalse(OceanicPresentationState.hasActiveGrab(-1, true));
    }
}
