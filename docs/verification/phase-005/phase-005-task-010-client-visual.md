# Phase 005 task 010 client visual and interaction evidence

Date: September 11, 2026

This gate used the exact Phase 005 release candidate in a graphics-capable
Prism Launcher client on EnVy’s Linux laptop. The server remained a disposable
production Forge runtime on node 1. No client or renderer was started on node
1.

## Candidate and runtime binding

| field | value |
| --- | --- |
| source candidate | commit `a939aa2` and its Phase 005 build evidence |
| artifact | `BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar` |
| artifact size | `2013933` bytes |
| artifact SHA-256 | `94bfe2c8b45fc54505d42b63d026e203086959c34bb28325da3cf762c46578bd` |
| artifact SHA-512 | `8d9edc14bf81e11fd8dc5e905b9aa1201e84c7d7bf303c53cee2f9a57fee516cd99076066b6df92d14d116f6199ddacf7deb03ee4237b1f37ccd369ae8bb5675` |
| client profile | `bfsm-p001-manual-20260909` |
| client loader | Minecraft `1.20.1`, Forge `47.2.0` |
| client Java | Microsoft Java `17.0.15` |
| renderer | NVIDIA GeForce RTX 5090 Laptop GPU, OpenGL `4.6.0`, driver `610.57.04` |
| server endpoint | node 1, Tailscale `100.76.164.109:25576` |
| server state | packaged Forge server reached `Done` and recorded `EnVyOnMyMind joined the game` |
| client jar hash | matched the candidate SHA-256 and SHA-512 above |

The client log records `Connected to a modded server`, the matching endpoint,
Forge `47.2.0`, and the RTX 5090 renderer. The server log records the same
player UUID and join. The client master volume was set to `0.0` before launch.
No playback stream attributable to the owned client was present during the
check, so audio behavior is not claimed as acceptance evidence.

## Visual and interaction matrix

The owner reviewed the live client on workspace 2 after connection to the
packaged server. The review request covered the ocean fish and sharks,
GeckoLib animations, smooth body pitch and vertical travel without sideways
spinning or circular ascent, Oceanic Whitetip presentation, armor views,
advancement icons and copy, algae visuals, fishing delivery, and advancement
toast behavior. EnVy replied `i approve continue` with no visual defect or
retest request. This is the owner approval binding for the graphics-capable
matrix.

The approval is limited to the exact candidate, client profile, server
endpoint, loader, Java runtime, and renderer recorded above. It does not waive
the separate multiplayer, population, fresh-profile advancement, fixed-seed,
artifact-security, documentation, review, merge, tag, or release endpoint
gates.

## Result

The graphics-capable client load and owner visual approval gate passed. The
remaining Phase 005 work continues with `P005-TASK-011`, using the same exact
candidate binding and a newly recorded disposable runtime protocol where
required.
