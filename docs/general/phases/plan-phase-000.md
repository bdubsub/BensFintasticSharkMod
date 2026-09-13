# Phase 000 Execution Plan

> **Plan ID:** PLAN-PHASE-000  
> **Phase ID:** BFS2-PHASE-000  
> **Owner:** Repository maintainer  
> **Classification:** MANDATORY  
> **Master plan:** [plan.md](../plan.md)  
> **Phase sequence:** 000 of 007

## Purpose and Ownership

This phase establishes the truthful PR29 baseline, makes the existing bounded diagnostic path sufficient to explain the performance failure, and closes the retained performance work through an installed Forge comparison. It owns only BFS2-REQ-001, BFS2-REQ-020, and BFS2-REQ-022. The master remains authoritative for product scope, acceptance thresholds, interfaces, phase order, and endpoint. This file supplies the execution order, evidence, recovery, and integration detail for Phase 000 only.

## Evidence-Based Entry State

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| OBSERVED | source baseline | The retained PR29 worktree is `envy/rc1-release-evidence` at `40207d1b4cbe8db9963f32e43b79bdcfce58918a`, based on `1.20.1`; the root anchor is historical and dirty. | F001, F002, SRC-102, SRC-103 | Recheck ancestry, complete status, protected paths, and remote PR/default state before any edit. |
| OBSERVED | performance | The historical candidate was finally rejected after 22,400 contiguous measured ticks. Its doubled cases were never run. | F003, F004, SRC-104, SRC-105 | Historical records remain immutable. Every new candidate requires its own source, artifact, dependency, configuration, harness, and raw measurement binding. |
| OBSERVED | diagnostics | PR29 includes bounded server and separate client diagnostic managers, a parser, support guide, and diagnostic GameTests. | F005, SRC-110 | Recheck source hashes and actual dispatcher behavior before extension. |
| INFERRED | performance causes | Navigation, collision and recovery, sensing, species policy, stingray contact, allocation, and replenishment are leads. Lobster death churn is not a proven cause. | F015, SRC-105 | A profiler and category counters must attribute cost before a repair is selected. |
| OBSERVED | runtime boundary | The performance workload is server observable and requires no client or player join. | DEC-007, verification strategy | A later visual or client assertion remains outside this phase and cannot be inferred from server results. |

## Scope Boundaries

### Included Scope

- BFS2-REQ-001. Reconcile the retained source, PR29, issue28, protected working state, toolchain, and evidence without restoring retired plans or relabeling failure.
- BFS2-REQ-022. Verify and extend the reusable IFC-001 diagnostic core, parser, permission and completeness behavior before performance profiling.
- BFS2-REQ-020. Reproduce, attribute, repair only evidenced causes, and pass the ordinary and doubled installed Forge matrix with the retained threshold.

### Explicit Exclusions

- BFS2-REQ-002 through BFS2-REQ-019 are owned by later phases. Phase 000 may emit only current diagnostic categories and direct current path observations needed for performance attribution.
- BFS2-REQ-021 and the final testing JAR delivery are Phase 007 work. This phase produces its own candidate evidence and PR29 integration proof only.
- FUT-001 through FUT-004, NG-001 through NG-006, public publication, a new profile system, balance choices, asset redesign, Fabric work, and unrelated optimization are excluded.
- No Minecraft client launches in this phase. The performance matrix has no residual input, rendering, synchronization, or presentation claim.

## Phase Contract

### BFS2-PHASE-000 — Truthful baseline, diagnostics, and performance recovery

**Objective:** Integrate PR29 only after the current source is protected, diagnostic evidence explains the observed regression, and a new installed Forge candidate completes and passes all four retained comparison cases.  
**Owner:** Repository maintainer  
**Dependencies:** EXT-002  
**Canonical requirements:** BFS2-REQ-001, BFS2-REQ-020, BFS2-REQ-022  
**Documentation and release impact:** Update `README.md`, `DOCUMENTATION.md`, `docs/README.md`, `docs/test/debug-diagnostics.md`, `docs/test/packaged-performance.md`, and the affected verification evidence only with executed results. Reconcile PR29 and issue28 after actual acceptance. No public release.  
**Next transition:** BFS2-PHASE-001 at its first Work Packages entry after the merged default and signed Phase 000 tag are verified.

**Entry criteria**

- The retained PR29 source, PR head, issue28 state, historical rejection, protected root state, Java 17, checked wrapper, Forge 47.2.0, GeckoLib 4.4.7, SmartBrainLib 1.14.2, author identity, signing capability, and required checks are revalidated without mutation.
- No deleted plan or cursor is restored, and no future phase branch is created.

**Implementation scope**

- BFS2-REQ-001. P000-TASK-001 freezes the truthful source and evidence intake.
- BFS2-REQ-022. P000-TASK-002 delivers any missing IFC-001 observability and parser assertions before profiling.
- BFS2-REQ-020. P000-TASK-003 profiles real retained workload and changes only directly attributed work.
- BFS2-REQ-020 and BFS2-REQ-022. P000-TASK-004 proves the complete performance and diagnostic bounds matrix and cleanup.
- BFS2-REQ-001, BFS2-REQ-020, and BFS2-REQ-022. P000-TASK-005 records only truthful results, completes the PR29 integration workflow, verifies `1.20.1`, reconciles issue28, and creates the required signed annotated phase tag.

**Execution order**

1. `P000-TASK-001` executes BFS2-REQ-001 and establishes the candidate and protected-resource manifest.
2. `P000-TASK-002` executes BFS2-REQ-022 and makes the diagnostic output and parser sufficient for the profiling questions.
3. `P000-TASK-003` executes BFS2-REQ-020 and reproduces the failure and repairs only a measured bottleneck.
4. `P000-TASK-004` executes BFS2-REQ-020 and BFS2-REQ-022 and runs the full acceptance matrix with diagnostics disabled and the separate bounded off/on diagnostic checks.
5. `P000-TASK-005` executes BFS2-REQ-001, BFS2-REQ-020, and BFS2-REQ-022, documents evidence, reviews, merges, checks resulting default, reconciles tracking, and tags the exact merge.

**Required evidence**

- Source ancestry, protected-state manifest, PR29 and issue28 snapshots, toolchain and signing preflight.
- Complete diagnostic captures, parser assertions, permission and failure behavior, bounded overhead and off/on gameplay parity.
- Original failed identity plus new profiler attribution and four complete 2,400 warmup plus 36,000 measured tick cases, each with source, JAR, dependency, config, harness, population, death, work, percentile, and completeness records.
- PR checks, private independent review, merge commit on `1.20.1`, signed annotated phase tag, documentation links, issue status, and verified cleanup.

**Exit criteria**

- All four installed Forge cases preserve seed 240024, all 22 species, actual predation, periodic replenishment, ordinary target 63 or doubled target 126, no player join, 2,400 warmup ticks, and 36,000 contiguous measured ticks.
- At ordinary and doubled populations, each candidate p95 is at most 110 percent of its paired baseline. Under the retained environment, the ordinary candidate p95 also does not exceed the historical 7.639247 ms limit. No substituted workload, incomplete trace, dropped sample, disabled safety, or historical relabeling is accepted.
- PR29 is merged through GitHub into `1.20.1`; resulting default ancestry, required checks, signed annotated phase tag, issue28 reconciliation, evidence paths, and cleanup are verified.
- No known mandatory Phase 000 defect remains.

## Shared Contract Projection

```json
{
  "phase_id": "BFS2-PHASE-000",
  "canonical_requirement_ids": [
    "BFS2-REQ-001",
    "BFS2-REQ-020",
    "BFS2-REQ-022"
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

IFC-001 is produced by the existing `BfsDebugCommands` and `BfsDebugManager` server path. P000-TASK-002 verifies the existing `bfs-debug-v2` header, bounded writer, terminal summary, parser, permission level 2, redaction, and disabled path before P000-TASK-003 uses only server captures for attribution. Current server categories are `all`, `movement`, `brain`, `combat`, `population`, `advancement`, and `algae`; performance fields extend only this version compatible record and must identify absent values with a reason rather than a fabricated zero. P000-TASK-004 consumes it to prove completeness, bounds, and overhead; later phases consume the unchanged core and add their own typed categories.

IFC-008 is produced by the packaged performance observer, harness, matrix runner, analysis output, and Phase 000 integration record. P000-TASK-001 establishes original and candidate identity; P000-TASK-003 produces profiling input; P000-TASK-004 produces the full case evidence; P000-TASK-005 consumes the verified branch for the sole allowed integration chain. Later phases consume the merged default and Phase 000 tag, never an unmerged performance claim.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| EXT-002 | Repository and signing environment | EnVisione write access, EnVy identity, registered SSH signing key, actual protected default and required checks | Nonmutating authenticated and signing preflight before any commit or tag | Stop integration work. Preserve local evidence and report the exact unavailable capability. |
| PR29 source and issue28 | Existing retained worktree and GitHub records | `40207d1b4cbe8db9963f32e43b79bdcfce58918a` is intake provenance. Classify later planning-only or current branch commits against it; default is `1.20.1`. | Ancestry, full status, PR base/head/checks, issue text, review state, and protected-file manifest | Stop on unclassified divergence. Do not work from historical root, overwrite state, or reinterpret remote evidence. |
| Historical performance evidence | SRC-105 | Original baseline and failed candidate identities are readable and unchanged | Compare manifest hashes and report values against F004 | Treat missing or altered history as evidence conflict and do not claim a pass. |
| IFC-001 core | Existing debug manager, commands, parser, guide, and tests | Default off, bounded capture, server console operation, parser understands complete and incomplete results | Dispatcher, manager, parser, and GameTest or unit assertions on real server path | Repair diagnostics first. Profiling cannot begin with unparseable, incomplete, or behavior changing output. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| Verified IFC-001 core | Phases 001 through 007 | Default off, bounded v2 capture with truthful terminal completeness and support procedure | Additive `schemaMinor` fields preserve v2 parsing. Major change requires a new version and fixture migration. | Capture, parser, negative, recovery, off/on parity, and overhead records. |
| Measured performance recovery | Phase 001 entry and Phase 007 final regression | PR29 work is integrated only after full retained matrix passes on new candidate identity | Historical artifact and failure identities remain distinct. Final phase must remeasure final artifact. | Four complete case manifests and reports. |
| Integrated Phase 000 baseline | BFS2-PHASE-001 | Verified `1.20.1` merge commit and signed annotated Phase 000 tag | Next branch starts only from fetched resulting default. | GitHub merge, default ancestry, tag signature, and tracking record. |

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| P000-TASK-001 | BFS2-REQ-001, BFS2-REQ-020, BFS2-REQ-022 | Reconcile retained source, protections, PR29, issue28, toolchain, runtime allocation rules, historical evidence, and exact candidate manifest. | EXT-002, F001 through F005, retained worktree, `1.20.1`, SRC-103 through SRC-105 | Signed off intake record identifying source, default, PR, issue, protected state, historical failures, build graph, and owned scratch boundaries. | Retained `_qa/windows-startup-fix` worktree, PR29, issue28, `docs/verification`, `docs/test`, `tools/performance`, IFC-008. | Exact ancestry and file fingerprints match intake or are explicitly classified. Required tools and headless task graph are identified without launching a runtime. |
| P000-TASK-002 | BFS2-REQ-022, BFS2-REQ-020 | Verify and extend the existing server diagnostic manager, command parser, support guide, and tests before profiling. Add only direct current-path measurements that distinguish navigation, collision and recovery, sensing, species policy, stingray contact, allocation, replenishment, population, deaths, and work without changing decisions. | P000-TASK-001, IFC-001, F005, F015 | Version compatible typed records, parser assertions, support procedure, permission and completeness behavior, and a bounded diagnostic overhead report. | `BfsDebugCommands`, `BfsDebugManager`, `tools/bfs_debug_analyze.py`, parser tests, diagnostic GameTests, `docs/test/debug-diagnostics.md`, IFC-001. | Console on/status/off, permission denial, target absent or removed, timeout, reload, overflow, writer failure, redaction, complete footer, off/on parity, and p95 capture overhead all have falsifiable checks. |
| P000-TASK-003 | BFS2-REQ-020, BFS2-REQ-022 | Reproduce the retained ordinary failure on an installed headless Forge server, profile each named lead, select a repair only when attribution identifies its cost, and preserve current predation and workload semantics. | P000-TASK-001, P000-TASK-002, historical fixture, IFC-001, IFC-008 | Source linked profiler reports, a causal repair rationale, scoped implementation and regression cases, or an honest failure record if no safe attribution is obtained. | `tools/performance/ProbeAgent.java`, `TickProbe.java`, `run_performance.py`, `analyze_performance.py`, `SpeciesBehaviorEngine`, route and movement code only after attribution, IFC-001, IFC-008. | Seeded ordinary fixture reaches bounded profile window. Attribution separates the listed leads. A proposed repair is rejected if it changes population targets, predation, safety, fixture, threshold, or observer semantics. |
| P000-TASK-004 | BFS2-REQ-020, BFS2-REQ-022 | Execute ordinary baseline, ordinary candidate, doubled baseline, and doubled candidate full installed Forge matrix. Run affected regression and diagnostic bounds checks, retain minimum sanitized evidence, and prove teardown. | P000-TASK-003, IFC-001, IFC-008, unchanged fixture and threshold | Four complete case records, comparison report, affected regression results, diagnostic bounds and overhead results, archive inspection, and cleanup record. | Packaged performance runbook, matrix and analysis tools, installed server runtime, parser tests, diagnostic GameTests, affected unit and GameTests, IFC-001, IFC-008. | Each case has 2,400 warmup plus 36,000 contiguous measurements, complete observer output, unchanged threshold, hashes, population and death data. Any missing sample, incomplete capture, failed check, or cleanup failure fails the task. |
| P000-TASK-005 | BFS2-REQ-001, BFS2-REQ-020, BFS2-REQ-022 | Update truthful documentation and evidence, perform the required private independent review and PR29 merge, verify resulting `1.20.1`, reconcile issue28, and create and push the signed annotated Phase 000 tag. | P000-TASK-004, EXT-002, required checks, private independent review | Merged PR29, resulting default proof, signed tag, issue28 status matching evidence, updated support and performance documents, and Phase 001 handoff. | `README.md`, `DOCUMENTATION.md`, `docs/README.md`, diagnostic and performance guides, verification evidence, PR29, issue28, IFC-008. | Full diff and secret scan are clean, required checks and conversations are resolved, GitHub merge commit is fetched from `origin/1.20.1`, tag signature and target match it, issue28 is not closed unless acceptance and merge evidence exist. |

P000-TASK-001 is strictly first. P000-TASK-002 must finish before any profile based decision. P000-TASK-003 may iterate scoped repairs and its local regression in sequence, preserving a new manifest per candidate. P000-TASK-004 begins only after a candidate is attributable and passes local regressions. P000-TASK-005 begins only after all evidence is complete. A failed task retains readable sanitized evidence, tears down only owned resources, restores no historical file, and returns to the earliest invalidated task. No task may proceed in parallel with an unreconciled candidate or integration gate.

## Architecture and Implementation Boundaries

Performance evidence flows from the retained installed Forge fixture through `TickProbe` and the matrix runner to IFC-008 evidence. The diagnostic path is independent and default off: `BfsDebugCommands` validates permission and category, `BfsDebugManager` snapshots data on the server thread, bounded writer ownership serializes immutable records, and `bfs_debug_analyze.py` validates schema, sequence, terminal completion, and selected assertions. Measurements must report desired versus actual work and bounded reason codes where applicable. They must not add world scans, per tick file writes, network packets, random draws, behavior mutations, or unbounded entity references.

The repair boundary is evidence guided. Existing `SpeciesBehaviorEngine`, route simulation, collision and recovery code, sensing, contact damage, allocation, and replenishment are candidate components only. A change must retain actual prey and predator interaction, target populations, seed, observer, thresholds, and safety. It must preserve current serialized identifiers, data, assets, Forge and Java target. Use current classes and test seams rather than a parallel performance subsystem.

Server authority is sufficient for this phase. Captures and matrix evidence carry `side=server`, headless server host role, source and artifact hashes, dependency and configuration digests, fixture ID, sanitized runtime ID, seed, exact tick window, result, and cleanup. No client record is inferred. Concurrent capture starts must reject or report the existing session, repeated stop must be idempotent, and terminal records must state whether counters prove completeness.

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| Historical root or protected state differs | ancestry, status, fingerprints, and protected-state manifest | No edit or runtime allocation uses the mismatched checkout | Stop, preserve all state, locate retained PR29 checkout, and reexecute P000-TASK-001 | Current diagnostics and performance files are present only in accepted source. |
| Incomplete historical or new capture | missing footer, dropped counter, queue limit, I O result, or partial tick window | Result is failed or unverified, never passed | Retain sanitized failure reason, remove owned runtime, correct cause, fresh bounded rerun | Parser rejects truncated and overflow samples. |
| Unauthorized or malformed diagnostic request | command permission and typed argument result | No session starts and no diagnostic writer is allocated | Return actionable denial or validation result; use server console level 2 for valid controlled run | Permission and invalid category, tick, and target tests. |
| Target removal, reload, timeout, or repeated off | lifecycle reason and terminal summary | Capture terminates once with truthful counters and no new records after off by next tick | Analyze completed evidence only if footer is complete; otherwise rerun fixture | Removal, reload, timeout, and idempotent stop tests. |
| Diagnostic changes behavior or exceeds budget | paired seed result, p95 overhead, allocations, output bounds | Capture remains default off and cannot change decisions; over budget is a failed diagnostic gate | Disable capture, repair snapshot or writer path, rerun paired case | Same final decisions off/on plus no more than 5 percent or 0.25 ms p95 allowance, whichever is larger. |
| Repair hides workload or treats death churn as cause | manifest comparison, fixture counters, profiler attribution | Reject change when seed, targets, predation, replenishment, safety, threshold, or observer differs | Revert scoped repair through normal source control and rerun from P000-TASK-003 | Full ordinary and doubled matrix with workload manifests and raw counters. |
| Server launch or cleanup failure | readiness deadline, owned PID, path inventory, teardown readback | No runtime result is accepted while cleanup is incomplete | Gracefully stop only owned process, report exact remaining path or PID, reconcile before another run | Runtime record has shutdown and absence proof. |
| PR, checks, review, merge, tag, or default verification fails | GitHub state, required check results, fetched default ancestry, signature verification | Do not start Phase 001 or mark issue28 resolved | Correct only phase-owned evidence or code, obtain replacement review after material change, then repeat affected gates | Merge commit and signed tag point to same verified resulting default commit. |

## Diagnostics and Debugging

**Requirement IDs:** BFS2-REQ-020, BFS2-REQ-022  
**Task IDs:** P000-TASK-001, P000-TASK-002, P000-TASK-003, P000-TASK-004, P000-TASK-005  
**Controls:** Use existing server console permission level 2 controls, `bfs debug on`, `bfs debug on <category> <ticks> [targets]`, `bfs debug status`, and `bfs debug off`. Status must expose enabled state, side, categories, selected targets, remaining ticks, wall deadline, counters, and exact output path. The parser help determines supported arguments before analysis.  
**Signals:** Every selected record has format, schema minor, capture ID, monotonic sequence, tick, side, dimension, pseudonymous entity, entity type, bounded event, settings revision when present, intent ID when present, bounded reason, and typed data. Performance data distinguishes navigation, collision and recovery, sensing, policy, stingray contact, allocation, replenishment, population, deaths, and work counts. Measurements state units and null plus reason where a value is unavailable.  
**Collection procedure:** This server-only procedure uses a tagged actual fixture and the recorded packaged performance invocation after its required arguments have been validated from `run_performance.py --help`.

1. P000-TASK-001 records source commit, candidate JAR SHA 256 and SHA 512, dependency and configuration digests, fixture ID, seed 240024, headless host role, unique nested runtime path, expected output path, owned server PID, and exact cleanup targets. It records preexisting contents before allocation. A server runtime receives and reads back an effective `eula=true` before launch.
2. P000-TASK-002 starts the dedicated server on node-1 and requires the existing harness ready token within 180 seconds, fixture ready token within 120 seconds, and population ready token within 120 seconds. On the owned server console it runs `bfs debug status`, then the valid scoped command `bfs debug on movement 200 @e[tag=bfs2_probe,limit=8]`, then `bfs debug status` and records the reported exact output path. The tagged fixture applies the actual current path. No command grants behavior under test. An absent entity category target, bad type, or denied caller is a failed setup result.
3. P000-TASK-002 stops with `bfs debug off`, verifies `bfs debug status`, requires a terminal completeness summary, invokes the discovered parser interface, and checks monotonic sequence and matching capture identity. Diagnostic sessions have the retained 30 minute wall deadline. It repeats the path for timeout, removed target, reload, overflow, writer failure, redaction, and repeated stop. A missing footer, dropped record, size stop, or I O error is `CAPTURE_INCOMPLETE`.
4. P000-TASK-003 runs the retained ordinary fixture with diagnostics off for acceptance semantics and separately uses bounded server diagnostic or profiler observations for attribution. Before launch it records the exact existing runbook invocation and all arguments validated by `python3 -B tools/performance/run_performance.py --help`, including suite, installed Forge libraries, selected JAR, dependency JARs, Java executable, and unique run name. It retains source, artifact, config, harness, and exact tick range for each candidate. It records no client signal and no player joins.
5. P000-TASK-004 uses the packaged performance harness to run the four accepted names, `baseline-normal`, `candidate-normal`, `baseline-double`, and `candidate-double`, with `--scale 2` only for doubled cases. Each full run uses the recorded default 2,400 warmup and 36,000 tick arguments. The existing harness requires completion within 4,020 seconds, waits no more than 60 seconds for a progress token, then stops its owned server within 60 seconds or 20 seconds after broken pipe and joins its reader within 5 seconds. It compares capture off and scoped capture on with the same candidate and seeded behavior, then checks maximum targets, 36,000 tick cap, wall deadline, queue and output directory bounds. It never lets enabled capture supply performance acceptance.
6. P000-TASK-005 creates a sanitized support packet containing version and loader, minimal relevant settings, reproduction steps, expected and observed state, capture or test ID, parser summary, and only necessary logs. It removes player names, chat, arbitrary NBT, credentials, private addresses, and unrelated paths. It updates the support guide with enable, reproduce, stop, locate, redact, and minimum evidence instructions.
7. On every exit path, disable capture, verify it stops emitting, preserve only required sanitized evidence outside scratch, gracefully stop the exact owned process, verify exit, delete only verified runtime, logs, world, configuration, download, trace, report, and test output paths after their last consumer, and verify absence. Cleanup failure is recorded separately and blocks phase closure.

**Headless verification:** The actual entry points are the dedicated installed Forge performance server, its owned console, `tools/performance/run_performance.py`, `run_matrix.py`, `analyze_performance.py`, `BfsDebugCommands`, `BfsDebugManager`, parser tests, and the dedicated server GameTest task only after its graph confirms no client or renderer. P000-TASK-004 requires bounded readiness and each full 38,400 tick case. Server logs, observer output, raw summaries, and parser records prove server behavior only.  
**Client verification:** None. This phase measures a no-player server workload and introduces no client behavior. Laptop visual, input, rendering, and synchronization claims remain mandatory only in their owning later phases.  
**Client audio isolation:** No Minecraft client is launched in this phase. The retained no-player installed server workload has no client claim, so no disposable instance, master output setting, Hyprland window identity, application stream mute, replacement stream handling, watcher, or client teardown exists. If scope changes to require a client, Phase 000 must stop for an authorized plan amendment because server evidence cannot substitute for that claim.  
**Budgets and privacy:** Capture is default off. Retain or tighten 32 targets, 20 through 36,000 ticks, 1,200 default ticks, 30 minute wall deadline, 8,192 queued records, 100,000 emitted records, 64 MiB per capture, and 256 MiB per directory. Movement sampling is at most every five ticks per target, except at most eight targets for no more than 200 ticks of high detail. Output is pseudonymous and redacted. No tick thread blocks on I O or reads mutable world state from a writer thread.  
**Regression and support:** P000-TASK-002 adds parser and diagnostic tests with real dispatcher coverage. P000-TASK-004 runs paired off/on parity and overhead checks before full matrix and repeats relevant affected unit and GameTests after repair. `docs/test/debug-diagnostics.md` remains the operator runbook. The sanitized packet is retained only where required evidence needs it; raw runtime output is disposable.

| Signal | Source and unit | Expected observation |
|---|---|---|
| capture identity and completeness | server manager, capture ID, sequence, counters, footer | One bounded session has ordered records and a terminal complete footer, otherwise it fails. |
| diagnostic state | command status, booleans, ticks, targets, bytes, records | Default is off. Status matches selected bounded category, targets, deadline, output path, and counters. |
| profiler attribution | installed server observer, milliseconds and bounded work counts | Navigation, recovery, sensing, policy, stingray contact, allocation, and replenishment costs remain distinguishable before repair selection. |
| workload identity | fixture manifest, seed, targets, warmup and measured ticks | All 22 species, seed 240024, predation, replenishment, no player, and 63 or 126 targets remain unchanged. |
| performance result | raw tick samples, milliseconds, population, deaths, work | Each case completes 2,400 warmup plus 36,000 measured ticks. Candidate p95 is at most 110 percent of its paired baseline at each population, and under the retained environment ordinary candidate p95 is at or below 7.639247 ms. |
| diagnostic overhead | paired p95 milliseconds and final decision state | Scoped enabled capture adds at most 5 percent or 0.25 ms, whichever is larger, and final gameplay decisions match capture off. |
| cleanup state | owned PID and exact path inventory | Owned server exits and every verified disposable path is absent. |

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Execution host and prerequisites | Evidence artifact |
|---|---|---|---|---|---|---|
| BFS2-REQ-001, P000-TASK-001 | Ancestry, fingerprints, wrapper and task graph inspection | PR29 base/head, issue28, protected state and author/signing preflight | None | Historical root, dirty protected path, missing signing, or remote divergence stops work | node-1, retained worktree, EXT-002 | Intake manifest and reconciliation record. |
| BFS2-REQ-022, P000-TASK-002 | Parser, schema, bounds, redaction, permission and stop tests | Actual dispatcher, manager, writer, terminal footer, and GameTest path | Dedicated server console capture of current path | Denial, malformed request, absent or removed target, timeout, reload, overflow, writer failure, repeated off | node-1 headless dedicated server, unique runtime, EULA readback | Sanitized capture, parser output, test report, support guide delta, cleanup record. |
| BFS2-REQ-020, P000-TASK-003 | Affected unit and analysis tests | Source linked profiler plus current diagnostic category assertions | Installed headless Forge ordinary fixture with no player | Missing attribution, changed workload, incomplete observation, or unsafe repair rejects candidate | node-1 installed Forge server, verified fixture and hashes | Profiler attribution report and candidate rationale. |
| BFS2-REQ-020, BFS2-REQ-022, P000-TASK-004 | Analysis and parser regression, archive inspection, affected unit and GameTests | Matrix runner compares four manifest bound cases | Installed Forge ordinary and doubled baseline and candidate, each 38,400 total ticks | Partial window, p95 breach, sample loss, workload mismatch, cleanup failure, or overhead breach requires fresh rerun | node-1 only, headless, exact candidate and unique runtimes, EULA readback | Four raw summaries, comparison, hashes, completeness, population and death data, cleanup proof. |
| P000-TASK-005 | Diff check, secret scan, documentation link check, tag verification | Required GitHub checks, review, merge, fetched default ancestry, issue state | No game runtime | Pending or failed check, unresolved review, merge mismatch, tag failure, or unsupported issue closure blocks transition | GitHub and local signed Git environment, EXT-002 | PR merge record, default commit, signed tag verification, issue record, documentation and final cleanup record. |

## Documentation, Operations, and Release

P000-TASK-005 updates the root README, `DOCUMENTATION.md`, `docs/README.md`, the diagnostic support guide, packaged performance runbook, and verification evidence that actually changed. It preserves original artifact provenance and historical rejection as failed. New candidate documents identify their own checksums, source commit, dependencies, configuration, fixture, runner, comparison result, limitations, and cleanup. It prepares the corresponding wiki changes from tracked documentation and publishes them after the approved PR29 merge. Public release publication remains excluded. Issue28 remains open until the evidence and PR merge satisfy its performance concern; its historical facts are not deleted.

## Risks and Evidence Invalidation

| Risk ID and owner task | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| BFS2-RISK-001, P000-TASK-001 and P000-TASK-005 | Pin retained PR29 source and protect historical root state | Ancestry, status, file hashes, PR and default comparison | Stop mismatched work and return to retained source | Source, performance, integration, and tag evidence | Repeat intake and every downstream task. |
| BFS2-RISK-011, P000-TASK-003 and P000-TASK-004 | Preserve fixture, safety, predation, observer, target populations, and threshold | Harness manifest, full window, population, death, work, and raw sample checks | Reject candidate and profile again without workload change | Profiling and all candidate comparison evidence | Repeat profiler, regressions, and four case matrix. |
| BFS2-RISK-012, P000-TASK-002 and P000-TASK-004 | Default off, bounded writer, immutable snapshots, parser completeness | Off/on parity, p95, footer, dropped count, redaction and output limits | Stop capture, repair diagnostics, and fresh bounded run | Dependent profile and diagnostic evidence | Repeat diagnostic core then affected profile and matrix checks. |
| BFS2-RISK-013, every runtime task | Headless node-1 only, exact runtime ownership, prelaunch EULA readback, teardown registration | Task graph, readiness, owned PID, path inventory, exit and absence checks | Graceful owned shutdown, report and reconcile exact leftovers | Affected runtime result and cleanup proof | Repeat setup and affected run after cleanup. |

## Phase Completion Packet

- Reconciliation manifest covering retained source, PR29, issue28, historical baseline and failed candidate identities, protected paths, exact toolchain, task graph, and EXT-002 preflight.
- IFC-001 implementation and parser evidence, permission and lifecycle negatives, bounded completeness and redaction checks, capture off/on parity, overhead result, and updated support guide.
- Profiling attribution for every named lead, scoped repair rationale, affected regression results, and a record of rejected hypotheses.
- Four complete installed Forge matrix records with required IFC-008 fields, raw tick summaries, p95 comparison, population, death and work values, source and artifact hashes, dependency/config/harness digests, and cleanup status.
- Documentation and evidence updates that identify actual results only, full diff review, generated resource drift check, archive inspection where a candidate JAR is built, and secret scan.
- PR29 review and required check state, GitHub merge commit, fetched `origin/1.20.1` verification, issue28 reconciliation, signed annotated Phase 000 tag verification, and pushed tag proof.
- For every check and runtime, the exact owned scratch paths, processes, retained sanitized evidence destination, shutdown confirmation, cleanup result, and any explicit leftover with recovery action. No audit closes while cleanup is incomplete.

## Next Transition

Only after P000-TASK-005 verifies the merged default and signed Phase 000 tag may execution reread `phases/plan-phase-001.md` and begin BFS2-PHASE-001 at its first Work Packages entry. The next branch starts from the fetched resulting `origin/1.20.1` merge commit. No later phase starts from PR29 or an unmerged Phase 000 candidate.
