Objective:
Complete every mandatory requirement and every stable-release gate in BFS2-REQ-001 through BFS2-REQ-022. Successful completion is permitted only when runtime, performance, cleanup, merge commits, verified 1.20.1 default commit, signed phase tags, and source bound testing JAR checksums pass with no known mandatory repository owned defect.

Immediate checkpoint:
Active phase state: /mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix/docs/plan/active_phase.md
Read active phase state and plan before work. Perform one bounded inspection that ends as soon as each mandatory criterion is classified as implemented with valid evidence, incomplete, stale evidence, or externally blocked. Immediately execute the first incomplete or stale evidence criterion as the next action. The map is not a deliverable. Do not stop after producing it or rebuild it from unchanged evidence. Do not produce a narrative audit before implementation. First recheck pull request 29 and remote.

Authoritative plan:
Plan: /mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix/docs/general/plan.md
Plan SHA-256: c763a41ce499288783df1600b9634477e43aca5c1f73740ceb33f747af339bc6
Plan manifest: /mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix/docs/general/plan.index.json
Plan set SHA-256: 62f0518259f6423afc4ddb0c84c69115f25e555e8dba4ae94206769e26cdaa03
Phase plans directory: /mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix/docs/general/phases
Completion endpoint: All mandatory repairs and debug controls are verified, integrated through sequential merge commits into 1.20.1, tagged, documented, and delivered as an exact verified testing JAR with checksums and source binding. Public release publication is excluded.
Plan digests are creation-time provenance for complete registered plan set, not runtime locks. At start, resumption, or change, read the current authoritative plan set, plan.handoff.json, active_phase.md, every registered plan, and cursor phase plan through EOF. Classify current plan changes; routine progress, evidence, status, clarification, and phase transitions continue without owner input. Never stop solely because a plan or handoff digest changed.
Repository root: /mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix
Observed checkout branch: envy/rc1-release-evidence
Observed checkout commit: c6dece91a624b9297a21c694df3ca8cbe0d61298
Observed local default branch: 1.20.1
Observed local default-branch commit: 15d3a8ce4913dbe5b647b67dbb5993b2f58c983e
Observed local remote-tracking ref: origin/1.20.1
Observed local remote-tracking commit: 33f849318b235ecded3012cdb625690096ad6795
Current remote default-branch head: 33f849318b235ecded3012cdb625690096ad6795
Remote-head evidence: git ls-remote origin refs/heads/1.20.1 observed 2026-09-12 15:16:32.447479+00:00
Authoritative working baseline: established
Applicable implementation branch: envy/rc1-release-evidence at c6dece91a624b9297a21c694df3ca8cbe0d61298
Applicable open pull request: draft pull request 29 into 1.20.1, https://github.com/bdubsub/BensFintasticSharkMod/pull/29
Authoritative remote:
origin
https://github.com/bdubsub/BensFintasticSharkMod.git

Execution behavior:
Read active_phase.md and its active phase blueprint. Implement, test, audit, fix, and verify exit criteria and evidence. Complete integration, verify the default branch and signed tag before the next phase, then create a complete temporary phase gate receipt. Invoke advance_active_phase.py with the expected current cursor digest and atomically advance only active_phase.md to the next contiguous registered phase and declared entry action after the receipt proves exit, audit, integration, default branch, and tag gates. Delete the receipt, reread the next contiguous phase file, keep immutable goal and active_phase.md cursor separate, and continue through remaining mandatory work. Never stack phase branches or start a future phase while the current phase's pull request, checks, merge, default verification, or tag is incomplete. The final phase must pass the plan wide Definition of Done.

Verify the plan, repository identity, package metadata, and remote describe the same project. Verify origin is intended repository. Fetch origin to refresh and inspect without altering the remote. Verify the fetched remote-tracking ref against the current remote default-branch head; classify the local default branch as equal, behind, ahead, or diverged. Fast-forward only when safe. Search local branches, remote branches, and repository wide open pull requests. Resume applicable work; otherwise branch from verified authoritative baseline. Create or resume the implementation branch before modifying tracked files. Do not invent a branch when applicable active branch exists. Do not commit directly to the default branch; allow safe fast-forward reconciliation and authorized pull-request integration only. Do not reset, force, discard, or overwrite unexpected history.

Use node-1 only for headless work and verified silent laptop for visual proof. Stop client if identity or mute fails. Documentation changes do not substitute for implementation. Do not repeat completed work without regression evidence. Preserve failures, fix each root cause, inspect adjacent affected behavior, add regression proof, and continue.

Guardrails and authority:
Preserve completed work, legitimate and unrelated changes, branches, tags, locked decisions, and scope. Optional and future work is excluded. Resolve details; escalate material changes.

Treat docs/plan/goal.md as the immutable goal and create once; active_phase.md is its cursor. Never invoke Plan Creator, Plan Maintainer, or Goal Creator, spawn authors, or refresh, rewrite, rebind, overwrite, or replace goal.md. Plan maintenance requires EnVy's current direct request. Goal replacement requires a later direct Goal Creator invocation with explicit replacement wording. Prior invocations or phase transitions never renew authority.

Reverify the Existing repository write and registered signing capability prerequisite at use. Use approved credential mechanism and credential store. Never print, echo, log, commit, serialize, cache, or place secrets in command output, ledger, fixture, or report. Preserve rejected or incomplete performance evidence. Natural gameplay and silent laptop proof cannot use weaker evidence. One phase cannot satisfy endpoint.

Verification and stopping:
Verify real behavior with highest fidelity evidence. Never weaken, skip, disable, delete, narrow, or reclassify valid tests. Never suppress a valid failure, ignore a required exit code, reduce a required threshold, or mark a required check as allowed to fail. Never introduce a production bypass solely for tests or substitute mocked behavior for required real proof. If a test contradicts the contract, prove it and replace it with equal or stronger coverage. Rerun narrow and higher level checks, including negative, boundary, concurrency, security, recovery paths. Headless tests cannot prove rendering or input.

Accept only complete paired installed Forge captures under unchanged workload and thresholds. Bind evidence and JAR to source, merge, hashes, configuration, host, and cleanup. Before integration run git status, git diff --check, and git log; reject secret bearing files and unintended changes. Verify authoritative remote branch and artifact identity. After integration inspect the exact merged default branch commit and rerun gates affected by merge resolution, generated release state, or default configuration.

Every mandatory requirement, stable release gate, integration, runtime proof, audit, artifact, and cleanup must pass with no known mandatory repository owned defect. Permitted terminal states: SUCCESS only after full proof. PLAN_MAINTENANCE_REQUIRED only for a material product contract change, reporting affected stable IDs and the owner decision needed. GOAL_REVISION_CONFLICT only for an altered saved goal, reporting the expected goal digest and observed goal digest. ACTIVE_PHASE_STATE_CONFLICT only for an unrecoverable cursor conflict, reporting the expected cursor digest and observed cursor digest. OWNER_INPUT_REQUIRED — REPOSITORY MISMATCH applies only to unresolved identity conflict. REPOSITORY_STATE_CONFLICT applies only to unsafe or irreconcilable history. Plan or handoff digest drift is never a stopping state. Before returning repository states, attempt every safe non destructive resolution from repository metadata and remote evidence, and do the same for cursor evidence. No other early stopping state is permitted.

Continuity:
Track active_phase.md, completed phase gates, evidence, blockers, and the next contiguous phase after meaningful work. Transitions update only its four fields and never rewrite goal.md. The requirement map and ledger are temporary internal continuity state; unless required, do not commit, publish, or add them to plan.md, status.md, issues, pull requests, or repository documentation.

Reuse evidence only while code, dependencies, configuration, fixtures, environment, and paths remain unchanged. Do not rerun the same unchanged failing check more than twice without changing code, configuration, environment, instrumentation, or diagnostic hypothesis. Bound external retries; never wait, sleep, or poll indefinitely. Complete every independent mandatory action before recording an external blocker: prerequisite, evidence, attempt, action, and resume check. Repository owned defects remain work. Recover after compaction without restarting phases. Clean test resources while preserving evidence. Never falsely mark an incomplete goal complete.
