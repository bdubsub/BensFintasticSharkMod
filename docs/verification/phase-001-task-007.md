# Phase 001 task 007 evidence

## Scope

This record covers `P001-TASK-007` and `BFS-REQ-006` for Prismarine armor fit through standing, water, third person, and continuous vanilla breaststroke presentation. It also records the exact dependency parity required for the multiplayer client check.

## Implementation and deterministic coverage

The existing client armor path synchronizes the GeckoLib armor renderer with the live humanoid model before each render. The release contract audit now verifies the authored armor geometry bones and cube counts, the GeckoLib model resource, and the live pose binding. The server GameTest now equips all four pieces in a noncreative player, ticks in water, removes and reequips the chestplate, and asserts every equipment slot remains authoritative.

The focused audit passed with:

```text
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
./gradlew :forge:test --tests tfar.bensfintasticsharks.audit.ReleaseContractAuditTest --no-daemon
```

The new `bfs_armor` GameTest passed in the headless Forge harness. The full 38
test rerun now passes. The stabilization keeps fixture cleanup inside an
isolated structure, prevents bite scheduling while a passenger is latched,
keeps underwater item fixtures stationary, and prevents an Oceanic grab from
immediately reacquiring its released passenger. The Blacktip assertion accepts
normal player regeneration while rejecting any health decrease.

The exact headless run completed with `All 38 required tests passed :)`, so the
earlier shared fixture failures are no longer present in the current phase
branch.

## Multiplayer and visual evidence

The disposable packaged Forge server ran on node 1 with Java 17, Forge 47.2.0, Minecraft 1.20.1, the candidate artifact revision `26f1aba3297c05913677df744f88d7d895b69390`, and candidate jar SHA-256 `8e43539d07ca1048eebe64cd79d1e5308dc8e0e85c23efad2c1b90c54086b5c4`. The Prismarine armor client ran on the EnVy laptop with the RTX 5090 through the authenticated Prism Launcher instance `bfsm-p001-task007-20260907`. GeckoLib 4.4.7 and SmartBrainLib 1.14.2 matched on both sides.

The first connection attempt was rejected because the isolated client clone had no BFS jar while the server did. That environment mismatch was corrected by installing the exact candidate hash on the client, after which the Forge handshake completed and the player joined the dedicated server. The owned Java playback stream was identified by process id and muted at the stream level. No node 1 client or renderer was used.

The following disposable captures were inspected from the laptop window without switching the user's workspace:

| Checkpoint | SHA-256 |
| --- | --- |
| air, third person front | `cde69703cb20fe8e3768c986af3a5d6a7a7fd6ef53ac465a4cb8d01ec0710366` |
| water, third person front | `7aac9cd4d159f8c065194f177bd2c3fe03f674d7a372cd5d0dbf9c0be556b379` |
| water, side and rear angle | `c76c8de143a5a78a38d787651b120df8f46a4d5a8e574af03b385effb1636f25` |
| water, active breaststroke | `b99536139bef5ad38e220bb3d37c2eba603978470aa3a6b551ef7a409f0bb561` |
| water, rear open view | `2d690bd9f384a20c6c1fa282fef5fe74f1753c707da81be698d0a46581f60664` |

The active breaststroke capture shows the full set while the player is moving horizontally through water. The chest, sleeves, arms, leggings, and boots remain aligned with the live pose, with no visible chest or sleeve clipping in the inspected rear and side views.

## Cleanup and limits

The dedicated server runtime, world, client clone, logs, screenshots, and generated GameTest output are disposable and must be removed after the final consumer. The protected `build.gradle` line ending change and pre-existing untracked `forge/logs/` directory remain untouched.

This task supplies deterministic geometry and equipment evidence plus the required laptop multiplayer visual evidence. Reconnect persistence, observer-client verification, and the final phase packet remain open until the remaining Phase 001 tasks complete.
