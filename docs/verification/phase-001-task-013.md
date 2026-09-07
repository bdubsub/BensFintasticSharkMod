# Phase 001 task 013, consolidated verification

This record binds the current Phase 001 verification rerun to the exact source
worktree and candidate artifact. The phase remains open until the laptop visual
gate, final documentation review, and integration task are complete.

## Command matrix

All commands ran in `/mnt/hermes/projects/BFSMOD-phase-001` on 2026-09-07 with
Java 17.0.19 and the checked-in Gradle wrapper.

The Gradle task graph has no repository formatter, static analysis, lint,
Checkstyle, Spotless, PMD, or equivalent task. This absence was recorded and
does not create an invented gate.

| command | result |
| --- | --- |
| `./gradlew :forge:compileJava :forge:compileTestJava --no-daemon` | `BUILD SUCCESSFUL` |
| `./gradlew :forge:test --no-daemon` | `BUILD SUCCESSFUL` |
| `./gradlew :forge:Data --no-daemon` | `BUILD SUCCESSFUL` |
| `./gradlew :forge:GameTestServer --no-daemon --rerun-tasks` | `BUILD SUCCESSFUL`, all 42 required tests passed |
| `./gradlew :forge:build --no-daemon` | `BUILD SUCCESSFUL` |

The required `./gradlew :forge:Server --no-daemon --args='--port 25615
--nogui'` smoke reached `Done (13.077s)!`, loaded 7 recipes and 1343
advancements, and stopped after the readiness check. Gradle reports exit 143
because the owned server child was terminated during disposable-runtime
cleanup. The retained server log is
`/tmp/bfsm-p001-evidence-20260907-1742/dev-server-latest.log` with SHA-256
`bf8a0fd8d98cddcc5dc277bc98cfd77b220b4fb949d73fd06a257fa7d6882294`.

The final GameTest run was server-only and headless. It exercised the shared
movement, vertical arrival braking, bite timing, Tiger curiosity cleanup,
Oceanic and Blacktip lifecycle cleanup, fish parity, fish acquisition,
replacement and population controls, advancement fishing criteria, and the BFS
debug capture lifecycle. No GameTest failure remained after the final source
changes. The retained final GameTest log is
`/tmp/bfsm-p001-evidence-20260907-1742/gametest-latest.log` with SHA-256
`af42fd899922cfd9b1f181c1cf13402fc880187c7b5f48dc624724f73d04eb79`.

## Candidate artifact

The Forge artifact is
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar`.

* SHA-256: `1063f3174db789f59f64da1f8c3c145136905eb75a6611a7b5afacd2cb62f098`
* SHA-512: `0f34f5b70b24c84ede22cd1ae60c54a0ffd38da96de9f8a539799168181e61471984952c98d43b3e6de5529807bc4baeed568553d84e6e2f1de9b354de2ba803`
* `unzip -tqq` passed.
* The jar contains the Atlantic fish classes and resources, Oceanic Whitetip
  resources, retained advancement JSON, generated loot modifiers, item models,
  and supplied textures.

The generated resource review found the two Atlantic fishing modifiers changed
from the unused `minecraft:gameplay/fishing/fish` table to the actual vanilla
`minecraft:gameplay/fishing` root table used by `FishingHook.retrieve`. The
candidate jar contains the regenerated JSON with that corrected condition.

## Open task 013 gate

The exact laptop client visual matrix, fresh profile advancement tree review,
and final documentation consistency pass remain open. No node 1 client or
renderer was used, and no headless client result is being substituted for that
visual evidence.
