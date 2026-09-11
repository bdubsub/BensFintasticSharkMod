# Phase 005 task 003 archive and transformation provenance

Date: September 11, 2026

This task rechecks the owner supplied archives, permanent algae inputs, and
DEC-007 animation transformation chain from protected source material through
the current repository destinations. The source directory is the original
owner path `/mnt/hermes/projects/BFSMOD/Content`. It was read only and was not
copied into the distributable artifact.

## Archive identity and safety

| archive | SHA-256 | SHA-512 | members | result |
| --- | --- | --- | ---: | --- |
| `Content/BFS 0.23 Content.zip` | `1dcfd0db544184bffd467255ba294b57f0a1376b06023f10034f380b9e5d0eaa` | `09a8bb217d25cb146f6b63c38c2b269c0c7b8d862459cc0916f4d93b12da81001a6bd308d745ef5b86c0cb3816bc9e7ae222b5b7aaae96eb4b42ba26e7a11527` | 19 | `unzip -tqq` passed and all members stayed below the archive root |
| `Content/BFS 0.24 Content.zip` | `25290e8d019339aff68c8ba168bd750d5c445aa915720d249aeed5700c9f3274` | `cc18be0e963823e8cd2c220a8ee3a565a626311c8c4e66a0c0674d58fa93f61d2600558a539f575cbfdec02ee113ccbda5ba8a2a57c3185bc9a3ad6baf42f63e` | 9 | `unzip -tqq` passed and all members stayed below the archive root |

The archive member names were inspected for traversal components before any
member was read. No links, unsafe paths, changed archive hash, or unexpected
member count was found.

## Permanent algae chain

The three public textures remain byte identical to the protected loose inputs.
The strips preserve their authored dimensions and frame order.

| source | destination | dimensions | SHA-256 | byte comparison |
| --- | --- | --- | --- | --- |
| `Content/Algae Textures/algea_block.png` | `common/src/main/resources/assets/bensfintasticsharks/textures/block/algae_block.png` | 16 by 16 | `562c9b1a75c2f9ed912c22b0f424651792a3df02ea96bf72b8b5cb6a59ba7392` | exact |
| `Content/Algae Textures/biggreenalgea_strip.png` | `common/src/main/resources/assets/bensfintasticsharks/textures/block/large_green_algae.png` | 16 by 160 | `6818750169255d1fd99f33f3363c96157ef5355f2fe8663ba78f3f344ef7f0ab` | exact |
| `Content/Algae Textures/bigredalgea_strip.png` | `common/src/main/resources/assets/bensfintasticsharks/textures/block/large_red_algae.png` | 16 by 144 | `ad9c3abbd801de619c1d8e14b53b3ec0e74bb21c47da1e980bca553210f8110c` | exact |

The numbered loose frame files remain source evidence only. Existing
animation metadata selects green frames 0 through 9 and red frames 0 through
8 with frame time 4 and no interpolation. No alternate public `algea` path or
identifier is present.

## Remastered icon chain

All seven 0.24 PNG members were read directly from the protected archive and
compared to their existing destination textures. Every source and destination
is a 16 by 16 PNG and every comparison is exact.

| archive member | destination | SHA-256 |
| --- | --- | --- |
| `Awkward Advancement Icon 16x.png` | `textures/item/harbor_seal_block.png` | `e5e531f62b458fea3ab50b1e61abc4504c75453fa7cd06c8640aabf794b0abd1` |
| `Its A Shiny Advancement Icon 16x.png` | `textures/item/albino.png` | `e0638a22ee40480f03a7e4361ff324e4a8d9b1cb93590a2ec695b2830f79ab76` |
| `Mommy Shark Advancement 16x.png` | `textures/item/mommy_shark.png` | `58fb46c5267ea9b6e027fdaf4532ad2d76de7dcf75aa046367f3720f18c00cde` |
| `Sharks Galore Advancement Icon 16x.png` | `textures/item/sharks_galore.png` | `11f66a72567bb8ae16aa2022ca21778d43eb0d0fcbb531b21d34e03fa4e020f6` |
| `Sleeping With The Fishes Advancement Icon 16x.png` | `textures/item/sleeping_with_the_fishes.png` | `6c01085423275d602fbad91924a8772249c1a8dbeee749f5e1e9907df0995977` |
| `Specimen8 Advancement Icon 16x.png` | `textures/item/specimen_8.png` | `88f6dc345a7c399034142cf2bebc54fdd488e41b9f36f19d470806d393c1fc65` |
| `Thunder Bringer Advancement 16x.png` | `textures/item/zippy_pixel_art.png` | `4f54793625dc71ab456ca58de55b6bfe015f586c81930d11b72d5a3d6942595d` |

The hidden-item model and advancement display mappings remain the canonical
Phase 003 mappings. No source archive member is packaged as an extra support
file.

## DEC-007 authored animation chain

The 0.23 authored Cod, Salmon, and Oceanic Whitetip animation members remain
the same hashes recorded in the asset ledger. The current transformation tool
and its tests are unchanged at:

| file | SHA-256 |
| --- | --- |
| `tools/bake_molang_animations.py` | `9f0dc7803b47ffa2df897b827bbea75ee4df162511a632aca3af72da6c3f73d5` |
| `tools/test_bake_molang_animations.py` | `8858fd2e5404111a5ee8aa37f6fc8f3ebe67c2ce6792acb943df22c7997fe4e1` |

The fail closed transformer test suite passed all 5 tests. Prior controlled
bake evidence records 122 transformed channels, stable ascending keyframes,
preserved loops, five point sample equivalence within `0.0001`, and a no-op
second invocation. Current destination hashes remain:

| destination | SHA-256 |
| --- | --- |
| `animations/entity/atlantic_cod.animation.json` | `ffa889ee5418b1439e4b1f6865404b02c4adfd5a6a03cc7e9df6f4b363c125ab` |
| `animations/entity/atlantic_salmon.animation.json` | `f7670f1eb24182309c536e032a412da69f6ddea57748c62f93dae8d1e4d6faed` |
| `animations/entity/oceanic_whitetip_shark.animation.json` | `2f7d7399b4c165c5def46df0d3fb1027c54b49a13e1e9398dc4e4581860e12ee` |
| `geo/entity/oceanic_whitetip_shark.geo.json` | `c8cc1fd88ad8df8ce528c4cbcf63a8faff2ebd88e166cb124051029048d4017e` |

Cod geometry normalization and the Oceanic `leftFin` to `Fin` and `rightFin`
to `Fin2` bindings are the documented compatibility transformations. No
undocumented animation, geometry, texture, bone, clip, or pixel change was
found in this intake.

## Disposition and cleanup

P005-TASK-003 is complete. The archive, algae, icon, and animation chains are
traceable and unchanged. Commands used only direct reads or streamed archive
members. The temporary path validation output was removed after its final
consumer. No extraction directory, archive copy, generated cache, or owner
source was left behind. This evidence is ready for the P005-TASK-004 defect
ledger.

