# BFS Debug Diagnostics

The debug capture system produces bounded structured evidence before an interactive client session is requested. It is intended for server side movement, brain, combat, population, advancement, and algae investigations. It does not change entity movement, AI, spawning, or combat behavior.

## Server capture

The server command requires permission level 2. It also works from the dedicated server console.

```text
/bfs debug on
/bfs debug on <all|movement|brain|combat|population|advancement|algae> <20-36000> [targets]
/bfs debug status
/bfs debug off
```

`/bfs debug on` captures the `all` category for 1,200 server ticks. Without an explicit target selector, it selects only loaded BFS living entities within 128 blocks of the command source. The selected list is capped at 32 entities. The header records the eligible, selected, and excluded counts, so an empty selection cannot be used as proof of an entity behavior check.

Each server owns at most one session. A second `on` command reports the existing session without resetting its limits. A session ends at its requested tick duration, its wall time deadline, an explicit `off`, a source dimension loss, or server shutdown. The wall deadline is twice the requested tick duration at 20 ticks per second plus 30 seconds. Queue overflow, oversized records, write failure, source loss, and server shutdown mark the capture incomplete.

Server captures are written under the server game directory at `logs/bfs-debug/bfs-debug-<timestamp>-<session>.jsonl`. The writer runs outside the logical server tick. It accepts at most 8,192 queued records, limits each record to 16 KiB, limits a session to 32 MiB, and retains no more than 256 MiB of server capture files. Capture files have a `bfs-debug-v2` JSONL header, samples or event records, and exactly one terminal record when they finish cleanly. Every record includes a monotonic sequence, UTC timestamp, and elapsed monotonic time so a trace can be rejected when ordering is corrupted.

## Local client capture

The local client command is separate from the server command:

```text
/bfs debug client on
/bfs debug client status
/bfs debug client off
```

It is registered through Forge's client command API and never sends a command or diagnostic payload to the server. It selects up to 32 loaded BFS living entities within 128 blocks of the local player, expires after 1,200 client ticks or 90 seconds, and writes only under the local game directory at `logs/bfs-debug/client/`. It is for client state comparison after the server side evidence identifies a question that needs rendering or input verification. Run it only on EnVy's Linux laptop, never on `node-1`.

## Offline analysis

Use the parser with the raw capture, the planned scenario and requirement identifiers, and the candidate profile or configuration manifest:

```bash
python3 tools/bfs_debug_analyze.py \
  <capture.jsonl> \
  --scenario <scenario-id> \
  --requirement <requirement-id> \
  --candidate-manifest <candidate-manifest.json> \
  --output <empty-analysis-directory>
```

The parser preserves the raw sample history and writes `verdict.json` and `summary.md`. `complete` requires valid JSONL, a matching schema and session, finite movement values, monotonic per-dimension ticks, and one complete end record. `incomplete` is never a passing verdict. `invalid` identifies malformed JSON, wrong schema, a missing end record, a nonfinite value, a tick reversal, or a manifest mismatch.

The optional candidate manifest supplies the only acceptance thresholds applied by the parser. For example, a scenario can bind an expected artifact hash and entity specific sample count, net vertical displacement, coordinate continuity, and pitch transition limits. The analyzer reports measured peaks and complete history instead of inventing limits.

```json
{
  "artifactSha256": "<candidate-jar-sha256>",
  "entities": {
    "<entity-uuid>": {
      "minimumSamples": 20,
      "minimumNetVerticalDisplacement": 0.25,
      "maximumCoordinateStep": 1.0,
      "maximumPitchStepDegrees": 0.3
    }
  }
}
```

Current development captures explicitly report an unavailable artifact hash until a packaged candidate is bound. Do not use an unbound development capture as release evidence.

## Headless verification and cleanup

Run server side GameTests on `node-1` only in an isolated runtime. Use Java 17 and set the exact disposable runtime's `eula.txt` to `eula=true` before launch. The `bfsGameTestRunDir` Gradle property prevents a GameTest run from touching a personal development world:

```bash
JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 \
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks \
  -PbfsGameTestRunDir=<disposable-runtime>
```

Confirm the server process exits, retain only the required sanitized evidence, then remove the exact disposable runtime. Do not delete or alter `forge/run`, a personal instance, an existing world, or a shared dependency cache.

## Depth route baseline fixture

`BfsGameTests` runs isolated 60 tick movement captures for Atlantic Cod, Atlantic Salmon, Bottlenose Dolphin, Oceanic Whitetip Shark, and Tiger Shark. Each fixture selects its one spawned entity with a bounded server selector, reissues the same elevated navigation target each tick, and stops the diagnostic session cleanly. This keeps captures from other GameTest batches out of the trace while exercising the same route continuation used by the vertical movement checks.

Use the analyzer on each resulting JSONL file before interpreting movement values. The baseline fixture establishes observed behavior only. It does not supply speed, pitch, route winding, or vertical displacement thresholds, because those belong in the explicit candidate profile used by the later movement phase.
