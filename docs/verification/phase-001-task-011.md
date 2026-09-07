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
runtime used the headless GameTest server on node 1 with no client or renderer.

The Forge artifact passed `unzip -tqq`. The candidate artifact was
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar` with SHA 256
`72c4fae0e590e6e8f04c7bef60d357df02d74fbe7f551627c7fbbeafee0460eb` and SHA
512
`b55308ea1ab35fecddf5bd355876b32f108bb0a8b379fd5b181f9bf6230a15d3b5e99b193cfb05a312477020b2ffa338e91093ce47665b4ce8794a2cab0fff07`.

The disposable `forge/run` GameTest runtime, world, configuration, EULA,
logs, and debug captures were removed after the final evidence consumer
completed. The protected pre-existing `forge/logs/` directory and generated
cache changes were preserved.

## Remaining gate

The implementation and deterministic server fixtures pass. The extended
fixed-seed replacement-on and replacement-off ocean population soak, including
the retained player count and loaded chunk area comparison and non-destructive
excess-fish recovery record, remains a Phase 001 runtime evidence gate. It
must be rerun against the final phase artifact before Phase 001 integration.
