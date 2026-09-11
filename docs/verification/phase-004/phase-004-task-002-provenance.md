# Phase 004 task 002 provenance evidence

Date: September 11, 2026

The Phase 004 entry audit reconciled the supplied 0.23 and 0.24 content with
the existing algae implementation. No source asset was rewritten. The live
amended plan remains authoritative and the entry bindings are recorded in
`phase-004-task-001-entry.md` and `phase-004-entry-manifest.json`.

## Provenance and identity

The 0.24 content archive SHA-256 is
`25290e8d019339aff68c8ba168bd750d5c445aa915720d249aeed5700c9f3274`.
The 0.23 content archive SHA-256 is
`1dcfd0db544184bffd467255ba294b57f0a1376b06023f10034f380b9e5d0eaa`.
The loose source ledger contains all 22 expected algae rows. The block texture
is 16 by 16. The green strip is 16 by 160 and the red strip is 16 by 144.
The destination block and strip hashes match the source hashes exactly.

The green animation metadata contains frames 0 through 9 with frametime 4 and
interpolate false. The red metadata contains frames 0 through 8 with the same
timing and interpolation setting. The segmented frame hashes and all
destination hashes are recorded in the entry manifest.

The common registration contains exactly these block and item pairs:

* `bensfintasticsharks:algae_block`
* `bensfintasticsharks:large_green_algae`
* `bensfintasticsharks:large_red_algae`

Generated blockstates, block models, item models, translations, tags, loot,
configured features, placed features, and biome modifiers use those exact
identifiers. A source and generated resource scan found no public `algea`
path or identifier. Creative enumeration keeps all three items visible as
ordinary entries. Client cutout registration is isolated to the Forge client
setup.

## Disposition

P004-TASK-002 is complete. The implementation matched the provenance and
identity contract except for the survival loot defect handled by P004-TASK-003.
The owner `Content/` directory remains untracked and untouched.

