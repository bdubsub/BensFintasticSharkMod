# Phase 001 client visual evidence

Date: 2026-09-13

Status: expanded residual client gate. The matching production Forge client joined the disposable
dedicated server with the current phase candidate and remained silent. Fresh client captures cover
all named terrain fixtures at the default scale and the open surface at scales 0.25, 1, and 2. The
remaining client claim is the complete terrain matrix at all three scales and direct mesh clearance
assertion for every body and fin part. Independent review, pull request, merge, default branch
verification, and the signed phase tag also remain open.

## Candidate and hosts

The candidate source revision was
`b9d50b9799fba75d0962d515d6789ae53e084cf7`. The Forge archive was
`forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar` with these hashes.

```text
sha256 95939fb61125aa74dea45668d2d0c311c65afbc2ae8a761cd78bcb703d69d633
sha512 fe25fc08aefdbcb4bfd1b26bc106d9e2e4f6ea68c67b616057cefd6df67e0a3b0c54be4215bbd79f67470572502afd8dbce6545b05655489ff68bd11dc26de14
```

The server ran on node 1 from the isolated phase worktree runtime
`.test-runs/bfs2-p001-visual-matrix-20260913`, using installed production Forge 47.2.0 and Java
17, port `25643`, world `bfs2_p001_visual_matrix_20260913`, `eula=true`, and
`online-mode=false`. The copied dependency hashes were SmartBrainLib 1.14.2
`3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b` and GeckoLib 4.4.7
`6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0`.

The client ran on the authorized EnVyOnMyMind laptop with an NVIDIA GeForce RTX 5090 Laptop GPU
and the live Hyprland session. The exact client process was Java PID `1756089`, window class
`Minecraft* 1.20.1`, and title `Minecraft Forge* 1.20.1 - Multiplayer (3rd-party Server)`. The
client joined the intended server and the server console recorded the player join.

## Command feedback

The client entered these commands through Minecraft chat after the server was ready.

```text
/bfs debug settings set great_white_shark horizontal_speed 6.25
/bfs debug settings get great_white_shark
/bfs debug settings reset great_white_shark horizontal_speed
/bfs debug settings get great_white_shark
```

The set response reported one species and one field at revision 12. The get response reported
`horizontal_speed` as `6.250 blocks_per_second [session] supported, owned by the BFS species
settings adapter`. After reset, revision 13 returned `horizontal_speed` to `5 blocks_per_second
[server_config] supported, owned by the BFS species settings adapter`. The settings output also
exposed detection radius, disengage distance, action timeout, memory ticks, and scan radius with
units, source, support, and capability reason.

The command screenshot was a temporary file with SHA 256
`2d632cbca56720089dc5c4fa7c78c019b39c324be91dfbcad029e1b2c39922ed`.

## Client capture matrix

Each capture used `/bfs debug client on` and `/bfs debug client off`. Every header selected one
Great White Shark target and bound source revision `b9d50b9799fba75d0962d515d6789ae53e084cf7` and
artifact SHA 256 `95939fb61125aa74dea45668d2d0c311c65afbc2ae8a761cd78bcb703d69d633`. Every terminal
record reported `recordsDropped=0` and `incomplete=false`.

| fixture | scale | capture | records | presentation | interpolated pitch | JSONL SHA 256 |
| --- | ---: | --- | ---: | ---: | ---: | --- |
| open surface | 0.25 | `2817600d-37d3-4bb4-9a68-6c9476e1729d` | 697 | 140 | 19.6875 | `62eb5bdfaf8044048572d640eab5b9fffd2e4619f5cb7b210997dfd8b50da620` |
| open surface | 1 | `22cd7fd5-2817-46c2-a9a1-24e02a687cf4` | 140 | 28 | -21.09375 | `e2b894d3a283aafa74618971f545f35f6d4f5cd21466f4a964f29c39281d45de` |
| open surface | 2 | `6e075dcb-eba4-4617-bb91-f1c8fb7f290b` | 240 | 48 | 33.75 | `efa8a7e7add43126a87c8046a530c55ae46f03829e07f5e0009a252e40d0d845` |
| shallow seabed | 1 | `a56db6c9-77a7-45ad-8390-3b9cc448b47a` | 142 | 29 | 14.0625 | `488f29b64ff85211c5ae390957415d59418651546f431beca472a6adb07d65af` |
| slope | 1 | `4274709f-2646-439b-bbaa-562c0e783607` | 142 | 29 | 9.84375 | `08ff870aa262655bdc509b31b316ab659a8e0baa6ac5c86c15c0cc8bc329b528` |
| wall | 1 | `d51ff81b-14bb-4359-b17a-bedb6105ac36` | 140 | 28 | -5.625 | `f9d5c639f5552f24a22c224ea5623ea0b0fcc2b58aa544f8e96f545a7d5b8ee1` |
| ceiling | 1 | `382e3865-17cf-45d1-92f2-ffcd06f66e13` | 142 | 29 | 4.21875 | `dd730c12f3cec1b2c22cf24bf86242ce842baea38fe37577b20cfa00a30ce258` |
| flowing water | 1 | `8e9cf148-47a5-44b9-8ff2-f63000ea9895` | 141 | 29 | -15.46875 | `585d182dc93a14a4f1db4295ae5687b1aceddc817e492439ee05735fc21148ec` |
| waterlogged obstacle | 1 | `1cda8418-bd09-40d9-9616-965eaba9931f` | 139 | 27 | 23.90625 | `2e4e5e16199417845b814542f56f66d01c72aa1438ed686ad2cd0a48fbf5135d` |

The captures provide render stage presentation records with interpolated position, yaw, pitch, and
GeckoLib controller state. They are sanitized evidence hashes only. The raw JSONL and screenshots
were removed during cleanup after the hashes and terminal records were recorded.

## Audio and cleanup

Before launch, the isolated client master category was zero. During the test, the exact owned Java
playback stream for PID `1756089` reported `Mute: yes` through the laptop audio server. No system
sink or unrelated application was muted. The client and server were stopped during cleanup, and
the temporary candidate, capture files, screenshot, world, logs, and runtime paths were removed.
The user owned instance files were restored and matched their pre test hashes for `instance.cfg`,
`options.txt`, `servers.dat`, and the original BFS jar.
