# BFS2 Phase 003 Task 002 evidence

This task connects the typed disturbance producers to the session species settings service. The handler now resolves one immutable revision for each candidate and applies global enablement, per source enablement, reaction mode, sensitivity, strength, radius, threshold, and cooldown rules. Settings changes remain server session only. Wildcard writes validate every target before publishing, reset removes only session overrides, and reload preserves valid overrides.

The catalog exposes the global disturbance fields and typed source fields for swim sprint, attack, damage, block break, fall, projectile, water entry, water jump, and occupied boat events. A shark uses its body water predicate during candidate selection. Boat diagnostics distinguish empty, stationary, subthreshold, duplicate, wrong dimension, and invalid passenger routes. Decision records include species, settings revision, radius, sensitivity, source strength, interval, alert lifetime, threshold acceptance, and the named rejection reason.

Verification used Java 17, Forge 47.2.0, Minecraft 1.20.1, and the checked in Gradle wrapper on the headless host. The exact source worktree was `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix` on branch `envy/bfs2-phase-003`, based on commit `cc3535127d880204d64d83df70c9681ba7dfd310`. The task implementation commit is `89186be`. No client or renderer was started.

Passed checks.

* `./gradlew :forge:test --no-daemon` passed all Java unit tests, including disturbance defaults, capability boundaries, atomic wildcard validation, reset, threshold equality, cooldown, disabled paths, radius, and investigation policy.
* `python3 -m unittest tools.test_bfs_debug_analyze -v` passed all 24 diagnostic parser tests with the extended decision schema.
* `./gradlew :forge:GameTestServer --no-daemon --console=plain -PbfsGameTestRunDir=<fresh disposable runtime> -PbfsGameTestNamespaces=bfsdisturbance` passed all 3 required tests. The source transition test passed, the occupied moving boat interval test passed, and the real water entry policy test proved disabled and enabled session revisions through the actual event bus path.
* Disposable GameTest runtimes were removed after the final log was read. No owned GameTest process remained.

The full phase gate remains open until the later Phase 003 tasks complete their command, documentation, review, and integration checks.
