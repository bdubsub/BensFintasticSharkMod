# Phase 007 task 002 final regression

The pinned Phase 007 candidate from [task 001](task-001-candidate-20260915.md) was exercised through the full server GameTest graph and the parser and unit suites.

| Gate | Result | Evidence |
|---|---|---|
| Forge GameTest server | Passed | All 131 required tests passed in `6m 17s` |
| Movement and species coverage | Passed | 22 species movement and safety fixtures ran in the final graph |
| Follow coverage | Passed | Claim, arrival retention, moving groups, controller families, restoration, arbitration and feedback fixtures ran |
| Fishing and old gameplay | Passed | Fishing, advancement, Cod and Salmon fixtures ran; one expected rejected delivery warning is retained as a negative path |
| Algae and world generation | Passed | State, navigation and fixed world generation fixtures ran |
| Dive coverage | Passed | Dive equipment, oxygen lifecycle and work parity fixtures ran |
| Diagnostics | Passed | Lifecycle, parity, population and movement captures completed with zero dropped records in the passing fixtures |
| Parser suite | Passed | `python3 -B -m unittest tools/test_bfs_debug_analyze.py`, 31 tests |
| Unit suite | Passed | `./gradlew :forge:test --no-daemon` |

The GameTest run used the disposable runtime `forge/forge/run/bfs2-p007-final-regression-20260915`. Its final log contained the exact terminal line `All 131 required tests passed :)` and had SHA 256 `50e6ac4267329de9a1c8953a0a3be74c78d9871f9ec9eb61c1fc69119db6ed23`. The run emitted existing fixture cleanup warnings for stale block entities and the expected fishing delivery rejection; none was a GameTest failure. Twenty bounded diagnostic captures were started and stopped by the fixture graph, with complete terminal records and zero dropped records in the passing assertions.

The runtime, generated world, captures and logs were removed after the result was recorded and the exact runtime path was verified absent. No server, client, watcher or test-owned process remains. This packet is bound to candidate SHA 256 `2537c76a09cda0ada4134677a871df2e0ee1024a4c1202da9a57b08598b631cf`, candidate SHA 512 `f4bf1dccaee263753cd45e3735bc6585188c2de8bfce241ee017c96ccdb81fe9154d6f53bc5a533d8d9fa3bc0709b50626d71efa205395102430d2a1af959e30`, source input digest `d7dadb7c707d4fb4b72a3026cf9d87560078fb397ace90b4e6e91d3f72fd8580`, dependency digest `0a411a5dfee926da97dba505c15441158280e9be71f0f879e8551236cc7b783f`, and configuration digest `beb66731db56f88b5a91bace419bfcfe777eb6e8d362fcdc8dd1f8c3fd880c9b`.

The installed Forge ordinary and doubled performance matrix, final laptop residual checks, private review, integration, final tag, delivery packet and postmerge wiki synchronization remain open.

## candidate repair amendment

The original regression packet remains bound to its 20260915 candidate and is not reused after the phase branch changed. The repaired source passed the affected first party GameTest namespaces separately with all 145 required tests passing, including disturbance, dive, movement and follow coverage. The full phase regression and client residual gates remain open until the repaired candidate identity is replayed on the required hosts.

The later isolated rechecks reproduced the result with 95 main species tests and 7 disturbance tests passing on the repaired source. The combined graph also confirms that the pinned third party test requires its separately packaged Alex's Mobs and Citadel runtime, which is intentionally absent from the Forge userdev classpath. Its production client and server evidence remains the authoritative third party fixture. A combined graph social route failure did not reproduce in the isolated main namespace and is retained as an ordering or host load observation rather than accepted as a pass.
