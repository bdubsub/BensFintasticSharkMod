# Phase 002 target memory evidence

Date: September 9, 2026

This evidence records the bounded target memory cleanup added in commit
`a473afb41df75c19cfbd31b31c706a429f61cdfa` on `envy/0.24-phase-002`.

`SmartWaterAnimal` now owns an ephemeral remembered entity reference and a
bounded memory countdown. `SpeciesBehaviorEngine` records the target for
escape, social, and live food routes. Block, surface, and floor routes do not
invent an entity target. The route is cleared when the target is removed,
dead, in another level, or when the profile memory expires. Entity removal
clears both the walk target and the remembered reference. The state is not
serialized, so it cannot change saved entity data or create a stale target
after reload.

The focused GameTest
`speciesPolicyReleasesSocialRouteWhenTargetDisappears` creates two
no-AI Bottlenose Dolphins, observes the shared social route, kills the
remembered neighbor, and verifies that the actor returns to the `none` action
within five server ticks. The target-loss assertion passed in the 70-test
runs `r30` and `r31` on node 1 with Java 17.0.19, Minecraft 1.20.1, Forge
47.2.0, and `eula=true`.

The first `r29` run correctly failed the new assertion and exposed that the
fixture used a discard path that was not deterministic for this lifecycle
check. The fixture now uses the production kill lifecycle and includes the
observed action and removal state in any failure message.

The unit suite passed with:

```text
./gradlew :forge:test --no-daemon --console=plain
```

The complete 70-test server suite remains open. The target-memory test passed
in the retained runs, but each complete run also exposed one unrelated
retained fixture failure, first the fishing roundtrip in `r30` and then the
Oceanic target-invalidation fixture in `r31`. Those failures are not recorded
as passing evidence and must be resolved or reproduced independently before
the Phase 002 exit gate can close.

The disposable runtimes `r29`, `r30`, and `r31` were stopped and removed after
inspection. The protected `build.gradle` change and untracked `forge/logs/`
directory remain untouched and were not staged.

This is not phase completion. The full species behavior matrix, animation
structure and visual review, packaged production parity, multiplayer
reconnect checks, performance soak, documentation review, and phase
integration gates remain open.
