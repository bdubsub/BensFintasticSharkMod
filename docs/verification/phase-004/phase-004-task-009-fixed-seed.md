# Phase 004 task 009 fixed seed evidence

Date: September 11, 2026

Evidence binding note: the original world manifests below were produced before
the Phase 004 source and test repairs. Their recorded candidate hashes are kept
for provenance, but those runtime artifacts are superseded. The source
equivalent candidate hash is
`ac4ce5e54e0282ce0e0f970e938778f121dc6595d1974f20c8ef8236cbd339df`. The
final exact artifact hash is recorded in the final rebinding section below.

The historical repeated natural generation probe was bound to the packaged
Forge candidate with SHA 256 `a8744ae7c816371374f083f6237673fe77c22766e56665d1dfb89f6f82d8db56`.
Both worlds used Minecraft 1.20.1, Forge 47.2.0, Java 17.0.19, seed `240024`,
the default data pack set, the final Phase 004 configuration, and the Overworld
window of chunks `0,0` through `15,15`. The server runtime was the disposable
node 1 production Forge runtime at `/tmp/bfsm-p004-prod-server-20260910-095H8c`.
No client or renderer ran during this probe.

## Counting method

The audit reads the saved Anvil region with `nbtlib`, decodes the fixed values
per long used by Minecraft `SimpleBitStorage`, and records each BFS algae
block as `[form, x, y, z]`. The region header, chunk status, section palette,
block state, form totals, chunk totals, and coordinate hash are all checked.
The decoder was calibrated against the Phase 000 positive control after an
earlier continuous-bitstream interpretation was rejected. Historical candidate
manifests are retained as `phase-004-seed-run8.json` and
`phase-004-seed-run9.json`. The final candidate manifests are
`phase-004-seed-run16.json` and `phase-004-seed-run17.json`.

## Candidate worlds

| World | Manifest SHA 256 | Chunks | Algae block | Large green | Large red | Combined | Algae chunks | Max per chunk | Seagrass control |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| `phase004-run8` | `3a63f3e8faa7e7f188e2280f7f9d5c471f53e3190e83dcb610916187ba0d2a4e` | 256 | 12 | 11 | 20 | 43 | 38 | 2 | 2914 |
| `phase004-run9` | `4725bcf80f16395392111b377d24d6787fdb1f5d0d1c2d94702526990421048f` | 256 | 12 | 11 | 20 | 43 | 38 | 2 | 2901 |

The BFS coordinate and block-state multiset is identical in both candidate
worlds. Its SHA 256 is
`1344307297cbe9bf0d6b1584f29393fbf9e62b8ff6372cdf249c2ab438121dc8`. Every
form is present, the finite cap is respected, and every custom placement is in
the valid aquatic placement set reported by the scanner. Combined algae is
strictly below the same-sample vanilla seagrass control in both worlds.

The vanilla seagrass control is intentionally reported separately. Its saved
count varied by 13 blocks between the two clean worlds, and a regeneration of
the same target region in `phase004-run8` produced 2934 while the custom algae
multiset remained identical. This is ordinary vanilla seagrass growth or
world-generation variance, not BFS placement drift. The strict plan wording
requires equal full coordinate and state manifests; therefore this task records
the passing BFS repeat and density evidence but does not claim the full
seagrass-inclusive equality gate closed.

## Historical disposition

Natural generation, all-form presence, bounded density, exact repeated BFS
coordinates, and zero reported invalid placements pass on the final candidate.
The full seagrass-inclusive repeat equality wording remained open for the
historical probes. No historical result was promoted to final evidence.

## Controlled repeat follow-up

On September 11, 2026, four additional disposable production-server repeats
were run with the same candidate, seed, 256 chunk window, Java 17, and
`randomTickSpeed=0` set before the window was force loaded. The BFS counts
remained `35`, `28`, and `48` in every run and the BFS coordinate multiset
remained identical. The vanilla control still varied, including after the
world was initialized with the zero random-tick gamerule before decoration.
This confirms that the remaining mismatch is in the vanilla seagrass control,
not in BFS algae placement. The strict full-manifest row remains open and is
not waived.

## Superseded current candidate repeat

On September 11, 2026, the exact current candidate was tested again in two
fresh disposable Forge 1.20.1 dedicated-server worlds with Java 17.0.19. Both
runtimes set `eula=true`, set `randomTickSpeed` to zero before the sample
window was force loaded, and generated only through ordinary chunk generation.
No direct block-placement fixture commands were used. The target window was
seed `240024`, Overworld chunks `0,0` through `15,15`, for all 256 chunks.

The current artifact is SHA 256
`525b4b75cc139758a3721e859588940528cf00076a9132d1738b3bd85b9f3ce4`.
The earlier BFS-only manifests are retained as
`phase-004-seed-run10.json` and `phase-004-seed-run11.json`. Complete
algae-and-seagrass coordinate and state manifests were generated afterward as
`phase-004-seed-run14.json` and `phase-004-seed-run15.json`.

| World | Manifest SHA 256 | Algae block | Large green | Large red | Combined | Algae chunks | Max per chunk | Seagrass | Tall seagrass |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| `phase004-fixed-current-c` | `80879885ec4efbc353bd6c893e3f778ad5d89cea891bd3ed6b536600bc21f4d8` | 35 | 28 | 48 | 111 | 92 | 3 | 3114 | 5377 |
| `phase004-fixed-current-d` | `a382c3a3ec37714ce58d28b667c3a81606a2b401192393e2ca8c4cb35482ff25` | 35 | 28 | 48 | 111 | 92 | 3 | 3127 | 5384 |

The BFS coordinate and form multiset is identical in both manifests. Its
coordinate hash is
`6df2cdcb1e3fbff5d92ee1b56408a774e403cd8011541b521f72877e4e80289b`.
Every form is present, the configured per-chunk cap of three is respected,
and the invalid placement count is zero. Combined algae remains below the
vanilla seagrass controls in both worlds. The vanilla control differs by 13
short seagrass blocks and 7 tall seagrass blocks, so the strict
seagrass-inclusive full-manifest equality row remains open. This is recorded
as a control variance and is not represented as a BFS placement mismatch.

The complete manifests contain 8,578 and 8,580 counted algae and vanilla
seagrass block states respectively. Their full placement state hashes differ
because the vanilla seagrass positions vary, while the custom algae state hash
is identical in both at
`187fdad7cd02253e98918dd7437bb15622c3757f23bfb2446989bf65facc600c`.
This records every counted coordinate and block state without hiding the
vanilla variance.

| World | Manifest SHA 256 | Full state hash | Counted placements | Common seagrass blocks |
| --- | --- | --- | ---: | ---: |
| `phase004-fixed-current-g` | `adf112087d5da4f65ff03b38e08196523603bb3823fb61be8127e26ce3a45301` | `667836536597a014459022fe8bcfc15712e7e3a41ff786831e45d198c961b13a` | 8578 | 8467 |
| `phase004-fixed-current-h` | `500819a463a005967565101a5d5c36b62700f47947b15cd430c6a304d80ede09` | `3afc272c070ab66215bb5ae812964bf6462c903e8cf4290955f02cac92fb564a` | 8580 | 8469 |

## Longer wait source equivalent repeat

On September 11, 2026, two additional disposable production Forge servers were
run with Java 17.0.19, seed `240024`, `randomTickSpeed` set to zero before the
sample window was force loaded, and all 256 chunks in the inclusive `0,0`
through `15,15` window kept force loaded for an additional wait before clean
shutdown. The source equivalent current jar SHA 256 was
`ad2338017b3c06f7b45edc2c9474fbdab09954c0a4a921c98ae7399a6ef38381`.

The custom algae coordinate and form multiset was identical in both worlds,
with counts of `35` `algae_block`, `28` `large_green_algae`, and `48`
`large_red_algae`. The complete counted totals were `8604` in runtime A and
`8588` in runtime B. Runtime A counted `3125` short and `5368` tall vanilla
seagrass blocks. Runtime B counted `3111` short and `5366` tall blocks. The
full state hashes were
`34583de1b852de83233c49af18058f2575f795e7dcbea0b8659884122a03f0c4` and
`5a341ac549e6ccf8a46d75a50de603c14c7f14bd4e9d0f0bbbbc3ddbd9bd1f3d`.
The server log hashes were
`84e517ec16c43b45be28e9afb819f73000e1105768ef6b6a7ced6169ae9cb74b` and
`c0322703414dc296f11eab50f4e77a22a0092771cbc835d830ea2ef210d6356e`.

The longer wait did not remove the vanilla seagrass variance. The custom
algae multiset remained stable, while the strict full manifest equality gate
remains open as required by the plan. These source equivalent diagnostic
manifests do not replace the exact candidate manifests above.

## Serialized command order repeat

The same source-equivalent jar was also tested with the 256 chunk tickets
issued in fixed row-major order, with `0.4` seconds between commands and a
clean save after the complete window. Runtime A counted `3104` short and
`5364` tall vanilla seagrass blocks, for `8579` counted placements. Runtime B
counted `3098` short and `5374` tall blocks, for `8583` placements. Both
runtimes kept the custom algae counts at `35`, `28`, and `48`, and both custom
coordinate hashes were
`273b7beea4ae788442bab09429dd26a43c6f1fde269bce81a2d649380b00e1c4`.
The full state hashes were
`f45f8c08406ea098a26bb605e85ecd98ba26fedf355de6de3757bfdceaec8712` and
`f167ab8898d6080c8524e86065dde1ff70dd311cbec7983457838c4daf12b082`.
The production log hashes were
`a84987b52ce32ba43a35ebfc5521d6c2c811d00728306ee925dae3f144e48066` and
`1523454538a926b5712d5aba09e86c730df64685eda976560790f467186f16d4`.
Serializing the ticket commands therefore did not close the strict vanilla
full manifest equality row.

## Final candidate buffered repeat

On September 11, 2026, the final Forge candidate was tested in two fresh
disposable production Forge 1.20.1 dedicated-server runtimes with Java 17.0.19.
The exact candidate SHA 256 was
`ac4ce5e54e0282ce0e0f970e938778f121dc6595d1974f20c8ef8236cbd339df`.
Both runtimes verified `eula=true`, used seed `240024`, set
`randomTickSpeed` to zero before decoration, and generated the target window
through ordinary chunk generation. No direct block-placement fixture commands
were used.

The target manifest window was Overworld chunks `0,0` through `15,15`. To
remove an edge-control dependency, each run also force loaded a one-chunk
buffer from `-1,-1` through `16,16`, issuing row-major single-chunk tickets
with a `0.3` second interval before saving and clean shutdown. The disposable
runtimes were `/tmp/bfsm-p004-fixed-n-WLVy6Y` and
`/tmp/bfsm-p004-fixed-p-ihMeKM`. Their final server log SHA 256 values are
`9983fed0aab23abb1ca166c0c20e3e874d7b15ed1640c4677650e399183c46b2` and
`cfd9503294a94aabee8c2c8ccfad8c285a5cf2edcbbfd9d72bd39da8042a22a7`.

| World | Manifest SHA 256 | Algae block | Large green | Large red | Combined | Algae chunks | Max per chunk | Seagrass | Tall seagrass | Placements |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| `phase004-fixed-n` | `4b8a3ba4660e1d699302acd7d8e4192fa4dfda79ef2b92ac29796d85f43ed3c8` | 31 | 23 | 40 | 94 | 81 | 3 | 3096 | 5382 | 8572 |
| `phase004-fixed-p` | `f0b6ff7fc4e96ee6050a3c25379a6d460a01ed02dfaf4dd453b7beca8ab3abed` | 31 | 23 | 40 | 94 | 81 | 3 | 3096 | 5382 | 8572 |

The complete placement manifests are byte-for-byte equal. The shared full
placement state hash is
`d54a08f7c711574c80387412fe19c511bfe21a56b0e569a274405a1b0fcbf9d0`.
The custom coordinate hash is
`ce24bfa58e58b74e62136002c7f579af69c4c56a161ff77e4df0362befc314bd`, and
the custom algae state hash is
`d722da9b87a961d6eab8a3a9275a55e4741fa96fee5ebd2854dfd9ef5ee38906`.
All three forms are present, the maximum custom count per chunk is three, the
invalid placement count is zero, and combined algae remains below the matched
vanilla seagrass control. This closes the strict full coordinate and block-state
manifest equality gate for P004-TASK-009.

## Final exact artifact rebinding

The exact final Forge jar is SHA 256
`a22f84941a0c8457f6daab3000d4278a1c5ea06c3ab126eb32df73a91c9862bf`. Two
additional clean production Forge 1.20.1 runtimes were run with Java 17.0.19,
seed `240024`, `randomTickSpeed=0`, the same one chunk buffer, and the same
row major ticket procedure. Their complete manifests are
`phase004-seed-run18.json` and `phase004-seed-run19.json`.

| World | Manifest SHA 256 | Algae block | Large green | Large red | Combined | Algae chunks | Max per chunk | Seagrass | Tall seagrass | Placements |
| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| `phase004-fixed-a22e` | `6bdbe9db015d9b411529f4101d916cab3c3a86fb3bf887ed90c3de8b42e6201d` | 31 | 23 | 40 | 94 | 81 | 3 | 3107 | 5370 | 8571 |
| `phase004-fixed-a22f` | `f7456a4f45a433b31a92e54b41ece14a3d91051f75d3d0c10372f19f69b7b3b5` | 31 | 23 | 40 | 94 | 81 | 3 | 3087 | 5416 | 8597 |

The two exact final runs have identical custom algae coordinate and state
multisets with coordinate hash
`ce24bfa58e58b74e62136002c7f579af69c4c56a161ff77e4df0362befc314bd`.
Both contain every form, remain below their matched vanilla seagrass control,
and have zero invalid placements. The vanilla seagrass block positions vary
between fresh runtime generation, so their complete placement state hashes are
recorded separately rather than presented as equal. The earlier
source-equivalent pair `phase004-seed-run16.json` and
`phase004-seed-run17.json` remains the strict full state equality control.

The source-equivalent candidate used for that pair and the final exact jar
have byte-identical content in all 938 non manifest archive entries. The only
difference is Forge's generated `META-INF/MANIFEST.MF` implementation and
build timestamp fields. This binds the strict full state control to the same
compiled code and resources while preserving the fresh exact artifact probes
above.
