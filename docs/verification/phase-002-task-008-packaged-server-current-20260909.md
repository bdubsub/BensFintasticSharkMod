# Phase 002 current packaged server evidence

Date: September 9, 2026

This record binds packaged server startup to the exact current Phase 002
revision and rebuilt Forge artifact.

Revision: `ca7fc11`

The disposable production Forge runtime used Minecraft `1.20.1`, Forge
`47.2.0`, Java `17.0.19`, and `eula=true`. It loaded these exact mod files:

* `BensFintasticSharks-forge-1.20.1-0.24.jar`
* `geckolib-forge-1.20.1-4.4.7.jar`
* `SmartBrainLib-forge-1.20.1-1.14.2.jar`

The rebuilt candidate artifact passed `unzip -tqq` and has these hashes:

* SHA 256: `0e025a8c0c3e6ee05f273e1526e77d02921692f136a1bb36efc9f6eeccadf283`
* SHA 512: `06b77edad372d486d3aa3fc1c031cda9b31079a9c5a71ea277f359c0f0eade451f5b03be781f9f7493e37246aa05bedf1aa4a9fd3c38c1dff09b29786d16d984`

The corrected server run used disposable runtime `/tmp/bfsm-p002-packaged-r6`,
loopback port `25936`, server `online-mode=true` from its isolated
`server.properties`, and the packaged launch script. It reached:

```text
Done (13.249s)! For help, type "help"
```

The retained server log hashes were:

* SHA 256: `d6b1d8b0c920597320230b8e5efedda51f11e3aab1d1dd7d0b465dcbcadcc21a`
* SHA 512: `bdb1408ec7726dba256ecdfbc97bfe411a1b2cb8e54a2bb5933fbc7e4c44cde0e2f594707f888ae8247992b790dca806498aaa7dcab264aac157d2936955bcad`

The startup log contained no error, exception, missing class, dependency
failure, or mixin failure markers. Forge emitted only the known development
reference map warnings and normal configuration corrections. A setup probe
before this run passed an unsupported `--online-mode` command line option to
Minecraft and failed before server startup. That disposable runtime was
inspected and removed; it is not counted as a parity result.

The corrected runtime was stopped after readiness and a bounded server debug
capture. The capture bound source revision `ca7fc11`, artifact SHA 256
`0e025a8c0c3e6ee05f273e1526e77d02921692f136a1bb36efc9f6eeccadf283`, motion
profile `bfs-phase-002-profile-v1`, configuration fingerprint
`properties-sha256:9643eb089e2f3970abd969594534fc94722bff572b55b7310a02583ce7cd6e9e`,
and data pack fingerprint
`jar-resources-sha256:0e025a8c0c3e6ee05f273e1526e77d02921692f136a1bb36efc9f6eeccadf283`.
The capture JSONL SHA 256 is
`f2d9a430c3afdb68e42b5f8b331970f37137be8efcab6fc174c955a82979981f` and SHA
512 is
`d0ffbcfbdcb4f417cce869e7cf74129cce6fa75cea6360ff1a2ac40aef01a9891f3f3a391ecb576875ebd0ce14c46c04efa363bae27fa171fe42f2e55ede36bf`.
Its strict horizontal movement analysis completed with zero errors and
warnings. The analyzer verdict SHA 256 is
`a5355696f5630b37f21ab900c3e638d00bb2fc1d77f821ace21dd831a4ec615a`. The
horizontal manifest intentionally omits trajectory pitch because the bounded
route remained level. Scenario and requirement labels remain the analyzer's
explicit candidate manifest placeholders because the production command does
not accept those labels.

The exact disposable directory was removed and verified absent after these
hashes and verdicts were recorded. No client, renderer, or window ran on node
1. This closes current packaged startup and diagnostic parity only. Laptop
visual approval, multiplayer behavior, performance, documentation, review,
merge, and the signed phase tag remain open.
