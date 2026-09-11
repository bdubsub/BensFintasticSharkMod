# Phase 004 task 012 closure evidence

Date: September 11, 2026

This closure record maps every Phase 004 requirement and the algae defect to
the final evidence packet. The final Forge artifact is
`BensFintasticSharks-forge-1.20.1-0.24.jar` with SHA 256
`a22f84941a0c8457f6daab3000d4278a1c5ea06c3ab126eb32df73a91c9862bf`.

| Contract | Required evidence | Bound evidence | Result |
| --- | --- | --- | --- |
| `BFS-REQ-020` registry and identity | three blocks and items, supplied textures, frame metadata, translations, models, tags, loot, creative access and artifact paths | `phase-004-task-001-entry.md`, `phase-004-task-002-provenance.md`, `phase-004-task-003-survival-loot.md`, `phase-004-task-005-data.md`, `phase-004-task-011-artifact.md` | closed |
| `BFS-REQ-020` aquatic behavior and collection | submerged placement, support survival, noncollision, replacement, restored water, shears collection and wrong-tool rejection for all forms | `phase-004-task-003-survival-loot.md`, `phase-004-task-008-gametest.md`, final 85-test artifact record | closed |
| `BFS-REQ-020` client presentation | complete green and red authored loops, repeated holds, no interpolation, distinct patch form and clean resource loading | `phase-004-task-004-presentation.md`, `phase-004-task-004-loop-manifest.json`, final laptop approval capture and log hashes | closed |
| `BFS-REQ-021` data-driven generation | configured and placed features, eligible biome tag, bounded finite search, codec and data-pack controls | `phase-004-task-005-data.md`, `phase-004-task-006-worldgen.md`, final exact malformed control | closed |
| `BFS-REQ-021` natural fixed-seed output | all three forms, bounded density, zero invalid placements, lower combined density than seagrass, repeated custom coordinate and state equality | `phase-004-task-009-fixed-seed.md`, `phase-004-seed-run18.json`, `phase-004-seed-run19.json` | closed |
| `BFS-REQ-021` navigation and server safety | representative fish and shark routes, dedicated server readiness, forward load, backup restore and cleanup | `phase-004-task-008-gametest.md`, `phase-004-task-010-runtime.md`, `phase-004-task-011-artifact.md` | closed |
| `DEF-024-008` algae permanence defect | every minimum registry, data, world, natural gameplay, survival, loot, server, client and final-artifact row | all rows above, exact final jar, exact malformed control, and signed phase integration record | closed |

The fresh exact-candidate fixed-seed runs intentionally retain their
independent vanilla seagrass control variance. Their custom algae coordinate
and block-state multisets are identical. The strict full placement-state
control is preserved by `phase-004-seed-run16.json` and
`phase-004-seed-run17.json`; a byte comparison found the same compiled code
and resources in all 938 non manifest archive entries of the final exact jar,
with only Forge generated manifest timestamp fields differing.

The final exact candidate passed Java 17 compilation, Forge tests, two byte
stable Data runs, 85 required dedicated GameTests, the Forge build, packaged
dedicated-server startup, malformed data-pack rejection, and the approved
laptop visual gate. Phase 005 retains final release metadata, checksums, SBOM,
attestations, publication, and integrated release rechecks.

The phase-owned server, client, audio stream, temporary runtimes, worlds,
captures and scratch output were stopped and removed. The pre-existing
`build.gradle` line-ending change and protected `forge/logs/` directory were
left untouched and unstaged.
