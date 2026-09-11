# Phase 004 task 006 world generation evidence

Date: September 10, 2026

The packaged candidate contains one configured and one placed feature for each
required form, all three using the `vegetal_decoration` add features step and
the shared `bensfintasticsharks:algae_spawns` biome tag. The tag contains the
nine supported vanilla ocean families. Each placed feature uses chance `3`, an
in square spread, and a uniform absolute height range from `20` through `62`.
The custom feature predicate keeps placement in source water with valid
seagrass support and uses bounded local search.

The three Forge biome modifier paths are present in the exact candidate:

* `algae_block_feature.json`
* `large_green_algae_feature.json`
* `large_red_algae_feature.json`

The generated JSON and the second unchanged Data run agree byte for byte. The
natural seed probe in `phase-004-task-009-fixed-seed.md` confirms all three
forms are reachable in ordinary fresh chunk decoration and that the finite
per-chunk cap is respected.

The exact current candidate passed data-pack disable, bounded replacement, and
existing-world preservation in the packaged runtime controls recorded in task
010. The holder and feature graph audit therefore passes. A malformed-reference
failure control is still not recorded separately, and the fixed-seed task 009
retains the strict full-manifest equality row as open.
