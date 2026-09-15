# Phase 006 dive GameTest evidence

The isolated server fixture was run from the exact Phase 006 worktree with:

```text
./gradlew :forge:GameTestServer --no-daemon -PbfsGameTestNamespaces=bfsdive
```

The disposable runtime was `forge/run/bfs2-p006-dive-gametest-20260915p`. All four required `bfsdive` tests passed.

* `fullSuitEligibilityAndLandlikeTravel` passed the full four piece predicate, body water contact, one owned travel decision, gravity, and the grounded jump edge.
* `oxygenReserveUsesOnlySubmergedTicksAndRealAirRefill` passed the 6,000 tick submerged boundary, zero reserve behavior, 300 refill ticks at 20 ticks per tick, and partial suit retention.
* `oxygenSchemaRepairsCorruptionAndPreservesNewerState` passed missing, corrupt, and newer schema handling.
* `waterWorkKeepsMatchingBreakSpeed` passed matched submerged and dry break speed.

The runtime and its generated logs were removed after the final consumer. The protected pre-existing `forge/logs/` directory and `performance-matrix-20260912-interval50/` directory were not changed.

The test proves server behavior only. Worn geometry and player input were checked separately on the `envision` laptop with an NVIDIA GeForce RTX 5090 Laptop GPU. The client reached a Forge 47.2.0 singleplayer world, equipped the exact dive item IDs through server commands, and displayed the supplied suit in the inventory preview. The client was stopped and its exact runtime was removed. The client stream was muted before interaction and was absent after shutdown.
