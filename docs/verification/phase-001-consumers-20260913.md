# Phase 001 movement and lifecycle consumer evidence

Date: 2026-09-13

Status: implementation in progress. This record covers the current phase branch only. It does
not claim the server GameTest, dedicated server, client, pull request, merge, artifact checksum,
or phase tag gates.

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
effective base and sprint values.

## Verification completed

The following commands passed on the phase branch.

```text
./gradlew :forge:compileJava :forge:compileTestJava :forge:test --no-daemon --console=plain
```

The unit suite covers the 22 species adapter inventory, atomic settings behavior, and the
independent anisotropic movement equation. Test-created `forge/logs` output was removed after the
run. No graphical client or dedicated server was launched for this check.

## Gates still open

The required movement oracle still needs real server fixtures for every registry entry, matched
200 tick captures, pursuit and flee state transitions, and external impulse assertions. Scale,
spawn, attribute, and body envelope GameTests still need their named terrain and lifecycle fixtures.
The silent laptop visual gate, complete diagnostics parser evidence, full phase documentation review,
pull request checks, merge, default branch verification, and signed phase tag remain open.
