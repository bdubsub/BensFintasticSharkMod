# Phase 002 Task 007 headless regression evidence

Date: September 9, 2026

The complete Forge GameTest suite passed on the exact Phase 002 candidate source revision after the focused fixture corrections. The run used the dedicated Forge GameTest server on headless node 1 with Minecraft 1.20.1, Forge 47.2.0 and Java 17.0.19. No client, renderer or display was started.

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain \
  -PbfsGameTestRunDir=/tmp/bfsm-p002-fish-20260909-r6

========= 64 GAME TESTS COMPLETE ======================
All 64 required tests passed :)
BUILD SUCCESSFUL in 3m 35s
```

The passing run includes the fishing transaction matrix, fish and dolphin movement diagnostics, Cod and Salmon presentation states, Oceanic Whitetip grab, bite and terminal cleanup, Tiger pursuit and curiosity, replacement population bounds, spawn controls, debug capture lifecycle and permission boundaries, advancement signals, algae behavior, armor, and the retained Phase 001 regression coverage.

The exact disposable runtime had `eula=true` before launch. Its final log SHA-256 is `a94435b53701f5d94f941366b32330ff39bfd0b8bbfb967417a3fc932ecca85c`. The source fixture file at the run revision has SHA-256 `55f372389d2e4c3a28d4a2c9f918d81518605d8b5be6209a8b6e4348402f017b`.

The earlier bounded runs exposed and corrected only fixture timing, gravity stabilization, population-cap isolation and a brittle type-selector dependency. The final run passed those paths. The runtime and its logs were removed after inspection and cleanup was verified. This evidence closes the current deterministic and GameTest gate only. Packaged production-server, laptop visual, multiplayer, review, merge and signed-tag gates remain open for Phase 002.
