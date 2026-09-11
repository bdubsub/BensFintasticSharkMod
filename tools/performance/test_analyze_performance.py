import json
from pathlib import Path
import tempfile
import unittest

from analyze_performance import SPECIES, compare, java_map, load_run, percentile


class PerformanceEvidenceTest(unittest.TestCase):
    def test_nearest_rank_uses_individual_values(self):
        self.assertEqual(percentile(list(range(1, 101)), 0.95), 95)
        self.assertEqual(percentile([1], 0.99), 1)

    def test_empty_capture_is_rejected(self):
        with self.assertRaises(ValueError):
            percentile([], 0.95)

    def test_forge_class_names_match_population_targets(self):
        self.assertEqual(java_map("{AtlanticCodEntityForge=8, SandtigerSharkEntityForge=1}"),
                         {"AtlanticCodEntity": 8, "SandtigerSharkEntity": 1})

    def test_empty_counter_map_is_supported(self):
        self.assertEqual(java_map("{}"), {})

    def test_unmatched_artifact_environment_is_rejected(self):
        with self.assertRaises(ValueError):
            compare({"ticks": 36000}, {"ticks": 100})

    def test_performance_budget_is_not_relaxed(self):
        common = {"name": "baseline", "ticks": 36000, "warmup_ticks": 2400, "seed": 240024,
                  "scale": 1, "geckolib_sha256": "g", "smartbrainlib_sha256": "s",
                  "probe_agent_sha256": "a", "probe_data_sha256": "b", "tick_ms": {"p95": 10}}
        slower = dict(common, name="candidate", tick_ms={"p95": 11.01})
        self.assertFalse(compare(common, slower)["p95_gate_passed"])
        boundary = dict(common, name="candidate", tick_ms={"p95": 11})
        self.assertTrue(compare(common, boundary)["p95_gate_passed"])
        faster = dict(common, name="candidate", tick_ms={"p95": 9})
        self.assertTrue(compare(common, faster)["p95_gate_passed"])


class CaptureValidationTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="bfs-performance-parser-")
        self.addCleanup(self.temporary.cleanup)
        self.suite = Path(self.temporary.name)
        self.name = "fixture"
        manifest = {"result": "completed", "exit_code": 0, "ticks": 200, "warmup": 40,
                    "scale": 1, "seed": 240024, "jar_sha256": "j", "geckolib_sha256": "g",
                    "smartbrainlib_sha256": "s", "probe_agent_sha256": "a", "probe_data_sha256": "b",
                    "console_sha256": "c", "species_targets": {k: v[1] for k, v in SPECIES.items()}}
        self.path("manifest.json").write_text(json.dumps(manifest))
        self.path("probe.complete").write_text("ticks=200\n")
        self.path("probe-ticks.csv").write_text("tick,duration_ns\n" + "".join(
            f"{tick},1000000\n" for tick in range(1, 201)))
        counts = "{" + ", ".join(f"{entry[0]}Forge={entry[1]}" for entry in SPECIES.values()) + "}"
        air = "{" + ", ".join(f"{entry[0]}Forge=300" for entry in SPECIES.values()) + "}"
        self.path("probe-census.tsv").write_text(
            "tick\theap_used_bytes\tspecies_counts\tminimum_air\tmaximum_brain_memories\tcounters\tmoving_entities\tposition_samples\n"
            + "".join(f"{tick}\t100000\t{counts}\t{air}\t11\t{{sensor_calls=10}}\t63\t63\n"
                      for tick in (-40, 0, 200)))

    def path(self, suffix):
        return self.suite / f"{self.name}-{suffix}"

    def tearDown(self):
        self.temporary.cleanup()
        self.assertFalse(self.suite.exists())

    def test_complete_pilot_requires_explicit_short_duration(self):
        with self.assertRaises(ValueError):
            load_run(self.suite, self.name)
        self.assertEqual(load_run(self.suite, self.name, expected_ticks=200)["ticks"], 200)

    def test_missing_tick_is_rejected(self):
        self.path("probe-ticks.csv").write_text(
            self.path("probe-ticks.csv").read_text().replace("99,1000000\n", ""))
        with self.assertRaises(ValueError):
            load_run(self.suite, self.name, expected_ticks=200)

    def test_duplicate_tick_is_rejected(self):
        self.path("probe-ticks.csv").write_text(
            self.path("probe-ticks.csv").read_text().replace("99,1000000\n", "98,1000000\n"))
        with self.assertRaises(ValueError):
            load_run(self.suite, self.name, expected_ticks=200)

    def test_missing_census_is_rejected(self):
        self.path("probe-census.tsv").write_text("\n".join(
            self.path("probe-census.tsv").read_text().splitlines()[:-1]) + "\n")
        with self.assertRaises(ValueError):
            load_run(self.suite, self.name, expected_ticks=200)

    def test_wrong_species_is_rejected(self):
        self.path("probe-census.tsv").write_text(
            self.path("probe-census.tsv").read_text().replace("AtlanticCodEntity", "UnknownEntity"))
        with self.assertRaises(ValueError):
            load_run(self.suite, self.name, expected_ticks=200)

    def test_failed_shutdown_is_rejected(self):
        manifest = json.loads(self.path("manifest.json").read_text())
        manifest["exit_code"] = 143
        self.path("manifest.json").write_text(json.dumps(manifest))
        with self.assertRaises(ValueError):
            load_run(self.suite, self.name, expected_ticks=200)

    def test_warmup_boundary_and_measurement_start_are_distinct_rows(self):
        manifest = json.loads(self.path("manifest.json").read_text())
        manifest["warmup"] = 200
        self.path("manifest.json").write_text(json.dumps(manifest))
        rows = self.path("probe-census.tsv").read_text().splitlines()
        rows[1] = rows[1].replace("-40\t", "-200\t", 1)
        rows.insert(2, rows[2])
        self.path("probe-census.tsv").write_text("\n".join(rows) + "\n")
        report = load_run(self.suite, self.name, expected_ticks=200)
        self.assertEqual(report["warmup_census_rows"], 2)
        self.assertEqual(report["measurement_census_rows"], 2)

    def test_extra_measurement_start_row_is_rejected(self):
        rows = self.path("probe-census.tsv").read_text().splitlines()
        rows.insert(2, rows[2])
        self.path("probe-census.tsv").write_text("\n".join(rows) + "\n")
        with self.assertRaises(ValueError):
            load_run(self.suite, self.name, expected_ticks=200)


if __name__ == "__main__":
    unittest.main()
