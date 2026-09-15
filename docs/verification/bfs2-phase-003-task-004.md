# BFS2 Phase 003 Task 004 evidence

This task records the phase runtime and recovery gate for disturbance producers and Great White
boat interest. The source candidate is `866aeb7f47d06c92508aac295798c1784b914ea7`, the signed
branch tip on `envy/bfs2-phase-003`. It targets Minecraft 1.20.1 with Forge 47.2.0, Java 17, and
the checked in Gradle wrapper.

The focused server and parser checks passed.

* `./gradlew :forge:test --no-daemon` passed the Java unit suite, including the Great White boat
  route helpers and bounded prediction checks.
* `./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestNamespaces=bfsdisturbance`
  passed all seven disturbance tests. Coverage includes real water entry and jump transitions,
  occupied moving boat throttling, a safe behind route, combat priority, dismount and stationary
  release, and an obstructed route.
* `python3 -m unittest tools.test_bfs_debug_analyze -v` passed all 24 parser tests.
* `git diff --check` passed before this evidence update.

An exploratory all namespace run was not accepted as phase evidence. The existing
`bfsgametests.tigercuriosityfleepreemptionclearsinvestigation` test failed with
`flee preemption must clear curiosity state`, and
`bfsmovementoraclegametests.allspeciesmovementoracle` failed because the Shortfin Mako powered
vector did not match the expected value. The run then stopped when the existing
`gameteststructures/bfsfollowthirdpartygametests.empty.snbt` fixture was unavailable. These are
outside the disturbance implementation and remain visible for their owning work. No claim of a
full namespace pass is made.

## Packaged split host runtime

The exact Forge artifact was built from the source candidate and copied to the disposable server
and laptop instances.

* Forge artifact SHA 256: `917b9992f74f61fa4297b9888a76267d48c40ab6642358a3f9c6125bd5feb3bf`.
* Forge artifact SHA 512: `ecd0b3e17eedd2513be9bab00a33c988047fb9c7d825c3d926e8252fafdaa71651120af560d230390e136a989eeffdbc353d7f066c91d471ee1d54a79dca42c3`.
* GeckoLib SHA 256: `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0`.
* SmartBrainLib SHA 256: `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b`.
* Forge installer SHA 256: `bc2a0f7b161a2d8284df3d603f7f2b22313b246f026ad77511cbd35bcd01caac`.
* The node-1 server used `/tmp/bfsm-bfs2-task004-20260915`, Java 17.0.19, and the private
  endpoint `100.76.164.109:25990`. The server joined `EnVyOnMyMind` with the expected UUID and
  logged the client disconnect during teardown. Its final log SHA 256 was
  `575d9e59065e5057088487ae4ba1fdf46813f725feec654810ab141ba657678d`.
* The laptop used the disposable PrismLauncher profile
  `bfsm-bfs2-task004-20260915`, Java 17.0.15, an NVIDIA GeForce RTX 5090 Laptop GPU, and the
  matching Forge 47.2.0 client. The owned window was PID 358094, stable ID `18000354`, class
  `Minecraft* 1.20.1`, title `Minecraft Forge* 1.20.1 - Multiplayer (3rd-party Server)`, on
  `special:chatgpt`. The client log SHA 256 was
  `4c2a82b9a5b01cbd77712532cf83ce2c9887212d1b4b12b0212c0df023cd2a8c` and records the direct
  connection to the verified endpoint and a modded server handshake.
* The Java stream for PID 358094 was PipeWire node 148. Its process identity and output target
  were checked before input, and `wpctl get-volume 148` reported `Volume: 1.00 [MUTED]`. After
  teardown the stream and owned client processes were absent. The active Hyprland workspace was
  `3` before and after capture.

The client performed the residual physical actions. The player mounted the boat through a real
right click, then held forward for approximately five seconds. The boat moved from Z 0.5 to
16.3125. At the end of the capture the Great White was at X 1.9621, Y 59.8863, Z 9.1642 while
the boat was at X 0.5, Y 60.5233, Z 16.3125. The body remained below the water surface and the
shark stayed behind the boat. A physical shift input then removed the `RootVehicle` relation and
left the player at Y 61.0858. The mounted client image was captured at
`/tmp/bfsm-bfs2-task004-mounted.png`, SHA 256
`fe8e37d5a3bd63fcc093e467f76d11b07a219f4d4e1d379e38416e03b03aa565`.

The player was then placed in the water and used physical space and forward input. The bounded
disturbance capture contained seven `water_entry` source records and seven decisions, with a
complete footer and zero dropped records. The boat capture contained five `occupied_boat` source
records and 21 decisions, also with a complete footer and zero dropped records. The parser was
run against both captures with explicit candidate manifests.

* Boat capture `2f6b5ce6-b37a-484b-8116-4572a385738c` returned `complete`, 28 records, five
  source records, 21 decisions, and no errors or warnings for `BFS2-REQ-012`.
* Water capture `c0d44d74-6112-4a92-8fc1-3775e8b2c4cc` returned `complete`, 16 records, seven
  source records, seven decisions, and no errors or warnings for `BFS2-REQ-011`.

The exact disposable server was stopped after the capture. Forge reported a
`ConcurrentModificationException` while its normal distance manager shutdown was unwinding; the
owned `run.sh` and Java PIDs were then terminated explicitly and verified absent. This shutdown
exception is retained as a runtime residual for the integration audit. The laptop client and its
PipeWire stream exited cleanly. The disposable profile, server runtime, raw captures, parser
outputs, and temporary screenshots are cleanup targets; the protected `forge/logs/` and
`performance-matrix-20260912-interval50/` directories remain untouched.

The phase gate remains open pending the integration audit, independent review, merge, resulting
default branch verification, and signed phase tag. The all namespace exploratory failures and the
server shutdown residual must remain visible in that audit.
