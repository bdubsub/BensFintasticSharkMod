# Phase 006 dive diagnostics evidence

This packet records the bounded dive diagnostic and schema lifecycle work present on source commit `0825114ec8c08b35f06acc1dbe9b395bf7606123`.

## Candidate identity

The Forge artifact was built from that commit with the checked in Gradle wrapper and passed `unzip -tqq`.

```text
artifact: forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar
sha256: fee46e18c90e3137af9ff2ccaeaf657e10c05fde948a6ef5f1c8a7f6554f8ca9
sha512: 63f9b5a59e109d295c53b392c1897bc4d558bca8901aac637e8ceae42833369da8fd73b4a85a8e433fe93193295d3011789a31d29e55820a721c9b7499140a93
```

The package contains the four dive item models, the supplied armor textures, the compatible geometry, the server diagnostics classes, and no dive recipe. Existing recipe resources remain unchanged.

## Server checks

The targeted command and schema fixture passed:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestNamespaces=bfsdive -PbfsGameTestRunDir=forge/run/bfs2-p006-dive-gametest-20260915-schema-passed
```

Four required tests passed. They cover full suit eligibility and landlike travel, the 6000 tick reserve and 20 tick per tick real air refill, missing, established corrupt, and newer schema state, and matched water and air break speed. The existing `bfs_debug_dive` fixture also reached its capture assertions during the complete server run and verified pseudonymous player records, movement and oxygen fields, and a clean terminal footer.

The complete 95 test run passed all required tests, including `bfs_debug_dive`, with the capture schema and `oxygenSchema` fields intact.

The new `dive` category requires explicit player targets and records `dive_eligibility`, `dive_oxygen`, `dive_travel`, and `dive_work` events. The capture schema remains `bfs-debug-v2`; the player reserve is recorded as `oxygenSchema`. Reserve schema 1 keeps a newer schema opaque and repairs supported established malformed state to zero without downgrade. Break speed, break, and placement observations are retained for water work parity analysis.

## Cleanup

The disposable GameTest runtimes were removed after their final consumers and verified absent. Protected `forge/logs/` and `performance-matrix-20260912-interval50/` directories were left untouched. No laptop client evidence was created by this server-only run.

The phase remains open for the full lifecycle matrix, client HUD and worn rendering checks, input and reconnect evidence, independent review, merge, default branch verification, wiki synchronization, and the signed phase tag.
