# Release Candidate 1.0 performance rejection

Date: September 11, 2026.

Status: Failed performance gate. This is not a completed four case comparison and does not close `P002-TASK-012`, issue [28](https://github.com/bdubsub/BensFintasticSharkMod/issues/28), or completed plan retirement. The Windows startup correction remains confirmed. No product code, delivered JAR, plan, goal, or cursor changed during this investigation.

## Bound inputs

The [packaged performance procedure](../test/packaged-performance.md) defines the fixture and recorder. Both cases used the same headless node 1 host, Intel Core i9-13900HK with 20 logical processors, 100,999,901,184 bytes of physical memory, Linux `6.12.63+deb13-amd64`, and Temurin `17.0.19+10`. JVM arguments were `-Xms2G -Xmx2G -XX:+UseG1GC`. No graphical client or local build ran alongside measurement. Unrelated host services were preserved; this was not an isolated hardware benchmark.

The runtime was installed production Forge `47.2.0` for Minecraft `1.20.1`, with launch target `forgeserver`, `nogui`, loopback binding, online mode, and verified `eula=true`. No player connected. GeckoLib `4.4.7` and SmartBrainLib `1.14.2` hashes match the [corrected artifact manifest](artifacts/windows-startup/bfs-1.0-rc.1-source-manifest.txt).

| Input | Identity |
| --- | --- |
| Approved prebehavior source | `692962f25b5b5eda65cc41fe1073c316668fd4c0` |
| Newly built baseline JAR SHA 256 | `c0c1f50ade56a35ccc7590d8196605467634ae86483987163f2a08f613939ff4` |
| Corrected candidate source | `89a8daf03976cc3005da65e90418a687ccf34b5d` |
| Candidate default branch integration | `33f849318b235ecded3012cdb625690096ad6795` |
| Exact delivered candidate SHA 256 | `ac509f173563582485bb041f08cf75c827200110e2a73fb6b5b214f80906872a` |
| Observer agent SHA 256 | `a181c2b235fad5a1ec231664335f903f76645e45f22131620ee3ecb06b9bd6ab` |
| Observer data SHA 256 | `ff2bf7e3f97d742c4411976af827bee132a4d284cf46f28088ca2d5a03cb6685` |
| Java executable SHA 256 | `9276cb8ab3ca31f7185ae733ac4c599f0be64f97dffabbf7e121b1a233f00b08` |
| Installed Forge argument file SHA 256 | `40d86982e24aebab85aa2ed3553dd8d332a63efd4b75b13528f9c032bbb3f2e2` |

Both cases used seed `240024`, a 2400 tick warmup, and a planned 36000 tick measurement. All 22 species were present. Their identical prescribed population vector totaled 63 animals. Missing animals were replenished after each 200 tick census, with natural spawning disabled and real movement, damage, and predation enabled. Actual populations varied and are reported rather than described as constant or identical. The doubled population cases did not start after the normal population case became incapable of passing.

## Measured result

| Metric | Completed baseline | Failed candidate prefix |
| --- | ---: | ---: |
| Recorded ticks | 36000 | 22400 |
| Nominal measured simulation duration | 30 minutes | 18 minutes, 40 seconds |
| Median tick, ms | 3.387195 | 4.629247 |
| p95 tick, ms | 6.944770 | 8.694508 |
| p99 tick, ms | 15.459513 | 14.661901 |
| Maximum tick, ms | 33.714290 | 60.646026 |
| Minimum actual population | 60 | 54 |
| Mean actual population | 62.839779 | 58.761062 |
| Maximum actual population | 63 | 63 |

The candidate percentiles describe only its retained prefix, not a completed 30 minute result. The rejection does not depend on treating that partial p95 as final. The baseline establishes a limit of exactly `7,639,247` nanoseconds, which is `6,944,770 * 11 / 10`. Nearest rank p95 for 36000 observations permits at most 1800 observations strictly above that limit. The monitor first observed 1803 such ticks in a contiguous 22000 tick prefix. At shutdown, the retained contiguous 22400 tick prefix contained 1833. Even if every unobserved tick were faster, the completed window's p95 could not be lower than `7,671,756` nanoseconds. Failure was therefore already unavoidable under the unchanged threshold.

The verified runner received a scoped stop request. Its `finally` block sent the server's ordinary `stop` command. The server saved all dimensions and exited with code zero. The runner and matrix exited unsuccessfully by design, preventing the remaining cases from starting. There is no completed marker for the candidate, and the full comparison analyzer correctly rejects it. No crash, fatal message, exception, or recorder failure appeared in the server log. A single 2268 ms setup lag warning occurred before warmup and measurement began; it is not silently classified as a measured crash or removed from the log identity.

## Behavior and memory observations

The [baseline summary](artifacts/performance-rc1/baseline-normal.json) and [candidate failure summary](artifacts/performance-rc1/candidate-normal-failed.json) include every species' count range, mean and final count, minimum observed air, death causes, sensing, navigation, behavior counters, heap windows, and raw capture hashes.

The completed baseline observed 29 deaths, 49,716 central navigation calls and 82,190 sensor method entries. The shorter candidate prefix observed 477 deaths, 34,027 navigation calls, 48,730 sensor entries and 69,173 bounded living scans. Its action counters recorded 4809 feed, 456 escape, 442 social and 248 habitat selections. Counter windows exclude their first measured tick as documented by the recorder; different durations must not be compared as equal rate windows.

Candidate predation was dominated by 449 American Lobster deaths attributed to Common Stingrays, compared with three such deaths in the longer baseline. This is an investigation lead, not proof of a CPU hotspot or a balanced natural ecosystem. Fixed replenishment locations and changed behavior affect the workload. Profiling must distinguish behavior cost, navigation, sensing, allocation and replenishment churn before selecting an optimization. The current observation does not establish the cause of the timing regression.

All recorded deaths had actual mob damage causes. No drowning death was recorded. Mammal air minima remained positive in these samples, and no observed species exceeded its prescribed administrative count. These facts do not substitute for natural spawning invariants, unauthorized player attack checks, all species transitions, or full breathing starvation acceptance. No connected player was present to supply those interaction claims. Feeding selections do not prove biological hunger or starvation recovery.

Baseline five minute heap minima were `259728384`, `252912640`, `261301248`, `257631232`, `245048320`, and `252388352` bytes. The candidate's three complete five minute window minima were `268593664`, `246397864`, and `269348384` bytes. Maximum observed brain memory map size was 11 for both cases. The observer retained at most 63 current position records. These bounded observations show no monotonic heap floor growth in this run, but are not a retained heap analysis or full lifecycle proof.

## Inspectable evidence and remaining work

The retained gzip files contain only anonymous tick durations and species level census records. They contain no credentials, player identifiers, worlds, or raw server logs. Their decompressed SHA 256 values are recorded in the JSON summaries. Both compressed copies were decompressed, hashed, and recalculated successfully before disposable originals were removed.

* [Baseline tick durations](artifacts/performance-rc1/baseline-normal-probe-ticks.csv.gz).
* [Baseline census](artifacts/performance-rc1/baseline-normal-probe-census.tsv.gz).
* [Failed candidate tick prefix](artifacts/performance-rc1/candidate-normal-probe-ticks.csv.gz).
* [Failed candidate census](artifacts/performance-rc1/candidate-normal-probe-census.tsv.gz).

The evidence validator's 19 tests pass locally and in CI. They cover complete tick and census sequences, missing and duplicate records, population prescriptions, unsuccessful shutdown, warmup boundaries, heap windows, and exact early rejection limits. Passing those tests proves recorder validation, not mod performance acceptance.

The next performance action is scoped profiling and optimization, followed by a fresh exact candidate comparison at ordinary and doubled populations. Do not weaken the 10 percent threshold, claim a 30 minute candidate pass, reuse this incomplete prefix as one, or retire the plan. Existing Windows startup confirmation and unrelated completed gameplay evidence remain intact.

## Teardown

The normal baseline and candidate server processes, matrix runner, and bound monitor exited. The loopback test endpoint closed. After evidence extraction, both runtime worlds, configuration, logs, copied dependencies, duplicate candidate and baseline artifacts, observer binaries, compiled classes, pilot captures and private suite state were removed. The shared Forge libraries and dependency caches were preserved. No laptop runtime was created, and the delivered Downloads JAR was only read and copied for comparison.

The existing implementation worktree remains for the open evidence pull request and its preexisting `build.gradle` line ending state. That file was not staged or normalized. No forced worktree removal, historical branch deletion, or planning file deletion was performed.
