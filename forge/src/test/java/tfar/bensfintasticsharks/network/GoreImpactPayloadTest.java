package tfar.bensfintasticsharks.network;

import org.junit.jupiter.api.Test;
import tfar.bensfintasticsharks.gore.GoreDebugSettings;
import tfar.bensfintasticsharks.gore.GoreImpactKind;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoreImpactPayloadTest {
    private static final GoreDebugSettings.Settings DEFAULTS =
            new GoreDebugSettings.Settings(8, 0.8F, 24, 0.75F, 80, 2, 1.0F, 120);

    @Test
    void acceptsBoundedVisualCue() {
        assertTrue(new GoreImpactPayload(1, 24.5D, 62.0D, -18.25D, GoreImpactKind.BITE, DEFAULTS).isValid());
        assertTrue(new GoreImpactPayload(1, 24.5D, 62.0D, -18.25D, GoreImpactKind.FATAL, DEFAULTS).fatal());
    }

    @Test
    void rejectsInvalidEntityAndCoordinates() {
        assertFalse(new GoreImpactPayload(0, 0, 64, 0, GoreImpactKind.BITE, DEFAULTS).isValid());
        assertFalse(new GoreImpactPayload(1, Double.NaN, 64, 0, GoreImpactKind.BITE, DEFAULTS).isValid());
        assertFalse(new GoreImpactPayload(1, 0, 4097, 0, GoreImpactKind.BITE, DEFAULTS).isValid());
    }

    @Test
    void rejectsOutOfRangeParticleTuning() {
        assertFalse(new GoreImpactPayload(1, 0, 64, 0, GoreImpactKind.BITE,
                65, 0.8F, 24, 0.75F, 80, 2, 1.0F, 120).isValid());
        assertFalse(new GoreImpactPayload(1, 0, 64, 0, GoreImpactKind.BITE,
                8, Float.POSITIVE_INFINITY, 24, 0.75F, 80, 2, 1.0F, 120).isValid());
        assertFalse(new GoreImpactPayload(1, 0, 64, 0, GoreImpactKind.BITE,
                8, 0.8F, 24, 0.75F, 80, 2, 1.0F, 6001).isValid());
        assertFalse(new GoreImpactPayload(1, 0, 64, 0, null, DEFAULTS).isValid());
    }
}
