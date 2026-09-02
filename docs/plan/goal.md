Objective:

Execute the complete Forge 0.24 contract so every mandatory requirement and every stable-release gate is implemented, tested, audited, fixed where evidence demands, and verified with no known mandatory repository-owned defect. Successful completion is permitted only when the final plan-wide audit passes, the exact default-branch commit is merged and signed, and the approved artifact is bound to that commit with all required release evidence.

Immediate checkpoint:

Active phase: BFS-PHASE-000
Active phase plan: /mnt/hermes/projects/BFSMOD/docs/general/phases/plan-phase-000.md
Active phase entry action: execute P000-TASK-001 to reconcile repository identity, establish the required main integration baseline, preserve owner work, and record the baseline evidence matrix.

Start with one bounded inspection of current evidence. It ends as soon as each mandatory criterion is classified as implemented with valid evidence, incomplete, stale evidence, or externally blocked. Immediately execute the first incomplete or stale-evidence criterion. This map is not a deliverable, so do not stop after producing the map. Do not repeatedly rebuild unchanged evidence mapping, do not produce a narrative audit before implementation, and do not repeat work already supported by fresh evidence. The next action remains P000-TASK-001.

Authoritative plan:

Plan: /mnt/hermes/projects/BFSMOD/docs/general/plan.md
Plan SHA-256: b75748b2dfbe2902015f5872e0bcf9ab7a41324861690833325aef95d5f0b027
Plan manifest: /mnt/hermes/projects/BFSMOD/docs/general/plan.index.json
Plan set SHA-256: 36841ba23be381dba6124a189b747316c2dbd6da88523d598890ec3b523d8dd9
Phase plans directory: /mnt/hermes/projects/BFSMOD/docs/general/phases
Plan handoff: /mnt/hermes/projects/BFSMOD/docs/general/plan.handoff.json

The complete registered plan set is creation-time provenance and comprises plan.md plus all six manifest-registered phase plans. Mandatory scope is BFS-REQ-001 through BFS-REQ-023 and BFS-PHASE-000 through BFS-PHASE-005. Optional and future FUT-001 is excluded. Owner decisions DEC-001 through DEC-003 are resolved and locked. There are no mandatory external prerequisites.

Repository root: /mnt/hermes/projects/BFSMOD
Observed checkout branch: 1.20.1
Observed checkout commit: 06a28f6b3fcc62ee54cead9e62370c86aea0bfb9
Authoritative remote:
origin
https://github.com/bdubsub/BensFintasticSharkMod.git
Observed local default branch: 1.20.1
Observed local default-branch commit: 06a28f6b3fcc62ee54cead9e62370c86aea0bfb9
Observed local remote-tracking ref: origin/1.20.1
Observed local remote-tracking commit: 06a28f6b3fcc62ee54cead9e62370c86aea0bfb9
Current remote default-branch head: 06a28f6b3fcc62ee54cead9e62370c86aea0bfb9
Remote-head evidence: git ls-remote --symref origin HEAD and GitHub API read-only evidence observed on 2026-09-02
Applicable implementation branch: none identified; no active BFS phase implementation branch exists
Applicable open pull request: none identified; open PRs 2 and 4 are unrelated Dependabot maintenance
Authoritative working baseline: established

Local and cached remote state contain only branch 1.20.1 at the observed commit. Live remote state also contains two Dependabot heads. Repository-wide open pull requests are PRs 2 and 4, both unrelated and failing their current checks. The dirty worktree has modified build.gradle, modified plan.md, untracked Content, six phase plans, plan.index.json, and plan.handoff.json. Preserve the user-owned build.gradle line endings and every Content byte.

Execution behavior:

Read the active phase plan through EOF, execute its tasks in dependency order, and satisfy its exit criteria and required evidence before integration. Diagnose each failure to its root cause, implement the smallest plan-conforming fix, add regression coverage, and verify real behavior in every required deterministic, GameTest, server, interactive-client, multiplayer, data, security, and artifact environment.

Do not stack phase branches. Complete the current pull request integration, verify the resulting default branch, and verify its signed phase tag before starting the next phase. Then reread the next contiguous phase file through EOF and continue through all subsequent remaining mandatory work under the same immutable goal. Only the final phase may satisfy the plan-wide Definition of Done.

After integration, check out or otherwise inspect the exact authoritative merged default-branch commit. Rerun every verification gate affected by merge resolution, generated release state, and default-branch configuration. Do not rely only on evidence from the pre-merge implementation branch.

Guardrails and authority:

1. Repository guardrail. Verify the authoritative plan, repository identity, package metadata, and remote still describe the same project, and verify origin is the intended repository. Fetch origin without altering the remote, verify the fetched remote-tracking ref equals the current remote default-branch head, and classify the local default branch as behind, ahead, or diverged. Refresh and inspect without altering the remote. Fast-forward only when safe; do not reset, force, discard, or overwrite unexpected history. Search local branches and remote branches plus repository-wide open pull requests. Resume an applicable active branch when one exists; otherwise branch from the verified authoritative baseline. Do not invent a branch when an applicable active branch exists. Create or resume the implementation branch before modifying tracked files. Do not commit directly to the default branch. Keep the default branch as a safe fast-forward baseline and use authorized pull-request integration. Preserve build.gradle and Content, reject secret-bearing files, and keep unrelated changes out of every diff.

2. Evidence guardrail. Never weaken, skip, disable, delete, or narrow valid tests. Never suppress a valid failure, never ignore a required exit code, never reduce a required threshold, never allow a check to fail, never add a production bypass solely for tests, and never substitute mocked behavior for required real behavior. If a test contradicts the plan contract, prove the contradiction and replace it only with equal or stronger coverage. Documentation changes do not substitute for implementation. Evidence must remain valid and fresh; invalidate stale evidence after affected code, configuration, environment, data, or artifact changes. Do not rerun the same unchanged failing check more than twice without changing code, configuration, environment, instrumentation, or the diagnostic hypothesis.

3. Contract guardrail. docs/plan/goal.md is immutable and create-once. Never refresh, rewrite, rebind, overwrite, or replace the saved goal. Never invoke or run Plan Creator, Goal Creator, or their authors. Read the current authoritative plan set after a detected plan change and classify the plan changes. Plan digests and plan set digests are creation-time provenance, not runtime locks. Never stop solely because of a plan or handoff digest. Routine progress, evidence, status, clarification, and phase transition changes continue without owner input. The requirement map and ledger are temporary internal continuity state; do not commit or publish them into plan.md, status.md, issues, pull requests, or repository documentation.

Verification and stopping:

Run all plan-required checks in order and audit the complete diff. Final hygiene includes git status, git diff --check, git log, secret-bearing file inspection, generated-output review, artifact inspection, and verification of the authoritative remote branch. Fix in-scope failures and continue until the active phase gate passes.

Permitted terminal states: SUCCESS only after the completion endpoint is proved; PLAN_REVISION_REQUIRED only for a material product-contract change, reporting affected stable IDs, evidence, and the owner decision needed; GOAL_REVISION_CONFLICT only when the saved goal changed, reporting the expected and observed goal digest; OWNER_INPUT_REQUIRED — REPOSITORY MISMATCH when same-project identity cannot be proved; and REPOSITORY_STATE_CONFLICT when ahead or diverged default history cannot be safely reconciled. Before returning a repository state, exhaust reconciliation attempts that are safe and non-destructive, using repository metadata and remote evidence. Plan or handoff digest drift is not a stopping condition or terminal state. No other early terminal state is permitted.

Completion endpoint: version 0.24 is fully evidenced, merged to the authoritative default branch through the required sequential phase workflow, signed and tagged, and its approved Forge artifact and release evidence satisfy the plan wide Definition of Done

Continuity:

Persist only the active phase ID and file, completed phase gates, the next contiguous phase, the first unfinished criterion, blocker evidence, and the next action as temporary internal continuity state. Resume from that next action after interruption or compaction. Keep the same immutable goal across every phase transition, and do not refresh the goal when a phase completes or when routine plan evidence changes.
