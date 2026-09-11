# Phase 001 Task 002 Evidence

## Scope

`P001-TASK-002` repairs the shared aquatic movement path used by sharks and
preserves the approved body aligned vertical movement contract. Vertical thrust
is derived from the current body pitch. Direct ascent and descent routes retain
their entry yaw, ease into pitch, use forward propulsion, and settle at the
latched final navigation depth without starting a horizontal orbit. Non pitch
driven aquatic movement keeps the existing smoothed vertical path.

The implementation is shared by `AbstractSharkEntity` and
`SharkSwimmingMoveControl`. `AquaticMovement` owns the forward vector,
body aligned input, powered velocity limit, and stale vertical slip removal.
The deterministic oracle keeps the affected vertical component at ten percent
of the entity speed and bounds pitch changes to six degrees per second at the
nominal twenty tick server rate.

## Source and runtime binding

The evidence was produced from branch `envy/0.24-phase-001` in
`/mnt/hermes/projects/BFSMOD-phase-001`. The entry commit is
`cef42d6`. The worktree target is Minecraft `1.20.1` with Forge `47.2.0`,
Java `17`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`.

All server checks ran headless on `node-1` using disposable runtime directories
under `/tmp`. No graphical client or window was started on that host. The
temporary runtime and its generated world and logs were removed by the test
cleanup trap after each bounded GameTest run.

## Verification results

The following checks passed after the shared movement changes.

```text
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
BUILD SUCCESSFUL

./gradlew :forge:test --no-daemon
BUILD SUCCESSFUL

./gradlew :forge:GameTestServer --no-daemon --rerun-tasks
All 22 required tests passed :)
BUILD SUCCESSFUL
```

The GameTest movement matrix covered level cruise, one and two block ascent and
descent, close arrival, turn recovery, shark pitch easing, the ten percent
vertical speed bound, monotonic vertical travel, and finite non circular entry
arcs. The shark route oracle also required a stable final tail, proving that the
controller stopped at the final depth instead of continuing into a new orbit.
The dolphin fixture was retained as a qualitative movement reference and
progress check. It was not used as a strict numeric oracle, in accordance with
`DEC-006`.

The unit audit added coverage for pitch derived vertical input, powered velocity
limits, and removal of stale vertical slip while preserving lateral drift. The
full headless suite remained green after the latest controller and oracle
changes.

## Limitations and next gate

This record covers the deterministic shared locomotion and server side
GameTest evidence for `P001-TASK-002`. It does not claim the Phase 001 client
pursuit capture, interactive animation matrix, predator attack behavior, or
Phase 001 exit gates. Those remain mandatory downstream evidence and must use
the matching packaged artifact on EnVy's Linux laptop when a rendered client is
required.
