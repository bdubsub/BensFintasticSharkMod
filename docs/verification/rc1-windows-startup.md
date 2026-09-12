# Release Candidate 1.0 Windows startup correction

Date: September 11, 2026.

Issue: [26](https://github.com/bdubsub/BensFintasticSharkMod/issues/26).

The correction is based on approved `1.20.1` commit `ce62955b37bdda1c53ba035e927d6051624a8b4b`, on `envy/rc1-windows-startup`. It addresses diagnostic startup and release integrity without reopening a completed phase or changing the plan or goal.

## Cause and correction

The original candidate constructed `Path.of("unavailable:no_completed_capture")` in both diagnostic managers' empty stop summaries. Static initialization reached the server summary during mod construction. Windows rejected the colon with `InvalidPathException`, causing `ExceptionInInitializerError` even when debugging was disabled. The client summary contained the same latent failure.

Both empty summaries now carry a nullable, absent output path. Completed summaries still carry the capture's real path. Status already guards the empty summary and does not print an output path before a capture exists. No dependency, configuration, gameplay, resource, or save format changes are included.

## Verification

The following checks ran on headless Linux with Temurin Java `17.0.19+10`.

1. Added `DebugStopSummaryTest` before the correction. Both empty summary tests failed because the original code returned a fabricated filesystem path. Both completed summary tests passed. These tests exercise the production factories and constructors without a renderer or Minecraft client.
2. Applied the correction. All four focused tests passed. The absent path invariant detects the defect on Linux as well as Windows without emulating Windows paths or adding a filesystem dependency.
3. Ran `./gradlew :forge:Data --no-daemon` successfully, followed by a separate `./gradlew :forge:build --no-daemon`. All 49 JUnit tests passed with zero failures, errors, or skips. Separate invocations ensure generated resources are packaged after data generation.
4. `unzip -tqq` passed. The archive still has exactly 939 entries. No entry was added or removed. Every entry is byte identical to the original candidate except the server stop summary, the client diagnostic manager and its session and stop summary classes, and manifest build timestamps. The client session difference is caused by source line shifts. All wildlife classes, assets, data, and dependency metadata are unchanged. No invalid placeholder remains anywhere in the JAR.
5. Started the exact rebuilt candidate in an installed Forge `47.2.0` dedicated runtime with GeckoLib `4.4.7` and SmartBrainLib `1.14.2`. This was the production `forgeserver` launch target, not a development server. The disposable runtime used loopback only, online mode, and an explicitly verified `eula=true`.
6. Verified real console `bfs debug status` before capture, repeated `off`, a 20 tick automatic capture, completed status with a real filename, a manually stopped capture, active status, repeated stop, and server restart. Both captures contain a header, sample, and terminal record with zero dropped records and `incomplete=false`. No player or entity was required for this diagnostic lifecycle check, and it is not movement or visual evidence.
7. Both server processes stopped through the console and saved their worlds. The first fixture used incomplete flat world settings and emitted `No key layers in MapLike[{}]`. After correcting only that disposable fixture, the restart reached `Done (1.650s)!` with no error, fatal, or startup exception in its complete log. Final log SHA 256: `ca8474a161765b87808688523efda9c3ccd24fa388bcb20ef9c08cea421a65b2`.

No native Windows client or graphical acceptance was run. The focused regression proves the failing path construction is removed on both sides. A native Windows launch remains the platform confirmation step. Earlier gameplay approvals are not represented as a fresh visual test of this artifact.

The owner subsequently confirmed the delivered fix and explicitly approved its merge and completed plan cleanup on September 11, 2026. This supplies owner confirmation of the reported startup correction. It does not claim an additional maintainer controlled client run.

## Corrected artifact

The delivered filename remains `bfs-1.0-rc.1.jar`, with embedded version `1.0-rc.1` and Minecraft `1.20.1`. Size: 2,013,918 bytes.

SHA 256:

```text
ac509f173563582485bb041f08cf75c827200110e2a73fb6b5b214f80906872a
```

SHA 512:

```text
7f8786f21d7961310c15c2c4e7b3e69b1a69beeaff583ef3fee956b45fc091a76278c4e6208d7503fbb03caa7b4c55c30da10b9aecabd8521214bb0d43e1b4a0
```

The replaced candidate's SHA 256 was `94bfe2c8b45fc54505d42b63d026e203086959c34bb28325da3cf762c46578bd`. Historical phase evidence and checksums describe that original artifact and remain unchanged. They must not be used to identify the corrected JAR.

The corrected JAR was transferred to the owner's laptop Downloads directory, checked for archive integrity and matching SHA 256, then installed atomically over the exact original candidate. Personal game instances and worlds were untouched. No public release was performed.

After owner confirmation, [PR 27](https://github.com/bdubsub/BensFintasticSharkMod/pull/27) merged the correction into `1.20.1` at `33f849318b235ecded3012cdb625690096ad6795`. Both the postmerge build and CodeQL checks passed. The [current artifact bundle](artifacts/README.md) binds the unchanged delivered binary to that integration and preserves the original candidate's historical identities separately.

## Cleanup

Both owned server processes exited and the loopback port was closed. Disposable worlds, logs, captures, runtime configuration, copied JARs, scratch issue text, unit reports, local build output and local task caches were removed after artifact comparison and delivery. The shared production libraries were only linked into the disposable runtime, and the link was removed without deleting its target. Shared dependency caches and the original checkout were preserved. No laptop test runtime was created, and the staging upload was consumed by the atomic replacement. The implementation worktree remains for review and integration with the fix, tests, and this sanitized evidence record.
