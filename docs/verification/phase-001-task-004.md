# Phase 001 Task 004 Evidence

## Scope

`P001-TASK-004` covers Tiger Shark edible item curiosity. The fixtures verify
edible eligibility, rejection of a nearby nonedible item, a single submerged
intercept, unchanged item identity and stack count after the cosmetic bite,
and cleanup when the item is removed, leaves water, navigation fails, a live
target is acquired, or a larger shark causes flee behavior. The timeout fixture
also verifies the per item retry cooldown.

## Implementation and test coverage

The retained `TigerSharkEntity` state machine remains server authoritative. The
GameTests exercise its existing cleanup path rather than injecting a private
target or consuming an item. The fixture keeps the item at a fixed submerged
position, and the edible and nonedible cases are separate so the negative
control cannot change the selected intercept. Curiosity, target, and flee
transitions all assert that the item remains alive with an unchanged count.

During this task the fish movement controller also received two bounded
repairs discovered by the shared movement suite. Post movement integration now
enforces the approved ten percent vertical speed cap, and vertical arrival
impulses ease to zero inside the final block so the fish does not reverse at
the target.

## Source and runtime binding

The evidence was produced from branch `envy/0.24-phase-001` in
`/mnt/hermes/projects/BFSMOD-phase-001`. The runtime target is Minecraft
`1.20.1` with Forge `47.2.0`, Java `17`, GeckoLib `4.4.7`, and SmartBrainLib
`1.14.2`.

The dedicated GameTest server ran headless on `node-1`. No client, renderer,
window, or virtual display was started. Each run used the disposable Forge
GameTest runtime under the ignored `forge/run` directory. That runtime is
removed after the final consumer completes.

## Verification results

```text
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
BUILD SUCCESSFUL
```

The curiosity batch contained seven required tests and passed in the complete
server runs. The complete suite contained 31 required tests, but the current
suite is not yet a clean phase gate. Repeated runs still expose inherited
movement and combat failures in `sharkVerticalRouteFollowsDolphinWithoutOrbit`,
`atlanticCodVerticalRouteUsesScaledPitch`, and
`blacktipBiteMatrixReachesStationaryAndMovingPrey`. The latest run must not be
treated as Phase 001 completion evidence until those failures are repaired and
the complete suite passes in one run.

No client or packaged artifact acceptance is claimed by this record. The
interactive curiosity capture and the final packaged artifact remain later
phase gates.
