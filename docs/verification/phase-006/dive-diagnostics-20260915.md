# Phase 006 dive diagnostics evidence

This packet records the bounded dive diagnostic and schema lifecycle work present on source commit `d6360c71030d06dcc09775f13a0d4ece449d755f`.

## Candidate identity

The Forge artifact was built from that commit with the checked in Gradle wrapper and passed `unzip -tqq`.

```text
artifact: forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar
sha256: 8c1c35cd34b8388cf3f9f60f173c8fc9c1f957f068225582b714d4d2cbff7529
sha512: 6d77f049f7098c6553e491f1b263168e69470d6c0c8f2d9b06871a3f01d226a67ca4163107939bfea20443538be77a7be020c8af1eff8df2f473092def356b2f
```

The package contains the four dive item models, the supplied armor textures, the compatible geometry, the server diagnostics classes, and no dive recipe. Existing recipe resources remain unchanged.

## Server checks

The targeted command and schema fixture passed:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestNamespaces=bfsdive -PbfsGameTestRunDir=forge/run/bfs2-p006-dive-gametest-20260915-schema-passed
```

Three required tests passed. They cover full suit eligibility and landlike travel, the 6000 tick reserve and 20 tick per tick real air refill, and missing, established corrupt, and newer schema state. The existing `bfs_debug_dive` fixture also reached its capture assertions during the complete server run and verified pseudonymous player records, movement and oxygen fields, and a clean terminal footer.

The complete 95 test run reached all batches and reported one pre existing failure in `bfsgametests.tigercuriosityignoresnonedibleitem`. That run is failed and does not close the phase gate. The failure is unrelated to the dive fixtures and remains open for its owning regression pass.

The new `dive` category requires explicit player targets and records `dive_eligibility`, `dive_oxygen`, `dive_travel`, and `dive_work` events. Reserve schema 1 keeps a newer schema opaque and repairs supported established malformed state to zero without downgrade. Break speed, break, and placement observations are retained for water work parity analysis.

## Cleanup

The disposable GameTest runtimes were removed after their final consumers and verified absent. Protected `forge/logs/` and `performance-matrix-20260912-interval50/` directories were left untouched. No laptop client evidence was created by this server-only run.

The phase remains open for the full lifecycle matrix, client HUD and worn rendering checks, input and reconnect evidence, independent review, merge, default branch verification, wiki synchronization, and the signed phase tag.
