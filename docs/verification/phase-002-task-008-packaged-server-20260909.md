# Phase 002 packaged server parity evidence

Date: September 9, 2026

This evidence records a production Forge server load using the exact candidate
artifact rather than the development run configuration.

Runtime: `/tmp/bfsm-p002-packaged-20260909-r1`

The disposable server used Minecraft 1.20.1, Forge 47.2.0, Java 17, and
`eula=true`. The runtime loaded these exact mod files:

* `BensFintasticSharks-forge-1.20.1-0.24.jar`
* `geckolib-forge-1.20.1-4.4.7.jar`
* `SmartBrainLib-forge-1.20.1-1.14.2.jar`

Candidate artifact hashes:

* SHA 256:
  `94f8f5de3c1eaefea81dbf0337cfda8fde20992c3bc331a27c8709a629d192f7`
* SHA 512:
  `1ec72bff4b465ad006fe6479429c703aa2aaac6e8576ee630441fd5dacdb140a31b4fa592dd084581b7d467ae6e9f4e91408c9d4a44edac5e66981547c5eb610`

The server started on the disposable loopback endpoint `127.0.0.1:25593` and
reached the normal dedicated server readiness line:

```text
Done (15.787s)! For help, type "help"
```

The startup log contained no `ERROR`, `Exception`, or mod dependency loading
failure. The jar passed `unzip -tqq`, and the packaged animation resources were
present. The runtime was stopped and removed after log inspection. No client,
renderer, or window was started on node 1.

This is packaged startup parity evidence only. It does not replace the laptop
visual, multiplayer, performance, or full phase integration gates.
