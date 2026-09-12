# Phase 002 Execution Plan

> **Plan ID:** PLAN-PHASE-002  
> **Phase ID:** BFS2-PHASE-002  
> **Owner:** Repository maintainer  
> **Classification:** MANDATORY  
> **Master plan:** [plan.md](../plan.md)  
> **Phase sequence:** 002 of 007

## Purpose and Ownership

This phase delivers the server owned temporary follow lease required for a marked stick to direct one eligible living mob safely, then restore its ordinary controller without collateral behavior changes. It owns detailed execution of BFS2-REQ-008 and BFS2-REQ-009 only. The master remains authoritative for product scope, frozen IFC signatures, hosts, integration, and final endpoint.

## Evidence-Based Entry State

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| OBSERVED | Follow diagnostic base | Existing `BfsDebugCommands`, `BfsDebugManager`, local client capture, parser, permission level two controls, and bounded `bfs-debug-v2` captures are reusable. | F005, SRC-110 | Invalidate when Phase 000 changes the diagnostic schema or parser contract. |
| OBSERVED | Movement ownership | Existing aquatic paths have several motion writers. IFC-003 is required before any follow adapter may write movement. | F006, F007, IFC-003 | Invalidate when the approved Phase 001 safety or intent contract changes. |
| PROPOSED | Follow behavior | A server issued marked stick must select every living mob family, including bosses and other mods, without a whitelist. | FIND-108, DEC-005 | Invalidate only through an owner authorized contract amendment. |
| PROPOSED | Restoration | Explicit and bounded lifecycle termination releases only lease owned state and restores ordinary behavior. | FIND-109, IFC-004 | Invalidate only through an owner authorized contract amendment. |
| OBSERVED | Host capability | Node-1 supports headless server evidence. Laptop client, renderer, private route, window identity, and per stream mute require fresh checks. | SRC-008, EXT-001 | Revalidate before each client gate. |
| UNKNOWN | External controller hook | The registry inventory cannot establish how every special controller accepts temporary movement ownership. | F006, IFC-004 | Resolve in P002-TASK-001 before adapter implementation or compatibility acceptance. |

## Scope Boundaries

### Included Scope

- BFS2-REQ-008. Server issued marked stick acquisition for every eligible living controller family, through an IFC-004 lease and IFC-003 safe motion intent.
- BFS2-REQ-009. Bounded release, controller restoration, interaction security, diagnostics, real paths, documentation, integration, and tag evidence for that lease.

### Explicit Exclusions

- BFS2-REQ-010 body envelope implementation remains an upstream Phase 001 dependency. This phase consumes IFC-003 and must not weaken it.
- BFS2-REQ-011 and BFS2-REQ-012 disturbance and boat behavior start in BFS2-PHASE-003 after this phase merges.
- NG-006 excludes players and nonmob entities from following. The stick never mounts, teleports, or directly controls a player.
- FUT-002 and FUT-003 remain manual speed tuning and no profile library.
- Unknown future third party artifacts are covered by the generic extension contract and a real pinned compatibility case, not a claim that every future mod has been run.

## Phase Contract

### BFS2-PHASE-002 — Broad mob following with reliable restoration

**Objective:** Acquire one validated temporary follow lease for every required controller family, apply only safe lease owned movement, and restore ordinary behavior within the stated bound on every termination path.  
**Owner:** Repository maintainer  
**Dependencies:** BFS2-PHASE-001, EXT-001, EXT-002  
**Canonical requirements:** BFS2-REQ-008, BFS2-REQ-009  
**Documentation and release impact:** Update root README.md, DOCUMENTATION.md, docs/README.md, docs/test/debug-diagnostics.md, the capability and support guide, verification evidence, milestone, pull request, and phase tag after verified merge. Do not advertise unmerged behavior or publish a public release.  
**Next transition:** BFS2-PHASE-003 begins with its first registered work package.

**Entry criteria**

- BFS2-PHASE-001 is merged into `1.20.1`, its resulting default commit and signed annotated phase tag are verified, and no unresolved prerequisite defect affects IFC-002 or IFC-003.
- EXT-001 and EXT-002 are revalidated at the applicable runtime and integration gates. The source, Forge 47.2.0, Java 17, GeckoLib 4.4.7, SmartBrainLib 1.14.2, configuration, and dependency identities are recorded.
- IFC-001 capture limits and parser acceptance, IFC-002 revision semantics, IFC-003 priority and safety outcomes, and IFC-008 evidence schema are available unchanged.

**Implementation scope**

- BFS2-REQ-008 creates the registry driven compatibility inventory, a server issued stick, acquisition validation, one owner and one mob lease limits, controller adapters, and real click paths.
- BFS2-REQ-009 creates deterministic release for every declared exit, removes only lease owned state, proves ordinary valid behavior resumes within forty ticks, and retains no restart persistent live lease.

**Execution order**

1. `P002-TASK-001` resolves BFS2-REQ-008 controller compatibility and adds prototype follow diagnostics before acquisition experiments for active registry families, special bosses, an independent fixture, and one pinned real third party artifact.
2. `P002-TASK-002` implements BFS2-REQ-008 acquisition and safe movement adapters only after P002-TASK-001 records each real ownership hook.
3. `P002-TASK-003` implements BFS2-REQ-009 idempotent release, lifecycle bounds, and interaction security against the adapters from P002-TASK-002.
4. `P002-TASK-004` verifies BFS2-REQ-008 and BFS2-REQ-009 through diagnostics first, real headless paths, and the residual laptop click and moving owner claim.
5. `P002-TASK-005` documents and integrates BFS2-PHASE-002 only after P002-TASK-004 evidence and cleanup are complete.

**Required evidence**

- Compatibility manifest with active registry inventory, class family, controller ownership hook, adapter capability ID, exercised result, and an `ADAPTER_CONFLICT` record for any unresolved controller.
- Pinned official compatible third party artifact provenance including version, Forge target, SHA 256, SHA 512, license, security inspection, controller path, and non BFS namespace. A fixture alone is insufficient.
- Server interaction, GameTest, and lifecycle packets bind source commit, candidate hashes, dependencies and config hashes, sanitized fixture and world IDs, tick windows, result, and cleanup through IFC-008.
- Laptop evidence proves actual right click input, joined correct dedicated server world, one lease, safe moving following, and release. Server evidence alone cannot close this residual client claim.

**Exit criteria**

- Every required controller family, Ender Dragon part resolution, Wither case, SmartBrainLib case, independently controlled non BFS fixture, and actual pinned third party path acquires, follows safely, and releases through a recorded adapter.
- Explicit stop, repeat click, reselection, missing held stick, permission loss, target or owner death or removal, logout, dimension change, unload, shutdown, timeout, range excess, blocked route, competing owner, forged marker, and duplicate callback meet IFC-004 bounds and prove restoration.
- The required private independent review is complete, required checks pass, the merge commit lands through GitHub on `1.20.1`, resulting default is verified, and a signed annotated phase tag is pushed.
- No known mandatory phase-owned defect remains.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| IFC-001 | BFS2-PHASE-000 | Default off bounded v2 capture and parser work on server and client sides. | Start, status, stop, footer completeness, parser query. | Mark evidence unusable on incomplete capture and rerun from diagnostic setup. |
| IFC-002 | BFS2-PHASE-001 | Immutable settings revision is readable and remains server authoritative. | Resolve snapshot before lease and include revision in follow records. | Stop phase work on missing or incompatible contract. |
| IFC-003 | BFS2-PHASE-001 | Follow priority is below safety and above ordinary behavior, with safe pose, velocity, clearance, and reason. | Exercise every adapter against safe and blocked routes. | Release lease with `ADAPTER_CONFLICT` or safety reason. Never bypass safety. |
| IFC-008 | BFS2-PHASE-000 | Evidence schema records source and artifact identity plus cleanup. | Validate each retained packet before phase close. | Recreate invalid packet from earliest affected test. |
| EXT-001 | Existing laptop and private controls | Discrete renderer, desktop, private route, candidate identity, stream correlation, and mute can be revalidated. | Perform the host checks before client launch. | Keep only client gate unverified. Do not use node-1 graphical fallback. |
| EXT-002 | Git and signing capability | EnVy identity and registered SSH signing work. | Revalidate before commit and tag. | Stop integration, preserve verified local evidence. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| IFC-004 | BFS2-PHASE-003 and BFS2-PHASE-007 | Version one lease state has server nonce, owner, mob, dimension, adapter, bounded ticks, and owned intent. | Preserve exact signature and reject unknown or expired lease data safely. | Acquisition, release, restart, and parser packet. |
| Follow diagnostic vocabulary | Operator, support guide, final verification | `follow` category records acquisition, adapter, progress, block, release, and rejection without private names or token values. | Additive `bfs-debug-v2` typed payloads only. | Parser fixtures and sanitized capture. |
| Compatibility manifest | Later phase and maintainer | Explicitly distinguishes exercised compatible controllers from unresolved defects and future untested types. | Pinned artifact records are evidence, never runtime dependencies. | Manifest provenance and security review. |
| Restored controller behavior | BFS2-PHASE-003 | Lease cleanup leaves no movement owner that competes with boat or survival intent. | Clear only owned goal, modifier, memory, and intent. | Forty tick valid goal selection proof. |

## Shared Contract Projection

```json
{
  "phase_id": "BFS2-PHASE-002",
  "canonical_requirement_ids": [
    "BFS2-REQ-008",
    "BFS2-REQ-009"
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

IFC-004 is produced here. The interaction callback provides a deduplicated `interactionId` to server `claim`; issuance registry and token validation authenticate the marker before a capability adapter is selected. A lease owns one `follow` intent that feeds IFC-003 rather than setting velocity, clearing goal lists, teleporting, changing physiology, or moving across dimensions. `tick` validates owner, target, hand, permission, dimension, distance, progress, and expiry before it updates the route. `release` removes only records identified by the lease token and restores the controller's own scheduling path.

IFC-001 is consumed before real path assertions. Its server producer records sanitized pseudonyms, settings revision, intent correlation, desired target, safe actual output, selected adapter, owner side, reason, lease age, range, route progress, and restoration result. IFC-002 supplies the revision captured into the lease and records. IFC-003 accepts or limits every follow proposal, always ahead of follow priority. IFC-008 binds the compatibility and runtime packets to the exact artifact and cleanup result. Phase 003 consumes an absence proof that released follow intent cannot conflict with later boat intent.

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| P002-TASK-001 | BFS2-REQ-008 | Before final adapters, add prototype `follow.claim`, `follow.adapter`, `follow.intent`, `follow.progress`, `follow.reject`, `follow.release`, and `follow.restore` records to the existing capture path, then create the registry compatibility inventory and acquire short lived prototype leases. Use named fixtures: `minecraft:zombie` on a 24 by 4 block solid floor with an L shaped two block wide obstacle route, `minecraft:salmon` in a 24 by 4 by 4 source water channel, `minecraft:phantom` in a 32 by 16 by 16 unobstructed air volume, `minecraft:frog` across a 12 block shallow source water and 12 block solid land route, `minecraft:villager` on a 24 by 4 solid route, `bensfintasticsharks:harbor_seal` in a 24 by 4 by 4 source water channel, `minecraft:ender_dragon` through a clicked dragon part in a 64 by 32 by 64 clear End arena, and `minecraft:wither` in a 48 by 24 by 48 clear arena. Each positive fixture must emit claim and adapter records in the acquiring server tick, record at least one safe ten tick route update and reduced owner distance by tick 400, then release and prove an ordinary valid controller decision by tick 40. A blocked fixture uses the same route closed by an unpassable barrier and must release at 200 blocked ticks. Before a real external artifact is loaded, the mandatory preflight selects an official redistributable Forge 47.2.0 compatible source and records exact artifact URL, version, SHA 256, SHA 512, license, security inspection, entity ID, and controller path. Then exercise that non BFS entity on a 24 by 4 obstacle route. | BFS2-PHASE-001, EXT-001, approved source and artifact provenance, IFC-001, IFC-003. | Versioned manifest, pinned artifact preflight, prototype acquisition captures, and adapter decision record. | Existing capture components, controller adapter boundary, IFC-004, IFC-003, registry capability detection. | Manifest contains every active family and named fixture. Each mandatory case has its observable prototype acquisition, positive route, negative safety route, exact deadline, restoration oracle, and recoverable conflict result. |
| P002-TASK-002 | BFS2-REQ-008 | Implement `/bfs debug followme` issuance, persistent server session issuance authorization, namespaced marker version and issue identifier bound to owner, both hand callback deduplication, claim arbitration, and every adapter recorded in task 001. A successful `claim` generates a unique server only per lease nonce and stores it only in the IFC-004 lease record. Release invalidates that lease nonce and record but leaves a valid held issued stick reusable during the same server session. Restart clears the issuance registry and all lease records, so prior markers are denied until reissued. Use a four block arrival distance, ten tick route update, 64 block range, 2400 tick lease, one lease per operator and mob, and 32 server limit. | P002-TASK-001, IFC-001, IFC-002, IFC-003. | One reusable legitimate issued stick and one fresh safe lease per valid click. | Existing command and debug components, Forge interaction callbacks, controller adapters, IFC-004. | Real click and server callback tests prove one claim and one intent, repeated legitimate same stick selection after release, target part resolution, obstacle following, owner conflict, invalid target, and no velocity bypass. |
| P002-TASK-003 | BFS2-REQ-009 | Implement idempotent release and lifecycle observers. Release within one server tick for explicit stop, same target click, reselection, held marker loss, permission loss, target or owner death or removal, logout, dimension change, unload, and shutdown. Release on 2400 ticks, greater than 64 blocks, or 200 blocked ticks without progress. Invalidate nonce at restart. | P002-TASK-002, IFC-004. | Token scoped cleanup and restoration result for every exit. | Lease storage, lifecycle callbacks, adapter restoration boundary, IFC-004. | Each reason clears only owned intent, goal, modifier, and memory. Fixture ordinary valid goal selection resumes within 40 ticks with unchanged health and unrelated memory. |
| P002-TASK-004 | BFS2-REQ-008, BFS2-REQ-009 | Add follow category payloads and parser expectations before broad real path testing. Run bounded server and GameTest cases, then exact laptop residual input and moving owner test against a disposable dedicated server. | P002-TASK-002, P002-TASK-003, IFC-001, IFC-008, EXT-001. | Sanitized complete proof packets and an explicit result for every case. | BfsDebugManager, parser, support guide, GameTests, dedicated server fixture, local client debug capture. | All acceptance cases, negative security cases, timeout and recovery are correlated. Client evidence proves actual click and world synchronization. |
| P002-TASK-005 | BFS2-REQ-008, BFS2-REQ-009 | Update documentation and support capability matrix, review complete diff, preserve evidence, and perform sequential phase integration, default verification, signed tag, and postmerge wiki synchronization. | P002-TASK-004, EXT-002, completed cleanup. | Merged phase, signed tag, documentation, synchronized wiki, and downstream handoff packet. | README, DOCUMENTATION, docs index and support guide, wiki, IFC-008 integration. | Required checks, completed private independent review, GitHub merge commit, default verification, artifact inspection, postmerge wiki synchronization, and tag evidence. |

Task 001 blocks tasks 002 through 004 because an unexamined controller cannot be silently mapped to ordinary navigation and its experiments need observable prototype leases before they start. Tasks 002 and 003 may share adapter fixtures only after each fixture's ownership hook is documented. Task 004 extends diagnostic assertions and completes broad evidence. A failed real artifact preflight, missing license or hash, or uncooperative controller stops compatibility acceptance, releases any experimental lease, retains sanitized failure evidence, and routes an `ADAPTER_CONFLICT` defect. It does not remove that family, create a namespace whitelist, or convert a fixture into a claim of external compatibility.

## Architecture and Implementation Boundaries

The logical server is sole lease authority. The stick marker is a request, not authority: it carries a namespaced marker version, issued stick identifier, and owner reference. The server session issuance registry validates that authorization against held stack, UUID, permission level two, and interaction. A successful claim then creates a distinct opaque per lease nonce that exists only in the server lease record. Releasing a lease invalidates only that nonce and leaves the legitimate issued marker reusable during the same server session. Server restart clears both issuance and lease records, so every old marker is denied until issuance occurs again. Copying NBT, replaying a stale marker, or sending both Forge interaction callbacks cannot manufacture a lease. Console issuance has an explicit online recipient and is independent of any unrelated owner join.

Registry driven capability detection evaluates the clicked living mob's actual controller behavior. It must recognize families and choose named adapter capabilities from P002-TASK-001 rather than accepting or rejecting by namespace or entity type whitelist. Multipart selection resolves a dragon part to its owning living mob. Players, projectiles, armor stands, items, vehicles, and nonliving entities return a rejection with no mutation. Boss adapters may temporarily suppress only a destructive travel side effect proven to compete with the lease; health, phase progression, attacks, brain memories, and unrelated goals remain owned by the original controller.

One active lease exists per operator and one owner per mob. A valid new target is fully validated before it releases the previous owned lease. A second click on the same target releases it. Another operator receives `OWNER_CONFLICT`, never a steal. The lease stores UUIDs and dimension IDs rather than live entity references, has no persistent follow flag, and dies at restart. The state machine is issued, claimed, active, blocked, released, or rejected. It is serialized on the server thread. `release` is safe to repeat after any partial callback sequence and invalidates only the per lease nonce before adapter cleanup.

Every follow route creates an IFC-003 `follow` intent with current settings revision and an expiration. The destination follows the owner without collision, drowning, surface, or controller bypass: aquatic entities retain wet safe body envelopes, ground entities retain traversable terrain, flying entities retain bounded safe air path, and no route crosses dimension boundaries. Safety and survival outrank follow. Combat and ordinary state resume only through their normal validity checks after token scoped cleanup. Actual displacement, blocked routes, and solver reductions are recorded separately from desired follow target.

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| P002-RISK-001. Controller does not expose safe temporary ownership. | Inventory lacks an ownership hook or experiment records conflict. | Emit `ADAPTER_CONFLICT`, create no unsafe movement owner, and keep required acceptance open. | Release token scoped state, retain manifest failure, repair adapter within phase. | Real special and third party controller experiment. |
| P002-RISK-002. Forged, copied, stale, or duplicate marker. | Issuance authorization, per lease nonce, owner, hand, permission, and interaction correlation disagree. | Reject with no lease or controller mutation. | A legitimate issued marker remains reusable after lease release. Restart requires new issuance. | Forgery, copy, stale restart, off hand, dual callback, and reuse after release fixtures. |
| P002-RISK-003. Existing lease is stolen or reselection partially releases. | Owner map and mob map disagree or a second owner claims same target. | Reject steal. Validate new claim before old release. Same target click releases once. | Idempotent release reclaims only matching token. | Two operator contention and selected target reselection. |
| P002-RISK-004. Safety route fails or makes no progress. | IFC-003 reason, range, route progress, and blocked tick counter. | Brake or hold safely. Release after 200 blocked ticks and never teleport or alter physiology. | Clear follow intent and prove normal controller selection within 40 ticks. | Obstacle, land, water, ceiling, and range fixtures. |
| P002-RISK-005. Lifecycle callback loses an entity reference. | UUID lookup or dimension validity fails. | Release within one tick where callback observes exit, retain no live reference, and invalidate restart token. | Reload finds no revived lease or owned marker state. | Death, unload, logout, dimension, shutdown, and restart fixtures. |
| P002-RISK-006. Cleanup deletes unrelated behavior. | Before and after controller snapshot differs outside token owned records. | Restore only adapter installed goal, modifier, memory, and intent. | Remove token state once and resume ordinary valid goal. | Health, phase, memory, combat, and normal scheduling assertion in 40 ticks. |
| P002-RISK-007. Capture reports success despite loss or privacy leak. | Footer, counters, record bounds, and redaction validation fail. | Mark `CAPTURE_INCOMPLETE`; do not accept result. | Stop capture, preserve minimal sanitized failure, correct instrumentation, rerun. | Overflow, I/O, absent target, denied command, and parser tests. |

## Diagnostics and Debugging

**Requirement IDs:** BFS2-REQ-008, BFS2-REQ-009, BFS2-REQ-022
**Task IDs:** P002-TASK-001, P002-TASK-002, P002-TASK-003, P002-TASK-004, P002-TASK-005
**Controls:** Reuse permission level two `/bfs debug on follow <ticks> [targets]`, `/bfs debug status`, and `/bfs debug off` from IFC-001. The local console syntax is `/bfs debug followme <recipient>`, `/bfs debug followme status <recipient>`, and `/bfs debug followme stop <recipient>`. The scoped capture example is `/bfs debug on follow 400 <target>`. Server controls reject unprivileged sources. Console captures entity categories only with explicit targets. `status` must reveal side, target count, tick and wall deadline, counters, and exact output path.  
**Signals:** Server records use the session pseudonym, registry entity type, side, dimension, capture and intent correlation, settings revision, adapter capability, desired target and safe actual pose in blocks, clearance, route distance in blocks, elapsed and blocked ticks, lease age in ticks, token validity outcome, reason, and restoration result. No raw UUID, player name, private endpoint, or token value leaves server memory.  
**Collection procedure:** Use the following bounded server first runbook, then the required residual client procedure.

1. On node-1, identify the phase candidate source commit, jar SHA 256 and SHA 512, dependency and config digests, unique disposable server runtime, sanitized fixture ID, target UUID aliases, expected capture directory, and cleanup boundary. Register teardown before creating runtime files. Configure the exact runtime EULA to effective `eula=true` and read it back.
2. Start the no GUI dedicated server, verify candidate readiness and controllable console, construct the documented fixture without using console to fake the tested click or permission path, and enable a `follow` capture for the required explicit target and bounded tick window. Confirm `status` reports selected target and remaining bounds.
3. Issue a legitimate marker with `/bfs debug followme <recipient>`, inspect `/bfs debug followme status <recipient>`, use the real interaction entry path, move the target owner through the named fixture route, then trigger one required release stimulus with the relevant natural event or `/bfs debug followme stop <recipient>`. Correlate claim, adapter selection, intent, safety decision, route progress, release, controller restoration, footer, and parser result. Repeat a separate bounded fixture for each negative, timeout, restart, and absent target condition. Verify a released legitimate marker can acquire a new lease before server restart.
4. Stop capture, inspect terminal completeness and parser assertions, retain only the sanitized packet required by IFC-008, gracefully stop owned server, verify process exit, remove exact test created runtime, world, logs, crash reports, captures, configuration, downloads, and scratch paths, then verify absence. Report `CLEANUP_INCOMPLETE` separately if any owned resource remains.

**Headless verification:** Node-1 runs bounded server unit, dispatcher, integration, and dedicated server GameTest paths after inspecting the actual task graph for no client process. These prove server issuance, permission, nonce, lease arbitration, adapter choice, progress bound, lifecycle release, and forty tick restoration. They cannot prove user input or client synchronization.  
**Client verification:** The residual claim is an actual laptop player right clicking the issued stick on a target, entering the exact dedicated server world, seeing the selected mob follow a moving owner safely around an obstacle, and observing release synchronization. Before assertions, inspect the pinned launcher for its supported version specific automatic direct connection method to the exact private endpoint. If it provides that method, use it. If it does not, use the existing authorized desktop control to connect without altering the personal server list. Confirm server console evidence records the intended player joining and the client has entered the matching server world and dimension before any assertion. Client logs and targeted visual evidence complement server capture. If existing authorized desktop controls cannot perform that input, request owner participation only for this named action and keep this gate unverified.  
**Client audio isolation:** The laptop procedure discovers an isolated disposable instance and its pinned version master volume configuration before launch, setting its master output to zero without touching personal instances. It verifies active desktop and discrete renderer, starts only the owned candidate, binds exact Hyprland address, class, title, and PID via `hyprctl clients -j`, correlates that PID or descendant with only its PipeWire or PulseAudio playback stream, mutes that stream with `wpctl` or `pactl`, and reads back muted state before connection or assertions. After reconnect, resource reload, device change, or stream recreation, it rebinds and remutes the replacement. Ambiguous identity or mute stops the owned client and leaves this gate unverified. Teardown stops the owned client, watcher, route, and server, verifies their processes and stream are gone, then removes exact disposable instance state.  
**Budgets and privacy:** Capture is default off. Preserve IFC-001 maximum 32 targets, 20 through 36000 ticks, 30 minute wall deadline, 8192 queue records, 100000 emitted records, 64 MiB capture, 256 MiB directory, five tick normal movement samples, and at most 200 ticks at one tick detail for eight targets. Follow records are transition driven and rate limited to route updates plus terminal transitions. Overflow, I/O failure, target loss, or missing footer fails evidence.  
**Regression and support:** Add parser and capture self tests for follow payload schema, redaction, forbidden server token output, denied controls, incomplete captures, and default off overhead. Update the support guide with issuance, scoped capture, status, real reproduction, stop, exact log location, redaction, and minimum packet steps. Rerun affected IFC-003 and ordinary controller regressions after every adapter change.

| Signal | Source and unit | Expected observation |
|---|---|---|
| `follow.claim` | Server event, one per accepted interaction | One random per session lease alias, owner and mob pseudonyms, adapter, expiry tick, and one owned intent. |
| `follow.reject` | Server event, one per rejected interaction | `INVALID_STICK`, `PERMISSION_DENIED`, `OWNER_CONFLICT`, invalid target, or capability reason with no mutation. |
| `follow.adapter` | Server event, claim and release | Inventory capability ID and real controller hook agree with manifest. |
| `follow.intent` | Server event, at most route update interval | Desired target, safe actual pose, horizontal and vertical safe velocity, clearance and IFC-003 reason. |
| `follow.progress` | Server event, blocks and ticks | Progress refreshes under valid route. Blocked counter reaches 200 before route failure release. |
| `follow.release` | Server event, terminal once | Reason, one tick release bound where applicable, token scoped cleared records, and restoration result. |
| `follow.restore` | Server event, ticks after release | Ordinary valid goal is selected by tick 40, unchanged health and unrelated state digest. |
| capture footer | IFC-001 terminal summary | No dropped records or error, complete result, exact output path, and parser acceptance. |

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Execution host and prerequisites | Evidence artifact |
|---|---|---|---|---|---|---|
| P002-TASK-001, BFS2-REQ-008 | Registry inventory, prototype diagnostic schema, and artifact preflight validation. | Prototype leases use the named Zombie, Salmon, Phantom, Frog, Villager, Harbor Seal, Dragon part, Wither, independent fixture, and third party controller routes. | Dedicated server proves same tick acquisition records, one safe route update, progress by tick 400, blocked release at 200 ticks, and ordinary controller decision by tick 40 after release. | Missing hook produces `ADAPTER_CONFLICT`, safe release, mandatory open defect. No third party runtime starts before URL, version, Forge compatibility, hashes, license, and security preflight are recorded. | Node-1 headless. Exact pinned Forge candidate, official artifact hash, license, security record, no client. | Compatibility manifest and IFC-008 packet. |
| P002-TASK-002, BFS2-REQ-008 | Marker codec, issuance registry, permission, nonce, hand, and callback dedup tests. | Claim map and adapter selection tests. | Real right click on each representative family and moving obstacle route. | Forged or copied NBT, stale token, player and nonmob rejection, conflicting owner, duplicate callbacks. | Node-1 for server evidence. Laptop only for residual real click, after EXT-001. | Complete `follow` capture, parser result, client evidence. |
| P002-TASK-003, BFS2-REQ-009 | Idempotent release state machine and no persistent restart lease tests. | Lifecycle event to adapter cleanup tests. | Dedicated server executes stop, same click, reselection, no stick, no permission, death, logout, dimension, unload, shutdown, timeout, range, and 200 blocked ticks. | Verify one tick release bound and ordinary selection by tick 40. | Node-1 no GUI. EULA readback, disposable runtime and console. | Lifecycle matrix with IFC-008 identities and cleanup outcome. |
| P002-TASK-004, BFS2-REQ-008, BFS2-REQ-009 | Parser schema, redaction, default off, counter and footer tests. | GameTests cover server contracts and safety handoff. | Laptop connects to matching node-1 dedicated server, actual input selects and releases, target follows a moving owner. | Lost stream, endpoint, renderer, or mute leaves client gate unverified. Incomplete capture reruns from setup. | Node-1 server plus laptop client. Matching identities, private route, server readiness, joined world proof, silent client contract. | Sanitized server and client packet, targeted visual, cleanup proof. |
| P002-TASK-005 | Diff check, documentation link checks, artifact inspection and hash validation. | Required checks and completed private independent review. | GitHub merge commit on `1.20.1`, verify resulting default and signed annotated phase tag, then synchronize the wiki from merged documentation. | Failed check, review finding, signing failure, or cleanup incomplete blocks merge and transition. | Repository integration capability EXT-002. | Merge, tag, docs, review, postmerge wiki synchronization, and final phase packet. |

## Documentation, Operations, and Release

Document the marker issuance and interaction workflow, reusable issued stick versus per lease nonce boundary, eligible and ineligible target categories, one owner and one follower limits, duration, range, release causes, safety behavior, and the fact that third party support is adapter and artifact evidenced rather than a future universal promise. The support guide must contain the bounded follow capture procedure, exact console syntax, interpretation of `ADAPTER_CONFLICT`, `OWNER_CONFLICT`, and release reasons, redaction instructions, and escalation packet contents. Update documentation indexes and links. Associate phase work with its milestone and pull request during execution. After the approved merge, verified default, and tag, synchronize the wiki from the merged documentation. Publish no release.

## Risks and Evidence Invalidation

| Risk ID and owner task | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| BFS2-RISK-004, P002-TASK-001 through P002-TASK-004 | Early all family compatibility experiment and adapter specific ownership. | Adapter ID, intent, progress, release, restoration signals. | Safe release and repair adapter. No exclusion. | Affected controller and broad compatibility proof. | Rerun its experiment, lifecycle route, and client case if presentation changed. |
| BFS2-RISK-012, P002-TASK-004 | Default off bounded diagnostic capture and strict footer. | Counters, dropped records, wall deadline, I/O and parser checks. | Mark packet unusable, retain minimal sanitized failure, rerun. | Any assertion dependent on incomplete capture. | Rerun from capture setup. |
| BFS2-RISK-013, P002-TASK-004 | Exact host, renderer, window PID, stream mute, and resource ownership checks. | Host identity, muted stream, endpoint, processes, paths, cleanup. | Stop owned client on ambiguity and reconcile exact leftovers. | Client visual and interaction evidence. | Revalidate EXT-001 and rerun only client residual case. |
| P002-RISK-006, P002-TASK-003 | Token scoped cleanup and controller snapshots. | State digest and forty tick ordinary goal signal. | Idempotent release without global goal reset. | Restoration, safety, and downstream no competing intent proof. | Rerun lifecycle and ordinary controller fixtures. |

## Phase Completion Packet

The closure packet contains the committed source identity, Forge testing jar SHA 256 and SHA 512, dependency and configuration digests, complete compatibility manifest, third party artifact provenance and security record, static and server test results, complete diagnostic captures and parser outputs, laptop client interaction evidence, lifecycle and restoration matrix, docs changes, review and required check state, merge commit, resulting `1.20.1` verification, and signed annotated phase tag. Each retained runtime packet uses IFC-008 with requirements, task IDs, fixture, world alias, host roles, tick window, result, and cleanup status.

Every verification declares its exact disposable server runtime, client instance, world, logs, captures, artifact copies, temporary configuration, process, audio watcher, and scratch reports before it starts. Teardown gracefully stops only owned processes, waits for their exit, preserves only sanitized required evidence, removes only verified test created paths after their final consumer, and verifies absence on both hosts. Source, tracked fixtures, owner data, personal instances, shared caches, and active worktrees are never cleanup targets. Exact leftovers keep cleanup incomplete and block phase closure until reconciled.

## Next Transition

After this phase is merged through GitHub, resulting `1.20.1` is verified, the signed annotated phase tag exists, the wiki is synchronized from merged documentation, all completion packet evidence is complete, and no known mandatory phase owned defect remains, atomically advance to BFS2-PHASE-003. Its first registered work package extends diagnostics and real water and boat source events. Do not begin it before these gates pass.
