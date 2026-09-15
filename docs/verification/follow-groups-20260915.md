# Follow groups and feedback verification

This is server verification for BFS2-REQ-008 and BFS2-REQ-009 in Phase 002. The phase remains open. The saved goal, plan set and phase cursor are unchanged by this implementation.

The original packet below is retained as historical evidence. The superseding rerun at the end of this document binds the boss adapter and shutdown correction to source commit `76f5982` and the rebuilt Forge artifact.

## Source and candidate

* Source commit: `4d3c4bf063618b5b16cf5c1430332ec47da60890` on `envy/bfs2-phase-002`.
* Target: Minecraft 1.20.1, Forge 47.2.0, Java 17.0.19 and SmartBrainLib 1.14.2.
* Candidate: `forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar`, 2,164,376 bytes and 984 archive entries.
* SHA 256: `bfd23944b647f5cc0d2b5edbd5c1c92ba2d19055189c9e443c739e99914f50f0`.
* SHA 512: `111c143143c2be2d2b2ad9a5b5e7bac5a70f362845305bc19d177221e0a853470bf484c2f4194cbd2f7a481ed1cec6fcbb4298e9a00e141b002c8195318ffb5c`.

The candidate was installed in a disposable packaged client and server pair for the live interaction check. It has not been integrated into `1.20.1` or published as a release.

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
| Focused follow GameTests | All 33 required tests passed, including the 321-member scheduler witness |
| Combined follow and ordinary regressions | All 123 required tests passed, including the population soak |
| Parser unit tests | All 21 tests passed |
| Actual follow capture | 12 records, complete verdict, no parser errors |
| Forge build and archive checks | Passed, including `unzip -tqq` and archive content inspection |
| Diff and credential checks | Passed, including the tracked text scan |
| Final server shutdown | All dimensions saved and the process exited cleanly |

The follow fixtures cover a moving 20 member group, a second owner movement without reclaiming, individual release of every member, 33 simultaneous memberships, a 321-member scheduler witness, paging, ownership conflict, reissue, held input deduplication, two feedback channels, retention beyond 2,400 ticks, marker and permission loss, slime navigation, shark route ownership against prey, controller restoration, and native fire escape. The scheduler witness observed at least one route evaluation for every member and a peak of no more than 32 route evaluations in one server tick.

The real capture came from `followCaptureWritesPrivateGroupTransitions`. Its SHA 256 is `6cd10acda68157a6fbcd62b2ee0cfecbeb929bd8804b7f53e89660ad563750da`. The independent parser verified its closing record and monotonic tick order. Raw captures were removed after validation.

The disposable runtime paths were `forge/run/follow-group-20260915-gametest`, `forge/run/follow-scheduler-20260915` and `forge/run/follow-group-20260915-data` under `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix`. The server properties SHA 256 was `be1674eb4e13ae49fdc2904b00261af3022876a7f11051e401ccca71a2c94912` for the combined run. The exact runtimes accepted `eula=true` before launch. Fixtures and relative coordinates are defined in `BfsFollowGameTests.java` and its 40 by 24 by 40 empty structure.

The verified combined command was:

```bash
./gradlew :forge:GameTestServer -PbfsGameTestNamespaces=bfsfollow,bensfintasticsharks -PbfsGameTestRunDir=/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix/forge/run/follow-group-20260915-gametest --no-daemon
```

## Earlier failures and remaining gates

Earlier runs exposed incorrect operator levels in the test harness, an unsettled navigation fixture, a vulnerable long duration fixture, the slime jump conflict, competing shark routes, an unregistered panic memory lookup, a missing capture footer and a stale terminal tick. These were repaired and covered by the final run.

One earlier combined run rejected the 20 member progress assertion. Later focused and combined runs passed. The original assertion lacked sufficient per member state to isolate that failure, so it now reports position, distance, route and group state. This does not close the remaining group stress gate. An earlier focused run also raised `ConcurrentModificationException` in `DistanceManager` during shutdown. The final focused and combined runs shut down cleanly; no cause is claimed for that earlier shutdown observation.

The complete controller and lifecycle matrix, boss locomotion, phase review, integration and signed phase tag remain open. Generic claims in older compatibility records do not prove those locomotion cases. No phase or full goal completion is claimed.

## Packaged client verification

The exact candidate was run on the `envision` laptop against a disposable production Forge server at `100.76.164.109:25870`. The host reported an NVIDIA GeForce RTX 5090 Laptop GPU, Java 17.0.15, Forge 47.2.0 and the Xwayland Minecraft window at PID `3265310`. The client used the pinned Alex's Mobs, Citadel, SmartBrainLib and GeckoLib dependencies recorded in the phase manifest. The client master volume was `0.0`, and the Java playback stream for that PID reported `Mute: yes`.

The real right click path produced visible chat feedback for each state. The operator selected `follow cow alpha [1]`, received waiting and resumption messages, received a blocked route pause, selected `follow cow gamma [2]` while alpha remained selected, and then right clicked gamma again. The release message reported `Released: follow cow gamma [2]. You clicked this mob again. 1 mob selected.` The server status immediately afterward showed alpha still selected and following. This verifies independent group membership and individual release at the same distance without the old move farther away rejection.

A second production client run used the same candidate against `100.76.164.109:25871` with Alex's Mobs `1.22.9` and Citadel `2.6.0`. The actual right click selected a pinned `alexsmobs:grizzly_bear`, then emitted `Paused`, `Following again`, and `Waiting nearby` while the server restored the bear's normal AI after setup and moved it from x `108` to x `101.29355298412146` toward the owner at x `96`. A second deliberate right click emitted `Released: external grizzly [1]. You clicked this mob again. 0 mobs selected.` This proves the pinned third party path keeps a member selected through waiting and releases only the clicked mob without a distance based reselection rule. The development GameTest path was not used for this external namespace because Citadel's mapped mixin does not load on the Forge userdev classpath; the production Forge runtime was used for the actual client interaction.

## Cleanup

Both owned runtimes and their worlds, captures, logs and generated configuration were removed and verified absent after their last consumer. The disposable Prism client copy, screenshots and installer were removed after the live evidence was saved. All owned runtime, launcher and client processes exited. Seventy seven newly created test build files were removed. Preexisting build files, shared dependency caches, protected untracked data and the owner Prism instance were preserved. The owner instance jar still matched `f0d839239c7a9b0efc68a83fb9837b09b75dad3c1d05f696ba413158d7cc02ef` and its master volume remained `0.0`.

The laptop check used only the disposable instance and the existing Hyprland session. No personal instance, default audio sink or unrelated stream was changed.

See the [follow support guide](../test/debug-diagnostics.md) for current commands and interpretation.

## Superseding boss adapter and shutdown rerun

This rerun extends the follow regression packet without changing the phase contract. Source commit `6f81eed` adds a vanilla Ender Dragon phase adapter and defers passenger cleanup from `EntityLeaveLevelEvent` so distance tracking is not modified during its iteration. The adapter uses the dragon's charge phase target and restores the phase captured at selection when the lease is released.

The disposable server runtime was `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix/forge/run/follow-boss-adapter-20260915-v4`. The 34 required `bfsfollow` GameTests passed, including the Ender Dragon head to parent resolution, Wither navigation, independent release of each boss, dragon phase restoration, and the 321 member scheduler witness. The boss fixture made both bosses invulnerable and silent so combat behavior could not remove the movement subjects while the follow adapter was under test. The server saved all dimensions and exited without the earlier `DistanceManager` shutdown exception.

The rebuilt candidate was `forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar`, with SHA 256 `146732dd96304be47d4f0061b8fc3ffc71198cbce750dda2322fafc5bfe85a0a` and SHA 512 `110ff999ed4643f84754e663c85fd6306d7d7d9092694dda12897e72e63c934637ffa9e8abf00e82f9e291212d607319c562f0d400e12f0a8060fd8cd5efea28`. `unzip -tqq` passed. The exact runtime was removed and verified absent after the log was inspected. The phase remains open for its remaining controller matrix, review, integration merge, and signed tag gates.

A final docs bound build at source commit `db3a589` passed the compile, unit, data, build, and archive checks. Its Forge testing JAR is SHA 256 `b74d864d23fc5cc34213e8ea2bfd1ccdfe900e3481f5b7911b88cb1c32603a65` and SHA 512 `129a0a75d492f11a010da7889f7ecb57c80b37145453b85d0219999c49a9d80b566adecec13aed8ced606275ce2def0fe267a1ef268582da5bccb72a2f148def`. This build check started no runtime; the earlier hash remains the boss runtime evidence identity.

## Current candidate feedback gate

On September 15, 2026, the exact candidate from source commit `c5fc98172dfa8d2d4e062e9c23a10c6effee9c4f` and artifact SHA 256 `b74d864d23fc5cc34213e8ea2bfd1ccdfe900e3481f5b7911b88cb1c32603a65` was installed in the disposable Prism instance `bfsm-p002-restart-gate-20260915c`. It joined the dedicated server at `100.76.164.109:25951` in world `bfs-p002-restart-gate-c`. The laptop renderer was an NVIDIA GeForce RTX 5090 Laptop GPU, the client Java runtime was 17.0.15, and the Java playback stream for client PID `3787793` was correlated to PipeWire node `148` and verified muted.

The first click with an unissued raw item produced the expected invalid-marker feedback. Running `/bfs debug followme` issued a fresh server marker. A real Xwayland right click then produced both chat and action bar feedback for `Selected: restart cow c [1]. 1 mob selected.` After the mob reached the owner, both channels reported `Arrived nearby: restart cow c [1]. 1 mob still selected. Click this mob again to release only it.` A second real right click produced `Released: restart cow c [1]. You clicked this mob again. 0 mobs selected.` A server status query immediately after selection reported one selected and one waiting member. This confirms arrival does not remove membership and deliberate repeat clicking releases only the clicked mob. No distance based move farther rejection occurred.

The exact disposable server, client, screenshots and launcher copy were test owned and have been removed after this evidence was retained. This gate closes the current candidate right click and paired feedback check. Restart marker invalidation, independent review, pull request integration, resulting default verification and the signed phase tag remain open.
