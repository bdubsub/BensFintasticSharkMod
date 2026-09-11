# Phase 004 task 008 GameTest evidence

Date: September 10, 2026

The final dedicated server GameTest rerun passed all 82 required tests. The
suite includes the three-form placement and water restoration fixtures, support
loss and collection paths, exact shears loot, wrong-tool rejection, and the
shared movement and fishing regression harnesses. The four clean 82 of 82 logs
and the independent JUnit log are bound in
`phase-004-task-003-survival-loot.md` and
`phase-004-task-011-artifact.md`.

The current isolated rerun used the Phase 004 worktree, Java 17, and a fresh
runtime at `/tmp/bfsm-p004-gametest-final-gj5DUd`. It reached the Forge
GameTest ready state, reported `All 82 required tests passed`, and shut down
cleanly. Its sanitized latest log SHA-256 is
`f816968a1cd4f7f12591d79272c3b4ddc8b036557edca296980bf4f8b5f9410c`.
The runtime and its generated world, logs, and configuration were removed
after the result was recorded.

The current suite does not yet provide a dedicated natural configured-feature
success and failure fixture, invalid data-pack fixture, or representative fish
and shark navigation crossing through an algae patch. Those acceptance rows
remain open even though the existing server-safe tests pass.
