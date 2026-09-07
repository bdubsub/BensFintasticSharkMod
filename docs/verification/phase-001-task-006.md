# Phase 001 task 006 evidence

## Scope

This record covers `P001-TASK-006` and `BFS-REQ-005` for species information cards, authoritative replacement habitats, diet metadata, and the Oceanic Whitetip natural spawn biome contract.

## Implementation

`/bfs info` now derives replacement habitats from the live biome registry and the vanilla spawner entries for Atlantic Cod and Atlantic Salmon. The command no longer depends on a duplicated habitat literal map. The retained nonshark showcase cards contain exactly the twelve planned entries with `TBD` diet values. The card output exposes the scientific name, habitats, behavior, diet, health, variants, registry id, spawn category, natural spawning mode, and natural cap.

The Oceanic Whitetip spawn tag remains data generated for Deep Ocean and Deep Lukewarm Ocean only. Deep Cold Ocean is intentionally excluded. The command and generated data use the same species registry and tag identifiers used by runtime registration.

## Deterministic evidence

The dedicated Forge GameTest harness ran headlessly on node 1 with Java 17, Minecraft 1.20.1, Forge 47.2.0, GeckoLib 4.4.7, and SmartBrainLib 1.14.2. No graphical client was started. The source compiled successfully with:

```text
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
```

The focused release contract audit passed with:

```text
./gradlew :forge:test --tests tfar.bensfintasticsharks.audit.ReleaseContractAuditTest --no-daemon
```

Data generation completed successfully with:

```text
./gradlew :forge:Data --no-daemon
```

The full headless GameTest command was:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestRunDir=forge/run-task006-gametest
```

All 37 required GameTests passed. The `bfs_info_cards` test captured the command output for the twelve `TBD` entries, Oceanic Whitetip, Atlantic Cod, and Atlantic Salmon; verified the sorted Deep Lukewarm Ocean and Deep Ocean habitat result; verified replacement mode text; verified unknown species and permission failures; and checked the live biome tag membership for Deep Ocean, Deep Lukewarm Ocean, and Deep Cold Ocean.

## Cleanup and limits

The GameTest runtime and its generated world, logs, captures, and temporary EULA are disposable phase evidence and must be removed after the final consumer. The protected `build.gradle` line ending change and pre-existing untracked `forge/logs/` directory are preserved.

This task supplies deterministic command, registry, generated-data, and tag evidence. Interactive client, multiplayer, natural population sampling, and final packaged artifact gates remain open in later Phase 001 tasks. This record does not claim Phase 001 or the release candidate is complete.
