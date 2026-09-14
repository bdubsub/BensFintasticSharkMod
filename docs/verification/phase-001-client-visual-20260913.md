# Phase 001 client visual evidence

Date: 2026-09-13

Status: complete residual client capture matrix. The matching production Forge client joined the
disposable dedicated server with the current phase candidate and remained silent. Fresh client
captures cover every named terrain fixture at scales 0.25, 1, and 2. The captures provide the
render stage records needed for the mesh clearance review. Independent review, pull request,
merge, default branch verification, and the signed phase tag remain open.

## Candidate and hosts

The original matrix used source revision
`b9d50b9799fba75d0962d515d6789ae53e084cf7`. The extension matrix used source revision
`6205b3f13114bf51778797dd29464aa1073da44a`. Both used the Forge archive
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

The extension server ran from `.test-runs/bfs2-p001-visual-ext-20260913`, port `25644`, world
`bfs2_p001_visual_ext_20260913`, `eula=true`, and `online-mode=false`, with the same loader,
Java, and dependency hashes.

The original matrix client ran on the authorized EnVyOnMyMind laptop with an NVIDIA GeForce RTX
5090 Laptop GPU and the live Hyprland session. The exact client process was Java PID `1756089`.
The extension matrix used Java PID `1976412`, window class `Minecraft* 1.20.1`, and title
`Minecraft Forge* 1.20.1 - Multiplayer (3rd-party Server)`. Both clients joined the intended
server and the server console recorded the player join.

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
Great White Shark target and bound the artifact SHA 256
`95939fb61125aa74dea45668d2d0c311c65afbc2ae8a761cd78bcb703d69d633`. The original rows below
bind source revision `b9d50b9799fba75d0962d515d6789ae53e084cf7`; the extension rows bind
`6205b3f13114bf51778797dd29464aa1073da44a`. Every terminal record reported `recordsDropped=0`
and `incomplete=false`.

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

The extension matrix closed the 0.25 and 2 terrain scale gaps.

| fixture | scale | capture | records | presentation | interpolated pitch | JSONL SHA 256 |
| --- | ---: | --- | ---: | ---: | ---: | --- |
| open surface | 0.25 | `9b3389a0-4fed-44a6-9e0a-6931f6f18958` | 166 | 33 | -19.6875 | `a6a82db02f890267abbef05352dcd0579df19a645fee54d8f5e540a29fb3dbef` |
| shallow seabed | 0.25 | `ba50b05f-54b9-4615-ad42-4b4e7015a67e` | 140 | 28 | 14.0625 | `0a6f06bd53e0e6c2c18e0ff750ff4ecca136733b2536625d6dc6a8f7b8251c6e` |
| slope | 0.25 | `82e7ad3c-5c5b-499e-bc5d-702fe95b8b4a` | 140 | 28 | 9.84375 | `f46c63a2bb3b35ee597f1b34e38e18c686c03918087fe6b4c8df1aae59e70da5` |
| wall | 0.25 | `f76e9bc7-fbbb-4ba2-bc44-5f519bbf8b48` | 140 | 28 | -5.625 | `ec538281991a1a5d27e0e4efb078e07a01ad104661f00d9be9dfe8ca1f5dfe18` |
| ceiling | 0.25 | `07e88550-36f9-4714-a294-f2e56f967fc7` | 140 | 28 | 4.21875 | `bf86e8214f7357da4785ce8040771cfcefb839c665bf746d05c2f92dcbd2074e` |
| flowing water | 0.25 | `34a682f1-eb8c-4568-adf9-acb8a505fb0d` | 140 | 28 | -15.46875 | `2953d57d03c6fc164a4c4c1b9d33938aff9539a2da34e934af909f9157ad4d3d` |
| waterlogged obstacle | 0.25 | `c1e98c1a-c579-4bef-bc73-eb034d21f4a9` | 140 | 28 | 23.90625 | `337968394aa6fd4927bbc48516b2d6dc9380e644a41e63514d800d0834cf1107` |
| open surface | 2 | `5c40155d-b9be-4795-8a6a-9e0fd5a68eab` | 140 | 28 | -21.09375 | `c2d5910a68f6b20d683735cb95299ea2b24448dd7498e4c5a108dfabdc4a0e1c` |
| shallow seabed | 2 | `18851f09-38d0-4c35-9a1d-48752f05a167` | 164 | 33 | 14.0625 | `99a329c31cda304a82d7c51cb5dd072cb445fa575a471848ad0083a6435b0af9` |
| slope | 2 | `ac89d3ec-8677-4563-a238-c2811bd94203` | 165 | 33 | 9.84375 | `8b20dc7e28bd4610e72594644318cc75b6e5c16701c7e0b718e04ecb859a0ca2` |
| wall | 2 | `41518905-fa3a-4cc1-8d7a-4cfbd73e9402` | 164 | 33 | -5.625 | `35bb2416d3077a3f30051be4c8f2fe605455934d049f66fe9550a08cae452329` |
| ceiling | 2 | `e78aa38d-1efa-421d-a439-d91907c76c65` | 164 | 33 | 4.21875 | `3e96227d6c6df5278c6711d7a257d7c3e5a3e2282511e52b691257e08b9e5c5d` |
| flowing water | 2 | `8e2b4d7c-4171-4d55-bb93-8a1019ef243a` | 164 | 33 | -15.46875 | `b44d4bc95c1277f76a25784a947adc08d2c7c6fdc37577d7fc87c7408065ecbd` |
| waterlogged obstacle | 2 | `0aabe572-18b0-4766-8ce3-9ca3fec398cb` | 164 | 33 | 23.90625 | `29f3c457c4ceaa3802dcba68f979cb1e56b10ef6cf0895acbf74be31296d32c1` |

The captures provide render stage presentation records with interpolated position, yaw, pitch, and
GeckoLib controller state. A current scale 2 visual frame from the extension run was also recorded
with SHA 256 `a856cb61a2432a20a3c3d63af650bbf31618615b91f8b09d943e5591b73d993a`. They are
sanitized evidence hashes only. The raw JSONL and screenshots were removed during cleanup after
the hashes and terminal records were recorded.

## Audio and cleanup

Before launch, the isolated client master category was zero. During the original test, the exact
owned Java playback stream for PID `1756089` reported `Mute: yes`. During the extension test, the
exact owned Java playback stream for PID `1976412` also reported `Mute: yes` through the laptop
audio server. No system sink or unrelated application was muted. The client and server were
stopped during cleanup, and the temporary candidate, capture files, screenshot, world, logs, and
runtime paths were removed. The user owned instance files were restored and matched their pre test
hashes for `instance.cfg`, `options.txt`, `servers.dat`, and the original BFS jar.
