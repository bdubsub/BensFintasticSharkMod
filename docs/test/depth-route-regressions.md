# Depth Route Regressions

## Scope and status

This is Phase 002 implementation evidence for the reported fish turning, incomplete depth arrival and sharks stuck near terrain. It does not reopen Phases 000 or 001, replace the saved goal, approve the full movement profile, or close visual acceptance. The tracked defect is [issue 17](https://github.com/bdubsub/BensFintasticSharkMod/issues/17).

The initial dedicated GameTest reproduction failed for both Atlantic Cod and Tiger Shark. Each reached approximately the destination's height but stopped more than ten blocks away horizontally. The previous short movement checks accepted a quarter block of height gain, so their passing result did not establish arrival.

## Ownership and recovery

`PitchSwimmingMoveControl` owns the selected approach, desired pitch, propulsion carry and controller progress deadline. `PitchSwimmingNavigation` retains vanilla water pathfinding while allowing that controller deadline to govern an active slow clearance or approach segment. Explicit navigation cancellation still clears the active route. The four shark classes with amphibious node evaluators retain those evaluators.

`AquaticRoute` retains the destination separately from its optional clearance point. Reaching the clearance point selects the original destination. Matching its height alone never completes the route. A blocked request receives a retry delay without excluding a different goal. Fish social policy no longer overwrites movement targets while navigation is following another route.

Progress means reducing the best distance to the current waypoint by at least 0.1 blocks. Sliding sideways, circling or returning toward an already visited distance does not reset the deadline. A waypoint handoff starts a new progress window. The prior displacement based check let small vertical slides along a wall postpone recovery without useful route closure.

The water travel step separates previously owned propulsion from subsequent velocity changes. Minecraft 1.20.1 clears components smaller than 0.003 blocks per tick before travel. The controller accounts for that known rounding when reconstructing its carry; it must not classify the negated carry as an opposing external impulse. Actual contact still clips propulsion and must make the progress deadline expire.

## Fixtures and assertions

Use Forge 47.2.0, Minecraft 1.20.1, Java 17 and the checked in wrapper on the headless compute host. The GameTest task starts no client or renderer. Use one uniquely named disposable run directory, verify its `eula.txt` contains `eula=true`, and retain it only for the bounded suite.

Every independent full suite starts with a fresh world. Do not reuse an earlier run directory unless the scenario explicitly tests restart persistence. Reusing a world retains entities outside the new fixture boundaries and can contaminate subsequent population and predator observations.

```bash
JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 ./gradlew :forge:test --no-daemon
JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 ./gradlew :forge:GameTestServer -PbfsGameTestRunDir=/absolute/disposable/runtime --no-daemon
```

The route fixtures provide source water, known floor geometry and controlled destinations. They remove autonomous goal selection to isolate navigation and movement, but do not teleport the animal along the tested path or inject movement velocity. The dedicated pursuit and curiosity tests retain their ordinary behavior owners.

* `codDepthRouteReachesPositionAndAcceptsNextGoal` and `tigerDepthRouteReachesPositionAndAcceptsNextGoal` require arrival within 0.75 blocks in all three dimensions, followed by a second destination with a different depth. Repeated requests preserve the original destination during each leg.
* `blockedFishRouteReleasesSteeringForAnotherGoal` places a solid obstruction across the first request, requires a finite blocked result, then requests a reachable destination on the same side of the obstruction.
* The existing Cod, ascending Tiger and descending Tiger fixtures now require complete destination arrival. Their deadline allows the retained vertical ceiling and pitch ramp to complete the whole route. It is not increased in response to a stall. The obsolete requirement to remain at one fixed heading is replaced by bounded smooth turning and no complete accumulated heading revolution, alongside distance closure.
* Unit tests distinguish height equality from arrival, enforce the finite clearance handoff, and cover short steep changes for animals whose clearance exceeds the requested depth change.

The full suite also exposed fixture interference. Bite observations now require the tested shark to own the damage, and the bounded combat volume excludes unrelated living actors. A failed Blacktip sample identified another shark as the attacker. Fish replacement fixtures run in separate batches and clear completed fish within the production counting radius before starting. Otherwise an unrelated fish can consume a natural spawn slot even when the small assertion volume is empty. The one fish cap, no vanilla leak and real creation source assertions remain intact.

The actual creation source fixture waits two setup ticks after preparing water across chunk boundaries. The assertions on each spawned replacement remain immediate. Before this setup window was added, the query missed a new fish at a chunk boundary even with replacement enabled and available population capacity.

## Candidate verification

On September 9, 2026, the Java 17 run of `:forge:test :forge:GameTestServer :forge:build` passed all 40 unit tests, all 73 required dedicated server GameTests, and the Forge build. No unit tests were skipped. This was a fresh disposable GameTest world on node 1, with Forge 47.2.0, GeckoLib 4.4.7 and SmartBrainLib 1.14.2. The artifact passed ZIP integrity inspection and contains the new route controller and unchanged supplied fish animation resources.

Candidate artifact: `BensFintasticSharks-forge-1.20.1-0.24.jar`, 1,995,947 bytes. The historical Phase 002 metadata remains unchanged pending Phase 005.

* SHA 256: `dd8cdffac12b455dccac767f7d7ccf2123d2620638fe14fdec6ccf8caa429ffa`.
* SHA 512: `58a3d2eeb005a1eee0aae11aec292e7b4bcdb18501de164f4c0cd293dd8ccfb31285560d9c922ae1f0d0c26f813727e059422c896443078665dae892cf1ff190`.

A fresh repeat exposed a wall sliding recovery failure in this artifact, so it was not deployed for visual acceptance. The progress check was corrected afterward and requires a new artifact and complete verification. The initial pass is historical evidence, not closure. Packaged multiplayer and owner visual acceptance remain unverified. Phase 002 is open.

The corrected progress candidate subsequently passed all 40 unit tests, all 73 required dedicated server GameTests and the Forge build in another fresh world. Its artifact is 1,995,993 bytes, with SHA 256 `2ca7fc521a19ebde95419eb918968ac9f1e2be747e2a5028d2bdb9514331d7d3` and SHA 512 `074bd5c2a02b7d6f0e92b3b6513f75a91468335afa2ef8c4fbe90837c41cdabe2292783ff34b718e18af2dd6bf95f8fc0a3c847f19e5e6965e9c3fd076ceac37`. This supersedes the first candidate for further runtime verification.

The source revision is `ab3e4f4b2c5a0ee6d2757fe4b301c46a9ccd9a5b`. A second fresh world repeat passed the movement regressions but failed one of the 73 required tests, `mixedLiveCatchesMatchRealRodResults`, with `Live mode must deliver one matching fish and no immediate item.` The failure occurred in the immediate delivery assertion, before the fixture ticks the caught animal. Its cause is not established. Do not classify it as a movement regression, harmless fixture noise, or a passing repeat without additional evidence. The next diagnostic step is to record the selected item, expected entity type, observed insertions and accepted delivery entities for the failing attempt, then rerun the affected fishing batch. Human rod timing is not part of this assertion.

## Packaged multiplayer observation

The corrected candidate joined successfully on September 9, 2026, using the installed production Forge 47.2.0 dedicated runtime on the headless compute host and an isolated Java 17 Prism instance on the NVIDIA laptop. Both sides loaded the candidate SHA 256 above and matching dependency files. No development server was used for this connection. Authentication remained enabled on the existing private endpoint. The client renderer reported NVIDIA GeForce RTX 5090 Laptop GPU. Master audio was zero before launch, and the playback stream belonging to the exact client process was verified muted afterward. The client remained on workspace 3. Operator access, creative mode, peaceful difficulty and immediate respawn were verified for observation.

Runtime identities were `bfsm-p002-packaged-r11` on the compute host and `bfsm-p002-client-20260909` on the laptop. The world was `bfs-p002-mp-r9`; its existing shoreline was retained for continuity, not represented as a fresh GameTest fixture. Matching files were verified with these hashes:

* GeckoLib 4.4.7 SHA 256: `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0`.
* SmartBrainLib 1.14.2 SHA 256: `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b`.
* Common configuration SHA 256: `43efc7f2adba5e5638dc52eff420038c89fcae84e755588842ef3b78c9906c8f`.

The source, artifact, movement profile, common configuration and bundled datapack fingerprints were supplied to both runtimes. The configuration fingerprint identifies the common file, not every possible configuration surface. The datapack fingerprint identifies the bundled mod artifact; the test world's external datapack directory was empty.

A 600 tick natural Salmon capture completed with 1,045 accepted records and zero dropped records. One of two targets left the level before the end. Both showed depth and pitch changes, but blocked route states also occurred near the shoreline. This is observation, not an all routes arrival pass.

An initial mixed Cod, Tiger Shark and Oceanic Whitetip fixture was unsuitable for independent swimming approval because the Whitetip grabbed the invulnerable Cod. The server confirmed the Cod in the shark's passenger data. Do not attribute the resulting carried trajectory or inherited movement controller to free Cod swimming. Those three test actors were removed and replaced for a separate cruise observation. The replacement sharks began with a hunting cooldown, while movement AI remained enabled. This changes the observation scope to cruise movement and does not verify combat or natural prey selection.

The replacement cruise capture, `27fabcb8-a944-4f36-940b-4f4f7b68ce85`, ran for 600 ticks near the observer's shoreline position. It completed with 1,801 accepted records, zero drops and three remaining targets. All three reported no combat target. Cod pitch ranged from approximately 12.4 degrees upward to 4.1 degrees downward, with 1.35 blocks of observed depth range. Tiger Shark and Whitetip showed approximately 0.46 and 0.40 blocks of depth range. No single observed route attempt accumulated a full heading revolution, but cancellation states and incomplete destinations remained in this autonomous sample. These measurements do not establish the requested maximum tilt, terrain recovery coverage, complete route behavior, or visual acceptance. Owner approval of the actual presentation remains pending.

The completed headless test runtimes and local deployment scratch were removed after preserving these results. The isolated multiplayer server, laptop client and exact client audio watcher remain active only for the pending bounded visual review. Their teardown is still pending; the complete multiplayer workflow is not cleanup complete.

## Translating pitch and level exit repair

The later owner video and server trace exposed another failure in source revision `73678d936f8fb61362fad13e21f922676bfacd4f`. Pitch error and remaining pitch margin independently reduced scalar travel while the nose continued turning. Arrival or cancellation then entered `WAIT` without completing a level exit. One captured Cod held a 28.410141 degree upward pose for 3.2 seconds while moving only 0.024956 blocks horizontally, without terrain collision. Increasing the class vertical ceiling did not repair this route control failure.

The repaired approach uses a parametric cubic curve with the current forward tangent and a level terminal tangent. It calculates turning demand without dividing by horizontal velocity or taking a tangent at a vertical pole. Curvature remains diagnostic and shapes the bounded pitch rate, but it no longer drives scalar propulsion toward zero. A class vertical travel floor keeps fish and sharks translating while they acquire a steep pose, and the travel integrator still applies the exact 20 percent fish or 25 percent shark projection ceiling. The clearance run reserves the level exit as well as depth gain, mirrors the finite entry leg when the preferred heading is obstructed, and preserves the exact navigation endpoint when a water path has no usable vertical node. Pitch integrates during powered travel, braking against the stopping distance before the hard limits. Route termination retains its reason and may finish a forward translating level exit after checking its water and collision clearance. The movement controller continues to own heading during that exit. It does not rotate a blocked actor against terrain.

Seven new dedicated GameTests cover Cod, Salmon and Tiger recovery from a tilted arrival, plus Cod, Salmon, Tiger and Oceanic forward progress during depth entry. The progress fixtures require more than one block of forward displacement and more than ten degrees of body pitch within 201 sampled ticks, retain the pitch rate and acceleration ceilings, and reject a sideways heading sweep on the clear forward route. The level exit fixtures require less than one degree of residual pitch and more than one block of forward displacement. Sharks in these controlled locomotion fixtures receive a hunt cooldown so nearby test actors cannot replace the requested route. This is motion isolation, not natural predation or rendering evidence.

The Oceanic invalidation fixture now waits for a real water state update before arming its grab, within its unchanged timeout. An earlier run attempted to arm the grab while that cached state was still false. Another run reused a populated test world and failed the population observation; that result was rejected and the final suite used a fresh world. No population assertion or production cap was weakened.

The final Java 17 run on node 1 passed all 43 unit tests with no skips, all 80 required dedicated server GameTests and the Forge build. ZIP integrity and packaged controller checks passed. The verified candidate is `BensFintasticSharks-forge-1.20.1-0.24.jar`, 2,002,524 bytes.

* SHA 256: `44f197c0026ba6891bee1baa114b730abc2c00d162d5c00ba4af9e8484c835c9`.
* SHA 512: `e88845bb09a7a300f07ab7415329141a211c5ba5897eb4970ba1d4a96ce9603dea8e40c1f4928b2357d05e7f3ddfdf01963f1bd5ac96eb5940669fec67c129fc`.

The 25 percent shark and 20 percent fish ceilings, accepted level speed and yaw, authored animations, external velocity accounting and original progress timeout are unchanged. These results establish the focused server regression, not owner approval of the rendered candidate or complete species calibration.

Source revision `f609304f6c8db203103dcf991bc7dcf018dca7a9` was installed into the existing packaged Forge runtime `bfsm-p002-ratio-server-cFhldJ` on node 1 and the isolated Prism instance `bfsm-p002-client-20260909` on the laptop. The candidate and both dependency hashes matched across hosts. The authenticated player joined successfully. The exact client window was on workspace 3, its graphics process used the NVIDIA RTX 5090 Laptop GPU, master audio was zero before launch, and its own playback stream was verified muted afterward. Operator access, creative mode, peaceful difficulty and immediate respawn remained enabled.

The packaged Cod capture `1375a7c2-3c55-4fb5-908c-8d8082cc412f` completed 600 ticks with 1,800 movement samples, three remaining targets, zero drops and no parser errors or warnings. Per Cod, cumulative horizontal travel ranged from 2.49 to 6.64 blocks and depth range from 1.24 to 2.70 blocks. No sample met the diagnostic frozen pose condition of pitch magnitude over ten degrees, pitch change below 0.00001 degrees and actual total travel below 0.003 blocks per tick. Two Cod entered translating pitch exits. This bounded autonomous observation is not a full route completion, speed calibration or visual approval verdict. The strengthened settling regression now requires more than one block of horizontal travel during a level exit, so a future candidate cannot pass with a barely moving nose-down or nose-up correction.

The separate shark capture `f547d0b6-32e1-4e29-8294-acfd70115662` completed 600 ticks and 1,200 samples without drops or parser errors. Tiger traveled 6.64 blocks horizontally with 0.59 blocks of depth range; Oceanic traveled 14.75 blocks horizontally with 0.50 blocks of depth range. Both entered translating exits and neither met the frozen pose condition above. Tiger also reported blocked paths in the bounded aquarium. These observations do not approve every ambient route or prove unobstructed maximum depth speed. The two preview sharks are persistent, invulnerable and temporarily satiated to isolate cruise appearance from predation.

The GameTest worlds were removed after results were recorded. The existing multiplayer preview remains active for owner review, with a bounded application audio guard. That preview's final client, server and guard teardown remains pending while it is in use.

## Limits and next acceptance

Accumulated heading is a useful regression detector, not a complete implementation of the plan's path topology checks. Swept rendered body geometry, measured anatomical length, spatial pitch curvature, acceleration and jerk calibration, explicit external-force attribution, mixed-species behavior and the final presentation matrix remain separate Phase 002 gates. Do not infer those passes from destination arrival.

After headless regression and packaging pass, match the exact candidate and dependencies on the packaged Forge server and isolated laptop client. Use server movement captures first, then request owner approval of visible body tilt, smooth transitions and animations. Do not substitute a development server, screenshots or a passing headless test for that approval.

Stop test-owned processes, preserve only the final sanitized result and required candidate identity, and remove disposable worlds, logs and runtime output after their last consumer. Preserve personal instances, existing worlds, shared dependency caches and unrelated server processes.
