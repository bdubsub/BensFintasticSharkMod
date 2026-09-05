# BFS Debug Diagnostics

The debug capture system produces bounded structured evidence before an interactive client session is requested. It is intended for server side movement, brain, combat, population, advancement, and algae investigations. It does not change entity movement, AI, spawning, or combat behavior.

## Server capture

The server command requires permission level 2. It also works from the dedicated server console.

The Gradle `:forge:Server` development task is a startup smoke test. It may not forward terminal input to the child server, so do not treat text echoed by that Gradle terminal as executed server commands. Use the real dedicated-server console for manual captures. Use the dedicated GameTest runtime to verify the dispatcher, permission gate, session lifecycle, and bounded capture behavior without a player or graphical client.

```text
/bfs debug on
/bfs debug on <all|movement|brain|combat|population|advancement|algae> <20-36000> [targets]
/bfs debug status
/bfs debug off
```

`/bfs debug off` is idempotent. Repeating it after a completed or manually stopped session reports that the capture is already inactive without returning a command failure.

`/bfs debug on` captures the `all` category for 1,200 server ticks. Without an explicit target selector, it selects only loaded BFS living entities within 128 blocks of the command source. The selected list is capped at 32 entities. The header records the eligible, selected, and excluded counts, so an empty selection cannot be used as proof of an entity behavior check.

Each server owns at most one session. A second `on` command reports the existing session without resetting its limits. A session ends at its requested tick duration, its wall time deadline, an explicit `off`, a source dimension loss, or server shutdown. The wall deadline is twice the requested tick duration at 20 ticks per second plus 30 seconds. Queue overflow, oversized records, write failure, source loss, and server shutdown mark the capture incomplete.

When a selected entity leaves the level, its `target_lifecycle` record includes its runtime ID, whether it was removed, and the engine removal reason when the engine exposes one. A missing entity without a loaded object records explicit `unavailable:` values instead. Use that lifecycle evidence before treating a missing target as an animation or navigation failure.

Server captures are written under the server game directory at `logs/bfs-debug/bfs-debug-<timestamp>-<session>.jsonl`. The writer runs outside the logical server tick. It accepts at most 8,192 queued records, limits each record to 16 KiB, limits a session to 32 MiB, and retains no more than 256 MiB of server capture files. Capture files have a `bfs-debug-v2` JSONL header, samples or event records, and exactly one terminal record when they finish cleanly. Every record includes a monotonic sequence, UTC timestamp, and elapsed monotonic time so a trace can be rejected when ordering is corrupted.

The server header identifies the mod, Minecraft, Forge, Java, GeckoLib, and SmartBrainLib versions. It also records GeckoLib's registered `geckolib:main` protocol version, the source revision or an explicit unavailable reason, artifact and configuration bindings, host role, side, tick rate, and units. `dedicated_server`, `integrated_server`, and `gametest_server` are distinct host roles. A GameTest run is server side evidence, but its `gametest_server` label must not be presented as a dedicated server or laptop client acceptance result.

## Local client capture

The local client command is separate from the server command:

```text
/bfs debug client on
/bfs debug client status
/bfs debug client off
```

Use the local command only after the laptop client has joined the exact disposable dedicated server. Keep the client on a nonactive Hyprland workspace with `pauseOnLostFocus:false`, identify its exact `hyprctl clients -j` row, and mute only the corresponding PipeWire stream before collecting a trace. Record the client PID, `stableId`, workspace, class, title, capture hash, and active workspace before and after every image capture. On the current Hyprland session, use `grim -T <stableId> <capture.png>` for an owned inactive window. It captures the target foreign toplevel without moving the user to that workspace. Do not use `DISPLAY=:1 import -window` for an inactive Xwayland client because it captures the active desktop instead. Do not automate portal selection.

It is registered through Forge's client command API and never sends a command or diagnostic payload to the server. It selects up to 32 loaded BFS living entities within 128 blocks of the local player, expires after 1,200 client ticks or 90 seconds, and writes only under the local game directory at `logs/bfs-debug/client/`. Presentation samples run after entity rendering at most once every four client ticks. They include interpolated position, yaw, pitch, partial tick, and available GeckoLib controller state. It is for client state comparison after the server side evidence identifies a question that needs rendering or input verification. Run it only on EnVy's Linux laptop, never on `node-1`.

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

The parser preserves the raw sample history and writes `verdict.json` and `summary.md`. The machine-readable result includes raw history, observed speed and transition extrema, declared limits, route verdicts, event counts, sample coverage, terminal-record state, and dropped-record count. `complete` requires valid JSONL, a matching schema and session, finite movement values, monotonic per-dimension ticks, and one complete end record. `incomplete` is never a passing verdict. `invalid` identifies malformed JSON, wrong schema, a missing end record, a nonfinite value, a tick reversal, a required-field gap, a route violation, or a manifest mismatch.

The candidate manifest supplies the scenario and requirement identity plus the only acceptance thresholds applied by the parser. For example, a scenario can bind an expected artifact hash and entity-specific sample count, moving transitions, net vertical displacement, coordinate continuity, pitch transition limits, required implemented fields, and route-shape limits. A field with an `unavailable:` reason is valid telemetry only when no current claim requires it. The analyzer reports measured peaks and complete history instead of inventing limits.

```json
{
  "scenarioId": "<scenario-id>",
  "requirementId": "<requirement-id>",
  "artifactSha256": "<candidate-jar-sha256>",
  "requiredMovementFields": ["routeAttemptId", "motionWriter"],
  "entities": {
    "<entity-uuid>": {
      "minimumSamples": 20,
      "minimumMovingSampleTransitions": 10,
      "minimumNetVerticalDisplacement": 0.25,
      "maximumCoordinateStep": 1.0,
      "maximumPitchStepDegrees": 0.3,
      "routeShape": {
        "target": {"x": 0.0, "z": 0.0},
        "maximumHorizontalWindingTurns": 0.25
      }
    }
  }
}
```

Use `requiredFields` inside an individual entity row when only that entity requires an observable field. Do not declare a future-phase value as required until its owning phase provides a real hook. Known-bad captures must be rejected for a missing tick, unticked actor, nonfinite value, wrong artifact, coordinate discontinuity, and arriving circular or helical route.

Bind a packaged candidate before capture with JVM properties. Supply only fingerprints or manifest identifiers, never raw configuration, player data, credentials, or private addresses.

```text
-Dbfs.source.revision=<source-revision>
-Dbfs.artifact.sha256=<candidate-jar-sha256>
-Dbfs.motion.profile.version=<profile-version-or-unavailable-reason>
-Dbfs.configuration.fingerprint=<configuration-fingerprint>
-Dbfs.datapack.fingerprint=<data-pack-fingerprint>
```

The analyzer binds its required `--scenario` and `--requirement` arguments to matching `scenarioId` and `requirementId` values in the candidate manifest. A capture header may intentionally report those two fields as `unavailable:provided_by_candidate_manifest`, but it cannot disagree with the manifest. Current development captures explicitly report unavailable bindings until these properties and the manifest are supplied. Do not use an unbound development capture as release evidence.

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

## Diagnostic parity fixture

`serverDebugCaptureLeavesPairedPhysicsUnchanged` starts a bounded server capture for one of two identical Atlantic Cod fixtures. Both fixtures have AI and gravity disabled, receive the same scripted `MoverType.SELF` water movement for 20 ticks, and must finish with equal displacement while the captured fixture produces a complete, lossless trace. The fixture also requires observable movement, so an inert pair cannot satisfy the check. It verifies that diagnostic observation does not mutate paired server-side entity physics. It does not replace species locomotion acceptance tests.
