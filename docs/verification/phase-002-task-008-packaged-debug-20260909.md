# Phase 002 packaged diagnostic evidence

Date: September 9, 2026

This record covers the bounded packaged server diagnostic command on the
exact current Phase 002 candidate. It is diagnostic facility evidence only.
It does not close movement gameplay, laptop visual acceptance, multiplayer,
or owner approval gates.

## Candidate binding

The disposable production Forge runtime used Minecraft `1.20.1`, Forge
`47.2.0`, Java `17.0.19`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`.
The candidate artifact is `BensFintasticSharks-forge-1.20.1-0.24.jar` with
SHA 256 `66ac5b99de5359ca90b28e2b5aa59df066e83b0f9d7e40f42c184c23a835a37b`.
The capture JVM binding used source revision
`cf14e5187edaebe728b06894bd6b429422c03eb8`, motion profile
`bfs-phase-002-profile-v1`, configuration fingerprint
`toml-sha256:43efc7f2adba5e5638dc52eff420038c89fcae84e755588842ef3b78c9906c8f`,
and data pack fingerprint
`jar-resources-sha256:66ac5b99de5359ca90b28e2b5aa59df066e83b0f9d7e40f42c184c23a835a37b`.

## Bounded capture

The trusted server console command was:

```text
bfs debug on movement 120 @e[type=bensfintasticsharks:tiger_shark,sort=nearest,limit=1]
```

The session selected one Tiger Shark and wrote 120 movement records plus one
terminal record. The raw JSONL capture has SHA 256
`e597c5b99ead7fca6795ce650be46602b2a54b20bd1e76043e30441cd973914e` and SHA
512
`26f9a9d759fe5efc142641ae90b26d4b07531b3daa5f57518fb2e913adae3531d5d2e31e029da30617b7b20701a7bef5c3bbd0d9fa434d1837987011b0dd52ac`.
It reported zero dropped records and `incomplete=false` with a
`duration_elapsed` terminal reason.

The strict analyzer invocation was:

```text
python3 tools/bfs_debug_analyze.py <capture> \
  --scenario p002-task008-packaged-tiger-movement-diagnostic-20260909 \
  --requirement BFS-REQ-027 \
  --candidate-manifest docs/verification/phase-002-task-008-debug-manifest.json \
  --output <empty-output-directory>
```

The analyzer returned `complete` with no errors or warnings. The verdict SHA
256 is `8e2d2c1486e329852f6de8dcfa19b38851a74648cdc852e9e3dcd3fc5496f0cd`.
The readable summary SHA 256 is
`b6314199324a638f4511935afcbbe3428ab1c8489d21bd7c774862688cc21b47`.

The selected Tiger remained in water with the registered
`SharkSwimmingMoveControl`. The trace measured a maximum total and horizontal
speed of `0.192000` blocks per tick, zero vertical speed, zero pitch step, and
zero coordinate step because the staged actor was held against a horizontal
collision. This is a valid complete diagnostic capture, but it is not a
positive movement or body pitch result.

## Byte budget negative control

An earlier command, `bfs debug on all 1200`, selected 19 loaded BFS actors and
stopped at the configured 32 MiB session limit. It reported
`recordsDropped=1`, `incomplete=true`, and
`incompleteReason=session output reached the maximum size`. Its SHA 256 is
`e6eab6be4093d2c94be203ccfd25fa44bef29f8304c62d1959983dc1d464480b`.
This remains an explicit incomplete diagnostic result and is not used as
acceptance evidence.

The packaged server is still held only for the pending owner visual review.
No client or renderer ran on node 1. After the owner review or an explicit
decision to defer it, stop the owned server and remove the exact disposable
runtime, captures, analyzer output, and temporary manifest output, then verify
that the paths and process are gone.
