#!/usr/bin/env python3
"""Focused parser tests for tools/bfs_debug_analyze.py."""

from __future__ import annotations

import json
import math
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

import bfs_debug_analyze


def record(event: str, tick: int, **values: object) -> dict[str, object]:
    return {
        "schema": "bfs-debug-v2",
        "sessionId": "session",
        "sequence": tick,
        "side": "server",
        "event": event,
        "tick": tick,
        "dimension": "minecraft:overworld",
        "timestamp": "2026-09-05T00:00:00Z",
        **values,
    }


class BfsDebugAnalyzerTest(unittest.TestCase):
    def test_complete_capture_preserves_history(self) -> None:
        records = [
            record("header", 1),
            record("movement", 2, entityUuid="fish", x=0.0, y=1.0, z=0.0,
                   velocityX=0.0, velocityY=0.01, velocityZ=0.1, yaw=0.0, pitch=-1.0),
            record("movement", 3, entityUuid="fish", x=0.0, y=1.01, z=0.1,
                   velocityX=0.0, velocityY=0.01, velocityZ=0.1, yaw=0.0, pitch=-2.0),
            record("end", 4, incomplete=False, recordsDropped=0),
        ]
        analysis = bfs_debug_analyze.validate(records, [], {"profile": "candidate"})
        self.assertEqual("complete", analysis["verdict"])
        history = analysis["metrics"]["entities"]["fish"]["history"]
        self.assertEqual(2, len(history))
        self.assertEqual(1.0, analysis["metrics"]["entities"]["fish"]["maxPitchStepDegrees"])

    def test_missing_end_is_invalid(self) -> None:
        analysis = bfs_debug_analyze.validate([record("header", 1)], [], {})
        self.assertEqual("invalid", analysis["verdict"])
        self.assertIn("capture must contain exactly one end record", analysis["errors"])

    def test_incomplete_capture_is_not_complete(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1),
            record("end", 2, incomplete=True, incompleteReason="writer queue reached its capacity", recordsDropped=4),
        ], [], {})
        self.assertEqual("incomplete", analysis["verdict"])
        self.assertTrue(analysis["warnings"])

    def test_non_finite_and_tick_reversal_are_invalid(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 4),
            record("movement", 3, entityUuid="fish", x=float("nan"), y=1.0, z=1.0,
                   velocityX=0.0, velocityY=0.0, velocityZ=0.0, yaw=0.0, pitch=0.0),
            record("end", 5, incomplete=False, recordsDropped=0),
        ], [], {})
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("moves backward" in error for error in analysis["errors"]))
        self.assertTrue(any("non-finite x" in error for error in analysis["errors"]))

    def test_manifest_rejects_static_and_discontinuous_motion(self) -> None:
        static = [
            record("header", 1, artifactSha256="candidate"),
            record("movement", 2, entityUuid="fish", x=0.0, y=1.0, z=0.0,
                   velocityX=0.0, velocityY=0.0, velocityZ=0.0, yaw=0.0, pitch=0.0),
            record("movement", 3, entityUuid="fish", x=5.0, y=1.0, z=0.0,
                   velocityX=0.0, velocityY=0.0, velocityZ=0.0, yaw=0.0, pitch=0.0),
            record("end", 4, incomplete=False, recordsDropped=0),
        ]
        manifest = {
            "artifactSha256": "candidate",
            "entities": {"fish": {"minimumSamples": 3, "minimumNetVerticalDisplacement": 0.25,
                                  "minimumMovingSampleTransitions": 2, "maximumCoordinateStep": 1.0}},
        }
        analysis = bfs_debug_analyze.validate(static, [], manifest)
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("net vertical" in error for error in analysis["errors"]))
        self.assertTrue(any("moving sample transitions" in error for error in analysis["errors"]))
        self.assertTrue(any("continuity" in error for error in analysis["errors"]))

    def test_manifest_rejects_missing_required_movement_field(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1, artifactSha256="candidate"),
            record("movement", 2, entityUuid="fish", x=0.0, y=1.0, z=0.0,
                   velocityX=0.0, velocityY=0.01, velocityZ=0.1, yaw=0.0, pitch=0.0,
                   routeAttemptId="unavailable:future_phase"),
            record("end", 3, incomplete=False, recordsDropped=0),
        ], [], {
            "artifactSha256": "candidate",
            "requiredMovementFields": ["routeAttemptId"],
            "entities": {"fish": {"minimumSamples": 1}},
        })
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("unavailable required field routeAttemptId" in error for error in analysis["errors"]))

    def test_manifest_rejects_wrong_artifact(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1, artifactSha256="wrong"),
            record("end", 2, incomplete=False, recordsDropped=0),
        ], [], {"artifactSha256": "candidate"})
        self.assertEqual("invalid", analysis["verdict"])
        self.assertIn("header artifactSha256 does not match the candidate manifest", analysis["errors"])

    def test_cli_binding_requires_matching_manifest_identity(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1, scenarioId="unavailable:provided_by_candidate_manifest",
                   requirementId="unavailable:provided_by_candidate_manifest"),
            record("end", 2, incomplete=False, recordsDropped=0),
        ], [], {"scenarioId": "cod_depth_baseline", "requirementId": "BFS-REQ-027"},
            "tiger_depth_baseline", "BFS-REQ-027")
        self.assertEqual("invalid", analysis["verdict"])
        self.assertIn("candidate manifest scenarioId does not match --scenario", analysis["errors"])

    def test_cli_binding_rejects_unbound_manifest(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1),
            record("end", 2, incomplete=False, recordsDropped=0),
        ], [], {}, "cod_depth_baseline", "BFS-REQ-027")
        self.assertEqual("invalid", analysis["verdict"])
        self.assertIn("candidate manifest scenarioId is missing", analysis["errors"])
        self.assertIn("candidate manifest requirementId is missing", analysis["errors"])

    def test_bad_json_is_invalid(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "capture.jsonl"
            path.write_text("not-json\n", encoding="utf-8")
            records, errors = bfs_debug_analyze.load_capture(path)
        analysis = bfs_debug_analyze.validate(records, errors, {})
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("not valid JSON" in error for error in analysis["errors"]))

    def test_arriving_helical_route_is_rejected_when_declared(self) -> None:
        records = [record("header", 1)]
        for tick, angle in enumerate((0.0, 1.8, 3.6, 5.4, 6.4), start=2):
            records.append(record("movement", tick, entityUuid="fish", x=10.0 * math.cos(angle),
                                  y=float(tick), z=10.0 * math.sin(angle), velocityX=0.0,
                                  velocityY=0.1, velocityZ=0.1, yaw=0.0, pitch=-5.0))
        records.append(record("end", 7, incomplete=False, recordsDropped=0))
        analysis = bfs_debug_analyze.validate(records, [], {
            "entities": {"fish": {"routeShape": {
                "target": {"x": 0.0, "z": 0.0}, "maximumHorizontalWindingTurns": 0.5
            }}}
        })
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("route winding" in error for error in analysis["errors"]))
        self.assertEqual("invalid", analysis["checks"]["routeVerdicts"]["fish"]["verdict"])

    def test_complete_capture_reports_coverage_extrema_and_route_verdict(self) -> None:
        records = [
            record("header", 1, artifactSha256="candidate"),
            record("movement", 2, entityUuid="fish", x=1.0, y=1.0, z=0.0,
                   velocityX=0.1, velocityY=0.02, velocityZ=0.0, yaw=0.0, pitch=-1.0),
            record("movement", 3, entityUuid="fish", x=0.5, y=1.02, z=0.0,
                   velocityX=0.1, velocityY=0.02, velocityZ=0.0, yaw=0.0, pitch=-2.0),
            record("end", 4, incomplete=False, recordsDropped=0),
        ]
        analysis = bfs_debug_analyze.validate(records, [], {
            "artifactSha256": "candidate",
            "entities": {"fish": {"minimumSamples": 2, "minimumMovingSampleTransitions": 1,
                                  "routeShape": {"target": {"x": 0.0, "z": 0.0},
                                                 "maximumHorizontalWindingTurns": 0.5}}},
        })
        self.assertEqual("complete", analysis["verdict"])
        self.assertEqual(1, analysis["coverage"]["movementEntityCount"])
        self.assertEqual(2, analysis["coverage"]["movementSampleCount"])
        self.assertGreater(analysis["metrics"]["entities"]["fish"]["maxHorizontalSpeed"], 0.0)
        self.assertEqual("pass", analysis["checks"]["routeVerdicts"]["fish"]["verdict"])

    def test_summary_reports_coverage_extrema_and_route_verdict(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1),
            record("movement", 2, entityUuid="fish", x=1.0, y=1.0, z=0.0,
                   velocityX=0.1, velocityY=0.0, velocityZ=0.0, yaw=0.0, pitch=0.0),
            record("movement", 3, entityUuid="fish", x=0.5, y=1.0, z=0.0,
                   velocityX=0.1, velocityY=0.0, velocityZ=0.0, yaw=0.0, pitch=0.0),
            record("end", 4, incomplete=False, recordsDropped=0),
        ], [], {"entities": {"fish": {"routeShape": {"target": {"x": 0.0, "z": 0.0},
                                                          "maximumHorizontalWindingTurns": 0.5}}}})
        with tempfile.TemporaryDirectory() as directory:
            output = Path(directory) / "analysis"
            bfs_debug_analyze.write_result(output, analysis, "fish_route", "BFS-REQ-027")
            summary = (output / "summary.md").read_text(encoding="utf-8")
        self.assertIn("## Coverage", summary)
        self.assertIn("## Observed Extrema", summary)
        self.assertIn("## Route Verdicts", summary)


if __name__ == "__main__":
    unittest.main()
