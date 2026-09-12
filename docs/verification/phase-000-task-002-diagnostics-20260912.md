# Phase 000 task 002 diagnostic verification

Date: 2026-09-12

Status: complete for `P000-TASK-002`. The phase cursor remains at `P000-TASK-001` because the overall Phase 000 gate is not complete. `P000-TASK-003` is the next unfinished task after this diagnostic work.

## Source and host

The checks ran from `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix` on `node-1`, a headless Linux host. The Forge target is Minecraft 1.20.1, Forge 47.2.0, and Java 17. The diagnostic completion implementation is recorded in signed commit `5b5f23b`. The only pre-existing source change remains the uncommitted line ending difference in `build.gradle`.

## Deterministic checks

`python3 -B -m unittest tools/test_bfs_debug_analyze.py` passed all 18 parser tests.

`PYTHONPATH=tools/performance python3 -B -m unittest tools/performance/test_analyze_performance.py` passed all 21 performance analysis tests.

`JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 ./gradlew :forge:compileJava :forge:compileTestJava --no-daemon --console=plain` passed.

`JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 ./gradlew :forge:test --no-daemon --console=plain` passed all 52 Forge unit tests, including `BfsDebugCaptureFailureTest`.

The new writer tests construct bounded sessions and verify that an oversized record stops with `record_byte_limit`, queue saturation stops with `queue_limit`, and an unavailable output directory stops with `writer_failure`. Each case asserts the incomplete marker and preserved stop reason. The tests use temporary directories and remove them after each assertion.

## Server GameTest check

The isolated GameTest runtime used Java 17 and an exact disposable directory with `eula=true`. The final complete run passed all 87 required tests. It exercised the server debug command start and stop path, status and repeated stop behavior, permission denial, brain redaction, population samples, fishing capture and settlement limits, movement captures, removed target lifecycle records, resource reload continuity, capture overhead telemetry, and the paired physics parity fixture. The runtime, worlds, logs, and captures were removed after the result was recorded.

## Acceptance matrix

| Check | Result | Evidence |
| --- | --- | --- |
| Console `on`, `status`, `off` | pass | server debug lifecycle and permission GameTests |
| Permission denial | pass | `serverDebugCommandKeepsOneSessionAndRejectsUntrustedSources` |
| Target absent or removed | pass | `serverDebugCaptureRecordsRemovedTargetLifecycle` records `wasRemoved=true`, `removalReason=discarded`, and a complete footer |
| Duration timeout | pass | population capture ends with `duration_elapsed` and a complete footer |
| Resource reload | pass | `serverDebugCaptureSurvivesResourceReload` runs the real `reload` command, keeps the session active, and verifies movement records plus a complete footer |
| Queue and record overflow | pass | `BfsDebugCaptureFailureTest` |
| Writer failure | pass | `BfsDebugCaptureFailureTest` |
| Redaction | pass | brain and fishing diagnostic GameTests reject debug strings, player UUIDs, names, and private rod data |
| Complete footer | pass | parser tests and server diagnostic captures require one terminal record |
| Off and on gameplay parity | pass | `serverDebugCaptureLeavesPairedPhysicsUnchanged` |
| p95 capture overhead | pass | the retained report measured 40 enabled samples at p95 `103532` ns, or `0.103532` ms, with zero disabled capture samples. The result is below the absolute `0.25` ms allowance. |

No performance candidate was profiled and no phase transition was attempted. `P000-TASK-003` performance profiling is the next unfinished action.
