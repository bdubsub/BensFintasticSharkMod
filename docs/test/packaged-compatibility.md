# Installed Forge compatibility fixture

This procedure runs the existing Grizzly Bear follow fixture against the exact packaged BFS candidate, Alex's Mobs `1.22.9` and Citadel `2.6.0`. It uses Java 17, Minecraft `1.20.1`, Forge `47.2.0`, GeckoLib `4.4.7` and SmartBrainLib `1.14.2`. Dependency identities are recorded in the [follow manifest](../verification/phase-002-follow-manifest.json).

Forge disables its development GameTest registration in a production launch. The isolated [probe](../../tools/verification/PackagedCompatibilityProbe.java) registers exactly one existing test and advances a separate test ticker from the normal server tick event. It does not change the candidate JAR, Forge's production flag, the follow implementation or the fixture assertions. No probe belongs in a player installation or delivery archive.

## Runtime preparation

Use a new ignored runtime beneath the verified project directory. Record ownership and exact cleanup paths before creating it. Link the existing installed Forge `libraries` directory without modifying its target. Copy the candidate and the four pinned dependencies into `mods`, verify their hashes, and copy `forge/src/main/resources/gameteststructures` to the runtime's `gameteststructures` directory. Verify `eula=true` in that runtime.

Bind the dedicated server to loopback with an unused test port, leave online mode enabled and select a disposable flat world with the normal bedrock, dirt and grass layers. The fixture uses the local area around `(0, -60, 0)`. Never reuse a personal world.

Build the probe outside the candidate archive using these arguments with the verified absolute paths:

```text
python3 -B tools/verification/build_packaged_probe.py --libraries <installed libraries> --candidate <candidate jar> --java-home <java 17 home> --output <runtime>/mods/bfs-packaged-verification.jar
```

From that runtime, launch the installed production server:

```text
<java 17 home>/bin/java -Xms1G -Xmx2G -Dforge.enabledGameTestNamespaces=bfsfollowthirdparty @libraries/net/minecraftforge/forge/1.20.1-47.2.0/unix_args.txt nogui
```

## Assertions and completion

The operator fixture clicks within ordinary interaction reach, requires an accepted interaction and an active membership, then moves away. The actual Grizzly Bear controller must reduce its distance by more than half a block in 60 ticks. The fixture requires a living target and a clean release. The probe shuts down after completion or its 400 tick limit. A process exit alone is insufficient. Require this terminal record and clean world saving:

```text
Packaged compatibility result. Complete true. Failed required 0. Done 1. Total 1.
```

This is server event and navigation evidence. It does not prove client input, rendering or receipt. Those portions are deferred by [DEC-012](../verification/phase-007/verification-scope-20260916.md).

Retain the source, candidate, dependency and probe hashes, exact launch arguments, host, fixture and sanitized result. Stop the exact owned process and verify it exited. Remove the runtime, world, logs, downloaded dependencies, probe and compiler scratch after the final consumer. Preserve shared libraries and required evidence.
