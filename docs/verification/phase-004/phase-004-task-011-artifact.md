# Phase 004 task 011 artifact evidence

Date: September 10, 2026

The final Phase 004 candidate was built with Java 17 from the phase worktree
on branch `envy/0.24-phase-004`.

## Ordered checks

| Check | Result | Log SHA 256 |
| --- | --- | --- |
| `:forge:test` | passed | `9471287040a53d62ef8fe70d1cf2bc95e353e195e73056b7c44e47cfa1aeb3c2` |
| `:forge:Data` first run | passed, 302 generated files | `ffa5dff47f3ab875514b65277990553e9deadc281558fa15cec93a8fd2f83f24` |
| `:forge:Data` unchanged second run | passed, zero rewritten files | `6cf309691ff7f20d8ee404685737328f70b739bae9d7ee72b8498004abc846b5` |
| `:forge:GameTestServer` final rerun | passed, 82 required tests | `d29e76a8f217569debaa324fd360615e20726547cdb47df4c4fe4bf7b6601b63` |
| `:forge:build` | passed | `e929ccb9b89d48bee68e712c5ccbb3b356dffa71dff6b1a723189315e0effbde` |

The final Forge JAR is
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar`.

* SHA 256: `a8744ae7c816371374f083f6237673fe77c22766e56665d1dfb89f6f82d8db56`
* SHA 512: `20291fc7f3cfa84f3db8e0f333fab2ea18b15aa15a6388b633cc47ca23e0c24da818f5c8616d650780daf15a1caf01b19ebcb9b54a8b09391e145f8517297f34`
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
