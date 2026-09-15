# Follow group regression evidence

This regression covers BFS2-REQ-008 and BFS2-REQ-009. The existing follow implementation keeps membership in a map without an arbitrary group limit, retains a mob after it arrives, and toggles only the mob that is deliberately clicked again. Every selection and release sends one chat receipt and one action bar receipt.

The test was run from the Phase 006 worktree with:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks -PbfsGameTestNamespaces=bfsfollow -PbfsGameTestRunDir=forge/run/bfs2-p006-follow-gametest-20260915-retry
```

All 36 required `bfsfollow` GameTests passed. The new `followGroupHasNoArbitraryMemberCap` fixture selected 20 cows during one held use action, verified 40 paired feedback receipts, released one mob after a fresh click, and verified that 19 members remained selected.

The exact disposable runtime, world, logs, debug capture, and generated configuration were removed after the final log inspection. The protected `forge/logs/` and `performance-matrix-20260912-interval50/` directories were not changed.

The source commit is `1d92832b6b01f449e8d7a9a7df768d37fe319a5e` on `envy/bfs2-phase-006`.

The packaged Forge artifact built after this test passed archive validation. Its SHA 256 is `7a240ff549085341be440463fe2d2cc5ee66fbd9c3b4f1acf74c9cf8bdd7b0bd` and its SHA 512 is `0e94d9bb9b1a18d110729de576b00907d2f54721efa03981490d14e09370a9bb7a16fb037cb76909bfcf3ec8553dc1848f0cdd07a701d089048d7df4064ca4bd`.
