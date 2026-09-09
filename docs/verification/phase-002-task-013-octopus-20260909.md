# Phase 002 octopus behavior evidence

Date: September 9, 2026

This evidence records the bounded defensive behavior implemented for both
registered octopus species under `P002-TASK-013`.

The server now chooses an away-biased route from a bounded horizontal fan. Each
candidate is sampled through water and checked against the entity collision box
before it is accepted. A valid route starts one twenty tick jet with bounded
acceleration and a maximum speed of `0.32` blocks per tick. The emission
cooldown is 1,200 ticks. No safe route denies the jet and leaves the octopus
available for local retreat. Suppressed-AI fixtures use the same authoritative
movement path, so the escape is not dependent on a brain tick.

The ink registry owns one server cloud per octopus. Clouds expire after 80
ticks, emit no more than 32 particle births, and expose a synchronized event
identity and lifetime. Visual threat checks respect water-valid cloud rays and
solid barriers. Entity removal clears the cloud and jet state.

The focused regression is `octopusThreatUsesBoundedSafeJetAndCooldown` in the
dedicated Forge suite. The fixture waits for the production twenty tick
proximity sampling interval, verifies cloud creation, verifies physical retreat
and the finite jet window, then verifies cloud expiry. The suite also retains
the finite cloud, camouflage, support removal, advancement, fish, shark, and
population regressions.

Final command:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain -PbfsGameTestRunDir=/tmp/bfsm-p002-octopus-20260909-r27
```

Result: all 68 required Forge GameTests passed on node 1 with Java 17,
Minecraft 1.20.1, Forge 47.2.0, and `eula=true`. The disposable runtime was
stopped and removed after the log was inspected. The Java unit suite also
passed with `./gradlew :forge:test --no-daemon --console=plain`.

This is not phase completion. The complete 22-species behavior matrix,
packaged production server, laptop visual approval, multiplayer reconnect
evidence, performance soak, documentation review, and phase integration gates
remain open.
