# Ben's Fintastic Sharks

Ben's Fintastic Sharks is a Minecraft 1.20.1 ocean wildlife mod for Forge 47.2.0. Version `0.24` adds twenty two marine species, shark hunting and disturbance behavior, natural variants, advancements, trades, equipment, the Shark Codex, and the Sunken Trove structure.

The 0.24 release is maintained on `envy/0.24`, based directly on the stable `1.20.1` branch. It retains the 0.23 content while integrating the supplied advancement artwork, repaired fish animation resources, a single Sharks Galore discovery node, and permanent algae blocks with bounded ocean generation.

This is a Forge only project. The `fabric` subproject is an unused template stub and does not produce a playable Fabric build.

## Requirements

Players and servers need:

* Minecraft 1.20.1.
* Forge 47.2.0.
* GeckoLib 4.4.7.
* SmartBrainLib 1.14.2.
* Java 17.

Install Forge, place the mod and its required dependencies in the `mods` directory, then start the game. Client and server installations must use matching mod versions.

## Features

* Eight shark species with distinct prey lists, variants, hunting traits, hunger cooldowns, and water disturbance reactions.
* Orcas, dolphins, octopuses, nautiluses, eels, turtles, lobsters, stingrays, seals, jellyfish, Atlantic Cod, and Atlantic Salmon.
* Independent spawn categories, species caps, rarity controls, and datapack editable biome and prey tags.
* Atlantic Cod and Atlantic Salmon replace only vanilla Cod and Salmon by default, including natural spawns and the two vanilla spawn eggs. Replacements share vanilla's water ambient population ceiling, and a server config switch restores separate vanilla and BFS spawn sources.
* Optional suppression of other natural vanilla aquatic spawning for packs that want BFS wildlife to fill the oceans.
* Creative showcase commands, including a guaranteed trade villager and detailed species information.
* Bounded server and local client diagnostics for movement, behavior, combat, population, advancement, and algae investigations.
* Shark tools, armor, a Prismarine armor set fitted to swimming poses, Captain Ben's Hat, collectible items, and advancements.
* The Shark Codex and the Sunken Trove ocean structure.
* Three permanent algae blocks with animated large green and red variants and ocean biome generation.

The documentation index is in [docs/README.md](docs/README.md). The 0.24 asset ledger and verification records are under [docs/verification](docs/verification/asset-ledger.md). Diagnostic command and parser instructions are in [docs/test/debug-diagnostics.md](docs/test/debug-diagnostics.md).

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

Server configuration is stored in `config/bensfintasticsharks-common.toml`. Existing configuration files retain old values when defaults change. The 0.23 release adds `[spawning] replace_vanilla_mobs`, which defaults to `true`. With the default, natural vanilla Cod and Salmon and their vanilla spawn eggs produce the matching Atlantic fish. Version `0.23-emergency-fix` corrects their population category so replacements count toward the same water ambient ceiling as the vanilla fish they replace. Tropical Fish, Pufferfish, and every other mob are excluded. Set the option to `false` to retain vanilla Cod and Salmon and enable the Atlantic fish's separate biome spawn sources. The separate `disable_vanilla_aquatic_spawns` option defaults to `false` and requires a game or dedicated server restart after it changes.

The mod targets Forge 47.2.0 on Minecraft 1.20.1 only. Eclipse and Fabric are not supported. Include the matching mod and dependency versions, configuration, reproduction steps, and a completed BFS debug capture when reporting a movement, behavior, combat, population, advancement, or algae issue.

## License

See [LICENSE](LICENSE).
