# BFS2 Phase 003 Task 001 Evidence

Status: complete for P003-TASK-001 headless scope.

Candidate source commit: `03c26baada739df7acb23a52910b5ce6785af521`.

The disturbance producer path now emits typed `swim_sprint`, `attack`, `damage`, `block_break`, `fall`, `projectile`, `water_entry`, `water_jump`, and `occupied_boat` events. Water entry and upward water exit use tracked server transitions. Occupied boat events require a dry rider, actual horizontal movement of at least 0.02 blocks per tick, and a ten server tick interval. Throttle keys include dimension, source UUID, and source kind. Expiry, entity removal, and level unload prune source state. The producer retains at most 4096 throttle keys per level. The handler inspects at most 64 eligible sharks per event.

Diagnostic records use pseudonymous entity identities. Source records are separated from decision and throttle records. Decision records include candidate count, source key count, boat correlation, threshold result, outcome, and reason. The parser rejects unknown source kinds, raw UUIDs, invalid strength, nonfinite positions, missing bounded counters, and incomplete terminal records.

## Verification

The following checks passed against the candidate commit.

* `./gradlew :forge:compileJava :forge:compileTestJava --no-daemon`
* `./gradlew :forge:test --no-daemon`
* `python3 -m unittest tools.test_bfs_debug_analyze -v`, 24 tests passed.
* `./gradlew :forge:GameTestServer -PbfsGameTestNamespaces=bfsdisturbance -PbfsGameTestRunDir=forge/run/bfs3-disturbance-20260915h --no-daemon`, two required tests passed.
* `./gradlew :forge:build --no-daemon`, build successful.
* The Forge jar passed `unzip -tqq` and contains the disturbance GameTest structure and compiled producer and diagnostic classes.

The focused GameTests prove actual air to water entry, upward water exit, occupied boat identity, dry rider identity, normalized strength, repeated movement, and ten tick event spacing. The test runtime used `eula=true`, ran headlessly, shut down cleanly, and was removed after log inspection. No client or rendered fin claim is part of this task evidence.

Testing artifact from the candidate commit: `forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar`.

SHA 256: `eac6782d5500cac0c3fa4d7823c99ecbe01a78b3501795b5991fbf9159262ece`.

SHA 512: `f3e7d0999d18c9242a276e398ff766331092abc8bfbac235232154f608d5e74ab142f948b72b302aa8431d52e674bfc6304206e93a2f17adc6b6fa7053a7e63a`.

Cleanup status: complete. The exact disposable GameTest runtime and its generated world, logs, configuration, and crash output were removed. Protected preexisting `forge/logs/` and `performance-matrix-20260912-interval50/` paths were preserved.
