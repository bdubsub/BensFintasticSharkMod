# Phase 004 task 008 GameTest evidence

Date: September 11, 2026

Three clean dedicated server GameTest reruns passed all 84 required tests on
the Phase 004 source after the navigation and fishing fixture repairs. The
suite includes all three algae forms, submerged placement and water
restoration, support loss and collection paths, exact shears loot, wrong-tool
rejection, data and debug controls, fishing transaction and live-catch
invariants, every vanilla Cod and Salmon creation source, and the shared
movement and combat regression harnesses.

The three runs used Java 17 and fresh disposable runtimes in the Phase 004
worktree. Each reached the Forge GameTest ready state, reported
`All 84 required tests passed`, and shut down cleanly.

| Run | Runtime | Sanitized Gradle log SHA 256 |
| --- | --- | --- |
| 1 | `/tmp/bfsm-p004-gametest-algae11-BD6P65` | `84b2180e704285dae806f4ee7e35c42bae59e27f5764c2e70644239b64e27f43` |
| 2 | `/tmp/bfsm-p004-gametest-algae12-iShxmq` | `79fccff26c833dc184bdf9d654458d9102434304ad83926bb08c9b921f253cbe` |
| 3 | `/tmp/bfsm-p004-gametest-algae13-bElE9A` | `8dae8d14220220bbd1b56ec87c4f3a514d19ff14edd0e09bb28099e9f33e0ca7` |

The algae navigation fixtures place all three forms in a spaced submerged
patch and require both Atlantic Cod and Tiger Shark to reach a fixed target
through or around that patch. The fishing fixture separates observed entity
join events from accepted rewards, so canceled insertions cannot be counted
as deliveries while accepted live fish and experience remain observable.
The population soak now waits for the prior capture to finalize before its
second replacement mode, preventing stale mode records.

These are server-side acceptance tests. They do not replace the required
interactive client visual approval or the packaged runtime navigation soak.
