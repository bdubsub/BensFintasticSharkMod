# Phase 001 movement oracle evidence

Date: 2026-09-13

Status: the phase branch passes the current headless movement and lifecycle gates. The
matching silent laptop client joined the disposable dedicated server and produced command and
presentation evidence recorded in `phase-001-client-visual-20260913.md`. The full client envelope
matrix, independent review, pull request, merge, default branch verification, and signed phase
tag remain open.

## Oracle coverage

The server GameTest movement oracle covers all 22 registered BFS species. Each species runs
the required anisotropic base pairs `(2,6)`, `(6,6)`, and `(6,2)` with matched normalized
intent, positive and negative directions, 100 settling ticks, and 200 measured ticks. Pursuit
and flee runs apply the independent sprint pairs `(1,1)`, `(1.2,1)`, and `(1.2,1.5)` while
cruise runs verify that sprint multipliers remain inactive. The fixture supplies intent to the
actual movement owner and compares measured displacement with the independently calculated
weighted vector. It does not assign final velocity.

The writer coverage fixture starts the real movement diagnostic capture across the same 22
species. It requires one recorded movement writer for every entity type, complete records with
no dropped events, and a separate external impulse on Atlantic Cod. The powered and external
components remain separately observable.

## Verification

The focused oracle and writer GameTest passed both required tests. The complete headless run
passed all 91 required tests, including the 24,000 tick population soak and the real fishing
round trip.

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks \
  -PbfsGameTestRunDir=/tmp/bfsm-bfs2-p001-pop-r147 --console=plain
All 91 required tests passed :)
BUILD SUCCESSFUL
```

The following deterministic checks also passed on this phase branch.

```text
./gradlew :forge:compileJava :forge:compileTestJava :forge:test --no-daemon --console=plain
./gradlew :forge:Data --no-daemon --console=plain
python3 -B tools/test_bfs_debug_analyze.py
Ran 18 tests in 0.001s
OK
```

The disposable GameTest runtime, logs, and parser scratch paths were removed after review. The
matching laptop client was launched only after the headless gates passed. Its server join, muted
owned audio stream, readable settings feedback, open surface rendering, and shallow ocean
rendering are recorded in `phase-001-client-visual-20260913.md`. The full terrain and scale
matrix remains unverified.

The Forge archive also passed `unzip -tqq` and the phase candidate built as
`forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar`.

```text
sha256 4a0ce9f0acc41f037444252471eff1390cf89beadf4b435f99edb8b8a715f4ff
sha512 984c3c90134bc5c3fe51c21f1ffb397bec12e583eef30faac9708a465dcc8b4f10dbbb33066d05a01d1ea35313608674ccfc0a5c8888fd60687eebdf52ad1
```

A disposable dedicated server smoke on node 1 reached the `Done` readiness marker in 16.092
seconds with `eula=true`, `online-mode=false`, and port `25641`. The owned server process then
stopped and saved all three dimensions before its wrapper was terminated after readiness. This is
startup evidence only and does not replace the laptop client gate.

## Remaining phase gates

The code and headless evidence are ready for the phase review, but phase 001 is not closed.
The independent review, pull request checks and merge into `1.20.1`, resulting default branch
verification, signed phase tag, and silent laptop visual acceptance must still be completed.
