# Follow groups and feedback verification

This is server verification for BFS2-REQ-008 and BFS2-REQ-009 in Phase 002. The phase remains open. The saved goal, plan set and phase cursor are unchanged by this implementation.

## Source and candidate

* Source commit: `3ffa91d610447b22852921f5b310814df3a44fd6` on `envy/bfs2-phase-002`.
* Target: Minecraft 1.20.1, Forge 47.2.0, Java 17.0.19 and SmartBrainLib 1.14.2.
* Candidate: `forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar`, 2,162,230 bytes and 983 archive entries.
* SHA 256: `99cf56f133a023ec6a217d27aa6e716f522a6b12aa590b9c318edc31c40f6b16`.
* SHA 512: `9108006373c2ca22cba7de227f16d0cb5f834a5055771bbc406e91bf3b9cdada358852cf302628c9cc2ea6f46a59deb92f7c166c2b2e7f69bf625ea461e899aa`.

The candidate is retained for the next matching packaged runtime checks. It has not been installed or visually accepted on the laptop, integrated into `1.20.1`, or published as a release.

## Implemented behavior

Each operator owns an independent group, and each mob has at most one owner. There is no arbitrary membership cap or duration expiry. A fresh right click toggles only that mob. Holding the button and duplicate Forge callbacks do not toggle it repeatedly. The stick uses the normal item use and release protocol while preserving its registered identifier.

Arrival retains the selection in a waiting state. Walking outside the arrival band resumes following. An unheld marker, excessive range or blocked route retains membership while movement is paused. Normal route work uses a rotating queue, a minimum ten tick interval per member and at most 32 evaluations per server tick. Blocked retries use a twenty tick interval. Commands expose group counts, ten members per page, targeted stop and group stop. Reissuing a stick keeps the group while rotating its issuance authorization.

Localized chat and action bar receipts distinguish selection, waiting, pause, resumption, release and rejection. Permission and marker validation remain authoritative on the server. Revoking issuance releases the affected group within one tick. Unrelated item interactions stay untouched.

Slimes retain their native jump controller and receive direction from their navigation path. SmartBrainLib follow ownership guards ordinary route writers and temporarily wraps competing activities. Sensors, memory expiry and core safety tasks continue ticking. Release restores the same original behavior instances and priorities. A selected animal can yield to its native fire escape goal and resume afterward. The manager does not teleport, discard or relocate mobs into fixtures.

Follow diagnostics carry version two group counts, revision and state. Pseudonyms do not contain raw UUID text. Closing a capture reschedules an unwritten terminal record when needed, and its terminal tick includes event callbacks that occur after the latest periodic sample.

## Verification

The final combined run finished on September 15, 2026 UTC. It used the actual `forgegametestserveruserdev` server launch target on `node-1`. No client or renderer was started on that host.

| Check | Result |
|---|---|
| Java compilation and unit tests | 61 tests in 14 suites passed, with no failures, errors or skips |
| Data generation | Passed, with zero resources rewritten on the final generation |
| Focused follow GameTests | All 32 required tests passed |
| Combined follow and ordinary regressions | All 123 required tests passed, including the population soak |
| Parser unit tests | All 21 tests passed |
| Actual follow capture | 12 records, complete verdict, no parser errors |
| Forge build and archive checks | Passed, including `unzip -tqq` and archive content inspection |
| Diff and credential checks | Passed, including the tracked text scan |
| Final server shutdown | All dimensions saved and the process exited cleanly |

The follow fixtures cover a moving 20 member group, a second owner movement without reclaiming, individual release of every member, 33 simultaneous memberships, paging, ownership conflict, reissue, held input deduplication, two feedback channels, retention beyond 2,400 ticks, marker and permission loss, slime navigation, shark route ownership against prey, controller restoration, and native fire escape. These are server fixtures. They do not prove a real client press or visible network delivery.

The real capture came from `followCaptureWritesPrivateGroupTransitions`. Its SHA 256 is `6cd10acda68157a6fbcd62b2ee0cfecbeb929bd8804b7f53e89660ad563750da`. The independent parser verified its closing record and monotonic tick order. Raw captures were removed after validation.

The disposable runtime paths were `forge/run/follow-group-20260915-gametest` and `forge/run/follow-group-20260915-data` under `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix`. The server properties SHA 256 was `be1674eb4e13ae49fdc2904b00261af3022876a7f11051e401ccca71a2c94912`. The exact runtime accepted `eula=true` before launch. Fixtures and relative coordinates are defined in `BfsFollowGameTests.java` and its 40 by 24 by 40 empty structure.

The verified combined command was:

```bash
./gradlew :forge:GameTestServer -PbfsGameTestNamespaces=bfsfollow,bensfintasticsharks -PbfsGameTestRunDir=/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix/forge/run/follow-group-20260915-gametest --no-daemon
```

## Earlier failures and remaining gates

Earlier runs exposed incorrect operator levels in the test harness, an unsettled navigation fixture, a vulnerable long duration fixture, the slime jump conflict, competing shark routes, an unregistered panic memory lookup, a missing capture footer and a stale terminal tick. These were repaired and covered by the final run.

One earlier combined run rejected the 20 member progress assertion. Later focused and combined runs passed. The original assertion lacked sufficient per member state to isolate that failure, so it now reports position, distance, route and group state. This does not close the remaining group stress gate. An earlier focused run also raised `ConcurrentModificationException` in `DistanceManager` during shutdown. The final focused and combined runs shut down cleanly; no cause is claimed for that earlier shutdown observation.

The 321 member scheduler witness, complete controller and lifecycle matrix, actual pinned third party fixture, boss locomotion, exact laptop input and visible feedback, packaged server and client acceptance, phase review, integration and signed phase tag remain open. Generic claims in older compatibility records do not prove those locomotion cases. No phase or full goal completion is claimed.

## Cleanup

Both owned runtimes and their worlds, captures, logs and generated configuration were removed and verified absent after their last consumer. All owned runtime and build processes exited. Seventy seven newly created test build files were removed. Preexisting build files, shared dependency caches, protected untracked data and the candidate JAR were preserved. The private audit scratch files were removed after this sanitized evidence was saved.

The laptop check was read only. Its existing Hyprland session and NVIDIA GeForce RTX 5090 Laptop GPU were reachable, and it had no Minecraft window. No laptop runtime, audio stream or screenshot was created or changed by this verification.

See the [follow support guide](../test/debug-diagnostics.md) for current commands and interpretation.
