# Phase 000 Test Runbook

This runbook is the repeatable entry procedure for the Forge 1.20.1 0.24 evidence harness.

## Preparation

Use Java 17 and the checked in Gradle Wrapper. Preserve the uncommitted `build.gradle` line ending change and the complete owner supplied `Content/` directory. Do not commit development run directories, logs, worlds, or local configuration.

From the repository root, verify the branch and default branch:

```bash
git status --short --branch
git ls-remote --symref origin HEAD
git branch -vv
```

The stable default branch is `1.20.1` and the 0.24 work branch is `envy/0.24`.

For phase execution, use a separate worktree and a branch such as `envy/0.24-phase-000` created from the verified canonical `origin/1.20.1` tip when that tip is newer than `origin/envy/0.24`. Do not create or rename `main` or `master`, and do not stage the protected `build.gradle`, generated `.cache`, or `Content/` paths.

## Host allocation

`node-1` is a headless build and server host. It may run Gradle compilation, deterministic verification, data generation, packaging, and dedicated server tasks. Do not start `./gradlew :forge:Client`, Xvfb, windowed GameTests, or any other game process that requires a window on `node-1`.

Run client rendering, interactive visual review, and windowed GameTest sessions on EnVy’s Linux laptop. When a client needs a server, start the dedicated server on `node-1` with an unused port and connect from the laptop. Record the host, port, client display, and connection path in the evidence record. Keep the disposable EULA target on `node-1` limited to the owner authorized server session.

## Deterministic checks

Run these checks in order after a clean source or resource change:

```bash
./gradlew :forge:compileJava :forge:compileTestJava
./gradlew :forge:test
./gradlew :forge:Data
./gradlew :forge:test
./gradlew :forge:build
```

The second test run verifies the generated resources produced by data generation. Review `git diff --check` and the complete generated resource diff after the data task.

`ReleaseContractAuditTest.fishAndWhitetipClipsHaveDistinctFivePointTransformSamples` is the structural animation gate. It samples a keyframed transform vector at the start, quarter, half, three quarter, and end of every required Cod, Salmon, and Oceanic Whitetip clip. At least one authored bone channel per clip must contain two vectors that differ by more than `0.0001`. This proves authored transform motion only. It does not replace the required laptop presentation capture or controller-state trace.

Run the server side GameTest harness after data generation and before packaging:

```bash
JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 \
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks \
  -PbfsGameTestRunDir=<disposable-runtime>
```

Set `eula=true` in the exact disposable runtime before launch and read it back. The `bfsGameTestRunDir` property keeps the harness away from `forge/run` and every personal world. The task copies the empty fixture into the isolated runtime, starts a clean Forge GameTest server, and requires every registered test to pass. The current harness verifies algae placement retains a full water fluid state, has no collision, survives with water support, and can be replaced with water cleanly. It also verifies the bounded server `/bfs debug` command path, Tiger Shark edible item pursuit, delayed bite damage with cooldown and target loss recovery, Oceanic Whitetip grab damage and timed passenger release, and shark vertical routes against the Bottlenose Dolphin reference for both ascent and descent without circular orbiting. Stop the server, verify its exit, preserve only sanitized required evidence, and remove the exact disposable runtime after its final consumer completes.

## Runtime checks

Run `./gradlew :forge:Server` with an unused development port and record the complete startup log. Use `-PbfsServerRunDir=<disposable-runtime>` so the dedicated server does not write to `forge/run`. Confirm that the mod loads on the dedicated server without a client class, registry, or data pack error. The exact disposable runtime must contain and verify `eula=true`. Never change a persistent or protected-worktree EULA. If the setting remains `eula=false`, record the server gate as blocked rather than treating the process exit as a pass. Use a fixed seed for world probes and retain the seed, coordinates, biome, configuration, and command transcript in the phase evidence record.

Run `./gradlew :forge:Client` only on EnVy's Linux laptop when client evidence is required. Capture labeled clips for the fish animation states, Oceanic Whitetip grab and release, Tiger Shark movement and curiosity, Shark Spotter, and Prismarine armor. Review the client log for missing models, textures, bones, animation clips, and render layer warnings. Never substitute a headless or software rendered client for laptop visual evidence.

Use the server and local client diagnostics in [BFS Debug Diagnostics](debug-diagnostics.md) before requesting an interactive connection. Analyze a completed server capture first. Start the laptop client only when rendering, UI, input, or client synchronization is the remaining question.

For interactive evidence that needs a server, use the dedicated-server path. Start the Forge server on `node-1` with an unused port and an exact `-PbfsServerRunDir=<disposable-runtime>` directory, then keep all server setup command driven. Launch the matching laptop client only through an authenticated test profile and connect it to the verified private endpoint. Keep server authentication enabled. Never bypass login validation or weaken authentication to obtain visual evidence. Use the server console or an authenticated RCON session for gamerules, fixture blocks, teleports, entity labels, and cleanup. Record the server ready marker, join and handshake lines from both logs, the server and client revisions, the client GPU, the endpoint, and every capture hash. Keep the Minecraft window on a nonactive workspace with a no-pause-on-focus-loss client option when hidden capture is needed, and verify that the laptop’s active workspace is unchanged before and after each capture. This replaces singleplayer setup for server-backed interactive checks and keeps windowed game processes off `node-1`.

The earlier verified run used phase revision `725bab666d38cd3f05e8e618f02fc96a6b84d7c7`, node 1 world `phase000-mp-final-2`, server port `25578`, RCON port `25579`, and the laptop endpoint `100.76.164.109:25578`. It is historical transport and controlled-state evidence only. For a new hidden-window capture, identify the exact owned `hyprctl clients -j` row, keep the active workspace unchanged, and use `grim -T <stableId> <capture.png>`. Do not use `DISPLAY=:1 import -window` because it captures the active desktop instead of the inactive Xwayland client.

## Recovery checks

For every temporary state, repeat the negative path and then the positive path. Required examples include an unreachable item, a nonfood item, a removed item, a target acquired during curiosity, an expired grab, a disconnected passenger, an obstructed spyglass ray, Deep Cold Ocean, replacement disabled, and a preexisting population above the ordinary cap. A recovery is valid only when ordinary navigation and state resume without a restart workaround unless the configuration contract requires a restart.

Do not delete excess fish from an upgrade world. Back up the world, install the corrected build, restart when the configuration requires it, and observe inherited despawn while new spawn attempts obey the corrected population rules.
