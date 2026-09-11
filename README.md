# Ben's Fintastic Sharks

Ben's Fintastic Sharks is a Minecraft 1.20.1 ocean wildlife mod for Forge 47.2.0. Release Candidate 1.0, version `1.0-rc.1`, includes twenty two marine species, shark hunting and disturbance behavior, natural variants, advancements, trades, equipment, the Shark Codex, and the Sunken Trove structure.

The Release Candidate 1.0 is maintained on `envy/0.24`, based directly on the stable `1.20.1` branch. It retains the 0.23 content while integrating the supplied advancement artwork, repaired fish animation resources, a single Sharks Galore discovery node, and permanent algae blocks with bounded ocean generation. This is a prerelease candidate, not a stable 1.0 publication.

This is a Forge only project. The `fabric` subproject is an unused template stub and does not produce a playable Fabric build.

## Requirements

Players and servers need:

* Minecraft 1.20.1.
* Forge 47.2.0.
* GeckoLib 4.4.7.
* SmartBrainLib 1.14.2.
* Java 17.

Install Forge, place the mod and its required dependencies in the `mods` directory, then start the game. Client and server installations must use matching mod versions.

The corrected `1.0-rc.1` build removes a Windows startup crash in diagnostic initialization. If a launch report mentions `InvalidPathException` and `unavailable:no_completed_capture`, replace the older candidate JAR with the corrected build. No configuration or world reset is needed.

## Features

* Eight shark species with distinct prey lists, variants, hunting traits, hunger cooldowns, and water disturbance reactions.
* Orcas, dolphins, octopuses, nautiluses, eels, turtles, lobsters, stingrays, seals, jellyfish, Atlantic Cod, and Atlantic Salmon.
* Independent spawn categories, species caps, rarity controls, and datapack editable biome and prey tags.
* Atlantic Cod and Atlantic Salmon replace newly created vanilla Cod and Salmon by default from natural and chunk spawning, spawn eggs, commands, bucket release, dispensers, spawners, and structures. Existing saved fish and unrelated species remain untouched. Replacements share vanilla's water ambient population ceiling, and a server config switch restores separate vanilla and BFS spawn sources.
* Optional suppression of other natural vanilla aquatic spawning for packs that want BFS wildlife to fill the oceans.
* Creative showcase commands, including a guaranteed trade villager and detailed species information.
* Bounded server and local client diagnostics for movement, behavior, combat, population, fishing, advancement, and algae investigations. Movement captures expose route state, selected waypoint, body orientation, pitch exit, curve speed limit and separately tracked propulsion. Fishing captures correlate delivery, rewards and actual rod cleanup without requiring an owner connection for server tests.
* Pitched fish and sharks retain forward propulsion while smoothly reorienting, with vertical travel still limited by their species movement profile.
* Shark tools, armor, a Prismarine armor set fitted to swimming poses, Captain Ben's Hat, collectible items, and advancements.
* The Shark Codex and the Sunken Trove ocean structure.
* Three permanent algae blocks with animated large green and red variants and ocean biome generation.

The documentation index is in [docs/README.md](docs/README.md). The retained 0.24 asset ledger and verification records are under [docs/verification](docs/verification/asset-ledger.md). Diagnostic command and parser instructions are in [docs/test/debug-diagnostics.md](docs/test/debug-diagnostics.md). Candidate installation and rollback guidance is in [docs/test/release-rollback.md](docs/test/release-rollback.md).

The retained Phase 001 verification records document the current 0.23 compatibility baseline, including the real fishing advancement path and the packaged Forge server checks. The interactive advancement and rendering review remains a laptop-only acceptance step.

The [fishing regression record](docs/test/phase-001-fishing-transactions.md) separates current exclusive delivery checks from historical results. Live and item catches use one guarded delivery, preserve Tropical Fish and Pufferfish, and grant matching fishing rewards only after insertion succeeds.

## Development

Use the checked in Gradle Wrapper and Java 17. On Linux:

```bash
./gradlew :forge:compileJava
./gradlew :forge:Data
./gradlew :forge:build
./gradlew :forge:Client
./gradlew :forge:Server
```

On Windows:

```bat
gradlew.bat :forge:compileJava
gradlew.bat :forge:Data
gradlew.bat :forge:build
gradlew.bat :forge:Client
gradlew.bat :forge:Server
```

The distributable JAR is written to `forge/build/libs`. There is no maintained Fabric artifact. The Forge module includes focused JUnit regression and release contract tests. The repeatable runtime procedure is in [docs/test/phase-000-runbook.md](docs/test/phase-000-runbook.md).

## Repository layout

* `common/src/main/java` contains loader independent gameplay and entity logic.
* `common/src/main/resources` contains assets and shared data.
* `forge/src/main/java` contains Forge registration, commands, configuration, rendering, spawning, and data generation.
* `common/src/generated/resources` contains generated data that is checked into the project.
* `tools` contains project maintenance utilities.

Keep generated resources synchronized by running `:forge:Data` after changing advancements, tags, recipes, loot tables, biome modifiers, or language providers.

## Configuration and support

Server configuration is stored in `config/bensfintasticsharks-common.toml`. Existing configuration files retain old values when defaults change. `[spawning] replace_vanilla_mobs` defaults to `true` and replaces newly created vanilla Cod and Salmon from every supported creation source with the matching Atlantic fish. Tropical Fish, Pufferfish, saved entities, and every unrelated mob are excluded. Set it to `false` to retain vanilla Cod and Salmon and enable the Atlantic fish's separate biome spawn sources. `[spawning] fish_entities` defaults to `true`, so a supported fishing catch reels in one live fish that must be killed for its normal drops. Set it to `false` for a direct item catch. Both settings, and the separate `disable_vanilla_aquatic_spawns` option, require a game or dedicated server restart after they change.

The mod targets Forge 47.2.0 on Minecraft 1.20.1 only. Eclipse and Fabric are not supported. Include the matching mod and dependency versions, configuration, reproduction steps, and a completed BFS debug capture when reporting a movement, behavior, combat, population, fishing, advancement, or algae issue.

## License

See [LICENSE](LICENSE).
