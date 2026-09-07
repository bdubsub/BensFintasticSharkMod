# Phase 001 task 012, retained advancement evidence

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

The packaged server runtime used the exact Forge jar hash
`1063f3174db789f59f64da1f8c3c145136905eb75a6611a7b5afacd2cb62f098`, Java
17.0.19, Forge 47.2.0, GeckoLib 4.4.7, and SmartBrainLib 1.14.2. It reached
`Done (13.533s)!`, loaded 7 recipes and 1343 advancements, and stopped cleanly.
The retained packaged server log is
`/tmp/bfsm-p001-evidence-20260907-1742/packaged-server-latest.log` with SHA-256
`baf6b3ea71bddb4313edf82b3ced9f6ad1b40943dfdf93fae76847ffeee6ba9d`.

The final post-package GameTest run completed with `All 42 required tests
passed :)`. The `bfs_fish_items` test performed real fishing-hook resolution
and recorded the server grants for `Why aren't you red?` and `Oh My Cod`.
The test also verifies that both matching Atlantic raw fish remain in the
resolved vanilla `minecraft:gameplay/fishing` loot table.

## Remaining acceptance gate

The fresh profile laptop presentation and complete interactive advancement tree
capture are not claimed here. The current host is the headless node 1 server
host, and no windowed client was launched there. That visual gate must be run
on EnVy's Linux laptop against this exact jar before Phase 001 can close.
