# Follow group regression evidence

This regression covers BFS2-REQ-008 and BFS2-REQ-009. The existing follow implementation keeps membership in a map without an arbitrary group limit, retains a mob after it arrives, and toggles only the mob that is deliberately clicked again. Every selection and release sends one chat receipt and one action bar receipt.

The test was run from the Phase 006 worktree with:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestNamespaces=bfsfollow -PbfsGameTestRunDir=forge/run/bfs2-p006-follow-gametest-20260915-retry
```

All 36 required `bfsfollow` GameTests passed. The new `followGroupHasNoArbitraryMemberCap` fixture selected 20 cows during one held use action, verified 40 paired feedback receipts, released one mob after a fresh click, and verified that 19 members remained selected.

The exact disposable runtime, world, logs, debug capture, and generated configuration were removed after the final log inspection. The protected `forge/logs/` and `performance-matrix-20260912-interval50/` directories were not changed.

The source commit is `1d92832b6b01f449e8d7a9a7df768d37fe319a5e` on `envy/bfs2-phase-006`.

The packaged Forge artifact built for the final Phase 006 candidate passed archive validation. Its SHA 256 is `6fd559a53e28ef6c81563d6923f3b7fc84e91d4df3860ad9163e72d2b7dfda1a` and its SHA 512 is `93dd9eb448b37fcc0f9588c34f89c92ec93353156fda724fbd15ba925dc27a6a7e0442dab552cb15266fe2a07ac378b013fbeb5af4b68db55982dd1ba2624ccb`.
