# Phase 003 task 001 baseline

Date: September 10, 2026

This entry report refreshes the Phase 003 baseline from the merged and tagged
Phase 002 result. No product source or generated resource was changed by this
task.

## Bound repository and inputs

* Branch: `envy/0.24-phase-003`.
* Base: `origin/envy/0.24` at merge commit
  `1ff812cba6863eec9580c7255927aee0511fc8a2`.
* Upstream tag: signed `bfs-0.24-phase-002`, targeting that merge.
* Game: Minecraft 1.20.1, Forge 47.2.0, Java 17.
* 0.24 archive SHA-256:
  `25290e8d019339aff68c8ba168bd750d5c445aa915720d249aeed5700c9f3274`.
* 0.24 archive SHA-512:
  `cc18be0e963823e8cd2c220a8ee3a565a626311c8c4e66a0c0674d58fa93f61d2600558a539f575cbfdec02ee113ccbda5ba8a2a57c3185bc9a3ad6baf42f63e`.
* Retained 0.23 archive SHA-256:
  `1dcfd0db544184bffd467255ba294b57f0a1376b06023f10034f380b9e5d0eaa`.
* Retained 0.23 archive SHA-512:
  `09a8bb217d25cb146f6b63c38c2b269c0c7b8d862459cc0916f4d93b12da81001a6bd308d745ef5b86c0cb3816bc9e7ae222b5b7aaae96eb4b42ba26e7a11527`.

The 0.24 archive contains the seven expected 16 by 16 PNG entries under
`Advancement Icons Remaster/`. Each extracted source hash matches its current
destination hash. The archive was extracted into a disposable directory for
the comparison and the directory was removed after the hashes were recorded.

## Advancement and copy inventory

The generated tree currently contains 49 advancement JSON files and 98
English advancement translation keys. The generated graph has no
`shark_whisperer` node or language key. `marine_biologist` and `apex_of_apex`
both directly parent to `bensfintasticsharks:sharks_galore`, and the parent
exists. The graph has 22 criteria for `marine_curious` and 22 criteria for
`marine_biologist`; `sharks_galore` has eight shark criteria. The full node
list is in the machine manifest, with the required parent edges and key
criterion counts recorded for this entry baseline.

The provider sources are:

* `BensFintasticSharksAdvancements.java`, SHA-256
  `b72c584682e07fde3f86cf9d7afc64ea3327338ae3cc6c5e11ba0d57fff99c89`.
* `ModLangProvider.java`, SHA-256
  `e154edd5da47913d984f0926b599ad09c54f32f307d27e7be6ee916095463b81`.
* Generated `en_us.json`, SHA-256
  `1ab4da32d7267324c6f73bf93a9cbfa6fe410e28186164bfdc8d6b06bbb12238`.

The existing deterministic audit passed with `./gradlew :forge:test`.
That inherited audit proves the supplied icon hashes, supplied titles, captain
description, terminal punctuation, retired-node absence, required child edges,
parent existence, and cycle absence. It does not close the Phase 003 gates for
the complete expected copy map, model resolution, semantic child snapshots,
fresh progression, old-profile compatibility, or seven-display visual review.

## Icon and model baseline

All seven destination textures are 16 by 16 RGBA PNGs and match the archive
hashes. Six targets already have generated flat item models with one
`minecraft:item/generated` parent and one matching `layer0`. The Harbor Seal
target currently has only the hand-authored 3D model at
`common/src/main/resources/assets/bensfintasticsharks/models/item/harbor_seal_block.json`;
the datagen provider does not generate its flat model. This is the known
`DEF-024-007` entry defect and is assigned to P003-TASK-002. No model was
changed in this baseline task.

## Profile and runtime boundary

No persistent or player-owned profile was inspected or modified. Disposable
fresh-profile completion and copied old-profile compatibility fixtures are
reserved for P003-TASK-007. Existing Phase 001 and Phase 002 reports remain
historical inputs only; they are not relabeled as fresh Phase 003 evidence.

The test command completed on the headless build host with exit code zero. Its
sanitized log was hashed as SHA-256
`9471287040a53d62ef8fe70d1cf2bc95e353e195e73056b7c44e47cfa1aeb3c2` and SHA-512
`26226b50d5b80cf07b3d61d4a282fe51cdb3122adef0932dce7e35bb8ea10900b6a2cff052315de2f3395bf59646a11aa173d501e5f53ed108cb1f3331f9a0bc`.
The log was retained outside Git only until this entry was recorded and then
removed during task cleanup.

## Entry disposition

P003-TASK-001 is complete as a protected baseline and traceability report.
The phase remains open. P003-TASK-002 is the next action and must resolve the
Harbor Seal effective-model conflict before any icon visual or interim artifact
gate can close. No owner plan, goal, phase sequence, archive, advancement
identifier, or supplied pixel was changed.
