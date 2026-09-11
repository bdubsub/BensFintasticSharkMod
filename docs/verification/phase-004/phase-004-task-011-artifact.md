# Phase 004 task 011 artifact evidence

Date: September 11, 2026

The final Phase 004 candidate was built with Java 17 from the phase worktree
on branch `envy/0.24-phase-004`.

## Ordered checks

| Check | Result | Log SHA 256 |
| --- | --- | --- |
| `:forge:test` | passed | `602ff3c3a9cd30897318000f8626d0fc6a7fcf421083b37a6c22f8e2c6f19d3d` |
| `:forge:Data` first run | passed, 302 generated files | `ffa5dff47f3ab875514b65277990553e9deadc281558fa15cec93a8fd2f83f24` |
| `:forge:Data` unchanged second run | passed, zero rewritten files | `6cf309691ff7f20d8ee404685737328f70b739bae9d7ee72b8498004abc846b5` |
| `:forge:GameTestServer` three clean reruns | passed, 84 required tests each | `84b2180e704285dae806f4ee7e35c42bae59e27f5764c2e70644239b64e27f43`, `79fccff26c833dc184bdf9d654458d9102434304ad83926bb08c9b921f253cbe`, `8dae8d14220220bbd1b56ec87c4f3a514d19ff14edd0e09bb28099e9f33e0ca7` |
| `:forge:build` | passed | `e69adb06cd0304e03ce6e72ab61f49563c2169904cb12771482b906d15eb5961` |

The final Forge JAR is
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar`.

* SHA 256: `525b4b75cc139758a3721e859588940528cf00076a9132d1738b3bd85b9f3ce4`
* SHA 512: `9f1da2f3fa6368315ff5a5f7a144169d88713a1e66785a84ba961b6627d2787e96dae6f94a873800dc43882c04a00e90d9d365267ebfd3689c632805b2f82711`
* `unzip -tqq` passed.
* The archive contains Forge metadata, all three algae block and item pairs,
  cutout models, exact block and strip textures, both animation metadata files,
  three loot tables, block and item tags, the nine-biome algae tag, three
  configured features, three placed features, and three Forge biome modifiers.

The generated output remained unchanged after the second Data run. The only
working tree state outside this evidence is the pre-existing `build.gradle`
line-ending change and the protected generated cache or log paths recorded by
the entry audit.

## Disposition

The deterministic and archive gates pass on the exact candidate. Final release
metadata, checksums, SBOM, attestations, and publication remain Phase 005 work.
The Phase 004 completion packet is still open for the current-candidate owner
visual approval and strict fixed-seed equality row listed in the task 009 and
task 010 records. The current-candidate 0.23 forward-load, backup restore
rehearsal, data-pack disable and bounded override controls pass, and the
required algae navigation GameTests pass in the three clean 84-test runs
above.

The current fixed-seed manifests are `phase-004-seed-run10.json` with SHA 256
`80879885ec4efbc353bd6c893e3f778ad5d89cea891bd3ed6b536600bc21f4d8` and
`phase-004-seed-run11.json` with SHA 256
`a382c3a3ec37714ce58d28b667c3a81606a2b401192393e2ca8c4cb35482ff25`.
Their BFS coordinate and form hash is identical, while the vanilla seagrass
control differs and remains explicitly open rather than waived.
