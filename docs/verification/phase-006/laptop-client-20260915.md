# Phase 006 laptop client evidence

The Phase 006 candidate was exercised on the `envision` laptop against the matching disposable Forge server at `100.76.164.109:25906`.

| Field | Value |
| --- | --- |
| Source revision | `a2213a7` |
| Candidate artifact SHA 256 | `e0f2a3ccd6c9a6e8da12e6d243dee82c7eac00383cf49eade5251e7b01be3214` |
| Forge | `47.2.0` |
| Minecraft | `1.20.1` |
| Java | `17.0.15` |
| Renderer | NVIDIA GeForce RTX 5090 Laptop GPU, OpenGL 4.6 |
| Window | `Minecraft Forge* 1.20.1 - Multiplayer (3rd-party Server)` |
| Final client PID | `3452152` |
| Final Hyprland stable ID | `18000445` |
| Server endpoint | `100.76.164.109:25906` |

The disposable Prism profile contained the candidate artifact and the matching GeckoLib 4.4.7 SHA 256 `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0` and SmartBrainLib 1.14.2 SHA 256 `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b`. The profile master volume was set to `0.0` before launch. The final Java playback stream was correlated to PID `3452152`; PulseAudio reported `Mute: yes` and both channels at `0%`.

The client joined the server, entered the ocean fixture, and displayed the complete dive suit with the `Dive oxygen: 6000 / 6000` HUD. Focused X11 input sent forward movement and jump input. The server position changed from `[-191.5, 44.0, 144.5]` to `[-191.5, 39.40120800362789, 152.32620816614286]`, proving the input reached the authoritative player and the seabed travel path remained active. `F3+T` completed a resource reload and the client log recorded `Reloaded resource packs` without a reload failure.

The bounded client capture was `efe99f66-c9f1-4553-80e5-ee48a0bcf627`. It contained 7,115 records, 5,683 movement records, 1,427 presentation records, 13 tracked entities, zero dropped records, and a complete terminal record. The raw JSONL SHA 256 was `c3f99b029c874334065565cfa0827d74f179f9a364ce5244b111aa8452a42bfd`. The analyzer returned `complete` for `bfs2-p006-laptop-client-20260915` and `BFS2-REQ-017`; the sanitized verdict SHA 256 was `7a299bed061097534f57b9af08c6fb01dfe75e46518aa45bc72811cc3419e595` and the summary SHA 256 was `23b1e3819c6ec4947f252007e8469defa1826dbb52e84fd319ac4d0eddad4f1d`.

The client diagnostic command reported a complete stop with 7,114 accepted records, zero dropped records, and no incomplete reason. The first client process was stopped and relaunched from the same disposable profile. The server recorded the disconnect and a new authenticated join for `EnVyOnMyMind` at the same endpoint, proving the reconnect path with the candidate and full suit state.

The client gate used the following targeted frame hashes. The joined underwater frame was `818f79c21f8fb59a6e787c9160b9d46bcca3c2ebf188b99da8325b3db641a2ea`, the full suit and night vision frame was `a5b9b63023f9cbff07e5b252bd181fc4834fb383e9b04ec5cccb11c4d768c31b`, and the post reload frame was `550c56a44576f2460fdf5e38b36b78f7ef856454d1cad0462e2aed398e77dc6f`.

The client, launcher, audio stream, server, copied candidate, temporary Prism root, screenshots, logs, world, and capture were stopped or removed after their final consumer. Each exact disposable path was checked absent. The personal Prism profile and shared dependency stores were not modified.

This closes the Phase 006 laptop client gate for the source bound candidate. It does not publish a release.
