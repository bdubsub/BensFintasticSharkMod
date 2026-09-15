# Follow group and feedback amendment

Observed: 2026-09-15T02:16:45.136615+00:00
Source revision: `faccaf58ffeb6c1d7b857c9c702a970d78062316`
Scope: BFS2-REQ-008, BFS2-REQ-009, DEC-011, IFC-004.

The owner confirms that the mob now follows but reports an arrival message requiring movement before selection. The owner requests independently retained selections until each mob is deliberately clicked again. Exact scoped wording: “i can have a band of 20 if i want” and “and i should get feedback in the text and chat for everything that happens”. These are owner requirements. The earlier report and command examples remain evidence rather than instructions. No group size acceptance, UI parity or runtime completion follows from that report.

At the recorded source revision, `BfsFollowManager` has a single `BY_OWNER` lease per operator, a 32 lease cap, a 2400 tick duration, and `ARRIVAL_LATCHES`. `claim` replaces an operator's prior target when a different mob is selected; repeated same-target events retain the lease instead of implementing a fresh deliberate toggle. Tick processing releases on arrival and adds a reselection latch. `sendRejectionFeedback` emits chat only; successful selection and release have no corresponding general outcome feedback. Both Forge handlers also treat an active matching lease as a consumed interaction independently of the rejected result, so membership must not become a permission bypass. These are static source observations, not a new runtime test.

`BfsFollowGameTests` contains arrival-latch, marker-loss, range-release and blocked-release assertions for the earlier contract. DEC-011 requires equivalent or stronger tests for retained membership, safe pause/resume and independent release. Existing controller and permission coverage remains required. `BfsDebugCommands` supplies singular follow status/stop; update that surface for group counts, pages and targeted release. The existing server and client diagnostic managers, parser, localized resources and follow item interaction path form the affected feedback boundary.

CodeGraph status reported current, but the targeted exploration omitted the follow manager and its tests and returned older unrelated symbols. Bounded direct inspection of the known manager/test paths supplies this gap. Java reflection, event registration, client press identity and actual controller/feedback behavior remain real-path test obligations. No index claim establishes runtime compatibility.

Fingerprints of affected implementation and test dependencies:

| Path | SHA 256 |
|---|---|
| `forge/src/main/java/tfar/bensfintasticsharks/follow/BfsFollowManager.java` | `875119a7cba0e117838b2ceb614361e74e40e1b05cc930983b811d338ad24ba0` |
| `forge/src/main/java/tfar/bensfintasticsharks/gametest/BfsFollowGameTests.java` | `e456b73abe8b2ed0500e2628570d46f10e885a2852888d558057ddb948db1523` |
| `forge/src/main/java/tfar/bensfintasticsharks/debug/BfsDebugCommands.java` | `93d3760cb5c2583deda4859b043ecd781511611e8d040c1b575560997cf92e73` |
| `forge/src/main/java/tfar/bensfintasticsharks/debug/BfsDebugManager.java` | `8154abad8d90543b94f42a3ffab49656020bed3108ae55c21efb7b382f7edb96` |
| `forge/src/main/java/tfar/bensfintasticsharks/client/BfsClientDebugManager.java` | `6868d64d1200fe4de097a008023bcfcec55cc75acfeedcd18dd50a90d87fa1ad` |
| `common/src/main/java/tfar/bensfintasticsharks/init/ModItems.java` | `c65b080807f3c8f96be8bf2d005bb529a8b389a763017fa882cc98bec5929996` |
| `tools/bfs_debug_analyze.py` | `805ed618e416b00cca162b03d0baa55a975c18629120c02554497befc1a59c41` |
| `tools/test_bfs_debug_analyze.py` | `0ca1813da0c14549f049d91de7424add0efc289775a0858b40f3406c55a01c02` |
| `docs/test/debug-diagnostics.md` | `7e82c900fa160d3d974d12b071a8221faa26387ff2132388e1dd5e2ad60dcc27` |
| `gradle.properties` | `4e80b57815a112037d2ce88acb7be4fcaa69752a63fc4b93cc4276bb7c84dd91` |
