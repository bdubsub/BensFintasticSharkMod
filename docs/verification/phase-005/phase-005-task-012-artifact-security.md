# Phase 005 task 012 artifact and security evidence

Date: September 11, 2026

This record audits the exact Forge release candidate after the Task 011
runtime gates. It does not publish the candidate or replace the required
review and merge gates.

## Candidate binding

| Field | Value |
| --- | --- |
| Candidate source revision | `a939aa255c10dd280dbd992d8fd7b0ac4b67dc34` |
| Evidence revision | `b29c6efa866fd8a97c05283988e776fd48ba0b4d` |
| Candidate | `BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar` |
| Minecraft and Forge | `1.20.1`, `47.2.0` |
| Java | Temurin `17.0.19+10` |
| GeckoLib | `4.4.7`, SHA 256 `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0` |
| SmartBrainLib | `1.14.2`, SHA 256 `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b` |
| Size | `2013933` bytes |
| Entries | `939` |
| Uncompressed size | `5799123` bytes |
| SHA 256 | `94bfe2c8b45fc54505d42b63d026e203086959c34bb28325da3cf762c46578bd` |
| SHA 512 | `8d9edc14bf81e11fd8dc5e905b9aa1201e84c7d7bf303c53cee2f9a57fee516cd99076066b6df92d14d116f6199ddacf7deb03ee4237b1f37ccd369ae8bb5675` |

The candidate passed `unzip -tqq`. The two checksum files are stored beside
the SPDX document and source manifest in
`docs/verification/artifacts/`.

## Package allowlist and stale registration check

The inventory has only the expected `META-INF`, `assets`, `data`,
`gameteststructures`, and `tfar` trees. It contains no source files, Fabric
artifact, logs, worlds, crash reports, private configuration files, credentials,
or archive members with traversal paths. Duplicate archive names are absent.
The `gameteststructures/bfsgametests.empty.snbt` member is the required
packaged GameTest template. The tracked `.cache` members are deterministic
datagen resource manifests containing resource hashes only. They are not
runtime logs, private configuration, credentials, or test worlds.

The final JAR contains the diagnostic implementation and command surface:

* `tfar/bensfintasticsharks/debug/BfsDebugCommands.class`
* `tfar/bensfintasticsharks/debug/BfsDebugManager.class`
* `tfar/bensfintasticsharks/client/BfsClientDebugManager.class`

The server command is registered below the permission level 2 `/bfs` root.
Server sessions bound their output to the normalized game directory
`logs/bfs-debug`, enforce 20 through 36,000 ticks, select at most 32 entities,
bound records to 16 KiB, sessions to 32 MiB, and the directory to 256 MiB.
The client capture has the same bounded queue and byte controls, a 90 second
wall deadline, and stops when the client level changes. Session records use
the `bfs-debug-v2` schema and pseudonymize player identities. These controls
were reviewed at `BfsDebugCommands.java:24-42`,
`BfsDebugManager.java:77-86`, `114-179`, `241-280`, `874-937`, and the
corresponding client manager bounds.

The packaged resource inventory contains only
`data/bensfintasticsharks/loot_modifiers/replace_fishing_fish.json` for the
fishing replacement path. The stale additive `add_atlantic_cod_fishing.json`
and `add_atlantic_salmon_fishing.json` registrations are absent. The global
modifier list contains the replacement identifier once.

## SPDX and provenance

`docs/verification/artifacts/bfs-1.0-rc.1.spdx.json` is valid SPDX 2.3 JSON.
It binds the candidate checksum to Minecraft 1.20.1, Forge 47.2.0, Parchment
2023.09.03, GeckoLib 4.4.7, and SmartBrainLib 1.14.2. Direct dependency
hashes were recalculated from the Gradle-resolved artifacts and match the
Task 009 server binding. The complete source, plan, archive, metadata, and
package disposition are in
`docs/verification/artifacts/bfs-1.0-rc.1-source-manifest.txt`.

## Security review

The review covered command registration and permission, untrusted category,
duration and entity arguments, dimension filtering, output path containment,
queue and file budgets, writer failure and incomplete-capture records,
disconnect or level-change cleanup, fishing trace pseudonyms, dependency
coordinates, packaged paths, stale fishing registrations, and secret exposure.
CodeGraph was used first for the debug command and manager call graph, then
the exact source lines, Gradle dependency graph, and frozen JAR were inspected.

No high, medium, or low confidence security finding was identified in the
candidate. The tracked text and candidate archive were scanned for private key
blocks and common GitHub, cloud, and messaging token signatures with no match.
The scan is pattern-based and is not a substitute for external secret
rotation or a full binary reverse-engineering review.

## Attestation capability

The repository declares GitHub CodeQL analysis in `.github/workflows/codeql.yml`
and the Forge build workflow in `.github/workflows/ci.yml`. Those workflows
provide analysis and build checks, not artifact signing or provenance. No
cosign, Sigstore, SLSA provenance, GitHub artifact attestation, or SBOM
attestation workflow is configured. Therefore the supported attestation result
for this candidate is `none available`, recorded honestly rather than inferred
from a passing build or CodeQL workflow declaration.

## Task result

The exact candidate, checksums, dependency provenance, SPDX document, source
manifest, allowlist, secret scan, diagnostic package, and attestation result
are complete. No source or product behavior changed during this task. The
protected pre-existing `build.gradle` line-ending change remains unstaged.
Task 013 remains the next action for the documentation and final packet audit.
