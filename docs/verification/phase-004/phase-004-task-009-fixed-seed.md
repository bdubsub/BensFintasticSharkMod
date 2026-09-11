# Phase 004 task 009 fixed seed evidence

Date: September 10, 2026

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
