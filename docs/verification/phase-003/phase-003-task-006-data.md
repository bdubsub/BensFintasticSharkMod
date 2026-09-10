# Phase 003 task 006 data generation

Date: September 10, 2026

P003-TASK-006 regenerated the complete data tree twice after the icon, graph,
copy, and audit changes. Both runs completed successfully and produced no
tracked non-cache generated-resource diff.

## Determinism result

The final generated tree contains 301 non-cache files. Its deterministic
path-and-content tree SHA-256 is
`3d744bfe4a720c414491afb35ccc25912eca915e25aa80890e1002b5a900f3e9`.

Forge's datagen cache writes timestamp and cache-entry changes during each
run. Those disposable cache changes were excluded from the source result and
restored to the protected baseline after the second run. The pre-existing
owner `build.gradle` line-ending change and `forge/logs/` directory were not
touched.

## Verification

* First run, `./gradlew :forge:Data --no-daemon --console=plain`, passed.
  Log SHA-256:
  `a514e95d2aa4cb7f3dce0f9980119bf1e07077c879d33aa143026a5011d7acee`.
* Second run, the same command, passed.
  Log SHA-256:
  `fbd084545dd83d8bce56ce9d928fcd9e4950c85230cb44a14ea8021b8e07edda`.
* `git diff --name-only -- common/src/generated/resources`, excluding the
  disposable `.cache` paths, returned no files after the second run.

The two disposable datagen logs were removed after their hashes were recorded.
The generated output is ready for the ordered runtime and packaging gates.
