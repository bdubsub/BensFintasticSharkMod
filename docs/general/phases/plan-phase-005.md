# Phase 005 Execution Plan

> **Plan ID:** PLAN-PHASE-005  
> **Phase ID:** BFS2-PHASE-005  
> **Owner:** Repository maintainer  
> **Classification:** MANDATORY  
> **Master plan:** [plan.md](../plan.md)  
> **Phase sequence:** 005 of 007

## Purpose and Ownership

This phase delivers the existing small algae as a water preserving supported multiface block, turns the existing large green and red algae identities into bounded kelp like columns, and makes their existing ocean generation richer without allowing natural red algae in enclosed underwater caves. It owns the executable detail for BFS2-REQ-014, BFS2-REQ-015, and BFS2-REQ-016 only. The frozen master remains authoritative for product scope, global interface signatures, host policy, acceptance, risks, and the phase sequence.

## Evidence-Based Entry State

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| OBSERVED | Current algae implementation | All three algae IDs use a private seagrass subclass, have bonemeal disabled, use cross models, and generate sparsely without an exposed floor or cave predicate. | F013, SRC-102, SRC-107 | Reinspect if the approved source revision or listed component fingerprints change. |
| OBSERVED | Generated data | Existing generation, models, tags, and loot are emitted by the stated providers and checked in under generated resources. | repository map, ModBlockstateProvider, ModItemModelProvider, ModDataPackProvider, ModBlockLoot | Reinspect after provider or generated output changes. |
| OBSERVED | Animation inputs | Green animation has ten frames and red animation has nine frames. Both use four ticks per frame and no interpolation. | F013, SRC-107 | Reinspect if either texture metadata fingerprint changes. |
| OBSERVED | Diagnostics | `BfsDebugCommands`, `BfsDebugManager`, client capture, parser, and diagnostic GameTests already provide default off bounded `bfs-debug-v2` capture. | F005, SRC-110 | Reinspect command, schema, or manager changes before diagnostics work. |
| PROPOSED | Target behavior | Supported multiface placement, column lifecycle, richer bounded generation, and cave exclusion are mandatory target behavior, not verified current state. | FIND-114, FIND-115, FIND-116 | Remains proposed until this phase produces required evidence. |

## Scope Boundaries

### Included Scope

- BFS2-REQ-014. Deliver waterlogged small algae attachment to supported horizontal sides and the top face of the block below, with face removal, placement, support update, fluid, and one item loot rules.
- BFS2-REQ-015. Deliver green and red vertical column state, save migration, placement, stacking, growth, bonemeal, harvest, support loss, loot, and natural multiblock placement using their existing public identities.
- BFS2-REQ-016. Deliver deterministic bounded exposed ocean floor generation, red cave exclusion, increased fixed seed occupancy, and unchanged authored green and red animation metadata.
- BFS2-REQ-022. Extend the inherited bounded diagnostics and support procedure only as needed to explain this phase before its real path assertions.
- BFS2-REQ-021. Produce phase documentation, provenance, integration, merged default verification, and signed phase tag evidence for the completed phase.

### Explicit Exclusions

- NG-002. Forge 1.20.1 remains the only supported target. No Fabric work or platform upgrade is part of this phase.
- NG-003. No unrelated ecosystem redesign, particles, light emission, or new algae ecology is part of this phase.
- BFS2-REQ-017, BFS2-REQ-018, and BFS2-REQ-019. Dive equipment and player movement begin only in BFS2-PHASE-006.
- BFS2-REQ-020. Phase 000 canonically owns performance diagnosis and the final whole product revalidation is a BFS2-PHASE-007 gate. This phase must investigate and repair any known generation regression it introduces before its own no mandatory defect exit, but it does not substitute its functional fixtures for the complete performance matrix.
- FUT-004. No public release publication occurs. A phase testing artifact is evidence only.

## Phase Contract

### BFS2-PHASE-005 — Underwater Algae States and Bounded Generation

**Objective:** Make every existing algae identity usable in underwater construction and deterministic ocean generation while preserving water, save compatibility, loot correctness, generation limits, and existing animated presentation.  
**Owner:** Repository maintainer  
**Dependencies:** BFS2-PHASE-004, EXT-001, EXT-002  
**Canonical requirements:** BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016  
**Documentation and release impact:** Create or update the phase milestone before implementation. Update README.md, DOCUMENTATION.md, docs/README.md, the relevant algae or world generation guide, and the existing diagnostic support guide with verified phase branch behavior. After merge, publish the matching wiki change and update roadmap and tracking state. No release publication occurs.  
**Next transition:** BFS2-PHASE-006. Its Work Packages section begins with the worn atlas mapping and player travel hook experiment.

**Entry criteria**

- The previous phase pull request is fully merged through GitHub into `1.20.1`, its resulting default commit is verified, and its signed annotated phase tag exists.
- The matching GitHub phase milestone exists or has been updated before any phase branch implementation begins.
- The source worktree is the current approved default lineage, with protected owner changes, the active PR29 worktree, `Content/`, old evidence, generated caches, and existing line ending change preserved.
- The current source, pinned loader, dependency identities, provider output, and IFC-001, IFC-007, and IFC-008 signatures are rechecked before implementation.
- EXT-001 and EXT-002 are rechecked at use time. A missing laptop or signing capability blocks phase closure at its dependent gate and is recorded as an external blocker.

**Implementation scope**

- BFS2-REQ-014. Replace the existing small algae implementation with the bounded waterlogged multiface behavior defined by IFC-007, including supported side and top attachment, normal item consumption, support pruning, water restoration, and one item per broken cell.
- BFS2-REQ-015. Retain large green and red public registry IDs and items while introducing the `single|body|top` and age state lifecycle, source water only growth, height eight cap, migration, manual stacking, bonemeal, harvest, support, and loot invariants defined by IFC-007. The eligible topmost segment includes `single`, which transforms to body plus top after successful extension or manual stacking.
- BFS2-REQ-016. Replace sparse unbounded candidate selection with two deterministic bounded attempts per existing opportunity, exposed ocean floor and source water eligibility, red open surface validation, and preserved authored frame metadata.
- BFS2-REQ-022. Deliver algae diagnostic fields and control coverage before real path assertions, keeping inherited captures default off, permission limited, bounded, redacted, and independently stoppable.
- BFS2-REQ-021. Generate data, retain source and artifact identity, document verified behavior, integrate the phase by checked merge commit, verify `1.20.1`, and create a signed annotated phase tag.

**Execution order**

1. `P005-TASK-001` executes BFS2-REQ-014 and BFS2-REQ-022 first, adding the algae diagnostic fields and the supported multiface state path before any dependent server assertion.
2. `P005-TASK-002` executes BFS2-REQ-015 after P005-TASK-001, adding compatible large column states and their lifecycle without changing public IDs.
3. `P005-TASK-003` executes BFS2-REQ-016 after P005-TASK-002, wiring bounded feature selection and generated data to the validated column contract.
4. `P005-TASK-004` executes BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016, and BFS2-REQ-022 after the diagnostic and implementation tasks, proving migration, fluid, drops, density, cave negatives, and animation using the required evidence ladder.
5. `P005-TASK-005` executes BFS2-REQ-021 and BFS2-PHASE-005 exit work after P005-TASK-004, documenting, reviewing, integrating, verifying the default branch, tagging, and publishing postmerge wiki facts.

**Required evidence**

- Unit and GameTest proof of multiface state, water, support, item count, column state transitions, age and height bounds, normal and blocked bonemeal, and old singleton save load.
- Dedicated server real path proof from P005-FX-OPEN-OCEAN and P005-FX-ROOFED-RED-CAVE that exposes floor placement, source water preservation, two to eight cell columns, no roofed red placement, and fixed seed population metrics.
- Diagnostic enable, status, permission denial, absent target, timeout, disable, output budget, redaction, disabled and enabled overhead, and captured event completeness proof before evaluating the event dependent assertions.
- A silent laptop client proof that a small algae old save normalizes to a visible supported waterlogged floor attachment, every supported side and top attachment renders coherently, and green and red single, body, and top segments render as coherent stacked columns. The same proof records existing discrete animation and resource reload behavior. Server evidence does not close any of these visual gates.
- Generated resource drift review, build and testing JAR identity, SHA 256 and SHA 512, dependency and configuration digests, documentation update, required private independent review, checked merge, default verification, signed tag, and cleanup evidence.

**Exit criteria**

- Every IFC-007 invariant is proven at its stated fidelity, including zero naturally generated red algae in the enclosed cave fixture, visible small face attachment, coherent visible large columns, and retained animation presentation.
- The fixed seed open ocean fixture has at least 1.5 times the recorded baseline occupied algae cells and a majority of natural large columns with two or more cells. Each patch performs no more than sixteen candidate attempts and each successful candidate may place a column of no more than eight cells.
- All phase evidence binds requirements, task IDs, source commit, artifact hashes, configuration, fixture, host roles, tick windows, result, and cleanup through IFC-008.
- The phase pull request has passed required checks and private independent review, merged by GitHub merge commit into `1.20.1`, has verified resulting default state, and has a signed annotated phase tag.
- No known mandatory phase-owned defect remains.

## Shared Contract Projection

```json
{
  "phase_id": "BFS2-PHASE-005",
  "canonical_requirement_ids": [
    "BFS2-REQ-014",
    "BFS2-REQ-015",
    "BFS2-REQ-016"
  ],
  "interfaces": [
    {
      "id": "IFC-001",
      "signature": {
        "capture": "start(category: Category, ticks: int[20,36000]=1200, targets: EntityRef[0,32]) -> CaptureResult",
        "record": {
          "format": "bfs-debug-v2",
          "schemaMinor": "int>=0",
          "captureId": "opaque string",
          "sequence": "long>=0",
          "tick": "long",
          "side": "server|client",
          "dimension": "registry ID",
          "entity": "session pseudonym|null",
          "entityType": "registry ID|null",
          "event": "bounded enum",
          "settingsRevision": "long|null",
          "intentId": "opaque string|null",
          "reason": "bounded enum",
          "data": "typed event payload"
        },
        "status": "status() -> enabled, side, categories, targets, remainingTicks, wallDeadline, counters, exactOutputPath",
        "stop": "stop(reason: StopReason) -> terminal completeness summary"
      },
      "acceptance_ids": [
        "BFS2-AC-022",
        "BFS2-AC-020"
      ]
    },
    {
      "id": "IFC-007",
      "signature": {
        "attachment": {
          "faces": "nonempty set<north|south|east|west|down>",
          "waterlogged": "boolean=true"
        },
        "column": {
          "segment": "single|body|top",
          "age": "int[0,25]",
          "waterlogged": "boolean=true"
        },
        "place": "place(context, existingState) -> validState|rejected(reason)",
        "generate": "generate(seed, oceanFloor, sourceWaterColumn) -> bounded placement summary"
      },
      "acceptance_ids": [
        "BFS2-AC-014",
        "BFS2-AC-015",
        "BFS2-AC-016"
      ]
    },
    {
      "id": "IFC-008",
      "signature": {
        "evidence": {
          "schema": "int=1",
          "requirementIds": "stable ID[]",
          "taskIds": "stable ID[]",
          "sourceCommit": "git object ID",
          "artifactSha256": "hex string",
          "artifactSha512": "hex string",
          "dependenciesDigest": "hex string",
          "configDigest": "hex string",
          "fixtureId": "string",
          "seed": "long|null",
          "world": "sanitized instance ID|null",
          "hostRoles": "headless_server|laptop_client[]",
          "tickWindow": "start,end|null",
          "result": "passed|failed|unverified",
          "cleanup": "complete|incomplete"
        },
        "integrate": "verified phase branch -> checked GitHub merge commit on 1.20.1 -> verified resulting default -> signed annotated phase tag"
      },
      "acceptance_ids": [
        "BFS2-AC-001",
        "BFS2-AC-020",
        "BFS2-AC-021"
      ]
    }
  ]
}
```

IFC-001 is the existing capture producer from BFS2-PHASE-000. P005-TASK-001 extends its algae category with a compact record for attempted and final state without creating a second command tree. The server produces authoritative placement, support, fluid, growth, and generation decisions. The client uses its separate capture only for actual frame presentation.

IFC-007 is produced in this phase. `down` names the support direction toward the top face of the block below. The multiface state contains a nonempty supported face set and waterlogged true. The large identities use one item per segment and `single|body|top`, not a second public body block. Existing singleton saves normalize to valid `single` states while preserving water. The topmost eligible segment is either `single` or `top`. Its successful extension or valid manual stack changes the former singleton to body and creates a top. Only that topmost segment may extend into a source water cell, and every extension stops at total height eight. Age 25 stops random growth only; manual stacking and bonemeal still require a valid source water destination and remaining height capacity.

IFC-008 binds each retained phase packet to its candidate. P005-TASK-005 consumes it for integration and P006 and P007 consume phase evidence as historical upstream proof, never as permission to reuse a mutable artifact or bypass their own checks.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| IFC-001 | BFS2-PHASE-000 | Existing `bfs-debug-v2`, permission level 2, 20 to 36,000 tick capture, target maximum 32, wall deadline, bounded writer and parser are present. | Read current dispatcher, manager, parser, and associated tests before extending algae events. | Stop dependent diagnostic assertions. Repair the shared extension in P005-TASK-001 without a parallel capture system. |
| IFC-007 | Frozen master | Exact attachment, column, placement, and generation signature is unchanged. | Compare declaration byte for byte in the active plan before implementation. | Treat mismatch as a plan conflict and return it to the coordinator. |
| IFC-008 | BFS2-PHASE-000 | Candidate identity and integration evidence schema are available. | Validate all required identity fields before retaining phase evidence. | Mark affected evidence unverified and rerun after identity recovery. |
| Approved source and algae inputs | BFS2-PHASE-004 and source baseline | Existing public IDs, item identities, animated metadata, and generated data remain available. | Recheck fingerprints and resource locations from repository map. | Stop migration or asset assertions until source change is classified. |
| EXT-001 | Owner environment | Laptop desktop, discrete renderer, private connection, and existing client diagnostic controls can be revalidated only at the named client gate. | Discover actual laptop anchor, renderer, window, PID, stream, and local client control dispatcher at use time. | Block phase closure on the required face and column visual gate. Do not use node-1 graphics or a singleplayer fallback. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| IFC-007 multiface attachment | BFS2-PHASE-006, BFS2-PHASE-007, players | Existing small algae identity accepts only supported side or top faces, always preserves water, has one cell loot semantics, and visibly normalizes old saves. | Existing registry ID retained. Saved existing small algae state maps to a valid supported attachment. | P005-TASK-004 migration, fluid, support, loot, and client attachment fixtures. |
| IFC-007 column state | BFS2-PHASE-006, BFS2-PHASE-007, players | Existing large IDs represent `single|body|top`, age zero through 25, and waterlogged true, with bounded lifecycle. | One public item per segment. No new body ID. | P005-TASK-004 stack, growth, break, and reload fixtures. |
| IFC-007 generation summary | BFS2-PHASE-007 and operators | Seeded ocean generation stays within patch, height, water, and exposed red surface limits. | No retroactive generation in existing chunks. | P005-TASK-004 fixed seed open ocean and roofed cave evidence. |
| Algae diagnostic record fields | Support operators and BFS2-PHASE-007 | Default off, permission checked, bounded records expose intended and actual action with reason codes. | `bfs-debug-v2` preserves parser compatibility through a schema minor extension. | P005-TASK-001 command and parser regression. |
| IFC-008 phase packet | BFS2-PHASE-006, BFS2-PHASE-007 | Source, artifact, environment, fixture, outcome, and cleanup are trustworthy and sanitized. | Schema 1 fields all present. | P005-TASK-005 postmerge packet. |

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| P005-TASK-001 | BFS2-REQ-014, BFS2-REQ-022 | Implement the small algae multiface state, supported placement and support removal rules, fluid preservation, normal item consumption, and one item cell loot. Extend bounded algae diagnostics before assertions. | IFC-001, IFC-007, source algae block and loot registrations, approved default source. | Valid waterlogged attachment state and diagnostic event path. | `ModBlocks`, existing algae block implementation, `ModBlockLoot`, `BfsDebugCommands`, `BfsDebugManager`, parser, `BfsGameTests`, IFC-001, IFC-007. | State property tests plus real server GameTest through actual placement and neighbor update paths. |
| P005-TASK-002 | BFS2-REQ-015, BFS2-REQ-022 | Implement green and red column states, legacy singleton normalization, topmost `single` or `top` growth and bonemeal, singleton to body and top transition, manual stacking, source water constraint, eight cell cap, harvest, support loss, drops, and water restoration. | P005-TASK-001, IFC-007, current public IDs and items. | Compatible column lifecycle for both algae colors. | Existing algae block implementation, registration, loot, tags, `BfsGameTests`, IFC-007. | Both colors cover legacy singleton, successful singleton random growth and bonemeal, valid singleton stack, age 25 random growth and eight cell extension negatives, and break position and neighbor cascade cases. |
| P005-TASK-003 | BFS2-REQ-016, BFS2-REQ-015 | Implement deterministic exposed floor candidate selection, depth limited multiblock placement, red surface continuity rejection, and provider updates. Preserve authored animation metadata unchanged. | P005-TASK-002, IFC-007, `AlgaePatchFeature`, `ModFeatures`, existing providers and tags. | Bounded placed feature and checked in generated data for richer algae distribution. | `AlgaePatchFeature`, `ModFeatures`, `ModBlockstateProvider`, `ModItemModelProvider`, `ModDataPackProvider`, `ModBlockLoot`, generated resources, IFC-007. | Fixed seed server fixtures, generated data run and drift review, exact metadata byte comparison. |
| P005-TASK-004 | BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016, BFS2-REQ-022 | Verify small and large legacy migration, fluid, placement, support, drop, singleton growth, stack transitions, bounded generation density, cave negatives, diagnostic coverage, visible face attachment, visible columns, and animation. | P005-TASK-001, P005-TASK-002, P005-TASK-003, IFC-001, IFC-007, EXT-001 for visual proof. | Sanitized phase evidence with passed headless and required passed laptop visual results. | Unit tests, `BfsGameTests`, parser tests, generated resources, dedicated server, laptop client, IFC-008. | Node-1 headless GameTests and required silent laptop presentation procedure with named fixtures and deadlines. |
| P005-TASK-005 | BFS2-REQ-021, BFS2-REQ-022, BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016 | Update documentation and support instructions, inspect full diff and generated drift, package evidence, perform private review, merge the phase, verify default, tag, and publish the matching wiki facts after merge. | P005-TASK-004 passed required gates, EXT-002, IFC-008. | Merged phase, signed tag, documentation and postmerge wiki state backed by evidence. | README.md, DOCUMENTATION.md, docs/README.md, algae or world generation guide, support guide, issue, milestone, pull request, wiki, IFC-008. | Required checks, review resolution, GitHub merge state, `1.20.1` commit and tag verification, artifact checksums, postmerge wiki comparison. |

P005-TASK-001 is a hard diagnostic and semantic dependency for all later state assertions. P005-TASK-002 may update shared state and loot in one coherent change but must retain P005-TASK-001 face semantics. P005-TASK-003 may proceed only after the column contract exists. P005-TASK-004 begins only after implementation compiles and records actual fixtures. P005-TASK-005 begins only after evidence is complete. A failed migration, water, cave, or animation gate restores the affected source to a known compatible design and reruns only the changed task plus every dependent verification. No task safely proceeds in parallel with an unreviewed public state signature change.

## Architecture and Implementation Boundaries

The common block and resource layer owns serialized block states and public registry identities. Forge owns feature registration, data generation, GameTests, and diagnostic command integration. Server world state is authoritative for placement, neighbor updates, random tick growth, bonemeal, generation, water, drops, and save decoding. Client resources only render the accepted state and existing animation metadata. Client code must remain out of common and dedicated server loading paths.

The small algae state uses the IFC-007 nonempty `faces` set with only north, south, east, west, and down. A click may add one valid missing face to an occupied cell but consumes an item only when it creates that new face. Invalid support pruning leaves other valid faces intact. The last invalid face restores water. No underside support is accepted. Fluid checks occur before state mutation so a rejected placement does not consume an item or replace nonwater content.

Large green and red retain their registry and item identities. State derives segment type from adjacent valid segments rather than introducing public body blocks. A legacy singleton loads as `single`, waterlogged true when the retained location contains water. The topmost segment, either `single` or `top`, is eligible for random tick or bonemeal extension. A successful extension turns an eligible singleton into body, creates an adjacent top, requires a source water destination and stays within total height eight. Ordinary manual, random, and bonemeal extension may fill the last available source water cell; they do not require another source cell above the resulting top. Valid manual stacking performs the same singleton to body and top transition. Random ticks use chance 0.14 and stop at age 25. Bonemeal attempts one cell and does not consume itself when blocked. Break and support updates make the lower supported portion remain valid, remove upper invalid segments, restore water in every removed location, and create no duplicate update drops.

Generation uses tagged existing ocean biomes and valid exposed ocean floor only. Each prior placement opportunity becomes two bounded patch attempts. Each patch performs at most sixteen candidate attempts, and a successful candidate may place one column of two through eight cells. Each natural column must fit actual water depth while retaining source water above its top. Red algae additionally requires an unbroken source water column to the actual open surface with no solid roof or occupied obstruction above the heightmap surface. This predicate applies only to natural generation, never manual cave decoration. Determinism is based on the supplied seed and traversal boundaries, not entity order or wall time. Existing chunks are not repopulated.

Existing green and red texture animation metadata remains ten and nine discrete frames, four ticks each, no interpolation. Data generation may change state, model, loot, tag, and feature outputs required by the source change but must not rewrite immutable authored pixels, clip timing, or texture metadata. The plan does not prescribe a new art path, particle system, or renderer behavior.

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| BFS2-RISK-008. Face placement targets dry, unsupported, occupied, or already present face. | `algae_place` diagnostic reports target fluid, requested face, existing set, accepted state, and bounded reason. | Reject without item consumption or state mutation. Accept only a new valid supported face in water. | Leave existing state untouched and rerun P005-FX-FACE after corrective change. | Server GameTest invokes the normal item placement path for every allowed face, an underside negative, dry negative, and duplicate face negative within 20 ticks each. |
| BFS2-RISK-008. Neighbor update removes a support while other faces remain. | `algae_support` reports before and after face sets, support direction, removed faces, fluid outcome, and reason. | Preserve supported faces. Restore water only when no face remains. | Recalculate from neighboring support rather than dropping the entire cell early. | P005-FX-FACE removes supports in two orders, settles within 20 ticks, and asserts face set, fluid, and one final cell drop. |
| BFS2-RISK-008. Prechange small or large singleton save or reload decodes to invalid state. | `algae_migrate` records source state, mapped attachment or segment, support, water state, and repair reason. | Small algae normalizes to a valid supported floor attachment with water and no duplicate drop. Each large color normalizes to `single` with retained water, never duplicate IDs or spawn drops on load. | Mark migration failure as a compatibility defect, retain fixture, repair decoder, and rerun save reload and all lifecycle cases. | P005-FX-FACE loads serialized prechange small algae on its valid floor attachment. P005-FX-COLUMN loads both green and red serialized prechange singletons. Each restart asserts final state and no additional item entities within 40 ticks. |
| BFS2-RISK-008. Column grows through non source water, exceeds cap, or consumes failed bonemeal. | `algae_grow` records segment, topmost eligibility, age, height, destination fluid, growth source, accepted state, and reason. | Only the topmost `single` or `top` grows into source water. Successful singleton growth changes it to body and top. Random chance is 0.14, age ends at 25, height ends at eight, and failed bonemeal remains in hand. | Keep column unchanged and restore no extra water or drops. | P005-FX-COLUMN verifies successful green and red singleton random growth, successful singleton bonemeal, valid manual stack, and blocked destination or height cases at ages zero, 24, and 25 plus heights one, seven, and eight. At height below eight, both colors must permit manual stacking and bonemeal into the last source water cell even at age 25. A selected random growth attempt at age below 25 must also succeed there; age 25 rejects random growth. The same lack of water above the resulting top must reject natural generation. |
| BFS2-RISK-008. Harvest or support collapse duplicates items or loses water. | `algae_remove` records removed coordinates, cause, loot count, and final fluid. | One permitted item per removed segment under ordinary loot context, no update duplication, lower supported segments retained. | Abort evidence on mismatch, restore fixture from seed, correct only removal or loot path, and rerun neighboring break permutations. | P005-FX-COLUMN top, middle, base, and external support breaks settle within 40 ticks with exact entity counts and water assertions. |
| BFS2-RISK-008. Red feature reaches a roofed underwater cave or generates over resource bounds. | `algae_generate` records seed, candidate, floor, depth, height, source water continuity, surface result, attempt index, cell count, and rejection reason. | Roofed cave produces zero red placements. Each patch uses at most sixteen candidates and each column at most eight cells. | Reject candidate without mutation and continue only remaining bounded candidates. | P005-FX-ROOFED-RED-CAVE and P005-FX-OPEN-OCEAN complete within 600 server ticks and assert zero cave red placement plus all counters within cap. |
| BFS2-RISK-008. Richer generation changes animation metadata or fails visual presentation. | Asset digest comparison and client capture frame, attachment face, segment, stack order, and resource reload observations. | Small algae visibly attaches to all supported sides and top, green and red stacks have coherent single, body, and top rendering, green remains ten frames and red nine, each at four ticks with no interpolation. | Restore the authored metadata or model and invalidate visual evidence before rerun. | P005-FX-ANIMATION observes all face and column states plus two full animation cycles after resource reload. The server fixture proves selected resources only, not rendered motion. |

## Diagnostics and Debugging

**Requirement IDs:** BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016, BFS2-REQ-022  
**Task IDs:** P005-TASK-001, P005-TASK-002, P005-TASK-003, P005-TASK-004, P005-TASK-005  
**Controls:** Reuse `/bfs debug on algae <ticks> [targets]`, `/bfs debug status`, and `/bfs debug off` under permission level 2. Console uses the discovered dispatcher form without a leading slash and may supply no entity target for world generation. P005-TASK-001 adds algae event filtering within the inherited category and parser support. Capture starts default off, accepts 20 through 36,000 ticks, defaults to 1,200 ticks, selects no more than 32 targets, and stops at wall deadline, timeout, reload, shutdown, target removal, or explicit idempotent off. The laptop uses the existing local `/bfs debug client on`, `/bfs debug client status`, and `/bfs debug client off` controls, separate from server algae capture.  
**Signals:** The server emits `algae_place`, `algae_support`, `algae_migrate`, `algae_grow`, `algae_remove`, and `algae_generate`. Each record has IFC-001 sequence, tick, capture ID, pseudonymous target when applicable, dimension, event, bounded reason, intended state and final state. Event data includes face set, water state, segment, age, height, support direction, source water result, candidate and attempt counters, surface continuity result, placed cell count, and item count in blocks or items as appropriate. Client capture adds resource ID, frame index, elapsed client ticks, interpolation flag, attachment face, column segment and adjacency, stack order, and reload generation, without server mutation. Client capture has the existing tighter 90 second and 32 MiB bounds.  
**Collection procedure:** The following steps enable the smallest server and separate client captures, reproduce named fixtures, inspect bounded correlated records, stop both captures, retain a sanitized packet, and verify teardown.  
**Headless verification:** On node-1, inspect the actual Gradle task graph, use the registered server GameTest entry point and a disposable no GUI server only if needed, then exercise normal placement, neighbor, random tick, bonemeal, save, and feature paths. It proves authoritative block and generation behavior, not rendered animation.  
**Client verification:** The required residual claims are visible supported small algae side and top attachment, visible prechange small algae normalization, coherent green and red single body top columns, and discrete animation after resource reload. Use only EnVy's verified laptop and a matching dedicated node-1 server. Before observation, discover the supported automatic connection mechanism, connect to the exact private endpoint, and confirm the intended player joined the correct world in both server evidence and laptop client state. Enable local `/bfs debug client on`, verify client status and its 90 second and 32 MiB bounds, collect client logs and targeted visuals, then stop and verify client capture. Loss of EXT-001 blocks phase closure while independent headless evidence continues.  
**Client audio isolation:** For every phase client launch, discover a dedicated disposable laptop instance beneath the verified project anchor and set its pinned version master audio output to zero before launch. After launch, bind the owned candidate window address, class, title, and PID using `hyprctl clients -j`. Correlate only that PID and its verified descendants to the PipeWire node or PulseAudio sink input. Mute it with `wpctl set-mute <discovered-node-id> 1` or `pactl set-sink-input-mute <discovered-input-id> 1`, read back verified muted state before automatic connection or any assertion, and repeat binding, muting, and readback after resource reload, reconnect, device change, or replacement stream. Never mute a default sink, microphone, personal client, unrelated application, or system output. At teardown stop the owned client and watcher, confirm its process and playback stream disappear, and remove only the instance audio state. Ambiguous identity or mute state stops this owned client and blocks phase closure.  
**Budgets and privacy:** Disabled capture avoids formatting, traversal, packet, and file work. Enabled capture keeps inherited entity, duration, queue, record, session, directory, and wall deadline bounds. World generation records coordinates only as sanitized relative fixture coordinates and never stores private addresses, chat, inventory, raw saves, credentials, or entire logs. Full output, dropped records, I O failure, missing target, permission denial, and timeout result in a terminal incomplete summary, never a passing capture. Preserve only sanitized decisive excerpts and IFC-008 identity fields after final consumption.  
**Regression and support:** P005-TASK-004 covers on, status, off, denial, absent target, timeout, reload reset, output limit, parser compatibility, redaction, disabled versus enabled overhead, and unchanged block results. P005-TASK-005 adds exact enable, status, reproduction, disable, output location, redaction, and support packet instructions to the existing support guide, then links only merged facts from user documentation.

| Signal | Source and unit | Expected observation |
|---|---|---|
| `algae_place` | Server, one record per normal placement attempt | Valid supported water face yields a nonempty final set. Dry, unsupported, underside, and duplicate requests report rejection with no consumption. |
| `algae_support` | Server, one record per neighbor reconciliation | Each removed support removes only its face. Final empty set restores water. |
| `algae_migrate` | Server, one record per legacy state load | Prechange small algae becomes a visible supported floor attachment with water and no loot. Prechange green and red singletons become `single` with water and no loot. |
| `algae_grow` | Server, one record per random tick or bonemeal attempt | Topmost `single` or `top` extends into source water. A successful singleton becomes body plus top. Height never exceeds eight and age never exceeds 25. |
| `algae_remove` | Server, one record per segment removal | Removed segment count equals allowed normal loot count and every removed cell is water. |
| `algae_generate` | Server, one summary per patch attempt | At most sixteen candidates and eight cells per column. Roofed red candidate rejects. |
| client algae presentation | Client, one record per attachment, segment, or frame transition in client ticks | All supported small faces, normalized old small algae, and green and red single body top stacks render coherently. Green visits frames zero through nine and red zero through eight at four tick intervals, with interpolation false before and after resource reload. |

1. Register a unique disposable scratch directory inside the verified host project anchor before each bounded suite. Record candidate commit, JAR hashes, Forge 47.2.0, Java 17, dependency and configuration digests, fixture ID, seed, runtime path, expected cleanup targets, and owned process IDs. For a server fixture set and read back `eula=true` before launch. Do not use personal worlds, shared caches, active PR29 worktree, or a preexisting test directory as disposable state.
2. Before real path capture, run P005-TASK-001 diagnostic self tests. From the authorized server console enable `bfs debug on algae 1200`, inspect status for server side, bounded remaining ticks, counters, and exact output path, then verify permission denial for a level below two and a no target generation session. If the command, parser, or output path fails, stop dependent diagnostic evidence and repair P005-TASK-001.
3. Run the deterministic server fixtures through normal handlers. P005-FX-FACE loads a serialized prechange small algae state at one valid floor attachment in source water, then exercises each supported side and down attachment, invalid underside and dry placement, duplicate face, two support removal orders, and final break. P005-FX-COLUMN loads serialized prechange green and red singletons, verifies valid manual singleton stacking, successful singleton random growth and bonemeal, blocked destination and height cases at ages zero, 24, and 25 and heights one, seven, and eight, with age 25 stopping random growth only, then breaks top, middle, base, and support. Also verify last source cell extension for both colors with air above the destination: manual stacking and bonemeal succeed below height eight, including age 25; a selected actual random tick succeeds below age 25. Natural generation rejects that same missing source water above the top. Use normal item, neighbor, tick, save, and loot paths, never direct final state assignment as proof.
4. Run P005-FX-OPEN-OCEAN on seed 240024 over exactly chunks X and Z 0 through 3 inclusive, which are blocks X and Z 0 through 63 inclusive, a 64 by 64 block region containing 16 chunks. Freeze those bounds, stable z then x traversal, registered placed feature opportunity count, and eligible exposed source water floor position denominator in the fixture header before comparison. Terrain preflight records the first qualifying tagged ocean chunk only as metadata; it never shifts the region origin. No current evidence proves a nonzero baseline in this sample. Reject preflight if the region has no eligible floor positions, the actual unmodified baseline has zero occupied algae cells, or baseline and candidate registered placement opportunities differ. Any replacement region must be fixed from baseline only terrain and occupancy evidence before inspecting candidate results, retain the original rejection, and repeat preflight with identical declared bounds for both runs. Run the unmodified baseline and candidate through the actual registered placed feature path over the same region, config, seed, 16 chunk count, and registered opportunity count. The candidate retains two patch attempts per original placement opportunity. Count unique occupied algae block positions after 600 server ticks, divide by the same eligible floor position denominator, record all column heights, and require candidate occupied cells at least 1.5 times baseline with a majority of natural large columns at least two cells tall. Never select a candidate favorable sample or weaken the ratio.
5. Run P005-FX-ROOFED-RED-CAVE using seed 240024 and the same registered feature path. Its 64 by 64 block fixture has four equal candidate lanes: a solid roof directly above source water, a solid roof above the heightmap surface, flowing water, and blocked water. It also includes a genuinely open ocean control lane with unobstructed source water to the actual surface and valid depth for a two through eight cell column. Bound each fixture to 600 server ticks. Assert zero red placement in every negative lane, an accepted open control when its bounded candidate is selected, no more than sixteen candidate attempts per patch, and no more than eight cells for each resulting column.
6. Inspect capture correlation, event reason, final state, counters, output footer, and parser result. Assert every required state transition before accepting game test outcomes. A missing footer, drop counter, parse error, I O failure, unequal opportunity count, or unrecorded fixture coordinate makes the capture incomplete.
7. For the required client presentation proof, verify the laptop host, desktop and discrete GPU capability, matching source and artifact, isolated runtime, exact private node-1 endpoint, and dedicated server readiness. Set the isolated client master output to zero before launch. Launch the owned client, bind its exact window and process, verify the actual discrete renderer, then correlate, mute and read back only its application stream. Use the discovered supported automatic connection method and confirm the intended player joined the correct world on both server and laptop. Only after that join, enable `/bfs debug client on` and verify local status with its 90 second and 32 MiB limits. Observe P005-FX-ANIMATION after normal placement and save reload for each small face and each green and red single body top state. Reload resources, reverify and remute any recreated application stream, then collect two full animation cycles, targeted visuals and client records. Compare resource identity to server selection. Repeat the join and mute checks after reconnect, and start any new local capture only inside the joined world. Server evidence does not close presentation.
8. Disable server capture through `bfs debug off` and local capture through `/bfs debug client off`, verify both status reports stopped and no subsequent matching record is appended. Retain a minimal sanitized packet containing fixture, source and artifact identities, requested requirement and task IDs, bounded tick window, decisive records, required client proof, and cleanup result. Redact private endpoint, paths, player data, and unrelated logs.
9. On every success, failure, timeout, cancellation, and interruption path, stop only the owned server, client, watcher, and harness; confirm process and stream exit; retain required sanitized evidence at its intended destination; remove exact test created runtimes, worlds, logs, crash reports, configs, screenshots, traces, scratch reports, downloads, and incidental bytecode. Recheck that each disposable path is absent. Cleanup incomplete keeps the test result separate and prevents phase closure.

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Execution host and prerequisites | Evidence artifact |
|---|---|---|---|---|---|---|
| BFS2-REQ-014, P005-TASK-001 | State serialization and loot table tests cover allowed face enum, nonempty set, waterlogged true, one cell drop, and no consumption on rejection. | Parser and diagnostic command tests cover server algae and separate client control enable, status, off, denial, absent target, timeout, and output footer. | P005-FX-FACE server GameTest loads prechange small algae at valid floor support, invokes normal item placement and neighbor update paths, and settles each scenario within 20 ticks, final support loss within 40 ticks. | Dry, underside, unsupported, duplicate face, support removal order, and prechange small load assert unchanged state or selective pruning, water, and no duplicate drop. | node-1, verified no GUI task graph, disposable fixture and cleanup registration. | Sanitized GameTest report, parser result, bounded capture, IFC-008 record. |
| BFS2-REQ-015, P005-TASK-002 | Tests cover green and red legacy decode, segment and age serialization, chance 0.14 selection, singleton to body top transition, height eight, age 25 random growth rejection, and blocked bonemeal retention. Last source cell manual and bonemeal extension remain valid below the height cap, including age 25; selected random extension is valid below age 25. | Generated loot and tag output is inspected with provider result. | P005-FX-COLUMN server GameTest uses normal singleton stack, random tick, bonemeal, break, support, save, reload, and water paths. Growth and break settle in at most 40 ticks, reload within one bounded server restart. | Non source water, obstruction, height eight, age 25 random growth, middle and base breaks, plus both large prechange singleton loads assert no duplicate drops and water restoration. | node-1, exact candidate and no GUI server only configuration. | Serialized fixture, GameTest report, capture records, cleanup confirmation. |
| BFS2-REQ-016, P005-TASK-003 | Deterministic feature helper tests assert two attempts, sixteen candidate attempts, eight cells per successful candidate, source water above, and red surface predicate. Metadata digest comparison asserts unchanged frame counts, timings, and interpolation field. | `:forge:Data` followed by checked generated resource drift review confirms providers are source of truth. | P005-FX-OPEN-OCEAN uses seed 240024, the fixed 16 chunks X and Z 0 through 3, blocks X and Z 0 through 63, a 64 by 64 block sample, equal baseline and candidate opportunities, and a nonzero baseline. P005-FX-ROOFED-RED-CAVE uses the same seed and 64 by 64 negative plus open control lanes. Each completes within 600 server ticks through the registered placed feature path. | Direct roof, roof above heightmap surface, flowing water, blocked water, depth boundary, and candidate cap cases reject with a reason. | node-1 headless only, exact seed and disposable world. | Density denominator and count report, generated diff review, metadata digest, capture footer, cleanup confirmation. |
| BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016, P005-TASK-004 | P005-FX-ANIMATION client diagnostic parser validates attachment face, prechange small normalization, column segment and stack order, frame order, and reload generation. | Matching artifact and resource identity are compared on server and client. | Required split host proof observes side and top small algae, normalized old small algae, and green and red single body top columns before and after resource reload, plus two full animation cycles. Server readiness, supported automatic connection, and player joined world proof are required on both sides. | Missing discrete renderer, window to stream identity, mute state, private connection, client capture, face, or column proof blocks phase closure and records EXT-001. No fallback is allowed. | node-1 dedicated server and EnVy's Linux laptop only, verified private endpoint, matching hashes, silent isolated client procedure. | Sanitized client log and targeted visuals, server and client join evidence, mute verification, IFC-008 record. |
| BFS2-REQ-021, BFS2-REQ-022, P005-TASK-005 | `git diff --check`, generated drift review, secret scan of tracked changes, and documentation link validation. | Required Gradle compile, tests, data, GameTest server, and build run only after actual task graph inspection. | Artifact archive integrity and listing plus SHA 256 and SHA 512 bind the testing JAR. | A failed check, incomplete capture, unreviewed change, unresolved conversation, missing signature, or cleanup failure blocks merge and tag. | node-1 for headless checks. EXT-002 before commit, pull request, merge, or tag. | Check reports, hashes, review record, merged default commit, signed phase tag, documentation and wiki proof, cleanup record. |

## Documentation, Operations, and Release

The phase entry gate creates or updates the milestone before implementation. P005-TASK-005 confirms that existing milestone and updates its integration state. It updates README.md when player placement, growth, harvest, or availability changes require user guidance. It updates DOCUMENTATION.md, docs/README.md, and the existing algae or world generation guide with verified phase branch behavior: exact supported faces, visible old save normalization, source water constraints, singleton to body top transition, column cap, cave generation boundary, and no retroactive population rule. It updates the established debug support guide with separate server algae and local client capture procedures, output location discovery, redaction, and minimum evidence packet.

Before merge, the pull request and milestone describe only verified phase branch behavior and do not claim merged or released status. After GitHub reports the merge and `1.20.1` has been verified, publish the corresponding wiki change from tracked documentation, update roadmap and milestone state, and preserve issue 28 unless its own acceptance is truly complete. Do not publish a release or call the testing JAR delivered public release content.

## Risks and Evidence Invalidation

| Risk ID and owner task | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| BFS2-RISK-008, P005-TASK-001 and P005-TASK-002 | Preserve existing IDs, normalize legacy state, source water checks, selective support pruning, and ordinary loot context. | Algae records, state fixture, exact item counts, fluid checks, and save reload report. | Correct the narrow state or loot path, rebuild fixture, rerun every dependent lifecycle test. | State, migration, fluid, growth, and drop evidence for altered algae identity. | P005-TASK-004 complete state and lifecycle matrix. |
| BFS2-RISK-008, P005-TASK-003 | Two attempts, sixteen candidate attempts, eight cells per successful candidate, source water above, and red open surface predicate bound generation. | Generation summary includes every cap, seed, recorded chunk sample, floor, depth, surface, opportunity count, and reason. | Reject invalid candidate and repair selection without bypassing water or cave rule. | Density, cave exclusion, and deterministic seed evidence. | P005-TASK-004 open ocean and cave fixtures plus data regeneration. |
| BFS2-RISK-012, P005-TASK-001 and P005-TASK-004 | Default off bounded capture and parser compatibility precede behavior assertions. | Status, counters, output footer, denied command, drop count, overhead parity, and redaction test. | Stop incomplete capture, retain sanitized reason, repair diagnostics, then rerun affected real path fixture. | Any assertion relying on incomplete or schema incompatible capture. | Diagnostic self test and all affected GameTests. |
| BFS2-RISK-013, P005-TASK-004 | Host policy, matching artifact, prelaunch zero, verified application mute, automatic connection, joined world proof on both sides, and owned resource cleanup. | Desktop and renderer checks, window PID stream binding, muted readback, client status, server and client joined world state, process and path teardown. | Stop owned client on ambiguity, preserve headless results, record EXT-001, and block closure until the required visual gate is recovered. | Client face, column, animation, and any split host evidence. | Full silent laptop procedure after capability recovery. |
| BFS2-RISK-011, P005-TASK-003 and P005-TASK-005 | Preserve generation bounds and do not confuse feature evidence with performance acceptance. | Generation counters, current phase regression investigation, and Phase 000 owned performance evidence with final whole product revalidation. | Repair a known phase generation regression before phase closure. Preserve unchanged full matrix ownership for its canonical and final gates. | No phase 005 functional proof is relabeled performance proof. | Rerun phase generation checks immediately and retain Phase 000 and BFS2-PHASE-007 matrix gates. |

## Phase Completion Packet

Before closure, retain outside the protected plan set a sanitized IFC-008 packet containing requirement IDs BFS2-REQ-014 through BFS2-REQ-016, task IDs P005-TASK-001 through P005-TASK-005, source commit, testing JAR SHA 256 and SHA 512, dependency and configuration digests, fixture IDs, fixed seed, sanitized world IDs, host roles, measured tick windows, result, and cleanup result.

The packet includes source and generated resource diff review, metadata digest comparison, unit and GameTest reports, parser and diagnostic self test evidence, seed 240024 open ocean header with bounds X and Z 0 through 3 chunks and 0 through 63 blocks, the qualifying ocean chunk as metadata, 16 chunk count, eligible floor denominator, registered opportunity count, independently proven nonzero baseline, and candidate density report, roofed cave negative and open control report, small and large migration and loot fixtures, required client face, old save, column, animation, reload, automatic connection, joined world, and mute proof, artifact archive test and listing, documentation links, required check state, required private independent review result, GitHub merge commit, verified `1.20.1` commit, and signed annotated phase tag.

Each verification declares exact disposable resources before start. It retains only sanitized reports, hashes, required visual proof, and requested deliverables. It stops and confirms owned server, client, watcher, and harness exit after the last consumer, removes exact test created worlds, runtimes, logs, crash reports, configuration, screenshots, traces, scratch reports, downloads, and bytecode, then verifies absence on every used host. It preserves source, tracked fixtures, owner data, personal instances, active worktrees, shared caches, historical evidence, and tags. Any exact leftover is recorded with owner and recovery action, and cleanup incomplete blocks phase closure.

## Next Transition

After every Phase 005 implementation, evidence, review, required check, GitHub merge, resulting `1.20.1` verification, signed annotated phase tag, postmerge documentation and wiki update, and cleanup gate passes, atomically advance the active phase cursor to BFS2-PHASE-006. Reread `phases/plan-phase-006.md` and begin its worn atlas mapping and player travel hook experiment. Do not create a Phase 006 branch or start its implementation before this phase is fully merged and tagged.
