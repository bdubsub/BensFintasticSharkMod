# Phase 000 task 001 intake

## Scope

This record freezes the truthful Phase 000 intake for the current debug and performance pass. It covers BFS2-REQ-001, the source and evidence boundary used by BFS2-REQ-020, and the diagnostic boundary used by BFS2-REQ-022. It does not claim a gameplay fix, a performance pass, or Phase 000 completion.

## Source and remote state

The applicable worktree is `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix` on `envy/rc1-release-evidence` at commit `627ce25c918bc6525becd2a414580eacd6b5faad`. The goal and documentation commit is signed by EnVy with the registered SSH key. The implementation tree under `common`, `forge`, `fabric`, and `tools` is byte identical between the historical intake commit `40207d1b4cbe8db9963f32e43b79bdcfce58918a` and the current commit. The intervening commits add the reviewed plan, research, goal, cursor, and validation records; they do not change implementation behavior.

The canonical remote is `origin`, `https://github.com/bdubsub/BensFintasticSharkMod.git`. The live default branch is `1.20.1` at `33f849318b235ecded3012cdb625690096ad6795`. Local `1.20.1` is `15d3a8ce4913dbe5b647b67dbb5993b2f58c983e` and is an ancestor of that live head. The current branch is the applicable retained implementation branch. Pull request 29 is open and draft at the current head, based on `1.20.1`. Issue 28 remains open with the retained performance work.

## Protected state

The worktree had one pre-existing tracked change, the line ending difference in `build.gradle`, with SHA 256 `71fcd9ca04c96389a97202347bf4230008a0a149c96a6c77da84a0cde187ebf2`. It remains untouched and unstaged. The required CodeGraph index is ignored under `.codegraph/`. No other tracked or untracked source, asset, plan, goal, cursor, or owner path was changed by this intake. Temporary Gradle task outputs were removed after the task inventory check. No Minecraft runtime, client, dedicated server, display, or audio stream was launched.

## Toolchain and build graph

The repository targets Minecraft `1.20.1`, Forge `47.2.0`, Parchment `2023.09.03`, Java `17`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`. The checked wrapper is Gradle `8.8`; the verified Java 17 runtime is Temurin `17.0.19`. The Gradle project includes `common`, the Forge implementation, and an unused Fabric template. Relevant Forge tasks are `compileJava`, `compileTestJava`, `test`, `Data`, `GameTestServer`, `Server`, `Client`, and `build`. No task was run that compiles or starts the game during this intake.

## Historical failure boundary

The retained performance record remains failed and immutable. Its completed baseline measured 36,000 ticks. The candidate stopped with a contiguous 22,400 tick prefix after the unchanged p95 limit had already become impossible, recording 1,833 observations above the allowed limit of 1,800. The baseline limit is `7,639,247` nanoseconds. The recorded candidate artifact identity is `ac509f173563582485bb041f08cf75c827200110e2a73fb6b5b214f80906872a`. This is attribution input only. The doubled cases were not run, and no failure is relabeled as a pass.

## Diagnostic and scratch boundary

The existing server diagnostic path is the IFC-001 input for the next task. It is default off, bounded, permission checked, server authoritative, parser backed, and writes under its designated diagnostic directory. P000-TASK-002 must verify its current schema, terminal completeness, parser behavior, redaction, failure paths, and bounded overhead before any profile based repair. Scratch output for subsequent work must stay inside an exact disposable directory under the project anchor, retain only sanitized evidence, and be removed after its last consumer. Shared dependency caches and owner data remain protected.

## Result

The source, remote, protected state, toolchain, historical failure, diagnostic input, and scratch boundary are identified. P000-TASK-001 is ready to be recorded as complete. The next action is P000-TASK-002, beginning with the existing diagnostic manager, command parser, support guide, and assertions. No performance decision is authorized from this intake alone.
