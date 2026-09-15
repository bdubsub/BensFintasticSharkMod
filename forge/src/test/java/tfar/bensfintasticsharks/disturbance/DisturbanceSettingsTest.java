package tfar.bensfintasticsharks.disturbance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DisturbanceSettingsTest {

    private static DisturbanceSettings.Effective policy(boolean enabled, boolean sourceEnabled,
                                                        DisturbanceSettings.Reaction reaction,
                                                        double radius, double sensitivity, double strength,
                                                        int interval) {
        return new DisturbanceSettings.Effective("great_white_shark", 7L, true, "supported",
                enabled, sourceEnabled, reaction, radius, sensitivity, strength, interval, 20, 100, 0.02D);
    }

    @Test
    void thresholdEqualityIsAcceptedAndCooldownIsNamed() {
        DisturbanceSettings.Effective settings = policy(true, true, DisturbanceSettings.Reaction.ALERT,
                24.0D, 1.0D, 0.5D, 20);
        DisturbanceSettings.Decision accepted = DisturbanceSettings.evaluate(settings, 0.5D,
                0.0D, 100L, null);
        assertTrue(accepted.accepted());
        assertTrue(accepted.thresholdAccepted());
        assertEquals(0.25D, accepted.effectiveStrength(), 1.0e-9);
        DisturbanceSettings.Decision cooldown = DisturbanceSettings.evaluate(settings, 0.5D,
                0.0D, 110L, 100L);
        assertFalse(cooldown.accepted());
        assertEquals("cooldown", cooldown.reason());
    }

    @Test
    void policyReportsDistinctDisabledThresholdRangeAndReactionReasons() {
        assertEquals("global_disabled", DisturbanceSettings.evaluate(
                policy(false, true, DisturbanceSettings.Reaction.ALERT, 24, 1, 1, 20),
                1, 0, 0, null).reason());
        assertEquals("source_disabled", DisturbanceSettings.evaluate(
                policy(true, false, DisturbanceSettings.Reaction.ALERT, 24, 1, 1, 20),
                1, 0, 0, null).reason());
        assertEquals("reaction_ignored", DisturbanceSettings.evaluate(
                policy(true, true, DisturbanceSettings.Reaction.IGNORE, 24, 1, 1, 20),
                1, 0, 0, null).reason());
        assertEquals("low_threshold", DisturbanceSettings.evaluate(
                policy(true, true, DisturbanceSettings.Reaction.ALERT, 24, 1, 0.4, 20),
                0.5, 0, 0, null).reason());
        assertEquals("outside_radius", DisturbanceSettings.evaluate(
                policy(true, true, DisturbanceSettings.Reaction.ALERT, 12, 1, 1, 20),
                1, 13, 0, null).reason());
    }

    @Test
    void investigateProducesASeparateAcceptedOutcome() {
        DisturbanceSettings.Decision decision = DisturbanceSettings.evaluate(
                policy(true, true, DisturbanceSettings.Reaction.INVESTIGATE, 24, 1, 1, 20),
                1.0D, 4.0D, 40L, null);
        assertTrue(decision.accepted());
        assertEquals("investigate", decision.outcome());
        assertEquals("eligible_sharks", decision.reason());
    }
}
