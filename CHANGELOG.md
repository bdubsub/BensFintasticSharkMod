# Changelog

## 0.8 Legacy 1.0 polish pass (2026-05-19)

Everything since the previous build (`eeea267`, "Shortfin Mako first implementation WIP"). The list is split by category. Numbers in parentheses are the new defaults where relevant.

### Added: new species

Tiger Shark, Oceanic Whitetip, Sandtiger, and Blacktip Reef Shark join the apex predator category. Bottlenose Dolphin (water creature), Orca (apex predator), Common Octopus, Caribbean Reef Octopus, Nautilus, Giant Moray Eel, Green Sea Turtle, American Lobster, Black Sea Nettle Jellyfish, and Cannonball Jellyfish fill out the rest of the roster. Each ships with a geo model, animations, textures, biome modifier, loot table, spawn placement, and category routing.

### Added: variants

Ten species now ship multiple texture variants that roll randomly on spawn from a weighted list. Existing variants on the alpha species (Mako, Great White, Hammerhead, Thresher, Stingray, Harbor Seal) are unchanged.

| Species | Variant count |
|---|---|
| American Lobster | 5 (defaults plus blue and red, both rare) |
| Tiger Shark | 4 (sandy is rare) |
| Oceanic Whitetip | 6 (equal weights) |
| Sandtiger | 3 (equal) |
| Blacktip Reef | 4 (equal) |
| Bottlenose Dolphin | 5 (equal) |
| Common Octopus | 4 (equal) |
| Giant Moray Eel | 3 (equal) |
| Green Sea Turtle | 3 (equal) |
| Nautilus | 2 (equal) |

The variant id is stored as an int in the entity's `Variant` NBT tag, so `/summon bensfintasticsharks:<species> ~ ~ ~ {Variant:N}` works for testing. The data syncs to the client and persists across saves.

### Added: spawn egg variant cycling

Shift plus right click a spawn egg in air to cycle the variant slot. The current variant shows in a small action bar message and appears as a tooltip line on the egg. Right click on a block to spawn the entity with that variant applied. Spawn eggs for species with only one texture (Caribbean Reef Octopus, Orca, both jellyfish) skip the cycle and don't show the tooltip line.

### Added: Sunken Trove

A small underwater dig site that generates in non frozen ocean biomes. Layout is roughly 5 by 3 by 5: sand and sandstone ring around the perimeter, waterlogged chest in the centre, sandstone spoil pile, seagrass clumps, and an optional kelp stalk. The feature scans downward through water to find the seafloor before placing, so it stops landing above the surface. Locate with `/locate structure bensfintasticsharks:sunken_trove` or via the `#bensfintasticsharks:trove` tag.

### Added: Captain Ben's Hat

A cosmetic head slot armor with leather tier stats and Rare rarity glow. Worn model is a 3D GeckoLib pirate hat that sits on top of the player's head. Loot table puts one in roughly 5% of Sunken Trove chests, 5% of vanilla buried treasure chests, and 3% of shipwreck map chests.

### Added: Shark Codex chain

Four new items. Lost Manuscript drops at 5% per roll from vanilla dungeon, shipwreck, buried treasure, and ocean ruin loot (handled by global loot modifier), plus from Sunken Trove chests. Nine Lost Manuscripts craft a Codex Page. Nine Codex Pages craft a Codex Volume. A Codex Volume plus a Book, a Shark Tooth, and a Tropical Fish Bucket craft a Shark Codex. Right click the Shark Codex to open a written book reader with fifteen pages of in world prose.

### Added: water disturbance system

Sharks now react to player activity in the water. Light disturbances (sprint swimming, arrows, fishing bobbers, projectile impacts), heavy disturbances (underwater attacks, block breaks near water, falling into water), and blood disturbances (any living entity hurt in water) feed into a state machine that moves sharks between Idle, Curious, Hostile, and Beached. Sharks ignore creative and spectator players entirely.

### Added: prey hunting and reinforcements

`AbstractSharkEntity` now scans for entities in the `shark_prey` tag every twenty ticks and locks on to the closest one in range. The chase is a direct approach at aggro speed. Bite trigger is two phase: animation fires immediately, damage lands five ticks later on the visual impact frame. When a shark is hurt by a living attacker, it locks the attacker as its target and recruits up to four same species sharks within 32 blocks to do the same.

### Added: prey flee behaviour

Every peaceful BFS species checks for nearby apex predators every twenty ticks. If a predator is within 12 blocks, the prey walks 8 blocks in the opposite direction at 1.4x speed.

### Added: per species mob caps and config

`bfs_water_creature` and `bfs_water_ambient` categories are independent of vanilla water mobs. Defaults: apex predator 5, water creature 15, water ambient 25. Every species also has its own per species cap that counts existing mobs within 64 blocks. Caps are runtime tunable. Live config also exposes spawn chance multipliers, HP and damage multipliers per species, knockback resistance, group size minimums, disturbance sensitivity multipliers, and AI radius multipliers.

### Added: admin commands

`/bfs help`, `/bfs list`, `/bfs find <species>`, `/bfs info <species>`, `/bfs count <species>`, `/bfs cap help / list / get / set / reset`, `/bfs disturbance <type>`, and `/bfs reload`. `/bfs disturbance` reports how many sharks are in the relevant radius so you can tell when nothing happened because nothing was nearby.

### Added: drowning for beached aquatics

`BfsAquaticEntity.handleAirSupply` now mirrors the vanilla water animal pattern. Air drains by one per tick when out of water, drown damage of 2 fires when air hits negative twenty. Mammals with their own surfacing systems keep that behaviour.

### Added: spawn placement predicates

The Common Octopus, Caribbean Reef Octopus, American Lobster, and Nautilus use a custom seafloor predicate that requires water at the spawn position, a solid block below, and at least four blocks below sea level. The Nautilus additionally requires twenty plus blocks of depth (so it ends up in ravines) and rejects 80% of natural spawns during the day so they come out mostly at night.

### Added: octopus, lobster, nautilus pitch lock

Those species now keep their visual pitch at zero so they don't tilt nose up or nose down when ascending or descending. They still move on the Y axis, they just stay horizontal visually.

### Added: jellyfish beached animation

Both jellyfish now have a beached pose that plays out of water. The Black Sea Nettle still stings while beached.

### Added: advancements

Per species encounter advancements for Tiger Shark, Oceanic Whitetip, Sandtiger, Blacktip Reef, Common Octopus, Caribbean Reef Octopus, Nautilus, Giant Moray Eel, Green Sea Turtle, American Lobster, Black Sea Nettle, and Cannonball Jellyfish. Themed advancements added: Marine Curious, Shark Spotter, Shark Whisperer, Conservationist, Marine Biologist, Apex Awareness, Wrong Place Wrong Time, Sleeping with the Fishes, Stung, Inked, Apex of Apex, Dolphin Friend, Fresh Catch, Hidden Trove, Captain's Heir.

### Added: Respect the Ocean effect

Killing any species in the `conservation_protected` tag (every BFS species except American Lobster) gives the player a two minute Respect the Ocean effect. No mechanical impact. The icon shows "The ocean is watching..." in the effect list. Toggle in `[visuals] conservation_debuff_enabled`.

### Added: Lost Manuscript loot in vanilla chests

A global loot modifier inserts Lost Manuscript at 5% per roll into vanilla dungeon, shipwreck, buried treasure, and ocean ruin loot tables. Captain Ben's Hat gets a separate global modifier for vanilla buried treasure (5%) and shipwreck map chests (3%).

### Added: lobster meat textures

Raw and cooked claw plus raw and cooked tail. Pulled from the updated texture set. Furnace recipe (200 ticks) and campfire recipe (600 ticks) cook the raw forms. Raw lobster is 2 nutrition and 0.2 saturation. Cooked is 6 nutrition and 0.8 saturation, counts as meat.

### Changed: shark fight loop

Removed the orbit and circle attack target behaviour entirely. The shark fight tasks used to circle a target at 5.5 blocks; that looked artificial. They now lock on, swim straight, bite when in range. The `CircleAttackTarget` class is deleted.

### Changed: Mako stats

Detection 24 to 28. Blood detection 36 to 42. Aggression 70 to 85. Aggro speed 1.2x to 1.6x. Disengage distance 64 to 80. Disengage timeout 300 to 400 ticks. Bite cooldown 18 to 14 ticks. Bite damage 3 to 4. Should now win against a pair of vanilla dolphins.

### Changed: Mako animation controller

Stripped out every animation reference that wasn't actually in `shortfin_mako.animation.json` (the old controller asked for `misc.idle` and `misc.beached2` which don't exist there, freezing the model after a bite). The controller now uses only the keys the file has: `move.swim`, `move.fast_swim`, `misc.beached2`, `attack.bite`, `attack.thrash`. No more frozen poses after a successful kill.

### Changed: new shark animation controllers

Tiger Shark, Oceanic Whitetip, Sandtiger, and Blacktip Reef now play SWIM unconditionally while in water rather than gating on `isMoving()`. The previous gate caused the model to freeze on the idle pose any tick the entity had zero delta movement.

### Changed: Oceanic Whitetip bite cooldown

25 to 40 ticks. Combined with the 20 tick bite animation that gives about a full second of visible swim between bites.

### Changed: BfsAquaticEntity speed

New `swimSpeedMultiplier` (default 0.18) and `maxHorizontalSpeed` (default 0.45 blocks per tick) hooks. Sharks use 0.16 multiplier with a target aware cap of 0.55. Octopus, lobster, and nautilus use 0.08 to 0.10 multiplier with caps between 0.12 and 0.18. This is the fix for "mach 10 dolphins" and the spinning common octopus.

### Changed: Sunken Trove biomes

Removed frozen ocean and deep frozen ocean from the trove biome tag. The trove was visually obscured by iceberg surfaces and `/locate` plus teleport sent players to the ice rather than the underwater structure.

### Changed: spawn egg item

Replaced vanilla `SpawnEggItem` for all BFS species with `BfsSpawnEggItem`. Constructor now takes a `variantCount`. Species with `variantCount > 0` get the shift plus right click cycle. Species with `variantCount == 0` (jellyfish, Orca, Caribbean Reef Octopus, Stingray placeholder, etc.) skip the cycle and don't show the variant tooltip.

### Changed: animation amplitudes

Bottlenose Dolphin swim amplitudes roughly tripled (body 7 to 16, tail 16 to 26, tail fin 18 to 32) and cycle shortened from 2 seconds to 1.5 seconds. Giant Moray Eel amplitudes roughly doubled. The previous animations were technically running but the motion was too subtle to read as movement.

### Changed: Nautilus animation pacing

Move cycle lengthened from 1 second to 2.8 seconds so the propulsion looks like real nautilus movement instead of a panic flap.

### Changed: Nautilus hide controller

Hide animation is now `thenPlayAndHold` and only triggers when `isResting()` is true. The previous controller called `setAndContinue(HIDE)` every render frame which restarted the animation continuously.

### Changed: octopus animation controllers

Both Common Octopus and Caribbean Reef Octopus controllers now swap on `isInWaterOrBubble()` rather than `isMoving()`. Floating idle in water now plays SWIM. The on ground pose only shows when actually beached.

### Changed: dolphin advancement

Dolphin Friend description rewritten from "Have a dolphin follow your boat for 30 seconds" (which didn't match the actual trigger) to "Encounter a Bottlenose Dolphin in the wild". The duplicate Friendly Wake advancement that I added briefly was removed.

### Changed: Captain Ben's Hat geo

Pulled in the updated geo from the BFS Legacy directory, wrapped it in the `bipedHead` plus `armorHead` armor attachment pattern, and shifted all cube positions up six units so the hat clearly sits above the head top instead of clipping the face.

### Changed: variant textures

Pulled in all the variant textures from the BFS Legacy directory, normalized the file names to `default_1.png` through `default_N.png` (or `<name>.png` for named variants like blue / red / sandy / vanilla / blacktip), and copied them into the proper `textures/entity/<species>/` folders.

### Changed: documentation

Full rewrite of `DOCUMENTATION.md` in plain prose (no dashes, no ampersands). Covers spawning, config, commands, behaviors, variants, codex chain, and troubleshooting. New sections for variants (section 9) and advancements (section 16).

### Fixed: spawn eggs not spawning the four new sharks

The four new sharks inherited `AbstractSharkEntity.getSensors()` and `getIdleTasks()` raw. Those overrides referenced an SBL sensor configuration that interacted badly with the new entities' construction and was the most likely cause of spawn egg failure. Removed the overrides; new sharks now use `BfsAquaticEntity`'s simpler defaults plus the tick based prey scanner.

### Fixed: Mako stuck pose after kill

See "Changed: Mako animation controller". The root cause was `DefaultAnimations.IDLE` resolving to `misc.idle` which is not in `shortfin_mako.animation.json`.

### Fixed: dead target keeps shark in chase loop

`AbstractSharkEntity.customServerAiStep` now checks whether the current target is dead or dying and disengages immediately if so. The Mako specifically used to lock onto a dead prey because the grab system kept the rider attached.

### Fixed: lobster meat textures

The textures were 128 by 128 with a sparse pattern that downsampled to mostly transparent at inventory size. Replaced with proper 16 by 16 art for both raw and cooked claw and tail.

### Fixed: Captain Ben Hat clipping the face

Two iterations. First shifted cubes up four units and dropped the forward tilt. Then pulled in the updated geo file from the BFS Legacy directory and shifted six more units. Hat now sits clearly above the head.

### Fixed: spinning octopus / mach 10 dolphin

`BfsAquaticEntity.travel` was effectively unbounded. New `swimSpeedMultiplier` and `maxHorizontalSpeed` hooks cap horizontal velocity. Octopuses and the nautilus also lock their visual pitch at zero so they swim level instead of nose down spinning when descending.

### Fixed: variant cycling didn't apply

The first pass of the spawn egg cycle stored 0 to 9 in NBT but never wrote it to the spawned entity. The current implementation searches the spawn area for the freshly spawned entity of the egg's exact type, finds the one with the lowest tick count, and calls `setBfsVariantId` after `super.useOn` completes. Variant applies cleanly now.

### Fixed: variant cycling on species without variants

Spawn eggs for species without variants used to still cycle 0 to 9 with no visible change. The new constructor parameter `variantCount` gates the cycle and the tooltip on species that actually have variants.

### Fixed: jellyfish glow squid particles

Black Sea Nettle no longer renders glow squid particles. They were called out as visually noisy and out of place.

### Fixed: Z fighting on jellyfish tendrils, frills, and octopus tentacles

Added a tiny inflate (0.01) to the billboard cubes so coplanar planes don't fight for the same pixels.

### Fixed: octopus dual siphons

Both octopus models had `leftBreathingHole` and `rightBreathingHole` cube pairs. Real octopuses have a single funnel. Merged into one centered `siphon` bone. Animation references updated.

### Fixed: Green Sea Turtle laying eggs while beached

Disabled the egg laying behaviour entirely. BFS turtles are a wild encounter species only. Use vanilla turtles for breeding.

### Fixed: jellyfish hardly stings

Black Sea Nettle sting box now includes 0.6 blocks around the body and three blocks down so the tendrils also damage. Both jellyfish now sting every 10 ticks instead of every 20. Cannonball sting radius widened from 0.3 to 0.5.

### Fixed: Oceanic Whitetip swim animation never visible

With a 25 tick bite cooldown and a 20 tick bite animation, the swim could only show for 5 ticks at a time. Raised the cooldown to 40 ticks so the gap between bites is closer to a full second.

### Fixed: bite animation lagged behind the hit

Bite trigger used to call the animation and the damage on the same tick. Felt like the damage came out of nowhere. Added a `biteImpactDelayTicks` (5) so the animation fires immediately and the damage lands on the visual impact frame.

### Fixed: lobsters spawning at the water surface

Replaced their spawn predicate with the seafloor check.

### Fixed: trove location teleporting to icebergs

Frozen oceans removed from the trove biome tag.

### Fixed: stale Lobster Pincer texture names

The updated texture set ships `raw_lobster_pincer.png` and `cooked_lobster_pincer.png`. We renamed those to `raw_lobster_claw.png` and `cooked_lobster_claw.png` on copy so the existing item registrations still resolve.

### Fixed: Dolphin Friend triggered on spawn egg use

See "Changed: dolphin advancement". The trigger is correct; the description was wrong.

### Removed: orca curious orbit

The Orca used to slowly approach a player within 40 blocks and orbit at 12 blocks. Removed entirely. The Orca now wanders wide territory and surfaces to breathe; it does not target or follow players.

### Removed: CircleAttackTarget AI behaviour

The class and all references are gone. Sharks never circle.

### Removed: Friendly Wake advancement

Was a duplicate of Dolphin Friend. Removed alongside its lang entry.

### Removed: turtle egg laying

See "Fixed: Green Sea Turtle laying eggs while beached".

### Internal notes

`AbstractSharkEntity` no longer overrides `getSensors`, `getIdleTasks`, or `getFightTasks`. Prey hunting, bite scheduling, beach avoidance, and reinforcement calling all live in `onSharkTick` and `hurt` overrides on the common entity. The four existing sharks (Mako, Great White, Hammerhead, Thresher) still have their own SBL based fight tasks; the four new sharks (Tiger, Oceanic Whitetip, Sandtiger, Blacktip) use the tick based system. Both paths share the bite impact delay and the dead target disengage.

`BfsVariantHolder` is a new interface on the common side. Entities that implement it expose `bfsVariantCount` and `setBfsVariantId`. The spawn egg looks for this interface to apply variants. New per species enums all live inside their entity class as `Variant`.

`BfsSpawnEggItem` replaces vanilla `SpawnEggItem` for all BFS species. It adds the shift cycle, the tooltip, and the post spawn variant apply.

`BfsSpawnPlacements` is a new Forge side class that registers the seafloor predicate for octopuses, lobster, and the nautilus, plus the night and ravine gate for the nautilus specifically.
