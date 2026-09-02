# Phase 004 Execution Plan

> **Plan ID:** PLAN-PHASE-004
> **Phase ID:** BFS-PHASE-004
> **Owner:** World generation maintainer
> **Classification:** MANDATORY
> **Master plan:** [plan.md](../plan.md)
> **Phase sequence:** 004 of 005

## Purpose and Ownership

This phase turns the complete supplied algae drop into permanent Forge 1.20.1 content. It canonically owns `BFS-REQ-020`, `BFS-REQ-021`, and closure of `DEF-024-008`: three correctly named, obtainable aquatic blocks with faithful assets and behavior, plus conservative data driven generation across supported vanilla ocean families.

The master owns product scope, stable identifiers, source precedence, the frozen phase sequence, and release completion. This file owns only dependency ordered execution and evidence for `BFS-PHASE-004`. It must not change algae into experimental content, propagate source misspellings into public identifiers, invent ecology, revise earlier features, set the final release version, or weaken the fixed seed, runtime, compatibility, and artifact gates. A material conflict or owner decision stops execution with `PLAN_REVISION_REQUIRED`.

## Evidence-Based Entry State

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| VERIFIED | Product contract | The master assigns `BFS-REQ-020`, `BFS-REQ-021`, and `DEF-024-008` to this phase. | `docs/general/plan.md`, stable requirement register, defect ledger, and frozen phase catalog | Invalidated by an owner authorized master revision or changed plan index registration. |
| VERIFIED | Compatibility | The supported target is Minecraft `1.20.1`, Forge `47.2.0`, Java `17`, Parchment `2023.09.03`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`; Fabric remains unsupported. | `docs/general/plan.md`, `gradle.properties`, `forge/build.gradle` | Invalidated by an authorized compatibility revision. |
| VERIFIED | Algae source inventory | `Content/Algae Textures/` contains one 16 by 16 `algea_block.png`, one 16 by 160 green strip with ten ordered frames, one 16 by 144 red strip with nine ordered frames, and the nineteen numbered frame files. | Master asset inventory, Phase 000 asset ledger, `file Content/Algae\ Textures/*.png` | Any source byte, name, dimension, frame order, or Phase 000 ledger change invalidates the asset evidence. |
| VERIFIED | Authored holds | Green frames include repeated runs at 2 and 3, 4 through 6, 7 and 8, and 9 and 10. Red frames repeat in runs 1 through 3, 4 through 6, and 7 through 9. These are authored timing evidence, not redundant files to deduplicate. | SHA 256 comparison of numbered source frames and Phase 000 strip segmentation evidence | Any source hash or segmentation change requires the sequence and hold audit to be rerun. |
| VERIFIED | Public identity | Source spellings such as `algea` are evidence names only. Required public IDs are `bensfintasticsharks:algae_block`, `bensfintasticsharks:large_green_algae`, and `bensfintasticsharks:large_red_algae`; display names are `Algae Block`, `Large Green Algae`, and `Large Red Algae`. | `docs/general/plan.md`, algae drop binding decisions | Invalidated only by an owner authorized product contract revision. |
| VERIFIED | Registration baseline | `BensFintasticSharks.init()` uses the platform `registerAll` path for common registries, derives lower case IDs from public field names, and currently registers entity types, items, effects, and the creative tab but no block holder. | `common/src/main/java/tfar/bensfintasticsharks/BensFintasticSharks.java`; `forge/src/main/java/tfar/bensfintasticsharks/platform/ForgePlatformHelper.java` | Reinspect after common bootstrap, reflection registration, or registry lifecycle changes. |
| VERIFIED | Creative item behavior | `ModCreativeTabs` enumerates all BFS items except `HiddenItem`, so ordinary algae block items enter the existing tab without a separate hard coded list. | `common/src/main/java/tfar/bensfintasticsharks/init/ModCreativeTabs.java`; `ModItems.java` | Reinspect after item class or creative display filtering changes. |
| VERIFIED | Data generation | `ModDatagen` already wires language, blockstate, item model, loot, block tag, item tag, biome tag, and built in data pack providers. Blockstate and block loot providers currently contain no generated entries. | `forge/src/main/java/tfar/bensfintasticsharks/datagen/ModDatagen.java`; `ModBlockstateProvider.java`; `data/loot/ModBlockLoot.java`; `data/tags/ModBlockTagsProvider.java` | Provider registration, generated root, or provider implementation changes invalidate this map. |
| VERIFIED | World generation baseline | The existing built in provider registers Forge biome modifiers for entity spawns. `BiomeModifiers` owns their keys. The only custom feature registration is the unrelated Sunken Trove boundary in `ModFeatures`. There are no algae configured features, placed features, biome tags, or add feature modifiers. | `forge/src/main/java/tfar/bensfintasticsharks/datagen/data/ModDataPackProvider.java`; `BiomeModifiers.java`; `worldgen/ModFeatures.java`; generated resources | Reinspect after any world generation registry, provider, tag, or generated resource change. |
| VERIFIED | Client boundary | `ModClientForge.init()` and `setup()` are loaded only on the client distribution and are the existing Forge client registration boundary. No block render layer registration exists at baseline. | `forge/src/main/java/tfar/bensfintasticsharks/BensFintasticSharksForge.java`; `client/ModClientForge.java` | Reinspect after client bootstrap, distribution guards, or rendering setup changes. |
| VERIFIED | Test contract | Phase 000 defines seed `240024`, Overworld chunks `0,0` through `15,15`, identical counting rules for algae and vanilla seagrass, and a Forge GameTest harness for aquatic placement, survival, and configured feature placement. | `docs/general/phases/plan-phase-000.md` and the registered `BFS-PHASE-000` algae, GameTest, and runtime-protocol tasks | Any approved Phase 000 evidence revision or fixture change invalidates dependent measurements. |
| VERIFIED | Protected work | The master protects the line ending only `build.gradle` change and the untracked `Content/` directory. Supplied files remain immutable evidence and must not be committed. | `docs/general/plan.md`, global constraints and non goals | Recheck before each diff, commit, and integration action. |

## Scope Boundaries

### Included Scope

- `BFS-REQ-020`: register exactly the three public algae blocks and matching ordinary block items, translations, blockstates, models, textures, animation metadata, loot, render layers, block and item tags, and creative access.
- `BFS-REQ-020`: preserve every supplied pixel, strip frame, order, and repeated authored hold while creating calm noninterpolated loops without atlas warnings or skipped frames.
- `BFS-REQ-020`: implement replaceable and noncolliding submerged plant behavior, valid floor support, water preservation, neighbor survival, removal restoration, placement, collection, and seagrass compatible interaction semantics.
- `BFS-REQ-021`: provide separate data driven configured and placed features for all three forms, attach them at the appropriate vegetation generation step through neutral biome tags, and expose both colors across supported vanilla ocean families.
- `BFS-REQ-021`: bound candidate work and observed density per chunk, keep combined algae below common vanilla seagrass in the shared eligible sample, and prove fixed seed determinism, land rejection, valid support, collision freedom, and data pack override behavior.
- `DEF-024-008`: close the defect with registry, data generation, GameTest, fixed seed, survival, loot, interactive rendering, navigation, dedicated server, and final Forge JAR evidence.
- Existing save compatibility, old world loading, new chunk behavior, data pack rollback, documentation, phase integration, default branch verification, and signed annotated phase tagging required for the owned work.

### Explicit Exclusions

- `BFS-REQ-001` through `BFS-REQ-019` belong to earlier merged and tagged phases. Algae work must not alter entity behavior, animations, advancement graph, icons, or localization copy except the three algae display names.
- `BFS-REQ-022` and `BFS-REQ-023` final audit, version `0.24`, release packaging, checksums, SBOM, attestations, and publication belong to `BFS-PHASE-005`. This phase still inspects its candidate JAR and updates feature documentation.
- `FUT-001` and `docs/modjam/` are unrelated. Platform upgrades, Fabric completion, new lore ecology, algae variants beyond the three public IDs, new structures, unrelated vegetation, and world migration tooling are not authorized.
- The nineteen numbered PNGs are evidence frames, not nineteen blocks, items, or separately packaged public textures. The two strips are animation sheets, not spatially tall ten block or nine block textures.
- Existing chunks are not retroactively decorated. No phase task scans, rewrites, or removes blocks from an existing world.
- The supplied `Content/` directory must not be renamed, edited, staged, or committed.

## Phase Contract

### BFS-PHASE-004 — Permanent Algae and World Generation

**Objective:** Deliver three correctly identified and obtainable aquatic algae forms whose supplied art animates faithfully, whose placement and removal preserve water, and whose data driven natural generation is deterministic, valid, navigable, conservative, and packaged in the supported Forge artifact.
**Owner:** World generation maintainer
**Dependencies:** BFS-PHASE-003, DEC-001
**Canonical requirements:** BFS-REQ-020, BFS-REQ-021
**Documentation and release impact:** Update `README.md`, `DOCUMENTATION.md`, `CHANGELOG.md`, and `docs/README.md` with only verified algae behavior, data pack extension points, generation scope, compatibility, and troubleshooting. Do not set version `0.24` or publish.
**Next transition:** `BFS-PHASE-005`, only after this phase pull request is merged, the result is present on the authoritative default branch, and the signed annotated Phase 004 tag is pushed and verified.

**Entry criteria**

- `BFS-PHASE-003` is merged through GitHub, `origin/main` contains its merge commit, and its signed annotated phase tag verifies against that commit.
- The Phase 004 branch is created from that updated `origin/main`. No future phase branch is stacked on it.
- Phase 000 provides exact source hashes, strip segmentation equality, fixed seed `240024`, sample chunks `0,0` through `15,15`, counting rules, GameTest fixtures, evidence format, and protected path fingerprints.
- The three public IDs and display names have no registry collision, and no stale `algea` public identifier exists.
- Java 17 and the checked in Gradle wrapper resolve the pinned Forge toolchain without dependency changes.
- No unresolved owner choice or contradiction affects block shape, acquisition, biome scope, distribution, or compatibility. A missing material decision blocks implementation.

**Implementation scope**

- `BFS-REQ-020` and `BFS-REQ-021` are the complete canonical implementation assignment for `BFS-PHASE-004`.
- `BFS-PHASE-004`: Add a common block registration holder and register blocks before their matching items through the existing `BensFintasticSharks.init()` and platform `registerAll` lifecycle.
- `BFS-PHASE-004`: Add focused aquatic plant implementations using vanilla seagrass behavior as the semantic baseline while preserving the distinct patch presentation of `algae_block` and the supplied green and red visual forms.
- `BFS-PHASE-004`: Extend the existing data generation providers rather than hand editing generated blockstates, item models, language, loot, tags, configured features, placed features, or biome modifiers.
- `BFS-PHASE-004`: Keep texture animation and render layer setup entirely client safe. Common block behavior, feature placement, registries, codecs, tags, and server data must not import client classes.
- `BFS-PHASE-004`: Use vanilla and Forge data pack feature types where they satisfy the contract. Do not couple algae to the Sunken Trove custom feature or introduce a custom feature codec without demonstrated necessity and codec tests.

**Execution order**

1. `P004-TASK-001` for `BFS-PHASE-004` validates the Phase 003 handoff, protected work, registry vacancy, Phase 000 algae ledger, fixtures, fixed seed, and evidence bindings.
2. `P004-TASK-002` for `BFS-PHASE-004` adds dependency ordered block and item registration with exact public IDs and creative access.
3. `P004-TASK-003` for `BFS-PHASE-004` implements submerged placement, support, survival, water state, removal, collection, replacement, shape, and collision behavior for all three forms.
4. `P004-TASK-004` for `BFS-PHASE-004` installs the supplied texture destinations, exact strip sequences, animation metadata, distinct models and blockstates, item models, cutout rendering, and visual asset audits.
5. `P004-TASK-005` for `BFS-PHASE-004` extends language, loot, block and item tags, and deterministic generated data for all three content units.
6. `P004-TASK-006` for `BFS-PHASE-004` adds separate configured and placed features, bounded placement modifiers, neutral biome tags, and Forge add feature biome modifiers at the vegetation decoration stage.
7. `P004-TASK-007` for `BFS-PHASE-004` adds deterministic registry, codec, tag, asset, loot, blockstate, model, and per chunk bound tests.
8. `P004-TASK-008` for `BFS-PHASE-004` adds and runs GameTests for valid placement, invalid placement, support loss, water restoration, collection, feature placement, cleanup, and aquatic navigation.
9. `P004-TASK-009` for `BFS-PHASE-004` runs the shared fixed seed sample twice and compares exact coordinate multisets, land and support validity, per chunk maxima, each feature's observability, and combined algae coverage against vanilla seagrass.
10. `P004-TASK-010` for `BFS-PHASE-004` completes dedicated server, interactive client, old save, new chunk, data pack override, navigation soak, and log review evidence.
11. `P004-TASK-011` for `BFS-PHASE-004` runs the master ordered command matrix, inspects generated drift, final JAR paths, compatibility, documentation, complete diff, and protected work.
12. `P004-TASK-012` for `BFS-PHASE-004` closes `DEF-024-008`, assembles the completion packet, synchronizes phase tracking, integrates through the required pull request, verifies the default branch merge and signed tag, and hands off to `BFS-PHASE-005`.

**Required evidence**

- Registry and creative inventory evidence for exactly three block IDs and three matching item IDs, with exact display names and zero `algea` namespace paths or translation keys.
- Pixel and animation audit proving exact source strip segmentation, all ten green and nine red frames in source order, authored repeated holds retained, noninterpolated calm cadence, and no resampling.
- Generated blockstate, block model, item model, language, loot, block tag, item tag, configured feature, placed feature, biome tag, and biome modifier evidence with no unexplained drift.
- GameTests proving source water, valid floor support, collision free placement, invalid dry and unsupported rejection, neighbor survival, removal water restoration, expected tool collection, and path completion through representative algae.
- Fixed seed output for two independently generated worlds at seed `240024` and chunks `0,0` through `15,15`, including exact coordinate and state manifests, counts by form and chunk, maximum candidates and placements, rejection reasons, and seagrass comparison.
- Dedicated server ready state without registry, codec, data pack, feature, tag, loot, model reference, or client class loading errors.
- Interactive client captures of every item in hand and inventory, every placed form in water, a complete green and red animation cycle, generated ocean distribution, transparent edges, water surfaces, particles, breaking, collection, and removal.
- Final tested Forge JAR inventory and hash proving every required class and resource is packaged at the correct public path and no numbered evidence frame or misspelled public destination leaked into the artifact.

**Exit criteria**

- `BFS-REQ-020` and `BFS-REQ-021` pass every deterministic, generated data, GameTest, server, client, compatibility, navigation, fixed seed, and artifact gate.
- `DEF-024-008` closes from its required evidence, not source inspection: all three forms register, survive and collect correctly, render, animate, generate naturally, and appear in the tested JAR.
- Every form is observable in the shared eligible sample; no placement is on land, outside source water, on invalid support, or in occupied space; repeated generation produces an identical coordinate and state manifest.
- Combined algae coverage remains strictly below common vanilla seagrass coverage under the same seed, chunks, eligibility rules, and counting method. Per chunk candidates and placements never exceed their configured finite caps.
- Representative aquatic navigation completes through and around generated algae without a persistent path barrier, and the sustained sample does not dominate the seabed or replace structures.
- Existing worlds load without migration or missing registry errors. Disabling the algae biome modifier stops generation in new chunks without deleting placed or previously generated algae.
- The generated resource diff and complete repository diff are intentional. Protected `build.gradle` and `Content/` state remains unchanged and excluded.
- The Phase 004 pull request is merged through GitHub with required checks resolved. One private independent review passes when supported, or verified capability unavailability is recorded. The default branch contains the merge, and the signed annotated Phase 004 tag points to it.
- No known mandatory phase owned defect remains.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| Master and registered phase set | Master plan and deterministic index | Phase 004 owns only `BFS-REQ-020`, `BFS-REQ-021`, and `DEF-024-008`; Phase 003 is the contiguous dependency | Read master, index, and handoff through EOF and verify digests | Stop on a material contract mismatch. Do not edit the master or goal. |
| Phase 003 integration | `BFS-PHASE-003` | Pull request merged, default branch verified, signed phase tag pushed, registries and generated copy stable | GitHub merge state, `origin/main` containment, signature and tag target | Do not create or stack the Phase 004 branch until all gates pass. |
| Algae source drop | Owner and `SRC-004` | All 22 files match the approved Phase 000 ledger and remain immutable | SHA 256, PNG dimensions, RGBA mode, strip segmentation, frame order, repeated frame groups | Stop asset work on any mismatch. Obtain corrected owner input or authorized revision. |
| Public naming contract | Master and `DEC-001` | Exact three IDs and display names, with source misspellings excluded from public surfaces | Registry, translation, resource, generated data, and JAR scans | Fail registration and packaging checks on any collision, alias, fourth form, or misspelling. |
| Evidence harness | `BFS-PHASE-000` | Fixed seed, bounded sample, GameTest framework, controls, count rules, capture schema, and artifact checks are executable | Run positive controls for vanilla seagrass and known valid or invalid aquatic placement | Block closure if a test cannot distinguish absent content from a broken fixture. |
| Existing registration lifecycle | Common and Forge bootstrap | Blocks register before BlockItems and remain available to datagen without client classes | Registry events, duplicate key checks, dedicated server startup | Fix ordering within the phase. Never force registry writes after freeze. |
| Existing data generation pipeline | `ModDatagen` and providers | Provider output under `common/src/generated/resources/` is authoritative and reproducible | Clean `./gradlew :forge:Data` and complete generated diff | Treat hand edited generated output or nondeterminism as blocking drift. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| Three algae blocks and items | Players, commands, creative tab, data packs | Exact stable public IDs, display names, obtainability, placement, collection, and water safe removal | Additive to old saves; IDs may not be renamed after release | Registry dump, creative capture, give and place workflow, GameTests, JAR |
| Faithful visual resource set | Client renderer and Phase 005 | Distinct block patch plus complete green and red animations with repeated holds and transparent rendering | Supplied pixels and sequence remain unchanged | Hash manifest, metadata audit, atlas log, cycle captures, JAR |
| Generated content data | Runtime registries and resource loading | Complete blockstates, models, language, loot, and tags generated from providers | Generated data must match provider output exactly | Data run, JSON reference audit, clean second regeneration |
| Data driven world generation | World generator and data packs | Three decodable configured and placed features attached by biome tag with finite default work and conservative density | Existing chunks unchanged; data packs may replace supported data entries | Codec tests, generated JSON, biome tag audit, fixed seed manifests |
| Compatibility and rollback contract | Operators and Phase 005 | Old saves load; disabling the add feature modifier stops future placement; existing blocks remain; downgrade requires a backup | No silent block deletion or retroactive chunk mutation | Old save smoke, disabled pack test, backup and rollback run sheet |
| Defect closure packet | `BFS-PHASE-005` | `DEF-024-008` has deterministic, runtime, and artifact proof bound to the merged revision | Any later registry, resource, placement, worldgen, or packaging change invalidates affected proof | Completion packet, merge commit, tag, artifact hash |

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| `P004-TASK-001` | `BFS-REQ-020`, `BFS-REQ-021` | Validate upstream merge and tag, source hashes, strip segmentation, public key vacancy, fixed seed fixtures, protected work, and evidence destinations. | Phase 003 merge; Phase 000 ledger and harness; `SRC-004` | Entry manifest and task traceability | Git, plan handoff, `Content/Algae Textures/`, registries, test fixtures | Every entry criterion passes on one named revision; protected hashes match. |
| `P004-TASK-002` | `BFS-REQ-020` | Register three blocks before three matching BlockItems using exact field derived IDs; expose ordinary items through the existing creative tab. | `P004-TASK-001`; common registry lifecycle | Six registry entries and creative access | `BensFintasticSharks.init()`, new common block holder, `ModItems`, `ModCreativeTabs`, platform `registerAll` | Unit registry assertions, duplicate key scan, `/give`, creative inventory, server start. |
| `P004-TASK-003` | `BFS-REQ-020` | Implement one block high aquatic vegetation semantics: replaceable, no collision, water source fluid state, sturdy submerged floor support, scheduled fluid maintenance, neighbor invalidation, water restoration, and vanilla seagrass style collection. Keep the block texture form visually distinct from both animated plant forms. | `P004-TASK-002`; vanilla seagrass contract | Three water safe block behaviors | Common block implementation and block properties | GameTests for valid and invalid states, support removal, fluid retention, collection tools, drops, and collision shape. |
| `P004-TASK-004` | `BFS-REQ-020` | Map source art to correctly spelled destinations; preserve strip bytes and frame ordering; author metadata that retains repeated holds without interpolation; create distinct block and inventory presentation; register transparent plant render layers client side. | `P004-TASK-003`; Phase 000 asset ledger | Client assets and render setup | `common/src/main/resources/assets/bensfintasticsharks/`; `ModBlockstateProvider`; `ModItemModelProvider`; `ModClientForge` | PNG and segmentation audit, JSON reference audit, atlas log, interactive cycle and item captures. |
| `P004-TASK-005` | `BFS-REQ-020` | Generate exact translations, blockstates, item models, collection loot, and applicable block and item tags. Ensure block and item tags agree where semantic parity is required. | `P004-TASK-002` through `P004-TASK-004` | Reproducible generated content data | `ModLangProvider`; `ModBlockstateProvider`; `ModItemModelProvider`; `ModBlockLoot`; `ModBlockTagsProvider`; `ModItemTagsProvider`; generated resources | Two data runs are byte stable; loot tests cover correct and incorrect tools; no missing reference. |
| `P004-TASK-006` | `BFS-REQ-021` | Register separate configured and placed features for all three forms, use finite candidate modifiers and placement validation, attach at vegetation decoration through a neutral BFS ocean biome tag and Forge add feature modifiers. Include both color variants across supported vanilla ocean families without hard coded biome checks. | `P004-TASK-003`, `P004-TASK-005`; data pack registry lookup | Three data driven generation chains | `ModDataPackProvider`; `ModTags.Biomes`; `ModBiomeTagsProvider`; `BiomeModifiers`; generated configured feature, placed feature, biome tag, and biome modifier resources | Codec round trip, registry reference and generation step audit, data pack load, bounded candidate assertion. |
| `P004-TASK-007` | `BFS-REQ-020`, `BFS-REQ-021` | Add deterministic audits for registry pairs, names, no misspelled public paths, source assets, metadata frames, models, tags, loot, feature codecs, biome attachment, generation step, and finite bounds. | `P004-TASK-002` through `P004-TASK-006`; Phase 000 JUnit layer | Automated algae audit suite | Forge test source set rooted at `forge/src/test/java` and generated resource interfaces | `./gradlew :forge:test`; known bad fixtures fail for the intended reason and are removed before commit. |
| `P004-TASK-008` | `BFS-REQ-020`, `BFS-REQ-021` | Exercise placement, survival, update, break, collect, water restoration, configured placement, invalid support, occupied space, dry land, cleanup, and fish or shark navigation through representative patches. | `P004-TASK-003`, `P004-TASK-006`; Phase 000 GameTest harness | World dependent behavioral proof | Established Forge GameTest namespace and structures | All tests pass twice with bounded ticks, exact postconditions, and no leaked fixture state. |
| `P004-TASK-009` | `BFS-REQ-021` | Generate two clean worlds with the shared seed and sample; record every algae and seagrass coordinate and state; verify determinism, each form, support, biome, land rejection, per chunk caps, total density, and structure nonreplacement. | `P004-TASK-006` through `P004-TASK-008`; seed `240024`, chunks `0,0` through `15,15` | Fixed seed manifests and density report | Forge server world generator, biome tags, placed features | Both manifests match exactly; each form count is positive; invalid count is zero; combined algae is below seagrass. |
| `P004-TASK-010` | `BFS-REQ-020`, `BFS-REQ-021`, `DEF-024-008` | Run dedicated server, interactive client, old save, new chunk, data pack disable and override, navigation soak, and log matrix. | `P004-TASK-007` through `P004-TASK-009` | Runtime, compatibility, and recovery evidence | Forge Server and Client runs, test worlds, external data pack boundary | Ready state, visual acceptance, stable old save, expected new chunk generation, disabled generation behavior, clean logs. |
| `P004-TASK-011` | `BFS-REQ-020`, `BFS-REQ-021` | Execute ordered checks, regenerate data, build Forge, inspect JAR and full diff, update documentation, and prove no protected or unrelated changes. | All implementation and test tasks | Verified phase candidate and documentation | Gradle tasks, generated resources, Forge JAR, `README.md`, `DOCUMENTATION.md`, `CHANGELOG.md`, `docs/README.md` | Every mandatory check passes and artifact inventory matches source references. |
| `P004-TASK-012` | `BFS-REQ-020`, `BFS-REQ-021`, `DEF-024-008` | Assemble evidence, close the defect, synchronize tracking, complete independent review when supported, merge the phase pull request, verify default branch, and push the signed annotated phase tag. | All previous tasks and exit gates | Phase 004 completion packet and Phase 005 entry authorization | Issue, milestone, Project, pull request, checks, review, merge, tag | GitHub reports merged; required checks and conversations pass; `origin/main` contains merge; tag target and signature verify. |

### Ordering, Parallelism, and Recovery

`P004-TASK-001` is a hard prerequisite. Registration and common behavior are sequential because items, models, loot, and features depend on stable block identities. Asset work may proceed beside late behavior refinement only after IDs and model roles are fixed. Generated content and world generation follow final block semantics. Deterministic tests and GameTests may proceed in parallel after their target interfaces stabilize. Fixed seed measurement, runtime evidence, final verification, and integration are strictly sequential.

If a registry or serialized identifier changes after evidence begins, invalidate all registry, generated data, world, and JAR proof and rebuild fresh test worlds. If generation writes an unsafe world, preserve the failing seed and coordinates as evidence, disable the phase data pack for further operator testing, and use disposable copies only. Roll back phase code and generated output together; never leave provider and generated resources at different revisions. Do not use an algae populated world to test a downgraded build without a backup.

## Architecture and Implementation Boundaries

### Registration and State

The common block holder owns the three block instances. `BensFintasticSharks.init()` registers the block holder before `ModItems`, because each BlockItem depends on a stable block object. The existing platform helper and Forge `RegisterEvent` remain the registration mechanism. Public static field names must derive exactly the three required lower case registry paths. No alias under an `algea` spelling is permitted.

All three forms are server safe block state. They occupy one block position, report no collision, permit aquatic pathing, expose a water source fluid state while present, and schedule water updates. Placement succeeds only into suitable water with valid ocean floor support and free plant space. Neighbor changes recheck survival. Invalidated plants follow vanilla aquatic vegetation cleanup and leave water rather than air. Collection behavior follows vanilla seagrass, including tool sensitive loot, unless the supplied visual form requires only a model exception. Block behavior does not depend on client classes or persistent block entities.

### Assets and Rendering

`algea_block.png` supplies the distinct `algae_block` patch or volume texture but never its misspelling. The green and red strips each represent one animated 16 by 16 texture. Their numbered files prove frame ordering and repeated holds; they are not independent runtime content. Animation metadata enumerates or times every authored frame without interpolation, deduplication, synthetic frames, reordering, or resampling. Cadence must be calm and repeatable, and a full loop must return without a visible discontinuity beyond the authored sequence.

Transparent plant models use a cutout compatible render layer registered only through the Forge client boundary. Models must not stretch `algae_block` into the green or red plant forms. Inventory presentation must be identifiable and free of missing texture fallback. Atlas warnings, frame size errors, metadata errors, z fighting that breaks recognition, or opaque background pixels are blocking.

### Generated Data and World Generation

Providers are authoritative for generated resources. `ModDatagen` already owns the provider graph and `common/src/generated/resources/` output. Blockstates map every reachable state, block models use the correct textures, item models represent their blocks, language uses exact names, loot encodes collection rules, and tags capture semantic interoperability without broad accidental membership.

Each block has its own configured feature and placed feature chain. Default configured data selects only its matching state. Placement modifiers establish a finite candidate budget per chunk, search eligible ocean floor positions, require source water and free plant space, and apply a biome filter. A Forge add feature biome modifier attaches each placed feature at vegetation decoration by neutral BFS biome tag. Implementation must use registry references and data pack entries, not Java biome identity conditionals. Both colors share supported vanilla ocean families by default; no temperature, rarity, or species lore distinction is inferred.

The configured candidate ceiling is the static upper bound. Runtime evidence records both candidate and successful placement maxima. Combined successful BFS algae coverage must be less than common vanilla seagrass under the exact same eligible position counting in the shared sample. A data pack can override registered configured features, placed features, biome tags, and modifiers without a code change; malformed or cyclic references must fail data pack loading visibly rather than fall back to hidden hard coded generation.

### Compatibility, Performance, and Security

New registry entries are additive. Existing 0.23 worlds must load with no migration. Natural generation affects only chunks generated while the enabled data pack entries are active; existing placed algae persists when generation is disabled. Removal restores water but never edits neighboring terrain beyond normal block updates. Feature loops and search radii are finite, avoid broad chunk scans, do not force chunk loads, and do not replace structures or nonreplaceable blocks.

World generation remains server authoritative and seed deterministic. Client code only selects rendering. No networking or persistent custom data is needed. Data packs are untrusted input at decode boundaries: invalid ranges, missing holders, invalid states, or malformed tags must fail with actionable registry or pack errors and no partial silent registration. Logs must not spam per placement or expose local paths.

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| Source asset, dimension, or segmentation mismatch | Hash and PNG audit | Stop before copying; do not redraw or repair owner input. | Restore verified source or obtain owner authorized revision. | Repeat complete 22 file audit. |
| Misspelled or duplicate public identifier | Registry and resource scan | Fail before world creation; do not provide compatibility aliases for never released IDs. | Correct field and all references, regenerate data, discard affected test worlds. | Registry dump and JAR scan contain only three correct paths. |
| Registry ordering or freeze failure | Mod load exception or missing BlockItem target | Keep registration dependency ordered and fail startup visibly. | Repair bootstrap ordering; never mutate frozen registries late. | Unit registry test plus dedicated server ready state. |
| Dry, flowing, unsupported, occupied, or land placement | Placement or survival oracle | Reject without replacing terrain or leaving air in water. | No world repair should be needed; fixture cleanup removes only test owned state. | Negative GameTests for each condition. |
| Support is removed after placement | Neighbor update | Break the algae under vanilla aquatic semantics and preserve the water cell. | Restore test support and replace through normal placement only. | Support loss and fluid state GameTest. |
| Wrong collection tool or explosion path | Loot context test | Follow generated loot and vanilla seagrass style collection without duplicate drops. | Correct provider, regenerate, and rerun loot matrix. | Tool positive and negative loot tests plus in game collection. |
| Animation frame skipped, deduplicated, interpolated, or reordered | Strip segmentation, metadata sequence, and client capture | Block acceptance even if texture loads. | Restore exact source strip and authored hold mapping. | Full loop frame timeline and source hash comparison. |
| Texture atlas or render layer fault | Client log, missing texture, opaque quad, or visual artifact | Keep visual gate open. | Correct resource reference or client only render registration. | Clean atlas log and interactive captures. |
| Missing configured or placed feature holder | Data pack registry error | Fail load visibly; never substitute a hard coded fallback. | Repair provider references and regenerate from a clean output. | Codec, holder, and dedicated server reload tests. |
| Generation on land, invalid floor, or occupied space | Fixed seed coordinate validator | Fail the phase and preserve failing coordinates. | Repair placement predicate or modifiers, discard generated test worlds, rerun full sample. | Zero invalid placements in both repeated worlds. |
| Density equals or exceeds seagrass, or per chunk cap is exceeded | Shared sample report | Treat as a world generation flood and keep `DEF-024-008` open. | Reduce finite default attempts or patch spread, regenerate worlds, repeat all density evidence. | Combined count below seagrass and maxima within configured caps. |
| A required form is absent from the sample | Zero count with seagrass positive control | Treat as failed exposure, not conservative success. | Adjust data driven default placement without adding lore partitioning. | Positive count for all three forms in both identical manifests. |
| Navigation stalls or reroutes broadly | Bounded aquatic path GameTest and runtime soak | Keep collision and path obstruction gate open. | Correct shape and pathfinding semantics; regenerate representative patches. | Repeated fish and shark path completion through and around algae. |
| Data pack disables or overrides algae | Controlled pack test | New chunks follow the active pack; existing blocks remain intact. | Remove the override or restore default pack and create fresh chunks. | Default, disabled, and bounded override cases pass. |
| Old save or downgrade risk | Backup rehearsal and registry log | Old save must load forward. Never promise safe downgrade with registered blocks present. | Restore backup for downgrade; disabling generation is the supported runtime rollback. | Forward load smoke and documented backup restore rehearsal. |
| Dedicated server loads a client class | Class loading exception or client package reference audit | Fail the phase. | Move rendering registration back behind `ModClientForge` and rebuild. | Static boundary test and dedicated server ready state. |
| Provider and generated output disagree | Second Data run or source comparison | Treat as blocking drift; never patch only generated JSON. | Fix provider, regenerate from clean output, inspect complete diff. | Two consecutive data runs are byte stable. |

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Evidence artifact |
|---|---|---|---|---|---|
| `BFS-REQ-020`, identities | Exact registry and item pairs, translations, no `algea` paths | Common bootstrap through Forge registry event | Creative inventory, `/give`, place and pick workflows | Duplicate or misspelled fixture rejected | Registry manifest and screenshots |
| `BFS-REQ-020`, behavior | Properties, shapes, fluid state, loot contexts | GameTests for place, survive, update, break, collect | Interactive underwater placement and removal | Dry, invalid support, occupied, wrong tool, and support loss cases | Test reports and fluid state captures |
| `BFS-REQ-020`, assets | Source hashes, dimensions, strip segmentation, ordered metadata | Model, blockstate, item model, and atlas reference audit | Full green and red loops in water, item and block rendering | Altered frame, order, metadata, and missing texture controls | Asset manifest, client log, captures |
| `P004-TASK-005`, generated data | Provider coverage and JSON schema tests | Two byte stable Data runs | Resource reload with all blocks and loot usable | Stale generated file and missing reference controls | Generated diff and regeneration hashes |
| `BFS-REQ-021`, data driven features | Codec round trip, holder graph, tag membership, finite limits | Configured to placed to biome modifier chain | Dedicated server data pack load and reload | Malformed holder, missing tag, and disabled modifier cases | Registry dump and server logs |
| `BFS-REQ-021`, fixed seed | Coordinate, support, biome, chunk maximum, and count validator | Two clean worlds, seed `240024`, chunks `0,0` through `15,15` | Generated ocean inspection and navigation soak | Land, invalid support, absent form, cap breach, and density breach fail | Coordinate manifests and density report |
| Compatibility and rollback | No existing ID changes, no migration schema | Existing 0.23 save forward load and data pack disable | Existing block persistence and new chunk behavior | Backup restore for downgrade, malformed override failure | Compatibility run sheet and backup record |
| `DEF-024-008` | Combined deterministic suite | Data generation, GameTests, build, server, and JAR chain | Client rendering and fixed seed natural generation | All defect oracle failures keep defect open | Closure record bound to revision and JAR hash |

### Environments, Fixtures, and Rerun Order

Use the Phase 000 Forge GameTest structures and evidence schema. Fixed seed acceptance uses Overworld seed `240024`, inclusive chunk square `0,0` through `15,15`, default BFS data pack, unchanged relevant configuration, and one named Forge build. The seagrass positive control and algae counter must share eligibility rules. Record block state, position, biome, floor state, fluid state, feature origin when observable, and chunk for every counted placement.

Run formatter or record its absence, configured static analysis or record its absence, `./gradlew :forge:test`, `./gradlew :forge:Data`, Forge GameTests, `./gradlew :forge:build`, `./gradlew :forge:Server`, `./gradlew :forge:Client`, compatibility and data pack workflows, then JAR and full diff inspection. A source, generated data, registry, block behavior, placement, tag, or density change invalidates downstream proof. Restart from the earliest affected step. The unsupported root Fabric build is recorded separately and never substitutes for Forge success. Headless client loading cannot satisfy visual acceptance.

## Documentation, Operations, and Release

- Update `README.md` with the three algae forms, creative access, natural ocean generation, supported version, and links to technical details after behavior passes.
- Update `DOCUMENTATION.md` with registration flow, aquatic block semantics, asset and animation pipeline, provider ownership, configured and placed feature graph, biome tag extension point, generation step, default distribution principles, compatibility, and client boundary.
- Update `CHANGELOG.md` with verified algae content and world generation behavior without claiming the complete `0.24` release is published.
- Update `docs/README.md` links if algae or verification topic documentation is added. Store test procedures and results under the existing documentation layout rather than inside this protected plan file.
- Document operator rollback: back up worlds before upgrade, disable the algae add feature biome modifier or biome tag through a data pack to stop future generation, retain existing algae blocks, and restore a backup before downgrading to a build that lacks the registry entries.
- Record identifiers and data pack entry points for pack authors. Do not claim unsupported biome ecology, retroactive generation, Fabric support, or safe downgrade of populated worlds.
- Phase 005 owns final version metadata, checksums, SBOM, attestations, release notes, publication, and final plan wide revalidation.

## Risks and Evidence Invalidation

| Risk | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| Reflection derived field name creates wrong public ID | Exact holder naming and preworld registry test | Registry dump and `algea` scan | Correct holder and every reference, regenerate | Registry, data, worlds, JAR | Full phase matrix |
| Repeated frames are optimized away | Treat numbered frames and strips as immutable authored timing | Frame hash groups and timeline audit | Restore exact strip and metadata | Asset, client, JAR | Asset audit and full loop capture |
| Aquatic removal creates air or deletes water | Vanilla seagrass semantics and fluid update tests | GameTest fluid state after all removal paths | Correct block update and fluid behavior | Behavior, feature, navigation | GameTests through runtime matrix |
| Generation floods or dominates seabed | Finite attempts, conservative defaults, shared seagrass comparison | Per chunk maxima and aggregate density | Reduce defaults and regenerate fresh worlds | Fixed seed, runtime, docs | Both world manifests and navigation soak |
| Tag scope silently excludes or partitions a color | Neutral shared ocean family coverage | Tag and observed biome matrix | Correct provider and regenerate | Worldgen, server, docs | Codec, tags, fixed seed, runtime |
| Transparent rendering leaks into server | Client only registration | Dependency scan and server startup | Move all renderer references behind client boundary | Build, server, client, JAR | Static audit then server and client |
| Existing world compatibility is assumed | Use representative 0.23 save and backup copy | Registry and data pack load logs | Fix additive registration or restore backup | Compatibility and completion | Forward load and new chunk generation |
| Data pack override bypasses placement safety | Keep survival and replacement checks in block and feature contract | Malformed and high attempt override tests | Reject invalid data or rely on block placement validation | Codec, recovery, security | Unit, GameTest, server reload |
| Later phase changes an algae surface | Bind proof to source revision and JAR hash | Phase 005 diff against completion packet | Reopen affected gate | Exact touched evidence | Rerun from earliest impacted verification step |

## Phase Completion Packet

The phase may close only with all of the following proof stored outside the protected plan set:

1. Phase branch point, Phase 003 merge and tag verification, source revision, dependency versions, configuration, protected path hashes, and clean scope manifest.
2. All 22 source asset hashes and dimensions, strip segmentation proof, repeated hold groups, source to destination map, and no misspelled public path scan.
3. Registry and creative inventory manifests for the three blocks and three items, with exact translations.
4. Generated blockstate, model, item model, language, loot, block tag, item tag, configured feature, placed feature, biome tag, and biome modifier inventories plus two byte stable Data runs.
5. Deterministic test and GameTest reports covering identities, assets, loot, codecs, valid and invalid placement, survival, fluid restoration, collection, feature placement, cleanup, and navigation.
6. Two fixed seed coordinate and state manifests for seed `240024` and chunks `0,0` through `15,15`, per form and per chunk counts, finite cap proof, invalid placement report, structure check, and common seagrass comparison.
7. Dedicated server ready log and data pack reload results with no registry, codec, tag, loot, feature, or client class loading fault.
8. Interactive client captures for inventory, in hand and placed presentation, full animation loops, generated ocean distribution, collection, removal, and clean atlas log.
9. Existing 0.23 save forward load, new chunk generation, disabled generation pack, bounded override, backup, rollback, and downgrade warning evidence.
10. Ordered command results, Forge JAR hash and complete inventory, complete Git diff, generated diff, documentation changes, and protected owner work verification.
11. `DEF-024-008` closure record mapping every minimum and phase acceptance criterion to exact evidence.
12. Issue, milestone, Project, pull request, deterministic checks, independent review disposition when supported or verified capability-unavailability record, merge commit, `origin/main` containment, signed annotated Phase 004 tag target and signature, and Phase 005 handoff.

## Next Transition

The only next transition is `BFS-PHASE-005`, systematic defect audit and release. After GitHub reports the Phase 004 pull request merged, fetch the remote, verify the resulting merge commit is contained by the authoritative default branch, create and push the signed annotated Phase 004 tag on that merge commit, and verify its signature and target. Then reread the master and `docs/general/phases/plan-phase-005.md` through EOF, rebind Phase 004 evidence to the merged revision, and begin the first unfinished Phase 005 task from updated `origin/main`. Do not start Phase 005 from the Phase 004 branch or while any Phase 004 check, review, merge, tag, defect closure, or evidence gate remains open.
