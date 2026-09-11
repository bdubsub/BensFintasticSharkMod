# Phase 005 Task 008. Data and GameTest gate

## Scope

This gate verifies deterministic data generation and the complete dedicated server GameTest suite for the Phase 005 release candidate baseline. It covers the registered data resources, algae world generation fixtures, fishing replacement behavior, movement diagnostics, species behavior, combat, and lifecycle assertions.

## Data generation

The checked in wrapper ran the data task twice on `node-1`.

```text
./gradlew :forge:Data --no-daemon --console=plain
./gradlew :forge:Data --no-daemon --console=plain
```

The first run generated the existing resource set. The second run wrote zero changes. The two generated hash manifests were identical with SHA256 `260723e91eb584d82e55d9e3a444aca008faeb849ed3b471c257ebf36284df04`, and `git diff --exit-code -- common/src/generated/resources` passed after both runs. Forge and GeckoLib development warnings were present but no data task failure occurred.

## GameTest execution

The suite used a disposable runtime on `node-1` with an explicit `eula=true` file. The exact runtime was `/tmp/bfsm-p005-task008-gametest-final.6z2bFe`, and the server selected Java `17.0.19` for Minecraft 1.20.1 and Forge 47.2.0.

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain \
  -PbfsGameTestRunDir=/tmp/bfsm-p005-task008-gametest-final.6z2bFe
```

Result:

```text
========= 85 GAME TESTS COMPLETE ======================
All 85 required tests passed :)
BUILD SUCCESSFUL
```

The final log SHA256 was `c45e266a893a5b2b4ca52779b20de63ddf9237e9735182d64a1c5fa342c61c56`.

## Fixture determinism repair

Two fixture isolation gaps were repaired in `BfsGameTests`.

The tiger shark algae traversal now runs in its own batch instead of concurrently with the Atlantic Cod traversal. Both algae fixtures clear only their own structure area before spawning entities. The tiger curiosity timeout fixture now clears inherited aquatic entities before creating its shark and item. These changes prevent neighboring GameTest entities from changing prey scans or movement ownership while preserving the gameplay assertions.

The first two full-suite probes exposed those isolation problems with different failures. The final full run passed all 85 required tests after the bounded fixture repair.

## Cleanup

The disposable GameTest runtime and its generated world, configuration, logs, and EULA file were removed after the evidence was recorded. Temporary data-generation manifests and logs were also removed. No generated resource drift remains.
