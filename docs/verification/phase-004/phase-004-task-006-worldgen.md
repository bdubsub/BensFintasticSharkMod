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

## Malformed reference failure control

On September 11, 2026, a fresh disposable production Forge 1.20.1 server was
started with Java 17.0.19, the current source-equivalent Forge jar, and a
world datapack that overrides the `algae_block` placed feature with the
unbound configured-feature holder
`bensfintasticsharks:missing_configured_feature`. The runtime was
`/tmp/bfsm-p004-malformed-ggv8Lk`, the server port was `25903`, and the jar
SHA 256 was
`ad2338017b3c06f7b45edc2c9474fbdab09954c0a4a921c98ae7399a6ef38381`.

The server did not reach ready state. It logged the datapack discovery,
reported `Unbound values in registry ... configured_feature`, and terminated
with `Failed to load datapacks, can't proceed with server load`. The complete
latest log SHA 256 is
`50d06bce9f72de4928305e7201a9b7022ec6d85ba265aeef9b923d407ea6d652`.
This closes the malformed-reference behavior control for the source-equivalent
runtime. The final Phase 004 packet still requires rebinding the control to the
exact final jar after the last artifact build.
