# Phase 002 Execution Plan

> **Plan ID:** PLAN-PHASE-002
> **Phase ID:** BFS-PHASE-002
> **Owner:** Entity systems maintainer
> **Classification:** MANDATORY
> **Master plan:** [plan.md](../plan.md)
> **Phase sequence:** 002 of 005

## Purpose and Ownership

This phase repairs the 0.24 beta failures in authored entity motion and Tiger Shark predation. It canonically owns `BFS-REQ-017`, `BFS-REQ-018`, and `BFS-REQ-019`, and it must close `DEF-024-002`, `DEF-024-003`, and `DEF-024-004` with deterministic, runtime, multiplayer, and packaged-artifact evidence.

The master plan owns product scope, requirement meaning, the frozen phase sequence, global verification order, and the plan-wide Definition of Done. This blueprint owns only the dependency-ordered execution of `BFS-PHASE-002`. It may verify retained contracts that this work could regress, especially `BFS-REQ-001`, `BFS-REQ-003`, `BFS-REQ-004`, `BFS-REQ-007`, and `BFS-REQ-008`, but it does not take canonical ownership of those requirements from `BFS-PHASE-001`.

Phase completion means all required Cod, Salmon, and Oceanic Whitetip states visibly show authored motion, Oceanic bite and thrash presentation agree with authoritative server state, and Tiger Sharks idle, acquire supported prey, navigate in three dimensions, bite exactly once per attack window, and recover cleanly. Compilation or source inspection alone cannot close this phase.

## Evidence-Based Entry State

All repository observations in this section are planning-time evidence from the intake revision `06a28f6`. They are provisional until rechecked from the merged and tagged `BFS-PHASE-001` default-branch commit.

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| VERIFIED | Product contract | `BFS-PHASE-002` owns `BFS-REQ-017` through `BFS-REQ-019` and follows merged and tagged `BFS-PHASE-001`. | `docs/general/plan.md`, frozen phase catalog and requirement traceability | Invalid if the owner authorizes a plan revision that changes phase ownership or sequence. |
| VERIFIED | Runtime and toolchain | The supported target is Minecraft `1.20.1`, Forge `47.2.0`, Java `17`, GeckoLib `4.4.7`, SmartBrainLib `1.14.2`, and Parchment `2023.09.03`. | `docs/general/plan.md`, `forge/build.gradle`, project manifests | Recheck after the Phase 001 merge or any manifest change. A version change requires plan review. |
| OBSERVED | Baking tool | `tools/bake_molang_animations.py` detects bare-array and wrapped single-keyframe MoLang vectors, samples 20 segments, writes time-keyed maps, and is intended to be idempotent. Its documented vocabulary is numeric arithmetic, `math.sin`, `math.cos`, and `q.anim_time` or `query.anim_time`. | `tools/bake_molang_animations.py` | Recheck after any tool or animation-resource change. Tool behavior must be characterized by tests before it edits authoritative resources. |
| OBSERVED | Unbaked fish channels | The Cod resource contains 48 wrapped single-keyframe MoLang channels and the Salmon resource contains 39. These are exactly the GeckoLib shape reported to freeze outside Blockbench. | `common/src/main/resources/assets/bensfintasticsharks/animations/entity/atlantic_cod.animation.json`, `common/src/main/resources/assets/bensfintasticsharks/animations/entity/atlantic_salmon.animation.json`, deterministic `jq` shape audit | Invalid after either resource is changed or baked. Preserve the pre-change audit in the completion packet. |
| OBSERVED | Unbaked Oceanic channels | The Oceanic Whitetip resource contains 35 wrapped single-keyframe MoLang channels and 39 already time-keyed channels. | `common/src/main/resources/assets/bensfintasticsharks/animations/entity/oceanic_whitetip_shark.animation.json`, deterministic `jq` shape audit | Invalid after the resource is changed or baked. Preserve the pre-change audit in the completion packet. |
| OBSERVED | Required clip inventory | Cod has `idle`, `swim`, `swim_fast`, and `flop`; Salmon has `spin`, `idle`, `swim`, `swim_fast`, and `flop`; Oceanic Whitetip has `idle`, `swim_new`, `swim_fast_new`, `bite_new`, `death`, `beached`, and `thrash`. | The three existing animation JSON resources | Recheck against the supplied 0.23 archive and merged Phase 001 resources before modification. |
| OBSERVED | Geometry bindings | Cod and Salmon animation bone names are present in their geometry. Oceanic Whitetip animation channels currently reference `leftFin` and `rightFin`, while its geometry exposes `Fin` and `Fin2`. This is a concrete structural mismatch that must be reconciled without dropping authored fin motion or renaming model bones speculatively. | The three animation JSON files and `common/src/main/resources/assets/bensfintasticsharks/geo/entity/atlantic_cod.geo.json`, `atlantic_salmon.geo.json`, and `oceanic_whitetip_shark.geo.json` | Recheck against the supplied archive and after Phase 001. Any different mismatch supersedes this observation and must be recorded. |
| OBSERVED | Fish controllers | Forge controllers currently select Cod flop before fast, swim, and idle. Salmon selects `Spin` first, then flop, fast, swim, and idle. Fast swim currently uses horizontal speed squared greater than `0.0225`. | `forge/src/main/java/tfar/bensfintasticsharks/entity/AtlanticCodEntityForge.java`, `AtlanticSalmonEntityForge.java` | Re-audit after Phase 001 and after any controller edit. Threshold behavior needs deterministic boundary coverage. |
| OBSERVED | Oceanic controller | The Oceanic locomotion controller maps beached, hostile fast swim, moving swim, and idle, with triggerable bite and death. A separate thrash controller runs while passengers exist and can affect bones also owned by the locomotion controller. | `forge/src/main/java/tfar/bensfintasticsharks/entity/OceanicWhitetipSharkEntityForge.java` | Re-audit after Phase 001. Any controller topology change invalidates the state-matrix evidence. |
| OBSERVED | Oceanic server state | Grab state is server-authored through a synchronized grab timer and passenger state. Damage repeats every ten ticks during a valid grab, cleanup ejects passengers on timeout, death, leaving water, or loss of a living passenger, and bite impact delay is currently six ticks. | `common/src/main/java/tfar/bensfintasticsharks/entity/OceanicWhitetipSharkEntity.java` | Recheck after Phase 001 and after any grab, attack, synchronization, or lifecycle change. |
| OBSERVED | Tiger curiosity | Tiger curiosity scans for a nearby underwater item, chooses an underwater approach point, sets a SmartBrainLib walk target, leaves the item alive, uses a 200-tick investigation, and applies a 600-tick per-item retry cooldown. Fleeing and live prey are intended to preempt curiosity. | `common/src/main/java/tfar/bensfintasticsharks/entity/TigerSharkEntity.java` | Recheck after Phase 001. Runtime proof must establish that the intended precedence actually executes. |
| OBSERVED | Tiger combat parameters | Tiger Shark currently declares detection radius `26`, disengage distance `64`, disengage timeout `300`, bite cooldown `20`, bite damage `4.0`, movement speed `1.2`, and bite impact delay `8`. Its prey contract is the generated `tiger_shark` prey tag. | `common/src/main/java/tfar/bensfintasticsharks/entity/TigerSharkEntity.java`, `common/src/generated/resources/data/bensfintasticsharks/tags/entity_types/prey/tiger_shark.json` | Recheck after Phase 001. Preserve accepted balance unless evidence shows the defect is caused by a contract mismatch. |
| OBSERVED | Shared locomotion boundary | `AbstractSharkEntity`, `BfsAquaticEntity`, and `SharkSwimmingMoveControl` own shared shark state, target and delayed bite handling, water travel, walk-target movement, and arrival or turn braking. | `common/src/main/java/tfar/bensfintasticsharks/entity/AbstractSharkEntity.java`, `BfsAquaticEntity.java`, `SharkSwimmingMoveControl.java` | Recheck after Phase 001. A shared edit invalidates Phase 001 locomotion and combat evidence for every shark in its blast radius. |
| VERIFIED | Current automated coverage limitation | Planning-time evidence found only `forge/src/test/java/tfar/bensfintasticsharks/spawn/VanillaFishReplacementPolicyTest.java`; the master requires Phase 000 to establish the smallest Forge GameTest harness before this phase. | `forge/src/test/java`, `docs/general/plan.md` | Replace this observation with the merged Phase 000 and Phase 001 harness inventory at entry. Missing required harness capability blocks implementation completion. |
| VERIFIED | Supported commands | Forge JUnit, data generation, build, dedicated server, and client tasks are `./gradlew :forge:test`, `:forge:Data`, `:forge:build`, `:forge:Server`, and `:forge:Client`. | `forge/build.gradle`, `docs/general/plan.md` | Recheck with `./gradlew tasks` if Gradle configuration changes. |
| VERIFIED | Documentation surfaces | Existing project documentation includes `README.md`, `DOCUMENTATION.md`, `CHANGELOG.md`, and `docs/README.md`. | Repository inventory | Recheck after Phase 001 integration. Preserve current paths and casing. |

## Scope Boundaries

### Included Scope

- `BFS-REQ-017`: make Cod and Salmon visibly animate in idle, ordinary swim, and fast swim while preserving flop and exact-name `Spin` behavior.
- `BFS-REQ-018`: make Oceanic Whitetip visibly animate in idle, normal swim, fast swim, bite, death, beached, and thrash, with coherent priorities, transitions, damage timing, and grab cleanup.
- `BFS-REQ-019`: restore Tiger idle locomotion, supported-prey acquisition, horizontal and vertical pursuit, physical bite contact, one intended damage application per attack window, and recovery after target loss or death.
- The phase-owned slice of `BFS-REQ-022`: close `DEF-024-002`, `DEF-024-003`, and `DEF-024-004` using their required deterministic and runtime evidence.
- Regression verification for `BFS-REQ-001`, `BFS-REQ-003`, `BFS-REQ-004`, `BFS-REQ-007`, and `BFS-REQ-008` where the Phase 002 changes touch shared movement, curiosity, grab cleanup, Oceanic clip mapping, or fish special states.
- Deterministic baking and sampling support in `tools/bake_molang_animations.py`, the three phase-owned animation resources, the corresponding geometry bindings and Forge controllers, Tiger and shared behavior code only where blast-radius evidence requires it, the Phase 000 test harness, existing technical documentation, and packaged Forge resources.

### Explicit Exclusions

- `BFS-REQ-013` through `BFS-REQ-016`, advancement icons, topology, titles, and punctuation, remain owned by `BFS-PHASE-003`.
- `BFS-REQ-020` and `BFS-REQ-021`, algae blocks and world generation, remain owned by `BFS-PHASE-004`.
- Final version changes, complete release packaging, publication, and the plan-wide defect audit under `BFS-REQ-023` remain owned by `BFS-PHASE-005`. Phase 002 still inspects its interim Forge JAR for phase-owned resources.
- Tiger Shark does not receive a new or replacement animation set. Its existing clips may be validated as presentation evidence for `BFS-REQ-019`, but unauthored animation work is prohibited.
- Sand Tiger Shark, Blacktip Reef Shark, Harbor Seal, and other species do not receive replacement animation sets or unrelated behavior redesigns. They are regression subjects only if shared code changes affect them.
- No Minecraft, Forge, Java, Gradle, mapping, GeckoLib, or SmartBrainLib upgrade is permitted.
- The Fabric template is not a supported release target, and its known missing SmartBrainLib dependency is not repaired in this phase.
- No stable registry identifier, serialized field, public resource identifier, prey-tag meaning, accepted balance value, or save format changes unless a plan revision explicitly authorizes one.
- The protected `build.gradle` line-ending change and untracked `Content/` evidence directory must remain outside every phase diff and commit.

## Phase Contract

### BFS-PHASE-002 — Animation and Predator Behavior Repair

**Objective:** Produce deterministic and in-game proof that all phase-owned supplied animation states visibly move and that Tiger Shark locomotion and predation complete reliably without starving curiosity or leaving stale server or client state.
**Owner:** Entity systems maintainer
**Dependencies:** BFS-PHASE-001
**Canonical requirements:** BFS-REQ-017, BFS-REQ-018, BFS-REQ-019
**Documentation and release impact:** Update the existing user and maintainer documentation only with behavior proved in this phase. Produce transform, controller, GameTest, runtime, multiplayer, log, and interim JAR evidence for the final release audit. Do not set version `0.24` or publish an artifact here.
**Next transition:** `BFS-PHASE-003`

**Entry criteria**

- GitHub reports the `BFS-PHASE-001` pull request merged, `origin/main` contains its merge commit, and the required signed annotated Phase 001 tag resolves to that commit.
- The Phase 002 branch is created from that verified `origin/main`; no stacked phase branch or direct default-branch work is allowed.
- The frozen master, phase index, this registered phase file, Phase 000 completion evidence, and Phase 001 completion evidence agree on project identity, requirement ownership, and phase sequence.
- The Phase 000 harness can represent transform audits, deterministic behavior decisions, and world-dependent predator pursuit and damage. Missing capability is filled within the approved harness design, not by weakening acceptance.
- `DEF-024-002`, `DEF-024-003`, and `DEF-024-004` have repeatable baseline fixtures or positive controls. If the merged Phase 001 state positively disproves a reported symptom, preserve the disproof and still run every Phase 002 acceptance gate.
- The 0.23 archive hashes and pre-change resource hashes are recorded. The Cod, Salmon, and Oceanic clip inventory and geometry-bone inventory are regenerated from the actual entry commit.
- The working tree inventory identifies and excludes all owner changes, generated output, run directories, logs, and `Content/` evidence from the phase branch.

**Implementation scope**

- `BFS-REQ-017`, `BFS-REQ-018`, and `BFS-REQ-019` are the complete canonical implementation assignment for `BFS-PHASE-002`.
- `BFS-PHASE-002`: Convert phase-owned single-keyframe MoLang channels into deterministic GeckoLib 4.4.7-compatible time-keyed transforms while preserving clip identifiers, duration, loop intent, authored curves, static poses, and already-valid keyframes.
- `BFS-PHASE-002`: Validate clip references, animation and geometry bone sets, moving-bone variance, sampled transform equivalence, controller reachability, transition priority, and absence of missing-bone or controller warnings.
- `BFS-PHASE-002`: Repair Cod and Salmon controller state selection so idle, ordinary swim, fast swim, flop, and Salmon `Spin` are reachable, stable, and visually distinct where authored.
- `BFS-PHASE-002`: Repair Oceanic Whitetip controller coordination and authoritative attack or grab coupling so bite damage and thrash lifetime match visible states.
- `BFS-PHASE-002`: Diagnose and repair Tiger scheduling, walk-target ownership, water navigation, prey acquisition, pursuit, reach, delayed damage, cooldown, target invalidation, and ordinary-navigation recovery while keeping curiosity nonconsuming and bounded.
- `BFS-PHASE-002`: Add deterministic, GameTest, server, client, multiplayer, recovery, and interim artifact verification sufficient to close the three owned defects.

**Execution order**

1. `P002-TASK-001` for `BFS-PHASE-002` establishes the exact post-Phase-001 baseline, protects unrelated work, and reproduces or positively disproves all three phase defects.
2. `P002-TASK-002` for `BFS-PHASE-002` makes the MoLang baking path targeted, deterministic, idempotent, fail-closed, and testable before it changes authoritative animation resources.
3. `P002-TASK-003` for `BFS-PHASE-002` bakes and structurally validates Cod, Salmon, and Oceanic Whitetip resources, including all clip IDs, lengths, loops, moving bones, and geometry bindings.
4. `P002-TASK-004` for `BFS-PHASE-002` implements and verifies the Cod and Salmon presentation state matrices.
5. `P002-TASK-005` for `BFS-PHASE-002` implements and verifies the Oceanic Whitetip presentation, damage, and thrash-cleanup state matrix.
6. `P002-TASK-006` for `BFS-PHASE-002` repairs Tiger Shark server behavior and proves curiosity and combat coexist without movement starvation.
7. `P002-TASK-007` for `BFS-PHASE-002` completes deterministic and GameTest coverage for all phase contracts and every required negative or recovery path.
8. `P002-TASK-008` for `BFS-PHASE-002` runs the ordered Forge build, server, client, multiplayer, log, and interim JAR evidence matrix.
9. `P002-TASK-009` for `BFS-PHASE-002` updates existing documentation, assembles the completion packet, integrates through GitHub, verifies default branch state, and creates the signed phase tag.

**Required evidence**

- A before-and-after channel-shape inventory for each phase-owned animation file, plus an idempotence proof showing a second bake produces no resource diff.
- For every required clip, samples at start, quarter, half, three-quarter, and end; at least one valid moving bone with two transform vectors different by more than `0.0001`; and baked-versus-authored transform equivalence within `0.0001` at the mandated sample times.
- A geometry and animation binding audit proving every referenced bone resolves and every controller clip ID exists. No GeckoLib missing-bone, missing-animation, reset, or controller-conflict warning may remain.
- Deterministic state-matrix tests for Cod, Salmon, Oceanic Whitetip, and Tiger presentation, including exact threshold boundaries and state-preemption rules.
- A deterministic Tiger target and damage test, a world-dependent horizontal and vertical pursuit GameTest, curiosity preemption and recovery tests, and proof of exactly one intended damage event per attack window.
- Interactive client evidence for all Cod, Salmon, and Oceanic required clips and for Tiger idle, pursuit, bite, target loss, and curiosity behavior.
- Dedicated-server readiness without client class loading, registry, data pack, animation-reference, or behavior scheduler failures.
- Multiplayer evidence for Oceanic bite and thrash synchronization, grab release on every required lifecycle path, Tiger attack state synchronization, target death, disconnect, reconnect, dimension change, unload, and no permanent passenger or camera state.
- Interim Forge JAR contents proving the three final animation resources and related geometry resources are packaged exactly once and match the verified working-tree outputs.
- A complete diff and ignored-output audit proving protected owner work and unrelated resources were not modified or committed.

**Exit criteria**

- `BFS-REQ-017`, `BFS-REQ-018`, and `BFS-REQ-019` pass every applicable row of the verification matrix.
- `DEF-024-002`, `DEF-024-003`, and `DEF-024-004` each have their master-required minimum evidence and are no longer reproducible.
- Cod and Salmon visibly move in idle, ordinary swim, and fast swim; flop still works; and Salmon named exactly `Spin` loops spin until the name changes or is removed, then immediately returns to the correct ordinary state.
- Oceanic Whitetip visibly moves in idle, normal swim, fast swim, bite, death, beached, and thrash. Bite impact agrees with authoritative damage, and thrash stops no later than grab cleanup.
- Tiger Shark moves during ordinary idle behavior, selects supported prey, follows horizontal and vertical water paths, reaches contact, applies intended damage once per attack window, and resumes ordinary navigation after invalidation, target loss, or target death.
- Tiger item curiosity leaves the item in the world, terminates on success, invalidation, timeout, or path failure, applies the per-item retry cooldown, yields to combat and fleeing, and cannot permanently suppress subsequent idle or combat movement.
- No client-only type is reachable from common or dedicated-server initialization, no stale attack or passenger state remains across multiplayer lifecycle events, and no shared locomotion regression is introduced.
- Required commands, interactive runtime checks, complete diff inspection, interim JAR inspection, and required GitHub checks pass. One private independent review passes when the capability is available; otherwise its verified unavailability is recorded without weakening deterministic gates.
- The Phase 002 pull request is merged through GitHub using the permitted merge method, the resulting merge commit is present on `origin/main`, and the signed annotated Phase 002 tag points to that merge commit.
- No known mandatory phase-owned defect remains.
- The final phase verifies the owner-selected completion endpoint and plan-wide Definition of Done.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| Frozen master and registered phase set | Plan Creator integration audit | Phase 002 remains contiguous, canonically owns `BFS-REQ-017` through `BFS-REQ-019`, and has no unresolved owner choice. | Verify the plan index, file registration, digests, phase order, and absence of extra phase files. | Stop with the applicable plan conflict. Do not edit the master or another phase file. |
| Phase 000 completion packet | `BFS-PHASE-000` | Contains baseline defect fixtures, transform-audit design, GameTest harness design, protected-work inventory, and artifact evidence conventions. | Resolve each recorded evidence artifact and rerun its health check. | Keep Phase 002 blocked until missing mandatory baseline evidence is restored through the authorized prior-phase process. |
| Stable movement and combat baseline | `BFS-PHASE-001` | `BFS-REQ-001` through `BFS-REQ-012` are merged, tagged, and evidenced; shared steering, delayed bite, curiosity, grab cleanup, and fish special behavior are known-good. | Verify PR merge, tag signature, default-branch ancestry, checks, completion packet, and focused smoke controls. | Do not start or stack Phase 002. Route a regression to the Phase 001 gate or an owner-authorized plan revision as appropriate. |
| Supplied Atlantic and Oceanic assets | 0.23 content archive | Clip names, authored transforms, durations, loop intent, geometry, and pixels are intact; Cod geometry identifier correction remains accepted. | Verify archive SHA-256 and compare the three resource families against the recorded asset ledger. | Stop resource transformation on mismatch. Restore from verified evidence without overwriting unrelated changes. |
| Existing baking implementation | Repository toolchain | The tool can identify both supported single-keyframe shapes and leave valid or static channels unchanged. | Characterization tests on controlled fixtures, output normalization check, idempotence check, and unsupported-expression negative tests. | Do not run it against authoritative resources until characterization passes. Retain pre-bake copies outside committed output for comparison. |
| GeckoLib geometry and controller interfaces | Common resources and Forge entity adapters | Every controller clip resolves to one animation, every animated bone resolves to model geometry, and client controllers consume synchronized or client-observable state only. | Structural audit plus client log and transition matrix. | Missing bindings are release-blocking. Apply the smallest evidence-backed correction; never drop a channel silently. |
| Tiger prey contract | Generated entity type tag and common entity behavior | Only supported, live, valid prey can be acquired, with accepted cooldown and player exclusions preserved. | Load-time tag audit, predicate tests, and GameTest prey fixtures. | Fail closed on absent or malformed tags. Do not broaden prey or player aggression to make a test pass. |
| Owner working tree | Repository state | Existing line-ending and `Content/` changes remain untouched and uncommitted. | Before-and-after `git status`, scoped diff, ignored and untracked-file review. | Stop before staging or committing if ownership cannot be separated safely. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| Baked Atlantic Cod animation resource | Client runtime and `BFS-PHASE-005` | All four required clips retain names, lengths, loops, static intent, and authored motion while using GeckoLib-compatible time-keyed moving transforms. | Resource identifier and geometry identifier remain stable; no save or registry impact. | Structural audit, five-point transform equivalence, visual capture, and JAR extraction hash. |
| Baked Atlantic Salmon animation resource | Client runtime and `BFS-PHASE-005` | All five required clips retain names and authored behavior, including exact-name `Spin`, and ordinary states visibly move. | Resource and entity IDs remain stable; name easter egg remains exactly case-sensitive `Spin`. | Structural audit, five-point transform equivalence, state transition test, visual capture, and JAR hash. |
| Baked Oceanic Whitetip animation and binding contract | Client runtime, multiplayer runtime, and `BFS-PHASE-005` | Seven required clips resolve, animate valid geometry bones, and follow one coherent priority model tied to authoritative attack and grab state. | Existing clip names, geometry identifier, entity ID, and accepted grab contract remain stable. | Bone audit, transform report, transition trace, damage timing trace, cleanup matrix, client capture, and JAR hash. |
| Deterministic animation transformer | Maintainers and `BFS-PHASE-005` | Targeted input selection, deterministic formatting, fail-closed vocabulary validation, non-destructive handling of valid channels, and second-run idempotence are proven. | Existing default use remains compatible unless an evidence-backed interface tightening is required; unrelated animations remain unchanged. | Fixture results, hashes, first-run diff, second-run empty diff, and unsupported-input result. |
| Tiger behavior contract | Server runtime, clients, and `BFS-PHASE-005` | Stable scheduling, three-dimensional pursuit, physical reach, delayed single damage, recovery, and curiosity coexistence are authoritative and bounded. | Preserve Tiger entity ID, prey-tag contract, accepted parameters, saved variant, and nonconsuming curiosity. Ephemeral target and timer state is not persisted. | Unit and decision tests, GameTests, runtime traces, multiplayer capture, and save or reload smoke evidence. |
| Regression suite | Later phases and release audit | Phase-owned animation and predator regressions fail deterministically before release. | Tests run on the supported Forge source set and Phase 000 harness; lower-fidelity tests do not replace client or multiplayer proof. | `:forge:test`, GameTest results, fixture definitions, and recorded expected outcomes. |
| Phase completion packet | `BFS-PHASE-003` and `BFS-PHASE-005` | Defect closure, checks, review, merge, tag, documentation, runtime, recovery, and artifact proof are complete and refer to the merged default-branch result. | Evidence is stored outside the protected plan set using Phase 000 conventions. | Packet inventory and verified links or hashes. |

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| `P002-TASK-001` | `BFS-REQ-017`, `BFS-REQ-018`, `BFS-REQ-019`, `DEF-024-002` through `DEF-024-004` | Establish the post-Phase-001 source, asset, defect, test, and working-tree baseline. Reproduce each symptom with positive controls and record exact clip, controller, scheduler, navigation, target, and damage observations. | Merged and tagged Phase 001, Phase 000 fixtures, archive ledger, owner-work inventory | Entry evidence matrix, current hashes, repeatable failure traces, and scoped implementation boundary | Master-listed resources, Forge entity adapters, common shark behavior, existing tests and GameTest harness | Baseline commands and runtime fixtures reproduce or positively disprove each defect; protected changes remain out of scope. |
| `P002-TASK-002` | `BFS-REQ-017`, `BFS-REQ-018` | Characterize and, where required, tighten the baking tool so it accepts targeted files, handles both single-keyframe shapes, rejects unsupported or indeterminate expressions, preserves valid channels, emits stable numeric and key ordering, and is idempotent. | `tools/bake_molang_animations.py`, controlled copies of required resources, Phase 000 transform-fixture design | Verified transformation pipeline and deterministic preflight report | `tools/bake_molang_animations.py`; phase test fixtures without inventing a new production resource path | Golden structural comparison, five-point evaluation, malformed input, missing length, already-baked input, static wrapper, deterministic hash, and second-run no-op. |
| `P002-TASK-003` | `BFS-REQ-017`, `BFS-REQ-018`, compatibility with `BFS-REQ-007` | Apply the verified transform pipeline only to Cod, Salmon, and Oceanic resources. Audit clip IDs, loop and length metadata, time keys, transform dimensions, geometry bones, and moving-bone variance. Reconcile the Oceanic fin binding using archive and visual evidence without renaming model bones or dropping motion. | Tasks 001 and 002, verified archive, three animation and three geometry resources | Final phase-owned animation JSON, binding map, transform-equivalence report, no unrelated resource diff | Existing animation and geometry resources under `common/src/main/resources/assets/bensfintasticsharks` | Every required clip and bone resolves; mandated samples match authored evaluation within tolerance; every clip has required movement; full-directory audit reports no new warning. |
| `P002-TASK-004` | `BFS-REQ-017`, compatibility with `BFS-REQ-008`, `DEF-024-002` | Enforce Cod and Salmon state matrices, stable threshold behavior, `Spin` preemption and immediate exit, flop preservation, and no controller reset loop. | Task 003, fish Forge adapters, synced movement and water state | Reachable and deterministic Cod and Salmon presentation states | `AtlanticCodEntityForge`, `AtlanticSalmonEntityForge`, corresponding animation resources | State table tests, threshold boundary tests, rename transition, land and water transitions, client captures, and clean GeckoLib logs. |
| `P002-TASK-005` | `BFS-REQ-018`, compatibility with `BFS-REQ-004` and `BFS-REQ-007`, `DEF-024-003` | Enforce Oceanic priority, eliminate overlapping-controller conflicts, align `bite_new` impact with server damage, bind thrash strictly to a valid grab, and ensure death, beached, target loss, and passenger cleanup terminate incompatible states. | Task 003, Oceanic common and Forge entity classes, Phase 001 grab contract | Coherent seven-state presentation and attack or grab lifecycle | `OceanicWhitetipSharkEntity`, `OceanicWhitetipSharkEntityForge`, Oceanic animation and geometry resources | State matrix tests, damage-frame trace, grab cleanup GameTests, client and multiplayer capture, and zero missing bone or controller warnings. |
| `P002-TASK-006` | `BFS-REQ-019`, compatibility with `BFS-REQ-001` and `BFS-REQ-003`, `DEF-024-004` | Trace and repair Tiger scheduler precedence, target acquisition, walk-target ownership, water pathing, arrival and bite reach, delayed damage, attack cooldown, path failure, target invalidation, and recovery. Prove curiosity leaves items intact, yields to combat and fleeing, retries only after cooldown, and releases movement ownership. | Task 001, common Tiger entity, Forge adapter, shared shark and aquatic movement, Tiger prey tag, Phase 001 baseline | Server-authoritative Tiger behavior with bounded transitions and no stale memory | `TigerSharkEntity`, `TigerSharkEntityForge`, `AbstractSharkEntity`, `BfsAquaticEntity`, `SharkSwimmingMoveControl`, SmartBrainLib memory and activity interfaces, Tiger prey tag | Deterministic target and damage tests, horizontal and vertical GameTests, curiosity matrix, target death and path-failure recovery, multiplayer state trace, and in-game pursuit evidence. |
| `P002-TASK-007` | `BFS-REQ-017` through `BFS-REQ-019`; `DEF-024-002` through `DEF-024-004` | Complete automated coverage for transform variance, clip and bone references, controller matrices, Tiger decisions, pursuit, damage, cleanup, and negative paths. Add shared blast-radius tests if any shared component changed. | Tasks 002 through 006, Phase 000 harness | Repeatable regression suite and fixtures | Existing Forge JUnit source set and Phase 000 GameTest harness; exact harness paths are inherited from the merged prior phase | `:forge:test` and GameTests pass from a clean state; a deliberate fixture mutation proves each new assertion can fail. |
| `P002-TASK-008` | `BFS-REQ-017` through `BFS-REQ-019`; `DEF-024-002` through `DEF-024-004` | Execute formatter or record absence, static analysis or record absence, Forge tests, data generation if required, GameTests, Forge build, dedicated server, interactive client, multiplayer and reconnect matrix, final log scan, and interim JAR inspection in master order. | Tasks 003 through 007 | Command logs, runtime captures, multiplayer results, and artifact inventory | Gradle Forge tasks, supported client and server runs, built Forge JAR | Every mandatory gate passes; headless smoke is not accepted as visual proof; known Fabric stub failure is recorded only if the unsupported root task is additionally run. |
| `P002-TASK-009` | `BFS-REQ-017` through `BFS-REQ-019`, phase slice of `BFS-REQ-022` | Update existing documentation with verified behavior and recovery guidance, assemble closure evidence, inspect complete diff, commit and push on the phase branch, create the phase pull request, obtain required checks and independent review, merge, verify default branch, and sign the phase tag. | All earlier tasks and repository workflow rules | Merged, tagged Phase 002 and downstream handoff | `README.md`, `DOCUMENTATION.md`, `CHANGELOG.md`, `docs/README.md` as applicable; Git and GitHub phase workflow | Documentation matches passed behavior, no protected or generated junk is committed, checks and review pass, merge commit is on `origin/main`, and signed tag verification succeeds. |

Tasks 002 and the diagnostic portion of Task 006 may proceed independently after Task 001 because they own separate animation and behavior boundaries. Task 003 depends on the transformer characterization in Task 002. Tasks 004 and 005 depend on final structural resources from Task 003. Test scaffolding in Task 007 may begin alongside Tasks 003 through 006, but assertions are not accepted until run against the final implementation. Task 008 is strictly after implementation and deterministic tests. Task 009 is strictly after every technical and runtime gate.

If a shared component is edited during Task 006, all affected shared-shark regression checks become mandatory before any runtime evidence is accepted. If the fix would alter accepted behavior outside this phase or change a stable public contract, stop with `PLAN_REVISION_REQUIRED` rather than widening the implementation.

## Presentation State Matrices

### Atlantic Cod

Evaluate in priority order. A higher row suppresses every lower row for that tick and through any required transition debounce.

| Priority | Predicate | Required clip or presentation | Transition invariant |
|---|---|---|---|
| 1 | Dead or dying | Preserve the established vanilla or merged Phase 001 death presentation. There is no supplied Cod death clip in this phase. | Death cannot be presented as proof of ordinary animated motion and must not restart a live-state loop. |
| 2 | Alive and outside water or bubble fluid | `animation.atlantic_cod.flop` | Flop remains continuously visible while beached and exits promptly on re-entry to water. |
| 3 | In water and above the verified fast-swim threshold | `animation.atlantic_cod.swim_fast` | Boundary behavior is deterministic and does not flap when velocity hovers near the threshold. |
| 4 | In water and moving below the fast threshold | `animation.atlantic_cod.swim` | Ordinary swim remains selected during real low-speed locomotion and contains visible authored movement. |
| 5 | In water and not moving | `animation.atlantic_cod.idle` | Idle remains stable long enough to observe and contains visible authored movement rather than a frozen pose. |

### Atlantic Salmon

| Priority | Predicate | Required clip or presentation | Transition invariant |
|---|---|---|---|
| 1 | Dead or dying | Preserve the established vanilla or merged Phase 001 death presentation. There is no supplied Salmon death clip in this phase. | Death suppresses live-state loops and is not used as ordinary-motion evidence. |
| 2 | Alive and custom name is exactly `Spin` | `animation.atlantic_salmon.spin` | `Spin` wins over water, movement, and flop predicates while the exact name exists. Changing case, renaming, or removing the name immediately selects the correct ordinary state without a stale spin loop. |
| 3 | Not named `Spin` and outside water or bubble fluid | `animation.atlantic_salmon.flop` | Flop remains working and exits promptly on water re-entry. |
| 4 | In water and above the verified fast-swim threshold | `animation.atlantic_salmon.swim_fast` | Fast swim is reachable with deterministic threshold boundaries and no reset churn. |
| 5 | In water and moving below the fast threshold | `animation.atlantic_salmon.swim` | Ordinary swim visibly moves and does not fall through to idle while navigation is active. |
| 6 | In water and not moving | `animation.atlantic_salmon.idle` | Idle visibly moves and remains stable during genuine stillness. |

### Oceanic Whitetip

The implementation may retain multiple GeckoLib controllers only if they do not concurrently write incompatible transforms to the same bones. Otherwise, presentation ownership must be coordinated so one state controls each affected bone at a time.

| Priority | Authoritative or observable predicate | Required clip | Server and client invariant |
|---|---|---|---|
| 1 | Dead or dying | `animation.oceanicwhitetipshark.death` | Death cancels pending attack presentation, triggers grab cleanup, prevents thrash continuation, plays once and holds as authored, and cannot fall through to locomotion. |
| 2 | Alive but grab is invalid because of timeout, no living passenger, leaving water, unload, disconnect, or dimension change | No stale grab clip | Server clears grab state and passengers first; clients stop thrash and restore camera or passenger presentation without waiting for an unrelated animation timeout. |
| 3 | Alive, out of water, and grounded | `animation.oceanicwhitetipshark.beached` | Beached suppresses locomotion, bite, and thrash after required server cleanup and contains visible authored motion. |
| 4 | Alive with a valid active passenger grab | `animation.oceanicwhitetipshark.thrash` | Thrash begins only after a valid bite and grab transition, does not overlap incompatible locomotion transforms, and ends no later than authoritative grab cleanup. |
| 5 | Alive in a bite attack window before any resulting grab | `animation.oceanicwhitetipshark.bite_new` | The one-shot is not restarted each tick. Its verified jaw impact frame matches exactly one scheduled bite damage event; a later successful grab transitions to thrash. |
| 6 | Alive, in water, hostile, and pursuing | `animation.oceanicwhitetipshark.swim_fast_new` | Hostile pursuit cannot fall through to normal swim and remains synchronized from server-authored shark state. |
| 7 | Alive, in water, nonhostile, and moving | `animation.oceanicwhitetipshark.swim_new` | Normal swim remains stable and visibly animated. |
| 8 | Alive, in water, nonhostile, and not moving | `animation.oceanicwhitetipshark.idle` | Idle visibly animates and cannot mask a higher-priority state. |

## Tiger Server Behavior Matrix

Server behavior, not the animation controller, owns target selection, navigation, contact, damage, cooldown, and cleanup. The client renders synchronized results and must never originate damage or target state.

| Priority | Condition | Required server behavior | Completion or recovery condition |
|---|---|---|---|
| 1 | Dead, removed, unloaded, or changing dimension | Cancel pending bite, clear attack and walk-target ownership, release any incidental passenger state, and stop navigation. | No delayed damage or stale hostile state occurs after lifecycle completion. |
| 2 | Fleeing from a larger shark | Drop item curiosity and hunting work, preserve the shared bounded flee contract, and keep a reachable escape walk target. | On flee completion, resume ordinary scheduling without the old item or prey waypoint. |
| 3 | Valid live combat target exists | Curiosity yields immediately. Maintain one target, refresh a reachable water pursuit path at a bounded cadence, and preserve vertical correction. | Target reaches bite approach, becomes invalid, dies, exceeds disengage rules, unloads, or path failure reaches its bounded recovery policy. |
| 4 | Target is within verified physical bite range and attack cooldown permits | Trigger one bite presentation and one delayed server damage window. Freeze duplicate scheduling until that window resolves. | At impact, revalidate target identity, life, dimension, water context, and physical contact. Apply `4.0` intended damage once or cancel without damage. |
| 5 | No target and a valid curiosity item is selected | Use one stable reachable underwater intercept, never consume or damage the item, and avoid replacing the walk target every tick. | Cosmetic bite, item invalidation, timeout, bounded path failure, or combat or flee preemption clears curiosity and applies the per-item retry cooldown where required. |
| 6 | No target, flee, or active curiosity | Run normal idle scheduler and random water navigation. | The shark shows nonzero bounded displacement over the deterministic idle window and can later enter curiosity or combat. |

Target loss, death, canceled impact, and path failure must clear only state owned by the ending behavior. They must not erase a newer higher-priority walk target or damage a replacement target. Memory changes and entity mutation occur on the logical server thread. Any client-visible state must use existing synchronized entity data or an explicitly justified, validated server-to-client interface.

## Architecture and Implementation Boundaries

### Resource transformation and validation

- Treat the supplied archive and repository animation JSON as trusted authored input, but validate the expression vocabulary before evaluation. Unknown functions, names, object traversal, nonnumeric results, nonfinite values, malformed vectors, absent bones, and clips with no determinable duration fail closed with a file, clip, bone, and channel diagnostic.
- Scope authoritative transformation to the three phase-owned animation files. The existing default directory mode must not cause unrelated animation resources to enter the diff. A targeted-file interface or an equivalent controlled staging process must preserve deterministic behavior.
- Time keys and numeric output must be stable across runs, locales, and machines. Preserve animation order, clip identifiers, `animation_length`, loop setting, static vectors, already-valid time-keyed channels, and channels outside the single-keyframe MoLang defect shape.
- The mandatory five sample times are derived from each clip's effective duration: `0`, `duration / 4`, `duration / 2`, `3 * duration / 4`, and `duration`. At those times, compare each baked component with authored MoLang evaluation and require absolute error no greater than `0.0001`.
- A loop-closing rewrite may not overwrite an authored endpoint that differs at a required sample. Seam handling must preserve both the authored evaluation and duration; any detected discontinuity is recorded and resolved from source evidence rather than silently forcing the final keyframe to the initial vector.
- At least one geometry-resolved bone in every required clip must have two sampled transform vectors whose component difference exceeds `0.0001`. Clip existence, changing animation time, or controller selection without transform variance is insufficient.
- Produce a full animation-to-geometry binding report. A channel targeting no geometry bone is an error even if GeckoLib merely logs a warning. The Oceanic `leftFin` and `rightFin` mismatch must be reconciled through verified left and right fin intent. Do not rename geometry bones, invent a bone, or discard fin channels just to satisfy the validator.

### Logical-side ownership

- Common entity classes own gameplay state, target rules, navigation, cooldowns, contact validation, damage, passenger state, cleanup, and persistence. Forge GeckoLib adapters own presentation and may trigger animation from server-authored or synchronized state.
- No `net.minecraft.client` class may be referenced from common initialization or dedicated-server paths. Renderer and visual-only code remain under the existing Forge client boundary.
- Damage, target acquisition, item selection, item retention, walk-target writes, grab initiation, and cleanup execute only on the logical server. Clients never predict an authoritative hit or retain a local grab after the server has cleared it.
- Animation trigger messages are server to client. If existing GeckoLib trigger synchronization is sufficient, do not add networking. If it is insufficient, any new payload must be bounded, side-registered, identity-validated, handled on the correct thread, and incapable of causing gameplay mutation on receipt.

### Controller and state ownership

- Controller predicates must be pure presentation decisions over stable state. They must not mutate navigation, targets, health, passengers, or persistent data.
- One-shot bite and death clips must be edge-triggered, not retriggered every tick. Looping locomotion clips may continue while their predicate remains true without resetting time.
- State transitions must respect the explicit matrices. If multiple controllers touch overlapping bones, coordinate them by mutually exclusive predicates or one authoritative presentation selector. Prove the absence of additive conflict rather than assuming controller order.
- Fish fast-swim thresholds must use the same motion measure in implementation and tests. Cover exactly below, at, and above the boundary. If a debounce is required to avoid visible flapping, it must not delay a genuine state change enough to hide the required state in runtime evidence.
- Salmon `Spin` remains an exact, case-sensitive display-name behavior. It is presentation-only and must not alter movement, attributes, loot, persistence, or the stored name.

### Tiger scheduling, navigation, reach, and damage

- Preserve SmartBrainLib activity and memory conventions already used by the repository. Determine why the Tiger-specific live scheduler fails before adding species-specific duplication. Prefer the narrowest correction that restores Tiger behavior.
- Shared edits to `AbstractSharkEntity`, `BfsAquaticEntity`, or `SharkSwimmingMoveControl` require CodeGraph blast-radius review and rerunning all Phase 001 movement, approach, damage, curiosity, and grab regression gates affected by the change.
- `ModTags.EntityTypes.TIGER_SHARK_PREY` remains the prey authority. Missing tags, same-type targets, dead entities, creative or spectator players, cross-dimension entities, passengers of the attacker, and prey excluded by hunt cooldown fail closed.
- The pursuit scheduler must not let curiosity overwrite a live target's walk target. Conversely, target loss must not leave a stale combat waypoint that blocks later curiosity or idle navigation.
- Navigation must choose reachable water paths and demonstrate both horizontal and meaningful vertical correction. Path refresh must be bounded and must not allocate or rescan broadly every tick. Arrival braking must allow the collision volumes to enter bite range instead of orbiting or stopping outside contact.
- Bite reach is based on actual entity geometry and a verified attack reference point. It must accept supported prey sizes at physical contact, reject out-of-range targets, and avoid reach inflation that permits hits through blocks or across visible gaps.
- One attack window has one target identity, one presentation edge, one delayed impact, and at most one damage application. At impact, revalidate contact and cancel safely if the target has died, unloaded, changed dimension, left valid context, or been replaced.
- Preserve accepted Tiger balance from Phase 001: movement speed `1.2`, intended bite damage `4.0`, 20-tick bite cooldown, 26-block detection radius, 64-block disengage distance, and 300-tick disengage timeout unless the Phase 001 evidence proves a value was not accepted. Repair scheduling, geometry, or timing before retuning balance.
- Curiosity chooses one reachable underwater intercept and refreshes it only when the item materially moves or the path invalidates. Cosmetic bite leaves the item entity and stack count unchanged. Completion, invalidation, timeout, and path failure clear owned memory, stop only owned navigation, and apply the 600-tick per-item retry cooldown.
- Runtime-only investigation, pending attack, path, and target state remains ephemeral. Do not add persistence for transient scheduler state. Existing Tiger variant and custom-name persistence remains unchanged.

### Concurrency, performance, and compatibility

- All entity and brain mutation runs on the logical server tick thread. Multiplayer clients observe synchronized state but cannot race a local mutation against server cleanup.
- Avoid filesystem access, JSON evaluation, resource scans, registry scans, and broad entity collection scans in entity ticks. Baking and structural validation are build-time operations.
- Bound target scans, item scans, path recalculation, retries, and logging. No per-tick warning spam is acceptable; repeated malformed runtime state should produce actionable bounded diagnostics.
- Preserve registry names, clip names, geometry identifiers, entity tracking behavior, NBT fields, and save compatibility. Loading a Phase 001 world must not require migration and must not discard variants or names.
- Forge remains the supported runtime. Common changes must compile cleanly within the existing multi-module source layout, but the unsupported Fabric stub cannot weaken or replace Forge evidence.

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| Unsupported or malformed MoLang expression | Preflight reports unknown token, function, nonfinite result, malformed vector, or evaluation failure with exact location | Abort before writing any authoritative file. Never partially bake a file or coerce the value. | Correct the transformer for an in-contract expression or restore the verified source; rerun characterization and all samples. | Negative transformer fixture and unchanged input hash. |
| Clip has no explicit or inferable duration | Transformer finds a single-keyframe moving channel with zero effective length | Fail closed because deterministic sampling cannot preserve time behavior. | Resolve duration from supplied asset evidence or stop for contract clarification if evidence conflicts. | Indeterminate-length fixture and no output diff. |
| Baked output changes on a second run | Hash or diff differs after idempotence pass | Reject the output and do not stage resources. | Restore source hashes, fix normalization or channel detection, and rerun the complete transform suite. | First-run expected diff and second-run empty diff. |
| Authored and baked transforms differ above `0.0001` at a mandated sample | Component-wise sample comparison fails | Keep the defect open and reject visual review as compensating evidence. | Restore verified input, correct evaluation semantics, rebake, and rerun every clip sample. | Five-point report for all required clips. |
| Loop seam changes the authored endpoint | End sample differs or forced closure changes the final vector | Do not silently replace the endpoint. Preserve authored evaluation and duration. | Adjust deterministic sampling or use evidence-backed key placement, then revalidate seam and mandated samples. | Endpoint equivalence and client loop capture. |
| Animation references a missing bone | Structural audit or GeckoLib log identifies the binding | Treat the clip as incomplete even if other bones move. | Reconcile against supplied geometry and visual left or right intent; do not invent, rename, or drop bones without evidence. | Binding report and clean client log. |
| Controller state is unreachable | State-matrix fixture cannot select a required clip | Block defect closure. | Correct predicate priority or synchronized state, then rerun adjacent transition cases. | Full matrix with each row reached and a client capture. |
| Controller flaps at a motion threshold | Transition trace repeatedly resets between idle, swim, or fast swim around the boundary | Stabilize selection without masking legitimate movement changes. | Apply evidence-backed threshold semantics or minimal debounce and rerun boundary and visual tests. | Below, equal, above, accelerate, and decelerate cases. |
| Oceanic bite damage leads or trails the visible impact | Server tick trace disagrees with the verified jaw-impact sample | Do not accept the attack. | Align delayed impact to authored clip timing while retaining one-hit semantics; rerun client and multiplayer evidence. | Frame or tick-aligned capture and health delta trace. |
| Oceanic grab ends while thrash continues | Passenger or grab timer clears but client controller remains active | Stop thrash immediately from synchronized cleanup state and restore ordinary presentation. | Force server cleanup on the invalid lifecycle path, resynchronize passengers, and rerun the matrix. | Timeout, death, unload, disconnect, dimension-change, and no-passenger tests. |
| Tiger finds no valid prey | Sensor or tag trace has supported prey in radius but no target memory | Fail closed on invalid prey but acquire valid tagged prey within the accepted scan cadence. | Repair sensor registration or activity scheduling without broadening the prey contract. | Deterministic supported and unsupported prey cases. |
| Tiger has a target but remains stationary | Target exists while walk target, path, or velocity remains absent across the fixture window | Keep `DEF-024-004` open. | Trace activity priority, memory ownership, navigation creation, move control, and water path validity; clear stale curiosity state; retry within bounded cadence. | Horizontal and vertical pursuit GameTests plus movement trace. |
| Tiger orbits or brakes outside bite contact | Repeated path completion occurs while perceived distance remains beyond physical reach and no bite starts | Separate approach, arrival braking, and contact thresholds using collision evidence. | Apply the narrowest Tiger or shared correction, then rerun shared shark regressions if applicable. | Stationary and moving prey contact tests. |
| Tiger schedules duplicate damage | More than one health delta occurs in one attack window or an old target is damaged after replacement | Cancel duplicate or stale pending impacts and preserve one authoritative window. | Clear the owned pending attack on all terminal transitions and revalidate identity at impact. | Tick-by-tick health and target trace. |
| Target dies, unloads, or changes dimension before impact | Lifecycle fixture invalidates the pending target | Cancel damage, clear stale combat state, and resume eligible navigation. | No retry against the invalid target; allow a later independent target after normal rules. | Death, unload, dimension, and replacement-target tests. |
| Curiosity and combat become eligible together | Item and supported prey exist in the same scan window | Combat wins; curiosity clears without consuming the item or permanently blacklisting unrelated items. | After combat ends and cooldown rules permit, ordinary scheduling and later curiosity resume. | Coexistence GameTest and item stack identity check. |
| Curiosity item moves, dies, leaves water, times out, or has no path | Item or path predicate becomes invalid | End or boundedly refresh the investigation according to the contract, clear owned walk target, and apply retry cooldown where required. | Ordinary idle or combat movement resumes without manual intervention. | Separate negative cases and 600-tick retry-boundary test. |
| Dedicated server loads a client class | Server startup exception or class-loading trace | Treat as release-blocking. | Move visual code back to the Forge client boundary and rerun compile, server, and client gates. | `:forge:Server` ready-state log and class-reference audit. |
| Client misses authoritative cleanup after reconnect | Passenger, camera, hostile, bite, or thrash state remains stale after lifecycle action | Server state wins and the reconnect snapshot restores a clean state. | Resend or derive state through existing synchronization, never client-side gameplay mutation. | Two-client disconnect, reconnect, and dimension-change matrix. |
| Interim JAR contains stale or duplicate animation data | JAR listing, extracted hash, or structural audit disagrees with source output | Reject the artifact. | Clean supported build outputs, rebuild Forge, and rerun JAR inspection. | Entry listing, extracted hashes, and parsed packaged resources. |
| Root build enters the unsupported Fabric stub | Optional `./gradlew build` fails for missing Fabric SmartBrainLib | Do not treat it as the supported release result or alter Fabric scope. | Record the known baseline and continue only with successful required Forge gates. | Successful `:forge:build` and unchanged Fabric failure signature if root build was run. |
| Interactive visual evidence is unavailable | Headless client reaches menu but no rendered entity review can be captured | Do not close animation defects. | Run the interactive client on a graphics-capable environment and complete every clip and transition review. | Captures and reviewer checklist tied to the tested commit. |

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Evidence artifact |
|---|---|---|---|---|---|
| `P002-TASK-002` transformer | Fixture tests for both single-keyframe shapes, static vectors, valid maps, expression allowlist, time formatting, and deterministic ordering | Targeted runs over controlled copies of all three resources; first and second run compared | Not applicable to live gameplay; output is consumed by client integration | Malformed expression, nonfinite result, absent duration, unrelated file, and second-run cases | Transformer characterization report, input and output hashes, first diff, empty second diff |
| `BFS-REQ-017`, Cod transforms | Clip and geometry IDs, bone resolution, loop and duration, five-point equivalence, variance per clip | Controller matrix selects `idle`, `swim`, `swim_fast`, and `flop` against final resource | Interactive client records idle, ordinary swim, fast swim, water exit, flop, and water re-entry | Exact fast threshold boundary, stationary-to-moving transition, and no missing-bone warning | Cod transform table, state trace, client capture, log excerpt, packaged hash |
| `BFS-REQ-017`, Salmon transforms | Same structural and five-point checks for `spin`, `idle`, `swim`, `swim_fast`, and `flop` | Exact-name `Spin` preemption and immediate rename or removal transition | Interactive client records all five clips and name transitions in and out of water | `spin`, `SPIN`, no name, renamed while moving, renamed while beached, and threshold boundary | Salmon transform table, name-state trace, capture, log excerpt, packaged hash |
| `DEF-024-002` closure | All Cod and Salmon required clips have valid variance and references | Final controllers reach every required state without reset churn | Side-by-side baseline and fixed client evidence for idle, swim, fast, flop, and Spin | Frozen-channel fixture fails the audit; name and water recovery pass | Defect closure record linked to structural and visual evidence |
| `BFS-REQ-018`, Oceanic transforms | Seven clip IDs, durations, loops, bone resolution, five-point equivalence, and variance | Complete priority matrix and overlapping-bone controller audit | Interactive client records idle, swim, fast, bite, death, beached, and thrash | Missing fin binding, state overlap, beached during invalid grab, death during grab, and state recovery | Oceanic binding map, transform table, controller trace, captures, clean logs |
| `BFS-REQ-018`, bite timing | Unit trace proves one scheduled impact tied to one attack identity and authored impact sample | Server health delta and client bite trigger share the verified tick window | Interactive client and multiplayer capture visible jaw impact and target damage | Target leaves range, dies, unloads, changes dimension, or is replaced before impact | Tick-by-tick trigger, impact, range, target ID, and health report |
| `BFS-REQ-018`, thrash cleanup | State test binds thrash only to valid active grab state | GameTests cover timer, passenger, water, death, and unload cleanup | Multiplayer covers timeout, death, disconnect, reconnect, dimension change, and camera restoration | Empty passenger list, dead passenger, dry shark, removed shark, stale client snapshot | Cleanup matrix, server and client logs, two-client capture |
| `DEF-024-003` closure | Structural transform and binding audits pass | Every Oceanic state is reachable and bite damage or thrash coupling passes | Baseline and fixed client evidence plus multiplayer damage timing | Deliberate missing binding and stale-grab fixtures fail | Defect closure record linked to state, timing, cleanup, and visual evidence |
| `BFS-REQ-019`, target selection | Predicate cases for supported tag member, unsupported entity, same type, dead entity, cooldown, creative and spectator player | Sensor and brain activity set one valid attack target and walk target | Runtime Tiger acquires supported prey without command-forced target assignment | Missing tag fails closed; invalid prey never becomes a target | Target decision table and sensor or memory trace |
| `BFS-REQ-019`, idle and pursuit mobility | Deterministic scheduler and navigation decisions have bounded refresh and valid water destinations | GameTests measure nonzero idle displacement, horizontal pursuit, vertical pursuit, and arrival in physical contact | Interactive client shows natural idle movement and pursuit of stationary and moving prey | Blocked route, unreachable destination, prey above and below, path failure, and target loss | Position and velocity series, path state, GameTest result, client capture |
| `BFS-REQ-019`, reach and damage | Below, at, and above contact boundary; one pending attack; intended `4.0` damage once per window | GameTest verifies presentation edge, eight-tick baseline or evidence-backed final impact delay, contact revalidation, cooldown, and later independent attack | Multiplayer target health and Tiger bite presentation agree | No hit through obstruction or visible gap; cancel on death, unload, dimension, range, or identity change | Range table and tick-level health or target trace |
| `BFS-REQ-019`, recovery | State tests clear only owned target, walk, path, pending hit, and hostile state | GameTests cover target death, invalidation, disengage, failed path, and new later target | Runtime Tiger resumes idle navigation and can subsequently investigate an item or hunt again | No permanent stop, stale hostile state, or damage to replacement target | Recovery state trace and follow-on behavior capture |
| `BFS-REQ-019`, curiosity coexistence | Decision tests prove flee and combat preemption, stable intercept, timeout, and 600-tick retry boundary | GameTest with one item and one supported prey confirms item entity and count remain unchanged | Interactive client shows approach, cosmetic bite, combat interruption, and later ordinary movement | Item death, movement, dry state, path failure, timeout, same-item retry before and after cooldown | Item UUID and count record, memory trace, client capture |
| Shared-regression gate | Existing Phase 001 tests plus new blast-radius cases | Relevant shark locomotion, bite, curiosity, latch, and grab GameTests | Focused runtime smoke for every species affected by a shared edit | Revert or isolate a shared change if unrelated accepted behavior regresses | Phase 001 comparison report and updated blast-radius evidence |
| Logical-side safety | Static client-reference and side-registration audit | Forge build and data or resource loading | Dedicated server reaches ready state; client reaches playable world | Server must fail the phase on client class loading or animation resource exceptions | Server log, client log, class-reference report |
| `P002-TASK-008` artifact proof | Parse final source and packaged JSON; compare hashes | Clean `:forge:build` packages one authoritative copy | Launch the built artifact in server and client evidence environments where the harness supports it | Stale generated resource, duplicate path, wrong clip, or mismatched hash blocks integration | JAR listing, extracted hashes, parsed binding report |
| Phase exit | Documentation and complete diff audit | Required checks, review, and PR integration | Default-branch verification after GitHub merge | Failed check, unresolved review, merge conflict, unsigned tag, or missing evidence keeps phase open | Completion packet, merged PR, merge commit, verified signed tag |

### Fixtures, environments, and expected results

- Transformer fixtures cover one bare-array MoLang channel, one wrapped channel, one static wrapper, one pre-keyed channel, one loop, one nonloop, one inferred-length clip, one indeterminate-length clip, and one rejected expression. The source fixture remains immutable; outputs are compared structurally and by normalized hash.
- Animation fixtures use the exact three entry resources and their geometry files. The report enumerates every clip, every channel, every referenced bone, the effective duration, loop mode, mandated sample times, authored vector, baked vector, absolute component error, and variance witness.
- Fish state fixtures control water or land state, exact horizontal velocity around `0.0225` squared, moving state, and Salmon custom name. Expected output is one and only one matrix row per tick, without resetting the same looping clip.
- Oceanic fixtures control synchronized shark state, movement, water and ground state, bite edge, death, grab timer, passenger validity, and lifecycle invalidation. Expected output follows the explicit priority matrix and leaves no passenger or presentation state after cleanup.
- Tiger deterministic fixtures use one known member of the generated Tiger prey tag and one excluded entity. GameTest water volumes must include open horizontal pursuit, meaningful elevation change, a stationary prey case, a moving prey case, and a blocked or unreachable recovery case.
- Runtime evidence uses the supported Forge dedicated server and an interactive Forge client. Multiplayer uses a dedicated server and at least two clients when needed to distinguish attacker, passenger or target, and observer synchronization.
- Evidence is tied to the tested commit, configuration, world or fixture identifier, and relevant seed. A later resource, controller, shared movement, tag, Gradle, or packaging change invalidates the affected result.

### Required rerun order

1. Run the repository formatter or formatting check if present. If none exists, record its absence with the command used to establish that fact.
2. Run configured static analysis and lint tasks if present. Record genuine absence without inventing a gate.
3. Run transformer characterization, structural binding, five-point equivalence, state-matrix, and negative tests.
4. Run `./gradlew :forge:test`.
5. Run `./gradlew :forge:Data` if any data or generated resource changed, then inspect the complete generated diff. Animation resources alone do not justify an unnecessary generated-data rewrite.
6. Run the Phase 000 Forge GameTests and all Phase 002 fixtures.
7. Run `./gradlew :forge:build`.
8. Run `./gradlew :forge:Server` and complete the ready-state, behavior, lifecycle, and log matrix.
9. Run `./gradlew :forge:Client` in an interactive environment and complete every required clip, transition, and Tiger interaction review.
10. Complete dedicated-server multiplayer, disconnect, reconnect, dimension-change, death, unload, attack, and passenger or camera cleanup checks.
11. Inspect the interim Forge JAR, source and packaged hashes, complete Git diff, working tree, and evidence packet.

Any source or test change after a passing step restarts at the earliest affected step. Any animation-resource change restarts transformer, structural, client, build, and artifact evidence. Any common behavior change restarts unit, GameTest, server, client, multiplayer, and blast-radius evidence. A failed required command keeps the phase open; lower-fidelity proof cannot replace it.

## Documentation, Operations, and Release

- Update `DOCUMENTATION.md` with the verified animation transformation contract, required clip and controller matrices, Tiger scheduler and recovery behavior, logical-side ownership, test procedure, failure diagnostics, and operator recovery steps.
- Update `README.md` where it describes fish animation, Oceanic behavior, Tiger behavior, supported verification, or user-visible features. If no affected statement belongs in the README, record that scoped review in the completion packet rather than adding release-note clutter.
- Update `CHANGELOG.md` with concise Phase 002 behavior that actually passed. Do not claim all 0.24 defects are fixed, alter advancement or algae claims, set the final release version, or describe unverified behavior.
- Update `docs/README.md` only when Phase 000 established tracked evidence or test documentation that needs a navigable link. Preserve the existing documentation layout and filename casing.
- Store transform tables, state traces, GameTest results, runtime captures, server and client logs, multiplayer cleanup evidence, and JAR inventories at the evidence locations established by Phase 000. Do not use this protected phase file as a progress diary.
- Record no end-user configuration change and no save migration because the phase must preserve stable settings, entity IDs, NBT, and prey tags. Document recovery as reverting the phase commit or restoring verified pre-bake animation resources, then rerunning all invalidated gates.
- Build only the supported Forge interim artifact. Do not publish it, create a public release, upload to CurseForge or Modrinth, set version `0.24`, or claim plan-wide completion.
- Before commit, inspect all staged content for run worlds, logs, crash reports, caches, build outputs, environment files, credentials, absolute machine paths, owner evidence under `Content/`, and unrelated formatting.
- Commit and push verified changes on the Phase 002 branch using the configured EnVy identity and signing key. Git and GitHub metadata must follow repository communication rules and contain no assistant attribution.
- Open or update the phase pull request only after local deterministic and runtime gates pass. Link the correct phase milestone and tracked defects, obtain all required checks and one private independent review when supported, and resolve every actionable finding with rerun evidence.
- Merge through GitHub using a merge commit. Do not push an integration commit directly to `main`, bypass checks, dismiss findings, or substitute squash or rebase without authorization.
- After GitHub reports the pull request merged, fetch and verify the resulting commit on `origin/main`, verify the tested resources and checks still correspond to that result, create the signed annotated Phase 002 tag using the established phase-tag convention, push only the tag, and verify its signature and target.
- Close the three defect records and mark their Project items done only after merge, default-branch verification, and required evidence. Publish any prepared wiki update only after the approved merge.

## Risks and Evidence Invalidation

| Risk | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| GeckoLib freezes a nominally present channel | Bake supported single-keyframe MoLang shapes into real time keys and require transform variance | Five-point samples and interactive observation show a constant pose | Restore source, fix transformer, rebake only phase files | Structural, client, build, and artifact proof for that resource | Tasks 002, 003, affected state matrix, client, build, JAR |
| Baking changes authored curves or loop duration | Compare against authored evaluation and preserve metadata | Sample error, duration diff, seam snap, or changed static channel | Reject output and correct evaluation or sampling | All animation evidence for the changed file | Complete transform and visual matrix for that file |
| Transformer modifies unrelated animations | Target inputs and compare repository-wide resource hashes | Unrelated animation appears in diff or hash ledger | Restore unrelated files and narrow the interface | Tool idempotence and complete diff evidence | Transformer suite and repository diff audit |
| Bone-name correction binds the wrong fin | Use archive geometry, animation intent, orientation, and client review | One fin remains static, mirrors incorrectly, or warning persists | Revert binding and test the alternate evidence-backed mapping | Oceanic structural and visual evidence | Full Oceanic transform, controller, client, and JAR matrix |
| Multiple controllers blend incompatible Oceanic transforms | Establish exclusive presentation ownership for overlapping bones | Bone conflict, jitter, fallback locomotion during bite or thrash | Coordinate controller predicates or consolidate ownership | Oceanic state, timing, thrash, and visual proof | All Oceanic deterministic, client, and multiplayer gates |
| Tiger repair regresses all sharks through shared code | Prefer species-local fix and review blast radius before shared edits | Phase 001 control changes or other shark GameTest fails | Isolate the change or correct the shared invariant | All prior movement, bite, curiosity, and grab evidence in blast radius | Rerun affected Phase 001 and Phase 002 gates |
| Tiger target and curiosity fight over `WALK_TARGET` | Define explicit priority and ownership cleanup | Alternating paths, loops, stationary state, or stale memory | Clear only losing behavior state and reassign one stable target | Tiger pursuit, curiosity, and recovery evidence | Complete Tiger matrix and runtime capture |
| Reach fix enables remote or through-wall hits | Derive contact from collision geometry and attack point | Damage occurs outside physical contact or through obstruction | Reduce or reposition reach without reintroducing arrival deadlock | Tiger damage and shared bite evidence | Boundary, obstruction, stationary and moving prey tests |
| Delayed damage survives target lifecycle | Bind pending hit to identity and revalidate at impact | Dead, unloaded, cross-dimension, or replacement target is hurt | Cancel stale hit on every terminal transition | Damage, recovery, and multiplayer evidence | Negative lifecycle suite and multiplayer timing |
| Curiosity consumes or permanently suppresses an item | Assert item UUID, entity life, and stack count; bound cooldown and path retries | Item disappears or same item immediately reselected or movement never resumes | Cancel investigation, restore owned memory only, apply cooldown | Curiosity and Tiger recovery evidence | Full curiosity matrix plus follow-on hunt and idle checks |
| Client-only animation work breaks server startup | Keep Forge adapters and renderers client-safe and common code client-free | Dedicated server class-load exception | Move references to client boundary and rebuild | Build, server, and artifact evidence | Static audit, `:forge:build`, `:forge:Server`, client smoke |
| Multiplayer cleanup is lost during disconnect or dimension change | Server-authoritative passenger and attack cleanup with synchronized final state | Observer or reconnecting client sees stale rider, camera, attack, or thrash | Clear on lifecycle hook and resynchronize through existing protocol | Oceanic and Tiger multiplayer evidence | Complete two-client cleanup matrix |
| Runtime proof uses stale classes or resources | Clean relevant outputs and tie captures to commit and artifact hash | Source hash differs from packaged or running artifact | Rebuild and relaunch from verified artifact | All runtime evidence using stale build | Build, server, client, multiplayer, and JAR inspection |
| Phase 003 starts before integration | Enforce sequential PR, main ancestry, and tag gates | Phase 002 PR open, checks pending, main missing merge, or tag absent | Stop and complete Phase 002 integration | Downstream entry evidence | Verify GitHub merge, `origin/main`, tag signature and target |

## Phase Completion Packet

The packet must contain all of the following, stored outside the protected plan set using the conventions established by Phase 000:

- The exact Phase 001 merge commit and signed tag used as the branch base, plus proof the Phase 002 branch descended from verified `origin/main`.
- Pre-change and final hashes for `tools/bake_molang_animations.py`, the three phase-owned animation resources, and the three corresponding geometry resources.
- The pre-bake channel-shape inventory, required clip inventory, geometry-bone inventory, and explicit resolution of every mismatch, including Oceanic fin bindings.
- Transformer characterization results for supported shapes, unsupported expressions, missing duration, static and already-baked channels, deterministic formatting, targeted scope, first-run output, and second-run idempotence.
- A machine-readable or tabular transform report for all 16 required clips, with effective duration, loop mode, sample times, authored and baked values, component error, and one moving-bone variance witness per clip.
- Cod, Salmon, and Oceanic controller state traces proving every row of each presentation matrix, exact boundary behavior, one-shot stability, and recovery transitions.
- Tiger target-selection, scheduler, memory, navigation, reach, damage, cooldown, path-failure, target-lifecycle, curiosity, and follow-on recovery results.
- Forge JUnit and GameTest command lines, exit codes, test reports, fixture or world identifiers, relevant seed, expected outcomes, and failure interpretation.
- Dedicated-server ready-state log review, interactive client captures for every required animation state, Tiger idle and combat capture, Tiger curiosity capture, and clean GeckoLib warning search.
- Multiplayer evidence for bite synchronization, Oceanic grab and thrash cleanup, target death, disconnect, reconnect, dimension change, unload, camera or passenger restoration, and Tiger follow-on behavior.
- The successful Forge command sequence, including formatter and lint presence or absence, `:forge:test`, conditional `:forge:Data`, GameTests, `:forge:build`, `:forge:Server`, `:forge:Client`, and multiplayer checks.
- The interim Forge JAR path, artifact hash, entry listing, extracted phase-owned resource hashes, parsed clip and bone report, and confirmation that no duplicate or stale phase resource is packaged.
- Documentation diffs for applicable existing surfaces and a statement that no configuration migration, save migration, final release publication, or unsupported Fabric support was introduced.
- Complete working-tree and staged diff audits proving the protected `build.gradle` line ending and untracked `Content/` evidence were not included, plus generated-output and secret scans.
- Closure records for `DEF-024-002`, `DEF-024-003`, and `DEF-024-004`, each linked to the exact deterministic, client, and where applicable server or multiplayer evidence required by the master.
- Signed commits by the configured EnVy identity, pushed Phase 002 branch state, pull request and phase milestone links, required check results, independent review result and dispositions when supported or a verified capability-unavailability record, and resolved review conversations.
- GitHub merge result, verified merge commit on `origin/main`, signed annotated Phase 002 tag, tag signature verification, and proof the tag targets the merge commit.
- A downstream handoff stating that `BFS-PHASE-003` may begin only from the verified tagged Phase 002 default-branch result and that advancement acceptance evidence can now be captured without further entity-behavior changes.

## Next Transition

After every exit criterion passes, merge Phase 002 through its approved GitHub pull request, verify the resulting merge commit on `origin/main`, and create and verify the signed annotated Phase 002 tag on that merge commit. Only then may `BFS-PHASE-003` begin from the updated `origin/main`.

The first Phase 003 action is to revalidate the merged Phase 002 artifact and confirm that entity behavior no longer changes advancement visual or completion evidence. Phase 003 then executes `BFS-REQ-013` through `BFS-REQ-016`. An open pull request, queued auto-merge, pending or failed check, unresolved review, absent merge commit, missing tag, invalid tag signature, or incomplete completion packet keeps Phase 002 active and blocks the transition.
