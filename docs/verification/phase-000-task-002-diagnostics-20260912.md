# Phase 000 task 002 diagnostic verification

Date: 2026-09-12

Status: in progress. The reusable diagnostic core is verified for the exercised paths below. The phase cursor remains at `P000-TASK-002` because target reload behavior and the paired p95 capture overhead gate still need their dedicated evidence.

## Source and host

The checks ran from `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix` on `node-1`, a headless Linux host. The Forge target is Minecraft 1.20.1, Forge 47.2.0, and Java 17. The implementation baseline was `c8b8818e0b742b366a22dda9517d3a6079232bbe`. The only pre-existing source change remains the uncommitted line ending difference in `build.gradle`.

## Deterministic checks

`python3 -B -m unittest tools/test_bfs_debug_analyze.py` passed all 18 parser tests.

`PYTHONPATH=tools/performance python3 -B -m unittest tools/performance/test_analyze_performance.py` passed all 21 performance analysis tests.

`JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 ./gradlew :forge:compileJava :forge:compileTestJava --no-daemon --console=plain` passed.

`JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 ./gradlew :forge:test --no-daemon --console=plain` passed all 52 Forge unit tests, including `BfsDebugCaptureFailureTest`.

The new writer tests construct bounded sessions and verify that an oversized record stops with `record_byte_limit`, queue saturation stops with `queue_limit`, and an unavailable output directory stops with `writer_failure`. Each case asserts the incomplete marker and preserved stop reason. The tests use temporary directories and remove them after each assertion.

## Server GameTest check

The isolated GameTest runtime used Java 17 and an exact disposable directory with `eula=true`. The first complete run finished with 85 required tests passed. It exercised the server debug command start and stop path, status and repeated stop behavior, permission denial, brain redaction, population samples, fishing capture and settlement limits, movement captures, and the paired physics parity fixture. The runtime, worlds, logs, and captures were removed after the result was recorded.

A later full rerun was started after a temporary lifecycle fixture was added. The temporary fixture itself emitted a removed target lifecycle record with `wasRemoved=true`, `removalReason=discarded`, and a complete footer, but the full run also exposed three unrelated existing flaky GameTests. That temporary fixture was removed from the source and its runtime was discarded. The retained acceptance result is therefore the earlier 85 test run, not the interrupted rerun.

## Acceptance matrix

| Check | Result | Evidence |
| --- | --- | --- |
| Console `on`, `status`, `off` | pass | server debug lifecycle and permission GameTests |
| Permission denial | pass | `serverDebugCommandKeepsOneSessionAndRejectsUntrustedSources` |
| Target absent or removed | pending | lifecycle writer path exists and the temporary fixture observed it, but no retained passing fixture is committed yet |
| Duration timeout | pass | population capture ends with `duration_elapsed` and a complete footer |
| Resource reload | pending | no isolated reload and resume evidence has been retained |
| Queue and record overflow | pass | `BfsDebugCaptureFailureTest` |
| Writer failure | pass | `BfsDebugCaptureFailureTest` |
| Redaction | pass | brain and fishing diagnostic GameTests reject debug strings, player UUIDs, names, and private rod data |
| Complete footer | pass | parser tests and server diagnostic captures require one terminal record |
| Off and on gameplay parity | pass | `serverDebugCaptureLeavesPairedPhysicsUnchanged` |
| p95 capture overhead | pending | the current manager records elapsed trace time but does not yet provide the paired off and on overhead report required by the phase gate |

No performance candidate was profiled and no phase transition was attempted. The next action is to add the retained reload and lifecycle fixtures, then measure paired off and on p95 overhead before starting P000 task 003.
