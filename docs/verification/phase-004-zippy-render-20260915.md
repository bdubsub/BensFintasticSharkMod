# Phase 004 Zippy render evidence

This record binds the Phase 004 render repair to the supplied Zippy resources and the local client diagnostic contract. It records the exact candidate, the silent laptop observation, the bounded parser result, and the cleanup result.

## Source and resource identity

The candidate source is commit `6a1252158df1f28142a13cd0a89c032dad017a12`. The Forge artifact is `forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar` with SHA 256 `a629f91e692c77ad7adf7f7ba6098b37f41f6ecfe59948bc2cabe605dfc7636e` and SHA 512 `cecb4fdd2cfdcb68cda150e5b45fa26dbb9eea03effa255549e27dcf9e5ec05bb616c66368a8d6b0a90a5068f20f65eca76f7d9766da26777f36e212b41adc18`.

The supplied base PNG remains `cd4352898da93334fb3c313d293ada7f7cae1b7d99729e9b01ce7d10fba4767d`. The supplied glow mask remains `cb6035ce2dafe249c020c4e9fa4f2b847119b17470a0bdf8cf41f51af184ff8f`. No source art or resource identifier was changed.

## Implementation and static evidence

The previous layer used GeckoLib `AutoGlowingGeoLayer`, whose `AutoGlowingTexture` derives a mask by mutating the base image. The Phase 004 layer now extends `GeoRenderLayer` and renders the authored `_glowmask` with `RenderType.eyes` in a full bright second pass. This keeps normal rendering on the authored base image while retaining the existing `hasGlowingLayer` brightness gate. The client diagnostic cache hashes both resources, checks matching dimensions and mixed mask alpha, invalidates on a resource manager reload, and records a bounded reason when resolution fails.

The focused Java tests cover the brightness boundary at raw light 7 and 8, non Zippy base selection, and the glow mask resource identity. The parser tests cover day and dark layer selection, reload generation monotonicity, resource hash shape, missing selected hashes, and wrong layer rejection. Forge compile, unit tests, Data, build, follow GameTests, and the Python parser suite passed for this candidate.

## Silent laptop runtime gate

The exact packaged artifact was loaded by a dedicated Forge 1.20.1 server on node 1 and the matching disposable Prism client on the Linux laptop. The client JVM carried source revision `6a12521`, artifact SHA 256 `a629f91e692c77ad7adf7f7ba6098b37f41f6ecfe59948bc2cabe605dfc7636e`, configuration `bfs2-p004-final-20260915`, and data pack fingerprint `bfs2-p004-final-20260915`. The client used the NVIDIA laptop renderer. The master audio option was zero and no Java playback stream was present during the visual sequence and after reload.

The decisive clean capture is session `41deb405-270e-4f66-8b39-df4913bea3be`. Its header selected exactly one target and no excluded targets. The terminal record reports `recordsAccepted: 1500`, `recordsDropped: 0`, `incomplete: false`, `missingTargets: 0`, and `remainingTargets: 1`. The capture contains 300 presentation records, all for `bensfintasticsharks:common_thresher_shark`, all selected, and every selected record resolves the authored base and mask hashes with `alphaBackgroundCheck: true` and reason `resources_resolved`.

The dark portion reports raw brightness 0 and the `glow` layer for 93 presentation samples at client ticks 70575 through 70943. The daylight return reports raw brightness 10 and the `marking` layer for 207 samples at ticks 70947 through 71771. The actual F3 plus T resource reload was visible in client chat and changed the diagnostic resource generation from 6 to 8 while the marking layer and both authored hashes remained unchanged. The reload portion reports 116 marking samples after generation 8. The daylight fixture screenshot showed the unchanged Zippy body and lightning markings. The dark path is additionally bound by the selected glow records at raw brightness 0.

The analyzer command used the candidate manifest for `BFS2-REQ-013` and returned `complete` with no errors or warnings. The capture therefore proves target selection, authored resource identity, alpha validity, dark glow selection, daylight marking selection, daylight return, reload generation change, and complete terminal accounting. The default client command ran to its automatic 1200 tick bound. The phase procedure's stricter manual stop target of 200 client ticks was not exercised by this capture and remains a separately tracked evidence limitation.

## Cleanup

The owned laptop client and launcher exited. The Java playback stream was absent. The disposable node 1 server process and ports 25597 and 25598 were stopped. The disposable server runtime, isolated Prism instance, temporary captures, screenshots, manifests, and analysis directories were removed. Protected repository paths were not modified.
