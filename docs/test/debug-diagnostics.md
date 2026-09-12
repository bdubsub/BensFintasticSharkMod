# BFS Debug Diagnostics

The debug capture system produces bounded structured evidence before an interactive client session is requested. It is intended for server side movement, brain, combat, population, advancement, and algae investigations. It does not change entity movement, AI, spawning, or combat behavior.

Before the first capture, server and client status report inactive without an output path. No placeholder is converted to a filesystem path during startup. A completed capture retains its actual output path. This avoids the Windows `InvalidPathException` caused by the former `unavailable:no_completed_capture` placeholder, even when debugging was disabled.

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

The active status line and terminal record expose the p95 nanoseconds spent in the bounded server capture path and the number of sampled server ticks. The sample is collected only while a session is enabled and never allocates or scans when capture is disabled. Compare this value with the same seeded fixture with capture off, and reject the diagnostic gate when the added p95 exceeds 5 percent or 0.25 milliseconds, whichever allowance is larger.

When a selected entity leaves the level, its `target_lifecycle` record includes its runtime ID, whether it was removed, and the engine removal reason when the engine exposes one. A missing entity without a loaded object records explicit `unavailable:` values instead. Use that lifecycle evidence before treating a missing target as an animation or navigation failure. The server GameTest fixture `serverDebugCaptureRecordsRemovedTargetLifecycle` exercises the discarded path and requires the complete terminal record.

The bounded failure paths have deterministic checks in `BfsDebugCaptureFailureTest`. An oversized record stops with `record_byte_limit`, a full writer queue stops with `queue_limit`, and an output failure stops with `writer_failure`; each path marks the capture incomplete and preserves the reason in the stop summary. These tests exercise the writer without allocating a server world. The server GameTest harness remains the check for dispatcher behavior, permissions, target lifecycle, resource reload continuity, terminal footer, and gameplay parity. `serverDebugCaptureSurvivesResourceReload` runs the real `reload` command during an active capture and requires the session to remain complete and retain movement records.

Server captures are written under the server game directory at `logs/bfs-debug/bfs-debug-<timestamp>-<session>.jsonl`. The writer runs outside the logical server tick. It accepts at most 8,192 queued records, limits each record to 16 KiB, limits a session to 32 MiB, and retains no more than 256 MiB of server capture files. Capture files have a `bfs-debug-v2` JSONL header, samples or event records, and exactly one terminal record when they finish cleanly. Every record includes a monotonic sequence, UTC timestamp, and elapsed monotonic time so a trace can be rejected when ordering is corrupted.

The server header identifies the mod, Minecraft, Forge, Java, GeckoLib, and SmartBrainLib versions. It also records GeckoLib's registered `geckolib:main` protocol version, the source revision or an explicit unavailable reason, artifact and configuration bindings, host role, side, tick rate, and units. `dedicated_server`, `integrated_server`, and `gametest_server` are distinct host roles. A GameTest run is server side evidence, but its `gametest_server` label must not be presented as a dedicated server or laptop client acceptance result.

## Movement route capture

For fish and sharks using the pitch route controller, movement samples include `routeAttemptId`, `routeState`, `desiredPitch`, `selectedWaypoint`, `remainingDistance`, `stalledTicks`, `bodyYaw` and `headYaw`. The controller reports `clearance`, `approach`, `path`, `arrived`, `cancelled` or `blocked` as applicable. A new attempt belongs to a changed destination, not each refreshed navigation node. A blocked attempt has a bounded retry delay; a different destination remains eligible immediately.

`poweredVelocityX`, `poweredVelocityY`, `poweredVelocityZ` and `scalarPropulsionSpeed` describe the controller's retained propulsion contribution. `externalVelocityX`, `externalVelocityY` and `externalVelocityZ` describe the remaining observed velocity. They do not identify a specific external source such as a current or knockback. Do not label unexplained residual velocity as a validated external force. Other controllers retain explicit unavailable fields where they do not expose this state.

For an arrival regression, capture the full starting position, intended destination, transition, final position and the next requested destination. Reaching the destination's height alone is not arrival. Compare actual position deltas with pitch and both yaw values. Check route retries, terrain contact and progress before requesting visual review. A headless pass still does not approve appearance or animation.

`pitchExitActive` identifies a translating level exit after a route stops. The original `routeState` retains its terminal reason. `pitchRate` records the controller's angular state in degrees per tick. `depthCurvature` is the signed planned vertical plane curvature in inverse blocks, and `routeSpeedCap` is the resulting scalar limit in blocks per tick, or `unbounded` when no route limit exists. Compare these fields with measured position and pitch deltas. An active exit is not proof that the full destination was reached, and a speed cap is not measured travel.

To investigate a nose held up without travel, capture at least 200 ticks through the active approach and its exit. Check forward displacement during pitch changes and continued movement while leveling, not just whether the pitch eventually changes. A physically obstructed exit must retain a safe pose rather than rotate against terrain. The dedicated depth entry and settling fixtures cover these cases without requiring a player connection.

## Fishing capture

Use `all` or `advancement` for fishing investigations. Fishing events do not require a selected animal or a connected owner. Ordinary players still fish normally; the capture is enabled only by the existing trusted command source. No separate fishing command or permission is added.

```text
/bfs debug on advancement 1200
/bfs debug status
```

Perform the failing cast and reel, allow at least one server tick for post-reel observation, then run `/bfs debug off`. For an actual player-input or appearance problem, use the matching packaged laptop client. For server delivery regressions, use the real rod GameTest fixture without asking a person to join.

Each hook UUID is an `attemptId`. The `fishing` event has two stages. `delivery` records the actual fishing callback, and `settled` observes the rod and removed hook at server tick end after the callback returns. Reentry and replay have separate delivery observations with the same attempt ID and cannot masquerade as new committed catches.

The allowlisted fields include effective replacement and entity-catch settings, source loot table and original item when the loot context is available, selected item/count/species, up to 16 observed item IDs/counts, mapping disposition, actual tool hand and enchantment IDs/levels, prior cancellation, insertion acceptance, delivery identity/kind, reel impulse in blocks per tick, requested and accepted XP, catch-statistic delta, and before/after Cod and Salmon advancement completion. Post-reel records include observed rod damage/count and hook cleanup. An advancement earned during the active delivery callback includes its catch attempt ID. Player identifiers are pseudonymous within the server capture; names and arbitrary item NBT are not included.

An event-boundary test or third-party caller may lack the original loot context. Such fields explicitly report `unavailable:`. An unsupported item is left on the vanilla item path and recorded as `unmodified_loot_passthrough`; its underlying junk, treasure or custom pool is not inferable reliably from the final item alone and is reported unavailable. This is not proof of its later vanilla delivery. Do not use these observations to claim an unobserved pool identity or successful passthrough insertion.

At most 32 hook settlements may be pending. Exceeding this bound stops the capture as incomplete without changing fishing. Several catches by one angler before settlement mark the rod observations ambiguous, because the eventual damage could include several uses. Stopping before settlement also marks the capture incomplete. Unbreaking, breakage and other tool behavior mean requested rod cost is not always equal to the measured durability change. Choose a fixture-specific expectation; never turn an unavailable or ambiguous value into zero.

Add a fishing section to the existing candidate manifest to request strict fishing validation:

```json
{
  "scenarioId": "phase-001-fishing-diagnostics",
  "requirementId": "BFS-REQ-009",
  "fishing": {
    "minimumAttempts": 5,
    "requiredOutcomes": ["committed", "previously_cancelled"],
    "expectedRodDamageDelta": 1
  }
}
```

Bind artifact/configuration fields as required for the actual candidate. The rod-damage expectation above is for an unbroken fixture rod without Unbreaking; omit it when that is not the scenario. The analyzer rejects absent attempts or settlement, duplicate commits, wrong live/item delivery, replaced vanilla fish leakage, failed catches with rewards, incorrect matching advancements, missing or nonfinite impulse, ambiguous rod evidence, and required outcomes that never occurred. Retain the ordinary JSONL completeness checks and current-candidate bindings. A passing delivery trace does not establish mouse input, rendering, natural spawning or restart persistence.

`BfsFishingDiagnosticGameTests` exercises real rod use with diagnostics off and on, four delivery modes, offhand tools and preceding cancellation. Its known Cod selection isolates observation and delivery from fishing randomness; the separate mode suite covers unmodified table selection. The capture is retained only until its bounded offline analysis and evidence collection finish, then cleaned with the owned runtime.

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

For the current fish and shark vertical profile, server movement captures must also be
checked against the selected class ratio and the matching pre-ceiling scalar speed.
Atlantic Cod and Atlantic Salmon fixtures are expected to remain within
`abs(powered vertical velocity) <= Vreference * 0.20`. Shark fixtures are expected to
remain within `abs(powered vertical velocity) <= Vreference * 0.25`. Each ratio applies
once to the accepted same-species, same-state scalar speed. The controller eases the
transition from its prior controlled value. Bottlenose Dolphin remains the qualitative
smoothness reference and is not included in either class cap assertion. A first tick
must not inherit the full unscaled vanilla vertical impulse.

Movement records also expose `speciesProfile`, `locomotionMode`, `behaviorAction`,
`scalarPropulsionSpeed`, `verticalTravelClass`, `verticalSpeedRatio`,
`verticalReferenceSpeed`, and `verticalSpeedCeiling` when the selected entity has a
registered species policy.
Shark actions use the authoritative shark state. SmartBrain aquatic animals use the
bounded policy action. These fields are observations, not acceptance thresholds by
themselves.

The first movement sample for each selected entity has no prior position or orientation sample. Its derived deltas, rates, and angular differences therefore use the explicit `unavailable:no_previous_sample` value. The analyzer accepts that reason only on the first sample for those derived fields. Later samples must contain finite values whenever the field is part of the selected candidate manifest.

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

Numeric upper limits use a bounded `1e-6` comparison tolerance to absorb floating point serialization roundoff at the declared boundary. The tolerance does not replace a candidate threshold or permit a measurable excursion beyond that bound.

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
