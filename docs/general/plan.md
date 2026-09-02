# Ben's Fintastic Sharks Plan

## Source of truth

This plan tracks the active Forge 1.20.1 development work. Current maintainer requests and supplied assets take precedence over older release notes. Implemented code, generated resources, and verification evidence must agree before an item is considered complete.

## Active phase: 0.23 movement repair and Atlantic wildlife

### Purpose

Version 0.23 restores the stable shark movement baseline from 0.21, removes the regressions introduced in 0.22, corrects showcase information and Oceanic Whitetip habitat data, finishes the Prismarine armor swimming fit, replaces Oceanic Whitetip animations, and adds Atlantic Cod and Atlantic Salmon as passive BFS prey species.

### Compatibility and constraints

* Keep Minecraft 1.20.1, Forge 47.2.0, Java 17, GeckoLib 4.4.7, SmartBrainLib 1.14.2, and existing mappings unchanged.
* Forge remains the only supported playable loader. Common code must remain safe for dedicated servers.
* Preserve save compatibility and all stable resource identifiers from 0.22.
* Use the supplied 0.23 models, textures, item sprites, and Oceanic Whitetip animation file without redrawing or renaming authored bones.
* Natural spawn controls must not block commands, breeding, buckets, structures, or other nonnatural creation paths. The explicit vanilla Cod and Salmon spawn egg conversion is the only spawn egg exception.
* Shark movement repair must use the recovered 0.21 behavior as the baseline. Do not retain 0.22 steering changes that suppress valid vertical path corrections.
* Ben's supplied behavior and content notes are authoritative. Ambiguous implementation details should follow existing BFS conventions and vanilla fish balance.

### Phase 1. Movement and combat regression repair

#### Implementation

* Restore continuous shared shark steering. Remove the 0.22 level cruising branch that repeatedly suppresses vertical input, vertical momentum, and pitch while navigation still requires a depth change.
* Retain turn aware horizontal braking that prevents wide orbiting, but ensure arrival braking cannot trap sharks around an unreachable path node.
* Restore Sand Tiger and Blacktip Reef approach behavior so bite reach, navigation stopping, speed tapering, and braking are not all constrained by one undersized center to center radius.
* Separate physical bite contact from approach and braking distances when a species needs a shorter bite.
* Change Tiger Shark item curiosity to choose one stable, reachable underwater intercept point. Do not continuously chase the floating item's changing surface position or reacquire the same item every tick.
* End Tiger Shark investigation cleanly after its cosmetic bite, invalidation, timeout, or failed pathing. Preserve the existing behavior that leaves the item in the world. Add a short per item retry cooldown to prevent immediate orbiting.
* Preserve post feeding self defense unless verification proves it causes an independent defect.
* Connect the supplied Oceanic Whitetip `thrash` animation to the same server authoritative grab and thrash contract used by Great White, Great Hammerhead, and Shortfin Mako sharks.
* Give Blacktip Reef Sharks a smaller shark latch behavior instead of the large shark thrash. The initial bite remains its only latch damage. The latch must have bounded duration, escape and release conditions, death and unload cleanup, and client presentation without permanent camera or passenger state.
* Do not enable unauthored thrash animations for species whose replacement animation files are still pending. Keep their gameplay hooks isolated so authored clips can be connected later.

#### Acceptance criteria

* An idle shark can reach path nodes one and two blocks above or below it without alternating rapidly between climb and dive commands.
* Sharks cruise in a stable line when no vertical correction is needed.
* Sharks turn toward moving prey without sustained circles, figure eights, or repeated overshoot.
* Sand Tigers and Blacktips reach mouth distance, brake, and land delayed bites on stationary and moving prey across their supported size range.
* A Tiger Shark approaches a floating edible item, completes its cosmetic bite without consuming the item, and resumes normal navigation without spinning in place.
* Oceanic Whitetip thrashing and Blacktip latching always release the player under documented termination conditions.

### Phase 2. Showcase information and habitat correction

#### Implementation

* Keep `/bfs info` derived from content that is implemented in the mod.
* Display `TBD` as the diet for American Lobster, Black Sea Nettle Jellyfish, Common Bottlenose Dolphin, Cannonball Jellyfish, Caribbean Reef Octopus, Common Octopus, Common Stingray, Giant Moray Eel, Green Sea Turtle, Harbor Seal, Nautilus, and Orca.
* Report Oceanic Whitetip habitat as Deep Ocean and Deep Lukewarm Ocean only.
* Remove Deep Cold Ocean from the Oceanic Whitetip biome tag and generated biome modifier inputs.
* Add Atlantic Cod and Atlantic Salmon information cards using their implemented habitats, behavior, diet, health, variants, registry identifiers, spawn categories, and caps.
* Keep command feedback localized, readable, and consistent with the existing structured species card.

#### Acceptance criteria

* Every listed nonshark species displays exactly `Diet: TBD`.
* `/bfs info oceanic_whitetip_shark` lists only Deep Ocean and Deep Lukewarm Ocean.
* Natural Oceanic Whitetips do not spawn in Deep Cold Ocean after regenerated data is loaded.
* Information cards never claim a diet or habitat that is absent from runtime behavior and generated data.

### Phase 3. Prismarine armor swimming fit

#### Implementation

* Reinspect the full breaststroke pose at the start, midpoint, and full arm extension from front, side, rear, overhead, and low camera angles.
* Correct chest and sleeve geometry rather than hiding player limbs or globally shrinking the model.
* Keep standing, walking, crouching, riding, and ordinary arm poses aligned.
* Apply any swim specific clearance continuously across the pose blend so the player skin cannot emerge between discrete pose states.

#### Acceptance criteria

* No player chest, shoulder, or upper arm visibly passes through the Prismarine chestplate during the full vanilla swimming cycle.
* Armor does not float excessively away from the player in normal poses.
* First and third person rendering remain stable after equipment changes and reconnects.

### Phase 4. Oceanic Whitetip animation replacement

#### Implementation

* Replace the existing Oceanic Whitetip animation resource with the supplied file.
* Map runtime states to `bite_new`, `swim_new`, `swim_fast_new`, `death`, `beached`, and `thrash`.
* Preserve a compatible idle animation and verify every referenced bone exists in the current model.
* Ensure death, beached, bite, fast swim, and thrash states cannot incorrectly fall through to ordinary swimming.

#### Acceptance criteria

* Every supplied animation is reachable from its matching gameplay state.
* No missing animation, missing bone, or controller warning appears in the client log.
* Animation transitions do not interrupt delayed bite damage or player release cleanup.

### Phase 5. Atlantic Cod and Atlantic Salmon

#### Runtime behavior

* Add `atlantic_cod` and `atlantic_salmon` entities using the supplied GeckoLib geometry, animation, and texture assets.
* Both species are passive aquatic mobs. They use the matching vanilla Cod or Salmon movement, schooling, panic, player avoidance, beached flop, and water navigation behavior without custom combat or hunting logic.
* Use vanilla Cod and Salmon as the direct AI and balance reference for health, movement speed, hitbox scale, food nutrition, and fishing availability unless supplied assets require a visual adjustment.
* Atlantic Salmon named exactly `Spin` must remain in its supplied looping spin animation. Changing or removing the custom name must return it to ordinary animation state immediately.
* Register attributes, spawn placements, renderers, models, textures, sounds or vanilla sound reuse, tracking, and creative tab entries.
* Add species caps, spawn chances, biome tags, biome modifiers, command metadata, entity tags, and configuration entries following existing BFS patterns.

#### Items and loot

* Add the supplied Atlantic Cod and Atlantic Salmon spawn egg sprites.
* Add supplied Raw Atlantic Cod, Cooked Atlantic Cod, Raw Atlantic Salmon, and Cooked Atlantic Salmon sprites and item models.
* Entity deaths drop the matching raw fish item, with fire or cooking damage producing the cooked form through the loot table.
* Add both raw fish items to gameplay fishing loot at balanced weights.
* Add recipes for cooking and smoking each raw fish into its cooked form.

#### Vanilla aquatic spawn control

* Add a common configuration option that disables natural spawning for vanilla water creature, water ambient, underground water creature, and axolotl categories.
* Default the option to disabled so existing worlds keep vanilla spawning.
* Apply it only to `minecraft` namespace entities and only to natural or chunk generation spawning.
* Document that a game or server restart is required when changing the option.
* Add `[spawning] replace_vanilla_mobs`, defaulting to `true`.
* When replacement is enabled, convert natural and chunk generation `minecraft:cod` spawns to `bensfintasticsharks:atlantic_cod` and `minecraft:salmon` spawns to `bensfintasticsharks:atlantic_salmon`.
* When replacement is enabled, the vanilla Cod Spawn Egg and Salmon Spawn Egg must create the matching Atlantic fish. The BFS Atlantic spawn eggs continue to create their own fish.
* Suppress the separate BFS Atlantic fish biome modifier spawns while replacement is enabled so one vanilla spawn attempt produces one Atlantic fish and the two spawn lists cannot double the population.
* When replacement is disabled, leave vanilla Cod and Salmon spawns and eggs unchanged and allow the BFS Atlantic fish to use their own biome modifiers, caps, spawn chances, and group settings.
* Replacement must preserve the source position, rotation, custom name, and safe spawn egg entity data. It must not convert bucket releases, commands, spawners, structures, or existing entities.
* Apply Cod and Salmon conversion before `disable_vanilla_aquatic_spawns`. With both options enabled, vanilla Cod and Salmon become Atlantic fish while other covered vanilla aquatic natural spawns remain blocked.

#### Acceptance criteria

* Both entities use vanilla fish movement and schooling behavior, can be created by their BFS spawn eggs and commands, and never attack.
* Both raw items can be obtained by fishing and entity drops.
* All four foods use the intended sprites, models, nutrition, and cooking recipes.
* The `Spin` easter egg starts and stops solely from the Atlantic Salmon custom name.
* Enabling the vanilla aquatic spawn option prevents new natural vanilla aquatic mobs without deleting existing mobs or blocking commands and spawn eggs.
* With `replace_vanilla_mobs = true`, natural vanilla Cod and Salmon and both vanilla spawn eggs produce only the matching Atlantic fish, without an extra BFS natural spawn from the same area.
* With `replace_vanilla_mobs = false`, vanilla Cod and Salmon remain vanilla and Atlantic Cod and Salmon continue spawning from their own biome modifiers.
* Commands, buckets, spawners, existing entities, and unrelated aquatic species are unchanged by replacement.

#### Emergency fix: 0.23-emergency-fix

##### Defect and evidence

The initial 0.23 implementation registered Atlantic Cod and Atlantic Salmon in the custom BFS water creature category while using them to replace vanilla water ambient Cod and Salmon spawn attempts. The vanilla natural spawner therefore did not count an accepted Atlantic replacement against the water ambient population it was trying to fill. Oceans could accumulate hundreds of Atlantic fish because Minecraft kept retrying the apparently empty vanilla fish slots. The replacement allowlist itself contains only exact `minecraft:cod` and `minecraft:salmon` entity types. Tropical Fish, Pufferfish, and every other aquatic type were not converted.

##### Scope and design

* Release the correction as version and artifact `0.23-emergency-fix`.
* Register Atlantic Cod and Atlantic Salmon in `MobCategory.WATER_AMBIENT`, matching the exact vanilla source entities. Each accepted replacement must occupy the same population category slot as the Cod or Salmon it replaces.
* Keep replacement restricted to exact `EntityType.COD` and `EntityType.SALMON` inputs. Tropical Fish, Pufferfish, other vanilla aquatic mobs, BFS mobs, and modded fish must never enter the replacement path.
* Retain one replacement for one accepted vanilla source. Do not add a second Atlantic group, retry replacement, or use the separate Atlantic biome modifiers while replacement mode is enabled.
* Add a fail closed category parity guard. If a future registration change makes a replacement use a different population category from its source, cancel that source attempt without adding a replacement and report the configuration error once rather than allowing another population flood.
* When replacement is disabled, vanilla Cod and Salmon remain unchanged and the Atlantic biome modifiers remain enabled. Atlantic fish still use their configured per species cap and chance, while sharing the ordinary vanilla water ambient category ceiling.
* Do not change vanilla or BFS spawn weights, convert existing entities, delete an existing population, or alter unrelated aquatic spawn behavior. Existing excess Atlantic fish can despawn through inherited vanilla fish behavior after the fixed build is installed.

##### Verification and recovery

* Prove that Cod maps only to Atlantic Cod, Salmon maps only to Atlantic Salmon, and Tropical Fish, Pufferfish, and unrelated entity types map to no replacement.
* Prove that both replacement pairs use `MobCategory.WATER_AMBIENT`.
* Exercise repeated natural replacement attempts on a dedicated server and verify the Atlantic population is bounded by the water ambient population instead of growing on every refill cycle.
* Recheck vanilla and BFS spawn eggs in both config modes.
* Run data generation, Forge tests, the Forge build, dedicated server startup, client startup, and final JAR inspection.
* Document the cause, corrected cap behavior, exact nonreplacement scope, upgrade behavior, and optional operator cleanup guidance.

##### Acceptance criteria

* No natural spawn path can produce more than one Atlantic fish from one accepted vanilla Cod or Salmon.
* Atlantic replacements count toward the same water ambient category ceiling as vanilla Cod and Salmon.
* Tropical Fish, Pufferfish, and every unrelated vanilla or modded entity remain their original type.
* `replace_vanilla_mobs = false` continues to preserve vanilla Cod and Salmon while allowing separately configured Atlantic spawns.
* The built JAR and embedded mod metadata report `0.23-emergency-fix`.
* A sustained in world ocean test does not reproduce the runaway population shown in the 0.23 report.

### Phase 6. Advancements

#### Implementation

* Replace Shark Spotter with `Spot a shark using a Spyglass`.
* Use the vanilla Spyglass item as the Shark Spotter icon.
* Grant Shark Spotter only while a player is actively using a Spyglass and the view ray reaches a BFS shark without a blocking solid hit.
* Add `Oh My Cod` for catching an Atlantic Cod, using Cooked Atlantic Cod as the icon.
* Add `Why aren't you red?` for catching an Atlantic Salmon, using Cooked Atlantic Salmon as the icon.
* Add `Gadus morhua` for encountering an Atlantic Cod, using Raw Atlantic Cod as the icon.
* Add `Salmo salar` for encountering an Atlantic Salmon, using Raw Atlantic Salmon as the icon.
* Catch criteria must use fishing gameplay, not generic inventory acquisition. Encounter criteria must use the existing BFS encounter trigger.

#### Acceptance criteria

* Shark Spotter no longer displays a species counter and cannot trigger without a Spyglass sight line.
* Each fishing advancement triggers from catching its matching raw BFS fish.
* Each encounter advancement triggers from approaching its matching living entity.
* Advancement titles, descriptions, icons, parent links, and generated JSON load without warnings.

### Verification

Run checks in this order:

1. Compile focused common and Forge source sets after each registration or API boundary change.
2. Run unit tests for steering calculations, item intercept stability, latch and thrash release state, and configuration spawn filtering where practical.
3. Add deterministic GameTests for entity registration, passive fish behavior, shark vertical path completion, item investigation completion, player release cleanup, fishing loot references, and advancement triggers where the Forge 1.20.1 harness can represent the contract.
4. Run `./gradlew :forge:Data` and review every generated resource change.
5. Run `./gradlew :forge:test`.
6. Run `./gradlew :forge:build`.
7. Start `./gradlew :forge:Server` and verify registry, datapack, tag, loot, recipe, advancement, and dedicated server startup.
8. Start `./gradlew :forge:Client` and manually inspect shark movement, Tiger Shark item curiosity, Oceanic Whitetip animation transitions, both Atlantic fish, the Salmon `Spin` easter egg, advancement icons, and Prismarine armor across the full swimming pose.
9. Inspect the final JAR for required classes, textures, models, animations, loot tables, recipes, tags, biome modifiers, advancements, and mod version `0.23-emergency-fix`.
10. Inspect the complete Git diff for unrelated changes, generated output drift, caches, logs, credentials, absolute paths, and accidental dependency upgrades.

### Documentation and release impact

* Set the project and mod artifact version to `0.23-emergency-fix`.
* Update `README.md`, `DOCUMENTATION.md`, `CHANGELOG.md`, and relevant documentation indexes after implementation and verification.
* Document the two new species, items, foods, fishing availability, spawn biomes, caps, configuration option, commands, advancements, movement repair, animation replacement, thrash and latch behavior, and known pending animation assets.
* Keep this phase open until required checks pass and approved integration is merged.

### Implementation status

The core 0.23 implementation and the `0.23-emergency-fix` population correction are locally complete. The phase remains open until approved integration is merged.

* `./gradlew :forge:Data`, `./gradlew :forge:test`, and `./gradlew :forge:build` pass on Java 17.
* A focused JUnit policy test proves that only `minecraft:cod` and `minecraft:salmon` map to Atlantic replacements. Tropical Fish, Pufferfish, Squid, Dolphin, BFS entities, and other mod namespaces map to no replacement.
* Atlantic Cod and Atlantic Salmon register in `MobCategory.WATER_AMBIENT`. Common setup validates both source and replacement category pairs, and the natural replacement path fails closed if the invariant is ever broken.
* Dedicated server startup loads the generated recipes and advancements and reaches the ready state.
* Dedicated server probes confirm that vanilla Cod and Salmon spawn egg flows create only their matching Atlantic fish when `replace_vanilla_mobs` is enabled and remain vanilla when it is disabled. Tropical Fish remains unchanged in both modes.
* A connected client and dedicated server soak in a newly generated vanilla Ocean sampled 20 Atlantic Cod after 50 seconds, 25 after 110 seconds, and 26 after 170 seconds. The population remained at the vanilla water ambient ceiling plus one accepted school instead of growing into the hundreds shown by the original 0.23 defect. Vanilla Cod remained at zero throughout replacement mode.
* The natural and chunk generation replacement path is implemented through Forge finalization events, preserves vanilla school spawn group data, and suppresses the separate BFS Atlantic biome modifiers while replacement is enabled.
* Headless client startup through Xvfb completes mod loading, resource reload, texture atlas creation, and renderer initialization. The environment has no OpenAL device, so Minecraft disables sound during this smoke test without affecting assets or gameplay.
* JSON validation, advancement criterion counts, Oceanic Whitetip biome data, fishing modifiers, supplied 16 by 16 item sprites, and final JAR contents pass inspection. The release artifact and embedded Forge metadata report `0.23-emergency-fix`.
* The repository wide `./gradlew build` still enters the unsupported Fabric template and fails because that stub has no SmartBrainLib dependency. The supported Forge build is unaffected.
* No GameTest namespace exists in this repository. Automated GameTests remain unavailable until a harness and fixtures are added.
* Headless startup cannot replace manual in world review. Shark pursuit, Tiger Shark item approach, the full Prismarine armor swimming stroke, supplied animations, latch presentation, and both fish still require final visual acceptance in Minecraft.

## Future work

* Connect replacement animations for Tiger Shark, Sand Tiger Shark, Blacktip Reef Shark, and Harbor Seal when Ben supplies them.
* Add authored advancement icons for Sharks Galore, Mommy Shark, Shark of Zeus, It's a shiny, Sleeping with the fishes, and Awkward when those assets and final criteria are supplied.
* Revisit Atlantic Cod and Atlantic Salmon animations only if in game review shows an authored asset problem.

### Future project: BFS:AO The Trench for Minecraft 26.2

The planned ModJam project is a new NeoForge 26.2 underwater survival mod titled **BFS:AO The Trench**. A Kraken physically drags the player beneath a Mariana inspired trench and through a cold natural barrier into a sealed prehistoric ocean with no surface. A real time descent cutscene covers the technical world transfer so vanilla's `Loading terrain` presentation never appears, then flows directly into a neutral Mermaid rescue. The player must gather, craft, survive pressure, build an ascent vehicle, escape the Kraken, and return to the vanilla world. It will reuse EnVy owned BFS behavior designs and authored assets through a clean current API implementation rather than an in place upgrade of this Forge 1.20.1 project.

Five supplied but currently unanimated Blockbench models define the required new creature roster: Mosasaurus, Kraken, Mermaid, Otodus Megalodon, and Purussaurus. Rig auditing, smooth animation production, GeckoLib 5 export, runtime state mapping, and in game visual acceptance are part of the implementation plan.

The complete audit, reuse matrix, architecture, story conversion, phased implementation plan, verification gates, and risk register are maintained in the [Minecraft 26.2 ModJam port plan](../modjam/26.2-port-plan.md).

Implementation belongs in a new `MCEnvision` repository after the current BFS phase is integrated. This future plan does not change the scope, version, or completion state of the active `0.23-emergency-fix` phase.
