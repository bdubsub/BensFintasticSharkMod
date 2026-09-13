# Phase 001 client visual evidence

Date: 2026-09-13

Status: partial residual client gate. The matching production Forge client joined the disposable
dedicated server with the phase candidate and remained silent. Settings command feedback and
rendered open surface and shallow ocean presentation were observed. The complete client envelope
matrix for every named terrain fixture at scales 0.25, default, and 2 is not claimed complete.
Independent review, pull request, merge, default branch verification, and the signed phase tag
also remain open.

## Candidate and hosts

The candidate source revision was
`74bc03bd77ea71c7cdc78fbd2bd063ff8db3b29e`. The Forge archive was
`forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar` with these hashes.

```text
sha256 4a0ce9f0acc41f037444252471eff1390cf89beadf4b435f99edb8b8a715f4ff
sha512 984c3c90134bc5c3fe51c21f1ffb397bec12e583eef30faac9708a465dcc8b4f10dbbb33066d05a01d1ea35313608674ccfc0a5c8888fd60687eebdf52ad1
```

The server ran on node 1 from the isolated phase worktree runtime
`.test-runs/bfs2-p001-visual-server-20260913`, using installed production Forge 47.2.0 and
Java 17, port `25642`, world `bfs2_p001_visual_20260913`, `eula=true`, and
`online-mode=false`. The copied dependency hashes were SmartBrainLib 1.14.2
`3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b` and GeckoLib 4.4.7
`6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0`.

The client ran on the authorized EnVyOnMyMind laptop with an NVIDIA GeForce RTX 5090 Laptop GPU
and the live Hyprland session. The owned Minecraft window was Java PID `1575054`, class
`Minecraft* 1.20.1`, title `Minecraft Forge* 1.20.1 - Multiplayer (3rd-party Server)`. The
client joined the intended server and the server console recorded the player join.

## Command and rendering observations

The client entered these commands through the Minecraft chat input after the server was ready.

```text
/bfs debug settings set great_white_shark horizontal_speed 6.25
/bfs debug settings get great_white_shark
/bfs debug settings reset great_white_shark horizontal_speed
/bfs debug settings get great_white_shark
```

The set response reported one species and one field at revision 2. The get response reported
`horizontal_speed` as `6.250 blocks_per_second [session] supported, owned by the BFS species
settings adapter`. After reset, the value returned to its server configuration baseline. The
settings output also exposed detection radius, disengage distance, action timeout, memory ticks,
and scan radius with units, source, support, and capability reason.

The client visual record showed the rendered ocean scene and shark presentation on an open surface
and in shallow water with the candidate resource set. Temporary screenshot hashes are retained as
sanitized evidence without retaining the screenshots in the source tree.

```text
open surface, /tmp/bfs2-p001-shark7.png,
317eabd034d1ab595314ba0558c2b9608be6086d50ae3a515f532b014a63b5de
shallow ocean, /tmp/bfs2-p001-ocean.png,
77099b4b49427ae1903f0602cb87d519b5073018b9a054511880aa3819968fbf
settings get and reset, /tmp/bfs2-p001-settings-valid.png,
25e65b5a74d6d6c98c5823805a7f3535004ce65e863fedc379640d6a7a9680f3
settings set, /tmp/bfs2-p001-settings-set-valid.png,
baa63a1c9c469413926dd238868a1d78549310c42734e5649cf921d77934fc42
```

The screenshot files are disposable evidence paths and are not part of the tracked artifact. The
full client claim still needs targeted observations for the open surface, shallow seabed, slope,
wall, ceiling, flowing water, and waterlogged obstacle at the three required scales, with snout,
tail, belly, dorsal fin, and interpolated pitch evidence.

## Audio and cleanup

Before launch, the isolated client master category was zero. During the test, the exact owned
Java playback stream for PID `1575054` reported `Mute: yes` through the laptop audio server. No
system sink or unrelated application was muted. The client and server were stopped during cleanup,
and the temporary candidate, backup, screenshot, world, logs, and runtime paths were removed after
their hashes and observations were recorded. The user owned instance files were restored and
matched their pre test hashes for `instance.cfg`, `options.txt`, `servers.dat`, and the original
BFS jar.
