# Phase 004 task 007 deterministic audit evidence

Date: September 10, 2026

The Forge contract suite passed on Java 17, and the exact candidate passed
archive validation. The audit covers the supplied texture and metadata hashes,
all three block and item pairs, translations, cutout models, loot semantics,
algae tags, feature holders, placed-feature bounds, biome coverage, modifiers,
and the forbidden public `algea` spelling. Two unchanged Data runs reported
zero rewritten generated files on the second run.

The 82-test dedicated GameTest suite also passed on the final source candidate
after one transient shared fishing fixture failure was isolated and a clean
rerun passed. The failure and rerun are retained in the task 003 evidence; no
assertion was weakened. A calibrated known-bad fixture transcript for the new
Phase 004 generation assertions was run by changing the generated
`algae_block` placed-feature chance from `3` to `2` and running
`./gradlew :forge:test --tests
tfar.bensfintasticsharks.audit.ReleaseContractAuditTest.permanentAlgaeResourcesAreCompleteAndBounded --no-daemon`.
The test failed at the expected chance assertion with exit code `1`; the
negative transcript SHA 256 is
`189ea85d2709b9cd5f4e89dd78991e499f14bdfae66e918a5ff938017465de10`.
The generated resource was restored before a clean rerun of the same test,
which passed with transcript SHA 256
`90487bac3a809e60e401e1a4c82b46fa1b5c15625ca9d68cb15134e5f16c52f5`.
The temporary mutation and logs were removed after their hashes were recorded,
so the calibration subgate passes without changing the candidate.
