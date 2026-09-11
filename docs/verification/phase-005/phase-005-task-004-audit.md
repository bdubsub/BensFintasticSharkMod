# Phase 005 task 004 release blocking audit

## Scope

This audit covers the integrated Phase 004 baseline after the completed
Phase 005 entry, traceability, and archive or transformation provenance gates.
The phase branch is `envy/0.24-phase-005`. Its verified parent is the signed
Phase 004 merge `a919528d5e65e561707de700168babcfcde086a1`, tagged
`bfs-0.24-phase-004`.

The authoritative release target is `1.0-rc.1`, displayed as Release
Candidate 1.0. Current source metadata still says `0.24`; changing that is
the later P005-TASK-006 action and is intentionally not folded into this
audit.

## Review coverage

| surface | result | evidence |
| --- | --- | --- |
| initialization and side boundaries | pass | Forge bootstrap reaches client registration only through `FMLEnvironment.dist.isClient()`. Client renderers and client diagnostics remain in `ModClientForge`. No client-only reference was found in `common/src`. |
| registries and serialized identifiers | pass | Existing registry holders, resource identifiers, GeckoLib clip names, and documented bindings remain unchanged in the intake commits. |
| movement, combat, animation, and entity state | pass for inherited baseline | Phase 004 supplied the required movement, animation, combat, and GameTest evidence. Final candidate rechecks remain open in the traceability matrix. |
| fishing and vanilla replacement | pass for inherited baseline | Fishing delivery uses one supported fish selection, the `fish_entities` setting, and bounded server-side settlement. Cod and Salmon replacement is category-validated and limited to documented sources. |
| configuration and reload | pass | Config values are Forge-owned and bounded by their specs. `/bfs reload` is behind the root permission gate. |
| commands and permissions | pass | `/bfs` and every child command require permission level 2. Debug category, duration, target, record, queue, session, and directory limits are bounded. |
| diagnostics and filesystem paths | pass | Server and client diagnostics write below `logs/bfs-debug`, normalize and verify containment, enforce 256 MiB directory and 32 MiB session budgets, cap records at 16 KiB, and stop on queue overflow. Player identifiers are pseudonymized per capture. |
| networking and logical authority | pass | No custom packet channel or unvalidated client mutation was found. Entity, advancement, population, fishing, and configuration decisions remain server authoritative. Existing vanilla packets are sent only from server-side entity paths. |
| persistence and reload boundaries | pass for inherited baseline | Phase 004 server and restart evidence passed. No Phase 005 entry change alters persistent schemas or reload behavior. |
| data generation and resources | pass for inherited baseline | Phase 004 data generation and resource checks passed. The final candidate must repeat byte-stable generation after metadata changes. |
| dependencies and packaging | pending planned action | Forge 47.2.0, Minecraft 1.20.1, Java 17, GeckoLib 4.4.7, and SmartBrainLib 1.14.2 are pinned. Final metadata, jar, checksums, SBOM, and attestations belong to P005-TASK-006 and P005-TASK-012. |

## Security and hygiene checks

The following read-only checks were run in the phase worktree.

* Secret-pattern scan over `common` and `forge` source returned no matches.
* Client-only reference scan found references only in Forge client, renderer,
  and data-generation packages. No client reference was found in `common`.
* Filesystem API scan found only bounded server and client diagnostic writers
  and GameTest evidence readers. No process execution, network URL fetch,
  object deserialization, or arbitrary path input was found.
* Command registration review confirmed permission level 2 on the `/bfs` root,
  including debug and configuration commands.
* `git diff --check` passed.
* The only worktree modification outside the committed evidence files is the
  protected pre-existing `build.gradle` line-ending change. It was not staged.

Historical evidence documents contain absolute `/tmp` and worktree paths as
provenance. They are retained documentation references, not runtime path
inputs or newly created release artifacts.

## Findings

No release-blocking security or major implementation defect was found in the
reviewed integrated baseline. There is no repair to apply under P005-TASK-005
from this audit.

The following remain open by design and are not audit defects.

1. Source and jar version metadata must change from `0.24` to `1.0-rc.1`.
2. The final exact candidate needs artifact, checksum, SBOM, and attestation
   evidence.
3. Dedicated-server, multiplayer, fresh-profile, fixed-seed, and laptop
   visual acceptance must rerun against that exact candidate.
4. The traceability matrix must bind every row to final evidence and the owner
   must approve the Release Candidate 1.0 endpoint.

These are the ordered P005-TASK-006 through P005-TASK-014 gates. The audit
exits cleanly and authorizes continuation to version metadata.

