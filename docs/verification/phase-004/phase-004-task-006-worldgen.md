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
010. The holder and feature graph audit therefore passes. The final malformed
reference control and fixed-seed equality evidence are recorded below.

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
runtime as historical provenance.

## Source equivalent candidate malformed reference control

On September 11, 2026, the exact final candidate with SHA 256
`ac4ce5e54e0282ce0e0f970e938778f121dc6595d1974f20c8ef8236cbd339df` was
started in the disposable production Forge runtime
`/tmp/bfsm-p004-malformed-final-uxSv0U` with Java 17.0.19, Minecraft 1.20.1,
Forge 47.2.0, both required dependencies, and `eula=true`. A disposable world
datapack replaced the `algae_block` placed feature with the unbound configured
feature holder `bensfintasticsharks:missing_configured_feature`.

The server rejected the datapack before readiness with
`Unbound values in registry ResourceKey[minecraft:root / minecraft:worldgen/configured_feature]: [bensfintasticsharks:missing_configured_feature]`
and `Failed to load datapacks, can't proceed with server load`. The final
runtime log SHA 256 is
`e48266aaecd711b6ac7b2662dac119e660353649dfa0c4d80dbb0edde3e515fb`.
This closes the malformed-reference behavior control on the exact final
candidate.

## Final exact artifact malformed reference control

The final packaged Forge jar with SHA 256
`a22f84941a0c8457f6daab3000d4278a1c5ea06c3ab126eb32df73a91c9862bf` was
started in `/tmp/bfsm-p004-malformed-a22-zQ7EhE` with Java 17.0.19,
Minecraft 1.20.1, Forge 47.2.0, both required dependencies, and `eula=true`.
The same disposable datapack replaced the `algae_block` placed feature with
the unbound configured feature holder
`bensfintasticsharks:missing_configured_feature`.

The server rejected the datapack before readiness with
`Unbound values in registry ResourceKey[minecraft:root / minecraft:worldgen/configured_feature]: [bensfintasticsharks:missing_configured_feature]`
and `Failed to load datapacks, can't proceed with server load`. The exact
final runtime log SHA 256 is
`1c8489ecf50676c5846d59782c3212402b6f9993c09519b6235c8e39f5b9b906`.
This closes the malformed-reference control for the exact final artifact.
