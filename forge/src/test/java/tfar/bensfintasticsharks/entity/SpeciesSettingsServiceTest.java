package tfar.bensfintasticsharks.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpeciesSettingsServiceTest {

    @BeforeEach
    void reset() {
        SpeciesSettingsService.resetSession();
        SpeciesSettingsService.reloadBaseline(SpeciesSettingsService.defaultBaseline());
    }

    @Test
    void inventoryContainsEveryCurrentSpeciesAndEveryTypedField() {
        assertEquals(22, SpeciesSettingsService.speciesIds().size());
        assertEquals(SpeciesSettingsService.Field.values().length,
                SpeciesSettingsService.snapshot().species().get("great_white_shark").fields().size());
    }

    @Test
    void wildcardPatchPublishesOneRevisionToAllSpecies() {
        long revision = SpeciesSettingsService.revision();
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(SpeciesSettingsService.Field.HORIZONTAL_SPEED, 6.0D);
        patch.put(SpeciesSettingsService.Field.VERTICAL_SPEED, 4.0D);

        SpeciesSettingsService.MutationResult result = SpeciesSettingsService.apply(revision, "*", patch);

        assertTrue(result.applied());
        assertEquals(revision + 1, result.revision());
        assertEquals(22, result.targets().size());
        assertEquals("session", result.snapshot().species().get("atlantic_cod")
                .field(SpeciesSettingsService.Field.HORIZONTAL_SPEED).source());
        assertEquals(6.0D, result.snapshot().species().get("great_white_shark")
                .field(SpeciesSettingsService.Field.HORIZONTAL_SPEED).value());
    }

    @Test
    void invalidOrStalePatchLeavesTheWholeSnapshotUnchanged() {
        long revision = SpeciesSettingsService.revision();
        Map<SpeciesSettingsService.Field, Double> invalid = Map.of(
                SpeciesSettingsService.Field.HORIZONTAL_SPEED, 21.0D);
        SpeciesSettingsService.MutationResult invalidResult = SpeciesSettingsService.apply(revision, "*", invalid);
        assertFalse(invalidResult.applied());
        assertEquals(revision, SpeciesSettingsService.revision());

        Map<SpeciesSettingsService.Field, Double> valid = Map.of(
                SpeciesSettingsService.Field.VERTICAL_SPEED, 5.0D);
        SpeciesSettingsService.MutationResult stale = SpeciesSettingsService.apply(revision - 1, "*", valid);
        assertFalse(stale.applied());
        assertEquals(revision, SpeciesSettingsService.revision());
    }

    @Test
    void pairBoundsRejectPartialSpawnAndScaleChanges() {
        long revision = SpeciesSettingsService.revision();
        SpeciesSettingsService.MutationResult spawn = SpeciesSettingsService.apply(revision, "great_white_shark",
                Map.of(SpeciesSettingsService.Field.SPAWN_GROUP_MIN, 5.0D,
                        SpeciesSettingsService.Field.SPAWN_GROUP_MAX, 2.0D));
        assertFalse(spawn.applied());
        assertEquals(revision, SpeciesSettingsService.revision());

        SpeciesSettingsService.MutationResult scale = SpeciesSettingsService.apply(revision, "great_white_shark",
                Map.of(SpeciesSettingsService.Field.SCALE_MIN, 1.5D,
                        SpeciesSettingsService.Field.SCALE_MAX, 1.0D));
        assertFalse(scale.applied());
        assertEquals(revision, SpeciesSettingsService.revision());
    }

    @Test
    void resetOnlyRemovesRequestedSessionFields() {
        long revision = SpeciesSettingsService.revision();
        SpeciesSettingsService.apply(revision, "great_white_shark",
                Map.of(SpeciesSettingsService.Field.HORIZONTAL_SPEED, 6.0D,
                        SpeciesSettingsService.Field.VERTICAL_SPEED, 4.0D));
        revision = SpeciesSettingsService.revision();
        SpeciesSettingsService.MutationResult result = SpeciesSettingsService.reset(revision,
                "great_white_shark", Set.of(SpeciesSettingsService.Field.HORIZONTAL_SPEED));

        assertTrue(result.applied());
        SpeciesSettingsService.SpeciesSnapshot snapshot = result.snapshot().species().get("great_white_shark");
        assertEquals("server_config", snapshot.field(SpeciesSettingsService.Field.HORIZONTAL_SPEED).source());
        assertEquals("session", snapshot.field(SpeciesSettingsService.Field.VERTICAL_SPEED).source());
    }

    @Test
    void invalidReloadRetainsPriorBaselineAndOverrides() {
        long revision = SpeciesSettingsService.revision();
        SpeciesSettingsService.apply(revision, "great_white_shark",
                Map.of(SpeciesSettingsService.Field.HORIZONTAL_SPEED, 7.0D));
        revision = SpeciesSettingsService.revision();
        Map<String, Map<SpeciesSettingsService.Field, Double>> invalid =
                new java.util.HashMap<>(SpeciesSettingsService.defaultBaseline());
        invalid.remove("atlantic_cod");

        SpeciesSettingsService.ReloadResult result = SpeciesSettingsService.reloadBaseline(invalid);

        assertFalse(result.applied());
        assertEquals(revision, SpeciesSettingsService.revision());
        assertEquals("session", SpeciesSettingsService.resolve("great_white_shark")
                .field(SpeciesSettingsService.Field.HORIZONTAL_SPEED).source());
    }
}
