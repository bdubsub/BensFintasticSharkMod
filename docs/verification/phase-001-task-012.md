# Phase 001 task 012, retained advancement evidence

The earlier artifact results below do not close DEC-014 live/item delivery and its failure or replay cases. The [current transaction regressions](../test/phase-001-fishing-transactions.md) verify matching advancement timing for their bounded live Cod fixtures; the remaining species, mode, progress and client matrix remains open.

This record covers `P001-TASK-012` and `BFS-REQ-012` for Shark Spotter and the
four retained Atlantic fish advancements.

## Deterministic and packaged checks

The generated advancement audit passed for the exact candidate built on 2026-09-07.

* `shark_spotter` uses the registered `spyglass_spotted_shark` trigger and a
  spyglass icon.
* `gadus_morhua` and `salmo_salar` use the registered `player_found_entity`
  trigger with exact Atlantic Cod and Atlantic Salmon predicates and raw fish
  icons.
* `oh_my_cod` and `why_arent_you_red` use the vanilla
  `minecraft:fishing_rod_hooked` trigger with the matching raw fish item
  criterion and cooked fish display icon.
* All four retained fish advancement identifiers, parent links, requirements,
  translations, and item roles are present in the candidate Forge jar.

The final packaged server readiness probe used the rebuilt Forge jar with
SHA-256
`9f0aa22a3e76740df3d5f035015a11cf0363f9fafc794873d995b7dc9b780571`, Java
17.0.19, Forge 47.2.0, GeckoLib 4.4.7, and SmartBrainLib 1.14.2. It reached
`Done`, loaded 7 recipes and 1343 advancements, and stopped cleanly. The
retained final log is `/tmp/bfsm-p001-packaged-final-java17.log` with SHA-256
`3453b987c0b1a96fe0af8c31d57490f7a4f86c8cee165e9a6d278339167870cb`.

The final rebuilt GameTest run completed with `All 42 required tests
passed :)`. The `bfs_fish_items` test performed real fishing-hook resolution
and recorded the server grants for `Why aren't you red?` and `Oh My Cod`.
The test also verifies that both matching Atlantic raw fish remain in the
resolved vanilla `minecraft:gameplay/fishing` loot table.

The final rebuilt GameTest log is
`/tmp/bfsm-p001-final-evidence-20260907/gametest-final.log` with SHA-256
`35df1fe4da934a25547ac268794bf65e8a9f3c6a48c0b19b1f25d874d076611a`.

## Remaining acceptance gate

The fresh profile laptop presentation and complete interactive advancement tree
capture are not claimed here. The current host is the headless node 1 server
host, and no windowed client was launched there. That visual gate must be run
on EnVy's Linux laptop against this exact jar before Phase 001 can close.
