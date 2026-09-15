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
    def test_render_boundary_and_reload_contract(self) -> None:
        base = {
            "render.variantId": "zippy",
            "render.baseResource": "bensfintasticsharks:textures/entity/common_thresher_shark/zippy.png",
            "render.maskResource": "bensfintasticsharks:textures/entity/common_thresher_shark/zippy_glowmask.png",
            "render.selected": True,
            "render.reason": "resources_resolved",
            "render.textureHash": "a" * 64,
            "render.maskHash": "b" * 64,
            "render.alphaBackgroundCheck": True,
        }
        records = [
            {**record("header", 1), "side": "client"},
            {**record("presentation", 2, **base, **{
                "render.rawBrightness": 7, "render.layer": "glow",
                "render.resourceReloadGeneration": 1,
            }), "side": "client"},
            {**record("presentation", 3, **base, **{
                "render.rawBrightness": 8, "render.layer": "marking",
                "render.resourceReloadGeneration": 2,
            }), "side": "client"},
            {**record("end", 4, incomplete=False, recordsDropped=0), "side": "client"},
        ]
        analysis = bfs_debug_analyze.validate(records, [], {"render": {"minimumSamples": 2}})
        self.assertEqual("complete", analysis["verdict"])
        self.assertEqual([1, 2], analysis["metrics"]["render"]["reloadGenerations"])

    def test_render_rejects_wrong_layer_and_missing_hash(self) -> None:
        row = {
            "render.variantId": "zippy", "render.baseResource": "base.png",
            "render.maskResource": "mask.png", "render.rawBrightness": 8,
            "render.layer": "glow", "render.selected": True,
            "render.reason": "resources_resolved", "render.resourceReloadGeneration": 0,
            "render.textureHash": None, "render.maskHash": "b" * 64,
            "render.alphaBackgroundCheck": True,
        }
        analysis = bfs_debug_analyze.validate([
            {**record("header", 1), "side": "client"},
            {**record("presentation", 2, **row), "side": "client"},
            {**record("end", 3, incomplete=False, recordsDropped=0), "side": "client"},
        ], [], {})
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("zippy layer must be marking" in error for error in analysis["errors"]))
        self.assertTrue(any("selected without a texture hash" in error for error in analysis["errors"]))

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

    def test_client_render_cadence_can_skip_server_ticks(self) -> None:
        records = [
            {**record("header", 1), "side": "client"},
            {**record("movement", 2, entityUuid="fish", x=0.0, y=1.0, z=0.0,
                      velocityX=0.0, velocityY=0.01, velocityZ=0.1, yaw=0.0, pitch=-1.0),
             "side": "client"},
            {**record("movement", 4, entityUuid="fish", x=0.0, y=1.02, z=0.2,
                      velocityX=0.0, velocityY=0.01, velocityZ=0.1, yaw=0.0, pitch=-2.0),
             "sequence": 3,
             "side": "client"},
            {**record("end", 5, incomplete=False, recordsDropped=0), "sequence": 4, "side": "client"},
        ]
        analysis = bfs_debug_analyze.validate(records, [], {
            "entities": {"fish": {"minimumSamples": 2}},
        })
        self.assertEqual("complete", analysis["verdict"])
        self.assertEqual(1, analysis["metrics"]["entities"]["fish"]["tickGaps"])

    def test_server_tick_gap_remains_invalid(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1),
            record("movement", 2, entityUuid="fish", x=0.0, y=1.0, z=0.0,
                   velocityX=0.0, velocityY=0.01, velocityZ=0.1, yaw=0.0, pitch=-1.0),
            record("movement", 4, entityUuid="fish", x=0.0, y=1.02, z=0.2,
                   velocityX=0.0, velocityY=0.01, velocityZ=0.1, yaw=0.0, pitch=-2.0),
            record("end", 5, incomplete=False, recordsDropped=0),
        ], [], {"entities": {"fish": {"minimumSamples": 2}}})
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("missing required movement tick" in error for error in analysis["errors"]))

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

    def test_manifest_allows_first_sample_derivative_without_history(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1),
            record("movement", 2, entityUuid="fish", x=0.0, y=1.0, z=0.0,
                   velocityX=0.0, velocityY=0.01, velocityZ=0.1, yaw=0.0, pitch=-1.0,
                   positionDeltaX="unavailable:no_previous_sample",
                   positionDeltaY="unavailable:no_previous_sample",
                   positionDeltaZ="unavailable:no_previous_sample"),
            record("movement", 3, entityUuid="fish", x=0.0, y=1.01, z=0.1,
                   velocityX=0.0, velocityY=0.01, velocityZ=0.1, yaw=0.0, pitch=-2.0,
                   positionDeltaX=0.0, positionDeltaY=0.01, positionDeltaZ=0.1),
            record("end", 4, incomplete=False, recordsDropped=0),
        ], [], {
            "requiredMovementFields": ["positionDeltaX", "positionDeltaY", "positionDeltaZ"],
            "entities": {"fish": {"minimumSamples": 2}},
        })
        self.assertEqual("complete", analysis["verdict"])

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

    def test_truncated_jsonl_is_invalid(self) -> None:
        with tempfile.TemporaryDirectory() as directory:
            path = Path(directory) / "capture.jsonl"
            path.write_text(json.dumps(record("header", 1)) + "\n{\"event\":\n", encoding="utf-8")
            records, errors = bfs_debug_analyze.load_capture(path)
        analysis = bfs_debug_analyze.validate(records, errors, {})
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("not valid JSON" in error for error in analysis["errors"]))
        self.assertIn("capture must contain exactly one end record", analysis["errors"])

    def test_algae_events_validate_required_fields_and_caps(self) -> None:
        common = {"x": 1, "y": 2, "z": 3, "reason": "accepted"}
        records = [
            record("header", 1),
            record("algae_place", 2, **common, afterState="bensfintasticsharks:algae_block"),
            record("algae_support", 3, **common, beforeState="small", afterState="water",
                   supportDirection="north", waterAfter=False),
            record("algae_migrate", 4, **common, beforeState="legacy", afterState="single"),
            record("algae_grow", 5, **common, beforeState="single", afterState="top",
                   heightBefore=1, heightAfter=2, ageBefore=24, ageAfter=25, sourceWater=True),
            record("algae_remove", 6, **common, beforeState="top", itemCount=1, waterAfter=True),
            record("algae_generate", 7, **common, attempt=0, candidateAttempts=16,
                   placedCells=8, sourceWater=True, surfaceVisible=True),
            record("end", 8, incomplete=False, recordsDropped=0),
        ]
        analysis = bfs_debug_analyze.validate(records, [], {
            "algae": {"requiredEvents": sorted(bfs_debug_analyze.ALGAE_EVENTS), "minimumRecords": 6},
        })
        self.assertEqual("complete", analysis["verdict"])
        self.assertEqual(1, analysis["metrics"]["algae"]["eventCounts"]["algae_generate"])

    def test_algae_events_reject_overflow(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1),
            record("algae_generate", 2, x=1, y=2, z=3, reason="rejected",
                   attempt=0, candidateAttempts=17, placedCells=9,
                   sourceWater=False, surfaceVisible=False),
            record("end", 3, incomplete=False, recordsDropped=0),
        ], [], {"algae": {"requiredEvents": ["algae_generate"]}})
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("sixteen candidate" in error for error in analysis["errors"]))
        self.assertTrue(any("eight cell" in error for error in analysis["errors"]))

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

    def test_manifest_limit_allows_float_rounding_at_declared_pitch_step(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1, artifactSha256="candidate"),
            record("movement", 2, entityUuid="fish", x=0.0, y=1.0, z=0.0,
                   velocityX=0.0, velocityY=0.0, velocityZ=0.0, yaw=0.0, pitch=0.0),
            record("movement", 3, entityUuid="fish", x=0.0, y=1.0, z=0.0,
                   velocityX=0.0, velocityY=0.0, velocityZ=0.0, yaw=0.0,
                   pitch=0.3000004),
            record("end", 4, incomplete=False, recordsDropped=0),
        ], [], {
            "artifactSha256": "candidate",
            "entities": {"fish": {"maximumPitchStepDegrees": 0.3}},
        })
        self.assertEqual("complete", analysis["verdict"])

    def test_follow_events_require_redacted_identity_and_restore(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1),
            record("follow.claim", 2, owner="player_owner", target="entity_target",
                   targetType="minecraft:zombie", reason="claimed", adapter="navigation",
                   leaseAge=0, blockedTicks=0, distance=5.0),
            record("follow.restore", 3, owner="player_owner", target="entity_target",
                   targetType="minecraft:zombie", reason="ordinary_controller_resume", adapter="navigation",
                   leaseAge=1, blockedTicks=0, distance=5.0),
            record("end", 4, incomplete=False, recordsDropped=0),
        ], [], {"follow": {"minimumClaims": 1, "requireRestore": True}})
        self.assertEqual("complete", analysis["verdict"])
        self.assertEqual(2, analysis["metrics"]["follow"]["recordCount"])

    def test_follow_group_states_validate_counts_and_revisions(self) -> None:
        state = record("follow.state", 2, owner="player_owner", target="entity_target",
                       targetType="minecraft:cow", reason="nearby", adapter="mob_navigation",
                       leaseAge=2401, blockedTicks=0, distance=3.0, followVersion=2,
                       selectedCount=33, groupRevision=34, state="waiting")
        rows = [record("header", 1), state, record("end", 3, incomplete=False, recordsDropped=0)]
        self.assertEqual("complete", bfs_debug_analyze.validate(rows, [], {})["verdict"])
        for field, value in (("selectedCount", -1), ("groupRevision", "34"), ("state", "unknown")):
            invalid = dict(state, **{field: value})
            result = bfs_debug_analyze.validate([rows[0], invalid, rows[2]], [], {})
            self.assertEqual("invalid", result["verdict"])
            self.assertTrue(any(field in error for error in result["errors"]))

    def test_follow_events_reject_raw_uuid_and_missing_fields(self) -> None:
        analysis = bfs_debug_analyze.validate([
            record("header", 1),
            record("follow.claim", 2, owner="550e8400-e29b-41d4-a716-446655440000",
                   target="entity_target", targetType="minecraft:zombie", reason="claimed",
                   adapter="navigation", leaseAge=0, blockedTicks=0, distance=5.0),
            record("end", 3, incomplete=False, recordsDropped=0),
        ], [], {"follow": {"minimumClaims": 1, "requireRestore": True}})
        self.assertEqual("invalid", analysis["verdict"])
        self.assertTrue(any("raw owner uuid" in error for error in analysis["errors"]))
        self.assertTrue(any("restore event" in error for error in analysis["errors"]))

    def test_disturbance_and_boat_sources_validate_typed_identity(self) -> None:
        rows = [
            record("header", 1),
            record("disturbance.source", 2, sourceId="source_a", sourceType="minecraft:player",
                   sourceKind="water_entry", strength=0.5, positionX=1.5, positionY=2.5,
                   positionZ=3.5, boatId="unavailable", riderId="unavailable",
                   outcome="emitted", reason="producer"),
            record("boat.source", 3, sourceId="source_b", sourceType="minecraft:boat",
                   sourceKind="occupied_boat", strength=1.0, positionX=4.5, positionY=2.5,
                   positionZ=3.5, boatId="boat_c", riderId="rider_d",
                   outcome="alert", reason="eligible_sharks_1"),
            record("disturbance.decision", 4, sourceId="source_a", sourceType="minecraft:player",
                   sourceKind="water_entry", strength=0.5, positionX=1.5, positionY=2.5,
                   positionZ=3.5, boatId="unavailable", riderId="unavailable",
                   boatCorrelation=False, candidateCount=2, sourceKeyCount=1,
                   species="great_white_shark", settingsRevision=4, radius=24.0,
                   sensitivity=1.0, sourceStrength=0.5, sourceIntervalTicks=20,
                   alertTicks=100, effectiveStrength=0.25,
                   acceptedThreshold=True, outcome="alert", reason="eligible_sharks"),
            record("end", 5, incomplete=False, recordsDropped=0),
        ]
        analysis = bfs_debug_analyze.validate(rows, [], {
            "disturbance": {"minimumEvents": 1},
            "boat": {"minimumEvents": 1},
        })
        self.assertEqual("complete", analysis["verdict"])
        self.assertEqual(1, analysis["metrics"]["disturbance"]["recordCount"])
        self.assertEqual(1, analysis["metrics"]["boat"]["sourceKindCounts"]["occupied_boat"])
        self.assertEqual(1, analysis["metrics"]["disturbanceDecisions"]["recordCount"])

    def test_decision_and_throttle_records_require_bounded_fields(self) -> None:
        invalid = record("boat.throttle", 2, sourceId="source_a", sourceType="minecraft:boat",
                         sourceKind="occupied_boat", strength=0.5, positionX=0.0,
                         positionY=0.0, positionZ=0.0, boatId="boat_a", riderId="rider_a",
                         outcome="ignored", reason="throttle_duplicate", candidateCount=0,
                         sourceKeyCount=-1, throttleElapsedTicks=-2)
        result = bfs_debug_analyze.validate([
            record("header", 1), invalid, record("end", 3, incomplete=False, recordsDropped=0),
        ], [], {})
        self.assertEqual("invalid", result["verdict"])
        self.assertTrue(any("sourceKeyCount" in error for error in result["errors"]))
        self.assertTrue(any("throttleElapsedTicks" in error for error in result["errors"]))

    def test_boat_sources_reject_raw_identity_and_invalid_strength(self) -> None:
        invalid = record("boat.source", 2, sourceId="source_a", sourceType="minecraft:boat",
                         sourceKind="occupied_boat", strength=2.0, positionX=0.0,
                         positionY=0.0, positionZ=0.0,
                         boatId="550e8400-e29b-41d4-a716-446655440000", riderId="rider_d",
                         outcome="alert", reason="eligible_sharks_1")
        result = bfs_debug_analyze.validate([
            record("header", 1), invalid, record("end", 3, incomplete=False, recordsDropped=0),
        ], [], {"boat": {"minimumEvents": 1}})
        self.assertEqual("invalid", result["verdict"])
        self.assertTrue(any("invalid strength" in error for error in result["errors"]))
        self.assertTrue(any("raw boatId uuid" in error for error in result["errors"]))

    def test_dive_records_validate_schema_and_transition_fields(self) -> None:
        common = {
            "player": "player_abc", "oxygenSchema": 1, "revision": 2,
            "fullSuit": True, "submergedEyes": True, "waterContact": True, "eligible": True,
        }
        rows = [
            record("header", 1),
            record("dive_eligibility", 2, **common, entityType="minecraft:player",
                   movementMode="seabed", oxygenMode="protected", remainingTicks=5999,
                   onGround=False, jumping=False, velocityX=0.0, velocityY=-0.02, velocityZ=0.0,
                   positionX=1.0, positionY=2.0, positionZ=3.0, airSupply=300, tickCount=20),
            record("dive_oxygen", 3, **common, beforeTicks=5999, remainingTicks=5998,
                   deltaTicks=-1, consumed=True, refilled=False, transition="consumed", reason="none"),
            record("dive_travel", 4, **common, applied=True, reason="applied", jumpEdge=False,
                   inputX=0.0, inputY=0.0, inputZ=1.0, beforeVelocityX=0.0,
                   beforeVelocityY=-0.02, beforeVelocityZ=0.0, afterVelocityX=0.0,
                   afterVelocityY=-0.04, afterVelocityZ=0.1, movementMode="seabed"),
            record("dive_work", 5, **common, action="break_speed", block="minecraft:stone",
                   positionX=1, positionY=2, positionZ=3, beforeSpeed=0.2, afterSpeed=1.0,
                   result="observed", reason="removed_underwater_penalty"),
            record("end", 6, incomplete=False, recordsDropped=0),
        ]
        analysis = bfs_debug_analyze.validate(rows, [], {
            "dive": {"requiredEvents": sorted(bfs_debug_analyze.DIVE_EVENTS), "minimumRecords": 4},
        })
        self.assertEqual("complete", analysis["verdict"])
        self.assertEqual(4, analysis["metrics"]["dive"]["recordCount"])

    def test_dive_records_reject_raw_players_and_bad_reserve_delta(self) -> None:
        invalid = record("dive_oxygen", 2, player="550e8400-e29b-41d4-a716-446655440000",
                         oxygenSchema=1, revision=1, fullSuit=True, submergedEyes=True,
                         waterContact=True, eligible=True, beforeTicks=4, remainingTicks=2,
                         deltaTicks=0, consumed=True, refilled=False,
                         transition="consumed", reason="none")
        result = bfs_debug_analyze.validate([
            record("header", 1), invalid, record("end", 3, incomplete=False, recordsDropped=0),
        ], [], {"dive": {"minimumRecords": 1}})
        self.assertEqual("invalid", result["verdict"])
        self.assertTrue(any("pseudonymous player" in error for error in result["errors"]))
        self.assertTrue(any("deltaTicks" in error for error in result["errors"]))


if __name__ == "__main__":
    unittest.main()
