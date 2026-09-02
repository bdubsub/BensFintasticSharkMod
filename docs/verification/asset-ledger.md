# 0.24 Asset Ledger

This ledger records the supplied source material used by the 0.24 release work. The archive files remain owner supplied working material under `Content/` and are intentionally not copied into the distributable mod.

## Archive records

| Source | SHA 256 | Safety review |
| --- | --- | --- |
| `Content/BFS 0.23 Content.zip` | `1dcfd0db544184bffd467255ba294b57f0a1376b06023f10034f380b9e5d0eaa` | All entries stay within the archive root, contain no links, and pass archive integrity checks. |
| `Content/BFS 0.24 Content.zip` | `25290e8d019339aff68c8ba168bd750d5c445aa915720d249aeed5700c9f3274` | All entries stay within the archive root, contain no links, and pass archive integrity checks. |

The 0.23 archive supplies the Atlantic Cod and Atlantic Salmon geometry, textures, animations, spawn egg sprites, food sprites, Oceanic Whitetip animation set, fishing note, and four fish advancement roles. The repository keeps the intentional `geometry.atlantic_cod` correction required by the existing model loader.

The 0.24 archive supplies seven advancement icons. They are copied byte for byte into the existing item texture paths and remain 16 by 16 pixels.

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

## Rechecking the ledger

Run the following commands from the repository root when replacing source material:

```bash
sha256sum "Content/BFS 0.23 Content.zip" "Content/BFS 0.24 Content.zip"
unzip -t "Content/BFS 0.23 Content.zip"
unzip -t "Content/BFS 0.24 Content.zip"
./gradlew :forge:Data :forge:test
```

The release contract test also checks the destination hashes, dimensions, advancement references, animation motion, and generated algae resources.
