# Phase 003 Execution Plan

> **Plan ID:** PLAN-PHASE-003  
> **Phase ID:** BFS2-PHASE-003  
> **Owner:** Repository maintainer  
> **Classification:** MANDATORY  
> **Master plan:** [plan.md](../plan.md)  
> **Phase sequence:** 003 of 007

## Purpose and Ownership

This phase makes disturbance settings reach actual reactions, adds real water entry, water jump, and occupied moving boat producers, and gives Great White sharks a safe noncombat boat interest. It owns the detailed implementation, proof, recovery, and integration work for BFS2-REQ-007, BFS2-REQ-011, and BFS2-REQ-012. The master remains authoritative for product scope, the frozen interfaces, their acceptance criteria, execution hosts, and final endpoint.

The accountable owner is Repository maintainer. The component scope is disturbance and shark intent. The phase does not add boat damage, rider ejection, a combat target, a new movement owner, persistent tuning profiles, or an ecosystem redesign.

## Evidence-Based Entry State

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| OBSERVED | Disturbance path | `WaterDisturbanceListeners` feeds `WaterDisturbanceEvent`, `WaterDisturbanceHandler`, and shark state, but the handler narrows sources to `LivingEntity`; boat identity is therefore lost and no explicit moving boat producer exists. | SRC-102, F011, repository map disturbance boundary | Reinspect if the approved Phase 002 merge changes any named component. |
| OBSERVED | Diagnostics | Existing server capture is bounded `bfs-debug-v2`; `BfsDebugManager`, parser, tests, and the support guide are reusable. | SRC-110, F005 | Reinspect the current manager, parser, and guide before changing emitted fields. |
| PROPOSED | Motion ownership | IFC-003 creation provenance defines the required body safety writer and priority. At Phase 003 entry, recheck the upstream merged implementation and its acceptance packet before relying on it. | BFS2-PHASE-001, BFS2-PHASE-002, IFC-003 | Stop if the actual upstream entry check lacks the required merge, signed tag, or one final writer. |
| PROPOSED | Disturbance controls | Every source, radius, sensitivity, rate, eligibility, and reaction must be observable and tunable through IFC-002. | FIND-107, BFS2-REQ-007 | Invalid if the frozen field catalog or IFC-002 signature changes through an authorized plan amendment. |
| PROPOSED | Real producers | Water entry and shallow water jump must be real server transitions; a three block fall callback and forced disturbance command are insufficient. | FIND-111, BFS2-REQ-011 | Invalid if source event APIs or real path assumptions change at implementation inspection. |
| PROPOSED | Boat safety | Great Whites require an actual behind boat waypoint, fully submerged body, safe fin exposure only, bounded release, and no boat combat behavior. | FIND-112, BFS2-REQ-012, IFC-003, IFC-005 | Invalid if the actual Great White controller has a conflicting upstream writer that cannot participate in IFC-003 arbitration. |
| UNKNOWN | Client gate capability | The laptop connection, Hyprland, NVIDIA capability, and audio controls were found, but the actual candidate renderer, isolated runtime, window, stream, and private endpoint are runtime checks. | EXT-001, SRC-008 | Revalidate before every client launch. A failure keeps only the named client gate unverified. |

## Scope Boundaries

### Included Scope

- BFS2-REQ-007. Implement and expose the complete per species disturbance catalog, effective settings readback, capability reasons, and real reaction policy.
- BFS2-REQ-011. Produce and deduplicate real water entry, water jump, and occupied moving boat events, preserving typed source and boat identity with bounded state.
- BFS2-REQ-012. Add safe Great White noncombat boat tracking through IFC-003 and IFC-005, including safe behind trajectory selection, finite reevaluation, and recovery.
- BFS2-REQ-022 contribution. Extend existing diagnostics, parser assertions, support procedure, permissions, bounded captures, redaction, overhead checks, and cleanup for the changed behavior.

### Explicit Exclusions

- NG-003 excludes boat destruction and unrelated ecosystem behavior. Boat interest never applies damage, ejects riders, mounts entities, or creates a combat target.
- FUT-002 excludes subjective final species speed balance. The listed defaults are diagnostic starting values and not a balance outcome.
- FUT-003 excludes named profiles and import or export. Disturbance edits are live session overrides through IFC-002.
- BFS2-REQ-010 body envelope implementation is upstream. This phase consumes it and adds boat specific acceptance and recovery coverage without replacing its movement or renderer architecture.
- BFS2-REQ-008 and BFS2-REQ-009 follow ownership are upstream. This phase only honors their priority and release semantics.

## Phase Contract

### BFS2-PHASE-003 — Real disturbance controls and safe Great White boat interest

**Objective:** Produce bounded real disturbances whose effective per species settings decide a visible reaction, then allow only eligible Great Whites to trail a moving occupied boat from a safe submerged route with a safely exposed fin.  
**Owner:** Repository maintainer  
**Dependencies:** BFS2-PHASE-002, EXT-001, EXT-002  
**Canonical requirements:** BFS2-REQ-007, BFS2-REQ-011, BFS2-REQ-012  
**Documentation and release impact:** Update root `README.md`, `DOCUMENTATION.md`, `docs/README.md`, the existing tuning and boat behavior documentation, `docs/test/debug-diagnostics.md`, and `docs/verification/bfs2-phase-003.md` with verified branch behavior during this phase. Create or update the matching milestone before implementation and keep tracking current during work. Prepare the corresponding wiki update from tracked documentation and publish it only after approved merge. The phase produces a testing artifact and evidence, not a public release.  
**Next transition:** BFS2-PHASE-004 at Work Packages item 001 in its linked execution blueprint.

**Entry criteria**

- BFS2-PHASE-002 is merged into `1.20.1`, its resulting default commit is checked, and its signed annotated phase tag exists.
- IFC-001, IFC-002, IFC-003, IFC-004, and IFC-008 are present at their accepted upstream signatures; the source, dependency, config, and diagnostic parser identities are recorded before changes.
- No unresolved upstream movement owner writes a Great White pose outside IFC-003 arbitration. Any such conflict stops boat intent work and is reported as an upstream contract failure.
- EXT-001 and EXT-002 are revalidated at their use gates. A missing laptop capability leaves visual and input evidence unverified without blocking headless implementation and server proof.
- The matching phase milestone exists before implementation starts and is kept current through review and integration.

**Implementation scope**

- BFS2-REQ-007 extends the settings catalog and IFC-002 consumers for all IFC-005 source controls and explicit eligibility or rejection reasons.
- BFS2-REQ-011 adds actual transition and boat producers, source identity preservation, world tick throttles, pruning, bounded candidate selection, and diagnostic records before source assertions.
- BFS2-REQ-012 adds only a `boat` owned `boat_track` intent to existing IFC-003 arbitration, applies `boatTarget`, and uses the precomputed envelope to accept or reject a route.

**Execution order**

1. `P003-TASK-001` executes BFS2-REQ-011 and BFS2-REQ-022 before dependent source assertions by extending diagnostics and real producers.
2. `P003-TASK-002` executes BFS2-REQ-007 after P003-TASK-001 by connecting the complete disturbance catalog to IFC-002 and actual reaction decisions.
3. `P003-TASK-003` executes BFS2-REQ-012 after P003-TASK-001 and P003-TASK-002 by creating safe Great White boat intent through IFC-003.
4. `P003-TASK-004` executes BFS2-REQ-007, BFS2-REQ-011, and BFS2-REQ-012 after P003-TASK-003 with real server, client input, fin and recovery evidence.
5. `P003-TASK-005` executes BFS2-REQ-021 and IFC-008 after all local evidence passes by documenting, reviewing, integrating, verifying the resulting default, and signing the phase tag.

**Required evidence**

- Source to event to decision records with source kind, source and boat correlation, effective settings revision, throttle or eligibility reason, shark intent, clearance, and terminal recovery reason.
- Unit and parser checks for field validation, threshold decisions, source keying, tie breaking, bounded maps and candidate count, timeout, and redaction.
- Dedicated server real path tests for a shallow jump, water entry, dry seated moving rider, throttle, empty boat, stationary boat, source removal, dimension isolation, saturation, and full recovery.
- Paired silent laptop player input and targeted visuals for a real jump and boat ride, joined world identity, submerged Great White body, visible dorsal fin, behind trajectory, turn, dismount, and safe route recovery.
- Phase evidence with IFC-008 identity fields, integration proof, signed tag, documentation links, and complete cleanup result.

**Exit criteria**

- All P003-TASK-001 through P003-TASK-005 oracles pass against the same candidate identity, with complete diagnostic footers and no capture loss.
- The exact real source, settings, reaction, boat route, priority, recovery, permissions, bounded work, and client presentation requirements pass at their required fidelity. A missing or unverified required client input or fin and body claim blocks merge and phase closure even though independent headless work may continue.
- The phase branch is reviewed and merged by GitHub merge commit into `1.20.1`, the resulting default is verified, and a signed annotated `bfs2-phase-003` tag is pushed.
- No known mandatory phase-owned defect remains.

## Shared Contract Projection

```json
{
  "phase_id": "BFS2-PHASE-003",
  "canonical_requirement_ids": [
    "BFS2-REQ-007",
    "BFS2-REQ-011",
    "BFS2-REQ-012"
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
      "id": "IFC-002",
      "signature": {
        "resolve": "resolve(species: ResourceLocation) -> EffectiveSpeciesSettings",
        "snapshot": {
          "schema": "int=1",
          "revision": "long>=0",
          "species": "BFS registry ID",
          "fields": "sorted map<FieldId, TypedValue>",
          "provenance": "map<FieldId, default|server_config|session>",
          "capabilities": "map<FieldId, CapabilityResult>"
        },
        "mutate": "apply(expectedRevision: long, targets: sorted SpeciesId[], patch: TypedPatch) -> Applied(newRevision)|Rejected(reason, fields, species)",
        "reset": "reset(targets: SpeciesId[], fields: FieldId[]|all) -> Applied|Rejected"
      },
      "acceptance_ids": [
        "BFS2-AC-002",
        "BFS2-AC-003",
        "BFS2-AC-004",
        "BFS2-AC-005",
        "BFS2-AC-006",
        "BFS2-AC-007"
      ]
    },
    {
      "id": "IFC-003",
      "signature": {
        "intent": {
          "id": "opaque string",
          "owner": "safety|follow|combat|boat|ordinary",
          "state": "idle|cruise|pursuit|flee|follow|boat_track|recovery",
          "target": "dimension plus Vec3 in blocks|null",
          "settingsRevision": "long",
          "horizontalBps": "finite double[0,20]",
          "verticalBps": "finite double[0,20]",
          "expiresAtTick": "long"
        },
        "evaluate": "evaluate(entity, proposedPose, sweptSegment, mediumPolicy) -> safePose, safeVelocity, clearance, reason",
        "clearance": {
          "scale": "finite double[0.25,2]",
          "bodyWet": "boolean",
          "solidClear": "boolean",
          "finExposure": "blocks>=0",
          "surfaceY": "finite double|null",
          "limitedAxes": "set<horizontal|vertical>"
        }
      },
      "acceptance_ids": [
        "BFS2-AC-003",
        "BFS2-AC-004",
        "BFS2-AC-010",
        "BFS2-AC-012"
      ]
    },
    {
      "id": "IFC-004",
      "signature": {
        "lease": {
          "version": "int=1",
          "token": "server issued opaque nonce",
          "owner": "UUID",
          "mob": "UUID",
          "dimension": "registry ID",
          "adapter": "capability ID",
          "startedTick": "long",
          "expiresTick": "long",
          "lastProgressTick": "long",
          "ownedIntentId": "opaque string"
        },
        "claim": "claim(serverPlayer, clickedEntity, markedStack, hand, interactionId) -> Lease|Rejected(reason)",
        "tick": "tick(lease, level) -> active|blocked|released(reason)",
        "release": "release(lease, reason) -> restoration result"
      },
      "acceptance_ids": [
        "BFS2-AC-008",
        "BFS2-AC-009"
      ]
    },
    {
      "id": "IFC-005",
      "signature": {
        "event": {
          "version": "int=1",
          "dimension": "registry ID",
          "worldTick": "long",
          "sourceId": "UUID",
          "sourceKind": "swim_sprint|attack|damage|block_break|fall|projectile|water_entry|water_jump|occupied_boat",
          "position": "Vec3 blocks",
          "strength": "finite double[0,1]",
          "boatId": "UUID|null",
          "riderId": "UUID|null"
        },
        "react": "react(event, speciesSettings) -> ignored(reason)|alert(untilTick)|investigate(intent)",
        "boatTarget": "boatTarget(boat, sharkEnvelope) -> safeBehindWaypoint|rejected(reason)"
      },
      "acceptance_ids": [
        "BFS2-AC-007",
        "BFS2-AC-011",
        "BFS2-AC-012"
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

IFC-005 is produced in this phase. Real Forge transition and boat listeners construct the event without forcing it through a command, preserve world tick, dimension, source UUID, optional boat UUID, and optional rider UUID, then submit it to the bounded handler. The handler consumes the immutable IFC-002 snapshot only at the event decision and returns a named ignored, alert, or investigate result. Great White interest consumes an accepted occupied boat event and uses `boatTarget` plus the established IFC-003 envelope before publishing a single `boat_track` intent. The existing movement integrator remains the only pose writer.

IFC-001 records are emitted from the server event producer, throttle, reaction decision, candidate selection, intent arbitration, envelope evaluation, and recovery. They are observations only. IFC-004 is read only for its active follow ownership and release state; it never changes a lease. IFC-008 binds each retained proof to the exact candidate and cleanup result.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| IFC-001 | BFS2-PHASE-000 | Default off bounded capture with status, stop footer, redaction, parser, and permission level 2. | Run existing control self tests before adding `disturbance` and `boat` payloads. | Stop dependent assertion if capture cannot report complete status or parser compatibility. |
| IFC-002 | BFS2-PHASE-001 | Immutable revisioned snapshot with atomic mutation, reset, reload, fields, provenance, and capability reasons. | Readback every new disturbance field and assert one revision per accepted edit. | Reject incomplete field catalog or stale revision with the existing transaction path. |
| IFC-003 | BFS2-PHASE-001 | One final travel writer, known priority, conservative body and fin envelope, bounded recovery. | Confirm boat intent is below safety, follow, and combat before route tests. | Do not introduce a second controller. Route to safe recovery or stop on ownership conflict. |
| IFC-004 | BFS2-PHASE-002 | Follow lease exposes owner, target, intent, and release state without stale references. | Fixture competing follow against boat acquisition and release. | Follow remains authoritative. Boat intent stays suppressed or is released. |
| EXT-001 | Existing laptop and private controls | Laptop only client capability, renderer, quiet owned stream, and private server route can be revalidated. | Perform the full silent client and joined world gate before client assertions. | Keep client rows unverified and record the exact missing capability. |
| EXT-002 | Repository signing and merge capability | EnVy author identity, signing key, GitHub checks, protected default, and merge commit workflow. | Revalidate immediately before commit, tag, and merge actions. | Stop integration before a failed signature or unmet check. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| IFC-005 version 1 | BFS2-PHASE-007 final regression | Exact event, reaction, and `boatTarget` signatures; named source kinds, dimensions, IDs, bounds, and decisions. | No source kind rename or field deletion. Additions require an explicit interface version decision. | Unit, parser, GameTest, real server, client, and evidence packet assertions. |
| Disturbance settings catalog | Operators and final regression | All source, radius, sensitivity, interval, alert, eligibility, and reaction controls provide effective value, provenance, capability, and reason. | Session only via IFC-002; reset and reload preserve declared semantics. | Transaction, readback, real source, and recovery fixtures. |
| Great White boat intent | Existing IFC-003 integrator and Phase 007 | One noncombat lower priority intent with bounded target evaluation and release. | Existing motion semantics and saved entity identities stay unchanged. | Arbitration, clearance, recovery, and laptop trajectory proof. |
| Diagnostics and support update | Operators and final regression | Default off `disturbance` and `boat` capture records are parsable, bounded, redacted, and supportable. | Preserve `bfs-debug-v2` and existing parser compatibility. | Off/on parity, bounds, permission, truncated capture, guide replay. |
| Phase evidence and integration packet | BFS2-PHASE-004 | Source and artifact identity, result, cleanup, merged default, and signed phase tag are available before Work Packages item 001 in its linked execution blueprint. | IFC-008 schema 1. | Recorded phase evidence and checked tag. |

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| P003-TASK-001 | BFS2-REQ-011, BFS2-REQ-022 | Extend local diagnostic payloads before assertions, then add real water entry, water jump, and occupied boat producers with typed identity, lifecycle pruning, and bounded work. | IFC-001, IFC-002, source map, BFS2-PHASE-002. | Parsable records and real IFC-005 events. | `WaterDisturbanceListeners`, `WaterDisturbanceEvent`, `WaterDisturbanceHandler`, `SharkAlertHandler`, `BfsDebugManager`, parser, GameTests. | Actual transition and moving boat paths produce exactly one permitted event with complete footer. |
| P003-TASK-002 | BFS2-REQ-007 | Implement complete catalog fields, capability reporting, atomic mutation, reset and reload behavior, and effective reaction policy. | P003-TASK-001, IFC-002, accepted source defaults. | Real event to `ignored`, `alert`, or `investigate` decision with reason. | Effective settings service, command help and readback, disturbance handler, diagnostics. | Boundary, disabled, ineligible, cooldown, radius, wildcard, reset, and reload fixtures. |
| P003-TASK-003 | BFS2-REQ-012 | Implement Great White behind boat selection, intent arbitration, envelope checked waypoint, reevaluation, and safe release. | P003-TASK-001, P003-TASK-002, IFC-003, IFC-004. | Bounded noncombat `boat_track` intent and safe recovery. | Great White shark behavior, shark alert path, intent arbitration, aquatic route and envelope consumer. | Deep open water, turn, shore, obstruction, dismount, stop, removal, multiple boat, and competing intent assertions. |
| P003-TASK-004 | BFS2-REQ-007, BFS2-REQ-011, BFS2-REQ-012, BFS2-REQ-022 | Prove source to reaction, real player boat and jump input, fin and body presentation, permissions, capture bounds, and recovery. | P003-TASK-001 through P003-TASK-003, EXT-001. | Server and client evidence packets with honest residuals. | `BfsGameTests`, parser tests, silent laptop client, disposable dedicated server, diagnostics guide. | Real server paths plus paired laptop input and targeted fin or body visuals. |
| P003-TASK-005 | BFS2-REQ-021, BFS2-REQ-022 | Document, inspect, review, integrate, verify resulting default, publish the signed phase tag, and prepare postmerge documentation and tracking updates. | P003-TASK-004, EXT-002, complete evidence packet. | Merged phase, tag, documentation, and downstream handoff. | User and technical docs, verification evidence, milestone, pull request, wiki preparation. | Required checks, private independent review, GitHub merge commit, default branch proof, signed tag, and cleanup confirmation. |

### P003-TASK-001 detail

Use the existing listener chain rather than a direct handler call. Emit `water_entry` when the tracked actual actor transition crosses from air to water, and `water_jump` on a genuine water exit and reentry path or equivalent vanilla transition that preserves the real water impulse. The shallow fixture must be less than the three block fall threshold, proving the producer did not rely on fall damage. For boats, observe an occupied `Boat` through its actual tick movement, retain the boat UUID and dry seated rider UUID, and use horizontal displacement over world ticks. A boat event requires occupancy, movement at least 0.02 blocks per tick, enabled source, and its 10 tick interval.

Key throttle state by dimension, source UUID, and source kind. Use server level tick time, never entity age or wall clock. Retain no more than 4096 recent keys per level, inspect no more than 64 eligible nearby sharks per event, and prune on entity removal, dimension unload, and expiry. Saturation rejects with `SOURCE_BUDGET`; it cannot expand to a global scan. A duplicate callback returns a named throttle reason, produces no second accepted event, and leaves one source record. A removed source, reload or shutdown drops source state safely. Before a producer assertion, emit event, throttle, candidate count, and lifecycle records using pseudonymized entity fields with no arbitrary NBT, chat, private address, or projectile payload.

### P003-TASK-002 detail

Wire `disturbance.enabled`, `disturbance.source.<kind>.enabled`, `disturbance.reaction`, `disturbance.radius`, `disturbance.sensitivity`, `disturbance.source.<kind>.strength`, `disturbance.interval_ticks`, `disturbance.alert_ticks`, and `disturbance.source.<kind>.interval_ticks` into the same typed field catalog and snapshot service as upstream settings. Retain existing source defaults. New eligible shark source defaults are enabled water entry and water jump at strength 0.5 with 20 tick cooldown, occupied boat at strength 0.5 with 10 tick interval and 0.02 blocks per tick movement threshold, radius 24 blocks, sensitivity 1, and alert lifetime 100 ticks. Acceptance requires strength multiplied by sensitivity at least 0.25 and source distance within radius.

The decision consumes one resolved snapshot. It reports `ignored` with distinct reasons for source disabled, global disabled, not applicable species, reaction ignored, low threshold, outside radius, cooldown, wrong dimension, empty or stationary boat, duplicate, source budget, and invalid boat route. It reports `alert` with `untilTick`, or `investigate` with the newly owned intent. An unsupported field is explicit in capability readback; valid wildcard mutation validates every target and fails atomically with sorted species and fields. Reset removes only session overrides. Reload accepts an entire valid baseline or keeps the prior snapshot, preserving session overrides. Neither path writes TOML or mutates saved species configuration.

### P003-TASK-003 detail

Only a Great White that is eligible under resolved disturbance policy may acquire an occupied moving boat. From actual horizontal travel direction, compute the waypoint behind the boat by at least boat half length plus scaled shark half length plus two blocks. Predict no more than 20 ticks and retain existing turn bounds. Call IFC-005 `boatTarget`, then IFC-003 `evaluate`; publish a `boat` owned `boat_track` intent only when the conservative swept body is wet and solid clear. A positive fin exposure is allowed only where the current route and depth permit it. The body never follows the fin above the surface.

Arbitration is exact: safety and survival recovery outrank follow, follow outranks combat, combat outranks boat, and boat outranks ordinary navigation. A preexisting valid boat target persists to avoid oscillation. Multiple candidates use nearest safe candidate then stable UUID tie break. Reevaluate every 10 ticks. Dismount, removal, wrong dimension, and unsafe route release within 20 ticks; a stationary boat expires at 100 ticks. On rejection or loss, clear only the boat owned intent, brake or replan to a safe wet waypoint, apply configured stall and retry bounds, then return to ordinary safe recovery. Do not alter combat memories, follow lease data, boat state, rider state, or damage behavior.

### P003-TASK-004 detail

Run headless assertions before the residual client gate. Use a tagged fixture with a 48 by 48 block open water basin, water from Y 48 through Y 59 over an impermeable floor at Y 47, and a 12 block wide shore shelf rising to Y 58 on one side. Add a two block wide solid obstruction in the open water route and a ceiling segment at Y 60. Place Great Whites at minimum, default, and maximum supported scale in separate repeatable lanes. The positive boat lane remains at least 12 blocks deep and 24 blocks from the shore, while the shallow lane is two blocks deep. The fixture assigns distinct tagged source, rider, boat, shark, and observer entities and uses a declared fixed seed. Unit fixtures cover finite values and sources, threshold equality, source key identity, tie break determinism, maximum map capacity, 64 candidate cap, and each terminal reason. Dedicated GameTests use a real shallow step or jump whose air segment is less than three blocks, then actual water entry, plus normal occupied boat ticking for at least 40 ticks. Each source to reaction window is at most 200 ticks, a dismount or unsafe route must release within 20 ticks, and stationary expiration must occur by 100 ticks. These tests prove source, decision, bounds, priority, and recovery but do not prove player input or rendered fin clearance.

For the residual client claim, use a disposable no GUI dedicated server on node-1 and matching laptop client only after verifying exact candidate and configuration identities, server readiness, and private reachability. Use a supported version specific automatic direct connection to the verified private endpoint, or existing authorized desktop controls when the launcher supports no direct connection argument. Verify the intended player joined the intended server world in both server and client evidence before physical input. The player then uses physical client input to ride, steer, stop, and dismount from the boat and to perform a shallow jump into water. The server console may prepare the fixture but may not force the producer, move the boat, or grant progress through the boat interest path. Client capture and targeted visual proof show actual input, boat relation, body submersion, dorsal fin exposure, turn behavior, and recovery. If any client identity, renderer, automatic connection, joined world, private route, or mute condition fails, stop the owned client, retain the server proof, and mark exactly that client gate unverified. That unverified mandatory client gate blocks merge and phase closure.

### P003-TASK-005 detail

Create or update the matching milestone before implementation and keep its linked tracking state current during work. Document verified branch behavior as each work package completes. Explain operator fields, units, capability reasons, live session scope, reset and reload, source defaults, noncombat Great White behavior, safe recovery, and the smallest diagnostic packet. Update the existing support guide before recording its replay. Inspect complete diff, generated drift, archive contents, and tracked text for sensitive material. Create the phase branch only from the fully integrated and tagged prior default. Run deterministic checks and the required private independent review. Merge with GitHub merge commit only after all checks and conversations are satisfied, fetch the resulting `1.20.1`, rerun required resulting default proof, create the signed annotated phase tag, and publish only the tag. Prepare corresponding wiki changes from tracked documentation and publish them only after the merged documentation is verified.

## Architecture and Implementation Boundaries

The common disturbance model carries only the IFC-005 typed event. Forge listeners own actual platform event extraction and boat ticking. `WaterDisturbanceHandler` owns bounded source lifecycle, nearby candidate selection, and resolved policy application. `SharkAlertHandler` consumes an accepted response but does not reconstruct missing boat identity. The effective settings service owns field validation, snapshot revision, source provenance, atomic wildcard behavior, reset, and reload. `BfsDebugManager` receives immutable primitive snapshots from server thread decisions and queues bounded records asynchronously; neither writer nor parser may read mutable world objects off thread.

Great White behavior owns selection of a boat candidate and construction of a `boat_track` proposal. IFC-003 owns its arbitration, actual movement integration, swept envelope evaluation, collision, fluid safety, pitch, bounded work, stall handling, and recovery. The handler must not directly write position, velocity, navigation target, animation state, or a player or boat field. It may publish exactly one intent with an expiry. The existing final travel writer consumes the winning intent once per simulation tick. Boat interest uses base movement coefficients, not sprint multipliers.

State is server lifetime only. Source throttle records contain no live entity references and are pruned. Session disturbance overrides never persist. Great White boat intent contains no durable boat UUID that revives after reload. An entity unload, level unload, server stop, or source removal releases only phase owned transient state. Existing registry IDs, save identifiers, fishing, advances, scale, animation clips, and resources remain unchanged.

All server control remains permission level 2 through the established debug root. Console use requires explicit diagnostic targets for entity categories. Client local diagnostics do not grant server mutation. The only client code used here is an existing client diagnostic extension for the named visual and input receipt, isolated from common and dedicated server class loading.

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| Shallow entry or jump has no event | Real transition record missing while fixture crossed the water boundary. | Mark producer assertion failed. Do not substitute a fall callback or direct handler test. | Repair the actual transition listener and rerun source to reaction chain. | Less than three block fixture produces one `water_entry` or `water_jump` event. |
| Duplicate producer callback | Same dimension, source UUID, kind, and permitted window reappear. | One accepted event at most per source interval, named throttle record for duplicate. | Preserve first record and expire duplicate state naturally. | Repeated callback fixture has one accepted decision. |
| Empty, stationary, slow, or wrong dimension boat | Occupancy, movement threshold, or dimension fields fail. | Produce no occupied boat alert and explain the precise reason. | No intent is acquired or existing intent is released on wrong dimension. | Empty, stopped, subthreshold, and cross dimension negatives. |
| Map or candidate work pressure | Map reaches 4096 or local candidates exceed 64. | Reject excess with `SOURCE_BUDGET`; no global scan, unbounded allocation, or retained stale record. | Prune expired or removed source keys and retry only on a new real event. | Saturation fixture reports caps, no missing footer, and stable processing bound. |
| Disabled, low threshold, out of radius, cooldown, or ineligible policy | Effective snapshot and decision records disagree with expected fixture. | Return a distinct ignored reason and preserve other species settings. | Reset or reload reverts declared behavior atomically. | Boundary equality and one sided threshold tests, wildcard failure, reset and reload. |
| Stale revision or malformed batch edit | Transaction reject includes expected revision or sorted invalid fields and species. | Make no partial mutation and leave active snapshot intact. | Operator rereads revision and submits a valid whole transaction. | Concurrent revision and one invalid wildcard member fixtures. |
| Unsafe shallow, shore, wall, or obstructed boat route | IFC-003 reports body wet false, solid clear false, fin exposure invalid, or work limit. | Reject boat target or release it within 20 ticks, then brake or recover to safe water without teleport or flight. | Clear only boat intent and permit ordinary safe recovery. | Surface, slope, wall, ceiling, shallow, obstruction, and highest scale fixtures. |
| Dismount, stop, remove, unload, or dimension change | Boat or rider lifecycle event and expired intent record. | Release boat interest within 20 ticks, or 100 ticks after stationarity. | Restore ordinary valid behavior with no stale source or target reference. | Each termination fixture and source recreation test. |
| Follow or combat competes with boat | Arbitration record has active higher priority intent. | Boat stays suppressed or releases. Follow and combat state remain intact. | Reevaluate later only if eligible and safe. | Follow, combat, and safety priority fixtures. |
| Capture unauthorized, absent target, timeout, I O failure, or loss | Control response, status counters, terminal footer, and parser completeness. | Reject unauthorized controls. Mark any incomplete capture `CAPTURE_INCOMPLETE`, never pass it. | Stop idempotently, retain sanitized reason, repair capacity or path, and rerun a fresh bounded window. | Permission, absent target, timeout, writer failure, and queue saturation tests. |
| Client audio or identity ambiguity | Renderer, window PID, stream correlation, or mute readback fails. | Stop only the owned client before assertions and leave the client gate unverified. | Preserve headless result and exact missing prerequisite for retry. | Evidence shows no client assertion after failed isolation. |

## Diagnostics and Debugging

**Requirement IDs:** BFS2-REQ-007, BFS2-REQ-011, BFS2-REQ-012, BFS2-REQ-022  
**Task IDs:** P003-TASK-001, P003-TASK-002, P003-TASK-003, P003-TASK-004, P003-TASK-005  
**Controls:** Preserve level 2 `/bfs debug on [category] [ticks] [targets]`, `status`, and `off`. Add only the IFC-001 `disturbance` and `boat` categories under the existing command root and help. Console entity capture requires selected explicit targets, with a bounded aggregate capture only where an aggregate source applies. The support flow starts with `status`, enables one scoped capture, reads returned exact output path, disables with `off`, then requires stopped status and a complete footer.  
**Signals:** Every record retains header binding, pseudonymized entity identity, side, dimension, tick, settings revision, intent ID, bounded event and reason. Disturbance data includes source kind, normalized strength, radius, sensitivity, source interval, elapsed world ticks, accepted threshold result, candidate count, source key count, boat correlation availability, and reaction or ignored reason. Boat data includes boat movement, occupancy result, selected candidate count, stable selection result, behind offset, prediction ticks, desired and safe waypoint, body wet, solid clear, fin exposure in blocks, limited axes, competing owner, expiry, progress, and release or recovery reason. Units are blocks, blocks per tick, blocks per simulation second, ticks, counts, and normalized scalar values.  
**Collection procedure:** Follow the local numbered procedure below. It names fixture, host, control, actual stimulus, parser inspection, stop, and cleanup.  
**Headless verification:** Node-1 runs unit and parser checks, then the actual `BfsGameTests` dispatcher, listener, boat tick, handler, intent, and envelope path after verifying the task graph has no client or renderer. A disposable dedicated no GUI server proves real producer to decision and recovery with server owned fixture movement. It does not prove physical player input or rendered fin visibility.  
**Client verification:** The residual claims are actual jump and boat input, joined world behavior, and rendered behind boat body and dorsal fin presentation. Use client logs first, then targeted visuals only for the mesh and input relation that logs cannot prove. No owner action is requested unless existing authorized laptop control cannot perform a named required physical input or visual judgment.  
**Client audio isolation:** Every owned client uses a discovered isolated disposable laptop instance and zero master output in its pinned version configuration before launch. After launch, query `hyprctl clients -j`, bind the exact window address, class, title, and PID to the owned process, correlate only that process tree to its PipeWire or PulseAudio stream, mute with `wpctl` or `pactl`, and verify the specific application stream is muted before connection or assertions. Recheck and mute any replacement stream after reconnect, resource reload, or device change. Never mute default output, a microphone, another app, or a personal client. Stop the owned client if correlation or mute readback is uncertain. Teardown stops the client and watcher, verifies processes and stream absence, and removes only the temporary instance and audio state.  
**Budgets and privacy:** Capture remains default off with the inherited 32 target, 36000 tick, 30 minute wall deadline, 8192 queued record, 100000 record, 64 MiB capture, and 256 MiB directory maximums, using a stricter existing limit if present. Normal samples are no more frequent than every five ticks per target. High detail is at most 200 ticks and eight targets. Source cache is at most 4096 keys per level and candidate inspection is at most 64 per event. No record stores arbitrary NBT, chat, player names, credential, private endpoint, or raw local path. A dropped record, writer failure, absent footer, or stopped completeness counter produces `CAPTURE_INCOMPLETE`.  
**Regression and support:** Extend `docs/test/debug-diagnostics.md`, `tools/bfs_debug_analyze.py`, parser tests, and applicable `BfsGameTests` before their dependent assertion. Verify permission denial, disabled capture parity, source and boat records, lifecycle stop, target removal, cap behavior, redaction, and parser compatibility. The support packet contains only candidate identity, minimal settings, tagged fixture, reproduction actions, expected and actual result, capture ID, sanitized summaries, and only necessary client evidence.

| Signal | Source and unit | Expected observation |
|---|---|---|
| `sourceKind`, source pseudonym, dimension, world tick, event position, strength | Server listener, event, blocks and normalized scalar | Actual shallow water transition and qualifying moving occupied boat each reach exactly one producer record per permitted interval. |
| `sourceKeyCount`, candidate count, throttle elapsed, source budget reason | Server handler, counts and ticks | At most 4096 source keys and 64 candidates per event; excess is rejected explicitly with no global scan. |
| Settings field, provenance, capability, revision, transaction reason | Effective settings service, typed field units and revision | Every catalog field changes the corresponding real decision; invalid wildcard input leaves all targets unchanged. |
| Reaction, alert expiry, investigation intent, ignored reason | Handler, tick and bounded enum | Threshold, range, cooldown, disabled, ineligible, and source negatives are distinguishable. |
| Boat ID correlation, occupancy, movement, selected UUID tie break, behind offset | Boat producer and Great White selector, UUID availability, blocks, ticks | Dry seated rider remains correlated; empty or stationary boat fails; nearest safe boat and stable tie break are deterministic. |
| Intent owner, state, expiry, desired and safe waypoint, higher owner | IFC-003, bounded enum, blocks, ticks | `boat_track` exists only when safe and loses to safety, follow, and combat. |
| Body wet, solid clear, fin exposure, surface Y, limited axes, recovery reason | IFC-003, boolean, blocks, axis set, bounded enum | Body stays submerged, fin is exposed only on safe route, and unsafe route releases within its deadline. |
| Capture counters, footer state, parser result, output path | BfsDebugManager and parser, count and terminal state | Capture is complete and parsable. Any loss or failure invalidates the assertion. |

1. Before each bounded suite, record candidate commit, JAR SHA 256 and SHA 512, dependency and config digests, fixture ID, seed where relevant, host role, sanitized runtime identity, exact test created paths, owned processes, and cleanup targets. Confirm the task graph is genuinely headless before node-1 runs. For dedicated server runs, register teardown, set the exact disposable runtime EULA to `true`, read it back, start no GUI server, and verify console control and readiness.
2. Construct the tagged 48 by 48 by 12 basin fixture described in P003-TASK-004 on the disposable server, record its seed and coordinates in the sanitized packet, and verify no unrelated entities occupy its lanes. On the owned server console, inspect current help and `docs/test/debug-diagnostics.md`, run `bfs debug status`, then run `bfs debug on disturbance 200 @e[tag=bfs2_probe,limit=8]` for source and policy proof or `bfs debug on boat 200 @e[tag=bfs2_probe,limit=8]` for selection and clearance proof. Run `bfs debug status` again and record its exact output path and counters. A denied permission, unknown category, or absent selected entity is a failed setup result. Do not broaden targets or use a forced disturbance command as a producer substitute.
3. Apply the real stimulus within its declared deadline. For headless proof, cause the tagged actor to cross the water boundary and complete a water jump with less than three blocks of fall, then move the occupied boat through its normal tick path for 40 ticks at or above 0.02 blocks per tick. Repeat once inside the ten tick boat interval as the duplicate negative. For client residual assertions, only the joined laptop player supplies jump, boat movement, turn, stop, and dismount input. The positive observation window is at most 200 ticks. Use console only to establish deterministic world fixtures and inspect server state.
4. Evaluate real event to reaction chain, settings revision, throttle state, candidate bounds, intent priority, waypoint, clearance, body and fin state, and bounded release. Assert exactly one accepted source per interval, no more than 64 inspected candidates, and no more than 4096 retained source keys. Assert the deep lane reaches `boat_track` with body wet and positive fin exposure, while shore, shallow, ceiling, and obstruction lanes either follow safely or release within 20 ticks. Assert stationary interest expires by 100 ticks. For a visual gate, inspect the actual client log and targeted image at deep open water, a turn, and the configured recovery condition. Any unsafe clearance, duplicated accepted event, missing identity, source overrun, missed deadline, or client mismatch fails the named assertion.
5. Stop capture with `bfs debug off`, verify stopped status, require a complete footer with no dropped or missing records, discover current parser arguments through `python3 -B tools/bfs_debug_analyze.py --help`, and run selected source, decision, and intent assertions against the returned path. Keep server and client clocks separate. Treat timeout, removal, reload, shutdown, queue pressure, or I O failure as the registered stop path and rerun a new bounded capture.
6. Retain only the readable sanitized IFC-008 packet and required targeted visual evidence. Stop exact owned server, client, watcher, and temporary route; verify their processes and streams exited; remove exact test created worlds, logs, configs, downloads, screenshots, traces, reports, and runtime directories after their final consumer; and verify absence on every used host. Report `CLEANUP_INCOMPLETE` separately from a failed or passed behavior assertion.

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Execution host and prerequisites | Evidence artifact |
|---|---|---|---|---|---|---|
| P003-TASK-001, BFS2-REQ-011 | Source kind validation, source key, threshold, interval, prune, 4096 key and 64 candidate bounds. | Parser and diagnostic schema test; listener to event to handler chain. | Dedicated server actual air to water, shallow jump, and occupied boat tick fixtures. | Duplicate, empty, stationary, subthreshold movement, removal, unload, wrong dimension, saturation. | Node-1 only after task graph proves server only. Unique disposable no GUI runtime, readback `eula=true`, console, readiness, teardown. | Sanitized records, GameTest result, parser result, IFC-008 packet. |
| P003-TASK-002, BFS2-REQ-007 | Typed field bounds, atomic wildcard failure, stale revision, reset, reload, capability reason. | Command to IFC-002 snapshot to handler decision. | Real producer fixture crosses threshold, radius, cooldown, and reaction states. | Global or source disabled, ineligible species, low threshold, out of range, cooldown, invalid reload. | Node-1 server or genuine headless GameTest only. No laptop required. | Settings readback, event decision capture, test result, clean packet. |
| P003-TASK-003, BFS2-REQ-012 | Behind offset, twenty tick prediction cap, stable UUID tie break, intent priority and deadline properties. | Actual handler to Great White selector to IFC-003 evaluation and travel writer. | Dedicated server deep open water, turn, shore, obstruction, shallow, stop, dismount, removal, and multiple boat fixtures. | No safe waypoint, safety owner, follow owner, combat owner, stationarity, route stall, source removal. | Node-1 server only after upstream IFC-003 and IFC-004 provenance checks. | Intent, clearance, recovery records and test results. |
| P003-TASK-004, BFS2-REQ-011 | Not a substitute for real input. Validate capture permissions, completeness and redaction. | Correlate server event and decision capture with client log. | Player on silent laptop automatically connects by a supported version specific direct connection or authorized desktop control to the verified private endpoint, confirms intended player and world on both sides, jumps into water, rides and steers a boat, then sees Great White body and fin relation. | Visual route recovery, stream recreation, failed renderer or mute, automatic connection or join failure. | Node-1 dedicated server plus verified Linux laptop client. Matching candidate hashes, private endpoint, joined world on both sides, discrete renderer, isolated instance, exact muted application stream. | Server and client correlation, targeted visuals, stream mute evidence, IFC-008 packet. An unverified mandatory client gate blocks merge and phase closure. |
| P003-TASK-005, BFS2-REQ-021, BFS2-REQ-022 | Documentation link and evidence schema audit, diff and secret scan. | Required checks and private independent review. | Resulting default branch build and required phase regression after GitHub merge. | Failed check, review finding, signature failure, merge restriction, cleanup incomplete. | Verified branch and signing capability under EXT-002. No graphical workload. | Merged PR state, default commit, signed tag, documentation links, cleanup record. |

Lower fidelity unit or forced handler tests never replace the real producer paths. Server evidence never replaces the laptop input or body and fin presentation claims.

## Documentation, Operations, and Release

Update the existing operator documentation with every tunable field, type, unit, accepted range, capability state, readback meaning, session only override behavior, reset, reload, real source behavior, threshold rule, and distinct rejection reasons. The boat guide must state that interest is Great White only, noncombat, behind moving occupied boats, constrained by safety, and releases on unsafe or stale conditions. It must not imply boat damage or a final speed balance.

Update `docs/test/debug-diagnostics.md` with the local `disturbance` and `boat` commands, selected target requirements, capture limit and privacy boundaries, real reproduction steps, status path discovery, parser inspection, disable verification, redaction, and minimum support packet. Add `docs/verification/bfs2-phase-003.md` plus minimal referenced artifacts under the established phase evidence convention after proof exists. Update root `README.md`, `DOCUMENTATION.md`, and `docs/README.md` links only to behavior that is actually merged.

At integration, update the matching milestone and linked issue or pull request state. Prepare wiki content from merged tracked documentation and publish it only after merge. A testing artifact is not a public release, CurseForge release, Modrinth release, or replacement for the delivered artifact.

## Risks and Evidence Invalidation

| Risk ID and owner task | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| BFS2-RISK-005, P003-TASK-003 and P003-TASK-004 | Consume IFC-003 envelope and final writer. Permit only fin exposure on a safe boat route. | Desired and safe pose, wet body, solid clear, fin exposure, axes, progress, and recovery record. | Brake or replan to safe wet water, release boat intent, do not teleport. | Any movement, envelope, scale, config, or renderer change. | Repeat server clearance matrix and laptop body or fin visuals. |
| BFS2-RISK-006, P003-TASK-001 and P003-TASK-004 | Tick keyed source identity, 4096 key cap, 64 candidate cap, lifecycle pruning, bounded capture. | Source key count, candidate count, throttle, saturation, removal and unload records. | Prune and release stale state. Reject excess with `SOURCE_BUDGET`. | Listener, handler, threshold, event schema, config, or fixture source changes. | Rerun real producer, saturation, removal, and dimension isolation suites. |
| BFS2-RISK-012, P003-TASK-001 and P003-TASK-004 | Default off observations, bounded snapshot data, parser contract, complete footer requirement. | Permission result, dropped counter, writer status, footer, parser outcome, off/on parity. | Stop idempotently and rerun fresh bounded capture after repair. | Diagnostic manager, parser, capture limit, output path, or source identity changes. | Rerun control, parser, redaction, permission, bounds, and changed behavior tests. |
| BFS2-RISK-013, P003-TASK-004 and P003-TASK-005 | Laptop only graphical proof, prelaunch zero audio, exact stream mute, registered cleanup. | Host, renderer, window PID, stream mute, joined world, process and path teardown records. | Stop owned client on ambiguity and keep client gate unverified. | Candidate artifact, client config, dependency hashes, endpoint, renderer, stream recreation, or visual route changes. | Repeat full split host gate with fresh silent client evidence. |

## Phase Completion Packet

Before closure, retain the following outside the protected plan set.

- The phase branch source commit, candidate artifact SHA 256 and SHA 512, dependency digest, configuration digest, fixture identities, sanitized world identity, host roles, tick windows, result, and cleanup status under IFC-008.
- Results for compilation, relevant unit checks, parser tests, data generation if affected, dedicated GameTests, real dedicated server source to reaction and recovery suites, and client input or fin body gate. Each result names the exact source revision and cleanup result.
- Complete bounded capture headers and footers, parser summaries, minimal diagnostic excerpts, and targeted laptop visuals. Remove raw private logs, player names, private address details, full runtime directories, duplicate screenshots, and unneeded captures after final consumption.
- Updated documentation, support guide replay evidence, phase verification record, complete diff inspection, generated resource drift check, artifact inspection where built, secret scan, required check results, private review result, merge commit, resulting `1.20.1` verification, and signed annotated `bfs2-phase-003` tag.
- For each check or audit, the registered exact owned resources, EULA readback for server runs, graceful shutdown result, process and stream absence, exact deleted test created paths, retained deliverables, and any explicit leftover with recovery action. A result with incomplete cleanup cannot close the phase.

## Next Transition

After every Phase 003 implementation, evidence, review, merge, resulting default verification, and signed phase tag gate passes, advance the active phase cursor exactly once to BFS2-PHASE-004. The next executable action is Work Packages item 001 in the linked Phase 004 execution blueprint. Do not start Phase 004 code, client work, or integration branch before this phase is fully merged and tagged.
