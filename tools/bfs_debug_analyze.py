#!/usr/bin/env python3
"""Validate a bounded BFS debug capture without modifying the raw JSONL input."""

from __future__ import annotations

import argparse
import json
import math
import re
import sys
from collections import defaultdict
from pathlib import Path
from typing import Any


SCHEMA = "bfs-debug-v2"
COMPARISON_EPSILON = 1.0e-6
FIRST_SAMPLE_DERIVED_FIELDS = {
    "positionDelta",
    "positionDeltaX",
    "positionDeltaY",
    "positionDeltaZ",
    "horizontalBlocksPerTick",
    "signedVerticalBlocksPerTick",
    "totalBlocksPerTick",
    "nominalBlocksPerSecond",
    "elapsedBlocksPerSecond",
    "yawDeltaDegrees",
    "pitchDeltaDegrees",
}
FOLLOW_EVENTS = {
    "follow.claim",
    "follow.adapter",
    "follow.intent",
    "follow.progress",
    "follow.block",
    "follow.release",
    "follow.restore",
    "follow.reject",
    "follow.state",
}
SOURCE_KINDS = {
    "swim_sprint", "attack", "damage", "block_break", "fall", "projectile",
    "water_entry", "water_jump", "occupied_boat",
}
UUID_PATTERN = re.compile(r"(?i)\b[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}\b")
HASH_PATTERN = re.compile(r"(?i)\A[0-9a-f]{64}\Z")
RENDER_LAYERS = {"base", "marking", "glow"}
ALGAE_EVENTS = {
    "algae_place",
    "algae_support",
    "algae_migrate",
    "algae_grow",
    "algae_remove",
    "algae_generate",
}


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("capture", type=Path, help="server or client JSONL capture")
    parser.add_argument("--scenario", required=True, help="planned scenario identifier")
    parser.add_argument("--requirement", required=True, help="planned requirement identifier")
    parser.add_argument("--candidate-manifest", required=True, type=Path,
                        help="immutable candidate configuration or profile manifest")
    parser.add_argument("--output", required=True, type=Path,
                        help="new or empty analysis output directory")
    return parser.parse_args()


def load_json(path: Path) -> dict[str, Any]:
    try:
        value = json.loads(path.read_text(encoding="utf-8"))
    except OSError as error:
        raise ValueError(f"candidate manifest cannot be read: {error}") from error
    except json.JSONDecodeError as error:
        raise ValueError(f"candidate manifest is not valid JSON: {error}") from error
    if not isinstance(value, dict):
        raise ValueError("candidate manifest must be a JSON object")
    return value


def load_capture(path: Path) -> tuple[list[dict[str, Any]], list[str]]:
    records: list[dict[str, Any]] = []
    errors: list[str] = []
    try:
        lines = path.read_text(encoding="utf-8").splitlines()
    except OSError as error:
        return records, [f"capture cannot be read: {error}"]
    if not lines:
        return records, ["capture is empty"]
    for line_number, line in enumerate(lines, start=1):
        if not line.strip():
            errors.append(f"line {line_number} is empty")
            continue
        try:
            record = json.loads(line)
        except json.JSONDecodeError as error:
            errors.append(f"line {line_number} is not valid JSON: {error.msg}")
            continue
        if not isinstance(record, dict):
            errors.append(f"line {line_number} is not a JSON object")
            continue
        records.append(record)
    return records, errors


def validate(records: list[dict[str, Any]], parse_errors: list[str], manifest: dict[str, Any],
             scenario: str | None = None, requirement: str | None = None) -> dict[str, Any]:
    errors = list(parse_errors)
    warnings: list[str] = []
    if not records:
        return result("invalid", errors or ["capture has no valid records"], warnings, {}, [])

    header = records[0]
    if header.get("event") != "header":
        errors.append("first record must be the header")
    if header.get("schema") != SCHEMA:
        errors.append(f"header schema must be {SCHEMA}")
    session_id = header.get("sessionId")
    if not isinstance(session_id, str) or not session_id:
        errors.append("header sessionId is missing")
    if header.get("side") not in {"server", "client"}:
        errors.append("header side must be server or client")

    previous_ticks: dict[str, int] = {}
    previous_sequences: dict[str, int] = {}
    movement_history: dict[str, list[dict[str, Any]]] = defaultdict(list)
    events: defaultdict[str, int] = defaultdict(int)
    end_records: list[dict[str, Any]] = []
    for index, record in enumerate(records, start=1):
        if record.get("schema") != SCHEMA:
            errors.append(f"record {index} has an incompatible schema")
        if session_id and record.get("sessionId") != session_id:
            errors.append(f"record {index} has a different sessionId")
        sequence = record.get("sequence")
        if not isinstance(sequence, int) or sequence < 1:
            errors.append(f"record {index} has no positive integer sequence")
        elif session_id:
            previous_sequence = previous_sequences.get(session_id)
            if previous_sequence is not None and sequence != previous_sequence + 1:
                errors.append(f"record {index} has a sequence gap: {sequence} after {previous_sequence}")
            previous_sequences[session_id] = sequence
        event = record.get("event")
        if not isinstance(event, str):
            errors.append(f"record {index} has no event")
            continue
        events[event] += 1
        tick = record.get("tick")
        if not isinstance(tick, int):
            errors.append(f"record {index} has no integer tick")
            continue
        dimension = record.get("dimension")
        if not isinstance(dimension, str):
            errors.append(f"record {index} has no dimension")
            continue
        previous = previous_ticks.get(dimension)
        if previous is not None and tick < previous:
            errors.append(f"record {index} moves backward in {dimension}: {tick} after {previous}")
        previous_ticks[dimension] = tick
        if event == "movement":
            entity_id = record.get("entityUuid")
            if not isinstance(entity_id, str):
                errors.append(f"movement record {index} has no entityUuid")
                continue
            validate_finite_coordinates(record, index, errors)
            movement_history[entity_id].append(record)
        if event == "end":
            end_records.append(record)

    if len(end_records) != 1:
        errors.append("capture must contain exactly one end record")
    else:
        end = end_records[0]
        if end.get("incomplete") is True:
            warnings.append(f"capture is incomplete: {end.get('incompleteReason', 'unspecified')}")
        if int_or_zero(end.get("recordsDropped")) > 0:
            warnings.append(f"capture dropped {end.get('recordsDropped')} records")

    validate_candidate_binding(header, manifest, scenario, requirement, errors)
    # Server captures are emitted once per authoritative game tick, so a gap
    # means a required server sample was lost. Client captures are emitted by
    # the client tick/render loop and may legitimately skip a server tick when
    # the render cadence falls behind. Both sides still require monotonic
    # capture ticks and contiguous record sequences.
    metrics = movement_metrics(movement_history, errors, strict_tick_continuity=header.get("side") == "server")
    checks = apply_manifest_checks(header, metrics, manifest, errors)
    if "fishing" in manifest:
        metrics["fishing"] = validate_fishing(records, manifest["fishing"], errors)
    follow_records = [row for row in records if isinstance(row.get("event"), str)
                      and row.get("event", "").startswith("follow.")]
    if follow_records or "follow" in manifest:
        metrics["follow"] = validate_follow(follow_records, manifest.get("follow", {}), errors)
    disturbance_records = [row for row in records if row.get("event") == "disturbance.source"]
    boat_records = [row for row in records if row.get("event") == "boat.source"]
    disturbance_decisions = [row for row in records if row.get("event") in {
        "disturbance.decision", "disturbance.throttle"}]
    boat_decisions = [row for row in records if row.get("event") in {
        "boat.decision", "boat.throttle"}]
    if disturbance_records or "disturbance" in manifest:
        metrics["disturbance"] = validate_source_events(
            disturbance_records, manifest.get("disturbance", {}), errors, "disturbance")
    if boat_records or "boat" in manifest:
        metrics["boat"] = validate_source_events(
            boat_records, manifest.get("boat", {}), errors, "boat")
    if disturbance_decisions:
        metrics["disturbanceDecisions"] = validate_decision_events(
            disturbance_decisions, errors, "disturbance")
    if boat_decisions:
        metrics["boatDecisions"] = validate_decision_events(
            boat_decisions, errors, "boat")
    presentation_records = [row for row in records if row.get("event") == "presentation"]
    if presentation_records or "render" in manifest:
        metrics["render"] = validate_render(presentation_records, manifest.get("render", {}), errors)
    algae_records = [row for row in records if row.get("event") in ALGAE_EVENTS]
    if algae_records or "algae" in manifest:
        metrics["algae"] = validate_algae(algae_records, manifest.get("algae", {}), errors)
    verdict = "invalid" if errors else "incomplete" if warnings else "complete"
    coverage = {
        "movementEntityCount": len(movement_history),
        "movementSampleCount": sum(len(samples) for samples in movement_history.values()),
        "droppedRecords": int_or_zero(end_records[0].get("recordsDropped")) if len(end_records) == 1 else None,
        "hasTerminalRecord": len(end_records) == 1,
    }
    return result(verdict, errors, warnings, metrics, records, manifest, dict(events), checks, coverage)


def validate_algae(records: list[dict[str, Any]], contract: Any,
                   errors: list[str]) -> dict[str, Any]:
    """Validate additive server fields for bounded algae diagnostics."""
    if not isinstance(contract, dict):
        errors.append("algae manifest must be an object")
        contract = {}
    counts: defaultdict[str, int] = defaultdict(int)
    required_events = contract.get("requiredEvents", [])
    if not isinstance(required_events, list) or any(not isinstance(event, str) for event in required_events):
        errors.append("algae requiredEvents must be a list of strings")
        required_events = []
    for event in required_events:
        if event not in ALGAE_EVENTS:
            errors.append(f"algae manifest names unknown event {event}")
    for index, row in enumerate(records, start=1):
        event = row.get("event")
        counts[event] += 1
        prefix = f"algae record {index}"
        for field in ("reason", "dimension", "tick", "sequence"):
            if field not in row:
                errors.append(f"{prefix} is missing {field}")
        for field in ("x", "y", "z"):
            if not isinstance(row.get(field), int):
                errors.append(f"{prefix} has no integer {field}")
        if event in {"algae_place", "algae_support", "algae_migrate", "algae_grow", "algae_remove"}:
            if "beforeState" not in row and event != "algae_place":
                errors.append(f"{prefix} is missing beforeState")
            if event in {"algae_place", "algae_support", "algae_migrate", "algae_grow"} and "afterState" not in row:
                errors.append(f"{prefix} is missing afterState")
        if event == "algae_support":
            if not isinstance(row.get("supportDirection"), str):
                errors.append(f"{prefix} is missing supportDirection")
            if not isinstance(row.get("waterAfter"), bool):
                errors.append(f"{prefix} has invalid waterAfter")
        if event == "algae_grow":
            for field in ("heightBefore", "heightAfter"):
                value = row.get(field)
                if not isinstance(value, int) or not 0 <= value <= 8:
                    errors.append(f"{prefix} has invalid {field}")
            for field in ("ageBefore", "ageAfter"):
                value = row.get(field)
                if not isinstance(value, int) or not 0 <= value <= 25:
                    errors.append(f"{prefix} has invalid {field}")
            if isinstance(row.get("heightAfter"), int) and row["heightAfter"] > 8:
                errors.append(f"{prefix} exceeds the eight cell height cap")
            if row.get("sourceWater") is not True:
                errors.append(f"{prefix} must record sourceWater true")
        if event == "algae_remove":
            if not isinstance(row.get("itemCount"), int) or row["itemCount"] < 0:
                errors.append(f"{prefix} has invalid itemCount")
            if row.get("waterAfter") is not True:
                errors.append(f"{prefix} must record waterAfter true")
        if event == "algae_generate":
            for field in ("attempt", "candidateAttempts", "placedCells"):
                value = row.get(field)
                if not isinstance(value, int) or value < 0:
                    errors.append(f"{prefix} has invalid {field}")
            if isinstance(row.get("candidateAttempts"), int) and row["candidateAttempts"] > 16:
                errors.append(f"{prefix} exceeds the sixteen candidate attempt cap")
            if isinstance(row.get("placedCells"), int) and row["placedCells"] > 8:
                errors.append(f"{prefix} exceeds the eight cell placement cap")
            for field in ("sourceWater", "surfaceVisible"):
                if not isinstance(row.get(field), bool):
                    errors.append(f"{prefix} has invalid {field}")
    for event in required_events:
        if counts[event] == 0:
            errors.append(f"algae capture is missing required event {event}")
    minimum_records = contract.get("minimumRecords")
    if minimum_records is not None and (not isinstance(minimum_records, int) or len(records) < minimum_records):
        errors.append("algae capture has fewer records than required")
    return {"eventCounts": dict(counts), "recordCount": len(records),
            "requiredEvents": required_events}


def validate_render(records: list[dict[str, Any]], contract: Any, errors: list[str]) -> dict[str, Any]:
    """Validate the bounded client render observations used by the Zippy gate."""
    if not isinstance(contract, dict):
        errors.append("render manifest must be an object")
        contract = {}
    generations: list[int] = []
    selected = 0
    for index, row in enumerate(records, start=1):
        prefix = f"presentation record {index}"
        required = {
            "render.variantId", "render.baseResource", "render.maskResource",
            "render.rawBrightness", "render.layer", "render.selected", "render.reason",
            "render.resourceReloadGeneration", "render.textureHash", "render.maskHash",
            "render.alphaBackgroundCheck",
        }
        missing = sorted(field for field in required if field not in row)
        if missing:
            errors.append(f"{prefix} is missing render fields: {', '.join(missing)}")
            continue
        brightness = row["render.rawBrightness"]
        layer = row["render.layer"]
        variant = row["render.variantId"]
        if not isinstance(brightness, int) or not 0 <= brightness <= 15:
            errors.append(f"{prefix} has raw brightness outside 0 through 15")
        if layer not in RENDER_LAYERS:
            errors.append(f"{prefix} has an unknown render layer")
        if not isinstance(row["render.selected"], bool):
            errors.append(f"{prefix} render.selected must be boolean")
        generation = row["render.resourceReloadGeneration"]
        if not isinstance(generation, int) or generation < 0:
            errors.append(f"{prefix} has an invalid resource reload generation")
        else:
            if generations and generation < generations[-1]:
                errors.append(f"{prefix} moves resource reload generation backward")
            generations.append(generation)
        texture_hash = row["render.textureHash"]
        if texture_hash is not None and (not isinstance(texture_hash, str) or not HASH_PATTERN.fullmatch(texture_hash)):
            errors.append(f"{prefix} has an invalid texture hash")
        mask_hash = row["render.maskHash"]
        if mask_hash is not None and (not isinstance(mask_hash, str) or not HASH_PATTERN.fullmatch(mask_hash)):
            errors.append(f"{prefix} has an invalid mask hash")
        alpha = row["render.alphaBackgroundCheck"]
        if alpha is not None and not isinstance(alpha, bool):
            errors.append(f"{prefix} alpha background check must be boolean or null")
        if isinstance(variant, str) and variant == "zippy" and isinstance(brightness, int):
            expected = "marking" if brightness >= 8 else "glow"
            if layer != expected:
                errors.append(f"{prefix} zippy layer must be {expected} at raw brightness {brightness}")
        elif isinstance(variant, str) and variant != "zippy" and layer != "base":
            errors.append(f"{prefix} non zippy layer must be base")
        if row["render.selected"]:
            selected += 1
            if not isinstance(row["render.baseResource"], str) or row["render.baseResource"].startswith("unavailable:"):
                errors.append(f"{prefix} selected without a base resource")
            if texture_hash is None:
                errors.append(f"{prefix} selected without a texture hash")
            if alpha is False:
                errors.append(f"{prefix} selected with an invalid mask alpha background")
    minimum = contract.get("minimumSamples")
    if isinstance(minimum, int) and len(records) < minimum:
        errors.append(f"render capture has {len(records)} samples, expected at least {minimum}")
    return {"sampleCount": len(records), "selectedCount": selected,
            "reloadGenerations": sorted(set(generations))}


def validate_follow(records: list[dict[str, Any]], contract: Any, errors: list[str]) -> dict[str, Any]:
    """Validate bounded follow events and reject raw server identity leakage."""
    if not isinstance(contract, dict):
        errors.append("follow manifest must be an object")
        contract = {}
    counts: defaultdict[str, int] = defaultdict(int)
    for index, row in enumerate(records, start=1):
        event = row.get("event")
        if event not in FOLLOW_EVENTS:
            errors.append(f"follow record {index} has an unknown event")
        else:
            counts[event] += 1
        for field, prefixes in (("owner", ("player_", "owner_unavailable")),
                                ("target", ("entity_", "entity_unavailable"))):
            value = row.get(field)
            if isinstance(value, str) and UUID_PATTERN.search(value):
                errors.append(f"follow record {index} leaks a raw {field} uuid")
            elif not isinstance(value, str) or not value.startswith(prefixes):
                errors.append(f"follow record {index} has no pseudonymous {field}")
        target_type = row.get("targetType")
        if not isinstance(target_type, str) or not target_type:
            errors.append(f"follow record {index} has no targetType")
        for field in ("reason", "adapter"):
            if not isinstance(row.get(field), str) or not row[field]:
                errors.append(f"follow record {index} has no {field}")
        for field in ("leaseAge", "blockedTicks"):
            value = row.get(field)
            if type(value) is not int or value < 0:
                errors.append(f"follow record {index} has no nonnegative {field}")
        if row.get("followVersion") == 2:
            for field in ("selectedCount", "groupRevision"):
                if type(row.get(field)) is not int or row[field] < 0:
                    errors.append(f"follow record {index} has invalid {field}")
            if row.get("state") not in {"following", "waiting", "paused"}:
                errors.append(f"follow record {index} has invalid state")
        distance = row.get("distance")
        if not isinstance(distance, (int, float)) or not math.isfinite(distance) or distance < 0.0:
            errors.append(f"follow record {index} has no finite nonnegative distance")
    minimum_claims = contract.get("minimumClaims", 0)
    if type(minimum_claims) is not int or minimum_claims < 0:
        errors.append("follow minimumClaims must be a nonnegative integer")
    elif counts["follow.claim"] < minimum_claims:
        errors.append(f"follow observed {counts['follow.claim']} claims, requires {minimum_claims}")
    if contract.get("requireRestore") is True and counts["follow.restore"] == 0:
        errors.append("follow capture has no restore event")
    return {"eventCounts": dict(counts), "recordCount": len(records)}


def validate_source_events(records: list[dict[str, Any]], contract: Any,
                           errors: list[str], kind: str) -> dict[str, Any]:
    """Validate typed water or boat producers and their bounded pseudonymous fields."""
    if not isinstance(contract, dict):
        errors.append(f"{kind} manifest must be an object")
        contract = {}
    counts: defaultdict[str, int] = defaultdict(int)
    for index, row in enumerate(records, start=1):
        source_kind = row.get("sourceKind")
        if source_kind not in SOURCE_KINDS:
            errors.append(f"{kind} record {index} has an unknown sourceKind")
        else:
            counts[source_kind] += 1
        source_id = row.get("sourceId")
        if not isinstance(source_id, str) or not source_id.startswith(("source_", "source_unavailable")):
            errors.append(f"{kind} record {index} has no pseudonymous sourceId")
        if isinstance(source_id, str) and UUID_PATTERN.search(source_id):
            errors.append(f"{kind} record {index} leaks a raw source uuid")
        if not isinstance(row.get("sourceType"), str) or not row["sourceType"]:
            errors.append(f"{kind} record {index} has no sourceType")
        strength = row.get("strength")
        if not isinstance(strength, (int, float)) or not math.isfinite(strength) or not 0.0 <= strength <= 1.0:
            errors.append(f"{kind} record {index} has invalid strength")
        for field in ("positionX", "positionY", "positionZ"):
            value = row.get(field)
            if not isinstance(value, (int, float)) or not math.isfinite(value):
                errors.append(f"{kind} record {index} has invalid {field}")
        for field in ("outcome", "reason"):
            if not isinstance(row.get(field), str) or not row[field]:
                errors.append(f"{kind} record {index} has no {field}")
        for field in ("boatId", "riderId"):
            value = row.get(field)
            if not isinstance(value, str) or not value:
                errors.append(f"{kind} record {index} has no {field}")
            if isinstance(value, str) and UUID_PATTERN.search(value):
                errors.append(f"{kind} record {index} leaks a raw {field} uuid")
        if kind == "boat" and source_kind != "occupied_boat":
            errors.append(f"boat record {index} must use occupied_boat")
    minimum = contract.get("minimumEvents", 0)
    if type(minimum) is not int or minimum < 0:
        errors.append(f"{kind} minimumEvents must be a nonnegative integer")
    elif len(records) < minimum:
        errors.append(f"{kind} observed {len(records)} events, requires {minimum}")
    return {"sourceKindCounts": dict(counts), "recordCount": len(records)}


def validate_decision_events(records: list[dict[str, Any]], errors: list[str], kind: str) -> dict[str, Any]:
    """Validate bounded source decisions and named throttle rejections."""
    counts: defaultdict[str, int] = defaultdict(int)
    for index, row in enumerate(records, start=1):
        source_kind = row.get("sourceKind")
        if source_kind not in SOURCE_KINDS:
            errors.append(f"{kind} decision {index} has an unknown sourceKind")
        else:
            counts[row.get("event", "unknown")] += 1
        for field in ("sourceId", "sourceType", "boatId", "riderId", "outcome", "reason"):
            value = row.get(field)
            if not isinstance(value, str) or not value:
                errors.append(f"{kind} decision {index} has no {field}")
            elif field.endswith("Id") and UUID_PATTERN.search(value):
                errors.append(f"{kind} decision {index} leaks a raw {field} uuid")
        strength = row.get("strength")
        if not isinstance(strength, (int, float)) or not math.isfinite(strength) or not 0.0 <= strength <= 1.0:
            errors.append(f"{kind} decision {index} has invalid strength")
        for field in ("positionX", "positionY", "positionZ"):
            value = row.get(field)
            if not isinstance(value, (int, float)) or not math.isfinite(value):
                errors.append(f"{kind} decision {index} has invalid {field}")
        for field in ("candidateCount", "sourceKeyCount"):
            value = row.get(field)
            if type(value) is not int or value < 0:
                errors.append(f"{kind} decision {index} has invalid {field}")
        if row.get("event", "").endswith(".decision"):
            for field in ("species",):
                if not isinstance(row.get(field), str) or not row[field]:
                    errors.append(f"{kind} decision {index} has no {field}")
            for field in ("settingsRevision", "sourceIntervalTicks", "alertTicks"):
                value = row.get(field)
                if type(value) is not int or value < 0:
                    errors.append(f"{kind} decision {index} has invalid {field}")
            for field in ("radius", "sensitivity", "sourceStrength", "effectiveStrength"):
                value = row.get(field)
                if not isinstance(value, (int, float)) or not math.isfinite(value) or value < 0.0:
                    errors.append(f"{kind} decision {index} has invalid {field}")
            if type(row.get("boatCorrelation")) is not bool:
                errors.append(f"{kind} decision {index} has no boolean boatCorrelation")
            if type(row.get("acceptedThreshold")) is not bool:
                errors.append(f"{kind} decision {index} has no boolean acceptedThreshold")
        if row.get("event", "").endswith(".throttle"):
            value = row.get("throttleElapsedTicks")
            if type(value) is not int or value < 0:
                errors.append(f"{kind} throttle {index} has invalid throttleElapsedTicks")
    return {"eventCounts": dict(counts), "recordCount": len(records)}


def validate_fishing(records: list[dict[str, Any]], contract: Any, errors: list[str]) -> dict[str, Any]:
    """Check correlated delivery and post reel observations without inventing missing evidence."""
    if not isinstance(contract, dict):
        errors.append("fishing manifest must be an object")
        return {}
    attempts: dict[str, list[dict[str, Any]]] = defaultdict(list)
    outcomes: defaultdict[str, int] = defaultdict(int)
    for row in records:
        if row.get("event") != "fishing":
            continue
        attempt = row.get("attemptId")
        if not isinstance(attempt, str) or not attempt:
            errors.append("fishing record has no attemptId")
            continue
        attempts[attempt].append(row)
    minimum = contract.get("minimumAttempts", 1)
    if type(minimum) is not int or minimum < 1:
        errors.append("fishing minimumAttempts must be a positive integer")
    elif len(attempts) < minimum:
        errors.append(f"fishing observed {len(attempts)} attempts, requires {minimum}")
    failure_outcomes = {"attempt_already_reserved", "previously_cancelled", "invalid_hook_or_owner",
                        "missing_entity_mapping", "entity_creation_failed", "insertion_rejected",
                        "hook_invalidated_after_insertion"}
    for attempt, rows in attempts.items():
        prefix = f"fishing {attempt}"
        deliveries = [row for row in rows if row.get("stage") == "delivery"]
        settlements = [row for row in rows if row.get("stage") == "settled"]
        if not deliveries:
            errors.append(f"{prefix} has no delivery observation")
        committed = 0
        for row in deliveries:
            outcome = row.get("outcome")
            if not isinstance(outcome, str):
                errors.append(f"{prefix} has no outcome")
                continue
            outcomes[outcome] += 1
            if not isinstance(row.get("angler"), str) or not row["angler"].startswith("player_"):
                errors.append(f"{prefix} has no pseudonymous angler")
            if row.get("dropListTruncated") is not False or row.get("rodEnchantmentsTruncated") is not False:
                errors.append(f"{prefix} contains missing or truncated item evidence")
            for field in ("replaceVanillaMobs", "fishEntities", "cancelledBefore", "cancelledAfter"):
                if type(row.get(field)) is not bool:
                    errors.append(f"{prefix} has no boolean {field}")
            for field in ("deliveryCount", "selectedCount", "fishStatisticDelta", "xpRequested", "xpAccepted"):
                if type(row.get(field)) is not int or row[field] < 0:
                    errors.append(f"{prefix} has no nonnegative integer {field}")
            if outcome == "unmodified_loot_passthrough":
                if row.get("deliveryCount") != 0 or row.get("xpAccepted") != 0:
                    errors.append(f"{prefix} added a BFS reward to passthrough loot")
                continue
            before, after = row.get("advancementsBefore"), row.get("advancementsAfter")
            if not isinstance(before, dict) or not isinstance(after, dict) or any(
                    type(progress.get(key)) is not bool for progress in (before, after)
                    for key in ("oh_my_cod", "why_arent_you_red")):
                errors.append(f"{prefix} is missing catch advancement progress")
                before, after = {}, {}
            if outcome == "committed":
                committed += 1
                if row.get("deliveryCount") != 1 or row.get("selectedCount") != 1:
                    errors.append(f"{prefix} did not commit exactly one fish")
                if row.get("insertionAccepted") is not True or row.get("fishStatisticDelta") != 1:
                    errors.append(f"{prefix} lacks accepted insertion or one catch statistic")
                if row.get("cancelledBefore") is not False:
                    errors.append(f"{prefix} committed a previously cancelled catch")
                xp = row.get("xpAccepted")
                if type(xp) is not int or not 1 <= xp <= 6 or xp != row.get("xpRequested"):
                    errors.append(f"{prefix} lacks its accepted XP reward")
                expected_kind = "live" if row.get("fishEntities") is True else "item"
                if row.get("deliveryKind") != expected_kind:
                    errors.append(f"{prefix} has the wrong delivery mode")
                expected_type = row.get("selectedSpecies") if expected_kind == "live" else "minecraft:item"
                if not isinstance(expected_type, str) or expected_type.startswith("unavailable:") \
                        or row.get("deliveryType") != expected_type:
                    errors.append(f"{prefix} has the wrong delivered entity type")
                selected = row.get("selectedItem")
                species = {
                    "minecraft:cod": "minecraft:cod", "minecraft:salmon": "minecraft:salmon",
                    "minecraft:tropical_fish": "minecraft:tropical_fish", "minecraft:pufferfish": "minecraft:pufferfish",
                    "bensfintasticsharks:raw_atlantic_cod": "bensfintasticsharks:atlantic_cod",
                    "bensfintasticsharks:raw_atlantic_salmon": "bensfintasticsharks:atlantic_salmon",
                }
                if selected not in species or row.get("selectedSpecies") != species.get(selected):
                    errors.append(f"{prefix} has an inconsistent selected species")
                if row.get("replaceVanillaMobs") is True and selected in {"minecraft:cod", "minecraft:salmon"}:
                    errors.append(f"{prefix} leaked a replaced vanilla fish")
                for item, key in (("bensfintasticsharks:raw_atlantic_cod", "oh_my_cod"),
                                  ("bensfintasticsharks:raw_atlantic_salmon", "why_arent_you_red")):
                    if selected == item and after.get(key) is not True:
                        errors.append(f"{prefix} did not grant its matching advancement")
                    elif selected != item and after.get(key) != before.get(key):
                        errors.append(f"{prefix} changed an unrelated catch advancement")
                impulse = row.get("reelImpulse")
                if not isinstance(impulse, dict) or any(type(impulse.get(axis)) not in (int, float)
                        or not math.isfinite(impulse[axis]) for axis in ("x", "y", "z")):
                    errors.append(f"{prefix} has no finite reel impulse")
            elif outcome in failure_outcomes:
                if row.get("deliveryCount") != 0 or row.get("fishStatisticDelta") != 0 \
                        or row.get("xpAccepted") != 0 or before != after:
                    errors.append(f"{prefix} awarded success for a failed attempt")
            else:
                errors.append(f"{prefix} has an unverified outcome {outcome}")
        if committed > 1:
            errors.append(f"{prefix} committed more than once")
        if len(settlements) != 1:
            errors.append(f"{prefix} requires exactly one post-reel settlement")
        else:
            settled = settlements[0]
            if settled.get("ambiguousSettlement") is not False:
                errors.append(f"{prefix} has ambiguous same-tick rod settlement")
            if settled.get("hookRemoved") is not True or settled.get("hookStillOwned") is not False:
                errors.append(f"{prefix} did not clear its hook")
            for field in ("rodDamageBefore", "rodDamageAfter", "rodCountAfter"):
                if type(settled.get(field)) is not int or settled[field] < 0:
                    errors.append(f"{prefix} lacks observed {field}")
            expected_damage = contract.get("expectedRodDamageDelta")
            if expected_damage is not None:
                if type(expected_damage) is not int or expected_damage < 0:
                    errors.append("fishing expectedRodDamageDelta must be a nonnegative integer")
                elif type(settled.get("rodDamageBefore")) is int and type(settled.get("rodDamageAfter")) is int \
                        and settled["rodDamageAfter"] - settled["rodDamageBefore"] != expected_damage:
                    errors.append(f"{prefix} has the wrong observed rod damage")
    required = contract.get("requiredOutcomes", [])
    if not isinstance(required, list) or any(not isinstance(value, str) for value in required):
        errors.append("fishing requiredOutcomes must be a string list")
    else:
        for outcome in required:
            if not outcomes[outcome]:
                errors.append(f"fishing did not observe required outcome {outcome}")
    return {"attemptCount": len(attempts), "outcomes": dict(outcomes)}


def validate_candidate_binding(header: dict[str, Any], manifest: dict[str, Any], scenario: str | None,
                               requirement: str | None, errors: list[str]) -> None:
    """Bind a CLI analysis invocation to a declared candidate scenario and requirement."""
    if scenario is None and requirement is None:
        return
    manifest_scenario = manifest.get("scenarioId")
    manifest_requirement = manifest.get("requirementId")
    if not isinstance(manifest_scenario, str) or not manifest_scenario:
        errors.append("candidate manifest scenarioId is missing")
    elif scenario != manifest_scenario:
        errors.append("candidate manifest scenarioId does not match --scenario")
    if not isinstance(manifest_requirement, str) or not manifest_requirement:
        errors.append("candidate manifest requirementId is missing")
    elif requirement != manifest_requirement:
        errors.append("candidate manifest requirementId does not match --requirement")
    for field, expected in (("scenarioId", manifest_scenario), ("requirementId", manifest_requirement)):
        recorded = header.get(field)
        if not isinstance(recorded, str):
            errors.append(f"header {field} is missing")
        elif isinstance(expected, str) and recorded not in {expected, "unavailable:provided_by_candidate_manifest"}:
            errors.append(f"header {field} does not match the candidate manifest")


def validate_finite_coordinates(record: dict[str, Any], index: int, errors: list[str]) -> None:
    for field in ("x", "y", "z", "velocityX", "velocityY", "velocityZ", "yaw", "pitch"):
        value = record.get(field)
        if not isinstance(value, (int, float)) or not math.isfinite(value):
            errors.append(f"movement record {index} has non-finite {field}")


def movement_metrics(history: dict[str, list[dict[str, Any]]], errors: list[str],
                     strict_tick_continuity: bool = True) -> dict[str, Any]:
    entities: dict[str, Any] = {}
    for entity_id, samples in history.items():
        max_vertical_speed = max((abs(float(sample["velocityY"])) for sample in samples
                                  if isinstance(sample.get("velocityY"), (int, float))), default=0.0)
        max_horizontal_speed = max((math.hypot(float(sample["velocityX"]), float(sample["velocityZ"]))
                                    for sample in samples
                                    if isinstance(sample.get("velocityX"), (int, float))
                                    and isinstance(sample.get("velocityZ"), (int, float))), default=0.0)
        max_total_speed = max((math.sqrt(float(sample["velocityX"]) ** 2
                                          + float(sample["velocityY"]) ** 2
                                          + float(sample["velocityZ"]) ** 2)
                               for sample in samples
                               if isinstance(sample.get("velocityX"), (int, float))
                               and isinstance(sample.get("velocityY"), (int, float))
                               and isinstance(sample.get("velocityZ"), (int, float))), default=0.0)
        max_pitch_step = 0.0
        coordinate_steps: list[float] = []
        moving_samples = 0
        tick_gaps = 0
        for previous, current in zip(samples, samples[1:]):
            if current.get("tick") != previous.get("tick", 0) + 1:
                tick_gaps += 1
            if strict_tick_continuity and current.get("tick") != previous.get("tick", 0) + 1:
                errors.append(f"entity {entity_id} has a missing required movement tick between "
                              f"{previous.get('tick')} and {current.get('tick')}")
            if all(isinstance(sample.get(field), (int, float))
                   for sample in (previous, current) for field in ("x", "y", "z", "pitch")):
                coordinate_steps.append(math.dist(
                    (previous["x"], previous["y"], previous["z"]),
                    (current["x"], current["y"], current["z"])))
                if coordinate_steps[-1] > 0.0:
                    moving_samples += 1
                max_pitch_step = max(max_pitch_step, wrapped_degrees(float(current["pitch"]) - float(previous["pitch"])))
        entities[entity_id] = {
            "samples": len(samples),
            "maxVerticalSpeed": max_vertical_speed,
            "maxHorizontalSpeed": max_horizontal_speed,
            "maxTotalSpeed": max_total_speed,
            "maxPitchStepDegrees": max_pitch_step,
            "maxCoordinateStep": max(coordinate_steps, default=0.0),
            "movingSampleTransitions": moving_samples,
            "tickGaps": tick_gaps,
            "netVerticalDisplacement": net_vertical_displacement(samples),
            "history": samples,
        }
    return {"entities": entities}


def net_vertical_displacement(samples: list[dict[str, Any]]) -> float:
    if len(samples) < 2 or not all(isinstance(sample.get("y"), (int, float)) for sample in samples):
        return 0.0
    return float(samples[-1]["y"]) - float(samples[0]["y"])


def apply_manifest_checks(header: dict[str, Any], metrics: dict[str, Any], manifest: dict[str, Any],
                          errors: list[str]) -> dict[str, Any]:
    """Apply only thresholds explicitly supplied by the candidate manifest."""
    checks: dict[str, Any] = {"limits": {}, "routeVerdicts": {}}
    expected_artifact = manifest.get("artifactSha256")
    if isinstance(expected_artifact, str) and header.get("artifactSha256") != expected_artifact:
        errors.append("header artifactSha256 does not match the candidate manifest")

    entity_expectations = manifest.get("entities", {})
    if not isinstance(entity_expectations, dict):
        errors.append("candidate manifest entities must be an object")
        return checks
    global_required_fields = manifest.get("requiredMovementFields", [])
    if not isinstance(global_required_fields, list) or not all(isinstance(field, str) for field in global_required_fields):
        errors.append("candidate manifest requiredMovementFields must be an array of field names")
        global_required_fields = []
    for entity_id, expectation in entity_expectations.items():
        if not isinstance(expectation, dict):
            errors.append(f"candidate manifest entity {entity_id} must be an object")
            continue
        measured = metrics["entities"].get(entity_id)
        if measured is None:
            errors.append(f"expected entity {entity_id} has no movement samples")
            continue
        required_fields = expectation.get("requiredFields", global_required_fields)
        if not isinstance(required_fields, list) or not all(isinstance(field, str) for field in required_fields):
            errors.append(f"candidate manifest entity {entity_id} requiredFields must be an array of field names")
            required_fields = []
        validate_required_fields(entity_id, measured["history"], required_fields, errors)
        limit_report: dict[str, Any] = {}
        minimum_samples = expectation.get("minimumSamples")
        if isinstance(minimum_samples, int) and measured["samples"] < minimum_samples:
            errors.append(f"entity {entity_id} has fewer than {minimum_samples} movement samples")
        if isinstance(minimum_samples, int):
            limit_report["minimumSamples"] = minimum_samples
        minimum_vertical = expectation.get("minimumNetVerticalDisplacement")
        if isinstance(minimum_vertical, (int, float)) and abs(measured["netVerticalDisplacement"]) < minimum_vertical:
            errors.append(f"entity {entity_id} did not reach the declared net vertical displacement")
        if isinstance(minimum_vertical, (int, float)):
            limit_report["minimumNetVerticalDisplacement"] = minimum_vertical
        minimum_moving_transitions = expectation.get("minimumMovingSampleTransitions")
        if isinstance(minimum_moving_transitions, int) and measured["movingSampleTransitions"] < minimum_moving_transitions:
            errors.append(f"entity {entity_id} did not produce the declared moving sample transitions")
        if isinstance(minimum_moving_transitions, int):
            limit_report["minimumMovingSampleTransitions"] = minimum_moving_transitions
        maximum_step = expectation.get("maximumCoordinateStep")
        if isinstance(maximum_step, (int, float)) and exceeds_limit(measured["maxCoordinateStep"], maximum_step):
            errors.append(f"entity {entity_id} exceeded the declared coordinate continuity limit")
        if isinstance(maximum_step, (int, float)):
            limit_report["maximumCoordinateStep"] = maximum_step
        maximum_pitch_step = expectation.get("maximumPitchStepDegrees")
        if isinstance(maximum_pitch_step, (int, float)) and exceeds_limit(measured["maxPitchStepDegrees"], maximum_pitch_step):
            errors.append(f"entity {entity_id} exceeded the declared pitch transition limit")
        if isinstance(maximum_pitch_step, (int, float)):
            limit_report["maximumPitchStepDegrees"] = maximum_pitch_step
        checks["limits"][entity_id] = limit_report
        route = expectation.get("routeShape")
        if isinstance(route, dict):
            checks["routeVerdicts"][entity_id] = apply_route_shape_check(entity_id, measured["history"], route, errors)
    return checks


def validate_required_fields(entity_id: str, samples: list[dict[str, Any]], required_fields: list[str],
                             errors: list[str]) -> None:
    for sample_index, sample in enumerate(samples, start=1):
        for field in required_fields:
            value = sample.get(field)
            if value is None:
                errors.append(f"entity {entity_id} movement sample {sample_index} is missing required field {field}")
            elif (sample_index == 1 and field in FIRST_SAMPLE_DERIVED_FIELDS
                  and value == "unavailable:no_previous_sample"):
                continue
            elif isinstance(value, str) and value.startswith("unavailable:"):
                errors.append(f"entity {entity_id} movement sample {sample_index} has unavailable required field {field}")


def exceeds_limit(value: float, limit: float) -> bool:
    tolerance = COMPARISON_EPSILON * max(1.0, abs(limit))
    return value - limit > tolerance


def apply_route_shape_check(entity_id: str, samples: list[dict[str, Any]], route: dict[str, Any],
                            errors: list[str]) -> dict[str, Any]:
    """Reject winding only when the candidate manifest declares the route target and limit."""
    report: dict[str, Any] = {"verdict": "invalid", "horizontalWindingTurns": None}
    target = route.get("target")
    maximum_turns = route.get("maximumHorizontalWindingTurns")
    if not isinstance(target, dict) or not isinstance(maximum_turns, (int, float)):
        errors.append(f"entity {entity_id} routeShape must declare target and maximumHorizontalWindingTurns")
        return report
    target_x = target.get("x")
    target_z = target.get("z")
    if not isinstance(target_x, (int, float)) or not isinstance(target_z, (int, float)):
        errors.append(f"entity {entity_id} routeShape target requires finite x and z")
        return report
    angles: list[float] = []
    for sample in samples:
        x, z = sample.get("x"), sample.get("z")
        if not isinstance(x, (int, float)) or not isinstance(z, (int, float)):
            continue
        horizontal_distance = math.hypot(float(x) - float(target_x), float(z) - float(target_z))
        if horizontal_distance > 0.0:
            angles.append(math.atan2(float(z) - float(target_z), float(x) - float(target_x)))
    if len(angles) < 2:
        report["verdict"] = "not_applicable"
        return report
    winding = 0.0
    previous = angles[0]
    for current in angles[1:]:
        delta = (current - previous + math.pi) % (2.0 * math.pi) - math.pi
        winding += delta
        previous = current
    winding_turns = abs(winding) / (2.0 * math.pi)
    report["horizontalWindingTurns"] = winding_turns
    report["maximumHorizontalWindingTurns"] = float(maximum_turns)
    if exceeds_limit(winding_turns, float(maximum_turns)):
        errors.append(f"entity {entity_id} exceeded declared horizontal route winding limit "
                      f"with {winding_turns:.6f} turns")
        return report
    report["verdict"] = "pass"
    return report


def wrapped_degrees(value: float) -> float:
    value = abs((value + 180.0) % 360.0 - 180.0)
    return value


def result(verdict: str, errors: list[str], warnings: list[str], metrics: dict[str, Any],
           records: list[dict[str, Any]], manifest: dict[str, Any] | None = None,
           events: dict[str, int] | None = None, checks: dict[str, Any] | None = None,
           coverage: dict[str, Any] | None = None) -> dict[str, Any]:
    return {
        "verdict": verdict,
        "errors": errors,
        "warnings": warnings,
        "metrics": metrics,
        "recordCount": len(records),
        "candidateManifest": manifest or {},
        "events": events or {},
        "checks": checks or {},
        "coverage": coverage or {},
    }


def int_or_zero(value: Any) -> int:
    return value if isinstance(value, int) else 0


def write_result(output: Path, analysis: dict[str, Any], scenario: str, requirement: str) -> None:
    output.mkdir(parents=True, exist_ok=True)
    (output / "verdict.json").write_text(json.dumps(analysis, indent=2, sort_keys=True) + "\n", encoding="utf-8")
    event_lines = [f"{name}: {count}" for name, count in sorted(analysis["events"].items())] or ["None"]
    coverage = analysis["coverage"]
    coverage_lines = [
        f"Movement entities: {coverage.get('movementEntityCount', 0)}",
        f"Movement samples: {coverage.get('movementSampleCount', 0)}",
        f"Dropped records: {coverage.get('droppedRecords', 'unavailable')}",
        f"Terminal record: {coverage.get('hasTerminalRecord', False)}",
    ]
    fishing = analysis["metrics"].get("fishing")
    if fishing is not None:
        coverage_lines.append(f"Fishing attempts: {fishing['attemptCount']}")
        coverage_lines.extend(f"Fishing {outcome}: {count}" for outcome, count in sorted(fishing["outcomes"].items()))
    follow = analysis["metrics"].get("follow")
    if follow is not None:
        coverage_lines.append(f"Follow records: {follow['recordCount']}")
        coverage_lines.extend(f"Follow {event}: {count}" for event, count in sorted(follow["eventCounts"].items()))
    extrema_lines = []
    for entity_id, metrics in sorted(analysis["metrics"].get("entities", {}).items()):
        extrema_lines.append(
            f"{entity_id}: samples={metrics['samples']}, max total speed={metrics['maxTotalSpeed']:.6f}, "
            f"max horizontal speed={metrics['maxHorizontalSpeed']:.6f}, "
            f"max vertical speed={metrics['maxVerticalSpeed']:.6f}, "
            f"max pitch step={metrics['maxPitchStepDegrees']:.6f}, "
            f"max coordinate step={metrics['maxCoordinateStep']:.6f}"
        )
    route_lines = [
        f"{entity_id}: {route['verdict']}, horizontal winding turns={route['horizontalWindingTurns']}"
        for entity_id, route in sorted(analysis["checks"].get("routeVerdicts", {}).items())
    ]
    summary = [
        "# BFS Debug Analysis",
        "",
        f"Scenario: {scenario}",
        f"Requirement: {requirement}",
        f"Verdict: {analysis['verdict']}",
        f"Records: {analysis['recordCount']}",
        "",
        "## Errors",
        *(analysis["errors"] or ["None"]),
        "",
        "## Warnings",
        *(analysis["warnings"] or ["None"]),
        "",
        "## Coverage",
        *coverage_lines,
        "",
        "## Observed Extrema",
        *(extrema_lines or ["None"]),
        "",
        "## Route Verdicts",
        *(route_lines or ["None"]),
        "",
        "## Event Counts",
        *event_lines,
    ]
    (output / "summary.md").write_text("\n".join(summary) + "\n", encoding="utf-8")


def main() -> int:
    arguments = parse_arguments()
    manifest = load_json(arguments.candidate_manifest)
    records, parse_errors = load_capture(arguments.capture)
    analysis = validate(records, parse_errors, manifest, arguments.scenario, arguments.requirement)
    write_result(arguments.output, analysis, arguments.scenario, arguments.requirement)
    print(f"BFS debug analysis: {analysis['verdict']}")
    return 0 if analysis["verdict"] == "complete" else 2 if analysis["verdict"] == "incomplete" else 1


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except ValueError as error:
        print(f"BFS debug analysis: invalid. {error}", file=sys.stderr)
        raise SystemExit(1)
