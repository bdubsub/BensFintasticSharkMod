package tfar.bensfintasticsharks.entity;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Deterministic coverage guard for the runtime species behavior contract. */
class SpeciesBehaviorProfileTest {

    private static final Set<String> EXPECTED = Set.of(
            "great_white_shark", "great_hammerhead_shark", "common_thresher_shark",
            "shortfin_mako_shark", "tiger_shark", "oceanic_whitetip_shark", "sandtiger_shark",
            "blacktip_reef_shark", "orca", "bottlenose_dolphin", "common_octopus",
            "caribbean_reef_octopus", "nautilus", "giant_moray_eel", "green_sea_turtle",
            "american_lobster", "common_stingray", "harbor_seal", "black_sea_nettle_jellyfish",
            "cannonball_jellyfish", "atlantic_cod", "atlantic_salmon");

    @Test
    void runtimeProfilesCoverExactlyTheLivingRegistry() {
        Set<String> actual = SpeciesBehaviorProfile.all().stream()
                .map(SpeciesBehaviorProfile.Profile::id).collect(Collectors.toSet());
        assertEquals(EXPECTED, actual);
        assertEquals(EXPECTED.size(), SpeciesBehaviorProfile.all().size());
        for (String id : EXPECTED) {
            SpeciesBehaviorProfile.Profile profile = SpeciesBehaviorProfile.forId(id);
            assertNotNull(profile);
            assertTrue(profile.scanRadius() > 0, id);
            assertTrue(profile.actionTimeoutTicks() > 0, id);
            assertTrue(profile.memoryTicks() >= 0, id);
            assertFalse(profile.locomotion() == null, id);
        }
    }

    @Test
    void familyPoliciesRemainDistinctAndExplicit() {
        assertEquals(SpeciesBehaviorProfile.Family.SHARK,
                SpeciesBehaviorProfile.forId("tiger_shark").family());
        assertEquals(SpeciesBehaviorProfile.ThreatResponse.INK_ESCAPE,
                SpeciesBehaviorProfile.forId("common_octopus").threatResponse());
        assertEquals(SpeciesBehaviorProfile.Locomotion.BOTTOM_WALK,
                SpeciesBehaviorProfile.forId("american_lobster").locomotion());
        assertEquals(SpeciesBehaviorProfile.FoodMode.PLANKTON,
                SpeciesBehaviorProfile.forId("cannonball_jellyfish").foodMode());
        assertTrue(SpeciesBehaviorProfile.forId("bottlenose_dolphin").social());
        assertTrue(SpeciesBehaviorProfile.forId("green_sea_turtle").needsSurface());
    }
}
