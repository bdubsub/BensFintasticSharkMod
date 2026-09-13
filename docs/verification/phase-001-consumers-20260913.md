# Phase 001 movement and lifecycle consumer evidence

Date: 2026-09-13

Status: local implementation and headless verification complete on the phase branch. This record
does not claim the silent laptop client, pull request, merge, default branch, or phase tag gates.

## Implemented consumers

The session settings snapshot now reaches one recorded movement adapter for every current BFS
species. Shark and Atlantic fish routes use the pitch movement controller. The common aquatic base,
lobster, turtle, stingray, seal, and jellyfish drift paths use the same independent axis resolver.
External velocity remains separate from the settings powered vector at each adapter boundary.

The powered vector follows the phase interface equation. A normalized intent uses the horizontal
base and horizontal sprint multiplier for x and z, and the vertical base and vertical sprint
multiplier for y. Sprint is active only for pursuit or flee states. Cruise does not apply sprint
multipliers.

Natural group admission reads the session minimum and maximum. Administrative summon and spawn egg
paths do not use group expansion. Behavior scan radius, action timeout, memory ticks, shark detection,
and disengage values now read from the effective settings snapshot.

Scale ranges retain the existing species defaults in the baseline. The common aquatic scale is
persisted and synchronized, and Atlantic fish, stingray, and seal now persist and apply the same
normalized scale to dimensions. Attribute application stores each unmodified base once, then
recomputes the effective value without compounding on repeated entity joins. Health keeps its
current fraction when the effective maximum changes.

Pitch route clearance now evaluates an expanded scaled body envelope and checks water at the body
and fin sample points. A route that would expose the body or collide with terrain is rejected before
the movement step, including a shallow surface approach.

Movement diagnostic records include the settings revision, adapter, writer, state source, and the
effective base and sprint values. The shared powered velocity diagnostic source now covers the
common aquatic base and its jellyfish, turtle, dolphin, octopus, eel, lobster, orca, and shark
consumers, as well as the stingray and seal travel writers. Movement records therefore expose the
powered and external vector split for every current movement writer, not only the pitch controllers.

## Verification completed

The following commands passed on the phase branch.

```text
./gradlew :forge:compileJava :forge:compileTestJava :forge:test --no-daemon --console=plain
```

The unit suite covers the 22 species adapter inventory, atomic settings behavior, and the
independent anisotropic movement equation. The added independent oracle test exercises every
adapter catalog entry across the three required base pairs, three signed normalized intents, and
the sprint multiplier pairs. The complete Forge data generation task also passed without generated
resource drift.

The existing `bfs-debug-v2` parser self-test also passed.

```text
python3 -B tools/test_bfs_debug_analyze.py
Ran 18 tests in 0.001s
OK
```

The required headless GameTest run passed all 89 required tests on the phase branch.

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain -PbfsGameTestRunDir=/tmp/bfsm-bfs2-p001-gametest-r53
All 89 required tests passed :)
BUILD SUCCESSFUL in 4m 7s
```

That run covered the movement diagnostics, algae navigation, pitch settling and progress,
curiosity pursuit and recovery, blocked route handling, combat latch and bite timing, lifecycle
reload, spawn replacement, population bounds, fishing, armor, the all species movement adapter
inventory fixture, and the existing baseline fixtures.
The disposable GameTest runtime and logs were removed after review. No graphical client was
launched.

After adding the shared powered velocity diagnostic source, a focused server GameTest run passed
all 22 required tests. It included the five existing depth movement captures and the all species
movement adapter inventory fixture, plus the other debug and fishing fixtures that share the same
diagnostic writer. The long population soak and unrelated world-generation batches were excluded
from this focused run because the full 24,000 tick soak is a separate phase gate and the algae
navigation fixture is independently flaky on this host.

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain -PbfsGameTestRunDir=/tmp/bfsm-bfs2-p001-gametest-r57
All 22 required tests passed :)
BUILD SUCCESSFUL in 54s
```

The focused disposable runtime and logs were removed after review.

A disposable dedicated Forge server smoke also passed on node 1. The exact runtime used
`server-port=25641`, `online-mode=false`, and `eula=true`. The server reached the `Done` readiness
marker, then stopped cleanly and saved all three dimensions. The runtime and its logs were removed
after review. This verifies headless startup and shutdown only; it does not replace the required
laptop client gate.

The Forge build and archive checks also passed.

```text
./gradlew :forge:build --no-daemon --console=plain
forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar
sha256 4d153d3cbc5cef3d62cf4f18fb57c934e02c608a5a9feaf26110650f3ac8cdb9
sha512 20ce5747eee5bda2c04ccae9ca829b4bf226009e8bf77dbec83417e28204e5f5bf386202911da176594b513bba6d5a199770bf7e79b76a6dec4797a660e26fcc
```

`unzip -tqq` passed for the Forge jar. The checksum identifies this local candidate artifact;
the final phase evidence will bind a newly built artifact to the merged default branch and signed
phase tag.

## Gates still open

The independent movement oracle still needs matched 200 tick captures for every registry entry,
including pursuit and flee state transitions and external impulse assertions. The silent laptop
visual gate for the scaled pitched body remains open. Complete diagnostics parser evidence, the
private independent review, pull request checks, merge, default branch verification, and signed
phase tag remain open.
