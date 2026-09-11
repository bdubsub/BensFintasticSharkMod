# Phase 004 task 010 runtime evidence

Date: September 11, 2026

Evidence binding note: the packaged runtime and client captures in this record
were produced before the Phase 004 source and test repairs. Their recorded
candidate hash is kept for provenance, but those runtime artifacts are
superseded and cannot close a current artifact-bound gate. The current
candidate hash is
`525b4b75cc139758a3721e859588940528cf00076a9132d1738b3bd85b9f3ce4`.

The current candidate was rebuilt with Java 17 after the source repairs and
is the only candidate used for the current runtime rebinding below. The
current packaged server runtime uses Minecraft 1.20.1, Forge 47.2.0, GeckoLib
4.4.7, SmartBrainLib 1.14.2, and `eula=true` at Tailscale endpoint
`100.76.164.109:25870`. Its disposable world is `phase004-current`, seed
`240024`, with the original natural green and red forms at `(104, 47, 187)`
and `(106, 18, 104)`.

The exact Forge candidate
`BensFintasticSharks-forge-1.20.1-0.24.jar` was deployed to the disposable
production Forge runtime on node 1. Its SHA 256 is
`a8744ae7c816371374f083f6237673fe77c22766e56665d1dfb89f6f82d8db56` and its
SHA 512 is
`20291fc7f3cfa84f3db8e0f333fab2ea18b15aa15a6388b633cc47ca23e0c24da818f5c8616d650780daf15a1caf01b19ebcb9b54a8b09391e145f8517297f34`.
The runtime used Java 17.0.19, Minecraft 1.20.1, Forge 47.2.0, GeckoLib
4.4.7, SmartBrainLib 1.14.2, and `eula=true`. The server endpoint was
Tailscale `100.76.164.109` on the bounded test ports 25864 through 25867.

## Dedicated server

The packaged server reached the Forge `Done` marker on every final seed run.
The runtime loaded the candidate and both required dependencies without a
registry, data-pack, or mod-loading failure. The relevant compressed log
identities are:

| Run | Runtime log | Decompressed log SHA 256 |
| --- | --- | --- |
| `phase004-run6` | `logs/2026-09-10-6.log.gz` | `c4d2089bff13819a6f6cc1e5d8e34434b806d8aee57bbcfeddfcb75032008228` |
| `phase004-run7` | `logs/2026-09-10-5.log.gz` | `1e68fbc0904ce77b2fb0331df51208cc850c96a9467b53f7747b500b83b1ee41` |
| `phase004-run8` | `logs/2026-09-10-3.log.gz` | `d81618f342f6dd24a4de0486a7120ee3ef941d895e1ed77886da1c27e9e2ad20` |
| `phase004-run9` | `logs/2026-09-10-1.log.gz` | `db11b6aab212005d38fbd7dcada13554e5ada8ec580ecda419bb7160f5697470` |

The final runs used only the headless dedicated server on node 1. The server
was stopped after each bounded probe, and the owned Java process and test port
were closed.

## Laptop client and visual approval

The exact candidate was also loaded by the disposable Prism profile on EnVy's
Linux laptop. The client rendered with the NVIDIA GeForce RTX 5090 Laptop GPU,
used Java 17, connected through Tailscale to the packaged server, and remained
muted for the entire owned process lifetime. The clean exhibit capture
`/tmp/bfsm-p004-final-client-clean.png` has SHA 256
`d660dfe719bbd052d51adc0121ec9506951130b555ebb076a7bf550d94b1503f`.
It shows the three natural algae forms in the glass exhibit without the
launcher overlay. EnVy approved continuation after reviewing the exhibit.
The client process and its audio stream were stopped after capture.

The exact-candidate reconnect capture used the same packaged JAR hash. Its
window capture SHA 256 is
`afe0cc4470cb6b83c4d3d24719ceaa7774506e6cf5abf2c5163529b9f2e74e15`, and the
matching client log SHA 256 is
`8543ef40ace17f42bad05ec829dedcc76d28c878d50f3844cb6001aa7e312508`.
The owned playback stream was verified as `Volume: 1.00 [MUTED]` and mapped to
the Minecraft process before the capture.

The final packaged runtime log is
`/tmp/bfsm-p004-prod-server-20260910-095H8c/logs/latest.log` with SHA 256
`826b1e0245053e8e1b8e7b875c67b30d65925dfdbb0bc82dc03aa0e25dbc9a9d`. It
contains the Forge `Done (13.472s)!` marker, two successful laptop joins, and
clean saving of all three dimensions on shutdown.

This proves packaged server loading, laptop transport, GPU rendering, clean
resource loading, and the owner visual exhibit approval for the superseded
runtime. The current candidate rebinding and the exact loop evidence are
recorded below. The current data-pack controls and owner approval still remain
open.

## Disposition

The dedicated packaged runtime and the superseded owner-approved exhibit pass.
The current candidate forward-load and backup restore pass. The current
data-pack controls and owner visual approval remain pending. No source or plan
change was made by this runtime probe.

## Data pack controls

On September 11, 2026, the same candidate was exercised on node 1 with Java
17.0.19 and the production Forge 47.2.0 server runtime. The original region
of `phase004-seed-g`, chunks `0,0` through `15,15`, retained 35
`algae_block`, 28 `large_green_algae`, and 48 `large_red_algae` placements.

A disabled pack replaced all three BFS Forge biome modifiers with
`forge:none`. Fresh chunks `32,32` through `35,35` generated 16 of 16
requested chunks with zero BFS algae, while the original region remained
unchanged. The server log reached the Forge `Done` marker and reported no
data pack load failure. A bounded override pack replaced the algae block
placed feature with the same finite search and a rarity chance of 10. Fresh
chunks `48,48` through `51,51` generated 16 of 16 requested chunks and
contained two algae blocks, two large green algae, and two large red algae.
This proves disable, preservation of existing blocks, and a valid bounded
override without command placement.

The bounded run log SHA 256 is
`8b898e80eaed2d780f72ff3a7210573ae370a43bf2d16df0e007cb2c05b5f3cc`.
The disabled and bounded test packs were disposable inputs and were removed
with their runtime after the comparison. These controls are retained as
superseded provenance because they predate the current candidate hash.

## Current candidate runtime rebinding

The current candidate reached the Forge `Done` marker and accepted the laptop
connection with no BFS registry, codec, data-pack, model, atlas, texture, or
render-layer failure. The laptop used the NVIDIA GeForce RTX 5090 Laptop GPU,
Java 17, and a disposable Prism profile. Its exact process stream was mapped
to the owned Minecraft process and reported muted for the complete capture
window. The current natural visual capture hashes are
`55c5ecbdfd9db9b4aa88563e038e8436f578a5f0eaa5bcce98ad17e2f4a8ad16` for
green and `54225d2e0f8641dd3a60abc3bf29e6bad959e4babede615da308310cbf6a7383`
for red. Their complete loop frame and crop hashes are in the current section
of `phase-004-task-004-loop-manifest.json`.

The current client log SHA 256 is
`4e28751c601a3750eaf0ee5bdefa40a7a0234784f48fb3948c0d1aa4c462dacc` and the
current packaged server log SHA 256 is
`84783846980bc846fe452b046153f859c15d5f961484a68392391eba8244d9c2`. The
server log includes the Forge `Done (13.341s)!` marker and the successful
`EnVyOnMyMind` join. The client log includes the modded server connection
marker.

The current server and client logs remain disposable runtime evidence until
the exact current server shutdown hash is recorded. The current visual gate is
awaiting EnVy approval. The previous data-pack hashes in this document remain
superseded provenance and do not close the current candidate gate.

## Current candidate data-pack controls

The current candidate was exercised again on node 1 with Java 17.0.19 and the
packaged Forge server. A disabled control pack replaced all three BFS Forge
algae biome modifiers with `forge:none`. Four by four target chunks `(32, 32)`
through `(35, 35)` were loaded and saved in `phase004-dp-disabled`; all 16
target chunks contained zero BFS algae blocks. The pack loaded with no data
pack failure. The final server log SHA 256 is
`f2ae0323f22be04d2d5afb824c22661fd142a51dd99b8106fb0d0b645fce426a`.

A second control pack replaced the registered `algae_block` placed feature
with the same finite placement chain and rarity chance `10`. The same 16
target chunks in `phase004-dp-override` loaded through the packaged server and
contained 7 `large_green_algae` blocks and 65 `large_red_algae` blocks, with no
unbounded scan or loading error. The override pack load and Forge `Done`
marker are in the final server log with SHA 256
`1788a9a95cc1d7a90dfb9d6536ce17d2d4d95ec6d69af5ee1fa1817d5e386830`.

Both packs were disposable inputs. Existing BFS blocks in the current natural
world were not touched by either control. The current data-pack disable and
bounded override rows now pass for the exact candidate.

## Current candidate 0.23 forward load and backup rehearsal

The recorded baseline commit `eb13b74` was built as a disposable Forge
1.20.1 jar with Java 17. Its baseline jar SHA 256 is
`971ade97d25de3715c8b14b1ade03ea69fcedf90131d0cf670bfea331eb95dc9`. A
separate headless server on node 1 created `phase004-legacy-023b` with that
baseline, force loaded the spawn chunk, placed a water volume, and saved a
named persistent Atlantic Cod with `NoAI:1b`. The saved world was then loaded
by the current candidate `525b4b75cc139758a3721e859588940528cf00076a9132d1738b3bd85b9f3ce4`
on Forge 47.2.0 at port 25875. The current server reached `Done (2.327s)!`,
found the named legacy Cod, and printed its retained UUID, custom name,
health, attributes, position, and persistence data. No migration or entity
load failure occurred. The final forward-load log SHA 256 is
`5ffbb91ec16046f4705dcaa01b64d8da718c49b40aa0ab3116584ecbf21350ec`.

For rollback rehearsal, the loaded save was copied before mutation. A gold
block marker was added to the working copy and saved, producing a different
working `level.dat` SHA `e9f47b1a368dc6c64ac04c5998af27208b221ced1482c6792dcdffe303835e25`.
The exact backup was restored, with matching pre-mutation and restored
`level.dat` SHA `4e2ac128e8fd2b7bc24b973d8cf67048af990914ffe3625637367e2438cc3b61`.
The restored current-candidate server reported both the retained legacy Cod
and the expected absence of the temporary marker, proving backup restore
before any downgrade. The same log contains the `backup_restored` marker.

The additive algae registry ID warnings emitted while loading this 0.23 save
are expected because the current candidate adds three new registry entries.
They were not registry, codec, data-pack, or entity load failures. The
disposable baseline and forward-load runtimes are stopped and remain only as
sanitized evidence until this record is committed.

## Current candidate fixed seed rerun

The exact current candidate was run in two additional fresh disposable
dedicated-server runtimes on node 1 with Java 17.0.19. Each runtime verified
`eula=true`, used seed `240024`, set `randomTickSpeed` to zero before loading
the target window, and force loaded only Overworld chunks `0,0` through
`15,15`. No client or direct block-placement fixture ran during either
sample. Both servers reached the Forge `Done` marker and were stopped after
all chunks were saved.

The current candidate produced identical BFS coordinate and form manifests in
both worlds, with 35 `algae_block`, 28 `large_green_algae`, and 48
`large_red_algae` placements across 92 chunks. The shared coordinate hash is
`6df2cdcb1e3fbff5d92ee1b56408a774e403cd8011541b521f72877e4e80289b`.
The complete algae-and-seagrass manifests are `phase-004-seed-run14.json` and
`phase-004-seed-run15.json`, with file hashes and full state hashes recorded in
the task 009 fixed-seed evidence. The custom algae state hash is identical in
both manifests. The vanilla seagrass control differs between the two fresh
worlds, so the strict seagrass-inclusive equality row remains open.
