#!/usr/bin/env python3
"""Validate a bounded BFS debug capture without modifying the raw JSONL input."""

from __future__ import annotations

import argparse
import json
import math
import sys
from collections import defaultdict
from pathlib import Path
from typing import Any


SCHEMA = "bfs-debug-v2"


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
    metrics = movement_metrics(movement_history, errors)
    checks = apply_manifest_checks(header, metrics, manifest, errors)
    verdict = "invalid" if errors else "incomplete" if warnings else "complete"
    coverage = {
        "movementEntityCount": len(movement_history),
        "movementSampleCount": sum(len(samples) for samples in movement_history.values()),
        "droppedRecords": int_or_zero(end_records[0].get("recordsDropped")) if len(end_records) == 1 else None,
        "hasTerminalRecord": len(end_records) == 1,
    }
    return result(verdict, errors, warnings, metrics, records, manifest, dict(events), checks, coverage)


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


def movement_metrics(history: dict[str, list[dict[str, Any]]], errors: list[str]) -> dict[str, Any]:
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
        for previous, current in zip(samples, samples[1:]):
            if current.get("tick") != previous.get("tick", 0) + 1:
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
        if isinstance(maximum_step, (int, float)) and measured["maxCoordinateStep"] > maximum_step:
            errors.append(f"entity {entity_id} exceeded the declared coordinate continuity limit")
        if isinstance(maximum_step, (int, float)):
            limit_report["maximumCoordinateStep"] = maximum_step
        maximum_pitch_step = expectation.get("maximumPitchStepDegrees")
        if isinstance(maximum_pitch_step, (int, float)) and measured["maxPitchStepDegrees"] > maximum_pitch_step:
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
            elif isinstance(value, str) and value.startswith("unavailable:"):
                errors.append(f"entity {entity_id} movement sample {sample_index} has unavailable required field {field}")


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
    if winding_turns > float(maximum_turns):
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
