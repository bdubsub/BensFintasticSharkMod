# Phase 005 task 001 entry evidence

Date: September 11, 2026

This entry binds Phase 005 to the verified Phase 004 integration and the
current authoritative plan set. The live plan set is rooted at
`/mnt/hermes/projects/BFSMOD/docs/general/`; the copies in this phase
worktree are historical branch content and do not override the live plan.

## Repository and branch boundary

The repository is `bdubsub/BensFintasticSharkMod`, authenticated as EnVisione,
and the remote default branch is `1.20.1`. No `main` or `master` branch exists.
The Phase 005 worktree is `/mnt/hermes/projects/BFSMOD-phase-005` on branch
`envy/0.24-phase-005`. Its `HEAD` is the Phase 004 merge commit
`a919528d5e65e561707de700168babcfcde086a1`, which is also
`origin/envy/0.24`. The branch is not stacked on a phase branch.

The Phase 004 pull request 21 merged through GitHub into `envy/0.24` with
build, analyze, and CodeQL checks passing. Signed annotated tag
`bfs-0.24-phase-004` resolves to the same merge commit and verifies with
EnVy's registered ED25519 signing key. Phase tags 000 through 003 also verify
and their targets are contained in the sequential release work ancestry.

## Authoritative plan and goal bindings

The immutable goal remains unchanged at SHA 256
`f48badcd994c8336aca4cb73b7a04efb72baf508f54343d191e1827cd729e25b`.
The active cursor is revision 4 and names `BFS-PHASE-005`, with entry action
`P005-TASK-001`. Its SHA 256 is
`5030d29747895d5d95cfe4d1485e790a1062f7d059bbc62ccd4ab167cf6b64dd`.

The current authoritative plan files were read through EOF and validated:

| File | SHA 256 |
| --- | --- |
| `docs/general/plan.md` | `857924e4841bc8b13d4fc4a7e9f815d84536ae163aad91ba06601e97dc91bf13` |
| `docs/general/plan.index.json` | `ef4a1f02770f3c9424dcb4c8c5352b5ee4932abd1a09e432309da86a72ab157e` |
| `docs/general/plan.handoff.json` | `5c82c7b485d4da827080a4bac981f2e081b07566ae5914c42f3daf311254814e` |
| `docs/general/phases/plan-phase-000.md` | `311867806b11fe3bea0807a3003940cfe827d53ef528936906f3876fea71347d` |
| `docs/general/phases/plan-phase-001.md` | `44f7606334b5ba0c862324def52521f9923d32ae27d361dfe62a9ec14161f926` |
| `docs/general/phases/plan-phase-002.md` | `5d1ed2bcbbed78d45708a6d5fe0ecbc342ba4e79af104d83366b6c5bc9a00493` |
| `docs/general/phases/plan-phase-003.md` | `5a3165d22fa362062a15b71591ae2d699513c98784f5492fb79e7042f7c6ef30` |
| `docs/general/phases/plan-phase-004.md` | `f7e1846e757e128fe5ebd2812ab1793b9477c58fb7497ef54d43f0a78e59d14f` |
| `docs/general/phases/plan-phase-005.md` | `044d750d85b6a5bee87f1b7c56c01b538ee2ba5b957f0e4bfbc334ab114ce769` |

The authoritative Phase 005 plan sets the final target to `1.0-rc.1`,
displayed as `Release Candidate 1.0`, while preserving the `0.24` branch and
phase tag names. The phase worktree's older plan copies are preserved as
historical checkout content and were not used to redefine the live contract.

## Isolation and protected state

The Phase 005 worktree contains no untracked test runtime, world, capture,
credential, or owner `Content/` input. Its only pre-existing status change is
the protected `build.gradle` line-ending difference. The original worktree
retains its protected owner `Content/` directory, generated cache changes,
server and launcher scratch state, plan authoring changes, and untracked
`docs/plan/active_phase.md`; none was copied, cleaned, staged, or overwritten.

Phase 005 therefore starts from the exact Phase 004 merged source in an
isolated worktree. P005-TASK-001 entry validation is complete. The next action
is P005-TASK-002, which builds the final requirement and defect traceability
matrix before any release metadata change.
