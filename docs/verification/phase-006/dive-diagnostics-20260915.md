# Phase 006 dive diagnostics evidence

This packet records the bounded dive diagnostic and schema lifecycle work present on source commit `806ec1cca23b8c4a2d29132d9e53fb52c3c246fc`.

## Candidate identity

The Forge artifact was built from that commit with the checked in Gradle wrapper and passed `unzip -tqq`.

```text
artifact: forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar
sha256: 56d4d9e5e7c8e593fab2cecc424a292a4703c14a683a7b5cfc634ebfbecc7913
sha512: a01f96770c16a359fe69efb6b87982fb86dfd6d1ee86600f7bc4afce33a31797acedb5b71e0354b71b6f5ac4a427935a47201bba4f443ecb4aa83a834981e166
```

The package contains the four dive item models, the supplied armor textures, the compatible geometry, the server diagnostics classes, and no dive recipe. Existing recipe resources remain unchanged.

## Server checks

The targeted command and schema fixture passed:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestNamespaces=bfsdive -PbfsGameTestRunDir=forge/run/bfs2-p006-dive-gametest-20260915-schema-passed
```

Three required tests passed. They cover full suit eligibility and landlike travel, the 6000 tick reserve and 20 tick per tick real air refill, and missing, established corrupt, and newer schema state. The existing `bfs_debug_dive` fixture also reached its capture assertions during the complete server run and verified pseudonymous player records, movement and oxygen fields, and a clean terminal footer.

The complete 95 test run passed all required tests, including `bfs_debug_dive`, with the capture schema and `oxygenSchema` fields intact.

The new `dive` category requires explicit player targets and records `dive_eligibility`, `dive_oxygen`, `dive_travel`, and `dive_work` events. The capture schema remains `bfs-debug-v2`; the player reserve is recorded as `oxygenSchema`. Reserve schema 1 keeps a newer schema opaque and repairs supported established malformed state to zero without downgrade. Break speed, break, and placement observations are retained for water work parity analysis.

## Cleanup

The disposable GameTest runtimes were removed after their final consumers and verified absent. Protected `forge/logs/` and `performance-matrix-20260912-interval50/` directories were left untouched. No laptop client evidence was created by this server-only run.

The phase remains open for the full lifecycle matrix, client HUD and worn rendering checks, input and reconnect evidence, independent review, merge, default branch verification, wiki synchronization, and the signed phase tag.
