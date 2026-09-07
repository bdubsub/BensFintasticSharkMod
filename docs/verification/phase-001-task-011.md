# Phase 001 task 011 evidence

## Scope

This record covers `P001-TASK-011`, `BFS-REQ-010`, and `BFS-REQ-011` for
vanilla aquatic controls, exact Cod and Salmon replacement, safe state copying,
category parity, replacement ordering, one for one behavior, and replacement
off behavior.

## Implementation and policy contract

The common spawning configuration keeps `replace_vanilla_mobs` enabled by
default and `disable_vanilla_aquatic_spawns` disabled by default. Both values
are marked as world restart settings. The replacement policy accepts only the
`minecraft:cod` and `minecraft:salmon` registry paths and resolves each to its
matching Atlantic fish. Other vanilla species, BFS species, and other
namespaces resolve to no replacement.

Natural and chunk generation replacement runs before broad vanilla aquatic
suppression. It creates one matching Atlantic fish, preserves position,
motion, body rotation, head rotation, custom name, safe spawn data, and school
group continuity, then cancels the source attempt. Vanilla spawn egg and
dispenser joins use the same safe state copy and remove source identity,
position, motion, rotation, passenger, and leash data before the replacement
is added. Replacement reentry is guarded so the replacement cannot recurse
through the same policy.

Atlantic Cod and Atlantic Salmon use the vanilla `WATER_AMBIENT` category.
Common setup validates category parity, and a category mismatch fails closed
with one atomic actionable error. Existing fish are not deleted. Disabling
replacement leaves vanilla Cod and Salmon unchanged and leaves the separate
Atlantic biome modifier path available.

## Verification results

The phase worktree is `/mnt/hermes/projects/BFSMOD-phase-001` on
`envy/0.24-phase-001`, targeting Minecraft `1.20.1`, Forge `47.2.0`, Java
`17`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`.

```text
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
BUILD SUCCESSFUL

./gradlew :forge:test --tests tfar.bensfintasticsharks.audit.ReleaseContractAuditTest --no-daemon
13 tests completed, BUILD SUCCESSFUL

./gradlew :forge:GameTestServer --no-daemon --rerun-tasks
All 42 required tests passed :)
BUILD SUCCESSFUL

./gradlew :forge:build --no-daemon
BUILD SUCCESSFUL
```

The dedicated server startup smoke also passed on the headless node with a
fresh fixed seed runtime, an explicit `eula=true`, and precreated server
properties. `./gradlew :forge:Server --no-daemon
-PbfsServerRunDir=<disposable-runtime> --args='--port 25613 --nogui'` reached
`Done (12.985s)!` and loaded Forge `47.2.0` and the mod. The sanitized log has
SHA 256
`5e73fd4273ef02c904f1f78b574abe6a5078197f5fb35a30a839aec0c4c1734a` and SHA
512
`95ef188f18be2dde7b5e52cd25b96f8d96f5c20deb3b2d276249b49b8994e9ca23380f42013690324381b0e3ccab7113b79f22b180ca68d9cf14468cfbd44713`.
The exact disposable runtime, world, configuration, EULA, and logs were
removed after the evidence hash was recorded.

The new dedicated server GameTest exercises a natural Cod replacement,
one for one entity count, custom name, body and head rotation, motion, school
continuity, an excluded Tropical Fish path, a real join event for a vanilla
Cod spawn egg, and replacement disabled for a chunk generated Salmon. The
The expanded matrix also covers Pufferfish, command, bucket, spawner, and
structure reasons, existing loaded Cod, BFS Cod spawn eggs, and replacement
disabled for vanilla Cod eggs. The runtime used the headless GameTest server on
node 1 with no client or renderer. The latest isolated run passed all 42
required tests. Its latest log has SHA 256
`4b1e8c76466028afcfcf250f9a4daa623b64e0a59e7fac49041602eee204adf1` and SHA
512
`ca335ce956afe4915c3e103524f44044b902f9c1ff0339a9eaa658354934328ac0117bc004b4a712202790e95b345c4bef856c345effe9d55af91405687c702f`.

The Forge artifact passed `unzip -tqq`. The candidate artifact was
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar` with SHA 256
`b18f548db778632f4b0eb598db43203829760ca05edcd2213c8841efb66c90c7` and SHA
512
`db281d4d585fdaa22d0f54ec9c079c650449c71bf4afa00a4db35d3499433b66b22a180d0a8c918e030d2d045546eee0229f253924c9cbb570958c3ea4c630d9`.

That hash belongs to the earlier task 011 evidence artifact. The subsequent
fishing modifier and shared arrival braking repairs produced a new candidate,
so the earlier packaged population records are retained as historical evidence
only. The current artifact hash and current full GameTest result are recorded
in [Phase 001 task 013](phase-001-task-013.md). A fresh packaged population
series remains required before the phase integration gate can close.

The disposable `forge/run` GameTest runtime, world, configuration, EULA,
logs, and debug captures were removed after the final evidence consumer
completed. The protected pre-existing `forge/logs/` directory and generated
cache changes were preserved.

## Packaged runtime population soak

The final artifact was copied without modification to two isolated packaged
Forge 1.20.1 server runtimes. Both runtimes used Java 17.0.19, Forge 47.2.0,
the same official GeckoLib 4.4.7 dependency with SHA 256
`6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0`, and the
same official SmartBrainLib 1.14.2 dependency with SHA 256
`3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b`.
The fixed seed was `240024`. The player joined the packaged server from the
authorized laptop client over the private connection, and the server retained
one connected player for every sample. The sampled area was the force loaded
64 chunk rectangle `[31,21]` through `[38,28]`, with counts limited to 128
blocks around the player at `(560,72,400)`.

With `replace_vanilla_mobs=true`, runtime
`/tmp/bfsm-p001-packaged-server-20260907152330` on port `25796` and RCON port
`25797` produced 21 samples every approximately 1,200 ticks. The run started
at gametime `4828` and ended at `29675`, for `24847` ticks. Atlantic Cod
remained at 19, Atlantic Salmon remained at 2, and vanilla Cod and Salmon
remained at zero for all samples. The enabled run used an op player in
survival and logged one drown before the first sample, but the player
connection remained present and no sample was lost.
The enabled server log SHA 256 was
`a8c8090ea439e047c1f13d6914d2bb529c407b2866535f14ada47169e3496df7`.

With `replace_vanilla_mobs=false`, runtime
`/tmp/bfsm-p001-packaged-server-20260907152330-false` on port `25798` and RCON
port `25799` produced the same 21 point, `24847` tick series from gametime
`1919` through `26766`. The player was op and creative for this run. Atlantic
Cod ranged from 7 to 15, Atlantic Salmon ranged from 0 to 8, vanilla Cod
ranged from 1 to 10, and vanilla Salmon ranged from 0 to 5. This confirms that
the disabled mode leaves vanilla fish available while the Atlantic biome path
continues independently, without an unbounded population bloom.
The disabled server log SHA 256 was
`1f4c774dc04c11b3a2519a1bc554937678f207f20821d9d7ebcc510660f8280a`.

The retained count series is:

| Mode | Samples | Gametime interval | Atlantic Cod | Atlantic Salmon | Vanilla Cod | Vanilla Salmon | Players |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| Replacement enabled | 21 | 4828 to 29675, 24847 ticks | 19 to 19 | 2 to 2 | 0 to 0 | 0 to 0 | 1 throughout |
| Replacement disabled | 21 | 1919 to 26766, 24847 ticks | 7 to 15 | 0 to 8 | 1 to 10 | 0 to 5 | 1 throughout |

## Non destructive recovery rehearsal

The isolated runtime
`/tmp/bfsm-p001-packaged-server-20260907152330-recovery` used the same final
artifact, dependency hashes, Java version, seed, and Forge server. The
replacement enabled config set the Atlantic Cod cap to 8. Fourteen persistent
Atlantic Cod were summoned into the force loaded recovery fixture at
`(560,62,400)` with the tag `bfs_recovery_excess`. No delete or kill command
was used. Before restart, the tag count was 14, `PersistenceRequired` was
`1b`, and the first sampled UUID was
`[I; -118422574, -320714601, -2136102299, 104897925]`.

After a clean server stop and restart of the same runtime, the tag count was
14, the same UUID was present, and `PersistenceRequired` remained `1b`. After
an additional one minute observation, the tag count remained 14 and the UUID
remained unchanged. This proves that the corrected build does not delete
existing excess Atlantic fish during restart or ordinary cap processing. The
recovery server was stopped after the observation and its disposable runtime
was removed after this record was prepared.
The recovery server log SHA 256 was
`ef03921b3d871b0bde57dbceadc5d2e4175444a2cc932f1e8be1e30a20f67adf`.

These packaged runs close the sustained two mode population and
non destructive recovery gates for `P001-TASK-011`. They do not close the
remaining Phase 001 advancement, full matrix, review, merge, default branch,
or signed tag gates owned by `P001-TASK-012` through `P001-TASK-014`.
