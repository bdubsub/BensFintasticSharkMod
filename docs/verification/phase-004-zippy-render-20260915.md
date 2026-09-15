# Phase 004 Zippy render evidence

This record binds the Phase 004 render repair to the supplied Zippy resources and the local client diagnostic contract. It records the exact candidate, the silent laptop observation, the bounded parser result, and the cleanup result.

## Source and resource identity

The candidate source is commit `5a3ebb0b7118943eb0b29fecd522eae0fc4939b4`. The Forge artifact is `forge/build/libs/BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar` with SHA 256 `6e5f7e0e657ace13f16665449d37c8e78367e480f8fea08a601fcea87f05bada` and SHA 512 `ed083bbca1c8727e46301a26e0d43b3ba23b9bd8261bb927d570a658242b916b2e5ab8bde11ed830cc8ef4cd4720f96f7c6ab2a45b4f3a7bb2798236aff5768c`.

The supplied base PNG remains `cd4352898da93334fb3c313d293ada7f7cae1b7d99729e9b01ce7d10fba4767d`. The supplied glow mask remains `cb6035ce2dafe249c020c4e9fa4f2b847119b17470a0bdf8cf41f51af184ff8f`. No source art or resource identifier was changed.

## Implementation and static evidence

The previous layer used GeckoLib `AutoGlowingGeoLayer`, whose `AutoGlowingTexture` derives a mask by mutating the base image. The Phase 004 layer now extends `GeoRenderLayer` and renders the authored `_glowmask` with `RenderType.eyes` in a full bright second pass. This keeps normal rendering on the authored base image while retaining the existing `hasGlowingLayer` brightness gate. The client diagnostic cache hashes both resources, checks matching dimensions and mixed mask alpha, invalidates on a resource manager reload, and records a bounded reason when resolution fails.

The focused Java tests cover the brightness boundary at raw light 7 and 8, non Zippy base selection, and the glow mask resource identity. The parser tests cover day and dark layer selection, reload generation monotonicity, resource hash shape, missing selected hashes, and wrong layer rejection. Forge compile, unit tests, Data, build, follow GameTests, and the Python parser suite passed for this candidate.

## Silent laptop runtime gate

The exact packaged artifact was loaded by a dedicated Forge 1.20.1 server on node 1 and the matching disposable Prism client on the Linux laptop. The client JVM carried source revision `5a3ebb0b7118943eb0b29fecd522eae0fc4939b4`, artifact SHA 256 `6e5f7e0e657ace13f16665449d37c8e78367e480f8fea08a601fcea87f05bada`, configuration `bfs2-p004-manual2-20260915`, and data pack fingerprint `bfs2-p004-manual2-20260915`. The client used the NVIDIA GeForce RTX 5090 Laptop GPU renderer. The master audio option was zero and the matched Java sink input reported mute enabled and zero volume during the visual sequence and after reload.

The decisive clean capture is session `e4bc58ae-ce75-4c96-a424-3bc135795faf`. Its header selected exactly one target and no excluded targets. The terminal record reports `recordsAccepted: 957`, `recordsDropped: 0`, `incomplete: false`, `missingTargets: 0`, and `remainingTargets: 1`. The capture contains 191 presentation records, all for `bensfintasticsharks:common_thresher_shark`, all selected, and every selected record resolves the authored base and mask hashes with `alphaBackgroundCheck: true` and reason `resources_resolved`.

The dark portion reports raw brightness 0 and the `glow` layer for 64 presentation samples at client ticks 44858 through 45110. The daylight return reports raw brightness 15 and the `marking` layer for 127 samples at ticks 45114 through 45620. The actual F3 plus T resource reload was visible in client chat and changed the diagnostic resource generation from 16 to 18 while the marking layer and both authored hashes remained unchanged. The reload portion reports 110 marking samples after generation 18. The daylight fixture screenshot showed the unchanged Zippy body and lightning markings. The sanitized Minecraft frame was captured from the laptop display with SHA 256 `11cec4e13775359ccc4e9a5e04b9cb19d93e43cef68118d8abd74ce8298f779e`. The dark path is additionally bound by the selected glow records at raw brightness 0.

The analyzer command used the candidate manifest for `BFS2-REQ-013` and returned `complete` with no errors or warnings. The capture therefore proves target selection, authored resource identity, alpha validity, dark glow selection, daylight marking selection, daylight return, reload generation change, and complete terminal accounting. The operator stop arrived three client ticks after the last presentation sample, within the phase procedure's 200 tick bound.

## Cleanup

The owned laptop client and launcher remain running only until the final phase audit completes. The exact Java playback stream is muted and at zero volume. The disposable server and isolated client are retained for the remaining checksum and cleanup audit, then will be stopped and removed. Protected repository paths were not modified.
