# Phase 001 Entry and Phase 000 Intake

## Entry binding

Phase 001 uses branch `envy/0.24-phase-001`, created only after Phase 000 was
merged into `envy/0.24`. The verified Phase 000 integration merge is
`f641131640481f17bfcfc54999c98700b42c1af6`, and the signed annotated tag
`bfs-0.24-phase-000` resolves to that exact commit. The tag signature verifies
with the EnVy signing key fingerprint
`SHA256:CE014W2Y8QMbKKspTiTQAJ37gK83TV3gup2el94DWb4`.

The phase branch was reconciled with that corrected integration merge before
entry evidence continued. Its reconciliation commit is recorded in the branch
history and preserves all previously reviewed Phase 001 work.

The canonical branch remains `1.20.1` at
`15d3a8ce4913dbe5b647b67dbb5993b2f58c983e`. The release work branch
`envy/0.24` is at `f641131640481f17bfcfc54999c98700b42c1af6`. The corrected
Phase 000 tag target is an ancestor of that work branch and of the reconciled
Phase 001 branch. Both ancestry directions required at entry are present, and
no `main` or `master` branch is used.

The supported entry toolchain is Minecraft `1.20.1`, Forge `47.2.0`, Java `17`,
Parchment `2023.09.03`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`. The
Phase 000 manifest, traceability matrix, evidence follow-up, artifact binding,
and signed merge packet are present in this branch.

## Immutable 0.23 input

The owner supplied archive remains outside tracked source at
`/mnt/hermes/projects/BFSMOD/Content/BFS 0.23 Content.zip`. It passed
`unzip -tqq` with size `25209` bytes and `19` entries, including `14` files.
Its SHA-256 is
`1dcfd0db544184bffd467255ba294b57f0a1376b06023f10034f380b9e5d0eaa`; its
SHA-512 is
`09a8bb217d25cb146f6b63c38c2b269c0c7b8d862459cc0916f4d93b12da81001a6bd308d745ef5b86c0cb3816bc9e7ae222b5b7aaae96eb4b42ba26e7a11527`.
The archived `Atlatnic Salmon` spelling remains immutable provenance. No
archive bytes were copied, normalized, or overwritten.

## Imported Phase 000 disposition

Phase 000 is accepted as the diagnostic, provenance, readiness, and integration
baseline. Its open gameplay rows are imported as explicit work for the canonical
Phase 001, Phase 002, Phase 003, Phase 004, and Phase 005 owners. This entry does
not claim those rows passed. The clean Phase 000 candidate artifact was
`BensFintasticSharks-forge-1.20.1-0.24.jar` with SHA-256
`ac566c2817d470ca5269d5c3d4906dd2be79d72e36cefbe2e4801550f58e80c9` and
SHA-512
`324223131ba2c8315a3908a9d80b082e32ab0377cdad4f6ed5bb2eec8f786c3110504e14f2fa38b38f4d7e62841178f56855c902a4c5d996e83ad9072eae5afb`.
The artifact is a nonfinal `0.24` candidate. Final `1.0-rc.1` metadata remains
owned by Phase 005.

The Phase 000 diagnostic command, strict analyzer, server and client evidence,
GameTest harness, cleanup rules, and invalidation rules are the inherited
diagnostic contract. Phase 001 extends those diagnostics only for its retained
requirements and does not add a second logger or permit debug commands to mutate
gameplay.

## Protected state

The phase worktree inherits the owner `build.gradle` line-ending change. It is
visible as an unstaged change and is excluded from the Phase 001 commit. The
original worktree's generated cache changes, untracked `Content/` directory,
temporary server files, and any personal instances remain outside this worktree
and are not modified. Protected state will be hashed again before the Phase 001
commit, review, merge, and tag.

## First unfinished work

`P001-TASK-001` is complete for branch, tag, archive, manifest, and protected
state intake. The next task is `P001-TASK-002`, shared shark locomotion and the
DEC-006 calibrated route oracle. It must preserve horizontal speed and yaw while
proving smooth forward propulsion, body pitch, vertical entry and exit, direct
above and below routes, non-orbiting arrival, and recovery from an unreachable or
reversed target.
