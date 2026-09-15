# Held follow group verification

This verification covers the follow interaction correction for BFS2-REQ-008 and BFS2-REQ-009. The saved goal and authoritative plan remain unchanged because they already require independent selection, retained arrival membership, and paired chat and action bar feedback.

## Source and behavior

The source commit is `63b37a9` on `envy/bfs2-phase-003`. A continuous use action now tracks handled targets independently. A duplicate Forge callback for the same target is consumed without a second toggle or duplicate receipt, while a different target can be selected during the same held action. Releasing the use button and clicking a selected target again releases only that member. Waiting feedback uses the wording `Waiting nearby` and continues to report the selected count.

## Verification

The checks ran on node 1 with Java 17.0.19 and Forge 47.2.0 for Minecraft 1.20.1.

| Check | Result |
| --- | --- |
| Java and test compilation | Passed |
| Forge unit tests | Passed |
| Data generation | Passed, one localized resource and its cache fingerprint updated |
| Focused `bfsfollow` GameTests | All 35 required tests passed |
| Held multi target fixture | Passed, two distinct mobs selected during one held action, duplicate callback ignored, individual release reported through both channels |
| Diff check | Passed |

The disposable GameTest runtime was `/tmp/bfsm-follow-held-20260915a`. It was configured with `eula=true`, stopped cleanly after all tests passed, and removed after the final log inspection. No test server or Gradle process remained. The protected untracked `forge/logs/` and `performance-matrix-20260912-interval50/` directories were left untouched.
