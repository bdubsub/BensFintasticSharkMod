# RC2 Debug Movement Hotfix Verification

Date: September 24, 2026. Version: `1.0-rc.2-hotfix.1`.

The owner authorized this bounded hotfix without a replacement plan. The source baseline is `a772bca2a957c894b3a7f30b2fe08eef4fc3812d` on `1.20.1`. The hotfix branch is `envy/rc2-debug-hotfix`. Earlier local plan retirement changes are excluded from the hotfix commit.

## Behavior

The four horizontal and vertical speed and sprint fields accept nonnegative finite values without the former speed 20 and sprint 4 gameplay limits. Validation still rejects numeric overflow and combinations that cannot produce a finite engine movement speed. Bare species IDs, `bfs:` and `bensfintasticsharks:` IDs, and an unquoted `*` target work through the command dispatcher.

Active movement overrides use independent horizontal and vertical propulsion during ordinary movement. Previously that calculation was selected only by a movement test fixture, while real entities used the older scalar integration. The tuned long vertical route also releases its turning throttle once aligned. Untuned movement retains its existing path, and resetting the four movement settings restores it. Water and obstacle clearance and arrival braking remain active.

## Verification

* All 73 Java unit tests passed, including large values, invalid number rejection, and atomic failure behavior.
* All 31 diagnostic parser tests passed.
* The fresh focused server run passed all 46 required GameTests from `bfstuning,bfsfollow,bfsdisturbance`.
* The three new tuning GameTests cover actual command parsing up to 1,000, ordinary upward and downward travel without the movement intent fixture, unchanged horizontal movement when only vertical speed changes, and release of the aligned vertical route throttle.
* The earlier broad run completed 148 tests with 146 passing. The follow brain ownership and real water entry policy tests failed in that combined run. Both passed in the subsequent fresh 46 test run without changes to their implementation or assertions. The combined run is not recorded as an all passing suite, and the cause of those two combined run failures was not established.
* The 48,000 tick population test finished in the broad run. The owner explicitly waived repeating it for this hotfix. It was excluded from the final focused run.
* `:forge:build`, JAR archive integrity, version metadata, scoped secret inspection, staged diff checks, and generated resource drift checks passed.
* The actual packaged JAR started on installed production Forge 47.2.0 with Java 17, GeckoLib 4.4.7, and SmartBrainLib 1.14.2. Console commands applied wildcard speed `100 250`, namespaced sprint `100 1000`, and a vertical value of `100000000000000000000`. Effective readback retained those values, including `1.0E20`, followed by session reset and clean shutdown.

The final focused command was:

```bash
./gradlew :forge:test :forge:GameTestServer :forge:build -PbfsGameTestNamespaces=bfstuning,bfsfollow,bfsdisturbance -PbfsGameTestRunDir=run/bfs-rc2-hotfix-20260924 --no-daemon
```

## Artifact and Delivery

Delivered filename: `bfs-1.0-rc.2-hotfix.1.jar`.

* Size: 4524624 bytes.
* SHA 256: `3cf483e3be6c1a7b89aa480b6546a534705a6bd02c7bc8205432450183ec0727`.
* SHA 512: `7847eb92fe0a5cfb268a77060d161c9a412935a22d6a87f3dbe2ce622ce7ee0453c0663d75f0377f4a02b28bc2c4617db69a93e244c3915fb50f6125aea9233a`.
* GeckoLib SHA 256: `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0`.
* SmartBrainLib SHA 256: `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b`.

The JAR, checksum files, and changelog were copied to `/home/envy/Downloads` on `envision`. All four copied file hashes matched the originals. Previous release candidates were preserved. A stable copy remains under the project's `deliverables/bfs-1.0-rc.2-hotfix.1` directory on `node-1`.

## Boundaries and Cleanup

All execution was server only on `node-1`, from the existing `_qa/windows-startup-fix` checkout. The two disposable runtimes were `forge/run/bfs-rc2-hotfix-20260924` and `forge/run/bfs-rc2-hotfix-packaged-20260924`. Both owned server processes exited. Both runtimes and 107 newly created verification output files were removed after packaging and delivery. Preexisting build output, dependency caches, owner files, and local plan retirement edits were preserved.

No client or graphical acceptance was performed for this hotfix. The delivered JAR is a testing hotfix, not a public release. Gameplay feel remains for Ben to tune. Replace the previous BFS JAR, keep matching client and server versions, and reapply session tuning after restart. See the [changelog](../../CHANGELOG.md) and [debug command reference](../test/debug-diagnostics.md).
