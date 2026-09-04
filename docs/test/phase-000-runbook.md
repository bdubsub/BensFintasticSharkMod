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

Run the server side GameTest harness after data generation and before packaging:

```bash
./gradlew :forge:GameTestServer
```

The task copies the empty fixture into the development run directory, starts a clean Forge GameTest server, and requires every registered test to pass. The current harness verifies algae placement retains a full water fluid state, has no collision, survives with water support, and can be replaced with water cleanly. It also verifies Tiger Shark edible item pursuit, delayed bite damage with cooldown and target loss recovery, Oceanic Whitetip grab damage and timed passenger release, and shark vertical routes against the Bottlenose Dolphin reference for both ascent and descent without circular orbiting.

## Runtime checks

Run `./gradlew :forge:Server` with an unused development port and record the complete startup log. Confirm that the mod loads on the dedicated server without a client class, registry, or data pack error. The disposable run must have an explicit `eula=true` only after the owner authorizes accepting the Minecraft EULA. Never change the EULA setting silently or in the protected worktree. If the setting remains `eula=false`, record the server gate as blocked rather than treating the process exit as a pass. Use a fixed seed for world probes and retain the seed, coordinates, biome, configuration, and command transcript in the phase evidence record.

Run `./gradlew :forge:Client` with the supported headless display setup when client evidence is required. Capture labeled clips for the fish animation states, Oceanic Whitetip grab and release, Tiger Shark movement and curiosity, Shark Spotter, and Prismarine armor. Review the client log for missing models, textures, bones, animation clips, and render layer warnings.

For interactive evidence that needs a server, use the dedicated-server path. Start the Forge server on `node-1` with an unused port and keep all server setup command driven. For example, use `./gradlew :forge:Server --no-daemon --args='--port 25578 --nogui'` on the server and launch the client only on the Linux laptop with `./gradlew :forge:Client --no-daemon --args='--username Dev7 --quickPlayMultiplayer <tailscale-server-ip>:25578'`. Use the server console or an authenticated RCON session for gamerules, fixture blocks, teleports, entity labels, and cleanup. Record the server ready marker, join and handshake lines from both logs, the server and client revisions, the client GPU, the endpoint, and every capture hash. Keep the Minecraft window on a nonactive workspace with a no-pause-on-focus-loss client option when hidden capture is needed, and verify that the laptop’s active workspace is unchanged before and after each capture. This replaces singleplayer setup for server-backed interactive checks and keeps windowed game processes off `node-1`.

## Recovery checks

For every temporary state, repeat the negative path and then the positive path. Required examples include an unreachable item, a nonfood item, a removed item, a target acquired during curiosity, an expired grab, a disconnected passenger, an obstructed spyglass ray, Deep Cold Ocean, replacement disabled, and a preexisting population above the ordinary cap. A recovery is valid only when ordinary navigation and state resume without a restart workaround unless the configuration contract requires a restart.

Do not delete excess fish from an upgrade world. Back up the world, install the corrected build, restart when the configuration requires it, and observe inherited despawn while new spawn attempts obey the corrected population rules.
