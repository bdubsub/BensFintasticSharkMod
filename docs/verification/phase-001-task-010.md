# Phase 001 task 010 evidence

## Scope

This record covers `P001-TASK-010` and `BFS-REQ-009` for Atlantic Cod and
Atlantic Salmon spawn eggs, food items, supplied sprites, raw and cooked death
loot, fishing acquisition, retained fishing weights, and furnace, smoker, and
campfire recipes.

## Implementation and data contract

Both Atlantic fish spawn eggs and all four fish food items are registered in
`ModItems`, have generated item models, and use the supplied 16 by 16 item
textures. They remain visible through the existing creative tab because only
`HiddenItem` entries are filtered.

Each fish entity loot table yields its raw item and applies the vanilla furnace
smelt function only when the entity is on fire. This preserves raw drops for an
ordinary death and cooked drops for a fire death. The shared cooking provider
generates matching smelting, smoking, and campfire recipes with the raw item as
the only input.

Fishing uses two global loot modifiers on the vanilla fishing fish table. Each
modifier adds one matching raw fish with a `0.125` chance. The test executes the
real `FishingRodItem.use` cast and `FishingHook.retrieve` reel path. The bite
timer is armed in a bounded server fixture so the test does not wait for random
weather and water timing. It then resolves the same fishing table 256 times and
asserts that both retained weighted outputs occur.

## Verification results

The phase worktree is `/mnt/hermes/projects/BFSMOD-phase-001` on
`envy/0.24-phase-001`, targeting Minecraft `1.20.1`, Forge `47.2.0`, Java
`17`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`.

```text
./gradlew :forge:Data --no-daemon
BUILD SUCCESSFUL

./gradlew :forge:test --tests tfar.bensfintasticsharks.audit.ReleaseContractAuditTest --no-daemon
12 tests completed, BUILD SUCCESSFUL

./gradlew :forge:GameTestServer --no-daemon --rerun-tasks
All 41 required tests passed :)
BUILD SUCCESSFUL
```

The headless GameTest server ran on node 1. It exercised both fish death loot
paths, cooking recipe lookup, real rod cast and reel behavior, and fishing table
weight resolution together with the retained Phase 001 suites. Data generation
completed without content drift in the generated JSON resources. The data
generator refreshed tracked cache metadata; those pre-existing generated cache
changes and the protected `build.gradle` line ending change were preserved and
are not part of this task commit. The disposable `forge/run` runtime, world,
logs, debug captures, and EULA were removed after verification. The protected
pre-existing `forge/logs/` directory was preserved.

## Limits and next gate

This task proves the server-side item, loot, recipe, generated resource, and
fishing contracts. It does not claim the later packaged JAR inspection or the
interactive laptop capture of fishing, inventory, and visual presentation.
Those remain later Phase 001 gates and require the matching rebuilt artifact.
