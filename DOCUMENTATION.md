# Ben's Fintastic Sharks

A 1.20.1 Forge mod, currently at version `0.24`, that adds twenty two sea creatures, a buried treasure structure, a captain's hat, a shark codex book, and a system that makes sharks pay attention to what you're doing in the water.

This document is the reference for everything the mod ships with. If you just installed it and you're trying to figure out where to find an Orca, skip to section 3.

The `envy/0.24` release branch is based on the stable `1.20.1` branch. It retains the 0.23 behavior while adding the supplied advancement artwork, repaired fish and Oceanic Whitetip animation resources, a single Sharks Galore discovery node, and permanent algae world generation.

## 0.23 and emergency fix highlights

Version 0.23 repairs the shark steering regression introduced by the 0.22 level cruising branch. Navigation can apply continuous vertical input again instead of alternating between a requested depth change and forced level travel. Turn aware horizontal braking remains in place to control overshoot. Tiger Sharks now use one stable underwater item intercept with timeout and retry memory, which prevents a floating item from pulling them into a permanent orbit.

Version `0.23-emergency-fix` corrects a population category mismatch in the default vanilla fish replacement. Atlantic Cod and Atlantic Salmon now occupy the same vanilla water ambient population slots as the Cod and Salmon they replace. The replacement allowlist remains limited to those two exact vanilla entity IDs. Tropical Fish, Pufferfish, other vanilla aquatic mobs, BFS mobs, and modded fish are never converted by this feature.

Atlantic Cod and Atlantic Salmon join the roster as passive BFS prey. Both have spawn eggs, raw and cooked food items, fishing and entity loot, cooking recipes, information cards, and encounter and fishing advancements. They use the matching vanilla fish movement, schooling, panic, player avoidance, water navigation, and beached flop behavior. Atlantic Salmon named exactly `Spin` uses its supplied spin loop.

`[spawning] replace_vanilla_mobs` defaults to `true`. Natural vanilla Cod and Salmon become the matching Atlantic fish, and the vanilla Cod and Salmon spawn eggs create the Atlantic versions. Setting it to `false` restores ordinary vanilla Cod and Salmon and enables the separate BFS Atlantic fish biome spawns. Servers can independently disable other new natural vanilla aquatic spawns with `[spawning] disable_vanilla_aquatic_spawns = true`.

Oceanic Whitetips now use the supplied replacement animation set and can thrash a player after a successful bite. Blacktip Reef Sharks use a shorter latch instead of a large shark thrash. Prismarine armor uses standard player arm pivots, full sleeves, and the uncontracted torso so the full swimming stroke remains covered. Shark Spotter now requires viewing a BFS shark through a Spyglass. `/bfs info` reports `TBD` for species without an implemented diet, and Oceanic Whitetip habitat and spawning are limited to Deep Ocean and Deep Lukewarm Ocean.

## 1. What's in the mod

The roster covers five loose groups.

The sharks are Great White, Great Hammerhead, Common Thresher, Shortfin Mako, Tiger, Oceanic Whitetip, Sandtiger, and Blacktip Reef. Two marine mammals, the Bottlenose Dolphin and the Orca. Three cephalopods, the Common Octopus, the Caribbean Reef Octopus, and the Nautilus. Five other fauna, the Common Stingray, the Harbor Seal, the American Lobster, the Giant Moray Eel, and the Green Sea Turtle. Two jellyfish, the Black Sea Nettle and the Cannonball. Two Atlantic fish, Atlantic Cod and Atlantic Salmon.

Most of these species ship with multiple skin variants that get rolled randomly on spawn and can also be cycled on the spawn egg. See section 9 for the variant tables.

There is one cosmetic item, Captain Ben's Hat, which is a head slot armor with leather tier stats and a 3D worn model. You find it in chest loot rather than crafting it.

There is one readable item chain that ends in the Shark Codex. You assemble it from Lost Manuscripts you find in dungeon, shipwreck, buried treasure, and ocean ruin loot. See section 11.

There is one structure, the Sunken Trove. It generates on the ocean floor in non frozen ocean biomes and contains a chest with our loot table.

The 0.24 branch also adds three permanent aquatic plants. Algae Block is a single still texture. Large Green Algae and Large Red Algae use their supplied animated strips and can generate in ocean biomes from y 20 through y 62. They require source water and a valid underwater seagrass position, use cutout rendering, and do not generate in rivers or non ocean biomes. Their block and item forms are available through the generated `bensfintasticsharks:algae` tags.

## 2. How spawning works

Vanilla Minecraft separates aquatic population categories. Cod, Salmon, Tropical Fish, and Pufferfish use the water ambient category. Dolphins and Squid use the water creature category. Population ceilings scale with the eligible spawning chunks around players.

This mod runs on its own categories so most BFS wildlife doesn't compete with vanilla water mobs for slots. There are three custom categories. The apex predator category holds all eight sharks plus the Orca and has a default cap of one. The water creature category holds dolphins, octopuses, stingrays, seals, lobsters, eels, turtles, and nautiluses with a cap of ten. The water ambient category holds the two jellyfish species with a cap of fifteen. Atlantic Cod and Atlantic Salmon deliberately use vanilla's water ambient category.

With `replace_vanilla_mobs = true`, a natural vanilla Cod or Salmon attempt is converted at the same position into one matching Atlantic fish. The separate Atlantic biome modifier attempt is suppressed so the two systems cannot double the population. These replacements follow the vanilla water ambient population and schooling source rather than the Atlantic per species cap, chance, and group controls. With `replace_vanilla_mobs = false`, vanilla fish remain unchanged and Atlantic fish use their own biome modifiers, caps, spawn chances, and configured groups while still sharing the ordinary water ambient category ceiling.

On top of the category caps, every individual species has its own cap. When a natural spawn fires, we count how many of that species already exist inside a 64 block radius of the spawn position, which is about four chunks. If the count is at or above the species cap, the spawn is cancelled. Setting a species cap to zero disables natural spawning entirely.

Caps only gate natural and chunk generation spawns. Spawn eggs work normally, except the vanilla Cod and Salmon eggs intentionally create the matching Atlantic fish while replacement is enabled. Both BFS Atlantic spawn eggs always create their own fish. `/summon`, buckets, spawners, and structure placements remain unchanged.

There is also a second knob called spawn chance which is a probability multiplier on each natural spawn attempt. A value of 1.0 leaves the biome modifier weight untouched. A value of 0.5 makes them spawn half as often. A value of 2.0 makes them spawn roughly twice as often, still bounded by the category cap. A value of 0.0 disables natural spawning the same as setting cap to zero. Use this when you want a species to be rarer without removing it entirely.

Sharks have one more local density gate. Before a natural or chunk generation shark spawn is accepted, the game checks a 96 block horizontal radius. Different shark types do not naturally stack inside that area. Non blacktips are solitary by default. Blacktip Reef Sharks are the schooling exception and can form groups of up to three. Command spawns and spawn eggs bypass this spacing check.

Spawn placement rules differ between species. Sharks, the Orca, jellyfish, dolphins, turtles, eels, seals, stingrays, and both Atlantic fish use the standard water rule. The Common Octopus, Caribbean Reef Octopus, American Lobster, and Nautilus use a custom seafloor predicate that requires water at the spawn position, a solid block below it, and at least four blocks of depth below sea level. That's why you no longer see lobsters spawning at the water surface. The Nautilus on top of that needs to be at least twenty blocks below sea level (so it ends up in ravines and deep crevices) and there's an 80% rejection on natural spawns during daytime, so they come out mostly at night.

## 3. Where each species spawns

The tables below are the source of truth for biomes, weights, group sizes, and caps. Weight is the biome modifier's spawn weight, which is how strongly the game prefers this species when picking from the category. Group is the size range that gets spawned together. Cap is the default per species cap that you can edit in config or with `/bfs cap`.

### Sharks

Every shark in this list is in the apex predator category. The category cap is one. The shallow water slowdown is on at depths under three blocks, which gives swimmers a fighting chance to escape near shore. Natural predators also need at least five blocks of water beneath their spawn and stay 24 blocks away from beach biomes.

| Species | Biomes | Weight | Group | Cap | Notes |
|---|---|---|---|---|---|
| Great White Shark | Ocean, Deep Ocean | 12 | 1 | 1 | Large prey hunter |
| Great Hammerhead | Ocean, Deep Ocean, Warm Ocean, Lukewarm Ocean, Deep Lukewarm Ocean | 12 | 1 | 1 | Patient, long disengage timeout |
| Common Thresher | Ocean, Deep Ocean | 12 | 1 | 1 | Tail whip hunter |
| Shortfin Mako | Lukewarm Ocean, Deep Lukewarm Ocean | 12 | 1 | 1 | Fastest aggro speed |
| Tiger Shark | Ocean, Lukewarm Ocean, Warm Ocean, Deep Ocean, Deep Lukewarm Ocean | 6 | 1 | 1 | Investigates dropped items in water |
| Oceanic Whitetip | Deep Ocean, Deep Lukewarm Ocean | 4 | 1 | 1 | Deep ocean only, limited same species blood convergence |
| Sandtiger | Ocean, Lukewarm Ocean, Warm Ocean | 6 | 1 | 1 | Coastal, sometimes hovers in place |
| Blacktip Reef | Lukewarm Ocean, Warm Ocean, Deep Lukewarm Ocean | 8 | 1 to 2 | 3 | Intentional school shark; configured minimum group size is 2 |

### Marine mammals

| Species | Category | Biomes | Weight | Group | Cap | Notes |
|---|---|---|---|---|---|---|
| Bottlenose Dolphin | water creature | Ocean, Lukewarm Ocean, Warm Ocean, Deep Lukewarm Ocean | 40 | 1 to 2 | 4 | Configured minimum group size is 2; surfaces, plays with items, follows boats |
| Orca | apex predator | Cold Ocean, Deep Cold Ocean | 10 | 1 | 1 | Cold ocean exclusive, peaceful presence, male model only |

### Cephalopods

| Species | Biomes | Weight | Group | Cap | Notes |
|---|---|---|---|---|---|
| Common Octopus | Lukewarm Ocean, Deep Lukewarm Ocean, Cold Ocean, Deep Cold Ocean | 30 | 1 | 3 | Seafloor spawn, hides in place, emits ink when threatened |
| Caribbean Reef Octopus | Warm Ocean | 20 | 1 | 2 | Seafloor spawn, warm water only |
| Nautilus | Deep Ocean, Deep Cold Ocean, Deep Lukewarm Ocean | 10 | 1 | 1 | Weight 40 in flooded cave biomes below y=0; mostly nocturnal above y=0 |

### Other fauna

| Species | Biomes | Weight | Group | Cap | Notes |
|---|---|---|---|---|---|
| Common Stingray | Ocean, Lukewarm Ocean, Warm Ocean, Swamp, Mangrove Swamp | 50 | 1 to 3 | 3 | Sinks to the seafloor and stays there. Poison sting on contact |
| Harbor Seal | Ocean, Deep Ocean, Warm Ocean, Lukewarm Ocean, Cold Ocean | 20 | 1 to 2 | 3 | Configured minimum group size is 2; arctic skin in cold biomes |
| American Lobster | Ocean, Lukewarm Ocean, Deep Ocean, Deep Lukewarm Ocean, Warm Ocean | 100 | 1 to 3 | 10 | Seafloor food source, snip attack, drops raw meat |
| Giant Moray Eel | Warm Ocean | 40 | 1 to 2 | 4 | Anchors and hides in coral, lunges at players within 2 blocks |
| Green Sea Turtle | Warm Ocean, Lukewarm Ocean, Deep Lukewarm Ocean | 25 | 1 to 2 | 3 | Wild encounter only, does not lay eggs |

### Jellyfish

These are the rarest spawns in the mod. The defaults give you one or two per area at most.

| Species | Biomes | Weight | Group | Cap | Notes |
|---|---|---|---|---|---|
| Black Sea Nettle | Deep Ocean, Deep Cold Ocean | 3 | 1 | 1 | Drifts toward the surface, contact poison and weakness |
| Cannonball Jellyfish | Lukewarm Ocean, Deep Lukewarm Ocean, Warm Ocean | 4 | 1 | 2 | Configured minimum group size is 2; contact weakness |

### Atlantic fish

| Species | Default replacement habitats | Own habitats when replacement is disabled | Own weight | Own group | Own cap | Notes |
|---|---|---|---|---|---|---|
| Atlantic Cod | Ocean, Deep Ocean, Cold Ocean, Deep Cold Ocean, Lukewarm Ocean, Deep Lukewarm Ocean | Ocean, Deep Ocean, Cold Ocean, Deep Cold Ocean | 60 | 2 to 4 | 8 | Vanilla Cod AI, passive shark prey, available through fishing |
| Atlantic Salmon | Cold Ocean, Deep Cold Ocean, Frozen Ocean, Deep Frozen Ocean, River, Frozen River | Cold Ocean, Deep Cold Ocean, River, Frozen River | 50 | 2 to 4 | 8 | Vanilla Salmon AI, exact custom name `Spin` activates its spin loop |

### Conditions every spawn passes through

A natural spawn has to pass several checks before the mob actually appears.

The position has to be a water block. Sharks, Orca, jellyfish, dolphins, turtles, eels, seals, stingrays, and both Atlantic fish use the standard water rule. Common Octopus, Caribbean Reef Octopus, American Lobster, and Nautilus need a solid block below them and at least four blocks of depth. The Nautilus also wants twenty plus blocks of depth and a night clock most of the time.

Sharks and Orcas refuse to spawn within 24 blocks of a beach biome. The spawn check samples biomes every 8 blocks horizontally and also requires at least five blocks of water below the spawn.

Natural sharks then pass the mixed species spacing check. Inside `shark_spacing_radius`, which defaults to 96 blocks, different types cannot stack. The default local ceiling is one non blacktip or three blacktips. These local ceilings are separate from the apex category cap and the per species cap.

If the species has a configured group size minimum and the spawn finalizes as the first of a group, we spawn additional siblings nearby to reach the minimum. This currently affects Blacktip Reef Shark, Harbor Seal, Bottlenose Dolphin, Cannonball Jellyfish, and both Atlantic fish. The Atlantic entries apply only while `replace_vanilla_mobs = false`; replacement mode keeps the source vanilla school.

Finally, the per species cap check counts existing mobs of that species inside a 64 block radius of the spawn position. If the count is at or above the cap, the spawn is cancelled.

## 4. Configuration file

The config lives at `config/bensfintasticsharks-common.toml`. Most edits take effect immediately because the values are read live during spawning and per tick AI.

The sections are organized roughly by what they affect.

`[spawning]` holds the three category caps, shark spacing controls, vanilla fish replacement, and the broader vanilla aquatic spawn switch. `apex_predator_cap` defaults to `1`, `bfs_water_creature_cap` to `10`, and `bfs_water_ambient_cap` to `15`. `shark_spacing_radius` defaults to `96`, `nearby_non_blacktip_shark_cap` to `1`, and `nearby_blacktip_shark_cap` to `3`. The spacing radius accepts 16 to 256 blocks; both nearby shark ceilings accept 1 to 16.

`replace_vanilla_mobs` defaults to `true`. It converts only natural and chunk generation `minecraft:cod` and `minecraft:salmon` and entities placed from the matching vanilla spawn eggs or their dispenser behavior. Position, rotation, custom egg name, and safe entity data are copied to the Atlantic replacement. The BFS Atlantic spawn eggs remain unchanged. Setting this option to `false` leaves vanilla Cod and Salmon alone and allows the Atlantic fish biome modifiers to spawn their separate populations. Atlantic fish always use the vanilla water ambient category, so either mode remains bounded by its ordinary population ceiling. Commands, buckets, spawners, structures, existing entities, Tropical Fish, Pufferfish, and every other unrelated species are never converted. This value is read for each new spawn attempt.

`disable_vanilla_aquatic_spawns` defaults to `false`. When enabled, it blocks only natural and chunk generation spawns for vanilla water creature, water ambient, underground water creature, axolotl, and turtle entities. Existing entities, commands, spawn eggs, and nonnatural creation paths remain unchanged. Replacement runs first, so enabling both settings still converts Cod and Salmon while blocking the other covered vanilla aquatic natural spawns. Restart the game or dedicated server after changing this suppression option.

`[caps]` holds twenty two individual species caps. The defaults are listed in the tables above. These values accept 0 to 64; zero disables that species's natural spawning.

`[spawn_chance]` holds the probability multipliers. Sharks default to `0.4`, Harbor Seals default to `0.5`, and other species default to `1.0`. Valid values are 0.0 to 5.0.

`[hp_mult]`, `[damage_mult]`, and `[knockback_resistance]` hold per-species attribute scalers. Default 1.0 for the first two and `-1` for knockback resistance, where `-1` means leave the default attribute alone.

`[group_size_min]` lets you bump the minimum group size for the species listed there.

`[disturbance]` holds three sensitivity multipliers (light, heavy, blood) plus toggles for particles and audio.

`[visuals]` holds toggles for the Respect the Ocean debuff and octopus ink particles.

`[ai]` holds two shark behavior multipliers. `shark_detection_radius_mult` scales how far sharks notice things. `shark_disengage_distance_mult` scales when they give up chasing.

`[combat]` holds global shark and jellyfish damage multipliers and the Orca's knockback resistance. Note that shark HP and damage stack with per species mults. If you set the global shark_hp_mult to 1.5 and the per species great_white_shark hp_mult to 2.0, Great Whites will have 3x base HP.

`[structures]` holds the Captain Ben's Hat chest weight, which is the out of 100 chance to appear in buried treasure and shipwreck loot.

## 5. Admin commands

Every `/bfs` command needs op permission (level 2). Species names and disturbance types autocomplete.

| Command | What it does |
|---|---|
| `/bfs help` | Top level help with a list of subcommands |
| `/bfs list` | Lists every species with category and current cap |
| `/bfs summon villager [trade]` | Summons a persistent master Fisherman with normal offers from all five tiers and one guaranteed BFS trade |
| `/bfs find <species>` | Tells you the coordinates of the nearest one within 512 blocks |
| `/bfs info <species>` | Creative operators only. Shows scientific name, habitats, behavior, diet, configured health, variants, registry ID, category, and cap |
| `/bfs count <species>` | Counts instances within 64 blocks of you. Useful for cap debugging |
| `/bfs cap help` | Explains the cap system in chat |
| `/bfs cap list` | Lists every cap with runtime override indicator |
| `/bfs cap get <species>` | Shows one species cap |
| `/bfs cap set <species> <value>` | Runtime override, value 0 to 64. Not saved to config |
| `/bfs cap reset <species>` | Clears that species runtime override |
| `/bfs cap reset` | Clears every runtime override |
| `/bfs disturbance <type>` | Fires a test light, heavy, or blood disturbance at your position. Reports how many sharks are in range to react |
| `/bfs reload` | Re reads config values without restart |

`/bfs cap set` is runtime only. Restart the server and your edits are gone. To make changes permanent, edit the config file.

For the showcase villager, omit `[trade]` or use `random` to choose randomly. Use one of these IDs to guarantee a specific offer:

```text
great_white_shark_tooth       great_hammerhead_shark_tooth
common_thresher_shark_tooth   tiger_shark_tooth
shortfin_mako_shark_tooth     oceanic_whitetip_shark_tooth
great_white_shark_skin        great_hammerhead_shark_skin
common_thresher_shark_skin    cartilage
shark_jaws                    megalodon_tooth
```

Examples:

```mcfunction
/bfs summon villager
/bfs summon villager random
/bfs summon villager megalodon_tooth
```

The villager is level 5 with 250 XP. It rolls up to two ordinary Fisherman offers from each vanilla trade tier, then appends the guaranteed BFS offer so it is easy to find at the end of the list.

When a runtime override is active, `/bfs cap list` shows it in gold and tells you what the config default is.

`/bfs disturbance` will also tell you, after firing, how many sharks were inside its diagnostic radius. If it reports zero, the system is fine; you just don't have any sharks nearby to respond. Spawn or find some first.

## 6. Water disturbance system

The mod's main mechanic. Sharks notice what you're doing in the water and react.

There are three disturbance types. Light disturbances come from sprint swimming, arrows hitting water, fishing bobbers, and projectile impacts. Heavy disturbances come from attacking something underwater, breaking blocks near water, or falling into water from three or more blocks up. Blood disturbances come from any living entity taking damage while in water. Their base reaction radii are 12, 16, and 24 blocks respectively.

A shark's response depends on which state it's currently in.

When a shark is idle and a light disturbance fires, there's a 15% chance it turns curious and swims toward the source. A heavy disturbance has a 60% chance. Blood is filtered through the shark's hunger and prey rules: a bleeding mob must be in that species's prey tag, while a wounded player selects one eligible non-schooling primary responder. Blacktip Reef Sharks are the intentional exception and may join as a school.

When a shark is already curious and a heavy disturbance fires, it definitely moves toward the new source. Light disturbances are ignored. Blood still escalates to hostile.

There are some safety rules. Sharks never target players in creative or spectator mode. If a target has been out of water for 60 ticks (three seconds), the shark drops it and returns to idle. Sharks in shallow water (less than 3 blocks deep) move at 60% speed. Sharks check for beach biomes every 40 ticks and walk back toward deeper water if they're near shore. If a target dies mid chase the shark drops it and returns to idle, so no more frozen poses after a successful kill.

You can scale how sensitive sharks are to each disturbance type with the three multipliers in `[disturbance]`. Setting any of them to 0.0 disables that type entirely. Particle and audio feedback also have their own toggles.

## 7. Predators and prey

Sharks aren't just reactive. They actively hunt.

Each species now reads its own datapack tag at `bensfintasticsharks:prey/<species>`. The broad `bensfintasticsharks:shark_prey` tag remains as a fallback for future shark types. The eight lists differ by diet, but all include `minecraft:drowned`. Datapacks can tune each species independently.

Players are deliberately not in the prey tags. Sharks can target a player through the disturbance system or retaliation; Blacktip Reef Sharks can also join an existing attack on a badly wounded swimmer. Creative and spectator players remain excluded.

Sharks do not orbit or circle their target. They lock on, swim straight at the prey at aggro speed, and bite when they're in melee range. There used to be a circle attack behavior in earlier builds. It was removed because it looked artificial. The current chase is a direct approach.

The bite is two phase. When the shark enters bite range, it swings its body and plays the bite animation. Damage lands five ticks later, on the visual impact frame of the animation. This way the hit syncs with the snap of the model instead of feeling like the damage came out of nowhere.

When a shark is hurt by a living attacker, it can defend itself, but ordinary sharks no longer recruit the surrounding population. Blacktip Reef Sharks are the pack-defense exception: a confident blacktip can alert nearby blacktips, with only part of the school joining the attack. It never spawns new sharks; it only rallies existing schoolmates.

On the prey side, every peaceful BFS species checks for nearby apex predators every twenty ticks. If a predator is within 12 blocks, the prey sets a walk target 8 blocks in the opposite direction at 1.4x speed. Sharks are faster than prey at their base speeds, so the chase still resolves in the shark's favor most of the time, but the prey gets a real escape attempt.

The pacing of the whole thing is deliberately slower than vanilla water mobs. Minecraft is a slow paced game and the mod tries to fit. Sharks cruise with a 0.16 multiplier on their attribute speed and a hard horizontal velocity cap at 0.40 blocks per tick (0.55 while chasing). Non shark aquatic mobs cap at 0.45 blocks per tick by default, and the seafloor species (octopus, lobster, nautilus) cap tighter at 0.12 to 0.18 blocks per tick.

## 8. Individual species behaviors

### Sharks

Every shark inherits the same base behavior from `AbstractSharkEntity`. They share the wander brain, disturbance reactions, turn-aware direct chase, shallow-water slowdown, beach avoidance, bite impact delay, and target-left-water disengage rule. Whole-body renderer pitch makes ascent and descent visible for all eight.

What varies between species is the tuning. Each one has its own detection radius, aggression level, aggro speed, disengage timeout, bite cooldown, and bite damage.

Some species have extra behaviors on top. The Tiger Shark scans for dropped items in water every 200 ticks. If it finds one, it chooses a stable reachable point below the item and approaches for a cosmetic bite. The item is not consumed. The investigation times out after 200 ticks and the same item cannot be selected again for 600 ticks. The Oceanic Whitetip has a limited, 20 block same species blood convergence for valid prey and a longer disengage timeout than other sharks, 600 ticks instead of 300. A successful Oceanic Whitetip player bite has a 10% chance to start a 40 tick grab. The victim is held at the mouth, takes 2 damage every 10 ticks, and is released when the timer ends, the shark or victim dies, either leaves valid water, or the entity is removed. The Blacktip Reef Shark is the only player frenzy and pack defense species. A successful player bite has a 20% chance to start its smaller 30 tick latch. The initial bite remains the only damage from the latch itself. It releases through the same bounded water, life, timeout, and removal checks and does not use the large shark thrash or camera effect. Blacktips naturally spawn in groups of one to two, with a configured minimum of two. The Sandtiger has a 10% chance every minute while idle to hover motionless for 5 to 10 seconds, and plays a tail whip warning animation when first turning curious.

### Marine mammals

The Bottlenose Dolphin surfaces to breathe every 30 to 60 seconds, swimming up to the world surface, splashing at the top, and playing the dolphin splash sound. While in shallow water it has a 5% chance per check to breach into a leap with the dolphin jump sound. It detects floating items every two seconds, walks toward them, and nudges them with an upward and horizontal velocity push. Items aren't consumed. It detects moving boats with a player driver every second, follows about 4 blocks behind at 1.3x speed, and stays in that mode for up to 30 seconds. Nearby sprint swimming players within 9 blocks get Dolphin's Grace for five seconds, refreshed while they stay in range.

The Orca is peaceful by design in Legacy 1.0. The plan for BFS 2.0 is for orcas to hunt sharks, but this build keeps them as imposing peaceful presences. They surface for a blowhole spout that is a taller bubble column and a lower pitched splash sound than the dolphin's. They wander wide territory and do not approach or orbit players. The current build uses the male Orca model only. Old `Female` NBT is ignored safely.

### Cephalopods

The Common Octopus has a 35% chance every 30 seconds to enter hide mode for 10 to 25 seconds. While hiding it switches to a ground idle animation and slows to a near stop. If a player gets within 5 blocks or the octopus is hurt, it emits squid ink particles, plays the squid squirt sound, and pushes itself away from the player. Both octopuses lean smoothly into their travel direction, use a faster but still restrained swim pace, and test alternate reachable water paths when a predator would otherwise pin them in a corner. Both models already include animated siphons.

The Caribbean Reef Octopus is the same logic with a slightly smaller size, scoped to warm ocean biomes only.

The Nautilus is the slowest mob in the mod. It drifts in tiny radii, rests in place 20% of the time for 5 to 10 seconds at a stretch, and has no flee behavior at all. If attacked, it sinks downward and keeps going about its business. Its resting state syncs to clients, the hide animation only triggers from that state, and the move loop now includes a gentle extension/compression pulse. Travel acceleration and its cap were raised just enough for the drift to be visible.

### Other fauna

The Common Stingray hugs the seafloor. It has a persistent downward gravity bias of 0.04 blocks per tick. Its wander radius is 8 horizontal by 1 vertical, and it idles 70% of the time. It rarely leaves the bottom. Contact with the stingray applies Poison I for 5 seconds.

The Harbor Seal swims and walks. Its idle brain picks a swim target with weight 6, a walk target with weight 2, or idle with weight 2. Its biome entry spawns one to two and the configured minimum group size is two. In cold biomes, including Cold Ocean and Frozen Ocean, it uses the arctic texture variant.

The American Lobster is the food source. It has the highest spawn weight in the mod (100) and the largest default cap (10). It rests 50% of the time, pausing for 5 to 20 seconds at a stretch and playing its synced idle animation. Close-waypoint braking prevents the old spinning, its vertical wander is limited to one block, and a downward bias returns it to the seafloor. If a player makes contact, it snips for 1.0 damage with a 1.5 second cooldown. Killing one drops 1 or 2 raw lobster claws and 1 raw lobster tail. Both can be cooked into food.

The Giant Moray Eel is anchored in place 70% of the time, motionless for 20 to 40 seconds at a stretch. Players within 2 blocks trigger a lunge attack for 2.0 damage. It spawns only in warm oceans at weight 40 with a cap of 4. Out of water it uses its dedicated beached animation instead of swimming in place.

The Green Sea Turtle is a wild encounter species only. It swims slowly and rests occasionally. It does not lay eggs in this build. Use vanilla turtles for breeding.

Atlantic Cod directly extends vanilla Cod, and Atlantic Salmon directly extends vanilla Salmon. Both have 3 health and inherit vanilla fish swimming, water navigation, panic, player avoidance, schooling, persistence, bucket interaction, and beached flop behavior. Cod uses the vanilla maximum school size of eight. Salmon uses the vanilla maximum school size of five. Neither fish has custom combat or hunting logic, while shark prey tags still let sharks hunt them. A salmon named exactly `Spin` stays in its supplied looping spin animation. Changing or removing that exact custom name returns the renderer to its ordinary idle, swim, fast swim, or flop state.

### Jellyfish

Both jellyfish use a custom travel method instead of brain pathfinding. They drift with a sinusoidal vertical bob and a slow horizontal current of 0.025 blocks per tick. Every 30 seconds they pick a new drift direction so blooms eventually move around.

There's a surface seeking bias built in. The drift direction tries to stay around 4 blocks below the world surface. If they're deeper than that they rise. If they're shallower they sink. This means jellyfish slowly migrate to the upper water column instead of staying at the spawn depth.

Contact with the Black Sea Nettle does 1.0 damage and applies Poison for 8 seconds. The sting box extends 0.6 blocks horizontally around the bell and 3 blocks down so the tentacles also sting. The Cannonball does 0.5 damage and applies Weakness for 6 seconds. Both check every 10 ticks so swimming through a jellyfish is now a real hazard.

The Black Sea Nettle no longer renders glow squid particles. Its bell underside UVs now use authored texture regions, and the asymmetric tentacle rotation was removed from the beached animation so one strand no longer sticks out.

When a jellyfish ends up on land it plays a sad squished beached animation. The Black Sea Nettle still stings while beached.

## 9. Variants

Most species ship multiple skin variants. On natural spawn, the variant is chosen randomly from a weighted list. You can also pick a specific variant with the spawn egg.

Shift plus right click the spawn egg in air to cycle through that species's variants. The current variant shows in a small action bar message and appears as a tooltip line on the egg. Right clicking on a block then spawns the entity with that variant applied. Spawn eggs for species that have only one texture (Caribbean Reef Octopus, Orca, both jellyfish) don't cycle and don't show the variant tooltip line.

`/summon bensfintasticsharks:american_lobster ~ ~ ~ {Variant:4}` also works for any species. The variant id is the table index below.

### Variant tables

| Species | Count | Variants (id : name) | Notes |
|---|---|---|---|
| Great White Shark | 7 | 0 default_1, 1 default_2, 2 default_3, 3 default_4, 4 default_5, 5 default_6, 6 albino | Weighted |
| Great Hammerhead | 6 | 0 default_1 through 5 default_6 | Weighted |
| Common Thresher | 4 | 0 default_1, 1 default_2, 2 default_3, 3 zippy | Zippy is rare |
| Shortfin Mako | 5 | 0 default_1, 1 default_2, 2 default_3, 3 melanistic, 4 albino | Melanistic and albino are rare |
| Tiger Shark | 4 | 0 default_1, 1 default_2, 2 default_3, 3 sandy | Sandy is rare |
| Oceanic Whitetip | 6 | 0 default_1 through 5 default_6 | Equal |
| Sandtiger | 3 | 0 default_1, 1 default_2, 2 default_3 | Equal |
| Blacktip Reef | 4 | 0 default_1, 1 default_2, 2 default_3, 3 default_4 | Equal |
| Bottlenose Dolphin | 5 | 0 default_1, 1 vanilla, 2 whitebelly, 3 blacktip, 4 blacktip_whitebelly | Equal |
| Common Octopus | 4 | 0 default_1, 1 default_2, 2 default_3, 3 default_4 | Equal |
| Caribbean Reef Octopus | 1 | 0 default_1 | Single texture |
| Nautilus | 2 | 0 default_1, 1 default_2 | Equal |
| Common Stingray | 4 | 0 default_1, 1 default_2, 2 default_3, 3 specimen_8 | Specimen 8 is rare |
| Harbor Seal | 4 | 0 default_1, 1 default_2, 2 default_3, 3 arctic | Arctic forced in cold biomes |
| American Lobster | 5 | 0 default_1, 1 default_2, 2 default_3, 3 blue, 4 red | Blue at about 1%, red at about 3% |
| Giant Moray Eel | 3 | 0 default_1, 1 default_2, 2 default_3 | Equal |
| Green Sea Turtle | 3 | 0 default_1, 1 default_2, 2 default_3 | Equal |
| Orca | 1 | 0 default_1 | Single texture |
| Black Sea Nettle | 1 | 0 default_1 | Single texture |
| Cannonball Jellyfish | 1 | 0 default_1 | Single texture |

Variants persist in NBT under the `Variant` integer tag, so they survive saves and loads, and they sync to the client.

## 10. Sunken Trove

A small underwater dig site that contains a chest with our loot table. It generates in non frozen ocean biomes: Ocean, Deep Ocean, Lukewarm, Deep Lukewarm, Warm, Cold, Deep Cold. Frozen oceans are excluded because trove locations would teleport players onto the surface ice rather than to the underwater feature.

The placement uses the jigsaw structure system with the underground decoration step and `OCEAN_FLOOR_WG` heightmap projection. The actual feature scans downward from the jigsaw origin through water until it hits a solid block, then builds there. This is what stops the trove from ending up above the water surface, which was happening in earlier builds.

The site has an irregular 7×7 excavation floor made from sand, gravel, sandstone, and a cut-sandstone center around the waterlogged chest. Two uneven broken shrine pillars frame the back, waterlogged sandstone stairs/slabs and blocks form scattered rubble, and a recessed sea lantern makes the silhouette readable in deep water. Seven to ten seagrass clumps reclaim the edges, with a 50% chance for a 2 to 4 block kelp stalk.

The placement settings are `random_spread` with spacing 24 chunks and separation 12. That's pretty dense so you can find them easily for testing. You can find one with `/locate structure bensfintasticsharks:sunken_trove` or via the tag `#bensfintasticsharks:trove`, and force place one with `/place structure bensfintasticsharks:sunken_trove`.

The chest rolls 2 to 4 items from a pool of paper (weight 30), iron ingot (20), gold ingot (15), book (15), lost manuscript (15), emerald (10), Captain Ben's Hat (5), and diamond (5). Captain Ben's Hat also drops from vanilla buried treasure at 5% and shipwreck map chests at 3% via separate global loot modifiers, so there are multiple paths to find one.

## 11. Lost Manuscript, Codex Page, Codex Volume, and the Shark Codex

These four items form a chain. You start with the Lost Manuscript, which is a loose page of someone's old notes. You find it in Sunken Trove chests and in vanilla dungeon, shipwreck, buried treasure, and ocean ruin chests at 5% per roll (a global loot modifier handles the vanilla side, so the chance applies to each roll the table makes, not the chest as a whole).

Nine Lost Manuscripts in a square crafts one Codex Page. Nine Codex Pages crafts one Codex Volume. A Codex Volume plus a Book, a Shark Tooth, and a Tropical Fish Bucket shapeless crafts the Shark Codex.

The Shark Codex is the readable artifact. Right click it in hand and a written book reader opens with fifteen pages of in world prose about the sharks, their behavior, the disturbance system, how prey targeting works, conservation, and the spawn rules. The codex item is single stack and stays in your hand after closing the reader. There is no save state. Closing the book just dismisses the screen. You can craft as many as you want.

The intended progression is exploration heavy. Drop into the ocean, raid a few troves and shipwrecks, accumulate manuscripts, work up to a codex. None of the recipes are gated by an advancement, so you can also just stack up manuscripts and skip ahead if you've got them.

## 12. Captain Ben's Hat

A cosmetic head slot armor with leather tier stats (3 armor) and the Rare rarity glow.

When worn, it renders as a 3D GeckoLib model attached to the player's head bone. The cubes sit above the head top so it actually looks like a hat instead of clipping the face. The feather planes have a tiny depth separation so their lower halves do not texture-fight. In the inventory it shows up as a 2D pixel sprite.

The tooltip says "Once worn by a captain who respected the sea." in italic gray.

You can find one in Sunken Trove chests, vanilla buried treasure chests, or vanilla shipwreck map chests. Admins can also `/give @s bensfintasticsharks:captain_ben_hat`.

## 13. Shark Trident

A trident reskin that exists in the alpha build. Latest pass fixed two bugs.

The in flight orientation now rotates with yaw and pitch so the point follows velocity, matching vanilla `TridentRenderer`. Previously the model floated through the air pointing in whatever fixed direction the static item model defined.

The throwing animation now uses vanilla display transforms. While drawing back, the trident is held in the same pose as a vanilla trident instead of the original weird sideways tilt.

It's riptide enabled (works in water or rain), throws like a vanilla trident, and has the tooltip "Best used for show. Sharks are friends." in italic gray.

## 14. Spawn placement and movement rules summary

Sharks have several rules that keep them where they belong.

Beach safety is enforced both at spawn time and during play. At spawn time, sharks won't pick a position within 24 blocks of a beach biome, sampled every 8 blocks, and require five blocks of water beneath them. At runtime, sharks check their surroundings every 40 ticks and walk toward deeper water if they're near a beach or in shallow water.

Natural shark spawns also enforce the configurable local spacing described in sections 2 and 4. Different species do not stack within the default 96 block radius. The local default is one non blacktip or three blacktips.

Shallow water slowdown applies to every shark. In water less than 3 blocks deep, they move at 60% of their normal speed. This gives you a window to swim to shore if one's chasing you.

Sharks can't actually leave water. The pathfinding uses the water bound path navigator which rejects land tiles. If their target leaves water for more than 60 ticks, they give up and return to idle.

Stingrays apply persistent downward gravity (0.04 blocks per tick). They essentially rest on the seafloor unless their AI walks them somewhere specifically.

Sharks use continuous vertical steering and visibly lean into meaningful ascent or descent. The turn aware control reduces horizontal thrust during a hard turn without suppressing the depth input requested by navigation. Sharks coast when they need to reorient, brake as they arrive, and remain level when their current route does not require a depth change. Octopuses also visibly lean into vertical travel. Lobsters remain level as seafloor crawlers. The Nautilus may pitch toward a vertical destination and now has enough controlled acceleration to reach it.

All BFS aquatic mobs now drown when beached. They follow the vanilla water animal pattern: when out of water their air supply ticks down by one per tick, and when it hits negative twenty they take two drown damage and reset the counter. This means a beached shark or stingray won't just live forever on the sand. Mammals with their own air systems (the dolphin) override this so they can still surface to breathe.

## 15. Crafting and food

Killing an American Lobster drops 1 or 2 raw lobster claws plus 1 raw lobster tail.

You can cook either one in a furnace (200 ticks) or on a campfire (600 ticks) to get the cooked variants. Raw lobster gives you 2 nutrition and 0.2 saturation. Cooked gives you 6 nutrition and 0.8 saturation and counts as meat for vanilla effects.

Atlantic Cod and Atlantic Salmon each drop one matching raw fish. If the fish dies while on fire, the entity loot table smelts the drop into its cooked form. Both raw fish also appear in vanilla fishing gameplay through Forge global loot modifiers at an independent 12.5% chance per completed catch. Furnace, smoker, and campfire recipes cook each raw item. Raw Atlantic Cod and Raw Atlantic Salmon provide 2 nutrition and 0.1 saturation. Cooked Atlantic Cod provides 5 nutrition and 0.6 saturation. Cooked Atlantic Salmon provides 6 nutrition and 0.8 saturation.

The Codex chain takes Lost Manuscripts and refines them into a Shark Codex. See section 11.

The Shark Trident and Captain Ben's Hat are find only.

## 16. Advancements

The advancement tree starts at `Marine Curious`, which fires when you encounter any BFS species. From there it branches into encounter chains and themed milestones.

Encounter advancements exist for every species the mod ships. Shark Spotter requires actively using a Spyglass while the view ray reaches a BFS shark before any solid block. It uses one custom criterion and a vanilla Spyglass icon, so it no longer shows the old `0/28` species counter. Sharks Galore is the single all eight shark discovery node. Marine Biologist and Apex of Apex now continue directly from Sharks Galore. Every individual shark encounter uses its matching supplied 16×16 sprite: Great White, Great Hammerhead, Common Thresher, Shortfin Mako, Tiger, Oceanic Whitetip, Sandtiger, and Blacktip Reef. Marine Biologist covers all twenty two species; other milestones include Apex of Apex (Orca), Dolphin Friend (Common Bottlenose Dolphin), Inked (either octopus), and Stung (either jellyfish).

The Atlantic fish add four advancements. `Gadus morhua` and `Salmo salar` trigger when the player encounters the matching living fish. `Oh My Cod` and `Why aren't you red?` trigger only when the matching raw fish is obtained from a fishing hook catch. Their icons use the supplied raw or cooked item sprites according to Ben's content notes.

The themed ones cover progression. Apex Awareness fires when any shark damages you. Wrong Place, Wrong Time is the survivor variant. Sleeping with the Fishes fires when a shark kills you. Fresh Catch fires when you have any cooked lobster meat in your inventory. Hidden Trove fires when you pick up a Lost Manuscript. Captain's Heir fires when you obtain Captain Ben's Hat. Conservationist is the gentle path: encounter the first three sharks without violence required.

Run `/advancement grant @s only bensfintasticsharks:marine_biologist` if you want to test the capstone.

## 17. Conservation

The mod gently discourages killing protected sea creatures.

When a non creative player kills any entity tagged `bensfintasticsharks:conservation_protected`, they receive a custom effect called Respect the Ocean for two minutes. It has no mechanical impact at all. The icon shows up in your effects list with the description "The ocean is watching..."

Nineteen of the twenty two species are in the conservation tag. American Lobster, Atlantic Cod, and Atlantic Salmon are the food source exceptions.

Disable the effect entirely with `[visuals] conservation_debuff_enabled = false`.

## 18. Configuration cheat sheet

A few common scenarios.

Lowest category ceilings (category caps cannot be set to zero):
```toml
[spawning]
  apex_predator_cap = 1
  bfs_water_creature_cap = 1
  bfs_water_ambient_cap = 1
```

To disable natural spawning, set the desired species to zero under `[caps]` or `[spawn_chance]`.

To keep the default Cod and Salmon replacement:
```toml
[spawning]
  replace_vanilla_mobs = true
```

To keep vanilla Cod and Salmon while spawning separate Atlantic fish from BFS biome modifiers:
```toml
[spawning]
  replace_vanilla_mobs = false
```

To suppress the remaining covered vanilla aquatic natural spawns:
```toml
[spawning]
  disable_vanilla_aquatic_spawns = true
```

Disable specific species:
```toml
[caps]
  orca = 0
  great_white_shark = 0
```

Half of the default 0.23 shark spawn probability:
```toml
[spawn_chance]
  great_white_shark = 0.2
  shortfin_mako_shark = 0.2
```

Hard mode sharks:
```toml
[combat]
  shark_hp_mult = 2.0
  shark_damage_mult = 1.5
```

Tiger Sharks specifically hardcore:
```toml
[hp_mult]
  tiger_shark = 3.0
[damage_mult]
  tiger_shark = 2.0
```

Make Orcas tanky and unkillable:
```toml
[hp_mult]
  orca = 5.0
[knockback_resistance]
  orca = 1.0
```

Quieter ocean for low end servers:
```toml
[spawning]
  apex_predator_cap = 1
  bfs_water_creature_cap = 6
  bfs_water_ambient_cap = 8
```

Lobster farm setup (bigger cap, larger groups):
```toml
[caps]
  american_lobster = 20
[group_size_min]
  american_lobster = 4
```

## 19. Compatibility

Existing 1.20.1 worlds load cleanly. The four alpha build sharks (Great White, Hammerhead, Thresher, Mako) and the two alpha fauna (Stingray, Harbor Seal) kept their registry IDs, NBT structures, and biome modifier references. Version 0.23 only adds new registry IDs and does not rename prior entities, items, tags, or advancements. Version `0.23-emergency-fix` changes only the Atlantic fish population category and replacement safety checks.

BFS mob categories are independent of vanilla categories except Atlantic Cod and Atlantic Salmon, which always use vanilla's water ambient category. Default replacement converts accepted vanilla Cod and Salmon spawn attempts into one matching Atlantic fish and suppresses the separate Atlantic biome attempts. With `replace_vanilla_mobs = false`, vanilla and Atlantic fish use separate spawn sources but share the same category ceiling. Enabling `disable_vanilla_aquatic_spawns` changes only future natural vanilla aquatic spawn attempts and does not delete existing mobs.

Dolphin boat follow uses a vanilla `Boat.class` instance check. Modded boats that don't extend `Boat` won't trigger the follow behavior.

Other mods can opt their water mobs into shark targeting through the relevant `bensfintasticsharks:prey/<shark_species>` tags; `bensfintasticsharks:shark_prey` is the broad fallback. The same applies to `bensfintasticsharks:apex_predator` if a mod adds its own sharks and wants the BFS prey to flee from them, or `bensfintasticsharks:conservation_protected` if they want the Respect the Ocean effect to fire on kills.

## 20. Troubleshooting

If no mobs are spawning:

First run `/bfs list` to see all the current per-species caps. If everything is zero, natural BFS spawning is disabled. Category caps in `[spawning]` have a minimum of one, so use `[caps]` or `[spawn_chance]` when you want to disable a species.

Then run `/bfs count <species>` near where you're testing to see how many of that species are already in your area. If the count matches the cap, no new ones will spawn until some leave.

Check that the biome you're in is in the species's spawn list (section 3). Orca only spawns in cold ocean. Whitetip only spawns in Deep Ocean and Deep Lukewarm Ocean. Caribbean Reef Octopus and Giant Moray Eel are warm ocean exclusive. Nautilus needs deep water and prefers night. In default replacement mode, Atlantic Cod follows vanilla Cod habitats and Atlantic Salmon follows vanilla Salmon habitats. With replacement disabled, their narrower BFS biome tags apply.

Make sure `[spawn_chance].<species>` isn't 0.

If vanilla aquatic mobs still spawn after enabling their suppression switch, restart the game or dedicated server. The setting intentionally does not remove mobs that already exist and does not block commands or spawn eggs.

If vanilla Cod or Salmon still appears naturally or from its spawn egg, confirm `[spawning] replace_vanilla_mobs = true`. Existing fish, commands, buckets, and spawner output are intentionally not converted. If separate Atlantic biome spawns are wanted alongside vanilla fish, set the option to `false`.

If a world was opened with the original 0.23 build and already contains an excessive Atlantic fish population, install `0.23-emergency-fix` first. The fixed build prevents new replacements from escaping the water ambient population ceiling but does not delete existing entities. Excess fish can leave or despawn through inherited vanilla fish behavior. An operator who wants immediate cleanup can run the following commands in each affected dimension. They remove all loaded matching Atlantic fish in that dimension, including named or intentionally placed fish.

```mcfunction
/kill @e[type=bensfintasticsharks:atlantic_cod]
/kill @e[type=bensfintasticsharks:atlantic_salmon]
```

If sharks aren't reacting to you:

Try `/bfs disturbance heavy` to fire a test disturbance at your position. The command will report how many sharks are in the radius. If it says zero, you need to spawn some sharks first. If it says a positive number and they still don't react, check `[disturbance].heavy_sensitivity_mult` isn't 0.

Sharks ignore disturbances on land. Be in water.

Sharks ignore creative and spectator players entirely. Switch to survival.

If a shark's animation looks frozen after a kill:

That was a Mako specific bug in an earlier build. The Mako's controller previously referenced an idle animation that didn't exist in its geo file, which froze the model on the last bite frame after the target died. Latest build fixes that by removing the missing idle reference. Targets that die mid chase also clear the shark's target now, so the shark resumes wandering.

If a spawned mob always looks the same:

Variant rolls happen in `finalizeSpawn`, which only runs when the entity is initially spawned. Mobs from `/summon` without an explicit `{Variant:X}` get whatever the default is (variant 0). Spawn eggs with a cycled variant apply it after the egg's normal spawn flow. `/summon bensfintasticsharks:<species> ~ ~ ~ {Variant:N}` is the deterministic way to test specific variants.

If the Sunken Trove isn't appearing:

Run `/locate structure bensfintasticsharks:sunken_trove` to see if there's one within search range. If yes, swim to it. If no, you might not be in an ocean biome. The structure does not generate in frozen oceans by design.

`/place structure bensfintasticsharks:sunken_trove ~ ~ ~` will force place one at your current location. Try that for sanity checking.

If sharks feel too fast, drop `[ai].shark_detection_radius_mult` to 0.5 so they're less aware of you, or edit the base movement speed attribute via a datapack attribute modifier if you want them genuinely slower.

If a dolphin or octopus moves at unreasonable speed, that's a bug worth reporting. The mod caps horizontal velocity at 0.45 blocks per tick for non shark aquatics (tighter for octopus, lobster, nautilus) so the mach 10 visual from earlier builds is fixed.

If stingrays are stuck on a ledge floating, they'll drift off the side within a few ticks because of the downward gravity bias.

## 21. Mod metadata

The mod ID is `bensfintasticsharks`. It targets Minecraft 1.20.1 and Forge 47.2.0. Required dependencies are GeckoLib 4.4.7 and SmartBrainLib 1.14.2. The license is CC0-1.0. Authors are Tfarcenim and BenLikesSharks.

## 22. Development and validation

Use Java 17 and the checked in Gradle Wrapper. Java 21 and newer may compile the source, but the current Gradle and Forge tooling must be launched with Java 17.

Linux commands:

```bash
./gradlew :forge:compileJava
./gradlew :forge:Data
./gradlew :forge:build
./gradlew :forge:Client
./gradlew :forge:Server
```

Windows commands:

```bat
gradlew.bat :forge:compileJava
gradlew.bat :forge:Data
gradlew.bat :forge:build
gradlew.bat :forge:Client
gradlew.bat :forge:Server
```

Server backed interactive verification uses a dedicated Forge server on `node-1` and a matching windowed client on the Linux laptop. Start the server with `./gradlew :forge:Server --no-daemon --args='--port <port> --nogui'` on `node-1`, then connect the laptop client with `./gradlew :forge:Client --no-daemon --args='--username Dev7 --quickPlayMultiplayer <tailscale-server-ip>:<port>'`. Use the server console or authenticated RCON for fixture setup and cleanup. Never run a windowed client, Xvfb, or rendering workload on `node-1`. For hidden laptop capture, keep the Minecraft window on a nonactive Hyprland workspace, set `pauseOnLostFocus:false`, and request it with `DISPLAY=:1 import -window <minecraft-window-id> <capture-path>` while verifying that the active workspace is unchanged.

The `Data` task writes to `common/src/generated/resources`. Run it after changing any provider, then inspect the generated diff. The distributable artifact is written to `forge/build/libs`. Inspect the JAR for `META-INF/mods.toml`, the `bensfintasticsharks` assets and data namespaces, mixin configuration, and required embedded metadata before release.

There is no maintained formatter or static analysis task. The Forge module has focused JUnit and Forge GameTests for pure policy and resource behavior, including the exact vanilla fish replacement allowlist and algae placement. Source changes must at minimum compile and pass `:forge:test` and `:forge:build`. Resource and data changes must also pass `:forge:Data`. The root `./gradlew test` skips the unsupported Fabric template and runs the supported Forge tests. The root `./gradlew build` still includes that Fabric template, which has no SmartBrainLib dependency and is not a release gate for this Forge only project. Common initialization, configuration, registration, command, or server behavior changes require a dedicated server smoke test. Rendering, models, textures, or client synchronization changes require a client smoke test.

The `common` module owns loader independent entity behavior, registries, tags, and shared resources. The `forge` module owns Forge lifecycle registration, commands, configuration, biome modifiers, spawn placement, rendering, and data generation. Common code must not import Forge or client classes. Client rendering stays under the Forge client packages. The `fabric` module is an unused template stub and is outside supported scope.

Generated build output, run directories, logs, caches, IDE settings, and local configuration must not be committed.
