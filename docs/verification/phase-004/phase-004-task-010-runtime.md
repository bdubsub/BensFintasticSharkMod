# Phase 004 task 010 runtime evidence

Date: September 11, 2026

Evidence binding note: the packaged runtime and client captures in this record
were produced before the Phase 004 source and test repairs. Their recorded
candidate hash is kept for provenance, but those runtime artifacts are
superseded and cannot close a current artifact-bound gate. The current
candidate hash is
`227c037b14decfe7e4b44bbdfd5ef777e695ba0ceaef9fb8c2581d68dc159309`.

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
resource loading, and the owner visual exhibit approval. It does not by itself
prove a complete 40 tick green or 36 tick red animation loop, 0.23 save
forward-load, disabled or bounded data-pack behavior, navigation soak, or
rollback rehearsal. Those rows remain open until their exact runtime evidence
is recorded.

## Disposition

The dedicated packaged runtime and the owner-approved laptop exhibit pass.
The compatibility, data-pack, navigation, complete-loop, and rollback rows
remain pending. No source or plan change was made by this runtime probe.

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
with their runtime after the comparison. The full 0.23 save forward-load,
navigation soak, complete client loops, and rollback rehearsal remain open.
