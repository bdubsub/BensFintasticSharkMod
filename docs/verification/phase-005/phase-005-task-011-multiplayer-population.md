# Phase 005 Task 011 multiplayer, population, advancement, and fixed seed evidence

This record binds Task 011 to source revision `a939aa2` and the exact Forge
candidate JAR `BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar`.

| Binding | Value |
|---|---|
| Minecraft and loader | Minecraft 1.20.1, Forge 47.2.0 |
| Java | 17.0.19, Eclipse Adoptium |
| Source revision | `a939aa2` |
| Candidate JAR SHA 256 | `94bfe2c8b45fc54505d42b63d026e203086959c34bb28325da3cf762c46578bd` |
| Candidate JAR SHA 512 | `8d9edc14bf81e11fd8dc5e905b9aa1201e84c7d7bf303c53cee2f9a57fee516cd99076066b6df92d14d116f6199ddacf7deb03ee4237b1f37ccd369ae8bb5675` |
| Server host | node 1, dedicated Forge runtime |
| Laptop host | `envision`, Prism Launcher profile `bfsm-p001-manual-20260909` |
| Private endpoint | `100.76.164.109:25577` |
| Runtime configuration binding | `p005-task011-20260911` |
| Data pack binding | `phase-005-candidate` |

## Multiplayer lifecycle and reconnect

The production packaged server used the candidate JAR and matching GeckoLib
4.4.7 and SmartBrainLib 1.14.2 dependencies. The laptop launched the same
candidate with Forge 47.2.0 and Java 17, then connected through the private
endpoint. The server recorded the first player join at `07:56:07` and a
controlled disconnect at `07:57:06`. The client was relaunched without changing
the profile or artifact, and the server recorded the second join at `07:57:37`.
The laptop log records the matching quick play endpoint and `Connected to a
modded server` at `07:57:37`.

The server log also records clean `save-all flush` and shutdown after the
remaining server-side checks. The server log SHA 256 is
`c745227b3aae8671ff1a78785745acda77dc7cea2bab42e7837d5428ba5025a5`.
The matching laptop `latest.log` SHA 256 is
`d4ac8838028cc1a4a9cfb27a460af7b1f7a60bd380d64afb0911a063930143c2`.

## Population soak

The exact command was `bfs debug on population 24000` on the dedicated server.
The capture header binds source `a939aa2`, the candidate JAR hash above,
`phase-005-dec006`, the configuration fingerprint, and the data pack
fingerprint. It selected 26 eligible BFS entities, selected all 26, excluded
none, and wrote the final `end` record after 24,000 ticks.

There were 21 population samples at offsets 0 through 24,000 in 1,200 tick
steps. Every sample reported zero vanilla Cod and zero vanilla Salmon while
the replacement population remained BFS entities. The final record reports
`recordsAccepted=730`, `recordsDropped=0`, `incomplete=false`,
`reason=duration_elapsed`, and `remainingTargets=0`. The capture contains 220
Atlantic Cod spawn records, 219 Atlantic Salmon records, 71 Common Octopus
records, and 172 Harbor Seal records during the soak.

The JSONL capture SHA 256 is
`cc8a3f472fdd5ca49bf1933ac3bb8f9f0e14da8f6bc788ff33eec1a5ef57d5df`.

## Fresh profile advancement

The fresh profile initially contained only the BFS root advancement. With the
real candidate runtime, the server started `bfs debug on advancement 600` and
summoned each of the 22 registered BFS species near the connected player. The
normal player proximity trigger, not an advancement grant command, completed
all 22 `marine_biologist` criteria. The eight shark criteria also completed
through their normal encounter triggers, and the server announced both
`Marine Biologist` and `Sharks Galore!`.

After `save-all flush`, the player advancement file persisted `marine_biologist`
and `sharks_galore` with `done=true` and every expected criterion present. The
advancement JSONL capture SHA 256 is
`1bc73eeefb5094c25c4ca20d0657cff001c85a9f98f190212270e7bc3b6e790b`.

## Fixed seed world generation

Seed `240024` was run in fresh production Forge runtimes with
`randomTickSpeed=0`. Each run used the documented one chunk buffer from chunk
`-1,-1` through `16,16`, sent row-major single chunk `forceload` tickets at the
documented interval, saved the world, and shut down cleanly. The target sample
was Overworld chunks `0,0` through `15,15`.

The first concurrent buffered pair was rejected because its custom placement
multiset differed by one placement. It remains a failed probe and is not used
as acceptance evidence. Two sequential isolated runs were then completed on
the exact candidate. Their custom algae placements were byte-equivalent at the
accepted normalized manifest level.

| Run | Algae block | Large green | Large red | Combined custom | Max custom per chunk | Custom coordinate and state hash |
|---|---:|---:|---:|---:|---:|---|
| `fixed-e` | 31 | 23 | 40 | 94 | 3 | `4e236194329d68f7a279cf31bf38a80d2d8348f1eb1e98796cf10a4718a1b1ca` |
| `fixed-f` | 31 | 23 | 40 | 94 | 3 | `4e236194329d68f7a279cf31bf38a80d2d8348f1eb1e98796cf10a4718a1b1ca` |

The full placement hashes differ because vanilla seagrass placement is a
control and varies between fresh production world serializations. The custom
algae coordinate and state multiset is identical, all three forms are present,
the maximum custom count per chunk is three, and no invalid placement was
observed. The final run logs have SHA 256 values
`94680870ba8838322d17c41438f94de062ce66dac418d1c0aac5ecc320ebc31a` and
`d0694b54218a29e8444ee58633dcf8b8877bb83ddad5cf95bc36b1b08f4437c3`.

The worldgen source and generated resource paths are unchanged between the
Phase 004 tagged revision `a919528` and candidate source `a939aa2`. This
preserves the Phase 004 exact-candidate worldgen control while rebinding the
current runtime result to this candidate.

## Disposition

Task 011 multiplayer join, controlled reconnect, 24,000 tick population
capture, fresh profile advancement completion, and fixed-seed custom algae
repeat are recorded against one candidate JAR. The failed concurrent probe is
retained as a rejected procedure result, not relabeled as a pass. Temporary
runtimes and logs remain only until the Task 011 evidence commit and review
consume them, then are removed by exact-path cleanup.
