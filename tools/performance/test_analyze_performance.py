import unittest

from analyze_performance import compare, java_map, percentile


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


if __name__ == "__main__":
    unittest.main()
