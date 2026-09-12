# Phase 000 task 003 profiling record

Date: 2026-09-12

Status: incomplete. The retained ordinary pilot reproduced the timing regression and isolated a policy work lead, but it did not provide enough causal timing attribution to select a safe repair. No performance acceptance case or phase transition was attempted.

## Scope and environment

The profiling runs used `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix` on `node-1`, a headless Linux host with Linux `6.12.63+deb13-amd64`. The server target was Minecraft 1.20.1 with Forge 47.2.0, Java `Temurin-17.0.19+10`, GeckoLib 4.4.7, and SmartBrainLib 1.14.2. No client or player joined. The runner used seed `240024`, the fixed flat water fixture, natural spawning disabled, actual movement and predation enabled, and the prescribed 22 species.

The prebehavior baseline source was `692962f25b5b5eda65cc41fe1073c316668fd4c` with JAR SHA 256 `2fe67e0a82fb547239aacad4c3aeaf3723e6b54d1e5afc13f097633bad7170a2`. The diagnostic candidate source was signed commit `30cb6093356347b82a4a82be33782e16b36d0fb4` with JAR SHA 256 `91f924e77070a29cad058a0442095f1f540614d6e5f0b8a3ae922b2fd08c5bf1`. Both used the same observer binaries, with agent SHA 256 `2826b464beb1045e263ac09b459d5278b7c8d59c2180002426fe1188cbde38b2` and data SHA 256 `3fff6f4eb53cf3fcfa054eddbaae51c41acdfb85e4bd57911f0e0de4149dffc9`.

## Pilot runs

The baseline completed 1,000 measured ticks after a 400 tick warmup. The candidate ran for a retained 1,400 tick prefix before its owned server was stopped. The candidate prefix has no completion marker and is profiling evidence only. The first 1,000 ticks are compared to the baseline pilot below.

| Run | Warmup | Measured ticks | Median | p95 | p99 | Maximum |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| baseline normal | 400 | 1,000 | 3.553345 ms | 6.564093 ms | 12.749934 ms | 19.709645 ms |
| candidate normal prefix | 400 | 1,000 of 1,400 | 4.750028 ms | 8.556711 ms | 12.663346 ms | 55.322840 ms |

The candidate p95 is about 30.35 percent above the matched baseline pilot. This is a bounded reproduction, not the required 2,400 warmup and 36,000 tick acceptance window.

## Attribution observations

The candidate's measured counter window at tick 1,000 reported 3,025 `bounded_living_scans`, 163 feed actions, 19 escape actions, 19 social actions, 1,531 navigation calls, 2,162 sensor calls, and 24 deaths. The corresponding baseline window reported no species policy scan counter, 1,752 navigation calls, 2,289 sensor calls, and six deaths. By tick 1,400, the candidate had 4,263 bounded living scans, 237 feed actions, 26 escape actions, 24 social actions, 2,156 navigation calls, 3,030 sensor calls, and 32 deaths, including 29 American Lobster deaths caused by Common Stingrays.

The source path for `bounded_living` allocates a bounded result list for each policy scan and returns an immutable copy. A local experiment returned the bounded list directly. Its candidate JAR SHA 256 was `778843ce152d187f18ea4f5fd9c0be47e9a63c37b39c7e3360a9e7c797804838`. That experiment completed 1,000 ticks but measured a p95 of 11.107452 ms, compared with 8.556711 ms for the retained candidate prefix. The source experiment was reverted before evidence commit because the pilot did not demonstrate an improvement or establish causality.

These observations identify species policy scanning, route selection, and associated predation and replenishment churn as profiling leads. They do not prove which operation dominates tick time, and no product repair is claimed.

## Retained evidence and cleanup

The inspected sanitized pilot inputs had these SHA 256 identities.

| Input | SHA 256 |
| --- | --- |
| baseline timing CSV | `1b77706db03e17d70b38bb22af6e5d536c30741aa325cccd4235b5df3b5fb37c` |
| baseline census TSV | `3feafe030565633f901435b01e0348939f19a90c679abbfaeaf70b5897723fcb` |
| candidate timing CSV | `a8183b4bbf47989e62859cb7ed57d2c95ecdd6133aea676d22d9dce472cb5c44` |
| candidate census TSV | `4d7c80f4cc8ba389066796073fffbb3cc068aaa13418a19eb30402d2cbe6dff3` |
| experimental candidate timing CSV | `a8f3d281e231bba7c8c3d2c9d1e451714b3d88677eae9c476980bc935f46c72a` |
| experimental candidate census TSV | `8521ffbbd024c3e2708e08e421b7a48c821d5fcaa1e8619f6c38fbc886e6264f` |

The exact disposable server runtimes, worlds, logs, copied dependencies, and probe classes were removed after extraction. No owned process remained. The source worktree retains only the pre-existing uncommitted `build.gradle` line ending difference.

The next task 003 action is a scoped method timing attribution for the species policy and its route helpers, followed by a fresh candidate only if that attribution identifies a repair that preserves the fixture, predation, replenishment, and observer semantics.
