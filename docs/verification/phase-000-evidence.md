# Phase 000 Evidence

Phase 000 establishes the evidence baseline for the Forge 1.20.1 0.24 work. The stable default branch is `1.20.1`; the implementation branch is `envy/0.24`. Both start at commit `06a28f6b3fcc62ee54cead9e62370c86aea0bfb9`.

## Baseline checks

| Check | Command | Result |
| --- | --- | --- |
| Repository identity | `git remote -v`, `git ls-remote --symref origin HEAD` | The remote is `https://github.com/bdubsub/BensFintasticSharkMod.git` and `origin/HEAD` resolves to `origin/1.20.1`. |
| Branch ancestry | `git merge-base --is-ancestor 1.20.1 envy/0.24` | The 0.24 branch is based directly on the stable 1.20.1 baseline. |
| Archive integrity | `sha256sum`, `unzip -t`, and entry path inspection | Both supplied archives pass hash, integrity, path traversal, and link checks. |
| Forge compilation | `./gradlew :forge:compileJava :forge:compileTestJava` | Passed on Java 17. |
| Forge unit and contract tests | `./gradlew :forge:test` | Passed. |
| Generated data | `./gradlew :forge:Data` | Passed and produced deterministic advancement, tag, model, loot, and algae world generation resources. |
| Forge GameTest harness | `./gradlew :forge:GameTestServer --rerun-tasks` | Passed. A clean server launch ran two required algae tests and reported all two required tests passed. |
| Dedicated server loading | `./gradlew :forge:Server --args='--port 25576 --nogui'` | A disposable `phase024-smoke` world reached the Forge ready state on unused port 25576 with the mod loaded and no mod-specific registry, data, or class-loading errors. The controlled smoke process was stopped after readiness. |
| Client loading smoke | `xvfb-run -a timeout --kill-after=15s 90s ./gradlew :forge:Client --args='--demo'` | The client reached OpenGL initialization, Forge mod loading, resource reload, texture atlas completion, and OpenAL initialization. The bounded smoke process was stopped after loading; interactive visual review remains separate evidence. |

The unrelated Fabric template is not a release target. Its aggregate build remains unsupported because the template does not include the Forge SmartBrainLib dependency used by the common gameplay sources. Forge checks are the authoritative build path.

## Automated evidence

`forge/src/test/java/tfar/bensfintasticsharks/audit/ReleaseContractAuditTest.java` provides deterministic checks for:

* the complete Captain's Legacy description and the seven supplied advancement titles,
* one terminal punctuation mark for every generated advancement description,
* retirement of `shark_whisperer` and direct `sharks_galore` parents for its existing children,
* exact 16 by 16 supplied icon copies,
* nonstatic transform values in the Cod, Salmon, and Oceanic Whitetip clips,
* permanent algae dimensions, animation frame order, cutout models, loot, tags, and bounded placed feature settings.

The test reads checked in generated resources. Run data generation first whenever a provider changes.

## Outstanding runtime evidence

The following evidence is intentionally runtime based and must be captured before the release endpoint:

* movement and vertical path recovery for every supported shark,
* Tiger Shark edible item curiosity, timeout, invalidation, and prey preemption,
* Oceanic Whitetip grab and Blacktip Reef latch cleanup through death, removal, disconnect, reconnect, and dimension changes,
* `/bfs info` output and Deep Ocean or Deep Lukewarm Ocean habitat probes,
* Prismarine armor clearance across the complete breaststroke camera matrix,
* Atlantic fish parity, fishing, recipes, loot, and exact `Spin` name behavior,
* fixed seed algae generation and interactive visual review,
* packaged JAR contents, checksums, SBOM, and signed integration evidence.

These checks cannot be replaced by source inspection or a successful compilation.
