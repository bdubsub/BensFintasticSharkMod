# Phase 006 Execution Plan

> **Plan ID:** PLAN-PHASE-006  
> **Phase ID:** BFS2-PHASE-006  
> **Owner:** Repository maintainer  
> **Classification:** MANDATORY  
> **Master plan:** [plan.md](../plan.md)  
> **Phase sequence:** 006 of 007

## Purpose and Ownership

This phase delivers four usable dive armor pieces, faithful worn presentation from the supplied art, a full suit seabed movement mode, and the exact player owned oxygen lifecycle. It owns the detailed execution of BFS2-REQ-017, BFS2-REQ-018, and BFS2-REQ-019. The master remains authoritative for product scope, decisions, IFC signatures, global risks, host policy, and sequential integration. This blueprint neither changes those contracts nor treats a proposed repair as verified behavior.

## Evidence Based Entry State

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| OBSERVED | Dive art | Four item PNG sources and a 1280 by 1280 JPEG worn atlas exist. The atlas has no alpha or supplied geometry. | FIND-023, SRC-004 through SRC-009 | Recheck source hashes before importing or transforming an asset. |
| OBSERVED | Current armor path | No dive armor exists. Prismarine armor has a GeckoLib item, model, and renderer path that must remain a regression fixture. | F014, repository map Armor row | Reinspect after the Phase 005 merge or a source fingerprint change. |
| OBSERVED | Player hook constraints | Forge 47.2.0 provides breathing, break speed, interaction, equipment, and lifecycle events. Gravity alone cannot replace vanilla water travel or fluid jumping. | F014, SRC-108 | P006-TASK-001 must test the actual selected hook on the current candidate. |
| PROPOSED | Movement and reservoir | IFC-006 requires one authoritative apply decision, a 6000 tick reserve, server to owner synchronization, and conservative corruption recovery. | IFC-006, FIND-118, FIND-119 | Every value remains unverified until the phase fixtures execute. |
| OBSERVED | Diagnostics | The reusable server capture has permission level 2, bounded on, status, and off commands and separate client capture. `dive` is a required local category extension. | F005, SRC-110 | Recheck parser and manager signatures at the candidate revision. |
| OBSERVED | Client gate | Laptop host, actual renderer, isolated runtime path, private endpoint, window, and app stream are execution discoveries, not established evidence. | EXT-001, SRC-008 | Verify immediately before each client launch. |

## Scope Boundaries

### Included Scope

- BFS2-REQ-017. Register the four named pieces with unchanged supplied item PNG bytes, a JPEG derived renderer texture preserving decoded RGB, documented compatible armor geometry and UVs, ordinary equipment access, and no recipe.
- BFS2-REQ-018. Apply one shared server and client eligibility predicate and one player only seabed travel path for a full suit in survival or adventure water contact, including landlike input, gravity, grounded jump, pose suppression, and water work parity.
- BFS2-REQ-019. Persist, synchronize, display, deplete, refill, and conservatively recover the player reserve across every named lifecycle path without turning equipment swaps into refill.

### Explicit Exclusions

- NG-001. Crafting and cooking recipes for dive equipment are excluded. Recipe absence is a required packaging assertion.
- NG-002. Forge 1.20.1 remains the only target. No Fabric output or platform upgrade is introduced.
- FUT-004. A testing artifact may support evidence but public release publication remains excluded.
- BFS2-REQ-021. Whole product packaging and final regression belong to Phase 007. This phase produces its bound evidence and integration packet only.

## Phase Contract

### BFS2-PHASE-006 — Faithful dive equipment, seabed movement and exact oxygen lifecycle

**Objective:** Deliver a full dive suit whose supplied art, equipment identity, underwater movement, work behavior, reserve timing, persistence, synchronization, and user feedback meet IFC-006 without altering vanilla behavior outside its declared eligibility.  
**Owner:** Repository maintainer  
**Dependencies:** BFS2-PHASE-005, EXT-001, EXT-002, DEC-002, DEC-004, DEC-007, DEC-008, DEC-009  
**Canonical requirements:** BFS2-REQ-017, BFS2-REQ-018, BFS2-REQ-019  
**Documentation and release impact:** Update `README.md`, `DOCUMENTATION.md`, `docs/README.md`, the existing diagnostic support guide, the asset ledger, and the existing equipment or gameplay topic guide. Prepare matching wiki edits after merge. Update the phase milestone, linked tracking state, and phase evidence. Do not publish a release.  
**Next transition:** BFS2-PHASE-007 at the first reserved work package in its linked execution blueprint

**Entry criteria**

- BFS2-PHASE-005 is merged into `1.20.1`, its resulting default commit is verified, and its signed annotated phase tag exists.
- Before implementation, P006-TASK-001 verifies EXT-002, creates or updates the matching phase milestone, and verifies the phase branch starts only from that approved default lineage. Record matched source, dependency, and configuration identities on every used host.
- Supplied PNG and JPEG source hashes are recorded, no current source fingerprint contradiction remains, and IFC-001 plus IFC-008 remain available.
- EXT-001 and EXT-002 are revalidated at the point of their respective runtime or integration use.

**Implementation scope**

- BFS2-REQ-017. Establish source identity, compatible geometry and UV evidence before rendering integration, then register the four items and client isolated resources without painting, alpha extraction, resampling, or a recipe.
- BFS2-REQ-018. Resolve the player travel hook through an early real path experiment, deliver exactly one shared eligibility and movement decision, retain ordinary collision and impulses, and prove water work parity without granting unrelated mining or placement privileges.
- BFS2-REQ-019. Keep reserve state on the player with schema 1, decrement once per authoritative submerged tick, preserve it through lifecycle paths, refill only in real breathable air, and synchronize an owning client with bounded revisions and readable feedback.

**Execution order**

1. `P006-TASK-001` executes BFS2-REQ-017 and BFS2-REQ-018 feasibility work before geometry or movement integration.
2. `P006-TASK-002` executes BFS2-REQ-017 resource and worn rendering work after the atlas mapping is accepted.
3. `P006-TASK-003` executes BFS2-REQ-018 shared eligibility and one travel writer after the hook experiment selects its narrow path.
4. `P006-TASK-004` executes BFS2-REQ-018 and BFS2-REQ-019 reservoir, synchronization, feedback, and work parity after common eligibility exists.
5. `P006-TASK-005` executes BFS2-REQ-017, BFS2-REQ-018, and BFS2-REQ-019 diagnostics and required server and client evidence after feature signals are present.
6. `P006-TASK-006` executes BFS2-PHASE-006 documentation, review, merge, default branch verification, wiki synchronization, and signed tag work after all feature gates pass.

**Required evidence**

- Asset manifest hashes, deterministic decoded RGB comparison, normalized UV table, resource load proof, all slot and pose rendering evidence, and recipe absence audit.
- Early hook experiment records from actual player travel, then server real path and GameTest evidence for one writer, eligibility exits, gravity, jump edge behavior, collision, work parity, and exact lifecycle timing.
- Bounded `dive` diagnostic captures and parser assertions with complete footer, revision and cleanup data; server and client observations remain separately correlated.
- Silent laptop proof for actual player input, pose, worn visual seams, HUD revision, reconnect, and absence of sustained correction, using a matching node-1 dedicated server.

**Exit criteria**

- Every IFC-006 signature, boundary, compatibility rule, and acceptance criterion BFS2-AC-017 through BFS2-AC-019 has its required evidence at this phase revision.
- The current phase branch passes its required checks, review findings are resolved, merge commit on `1.20.1` is verified, documentation and postmerge wiki synchronization are complete, and a signed annotated `bfs2-phase-006` tag identifies the merged commit.
- No known mandatory phase owned defect remains.

## Shared Contract Projection

```json
{
  "phase_id": "BFS2-PHASE-006",
  "canonical_requirement_ids": [
    "BFS2-REQ-017",
    "BFS2-REQ-018",
    "BFS2-REQ-019"
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
      "id": "IFC-006",
      "signature": {
        "persistent": {
          "schema": "int=1",
          "remainingTicks": "int[0,6000]",
          "initialized": "boolean"
        },
        "runtime": {
          "revision": "long>=0",
          "fullSuit": "boolean",
          "submergedEyes": "boolean",
          "waterContact": "boolean",
          "movementMode": "vanilla|seabed",
          "oxygenMode": "air|protected|empty|external_breathing|exempt",
          "jumpLatched": "boolean",
          "lastConsumptionTick": "long"
        },
        "sync": "server -> owning client: schema, revision, remainingTicks, eligibility, movementMode, oxygenMode",
        "apply": "apply(player, equipment, fluid, inputs, tick) -> one movement decision plus oxygen transition"
      },
      "acceptance_ids": [
        "BFS2-AC-017",
        "BFS2-AC-018",
        "BFS2-AC-019"
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

IFC-006 is produced here. The server creates and persists its schema 1 state, evaluates equipment, fluid, input, and tick once, then sends the permitted snapshot only to its owning client. The client uses the received revision for HUD and prediction reconciliation but cannot write reserve state. P006-TASK-003 consumes IFC-006 eligibility to ensure one movement writer. P006-TASK-004 consumes the same result for oxygen and work logic. P006-TASK-005 joins server and client records using capture and revision IDs rather than clocks.

IFC-001 is an upstream diagnostic facility. This phase adds bounded `dive` records before its hook experiment concludes so the experiment can distinguish hook absence, double application, noneligibility, and ordinary vanilla travel. IFC-007 is a retained Phase 005 compatibility regression only. IFC-008 binds every retained evidence record to the source, artifact, dependency, configuration, fixture, host role, tick window, result, and cleanup result; it never claims final delivery in this phase.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| IFC-001 capture and parser | BFS2-PHASE-000 | Default off bounded capture, level 2 console control, complete footer semantics, and schema compatible parser. | Run enable, status, off, denial, timeout, and parser help discovery before local assertions. | Stop local evidence if capture is incomplete or parser lacks required field support. Repair local extension before dependent proof. |
| IFC-007 algae state | BFS2-PHASE-005 | Merged and tagged state with valid generated resources. | Run preserved algae fixture and resource loading regression with dive changes. | Stop on resource or registry regression and restore compatibility before integration. |
| Four PNGs and JPEG | SRC-004 through SRC-009 | Original bytes and JPEG hash `dd398100067ce9f92b4c3f17c89d1f13c1d695729128abbdeb6c9b37f60d1cc3`. | Hash original inputs and compare decoded RGB pixels in the generated renderer format. | Reject altered source, fabricated alpha, or lossy transform and remap from the verified input. |
| Forge event and travel evidence | SRC-108 | Pinned Forge 47.2.0 APIs and water branch behavior. | Compile the earliest narrow hook experiment against the exact dependency and exercise actual player travel. | Record hook insufficiency, select only the narrow player injection permitted by the experiment, and rerun all movement fixtures. |
| IFC-008 identity record | BFS2-PHASE-000 | Exact candidate source, artifact, dependency, config, fixture, host, and cleanup values. | Produce a phase evidence record for every retained decisive fixture. | Evidence remains unverified when identity or cleanup is missing. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| IFC-006 schema 1 state and sync | BFS2-PHASE-007 | Remaining ticks stay within 0 through 6000, initialization is explicit, revisions do not regress, and only server writes state. | An unknown newer schema is retained opaque, marked unsupported, and denied reserve use without downgrade, reset, or refill. A supported established malformed schema repairs to zero with a reason. | Lifecycle fixtures, capture records, and persisted NBT migration fixtures. |
| Dive item identities and renderer mapping | BFS2-PHASE-007 and operators | Four exact IDs, supplied PNG bytes, JPEG derived RGB texture, UV table, and no dive recipes. | Preserve existing Prismarine armor path and supplied source identities. | Asset manifest, package listing, render proof, and Prismarine regression. |
| Seabed travel and work behavior | BFS2-PHASE-007 | Full eligible suit has one movement decision and water work parity only. All excluded modes use vanilla behavior immediately. | Dry, partial, creative, spectator, and incompatible state cannot retain owned modifiers or pose state. | Server fixtures and silent laptop input/reconnect evidence. |
| Dive support procedure and phase packet | Repository maintainer and support workflow | Reproducible bounded capture, redaction, analysis, cleanup, and known limitation record. | Existing `bfs-debug-v2` parser remains compatible through additive fields. | Readable documentation and IFC-008 packet after merge. |

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| P006-TASK-001 | BFS2-REQ-017, BFS2-REQ-018 | First verify EXT-002, create or update the phase milestone, set linked tracking to active implementation, and verify the branch from the approved predecessor default. Before production completion, hash inputs and construct the normalized atlas map. Create a bounded disposable prototype using the exact intended `dive_helmet`, `dive_chestplate`, `dive_leggings`, and `dive_boots` identities, the four piece eligibility predicate, draft compatible renderer, and the real player travel entry path. Add server `dive` observations and local player observations before running it. Compare full suit water contact with dry, partial, creative, and spectator fixtures to select one scoped writer or identify the exact missing branch and narrow injection need. | BFS2-PHASE-005, EXT-001, EXT-002, SRC-004 through SRC-009, SRC-108, DEC-002, DEC-007 | Phase milestone and branch readiness, source identity manifest, UV table, bounded exact identity prototype result, hook decision record, player observations, and recovery path for an insufficient hook. | ModItems and existing Prismarine armor patterns, client model and renderer boundary, Forge player hooks, IFC-001, IFC-006 | UV bounds stay in [0,1], decoded RGB matches, no guessed alpha, prototype renders the exact item identities, actual full suit travel has one writer, and excluded modes have none. No unrelated armor or direct helper writer is accepted. A failed hook experiment blocks production completion. |
| P006-TASK-002 | BFS2-REQ-017 | Replace the bounded prototype with production complete registration of `dive_helmet`, `dive_chestplate`, `dive_leggings`, and `dive_boots`; supply normal slots, names, creative availability, command availability, leather tier durability and defense, zero toughness and knockback bonus, leather sound, ordinary enchantability, and ordinary same item anvil combination. Finalize client isolated geometry, renderer, item models, language, and generated resources from the accepted map. | P006-TASK-001, DEC-002 | Four production usable item registrations and resources with faithful worn mapping. | `ModItems`, platform registration, PrismarineArmorItem and its model and renderer pattern, data providers, asset resources, IFC-006 | Package has four item resources and zero dive recipes. All slots render without missing texture, opaque sheet, mirrored lettering, UV bleed, or limb mismatch across front, back, side, walking, jump, crouch, resource reload, and common arm variants. |
| P006-TASK-003 | BFS2-REQ-018 | Implement the shared eligibility predicate and exactly one player only water travel path selected by the prototype and finalized against the production pieces. Full suit means four exact pieces, survival or adventure, and body water contact. Apply landlike horizontal input, gravity 0.02 blocks per tick squared, grounded rising edge jump impulse 0.24 blocks per tick, terminal downward speed no greater than 0.3 blocks per tick, ordinary collision, and external impulses. | P006-TASK-001, P006-TASK-002, DEC-007 | Server authoritative movement decision, shared client eligibility, immediate owned state cleanup, and movement diagnostic records. | Player travel hook, equipment change and mode lifecycle events, client prediction boundary, IFC-006 `apply` | One decision per tick. Grounded press gives one jump, holding input cannot rearm until release, no fake ground or teleport, apex and landing remain smooth, and dry, partial, creative, spectator, death, and dry exit return vanilla by the next server tick. |
| P006-TASK-004 | BFS2-REQ-018, BFS2-REQ-019 | Implement schema 1 player reserve, clone and persistent load handling, revisioned sync, localized oxygen feedback, exact ticking, real air refill, compatibility branches, and water work parity. Preserve seabed movement when empty. Remove only actual underwater and airborne mining penalties while eligible. | P006-TASK-002, P006-TASK-003, DEC-004 | Persistent remaining ticks and initialized flag, server to owner snapshots, HUD states, work decision and recovery reasons. | Player persistent storage, clone, login, dimension, breathing, break speed, client HUD boundary, IFC-006 | A truly missing legacy reservoir initializes once to 6000 and writes `initialized=true`. An established supported malformed reservoir repairs to zero. A newer schema is retained opaque, reported unsupported, and gets ordinary no reserve behavior without downgrade or refill. Ticks 1 through 6000 protect, tick 6001 starts vanilla drowning at air zero, refill occurs only in real air at 20 reserve ticks per tick, and all swap, death, restart, dimension, reconnect, bubble column, Water Breathing, Respiration, creative, spectator, corrupt, and stale sync fixtures meet the declared result. Matched land and submerged work rates are equal within one server tick for identical block, tool, effects, and jump state. |
| P006-TASK-005 | BFS2-REQ-017, BFS2-REQ-018, BFS2-REQ-019 | Complete local diagnostics before final assertions, run layered static, unit, server, GameTest, installed artifact, and silent laptop checks, analyze captures, and bind all decisive evidence. | P006-TASK-001 through P006-TASK-004, EXT-001, EXT-002 | Complete phase evidence with explicit passed, failed, or unverified result and cleanup status. | Debug manager and parser, BfsGameTests, player behavior, client diagnostics and renderer, IFC-001, IFC-006, IFC-007, IFC-008 | Exact fixture matrix below. Capture timeout, absent target, denied enable, reload, overflow, I/O failure, disabled overhead, stream recreation, and cleanup gates pass or keep the affected gate open. Server proof does not close client visual or input claims. |
| P006-TASK-006 | BFS2-REQ-017, BFS2-REQ-018, BFS2-REQ-019 | Confirm the milestone and branch established by P006-TASK-001. From verified phase branch behavior, update user and technical documentation, asset ledger, dive support runbook, and phase evidence before merge. After P006-TASK-005, review the complete diff, integrate through a merge commit, verify the default, publish prepared wiki changes only after that merge, and sign and push the phase tag. | P006-TASK-005, EXT-002 | Merged and tagged phase with documentation describing only proven behavior. | README, DOCUMENTATION, docs index, affected topic guide, diagnostic guide, asset ledger, GitHub milestone and wiki, IFC-008 | Required checks and private independent review pass. Any unverified required client proof blocks merge and phase closure. The merge commit is contained by `origin/1.20.1`, postmerge proof reruns, wiki reflects merged state, tag resolves to merged commit, and no release is published. |

P006-TASK-001 establishes milestone and branch readiness before its prototype work. P006-TASK-006 depends only on the completed preceding work and does not supply an entry prerequisite. P006-TASK-001 must complete before P006-TASK-002 through P006-TASK-004 because it supplies prototype feasibility, atlas boundaries, and signals needed to falsify later behavior. P006-TASK-002 completes production pieces before P006-TASK-003 finalizes travel against them. P006-TASK-004 consumes the final pieces and eligibility. P006-TASK-005 is the final feature proof and P006-TASK-006 never begins integration while a mandatory phase defect, required unverified client gate, or cleanup failure remains.

## Architecture and Implementation Boundaries

The four item IDs use normal armor slots and the established Forge registration and Prismarine armor pattern, while all renderer and model classes stay Forge client side. The JPEG is an immutable visual input: if the renderer requires PNG, deterministic decoding may preserve each decoded RGB pixel but cannot invent alpha, transparency, geometry, or missing pixels. The UV table assigns explicit normalized rectangles to each torso, leg, arm, boot, and helmet surface. It records orientation and seams, checks every coordinate in [0,1], and leaves nonassigned atlas regions nonrendering. Newly authored geometry carries those regions only; it is not a replacement visual design.

The eligibility function is shared semantically, but the logical server remains authoritative. It returns true only for the four exact registered pieces, survival or adventure, and body water contact. It also reports distinct `fullSuit`, `submergedEyes`, `waterContact`, `movementMode`, `oxygenMode`, and `jumpLatched` states. A full suit in dry travel, creative, spectator, or no water contact never invokes seabed travel. One server selected player travel writer owns submerged input and gravity. It does not set flight, fake `onGround`, teleport a player, remove collision, or replace external impulse. Client prediction accepts only the synchronized current revision and reconciles through the same predicate so stale state cannot sustain swim or HUD state.

The player persistent schema owns `{schema: 1, remainingTicks: 0..6000, initialized}` independently from item stacks. `initialized=true` is durable whenever schema 1 has been created or migrated successfully. A new player or legacy player with no reservoir compound and no durable initialized marker enters first migration, receives 6000 once, and immediately writes schema 1 plus `initialized=true`. A supported schema 1 record with that marker but missing or malformed `remainingTicks` is established corruption, clamps to zero, and emits `STATE_CORRUPT`; it never looks like first migration. An unknown newer schema is not parsed, downgraded, deleted, or overwritten. It is retained as opaque source data, emits `SCHEMA_UNSUPPORTED`, disables the reserve, and leaves ordinary breathing and movement behavior until compatible software can read it. The implementation cannot distinguish arbitrary external deletion from an authentic first migration without the durable marker, so only the explicitly described missing marker condition may initialize a reserve. Clone, login, dimension, and restart restore the same supported value. The server consumes exactly once only when eyes are submerged, the suit is full, and no independent breathing effect applies. Water Breathing pauses consumption without refilling. Respiration preserves its vanilla effect but does not change the protected 6000 tick count. Bubble columns preserve vanilla breathing behavior but cannot refill the reserve while eyes remain submerged. Real breathable air refills 20 reserve ticks each simulation tick regardless of worn pieces. Empty reserve keeps movement, and vanilla drowning begins at tick 6001 from vanilla air zero.

Server to owning client sync occurs at mode and equipment transitions, login, and dimension join, plus no more than once per 20 ticks during steady depletion. The payload contains only IFC-006 allowed fields. The client rejects stale revisions and shows localized remaining seconds, empty state, and refill state while relevant, preserving vanilla air and drowning feedback. Water and airborne mining penalties are removed only inside seabed mode. Tool correctness, effects, enchantments, hardness, reach, placement orientation, collision, permissions, and Mining Fatigue remain vanilla. If placement already has no water delay, parity is recorded rather than producing a new bonus.

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| JPEG mapping is ambiguous or has no alpha | Manifest hash, decoded pixel check, UV checker, render seam fixture | Preserve decoded RGB and never crop a guessed background or call the source a mesh. | Rework only newly authored geometry and UV table from the original source. | P006-TASK-001 bounds, P006-TASK-002 multi pose render check. |
| Event hook cannot replace water travel once | Hook experiment records vanilla travel plus candidate writer count for 200 water ticks. | Stop before broad implementation and identify the missing execution branch. | Use the narrowest player only injection allowed by the experiment, keep client and dedicated server loading safe, then rerun the experiment. | P006-TASK-001 writer count is exactly one in full suit and zero in excluded modes. |
| Partial, dry, creative, spectator, death, or equipment swap leaves a modifier | Eligibility transition record and next tick state observation. | Remove only owned state by the next authoritative tick and retain ordinary vanilla travel. | Idempotent removal followed by a clean mode recomputation. | P006-TASK-003 transition fixtures and client reconnect check. |
| Held jump swims upward or retriggers | `jumpLatched`, grounded state, vertical velocity, and input edge records. | Only a grounded rising edge produces 0.24 upward velocity. | Require release before rearm and return no vertical swim input. | 200 tick held key fixture has one impulse per press and no continuous ascent. |
| Reserve refills through armor swap, reconnect, death, or bubble column | Remaining ticks, refill reason, persistent schema, clone and login revision records. | Preserve charge across the lifecycle and refuse nonair refill. | Restore persisted supported value; established schema 1 corruption becomes zero with a repair reason; unknown newer schema remains opaque and unsupported with no refill. | One complete bounded capture containing the uninterrupted 6000 tick consumption interval, first empty tick, natural exit and 300 real air refill ticks, plus separate 5999, 6000, 6001 focused boundaries, swap loop, clone, restart, dimension, reconnect, and bubble fixtures. |
| External breathing is confused with real air | Oxygen mode and effect source records. | Water Breathing pauses consumption, Respiration does not extend protected duration, and neither causes refill underwater. | Clear only expired external state and resume deterministic branch. | Effects matrix with exact tick counter deltas. |
| Client HUD or prediction uses stale state | Server and client revision correlation and movement correction count. | Client ignores older revision and displays only current server state. | Resync on login, dimension join, equipment or mode transition. | Reconnect and out of order payload fixture with no sustained correction after 40 ticks. |
| Water work parity changes protected or unsupported work | Break speed factors, block result, permission outcome, and placement result. | Remove only water and airborne factors, preserve all other checks. | Drop the owned adjustment on eligibility exit and retain vanilla result. | Matched land/submerged cases, Mining Fatigue negative, protected block negative, and grounded versus jump cases. |
| Diagnostic capture stops or loses records | Status counters, footer, output path, parser result, and stop reason. | `CAPTURE_INCOMPLETE` never supports a pass. | Stop, retain minimal sanitized failure, resolve the cause, and collect a new bounded run. | Timeout, absent target, overflow, I/O, reload, and idempotent off cases. |

## Diagnostics and Debugging

**Requirement IDs:** BFS2-REQ-017, BFS2-REQ-018, BFS2-REQ-019, BFS2-REQ-022  
**Task IDs:** P006-TASK-001, P006-TASK-003, P006-TASK-004, P006-TASK-005, P006-TASK-006  
**Controls:** Server console controls are `bfs debug on dive <ticks> <target>`, `bfs debug status`, and `bfs debug off`, with permission level 2 and an explicit player target. P006-TASK-001 adds the `dive` category and exact player target validation before the hook experiment. The user command form is `/bfs debug on dive <ticks> <target>`. `on` accepts 20 through 36000 ticks and respects the inherited 30 minute wall limit, 32 target, 8192 queued record, 100000 record, 64 MiB capture, and 256 MiB directory limits. A missing target, no permission, or unknown category rejects without mutation. Local client controls are exactly `/bfs debug client on`, `/bfs debug client status`, and `/bfs debug client off`. P006-TASK-001 extends this local capture from BFS mob selection to the owning fixture player only for worn pose, HUD, travel, and sync observations. It has no server authority, captures no arbitrary inventory, and retains its tighter 1200 tick, 90 wall second, and 32 MiB limits.  
**Signals:** Each server `dive` event has capture and sequence IDs, logical side, pseudonymous player, tick, schema, revision, and bounded reason. `dive_eligibility` records equipment slots, mode, body water contact, eyes submerged, selected movement and oxygen mode. `dive_travel` records writer identity, input edge, grounded state, jump latch, requested and actual velocity in blocks per tick, gravity, collision, and correction count. `dive_oxygen` records before and after remaining ticks, last consumption tick, mode, refill source, external breathing classification, durable initialized marker, schema result, and persistence repair reason. `dive_work` records real break or placement entry path, water and airborne factors, preserved factors, result, and denial reason. `dive_sync` records server and client revision, direction, accepted or stale result, and payload size. Local owning player observation records `dive_local_player` with the same capture correlation, received revision, HUD state, pose, client input edge, predicted and rendered position, correction count, resource ID, UV region ID, slot, arm variant, missing texture flag, and reload generation.  
**Collection procedure:** Use the numbered runbook below. It captures only one test player and `dive` category, records the candidate identity, analyzes the complete file using parser arguments discovered by `python3 -B tools/bfs_debug_analyze.py --help`, retains only sanitized decisive summaries, and stops collection before cleanup.  
**Headless verification:** On node-1, use a dedicated server or verified server only GameTest configuration after inspecting the task graph. Actual equipment change, breathing, clone, login, dimension, break speed, and travel entry points are exercised in the `dive-seabed`, `dive-reserve`, and `dive-work-parity` fixtures. The server proves authoritative writer count, tick timing, state persistence, effect compatibility, and work factor preservation. It does not prove rendered worn geometry, actual keyboard input, HUD presentation, or client reconciliation.  
**Client verification:** Required residual claims are actual full suit movement input, no held key swimming ascent, visual swim pose suppression, smooth apex and landing, worn atlas seams and limb assignment, localized oxygen HUD and refill or empty status, resource reload, reconnect state receipt, and no sustained rubber band after 40 ticks. Each uses one silent laptop client connected to a matching node-1 server and bounded targeted screenshots only for visual assertions.  
**Client audio isolation:** Before launch, discover the laptop instance path and set only its pinned Minecraft version master output to zero. After launch, bind the exact owned Hyprland window address, class, title, and PID from `hyprctl clients -j` to the launched process. Correlate that process tree to only its PipeWire node or PulseAudio sink input, mute it with `wpctl set-mute <node-id> 1` or `pactl set-sink-input-mute <sink-input-id> 1`, and read back muted state before any assertion. Recheck after resource reload, reconnect, device change, or stream recreation and mute the replacement stream. Never change a default sink, microphone, unrelated application, personal instance, or global audio state. If correlation or mute readback fails, stop the owned client and leave only the affected client gate unverified. At teardown stop the owned client and watcher, confirm the matching playback stream is gone, and remove only the isolated audio state.  
**Budgets and privacy:** Diagnostics are default off, create no formatting, allocation, packets, scans, or file writes while disabled, and obey inherited bounds. Event payloads contain session pseudonyms and selected equipment state only, never player names, arbitrary inventory, chat, credentials, addresses, or raw NBT. I/O, overflow, timeout, target removal, reload, and writer failure stop safely with a terminal completeness record. Test enabled capture overhead against the same fixture with capture off, requiring p95 additional tick cost no greater than 5 percent or 0.25 ms, whichever allowance is larger, without changed decisions.  
**Regression and support:** Add parser and diagnostic self tests for `dive` fields, bounds, redaction, disabled branch behavior, status, off, timeout, absent target, denied use, reload, and complete footer. Rerun affected player, armor, resource, data, and Prismarine regressions. Update `docs/test/debug-diagnostics.md` and linked user documentation with enable, status, reproduce, off, locate, redact, and submit steps. The support packet contains source and artifact identities, configuration digest, fixture ID, capture range, sanitized summary, expected and actual behavior, and client material only for an actual client gate.

| Signal | Source and unit | Expected observation |
|---|---|---|
| `dive_eligibility` | Server per mode or equipment transition | Full suit plus survival or adventure water contact selects `seabed`; partial, dry, creative, and spectator select `vanilla`. |
| `dive_travel` | Server at most once per five ticks normally; per tick only in separate high detail windows of at most 200 ticks and eight targets, blocks per tick | One writer, 0.02 gravity, no more than 0.3 downward terminal speed, and exactly one 0.24 impulse per grounded key edge. |
| `dive_oxygen` | Server transition plus once per 20 ticks steady state, ticks | Sampled state and explicit interval boundaries agree with independent authoritative fixture counters: 6000 protected decrements, the first empty tick, and 300 eligible increments of 20. No per tick travel log is needed for the long run. |
| `dive_sync` | Server send and client receipt, monotonic revision | Transitions send immediately, steady state sends at most every 20 ticks, and stale client payload is rejected. |
| `dive_work` | Server real break or placement path, factor values | Eligible underwater factor equals matched land factor after only water and airborne components are removed. |
| `dive_local_player` | Local client capture, one owning player, no more than 1200 ticks | HUD, pose, input, render selection, and server revision correlation match the local fixture without arbitrary inventory capture. |
| capture footer | Server stop result | Zero missing and dropped records for a usable proof. Any incomplete result invalidates that capture. |

1. Create a unique phase owned scratch directory under the verified project anchor on each used host. Record source commit, candidate JAR SHA 256 and SHA 512, dependency and configuration digests, fixture ID, server runtime, laptop instance, exact expected capture location from status, and teardown list before launch. Do not create a runtime in an existing personal instance or active worktree.
2. Build the server `dive-seabed` fixture at x 0 through 23, y 64 through 71, z 0 through 15: a 24 by 16 source water basin with floor y 64, water surface y 70, a one block step at x 4, a two block ceiling span at x 8 through 10 and y 68, and a dry shore exit beginning x 16. Start the fixture player at x 1.5, y 65, z 7.5. Build the paired dry control at x 32 through 55 with the same floor, step, and ceiling geometry. Place `minecraft:stone` at x 12, y 65, z 7 and x 44, y 65, z 7; use an unenchanted `minecraft:iron_pickaxe`. Reserve a spawn protected stone placement at x 0, y 65, z 14 for the nonoperator protection denial. For server fixtures, configure the exact disposable runtime `eula.txt` to one effective `eula=true` assignment and read it back. Start the node-1 no GUI server, wait for its ready console message for no more than 120 seconds, and stop with `ENVIRONMENT_UNAVAILABLE` if readiness fails. Prepare each short hook, movement, jump, exit, or negative fixture before starting its own `bfs debug on dive 200 <fixture-player>` capture. Align the assertion to the first captured tick, with at most eight targets in high detail. The held jump case records all 200 held ticks; release and the next grounded press use a fresh bounded capture in the same player lifecycle. Setup, earlier walking and later actions do not consume that 200 tick assertion window. For the full reserve proof, first finish fixture preparation and use actual real air refill to verify a full reserve on shore. Then start `bfs debug on dive 7200 <fixture-player>` and record capture start tick C. Verify one target, category, output path and the actual remaining tick budget with `bfs debug status`. Arm the fixture to enter water within the first 200 capture ticks, without manually setting charge. The detailed interval accounting below keeps setup, depletion, natural exit and refill inside this single bounded capture.
3. Perform real entry paths. Equip the production full or partial suit through the fixture inventory path, enter the basin, walk 8 blocks over the step and beneath the ceiling, then leave through the shore exit. Over each 40 tick unobstructed segment, horizontal displacement must remain within 0.25 block of the paired dry control. Press and hold jump for 200 ticks, release, then press again only after landing; one rising edge produces a 0.24 blocks per tick impulse, apex remains 1.20 through 1.60 blocks above takeoff, landing occurs within 60 ticks, vertical descent never exceeds 0.3 blocks per tick, and no second impulse occurs while held. Break and place the paired stone through actual player handlers in dry and submerged grounded and suit jump cases. Break completion times must match within one server tick after only water and airborne factors are removed, and placement must produce the same handler result in each matched case. Repeat with Mining Fatigue, wrong tool, and the nonoperator spawn protected stone to prove preserved effect, suitability, and protection results. For the long run, execute the reserve interval accounting below. Keep the full suited player naturally eye submerged for 6000 consecutive protected ticks, observe the first empty submerged tick, then use actual movement to reach real air and observe 300 consecutive eligible refill ticks. The fixture must remain alive and retain seabed movement when empty. Record vanilla air zero at entry to the first empty countdown and its ordinary subsequent change, not an extra vanilla reserve. A near boundary preset can test focused negatives but never replaces this uninterrupted run. For no target, denied console, timeout, target removal, reload, overflow, and I/O fixtures assert safe stop and no accepted proof.
4. Disable with `bfs debug off`, then immediately run `bfs debug status` and assert new matching records cease by the next server tick. Discover parser syntax with `python3 -B tools/bfs_debug_analyze.py --help`, analyze the single reserve capture against the interval accounting below. Require complete ordered emitted sequences and a footer, continuous authoritative fixture tick counters, zero mismatch or duplicate writer counts, pinned revisions, 6000 protected decrements, first empty countdown, and 300 exact refill increments. Normal sampled records intentionally skip ticks; the parser must validate their declared cadence and counter coverage rather than require one emitted record per world tick. Reset, splice, incomplete interval, lost required boundary or footer, dropped record, or failed witness invalidates the run. Analyze each shorter capture separately and preserve only sanitized summaries and IFC-008 records after confirming readability.
5. For a named client gate, first discover laptop anchor, desktop, discrete renderer, and supported automatic connection method. Confirm node-1 server readiness and existing private reachability, apply the silent audio procedure, connect the owned client to the exact private endpoint, and confirm the intended player joined the intended server world on both sides. Use `/bfs debug client on`, verify `/bfs debug client status`, perform actual keyboard movement, jump, reload, worn pose, HUD, and reconnect observations, then use `/bfs debug client off`. The short local observation window records at most 1200 ticks and 90 wall seconds. Server and client positions must converge within 0.25 block and require no further correction within 40 ticks after reconnect. A server console may reset fixtures but must not grant progress through the gameplay path under test. Recheck mute after reload and reconnect.
6. Stop only owned server, client, watcher, and fixture processes. Confirm their PIDs exited and the owned stream disappeared. Retain the declared sanitized phase evidence outside scratch, remove exact test created runtimes, worlds, logs, captures, screenshots, audio state, copied artifacts, traces, and bytecode after their last consumer, and verify each disposable path is absent. Report cleanup separately; `CLEANUP_INCOMPLETE` blocks phase closure.

### Reserve interval accounting

P006-TASK-005 owns this continuous real player proof. The 7200 tick capture begins at C after setup on shore has established a full reserve through ordinary refill. Let T be the first eligible submerged tick, with `C <= T <= C+199`. The player wears the full suit, has no independent breathing immunity, and remains eye submerged throughout ticks T through T+6000. T through T+5999 are exactly 6000 protected ticks; the actual reserve changes from 6000 to zero by one per tick. E equals T+6000 and is the first empty submerged tick, where ordinary drowning begins from vanilla air zero while seabed movement remains active.

After observing E, move through the fixture's reachable shore exit with actual travel, without teleportation, charge edits, death, equipment changes or breathing effects. Let R be the first real air refill tick, where `E+1 <= R <= E+600`. Ticks R through R+299 must be 300 consecutive eligible real air ticks, each adding 20 reserve ticks, ending at 6000. The latest required observation is C+7098, within the 7200 tick capture and inherited wall and output limits. Stop and verify the complete capture after that observation. A setup, exit, survival, continuity or capture deadline failure requires a fresh full run; it cannot be repaired by splicing or a near boundary preset. Choose the standing position near the declared shore exit before immersion so normal drowning does not prevent the return path.

The real server test fixture independently observes each authoritative player tick and the persisted reserve before and after that tick. It maintains bounded counters and interval start/end ticks, observed protected and empty tick counts, observed consumption/refill totals, writer count violations, discontinuities, mismatches and the first failing tick. It compares actual state with the stated decrement/refill invariants without calling the production transition helper or trusting its success flag. Every eligible tick must be accounted for, with no duplicate update and zero mismatch. This constant size witness produces a start record, interval boundary records and one final assertion summary; it does not emit a per tick movement trace. Correlate those records with ordinary oxygen samples at most once every 20 ticks and event driven mode changes. Per tick travel remains confined to the separate 200 tick hook and movement fixtures. The parser checks interval lengths, counter totals, expected boundary values, ordering, zero witness errors and terminal completeness. Sampled diagnostics alone cannot prove all 6000 decrements.

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Execution host and prerequisites | Evidence artifact |
|---|---|---|---|---|---|---|
| BFS2-REQ-017, P006-TASK-001 | Hash all four PNGs and JPEG. Decode JPEG deterministically and compare every RGB pixel. Validate UV coordinates, no overlap except documented seam sharing, and unused regions. | Load item models, language, armor geometry and renderer resources. Run Prismarine model and renderer regression. | Laptop renders each slot front, back, and side while idle, walking, jumping, crouching, and after reload. | Bad hash, UV outside [0,1], missing resource, opaque sheet, mirrored detail, and no recipe each fail. Remap geometry only from verified art. | Static on node-1. Client requires EXT-001, matching node-1 server, verified renderer and silent client. | Asset manifest, UV table, resource report, package recipe listing, targeted render captures, IFC-008 entry. |
| BFS2-REQ-018, P006-TASK-001 | Assert eligibility truth table and selected hook writer count. | Actual player travel entry fixture runs 200 submerged ticks per mode. | Full suit player walks seabed, uses press hold release jump sequence, crosses a collision step, exits dry water, changes mode, and reconnects. | Hook absence, two writers, held ascent, midair retrigger, partial swap, creative or spectator transition, and collision mismatch fail. Recover by narrowing player hook and rerunning. | Node-1 headless first. Client claim requires private server readiness, joined world proof, laptop discrete renderer and verified mute. | Hook record, `dive_travel` capture, GameTest report, client log and targeted apex or pose evidence. |
| BFS2-REQ-018, P006-TASK-003 and P006-TASK-004 | Compare break speed factors for identical block, tool, enchantment, and effect state. | Real server break and placement handlers run land and submerged cases grounded and during suit jump. | Laptop mines and places one matched block underwater with the full suit. | Mining Fatigue, wrong tool, protected block, out of reach, and denied placement retain vanilla outcome. Existing no delay placement records parity rather than bonus. | Headless node-1 proves handlers. Client only proves input and presentation. | Factor table, handler capture, permissions result, client input record, IFC-008 entry. |
| BFS2-REQ-019, P006-TASK-004 | State codec tests cover schema 1, missing new state, corrupt established state, unknown newer state, clamp bounds, and stale revisions. | Dedicated player fixtures count 5999, 6000, and 6001 submerged ticks and real air 300 tick refill. Run swap, death clone, logout or login, restart, dimension, Water Breathing, Respiration, bubble column, creative, and spectator matrix. | Laptop observes HUD transitions, reconnect revision, empty movement continuation, and normal drowning start. | Any refill outside real air, duplicate decrement, vanilla extra 300 protected ticks, reset on swap, or accepted stale revision fails. Repair state and rerun from earliest affected lifecycle fixture. | Node-1 dedicated server with read back EULA. Client requires matching artifacts, private connection, joined world, silent laptop. | Bounded interval accounting and witness summary, persistent state fixtures, capture summaries, HUD and reconnect evidence, IFC-008 records. |
| BFS2-REQ-022, P006-TASK-005 | Parser, bounds, permission, redaction, disabled overhead, and schema compatibility checks. | On status off, timeout, absent target, reload, target removal, overflow, and writer failure use real manager paths. | Client capture records render and sync only for named residual claims. | Incomplete footer, output limit, I/O failure, unauthorized control, and stream recreation cannot yield pass evidence. | Node-1 server. Laptop only for client residual assertions and always silent. | Status transcript sanitized, parser report, overhead comparison, support packet, cleanup record. |
| P006-TASK-006 | Diff check, generated drift, artifact archive inspection, and documentation link validation. | Required checks and independent review on phase branch, merge commit and default branch verification. | No release workflow. Wiki publication occurs only after verified merge. | Failed check, unresolved review, merge restriction, changed source or artifact identity, or incomplete cleanup blocks integration. | GitHub and node-1 only. No client required unless postmerge client evidence is invalidated. | Review state, merged commit, signed phase tag, wiki update evidence, phase completion packet. |

## Documentation, Operations, and Release

Implementation updates `README.md`, `DOCUMENTATION.md`, `docs/README.md`, `docs/test/debug-diagnostics.md`, the existing dive or equipment topic guide, and the asset ledger. The documented user workflow states the four exact pieces, full suit eligibility, survival and adventure water behavior, gravity and jump controls, 300 second reserve, 20 tick per tick real air refill, empty behavior, external breathing compatibility, no recipe, no swap refill, and diagnostic collection and redaction instructions. It distinguishes supplied asset source from newly authored compatible geometry and leaves no claim of a supplied mesh.

At the beginning of P006-TASK-001, create or update the phase milestone and set linked tracking to active implementation. Before merge, update tracked documentation only from verified phase branch behavior, inspect the complete diff, source and generated output drift, and ensure no `AGENTS.md`, cache, runtime, secret, screenshot, personal path, or unrelated change is staged. A required client gate that remains unverified blocks merge and closure. After the approved merge and default verification, publish wiki edits prepared from the tracked documentation and reconcile only satisfied tracking. A testing artifact may be inspected and hashed for evidence. No public release page, upload, or release note publication is part of this phase.

## Risks and Evidence Invalidation

| Risk ID and owner task | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| BFS2-RISK-009, P006-TASK-001, P006-TASK-002, P006-TASK-005 | Immutable inputs, decoded RGB comparison, explicit UV table, client isolated renderer. | Source hash, UV, seam, slot, limb, pose, arm variant, and reload observations. | Correct geometry or UV only, preserving source artwork. | All render evidence after an asset, geometry, renderer, or reload path change. | Repeat static mapping, resource load, Prismarine regression, and silent laptop visual matrix. |
| BFS2-RISK-010, P006-TASK-001, P006-TASK-003, P006-TASK-004, P006-TASK-005 | Early hook experiment, one writer, player owned schema, authoritative ticking, exact eligibility and immediate removal. | Writer count, mode, jump latch, remaining tick, refill reason, revision, and correction records. | Return to vanilla on exit, repair corrupt state to zero, select narrow hook only when experiment proves need. | Every dependent travel, reserve, sync, work, lifecycle, and client result after hook, eligibility, schema, or timing change. | Rerun hook experiment, boundary matrix, persistence matrix, work parity, and named client evidence. |
| BFS2-RISK-012, P006-TASK-001, P006-TASK-005 | Default off bounded signals, explicit target, lazy payloads, complete footer. | Capture status, counter, footer, p95 overhead, parser result, redaction check. | Stop incomplete capture, retain sanitized reason, repair instrumentation and recollect. | Any capture with missing or dropped records, changed candidate identity, or cleanup failure. | Rerun affected fixture with diagnostics enabled and matched off control. |
| BFS2-RISK-013, P006-TASK-005, P006-TASK-006 | Split host policy, private endpoint, prelaunch zero plus verified app stream mute, exact owned resource list. | Renderer, window, PID, stream mute, join, process exit, and path absence records. | Stop owned client on ambiguity, retain client gate unverified, reconcile only owned leftovers. | Client evidence after any stream recreation, artifact mismatch, runtime identity loss, or incomplete cleanup. | Recreate isolated runtime and rerun only affected client procedure after host validation. |

## Phase Completion Packet

The packet is stored outside the protected plan set and includes the phase branch and resulting default commit IDs, candidate JAR SHA 256 and SHA 512, dependency and configuration digests, asset source and decoded RGB manifests, normalized UV table, resource and recipe absence report, hook experiment decision, generated output drift, unit and GameTest reports, exact lifecycle interval accounting and independent witness summaries, work parity matrix, diagnostic self test and parser reports, sanitized complete captures, client renderer input HUD and reconnect evidence, source and host binding, cleanup reports, documentation and asset ledger updates, milestone and review state, merge commit proof, postmerge wiki synchronization, and signed annotated `bfs2-phase-006` tag proof.

Each verification first records exact owned scratch paths, processes, worlds, captures, audio state, and copied artifacts. After the final consumer, it preserves only required sanitized evidence and requested deliverables, stops only owned processes and watchers, confirms their exit and stream disappearance, removes exact verified disposable resources without symlink traversal, and checks absence on every used host. Source files, tracked fixtures, active worktrees, personal instances, saves, shared caches, historical branches and tags, and the reusable diagnostic product remain protected. A packet whose cleanup field is incomplete cannot close this phase.

## Next Transition

Only after the phase branch is merged through the required GitHub merge commit, all required checks and private review pass, the resulting `origin/1.20.1` commit is verified, phase documentation and wiki synchronization are complete, the signed annotated `bfs2-phase-006` tag points at that merge, and all phase evidence has complete cleanup, advance to BFS2-PHASE-007 at the first reserved work package in its linked execution blueprint. Do not begin Phase 007 implementation before these gates pass.
