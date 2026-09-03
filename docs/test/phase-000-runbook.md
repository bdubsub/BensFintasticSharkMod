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

## Recovery checks

For every temporary state, repeat the negative path and then the positive path. Required examples include an unreachable item, a nonfood item, a removed item, a target acquired during curiosity, an expired grab, a disconnected passenger, an obstructed spyglass ray, Deep Cold Ocean, replacement disabled, and a preexisting population above the ordinary cap. A recovery is valid only when ordinary navigation and state resume without a restart workaround unless the configuration contract requires a restart.

Do not delete excess fish from an upgrade world. Back up the world, install the corrected build, restart when the configuration requires it, and observe inherited despawn while new spawn attempts obey the corrected population rules.
