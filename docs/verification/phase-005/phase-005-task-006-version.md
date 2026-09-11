# Phase 005 task 006 Release Candidate metadata

Date: September 11, 2026

The release candidate metadata change is committed at `5282853` on
`envy/0.24-phase-005` and pushed to the phase branch. The exact target is
`1.0-rc.1`, displayed to maintainers and players as Release Candidate 1.0.
The historical `envy/0.24` branch, phase tags, supplied archive names, and
prior verification artifact identities remain unchanged.

## Source and processed metadata

* `gradle.properties` reports `version=1.0-rc.1`.
* Forge resource processing produced `forge/build/resources/main/META-INF/mods.toml`
  with `version = "1.0-rc.1"`.
* Gradle's Forge archive naming contract resolves the candidate filename to
  `BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar`.
* The supported platform remains Minecraft 1.20.1, Forge 47.2.0, and Java 17.
  GeckoLib 4.4.7 and SmartBrainLib 1.14.2 remain pinned.

The final reobfuscated JAR and its embedded manifest are deliberately deferred
to P005-TASK-009, after the ordered deterministic and data gates. That task
must bind the actual file name and hash before runtime evidence begins.

## Documentation and release text

The root `README.md` and `DOCUMENTATION.md` identify the prerelease as
`1.0-rc.1`, display Release Candidate 1.0, preserve the `envy/0.24` work
branch and canonical `1.20.1` branch, and state that this is not a stable 1.0
publication. `CHANGELOG.md` adds the prerelease heading while retaining the
historical 0.24 section unchanged.

Historical phase evidence, archive ledgers, intermediate artifact names, and
immutable `bfs-0.24` or `bfs-0.24-final` identities were not rewritten. Their
old version text is provenance for earlier candidates, not a claim about the
Release Candidate 1.0 artifact.

## Verification

The ordered metadata validation passed:

```text
./gradlew :forge:processResources :forge:compileJava :forge:compileTestJava :forge:test --no-daemon --console=plain
BUILD SUCCESSFUL
```

The Forge unit suite passed after the metadata change. Generated metadata was
inspected directly and reported the exact candidate version. Test-created
Forge logs were removed after the run. No final artifact hash is claimed here;
the exact candidate binding remains a downstream gate.

P005-TASK-006 is complete and authorizes the P005-TASK-007 formatter, static
inventory, deterministic, and Forge test gates.

