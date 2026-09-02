# Ben's Fintastic Sharks 0.24 Plan

> **Plan ID:** PLAN-MASTER
> **Plan status:** VALIDATED
> **Project state:** EXISTING
> **Planning subject:** Ben's Fintastic Sharks Forge 1.20.1 release 0.24 with retained 0.23 behavior and supplied content
> **Plan profile:** software_product

## Project Identity

Requested artifact: authoritative_plan

Repository root: /mnt/hermes/projects/BFSMOD

Starting branch: 1.20.1

Starting commit: 06a28f6b3fcc62ee54cead9e62370c86aea0bfb9

Authoritative remote:
origin
https://github.com/bdubsub/BensFintasticSharkMod.git

Remote ref: origin/1.20.1

Remote commit: 06a28f6b3fcc62ee54cead9e62370c86aea0bfb9

### Plan identity

- Plan mode: `EXISTING_PLAN_UPDATE`.
- Target release: `0.24`.
- Baseline release: `0.23-emergency-fix` at merged repository commit `eb13b74`.
- Supported game: Minecraft `1.20.1`.
- Supported loader: Forge `47.2.0`.
- Toolchain: Java `17`, GeckoLib `4.4.7`, SmartBrainLib `1.14.2`, and Parchment `2023.09.03`.
- Authoritative master: `docs/general/plan.md`.
- Required manifest after phase blueprint creation: `docs/general/plan.index.json`.
- Required phase directory: `docs/general/phases/`.
- Required deterministic handoff after integration audit: `docs/general/plan.handoff.json`.
- Required integration branch: `main`. Planning-time repository evidence has legacy `origin/HEAD` at `origin/1.20.1`; `P000-TASK-001` must establish and verify `origin/main` at the same approved baseline history before the Phase 000 branch is created. Every phase pull request then targets `main`.
- Required tag sequence: `bfs-0.24-phase-000` through `bfs-0.24-phase-004`, followed by final release tag `bfs-0.24` for `BFS-PHASE-005`. Every tag is signed, annotated, and created only on the verified default-branch merge commit.
- Verified active phase: `BFS-PHASE-000`.
- Verified active phase path: `docs/general/phases/plan-phase-000.md`.
- First unfinished mandatory task: `P000-TASK-001`, establish the reconciled 0.23 and 0.24 baseline, reproduce or positively disprove every beta defect, and record the evidence matrix before implementation begins.
- Current endpoint: complete only `BFS-PHASE-000` and its integration gates before starting `BFS-PHASE-001`. The unchanged goal may then advance through each contiguous registered phase.

The global phase sequence is frozen as `BFS-PHASE-000` through `BFS-PHASE-005`. Phase files must use the exact paths in this plan. Each phase blueprint owns only its assigned scope and may not edit this master or another phase file. After all six phase blueprints exist, the plan set requires one integration audit, a deterministic index with file digests, and a deterministic handoff. Missing, extra, root level, unregistered, or renumbered phase files are invalid.

`docs/plan/goal.md` did not exist when this plan was revised. This plan does not create it. If the owner later invokes Goal Creator directly, the goal must preserve this project identity and record the verified active phase, exact phase path, first unfinished mandatory task, plan digest, plan set digest, endpoint, and required evidence gates. Once created, that goal is immutable.

The complete `BFS-REQ-014` relationship is frozen before execution: both existing direct children of the retired node, `marine_biologist` and `apex_of_apex`, must be reparented to `sharks_galore`. The Phase 003 blueprint, manifest digest, and deterministic handoff must carry both edges before `BFS-PHASE-003` execution. This statement records scope consistency only and does not claim that `BFS-PHASE-000` has started or completed.

## Planning Subject and Source Roles

Use the following locked source-role table. Lower numbered sources take precedence when evidence conflicts, while repository evidence establishes implementation reality without reducing owner-requested scope.

| Source ID | Role | Subject | Source | Use |
| --- | --- | --- | --- | --- |
| SRC-001 | owner_request | 0.24 plan update, retained 0.23 scope, beta defect closure, major defect audit, and consistency | current owner request and clarification | highest authority for mandatory scope and completion |
| SRC-002 | requirements | 0.23 Atlantic fish, Oceanic Whitetip, items, advancement, spawn, and fishing content | Content/BFS 0.23 Content.zip | binding retained content contract |
| SRC-003 | requirements | 0.24 advancement icons, advancement retirement, and algae request | Content/BFS 0.24 Content.zip | binding 0.24 content contract |
| SRC-004 | requirements | permanent algae textures and authored animation frames | Content/Algae Textures/ | binding permanent 0.24 algae asset contract |
| SRC-005 | review_feedback | Cod, Salmon, Oceanic Whitetip, Tiger Shark, advancement duplication, and punctuation beta failures | current owner pasted beta reports | mandatory defect ledger and acceptance evidence |
| SRC-006 | audit_evidence | truncated El Capitán's Legacy advancement description | /home/envy/.codex/attachments/519e48bf-889e-470c-aebb-65ac7e33d821/codex-clipboard-d78811c2-d6e9-4d75-8071-305eb208aa00.png | reproduction fixture and visual acceptance baseline |
| SRC-007 | existing_plan | 0.23 movement, Atlantic wildlife, advancement, emergency fix, verification, and future scope | docs/general/plan.md before authorized revision | preserve stable accepted history and unfinished evidence gates |
| SRC-008 | repository_evidence | current Forge 1.20.1 implementation, generated data, tests, manifests, and git state | /mnt/hermes/projects/BFSMOD at 06a28f6 | evidence based current state and affected boundaries |

README and technical documentation are derivative descriptions and must be corrected when they disagree with verified behavior.

## Purpose and Intended Outcome

Release 0.24 must retain every accepted 0.23 behavior and asset, integrate every supplied 0.24 asset, add permanent algae world generation, and close every reported beta defect. A successful build is not enough. Runtime movement, combat, animation, advancement presentation, natural generation, dedicated server safety, multiplayer cleanup, save compatibility, and packaged resources all require evidence.

“Everything fixed” means that no known mandatory defect within this plan remains open at release time. It requires a systematic defect audit and regression evidence. It does not claim that arbitrary future or unknown defects are impossible.

## Evidence-Based Current State

| Claim | Evidence class | Current state | Proof |
| --- | --- | --- | --- |
| Repository identity | VERIFIED | Existing Forge 1.20.1 project on branch `1.20.1` at `06a28f6b3fcc62ee54cead9e62370c86aea0bfb9` | Git revision and remote inspection executed for SRC-008 |
| Release baseline | OBSERVED | The repository retains 0.23 behavior and an emergency-fix history, while 0.24 work remains unexecuted | Existing plan, source, generated resources, and commit history in SRC-007 and SRC-008 |
| Supplied assets | VERIFIED | Both content archives and 22 algae PNG files are present and inspected | SHA-256 archive digests and image dimension inspection recorded from SRC-002 through SRC-004 |
| Beta failures | OBSERVED | The owner reports animation, predator, advancement, icon, punctuation, and algae gaps | SRC-005 reports and SRC-006 screenshot |
| Verification system | OBSERVED | One focused unit test exists, no GameTest namespace exists, and the supported Forge tasks remain the release path | Source and Gradle task inspection in SRC-008 |
| Default branch normalization | PROPOSED | Phase 000 establishes `main` from the verified legacy `origin/1.20.1` baseline before phase implementation | BFS-PHASE-000 and P000-TASK-001 |

## Product Contract and Profile Coverage

| Area | Status | Source | Plan section | Rationale |
| --- | --- | --- | --- | --- |
| inputs and outputs | covered | SRC-001 | Stable requirement register | plan defines supplied assets, runtime behavior, commands, advancements, blocks, generated data, and release artifacts |
| component architecture | covered | SRC-008 | Global constraints and non goals | plan defines common, server, client, data, state authority, and ownership boundaries |
| state and persistence | covered | SRC-007 | Stable requirement register | entity state, replacement state, advancement compatibility, and save preservation are defined |
| failure taxonomy | covered | SRC-005 | Beta defect ledger | reported and systematic failure classes have mandatory blocking behavior |
| versioning | covered | SRC-001 | Plan identity | 0.23 baseline, 0.24 target, stable ids, metadata, and compatibility are pinned |
| security | covered | SRC-008 | Global constraints and non goals | logical side, untrusted data, artifact, dependency, and release boundaries are defined |
| test system | covered | SRC-005 | Cross cutting acceptance and verification | unit, GameTest, runtime, multiplayer, fixed seed, visual, and artifact evidence are required |
| release lifecycle | covered | SRC-001 | Plan wide Definition of Done | ordered integration, tags, packaging, documentation, checksums, SBOM, and release closure are defined |
| generalization | covered | SRC-008 | Global constraints and non goals | supported Forge environment and unrelated loader, namespace, spawn, biome, and product exclusions are explicit |
| determinism | covered | SRC-003 | Cross cutting acceptance and verification | animation sampling, fixed seed generation, exact mapping, copy audit, and normalized artifacts are deterministic |

## Mandatory Scope

The mandatory release boundary is the complete retained and new contract declared by `BFS-REQ-001` through `BFS-REQ-023`, the supplied source artifacts `SRC-001` through `SRC-008`, and the sequential execution blueprints `BFS-PHASE-000` through `BFS-PHASE-005`. All beta-ledger defects and every required evidence gate are release blocking.

### Supplied content inventory and binding decisions

#### Version 0.23 archive

The 0.23 archive passed path traversal, link, and integrity checks. Its complete content is binding:

- Atlantic Cod geometry, texture, and `idle`, `swim`, `swim_fast`, and `flop` animations.
- Atlantic Salmon geometry, texture, and `idle`, `swim`, `swim_fast`, `flop`, and `spin` animations.
- Oceanic Whitetip `idle`, `swim_new`, `swim_fast_new`, `bite_new`, `death`, `beached`, and `thrash` animations.
- Atlantic Cod and Atlantic Salmon spawn egg sprites.
- Raw and Cooked Atlantic Cod sprites.
- Raw and Cooked Atlantic Salmon sprites.
- The 0.23 content note covering the `Spin` name easter egg, fishing loot, vanilla aquatic spawn control, fish animation review, and four fish advancements.

The repository copy of the Cod geometry intentionally corrects the supplied generic geometry identifier to `geometry.atlantic_cod`. All other verified 0.23 supplied asset bytes already match their repository copies. Authored bones, geometry, pixels, and clip intent must remain intact.

#### Version 0.24 archive

All seven supplied icons are 16 by 16 pixels and must replace the visual assets backing these existing advancement displays without resampling:

| Supplied icon | Advancement ID | Required displayed title |
| --- | --- | --- |
| Awkward Advancement Icon 16x | `harbor_seal_encounter` | `Awkward...` |
| Its A Shiny Advancement Icon 16x | `albino_encounter` | `It's a shiny!` |
| Sharks Galore Advancement Icon 16x | `sharks_galore` | `Sharks Galore!` |
| Sleeping With The Fishes Advancement Icon 16x | `sleeping_with_the_fishes` | `Sleeping with the fishes.` |
| Specimen8 Advancement Icon 16x | `specimen_8_encounter` | `I'll be back` |
| Mommy Shark Advancement 16x | `deep_blue_encounter` | `Mommy Shark.` |
| Thunder Bringer Advancement 16x | `zippy_encounter` | `THUNDER BRINGER!` |

The 0.24 note also requires retirement of `shark_whisperer` and permanent algae content for natural world generation.

#### Algae drop

The later `Content/Algae Textures/` drop contains 22 inspected PNG files. Source misspellings such as `algea` are evidence filenames, not public identifiers.

- `algea_block.png` is the visual source for permanent `bensfintasticsharks:algae_block`.
- `biggreenalgea_strip.png` is a 16 by 160 strip containing ten ordered 16 by 16 frames for permanent `bensfintasticsharks:large_green_algae`.
- `bigredalgea_strip.png` is a 16 by 144 strip containing nine ordered 16 by 16 frames for permanent `bensfintasticsharks:large_red_algae`.
- `greenbigalgea_1.png` through `greenbigalgea_10.png` and `bigredalgea_1.png` through `bigredalgea_9.png` are source frame evidence. Repeated frames encode authored visual holds and must remain in their supplied order.

The two strips are animated transparent aquatic plant textures. The numbered files are their source frames, not nineteen public blocks. The public display names are `Algae Block`, `Large Green Algae`, and `Large Red Algae`. These routine names resolve the archive's explicit `T&#66;D` without introducing a new lore decision.

## Optional / Future Scope

`FUT-001`, BFS AO The Trench Minecraft 26.2 product, is excluded from this plan and is nonblocking for the 0.24 endpoint. Its source is `SRC-007`; no part of this release may implement, package, or advertise it.

## Non-Goals

- `NG-001`: do not upgrade Minecraft, Forge, Java, mappings, Gradle, GeckoLib, or SmartBrainLib.
- `NG-002`: do not make the Fabric template a release target.
- `NG-003`: do not add unauthored replacement animation sets for pending species.
- `NG-004`: do not implement the separate ModJam product in release 0.24.

## Owner Decisions

### DEC-001 — Algae release status

**Status:** RESOLVED
**Selected choice:** permanent 0.24 content
**Rationale:** The owner's clarification makes every supplied algae asset mandatory release content rather than an experiment.
**Affected requirements:** BFS-REQ-020, BFS-REQ-021
**Supersedes:** none

### DEC-002 — Duplicate shark discovery advancements

**Status:** RESOLVED
**Selected choice:** retire shark_whisperer and retain sharks_galore as the single all shark encounter advancement
**Rationale:** One canonical discovery node removes duplicate progression while retaining both existing child branches.
**Affected requirements:** BFS-REQ-012, BFS-REQ-014
**Supersedes:** none

### DEC-003 — Advancement punctuation normalization

**Status:** RESOLVED
**Selected choice:** preserve exact supplied title punctuation, normalize other titles without accidental terminal punctuation, and require exactly one grammatical terminal mark on descriptions
**Rationale:** This preserves authored title intent while making all remaining advancement copy deterministic and testable.
**Affected requirements:** BFS-REQ-015, BFS-REQ-016
**Supersedes:** none

## External Prerequisites

No external prerequisite exists in the locked intake. All mandatory work can begin from the verified repository and supplied local evidence without credentials, billing changes, destructive approval, or third-party publication.

| Prerequisite ID | Name | Affected requirements | Availability | Authorization | Recovery |
| --- | --- | --- | --- | --- | --- |

## Architecture and Ownership Boundaries

### Global constraints and non goals

- Preserve Minecraft, Forge, Java, mappings, Gradle, GeckoLib, SmartBrainLib, and access configuration versions.
- Forge remains the only supported playable loader. The incomplete Fabric template is not a release target and must not weaken Forge verification.
- Keep common initialization safe on a dedicated server. Client classes and rendering state must remain client only.
- Preserve existing saves and stable public identifiers except the explicitly retired `shark_whisperer` advancement.
- Keep game state and combat authoritative on the logical server. Client animation is presentation driven by synchronized state.
- Do not redraw supplied art, rename authored bones, or reorder authored animation frames.
- Do not consume the floating item used by Tiger Shark curiosity.
- Do not convert existing entities or nonnatural spawns when applying vanilla fish replacement.
- Do not add unauthored replacement animation sets for Tiger Shark, Sand Tiger Shark, Blacktip Reef Shark, or Harbor Seal.
- Do not upgrade the project, add another loader, implement the separate ModJam product, or expand 0.24 into unrelated feature work.
- Protect existing uncommitted user work, including the line ending change in `build.gradle` and the untracked `Content/` directory.

Common code owns registrations, entity state, server-authoritative behavior, configuration, generated data contracts, and persistent identifiers. Dedicated server paths must never load client classes. Client code owns renderers, models, animation presentation, screens, and visual evidence. Data providers own generated advancement, recipe, loot, tag, model, block-state, and world-generation resources. Phase owners may modify contributors inside these boundaries, but every requirement and phase retains one canonical owner.

## Requirements

### Stable requirement register

#### Retained 0.23 contract

### BFS-REQ-001 — Shared shark locomotion

**Behavior:** Shared shark locomotion remains stable across cruising, pursuit, arrival, turning, and vertical path correction.
**Owner:** Entity behavior maintainer
**Contributors:** Test maintainer and client verification maintainer
**Dependencies:** none
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

Restore the stable 0.21 movement baseline. Sharks must cruise continuously, follow valid vertical path corrections, brake for turns and arrival without trapping themselves, and avoid sustained orbiting or figure eight pursuit.

**Acceptance criteria**

- Every supported shark cruises and follows reachable horizontal and vertical water paths without a sustained stationary, orbiting, or figure eight failure.
- Turning and arrival braking complete without trapping navigation or preventing the next valid movement goal.

**Required evidence**

- Deterministic movement tests record path completion, turn recovery, arrival recovery, and failure cleanup.
- In-game server and client captures show ordinary cruising and pursuit over the required runtime matrix.

### BFS-REQ-002 — Bite approach and contact

**Behavior:** Bite approach and contact remain functional for Sand Tiger and Blacktip Reef Sharks against supported prey sizes.
**Owner:** Combat behavior maintainer
**Contributors:** Entity behavior maintainer and test maintainer
**Dependencies:** BFS-REQ-001
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

Sand Tiger and Blacktip Reef Sharks must separate navigation, braking, and physical bite distances so supported prey sizes can be reached and delayed bites can land on stationary and moving targets.

**Acceptance criteria**

- Both shark species reach valid stationary and moving prey, enter physical bite range, and land one intended delayed bite per attack window.
- Navigation and braking thresholds do not strand either shark outside its physical contact distance.

**Required evidence**

- Deterministic pursuit tests cover stationary prey, moving prey, supported size boundaries, cooldown, and target invalidation.
- Runtime captures correlate approach distance, bite presentation, server damage timing, and post-attack recovery.

### BFS-REQ-003 — Tiger Shark item curiosity

**Behavior:** Tiger Shark item curiosity terminates safely without consuming the item or starving ordinary navigation and combat.
**Owner:** Entity behavior maintainer
**Contributors:** Combat behavior maintainer and test maintainer
**Dependencies:** BFS-REQ-001
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

A Tiger Shark must select one stable reachable underwater intercept for an edible floating item, perform a cosmetic bite, leave the item in the world, stop on completion, invalidation, timeout, or path failure, and apply a per item retry cooldown before ordinary navigation resumes. Existing post feeding self defense remains unless independently proven defective.

**Acceptance criteria**

- A valid curiosity attempt uses one stable reachable intercept, performs the cosmetic bite, and leaves the item entity and stack unchanged.
- Completion, invalidation, timeout, and path failure each clear curiosity state, apply the retry cooldown, and restore ordinary navigation and combat eligibility.

**Required evidence**

- Focused tests record each terminal path, unchanged item state, retry suppression, and later retry eligibility.
- Runtime evidence shows curiosity yielding to valid combat and no permanent stationary Tiger Shark state.

### BFS-REQ-004 — Grab and release behavior

**Behavior:** Oceanic Whitetip grab and Blacktip Reef latch state always release and clean up across every terminal condition.
**Owner:** Combat behavior maintainer
**Contributors:** Networking maintainer and client verification maintainer
**Dependencies:** BFS-REQ-001, BFS-REQ-002
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

Oceanic Whitetip uses its supplied thrash clip with the server authoritative large shark grab contract. Blacktip Reef uses a smaller latch whose initial bite is its only latch damage. Both behaviors need bounded duration, escape, death, unload, disconnect, dimension change, and passenger or camera cleanup.

**Acceptance criteria**

- Oceanic Whitetip thrash and Blacktip Reef latch obey their distinct bounded damage and duration contracts under server authority.
- Escape, death, unload, disconnect, dimension change, and invalid passenger state leave no stale rider, camera, attack, or synchronization state.

**Required evidence**

- GameTests exercise normal completion and every specified interruption with server-state assertions.
- Multiplayer reconnect and dimension-change captures prove passenger and camera restoration on all participating clients.

### BFS-REQ-005 — Showcase information and habitat

**Behavior:** Showcase information and Oceanic Whitetip habitat match authoritative runtime registrations, configuration, attributes, and spawn data.
**Owner:** Command maintainer
**Contributors:** Data maintainer and test maintainer
**Dependencies:** none
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

`/bfs info` must describe implemented runtime data. The twelve species named in the 0.23 plan display exactly `Diet: T&#66;D`. Oceanic Whitetip habitat and natural spawn data include only Deep Ocean and Deep Lukewarm Ocean, not Deep Cold Ocean. Atlantic Cod and Atlantic Salmon cards reflect their actual registries, habitats, behavior, diet, health, variants, categories, and caps.

**Acceptance criteria**

- Every required information card agrees with runtime registries, attributes, configuration modes, biome tags, behavior, variant counts, categories, and caps.
- The twelve retained nonshark cards display exactly `Diet: T&#66;D`, while Oceanic Whitetip excludes Deep Cold Ocean from habitat and natural spawn data.

**Required evidence**

- Deterministic comparisons bind each command field to its authoritative registry, tag, attribute, or configuration source.
- Command transcripts cover the twelve retained cards, Oceanic Whitetip, Atlantic Cod, and Atlantic Salmon in applicable modes.

### BFS-REQ-006 — Prismarine armor swim fit

**Behavior:** Prismarine armor fits throughout the complete vanilla breaststroke and remains stable after reconnect and equipment refresh.
**Owner:** Client rendering maintainer
**Contributors:** Model maintainer and multiplayer test maintainer
**Dependencies:** none
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

The Prismarine chest and sleeve geometry must clear the complete vanilla breaststroke from all specified camera angles without hiding player limbs, excessive normal pose separation, or reconnect instability.

**Acceptance criteria**

- Chest and sleeve geometry clears the complete breaststroke from front, rear, side, and diagonal views without hiding player limbs.
- Normal poses remain fitted, and equipment refresh, disconnect, and reconnect do not introduce stale transforms or missing geometry.

**Required evidence**

- Interactive screenshots or video cover all required camera angles during the complete swim cycle and normal pose.
- Multiplayer equipment-refresh and reconnect captures prove stable armor transforms and renderer state.

### BFS-REQ-007 — Oceanic Whitetip supplied clip set

**Behavior:** Oceanic Whitetip uses every supplied clip through a deterministic priority map that preserves server damage and cleanup timing.
**Owner:** Entity animation maintainer
**Contributors:** Combat behavior maintainer and client verification maintainer
**Dependencies:** BFS-REQ-004
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

Runtime state maps exactly to `idle`, `swim_new`, `swim_fast_new`, `bite_new`, `death`, `beached`, and `thrash`. Every referenced bone exists. Priority prevents death, beached, bite, fast swim, and thrash from falling through to normal swimming or interrupting delayed damage and release cleanup.

**Acceptance criteria**

- Every named runtime state selects its exact supplied clip, and every referenced animation bone exists in the model.
- Controller priority preserves exclusive death, beached, bite, fast-swim, and thrash presentation without breaking damage or release cleanup.

**Required evidence**

- Structural animation tests prove clip existence, bone validity, state mapping, and controller priority.
- Runtime transition captures correlate every clip with synchronized server state, delayed damage, and cleanup completion.

### BFS-REQ-008 — Atlantic fish entities and Spin

**Behavior:** Atlantic fish entities and the exact `Spin` name behavior remain functional across ordinary aquatic and beached states.
**Owner:** Entity behavior maintainer
**Contributors:** Entity animation maintainer and test maintainer
**Dependencies:** none
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

`atlantic_cod` and `atlantic_salmon` remain passive GeckoLib aquatic mobs modeled on the corresponding vanilla fish for schooling, panic, avoidance, navigation, beached behavior, attributes, hitboxes, sounds, tracking, and balance. Atlantic Salmon named exactly `Spin` loops its spin animation until the name is changed or removed, then immediately resumes its ordinary state.

**Acceptance criteria**

- Both Atlantic fish preserve their passive vanilla-based aquatic behavior, attributes, hitboxes, sounds, tracking, schooling, panic, avoidance, and beached behavior.
- An Atlantic Salmon named exactly `Spin` loops the supplied spin state and immediately returns to ordinary state after rename or name removal.

**Required evidence**

- Focused tests compare required entity contracts with their vanilla counterparts and exercise exact-name state transitions.
- Server and client captures show schooling, panic, avoidance, beached behavior, tracking, and Spin entry and exit.

### BFS-REQ-009 — Atlantic fish items, drops, recipes, and fishing

**Behavior:** Atlantic fish items, drops, cooking recipes, spawn eggs, and gameplay fishing remain complete and correctly packaged.
**Owner:** Item loot maintainer
**Contributors:** Data maintainer and test maintainer
**Dependencies:** BFS-REQ-008
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

Both BFS spawn eggs and all four supplied food sprites remain registered and packaged. Death drops the matching raw fish, fire or cooking damage selects cooked loot, gameplay fishing can catch each raw fish at balanced weights, and furnace and smoker recipes produce each cooked item.

**Acceptance criteria**

- Both spawn eggs and all four food items are registered, translated, modeled, textured, obtainable, and present in the supported Forge artifact.
- Ordinary death, fire or cooking death, gameplay fishing, furnace recipes, and smoker recipes produce the exact matching raw or cooked Atlantic item.

**Required evidence**

- Generated data assertions cover registrations, models, textures, loot conditions, fishing entries, recipes, and balanced weights.
- Runtime acquisition tests and final artifact inventory prove every required item path and packaged asset.

### BFS-REQ-010 — Vanilla aquatic controls and replacement

**Behavior:** Vanilla aquatic suppression and fish replacement retain their exact spawn-reason, type, configuration, and state-preservation scope.
**Owner:** Spawn system maintainer
**Contributors:** Configuration maintainer and test maintainer
**Dependencies:** BFS-REQ-008
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

The common natural vanilla aquatic suppression setting defaults off, affects only covered `minecraft` entities created by natural or chunk generation spawning, and requires restart. `[spawning] replace_vanilla_mobs` defaults true. It converts only natural and chunk generated Cod and Salmon, plus their vanilla spawn eggs, to their exact Atlantic counterparts. It preserves source position, rotation, custom name, safe spawn egg data, and school state. It never affects commands, buckets, spawners, structures, existing entities, or unrelated species. Replacement precedes suppression and disables the separate Atlantic biome modifier spawns. Disabling replacement preserves vanilla fish and enables the separate BFS spawn path.

**Acceptance criteria**

- Default settings, restart semantics, processing order, spawn-reason allowlist, exact species map, and separate BFS spawn-path behavior match this contract.
- Replacement preserves the listed safe source state and leaves commands, buckets, spawners, structures, existing entities, unsupported species, and other namespaces unchanged.

**Required evidence**

- Policy tests cover every allowed and denied spawn reason, species, namespace, configuration combination, processing order, and state field.
- Fixed-seed and spawn-egg runtime captures prove replacement-on and replacement-off behavior without duplicate spawn paths.

### BFS-REQ-011 — Emergency population invariant

**Behavior:** The Atlantic population invariant prevents runaway spawning while preserving bounded replacement and separate-spawn behavior.
**Owner:** Spawn system maintainer
**Contributors:** Performance test maintainer and configuration maintainer
**Dependencies:** BFS-REQ-010
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

Atlantic Cod and Atlantic Salmon remain `MobCategory.WATER_AMBIENT`. Exactly one accepted vanilla Cod or Salmon attempt can yield at most one matching Atlantic fish. Exact type allowlisting excludes Tropical Fish, Pufferfish, every other vanilla type, BFS types, and other namespaces. A category mismatch fails closed and logs once. Replacement off preserves vanilla fish and bounded BFS spawns. Existing excess fish are not deleted and may despawn naturally.

**Acceptance criteria**

- One accepted vanilla Cod or Salmon attempt yields at most one matching Atlantic fish in the same `WATER_AMBIENT` population category.
- Unsupported types and namespaces never convert, category mismatch fails closed with one actionable log, and replacement-off populations remain bounded.

**Required evidence**

- Exact mapping and population-policy tests cover allowed types, denied types, category mismatch, one-for-one behavior, and configuration modes.
- A sustained documented ocean sample reports bounded vanilla and Atlantic populations without reproducing the 0.23 flood.

### BFS-REQ-012 — Retained advancements

**Behavior:** Shark Spotter and the four Atlantic fish advancements remain functional through their intended gameplay criteria and supplied item roles.
**Owner:** Advancement maintainer
**Contributors:** Data maintainer and client verification maintainer
**Dependencies:** none
**Lifecycle stage:** continuous
**Production verification:** none
**Release impact:** stable release

Shark Spotter requires an actively used vanilla Spyglass whose unobstructed view ray reaches a BFS shark. The four fish advancements remain: `Oh My Cod`, `Why aren't you red?`, `Gadus morhua`, and `Salmo salar`, with their supplied item roles. Catch criteria use fishing gameplay and encounter criteria use the BFS encounter trigger.

**Acceptance criteria**

- Shark Spotter triggers only from active vanilla Spyglass use with an unobstructed view ray that reaches a BFS shark.
- All four Atlantic fish advancements retain their intended display, item role, fishing criterion, encounter criterion, reachability, and completion behavior.

**Required evidence**

- Advancement provider and generated-graph tests assert exact criteria, trigger types, parent links, item roles, and negative cases.
- Fresh-profile runtime captures prove valid completion paths and rejection of obstructed or wrong-trigger attempts.

#### New 0.24 contract and beta corrections

### BFS-REQ-013 — Remastered advancement icons

**Behavior:** All seven remastered advancement icons are integrated through the exact supplied display mapping without resampling or stale substitutes.
**Owner:** Advancement asset maintainer
**Contributors:** Data maintainer and client verification maintainer
**Dependencies:** none
**Lifecycle stage:** change
**Production verification:** none
**Release impact:** stable release

Integrate all seven supplied 16 by 16 icons according to the binding mapping above. Generated data, item models, textures, language, and final JAR contents must all reference the intended art with no stale 64 by 64 replacement remaining on those displays.

**Acceptance criteria**

- Each of the seven advancement displays resolves to its exact supplied 16 by 16 icon and required title through generated data and packaged resources.
- No targeted display uses a stale 64 by 64 replacement, resampled art, missing model, missing texture, or unintended icon item.

**Required evidence**

- A seven-row digest and dimension manifest links each source icon to its destination texture, model, display, and packaged bytes.
- Interactive advancement screenshots show every mapped icon and title in the supported client.

### BFS-REQ-014 — Advancement graph cleanup

**Behavior:** The duplicate Shark Whisperer advancement is retired while the surviving graph remains connected, reachable, and compatible.
**Owner:** Advancement maintainer
**Contributors:** Data maintainer and compatibility test maintainer
**Dependencies:** BFS-REQ-012, DEC-002
**Lifecycle stage:** change
**Production verification:** none
**Release impact:** stable release

Remove generated advancement `bensfintasticsharks:shark_whisperer`, its dedicated language entries, and unreachable presentation resources. Keep `sharks_galore` as the single all shark encounter achievement. Reparent both direct children, `marine_biologist` and `apex_of_apex`, to `sharks_galore` while preserving each child's existing criteria, rewards, display settings, and descendants so the graph remains reachable and completable. Existing progress in the retired node is intentionally not migrated because the surviving node has equivalent criteria.

**Acceptance criteria**

- Generated and packaged resources contain no `shark_whisperer`, while `marine_biologist` and `apex_of_apex` directly parent to `sharks_galore` with unchanged child semantics.
- Fresh and existing profiles load without graph errors, preserve unrelated progress, and can complete both reparented branches and all descendants.

**Required evidence**

- Before-and-after graph manifests and negative resource scans prove retirement, both replacement edges, and preserved child criteria, rewards, display settings, and descendants.
- Fresh-profile and existing-profile runtime traces prove reachability, completion, preservation, and no required migration.

### BFS-REQ-015 — Advancement copy

**Behavior:** Advancement copy includes the complete captain description and every exact supplied 0.24 title.
**Owner:** Localization maintainer
**Contributors:** Advancement maintainer and client verification maintainer
**Dependencies:** BFS-REQ-013, DEC-003
**Lifecycle stage:** change
**Production verification:** none
**Release impact:** stable release

The `captains_heir` title remains `El Capitán's Legacy` and its complete description becomes exactly `Obtain Capitán Ben's Hat.`. Apply the seven supplied 0.24 titles exactly as listed in the asset table, including case and intentional punctuation.

**Acceptance criteria**

- `captains_heir` displays the exact title and complete description specified by this requirement without truncation or fallback text.
- All seven supplied titles match the binding table byte for byte, including case, apostrophes, periods, ellipsis, and exclamation marks.

**Required evidence**

- Provider and generated-language comparisons assert every exact key and expected value.
- Interactive screenshots include the complete captain tooltip and all seven supplied displayed titles.

### BFS-REQ-016 — Punctuation consistency

**Behavior:** Advancement punctuation and copy follow one deterministic convention while preserving exact supplied title punctuation.
**Owner:** Localization maintainer
**Contributors:** Advancement maintainer and test maintainer
**Dependencies:** BFS-REQ-015, DEC-003
**Lifecycle stage:** change
**Production verification:** none
**Release impact:** stable release

Audit every English advancement title and description, including generated output. Supplied titles preserve their exact authored punctuation. Other titles use display title capitalization and no accidental trailing punctuation. Descriptions are complete sentence case text with exactly one grammatical terminal mark. Remove trailing whitespace, doubled terminal marks, accidental smart and straight apostrophe mixtures within one phrase, and title or description disagreements between providers and generated JSON. Add a deterministic copy audit so regressions fail verification.

**Acceptance criteria**

- Every English advancement title and description satisfies the locked punctuation convention, with supplied titles exempt only where their exact authored punctuation requires it.
- Provider values and generated output are identical and contain no trailing whitespace, doubled terminal marks, mixed apostrophe styles within one phrase, or incomplete description.

**Required evidence**

- A deterministic localization audit reports the complete expected key set and zero convention violations.
- Generated-resource diff inspection and interactive display review confirm the audited text is the text loaded by the client.

### BFS-REQ-017 — Atlantic fish animation motion

**Behavior:** Atlantic Cod and Atlantic Salmon visibly animate in ordinary idle, swim, and fast-swim movement states while retaining working special states.
**Owner:** Entity animation maintainer
**Contributors:** Resource tooling maintainer and client verification maintainer
**Dependencies:** BFS-REQ-008
**Lifecycle stage:** change
**Production verification:** none
**Release impact:** stable release

Cod and Salmon must visibly move in idle, ordinary swim, and fast swim states rather than animating only in time keyed flop, death presentation, or Salmon spin states. Preserve working flop and `Spin` behavior. Resolve GeckoLib 4.4.7 single keyframe MoLang freezing with the existing baking tool or an equivalently deterministic resource transformation that retains authored curves and loop duration.

**Acceptance criteria**

- Both species produce visible authored motion in idle, ordinary swim, and fast swim while preserving their working flop, death presentation, and Salmon Spin behavior.
- The deterministic GeckoLib transformation preserves authored curves, bones, loop duration, source ordering, and sampled transforms within the required tolerance.

**Required evidence**

- Structural sampling records distinct transforms at required clip times and compares transformed output with authored MoLang evaluation.
- Interactive client captures show every required ordinary and special state with no missing-bone or controller warning.

### BFS-REQ-018 — Oceanic Whitetip animation completeness

**Behavior:** Oceanic Whitetip visibly animates in every supplied state with coherent transitions, damage timing, and grab cleanup.
**Owner:** Entity animation maintainer
**Contributors:** Combat behavior maintainer and multiplayer test maintainer
**Dependencies:** BFS-REQ-007
**Lifecycle stage:** change
**Production verification:** none
**Release impact:** stable release

Oceanic Whitetip must visibly animate in idle, normal swim, fast swim, bite, death, beached, and thrash states. Controller priority and transitions must make each state reachable and mutually coherent. Bite animation and server damage timing must agree, and thrash must not outlive grab cleanup.

**Acceptance criteria**

- Idle, normal swim, fast swim, bite, death, beached, and thrash each show visible authored motion and enter from their required runtime state.
- Bite presentation agrees with one server damage window, and thrash stops no later than authoritative grab cleanup under every terminal condition.

**Required evidence**

- Structural clip and transform tests cover every supplied state, referenced bone, priority edge, and transition.
- Client and multiplayer captures correlate animation state with damage, grab, release, death, beached, and reconnect behavior.

### BFS-REQ-019 — Tiger Shark mobility and attacks

**Behavior:** Tiger Shark moves, targets, attacks, and recovers without curiosity or scheduler state causing permanent inactivity.
**Owner:** Combat behavior maintainer
**Contributors:** Entity behavior maintainer and test maintainer
**Dependencies:** BFS-REQ-001, BFS-REQ-003
**Lifecycle stage:** change
**Production verification:** none
**Release impact:** stable release

Tiger Sharks must move when idle, acquire supported prey, pursue through horizontal and vertical water paths, enter physical bite range, apply intended damage once per attack window, and resume navigation after target loss or death. Item curiosity must not starve combat or permanently lock movement.

**Acceptance criteria**

- Idle Tiger Sharks cruise, acquire supported prey, complete horizontal and vertical pursuit, reach physical bite range, and apply one intended damage event per attack window.
- Target loss, target death, failed path, completed attack, and curiosity interruption each restore valid scheduling and navigation without a permanent stationary state.

**Required evidence**

- Deterministic behavior and GameTests cover target selection, path completion, reach, cooldown, damage count, recovery, and curiosity arbitration.
- Runtime captures show idle motion, prey pursuit, bite contact, damage, target loss, and later navigation on server and client.

### BFS-REQ-020 — Permanent algae blocks

**Behavior:** Three permanent algae content units are registered, animated, obtainable, water-safe, and packaged through existing aquatic content boundaries.
**Owner:** Content registration maintainer
**Contributors:** Client rendering maintainer and data maintainer
**Dependencies:** DEC-001
**Lifecycle stage:** change
**Production verification:** none
**Release impact:** stable release

Register `algae_block`, `large_green_algae`, and `large_red_algae` with matching item access, translations, models, block states, loot, render layers, tags, and creative placement. They are replaceable, noncolliding aquatic vegetation that preserves water behavior, survives only in valid submerged placement, restores water when removed where vanilla aquatic plants do, and follows vanilla seagrass conventions for placement and collection unless a supplied asset requires a visual exception.

The green and red strip textures use all supplied frames in order with their repeated holds. Animation metadata must produce a calm loop without interpolation artifacts, atlas warnings, or frame skipping. The block texture remains a distinct algae volume or patch presentation and must not be stretched into either tall plant.

**Acceptance criteria**

- All three public identifiers have matching block and item access, translations, models, block states, loot, tags, render behavior, creative placement, and packaged assets.
- Each block obeys submerged placement, survival, noncollision, replacement, water restoration, collection, and visual rules without atlas or animation warnings.
- Green and red animation metadata uses every supplied frame in exact order, including repeated authored holds, while the block texture retains distinct presentation.

**Required evidence**

- Registry, generated-data, survival, loot, water-restoration, and artifact tests cover all three identifiers and every required resource.
- Interactive captures show inventory presentation, placement, animation cadence, invalid-support breakage, collection, and restored water.

### BFS-REQ-021 — Algae world generation

**Behavior:** Algae generate naturally at bounded deterministic density through data-driven features and biome tags.
**Owner:** World generation maintainer
**Contributors:** Data maintainer and performance test maintainer
**Dependencies:** BFS-REQ-020
**Lifecycle stage:** change
**Production verification:** none
**Release impact:** stable release

Add data driven configured and placed features for all three algae forms. Placement requires water, valid ocean floor support, collision free space, and biome tags rather than hard coded biome identity checks. Defaults expose both color variants across supported vanilla ocean families without assigning unsupported lore ecology. Distribution must be lower than common vanilla seagrass, bounded per chunk, deterministic for a fixed seed, and configurable through data packs. Algae may enrich an ocean floor but must not replace structures, generate on land, block navigation broadly, or dominate sampled seabed.

**Acceptance criteria**

- All three forms use data-driven configured and placed features, tagged eligible biomes, valid water and floor predicates, collision-free placement, and data-pack-configurable parameters.
- Fixed-seed output is repeatable, bounded per chunk, lower than common seagrass in the same sample, absent on land and invalid support, and safe for structures and navigation.

**Required evidence**

- Generated feature and biome-tag assertions prove codec validity, registration, configuration, and every placement predicate.
- A documented fixed-seed multi-chunk comparison reports per-feature counts, combined density, seagrass density, invalid placements, and repeated-run equality.

### BFS-REQ-022 — Systematic beta and major defect closure

**Behavior:** Systematic beta and major defect closure blocks release while any known mandatory defect remains open.
**Owner:** Verification maintainer
**Contributors:** All phase maintainers and security reviewer
**Dependencies:** BFS-REQ-021
**Lifecycle stage:** post_change
**Production verification:** none
**Release impact:** stable release

Close all defects in the defect ledger and audit the complete 0.23 plus 0.24 surface. Any reproducible crash, data load failure, registry failure, dedicated server class loading fault, multiplayer state leak, runaway population, advancement dead end, nonmoving required entity, nonattacking required predator, missing required asset, or world generation flood is release blocking. Minor unrelated cosmetic observations may be documented for later work only when they do not violate a requirement or acceptance criterion.

**Acceptance criteria**

- Every `DEF-024-*` entry closes through its named minimum evidence and all linked requirement criteria, with no assertion-only or source-inspection closure.
- A clean integrated audit finds no known mandatory in-scope crash, load fault, state leak, population failure, progression dead end, required-motion failure, predator failure, missing asset, or generation flood.

**Required evidence**

- The complete defect ledger contains reproduction evidence, fix linkage, closing evidence, rerun results, and a final closed disposition for every entry.
- Clean-checkout deterministic, GameTest, server, client, multiplayer, population, advancement, generation, security, and artifact results form one release audit packet.

### BFS-REQ-023 — Release and documentation integrity

**Behavior:** Version, documentation, artifact, and release evidence consistently and truthfully report release 0.24.
**Owner:** Release maintainer
**Contributors:** Documentation maintainer and verification maintainer
**Dependencies:** BFS-REQ-022
**Lifecycle stage:** post_change
**Production verification:** none
**Release impact:** stable release

Set all project, artifact, and embedded metadata versions to `0.24`. Update `README.md`, the detected technical documentation, `CHANGELOG.md`, documentation indexes, configuration references, advancement references, species behavior, algae, world generation, troubleshooting, upgrade notes, and verification records to describe only behavior that passed. Inspect the final JAR and publish only the approved Forge artifact with checksums, SBOM, and supported attestations.

**Acceptance criteria**

- Every version-bearing manifest, artifact, embedded metadata record, tracked document, and release note consistently identifies 0.24 and describes only verified behavior.
- The approved Forge artifact contains every required resource and no excluded loader artifact, secret, cache, debug output, protected user file, or unintended generated file.
- SHA-256, SHA-512, SPDX SBOM, source-commit manifest, and each supported attestation bind to the exact approved artifact and merged commit.

**Required evidence**

- Version and documentation audits compare every maintained surface with the verified feature and compatibility matrix.
- Final JAR inventory, metadata inspection, checksums, SBOM validation, attestation results, merge proof, and signed-tag verification identify one exact release artifact.

### Beta defect ledger

| Defect ID | Reported failure | Closing requirements | Minimum closing evidence |
| --- | --- | --- | --- |
| `DEF-024-001` | El Capitán's Legacy shows an incomplete description | `BFS-REQ-015` | Exact provider text, generated JSON or language text, and tooltip screenshot |
| `DEF-024-002` | Atlantic fish motion is absent outside the already working special states | `BFS-REQ-017` | Structural transform sampling and in game idle, swim, fast swim, flop, and Spin evidence |
| `DEF-024-003` | Oceanic Whitetip movement states and bite do not visibly animate | `BFS-REQ-007`, `BFS-REQ-018` | State transition matrix, transform sampling, client evidence, and damage timing evidence |
| `DEF-024-004` | Tiger Shark remains stationary and does not attack | `BFS-REQ-001`, `BFS-REQ-003`, `BFS-REQ-019` | Deterministic target and damage test plus in game pursuit evidence |
| `DEF-024-005` | Shark Whisperer duplicates Sharks Galore and directly parents Marine Biologist and Apex of Apex | `BFS-REQ-014` | Generated graph proves `shark_whisperer` is absent, both `marine_biologist` and `apex_of_apex` directly parent to `sharks_galore`, their criteria remain unchanged, and fresh profile paths complete |
| `DEF-024-006` | Advancement punctuation and wording are inconsistent | `BFS-REQ-015`, `BFS-REQ-016` | Deterministic copy audit and visual advancement review |
| `DEF-024-007` | Remastered icons are supplied but absent | `BFS-REQ-013` | Byte and dimension check plus in game advancement display review |
| `DEF-024-008` | Supplied algae are not permanent registered world generation content | `BFS-REQ-020`, `BFS-REQ-021` | Registry, data generation, fixed seed world generation, survival, loot, and client rendering evidence |

No ledger item may close from source inspection alone. Closure requires its minimum evidence and all linked acceptance criteria.

## Verification Strategy

### Cross cutting acceptance and verification

### Deterministic tests

- Preserve and expand the exact fish replacement policy test. Assert Cod maps only to Atlantic Cod, Salmon only to Atlantic Salmon, unrelated types map to none, and both replacement pairs share `WATER_AMBIENT`.
- Add focused tests for shark movement decisions, Tiger item intercept termination, target acquisition, attack cooldown, latch and thrash cleanup, config filtering, advancement topology including both required `sharks_galore` child links, localization punctuation, animation clip references, animation transform variance, algae registrations, feature codecs, biome tags, loot, and supplied asset dimensions.
- Establish the smallest Forge GameTest harness needed for world dependent contracts. Cover vertical path completion, predator pursuit and damage, fish passivity, player release cleanup, aquatic plant placement and survival, and configured feature placement. If a renderer cannot be tested by GameTest, keep deterministic state coverage and require client evidence.
- For every required animation clip, sample start, quarter, half, three quarter, and end times. At least one authored moving bone must produce two distinct transform vectors within a `0.0001` tolerance. Compare baked output against the authored MoLang evaluation at the same samples.
- For algae, use fixed seed feature tests and a documented multi chunk sample. Each required feature must be observable, no placement may occur on land or invalid support, and combined algae coverage must remain below common seagrass coverage in the same eligible sample.

### Runtime matrix

- Dedicated server startup must reach the ready state with no registry, data pack, tag, loot, recipe, advancement, animation reference, or client class loading failure.
- Client review must exercise every fish and Oceanic Whitetip clip, Tiger idle and combat movement, Tiger item curiosity, Blacktip latch, Oceanic thrash, Prismarine armor swim fit, all remastered advancement displays, El Capitán's Legacy tooltip, and all algae blocks in hand and in a generated ocean.
- Multiplayer review must include attack state synchronization, grab release, death, disconnect, reconnect, dimension change, equipment refresh, and no permanent passenger or camera state.
- A sustained ocean population sample must remain bounded by the water ambient ceiling and never reproduce the 0.23 population flood.
- A fresh profile must load the advancement tree without `shark_whisperer`, show both `marine_biologist` and `apex_of_apex` as direct children of `sharks_galore`, preserve their intended criteria, and permit all three advancements and their descendants to complete.

### Required command order

1. Run the repository formatter or formatting check if one exists. Record its absence rather than inventing a gate.
2. Run configured static analysis and lint tasks if present.
3. Run `./gradlew :forge:test`.
4. Run `./gradlew :forge:Data` for every data or resource change and inspect the complete generated diff.
5. Run the new Forge GameTests and fixtures.
6. Run `./gradlew :forge:build`.
7. Run `./gradlew :forge:Server` and complete the dedicated server smoke matrix.
8. Run `./gradlew :forge:Client` and complete the visual and interaction matrix.
9. Complete multiplayer and reconnect checks.
10. Inspect the final JAR, embedded metadata, checksums, SBOM, complete Git diff, and release evidence.

The repository wide `./gradlew build` currently enters an unsupported Fabric stub that lacks SmartBrainLib. This known stub failure does not replace or invalidate the supported Forge build gate, but 0.24 must not worsen it or describe Fabric as supported. Headless client startup without an OpenAL device is acceptable only for smoke loading. It does not replace interactive visual acceptance.

## Phased Roadmap

| Phase | Exact phase file | Owner | Primary requirements | Dependency | Entry gate | Exit gate | Next phase |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `BFS-PHASE-000` | [Phase 000](phases/plan-phase-000.md) | Integration and verification maintainer | No canonical requirement; mandatory prerequisite evidence supporting `BFS-REQ-022` and `BFS-REQ-023` | None | Master, archives, algae drop, screenshot, source, tests, normalized `main` integration branch, and dirty worktree protections are available | Complete evidence matrix, reproducible defect baselines, harness design, asset ledger, and no unresolved intake decision | `BFS-PHASE-001` |
| `BFS-PHASE-001` | [Phase 001](phases/plan-phase-001.md) | Gameplay and compatibility maintainer | `BFS-REQ-001` through `BFS-REQ-012` | Merged and tagged `BFS-PHASE-000` | Baseline and defect reproduction evidence approved | Every retained 0.23 requirement passes deterministic and required runtime regression checks, with repairs integrated where evidence found drift | `BFS-PHASE-002` |
| `BFS-PHASE-002` | [Phase 002](phases/plan-phase-002.md) | Entity animation and behavior maintainer | `BFS-REQ-017` through `BFS-REQ-019` | Merged and tagged `BFS-PHASE-001` | Stable retained movement and combat baseline | Fish and Oceanic clips show authored motion, Tiger moves and attacks, linked beta defects close, and multiplayer cleanup passes | `BFS-PHASE-003` |
| `BFS-PHASE-003` | [Phase 003](phases/plan-phase-003.md) | Advancement and localization maintainer | `BFS-REQ-013` through `BFS-REQ-016` | Merged and tagged `BFS-PHASE-002` | Entity behavior no longer changes advancement acceptance evidence | Seven icons and exact titles are integrated, El Capitán copy is complete, copy audit passes, duplicate node is removed, both `marine_biologist` and `apex_of_apex` directly parent to `sharks_galore`, and the graph is completable | `BFS-PHASE-004` |
| `BFS-PHASE-004` | [Phase 004](phases/plan-phase-004.md) | World generation and content maintainer | `BFS-REQ-020`, `BFS-REQ-021` | Merged and tagged `BFS-PHASE-003` | Registries, generated data, and release copy baseline are stable | Three algae content units are obtainable, animated, water safe, naturally generated at bounded density, and verified on server and client | `BFS-PHASE-005` |
| `BFS-PHASE-005` | [Phase 005](phases/plan-phase-005.md) | Release and verification maintainer | `BFS-REQ-022`, `BFS-REQ-023` | Merged and tagged `BFS-PHASE-004` | All feature phases merged, tagged, and reflected on the default branch | Full audit passes, no known mandatory in scope defect remains open, documentation and metadata report 0.24, final artifact evidence passes, approved pull request is merged, and signed release tag is verified | Plan complete |

#### Phase 000 summary

Objective: convert all supplied notes, assets, reported beta failures, retained 0.23 behavior, current implementation, and test limitations into one executable evidence baseline without changing product behavior.

Required work:

- Create the complete asset and requirement traceability ledger.
- Record clean reproductions or positive controls for `DEF-024-001` through `DEF-024-008`.
- Define deterministic fixtures, GameTest structures, fixed seeds, runtime captures, log review, and artifact checks used by later phases.
- Prove that protected user changes remain outside the phase diff.
- Convert each acceptance statement into owned tasks and evidence gates in the exact phase blueprints.

Exit: every requirement has a primary phase, each defect has a reproducible closing test, every supplied asset has a destination, and no material owner choice remains unresolved. Integrate through its own pull request and signed phase tag before `BFS-PHASE-001` starts.

#### Phase 001 summary

Objective: establish 0.23 as a proven regression baseline, not merely compiled historical code.

Required work:

- Verify and repair shared shark steering, bite approaches, Tiger curiosity, Oceanic thrash, Blacktip latch, information output, habitat data, Prismarine armor, Atlantic fish content, spawn controls, population invariants, Shark Spotter, and four fish advancements.
- Complete the manual checks left open by the previous plan and automate world dependent behavior where feasible.
- Preserve exact replacement scope and operator recovery behavior from `0.23-emergency-fix`.

Exit: `BFS-REQ-001` through `BFS-REQ-012` pass their deterministic and runtime gates. No 0.23 regression is deferred to a later feature phase. Integrate and tag before `BFS-PHASE-002`.

#### Phase 002 summary

Objective: fix the beta's nonmoving animation states and restore Tiger Shark movement and attack behavior.

Required work:

- Bake or otherwise make GeckoLib 4.4.7 evaluate the supplied single keyframe MoLang channels over time without changing authored motion.
- Validate all Cod, Salmon, and Oceanic Whitetip clips structurally and in game.
- Repair Tiger Shark behavior scheduling, navigation, target acquisition, physical reach, damage timing, and recovery while retaining item curiosity.
- Close `DEF-024-002`, `DEF-024-003`, and `DEF-024-004` with deterministic and client evidence.

Exit: every required animation state visibly moves, damage timing matches presentation, Tiger mobility and combat pass, and no controller or missing bone warning remains. Integrate and tag before `BFS-PHASE-003`.

#### Phase 003 summary

Objective: deliver the complete 0.24 advancement presentation and remove duplicate progression.

Required work:

- Install all seven icon assets through existing dedicated hidden icon item patterns.
- Apply the exact supplied titles and full El Capitán's Legacy description.
- Remove Shark Whisperer, make Sharks Galore the single all shark node, and reparent both Marine Biologist and Apex of Apex directly to Sharks Galore without changing either child's intended criteria.
- Audit all advancement punctuation and add a deterministic localization and graph check.
- Regenerate resources and inspect a fresh player's complete tree in game.

Exit: `DEF-024-001`, `DEF-024-005`, `DEF-024-006`, and `DEF-024-007` are closed; `shark_whisperer` is absent; `marine_biologist` and `apex_of_apex` both directly parent to `sharks_galore` with their intended criteria preserved; and every intended advancement loads, displays, remains reachable, and is completable. Integrate and tag before `BFS-PHASE-004`.

#### Phase 004 summary

Objective: turn the complete supplied algae drop into permanent, polished, bounded natural ocean content.

Required work:

- Register the three public content units and all item, model, texture, animation metadata, tag, loot, and translation dependencies.
- Implement aquatic placement, survival, water restoration, collection, and rendering through existing BFS and vanilla aquatic plant conventions.
- Add data driven configured and placed features with tagged biomes and conservative distribution.
- Verify fixed seed generation, invalid placement rejection, animation cadence, navigation safety, dedicated server loading, and final JAR contents.

Exit: `DEF-024-008` is closed and `BFS-REQ-020` plus `BFS-REQ-021` pass all deterministic and runtime evidence. Integrate and tag before `BFS-PHASE-005`.

#### Phase 005 summary

Objective: prove the integrated release as a whole and publish only when every mandatory contract is satisfied.

Required work:

- Rerun the complete deterministic, GameTest, server, client, multiplayer, population, advancement, world generation, packaging, security, documentation, and compatibility matrix from a clean checkout of the phase branch.
- Search for major defects across initialization, registries, networking, persistence, entities, combat, animation, configuration, data generation, world generation, advancements, assets, and packaging.
- Fix in scope regressions in this phase. Stop with `PLAN_REVISION_REQUIRED` rather than silently broadening the product contract when a discovered fix requires a material owner decision.
- Set version `0.24`, finish tracked documentation and release evidence, inspect the complete diff, and integrate through the required pull request workflow.

Exit: every stable requirement is evidenced, every beta defect is closed, no known mandatory in scope defect remains open, required checks and review pass, the merge is present on the default branch, and the signed annotated 0.24 phase or release tag points to that merge.

## Requirement traceability

| Requirement range | Primary phase | Mandatory recheck |
| --- | --- | --- |
| `BFS-REQ-001` through `BFS-REQ-012` | `BFS-PHASE-001` | `BFS-PHASE-005` |
| `BFS-REQ-017` through `BFS-REQ-019` | `BFS-PHASE-002` | `BFS-PHASE-005` |
| `BFS-REQ-013` through `BFS-REQ-016` | `BFS-PHASE-003` | `BFS-PHASE-005` |
| `BFS-REQ-020` and `BFS-REQ-021` | `BFS-PHASE-004` | `BFS-PHASE-005` |
| `BFS-REQ-022` and `BFS-REQ-023` | `BFS-PHASE-005` | `BFS-PHASE-005` |

`BFS-REQ-014` traces specifically to `BFS-PHASE-003` implementation and `BFS-PHASE-005` revalidation. Its required graph edge evidence is `sharks_galore` to `marine_biologist` and `sharks_galore` to `apex_of_apex`, with `shark_whisperer` absent and both child criteria preserved.

`BFS-PHASE-000` produces mandatory baseline, harness, traceability, and release-surface inventory evidence consumed by `BFS-REQ-022` and `BFS-REQ-023`; it is not a second primary owner. Canonical ownership and closure of both requirements occur exactly once in `BFS-PHASE-005`.

## Compatibility, Migration, Rollout, and Recovery

Release 0.24 preserves Forge 1.20.1, Forge 47.2.0, Java 17, existing mappings, existing library versions, saved entity data, configuration keys, registry identifiers, and public resource identifiers except the explicitly retired `shark_whisperer` advancement. No migration copies progress from that retired node because `sharks_galore` retains equivalent all-shark criteria; unrelated advancement progress and both reparented child branches must remain intact.

Rollout is phase-sequential. Each phase starts from the verified `origin/main` result of the prior merged phase, passes its deterministic and runtime gates, merges through GitHub, and receives its signed annotated tag before the next phase branch begins. A failed gate keeps the current phase open. Recovery reverts only the scoped phase change through the repository workflow, restores the last verified tagged default-branch state, preserves saves and protected user files, and reruns every invalidated downstream check. Published release claims remain blocked until the final clean-checkout packet passes.

## Documentation, Operations, and Release Gates

Tracked documentation is canonical for installed behavior and operations. Every phase updates only the pages affected by behavior that passed. Changes to commands, configuration, compatibility, data schemas, advancements, entity behavior, world generation, troubleshooting, build steps, or release operations must update `README.md`, `DOCUMENTATION.md`, `docs/README.md`, `CHANGELOG.md`, and focused topic documents as applicable. Planned or failed behavior must never be described as shipped.

The release gate requires version consistency across Gradle properties, manifests, embedded metadata, artifact names, changelog entries, documentation, and release notes. The final supported Forge JAR must pass content inspection, SHA-256 and SHA-512 generation, SPDX SBOM validation, source-commit binding, supported attestation checks, secret and generated-output inspection, and complete diff review. Release publication is outside partial phase completion and occurs only under the final owner-selected endpoint.

## Risks and Failure Boundaries

- GeckoLib 4.4.7 can freeze single keyframe MoLang vector channels. Use sampled transform equivalence plus visual review, not clip existence, as the control.
- The repository has only one focused unit test and no existing GameTest namespace. `BFS-PHASE-000` must establish the harness before world dependent completion claims.
- Advancement removal can orphan descendants. Explicit graph assertions for both `marine_biologist` and `apex_of_apex`, plus fresh profile completion paths for both branches, control this risk.
- Algae can flood chunks, replace invalid blocks, or create navigation barriers. Fixed seed placement tests, density comparison, survival rules, and sustained server review control this risk.
- Fish replacement can repeat the 0.23 population flood. Exact type mapping, population category parity, one for one tests, and a sustained ocean sample remain permanent release gates.
- Client animation work can introduce dedicated server class loading faults. Dedicated server startup is required after every shared boundary change.
- Headless graphics and absent audio cannot prove visual acceptance. Interactive client evidence remains mandatory.
- The supplied source filenames contain misspellings and spaces. Normalize destination identifiers while preserving the original files as immutable evidence.

## Plan wide Definition of Done

The 0.24 plan is complete only when all of the following are true:

- All six contiguous phase files exist at their exact registered paths and match `docs/general/plan.index.json` digests.
- Every phase exit gate passed in order, its pull request was merged through GitHub, the resulting default branch commit was verified, and its signed annotated phase tag was pushed before the next phase began.
- Every retained 0.23 requirement from `BFS-REQ-001` through `BFS-REQ-012` and every 0.24 requirement from `BFS-REQ-013` through `BFS-REQ-023` has implementation evidence, deterministic evidence where representable, required runtime evidence, and final artifact evidence.
- Every `DEF-024-*` item is closed by its minimum evidence, not by source inspection or assertion.
- The packaged advancement graph contains no `shark_whisperer`; both `marine_biologist` and `apex_of_apex` directly parent to `sharks_galore`; both preserve their intended criteria and descendants; and both branches remain reachable and completable on a fresh profile.
- All 0.23 content remains functional, all supplied 0.24 icons and algae assets are packaged, and the reported Cod, Salmon, Oceanic Whitetip, Tiger Shark, advancement, and copy failures are no longer reproducible.
- The systematic audit finds no known mandatory in scope defect left open.
- Forge tests, data generation, GameTests, Forge build, dedicated server, client, multiplayer, final JAR inspection, documentation checks, security checks, checksums, SBOM, and all repository required checks pass.
- The merged default branch and packaged artifact report `0.24`, tracked documentation describes verified behavior, and the owner selected release endpoint is satisfied.

The exact completion endpoint is: version 0.24 is fully evidenced, merged to the authoritative default branch through the required sequential phase workflow, signed and tagged, and its approved Forge artifact and release evidence satisfy the plan wide Definition of Done

Failure of any mandatory check keeps the current phase open. Future unknown defects do not invalidate honest completion evidence, but any known defect that violates this plan blocks release until fixed or explicitly removed through an owner authorized plan revision.

## Goal Creator Handoff

Mandatory boundary: BFS-REQ-001 through BFS-REQ-023 and BFS-PHASE-000 through BFS-PHASE-005 are mandatory for the Forge 0.24 endpoint.

Optional/future disposition: excluded

Locked owner decisions: DEC-001, DEC-002, DEC-003

Active phase: BFS-PHASE-000

Next executable action: execute P000-TASK-001 from docs/general/phases/plan-phase-000.md to reconcile repository identity, normalize the main integration branch, protect existing user work, and record the baseline evidence matrix.

Known failing checks: none; product implementation verification has not started.

Known external blockers: none

Completion endpoint: version 0.24 is fully evidenced, merged to the authoritative default branch through the required sequential phase workflow, signed and tagged, and its approved Forge artifact and release evidence satisfy the plan wide Definition of Done

Required evidence gates: complete every canonical requirement criterion, close every beta-ledger defect, pass deterministic tests, Forge GameTests, dedicated-server, interactive-client, multiplayer, fixed-seed generation, documentation, security, artifact, checksum, SBOM, repository-check, merge, default-branch, and signed-tag verification in sequential phase order.
