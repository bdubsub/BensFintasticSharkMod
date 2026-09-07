# Phase 001 task 009 evidence

## Scope

This record covers `P001-TASK-009` and `BFS-REQ-008` for Atlantic Cod and
Atlantic Salmon parity, registration, placement, hitboxes, tracking, passive
schooling behavior, water navigation, beached behavior, client state wiring,
and the exact case sensitive `Spin` name state.

## Implementation

Atlantic Cod continues to extend vanilla `Cod` and Atlantic Salmon continues to
extend vanilla `Salmon`, so their inherited goals provide vanilla panic,
player avoidance, schooling, passive navigation, sounds, bucket behavior,
despawn, and beached flop behavior. The BFS movement control and GeckoLib
presentation are layered on those server contracts without adding attack
targets.

The Forge registrations now match the vanilla fish dimensions and client
tracking range. Atlantic fish use the vanilla `IN_WATER` placement type and
`MOTION_BLOCKING_NO_LEAVES` heightmap while biome modifiers continue to control
their eligible habitats. The test harness compares both registrations directly
with the vanilla types and verifies water navigation and school sizes.

Salmon `Spin` is exact and immediate. Only `Spin` enters the supplied looping
state. `spin`, `Spin `, another name, and no name remain ordinary states, and
renaming or removing the name exits without reloading the entity.

## Verification results

The phase worktree is `/mnt/hermes/projects/BFSMOD-phase-001` on
`envy/0.24-phase-001`, targeting Minecraft `1.20.1`, Forge `47.2.0`, Java
`17`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`.

```text
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
BUILD SUCCESSFUL

./gradlew :forge:test --tests tfar.bensfintasticsharks.audit.ReleaseContractAuditTest --no-daemon
BUILD SUCCESSFUL

./gradlew :forge:GameTestServer --no-daemon --rerun-tasks
All 40 required tests passed :)
BUILD SUCCESSFUL
```

The headless GameTest server ran on node 1. It exercised the new fish parity
and `Spin` matrices together with the retained movement, combat, lifecycle,
and debug diagnostics. The disposable `forge/run` runtime, generated world,
logs, debug captures, and EULA were removed after the run. The protected
`build.gradle` line ending change and pre-existing `forge/logs/` directory were
preserved.

## Limits and next gate

This task proves server parity, registration, placement, and state selection.
It does not claim the required interactive ordinary and beached client capture,
final packaged-client resource review, or full phase completion. Those remain
later Phase 001 verification gates and require the matching rebuilt artifact.
