# Phase 002 current source GameTest evidence

Date: September 10, 2026

The complete Forge GameTest discovery passed on the current Phase 002 source
revision `f3be18f`. The run used the dedicated Forge GameTest server on
headless node 1 with Minecraft `1.20.1`, Forge `47.2.0`, Java `17.0.19`, and
an exact disposable runtime with `eula=true`. No client, renderer, display, or
graphical runtime was started.

Command:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain \
  -PbfsGameTestRunDir=/tmp/bfsm-p002-current-gametest-Bkjqrq
```

Result:

```text
========= 82 GAME TESTS COMPLETE ======================
All 82 required tests passed :)
BUILD SUCCESSFUL in 3m 24s
```

The suite includes the current fish replacement creation-source matrix,
fishing transactions, fish and dolphin movement diagnostics, Cod and Salmon
presentation, Oceanic grab and cleanup, Tiger pursuit and combat, species
policy and target memory, octopus lifecycle, population bounds, replacement
controls, algae, advancement, debug lifecycle, and retained Phase 001
regressions.

The final disposable runtime log hashes were:

| File | SHA-256 | SHA-512 |
| --- | --- | --- |
| `logs/latest.log` | `809d0700c55c44ca9e22cf6c639ae4142c2e8f46c503b2c61c8e4cec7792296b` | `fbcd51b4d25e76277f3bfd9e8c0e0bc51de9b33c96e97ba507d296f03f7697943f9287c9fc3cfa926509af95e93e22e29ca3fa322d7b9db7a1f5df4e7084e332` |
| `logs/debug.log` | `8da5c78e28273d572807477f039c2a8d500f90bb270c484ba3cb739f6de95e81` | `fe46e9a1dca45f7e686a4740f924d3b5cbb9d3dbde491c60721364767d05427a68970c79ee2767219ebc2116672c0f3cc9e88a3287588ab0279b5fc732fd4a08` |

The runtime and all generated worlds, configuration, debug captures, and logs
were removed after the result and hashes were recorded. Cleanup was verified.
This closes the current deterministic and GameTest gate only. Packaged
production-server parity, laptop visual approval, multiplayer lifecycle,
performance soak, documentation review, phase integration, and signed-tag
gates remain open.
