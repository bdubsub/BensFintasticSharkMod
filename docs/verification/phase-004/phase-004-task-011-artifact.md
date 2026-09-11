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
| `:forge:build` | passed | `757bfffe8f687a103cd44f214169b393a6a0f73e17f05d865d07fb53963e91f2` |

The final Forge JAR is
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar`.

* SHA 256: `227c037b14decfe7e4b44bbdfd5ef777e695ba0ceaef9fb8c2581d68dc159309`
* SHA 512: `c899d36ab31f3904fffff24f7cb28eead30af221dc430ceebeba200975366586e6b81b548f8ae82174616e24a08f10d943d5b5c28a7b5c7536ae0fdae3f2909f`
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
The Phase 004 completion packet is still open for the runtime compatibility,
full-loop, navigation, data-pack, rollback, and strict fixed-seed equality rows
listed in the task 009 and task 010 records.
