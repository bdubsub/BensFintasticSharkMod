# Living Species Action and Presentation Inventory

This Phase 000 baseline enumerates every registered living BFS species. It excludes the nonliving `shark_trident` projectile. The deterministic inventory test requires all 22 entries to retain an entity registration, a `/bfs info` species card, and a Forge renderer registration. It does not claim client presentation has passed. The required authenticated laptop state captures remain open.

The action column names the current server authority only. It is a code inventory, not a claim that the behavior is complete or realistic. The owning future phase must close the stated behavior and presentation evidence.

| Species ID | Server action source | Client presentation binding | Baseline status and owner |
| --- | --- | --- | --- |
| `great_white_shark` | `GreatWhiteSharkEntity` with `AbstractSharkEntity` shared shark scheduling | `GreatWhiteRenderer` | Static inventory only. `BFS-PHASE-001` retained shark audit. |
| `great_hammerhead_shark` | `GreatHammerheadSharkEntity` with `AbstractSharkEntity` shared shark scheduling | `GreatHammerheadRenderer` | Static inventory only. `BFS-PHASE-001` retained shark audit. |
| `common_thresher_shark` | `CommonThresherSharkEntity` with `AbstractSharkEntity` shared shark scheduling | `CommonThresherRenderer` | Static inventory only. `BFS-PHASE-001` retained shark audit. |
| `shortfin_mako_shark` | `ShortfinMakoSharkEntity` with `AbstractSharkEntity` shared shark scheduling | `ShortfinMakoRenderer` | Static inventory only. `BFS-PHASE-001` retained shark audit. |
| `tiger_shark` | `TigerSharkEntity` item curiosity, target, bite, and recovery state | `TigerSharkRenderer` | Server GameTest baseline exists. Interactive pursuit and presentation remain open in `BFS-PHASE-002`. |
| `oceanic_whitetip_shark` | `OceanicWhitetipSharkEntity` target, grab, thrash damage, and bounded release | `OceanicWhitetipSharkRenderer` | Structural clip audit and server grab baseline exist. Interactive state and cleanup matrix remain open in `BFS-PHASE-002`. |
| `sandtiger_shark` | `SandtigerSharkEntity` with `AbstractSharkEntity` shared shark scheduling | `SandtigerSharkRenderer` | Static inventory only. `BFS-PHASE-001` retained shark audit. |
| `blacktip_reef_shark` | `BlacktipReefSharkEntity` with `AbstractSharkEntity` shared shark scheduling | `BlacktipReefSharkRenderer` | Static inventory only. `BFS-PHASE-001` retained shark audit. |
| `orca` | `OrcaEntity` surface air and breach sequence | `OrcaRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `bottlenose_dolphin` | `BottlenoseDolphinEntity` air, breach, boat follow, and item play sequence | `BottlenoseDolphinRenderer` | Reference movement is server tested. Client presentation remains open. |
| `common_octopus` | `CommonOctopusEntity` ink, threat flee, hide state, and smoothed body pitch | `CommonOctopusRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `caribbean_reef_octopus` | `CaribbeanReefOctopusEntity` ink, threat flee, hide state, and smoothed body pitch | `CaribbeanReefOctopusRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `nautilus` | `NautilusEntity` rest state and defensive descent | `NautilusRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `giant_moray_eel` | `GiantMorayEelEntity` defensive lunge and delayed impact | `GiantMorayEelRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `green_sea_turtle` | `GreenSeaTurtleEntity` aquatic wandering and avoidance memory | `GreenSeaTurtleRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `american_lobster` | `AmericanLobsterEntity` seafloor movement and delayed snip impact | `AmericanLobsterRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `common_stingray` | `CommonStingrayEntity` SmartBrainLib movement and defensive sting | `CommonStingrayRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `harbor_seal` | `HarborSealEntity` SmartBrainLib movement and idle scheduling | `HarborSealRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `black_sea_nettle_jellyfish` | `BlackSeaNettleJellyfishEntity` drift and contact damage | `BlackSeaNettleJellyfishRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `cannonball_jellyfish` | `CannonballJellyfishEntity` drift and contact damage | `CannonballJellyfishRenderer` | Static inventory only. `BFS-PHASE-001` retained species audit. |
| `atlantic_cod` | Vanilla `Cod` behavior through `AtlanticCodEntity` | `AtlanticCodRenderer` | Five point transform baseline exists. Fish state capture remains open in `BFS-PHASE-002`. |
| `atlantic_salmon` | Vanilla `Salmon` behavior and exact `Spin` name state through `AtlanticSalmonEntity` | `AtlanticSalmonRenderer` | Five point transform baseline exists. Fish state capture remains open in `BFS-PHASE-002`. |

`BfsSpeciesInfo` still exposes `TBD` as the diet for the nonshark species listed above except Atlantic Cod and Atlantic Salmon. This is an observed presentation-data gap, not a Phase 000 product change. `P001-TASK-006` owns exact diet and species-card verification. The inventory test intentionally preserves that baseline rather than concealing it.

Source authority is `ModEntityTypes`, `BfsSpeciesInfo`, `ModClientForge`, and the individual common entity classes. The unit assertion is `ReleaseContractAuditTest.everyLivingSpeciesHasActionAndPresentationInventoryEntries`.
