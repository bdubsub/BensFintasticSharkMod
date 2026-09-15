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

The required laptop client gate is not verified for source commit `6325509a614164b1d09bc211d6bf0c3ba38b0de3` and artifact SHA 256 `f9c0c8ca970d31be6bd71a4051a4104f67508cdbefe89605968cbc3312c53141`. This execution host is headless `node-1`, so no client, renderer, or display was started here. The earlier Phase 005 client record is bound to a different source and artifact and is retained as historical evidence only. The phase remains open until the matching silent laptop client observes small algae attachments, old-save normalization, green and red column segments, and resource reload behavior.

## Cleanup and remaining binding

All evidence worlds, server processes, client processes, temporary analyzer outputs, and capture scripts are disposable and must be removed after their final consumer. Protected pre-existing `forge/logs/` and `performance-matrix-20260912-interval50/` directories are not phase-owned and remain untouched. The matching laptop client gate, merged `1.20.1` commit, signed phase tag, and final cleanup record remain before the Phase 005 pull request can be opened.
