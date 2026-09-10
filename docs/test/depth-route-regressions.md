# Depth Route Regressions

## Scope and status

This is Phase 002 implementation evidence for the reported fish turning, incomplete depth arrival and sharks stuck near terrain. It does not reopen Phases 000 or 001, replace the saved goal, approve the full movement profile, or close visual acceptance. The tracked defect is [issue 17](https://github.com/bdubsub/BensFintasticSharkMod/issues/17).

The initial dedicated GameTest reproduction failed for both Atlantic Cod and Tiger Shark. Each reached approximately the destination's height but stopped more than ten blocks away horizontally. The previous short movement checks accepted a quarter block of height gain, so their passing result did not establish arrival.

## Ownership and recovery

`PitchSwimmingMoveControl` owns the selected approach, desired pitch, propulsion carry and controller progress deadline. `PitchSwimmingNavigation` retains vanilla water pathfinding while allowing that controller deadline to govern an active slow clearance or approach segment. Explicit navigation cancellation still clears the active route. The four shark classes with amphibious node evaluators retain those evaluators.

`AquaticRoute` retains the destination separately from its optional clearance point. Reaching the clearance point selects the original destination. Matching its height alone never completes the route. A blocked request receives a retry delay without excluding a different goal. Fish social policy no longer overwrites movement targets while navigation is following another route.

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

Packaged multiplayer and owner visual acceptance remain unverified for this artifact. This result does not close Phase 002 or authorize its integration.

## Limits and next acceptance

Accumulated heading is a useful regression detector, not a complete implementation of the plan's path topology checks. Swept rendered body geometry, measured anatomical length, spatial pitch curvature, acceleration and jerk calibration, explicit external-force attribution, mixed-species behavior and the final presentation matrix remain separate Phase 002 gates. Do not infer those passes from destination arrival.

After headless regression and packaging pass, match the exact candidate and dependencies on the packaged Forge server and isolated laptop client. Use server movement captures first, then request owner approval of visible body tilt, smooth transitions and animations. Do not substitute a development server, screenshots or a passing headless test for that approval.

Stop test-owned processes, preserve only the final sanitized result and required candidate identity, and remove disposable worlds, logs and runtime output after their last consumer. Preserve personal instances, existing worlds, shared dependency caches and unrelated server processes.
