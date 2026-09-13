# Execution Goal Validation

The [execution goal](../plan/goal.md) and separate [active phase cursor](../plan/active_phase.md) define how to execute the existing [debug and content plan](../general/plan.md). They were checked on September 12, 2026. Saving these files does not start implementation or verify gameplay.

## Scope and Initial State

The contract covers all 22 mandatory requirements and all eight registered phases. It preserves the resolved decisions, excluded future work, exact testing JAR endpoint, sequential integration into `1.20.1`, verification of the merged default branch, signed phase tags, and cleanup gates. Public release publication remains excluded.

The initial cursor is revision zero at `BFS2-PHASE-000`, with entry action `P000-TASK-001` and its exact registered phase blueprint. Subsequent cursor changes require the deterministic transition helper, the expected current cursor digest, and a complete temporary receipt proving the current phase gates. Only one contiguous registered transition is permitted. The saved goal remains unchanged.

## Saved File Verification

The unchanged full linter passed against the actual installed files with 1,200 words, 43 nonblank lines, 3 percent plan overlap, and nine registered plan files. Both files were reopened through EOF and matched the validated draft bytes exactly.

| File or contract | SHA 256 |
| --- | --- |
| `docs/plan/goal.md` | `ed3fc782f96d37434bf08b104512b075dae3ae6d7d60891286cadde44f7cd654` |
| Initial `docs/plan/active_phase.md` | `13b1d452aea84c7dd8665cc409534688d9223b2488b278fcf83649958bdb47a8` |
| Master plan | `c763a41ce499288783df1600b9634477e43aca5c1f73740ceb33f747af339bc6` |
| Complete registered plan set | `62f0518259f6423afc4ddb0c84c69115f25e555e8dba4ae94206769e26cdaa03` |

The goal digest records its immutable creation bytes. The cursor digest records only its initial revision; later valid transitions retain their own evidence. These file checks were completed before the documentation commit, so the observed checkout commit in the goal remains historical creation provenance.

## Verification Boundaries

The full registered plan set, manifest, and handoff passed validation with historical repository observations treated as creation provenance. The existing master size warning remains nonblocking. The goal retains that distinction so later commits, routine evidence changes, and valid phase transitions cannot force goal replacement.

The contract requires bounded external retries and completion of independent mandatory work when a prerequisite is unavailable. It preserves failure evidence, root cause repair, regression coverage, adjacent behavior checks, real runtime evidence, and unchanged acceptance thresholds. A repository defect remains unfinished work. Plan maintenance requires a current direct owner request; replacement of the saved goal requires a later explicit replacement request through its protected workflow.

The repository observations were made on `envy/rc1-release-evidence` at `c6dece91a624b9297a21c694df3ca8cbe0d61298`. The live and remote tracking `1.20.1` commit was `33f849318b235ecded3012cdb625690096ad6795`. Local `1.20.1` at `15d3a8ce4913dbe5b647b67dbb5993b2f58c983e` was verified as an older ancestor. These are creation observations, not future execution locks.

No gameplay, performance, rendering, or audio assertion was exercised during this documentation change. Laptop and repository prerequisites remain subject to their planned verification immediately before use. The historical performance rejection remains failed, issue 28 remains open, and pull request 29 remains a draft. Existing source, supplied assets, plans, historical evidence, and the unrelated `build.gradle` change remain untouched.
