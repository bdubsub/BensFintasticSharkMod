# Phase 000 Execution Plan

> **Plan ID:** PLAN-PHASE-000
> **Phase ID:** BFS-PHASE-000
> **Owner:** Integration maintainer
> **Classification:** MANDATORY
> **Master plan:** [plan.md](../plan.md)
> **Phase sequence:** 000 of 005

## Purpose and Ownership

This phase establishes the reconciled `0.23-emergency-fix` and `0.24` evidence baseline before any product behavior is changed. It inventories every supplied archive and loose asset, assigns every stable requirement and beta defect to an owning phase, captures reproducible failure or positive-control evidence for `DEF-024-001` through `DEF-024-008`, and establishes the deterministic and runtime evidence harness used by all later phases.

The master plan owns product scope, requirement meaning, the frozen phase sequence, and the release endpoint. This file owns only the detailed execution of `BFS-PHASE-000`. It must not revise the master, decide new product behavior, close a defect from source inspection, or implement work assigned to `BFS-PHASE-001` through `BFS-PHASE-005`.

This enabling phase has no canonical stable-requirement ownership. It produces the mandatory baseline, defect-ledger, evidence-harness, and traceability inputs consumed by `BFS-REQ-022`, plus the documentation, version, and artifact-surface inventory consumed by `BFS-REQ-023`. Canonical ownership and final closure of both requirements occur exactly once in `BFS-PHASE-005`; Phase 000 does not set version `0.24` or publish a release.

## Evidence-Based Entry State

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| VERIFIED | Repository revision | The inspected worktree is on branch `1.20.1` at `06a28f6b3fcc62ee54cead9e62370c86aea0bfb9`; `eb13b74519837cd80cc72d219d744b0e86a9d565` is an ancestor and is the master-defined `0.23-emergency-fix` merged baseline. | `git rev-parse HEAD`, `git log --oneline`, and `git merge-base --is-ancestor eb13b74 HEAD` | Any branch-tip, default-branch, baseline-tag, or ancestry change invalidates the revision statement. |
| VERIFIED | Supported platform | `gradle.properties` pins Minecraft `1.20.1`, Forge `47.2.0`, Java `17`, Parchment `2023.09.03`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`; Forge is the supported module. | `gradle.properties`, `forge/build.gradle`, and root `build.gradle` | Any manifest, Gradle, wrapper, dependency, mapping, or Java toolchain change invalidates the platform record. |
| VERIFIED | Existing automated tests | The Forge test source set contains one focused JUnit test, `forge/src/test/java/tfar/bensfintasticsharks/spawn/VanillaFishReplacementPolicyTest.java`; no GameTest registration or GameTest source namespace is present. | `forge/build.gradle`, `forge/src/test/java`, and repository search for `GameTest` | Any test-source, Forge run configuration, or test dependency change invalidates this inventory. |
| VERIFIED | Existing Forge runs | `forge/build.gradle` defines `Client`, `Server`, and `Data` runs, but no GameTest run. | `forge/build.gradle` | Any ForgeGradle run configuration change invalidates this finding. |
| VERIFIED | Data generation | `forge/src/main/java/tfar/bensfintasticsharks/datagen/ModDatagen.java` owns language, blockstate, item model, loot, recipe, tag, advancement, data-pack, and global-loot-modifier providers. | `ModDatagen.java` and its provider registrations | Provider registration or generated-resource-root changes invalidate the inventory. |
| VERIFIED | Archive provenance | `Content/BFS 0.23 Content.zip` has SHA-256 `1dcfd0db544184bffd467255ba294b57f0a1376b06023f10034f380b9e5d0eaa`; `Content/BFS 0.24 Content.zip` has SHA-256 `25290e8d019339aff68c8ba168bd750d5c445aa915720d249aeed5700c9f3274`. | `sha256sum` and `unzip -Z1` | Any byte, member list, CRC, path-safety, or link-safety change invalidates all evidence derived from that archive. |
| VERIFIED | Algae source drop | `Content/Algae Textures/` contains 22 PNG files: one 16 by 16 block texture, one 16 by 160 green strip, one 16 by 144 red strip, ten green 16 by 16 frames, and nine red 16 by 16 frames. | `find`, `file`, and per-file hashing of `Content/Algae Textures/` | Addition, removal, rename, byte change, dimension change, or frame-order change invalidates the algae ledger. |
| VERIFIED | Advancement copy defect | Generated language currently says `Obtain the Capitán.` for `captains_heir`, while the required complete description is `Obtain Capitán Ben's Hat.`. | `forge/src/main/java/tfar/bensfintasticsharks/datagen/ModLangProvider.java` and `common/src/generated/resources/assets/bensfintasticsharks/lang/en_us.json` | Provider or generated language changes invalidate the baseline and require regeneration before comparison. |
| VERIFIED | Advancement graph defect | `shark_whisperer.json` exists. `marine_biologist.json` and `apex_of_apex.json` currently name it as parent, so node retirement requires a connected-parent audit, not deletion alone. | `BensFintasticSharksAdvancements.java` and generated advancement JSON | Provider or generated advancement changes invalidate the graph snapshot. |
| VERIFIED | Icon defect | The seven current destination textures are 64 by 64 and have bytes different from the supplied 16 by 16 icons. | `file`, `sha256sum`, archive member hashes, and existing item texture paths | Any source icon, destination texture, model, item registration, or advancement display change invalidates this baseline. |
| OBSERVED | Animation and behavior surfaces | Cod, Salmon, Oceanic Whitetip, and Tiger Shark animation or behavior classes and supplied animation resources exist, but source presence cannot prove visible motion, reachability, attack timing, or recovery. | `common/src/main/resources/assets/bensfintasticsharks/animations/entity/`, common entity classes, and Forge entity animation controllers | Entity logic, controller, animation JSON, geometry, GeckoLib, SmartBrainLib, or runtime environment changes invalidate the observation. |
| VERIFIED | Algae implementation baseline | No public algae block registration, generated algae resource, configured feature, placed feature, or biome tag exists. Existing world generation is centered on `forge/src/main/java/tfar/bensfintasticsharks/worldgen/ModFeatures.java` and `SunkenTroveFeature.java`. | Repository path and symbol search for `algae`, block registrations, and world-generation providers | Any registration, resource, tag, feature, or biome-modifier change invalidates the absence baseline. |
| VERIFIED | Protected dirty worktree | The source worktree has a line-ending-only modification to `build.gradle`, authorized plan-set authoring changes under `docs/general/plan.md` and `docs/general/phases/`, and untracked `Content/`. The worktree hash of `build.gradle` is `9fdd5f8c915a46caed327b1f709795f3ebc82255`; the `HEAD` blob is `81bed49d28dd8764d2b15dd91eaf09cf0a03305e`. | `git status --short`, `git diff --numstat`, `git diff -- build.gradle`, `git hash-object`, and `git rev-parse HEAD:build.gradle` | Any change to the original worktree, including line endings, archive bytes, plan-authoring output, or untracked path membership, invalidates the protection snapshot. |
| VERIFIED | Intake decisions | Algae are permanent `0.24` content, `shark_whisperer` is retired in favor of `sharks_galore`, and advancement punctuation rules are resolved. No owner questions remain open. | `/tmp/bfs-plan-intake.json`, decisions `DEC-001` through `DEC-003` | A direct owner decision or authorized master revision invalidates the affected decision record. |

## Scope Boundaries

### Included Scope

- `BFS-REQ-022`: establish a systematic baseline for every reported beta defect and every release-blocking failure class.
- `BFS-REQ-022`: define repeatable deterministic, GameTest, dedicated-server, client, multiplayer, fixed-seed, population, advancement, log, and artifact evidence protocols.
- `BFS-REQ-022`: create the complete requirement, defect, source-asset, destination-interface, test, and phase ownership matrix.
- `BFS-REQ-023`: inventory current version, documentation, generated-resource, final-JAR, checksum, SBOM, and attestation surfaces for the final release phase without changing release metadata.
- `DEF-024-001` through `DEF-024-008`: reproduce each reported failure or positively disprove it with a control that proves the exercised path. Baseline classification is not defect closure.
- Dirty-worktree protection for every pre-existing modified or untracked path, especially `build.gradle` and `Content/`.
- Test-only harness and fixture infrastructure needed to make later phase evidence repeatable, provided it does not alter shipped behavior or the product contract.
- Phase integration evidence, pull-request checks, merged default-branch verification, and the signed annotated phase tag required before `BFS-PHASE-001`.

### Explicit Exclusions

- `BFS-REQ-001` through `BFS-REQ-012`: behavior repair and retained-contract closure belong to `BFS-PHASE-001`; Phase 000 records their future gates only.
- `BFS-REQ-017` through `BFS-REQ-019`: animation baking, controller repair, Tiger Shark scheduling, navigation, targeting, reach, damage, and recovery belong to `BFS-PHASE-002`.
- `BFS-REQ-013` through `BFS-REQ-016`: icon replacement, advancement copy changes, graph changes, and localization normalization belong to `BFS-PHASE-003`.
- `BFS-REQ-020` and `BFS-REQ-021`: algae registration, rendering, survival, loot, tags, and world generation belong to `BFS-PHASE-004`.
- Final defect closure, version `0.24`, release documentation, release artifact creation, publication, and the plan-wide Definition of Done belong to `BFS-PHASE-005`.
- `FUT-001` and `NG-004`: the Minecraft 26.2 ModJam product under `docs/modjam/` is unrelated and excluded.
- `NG-001` through `NG-003`: dependency or platform upgrades, Fabric support work, and unauthored replacement animations are prohibited.
- Product source changes made only to hide, bypass, or pre-fix a baseline defect are prohibited in this phase.
- The protected `Content/` sources must not be normalized, renamed, moved, deleted, regenerated, or committed.

## Phase Contract

### BFS-PHASE-000 — Baseline Reconciliation and Evidence Harness

**Objective:** Produce an approved, repeatable evidence baseline in which every stable requirement has an owning phase and evidence gate, every supplied asset has verified provenance and a destination interface, every beta defect has a reproducible failing case or valid positive-control case, the later-phase harness is executable, and protected user work remains byte-for-byte unchanged.
**Owner:** Integration maintainer
**Dependencies:** none
**Canonical requirements:** none
**Documentation and release impact:** Record verified harness commands and baseline limitations in the existing technical-documentation system rooted at `DOCUMENTATION.md` and `docs/README.md`. Do not change the product version, changelog release claims, release artifacts, or user-facing behavior.
**Next transition:** `BFS-PHASE-001`, only after the Phase 000 pull request is merged, the resulting default-branch commit is verified, and the signed annotated Phase 000 tag is pushed.

**Entry criteria**

- The frozen master, locked intake, both supplied archives, all 22 loose algae PNGs, the beta screenshot, current source, generated resources, tests, and manifests are available and readable.
- Planning-time `origin/HEAD` points to legacy `origin/1.20.1`. Before a phase branch is created, establish and verify `origin/main` at the same approved baseline history, set or confirm it as the authoritative integration branch, and record the old and new branch identities. Do not rewrite history or advance product behavior during this normalization.
- Archive hashes equal the master values and archive safety inspection finds no traversal path, absolute path, symlink, hard link, duplicate normalized destination, or corrupt member.
- The original worktree's full dirty-state manifest and hashes are captured before any phase-execution branch or worktree is created.
- The implementation workspace is isolated from the protected dirty worktree. No stash, clean, reset, checkout-overwrite, or line-ending normalization is used.
- The branch point is the latest approved default-branch commit containing the verified baseline; any difference from the inspected `06a28f6` state is reconciled into the entry-state record before tests run.
- No unresolved owner choice exists. A newly discovered material choice stops the phase with `PLAN_REVISION_REQUIRED` rather than being inferred.

**Implementation scope**

- `BFS-PHASE-000`: Establish the traceability ledger and immutable source-asset fingerprint set.
- `BFS-PHASE-000`: Establish the baseline defect matrix and preserve raw evidence from clean, named environments.
- `BFS-PHASE-000`: Add only test and evidence-harness infrastructure required by this phase; keep it separate from shipping logic and generated product resources.
- `BFS-PHASE-000`: Define deterministic fixtures, fixed seeds, sample regions, controls, time bounds, tolerances, capture naming, log filters, and artifact inspections.
- `BFS-PHASE-000`: Execute enough of the harness to prove it can represent each later-phase acceptance gate.
- `BFS-PHASE-000`: Integrate the phase through the required GitHub pull-request and signed-tag workflow.

**Execution order**

1. `P000-TASK-001` for `BFS-PHASE-000` freezes repository identity, normalizes and verifies the required `main` integration branch from the approved legacy baseline without rewriting history, then records branch ancestry, supported versions, source-input hashes, and the protected dirty-worktree manifest.
2. `P000-TASK-002` for `BFS-PHASE-000` performs safe archive inspection and completes the source-asset-to-destination traceability ledger.
3. `P000-TASK-003` for `BFS-PHASE-000` maps every `BFS-REQ-001` through `BFS-REQ-023` statement and every `DEF-024-*` item to its primary phase, mandatory recheck, test level, and evidence owner.
4. `P000-TASK-004` for `BFS-PHASE-000` establishes advancement copy, graph, title, punctuation, and icon baselines for `DEF-024-001`, `DEF-024-005`, `DEF-024-006`, and `DEF-024-007`.
5. `P000-TASK-005` for `BFS-PHASE-000` establishes structural and runtime animation baselines for `DEF-024-002` and `DEF-024-003`.
6. `P000-TASK-006` for `BFS-PHASE-000` establishes deterministic and runtime mobility, targeting, attack, and recovery baselines for `DEF-024-004`.
7. `P000-TASK-007` for `BFS-PHASE-000` establishes registration, data, rendering, placement, and fixed-seed absence controls for `DEF-024-008`.
8. `P000-TASK-008` for `BFS-PHASE-000` adds the deterministic JUnit audit layer for policies, JSON topology and copy, animation sampling, image dimensions and bytes, registrations, tags, loot, and feature data.
9. `P000-TASK-009` for `BFS-PHASE-000` adds the smallest Forge GameTest harness and fixture contract needed by later phases, then proves discovery, execution, pass, and intentional-failure reporting.
10. `P000-TASK-010` for `BFS-PHASE-000` freezes runtime capture protocols for dedicated server, interactive client, multiplayer and reconnect, sustained population, fixed-seed world generation, and log review.
11. `P000-TASK-011` for `BFS-PHASE-000` runs the Phase 000 command matrix from the isolated phase branch, records results and limitations, and verifies the phase diff contains no product-behavior repair or protected user content.
12. `P000-TASK-012` for `BFS-PHASE-000` assembles the completion packet, synchronizes phase tracking, opens the Phase 000 pull request, waits for checks and independent review when supported, merges through GitHub, verifies the resulting default branch, and pushes the signed annotated phase tag.

**Required evidence**

- Repository and dirty-state manifests captured both before work and after integration, including hashes for every protected modified and untracked path.
- Archive member, CRC, size, type, normalized path, dimension, and SHA-256 manifests, plus exact source-to-destination mapping.
- A requirement and defect matrix with no unowned requirement, defect, input, runtime gate, artifact gate, or cross-phase recheck.
- Raw and summarized evidence for all eight defects. Each record names revision, environment, configuration, seed when applicable, exact steps, expected result, observed result, logs, and capture references.
- JUnit and GameTest harness proof, including an intentional-failure calibration that is removed before integration but whose output demonstrates that failures are surfaced.
- Dedicated-server, interactive-client, multiplayer, population, and artifact protocols proven executable or the phase remains open.
- Forge test and build results and a clean generated-resource comparison. Phase 000 does not regenerate and silently accept drift.
- Pull-request checks, review disposition, merge commit, default-branch verification, signed annotated tag verification, and downstream handoff.

**Exit criteria**

- Every `BFS-REQ-001` through `BFS-REQ-023` acceptance statement has a primary phase, a mandatory recheck where required, an evidence type, and an identified component or interface.
- Every `DEF-024-001` through `DEF-024-008` item is classified as reproduced or positively disproved under a valid control, and has a later-phase closing test that satisfies the master's minimum evidence. A blocked, skipped, source-only, or uncontrolled result fails this gate.
- Every member of both supplied archives and all 22 loose algae PNGs has a verified immutable source fingerprint and an owned destination interface or explicit evidence-only disposition.
- The deterministic and Forge GameTest harnesses are discoverable, repeatable, documented, and able to fail for the intended reason.
- Fixed seeds, fixtures, sample regions, capture protocols, log review, artifact inspection, and evidence invalidation rules are frozen for downstream phases.
- Original dirty worktree paths remain byte-for-byte and status-for-status unchanged except for separately authorized plan-authoring output. No protected source archive is committed.
- No unresolved intake decision or material owner choice remains.
- No product behavior, version, generated product data, or release claim was changed to make the baseline appear healthy.
- The Phase 000 pull request is merged through GitHub with required checks and review resolved, the resulting default-branch commit is verified, and a signed annotated Phase 000 tag points to that merge.
- No known mandatory phase-owned defect remains in the evidence harness itself.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| Current owner request | Owner, `SRC-001` | Complete `0.24` scope, retained `0.23` contract, mandatory beta closure, and consistency requirement | Compare locked intake and master requirement register through EOF | Stop with `PLAN_REVISION_REQUIRED` on a material mismatch. |
| `Content/BFS 0.23 Content.zip` | Owner, `SRC-002` | Exact hash, safe member names, intact supplied geometry, textures, item sprites, notes, and clips | SHA-256, CRC test, member-type and normalized-path audit, per-member hash and dimension inspection | Quarantine extraction, do not alter source, and stop because retained content provenance is broken. |
| `Content/BFS 0.24 Content.zip` | Owner, `SRC-003` | Exact hash, seven 16 by 16 icons, and binding note | Same safe archive protocol plus exact icon count and dimensions | Stop; no substitute art or inferred bytes are allowed. |
| `Content/Algae Textures/` | Owner, `SRC-004` | Exactly 22 inspected PNGs and preserved authored order, including repeated holds | Sorted file manifest, dimensions, per-file SHA-256, strip-to-frame pixel comparison | Stop on missing, additional, reordered, corrupt, or inconsistent source evidence. |
| Beta reports and screenshot | Owner, `SRC-005` and `SRC-006` | Reports remain mandatory; screenshot visibly captures the truncated captain description | Hash and inspect attachment; bind it to `DEF-024-001` | Stop the affected reproduction if the evidence cannot be authenticated; do not replace it with a recollection. |
| Retained 0.23 contract | Master and prior plan, `SRC-007` | All requirements remain active unless explicitly superseded | Complete semantic mapping into Phase 001 and Phase 005 gates | Stop on an unowned or contradictory retained requirement. |
| Repository at inspected revision | `SRC-008` | Source, generated output, test, manifest, and tool reality recorded without treating it as scope authority | Revision, tree, manifest, provider, generated-output, and build-task inventory | Rebaseline if the revision changes; never merge evidence across revisions silently. |
| Resolved decisions | `DEC-001` through `DEC-003` | Permanent algae, retired duplicate node, and punctuation convention remain fixed | Compare decision register to every task and test oracle | Stop on contradictory implementation or a new material decision. |
| Protected dirty state | Original worktree owner | Pre-existing modified and untracked bytes remain untouched | Before-and-after porcelain status, file hashes, binary diff, and untracked manifest | Abort the offending operation; restore only phase-owned files from the isolated worktree. Never reset, clean, or overwrite owner files. |

## Source Asset and Destination Traceability

The ledger below is normative for evidence routing. Existing paths are exact repository evidence. For algae destinations that do not yet exist, the ledger names the required Phase 004 resource or registration interface rather than inventing a path.

| Source asset or group | Verified source state | Destination or evidence disposition | Owning phase | Required trace proof |
|---|---|---|---|---|
| `BFS 0.23 Content/Atlantic Cod/atlantic_cod.animation.json` | Archive member | `common/src/main/resources/assets/bensfintasticsharks/animations/entity/atlantic_cod.animation.json` | `BFS-PHASE-001`, motion repair in `BFS-PHASE-002` | Member and destination hashes, clip-name inventory, sampled transform comparison, packaged path. |
| `BFS 0.23 Content/Atlantic Cod/atlantic_cod.geo.json` | Archive member with supplied generic geometry identifier | `common/src/main/resources/assets/bensfintasticsharks/geo/entity/atlantic_cod.geo.json`, retaining the intentional `geometry.atlantic_cod` identifier correction | `BFS-PHASE-001` | Structural comparison excluding only the documented identifier correction, bone inventory, packaged path. |
| `BFS 0.23 Content/Atlantic Cod/atlantic_cod.png` | Archive PNG | `common/src/main/resources/assets/bensfintasticsharks/textures/entity/atlantic_cod.png` | `BFS-PHASE-001` | Byte equality and packaged-path check. |
| `BFS 0.23 Content/Atlatnic Salmon/atlantic_salmon.animation.json` | Archive member; misspelled source directory is evidence only | `common/src/main/resources/assets/bensfintasticsharks/animations/entity/atlantic_salmon.animation.json` | `BFS-PHASE-001`, motion repair in `BFS-PHASE-002` | Member and destination hashes, clip inventory, sampled transform comparison, packaged path. |
| `BFS 0.23 Content/Atlatnic Salmon/atlantic_salmon.geo.json` | Archive member | `common/src/main/resources/assets/bensfintasticsharks/geo/entity/atlantic_salmon.geo.json` | `BFS-PHASE-001` | Byte or normalized-structure equality, bone inventory, packaged path. |
| `BFS 0.23 Content/Atlatnic Salmon/atlantic_salmon.png` | Archive PNG | `common/src/main/resources/assets/bensfintasticsharks/textures/entity/atlantic_salmon.png` | `BFS-PHASE-001` | Byte equality and packaged-path check. |
| `BFS 0.23 Content/Oceanic Whitetip Animations New/oceanicwhitetipshark.animation.json` | Archive member | `common/src/main/resources/assets/bensfintasticsharks/animations/entity/oceanic_whitetip_shark.animation.json` | `BFS-PHASE-001`, completeness repair in `BFS-PHASE-002` | Clip-name mapping, member and destination comparison, transform variance, controller reachability, packaged path. |
| Six 0.23 item sprites | Archive PNGs for two spawn eggs and four foods | Exact item texture paths under `common/src/main/resources/assets/bensfintasticsharks/textures/item/` named `atlantic_cod_spawn_egg.png`, `atlantic_salmon_spawn_egg.png`, `raw_atlantic_cod.png`, `cooked_atlantic_cod.png`, `raw_atlantic_salmon.png`, and `cooked_atlantic_salmon.png` | `BFS-PHASE-001` | One-to-one byte, dimension, model reference, registration, and packaged-path checks. |
| `BFS 0.23 Content/BFS 0.23 Notes for Coder.txt` | Archive member | Evidence-only requirement source for Spin, fishing, replacement, fish review, and four fish advancements | `BFS-PHASE-000` traceability, execution in `BFS-PHASE-001` | Text hash and mapping to `BFS-REQ-008` through `BFS-REQ-012`. |
| `Awkward Advancement Icon 16x.png` | Supplied 16 by 16 PNG | Existing hidden item texture `common/src/main/resources/assets/bensfintasticsharks/textures/item/harbor_seal_block.png`, model `common/src/main/resources/assets/bensfintasticsharks/models/item/harbor_seal_block.json`, display `harbor_seal_encounter` | `BFS-PHASE-003` | Source hash `e5e531f62b458fea3ab50b1e61abc4504c75453fa7cd06c8640aabf794b0abd1`, byte equality, model edge, generated advancement, title, client capture, JAR. |
| `Its A Shiny Advancement Icon 16x.png` | Supplied 16 by 16 PNG | Existing hidden item texture and generated model named `albino`, display `albino_encounter` | `BFS-PHASE-003` | Source hash `e0638a22ee40480f03a7e4361ff324e4a8d9b1cb93590a2ec695b2830f79ab76` and the same end-to-end checks. |
| `Sharks Galore Advancement Icon 16x.png` | Supplied 16 by 16 PNG | Existing hidden item texture and generated model named `sharks_galore`, display `sharks_galore` | `BFS-PHASE-003` | Source hash `11f66a72567bb8ae16aa2022ca21778d43eb0d0fcbb531b21d34e03fa4e020f6` and the same end-to-end checks. |
| `Sleeping With The Fishes Advancement Icon 16x.png` | Supplied 16 by 16 PNG | Existing hidden item texture and generated model named `sleeping_with_the_fishes`, display `sleeping_with_the_fishes` | `BFS-PHASE-003` | Source hash `6c01085423275d602fbad91924a8772249c1a8dbeee749f5e1e9907df0995977` and the same end-to-end checks. |
| `Specimen8 Advancement Icon 16x.png` | Supplied 16 by 16 PNG | Existing hidden item texture and generated model named `specimen_8`, display `specimen_8_encounter` | `BFS-PHASE-003` | Source hash `88f6dc345a7c399034142cf2bebc54fdd488e41b9f36f19d470806d393c1fc65` and the same end-to-end checks. |
| `Mommy Shark Advancement 16x.png` | Supplied 16 by 16 PNG | Existing hidden item texture and generated model named `mommy_shark`, display `deep_blue_encounter` | `BFS-PHASE-003` | Source hash `58fb46c5267ea9b6e027fdaf4532ad2d76de7dcf75aa046367f3720f18c00cde` and the same end-to-end checks. |
| `Thunder Bringer Advancement Icon 16x.png` | Supplied 16 by 16 PNG | Existing hidden item texture and generated model named `zippy_pixel_art`, display `zippy_encounter` | `BFS-PHASE-003` | Source hash `4f54793625dc71ab456ca58de55b6bfe015f586c81930d11b72d5a3d6942595d` and the same end-to-end checks. |
| `bfscontent0.24.txt` | Archive member | Evidence-only requirement source for icon mapping and advancement retirement | `BFS-PHASE-000` traceability, execution in `BFS-PHASE-003` and `BFS-PHASE-004` | Text hash and mapping to `BFS-REQ-013`, `BFS-REQ-014`, and `BFS-REQ-021`. |
| `Content/Algae Textures/algea_block.png` | Loose 16 by 16 PNG | Phase 004 block texture interface for public ID `bensfintasticsharks:algae_block`; source misspelling is not propagated | `BFS-PHASE-004` | Source hash, exact pixel preservation, model and blockstate edge, registry, client capture, JAR. |
| `Content/Algae Textures/biggreenalgea_strip.png` and `greenbigalgea_1.png` through `greenbigalgea_10.png` | 16 by 160 strip and ten ordered 16 by 16 evidence frames | Phase 004 animated texture and metadata interfaces for `bensfintasticsharks:large_green_algae`; numbered frames remain evidence-only | `BFS-PHASE-004` | Per-file hashes, strip segmentation equality, repeated-frame preservation, order, metadata cadence, atlas log, client capture, JAR. |
| `Content/Algae Textures/bigredalgea_strip.png` and `bigredalgea_1.png` through `bigredalgea_9.png` | 16 by 144 strip and nine ordered 16 by 16 evidence frames | Phase 004 animated texture and metadata interfaces for `bensfintasticsharks:large_red_algae`; numbered frames remain evidence-only | `BFS-PHASE-004` | Per-file hashes, strip segmentation equality, repeated-frame preservation, order, metadata cadence, atlas log, client capture, JAR. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| Immutable input manifest | All later phases | Archive, algae, screenshot, repository revision, and protected-worktree inputs are individually fingerprinted and safely classified. | Any input-byte or revision change forces explicit rebaseline; evidence is never silently reused. | Machine-readable manifest attached to the Phase 000 completion packet and reviewed human-readable ledger. |
| Requirement ownership matrix | `BFS-PHASE-001` through `BFS-PHASE-005` | Every requirement statement has one primary execution phase and the master-required final recheck. | Stable IDs remain unchanged. An authorized master revision invalidates affected rows. | Completeness audit with zero missing or duplicate-primary rows. |
| Defect baseline matrix | Phases 002, 003, 004, and 005 | Every `DEF-024-*` has steps, controls, oracle, raw evidence, status, and closing-test owner. | Baseline evidence names exact revision and environment. It is historical after an affected change, not current proof. | Eight reviewed records and capture links. |
| Deterministic audit harness | All implementation phases | Policy, graph, copy, animation, image, registration, generated-data, and artifact assertions are independently runnable and fail closed. | Tests target Forge `1.20.1` and pinned dependencies; platform upgrades are prohibited. | `./gradlew :forge:test` results plus calibration-failure evidence. |
| Forge GameTest harness contract | Phases 001, 002, 004, and 005 | Test registration, structures, fixtures, timeout rules, seed control, pass/fail reporting, cleanup, and rerun commands are defined and proven. | New fixtures may be added later; discovery and reporting contracts remain stable. | GameTest discovery and calibration results, then a clean passing harness run. |
| Runtime evidence protocol | All later phases | Dedicated server, client, multiplayer, reconnect, population, advancement, and world-generation captures use consistent metadata and log review. | A changed runtime, config, data pack, mod list, seed, or build artifact invalidates the affected capture. | Completed baseline run sheets and raw logs or media references. |
| Artifact inspection protocol | Phases 003, 004, and 005 | Final JAR inventory, embedded version, resource bytes, duplicate paths, checksums, SBOM, and attestation support have explicit checks. | Baseline artifact is not a release candidate; final evidence must be generated from the merged release revision. | Baseline JAR inventory and deterministic inspection command record. |
| Dirty-worktree protection record | Owner and all later phases | Original modified and untracked bytes remain intact and excluded from the phase diff and commits. | Protection lasts until the owner changes or authorizes those paths. | Before-and-after status, binary diff, hashes, and commit-tree inspection. |
| Phase integration record | `BFS-PHASE-001` | Phase 000 is merged, present on the default branch, and signed-tagged. | Phase branches are sequential and never stacked. | PR, checks, review disposition, merge commit, default-branch containment, signed tag verification. |

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| `P000-TASK-001` | Prerequisite evidence for `BFS-REQ-022` and `BFS-REQ-023` | Freeze repository, baseline, environment, archive, screenshot, and dirty-state identity before execution. Establish and verify `origin/main` from the approved legacy `origin/1.20.1` history without rewriting it, then create an isolated phase worktree from `main` without stashing or touching the original worktree. | `SRC-001` through `SRC-008`; no task dependency | Entry manifest and protected-path fingerprint set | Git repository identity, integration branch, original worktree, phase branch, manifests | Repeat all identity and hash commands; verify old and new branch ancestry, authoritative branch state, and original status and bytes. |
| `P000-TASK-002` | `BFS-REQ-022`, `BFS-REQ-013`, `BFS-REQ-017`, `BFS-REQ-018`, `BFS-REQ-020` | Audit archive safety and integrity, hash every member and loose PNG, inspect dimensions, compare strip segmentation to numbered frames, and complete source-to-destination traceability. | `P000-TASK-001`, `SRC-002` through `SRC-004` | Immutable asset manifest and discrepancy list | Supplied archives, loose algae drop, existing resource and generated-resource interfaces | CRC and path-safety pass, exact counts, source hashes, destination mapping, and no unexplained archive member. |
| `P000-TASK-003` | `BFS-REQ-001` through `BFS-REQ-023` | Decompose every master acceptance statement into primary phase, final recheck, test level, runtime environment, negative case, and evidence owner. | `P000-TASK-001`; complete master | Requirement ownership and evidence matrix | Master stable IDs and phase interfaces | Automated or reviewed completeness check reports every stable requirement exactly once as primary and every mandatory recheck present. |
| `P000-TASK-004` | `BFS-REQ-013` through `BFS-REQ-016`, `DEF-024-001`, `DEF-024-005`, `DEF-024-006`, `DEF-024-007` | Capture current provider and generated copy, advancement DAG, hidden-item model edges, icon bytes and dimensions, fresh-profile tree, and tooltip or display screenshots. | `P000-TASK-002`, current generated resources, `SRC-006` | Four defect baseline records and graph snapshot | `BensFintasticSharksAdvancements`, `ModLangProvider`, `ModItemModelProvider`, hidden advancement items, generated language and advancements, item textures | Deterministic graph and copy audit plus interactive fresh-profile display review. Verify controls can detect a deliberately altered fixture. |
| `P000-TASK-005` | `BFS-REQ-007`, `BFS-REQ-017`, `BFS-REQ-018`, `DEF-024-002`, `DEF-024-003` | Inventory clips, bones, controller references, state priorities, and transitions; sample start, quarter, half, three-quarter, and end transforms; capture each required state in an interactive client and record bite-damage timing. | `P000-TASK-002`, `tools/bake_molang_animations.py`, entity animation controllers and resources | Fish and Oceanic structural and runtime baselines | Cod, Salmon, and Oceanic Whitetip animation resources, geometry, Forge controllers, client runtime | At least one moving bone per required clip is sampled to `0.0001`; runtime capture proves state activation. Lack of motion is a reproduced defect, not a harness failure. |
| `P000-TASK-006` | `BFS-REQ-001`, `BFS-REQ-003`, `BFS-REQ-019`, `DEF-024-004` | Define controlled underwater fixtures for idle travel, horizontal and vertical pursuit, stationary and moving prey, physical contact, one-damage-window accounting, target loss, target death, and curiosity termination. Capture deterministic and interactive baseline results. | `P000-TASK-001`, current Tiger entity and Forge animation controller | Tiger Shark baseline record and later closing-test contract | `TigerSharkEntity`, shared shark movement and targeting interfaces, Forge controller, server-authoritative damage | Fixed starting state, bounded ticks, position deltas, target state, damage events, cooldown, and post-condition assertions plus client pursuit evidence. |
| `P000-TASK-007` | `BFS-REQ-020`, `BFS-REQ-021`, `DEF-024-008` | Prove the current absence of algae registration and generation, then define the later registry, data-generation, survival, loot, rendering, fixed-seed, density, invalid-support, land-rejection, and JAR tests. | `P000-TASK-002`, existing registration, datagen, and world-generation interfaces | Algae absence baseline and Phase 004 test contract | Common registries, Forge `ModFeatures`, datagen providers, biome tags, generated resources, server and client | Current registry and fixed-seed controls return no BFS algae while detecting vanilla seagrass. Later oracle requires all three forms, invalid-placement rejection, and combined count below seagrass in the same eligible sample. |
| `P000-TASK-008` | `BFS-REQ-001` through `BFS-REQ-023` | Implement the test-only deterministic audit layer in the existing Forge JUnit source set. Preserve and expand the fish replacement policy test and add data-oriented validators without loading client classes on server or unit paths. | `P000-TASK-003` through `P000-TASK-007`; JUnit `5.10.2` | Repeatable unit and static audit suite | Forge test source set rooted at `forge/src/test/java`; JSON, PNG, animation, registry, and policy validator interfaces | `./gradlew :forge:test`; each validator receives a known-bad fixture during calibration and must fail for the expected reason. Remove intentional failing fixtures before commit. |
| `P000-TASK-009` | `BFS-REQ-002` through `BFS-REQ-004`, `BFS-REQ-008`, `BFS-REQ-017` through `BFS-REQ-021` | Add the smallest Forge GameTest run, registration, fixture, structure, timeout, cleanup, and reporting infrastructure. Add one smoke fixture that proves server-side discovery and deterministic completion without encoding future fixes. | `P000-TASK-003`, current ForgeGradle run configuration | Executable GameTest harness contract | `forge/build.gradle`, Forge test namespace and structure-resource interfaces, common server-safe test seams | GameTest task discovers the smoke test, passes repeatably, and reports an intentional calibration failure. Dedicated-server logs contain no client class-loading error. |
| `P000-TASK-010` | `BFS-REQ-022` | Freeze run sheets and evidence metadata for server, client, multiplayer, reconnect, population, advancement, and fixed-seed generation. Use world seed `240024` and Overworld chunk sample `0,0` through `15,15` for the shared algae-versus-seagrass density protocol unless an authorized plan revision changes it. | `P000-TASK-003` through `P000-TASK-009` | Runtime matrix, capture schema, log filters, fixed seed, and sample bounds | `:forge:Server`, `:forge:Client`, multiplayer sessions, generated world, advancement profile, final JAR | A second maintainer can repeat each run sheet from the named revision and artifact. Capture metadata includes config, mod list, seed, coordinates, players, duration, and log path. |
| `P000-TASK-011` | `BFS-REQ-022` | Run ordered Phase 000 checks, classify failures without hiding them, inspect generated and final diffs, and prove only harness, test, and evidence-documentation changes are present. | `P000-TASK-008` through `P000-TASK-010` | Verification results and scoped diff | Formatter or absence record, static analysis or absence record, Forge tests, GameTests, Forge build, server smoke, generated resources, JAR inventory | Required commands complete in order. Unsupported root Fabric failure is recorded separately and never substitutes for Forge success. |
| `P000-TASK-012` | `BFS-REQ-022` | Assemble evidence, synchronize phase tracking, integrate through a merge-commit pull request, verify default branch, and create the signed annotated phase tag. | All prior tasks and exit gates | Complete Phase 000 packet and Phase 001 entry authorization | GitHub issue, milestone, Project, pull request, checks, review, merge, default branch, tag | Required checks pass, conversations resolve, private independent review is addressed when supported, GitHub reports merged, remote default branch contains merge, and signature verification succeeds. |

### Ordering, Parallelism, and Recovery

`P000-TASK-001` is a hard prerequisite for all other work. `P000-TASK-002` and `P000-TASK-003` may proceed in parallel after the protected-state snapshot. `P000-TASK-004` through `P000-TASK-007` may proceed in parallel only after their asset and requirement inputs are frozen. `P000-TASK-008` and `P000-TASK-009` may then proceed in parallel, but both must pass before runtime protocols are calibrated in `P000-TASK-010`. Verification and integration are strictly sequential.

If a task mutates or stages a protected path, stop immediately. Discard only phase-owned changes in the isolated phase worktree, reconstruct it from the approved branch point, and repeat the affected task. Never use `git reset --hard`, `git clean`, a broad checkout, or a stash against the original worktree. If evidence is tied to the wrong revision or artifact, mark it invalid rather than relabeling it.

## Architecture and Implementation Boundaries

### Harness Layers

1. **Pure deterministic audits.** Keep string mapping, DAG validation, localization rules, PNG metadata and bytes, JSON reference checks, and animation transform sampling free of client startup. The existing Forge JUnit source set is the home for Java tests. `tools/bake_molang_animations.py` is the current deterministic animation transformation interface and must be sampled rather than trusted by file existence.
2. **Forge GameTests.** Use server-safe fixtures for navigation, pursuit, damage, release cleanup, fish passivity, aquatic placement, survival, and feature placement. GameTests own world state and bounded tick assertions. They do not assert pixels or client-only animation rendering.
3. **Dedicated-server smoke.** Validate common initialization, registry construction, tags, data packs, loot, recipes, advancements, animation references used on shared paths, and absence of client class loading. A ready-state log is mandatory.
4. **Interactive client review.** Validate visual animation, icon presentation, tooltip copy, armor fit, render layers, texture animation, and generated-ocean appearance. Headless startup is only a loading smoke check.
5. **Multiplayer and reconnect review.** Validate server authority, synchronization, release cleanup, death, disconnect, reconnect, dimension change, passenger state, camera state, and equipment refresh.
6. **Artifact inspection.** Inspect the actual Forge JAR produced by the tested revision. Source-tree presence is not packaged-resource proof.

### State and Data Flow

- Supplied archives and loose algae files are immutable inputs. Extraction, if needed, occurs only in a fresh temporary directory. Tests consume hashes or temporary copies and never modify `Content/`.
- Datagen providers are authoritative for generated language, models, advancements, loot, recipes, and tags. A generated JSON edit without a matching provider change is drift, not a fix.
- Common and server test seams must not import `net.minecraft.client` classes. Client capture may observe synchronized state but may not become the authority for attacks, grabs, population, or world generation.
- Runtime evidence binds source revision, built JAR hash, configuration, data packs, seed, world coordinates, test actors, timestamps, and logs. Evidence from different bindings cannot be combined into one passing result.
- Each defect record has exactly one current baseline status: `REPRODUCED`, `POSITIVELY_DISPROVED`, or `BLOCKED`. `POSITIVELY_DISPROVED` requires a positive control proving the path ran and the oracle could detect the reported fault. `BLOCKED` prevents phase completion.
- Baseline reproduction does not close a defect. Closure occurs only in the owning implementation phase with the master's minimum evidence and is rechecked in `BFS-PHASE-005`.

### Determinism and Performance Limits

- Animation samples use normalized start, quarter, half, three-quarter, and end times and compare transform vectors with tolerance `0.0001`.
- Entity fixtures use fixed positions, known water volumes, controlled targets, bounded tick budgets, explicit damage-event counts, and cleanup postconditions. A timeout is a test failure, not an implicit pass.
- World-generation evidence uses seed `240024`, Overworld chunks `0,0` through `15,15`, unchanged relevant configuration and data packs, and identical count rules for BFS algae and vanilla seagrass. Later Phase 004 may add more seeds, but must retain this shared sample.
- Sustained population evidence records elapsed ticks, loaded chunks, players, configuration, entity categories, per-type counts, and maximum observed counts. It must be long enough to exercise repeated natural-spawn cycles as documented in the run sheet.
- Captures must avoid secrets, unrelated personal data, and absolute local paths in tracked artifacts. Raw local logs are sanitized before attachment.

## Defect Baseline and Closing-Test Contract

| Defect ID | Baseline procedure | Positive control and oracle | Phase 000 output | Closing owner and mandatory later evidence |
|---|---|---|---|---|
| `DEF-024-001` | Open `captains_heir` on a fresh profile and compare the tooltip with the provider and generated language. Bind the supplied screenshot as prior evidence. | Confirm another changed localization key appears after resource reload, proving the displayed pack and profile are current. Exact required text is `Obtain Capitán Ben's Hat.`. | Screenshot, generated/provider values, revision and pack metadata, status | `BFS-PHASE-003`: exact provider, generated text, and fresh in-game tooltip screenshot. |
| `DEF-024-002` | Spawn Cod and Salmon in controlled idle, ordinary-swim, fast-swim, flop, and exact-name `Spin` states; sample every clip structurally and capture movement. | Flop and Spin are known moving controls. Structural oracle requires two distinct vectors on at least one authored moving bone within `0.0001`. | Per-clip sample table, state activation log, client capture, status | `BFS-PHASE-002`: transform equivalence and in-game evidence for idle, swim, fast swim, flop, and Spin. |
| `DEF-024-003` | Drive Oceanic Whitetip through idle, `swim_new`, `swim_fast_new`, `bite_new`, death, beached, and thrash; record controller selection, bone variance, transitions, damage tick, and cleanup. | Trigger a known working clip and record it through the same controller and capture path. Each required state must be reachable and visually distinct where authored. | State-transition and transform tables, damage timeline, client capture, status | `BFS-PHASE-002`: state matrix, sampling, visual evidence, and server damage timing. |
| `DEF-024-004` | Place a Tiger Shark and supported prey in fixed horizontal and vertical underwater fixtures; record position, path, target, contact, damage count, cooldown, and recovery after loss or death. Repeat with a floating edible item to detect curiosity starvation. | Run the same position and damage recorder against a predator fixture known to navigate and attack, proving the world and oracle permit movement and damage. | Deterministic trace, GameTest result, interactive pursuit capture, status | `BFS-PHASE-002`: deterministic target and damage test plus in-game pursuit evidence. |
| `DEF-024-005` | Generate data, enumerate the advancement DAG, prove `shark_whisperer` exists, enumerate every child edge including `marine_biologist` and `apex_of_apex`, and load a fresh profile. | The graph auditor must accept the current root-connected nodes and reject a fixture with a missing parent or cycle. | Node and edge manifest, orphan/cycle audit, fresh-profile capture, status | `BFS-PHASE-003`: no retired node or dedicated resources, connected generated graph, and fresh-profile completion path. Every surviving former child must have a valid parent. |
| `DEF-024-006` | Compare every generated English title and description with provider output and the master punctuation rules. Record trailing whitespace, terminal punctuation, apostrophe mixture, title mismatch, and doubled-mark findings. | Feed one known-bad title and description fixture to every rule and prove exact diagnostics. Supplied seven titles are exact exceptions, not normalized inputs. | Full key audit and visual tree review, status | `BFS-PHASE-003`: deterministic copy audit and visual advancement review. |
| `DEF-024-007` | Compare all seven supplied icon hashes and dimensions to their hidden-item destination textures, generated models, advancement displays, and rendered tree. | Verify the checker accepts one exact temporary source/destination copy and rejects a dimension or byte mismatch. | Seven-row source-to-display chain and screenshots, status | `BFS-PHASE-003`: exact bytes, 16 by 16 dimensions, model and display edges, client review, and packaged-resource inspection. |
| `DEF-024-008` | Enumerate current block/item registries and generated worldgen data; search fixed-seed sample for the three public IDs; attempt place, survive, remove, collect, render, and locate natural generation. | Detect vanilla seagrass in the same registry, placement, survival, and fixed-seed scan so zero BFS algae is meaningful rather than a broken scan. | Absence matrix, vanilla controls, fixed-seed counts, status | `BFS-PHASE-004`: registry, datagen, GameTests, fixed-seed density, survival, loot, rendering, server, and JAR evidence. |

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| Archive path traversal, absolute member, link, duplicate normalized path, or CRC failure | Safe archive audit | Do not extract or consume the archive; keep source untouched and block the phase. | Obtain a corrected owner-supplied archive or authorized plan revision. | Re-run archive safety, hash, and member manifest from scratch. |
| Protected dirty path changes | Before-and-after hash or status mismatch | Stop all integration. Do not stage, commit, reset, clean, or overwrite the path. | Remove only phase-owned isolated-worktree changes and reconstruct the isolated worktree if needed. Owner restores or authorizes any original-worktree change. | Exact status, binary diff, and hashes equal pre-state. |
| Branch or source revision changes during capture | Revision, tree, or JAR hash mismatch | Mark affected evidence stale and do not merge it with old results. | Rebuild and rerun from one named revision. | All record bindings agree. |
| Generated resource differs from provider output | `:forge:Data` diff | Treat as provider/generated drift and block affected evidence. Do not hand-edit generated JSON. | Repair only harness-caused drift in phase scope or route product drift to its owning phase. | Clean regeneration and complete generated diff review. |
| Reported defect cannot be reproduced | Expected failure absent | Require a positive control, exact environment, and repeated run. Do not mark closed. | Classify `POSITIVELY_DISPROVED` only if the path and oracle are proven; otherwise `BLOCKED`. | Repeated run and control output. |
| Test passes without exercising target state | Missing state marker, criterion, event, or control | Fail the test as invalid. | Strengthen fixture and explicit precondition assertion. | Known-bad calibration fails for the target reason. |
| GameTest hangs or leaks world state | Timeout, retained entity, passenger, ticket, or fixture state | Fail closed and clean fixture-owned state. | Bound ticks, use teardown assertions, restart test server if isolation is compromised. | Same test passes twice in independent runs with clean postconditions. |
| Headless client reaches title screen but visual evidence is missing | No interactive capture | Record only loading smoke success; keep visual gate open. | Run an interactive client on a graphics-capable environment. | Capture required state, UI, or texture with revision and artifact metadata. |
| No audio device | OpenAL warning with otherwise healthy client load | May be accepted only for headless loading smoke. | Use interactive environment for visual and interaction gates; audio is not substituted. | Logs distinguish accepted audio warning from rendering or data failure. |
| Root `./gradlew build` fails in Fabric stub | Missing Fabric SmartBrainLib path | Record known unsupported-stub result; do not treat it as Forge failure or support Fabric. | Continue only if `./gradlew :forge:build` passes and no Phase 000 change worsened root behavior. | Before-and-after failure signature comparison and passing Forge build. |
| Fixed-seed sample contains no vanilla seagrass | Control count zero | Invalidate the sample as a density oracle. | Validate world setup and choose an additional documented eligible sample without replacing seed `240024` evidence. | Positive vanilla control and repeatable counts. |
| Screenshot, log, or video lacks revision or artifact binding | Metadata absent | Evidence is inadmissible. | Reproduce with complete metadata. | Packet validation finds no unbound capture. |
| Sensitive or unrelated content appears in raw logs | Review before attachment | Do not publish raw content. Preserve technical lines while redacting secrets and unrelated private data. | Re-run with narrower logging when redaction would remove decisive evidence. | Sanitized attachment and reviewer confirmation. |
| New major defect appears during baseline | Reproducible crash, data failure, state leak, runaway population, dead end, missing mandatory asset, or generation flood | Add it to the phase evidence packet and synchronize a deduplicated tracking issue without changing the protected plan. If scope or an owner decision changes, stop with `PLAN_REVISION_REQUIRED`. | Route in-scope repair to the owning current or final phase. | Reproduction and closing acceptance criteria are traceable. |

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Evidence artifact |
|---|---|---|---|---|---|
| `P000-TASK-001` | Hash and porcelain manifest validation | Isolated phase-worktree ancestry check | Original worktree reinspection | Deliberate mismatch in a copied manifest is detected | Entry and exit identity manifests. |
| `P000-TASK-002` | Archive and PNG validators | Source-to-destination edge audit | None; source assets are not executed here | Unsafe-path and wrong-dimension calibration fixtures fail | Asset manifest and discrepancy report. |
| `P000-TASK-003` | Stable-ID completeness and unique-primary audit | Cross-phase dependency and mandatory-recheck review | None | Missing, duplicate, or unknown stable ID fails | Requirement ownership matrix. |
| `DEF-024-001` | Exact provider/generated copy comparison | Datagen output check | Fresh-profile tooltip | Stale-pack control and exact-text mismatch | Copy record and screenshot. |
| `DEF-024-002` | Clip, bone, reference, and transform sampling | Controller-state activation trace | Interactive fish capture | Static transform and unreachable-state controls | Sample table, logs, and media. |
| `DEF-024-003` | Clip, bone, priority, and transform sampling | Controller and damage-timeline trace | Interactive Oceanic state capture | Missing bone, fallthrough, and stale thrash checks | Transition matrix, timeline, and media. |
| `DEF-024-004` | Target policy and event-count assertions | GameTest pursuit, contact, cooldown, and recovery | Interactive pursuit and curiosity capture | Invalid target, target death, timeout, and item invalidation | Deterministic trace, GameTest result, and media. |
| `DEF-024-005` | DAG, orphan, cycle, duplicate-criteria, and retired-ID audit | Generated resource load | Fresh-profile completion-path inspection | Missing-parent and cycle fixtures | Node-edge manifest and profile capture. |
| `DEF-024-006` | Full English key and punctuation audit | Provider-to-generated consistency | Visual advancement review | Known-bad whitespace, doubled-mark, and apostrophe fixtures | Audit report and screenshots. |
| `DEF-024-007` | Seven source/destination byte and dimension checks | Model, item, and advancement display reference chain | Interactive advancement display review | Wrong-byte, wrong-dimension, missing-model checks | Seven-row icon report and screenshots. |
| `DEF-024-008` | Registry and generated-data absence audit | Fixed-seed scan with vanilla control | Placement attempts and client observation | Land, invalid support, collision, missing control | Absence and control matrix. |
| `P000-TASK-008` | `./gradlew :forge:test` | Datagen/resource readers operate against current tree | Not applicable; deterministic layer is headless | Intentional calibration failures produce precise messages | JUnit report and command transcript. |
| `P000-TASK-009` | GameTest registration inspection | Forge GameTest smoke and fixture cleanup | Dedicated test-server execution | Intentional failure and timeout reporting | GameTest report and server log. |
| `P000-TASK-010` | Run-sheet schema validation | Artifact and environment binding checks | Server, client, multiplayer, population, and world sample dry runs | Missing metadata invalidates record | Completed baseline run sheets. |
| `P000-TASK-011` | Formatter and static-analysis presence audit | Forge test, GameTest, data, and build | Server ready state and required baseline client checks | Fabric stub comparison and log-error scan | Ordered verification record and JAR inventory. |
| `P000-TASK-012` | Diff, signature config, and packet completeness | Required GitHub checks and review | Merge and remote default-branch containment | Failed check, unresolved thread, missing signature, or non-merge integration blocks transition | PR, merge commit, signed tag, and handoff. |

### Fixture, Environment, and Rerun Rules

- JUnit fixtures must be minimal copies or synthetic invalid samples. They must not rewrite authoritative assets. Every intentional failing fixture is absent from the integrated test result unless it is encoded as an assertion that expects rejection.
- GameTest structures must contain only the blocks and entities necessary for the target contract. Each fixture documents origin, dimensions, water volume, floor, target positions, tick budget, and cleanup state.
- Client evidence uses a fresh profile where advancement persistence matters. Server and multiplayer evidence uses a fresh disposable world unless save compatibility is the target.
- Baseline runs use default relevant configuration plus one named negative configuration where required. Configuration files are attached or hashed in the packet.
- Rerun order after any affected change is: deterministic test, relevant GameTest, Forge build, dedicated server, relevant client flow, relevant multiplayer flow, then artifact inspection. A lower-fidelity rerun never substitutes for an invalidated higher-fidelity result.
- Required Phase 000 command order is formatter or documented absence, static analysis or documented absence, `./gradlew :forge:test`, data-generation comparison if harness work touches data or resources, Forge GameTests, `./gradlew :forge:build`, `./gradlew :forge:Server`, required `./gradlew :forge:Client` baseline captures, relevant multiplayer checks, final JAR and diff inspection.

## Documentation, Operations, and Release

- Update the existing technical documentation entry points, `DOCUMENTATION.md` and `docs/README.md`, with the actual harness commands, evidence classes, fixed seed, sample region, runtime prerequisites, expected results, and known unsupported Fabric-stub limitation after the harness is verified.
- Update `README.md` only if contributor setup or verified test commands materially change. Do not advertise beta fixes, algae, remastered icons, or version `0.24` as implemented in this phase.
- Do not add a `0.24` release entry to `CHANGELOG.md`; Phase 005 owns verified release notes.
- Keep phase proof outside the protected plan set. Use ordinary tracked test documentation, pull-request checks, CI artifacts, sanitized logs, screenshots, videos, and GitHub tracking. Do not turn this blueprint or the master into a status diary.
- Record formatter and static-analysis task absence explicitly. Do not invent a passing task.
- Record the root Fabric-stub build limitation without broadening support or weakening the Forge gates.
- Do not publish a mod artifact or release. A Phase 000 JAR is evidence-only and must be clearly bound to its revision.
- Before integration, inspect the complete commit diff and tree for generated build output, run directories, caches, local configuration, logs, crash reports, test worlds, absolute paths, credentials, source archives, and unrelated changes.

## Risks and Evidence Invalidation

| Risk | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| User work is overwritten or normalized | Isolated phase worktree; no stash, clean, reset, or checkout-overwrite; hash pre-state | Status, hash, binary diff, and line-ending comparison | Stop and restore only through owner-approved recovery | Entire phase integration packet | Repeat all tasks from a verified protected pre-state. |
| Evidence mixes `eb13b74`, `06a28f6`, a phase commit, or another artifact | Bind every record to revision and JAR hash | Packet consistency validation | Rebuild and rerun on one revision | All mixed records | Rerun affected matrix from the chosen revision. |
| Archive content changes unnoticed | Master hashes and per-member fingerprints | Archive and asset manifest comparison | Stop and request authoritative input | All derived asset, animation, icon, and algae proof | Repeat `P000-TASK-002` and downstream dependent tasks. |
| Datagen masks source or generated drift | Provider-first authority and clean regeneration diff | `:forge:Data` comparison | Route product drift to owning phase; revert only phase-owned accidental output | Advancement, copy, model, tag, loot, and worldgen evidence | Regenerate, audit full diff, rerun affected tests and runtime loads. |
| GeckoLib file presence is mistaken for motion | Structural sampling and client review | Identical transform vectors or static render | Keep defect open for Phase 002 | Animation closure evidence | Rerun sampling, controller trace, and interactive state capture. |
| GameTest harness does not exercise real logic | Explicit preconditions, state markers, positive controls, intentional failure calibration | Pass with absent target event or state | Strengthen fixture and oracle | Affected deterministic and GameTest records | Repeat calibration and two clean runs. |
| Advancement deletion or reparenting orphans a surviving node | Complete DAG enumeration includes all children of `shark_whisperer` | Missing parent, disconnected node, cycle, or fresh-profile load issue | Keep Phase 003 defect open and require connected-parent repair | Graph and completion-path evidence | Regenerate, rerun DAG audit, load fresh profile, complete path. |
| Icon check validates only dimensions | Require exact source hash and byte equality through JAR | Correct size but hash mismatch | Keep `DEF-024-007` open | Icon asset evidence | Repeat source, destination, model, display, and JAR chain. |
| Algae fixed-seed density oracle lacks eligible ocean or vanilla control | Fixed coordinates plus seagrass control and biome record | Zero eligible blocks or zero control count | Diagnose world setup and add a documented eligible sample | Density and absence evidence | Repeat identical counts for BFS algae and seagrass. |
| Runtime capture is stale after code, config, data, dependency, or resource change | Evidence invalidation matrix and artifact binding | Hash or environment mismatch | Mark stale and rerun, never relabel | All affected server, client, multiplayer, or world evidence | Start at lowest affected layer and continue through artifact inspection. |
| Product behavior is accidentally changed in Phase 000 | Scope-limited diff review and source ownership check | Shipping source or generated product resource diff unrelated to harness | Remove phase-owned out-of-scope changes without touching user work | Verification and integration evidence | Repeat build, runtimes, diff, and packet review. |
| Required GitHub check or review is bypassed | Merge only through GitHub with merge commit and required gates | Check, conversation, review, merge-method, or ruleset mismatch | Fix or wait; never bypass or administratively override | Phase integration evidence | Re-run checks and verify final merged state. |
| Tag points to pre-merge or phase-branch commit | Verify remote default branch and target object before tagging | Tag target differs from GitHub merge commit | Do not push; recreate only the unpushed incorrect tag safely | Transition authorization | Verify signing, target, remote tag, and default-branch containment. |

## Phase Completion Packet

Phase 000 may close only when its packet contains all of the following:

1. The approved phase-branch commit list and complete scoped diff.
2. Repository identity, baseline ancestry, supported-version, archive, screenshot, algae, and protected-dirty-state manifests.
3. The complete asset traceability ledger, including hashes for every archive member, all 22 algae PNGs, and the seven icon source and destination chains.
4. The complete stable-requirement ownership matrix and cross-phase evidence matrix.
5. Eight defect records, one for each `DEF-024-001` through `DEF-024-008`, with revision, environment, steps, controls, expected result, observed result, status, raw evidence references, and later closing-test owner.
6. JUnit audit results, GameTest discovery and calibration proof, clean GameTest results, and exact rerun commands.
7. Dedicated-server ready-state evidence, required interactive-client baseline captures, multiplayer protocol dry-run evidence, sustained-population protocol, fixed-seed world-generation protocol, log review, and limitations.
8. Formatter and static-analysis results or explicit absence records, Forge test result, data-generation comparison, Forge build result, server result, client result, multiplayer result, final JAR inventory, and complete diff inspection.
9. Before-and-after proof that original `build.gradle`, `Content/`, and every other protected pre-existing path remain unchanged and uncommitted.
10. Updated existing test and technical documentation describing only verified harness behavior.
11. GitHub issue, milestone, and Project synchronization records without a plan or goal rewrite.
12. Pull-request URL and merge method, required-check results, resolved review threads, private independent review disposition when supported, GitHub merge commit, verified remote default-branch containment, and signed annotated Phase 000 tag with signature verification.
13. A downstream handoff naming `BFS-PHASE-001`, exact path `docs/general/phases/plan-phase-001.md`, its merged-and-tagged dependency, and its first unfinished mandatory task.

The packet is execution evidence and must live outside this protected phase blueprint. A packet field that is missing, stale, source-only where runtime proof is required, or bound to a different revision keeps the phase open.

## Next Transition

The only next transition is `BFS-PHASE-001`, retained 0.23 contract closure. After GitHub reports the Phase 000 pull request merged, fetch the remote, verify the resulting merge commit is contained by the authoritative default branch, create and push the signed annotated Phase 000 tag on that merge commit, and verify its signature and target. Then reread the master and `docs/general/phases/plan-phase-001.md` through EOF and begin its first unfinished mandatory task from the updated default branch.

Do not create or stack the Phase 001 branch while the Phase 000 pull request is open, queued, pending, failed, or merely approved. Do not treat a local merge, direct default-branch push, unsigned tag, unverified remote state, or partial completion packet as transition authority.
