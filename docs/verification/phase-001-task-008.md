# Phase 001 task 008 evidence

## Scope

This record covers `P001-TASK-008` and `BFS-REQ-007` for the supplied Oceanic
Whitetip animation mapping, controller priority, delayed bite timing, and
thrash cleanup boundary.

## Implementation

The Forge controller references the supplied `idle`, `swim_new`,
`swim_fast_new`, `bite_new`, `death`, `beached`, and `thrash` clips without
renaming authored resources. Static contract checks parse the animation and
geometry JSON, compare every clip's animated bone set with the geometry, and
assert the exact controller references.

The main controller gives death priority over beached, hostile fast swim, and
ordinary movement. Bite remains a triggerable one shot through its delayed
server impact. Thrash is isolated on its own controller and now requires a
live passenger, an active synchronized grab timer, and water. It stops for
death or water exit, so temporary grab presentation cannot mask death or leak
after release.

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
All 38 required tests passed :)
BUILD SUCCESSFUL
```

The headless GameTest server ran on node 1. It exercised the existing real bite
to grab path, thrash damage and release, lifecycle cleanup, shared movement,
and debug capture checks. The disposable `forge/run` runtime, generated world,
logs, debug captures, and EULA were removed after the run. The protected
`build.gradle` line ending change and pre-existing `forge/logs/` directory were
preserved.

## Limits and next gate

This task proves the static mapping and server state contract. It does not
claim visible transform variance for every clip, which belongs to Phase 002,
or the final packaged-client state selection and clean client-log review. The
matching candidate artifact must be rebuilt before later client and integration
evidence is accepted.
