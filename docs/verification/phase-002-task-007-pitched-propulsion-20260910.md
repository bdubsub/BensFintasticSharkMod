# Phase 002 pitched propulsion verification

Date: September 10, 2026

Commit: `7aefa29`

The movement fix keeps normal forward propulsion when a fish or shark begins a
route already pitched against its requested vertical direction. The controller
limits only the powered vertical component to the existing species ratio while
the nose eases through level. Ordinary routes and final approach retain their
existing orbit and arrival protections.

The focused regression now drives an Atlantic Cod from a forty degree upward
pitch toward a lower forward waypoint for twenty server ticks. It requires
meaningful forward translation and a smooth pitch correction. The complete
Forge GameTest discovery also passed with the change.

Environment:

* Host: node 1, headless dedicated GameTest server.
* Minecraft: 1.20.1.
* Forge: 47.2.0.
* Java: 17.0.19.
* Runtime: disposable `/tmp/bfsm-p002-current-gametest-final7-NYdGbN`.

Command:

```text
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain \
  -PbfsGameTestRunDir=/tmp/bfsm-p002-current-gametest-final7-NYdGbN
```

Result:

```text
========= 82 GAME TESTS COMPLETE ======================
BUILD SUCCESSFUL in 3m 20s
```

The final disposable runtime log hashes were:

| File | SHA-256 | SHA-512 |
| --- | --- | --- |
| `logs/latest.log` | `098c6e5ae5e391d415f7b4fbbeb288f9ca89f73013d7a723279d259286531a75` | `f52b610367bc48b9e5831708cf312b55130b33e24b7692ae6c2c73833a3c34fdbfda2b050114342607f8de7aef96ebea4a70971ec339301d5ecd143c0261d44b` |

The runtime, world, generated configuration, and logs were removed after the
hashes were recorded. Cleanup was verified. Laptop visual approval remains a
separate acceptance gate.
