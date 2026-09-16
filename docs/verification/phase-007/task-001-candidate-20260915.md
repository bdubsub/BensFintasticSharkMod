# Phase 007 task 001 candidate audit

This packet records the final phase entry candidate before feature replay and performance measurement.

| Field | Verified value |
|---|---|
| Phase branch | `envy/bfs2-phase-007` |
| Branch source commit | `08b8cf272915b02e4875b80703365d5cb1357231` |
| Verified default base | `f5047be40ad772d796d6aeba039212a523063ec3` |
| Predecessor tag | `bfs2-phase-006`, signed and resolved to `f5047be40ad772d796d6aeba039212a523063ec3` |
| Forge and Minecraft | Forge `47.2.0`, Minecraft `1.20.1` |
| Java | `17` |
| GeckoLib and SmartBrainLib | `4.4.7`, `1.14.2` |
| Candidate artifact | `forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar` |
| Candidate SHA 256 | `2537c76a09cda0ada4134677a871df2e0ee1024a4c1202da9a57b08598b631cf` |
| Candidate SHA 512 | `f4bf1dccaee263753cd45e3735bc6585188c2de8bfce241ee017c96ccdb81fe9154d6f53bc5a533d8d9fa3bc0709b50626d71efa205395102430d2a1af959e30` |
| Source input count and digest | `789`, `d7dadb7c707d4fb4b72a3026cf9d87560078fb397ace90b4e6e91d3f72fd8580` |
| Dependency digest | `0a411a5dfee926da97dba505c15441158280e9be71f0f879e8551236cc7b783f` |
| Configuration digest | `beb66731db56f88b5a91bace419bfcfe777eb6e8d362fcdc8dd1f8c3fd880c9b` |
| Archive validation | `unzip -tqq` passed |

The predecessor ancestry is sequential. Signed tags `bfs2-phase-000` through `bfs2-phase-006` verify with the registered EnVy signing key. Pull requests 30, 32, 33, 34, 35, 36, 37, 38 and 39 are merged into `1.20.1`; pull request 39 is the Phase 006 integration vehicle and has green build, analyze and CodeQL checks. The Phase 007 milestone is `bfs2 phase 007`, number 10.

Issue 28 remains open by owner decision. Its latest state preserves the historical performance rejection for the prior candidate and defers a fresh ordinary and doubled comparison to this final phase. No historical artifact or failed result is relabelled. Draft pull request 29 is already merged as historical performance evidence and is not reused as the Phase 007 candidate.

The candidate gate is explicit. Branch ancestry, predecessor tag, source input manifest, dependency and configuration identities, archive integrity, and candidate hashes are **passed**. The final feature replay, installed ordinary and doubled performance comparison, private independent review, final merge, resulting default verification, final tag, delivery packet, and postmerge wiki synchronization are **unverified** and remain mandatory before completion. The candidate is frozen for those gates. Candidate build scratch is complete and no test runtime or process is retained.

The machine readable identity is [candidate-manifest.json](candidate-manifest.json), and its normalized production source input list is [candidate-source-inputs.txt](candidate-source-inputs.txt).

## candidate repair amendment

The original candidate identity above is retained as historical entry evidence. The phase branch advanced to source commit `62d1bda860800451c307928ad87d2542b1f726fe` after the performance repair identified two hot paths. The current candidate manifest and source input list now bind that commit, the rebuilt Forge jar SHA 256 `bc193e5127544cf00580378b5591f8457e2a1286a32e873572814ed24e7747d7`, SHA 512 `76cfeca6196b4ba44762dbc6da5cded97353792c571fd5856efdaf95f694f1d235c39c6d31ded04357c472c2430ab1c50204f87812536750fee44bd2c752f491`, source input digest `a18f8c379ccc90d248c34866bdd02e552f6b97da3e153c7815e2ce9449c9345e`, and 789 source inputs. The repaired candidate passed compile, unit, build and all 145 affected first party GameTests. The installed performance comparison remains open under [task 003](task-003-performance-20260916.md) because its matching uncontended host was unavailable.
