# Phase 004 task 001 entry evidence

Date: September 11, 2026

This entry record binds Phase 004 to the live amended plan and the exact
Phase 003 integration revision. The live authoritative plan set is rooted at
`/mnt/hermes/projects/BFSMOD/docs/general/`; copies in this worktree are
historical branch files and do not override it.

## Predecessor and branch boundary

The Phase 004 worktree is `/mnt/hermes/projects/BFSMOD-phase-004` on branch
`envy/0.24-phase-004`. Its base is the merged `envy/0.24` commit
`9026a43f757d57d7670f072c7a034467175ca9ca`. The remote branch points to that
same commit, and the branch is not stacked on the Phase 003 branch or on
`1.20.1`.

The signed annotated tag `bfs-0.24-phase-003` verifies with EnVy's registered
ED25519 key and points to commit `9026a43f757d57d7670f072c7a034467175ca9ca`.
The phase transition was accepted only after pull request 19 merged through
GitHub with build and CodeQL checks passing. The immutable goal remained
unchanged at SHA-256
`f48badcd994c8336aca4cb73b7a04efb72baf508f54343d191e1827cd729e25b`.

## Live plan and evidence bindings

The live authoritative files were read and validated before this entry:

| File | SHA-256 |
| --- | --- |
| `docs/plan/goal.md` | `f48badcd994c8336aca4cb73b7a04efb72baf508f54343d191e1827cd729e25b` |
| `docs/plan/active_phase.md` | `e60699612af8c47658d170b357d4a5d7059c7a1a75d3021c33e60d4acb808169` |
| `docs/general/plan.md` | `857924e4841bc8b13d4fc4a7e9f815d84536ae163aad91ba06601e97dc91bf13` |
| `docs/general/plan.index.json` | `ef4a1f02770f3c9424dcb4c8c5352b5ee4932abd1a09e432309da86a72ab157e` |
| `docs/general/plan.handoff.json` | `5c82c7b485d4da827080a4bac981f2e081b07566ae5914c42f3daf311254814e` |
| `docs/general/phases/plan-phase-004.md` | `f7e1846e757e128fe5ebd2812ab1793b9477c58fb7497ef54d43f0a78e59d14f` |
| `docs/verification/phase-000-manifest.json` | `24f4f6f8a12d124d0e683f63d504a18e7ed7fa5eb52d047fd3c5312cf67b8113` |

The amended live Phase 004 plan says the three algae forms already exist and
assigns this phase the remaining survival, collection, rendering, natural
generation, navigation, compatibility, and final artifact evidence. The
older phase file copied into the branch describes the pre amendment baseline;
that digest drift is classified as plan provenance, not a product conflict.
No plan or goal file was edited.

## Supplied asset and destination audit

The required archives remain byte-identical. `BFS 0.24 Content.zip` has
SHA-256 `25290e8d019339aff68c8ba168bd750d5c445aa915720d249aeed5700c9f3274`
and `BFS 0.23 Content.zip` has SHA-256
`1dcfd0db544184bffd467255ba294b57f0a1376b06023f10034f380b9e5d0eaa`.
The 22 loose source PNGs under the owner `Content/Algae Textures/` directory
match the Phase 000 manifest exactly. The source rows are:

| Source group | Dimensions | SHA-256 |
| --- | --- | --- |
| `algea_block.png` | 16 by 16 | `562c9b1a75c2f9ed912c22b0f424651792a3df02ea96bf72b8b5cb6a59ba7392` |
| `biggreenalgea_strip.png` | 16 by 160 | `6818750169255d1fd99f33f3363c96157ef5355f2fe8663ba78f3f344ef7f0ab` |
| `bigredalgea_strip.png` | 16 by 144 | `ad9c3abbd801de619c1d8e14b53b3ec0e74bb21c47da1e980bca553210f8110c` |
| `bigredalgea_1` through `_3` | each 16 by 16 | `818238e574a12e13050fb1b3897d5901a332be422c8779ce0095eded6f99d204` |
| `bigredalgea_4` through `_6` | each 16 by 16 | `49d1246b44d22bc572f140214cc2ffe05cb3fffd7f4d078dcd97318521d0903d` |
| `bigredalgea_7` through `_9` | each 16 by 16 | `bc8431c1432f863de7635d03b3c70dd66dda30d80dd44379076d24d162379a77` |
| `greenbigalgea_1` | 16 by 16 | `852cc1899f2756b241eaf7bad1aea675e0a00ab9b60de9a867da8496c43c1096` |
| `greenbigalgea_2` and `_3` | each 16 by 16 | `0fe9c99166e5f52e2ec49ab435ed9eb001a7c3b2db72c9553f62d273e8436ccf` |
| `greenbigalgea_4` through `_6` | each 16 by 16 | `76d85b975460493cc142f629106c56e6dafe0a0888095d124da148fd88a2e177` |
| `greenbigalgea_7` and `_8` | each 16 by 16 | `93450e1856167cddfe967f71a3ae240b1074adf983508bfa7ae1be62be2a32fc` |
| `greenbigalgea_9` and `_10` | each 16 by 16 | `0900b4ade7560b4e9e94caa3895f2b076a732801d4765df0b55effbecb316525` |

The runtime destinations match the three source strips and block texture:

| Destination | SHA-256 |
| --- | --- |
| `textures/block/algae_block.png` | `562c9b1a75c2f9ed912c22b0f424651792a3df02ea96bf72b8b5cb6a59ba7392` |
| `textures/block/large_green_algae.png` | `6818750169255d1fd99f33f3363c96157ef5355f2fe8663ba78f3f344ef7f0ab` |
| `textures/block/large_red_algae.png` | `ad9c3abbd801de619c1d8e14b53b3ec0e74bb21c47da1e980bca553210f8110c` |
| `textures/block/large_green_algae.png.mcmeta` | `2afbc253cd7562646ab5a3cde7308a6a35d4de2a28ac000ada40c9c187ba45c5` |
| `textures/block/large_red_algae.png.mcmeta` | `9395723d5f768158779f8388c2b3cac35af648cb535bba0901d839529566f37b` |

The green metadata contains frames 0 through 9 and the red metadata contains
frames 0 through 8. Both use `frametime: 4` and `interpolate: false`.

## Registration and public identity audit

The branch contains exactly one common block holder entry and one matching
BlockItem for each required public ID: `algae_block`, `large_green_algae`, and
`large_red_algae`. Generated blockstates, block models, item models,
translations, loot, tags, configured features, placed features, and biome
modifiers use those correctly spelled IDs. The source tree and generated tree
contain no public `algea` path, identifier, or translation key. The three
items remain ordinary creative entries through the existing creative tab
enumeration, while the supplied `Content/` directory remains outside Git.

## Preserved Phase 000 controls and open rows

The Phase 000 manifest remains readable and records fixed seed `240024`,
Overworld chunks `0,0` through `15,15`, 256 generated chunks, 35
`algae_block`, 28 `large_green_algae`, 48 `large_red_algae`, 111 total BFS
placements across 92 chunks, and 8,456 vanilla seagrass placements. The two
passed water placement and water restoration GameTests are preserved as
positive controls. They do not close the remaining Phase 004 layers: all-form
Survival collection and exact loot, complete client rendering and animation,
dedicated-server loading on the candidate, navigation, repeated fixed-seed
equality, compatibility and rollback, and final JAR proof.

## Protected state and entry disposition

The phase worktree has only the pre-existing protected `build.gradle` change.
The owner `Content/` directory and all owner worktree generated or runtime
files remain untouched and unstaged. No source, generated resource, registry,
plan, goal, or configuration mutation was made by this entry audit.

P004-TASK-001 entry validation is complete. The next action is P004-TASK-002,
which reconciles the existing algae implementation and source-to-destination
provenance before any behavior or generation repair.
