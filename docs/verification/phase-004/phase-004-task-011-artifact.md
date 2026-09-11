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
The current-candidate owner visual approval and packaged runtime gate now pass.
The Phase 004 completion packet now has exact malformed-reference evidence and
final fixed-seed evidence. The strict full state equality control is bound to
the same compiled code and resources through the source-equivalent pair in
task 009, whose only artifact difference from the final jar is the generated
Forge manifest timestamp. The current-candidate 0.23 forward-load, backup
restore rehearsal, data-pack disable and bounded override controls pass, and
the required algae navigation GameTests pass in the clean runs above.

The complete current fixed-seed manifests are `phase-004-seed-run14.json`
with SHA 256
`adf112087d5da4f65ff03b38e08196523603bb3823fb61be8127e26ce3a45301` and
`phase-004-seed-run15.json` with SHA 256
`500819a463a005967565101a5d5c36b62700f47947b15cd430c6a304d80ede09`.
Their custom algae coordinate and state hashes are identical. Their complete
vanilla seagrass state hashes differ and remain explicitly open rather than
waived.

## Final exact artifact record

The final Forge jar is
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar`.

* SHA 256: `a22f84941a0c8457f6daab3000d4278a1c5ea06c3ab126eb32df73a91c9862bf`
* SHA 512: `5e24f7ebf66035aa924e89b97c55304e41c1590646b264cdf5be45fbbf92ddc7785be4e02956b0989fea83d3efabdaff0c74301ff2ab2adf62d256f7d94e105b`
* `unzip -tqq` passed.
* The archive contains 939 files and 5,798,993 bytes. Its manifest, registries,
  generated resources, client assets, configured and placed features, and
  Forge biome modifiers are present at the required paths.

The final exact candidate passed compile, test, both byte stable Data runs,
the 85 required dedicated GameTests, and the Forge build. The final log
hashes are `9471287040a53d62ef8fe70d1cf2bc95e353e195e73056b7c44e47cfa1aeb3c2`
for `:forge:test`,
`dfda177461812e0c93711493f15310a59a71fa801ae4de94a52e7e2fdec25c8a` and
`aa1577ac11470d2dabbc57e7165df863b674889b875e009994e4db15abbc6ac1` for the
two Data runs,
`73d121e7388b0992a70536fc0299004c65744a23b51e41808e680c93addc625f` for the
85 test GameTests, and
`757bfffe8f687a103cd44f214169b393a6a0f73e17f05d865d07fb53963e91f2` for the
Forge build. The packaged
runtime reached `Done` with the pinned Java 17 and dependency set. The exact
malformed reference control rejected the unbound holder before readiness, and
the exact fixed seed repeats recorded all three forms, zero invalid
placements, finite per chunk bounds, and identical custom algae coordinates.
The final exact candidate runtime, client, fixed seed, malformed control, and
visual evidence are recorded in tasks 004, 006, 009, and 010.

The strict full state control remains the source equivalent pair in
`phase-004-seed-run16.json` and `phase-004-seed-run17.json`. A byte comparison
found the final exact jar and that source equivalent jar identical in all 938
non manifest entries. Only Forge generated manifest timestamp fields differ.
Final release metadata, checksums, SBOM, attestations, and publication remain
Phase 005 work.
