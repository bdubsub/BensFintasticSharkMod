import csv
import gzip
import hashlib
import json
from pathlib import Path
import tempfile
import unittest

from analyze_performance import SPECIES, compare, irreversible_p95_failure, java_map, load_run, percentile


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

    def test_early_rejection_requires_more_than_full_window_allowance(self):
        self.assertFalse(irreversible_p95_failure([111] * 1800, 100)["irreversible_failure"])
        self.assertTrue(irreversible_p95_failure([111] * 1801, 100)["irreversible_failure"])
        self.assertFalse(irreversible_p95_failure([110] * 1801, 100)["irreversible_failure"])

    def test_early_rejection_rejects_invalid_duration(self):
        for sample in ([], [0], [-1], [1] * 36001):
            with self.assertRaises(ValueError):
                irreversible_p95_failure(sample, 100)

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

    def test_population_above_prescription_is_rejected(self):
        path = self.path("probe-census.tsv")
        path.write_text(path.read_text().replace("AtlanticCodEntityForge=8", "AtlanticCodEntityForge=9"))
        with self.assertRaises(ValueError):
            load_run(self.suite, self.name, expected_ticks=200)

    def test_changed_population_prescription_is_rejected(self):
        path = self.path("manifest.json")
        manifest = json.loads(path.read_text())
        manifest["species_targets"]["atlantic_cod"] = 7
        path.write_text(json.dumps(manifest))
        with self.assertRaises(ValueError):
            load_run(self.suite, self.name, expected_ticks=200)

    def test_heap_window_excludes_initial_boundary_sample(self):
        path = self.path("probe-census.tsv")
        rows = path.read_text().splitlines()
        rows[2] = rows[2].replace("100000", "1", 1)
        path.write_text("\n".join(rows) + "\n")
        report = load_run(self.suite, self.name, expected_ticks=200)
        self.assertEqual(report["heap_bytes"]["minimum"], 1)
        self.assertEqual(report["heap_bytes"]["five_minute_window_minima"], [100000])


class RetainedEvidenceTest(unittest.TestCase):
    bundle = Path(__file__).resolve().parents[2] / "docs/verification/artifacts/performance-rc1"

    def test_compressed_captures_match_recorded_raw_hashes(self):
        for name, summary in (("baseline-normal", "baseline-normal.json"),
                              ("candidate-normal", "candidate-normal-failed.json")):
            record = json.loads((self.bundle / summary).read_text())
            for suffix, key in (("probe-ticks.csv", "ticks"), ("probe-census.tsv", "census")):
                data = gzip.decompress((self.bundle / f"{name}-{suffix}.gz").read_bytes())
                self.assertEqual(hashlib.sha256(data).hexdigest(), record["raw_sha256"][key])

    def test_retained_timing_data_reproduces_the_rejection(self):
        with gzip.open(self.bundle / "baseline-normal-probe-ticks.csv.gz", "rt") as stream:
            baseline = list(csv.DictReader(stream))
        self.assertEqual([int(row["tick"]) for row in baseline], list(range(1, 36001)))
        baseline_p95 = percentile([int(row["duration_ns"]) for row in baseline], 0.95)
        self.assertEqual(baseline_p95, 6944770)
        with gzip.open(self.bundle / "candidate-normal-probe-ticks.csv.gz", "rt") as stream:
            candidate = list(csv.DictReader(stream))
        self.assertEqual([int(row["tick"]) for row in candidate], list(range(1, 22401)))
        verdict = irreversible_p95_failure([int(row["duration_ns"]) for row in candidate], baseline_p95)
        record = json.loads((self.bundle / "candidate-normal-failed.json").read_text())
        self.assertEqual(verdict, record["rejection"])


if __name__ == "__main__":
    unittest.main()
