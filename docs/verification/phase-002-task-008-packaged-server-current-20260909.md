# Phase 002 current packaged server evidence

Date: September 9, 2026

This record binds packaged server startup to the exact current Phase 002
revision and rebuilt Forge artifact.

Revision: `7e0ced9b388873dc46caa9f40528adb4fe74c291`

The disposable production Forge runtime used Minecraft `1.20.1`, Forge
`47.2.0`, Java `17.0.19`, and `eula=true`. It loaded these exact mod files:

* `BensFintasticSharks-forge-1.20.1-0.24.jar`
* `geckolib-forge-1.20.1-4.4.7.jar`
* `SmartBrainLib-forge-1.20.1-1.14.2.jar`

The rebuilt candidate artifact passed `unzip -tqq` and has these hashes:

* SHA 256: `66ac5b99de5359ca90b28e2b5aa59df066e83b0f9d7e40f42c184c23a835a37b`
* SHA 512: `ab0deaf8f12f4ee0796471437cda5363fab7136f586384c24dce0b101405158509b82a3447b4212bd0bd45e1e7175c1c8141390eb8ff1cb03b7bc34e4b4d69fd`

The corrected server run used disposable runtime `/tmp/bfsm-p002-packaged-r4.uVfykL`,
loopback port `25933`, server `online-mode=true` from its isolated
`server.properties`, and the packaged launch script. It reached:

```text
Done (13.249s)! For help, type "help"
```

The retained server log hashes were:

* SHA 256: `183ed48beede290f58f72b7a5a98a6aff8a535d4376e05ddb4e44c9ad643a2b9`
* SHA 512: `93a63c11dfd62cb35a01db3d595d1e95209f0fe43f8e06a3fbccb04778236e5ceb0504e4914bec6f23dc66389ec5e811b826fccee5a2efd5d67234923243eada`

The startup log contained no error, exception, missing class, dependency
failure, or mixin failure markers. Forge emitted only the known development
reference map warnings and normal configuration corrections. A setup probe
before this run passed an unsupported `--online-mode` command line option to
Minecraft and failed before server startup. That disposable runtime was
inspected and removed; it is not counted as a parity result.

The corrected runtime was stopped after readiness, and its exact disposable
directory was removed and verified absent. No client, renderer, or window ran
on node 1. This closes current packaged startup parity only. Laptop visual
approval, multiplayer behavior, performance, documentation, review, merge,
and the signed phase tag remain open.
