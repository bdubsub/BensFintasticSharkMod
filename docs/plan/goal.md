Objective:
Complete every mandatory requirement and every stable release gate in the authoritative plan for Forge 1.0-rc.1. Successful completion is permitted only when an approved Forge artifact is bound to the verified 1.20.1 default-branch commit, required runtime verification and the final plan-wide audit pass, and no known mandatory repository-owned defect remains.

Immediate checkpoint:
Active phase: BFS-PHASE-000
Active phase plan: /mnt/hermes/projects/BFSMOD/docs/general/phases/plan-phase-000.md
Active phase entry action: P000-TASK-005. Reclassify mandatory criteria under amended plan, invalidate movement proof predating c3975d04f24eb2b8aa8a07cb55c47ec2f2c08afd, inventory living-species action and presentation coverage, then complete laptop fish and Oceanic state matrix.
Resume /mnt/hermes/projects/BFSMOD-phase-000 at c3975d04f24eb2b8aa8a07cb55c47ec2f2c08afd, preserving user work. EXT-001 readiness and rollback evidence is complete at its bound revision; stale status does not reopen it. Preserve valid unaffected work and rerun stale or affected proof.
First verify origin is the intended repository. Fetch origin to refresh and inspect without altering the remote. Verify the fetched remote-tracking ref against current remote default-branch head. Classify the local default branch as equal, behind, ahead, or diverged. Search local branches, remote branches, and repository-wide open pull requests. Create or resume the implementation branch before modifying tracked files. Resume applicable work; otherwise branch from verified authoritative baseline. Do not invent a branch when an applicable active branch exists.
One bounded inspection ends as soon as each mandatory criterion is classified as implemented with valid evidence, incomplete, stale evidence, or externally blocked. Immediately execute the first incomplete or stale-evidence criterion. Map is not a deliverable. Do not stop after producing it or repeatedly rebuild unchanged evidence. Do not produce a narrative audit before implementation.

Authoritative plan:
/mnt/hermes/projects/BFSMOD/docs/general/plan.md
Plan SHA-256: d89da8f5f87108db9a0bd6b7b18ecc5d26614e540dfea7aee6616c7e9f55b2a2
Plan manifest: /mnt/hermes/projects/BFSMOD/docs/general/plan.index.json
Plan set SHA-256: 9bef4cac83a2f52c1a744835919bffcdcd357f71b89530cdd12cdf0f1105c61a
Phase plans directory: /mnt/hermes/projects/BFSMOD/docs/general/phases
Handoff: /mnt/hermes/projects/BFSMOD/docs/general/plan.handoff.json
Completion endpoint: version 1.0-rc.1, displayed as Release Candidate 1.0, is fully evidenced, merged from envy/0.24 into the authoritative 1.20.1 default and integration branch through the required sequential phase workflow, signed and tagged, and its approved Forge artifact and release evidence satisfy the plan wide Definition of Done
Plan digests are creation-time provenance for the complete registered plan set, not runtime locks.
Observed checkout branch: envy/0.24
Observed checkout commit: a4d4e41511ab47a05f27d72387075e21a5ef3cb5
Repository root: /mnt/hermes/projects/BFSMOD
Authoritative remote:
origin
https://github.com/bdubsub/BensFintasticSharkMod.git
Observed local default branch: 1.20.1
Observed local default-branch commit: 15d3a8ce4913dbe5b647b67dbb5993b2f58c983e
Observed local remote-tracking ref: origin/1.20.1
Observed local remote-tracking commit: 15d3a8ce4913dbe5b647b67dbb5993b2f58c983e
Current remote default-branch head: 15d3a8ce4913dbe5b647b67dbb5993b2f58c983e
Remote-head evidence: git ls-remote --symref origin HEAD and git ls-remote origin refs/heads/1.20.1, observed on 2026-09-04 at 22:09:01 UTC
Authoritative working baseline: established
Applicable implementation branch: envy/0.24-phase-000
Applicable open pull request: none identified for envy/0.24-phase-000 at 2026-09-04T22:09:01Z
Verify authoritative plan, repository identity, package metadata, and remote describe the same project.

Execution behavior:
Read the active phase blueprint through EOF; follow dependencies, ownership, exit criteria, and required evidence. Implement and test real behavior, audit failure and recovery, fix each root cause, add regression coverage, and rerun affected layers.
Integrate each phase by GitHub merge commit into envy/0.24. Verify the authoritative remote branch, unchanged default branch, and signed phase tag before the next phase. Never stack phase branches. Open or queued pull requests, failed checks, unresolved review, missing evidence, or absent tags prohibit the next phase. Reread the next contiguous phase file and continue remaining mandatory work under the same immutable goal. After Phase 005 merges and is tagged, integrate envy/0.24 into 1.20.1. Only the final phase's plan-wide Definition of Done permits success.

Guardrails and authority:
BFS-REQ-001 through BFS-REQ-026, every registered phase, and EXT-001 are mandatory; owner decisions DEC-001 through DEC-011 are resolved and locked. Optional future FUT-001 is excluded. Preserve NG-001 through NG-004, 1.0-rc.1, pinned versions, authored inputs, identifiers, saves, Content/, inherited dirty files, and unrelated work. Evidence stays valid only while its source, dependencies, environment, and meaning remain unchanged.
External prerequisite EXT-001 is available and authorized. Owner authority permits `eula=true` for every development or disposable server; verify its exact file before launch and clean it afterward without new approval. Only the verified NVIDIA laptop runs a windowed client; node-1 is headless only. Diagnostics cannot replace required laptop gameplay evidence.
Do not commit directly to the default branch. Update it by safe fast-forward only when safe; later changes require authorized pull-request integration. Do not reset, force, discard, or overwrite unexpected history. Preserve bfs-0.24 and bfs-0.24-final targets. Sign commits and tags as EnVy. Do not infer publication or destructive authority. Use approved secret stores only; never print, log, commit, or persist secrets elsewhere.
Never invoke Plan Creator, Plan Maintainer, or Goal Creator, or spawn their authors. Only a direct current owner request authorizes plan maintenance. docs/plan/goal.md is immutable and create-once; never refresh, rewrite, rebind, overwrite, or replace the saved goal.

Verification and stopping:
Run every phase-ordered test, data-generation, GameTest, Forge build, dedicated-server, laptop-client, multiplayer, security, documentation, and artifact gate. Verify real behavior at required fidelity. Never weaken or skip valid tests, suppress a valid failure, ignore a required exit code, reduce a required threshold, or mark a required check allowed to fail. Never introduce a production bypass solely for tests or substitute mocked behavior for required runtime proof. If a test contradicts the plan contract, prove the contradiction and replace it with equal or stronger coverage.
After integration, check out or otherwise inspect the exact authoritative merged default-branch commit. Rerun every verification gate affected by merge resolution, generated release state, or default-branch configuration. Do not rely only on the pre-merge implementation branch. Bind the final JAR, checksums, SBOM, source manifest, supported attestations, and runtime evidence to that commit; verify integration ancestry and signed immutable tags.
Before integration inspect git status, git diff, git diff --check, and git log. Reject unexplained temporary, generated, unrelated, and secret-bearing files. Documentation changes do not substitute for implementation. After every suite, stop owned processes and remove test-owned resources while preserving required sanitized evidence and user data; verify cleanup after failure too.
Permitted terminal states: SUCCESS after every mandatory gate and completion endpoint passes; PLAN_MAINTENANCE_REQUIRED only for a material product-contract change, naming affected stable IDs and the owner decision; GOAL_REVISION_CONFLICT only if the saved goal changed, reporting expected and observed goal digest values; OWNER_INPUT_REQUIRED — REPOSITORY MISMATCH; REPOSITORY_STATE_CONFLICT. Before returning either repository state, exhaust safe non-destructive resolution using repository metadata and remote evidence. Plan or handoff digest drift is never a stopping or terminal state. No other early stopping state is permitted.

Continuity:
At start, resume, compaction, plan change, and transition, reread the current authoritative plan set, master first, then plan.index.json and every registered plan through EOF; consult plan.handoff.json as derived context. Identify, inspect, and classify plan changes. Progress, evidence, status, clarification, and phase transitions continue without owner input. Never stop solely for plan or handoff digest drift.
Recover revisions, artifacts, active phase ID and file, completed phase gates, first unfinished task, next contiguous phase, blocker, and next action. Preserve completed work; invalidate only evidence affected by changed dependencies. A repository defect is work, not an external blocker. Record unavailable prerequisites and required external action.
The requirement map and ledger are temporary internal continuity state. Unless the plan requires an evidence artifact, do not commit, publish, or add them to plan.md, status.md, issues, pull requests, or repository documentation. Update continuity after meaningful changes; never refresh the goal. Do not repeat completed work. Do not rerun the same unchanged failing check more than twice without changing code, configuration, environment, instrumentation, or diagnostic hypothesis.
