# Changelog

## 0.22 Movement, rarity, and species info (2026-07-23)

### Added

- **Expanded creative species information.** `/bfs info <species>` now reports scientific name, current habitat tags, behavior, diet, configured health, variant count, registry ID, spawn category, and natural cap. The command is limited to creative operators and non player command sources.

### Fixed

- **Satiated sharks defend themselves.** A shark on its post meal hunt cooldown now retaliates against living attackers instead of ignoring them.
- **Routine shark cruising is level.** Small vertical waypoint corrections are damped while a shark is not hunting or fleeing. Meaningful climbs, dives, pursuits, and escapes keep full vertical steering and body pitch.
- **Tiger Sharks stop circling floating items.** Their curiosity waypoint now leads below the drifting item, uses a wider arrival zone, and stops navigation after the cosmetic bite.
- **Sand Tiger and Blacktip Reef bite reach is close range.** Both species now cap bite distance at 1.75 blocks.
- **Prismarine armor follows the swimming pose.** The GeckoLib armor renderer now copies the actively posed wearer model, and the armor geometry uses the expected GeckoLib armor identifier.
- **Prismarine chestplate fits the full swimming stroke.** The sleeves stop above the wrists and retain pivots sized for standard four pixel player arms. While the swimming pose blends in, the bulky torso layers contract horizontally just enough to clear the arms at peak breaststroke without changing the standing fit.
- **BFS logo appears in both UI locations.** The creative tab keeps its existing BFS logo item, and the Forge mod list now receives the same logo at the metadata path expected by `mods.toml`.
- **Zippy glow presentation.** The glow mask aligns with the base texture, so the asset was not shifted. The emissive layer now appears only in low light instead of washing over the daylight skin.
- **Broken female Orca rendering removed.** Female state, NBT, scale, and dorsal fin deformation are disabled until the replacement model is ready. Existing `Female` NBT is ignored.

### Changed

- **Sharks are substantially rarer.** The apex category cap defaults to one, mixed shark spacing defaults to 96 blocks, non blacktip local density defaults to one, blacktip density defaults to three, shark species caps default to one, and shark spawn chance defaults to 0.4.
- **Harbor Seals are rarer.** Their weight is 20, group size is one to two, species cap is three, minimum group size is two, and spawn chance defaults to 0.5.
- **Shortfin Mako habitat corrected.** Natural Makos now spawn only in Lukewarm and Deep Lukewarm Oceans. Blacktip Reef Sharks remain in Warm, Lukewarm, and Deep Lukewarm Oceans.
- **Version metadata.** The release version is now `0.22` for Minecraft 1.20.1 Forge.

### Configuration migration

Existing `bensfintasticsharks-common.toml` files keep their old values. Regenerate the file or update the spawning, caps, spawn chance, and group size keys to adopt the 0.22 rarity defaults.

## 0.21 Showcase tools & feedback pass (2026-07-15)

### Added

- **Creative showcase fisherman:** operators can run `/bfs summon villager [trade]` to summon a persistent master-level Fisherman at their position. The villager receives normal Fisherman offers from all five vanilla tiers plus one guaranteed BFS offer at the end of the list. Omitting `[trade]`, or using `random`, chooses from the BFS catalogue at random.
- **Selectable showcase trade IDs:** `great_white_shark_tooth`, `great_hammerhead_shark_tooth`, `common_thresher_shark_tooth`, `tiger_shark_tooth`, `shortfin_mako_shark_tooth`, `oceanic_whitetip_shark_tooth`, `great_white_shark_skin`, `great_hammerhead_shark_skin`, `common_thresher_shark_skin`, `cartilage`, `shark_jaws`, and `megalodon_tooth`. Command suggestions expose the same list.
- **Eight supplied 16×16 shark sprites are now advancement icons.** Each shark's individual encounter advancement uses its matching pixel-art sprite without replacing the normal collectible/item models. Sharks Galore now checks all eight species, matching Shark Whisperer and the complete set of individual encounter criteria.
- **Female orcas:** natural spawns, spawn eggs, and ordinary command spawns now roll male/female at 50/50. Sex persists in the `Female` NBT flag; females render slightly smaller with a shorter dorsal fin. Showcases can force a result with `{Female:1b}` or `{Female:0b}`.

### Fixed

- **Figure-eight/circle chasing:** all sharks now use turn-aware swim steering, arrival braking, faster chase-target refreshes, and a short movement lead. Sharks coast while making a hard turn instead of applying full sideways thrust around a stale waypoint, while close targets are no longer overshot repeatedly.
- **Vertical pursuit:** shark steering preserves vertical thrust and tracks steeper targets. All eight renderers now apply a smooth, clamped whole-body pitch, so a shark visibly angles up or down before levelling out instead of floating vertically beneath or above prey.
- **Drowned are valid prey for all eight shark species.** The Drowned entry lives in each datapack-tunable per-species prey tag as well as the broad fallback prey tag.
- **Cross-species player dogpiles:** wounded players no longer pull every nearby shark into one feeding frenzy. One eligible non-schooling shark becomes the primary responder, while Blacktip Reef Sharks remain the intentional schooling exception. Only blacktips recruit packmates when attacked; other sharks still defend themselves without summoning a local swarm.
- **Tiger Shark and Oceanic Whitetip deaths:** both now play a dedicated 1.5-second death animation and remain for the full 30-tick animation before removal.
- **Missing beached/idle states:** Giant Moray Eels now use a dedicated beached animation. American Lobsters use their synced resting state for a real idle animation. Orcas and Common Bottlenose Dolphins debounce the idle transition when stopped in water instead of flickering between idle and swim.
- **Black Sea Nettle model/animation:** the protruding tentacle was removed from the beached pose, and the previously blank underside faces of the bell now point at authored texture regions.
- **Nautilus readability:** its movement animation now includes a gentle shell/body pulse, its resting state syncs to clients, and its slow travel tuning now produces visible movement without making it a fast swimmer.
- **Octopus corner pinning:** apex-predator escape logic now tests a reachable, away-biased fan of water paths instead of repeatedly pushing an octopus into the same blocked corner. Common and Caribbean Reef Octopus swimming was also tuned for more purposeful movement; both existing siphon bones remain animated.
- **Lobster spinning/floating:** American Lobsters use close-waypoint arrival braking, a one-block vertical wander range, and a stronger downward bias so they turn cleanly and settle back onto the seafloor. The reachable escape-path fix also prevents predator avoidance from pinning them in corners.
- **Captain Ben's Hat feather:** overlapping feather planes now have a tiny depth separation, removing the lower-half texture fighting in both the held/worn models.

### Changed

- **Natural shark spacing:** a new mixed-shark gate checks a configurable 64-block horizontal radius. Different shark types do not naturally stack in that area; non-blacktips permit at most a rare same-species pair by default, while blacktips retain schools up to four. The new `[spawning]` keys are `shark_spacing_radius = 64`, `nearby_non_blacktip_shark_cap = 2`, and `nearby_blacktip_shark_cap = 4`.
- **Sunken Trove:** the old chest-and-few-blocks site is now a readable 7×7 excavation with an irregular sand/gravel/sandstone floor, a cut-sandstone center, two uneven broken shrine pillars, waterlogged stair/slab rubble, a recessed sea lantern, and heavier seagrass/kelp reclamation. Loot and locate/place IDs are unchanged.
- **Version metadata:** release version is now `0.21` for Minecraft 1.20.1 Forge.

### Intentional / deferred

- Shark health values were left unchanged; the high health is intentional protection against casual killing rather than an AI bug.
- Compendium prose is still awaiting authored copy. The existing Codex remains available, but this pass does not invent the planned new compendium content.

## 0.20 Movement & AI bug sweep (2026-07-08)

### Fixed

- **Sharks no longer glide/swim backwards.** Root cause: the 0.19 chase-speed floor set the shark's velocity from a *blend* of its current velocity and the direction to its prey, which could point the shark's motion somewhere its body hadn't turned to yet (the swim steering only rotates 10°/tick), so it visibly slid sideways or tail-first mid-chase. The floor now drives speed **straight along the way the shark is facing**, gated on the body actually pointing at the prey   velocity always matches heading, so there's no backslide, and the anti-orbit behaviour from 0.19 is preserved (a mis-aimed shark coasts and re-aims instead of getting shoved). A second safety net damps any leftover reverse motion on every swim tick.
- **Blacktip Reef Sharks actually hunt now.** They were being falsely "satiated": the 10-minute post-meal hunger cooldown started whenever a shark's target died *for any reason*, so in a reef full of schooling blacktips, one shark's kill (or even a fish another predator killed) put the whole school to sleep for 10 minutes   which kept re-triggering before it wore off. A shark now only counts a kill as *its* meal if it actually landed the killing blow.
- **Great White, Great Hammerhead and Common Thresher now play their fast-swim animation while chasing.** Their animation controller checked a server-only value that's always empty on the client, so the fast-swim clip never showed. Switched to the same synced hunt-state the Mako/Tiger already use.
- **Great White now visibly accelerates when it starts a chase.** Its custom movement code (shared by the Hammerhead, Thresher and Mako) never got the chase-acceleration burst that the other sharks have   it just cruised at a flat speed and relied on the minimum-speed floor. All five movement paths now run through one shared routine, so every shark lunges when locked on.
- **Shortfin Mako no longer looks like it's idling while swimming.** Its swim and fast-swim animations were authored as single-keyframe formulas that the game freezes into a static pose (the same freeze bug fixed for other mobs in 0.13, but in a keyframe format the earlier fix didn't recognise). Both clips now animate   the tail beats through a full stroke, faster when hunting. (Also un-froze the Harbor Seal's `bask3` pose.)
- **A larger shark no longer gives up a chase when the smaller shark it's chasing eats something.** Two causes, both fixed: the small shark's kill was being (wrongly) credited to the big shark as *its* meal (see the blacktip fix above), and a nearby "blood in the water" event could yank the big shark off its live target onto whatever the small shark was biting. Blood now only diverts a shark that isn't already chasing.

### Added

- **Prey flees after a shark bites it.** Anything a shark attacks in the water gets an immediate dart away plus a short sprint to safety   including vanilla fish (cod, salmon, squid) that previously ignored being bitten. Grabbed victims are left alone (they're pinned in the jaws). This is on top of the existing "flee when a shark comes near" behaviour the mod's own sea creatures already had.
- **Smaller sharks flee when a much larger shark attacks them.** A blacktip or sand tiger bitten by a great white (or any shark ≥25% larger) now bolts for ~6 seconds instead of turning to fight a battle it can't win, and it won't call its pack into the hopeless brawl. Same-size sharks still retaliate as before.

### Changed

- **Swimming reads as more fluid.** The swim animation now speeds its tail-beat up and down with how fast the shark is actually moving, instead of looping at a fixed rate regardless of speed   a cruising shark looks calm, a charging one looks frantic. Combined with the backslide and frozen-Mako fixes, this addresses the "swimming looks a bit stiff" report.

## 0.19 Bug sweep + hunger system (2026-07-06)

### Fixed

- **Great Hammerhead (and Great White) grabs now actually hurt.** Only the Mako ever dealt thrash damage while holding a victim; the other two grabbers just counted the timer down and let go   worse, the hammerhead parks its victim at 2.55 blocks while its bite only reaches ~2.4, so it dealt literally zero damage for the whole grab. Both now deal the Mako's 2.0 damage every 10 ticks of the hold.
- **"Press SHIFT to dismount" no longer shows while a shark has you.** The vanilla mount hint was a lie anyway (sneak is disabled during the grab   the jaws decide when you leave).
- **Bite animation vs damage timing fixed for Oceanic Whitetip, Sand Tiger, Tiger, and Shortfin Mako**   same per-species impact-frame fix the Blacktip got: the damage now lands on each clip's jaw-snap frame (7/8/8/11 ticks) instead of the 0.25s default that hit before the mouth even opened.
- **Sharks (and all BFS sea creatures) ignore bubble columns.** Magma-block updrafts were rocketing sharks to the surface; the whole roster now swims straight through.
- **Spawn eggs got their scientific names back.** They'd been hand-edited into a *generated* lang file, so the next datagen run deleted all 20. They now live in the datagen provider itself and can't be wiped again. Also restored the "Chambered Nautilus" and "Sand Tiger Shark" display names that regressed the same way.
- **Chase orbiting ("spinning out of control") fixed.** Root cause: the 0.18 chase-speed floor re-amplified stale, sideways velocity every tick, so an overshooting shark could never slow down enough for its 10°/tick turn to converge   a stable orbit around the prey. The floor is now direction-aware (only boosts when pointed within ~60° of the target, gently re-aiming as it does) and tapers to just above player sprint-swim speed in a 2-block approach band around the bite range, so the final turn can tighten without any species becoming outswimmable. The bite check also runs every tick instead of every 10, so a fast pass through bite range actually connects. Chase speeds themselves are unchanged   no need to go back to slower sharks.
- **Latent one-bite-lockout fixed** (found by the review pass, present since 0.13): if a shark's scheduled bite victim died during the impact-delay window   a packmate's kill, its own thrash tick, a player finishing the fish off   the shark could never bite, grab, or tail-whip again until the chunk unloaded. With pack alerts, shark-on-shark predation, and the longer impact delays this would have fired constantly.
- Blood in the water now respects the prey lists for mobs: a bleeding cow no longer draws sharks (a bleeding cod still draws a hungry thresher, and bleeding *players* still draw every hungry shark in range). The whitetip's blood-convergence also no longer piles its school onto a bleeding shark   including its own packmates, who couldn't even fight back thanks to the same-species guard.
- A timid lone blacktip now actually flees: it drops whatever it was chasing when hit (previously the chase overwrote the flee every half-second and the pack-alert could still fire).
- All eight species now treat a badly wounded player (≤50% health) as blood in the water   previously only the four legacy sharks did.
- **Feeding frenzy fixed / hunger system completed.** The 10-minute hunt cooldown existed but the four legacy species (Great White, Hammerhead, Thresher, Mako) had their own targeting that bypassed it   and targeted *any* mob under half health, plus cows/pigs/sheep. All targeting paths now run through one hunger-gated predicate. A shark that eats does not hunt again for 10 real-time minutes (self-defense vs players is unaffected; satiated packmates also stop joining mob fights).

### Added

- **Per-species prey lists (Ben's Part II spec), datapack-tunable** (`bensfintasticsharks:prey/<species>` entity-type tags):
  - Great White: harbor seal, green sea turtle, bottlenose dolphin, blacktip reef shark, sand tiger shark (+ vanilla turtle, dolphin)
  - Great Hammerhead: stingray, lobster, both octopuses (+ vanilla squid/glow squid, cod, salmon, tropical fish)
  - Common Thresher: vanilla cod, salmon, tropical fish only
  - Shortfin Mako: both octopuses, bottlenose dolphin, green sea turtle, blacktip reef shark (+ vanilla dolphin, squid, turtle, cod, salmon, tropical fish)
  - Tiger: green sea turtle, harbor seal, common octopus, lobster, blacktip + sand tiger sharks (+ vanilla turtle, squid, cod, salmon, tropical fish)
  - Sand Tiger: both octopuses, lobster (+ vanilla cod, salmon, tropical fish)
  - Oceanic Whitetip: stingray, green sea turtle, bottlenose dolphin, common octopus (+ vanilla turtle, squid, dolphin, cod, salmon, tropical fish)
  - Blacktip Reef: caribbean reef octopus, stingray, lobster (+ vanilla cod, salmon, tropical fish)
  - Shark-on-shark predation is intentional and same-species attacks remain impossible.
- **Thresher tail whip now launches its victim** (strong knockback + upward fling, velocity synced to player clients) and shoves any other mobs caught within 3 blocks of the sweep.
- **Blacktip group confidence:** a lone blacktip that gets hit stays timid and bolts; with two or more other blacktips within 16 blocks it retaliates   and its help-call/pack-alert then brings the school. Pack alerts no longer fire from a fleeing (timid) blacktip. Natural spawns come in groups of 2-4 again so the mechanic actually shows up.
- **EXPERIMENT   Blacktip has no idle animation in water** (always plays swim). The velocity-threshold idle gate was the visible half of the "jittery" swim/stop/swim look. Oceanic Whitetip keeps its idle as the side-by-side control; say the word and the change spreads or reverts.

### Changed

- **Sharks are much rarer, especially near shore.** Apex-predator cap default lowered 3 → 2 (existing config files keep their old value   delete `apex_predator_cap` from `bensfintasticsharks-common.toml` to pick up the new default), beach exclusion ring widened 8 → 24 blocks, and predators now require at least 5 blocks of water beneath them to spawn   no more sharks materializing in waist-deep shore water inside encounter-advancement range of the beach.
- Datagen spawn weights synced to the shipped 0.13 values (they had silently diverged; a datagen run would have restored the old, much higher weights).
- **Shark Trident damage 9 → 11** (melee attribute +10; thrown damage raised to match at 11   vanilla tridents throw at 8).

### Known / punted

- **Zippy texture:** nothing new has arrived in the repo   `zippy.png` is still the 2024 file with the transparent body strip (the see-through tail). All variant plumbing is done; drop the repainted 128×128 `zippy.png` in place and it works, no code change.
- **Shark block-item advancement icons:** already fixed in this working tree   the bake tool output for all five display models validates clean against vanilla's rotation rules (Ben's report was from a build predating the bake). No 16×16 pixel icons needed; verify in-game before shipping.
- Hammerhead/Great White/Mako thrash kills count as the shark's meal and start its 10-minute hunger like any other kill.

## 0.18 Content drop + feedback round (2026-07-05)

### Added

- **Megalodon Tooth**   Ben's carved-netherite gag collectible (Epic rarity, fire-resistant, tooltip: *"100% authentic Otodus megalodon specimen tooth!...Probably."*). Sold by ~1% of master fishermen for a full stack of emerald blocks (576 emeralds), one sale per restock cycle. The scam is the point.
- **"Source: trust me bro"** advancement (challenge frame)   obtain a Megalodon Tooth.
- **Tiger, Shortfin Mako, and Oceanic Whitetip shark teeth**   join the existing three in the `shark_teeth` tag (usable in shark tool/armor recipes) and the master-fisherman catch table (12 emeralds, ~6% of master fishermen each).
- **Shark Jaws** is now a real item (was a hidden advancement icon): Ben's revamped skeleton-jaws sprite, Rare rarity, and the rarest fisherman find short of the Meg Tooth (48 emeralds, ~2.5% of master fishermen). Still doubles as the Conservationist advancement icon.
- Nautilus now drops a nautilus shell (the second exception, after the lobster, to the "protected species drop nothing" rule   it's capped at 1 per area and hides in the deep).
- Grab/thrash screen effects: while a Great White / Great Hammerhead / Shortfin Mako thrashes you, mouse-look is locked (the camera whips around with the thrash, facing the jaws; sneak can't dismount) and a red tint ramps in, fading out over ~1.5s after release. Tint intensity is one constant in `GrabClientHandler` if it lands on the "cringy" side of the experiment.
- `tools/bake_blockitem_rotations.py`   converts Blockbench generic-model exports into legal vanilla item models by baking quarter-turns into the geometry (with face/UV remapping) and snapping remainders to the 22.5° grid.

### Changed

- **Advancement icons:** "King of the Seas" (Great White), "Fast as hell, twice as mean." (Shortfin Mako), and "Whiplash!" (Common Thresher, the full-body model with the curled tail whip) now use Ben's 3D block-item models; "Stop! Hammer Time!" got the updated hammerhead model. Vanilla item models only allow one rotation axis per cube on a 22.5° grid, and Ben's exports carry free multi-axis rotations (the nonstandard `rotated` key vanilla silently ignores   in-game they'd render axis-aligned). The new bake tool decomposes each cube's true rotation into exact quarter-turns baked into the geometry plus a snapped single-axis remainder with centroid correction; leftover per-cube orientation error sits on 1–3-pixel cubes and is invisible at icon size (verified with offline renders). Conservation Violation got its revamped bloody-jaw texture.
- **Sharks now outswim players.** Chase acceleration roughly doubled and every species gained a chase-speed floor (~6.4 m/s, Mako ~8.4 m/s) vs the player's ~5.6 m/s sprint-swim; previously chase speed settled at ~3 m/s and any sprint-swimming player could escape any shark. Wander/cruise speeds unchanged.
- Shortfin Mako spawn sizes now span 3–5 m (scale 0.75–1.25 on the ~4 m base; was 0.9–1.05).
- Orca breach: run-up boosted and launch impulse raised 0.7 → 1.05 b/t (~3 → ~6.5 block apex) so it actually clears the water.
- Nautilus spawning below y=0: flooded caves in dripstone/lush/deep-dark biomes get a dedicated high-weight spawn entry, and the 80%-night-only gate is waived below y=0 (it's always dark down there). Tip: the `nautilus` per-species cap in `bensfintasticsharks-common.toml` still limits how many can be alive per 64-block radius.
- Bottlenose Dolphin renamed to **Common Bottlenose Dolphin** (entity name, spawn egg, and the Dolphin Tale advancement description).

### Fixed

- Bite animation no longer trails the damage by ~1s on Blacktip Reef Shark (impact delay now lands the hit on the 1.125s clip's chomp frame), Giant Moray Eel, and American Lobster (both dealt damage the same tick their 0.5s clip was *queued*; the hit is now scheduled onto the clip's impact frame).
- Master fishermen no longer roll empty level-5 trade slots. Vanilla draws exactly two listings from the master pool and doesn't redraw when one declines, so the old dozen chance-gated shark listings usually crowded out the vanilla trades and then produced nothing. All shark byproducts now live in one weighted "catch" listing that always yields exactly one offer   ~2/3 of master fishermen carry one shark byproduct alongside their intact vanilla trades.
- A crouch-swimming player could never be grabbed (vanilla refuses to mount sneaking riders; the shark thrashed empty water while the victim swam off). Grabs now force the mount.

### Known / punted

- Common Bottlenose Dolphin plays its swim animation while idle   the model has no idle clip yet; nothing to wire until Ben ships one.
- Remaining shark-head block items (Ben is modeling them) and the "Shark Jaws" decoration *block* form will come in a later drop.

## 0.16 Feedback round (2026-06-11)

### Fixed

- Tiger, Oceanic Whitetip, Sandtiger, and Blacktip Reef sharks no longer stop ~1 block short of their target and stare. The braking zone started 1 block outside the bite range, so slower species coasted to a stop in a dead band where the bite could never trigger; braking now begins only inside bite range.
- Those four species now switch to their fast-swim animation while locked onto prey (same synced-state pattern as the Shortfin Mako). The Oceanic Whitetip and Blacktip Reef gained new `fast_swim` clips (time-scaled from their swim loops).
- Shortfin Mako now plays its death animation (the trigger call and controller registration were missing).
- Jellyfish take knockback again. Their drift logic overwrote velocity every tick, swallowing punches and shoves; it now blends toward the drift so impulses play out.
- "Aw, you made me ink!" no longer pops on a random encounter. A new `octopus_inked` criterion fires only for the player who actually hurts an octopus.
- The Conservation Violation advancement icon item no longer shows the stale "Illegal Poaching" name on `/give`, and it got Ben's retextured bloody shark-jaws icon.

### Changed

- Nautilus now swims shell-first (visual 180° flip; navigation untouched).
- Octopuses lean into their swim direction like vanilla squids instead of drifting bolt upright.
- Shark Codex rebranded to "Capitán Ben's Codex" with the tooltip/GUI subtext "A Compendium of Marine Wildlife" (item name, tooltip, book first page, and the two codex advancement descriptions).
- "You're gonna need a bigger boat…" renamed to "King of the Seas".
- "Justice for Steve" is now "Crikey! Respect the wildlife!"   triggered by getting stung by a Common Stingray instead of killing one (same icon).
- "Stop! Hammer Time!" advancement icon replaced with Ben's 3D Great Hammerhead block-item model.
- "Find a Sunken Trove" advancement icon is now a plain chest.
- Root "Ben's Fintastic Sharks!" advancement icon switched from the Shark Trident to the BFS logo.

### Removed

- "Crankey!" (get stung by a stingray) advancement   folded into the reworked Crikey!/Justice for Steve.
- "Unethical" (kill a Harbor Seal) advancement and its icon item.

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
