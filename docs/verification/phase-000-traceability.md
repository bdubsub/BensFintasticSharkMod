# Phase 000 Traceability and Ownership Matrix

## Binding

This matrix is the reviewed evidence output for `P000-TASK-003`. It is derived from
the live master plan, the registered phase plans, `DEC-006` through `DEC-013`, and
`EXT-001`. It records ownership and evidence routing only. It does not close a
product requirement or beta defect.

The current baseline is bound to phase branch `envy/0.24-phase-000`. Gameplay rows
marked open or unverified remain release blocking and are handed to their canonical
implementation phase. Phase 000 owns only the diagnostic facility in
`BFS-REQ-027` and the baseline, readiness, provenance, artifact, and integration
evidence supporting `BFS-REQ-022` and `BFS-REQ-023`.

Evidence layer codes are `D` deterministic or unit, `G` server GameTest or
dedicated server, `O` offline trace analyzer, `C` laptop client, `M` multiplayer
or reconnect, `W` fixed-seed or population, and `A` packaged artifact.

## Stable requirement ownership

| ID | Canonical primary phase and closure task | Mandatory Phase 005 recheck | Required evidence layers | Negative or recovery case | Current Phase 000 disposition |
| --- | --- | --- | --- | --- | --- |
| `BFS-REQ-001` | `BFS-PHASE-001`, `P001-TASK-002` | `P005-TASK-007`, `P005-TASK-010`, `P005-TASK-011` | D, G, O, C, M, A | Near-vertical route, zero horizontal speed, snap, orbit, unreachable target, target loss, and recovery | Open gameplay handoff. Baseline procedure and DEC-006 routing recorded. |
| `BFS-REQ-002` | `BFS-PHASE-001`, `P001-TASK-003` | `P005-TASK-007`, `P005-TASK-011` | D, G, C, M, A | Missed contact, duplicate damage window, invalid prey, cooldown and recovery | Open gameplay handoff. |
| `BFS-REQ-003` | `BFS-PHASE-001`, `P001-TASK-004` | `P005-TASK-007`, `P005-TASK-011` | D, G, C, M, A | Nonedible, removed item, timeout, path failure, retry suppression and flee priority | Open gameplay handoff. |
| `BFS-REQ-004` | `BFS-PHASE-001`, `P001-TASK-005` | `P005-TASK-007`, `P005-TASK-010`, `P005-TASK-011` | D, G, C, M, A | Death, unload, disconnect, reconnect, dimension change and invalid passenger cleanup | Open lifecycle handoff. |
| `BFS-REQ-005` | `BFS-PHASE-001`, `P001-TASK-006` | `P005-TASK-007`, `P005-TASK-010` | D, G, C, A | Unknown species, missing field, Deep Cold negative and registry mismatch | Open command and habitat handoff. |
| `BFS-REQ-006` | `BFS-PHASE-001`, `P001-TASK-007` | `P005-TASK-010` | D, C, M, A | Unequip, reequip, refresh, reconnect and first or third person pose | Open client handoff. |
| `BFS-REQ-007` | `BFS-PHASE-001`, `P001-TASK-008` | `P005-TASK-007`, `P005-TASK-010` | D, G, C, M, A | Missing reference, unreachable state, death or release during temporary state | Open retained clip handoff. |
| `BFS-REQ-008` | `BFS-PHASE-001`, `P001-TASK-009` | `P005-TASK-007`, `P005-TASK-010`, `P005-TASK-011` | D, G, C, M, A | Wrong case, whitespace, rename, name removal, beached transition and prey exposure | Open retained fish handoff. |
| `BFS-REQ-009` | `BFS-PHASE-001`, `P001-TASK-010` | `P005-TASK-008`, `P005-TASK-009`, `P005-TASK-012` | D, G, C, A | Wrong species input, nonfire death, excluded inventory grant and missing packaged entry | Open item and acquisition handoff. |
| `BFS-REQ-010` | `BFS-PHASE-001`, `P001-TASK-011` | `P005-TASK-007`, `P005-TASK-008`, `P005-TASK-011` | D, G, W, A | Command, bucket, spawner, structure, existing entity, unrelated namespace and mismatch injection | Open replacement-policy handoff. |
| `BFS-REQ-011` | `BFS-PHASE-001`, `P001-TASK-011` | `P005-TASK-007`, `P005-TASK-011` | D, G, W, A | Category mismatch, excess population, restart and bounded-growth recovery | Open population handoff. |
| `BFS-REQ-012` | `BFS-PHASE-001`, `P001-TASK-012` | `P005-TASK-008`, `P005-TASK-010`, `P005-TASK-011` | D, G, C, M, A | No use, obstruction, wrong entity, wrong trigger and prior grant | Open retained advancement handoff. |
| `BFS-REQ-013` | `BFS-PHASE-003`, `P003-TASK-002` | `P005-TASK-003`, `P005-TASK-008`, `P005-TASK-010`, `P005-TASK-012` | D, C, A | Wrong bytes, wrong dimensions, pair swap, missing model and duplicate path | Open icon handoff. |
| `BFS-REQ-014` | `BFS-PHASE-003`, `P003-TASK-003` | `P005-TASK-007`, `P005-TASK-008`, `P005-TASK-011` | D, G, C, M, A | Missing parent, orphan, cycle, retired resource and changed child criteria | Open graph handoff. |
| `BFS-REQ-015` | `BFS-PHASE-003`, `P003-TASK-004` | `P005-TASK-006`, `P005-TASK-007`, `P005-TASK-010`, `P005-TASK-013` | D, G, C, A | Unicode, case, accent, apostrophe, whitespace and truncated captain description | Deterministic and captured-tooltip paths positively disproved; natural display and artifact remain open. |
| `BFS-REQ-016` | `BFS-PHASE-003`, `P003-TASK-004` and `P003-TASK-005` | `P005-TASK-006`, `P005-TASK-007`, `P005-TASK-010`, `P005-TASK-013` | D, C, A | Missing or extra key, fragment, doubled mark, mixed apostrophe and trailing space | Deterministic and captured-tooltip paths positively disproved; complete tree review remains open. |
| `BFS-REQ-017` | `BFS-PHASE-002`, `P002-TASK-004` | `P005-TASK-003`, `P005-TASK-007`, `P005-TASK-010` | D, G, O, C, M, A | Static transform, unreachable clip, snap, arriving spiral, missing pitch and state recovery | Open animation handoff. |
| `BFS-REQ-018` | `BFS-PHASE-002`, `P002-TASK-005` | `P005-TASK-003`, `P005-TASK-007`, `P005-TASK-010` | D, G, O, C, M, A | Missing state, priority fallthrough, bite or death masking, stale thrash and release leak | Open animation and cleanup handoff. |
| `BFS-REQ-019` | `BFS-PHASE-002`, `P002-TASK-006` | `P005-TASK-007`, `P005-TASK-010`, `P005-TASK-011` | D, G, O, C, M, A | Denied prey, stalled route, orbit, duplicate damage, target loss, cooldown and recovery | Open Tiger handoff. |
| `BFS-REQ-020` | `BFS-PHASE-004`, `P004-TASK-003` | `P005-TASK-008`, `P005-TASK-009`, `P005-TASK-010`, `P005-TASK-012` | D, G, C, M, A | Dry or flowing water, invalid support, occupied space, land, wrong tool and exact loot | Open algae handoff. |
| `BFS-REQ-021` | `BFS-PHASE-004`, `P004-TASK-006` and `P004-TASK-009` | `P005-TASK-008`, `P005-TASK-009`, `P005-TASK-011`, `P005-TASK-012` | D, G, W, C, A | Malformed feature, disabled pack, invalid support, absent eligible water and density control failure | Open world-generation handoff. |
| `BFS-REQ-022` | `BFS-PHASE-005`, `P005-TASK-002`, `P005-TASK-004`, `P005-TASK-005` | `P005-TASK-007` through `P005-TASK-014` | D, G, O, C, M, W, A | Any known mandatory defect, stale or mixed evidence, missing control, failed cleanup or unsupported claim | Phase 000 supplies baseline and diagnostic evidence; canonical closure remains Phase 005. |
| `BFS-REQ-023` | `BFS-PHASE-005`, `P005-TASK-006`, `P005-TASK-012`, `P005-TASK-013` | `P005-TASK-006` through `P005-TASK-014` | D, G, C, M, A | Beta or final claim, wrong filename, stale metadata, missing checksum, SBOM or provenance | Phase 000 inventories surfaces only; final RC closure remains Phase 005. |
| `BFS-REQ-024` | `BFS-PHASE-002`, `P002-TASK-010` through `P002-TASK-012` | `P005-TASK-007`, `P005-TASK-010`, `P005-TASK-011` | D, G, O, C, M, W, A | Missing unit or profile, unsupported art, air or habitat denial, lifecycle leak and performance failure | Open species inventory and behavior handoff. |
| `BFS-REQ-025` | `BFS-PHASE-002`, `P002-TASK-010` through `P002-TASK-012` | `P005-TASK-007`, `P005-TASK-010`, `P005-TASK-011` | D, G, O, C, M, W, A | Invalid sensor candidate, unbounded memory, starvation, unsafe target and interruption | Open cognition handoff. |
| `BFS-REQ-026` | `BFS-PHASE-002`, `P002-TASK-013` and `P002-TASK-012` | `P005-TASK-007`, `P005-TASK-010`, `P005-TASK-011` | D, G, O, C, M, A | No substrate match, blocked retreat, expired cloud, cooldown abuse, resource reload and late join | Open octopus handoff. |
| `BFS-REQ-027` | `BFS-PHASE-000`, `P000-TASK-013` | `P005-TASK-007`, `P005-TASK-009` through `P005-TASK-014`, with subsystem hooks from owning phases | D, G, O, C, A | Unauthorized source, empty selection, cap or queue overflow, timeout, missing end marker, mixed artifact, unavailable field labeled as zero, gameplay mutation and cleanup failure | Diagnostic facility is implemented and server-side evidence is current; final packet and later hook rechecks remain. |

## Defect ownership and present disposition

| Defect | Closing phase and task | Phase 005 recheck | Current baseline status | Evidence still required |
| --- | --- | --- | --- | --- |
| `DEF-024-001` | `BFS-PHASE-003`, `P003-TASK-007` | `P005-TASK-010`, `P005-TASK-013` | `POSITIVELY_DISPROVED` for deterministic and captured tooltip paths | Natural chest discovery display and final artifact. |
| `DEF-024-002` | `BFS-PHASE-002`, `P002-TASK-004` | `P005-TASK-010` | `BLOCKED` | Full Cod and Salmon authored state matrix, calibrated motion and smooth pitch evidence. |
| `DEF-024-003` | `BFS-PHASE-002`, `P002-TASK-005` | `P005-TASK-010`, `P005-TASK-011` | `BLOCKED` | Oceanic state transitions, damage timing, vertical route and terminal cleanup. |
| `DEF-024-004` | `BFS-PHASE-002`, `P002-TASK-006` | `P005-TASK-010`, `P005-TASK-011` | `BLOCKED` | Tiger pursuit, non-orbiting depth acquisition, contact, damage, recovery and curiosity arbitration. |
| `DEF-024-005` | `BFS-PHASE-003`, `P003-TASK-007` | `P005-TASK-011` | `BLOCKED` | Fresh profile completion and connected child graph with retired node absent. |
| `DEF-024-006` | `BFS-PHASE-003`, `P003-TASK-004` through `P003-TASK-007` | `P005-TASK-006`, `P005-TASK-010`, `P005-TASK-013` | `POSITIVELY_DISPROVED` for deterministic and captured tooltip paths | Complete advancement tree review and final artifact. |
| `DEF-024-007` | `BFS-PHASE-003`, `P003-TASK-002`, `P003-TASK-005`, `P003-TASK-007` | `P005-TASK-003`, `P005-TASK-010`, `P005-TASK-012` | `BLOCKED` | Seven exact display captures and packaged resource chain. |
| `DEF-024-008` | `BFS-PHASE-004`, `P004-TASK-010` and `P004-TASK-012` | `P005-TASK-008` through `P005-TASK-012` | `BLOCKED` | Natural survival, collection, loot, animated generation, dedicated server and final artifact. |

## Decision, prerequisite, runtime and artifact ownership

| Stable input | Phase 000 application | Canonical closure or recheck | Required proof | Present disposition |
| --- | --- | --- | --- | --- |
| `DEC-001` | Permanent algae status and no temporary-only interpretation | `BFS-PHASE-004`, then `P005-TASK-008` through `P005-TASK-012` | Registered blocks, generation, survival, loot, rendering and packaged IDs | Applied and handed off. |
| `DEC-002` | Retire `shark_whisperer` without orphaning descendants | `BFS-PHASE-003`, then `P005-TASK-011` | DAG, parent, criteria and fresh-profile proof | Applied; downstream graph remains open. |
| `DEC-003` | One deterministic punctuation rule with exact supplied-title exceptions | `BFS-PHASE-003`, then `P005-TASK-006`, `P005-TASK-010`, `P005-TASK-013` | Provider, generated, visual and artifact copy audit | Applied; complete visual and artifact review remains. |
| `DEC-004` | `1.20.1` is canonical default, `envy/0.24` is release work branch, phase branches are sequential | `P000-TASK-012`, then every phase integration task and `P005-TASK-014` | Remote refs, merge commits, ancestry and signed tags | Verified branch model; Phase 000 integration not complete. |
| `DEC-005` | Existing `bfs-0.24` and `bfs-0.24-final` are immutable | `P000-TASK-012`, `P005-TASK-014` | Tag target and no-move audit | Preserved. |
| `DEC-006` | Baseline movement, pitch, route and non-orbiting observations; no invented Phase 000 gameplay pass | `P001-TASK-002`, `P002-TASK-004`, `P002-TASK-006`, `P005-TASK-010`, `P005-TASK-011` | Research-bound profile, per-tick telemetry, pose and route evidence | Classified; downstream calibration remains open. |
| `DEC-007` | Immutable supplied GeckoLib assets and only documented compatibility transforms | `P001-TASK-008`, `P001-TASK-009`, `P002-TASK-002`, `P002-TASK-003`, `P005-TASK-003` | Source/output hashes, bindings, samples and JAR bytes | Provenance preserved; final candidate recheck remains. |
| `DEC-008` | EULA authorization only for exact disposable development or verification runtimes | `P000-TASK-010`, `P000-TASK-011`, `P005-TASK-009` | `eula=true` readback, ready marker, termination, rollback and cleanup | Authorized and partially evidenced; current candidate binding must remain exact. |
| `DEC-009` | Species-specific behavior and SmartBrainLib scope belongs to Phase 002 | `P002-TASK-010` through `P002-TASK-013`, `P005-TASK-011` | Living inventory, bounded cognition, action lifecycle and performance | Handed off; not a Phase 000 pass condition. |
| `DEC-010` | Octopus camouflage and finite ink escape remain Phase 002 implementation | `P002-TASK-013`, `P002-TASK-012`, `P005-TASK-010`, `P005-TASK-011` | Substrate, threat, cloud, retreat, cleanup and multiplayer evidence | Handed off; open. |
| `DEC-011` | Final metadata is `1.0-rc.1`, displayed as `Release Candidate 1.0` | `P005-TASK-006`, `P005-TASK-012`, `P005-TASK-014` | Source, processed metadata, filename, JAR, docs and release surface | Reserved; Phase 000 must not claim final metadata. |
| `DEC-012` | Prefer server logs and strict analyzer before requesting owner connection; preserve client-only gates | `P000-TASK-013`, every owning phase diagnostic hook, `P005-TASK-007`, `P005-TASK-009` | Permission, bounded JSONL, analyzer, client controls and support collection | Implemented server path; later subsystem hooks remain. |
| `DEC-013` | Separate Phase 000 diagnostics and readiness exit from later gameplay closure | `P000-TASK-003`, `P000-TASK-013`, `P000-TASK-010`, `P000-TASK-011` | Honest baseline dispositions, exact downstream tasks and no false passes | Active boundary; Phase 000 remains open on its own gates. |
| `EXT-001` | Bounded authorized packaged-server execution and cleanup | `P000-TASK-010`, `P000-TASK-011`, final `P005-TASK-009` recheck | Exact target, revision, artifact, EULA, ready marker, shutdown and post-state | Historical readiness preserved; current affected binding requires final proof. |

## Phase 000 evidence-layer ownership

| Evidence layer | Phase 000 owner | Canonical consumer | Pass condition | Current boundary |
| --- | --- | --- | --- | --- |
| Stable-ID and cross-phase traceability | `P000-TASK-003` | All phases and `P005-TASK-002` | Every stable ID appears once with owner, recheck, evidence, negative case and disposition | This matrix is the reviewed record. |
| Source and asset provenance | `P000-TASK-002` | `P001`, `P002`, `P003`, `P004`, `P005` | Archive and loose-file hashes, destination edges and packaged paths agree | Preserved evidence; final artifact edge remains. |
| Baseline defect records | `P000-TASK-004` through `P000-TASK-007` | Canonical defect owners and `P005-TASK-004` | Eight statuses, procedures, controls, evidence identities and closing tasks | Gameplay defects remain open where listed. |
| Diagnostic facility | `P000-TASK-013` | Every phase and `P005-TASK-007`, `P005-TASK-009` | Real commands, permissions, bounded captures, strict analyzer, parity, client control, support and cleanup | Server and analyzer evidence current; final packet binding remains. |
| Deterministic audit and GameTest harness | `P000-TASK-008` and `P000-TASK-009` | All implementation phases and `P005-TASK-007`, `P005-TASK-008` | Clean tests, calibration proof, discovery, repeated runs and teardown | Re-run only when affected; no broad restart. |
| Packaged readiness | `P000-TASK-010` and `P000-TASK-011` | `P005-TASK-009` | Exact candidate and dependency set reaches ready state, logs are clean and owned state is removed | Current candidate binding and cleanup remain phase gates. |
| Integration and signed tag | `P000-TASK-012` | `BFS-PHASE-001` and final endpoint | GitHub merge commit, ancestry, signed annotated phase tag and protected-state recheck | Not started until preceding gates pass. |

## Completeness review

The review checked the complete stable requirement register (`BFS-REQ-001` through
`BFS-REQ-027`), defect ledger (`DEF-024-001` through `DEF-024-008`), decisions
(`DEC-001` through `DEC-013`) and `EXT-001`. Each appears exactly once in its
canonical ownership section. `BFS-REQ-022` and `BFS-REQ-023` are explicitly owned
by Phase 005; Phase 000 is supporting evidence only. `BFS-REQ-027` is the sole
canonical Phase 000 requirement. No duplicate canonical owner or unknown stable ID
was found in this review.

Validation commands used:

```bash
rg -n 'BFS-REQ-[0-9]{3}|DEF-024-[0-9]{3}|DEC-00[1-9]|DEC-01[0-3]|EXT-001' docs/general/plan.md docs/general/phases/plan-phase-000.md docs/general/phases/plan-phase-001.md docs/general/phases/plan-phase-002.md docs/general/phases/plan-phase-003.md docs/general/phases/plan-phase-004.md docs/general/phases/plan-phase-005.md
```

Result: all 27 requirements, 8 defects, 13 decisions and `EXT-001` are present;
all have an evidence owner, negative or recovery case, downstream destination and
Phase 005 recheck. The remaining Phase 000 work is diagnostic, readiness, artifact,
cleanup, review, merge and signed-tag evidence, not a missing ownership assignment.

## Resumption checkpoint

The current compile, test and headless GameTest rerun passed on the dirty phase
working tree. The rerun discovered and passed 22 required GameTests and left no
runtime behind. `P000-TASK-013`, `P000-TASK-008`, `P000-TASK-009` and the current
`P000-TASK-010` packaged-server binding have passing evidence for their exercised
scope. The current clean `aa7d851` candidate now has passing `P000-TASK-011`
invalidation-aware artifact and protected-diff evidence, including the exact
packaged server readiness probe and cleanup record. The next unmet Phase 000
gate is `P000-TASK-012` review, merge, ancestry and signed tag. The client-only animation
and presentation gaps listed above remain downstream gameplay evidence, not a
reason to claim a Phase 000 pass or to skip its own readiness and integration
gates.
