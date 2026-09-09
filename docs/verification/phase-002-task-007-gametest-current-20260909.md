# Phase 002 current source GameTest evidence

Date: September 9, 2026

The exact checked in Phase 002 source revision `02671b000f0436e62ba8bc026aef14afa2cff3ea` passed the complete Forge GameTest discovery on headless node 1. This rerun was performed after the diagnostic only assertion change used by the earlier exploratory run had been reverted. No client, renderer, display, or graphical runtime was started.

Environment:

* Minecraft 1.20.1
* Forge 47.2.0
* Java 17.0.19
* EULA file preloaded and verified as `eula=true`
* disposable runtime `/tmp/bfsm-p002-current-r34.yXZQDd`

Command:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain -PbfsGameTestRunDir=/tmp/bfsm-p002-current-r34.yXZQDd
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
| `logs/latest.log` | `49b3fe1e4905bd4270ca27ea27f0fc561c4609264001debe310adeb96f612f5b` | `08a8140486d5504936a764b808f1452e4a0686023242f187bd08dbfbbd9f68144a0a60bbbed42389bf3819459812138658597cbed442a1750e0038aa090a0a3b` |
| `logs/debug.log` | `fcc9450a63422be57c71810ce8c4b6e8461678bd428c1e79c7bcf60a035c89ce` | `993cabcaae324a62b9b691cdc7b87fa04b30647c4eff958ea073c9d4524c6e9839646bb1bd1665874bdd0ba07fb98cde118162c60890a32e0652a828b0593b29` |

The run was stopped by the GameTest harness after saving all dimensions. The disposable runtime, worlds, generated configuration, and debug captures were removed after the log and result markers were inspected. Cleanup was verified. This closes the exact current source automated GameTest gate only. Packaged production server parity, laptop visual approval, multiplayer lifecycle, performance soak, documentation review, and phase integration remain open.
