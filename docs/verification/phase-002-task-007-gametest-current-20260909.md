# Phase 002 current source GameTest evidence

Date: September 9, 2026

The Phase 002 source working tree at branch head `1a91baa3ced4cbe27674d129c98cc0cd93f147aa`, with the scoped target invalidation and walk target cleanup change recorded below, passed the complete Forge GameTest discovery on headless node 1. No client, renderer, display, or graphical runtime was started.

Scoped change under verification: delayed bite victims and active or remembered targets are rejected when removed, dead, or from another level. Clearing such a target also clears the shark owned `WALK_TARGET` memory. The production change is covered by the existing target loss and recovery GameTests; a separate discard fixture was rejected as harness invalid because its out of scope shark was removed by the test runtime before the assertion.

Environment:

* Minecraft 1.20.1
* Forge 47.2.0
* Java 17.0.19
* EULA file preloaded and verified as `eula=true`
* disposable runtime `/tmp/bfsm-p002-gametest-20260909-r6`

Command:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain -PbfsGameTestRunDir=/tmp/bfsm-p002-gametest-20260909-r6
```

Result:

```text
========= 70 GAME TESTS COMPLETE ======================
All 70 required tests passed :)
BUILD SUCCESSFUL in 3m 58s
```

The run included the current fishing transaction, fish and dolphin movement diagnostics, Cod and Salmon presentation, Oceanic grab and cleanup, Tiger movement and combat, species policy and target memory, octopus lifecycle, population bounds, replacement controls, algae, advancement, debug lifecycle, and retained Phase 001 regression fixtures. The normal runtime log contained no error, exception, missing class, mixin failure, registry failure, resource failure, or scheduler failure markers. The harness emitted bounded fixture warnings for block entity cleanup and one intentional fishing settlement limit; neither produced a failed test.

The final dedicated runtime log hashes are:

| File | SHA-256 | SHA-512 |
| --- | --- | --- |
| `logs/latest.log` | `24118776608a7b6b1020f902a7a0cdf6a00de493f8d81d7836d8e7eb946e43ae` | `e365c9add63df860eef51860c6d22604444ae818ca749de8cf19227dcf40f5bd3a6d461bf027b8b45f4343f028a8e06fabd01b03e0c3ccd164ef07d08dfd5c33` |
| `logs/debug.log` | `9d6a01e3216b16d665825a7e5f95549c46ee0778261e63d6dad33351117154fb` | `36cd2410d9fa3f6c2ae8b46a7c628103aa5273ba655de2067495a8ca1ec741cfced9cb1616691aab41b4996598db82e27b4e9c38c33019731991a265b39116fc` |

The run was stopped by the GameTest harness after saving all dimensions. The disposable runtime, worlds, generated configuration, and debug captures were removed after the log and result markers were inspected. Cleanup was verified. This closes the exact current source automated GameTest gate only. Packaged production server parity, laptop visual approval, multiplayer lifecycle, performance soak, documentation review, and phase integration remain open.
