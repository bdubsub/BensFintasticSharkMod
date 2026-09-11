# Phase 000 EULA Readiness Session

This record documents owner authorized `EXT-001` readiness sessions. Each authorization was consumed in the isolated Phase 000 worktree only and was not used as a general EULA policy.

## Historical session binding

| Field | Recorded value |
| --- | --- |
| Phase worktree | `/mnt/hermes/projects/BFSMOD-phase-000` |
| Branch | `envy/0.24-phase-000` |
| Source revision | `5118a6fbdf173d962e02df49f79c3d0350bc4c8d` |
| Runbook | `docs/test/phase-000-runbook.md` |
| Runbook SHA-256 | `f8eb15d81cc984acfabb79fc571cb9a1efc578b486aef4e1c5e42cbe4c02ce89` |
| Target | `/mnt/hermes/projects/BFSMOD-phase-000/forge/run/eula.txt` |
| Target pre-state | Absent |
| Command | `timeout --kill-after=15s 90s ./gradlew :forge:Server --no-daemon --args='--port 25576 --nogui'` |
| Port | `25576` |
| Platform | Minecraft `1.20.1`, Forge `47.2.0`, Java `17` |

## Operation and result

The target was absent before the session. It was created with exactly one line, `eula=true`, and had size `10` bytes and SHA-256 `ee27072e4a23e088522f740ddaab0c7c4145c186969e90a86254faa3a5ec5ce6` during the run.

The server log reached the ready marker at `2026-09-03 10:28:34.237`:

```text
[10:28:34] [Server thread/INFO] [net.minecraft.server.dedicated.DedicatedServer]: Done (13.024s)! For help, type "help"
```

Forge loaded the mod on the dedicated server, registered `7` recipes and `1343` advancements, and reported no mod-specific class-loading, registry, data-pack, or resource error. The first-run `server.properties` missing-file message was followed by normal property generation and is expected for the disposable run. The Forge version check and development reference-map warnings are non-fatal environment warnings.

The bounded command ended with exit code `124` at the `90` second timeout after the ready state had been recorded. The server handled termination and logged `Stopping server`, `Saving players`, `Saving worlds`, and all-dimension chunk completion through `2026-09-03 10:29:41.189`.

## Rollback and isolation

After termination, port `25576` had no listening socket and no Forge server child process remained. The exact target was deleted because its pre-state was absent. A post-rollback check reported `eula absent`.

The original worktree `/mnt/hermes/projects/BFSMOD` retained its pre-session branch, status, protected `build.gradle` line-ending change, generated cache changes, plan-authoring changes, and untracked `Content/` tree. The phase worktree retained only its inherited protected `build.gradle` and generated cache changes. No tracked source, other worktree, remote ref, GitHub object, persistent server, or production system was changed by this session.

The remote default head remained `15d3a8ce4913dbe5b647b67dbb5993b2f58c983e`, and the release work head remained `a4d4e41511ab47a05f27d72387075e21a5ef3cb5`. The historical authorization is consumed and was not reused after its revision, target, runbook, or worktree changed.

## Current candidate session

| Field | Recorded value |
| --- | --- |
| Phase worktree | `/mnt/hermes/projects/BFSMOD-phase-000` |
| Branch | `envy/0.24-phase-000` |
| Source revision | `5dfade3cd29c7e8623608b4422e5e7ca59500536` |
| Evidence commit | `0e81b10d8d8aa26d2b1e76a95c1340ce3fc9c8a` |
| Runbook | `docs/test/phase-000-runbook.md` |
| Runbook SHA-256 | `5d6898aa44424cc5a0e3054476b243f1d2a1c357863d5cdda1b59ebb2536bf86` |
| Target | `/mnt/hermes/projects/BFSMOD-phase-000/forge/run/eula.txt` |
| Target pre-state | Absent |
| Command | `timeout --kill-after=15s 90s ./gradlew :forge:Server --no-daemon --args='--port 25576 --nogui'` |
| Port | `25576` |
| Platform | Minecraft `1.20.1`, Forge `47.2.0`, Java `17` |

The target was created with exactly one line, `eula=true`, and had size `10` bytes and SHA-256 `ee27072e4a23e088522f740ddaab0c7c4145c186969e90a86254faa3a5ec5ce6` during the run. The current server log reached the ready marker at `2026-09-03 14:21:33.147`:

```text
[14:21:33] [Server thread/INFO] [net.minecraft.server.dedicated.DedicatedServer]: Done (2.585s)! For help, type "help"
```

The server loaded Forge and the mod without a mod specific class loading, registry, data pack, resource, or startup error. The bounded command terminated at `2026-09-03 14:22:41.569` after the ready state, logged `Stopping server` and `Saving worlds`, closed port `25576`, and left no Forge server process. The command used exit code `124` from the authorized timeout. The target was deleted after the run and the post state was absent.

This current session supersedes the historical stale readiness record for the tested movement revision. Its authorization is consumed and must not be reused after the revision, target, runbook, or worktree changes.
