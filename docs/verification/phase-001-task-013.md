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
`/tmp/bfsm-p001-final-evidence-20260907/gametest-final.log` with SHA-256
`35df1fe4da934a25547ac268794bf65e8a9f3c6a48c0b19b1f25d874d076611a`.

A fresh server-only rerun on 2026-09-07 at 23:53 used the same checked-in
Phase 001 worktree and completed all 42 required GameTests successfully. The
run included the fish, movement, combat, curiosity, lifecycle, replacement,
advancement, and debug capture batches. This rerun produced no source or
resource changes and does not replace the still required laptop visual matrix.

## Candidate artifact

The Forge artifact is
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar`.

* SHA-256: `9f0aa22a3e76740df3d5f035015a11cf0363f9fafc794873d995b7dc9b780571`
* SHA-512: `5815653352c5bd12b727d17b4a869456b2006f98d36a56b7951f3f4ee8eb29aa4804dcfaa0b6d73302c68e31c62e92508108e4619eeba9b971cfcd16bd9c476c`
* `unzip -tqq` passed.
* The jar contains the Atlantic fish classes and resources, Oceanic Whitetip
  resources, retained advancement JSON, generated loot modifiers, item models,
  and supplied textures.

The generated resource review found the two Atlantic fishing modifiers changed
from the unused `minecraft:gameplay/fishing/fish` table to the actual vanilla
`minecraft:gameplay/fishing` root table used by `FishingHook.retrieve`. The
candidate jar contains the regenerated JSON with that corrected condition.

The final packaged readiness probe used the same artifact, Java 17.0.19, and
the matching Forge, GeckoLib, and SmartBrainLib dependency set. It reached
`Done` and stopped cleanly. Its log is
`/tmp/bfsm-p001-packaged-final-java17.log` with SHA-256
`3453b987c0b1a96fe0af8c31d57490f7a4f86c8cee165e9a6d278339167870cb`.

## Tracked documentation consistency audit

On 2026-09-07, the maintained Markdown link set in `README.md`,
`DOCUMENTATION.md`, `CHANGELOG.md`, and `docs/README.md` resolved without a
missing target. The plan index and authoring handoff are present. The checked
metadata sources report version `0.24` and Minecraft `1.20.1`, and the
packaged `META-INF/mods.toml` reports version `0.24`. The final Forge build and
`git diff --check` both passed during this audit. No tracked documentation
change was required by this consistency pass.

## Open task 013 gate

The exact laptop client visual matrix, fresh profile advancement tree review,
and final documentation consistency pass remain open. No node 1 client or
renderer was used, and no headless client result is being substituted for that
visual evidence.
