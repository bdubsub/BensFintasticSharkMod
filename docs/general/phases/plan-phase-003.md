# Phase 003 Execution Plan

> **Plan ID:** PLAN-PHASE-003
> **Phase ID:** BFS-PHASE-003
> **Owner:** Advancement localization maintainer
> **Classification:** MANDATORY
> **Master plan:** [plan.md](../plan.md)
> **Phase sequence:** 003 of 005

## Purpose and Ownership

This phase delivers the complete 0.24 advancement presentation, retires duplicate progression without breaking either descendant branch, and establishes deterministic advancement graph, localization, and icon audits. It canonically owns `BFS-REQ-013` through `BFS-REQ-016` and closes `DEF-024-001`, `DEF-024-005`, `DEF-024-006`, and `DEF-024-007`.

The master plan owns product scope, exact copy decisions, compatibility policy, the frozen phase sequence, and plan-wide completion. This blueprint owns only the dependency-ordered execution of `BFS-PHASE-003`. It may recheck retained advancement behavior under `BFS-REQ-012`, but it does not take that requirement from `BFS-PHASE-001`.

Phase completion requires deterministic source and generated-resource proof, a fresh-profile completion path, an existing-world compatibility check, interactive visual evidence, and interim JAR inspection. Source inspection or a successful build alone cannot close any owned defect.

## Evidence-Based Entry State

Planning-time observations must be refreshed from the merged and tagged `BFS-PHASE-002` default-branch commit before implementation.

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| VERIFIED | Product contract | This phase owns `BFS-REQ-013` through `BFS-REQ-016`, follows merged and tagged `BFS-PHASE-002`, and precedes `BFS-PHASE-004`. | `docs/general/plan.md` | Invalid after an owner-authorized plan revision affecting phase scope or order. |
| VERIFIED | Supplied icon archive | `Content/BFS 0.24 Content.zip` has SHA 256 `25290e8d019339aff68c8ba168bd750d5c445aa915720d249aeed5700c9f3274` and contains seven mapped 16 by 16 advancement icons. | Master inventory, archive listing, and hash | Recheck the hash before extraction. Any mismatch blocks use of the archive. |
| OBSERVED | Advancement provider | `BensFintasticSharksAdvancements` generates both `sharks_galore` and duplicate `shark_whisperer`; `marine_biologist` and `apex_of_apex` currently parent to `shark_whisperer`. | `forge/src/main/java/tfar/bensfintasticsharks/datagen/data/BensFintasticSharksAdvancements.java` | Re-audit after Phase 002 and after every provider edit. |
| OBSERVED | Generated graph | Generated resources currently include `shark_whisperer.json`; `marine_biologist.json` and `apex_of_apex.json` name it as parent. | `common/src/generated/resources/data/bensfintasticsharks/advancements/` | Invalid after data generation or manual resource change. |
| OBSERVED | Localization source | `ModLangProvider` owns English advancement copy. It currently contains dedicated `shark_whisperer` keys, the incomplete captain description, several missing terminal marks, and seven titles that do not all match the supplied 0.24 titles. | `forge/src/main/java/tfar/bensfintasticsharks/datagen/ModLangProvider.java` | Re-audit after Phase 002 and after every localization edit. |
| OBSERVED | Generated language | English output is generated at `common/src/generated/resources/assets/bensfintasticsharks/lang/en_us.json`. | Current data-generation layout | Invalid after provider or data-generation changes. |
| OBSERVED | Hidden icon items | Existing hidden items back all seven displays: `harbor_seal_block`, `albino`, `sharks_galore`, `sleeping_with_the_fishes`, `specimen_8`, `mommy_shark`, and `zippy_pixel_art`. | `common/src/main/java/tfar/bensfintasticsharks/init/ModItems.java`, `HiddenItem.java` | Recheck after registry changes. Stable item IDs must be preserved. |
| OBSERVED | Icon models | Six targets use generated flat item models. `harbor_seal_block` currently uses a hand-authored 3D model at `common/src/main/resources/assets/bensfintasticsharks/models/item/harbor_seal_block.json`. | Existing model resources and `forge/src/main/java/tfar/bensfintasticsharks/datagen/ModItemModelProvider.java` | Recheck at entry. Exactly one effective model per target may remain after generation. |
| OBSERVED | Icon textures | The seven current destination textures are 64 by 64, while the supplied replacements are 16 by 16 and must not be resampled. | `common/src/main/resources/assets/bensfintasticsharks/textures/item/` and archive dimension audit | Invalid after any icon copy. Record before and after dimensions and hashes. |
| VERIFIED | Current test limitation | Planning-time repository evidence found one Forge unit test and no dedicated advancement audit. Phase 000 must supply the reusable evidence harness contract. | `forge/src/test/java`, `docs/general/phases/plan-phase-000.md` | Replace with the merged test inventory at entry. Missing harness capability blocks completion, not implementation diagnosis. |
| VERIFIED | Commands | Supported gates use `./gradlew :forge:test`, `./gradlew :forge:Data`, `./gradlew :forge:build`, `./gradlew :forge:Server`, and `./gradlew :forge:Client`. | `docs/general/plan.md`, Forge build configuration | Recheck if Gradle configuration changes. |
| VERIFIED | Documentation | Existing maintained surfaces are `README.md`, `DOCUMENTATION.md`, `CHANGELOG.md`, and `docs/README.md`. | Repository inventory | Recheck after Phase 002 integration and preserve paths and casing. |

## Scope Boundaries

### Included Scope

- `BFS-REQ-013`: integrate the seven exact 16 by 16 icons through the existing hidden-item display pattern, with matching models, textures, generated advancement references, and JAR contents.
- `BFS-REQ-014`: retire `bensfintasticsharks:shark_whisperer`; remove its exclusive copy and unreachable presentation resources; preserve `sharks_galore` as the single all-shark achievement; directly reparent both `marine_biologist` and `apex_of_apex` to it without changing their criteria, rewards, display settings, or descendants.
- `BFS-REQ-015`: apply all seven exact supplied titles and set the captain copy exactly to title `El Capitán's Legacy` and description `Obtain Capitán Ben's Hat.`.
- `BFS-REQ-016`: normalize every English advancement title and description under the master convention and add deterministic provider-to-output copy validation.
- The phase-owned slice of `BFS-REQ-022`: close `DEF-024-001`, `DEF-024-005`, `DEF-024-006`, and `DEF-024-007` with their minimum evidence.
- Regression verification for retained Shark Spotter and Atlantic fish advancements under `BFS-REQ-012`, because the full graph and English advancement catalog are regenerated.
- Existing-world advancement compatibility and fresh-profile reachability, visual review, data generation, server loading, interim packaging, documentation, and phase integration.

### Explicit Exclusions

- Entity animation and predator behavior under `BFS-REQ-017` through `BFS-REQ-019` remain closed by `BFS-PHASE-002`; this phase does not alter their gameplay criteria or triggers.
- Algae content and world generation under `BFS-REQ-020` and `BFS-REQ-021` remain owned by `BFS-PHASE-004`.
- Final release versioning, publication, checksums, SBOM, attestations, and plan-wide revalidation remain owned by `BFS-PHASE-005`. This phase only inspects its interim Forge artifact.
- No advancement other than `shark_whisperer` is retired or renamed by identifier. No trigger, criterion, reward, frame, visibility flag, or descendant relationship changes unless explicitly required by `BFS-REQ-014` or needed to restore a provider-output mismatch without changing behavior.
- Existing `shark_whisperer` player progress is not copied, transformed, or awarded to another node. The surviving `sharks_galore` criteria remain authoritative.
- Supplied pixels are not redrawn, scaled, filtered, recolored, or recompressed. The source archive remains immutable evidence.
- No Minecraft, Forge, Java, Gradle, mapping, GeckoLib, SmartBrainLib, or loader change is permitted. The unsupported Fabric stub is not a release target.
- Protected owner changes in `build.gradle` and the untracked `Content/` directory remain outside the phase diff and commit.

## Phase Contract

### BFS-PHASE-003 — Advancements, Icons, and Copy

**Objective:** Produce a deterministic, visually verified, compatible advancement tree with seven exact remastered icons and titles, complete captain copy, consistent English punctuation, no `shark_whisperer`, and both former children directly reachable through `sharks_galore`.
**Owner:** Advancement localization maintainer
**Dependencies:** BFS-PHASE-002, DEC-002, DEC-003
**Canonical requirements:** BFS-REQ-013, BFS-REQ-014, BFS-REQ-015, BFS-REQ-016
**Documentation and release impact:** Update existing advancement and upgrade documentation with only verified behavior. Supply deterministic, visual, compatibility, and interim artifact evidence to Phase 005. Do not publish or set the final release version here.
**Next transition:** `BFS-PHASE-004`

**Entry criteria**

- GitHub reports the Phase 002 pull request merged, `origin/main` contains its merge commit, and the required signed annotated Phase 002 tag verifies against that commit.
- The Phase 003 branch starts from that verified `origin/main`; no stacked branch or direct default-branch change is allowed.
- The master, registered phase set, index, handoff, and prior completion packets agree on project identity, current phase, and the repaired `BFS-REQ-014` relationship contract.
- The Phase 000 archive ledger and defect fixtures are available. Recomputed archive, source-resource, and generated-resource inventories match or any drift is classified before edits.
- A clean fresh-profile fixture and a copy of an existing 0.23 or Phase 002 profile containing advancement progress are available. Tests operate only on disposable copies.
- The complete current advancement node, parent, criteria, rewards, display, translation-key, model, texture, and dimension inventory is captured.
- Owner work, evidence archives, run directories, caches, logs, and generated noise are identified and excluded from the branch diff.

**Implementation scope**

- `BFS-PHASE-003`: `BFS-REQ-013`: install all seven supplied remastered icons through the existing hidden advancement-item model pattern and verify exact source-to-destination bytes.
- `BFS-PHASE-003`: `BFS-REQ-014`: retire `shark_whisperer`, retain `sharks_galore`, reparent both direct children, and preserve all unaffected graph semantics and profile state.
- `BFS-PHASE-003`: `BFS-REQ-015`: apply the exact supplied titles and the complete El Capitán's Legacy title and description contract.
- `BFS-PHASE-003`: `BFS-REQ-016`: normalize all other advancement copy through one deterministic punctuation convention and provider-to-generated-output audit.
- `BFS-PHASE-003`: `BFS-PHASE-003`: regenerate data, exercise fresh and existing profiles, verify dedicated-server and interactive-client behavior, inspect the interim JAR, update affected documentation, and integrate through the required pull-request and signed-tag workflow.

**Execution order**

1. `P003-TASK-001` for `BFS-PHASE-003` refreshes the full advancement, copy, model, texture, profile, and defect baseline from the Phase 002 merge.
2. `P003-TASK-002` for `BFS-PHASE-003` extracts and installs the seven supplied icons byte-for-byte at the existing hidden-item destinations and normalizes their item models.
3. `P003-TASK-003` for `BFS-PHASE-003` removes `shark_whisperer`, reparents both direct children to `sharks_galore`, and proves all other node semantics are preserved.
4. `P003-TASK-004` for `BFS-PHASE-003` applies the seven exact titles, exact captain copy, and deterministic English advancement copy convention.
5. `P003-TASK-005` for `BFS-PHASE-003` implements deterministic icon, model, graph, and localization audits and negative fixtures.
6. `P003-TASK-006` for `BFS-PHASE-003` regenerates resources, reviews the complete generated diff, and proves a second generation is clean.
7. `P003-TASK-007` for `BFS-PHASE-003` runs existing-world compatibility, fresh-profile completion, dedicated-server, interactive client, and interim JAR checks.
8. `P003-TASK-008` for `BFS-PHASE-003` updates existing documentation, assembles closure evidence, integrates through GitHub, verifies default-branch state, and creates the signed phase tag.

**Required evidence**

- Archive hash, entry path, extracted-byte hash, destination-byte hash, PNG format, and exact 16 by 16 dimensions for every mapped icon.
- Generated advancement JSON proves each display uses its required existing hidden item and no target points to a stale model or texture.
- Generated graph proves `shark_whisperer` is absent, `marine_biologist` and `apex_of_apex` directly parent to `sharks_galore`, and their criteria, rewards, display properties, and descendants match the pre-change snapshot except for the required parent edge.
- Provider and generated language prove the seven exact titles and exact captain text. A deterministic audit covers every English advancement title and description, rejects missing or extra keys, and proves provider-output agreement.
- Fresh-profile runtime evidence shows the complete tree loads and both `sharks_galore` child branches are visible, reachable, and completable. Captures show all seven icons and titles plus the complete captain tooltip.
- Existing-world evidence shows a disposable profile with old `shark_whisperer` progress loads without data-pack error, does not migrate or grant that progress, preserves unrelated progress, and continues tracking surviving nodes normally.
- Dedicated-server readiness has no advancement, data-pack, registry, model-independent common initialization, or client-class-loading failure.
- Interim Forge JAR inventory proves required advancement JSON, language, models, and seven 16 by 16 textures are packaged once, while `shark_whisperer` JSON and exclusive language keys are absent.
- Complete diff and generated-output audits prove no stale 64 by 64 target icon, manual generated-output edit, evidence archive mutation, or unrelated change entered the phase.

**Exit criteria**

- `BFS-REQ-013` through `BFS-REQ-016` pass every applicable verification row.
- `DEF-024-001`, `DEF-024-005`, `DEF-024-006`, and `DEF-024-007` have their master-required evidence and are no longer reproducible.
- All seven icon mappings, exact titles, and exact captain text match the canonical table below in source, generated output, runtime display, and interim artifact.
- `shark_whisperer` has no generated node or dedicated language keys. `sharks_galore` remains the only all-shark achievement.
- Both `marine_biologist` and `apex_of_apex` directly parent to `sharks_galore`; their criteria, rewards, display settings, descendants, and completion behavior remain intact.
- Every intended advancement remains connected, loadable, reachable, and completable on a fresh profile. Existing saves load safely under the no-migration policy.
- All English advancement copy passes exact-map and punctuation audits. Data generation is deterministic and idempotent.
- Ordered Forge tests, data generation, applicable GameTests, Forge build, dedicated-server smoke, interactive client review, profile checks, and interim JAR inspection pass.
- Documentation matches verified behavior, the complete diff is scoped, and required checks pass. One private independent review passes when supported, or verified capability unavailability is recorded. The pull request is merged, `origin/main` contains the merge, and the signed annotated Phase 003 tag verifies on that merge.
- No known mandatory phase-owned defect remains. `BFS-PHASE-004` has not started early.

## Canonical Icon and Title Map

Copy each archive entry without pixel transformation. Preserve the existing item registry ID and make its effective model a flat generated item model referencing the listed destination texture.

| Archive entry under `Advancement Icons Remaster/` | Advancement ID | Existing icon item and destination texture | Exact title |
|---|---|---|---|
| `Awkward Advancement Icon 16x.png` | `harbor_seal_encounter` | `harbor_seal_block`, `textures/item/harbor_seal_block.png` | `Awkward...` |
| `Its A Shiny Advancement Icon 16x.png` | `albino_encounter` | `albino`, `textures/item/albino.png` | `It's a shiny!` |
| `Sharks Galore Advancement Icon 16x.png` | `sharks_galore` | `sharks_galore`, `textures/item/sharks_galore.png` | `Sharks Galore!` |
| `Sleeping With The Fishes Advancement Icon 16x.png` | `sleeping_with_the_fishes` | `sleeping_with_the_fishes`, `textures/item/sleeping_with_the_fishes.png` | `Sleeping with the fishes.` |
| `Specimen8 Advancement Icon 16x.png` | `specimen_8_encounter` | `specimen_8`, `textures/item/specimen_8.png` | `I'll be back` |
| `Mommy Shark Advancement 16x.png` | `deep_blue_encounter` | `mommy_shark`, `textures/item/mommy_shark.png` | `Mommy Shark.` |
| `Thunder Bringer Advancement 16x.png` | `zippy_encounter` | `zippy_pixel_art`, `textures/item/zippy_pixel_art.png` | `THUNDER BRINGER!` |

The destination root is `common/src/main/resources/assets/bensfintasticsharks/`. Generated flat models belong under `common/src/generated/resources/assets/bensfintasticsharks/models/item/`. The `harbor_seal_block` manual 3D model must not continue shadowing the remastered flat icon model.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| Merged Phase 002 baseline | `BFS-PHASE-002` | Entity behavior and advancement acceptance observations are stable. | Verify merge, default-branch ancestry, signed tag, and completion packet. | Stop. Do not stack this phase. |
| Phase 000 evidence contract | `BFS-PHASE-000` | Asset ledger, profile fixtures, runtime capture rules, and defect baselines exist. | Reconcile to current paths and hashes. | Fill only approved harness gaps or stop if evidence identity is unresolved. |
| Seven source icons | `Content/BFS 0.24 Content.zip` | Exact archive hash and seven expected PNG entries. | Safe archive listing, path validation, SHA 256, format, dimensions, and byte hashes. | Reject changed, malformed, missing, extra-mapped, or unsafe input. |
| Advancement providers | Current Forge data generation | Providers are the source of generated graph, models, and English copy. | Snapshot provider definitions and complete generated output. | Resolve drift at provider source; do not patch generated JSON by hand. |
| Retained advancement contract | `BFS-REQ-012` and Phase 001 | Shark Spotter and four Atlantic fish advancements remain functional. | Generated graph and fresh-profile checks. | Treat regression as phase blocking. |
| Copy decisions | `BFS-REQ-015`, `BFS-REQ-016`, `DEC-003` | Exact supplied titles and captain text override current inconsistent copy. | Exact Unicode-aware comparison against the canonical expected map. | Fail closed on any character, whitespace, apostrophe, case, or punctuation mismatch. |
| Retirement decision | `BFS-REQ-014`, `DEC-002` | `shark_whisperer` retires; both direct children move to `sharks_galore`; no old-progress migration occurs. | Structural snapshot, generated graph, fresh profile, and copied old profile. | Stop on any orphan, changed child semantics, migration, or load failure. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| Canonical advancement graph | `BFS-PHASE-004`, `BFS-PHASE-005` | One all-shark node, no retired node, two required direct child edges, and all intended nodes connected. | Stable IDs remain except intentional retirement of `shark_whisperer`. | Deterministic graph report, fresh-profile completion, and JAR graph inventory. |
| Canonical English copy | Phase 004 content additions and Phase 005 audit | Every advancement key has one exact audited title and description. | New advancement copy must satisfy the same convention. | Expected-map test, punctuation audit, generated `en_us.json`, and visual captures. |
| Seven icon pipeline | Phase 005 packaging | Existing hidden item IDs resolve to exact supplied 16 by 16 textures through unambiguous models. | No new public item IDs; bytes remain unchanged from archive entries. | Hash and dimension manifest, model resolution audit, and JAR inventory. |
| Save compatibility contract | Operators and Phase 005 | Old saves load; retired progress is ignored without migration; unrelated and surviving progress remain intact. | One intentional advancement ID retirement only. | Copied-profile load and before-after progress report. |
| Phase completion packet | `BFS-PHASE-004`, `BFS-PHASE-005` | Reproducible proof for all four requirements and defects. | Stored outside the protected plan set using Phase 000 conventions. | Test logs, reports, screenshots, profile results, artifact inventory, merge, and tag verification. |

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| `P003-TASK-001` | `BFS-REQ-013` through `BFS-REQ-016`; `DEF-024-001`, `DEF-024-005` through `DEF-024-007` | Recompute archive, provider, generated graph, translation, item, model, texture, profile, and defect inventories. Snapshot every node's parent, criteria, rewards, display flags, and descendants before edits. | Phase 002 merge and tag, Phase 000 fixtures, source archive | Protected baseline and traceability matrix | Existing advancement providers, generated resources, seven item/model/texture chains, disposable profiles | Hashes, JSON inventory, parent and semantic graph report, PNG dimensions, and clean diff boundary. |
| `P003-TASK-002` | `BFS-REQ-013`, `DEF-024-007` | Safely extract the seven mapped entries, copy bytes to existing texture destinations, ensure seven flat generated item models, and remove the obsolete manual Harbor Seal model conflict. Preserve hidden item IDs and creative-tab invisibility. | Task 001 and canonical map | Seven exact textures and one effective flat model per item | `ModItems`, `HiddenItem`, `ModItemModelProvider`, main texture resources, generated model resources | Byte equality, 16 by 16 dimensions, model-to-texture resolution, registry identity, creative inventory exclusion, client display, and JAR checks. |
| `P003-TASK-003` | `BFS-REQ-014`, `DEF-024-005` | Remove the duplicate builder and generated node, delete only dedicated language entries and truly exclusive unreachable presentation resources, retain `sharks_galore`, and directly reparent `marine_biologist` and `apex_of_apex` while preserving their remaining definitions. | Task 001 graph snapshot and retirement decision | Connected canonical graph and retirement manifest | `BensFintasticSharksAdvancements`, generated advancements, `ModLangProvider`, generated language | Exact graph assertions, before-after semantic comparison, orphan and cycle audit, negative retired-ID search, existing-profile load, and fresh-profile completion. |
| `P003-TASK-004` | `BFS-REQ-015`, `BFS-REQ-016`, `DEF-024-001`, `DEF-024-006` | Apply the canonical seven titles and exact captain text. Inventory and normalize every other English advancement title and description, then define one expected map as the deterministic authority for generated copy. | Task 001 copy inventory, canonical map, `DEC-003` | Complete provider copy with explicit expected values | `TextComponents`, `ModLangProvider`, generated `en_us.json`, all generated advancement translation references | Exact Unicode comparison, title and description convention checks, key completeness and uniqueness, generated agreement, captain screenshot, and complete visual review. |
| `P003-TASK-005` | `BFS-REQ-013` through `BFS-REQ-016`; `DEF-024-001`, `DEF-024-005` through `DEF-024-007` | Add focused automated audits for icon mapping and dimensions, model resolution, graph connectivity and semantics, retired resources, localization key coverage, exact copy, punctuation, and provider-output agreement. Include mutation fixtures that prove failures are detected. | Tasks 002 through 004 and Phase 000 harness | Repeatable advancement acceptance suite | Existing Forge unit-test source set and inherited test harness; exact new test paths follow the merged harness conventions | `:forge:test` fails on wrong bytes, dimensions, model, parent, orphan, cycle, stale key, missing key, extra key, whitespace, apostrophe mixture, doubled or missing terminal mark, or provider-output drift. |
| `P003-TASK-006` | `BFS-REQ-013` through `BFS-REQ-016` | Run data generation, inspect all changes, remove stale outputs through provider ownership, then rerun from a clean generated state and prove idempotence. | Tasks 002 through 005 | Authoritative generated advancements, language, and item models | `ModDatagen`, advancement, language, and item model providers; `common/src/generated/resources` | `./gradlew :forge:Data`, complete generated diff, second-run zero diff, no duplicate pack path, and deterministic hashes. |
| `P003-TASK-007` | `BFS-REQ-013` through `BFS-REQ-016`; `DEF-024-001`, `DEF-024-005` through `DEF-024-007`; regression `BFS-REQ-012` | Run ordered automated and runtime gates. Exercise disposable old and fresh profiles, all mapped displays, captain tooltip, both reparented branches, retained advancements, server load, and interim artifact packaging. | Final generated output from `P003-TASK-006` | Runtime and artifact closure evidence | Forge test, Data, GameTest, build, Server, Client tasks; player advancement persistence; built Forge JAR | Clean tests, server ready state, interactive screenshots, completion traces, no load warnings, compatibility report, and JAR inventory. |
| `P003-TASK-008` | Phase slice of `BFS-REQ-022` | Update maintained documentation, record migration and recovery behavior, assemble evidence, inspect the complete diff, commit and push on the phase branch, open or update the phase pull request, complete required checks and private independent review when supported, merge, verify main, and sign the phase tag. | All prior tasks and repository workflow rules | Merged, tagged Phase 003 and Phase 004 handoff | `README.md`, `DOCUMENTATION.md`, `CHANGELOG.md`, `docs/README.md` as applicable; Git and GitHub workflow | Documentation matches evidence, required checks pass, review passes when supported or capability unavailability is recorded, merge commit is on `origin/main`, signed tag verifies, and Phase 004 entry packet is complete. |

Tasks 002 and the initial graph or copy analysis in Tasks 003 and 004 may proceed independently after Task 001 because they touch distinct sources. Final model, graph, and copy changes must converge before Task 005. Task 006 is strictly after provider changes and audits exist. Task 007 is strictly after deterministic generated output. Task 008 is strictly after all technical and runtime gates pass.

If an audit discovers a child, criterion, reward, display contract, identifier retirement, or copy decision not covered by the master, stop with `PLAN_REVISION_REQUIRED`. Do not silently broaden the graph rewrite or invent copy.

## Architecture and Implementation Boundaries

### Provider and generated-resource ownership

- `forge/src/main/java/tfar/bensfintasticsharks/datagen/data/BensFintasticSharksAdvancements.java` is the graph and display-reference source. `forge/src/main/java/tfar/bensfintasticsharks/datagen/ModLangProvider.java` is the English copy source. `forge/src/main/java/tfar/bensfintasticsharks/datagen/ModItemModelProvider.java` owns generated flat item models.
- Generated resources are derived artifacts and must match those providers after `:forge:Data`. Do not hand-edit generated advancement JSON, language JSON, or generated item models to simulate success.
- Main texture resources hold the exact supplied bytes. Ensure a model exists at only one effective pack path per item. Remove or replace the current manual Harbor Seal 3D model so it cannot shadow the required flat model.
- Preserve the seven existing hidden item registry IDs and `HiddenItem` behavior. Do not expose icon-only items in the creative tab or create redundant icon items.
- Every advancement translation reference must resolve exactly once in `en_us.json`. Every advancement copy key must be referenced by an intended advancement; `shark_whisperer` keys must be absent.

### Graph invariants

- Treat the generated graph as a directed parent-to-child graph rooted at `bensfintasticsharks:root`. Every intended BFS advancement must be reachable from the root, have at most one parent, and participate in no cycle.
- `sharks_galore` retains its ID, all-eight-shark criteria, challenge presentation, reward, and existing parent. It is the sole all-shark encounter achievement.
- `marine_biologist` and `apex_of_apex` each change only their parent edge to `sharks_galore`. Snapshot and compare criteria, requirements strategy, rewards, display item, frame, visibility flags, and descendants before and after generation.
- Remove the `shark_whisperer` provider builder and derived JSON. Remove only language or presentation resources exclusively owned by it. The shared `sharks_galore` hidden item, model, texture, and copy remain required.
- Retained Shark Spotter, `Oh My Cod`, `Why aren't you red?`, `Gadus morhua`, and `Salmo salar` IDs, criteria, parents, and displays remain unchanged.

### Copy convention and deterministic audit

- The seven titles are exact character sequences from the canonical map. Their capitalization and terminal punctuation override generic normalization rules.
- `captains_heir` is exact: title `El Capitán's Legacy`; description `Obtain Capitán Ben's Hat.`. Preserve the acute accent and straight apostrophes exactly.
- For every other title, use intentional display-title capitalization and reject accidental trailing punctuation, trailing whitespace, doubled terminal marks, or mixed smart and straight apostrophes within one phrase. Preserve intentional punctuation only when represented in the reviewed expected map.
- Every description is complete sentence-case text with exactly one grammatical terminal mark at its end. Internal punctuation may remain when grammatical, but trailing whitespace, repeated terminal marks, fragments, and apostrophe mixtures fail.
- Build the audit around an explicit expected key-to-value map for every advancement title and description, supplemented by structural punctuation checks. Locale-sensitive title conversion or heuristic mutation at runtime is prohibited.
- Compare expected provider values, generated `en_us.json`, and translation keys referenced by generated advancement JSON. Missing, duplicate, extra retired, unreferenced, or divergent advancement keys fail verification.
- Unicode handling must be deterministic across machines and locales. Read and compare UTF 8 without smart-quote normalization so unintended substitutions are visible.

### Asset and runtime boundaries

- Validate PNG signature, dimensions, and hash before and after copy. Do not decode and re-encode merely to install the supplied bytes.
- Flat icon models use the established generated-item parent and one `layer0` reference to the mapped texture. No model may point to a different target or missing texture.
- Advancement, language, item registration, and graph generation remain common data. No client renderer class may enter common or dedicated-server initialization.
- Runtime completion criteria remain server authoritative through existing triggers. This phase changes presentation and parent topology, not trigger firing, criteria progress, or rewards.
- Deterministic scans and image validation run at test or build time, never during game ticks or advancement trigger handling.

### Compatibility and persistence

- Preserve every stable public identifier except `bensfintasticsharks:shark_whisperer`. Do not rename hidden items, translation keys for surviving nodes, criteria names, or advancement IDs.
- Minecraft may retain an unknown retired advancement entry in an old player's advancement progress file. The mod must ignore it safely; it must not rewrite player data destructively, fail data-pack load, or synthesize progress into `sharks_galore`.
- Existing `sharks_galore`, `marine_biologist`, `apex_of_apex`, and unrelated progress remains intact. Reparenting changes visibility and prerequisite topology, not already earned criteria or rewards.
- Test old progress only with disposable world and profile copies. Preserve originals and record checksums before launch.
- No schema migration, data fixer, command-driven grant, or startup rewrite is authorized. Recovery is artifact rollback plus restoration of the untouched profile copy if runtime validation fails.

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| Archive hash, entry, or PNG is invalid | Hash, safe listing, signature, or dimension mismatch | Stop before modifying destinations. | Restore destinations from the phase baseline; reacquire authoritative archive through owner workflow. | Corrupt, renamed, traversal, non-PNG, and wrong-size fixtures fail. |
| Wrong icon is mapped | Source and destination hashes disagree with canonical map | Fail the icon audit and block runtime review. | Restore all seven as one atomic mapping set and rerun audits. | Pair-swap mutation is detected. |
| Manual and generated models collide | Duplicate effective pack path or resource precedence scan | Fail generation review; keep one flat authoritative model. | Restore pre-task state, correct provider ownership, regenerate. | Duplicate and stale Harbor Seal model fixture fails. |
| Retired node or keys survive | Provider, generated tree, language, source, or JAR scan finds `shark_whisperer` | Block completion. | Remove from authoritative provider, regenerate from clean output, rerun graph and JAR scans. | Stale JSON and stale-key fixtures fail. |
| Descendant is orphaned or semantics drift | Reachability or before-after semantic comparison fails | Do not load the changed pack as accepted evidence. | Restore provider snapshot, apply only both required parent changes, regenerate. | Missing edge, wrong edge, changed criterion, cycle, and orphan fixtures fail. |
| Copy is malformed or divergent | Exact-map, Unicode, punctuation, key, or provider-output audit fails | Data generation or tests fail with key and reason. | Correct provider source and regenerate; never patch output. | Whitespace, apostrophe, punctuation, missing-key, extra-key, and drift mutations fail. |
| Data generation partially completes | Nonzero task exit, incomplete output, or unexpected broad diff | Discard derived partial output from the phase change and retain source edits for diagnosis. | Restore generated tree from baseline, fix provider, rerun full Data task twice. | Interrupted or deliberately invalid provider run cannot produce an accepted packet. |
| Old profile contains retired progress | Disposable compatibility profile includes old key | Load safely, ignore without migration, preserve unrelated progress, and allow normal surviving-node completion. | Stop using tested copy; restore untouched backup and roll back artifact if load corrupts data. | Before-after progress comparison and successful reload. |
| Fresh graph loads but cannot complete | Trigger path, parent visibility, or award trace stalls | Phase remains open. | Diagnose provider or retained trigger regression; restore prior artifact if needed. | Fresh-profile completion for `sharks_galore` and both direct child branches. |
| Client visual differs from deterministic assets | Wrong icon, clipped copy, missing texture, or stale resource is visible | Runtime evidence fails even when tests pass. | Clear only disposable client resource caches, rebuild, and retest; fix source if reproducible. | Interactive captures for seven displays and captain tooltip. |
| Dedicated server reports client linkage or data error | Startup log or failure before ready state | Block integration. | Revert offending common dependency or generated resource and rerun from clean server directory. | Dedicated-server ready-state log with zero relevant errors. |
| Rollback is required after integration candidate | Mandatory check or review fails | Keep Phase 004 closed and do not merge defective work. | Correct on Phase 003 branch or revert the candidate through normal Git workflow; retain evidence and profile backups. | Full ordered matrix rerun after final correction. |

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Evidence artifact |
|---|---|---|---|---|---|
| `BFS-REQ-013`, `P003-TASK-002` | Archive-to-destination hashes, exact dimensions, model resolution, item-ID mapping | Data generation and JAR resource inventory | Interactive review of all seven advancement icons at native UI scale | Corrupt PNG, wrong size, pair swap, missing texture, duplicate model | Seven-row asset manifest, model report, screenshots, JAR listing |
| `BFS-REQ-014`, `P003-TASK-003` | Node absence, exact required edges, root reachability, acyclic graph, semantic snapshots | Generated data and dedicated-server data-pack load | Fresh completion of `sharks_galore`, `marine_biologist`, and `apex_of_apex` | Orphan, cycle, stale key, changed criteria, old-profile no-migration load | Before-after graph report, completion trace, compatibility report, server log |
| `BFS-REQ-015`, `P003-TASK-004` | Exact Unicode values for seven titles and captain title and description | Provider, generated language, advancement references, and JAR agree | Seven title captures and complete captain tooltip screenshot | One-character, case, accent, apostrophe, punctuation, and whitespace mutations | Expected-map result, generated excerpts, screenshots |
| `BFS-REQ-016`, `P003-TASK-004` and `P003-TASK-005` | Complete key map and structural punctuation audit for every English advancement string | Clean deterministic Data output and exact provider-output comparison | Visual review of complete advancement tree, including long tooltips | Missing, extra, duplicate, fragment, doubled mark, mixed apostrophe, and trailing-space fixtures | Audit report, generated diff, visual checklist |
| Regression `BFS-REQ-012` | Stable ID, parent, display, and criterion comparison | Generated graph and test suite | Fresh-profile Shark Spotter and four fish advancement checks | Wrong item role, wrong trigger, or lost node blocks phase | Retained-advancement report and runtime trace |
| `P003-TASK-006` | Second Data run has no diff and output hashes are stable | Clean regeneration from provider sources | Not applicable; build-time determinism | Partial output is restored and regenerated | First-run diff, second-run status, hash manifest |
| `P003-TASK-007` | All automated audits pass | Forge build, server load, JAR and profile integration | Interactive client, old profile, fresh profile, branch completion | Rollback rehearsal uses disposable copies and prior artifact | Command logs, screenshots, profile checksums, JAR inventory |
| `P003-TASK-008` | Documentation and complete diff audit | Pull request checks and private independent review when supported, or capability-unavailability record | Merge, default-branch, and signed-tag verification | Failed gate keeps phase open and Phase 004 unstarted | Completion packet and GitHub evidence |

Use the Phase 000 fixtures and evidence location. Record exact commands, commit, Java version, Forge runtime, profile checksums, seed if any retained advancement setup uses a world fixture, expected result, actual result, and artifact hash. Run gates in master order: formatter or recorded absence, static analysis or recorded absence, `:forge:test`, `:forge:Data`, applicable GameTests, `:forge:build`, `:forge:Server`, `:forge:Client`, profile and multiplayer checks when the harness requires them, then interim JAR and complete-diff inspection. A headless client can prove loading only; it cannot replace interactive icon and tooltip evidence.

## Documentation, Operations, and Release

- Update `README.md` only if its advancement summary, usage, or compatibility guidance becomes inaccurate.
- Update `DOCUMENTATION.md` with the verified graph, single all-shark role, both direct child relationships, icon pipeline, copy convention, fresh-profile verification, and old-progress no-migration behavior.
- Update `CHANGELOG.md` with accurate 0.24 advancement additions, changes, retirement, copy fixes, and compatibility impact. Correct stale claims that conflict with the seven supplied remasters or duplicate progression.
- Update `docs/README.md` only when advancement documentation links or navigation change.
- Record the source archive hash, mapping manifest, test commands, runtime captures, profile compatibility result, interim JAR inspection, and recovery result in the Phase 000-defined evidence location, not in the protected plan set.
- No operator configuration change is introduced. Upgrade guidance must state that old `shark_whisperer` progress is intentionally not migrated, surviving advancement progress is preserved, and a rollback uses the prior compatible Forge artifact plus an untouched world backup.
- Do not set project version `0.24`, publish an artifact, create release checksums, produce the final SBOM, or attest a release in this phase. Preserve evidence for `BFS-PHASE-005`.

## Risks and Evidence Invalidation

| Risk | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| Provider changes orphan a branch | Assert both required direct edges and full root reachability | Graph audit and fresh completion | Restore snapshot and make only authorized parent changes | Graph, profile, server, and JAR evidence | Tasks 003, 005, 006, and 007 |
| Shared icon item is deleted with retired node | Treat `sharks_galore` item and assets as surviving shared presentation | Registry, reference, and JAR scans | Restore shared item chain and regenerate | Icon and graph evidence | Tasks 002 through 007 |
| Datagen overwrites hand edits or carries stale output | Providers are authoritative; regenerate cleanly twice | Broad diff, provider-output comparison, second-run diff | Restore generated baseline and fix provider | All generated and artifact evidence | Tasks 004 through 007 |
| Image tooling changes bytes | Copy without decode or re-encode | Archive and destination hash mismatch | Restore exact archive entry | Icon visual and artifact evidence | Tasks 002, 005, and 007 |
| Copy normalization damages authored titles | Exact seven-title map and reviewed full expected map | Unicode exact comparison and screenshots | Restore canonical values in provider | Copy, Data, visual, and JAR evidence | Tasks 004 through 007 |
| Old player data is modified destructively | Disposable copies and explicit no-migration implementation | Before-after profile comparison | Stop, restore backup, roll back artifact | Compatibility and phase completion evidence | Tasks 003, 005, and 007 |
| Later resource or provider change | Track source and generated hashes in packet | Diff or digest mismatch | Rebuild from authoritative source | Related test, runtime, screenshot, and JAR proof | Rerun impacted rows and Phase 005 full audit |
| Phase integration changes ancestry | Sequential merge and signed-tag verification | GitHub merge state, `origin/main`, and tag target | Keep Phase 004 blocked until corrected | Completion packet and downstream handoff | Full integration gate |

## Phase Completion Packet

The packet must contain the Phase 002 entry merge and tag proof; archive and destination hash manifest; seven-row icon mapping and dimension report; pre-change and final graph snapshots; child semantic comparisons; retired-resource negative scan; complete expected copy map and audit output; generated-resource diff and idempotence result; automated test and mutation-fixture results; dedicated-server ready log; fresh-profile completion trace; old-profile no-migration and preservation report; interactive screenshots for all seven displays and the captain tooltip; retained advancement regression result; interim JAR inventory and hash; documentation diff; complete repository diff and protected-work audit; required check results; an independent review result when supported or verified capability-unavailability record; pull request merge evidence; verified `origin/main` ancestry; and signed annotated Phase 003 tag verification.

Every artifact must identify the tested commit and environment. Evidence created before the final material change is stale and must be rerun for its affected rows. The completion packet records execution proof outside this protected phase blueprint and may not rewrite the master or saved goal.

## Next Transition

After every exit criterion passes, merge Phase 003 through its approved GitHub pull request using the required merge method. Fetch the remote, verify `origin/main` contains the merge commit, create and push the required signed annotated Phase 003 tag on that merge, and verify the tag signature and target.

Only then reread the complete registered `docs/general/phases/plan-phase-004.md` and its upstream contracts, create the Phase 004 branch from updated `origin/main`, and begin its first unfinished mandatory task. Do not start algae registration, asset integration, or world-generation work while the Phase 003 pull request is open, queued, failed, or not yet tagged.
