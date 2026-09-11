# Phase 003 task 002 icon pipeline

Date: September 10, 2026

P003-TASK-002 repaired the Harbor Seal advancement icon model conflict. The
supplied pixels were already exact and were not rewritten.

## Change

`ModItemModelProvider` now generates a flat `minecraft:item/generated` model
for `ModItems.HARBOR_SEAL_BLOCK`, matching the other six supplied icon items.
The obsolete hand-authored 3D model at
`common/src/main/resources/assets/bensfintasticsharks/models/item/harbor_seal_block.json`
was removed. Data generation produced the sole effective model at
`common/src/generated/resources/assets/bensfintasticsharks/models/item/harbor_seal_block.json`.

The generated model is:

```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "bensfintasticsharks:item/harbor_seal_block"
  }
}
```

All seven mapped items now resolve to exactly one generated flat model and one
matching `layer0` texture. No source model remains for any mapped item.

## Verification

* `./gradlew :forge:Data --no-daemon --console=plain` passed.
* `./gradlew :forge:test --no-daemon --console=plain` passed after the provider change.
* The post-change test log SHA-256 is
  `05fa0f2a56af2810847081d48a6797351908e4046c845fd2632a15e6e808007e`.
* The post-change test log SHA-512 is
  `80e8988410a4a953f42c5f16bad272e513a8a95951c8240a038aed51fdc2163467ee0ed4b66b38d3c9a798143a86a64ec9e6a0f03f9a36e8e57850ecc46c7d2e`.
* Each destination is a 16 by 16 RGBA PNG and matches its source archive
  entry. The seven destination hashes are recorded in the task 001 manifest.
* The generated Harbor Seal model SHA-256 is
  `d4660f6a8561abaa77310c2f4c4942873b654662a697cfa4139b79a586605f36`.

The disposable test log was removed after its hashes were recorded. This task
does not close the seven-display laptop review, complete model mutation tests,
or interim JAR inspection. Those remain ordered gates in P003-TASK-005 and
P003-TASK-007.
