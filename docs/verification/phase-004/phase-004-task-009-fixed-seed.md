# Phase 004 task 009 fixed seed evidence

Date: September 11, 2026

Evidence binding note: the original world manifests below were produced before
the Phase 004 source and test repairs. Their recorded candidate hash is kept
for provenance, but those runtime artifacts are superseded and cannot close a
current artifact-bound gate. The current candidate hash is
`525b4b75cc139758a3721e859588940528cf00076a9132d1738b3bd85b9f3ce4`.

This record binds the repeated natural generation probe to the packaged Forge
candidate with SHA 256 `a8744ae7c816371374f083f6237673fe77c22766e56665d1dfb89f6f82d8db56`.
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
earlier continuous-bitstream interpretation was rejected. The two candidate
manifests are retained as `phase-004-seed-run8.json` and
`phase-004-seed-run9.json`.

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

## Disposition

Natural generation, all-form presence, bounded density, exact repeated BFS
coordinates, and zero reported invalid placements pass on the final candidate.
The full seagrass-inclusive repeat equality wording remains the only open row
for P004-TASK-009. No source change was made in response to the variance.

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

## Current candidate repeat

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
