# Ben's Fintastic Sharks

Ben's Fintastic Sharks is a Minecraft 1.20.1 ocean wildlife mod for Forge 47.2.0. Version 0.22 adds twenty marine species, shark hunting and disturbance behavior, natural variants, advancements, trades, equipment, the Shark Codex, and the Sunken Trove structure.

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
* Orcas, dolphins, octopuses, nautiluses, eels, turtles, lobsters, stingrays, seals, and jellyfish.
* Independent spawn categories, species caps, rarity controls, and datapack editable biome and prey tags.
* Creative showcase commands, including a guaranteed trade villager and detailed species information.
* Shark tools, armor, a Prismarine armor set fitted to swimming poses, Captain Ben's Hat, collectible items, and advancements.
* The Shark Codex and the Sunken Trove ocean structure.

Full gameplay, configuration, command, architecture, and troubleshooting details are in [DOCUMENTATION.md](DOCUMENTATION.md). Release changes are in [CHANGELOG.md](CHANGELOG.md).

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

The distributable JAR is written to `forge/build/libs`. There is no maintained Fabric artifact and no automated GameTest suite at present.

## Repository layout

* `common/src/main/java` contains loader independent gameplay and entity logic.
* `common/src/main/resources` contains assets and shared data.
* `forge/src/main/java` contains Forge registration, commands, configuration, rendering, spawning, and data generation.
* `forge/src/generated/resources` contains generated data that is checked into the project.
* `tools` contains project maintenance utilities.

Keep generated resources synchronized by running `:forge:Data` after changing advancements, tags, recipes, loot tables, biome modifiers, or language providers.

## Configuration and support

Server configuration is stored in `config/bensfintasticsharks-common.toml`. Existing configuration files retain old values when defaults change. Regenerate the file or update the documented keys manually to adopt the quieter 0.22 spawn defaults.

The mod targets Forge 47.2.0 on Minecraft 1.20.1 only. Eclipse and Fabric are not supported. See [DOCUMENTATION.md](DOCUMENTATION.md#20-troubleshooting) before reporting a spawn, data, or startup problem.

## License

See [LICENSE](LICENSE).
