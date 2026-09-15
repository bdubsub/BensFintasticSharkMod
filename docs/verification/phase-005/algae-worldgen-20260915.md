# Phase 005 Algae and World Generation Evidence

This packet records the Phase 005 algae state, generation, diagnostic, and visual verification status for Minecraft 1.20.1.

## Contract identity

| Record | Value |
| --- | --- |
| Phase | `BFS2-PHASE-005` |
| Requirements | `BFS2-REQ-014`, `BFS2-REQ-015`, `BFS2-REQ-016`, `BFS2-REQ-022` |
| Default branch | `1.20.1` |
| Source implementation commit | `6325509a614164b1d09bc211d6bf0c3ba38b0de3` |
| Minecraft | `1.20.1` |
| Forge | `47.2.0` |
| Java | `17.0.19` Eclipse Adoptium |
| World seed | `240024` for the fixed density fixture |
| Candidate artifact SHA 256 | `f9c0c8ca970d31be6bd71a4051a4104f67508cdbefe89605968cbc3312c53141` |
| Candidate artifact SHA 512 | `e4e0b2a028dc8498a1c03fe71e385684fa9de99cff5b46e12bdb4b012d25fd7298aefd6db67867d594309ce07b5f55c4dfd3f30f41784388fc7869925827c742` |

## Implemented behavior

Small algae keeps its public registry identity and now stores a waterlogged multiface attachment on supported north, south, east, west, and down faces. Invalid support is pruned without removing other faces, and the final face restores source water. Large green and red algae keep their public identities and use waterlogged `single`, `body`, and `top` segments with ages zero through 25. Manual stacking, bonemeal, and valid random growth stop at eight cells and preserve source water. Legacy singleton states normalize during load.

Natural generation uses two patch attempts and at most sixteen candidates per patch. A successful large column is two through eight cells tall and is limited by the available source water. Red generation checks the first nonwater block above the source column for sky light, so a roofed underwater cave records `red_surface_rejected` while an open water surface can place a column. Existing chunks are not repopulated.

The algae command uses the existing bounded `bfs-debug-v2` writer. `/bfs debug algae scan <minX> <minZ> <size>` reports seed, region, small cells, green and red cells, column counts, tall column counts, and generated cells. Follow debug selection remains an unlimited per owner group with independent toggles. Selection, waiting, resume, pause, release, rejection, and lifecycle changes send both system chat and action bar feedback. Arrival never removes a selected member or blocks adding another member.

## Deterministic checks

| Check | Result |
| --- | --- |
| `./gradlew :forge:compileJava :forge:compileTestJava --no-daemon` | Passed |
| `./gradlew :forge:test --no-daemon` | Passed |
| `python3 -m unittest tools/test_bfs_debug_analyze.py` | Passed, 28 tests |
| `./gradlew :forge:Data --no-daemon` | Passed, 302 generated files, no stale removals |
| Forge GameTests | Passed, 129 required tests |
| `./gradlew :forge:build --no-daemon` | Passed |
| `git diff --check` | Passed before final evidence binding |

## Fixed seed density fixture

The real dedicated server used a disposable world on the headless execution host with seed `240024`. The inspected region was exactly chunks X and Z `0` through `3`, sixteen chunks total. The baseline source at the approved `1.20.1` merge placed one green cell per configured feature invocation, for 16 occupied large algae cells. The candidate invoked the registered configured green feature sixteen times after preparing a 64 by 64 open water floor. The final candidate scan reported 180 occupied green cells, 33 green columns, and 33 columns at height two or greater. The candidate therefore exceeded the 1.5 times baseline threshold and every occupied natural column was at least two cells tall.

The diagnostic capture recorded 37 `algae_generate` events, 32 support reconciliations, 32 state migrations, and a complete terminal record with zero dropped records. Generation records included bounded candidate attempts and placed cell counts.

For the cave control, stone floor fixtures were prepared on the same dedicated server. The roofed fixture recorded 31 `red_surface_rejected` events and no placed red column. The matching open surface placed two red columns, each two cells tall, with `surfaceVisible` true in the capture. The scan reported two red columns and four red cells in that open fixture. The red surface GameTest also passed both the open surface and roof rejection assertions.

## Follow group verification

The server GameTest suite includes twenty independently controlled followers and verifies that all twenty remain selected after reaching the owner, that a second move causes every member to follow again, and that each fresh click releases only its own member. The same suite covers thirty three members, generic non BFS mobs, slimes, SmartBrainLib mobs, sharks, lifecycle cleanup, and paired chat and action bar messages. The command documentation exposes `followme`, paged `status`, `stopone`, and group `stop` without an arbitrary follower count limit.

## Visual gate

The required laptop client gate ran on 2026-09-15 against the source bound candidate at private endpoint `100.76.164.109:25906`. Node 1 ran the disposable dedicated server from `forge/run/bfs2-p005-server-visual-final-20260915`, and the Linux laptop ran the matching Forge 1.20.1 development client from the same worktree. The client window was `Minecraft Forge* 1.20.1 - Multiplayer (3rd-party Server)`, Hyprland address `0x5602ea6b9160`, PID `2315774`, and workspace `9`. The renderer reported the NVIDIA GeForce RTX 5090 Laptop GPU. The client log is `/tmp/bfs2-p005-client-visual-final5-20260915.log` with SHA 256 `a0a61aac5d55cd304e7f61bb6013bdf412fd2789052ba9346b9f7bf4a156244a`. The server log is `forge/run/bfs2-p005-server-visual-final-20260915/logs/latest.log` with SHA 256 `9c9f939bee44f926b99c732213e9f6e5d25e080d735da663fad55c760acc9d2d`.

The isolated client set `soundCategory_master:0.0` before startup. Its exact Java playback stream was correlated to PID `2315774` at index `108672` and remained muted with zero volume. The client joined the server as `Dev`, and the server log records the same player join and every fixture command. This source bound visual run is paired with the independently archived candidate artifact SHA 256 `f9c0c8ca970d31be6bd71a4051a4104f67508cdbefe89605968cbc3312c53141` and SHA 512 `e4e0b2a028dc8498a1c03fe71e385684fa9de99cff5b46e12bdb4b012d25fd7298aefd6db67867d594309ce07b5f55c4dfd3f30f41784388fc7869925827c742`. The client used the source classpath for the disposable visual run, so this record does not claim that the laptop loaded the packaged jar directly.

The scene used a water floor and surface fixture at the server origin. Green and red large algae columns were placed at `(-3,61,0)` and `(3,61,0)` with three visible segments each. A small algae side attachment was placed at `(-9,64,0)`. The later close fixture added a supported stone wall at X `3` with four west-facing waterlogged algae cells and a down-facing floor attachment. The clean frame `/tmp/bfs2-p005-client-visual-final5-clean.png` has SHA 256 `6ce8f97a7b0a91a5bded939c5cfdc5a7c073f62fc0b60c983a4046a69f67f2aa` and shows the green and red columns in open water. The clean close frame `/tmp/bfs2-p005-client-visual-final5-small-clean.png` has SHA 256 `20a224e82aa860631af87bf932d1f7542528ed9910b3170e9db6bf95f997f5f3` and shows the supported small algae wall attachment together with the green column. The command feedback frame `/tmp/bfs2-p005-client-visual-final5-small.png` has SHA 256 `c3318f24a6aaed7106930afc9f1c6c4f9da2bbd60c644bba35bbf232f59c5ba7` and retains the in-game teleport feedback. The pre-wall frame `/tmp/bfs2-p005-client-visual-final5-algae4.png` has SHA 256 `bf7c8ca3057767bf95171a30f49dc18646158a5320b3c7ee8e75d676a9e23c29` and shows both colors without the close fixture occlusion.

The client executed `/reload` after the initial visual fixture. Its log records `Reloading!` followed by the refreshed advancement list, with no reload exception. The post-reload frame `/tmp/bfs2-p005-client-visual-final5-reload.png` has SHA 256 `d8ad649f997af36d7b7e087d09a2cb36ca1be5a83dcc7456bc0f2e1d497e1d63`. The old singleton normalization and all supported attachment faces remain covered by the server GameTests and source state assertions; the laptop frame specifically proves the rendered supported side and down attachments. No visual-only claim is made for a legacy save state that cannot be represented by a live command without writing a separate save fixture.

## Cleanup and remaining binding

The laptop client and dedicated server remain live until this evidence is committed and reviewed. After the final consumer, stop the exact client PID `2315774` and server session, remove only `forge/run/bfs2-p005-client-visual-final5-20260915`, `forge/run/bfs2-p005-server-visual-final-20260915`, `/tmp/bfs2-p005-client-visual-final5-20260915.log`, and the listed temporary screenshots, then verify the processes, stream, and paths are gone. Protected pre-existing `forge/logs/` and `performance-matrix-20260912-interval50/` directories remain untouched. The merged `1.20.1` commit, signed phase tag, and final cleanup record remain before the Phase 005 pull request can be opened.
