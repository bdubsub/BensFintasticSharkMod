# BFS2 Phase 003 Task 003 evidence

This task adds the Great White occupied boat interest path. A moving occupied boat now carries
its measured horizontal travel vector through the disturbance event. A Great White may acquire a
low priority `boat_track` lease only when the boat has a live rider and the predicted behind
waypoint has a finite, water filled body envelope. The waypoint includes the boat and shark half
widths plus two blocks of clearance and its prediction is capped at twenty ticks. The lease
refreshes every ten ticks, expires after one hundred stationary ticks, and releases on removal,
dimension loss, a missing rider, an unsafe route, a hard turn, or a higher priority safety, follow,
or combat owner. The ordinary shark scan is suppressed only while this lease is active, and the
handler writes a Brain walk target rather than entity position, velocity, navigation, animation,
player, or boat state.

Verification used Java 17, Forge 47.2.0, Minecraft 1.20.1, and the checked in Gradle wrapper on
the headless host. The exact source worktree was
`/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix` on branch `envy/bfs2-phase-003`. The source
implementation commit is `5fa8659`. No client or renderer was started.

Passed checks.

* `./gradlew :forge:test --no-daemon` passed the full Java unit suite, including the behind offset,
  depth preservation, and twenty tick prediction cap tests.
* `./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestNamespaces=bfsdisturbance`
  passed all five required tests. The source transition test, occupied boat interval test, real
  water entry settings test, safe Great White behind waypoint test, and combat priority test all
  passed.
* The same GameTest run was started with the disposable runtime target
  `/tmp/bfsm-bfs2-task003-20260915`, configured with `eula=true`, and the exact disposable target
  was removed after the final log was read. No owned GameTest process remained.
* `git diff --check` passed before the signed commit and push.

The phase gate remains open until the remaining Phase 003 tasks, independent review, integration,
default branch verification, and signed phase tag gates complete.
