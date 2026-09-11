# 0.24 Asset Ledger

This ledger records the supplied source material used by the 0.24 release work. The archive files remain owner supplied working material under `Content/` and are intentionally not copied into the distributable mod.

## Archive records

| Source | SHA 256 | Safety review |
| --- | --- | --- |
| `Content/BFS 0.23 Content.zip` | `1dcfd0db544184bffd467255ba294b57f0a1376b06023f10034f380b9e5d0eaa` | All entries stay within the archive root, contain no links, and pass archive integrity checks. |
| `Content/BFS 0.24 Content.zip` | `25290e8d019339aff68c8ba168bd750d5c445aa915720d249aeed5700c9f3274` | All entries stay within the archive root, contain no links, and pass archive integrity checks. |

The 0.23 archive supplies the Atlantic Cod and Atlantic Salmon geometry, textures, animations, spawn egg sprites, food sprites, Oceanic Whitetip animation set, fishing note, and four fish advancement roles. The repository keeps the intentional `geometry.atlantic_cod` correction required by the existing model loader.

The 0.24 archive supplies seven advancement icons. They are copied byte for byte into the existing item texture paths and remain 16 by 16 pixels.

Archive member safety and the complete machine-readable member manifest are recorded in `phase-000-manifest.json`. The file members, crc values, sizes, and hashes are summarized below. Directory entries have zero size and the empty file hash.

| Archive member | CRC 32 | Size | SHA 256 |
| --- | --- | ---: | --- |
| `BFS 0.23 Content/Atlantic Cod/atlantic_cod.animation.json` | `b8a9c28a` | 10514 | `efd3e6231ca130f5b28d0e87225960e7877b85cc1fb0407e6d7404cc3c4a04ad` |
| `BFS 0.23 Content/Atlantic Cod/atlantic_cod.geo.json` | `fdb3299b` | 13789 | `ef54d1552d53d530cf1b08d2566cc1fc4544b4022df750adc54038af2a5aa8cc` |
| `BFS 0.23 Content/Atlantic Cod/atlantic_cod.png` | `287cc5e7` | 3063 | `ed3987ecb2a1b21ebfdc6c63ce65e8431b4cd717ee1bf3567711ce9183b25ad` |
| `BFS 0.23 Content/Atlatnic Salmon/atlantic_salmon.animation.json` | `ab62451c` | 9922 | `19a5463fb83ce1fd9329125365f733a0ff715abdb5f06c6de5e7f1ccfff18648` |
| `BFS 0.23 Content/Atlatnic Salmon/atlantic_salmon.geo.json` | `72b63a6f` | 27513 | `e9af49fbf1b954db868d1b93de5dae7c14645081512534dba871a13a59e98ff6` |
| `BFS 0.23 Content/Atlatnic Salmon/atlantic_salmon.png` | `017db6a0` | 4790 | `0719e6aec5c2936480856ba8ad7d382b2f7a62697faf0131622ba8adc8f4849b` |
| `BFS 0.23 Content/BFS 0.23 Notes for Coder.txt` | `9a6fc6ef` | 1337 | `9a8042c18739b65e00b8c81c1698d7f1ab483fb49d4917c267c26ffc71ebe518` |
| `BFS 0.23 Content/New Items/Atlantic Cod Spawn Egg.png` | `416e490e` | 411 | `f34698d4115fe66dcd77b5c739bf8ad879959d1670d875c4de81b174653a32d5` |
| `BFS 0.23 Content/New Items/Atlantic Salmon Spawn Egg.png` | `6756069e` | 420 | `9999a81aa0c294aefa8a23df525a6d079b2c16514c94b6020fcc0fef43cb1542` |
| `BFS 0.23 Content/New Items/Cooked Atlantic Cod.png` | `1d1f6845` | 479 | `51769694f3cd9ac8565c327158f464532c11c6541c8374549b998dc1adf8a6c1` |
| `BFS 0.23 Content/New Items/Cooked Atlantic Salmon.png` | `b3574356` | 460 | `ff82528016ad8bb8a34454fc935b324b22f7552b4e4311d538132cc39189e2f1` |
| `BFS 0.23 Content/New Items/Raw Atlantic Cod.png` | `623fdea3` | 440 | `15bd270d1d10ee9fe23d84a57b06c5f7d14c5da6accef10802238d61cedf2326` |
| `BFS 0.23 Content/New Items/Raw Atlantic Salmon.png` | `fd11faec` | 464 | `85f66a2ef9ac43a50f1872aefdab3bf39f0b8f377d5b803b13674ac96bd75994` |
| `BFS 0.23 Content/Oceanic Whitetip Animations New/oceanicwhitetipshark.animation.json` | `cb15a6e1` | 26865 | `dece327b4655f89d1f79ae463f05a6a3d3683cc2f7eb9550205e8b199d700e86` |
| `Advancement Icons Remaster/Awkward Advancement Icon 16x.png` | `b93beb17` | 560 | `e5e531f62b458fea3ab50b1e61abc4504c75453fa7cd06c8640aabf794b0abd1` |
| `Advancement Icons Remaster/Its A Shiny Advancement Icon 16x.png` | `f191897b` | 491 | `e0638a22ee40480f03a7e4361ff324e4a8d9b1cb93590a2ec695b2830f79ab76` |
| `Advancement Icons Remaster/Mommy Shark Advancement 16x.png` | `41b62a57` | 536 | `58fb46c5267ea9b6e027fdaf4532ad2d76de7dcf75aa046367f3720f18c00cde` |
| `Advancement Icons Remaster/Sharks Galore Advancement Icon 16x.png` | `a44cafd1` | 293 | `11f66a72567bb8ae16aa2022ca21778d43eb0d0fcbb531b21d34e03fa4e020f6` |
| `Advancement Icons Remaster/Sleeping With The Fishes Advancement Icon 16x.png` | `3df200e8` | 684 | `6c01085423275d602fbad91924a8772249c1a8dbeee749f5e1e9907df0995977` |
| `Advancement Icons Remaster/Specimen8 Advancement Icon 16x.png` | `eb0e3212` | 576 | `88f6dc345a7c399034142cf2bebc54fdd488e41b9f36f19d470806d393c1fc65` |
| `Advancement Icons Remaster/Thunder Bringer Advancement 16x.png` | `a4b83c36` | 477 | `4f54793625dc71ab456ca58de55b6bfe015f586c81930d11b72d5a3d6942595d` |
| `bfscontent0.24.txt` | `51f18328` | 379 | `831cf974a07a7737238a6d1fdc72267d83667b24d1723481dbaa74d479d81de1` |

| Destination texture | SHA 256 |
| --- | --- |
| `common/src/main/resources/assets/bensfintasticsharks/textures/item/albino.png` | `e0638a22ee40480f03a7e4361ff324e4a8d9b1cb93590a2ec695b2830f79ab76` |
| `common/src/main/resources/assets/bensfintasticsharks/textures/item/harbor_seal_block.png` | `e5e531f62b458fea3ab50b1e61abc4504c75453fa7cd06c8640aabf794b0abd1` |
| `common/src/main/resources/assets/bensfintasticsharks/textures/item/mommy_shark.png` | `58fb46c5267ea9b6e027fdaf4532ad2d76de7dcf75aa046367f3720f18c00cde` |
| `common/src/main/resources/assets/bensfintasticsharks/textures/item/sharks_galore.png` | `11f66a72567bb8ae16aa2022ca21778d43eb0d0fcbb531b21d34e03fa4e020f6` |
| `common/src/main/resources/assets/bensfintasticsharks/textures/item/sleeping_with_the_fishes.png` | `6c01085423275d602fbad91924a8772249c1a8dbeee749f5e1e9907df0995977` |
| `common/src/main/resources/assets/bensfintasticsharks/textures/item/specimen_8.png` | `88f6dc345a7c399034142cf2bebc54fdd488e41b9f36f19d470806d393c1fc65` |
| `common/src/main/resources/assets/bensfintasticsharks/textures/item/zippy_pixel_art.png` | `4f54793625dc71ab456ca58de55b6bfe015f586c81930d11b72d5a3d6942595d` |

## Algae records

The loose algae drop contains 22 PNG files. `algea_block.png` is the 16 by 16 source for `bensfintasticsharks:algae_block`. `biggreenalgea_strip.png` is a 16 by 160 strip containing ten ordered frames for `bensfintasticsharks:large_green_algae`. `bigredalgea_strip.png` is a 16 by 144 strip containing nine ordered frames for `bensfintasticsharks:large_red_algae`. The numbered PNG files are source frame evidence and are not separate public blocks.

The destination texture hashes are:

| Destination texture | Source member | SHA 256 |
| --- | --- | --- |
| `textures/block/algae_block.png` | `algea_block.png` | `562c9b1a75c2f9ed912c22b0f424651792a3df02ea96bf72b8b5cb6a59ba7392` |
| `textures/block/large_green_algae.png` | `biggreenalgea_strip.png` | `6818750169255d1fd99f33f3363c96157ef5355f2fe8663ba78f3f344ef7f0ab` |
| `textures/block/large_red_algae.png` | `bigredalgea_strip.png` | `ad9c3abbd801de619c1d8e14b53b3ec0e74bb21c47da1e980bca553210f8110c` |

Animated metadata keeps the supplied frame order with frame time 4. The green strip uses frames 0 through 9 and the red strip uses frames 0 through 8.

The loose frame files are evidence fingerprints and are not copied as separate public resources.

| Loose file group | Dimensions | SHA 256 values in numeric order |
| --- | --- | --- |
| `bigredalgea_1.png` through `bigredalgea_3.png` | 16 by 16 | `818238e574a12e13050fb1b3897d5901a332be422c8779ce0095eded6f99d204` |
| `bigredalgea_4.png` through `bigredalgea_6.png` | 16 by 16 | `49d1246b44d22bc572f140214cc2ffe05cb3fffd7f4d078dcd97318521d0903d` |
| `bigredalgea_7.png` through `bigredalgea_9.png` | 16 by 16 | `bc8431c1432f863de7635d03b3c70dd66dda30d80dd44379076d24d162379a77` |
| `greenbigalgea_1.png` | 16 by 16 | `852cc1899f2756b241eaf7bad1aea675e0a00ab9b60de9a867da8496c43c1096` |
| `greenbigalgea_2.png` through `greenbigalgea_3.png` | 16 by 16 | `0fe9c99166e5f52e2ec49ab435ed9eb001a7c3b2db72c9553f62d273e8436ccf` |
| `greenbigalgea_4.png` through `greenbigalgea_6.png` | 16 by 16 | `76d85b975460493cc142f629106c56e6dafe0a0888095d124da148fd88a2e177` |
| `greenbigalgea_7.png` through `greenbigalgea_8.png` | 16 by 16 | `93450e1856167cddfe967f71a3ae240b1074adf983508bfa7ae1be62be2a32fc` |
| `greenbigalgea_9.png` through `greenbigalgea_10.png` | 16 by 16 | `0900b4ade7560b4e9e94caa3895f2b076a732801d4765df0b55effbecb316525` |

The complete per-file list, including `algea_block.png` and both strips, is in `phase-000-manifest.json`. Repeated frame hashes are intentional authored holds and do not indicate duplicate or missing frames.

## Rechecking the ledger

Run the following commands from the repository root when replacing source material:

```bash
sha256sum "Content/BFS 0.23 Content.zip" "Content/BFS 0.24 Content.zip"
unzip -t "Content/BFS 0.23 Content.zip"
unzip -t "Content/BFS 0.24 Content.zip"
./gradlew :forge:Data :forge:test
```

The release contract test also checks the destination hashes, dimensions, advancement references, animation motion, and generated algae resources.
