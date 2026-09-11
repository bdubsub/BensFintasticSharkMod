# Phase 003 task 005 deterministic advancement audit

Date: September 10, 2026

P003-TASK-005 extended `ReleaseContractAuditTest` without weakening the
inherited positive controls.

## Added checks

* The full generated graph has exactly one root, every node reaches that root,
  and cycles and missing parents fail.
* Provider and generated language scans reject the retired
  `shark_whisperer` identifier.
* `marine_biologist` and `apex_of_apex` retain their complete semantic
  snapshots while using the authorized `sharks_galore` parent. Their
  parent-excluded canonical SHA-256 digests are
  `50948053b89d56628b1fc3ed9fa4727daf4a2b7b415e1c601664fd74aaad2807` and
  `6c52dc0444a1038a51fd24b609795647108a85753caeb9b5f106e1454a04a443`.
* All seven supplied icon items resolve to one generated flat model with the
  matching `layer0` texture and no duplicate source model path.
* Mutation fixtures reject a removed criterion, wrong parent, missing copy
  key, and swapped icon texture.
* The complete expected copy map remains exact across every generated
  advancement reference.

## Verification

* `./gradlew :forge:test --no-daemon --console=plain` passed with 45 tests.
* Test log SHA-256:
  `20beede56b8035fc3e43cbeca7d6bf0753dd141c0ccd20446f57a9538a6e6fe9`
* Test log SHA-512:
  `5d36a349741642f5e03c261f8282fa6f2ea6c9fe9208eda4a7e218dedf5681ba9ea416633e37783c6fd93458f7774cff44528f4f40f0731b4430aaff9211ea42`

The test log was removed after hashing. Fresh and existing profile behavior,
interim JAR scans, and interactive display remain ordered Phase 003 runtime
gates.
