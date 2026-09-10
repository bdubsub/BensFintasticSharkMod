package tfar.bensfintasticsharks.audit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Schema and coverage guard for the Phase 002 species calibration ledger. */
class SpeciesProfileManifestTest {

    private static final Set<String> SPECIES = Set.of(
            "great_white_shark", "great_hammerhead_shark", "common_thresher_shark",
            "shortfin_mako_shark", "tiger_shark", "oceanic_whitetip_shark", "sandtiger_shark",
            "blacktip_reef_shark", "orca", "bottlenose_dolphin", "common_octopus",
            "caribbean_reef_octopus", "nautilus", "giant_moray_eel", "green_sea_turtle",
            "american_lobster", "common_stingray", "harbor_seal", "black_sea_nettle_jellyfish",
            "cannonball_jellyfish", "atlantic_cod", "atlantic_salmon");

    private static final Set<String> ACTION_FIELDS = Set.of(
            "preconditions", "candidate_ranking", "memory_ttl_seconds", "max_cardinality",
            "movement_owner", "completion", "timeout_seconds", "preemption", "cleanup",
            "retry_seconds", "presentation", "positive_fixture", "negative_fixture");

    private static final Path ROOT = findProjectRoot();

    @Test
    void manifestCoversEveryRegisteredSpeciesAndActionContract() throws IOException {
        JsonObject manifest = readManifest();
        assertEquals("bfs.phase-002.species-profile.v1", manifest.get("schema").getAsString());
        assertFalse(manifest.get("status").getAsString().isBlank());
        assertFalse(manifest.get("revision").getAsString().isBlank());

        JsonObject catalog = manifest.getAsJsonObject("action_catalog");
        assertFalse(catalog.entrySet().isEmpty());
        for (var entry : catalog.entrySet()) {
            JsonObject action = entry.getValue().getAsJsonObject();
            for (String field : ACTION_FIELDS) {
                assertTrue(action.has(field), entry.getKey() + " missing " + field);
                assertFalse(action.get(field).isJsonNull(), entry.getKey() + " null " + field);
            }
            assertTrue(action.get("max_cardinality").getAsInt() > 0, entry.getKey());
            assertTrue(action.get("memory_ttl_seconds").getAsInt() >= 0, entry.getKey());
            assertTrue(action.get("timeout_seconds").getAsInt() > 0, entry.getKey());
        }

        JsonArray rows = manifest.getAsJsonArray("species");
        assertEquals(SPECIES.size(), rows.size());
        Set<String> ids = new HashSet<>();
        for (JsonElement element : rows) {
            JsonObject row = element.getAsJsonObject();
            String id = requiredString(row, "id");
            assertTrue(ids.add(id), "duplicate species row " + id);
            assertTrue(SPECIES.contains(id), "unregistered species row " + id);
            requiredString(row, "scientific_name");
            requiredString(row, "evidence_confidence");
            requiredString(row, "family");
            requiredString(row, "locomotion_mode");
            requiredString(row, "body_length_convention");
            requiredString(row.getAsJsonObject("horizontal_baseline"), "unit");

            JsonObject vertical = row.getAsJsonObject("vertical_profile");
            assertNotNull(vertical, id);
            for (String field : Set.of("cruise_ascent", "cruise_descent", "hard_ascent", "hard_descent")) {
                assertTrue(vertical.get(field).isJsonPrimitive(), id + " missing numeric " + field);
                assertTrue(vertical.get(field).getAsDouble() >= 0.0, id + " negative " + field);
            }
            assertTrue(vertical.get("vertical_ceiling").getAsDouble() > 0.0, id);
            requiredString(vertical, "angle_unit");
            requiredString(vertical, "vertical_ceiling_unit");
            requiredString(vertical, "kind");
            requiredString(vertical, "endpoint_permission");

            JsonObject angular = row.getAsJsonObject("angular_profile");
            assertNotNull(angular, id);
            assertTrue(angular.get("rate").getAsDouble() > 0.0, id);
            assertTrue(angular.get("acceleration").getAsDouble() > 0.0, id);
            assertTrue(angular.get("jerk").getAsDouble() > 0.0, id);
            requiredString(angular, "unit");

            JsonObject code = row.getAsJsonObject("code");
            assertNotNull(code, id);
            for (String field : Set.of("common", "forge", "renderer", "animation")) {
                requiredString(code, field);
            }
            assertTrue(code.getAsJsonArray("motion_writers").size() > 0, id);
            assertTrue(row.getAsJsonArray("actions").size() > 0, id);
            for (JsonElement action : row.getAsJsonArray("actions")) {
                assertTrue(catalog.has(action.getAsString()), id + " unknown action " + action.getAsString());
            }
            requiredString(row, "art_status");
            assertTrue(row.getAsJsonArray("fixtures").size() > 0, id);
        }
        assertEquals(SPECIES, ids);
    }

    private static String requiredString(JsonObject object, String field) {
        assertTrue(object.has(field), "missing " + field);
        String value = object.get(field).getAsString();
        assertFalse(value.isBlank(), "blank " + field);
        return value;
    }

    private static JsonObject readManifest() throws IOException {
        Path path = ROOT.resolve("docs/verification/phase-002-task-010-profile-manifest.json");
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static Path findProjectRoot() {
        Path candidate = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        for (int depth = 0; depth < 8 && candidate != null; depth++, candidate = candidate.getParent()) {
            if (Files.isDirectory(candidate.resolve("common/src/generated/resources"))) return candidate;
        }
        throw new IllegalStateException("could not locate the project root");
    }
}
