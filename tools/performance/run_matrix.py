"""Run the remaining matched cases after an already active baseline finishes."""

import argparse
import json
import os
from pathlib import Path
import subprocess
import sys
import time


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--suite", type=Path, required=True)
    parser.add_argument("--libraries", type=Path, required=True)
    parser.add_argument("--geckolib", type=Path, required=True)
    parser.add_argument("--smartbrainlib", type=Path, required=True)
    parser.add_argument("--java", type=Path, required=True)
    args = parser.parse_args()
    suite = args.suite.resolve(strict=True)
    baseline_manifest = suite / "baseline-normal-manifest.json"
    deadline = time.monotonic() + 4200
    while True:
        manifest = json.loads(baseline_manifest.read_text())
        if manifest.get("result") == "completed" and manifest.get("exit_code") == 0:
            break
        if manifest.get("result") == "failed":
            raise RuntimeError("The baseline failed. The remaining cases were not started.")
        pid = manifest.get("pid")
        if not isinstance(pid, int) or Path(f"/proc/{pid}/cwd").resolve() != suite / "baseline-normal":
            raise RuntimeError("The expected baseline server is no longer running")
        if time.monotonic() >= deadline:
            raise TimeoutError("Baseline completion deadline exceeded")
        time.sleep(5)
    common = ["--suite", str(suite), "--libraries", str(args.libraries), "--geckolib", str(args.geckolib),
              "--smartbrainlib", str(args.smartbrainlib), "--java", str(args.java)]
    runner = Path(__file__).with_name("run_performance.py")
    for name, jar, scale in [("candidate-normal", "bfs-1.0-rc.1.jar", 1),
                              ("baseline-double", "bfs-phase-001-baseline.jar", 2),
                              ("candidate-double", "bfs-1.0-rc.1.jar", 2)]:
        print("Starting", name, flush=True)
        subprocess.run([sys.executable, "-B", str(runner), *common, "--name", name,
                        "--jar", str(suite / jar), "--scale", str(scale)], check=True)
    print("All four captures completed. Analysis and runtime teardown remain required.", flush=True)


if __name__ == "__main__":
    main()
