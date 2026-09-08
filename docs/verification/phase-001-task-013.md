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

## Current reconciled headless rerun

On 2026-09-07, the reconciled Phase 001 head `39dae8f385f51642fc7eabb8241e567eae4f670d`
was extracted into a clean temporary tree on node 1. The run used the explicit
Java 17.0.19 executable `/usr/lib/jvm/temurin-17-jdk-amd64/bin/java` and did
not start a client, renderer, display server or virtual display.

The ordered commands all passed:

```text
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
./gradlew :forge:test --no-daemon
./gradlew :forge:Data --no-daemon
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks
./gradlew :forge:build --no-daemon
```

The final GameTest server reported all `42` required tests passed. The Forge
artifact `forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar` passed
`unzip -tqq` with SHA-256
`f50cd99a15420b8ab09031d16c6354951cbe3cf7eb010d64573e0f04a01c9a53` and
SHA-512
`aad24e3527abfa98a9d680efcccbd68ab4e6794c357e27ef812e69e4486d16b940394d205a351dbffee5150c3ad1e4af46055fc2f27cd9721c4787cd9b76417b`.
Sanitized logs are retained at `/tmp/bfsm-p001-task013-evidence-20260907/`:
`compile.log` SHA-256
`07c9b66737010c3194e2c7b45857365f045b0782068caf8f126b626309f4c140`,
`test.log` SHA-256
`69fb8052233e63bfd1bfe30a6c22bb2481b3ecf096e90c821d02a322f7c30a3d`,
`data.log` SHA-256
`dc39c8d5c8573bb0826b1f8af8738e6b82be606fa4fa2287b419238b950c1983`,
`gametest.log` SHA-256
`fa0b03adc695069bf757459f9e9c919627d4dba7dfa67abeba97f696759095f4`, and
`build.log` SHA-256
`36dd8af84e7705c07bd9e8bc9bdde9547e7078da68dc847ef33e3516bd5d4131`.
The extracted tree and disposable GameTest runtime were removed after the
final consumer and the exact path was verified absent.

This closes the current deterministic and artifact portion of P001-TASK-013.
The laptop visual matrix, fresh profile advancement review, final documentation
review, and P001-TASK-014 pull request, merge and signed tag remain open.

## Bound packaged diagnostic rerun

On 2026-09-07, the exact Forge candidate copied to the disposable packaged
server and the authenticated laptop client was bound at runtime with source
revision `39dae8f385f51642fc7eabb8241e567eae4f670d`, artifact SHA-256
`5096bb71ceabec46e2270b09d88bc22e73b5daf592b621118d98f8936bc8b983`, Java
`17.0.19`, Forge `47.2.0`, GeckoLib `4.4.7`, SmartBrainLib `1.14.2`, and the
configuration and generated-data fingerprints recorded in the capture header.
The server ran on node 1 at `100.76.164.109:25820`. The laptop client used
Prism instance `bfsm-p001-task013`, connected through the private endpoint,
rendered with the NVIDIA GeForce RTX 5090 Laptop GPU, and its exact Java
playback stream was verified muted.

The first five-target capture is retained as a negative control because Tiger
Shark predation killed two prey targets. It contains 5,207 records with zero
drops but ends with two missing targets, so it is not acceptance evidence.
Separate tagged fixtures removed that interaction. The nonpredator capture
selected Atlantic Cod, Atlantic Salmon, and Bottlenose Dolphin for 300 ticks
each. It contains 900 movement samples, zero dropped records, no missing
targets, and one complete terminal record. Strict analysis returned `complete`
for scenario `p001-task013-packaged-motion-20260907` and requirement
`BFS-REQ-001`. The shark capture selected Tiger Shark and Oceanic Whitetip for
300 ticks each. It contains 600 movement samples, zero dropped records, no
missing targets, and one complete terminal record. Strict analysis returned
`complete` for scenario `p001-task013-packaged-shark-motion-20260907` and
requirement `BFS-REQ-001`.

Raw captures, candidate manifests, analyzer verdicts, and their SHA-256 ledger
are retained in `/tmp/bfsm-p001-task013-evidence-20260907/`. These captures
close the bound server telemetry and target-completeness portion of Task 013.
They do not close the authored client animation matrix, fresh-profile
advancement presentation, or final phase integration gates. The phase remains
open and the server-only diagnostic result does not substitute for those
client claims.

The same packaged client also supplied an exploratory static geometry capture
from the laptop window without changing the active Hyprland workspace. The
window was the exact owned Minecraft client at stable id `1800094f` on
workspace `3`. The inspected capture hash is
`2cfc073dbb9673bbd565b97d7e3895a2c572cb1ef503f0d6a2ab188a088f55db`.
The earlier connected-water checkpoint hash is
`645bf918d36007ba132b74d77f14580c701363c2c26a87e4ecac27c3fa8ed839`.
These are visual checkpoints only. They do not claim the complete authored
animation state matrix, continuous pitch and movement review, or fresh
advancement presentation. Both images are retained with the capture evidence
outside Git.

## Fresh profile advancement encounter and presentation rerun

On 2026-09-07, the exact packaged candidate from source revision
`a2a700bd8a63de8272c8b50ce1e300e011e53fa3` was copied to the disposable Forge
server at `100.76.164.109:25830` and to the fresh laptop profile
`bfsm-p001-task012-fresh-20260908`. The candidate SHA-256 was
`f2ab6a4b3f34aacde835b3ec0bb4afc3e81eacc499a44d21c0fcc3798563d91b`.
The server used Java 17.0.19, Forge 47.2.0, GeckoLib 4.4.7, SmartBrainLib
1.14.2, `spawn-animals=true`, and `doMobSpawning=false`. The laptop client
process was PID `3162910`, rendered with the NVIDIA GeForce RTX 5090 Laptop
GPU, and its playback stream was matched to that PID and verified muted.

The player was placed in survival at `[560.5d, 68.0d, 400.5d]`. The server
revoked all advancements, then summoned persistent no-AI Atlantic Cod and
Atlantic Salmon encounter fixtures at `560 68 400` and `561 68 400`. No
advancement grant command was used. The fresh client log recorded
`Gadus morhua` and `Salmo salar` after the encounter fixtures were present.
The sanitized client excerpt SHA-256 is
`f01f438d1880dea66cdfcb0f9e6009a3d4a61cc93fc8823dfa7ec16c66288800`.

The advancement screen was captured directly from the owned client window on
workspace `3` without switching to that workspace. The captured tree showed
the Atlantic Salmon node title `Salmo salar` and description `Encounter an
Atlantic Salmon.`. The PNG SHA-256 is
`7a8abe7e1a95a43550de5260bcf2a5bca3a20674c14e9ccc667b6b7381ad6878`.
The command transcript, sanitized log excerpt, and image are retained at
`/tmp/bfsm-p001-task013-evidence-20260908/` outside Git.

This rerun closes the fresh-profile Atlantic encounter and direct presentation
checkpoint for the two retained encounter advancements. It does not close the
complete authored animation matrix, the remaining fishing presentation rows,
or repository integration task `P001-TASK-014`.
