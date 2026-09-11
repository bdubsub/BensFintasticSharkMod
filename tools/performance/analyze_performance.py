"""Validate complete captures and compare matched packaged server tick distributions."""

import argparse
import csv
import hashlib
import json
import math
from pathlib import Path
import re
import statistics

from run_performance import SPECIES


def percentile(values, quantile):
    if not values:
        raise ValueError("An empty sample is not performance evidence")
    return sorted(values)[max(0, math.ceil(len(values) * quantile) - 1)]


def java_map(text):
    if not text.startswith("{") or not text.endswith("}"):
        raise ValueError("Invalid census map")
    if text == "{}":
        return {}
    pairs = [part.strip().split("=", 1) for part in text[1:-1].split(",")]
    return {key.removesuffix("Forge"): int(value) for key, value in pairs}


def digest(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def load_run(suite, name, expected_ticks=36000):
    manifest_path = suite / f"{name}-manifest.json"
    tick_path = suite / f"{name}-probe-ticks.csv"
    census_path = suite / f"{name}-probe-census.tsv"
    end_path = suite / f"{name}-probe.complete"
    manifest = json.loads(manifest_path.read_text())
    if manifest.get("result") != "completed" or manifest.get("exit_code") != 0:
        raise ValueError(f"{name} did not complete with a clean server shutdown")
    if manifest["ticks"] != expected_ticks:
        raise ValueError(f"{name} has the wrong requested duration")
    if f"ticks={expected_ticks}\n" not in end_path.read_text():
        raise ValueError(f"{name} has no matching completion marker")
    with tick_path.open() as stream:
        rows = list(csv.DictReader(stream))
    if [int(row["tick"]) for row in rows] != list(range(1, expected_ticks + 1)):
        raise ValueError(f"{name} contains missing, duplicate or reordered ticks")
    durations = [int(row["duration_ns"]) / 1_000_000 for row in rows]
    if any(not math.isfinite(value) or value <= 0 for value in durations):
        raise ValueError(f"{name} contains invalid tick durations")
    with census_path.open() as stream:
        all_samples = list(csv.DictReader(stream, delimiter="\t"))
    warmup = manifest["warmup"]
    warmup_offsets = [-warmup, *(elapsed - warmup for elapsed in range(200, warmup + 1, 200))]
    measurement_offsets = [0, *range(200, expected_ticks + 1, 200)]
    if expected_ticks % 200:
        measurement_offsets.append(expected_ticks)
    if [int(row["tick"]) for row in all_samples] != warmup_offsets + measurement_offsets:
        raise ValueError(f"{name} contains an incomplete census")
    samples = all_samples[len(warmup_offsets):]
    count_samples = [java_map(row["species_counts"]) for row in samples]
    expected_targets = {name: entry[1] * manifest["scale"] for name, entry in SPECIES.items()}
    if manifest["species_targets"] != expected_targets:
        raise ValueError(f"{name} has an unexpected population prescription")
    class_targets = {entry[0]: entry[1] * manifest["scale"] for entry in SPECIES.values()}
    if any(count < 0 or count > class_targets.get(species, -1)
           for sample in count_samples for species, count in sample.items()):
        raise ValueError(f"{name} exceeds its prescribed workload population")
    observed_species = sorted({key for sample in count_samples for key in sample})
    if set(observed_species) != {entry[0] for entry in SPECIES.values()}:
        raise ValueError(f"{name} did not observe all 22 species")
    heap = [int(row["heap_used_bytes"]) for row in samples]
    last = samples[-1]
    counters = java_map(last["counters"])
    counts = {species: {"minimum": min(row.get(species, 0) for row in count_samples),
                        "maximum": max(row.get(species, 0) for row in count_samples),
                        "mean": statistics.mean(row.get(species, 0) for row in count_samples),
                        "final": count_samples[-1].get(species, 0)} for species in observed_species}
    air = {species: min(java_map(row["minimum_air"])[species] for row in samples
                        if species in java_map(row["minimum_air"]))
           for species in observed_species}
    measured_heap = heap[1:]
    windows = [min(measured_heap[index:index + 30]) for index in range(0, len(measured_heap), 30)]
    return {"name": name, "ticks": expected_ticks, "warmup_ticks": manifest["warmup"],
            "scale": manifest["scale"], "seed": manifest["seed"], "jar_sha256": manifest["jar_sha256"],
            "warmup_census_rows": len(warmup_offsets), "measurement_census_rows": len(samples),
            "geckolib_sha256": manifest["geckolib_sha256"],
            "smartbrainlib_sha256": manifest["smartbrainlib_sha256"],
            "probe_agent_sha256": manifest["probe_agent_sha256"],
            "probe_data_sha256": manifest["probe_data_sha256"],
            "tick_ms": {"median": statistics.median(durations), "p95": percentile(durations, 0.95),
                        "p99": percentile(durations, 0.99), "maximum": max(durations)},
            "heap_bytes": {"minimum": min(heap), "maximum": max(heap), "final": heap[-1],
                           "five_minute_window_minima": windows},
            "per_species_counts": counts, "minimum_air": air, "counters": counters,
            "population": {"target": sum(manifest["species_targets"].values()),
                           "minimum": min(sum(row.values()) for row in count_samples),
                           "maximum": max(sum(row.values()) for row in count_samples),
                           "mean": statistics.mean(sum(row.values()) for row in count_samples)},
            "maximum_brain_memories": max(int(row["maximum_brain_memories"]) for row in samples),
            "moving_entity_samples": sum(int(row["moving_entities"]) for row in samples),
            "maximum_observer_position_entries": max(int(row["position_samples"]) for row in samples),
            "raw_sha256": {"ticks": digest(tick_path), "census": digest(census_path),
                           "completion": digest(end_path), "console": manifest["console_sha256"]}}


def compare(baseline, candidate):
    for field in ("ticks", "warmup_ticks", "seed", "scale", "geckolib_sha256", "smartbrainlib_sha256",
                  "probe_agent_sha256", "probe_data_sha256"):
        if baseline[field] != candidate[field]:
            raise ValueError(f"Unmatched comparison field {field}")
    change = candidate["tick_ms"]["p95"] / baseline["tick_ms"]["p95"] - 1
    return {"baseline": baseline["name"], "candidate": candidate["name"],
            "p95_change_percent": change * 100,
            "p95_gate_passed": candidate["tick_ms"]["p95"] <= baseline["tick_ms"]["p95"] * 1.10,
            "scope": "Performance only. Runtime safety and gameplay acceptance require their separate evidence."}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("suite", type=Path)
    args = parser.parse_args()
    names = ("baseline-normal", "candidate-normal", "baseline-double", "candidate-double")
    runs = {name: load_run(args.suite, name) for name in names}
    report = {"schema": 1, "percentile_method": "Nearest rank over individual completed server ticks",
              "runs": runs, "comparisons": [compare(runs["baseline-normal"], runs["candidate-normal"]),
                                               compare(runs["baseline-double"], runs["candidate-double"])]}
    print(json.dumps(report, indent=2))
    if not all(result["p95_gate_passed"] for result in report["comparisons"]):
        raise SystemExit(1)


if __name__ == "__main__":
    main()
