# Phase 003 task 004 advancement copy

Date: September 10, 2026

P003-TASK-004 completed the English advancement copy audit. The existing
provider values already matched the reviewed copy, so no player-facing text
was changed.

## Deterministic copy contract

`ReleaseContractAuditTest` now contains an explicit UTF-8 expected map for all
49 generated advancements and all 98 title and description keys. The test
collects every translation reference from generated advancement JSON and
requires exact key-set and value equality with that map. This covers the
seven supplied titles and the exact captain copy:

* `El Capitán's Legacy`
* `Obtain Capitán Ben's Hat.`

The audit also rejects trailing whitespace, mixed straight and smart
apostrophes in one phrase, missing description terminal punctuation, and
doubled terminal marks. Reviewed title punctuation remains preserved where it
is part of the expected map.

The retired `shark_whisperer` title and description remain absent. The
generated language file and all generated advancement references are covered
by the same comparison, so a missing, extra, duplicate, or divergent key
fails the test.

## Verification

* `./gradlew :forge:test --no-daemon --console=plain` passed.
* Test log SHA-256:
  `7885c3a44e1efdc1e3c08c310e4e81043a23bef5a5b46d6424793c8765ea9c97`
* Test log SHA-512:
  `d021d1d0d4f5f52dc3b543976d54333b3d9ec5437837491b5b2b24088e287224bb85d822ecd794cde50a1c97eecfe661e51a6d8da63a5a265168d048480d35e0`

Natural progression, mutation fixtures, complete client review, and interim
JAR inspection remain ordered later gates in this phase.
