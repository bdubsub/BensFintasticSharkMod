# Phase 005 task 003 release blocking audit

## Scope

This audit covers the integrated Phase 004 baseline at commit `0307d37` on
`envy/0.24-phase-005`, whose product parent is the signed Phase 004 merge
`a919528d5e65e561707de700168babcfcde086a1` and tag
`bfs-0.24-phase-004`. The audit is an intake gate for the Release Candidate
1.0 work. It does not close final candidate, packaging, or owner visual gates.

The authoritative release target is `1.0-rc.1`, displayed as Release
Candidate 1.0. The current source metadata still says `0.24`; changing that
metadata is the later P005-TASK-005 action and is intentionally not folded
into this audit.

## Review coverage

The following release surfaces were reviewed against the source and the live
Phase 005 plan.

| surface | result | evidence |
| --- | --- | --- |
| initialization and side boundaries | pass | Forge bootstrap registers server listeners first and reaches client registration only through `FMLEnvironment.dist.isClient()`. Client renderers and client debug registration remain in `ModClientForge`. No client-only references were found in `common/src`. |
| registries and serialized identifiers | pass | Existing registry holders, resource identifiers, GeckoLib clip names, and documented bindings remain unchanged in this intake commit. |
| movement, combat, animation, and entity state | pass for inherited baseline | Phase 004 supplied the required movement, animation, combat, and GameTest evidence. No new product code was changed by Phase 005 entry or traceability work. Remaining final-candidate acceptance is tracked by the traceability matrix, not treated as an audit pass. |
| fishing and vanilla replacement | pass for inherited baseline | Fishing delivery uses one supported fish selection, the `fish_entities` configuration, and bounded server-side settlement. Vanilla cod and salmon replacement is category-validated and limited to the documented spawn sources. |
| configuration and reload | pass | Config values are Forge-owned, bounded by their specs, and the `/bfs reload` command is behind the root permission level. Default replacement and live-fish behavior remain the planned values. |
| commands and permissions | pass | `/bfs` and all child commands require permission level 2. Debug categories, duration, target count, record size, queue size, session size, and directory size are bounded before capture. |
| diagnostics and filesystem paths | pass | Server and client diagnostics write below the game directory `logs/bfs-debug`, normalize and verify the path, enforce a 256 MiB directory budget and 32 MiB session budget, cap records at 16 KiB, and stop on queue overflow. Player identifiers are pseudonymized per capture. |
| networking and logical authority | pass | No custom packet channel or unvalidated client-to-server mutation was found. Entity, advancement, population, fishing, and configuration decisions remain server authoritative. Existing vanilla passenger and game-event packets are sent only from server-side entity paths. |
| persistence and reload boundaries | pass for inherited baseline | Phase 004 server and restart evidence passed. No Phase 005 entry change alters persistent schemas or reload behavior. |
| data generation and resources | pass for inherited baseline | Phase 004 data generation and resource checks passed. The final candidate must repeat byte-stable generation and resource validation after version metadata changes. |
| dependency and packaging metadata | pending planned action | Forge 47.2.0, Minecraft 1.20.1, Java 17, GeckoLib 4.4.7, and SmartBrainLib 1.14.2 are pinned. Final version metadata, jar contents, checksums, SBOM, and attestation evidence belong to P005-TASK-005 and P005-TASK-011. |

## Security and hygiene checks

The following read-only checks were run in the phase worktree.

* Secret-pattern scan over `common` and `forge` source returned no matches.
* Client-only reference scan found references only in Forge client, renderer,
  and data-generation packages. No client reference was found in `common`.
* Filesystem API scan found only the bounded server and client diagnostic
  writers and the GameTest evidence readers. No process execution, network
  URL fetch, object deserialization, or arbitrary path input was found.
* Command registration review confirmed the permission level 2 gate on the
  `/bfs` root, including debug and configuration commands.
* `git diff --check` passed.
* The only worktree modification outside the two committed evidence files is
  the protected pre-existing `build.gradle` line-ending change. It is not part
  of this phase and was not staged.

Historical evidence documents contain absolute `/tmp` and worktree paths as
provenance. They are retained documentation references, not runtime path
inputs or newly created release artifacts.

## Findings

No release-blocking security or major implementation defect was found in the
reviewed integrated baseline. There is no repair to apply under P005-TASK-004
from this audit.

The following are intentionally still open and are not defects in this audit:

1. Source and jar version metadata must be changed from `0.24` to `1.0-rc.1`.
2. Final exact-candidate artifact, checksum, SBOM, and attestation evidence
   must be produced.
3. Final dedicated-server, multiplayer, fresh-profile, fixed-seed, and
   laptop visual acceptance gates must be rerun against that exact candidate.
4. The complete traceability matrix must be bound to final evidence and the
   owner must approve the Release Candidate 1.0 endpoint.

These are the ordered P005-TASK-005 through P005-TASK-014 gates. The audit
therefore exits cleanly and authorizes continuation to the version metadata
task.

