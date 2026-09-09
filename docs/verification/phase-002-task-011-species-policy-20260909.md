# Phase 002 behavior policy evidence

Date: September 9, 2026

This evidence records the bounded shared behavior layer completed so far for
`P002-TASK-011`. `SpeciesBehaviorProfile` covers the 22 registered living
species and keeps locomotion, food, threat, habitat, social, surface and memory
limits in one server-side profile. `SpeciesBehaviorEngine` owns one finite
intent at a time for SmartBrainLib water animals and uses the native Forge
entity query limit of 32 candidates per scan. Atlantic Cod and Atlantic Salmon
use a separate bounded adapter for threat escape and same-species schooling.
Newly spawned fish receive a 20-tick settling window before a policy scan so
encounter triggers remain deterministic on their spawn tick.

Both octopus species now sample at most nine supported substrate positions every
20 ticks, select a server-safe map color, and synchronize a bounded concealment
target. The client blends toward the target over 40 ticks. Removing support
releases concealment without changing the variant or entity identity. Entity
removal clears the owned walk target and transient policy action.

The added GameTests cover social intent, passive jellyfish behavior, both
octopus camouflage rows, support removal, and the retained phase regressions.

Initial command:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain -PbfsGameTestRunDir=/tmp/bfsm-p002-species-camo-20260909-r9
```

Initial result: the required suite reached 66 tests before the later retained
phase fixtures were added. This run is retained as historical evidence only.

The same suite was rerun after the diagnostic parity fixture was corrected to
select its captured Cod by type and nearest distance ordering:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain -PbfsGameTestRunDir=/tmp/bfsm-p002-octopus-20260909-r14
```

The final rerun also passed after bounding item sensing and stabilizing the
retained advancement and Oceanic invalidation fixtures:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain -PbfsGameTestRunDir=/tmp/bfsm-p002-octopus-20260909-r17
```

Result: 67 required Forge GameTests passed on node 1 with Java 17.0.19,
Minecraft 1.20.1, Forge 47.2.0, and `eula=true`. The runtime was disposable
and was cleaned after this evidence was retained.

This is not phase completion. The 22-row natural behavior matrix, octopus ink
cloud and jet lifecycle, DEC-006 movement calibration, laptop visual approval,
multiplayer lifecycle, packaged production server, and final phase integration
remain open.
