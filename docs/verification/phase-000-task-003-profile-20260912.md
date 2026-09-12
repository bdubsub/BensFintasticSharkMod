# Phase 000 task 003 profiling record

Date: 2026-09-12

Status: incomplete. The retained ordinary pilot reproduced the timing regression, and scoped method timing selected a behavior preserving repair. The required four case performance matrix and phase transition remain open.

## Scope and environment

The profiling runs used `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix` on `node-1`, a headless Linux host with Linux `6.12.63+deb13-amd64`. The server target was Minecraft 1.20.1 with Forge 47.2.0, Java `Temurin-17.0.19+10`, GeckoLib 4.4.7, and SmartBrainLib 1.14.2. No client or player joined. The runner used seed `240024`, the fixed flat water fixture, natural spawning disabled, actual movement and predation enabled, and the prescribed 22 species.

The prebehavior baseline source was `692962f25b5b5eda65cc41fe1073c316668fd4c` with JAR SHA 256 `2fe67e0a82fb547239aacad4c3aeaf3723e6b54d1e5afc13f097633bad7170a2`. The diagnostic candidate source was signed commit `30cb6093356347b82a4a82be33782e16b36d0fb4` with JAR SHA 256 `91f924e77070a29cad058a0442095f1f540614d6e5f0b8a3ae922b2fd08c5bf1`. Both used the same observer binaries, with agent SHA 256 `2826b464beb1045e263ac09b459d5278b7c8d59c2180002426fe1188cbde38b2` and data SHA 256 `3fff6f4eb53cf3fcfa054eddbaae51c41acdfb85e4bd57911f0e0de4149dffc9`.

For the paired post repair pilot, the baseline was rebuilt from the same source and had JAR SHA 256 `f8bccc1e129563808d68ddde378068d76ac8ec975ee29ecd1484caa0e57ef605`. The candidate was the rebuilt artifact from signed commit `54cffe0a7590aa398bd73db8b99651229f1bb0e2` recorded above. The pilot used the production GeckoLib SHA 256 `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0`, SmartBrainLib SHA 256 `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b`, and current observer binaries.

## Pilot runs

The baseline completed 1,000 measured ticks after a 400 tick warmup. The candidate ran for a retained 1,400 tick prefix before its owned server was stopped. The candidate prefix has no completion marker and is profiling evidence only. The first 1,000 ticks are compared to the baseline pilot below.

| Run | Warmup | Measured ticks | Median | p95 | p99 | Maximum |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| baseline normal | 400 | 1,000 | 3.553345 ms | 6.564093 ms | 12.749934 ms | 19.709645 ms |
| candidate normal prefix | 400 | 1,000 of 1,400 | 4.750028 ms | 8.556711 ms | 12.663346 ms | 55.322840 ms |

The candidate p95 is about 30.35 percent above the matched baseline pilot. This is a bounded reproduction, not the required 2,400 warmup and 36,000 tick acceptance window.

## Causal attribution and repair

The 600 tick scoped timing run used the pre repair candidate and the same 200 tick warmup, seed, fixture, 22 species and installed dependencies. It attributed 52.474540 ms to `path_reachability`, 55.488279 ms to `escape_route` and 100.139698 ms to `policy_tick` across the measured window. `claimRoute` already rejects a new route when an existing walk target is present, but the policy built the escape path before that rejection. The repair in signed commit `54cffe0a7590aa398bd73db8b99651229f1bb0e2` moves the existing walk target guard before escape path construction. It leaves route selection, threat policy, predation, replenishment, targets and observer semantics unchanged.

The rebuilt installed candidate has JAR SHA 256 `12dc39c2f7e042a149d9a2cc71fff51d7fa35677292b6dcf1136a53a819c4857` and SHA 512 `f465cbfa35a9d3699df77afc46994e376eaeece073ae5afb2704b7cf165b22a616094285fe6ef0d57bc387472d94c2b95ad73a23c121408050924774f2862cbe`. A bounded post repair timing run completed 600 measured ticks after a 200 tick warmup. Its p50 was 3.296426 ms, p95 8.026109 ms, p99 14.378608 ms and maximum 18.535846 ms. The scoped totals were `path_reachability=5.120411 ms`, `escape_route=5.432568 ms` and `policy_tick=50.820908 ms`. This pilot is attribution evidence only, not the acceptance matrix.

The post repair full server suite completed all 87 registered tests twice. The tiger bite and new debug lifecycle tests passed in both runs. Each run exposed one different pre existing flaky fixture failure, `tigerbitelandsonceandrecoversaftertargetloss` in the first run and `sharkspotterandatlanticadvancementsrequiretheirgameplaysignals` in the second. The suite therefore remains unclaimed as a complete pass. Forge tests passed 52, debug parser tests passed 18 and performance analysis tests passed 21.

A fresh paired 600 tick pilot with the rebuilt prebehavior baseline and the post repair candidate used the same installed dependencies, seed, fixture, 200 tick warmup and 22 species. The baseline measured p50 3.577636 ms, p95 14.138435 ms, p99 18.482764 ms and maximum 21.127771 ms. The post repair candidate measured p50 3.296426 ms, p95 8.026109 ms, p99 14.378608 ms and maximum 18.535846 ms. This is a positive causal pilot, but its short window and differing natural deaths do not satisfy the required 36,000 tick paired acceptance cases.

## Attribution observations

The candidate's measured counter window at tick 1,000 reported 3,025 `bounded_living_scans`, 163 feed actions, 19 escape actions, 19 social actions, 1,531 navigation calls, 2,162 sensor calls, and 24 deaths. The corresponding baseline window reported no species policy scan counter, 1,752 navigation calls, 2,289 sensor calls, and six deaths. By tick 1,400, the candidate had 4,263 bounded living scans, 237 feed actions, 26 escape actions, 24 social actions, 2,156 navigation calls, 3,030 sensor calls, and 32 deaths, including 29 American Lobster deaths caused by Common Stingrays.

The source path for `bounded_living` allocates a bounded result list for each policy scan and returns an immutable copy. A local experiment returned the bounded list directly. Its candidate JAR SHA 256 was `778843ce152d187f18ea4f5fd9c0be47e9a63c37b39c7e3360a9e7c797804838`. That experiment completed 1,000 ticks but measured a p95 of 11.107452 ms, compared with 8.556711 ms for the retained candidate prefix. The source experiment was reverted before evidence commit because the pilot did not demonstrate an improvement or establish causality.

The method timers establish route construction as the causal work lead and the post repair pilot shows the measured route totals falling by about an order of magnitude. Full scale and behavior acceptance still require the installed four case matrix.

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
| post repair timing CSV | `bb72ca44539f177c506a0c702b5ff82ad4f132a6d7a6796b2284971fac4c9544` |
| post repair census TSV | `0ed05beba26bf2a131ad70c845d14e776cc9139942551aab5f61f155a4d49930` |
| post repair completion record | `5c9b55758716a6b9be0d5e606346396c55aaf43c77cb67694d22cfd6de049b1d` |
| rebuilt baseline timing CSV | `4f87f4b9a43ce6d845547f950745c6e85ccfa07de6236e0f2fdb2184f4ff57b2` |
| rebuilt baseline census TSV | `174b4c7b5676cc401c455931648ae48e4692c111b860c70a084b7b333257890b` |
| rebuilt baseline completion record | `8003aa2e38959688aae6111a717391357ec21d3a7dd32a7a36262bff951aa891` |

The exact disposable server runtimes, worlds, logs, copied dependencies, and probe classes were removed after extraction. No owned process remained. The source worktree retains only the pre-existing uncommitted `build.gradle` line ending difference.

The next task 003 action is the full installed four case matrix with diagnostics disabled for acceptance, followed by comparison and behavior review. No phase transition is allowed until those cases and the remaining release gates pass.
