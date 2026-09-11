# Phase 004 task 003 survival and loot evidence

Date: September 11, 2026

## Repair

The entry audit found all three generated algae loot tables used `dropSelf`.
That would make ordinary breaking produce a block drop instead of the frozen
vanilla seagrass collection rule. `ModBlockLoot` now registers
`createShearsOnlyDrop` for `algae_block`, `large_green_algae`, and
`large_red_algae`. Regenerated loot tables contain the exact shears tool
predicate and no unconditional self drop.

The repair is covered by the real server test-player path in
`BfsGameTests.algaeBreakDropsTheBrokenForm`. The fixture places each form,
breaks each with shears, and asserts the matching item entity. It then breaks
the red form with a stick and asserts that no red algae item is produced. The
existing placement, water preservation, support, replacement, and water
restoration tests remain in the same required server suite.

## Verification

The corrected dedicated server GameTest suite discovered and passed all 82
required tests on four clean runs. The first run was the post-repair run and
the following three runs are the required repeat evidence.

| Run | Log SHA-256 | Result |
| --- | --- | --- |
| post-repair | `74326b138de75b926572b87cd9455f55f372ce2a780f76e6e4a38d2c46ca781e` | 82 of 82 passed |
| repeat 1 | `1bf21f6a6f028b87731b8e85f3966afd019c7c920bf2e1a6419ceb52069232af` | 82 of 82 passed |
| repeat 2 | `8337389ca3989f5d652df15c2e308138921249f62dca324c352985e667af54e1` | 82 of 82 passed |
| repeat 3 | `5e35bb55c8cec059882592fe466da9072557aef9a22d2ee27d068f75f0c85550` | 82 of 82 passed |

The corrected run uses Java 17 at
`/usr/lib/jvm/temurin-17-jdk-amd64`. The independent Forge test task also
passed. Its log SHA-256 is
`676525fdfb8b12970dc0f2f3d941b991a593708762f972803da4384e2682492e`.

The test fixtures were isolated after the initial repeat run exposed
cross-fixture event observers. Failure-mode fishing observers now watch only
their own hook and owner vicinity, and the mode matrix uses disjoint fixture
positions. The same repair prevents a neighboring rejected-catch fixture from
cancelling a valid live or item catch. This is test isolation only; production
fishing delivery still retains the removed-hook safety gate.

## Data generation control

The loot repair was followed by two successful Data runs. The first log SHA-256
is `e88e63b5ea871ca111de46809d21ebce30cf379b134ee5c673be8022ddae9a5e` and
the second is
`5bd80a5e65a10a8a3742bf031a5941b5d0e8c69371f2633b79a85e1de15d30fb`.
The second run reported 301 cached files, 302 total generated files, zero
stale removals, and zero rewritten files, proving byte stability after the
repair.

## Scope status

P004-TASK-003 survival, exact collection semantics, and server loot gates are
complete. Client visual loops, natural-generation fixed-seed equality,
dedicated packaged runtime, compatibility, and final candidate artifact gates
remain assigned to the later Phase 004 tasks.

