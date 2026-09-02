# Phase 005 Evidence

This record is the release evidence packet for Forge 1.20.1 version 0.24. The owner selected `1.20.1` as the stable default branch and `envy/0.24` as the release branch. No `main` branch is required or created.

## Repository and contract identity

| Record | Value |
| --- | --- |
| Stable branch | `1.20.1` |
| Release branch | `envy/0.24` |
| Release source commit | `f386f19bd5616854ad476ad9c260b7182ceaa53f` |
| Minecraft | `1.20.1` |
| Forge | `47.2.0` |
| Java | `17.0.19+10` Eclipse Adoptium |
| Mod version | `0.24` |
| Plan SHA 256 | `b75748b2dfbe2902015f5872e0bcf9ab7a41324861690833325aef95d5f0b027` |
| Plan index SHA 256 | `8d22d8a17ed56a1f0f102f41a29ae0919ad66f61af3ec97fbf46231a0ca4a70d` |
| Plan handoff SHA 256 | `f979b8dd4e0a19f42ca54126e72f86cd0bd7f53eae15df0e32f3f97356be671f` |
| Goal SHA 256 | `142bc71f75fb32f343428daa225ef8e51c4d5c29e5fe1445bff37db9952ec6f2` |
| Commit signature | SSH ED25519, fingerprint `SHA256:CE014W2Y8QMbKKspTiTQAJ37gK83TV3gup2el94DWb4`, verified locally for EnVy |

The branch is a direct descendant of `1.20.1` commit `06a28f6b3fcc62ee54cead9e62370c86aea0bfb9`. The root `build.gradle` line ending change and the owner supplied `Content/` archives remain outside the commit.

## Deterministic verification

| Check | Command | Result |
| --- | --- | --- |
| Unit and release contract tests | `JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 PATH=/usr/lib/jvm/temurin-17-jdk-amd64/bin:$PATH ./gradlew :forge:test --rerun-tasks` | Passed. |
| Data generation | `JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 PATH=/usr/lib/jvm/temurin-17-jdk-amd64/bin:$PATH ./gradlew :forge:Data --rerun-tasks` | Passed. The data run reported 302 generated files and no stale removals. |
| Forge GameTests | `JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 PATH=/usr/lib/jvm/temurin-17-jdk-amd64/bin:$PATH ./gradlew :forge:GameTestServer --rerun-tasks` | Passed. The clean Forge server reported two required tests and all two passed. |
| Forge build | `JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 PATH=/usr/lib/jvm/temurin-17-jdk-amd64/bin:$PATH ./gradlew :forge:build --rerun-tasks` | Passed. |
| Repository diff hygiene | `git diff --check -- . ':(exclude)build.gradle'` | Passed. Generated cache files and the protected line ending change were not staged. |
| JSON validation | Python JSON parsing and duplicate key scan for tracked resource JSON | Passed. Zero parse errors and zero duplicate keys. |
| Side and secret scans | Client reference scan for common sources and secret pattern scan | Passed. No unexpected common side reference or secret pattern was found. |

The aggregate root `./gradlew build --rerun-tasks` remains a known unsupported template failure. It reaches the Fabric sources, which do not declare the Forge SmartBrainLib dependency used by the shared gameplay sources. The supported Forge build above is the release build gate, and this change does not advertise a Fabric artifact.

## Runtime loading evidence

| Surface | Command and result |
| --- | --- |
| Dedicated server | `./gradlew :forge:Server --args='--port 25576 --nogui'` reached `Done (3.323s)!` in a disposable `phase024-smoke` world on port 25576 with the mod loaded, recipes and advancements registered, and no mod specific error, exception, missing resource, or class loading marker. The process was stopped after readiness with a controlled termination. |
| Client loading | `xvfb-run -a timeout --kill-after=15s 90s ./gradlew :forge:Client --args='--demo'` reached OpenGL 4.5 llvmpipe, Forge mod loading, resource reload, texture atlas completion, and OpenAL initialization. The bounded process was stopped after loading with controlled Ctrl C termination. The log contained no fatal error, exception, missing model, missing texture, or missing resource marker. |

The loading smokes prove startup and resource registration only. Interactive visual captures, multiplayer reconnect behavior, a sustained fixed seed population sample, and a fresh profile completion of every advancement path remain owner acceptance checks rather than being claimed by this headless evidence.

## Packaged artifact

| Field | Value |
| --- | --- |
| File | `forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar` |
| Size | `1,748,439` bytes |
| SHA 256 | `513e7e734e163cbf527c77eead0bde3baf1992381f87d82a4f4730af450a30de` |
| SHA 512 | `7c50451ceab73c0d160a98b47fbe8e40597a2287cb009c079e09895a731284e623aa36a7d5884ed89600c925413a40a3d7d2e322e3e335195ed57e4974025b19` |
| Archive test | `unzip -t` passed with 874 entries and no errors. |
| Manifest | `Specification-Version`, `Implementation-Version`, and `mods.toml` version are `0.24`; `Built-On-Minecraft` is `1.20.1`; `Built-On-Java` is Java 17. |

The JAR contains all seven 16 by 16 advancement icons, the Cod, Salmon, and Oceanic Whitetip animation resources, three algae block textures and generated world data, the surviving advancement graph, and no `shark_whisperer` resource. Checksums and the SPDX document are stored beside this record in `docs/verification/artifacts`.

## Supplied source material

| Source | SHA 256 |
| --- | --- |
| `Content/BFS 0.23 Content.zip` | `1dcfd0db544184bffd467255ba294b57f0a1376b06023f10034f380b9e5d0eaa` |
| `Content/BFS 0.24 Content.zip` | `25290e8d019339aff68c8ba168bd750d5c445aa915720d249aeed5700c9f3274` |

Both archives passed `unzip -t`, traversal, and link safety checks. The archives remain local owner material and are not packaged or committed.

## Remaining owner acceptance gates

The implementation and deterministic Forge gates are complete. The following evidence is still explicitly required before a public release claim: interactive movement and animation review, multiplayer and reconnect cleanup review, fixed seed algae density and population sampling, and fresh profile completion of all advancement branches. CurseForge and Modrinth publication is not authorized by this record.
