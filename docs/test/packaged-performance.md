# Packaged server performance verification

This procedure measures the installed Forge server, not a development server or rendered client. It supports the mixed species performance gate and does not replace visual, multiplayer, natural spawning, or player interaction evidence.

## Fixed comparison

Use Minecraft `1.20.1`, Forge `47.2.0`, Java `17`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`. Keep hardware, JVM arguments, dependency hashes, fixture seed, loaded area, observer binaries, warmup, and measurement duration identical. Run comparisons sequentially. Do not run builds or another benchmark alongside a measurement.

The prebehavior baseline is the approved Phase 001 integration at `692962f25b5b5eda65cc41fe1073c316668fd4c0`. A newly built baseline binary must receive its own checksum. Do not substitute the original RC1 artifact as that baseline because its wildlife implementation already includes the later behavior changes.

The corrected candidate is the exact owner-delivered artifact identified in the [current release bundle](../verification/artifacts/README.md). Do not rebuild it merely to add instrumentation. Source commits, artifact hashes, and the corresponding integration commit remain separate identities.

Run four cases: baseline and candidate at ordinary population, then baseline and candidate at doubled population. Each case warms for `2400` ticks before recording `36000` individual completed server ticks. At nominal 20 TPS that is two minutes of warmup followed by a 30 minute simulation. A shorter pilot checks the recorder only and never satisfies this gate.

## Fixture

The runner creates a new disposable runtime beneath a separately allocated suite directory. It refuses to reuse an existing run directory. The server binds loopback, keeps online mode enabled, and has no connected human players. No graphical workload runs on the build host.

The fixed seed is `240024`. A flat water column runs from Y32 through Y62 above sand, with air at Y63. Glass walls enclose a 128 by 128 area. The fixture includes two bottom obstacles and a seagrass patch. A 160 by 160 area is force loaded so entities tick without a real player. Daylight and weather are fixed. Natural spawning is disabled to isolate the controlled workload.

The ordinary fixture contains all 22 species, with the sum of the shipped per species default caps as its explicit target population: 63 animals. The doubled case targets 126. These are administrative workload fixtures, not a claim that natural spawning should admit that many nearby sharks. The runner restores missing animals after each census using ordinary summon commands with persistence enabled. It never assigns targets, modifies velocity, enables invulnerability, or disables animal intelligence. Counts may briefly fall between censuses because predation and other real deaths remain enabled. Preserve minimum, maximum, and final counts rather than calling the target count constant.

## Recorder

The sources are under `tools/performance/`:

* `ProbeAgent.java` attaches entry and return observations to the packaged server tick, the central navigation path method, sensor calls, species scans, behavior actions, and death callbacks.
* `TickProbe.java` records elapsed nanoseconds for each whole server tick and a census every 200 ticks. It observes existing state without consuming gameplay randomness or changing decisions. A death record includes the victim class, actual damage source identifier, and causing entity class. The observer retains only current UUID and position snapshots, not entity references.
* `run_performance.py` builds the disposable world through the owned server console, bounds execution, replenishes the workload, and stops its exact server in a `finally` block. It retains runtime files only for the final log inspection and evidence extraction.
* `analyze_performance.py` rejects missing ticks, incomplete censuses, missing completion markers, unsuccessful shutdowns, and mismatched comparison inputs. Its median uses individual tick durations; p95 and p99 use nearest rank, not percentiles of time averaged TPS values.

Compile the recorder with the selected Java 17 JDK and the installed server's ASM `9.5` dependency. Package `TickProbe.class` in `probe-data.jar`. Package the agent classes in `probe-agent.jar` with this manifest:

```text
Manifest-Version: 1.0
Premain-Class: bfs.verification.ProbeAgent
Boot-Class-Path: probe-data.jar
```

Keep both recorder JARs beside each other in the disposable suite directory. They are verification tooling and must never be included in the mod JAR or delivered to ordinary players. The Forge module path supplies ASM to the agent; the bootstrap path contains only the small observation class, with no Minecraft implementation or replacement mod classes.

Run `python3 -B tools/performance/run_performance.py --help` for exact required paths. Each invocation requires the suite, installed Forge libraries, exact candidate or baseline JAR, both dependency JARs, Java executable, and one unique run name. The accepted full comparison names are `baseline-normal`, `candidate-normal`, `baseline-double`, and `candidate-double`. Use `--scale 2` for doubled cases. Defaults are the full duration; `--ticks` and `--warmup` overrides are only for explicitly identified pilots.

The runner creates `eula=true` only in its disposable runtime and reads it back before launch. It links the existing shared production libraries without copying or editing them. No authentication file, personal instance, save, or server list is required.

## Evidence and limitations

Retain source and dependency identities, fixture parameters, hardware and Java identity, per species counts, actual death causes, behavior counters, sensing and navigation counts, heap trend, per tick percentiles, and measurement file hashes. Validate the complete four case packet with `analyze_performance.py`. The p95 regression allowance is 10 percent at each matched population. A failure remains a failure; do not reduce duration, redefine the baseline after seeing results, omit slow ticks, or report an average as p95.

Sensor counters count instrumented method entries, including Java bridge methods where present. Navigation counters count the central path method's calls, including calls that reuse a cached path. They are work indicators, not distinct successful routes. Event counters start after the first measured tick, so their declared observation window is ticks 2 through 36000; all 36000 tick durations are recorded. Ambient feeding action counters indicate selected routes and must not be represented as measurements of biological hunger or food energy. Heap measurements are live JVM usage, not proof that every used byte is retained; inspect the trend and bounded memory structures alongside lifecycle tests.

The census runs after the measured tick interval and is excluded from that interval's duration. All compared cases use exactly the same recorder binaries. This is not the separate diagnostics off/on overhead test. No connected player means this fixture cannot establish client delivery, presentation, real input, player immunity, or unauthorized attack reachability. The administrative population does not replace natural population invariant verification. Bind those claims to their existing independent regression and acceptance evidence.

The raw census includes warmup rows before the measurement rows. When warmup ends exactly on a census interval, its final row and the following measurement start both carry offset zero. The analyzer validates the complete declared warmup and measurement sequences separately, retains the raw capture hash, and reports both row counts. It never discards a timed tick or accepts an extra measurement row to hide a malformed capture.

After log inspection and extraction, stop any remaining owned processes, verify their exit, unlink each runtime's owned `libraries` link without following it, and remove only the exact disposable runtime contents. Preserve the small sanitized report and required input hashes in the verification documentation. Remove duplicate JARs, raw logs, worlds, class files, and scratch captures after their last consumer. Keep the delivered artifact and shared libraries untouched.
