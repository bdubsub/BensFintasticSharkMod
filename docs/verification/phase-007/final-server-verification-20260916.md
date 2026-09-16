# Phase 007 final server verification

This record supersedes the earlier candidate for the server and package gates. Performance comparisons, measured diagnostic overhead and laptop verification are deferred by [DEC-012](verification-scope-20260916.md). Historical failures retain their original results.

## Candidate binding

The source commit is `a861327b9a8e559095e31ec9cbc18894a4c0ea3b` on `envy/bfs2-phase-007`. The [candidate manifest](candidate-manifest.json) binds the Forge JAR, 790 source inputs, exact runtime dependencies, configuration fingerprints and isolated compatibility probe. The JAR has 1,052 archive entries and SHA 256 `4a8714033063853cc49f33fdb1b221293032416615ce286669025531cfc13606`. Its SHA 512 is `59455d27022626eab9b3ab641aca7802181805a4aaf9b269c5df7d3f2fec2cf018f00c7143f6073b140a667f114f60ef20292f3eb4284fca54797761d4664296`.

The source input list uses Git blob hashes after the repository's declared clean filters. Every committed blob and corresponding working file was compared using its exact repository path. The apparent root `build.gradle` mismatch was line ending normalization, with no semantic difference. The protected working file was not changed. The source input list SHA 256 is `5b9ecc7299eb6b67a28f1580d64ebc354184b87e66d0aa5584a743a071522334`.

## Results

| Gate | Result |
|---|---|
| Forge compilation, JUnit and build | Passed. 71 tests, no failure, error or skip. |
| Diagnostic and fishing parser tests | Passed. 37 tests. |
| Combined first party server graph | All 145 required tests passed in one run. |
| Population correctness | Both replacement modes passed their complete 24,000 tick captures and exact fish counts. These are gameplay correctness assertions, not a performance comparison. |
| Social route target loss | Passed in the combined graph. The earlier isolated result alone was not accepted. |
| Follow groups and feedback | The combined graph passed retained arrival membership, individual toggles, twenty member movement, the 321 member scheduler, controller families, lifecycle and paired chat and action bar assertions. |
| Packaged third party follow | Passed with the exact final JAR, Alex's Mobs 1.22.9 and Citadel 2.6.0. |
| Installed server restart | Passed. The same packaged fixture passed again after a clean restart. |
| Resource and archive review | Integrity passed. No Fabric artifact, verification probe or Dive Suit recipe is included. The existing license entry is retained. |
| Generated resource binding | Generated resources remain unchanged from the zero drift data generation recorded in task 004. |
| Scoped source review | Settings snapshot publication, disturbance cleanup, fixture changes, package boundaries, complete diff and changed text secret scan reviewed. No unresolved finding in this change. |
| Independent review | Unavailable. Repository capability policy permits continuation with the required deterministic checks. |
| Performance and client verification | Deferred, with no new pass claimed. |

The first party run used the four namespaces `bensfintasticsharks,bfsfollow,bfsdisturbance,bfsdive` through `:forge:GameTestServer`. Its runtime was `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix/forge/run/bfs2-p007-combined-20260916`, world `world`, on `node-1` with Java 17 and Forge 47.2.0. The final external fixture correction changed exactly one of the 351 compiled classes, `BfsFollowThirdPartyGameTests.class`. The other 350 classes, including every first party test and gameplay class, were byte identical to the passing combined run.

The packaged checks used `/mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix/forge/run/bfs2-p007-packaged-20260916`, world `compatibility-world`, seed `240024`, loopback port `25967` and the installed `forgeserver` launch target. Online mode remained enabled. The [probe procedure](../../test/packaged-compatibility.md) exercises a server interaction within ordinary reach, then actual Grizzly Bear navigation and release. Both launches reported `Complete true. Failed required 0. Done 1. Total 1.` and saved all dimensions before exiting. This does not claim client input or client receipt.

## Corrected fixture failures

The first combined replay passed 144 of 145 tests. The second population mode captured `true` at offset zero while Forge's file watcher was reloading the autosaved configuration. All later samples correctly reported `false`. The fixture now waits for the requested mode to be visible on a subsequent server tick before starting capture. Every sample, offset and exact population assertion remains required.

The installed compatibility fixture initially had no matching structure file. After adding the existing empty fixture layout under its correct name, the next run exposed an invalid six block click. The fixture now clicks within normal reach, asserts both cancellation and a successful interaction result, then moves away for the navigation assertion. The candidate's permission and reach checks were preserved.

CodeGraph did not cover the current follow fixture symbols accurately. The omitted source was inspected directly. No runtime conclusion relies on the static index.

| Record | SHA 256 |
|---|---|
| Initial combined failure | `1c1debf2147ac83db65c25d35b760c1985fcef1c6c324f5b2f0cc998c4efb909` |
| Passing combined run | `9b112b7168e7cca69e20529e333b0097534f5f91c5404902d8ea74533529b58a` |
| Final build | `cd2a373097565536aa258ac9b970eae79cbc014e50d63232a995faefd67550f1` |
| Parser run | `aab34c2840caaa83d5697b759cd1a36122e6dd393842b59d59e49762796967e3` |
| Missing structure failure | `13b7f78379d7717d352bee301aeab4ce35bb9649d0ca1c17fbc38f3885119b13` |
| Invalid reach failure | `2b3d7f195a69c97c76105cfceb319ed6fcb3f561f3dee0b3ac9cbbd96b9835ce` |
| Passing packaged run | `5779eba75e5d45e2d6e43fef5dbbbfa4f624a1fe1f8846320c0a9755111c94c6` |
| Passing packaged restart | `43c1fc2bb66e2042b06bb9085fc89f54c8561b5b7a8ee03e40eb3b0503844b1a` |

Both owned server processes exited. Their exact runtimes, worlds, copied dependencies, logs, configuration, probe and crash reports were removed after extraction of this evidence. No client was launched. Preexisting unrelated runtimes, logs, performance records, worktrees and shared dependency libraries were preserved. Final integration, tag, stable artifact delivery and wiki synchronization remain separate completion gates.
