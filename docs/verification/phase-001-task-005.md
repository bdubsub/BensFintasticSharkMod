# Phase 001 task 005 evidence

## Scope

This record covers `P001-TASK-005` and `BFS-REQ-004` for the server side grab and latch lifecycle. The primary workflows begin with real bites. Oceanic Whitetip uses the existing grab timer and periodic thrash damage. Blacktip Reef uses its bounded latch and initial bite damage only.

## Implementation

Both grabber species now share an authoritative release helper that clears the timer, ejects every passenger, and resends the empty passenger packet to a server player. Oceanic Whitetip starts the grab contract on every successful bite, clears its hunt target, and holds a bounded post grab cooldown so timer expiry cannot immediately reacquire the same passenger. Release is invoked for timer expiry, missing or invalid target, leaving water, dead passenger, shark death, entity removal, player logout, dimension change, respawn, entity level removal, and server stopping. The Forge lifecycle listeners release the vehicle immediately when a player leaves or changes lifecycle state.

## Deterministic evidence

The dedicated Forge GameTest harness ran on the headless node 1 host with Java 17, Minecraft 1.20.1, Forge 47.2.0, GeckoLib 4.4.7, and SmartBrainLib 1.14.2. The exact command was:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks
```

The suite completed successfully with all 38 required tests passing. It includes:

* real bite to Oceanic Whitetip grab, server thrash damage, and normal timeout release;
* real bite to Blacktip Reef latch, initial damage, no periodic latch damage, and timeout release;
* target invalidation, shark leaving water, player death, and shark removal release probes;
* shared vertical movement and finite entry arc regression coverage;
* the previously established movement, bite, curiosity, and debug capture tests.

The Blacktip no periodic damage assertion allows normal survival player
regeneration while still rejecting any health decrease. The disposable fixtures
use isolated 40 by 24 by 40 structures and remove only nonplayer entities in
their own structure.

The source compiled successfully before the GameTest run with:

```text
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
```

The disposable `forge/run` GameTest runtime was removed after the run. The protected `build.gradle` line ending change and pre-existing untracked `forge/logs/` directory were preserved.

## Remaining phase gates

This task has not yet supplied interactive client or multiplayer capture evidence. Camera reset, reconnect, dimension transfer, disconnect, and packaged-client dependency parity remain required in later Phase 001 verification and integration tasks. This record does not claim those gates are complete.
