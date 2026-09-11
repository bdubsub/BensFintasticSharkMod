# Phase 005 task 009 Forge build and packaged server readiness

Date: September 11, 2026

P005-TASK-009 bound the exact Forge release candidate built from commit `a939aa2` on `envy/0.24-phase-005`. The protected `build.gradle` line-ending change remained unstaged.

## Candidate artifact

The build used the supported Java 17 runtime, Minecraft 1.20.1, Forge 47.2.0, GeckoLib 4.4.7 and SmartBrainLib 1.14.2.

```text
env JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 PATH=/usr/lib/jvm/temurin-17-jdk-amd64/bin:$PATH ./gradlew :forge:build --no-daemon --console=plain --rerun-tasks
BUILD SUCCESSFUL
24 actionable tasks: 24 executed
```

The reobfuscated release artifact is:

| Field | Value |
| --- | --- |
| File | `forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar` |
| Size | `2013933` bytes |
| SHA 256 | `94bfe2c8b45fc54505d42b63d026e203086959c34bb28325da3cf762c46578bd` |
| SHA 512 | `8d9edc14bf81e11fd8dc5e905b9aa1201e84c7d7bf303c53cee2f9a57fee516cd99076066b6df92d14d116f6199ddacf7deb03ee4237b1f37ccd369ae8bb5675` |
| Archive test | `unzip -tqq` passed |
| Inventory | 939 entries, 5799123 uncompressed bytes |

The embedded manifest reports `Specification-Version` and `Implementation-Version` as `1.0-rc.1`, `Built-On-Minecraft` as `1.20.1`, and `Built-On-Java` as `17.0.19+10 (Eclipse Adoptium)`. The processed `mods.toml` reports the same candidate version. The sources JAR was not used for runtime evidence.

## EXT-001 current binding

The existing DEC-008 authorization was bound to a new disposable runtime because the candidate revision and artifact changed. No tracked source, original worktree, production server, remote ref, credential or public endpoint was changed.

| Field | Value |
| --- | --- |
| Host | `node-1`, headless Linux |
| Runtime | `/tmp/bfsm-p005-task009-server` |
| Forge installer | `forge-1.20.1-47.2.0-installer.jar`, SHA 256 `bc2a0f7b161a2d8284df3d603f7f2b22313b246f026ad77511cbd35bcd01caac` |
| Java | Temurin `17.0.19+10` |
| Endpoint | private server port `25576`, no public exposure |
| EULA pre-state | `eula.txt` absent |
| EULA readback | `eula=true` before launch |
| Command | `./run.sh --nogui --port 25576` under `timeout --kill-after=15s 90s` |
| Candidate SHA 256 | `94bfe2c8b45fc54505d42b63d026e203086959c34bb28325da3cf762c46578bd` |
| GeckoLib SHA 256 | `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0` |
| SmartBrainLib SHA 256 | `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b` |

The installed Forge runtime loaded the exact candidate and dependencies. Its debug log identified `bensfintasticsharks` version `1.0-rc.1`, `geckolib` version `4.4.7`, `smartbrainlib` version `1.14.2`, Forge `47.2.0`, and Minecraft `1.20.1`. The server reached:

```text
[07:26:22] [Server thread/INFO] [minecraft/DedicatedServer]: Done (2.514s)! For help, type "help"
```

It then stopped through the server shutdown path and saved all dimensions. The final bounded transcript hash is `1b7db7139f5ef30a189ae6008820ee9d3ddc19e93a6a46b9bc8c4b524a010e67`. No fatal error, exception, missing registry, missing data pack, missing tag, missing loot, missing recipe, missing advancement, client class loading failure, or unknown registry marker appeared in the final transcript. The empty mixin configurations emit the existing non-fatal reference-map warnings; no mixin was registered for the server to apply.

## Cleanup and disposition

The server process exited before evidence was recorded. The generated EULA, world, configs, logs, installer, libraries, and copied mods were owned by this disposable runtime and were removed after the final consumer. The historical runbook and Phase 000 readiness record remain unchanged. The phase worktree and original worktree were rechecked after cleanup, with only the pre-existing protected `build.gradle` line-ending change remaining in the phase worktree.

P005-TASK-009 is complete and authorizes the matching packaged laptop client gate in P005-TASK-010.
