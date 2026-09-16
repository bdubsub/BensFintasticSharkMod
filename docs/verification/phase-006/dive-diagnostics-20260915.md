# Phase 006 dive diagnostics evidence

This packet records the bounded dive diagnostic and schema lifecycle work present on source commit `2a9ed8f`.

## Candidate identity

The Forge artifact was built from that commit with the checked in Gradle wrapper and passed `unzip -tqq`.

```text
artifact: forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar
sha256: 6fd559a53e28ef6c81563d6923f3b7fc84e91d4df3860ad9163e72d2b7dfda1a
sha512: 93dd9eb448b37fcc0f9588c34f89c92ec93353156fda724fbd15ba925dc27a6a7e0442dab552cb15266fe2a07ac378b013fbeb5af4b68db55982dd1ba2624ccb
```

The package contains the four dive item models, the supplied armor textures, the compatible geometry, both GeckoLib armor layer resources, the server diagnostics classes, and no dive recipe. Existing recipe resources remain unchanged. The layer resources close the client warning found during the first laptop pass.

## Server checks

The targeted command and schema fixture passed:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestNamespaces=bfsdive -PbfsGameTestRunDir=forge/run/bfs2-p006-dive-gametest-20260915-schema-passed
```

Four required tests passed. They cover full suit eligibility and landlike travel, the 6000 tick reserve and 20 tick per tick real air refill, missing, established corrupt, and newer schema state, and matched water and air break speed. The existing `bfs_debug_dive` fixture also reached its capture assertions during the complete server run and verified pseudonymous player records, movement and oxygen fields, and a clean terminal footer.

An earlier complete 95 test run passed all required tests, including `bfs_debug_dive`, with the capture schema and `oxygenSchema` fields intact. Two latest-source reruns remain outside the dive scope and were not clean: one reported `vanillafishreplacementhonorsoneforoneandmodes` and `tigercuriosityignoresnonedibleitem`, and the repeat reported `livefishingreelarcsoverobstructiontoangler` and `tigercuriosityignoresnonedibleitem`. The final targeted baseline run was repeated after the client diagnostics fix and passed all four original `bfsdive` tests.

The lifecycle witness added at source commit `2a9ed8f` passed a five test `bfsdive` run. It created a real `ServerPlayer`, started one explicit `dive` capture for 7,200 ticks, consumed the full 6,000 tick reserve while the eyes stayed submerged, observed the first empty tick with vanilla air at zero, moved the player to real air, and verified 300 consecutive refills of 20 reserve ticks. Capture `34c917de-b3ce-4a7c-997c-86d7dab5cd9c` contained 19,805 records, including 7,200 `dive_eligibility`, 6,302 `dive_oxygen`, and 6,301 `dive_travel` records, with zero dropped records and a complete footer. The analyzer returned `complete` for scenario `bfs2-p006-dive-lifecycle-20260915` and requirement `BFS2-REQ-019`. The raw runtime and temporary parser output were removed after analysis.

The new `dive` category requires explicit player targets and records `dive_eligibility`, `dive_oxygen`, `dive_travel`, and `dive_work` events. The capture schema remains `bfs-debug-v2`; the player reserve is recorded as `oxygenSchema`. Reserve schema 1 keeps a newer schema opaque and repairs supported established malformed state to zero without downgrade. Break speed, break, and placement observations are retained for water work parity analysis.

The final packaged server run used the disposable node 1 runtime with Java 17.0.19, Forge 47.2.0, port 25872, and source and artifact bindings above. Capture `507e7f63-6ed9-4247-a462-46c2708f2b3e` completed with 1,802 records, 600 each of `dive_eligibility`, `dive_oxygen`, and `dive_travel`, zero dropped records, and a complete footer. The parser returned `complete` for `BFS2-P006-DIVE-LAPTOP-20260915` and `BFS2-REQ-019`.

The final laptop run used the isolated Prism instance on `envision`, an NVIDIA GeForce RTX 5090 Laptop GPU, window PID 2962375, and the same packaged artifact. The owned Pulse stream stayed muted after connection and resource reload. Capture `9c1ab007-c234-43e9-8f9a-8de772f22eb2` completed with 18,327 records, 14,641 movement records, 3,670 presentation records, 21 tracked entities, zero dropped records, and a complete footer. The run included actual forward, jump, diagonal input, and `F3+T` resource reload. The client log reported the reload feedback and no missing dive layer texture. The parser returned `complete` for the same scenario and requirement.

The analyzer now accepts non applicable render hashes as absent for explicitly unavailable non thresher observations while retaining strict checks for selected resources. Its focused suite passes 31 tests.

## Cleanup

The disposable GameTest runtimes were removed after their final consumers and verified absent. Protected `forge/logs/` and `performance-matrix-20260912-interval50/` directories were left untouched. No laptop client evidence was created by this server-only run.

The phase remains open for the lifecycle matrix outside the uninterrupted interval, focused worn rendering and HUD assertions, reconnect correction evidence, independent review, merge, default branch verification, wiki synchronization, and the signed phase tag.
