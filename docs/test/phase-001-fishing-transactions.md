# Phase 001 fishing transaction regressions

This record covers fishing selection, live and item delivery, cancellation, reentry and interruption portions of `DEC-014`, `DEF-024-009`, and Phase 001 Tasks 010 through 012. It does not close the complete fishing or phase acceptance matrix. The first verification section preserves the earlier candidate; the later section binds the expanded tests to their changed source and artifact.

## Real execution boundary

`BfsFishingGameTests` creates an isolated server player and uses `FishingRodItem.use` to cast and reel a real `FishingHook`. A bounded fixture arms the bite timer and selects a known Atlantic Cod result at the fishing event boundary. This deliberately isolates delivery from random loot selection. It is not evidence for fishing probabilities or human mouse input.

Temporary event listeners inject nested delivery, rejected entity insertion, lower priority fishing cancellation, and hook removal during insertion. Every listener is removed in a `finally` block. Each fixture disposes of only its owned hook, spawned entities and test player, and unregisters the player's advancement listeners.

| Test | Protected invariant |
| --- | --- |
| `nestedFishInsertionCommitsOneCatch` | An entity insertion callback cannot recursively deliver a second fish or success reward. |
| `rejectedFishInsertionCannotRetryOrAwardCatch` | A rejected insertion terminates the attempt without retry, item fallback, XP, statistic or advancement. |
| `cancelledFishingCannotCreateLiveRewards` | Cancellation before the delivery listener prevents live rewards while preserving the real rod cost. |
| `committedFishingCannotReplayAfterHookRemoval` | A completed hook cannot produce another fish or reward. |
| `removedHookDuringInsertionCannotAwardCatch` | If insertion invalidates the hook, the new fish is removed and success rewards are withheld. |

Successful delivery must create exactly one fish and one XP orb, increment the catch statistic once, grant only the matching Cod advancement, damage the rod once, and clear the hook. Failed delivery must create no accepted fish or immediate item and no success reward. Tests check world membership rather than treating a constructed but rejected entity as a delivered catch.

## Implementation

`FishingCatchDelivery` reserves the attempt before calling any entity construction or insertion boundary. The existing completed marker is still recognized. Reservation survives failure so nested callbacks cannot retry an uncommitted insertion. The server player, current hook, owner, dimension, finite location and loaded chunk are checked before insertion and again before rewards.

The fishing listener runs at `LOWEST` priority and receives cancelled events so it can retain the cancellation and consume the attempt without delivery. This covers preceding event listeners, not an unsupported guarantee about another listener that deliberately changes cancellation after delivery at the same priority.

This change does not tune swimming, replace authored animation, change fishing weights, or alter the reel impulse. The complete four-mode, creation-source, restart/progress, population, packaged multiplayer and client presentation gates remain separate.

The delivery invariant is explicit in `FishingCatchDelivery`: the live `Mob`
and the fallback `ItemEntity` both receive the same `reelImpulse` before they
are inserted into the level. Its horizontal components point from the hook to
the angler at one tenth of the separation, and its vertical component uses the
same one tenth term plus the vanilla square-root distance lift. This gives a
live catch the same initial short pull as an item catch. The impulse is bounded
external displacement; after insertion the fish resumes its ordinary AI and
the player must kill it for normal loot.

## Reproduction procedure

Use Java 17, Minecraft 1.20.1, Forge 47.2.0, GeckoLib 4.4.7 and SmartBrainLib 1.14.2. The tests run on node-1 through the genuinely server-only GameTest entry point, with no client, renderer or display. Inspect the Gradle task graph before execution. Development GameTests are not packaged multiplayer verification.

Use a new, uniquely owned runtime for each complete suite. Create and read back `eula=true` and bind the current source state and configuration before launch. Do not reuse a prior GameTest world as a clean-world result.

```bash
JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 \
./gradlew :forge:test :forge:GameTestServer :forge:build --no-daemon \
  -PbfsGameTestRunDir=/absolute/path/to/new/disposable/runtime
```

Inspect individual failures, the total discovered test count, the required-test summary, unit-test XML and the Gradle exit code. Preserve decisive sanitized results, then confirm process exit and remove only owned runtime and test-build output. No connected player is required.

## Verification on September 8, 2026

The tested working source was based on Phase 001 commit `04dc2deb264d0d314380d4bf479a7a0d23019b37`, with the pending exclusive fishing, replacement and transaction changes present. It was not a clean committed revision. Both `common/src` and `forge/src` matched the isolated build copy byte for byte. The SHA-256 of the sorted, unique existing source-file checksum manifest, excluding generated `.cache` entries, was `2ed5f28ca9eef27c37b33b7472e4b5d645953ca2abc1221690959344f7a5e1a6`.

All runs used Java 17 on node-1 with the dedicated GameTest entry point. No graphical client or connected owner was used.

| Run | Result and interpretation |
| --- | --- |
| Before the transaction repair | The 51-test suite failed the three new nested insertion, rejected insertion and preceding cancellation cases. The replay control passed. This reproduced the delivery defects before the fix. |
| After the repair, reusing the first GameTest world | All four transaction cases passed, but three existing vanilla replacement fixtures failed. This run was not accepted as a full-suite pass. |
| Fresh disposable world, including hook invalidation during insertion | All 52 required GameTests passed. All 24 unit tests passed with no failures, errors or skips. `:forge:build` completed successfully in 2 minutes 49 seconds. |
| Packaged archive integrity | `unzip -tqq` passed for the resulting Forge JAR. Production Forge startup and matching-client acceptance were not repeated for this changed candidate and remain unverified. |

The intermediate replacement failures occurred in `naturalVanillaFishReplacementHonorsPopulationCap`, `vanillaFishReplacementHonorsOneForOneAndModes`, and `vanillaFishReplacementUsesActualCreationSources`. The fresh-world pass distinguishes that run from the reused-world failure; it does not independently prove why the reused world failed. No assertion was weakened, and a reused world must not be presented as a fresh fixture.

The final log recorded `All 52 required tests passed :)`, followed by world saving, GameTest server shutdown and `BUILD SUCCESSFUL in 2m 49s`. The successful final suite used `/tmp/bfsm-p001-transaction-check.u1zvBW/runtime-repeat` with loopback binding, port `25926`, `online-mode=true`, `enable-rcon=false` and verified `eula=true`. Source and build output were isolated in `/tmp/bfsm-p001-production-package.5EtI7c`.

| Evidence | SHA-256 |
| --- | --- |
| Before-repair run log | `f8ad2b265d136aeb78a2c853a583caf16411d5d014fcba2d109f7325b3b99fc7` |
| Reused-world run log | `92fb3c880e50a65dc3583243d98b025ca89eff78c7397dd149b0c9b140c572d6` |
| Successful fresh-world run log | `6ef19c1b3a1134737af35a7c636411f6a8467705d3902192c8d13863c47e4b3a` |
| `BensFintasticSharks-forge-1.20.1-0.24.jar` | `3b06af1690269f5f18637e70ce04fcb346c6d42f361c93ec534cf2ad88bed4ef` |

JAR SHA-512: `246afc229c8d41d7043d93abdc43dd7e3add7c82be8400e532a6bfe178fc73e272f8a195fd03929d7c2874569052ef6b5bff23cb48d31921a721856ed886c022`.

The JAR retains the current intermediate `0.24` build designation and is not the approved `1.0-rc.1` release artifact. These results close only the stated transaction regressions. The complete delivery-mode, source, restart, population, advancement, client and integration gates remain open.

Cleanup verified that no process retained any of the three owned runtime directories as its working directory. After the evidence checks, `/tmp/bfsm-p001-production-package.5EtI7c`, `/tmp/bfsm-p001-production-server.lPBAcp`, and `/tmp/bfsm-p001-transaction-check.u1zvBW` were removed, and their absence was verified. This removed the disposable source/build copy, intermediate JARs, worlds, raw logs and audit intake. The source worktrees, shared dependency cache, unrelated processes and personal instances were preserved. The hashes above identify the discarded test evidence; they are not links to retained raw logs or downloadable artifacts.

### Historical player feedback

The earlier production Forge candidate had SHA-256 `68ba7e05ed0f31fe149bd12ad4f1408ae533c95cc1c5d0c6f79afba9fa34e7f3`. Its server log recorded the owner's Salmon catch advancement at 19:04:24 on September 8, 2026, and the owner reported that the fishing rod worked. This preserves that limited feedback, not approval of untested delivery modes or the subsequent transaction repair. The server was stopped cleanly at 19:08:45.

At the time of this record, the Phase 001 plan defined the remaining acceptance and [issue 15](https://github.com/bdubsub/BensFintasticSharkMod/issues/15) tracked the combined fishing and replacement gate. That issue has since closed. The old planning files were later removed for an owner requested restart; this historical test record is preserved.

## Obstruction safe live catch reel on September 9, 2026

The live fish delivery path now owns a short server side return trajectory after
the catch is accepted. It follows a smooth eased arc, temporarily disables
gravity and collision so a wall cannot stop the fish, points the entity along
the current trajectory, and restores its original physics at a clear landing
point beneath or immediately beside the angler. The same path is used for item
delivery, so both configured modes have identical obstruction handling.

The dedicated GameTest `liveFishingReelArcsOverObstructionToAngler` passed in
the `bfs_fishing_arc` batch. Its fixture used the real rod and hook, placed a
two block stone wall between the hook and the angler, asserted a visible rise
over the wall, asserted final distance below one block from the angler's feet,
and asserted gravity and collision were restored after arrival. The first run
exposed only a fixture limitation because the synthetic test player was not in
the player list. The production path now retains the actual server player
object for the bounded reel path and removes it on disconnect or server stop.

The verification command was
`JAVA_HOME=/usr/lib/jvm/temurin-17-jdk-amd64 ./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain -PbfsGameTestRunDir=<unique-runtime>`.
The disposable GameTest runtimes were removed after the batch result and no
GameTest or Gradle process remained. This closes the server side obstruction
regression only. Laptop visual approval of the arc and the remaining Phase 001
integration gates are still required.

## Expanded fishing matrix on September 8, 2026

The next bounded suite added six mode and selection tests in `BfsFishingModeGameTests` and four item transaction cases in `BfsFishingGameTests`. These execute the real server rod, hook, fishing table, registered loot modifier and insertion paths. They do not substitute for physical client input or visual acceptance.

The initial 58-test run reproduced four failures. The selection policy could replace Tropical Fish and Pufferfish even though only vanilla Cod and Salmon were eligible for Atlantic selection. Both item delivery modes also missed the fish statistic for Atlantic items. The failing cases were `fishingSelectionPreservesUnmappedSpecies`, `loadedFishingTablePreservesCategoriesAndSpecies`, `replacementItemCatchesMatchRealRodResults`, and `mixedItemCatchesMatchRealRodResults`.

The selection policy now preserves Tropical Fish, Pufferfish, existing Atlantic items, junk and treasure. Both live and item delivery use the guarded insertion transaction and award success only after accepted insertion and hook revalidation. A weak hook context records the actual fishing rod and delivery mode at loot selection, preserving the selected hand's tool context rather than assuming the main hand.

### Coverage and limits

| Verification | Result and boundary |
| --- | --- |
| Pure selection and boundary checks | Noneligible species and ordinary items retain their item, count and data. Eligible Cod and Salmon use the documented exclusive selection rule. The 25 percent opportunity applies to an eligible Cod or Salmon outcome, not every fishing category. |
| Loaded fishing table | 8,192 paired seeded samples compare the underlying and modified fishing tables across replacement on/off and luck 0/3. Every result remains one stack. The tests preserve noneligible fish, junk, treasure and their item data, verify both Atlantic species are reachable, and prohibit vanilla Cod and Salmon when replacement is on. |
| Four delivery modes | 256 actual server cast/reel cycles cover replacement on/off crossed with live/item delivery. The fixture arms the bite timer but does not replace the selected loot. It alternates main and offhand and includes enchanted rods. Assertions compare each observed result with world delivery, reel impulse, rod cost, hook cleanup, statistic, XP and matching advancement. This is controlled server execution, not human fishing input. |
| Transaction failures | Nine live/item cases cover nested insertion, rejected insertion, prior cancellation, invalidation during insertion and completed-hook replay. No failed insertion may yield a fallback item or a success reward. |
| Unit tests and data generation | All 24 unit tests passed with zero failures, errors or skips. `:forge:Data` passed, and generated resources matched the working source excluding generator cache metadata. |
| Fresh complete GameTest suite and build | All 62 required GameTests passed. `:forge:build` completed successfully in 3 minutes 14 seconds. The final log records `All 62 required tests passed :)` and `BUILD SUCCESSFUL in 3m 14s`. |
| Packaged archive | `unzip -tqq` passed. The JAR contains the replacement modifier and current fishing implementation, without the two superseded additive fishing modifier resources. Installed production Forge startup, matching-client handshake and visual acceptance were not repeated for this candidate. |

The suite's population fixture uses controlled persistent, no-AI spawn waves. It does not establish unrestricted ambient spawning, natural despawn equilibrium or ecosystem recovery. The bucket source fixture that invokes `checkExtraContent` does not prove the complete player item-use, water placement or container-consumption path. Restart and configuration persistence, remaining source failure and permission cases, complete population acceptance, bounded fishing diagnostics, and required client and integration gates remain open. Aggregate test success must not be used to close those separate requirements.

### Source and runtime binding

The source remained based on `04dc2deb264d0d314380d4bf479a7a0d23019b37` with pending Phase 001 changes, not a clean committed revision. Both source trees matched the isolated build copy byte for byte. The sorted source-file checksum manifest produced by `rg --files common/src forge/src -g '!**/.cache/**' -0 | sort -z | xargs -0 sha256sum | sha256sum` had SHA-256 `ad3be96347ecb957e3fe49e16fb23795c7498c73a484cfe60f5b1d41a6817b85`.

Execution used Java 17 and the dedicated Forge GameTest entry point on node-1. The configured graph launched no client or renderer. The uniquely owned root was `/tmp/bfsm-p001-fishing-matrix.cyA58C`, with isolated `project`, `runtime-before` and `runtime-after` directories. The test runtimes bound to loopback port `25927`, with `online-mode=true`, `enable-rcon=false` and verified `eula=true`. No owner connection was required.

| Evidence | SHA-256 |
| --- | --- |
| Initial 58-test failure log | `106c207ef8375367109e0175ca15475473112ca2b4cb1209395ce1fbe1edfe47` |
| Successful 62-test suite and build log | `3c5d3ca4fd65a1586c25cab3fd59991e6b9ebadd67f8a08dfa4ab810e7aa81d2` |
| Unit and data generation log | `272ae52414d258b19804cd3eebea1ecb14495fca97d1f442ba0d29b5e0865463` |
| Intermediate Forge JAR | `f03b0c1f1f4b79243e96a20209a5efd15a49e297384508adfe14ac803c81fd46` |

JAR SHA-512: `629a51674390c5ba497b3d52e0975f6ed32190623a9cba5985a691c01d91c407962374bd17e282682cebae005b9c4fc69bd0950d42650cfe3dd9c9942f6b39ef`.

The artifact keeps the intermediate `0.24` designation. Neither the historical fishing feedback nor this server-only suite constitutes approval of changed client presentation or the final `1.0-rc.1` artifact.

After the test process exited successfully and the final evidence consumers completed, inspection found no process using the owned test root as its working directory. `/tmp/bfsm-p001-fishing-matrix.cyA58C` was removed and its absence verified. Cleanup removed its isolated source/build copy, intermediate JAR, worlds, raw logs and temporary plan validation intake. Only the sanitized record and hashes above were retained. Source worktrees, shared dependencies, unrelated runtimes and personal instances were not removed.

## Fishing diagnostics and production readiness on September 8, 2026

The next change added bounded, correlated fishing observations through the existing `all` and `advancement` capture categories. See the [operator procedure](debug-diagnostics.md#fishing-capture). The diagnostic path records actual delivery and reward results and observes rod cleanup after the real retrieval call returns. It does not manufacture a successful delivery from a selected loot result.

`fishingDiagnosticsObserveRealRodWithoutChangingRewards` exercises an unobserved control, four observed delivery modes, offhand tool identity and a preceding cancellation. A known Cod selection isolates diagnostics and delivery; the existing mode suite still supplies the real table-selection coverage. The normal capture contained 16 records covering five distinct hook attempts, four commits and one cancellation, with no dropped records. Strict offline analysis returned `complete` and checked matching advancement progress, accepted XP, delivery mode, finite impulse, observed rod damage and cleared hooks. Angler UUIDs, names and arbitrary rod NBT were absent from the capture.

The same fixture exercises 33 immediate catches against the 32 pending-settlement limit. Fishing continues while diagnostics stop with `fishing_settlement_limit`, one dropped observation and an incomplete marker. Stopping a separate capture before the next tick also reports incomplete settlement. The strict analyzer rejected both negative captures, reporting 32 missing settlements for the bounded overflow case and one missing settlement for the early stop. Neither negative result is a gameplay failure or a passing evidence packet.

The first 63-test suite failed the existing Tiger target-loss fixture before its lifecycle assertions because that setup demanded curiosity after exactly one tick. The fixture now lets the edible item tick in water before spawning the shark, then uses the existing bounded natural-acquisition helper. It retains the original 100-tick total deadline, real curiosity entry, five-tick prey preemption, item preservation and ten-tick target/item-loss assertions. No Tiger production behavior or timeout was changed. This removes the unsupported one-tick setup assumption; it does not claim a separate biological or movement defect was diagnosed from that setup failure.

The corrected suite passed all 63 required GameTests. After the capture-limit and early-stop controls were included, a second fresh complete suite also passed all 63 required GameTests and built successfully in 3 minutes 14 seconds. All 24 Java unit tests and all 24 diagnostic parser unit tests passed. `git diff --check` passed. The changed source contains no resource-provider changes beyond the already validated fishing generation recorded above.

### Packaged runtime

The final intermediate JAR was copied to a new installed production Forge server on node-1, not the Gradle development server. Execution used Java 17.0.19, Minecraft 1.20.1, Forge 47.2.0, raw GeckoLib 4.4.7 and raw SmartBrainLib 1.14.2. The server used the existing installed Forge libraries through a read-only-use directory link, a new disposable world, verified `eula=true`, loopback port `25930`, `online-mode=true` and disabled RCON. No client, renderer or connected owner was used.

The server reached `Done (12.666s)!`. Its generated common configuration had `replace_vanilla_mobs=true`, `fish_entities=true` and `disable_vanilla_aquatic_spawns=false`. The actual console executed `bfs debug on advancement 20` and `bfs debug status`; the capture expired cleanly at 20 ticks. Its header reported `dedicated_server`, the loaded versions above, GeckoLib network protocol `1`, the tested working-source binding and the exact candidate hash. The zero-target capture proves startup and command readiness only, not entity behavior, a client handshake, or visual acceptance. The console then executed `stop`, saved every dimension and exited with code zero. Port `25930` was no longer listening.

The working source was still based on `04dc2deb264d0d314380d4bf479a7a0d23019b37`. Both source trees matched the isolated build copy. The sorted source checksum manifest described above had SHA-256 `89e3ee42c7df58818e6ddb50c852d9a7ed08a017713b04a94bf6ed07a0c1c3ed`. The runtime root was `/tmp/bfsm-p001-fishing-debug.IifPSq`; the final GameTest world was `runtime-final` on loopback port `25929`, and the packaged server was `production` on port `25930`.

| Evidence | SHA-256 |
| --- | --- |
| Initial suite with the Tiger setup failure | `cbd9cc5e1fbf83c2d773b22f0f04a4c861d54a2c6850a0a8f850ba984400b3ff` |
| First corrected 63-test suite | `5af385241bf7aaede154ccff1fdf56d90ea8bc7b0c518f16850dd640fc8cefca` |
| Final 63-test suite and build | `4cb97c6314ac0cad3ae54f047598534a55f75d58c8227fcc53bea2da088aaf0d` |
| Complete five-attempt fishing capture | `535ec6736735d287b29f98c2c51b9dca4ca42c0710cacba0baa1b54690791b36` |
| Production Forge console log | `a1d5a382c5d1f99490e1d3bc756a9a1f89b36a693cf1e1a88289183105193f19` |
| Production readiness capture | `fc5bb7e6077ab11bc33af1476c41c335c96f42f8e768c98e77c342aed7e67246` |
| Intermediate Forge JAR | `2b89527fe21ca22875c97a8f227d575faf6f7470db28c1582db14dbb21bd0450` |
| Official GeckoLib dependency | `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0` |
| Official SmartBrainLib dependency | `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b` |

JAR SHA-512: `0ea455298ed9a429993b30ab0e6698412c2b36caca6331dabf32f11db4a45940299803bad16f71949cbaf0929f9ef4384b09bc268820a650e0a34fc4f3a645e9`. Archive integrity passed. This remains an intermediate `0.24` artifact, not the final `1.0-rc.1` deliverable.

The extended diagnostics and packaged readiness recorded here did not close restart persistence, complete source/permission behavior, unrestricted ambient population recovery, final player/client acceptance, phase review or integration. Those were separate gates in the historical Phase 001 plan. Removing that plan for an owner requested restart does not change the scope of this evidence or reopen Phase 000.

Cleanup verified that no process retained the owned test root as its working directory. After the final evidence consumers completed, `/tmp/bfsm-p001-fishing-debug.IifPSq` was removed and its absence verified. This removed the disposable source/build copy, three GameTest worlds, packaged server world, copied JARs, captures, raw logs and parser reports. Only the sanitized record above remains. The shared installed Forge libraries, dependency cache, source worktrees and unrelated runtimes were preserved.

## Laptop launch recovery on September 9, 2026

The first owned Prism client launch failed before Forge or the mod loaded
because the Hyprland Wayland GLFW backend could not provide a window
position. The decisive error was
`GLFW error before init: [0x1000C]Wayland: The platform does not provide the window position`.
This was not a fishing or server regression.

The instance was relaunched through the laptop's existing XWayland display
`:1` with `GLFW_PLATFORM=x11` and no `WAYLAND_DISPLAY`. It initialized on the
RTX 5090, connected to the exact installed Forge server at
`100.76.164.109:25857`, and retained the profile's zero master-volume
setting. The Java playback stream was matched to the owned Minecraft process
and individually muted before any acceptance action.

The bounded post-connection `/bfs debug on all 120` capture completed with
`5517` accepted records, zero dropped records and `incomplete=false`. No
fishing event occurred in that capture, so it is environment and diagnostic
readiness evidence only. Physical fishing acceptance remains unclosed until
the owner performs the cast and reel against the connected candidate.
