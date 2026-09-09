# Phase 001 Task 003 Evidence

## Scope

`P001-TASK-003` proves the Sand Tiger and Blacktip Reef Shark bite approach
and contact path against stationary and moving prey. The matrix uses Tropical
Fish as the small fixture, Cod as the ordinary fixture, and Drowned as the
large fixture. Each species runs all three sizes in stationary and moving
cases.

The fixture sets the real target, leaves the normal brain and navigation path
active, records the approach distance, waits for the delayed server impact, and
then kills the target to exercise target loss cleanup. It rejects animation
alone as proof because the assertion requires a health delta. The matrix also
requires the target to clear after impact and prevents a second impact inside
the attack window.

## Repair

The first matrix run exposed a Blacktip contact gap with the large moving
fixture. Its small collision box stopped at about 1.70 blocks from a large
target while the previous center distance threshold was about 1.20 blocks. The
Blacktip contact threshold now includes its bounded authored jaw extension and
does not reuse the navigation stopping distance as damage reach. No target
health is changed before the delayed impact path.

## Source and runtime binding

The evidence was produced from branch `envy/0.24-phase-001` in
`/mnt/hermes/projects/BFSMOD-phase-001`. The task changes are after commit
`32c3652`, with the task 003 repair uncommitted at the time of this record.
The runtime target is Minecraft `1.20.1` with Forge `47.2.0`, Java `17`,
GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`.

The dedicated GameTest server ran headless on `node-1`. No client, renderer,
window, or virtual display was started. The disposable runtime was created
under `/tmp/bfsm-p001-task003-gametest-Pi3fei` and removed by the cleanup trap
after the server stopped.

## Verification results

```text
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
BUILD SUCCESSFUL

./gradlew :forge:GameTestServer --no-daemon --rerun-tasks
All 24 required tests passed :)
BUILD SUCCESSFUL
```

The final run included the existing movement and combat checks plus
`sandtigerBiteMatrixReachesStationaryAndMovingPrey` and
`blacktipBiteMatrixReachesStationaryAndMovingPrey`. Both species completed all
six size and motion cases. Health changed only after the scheduled bite impact,
the nearest recorded approach was within the physical contact envelope, and
target loss cleared the active target before the next case.

The first two matrix runs are retained only as diagnosis in the task history,
not as passing evidence. One exposed the test harness removing the brain
consumer of walk targets. The second exposed the moving fixture using an
absolute position cap and teleporting prey outside the fixture. The final
matrix preserves normal brain navigation and moves the prey relative to its
fixture position.

## Limitations and next gate

This record covers deterministic server approach, delayed damage, contact,
and target loss. It does not claim the required client synchronized bite and
health capture, which remains a Phase 001 runtime gate and must use the matching
packaged artifact on EnVy's Linux laptop.
