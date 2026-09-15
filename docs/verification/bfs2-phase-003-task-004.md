# BFS2 Phase 003 Task 004 evidence

This task records the phase runtime and recovery gate for disturbance producers and Great White
boat interest. The source candidate is `ce7bb27`, the signed branch tip on
`envy/bfs2-phase-003`. It targets Minecraft 1.20.1 with Forge 47.2.0, Java 17, and the checked in
Gradle wrapper.

The focused server and parser checks passed.

* `./gradlew :forge:test --no-daemon` passed the Java unit suite, including the Great White boat
  route helpers and bounded prediction checks.
* `./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestNamespaces=bfsdisturbance`
  passed all seven disturbance tests. Coverage includes real water entry and jump transitions,
  occupied moving boat throttling, a safe behind route, combat priority, dismount and stationary
  release, and an obstructed route.
* `python3 -m unittest tools.test_bfs_debug_analyze -v` passed all 24 parser tests.
* `git diff --check` passed before the evidence commit.

An exploratory all namespace run was not accepted as phase evidence. The existing
`bfsgametests.tigercuriosityfleepreemptionclearsinvestigation` test failed with
`flee preemption must clear curiosity state`, and
`bfsmovementoraclegametests.allspeciesmovementoracle` failed because the Shortfin Mako powered
vector did not match the expected value. The run then stopped when the existing
`gameteststructures/bfsfollowthirdpartygametests.empty.snbt` fixture was unavailable. These are
outside the disturbance implementation and remain visible for their owning work. No claim of a
full namespace pass is made.

The paired laptop client gate is unverified. This task ran on the headless `node-1` host, where
`hyprctl`, `nvidia-smi`, and `wpctl` are unavailable. No client, window, renderer, or audio stream
was started. Consequently, real laptop jump and boat input, joined world identity, submerged body
and dorsal fin presentation, and visual recovery remain blocking evidence requirements.

Cleanup completed for this run. The exact disposable GameTest runtime and generated debug captures
owned by the exploratory runs were removed, no owned server or GameTest process remained, and the
pre-existing `forge/logs/` and `performance-matrix-20260912-interval50/` directories were left
untouched. The phase gate remains open pending the laptop gate, independent review, integration,
default branch verification, and signed phase tag.
