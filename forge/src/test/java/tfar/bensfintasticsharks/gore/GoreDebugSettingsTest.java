package tfar.bensfintasticsharks.gore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoreDebugSettingsTest {
    @BeforeEach
    void reset() {
        GoreDebugSettings.reset();
    }

    @Test
    void acceptsBoundedFieldsAndRejectsInvalidValuesWithoutPartialMutation() {
        assertTrue(GoreDebugSettings.set("particle_count", 64));
        assertTrue(GoreDebugSettings.set("particle_scale", 4.0));
        assertTrue(GoreDebugSettings.set("wound_lifetime", 1));
        assertTrue(GoreDebugSettings.set("remains_lifetime", 6000));
        GoreDebugSettings.Settings beforeInvalid = GoreDebugSettings.current();

        assertFalse(GoreDebugSettings.set("particle_count", 1.5));
        assertFalse(GoreDebugSettings.set("particle_scale", Double.NaN));
        assertFalse(GoreDebugSettings.set("wound_lifetime", 1201));
        assertFalse(GoreDebugSettings.set("unknown", 1));
        assertEquals(beforeInvalid, GoreDebugSettings.current());
    }

    @Test
    void resetRestoresSessionDefaults() {
        GoreDebugSettings.set("particle_count", 0);
        GoreDebugSettings.set("remains_scale", 3.5);
        GoreDebugSettings.reset();
        assertEquals(new GoreDebugSettings.Settings(8, 0.8F, 24, 0.75F, 80, 2, 1.0F, 120),
                GoreDebugSettings.current());
    }
}
