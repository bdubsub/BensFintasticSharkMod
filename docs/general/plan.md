# Ben's Fintastic Sharks Debug and Repair Plan

> **Plan ID:** PLAN-MASTER  
> **Plan status:** VALIDATED  
> **Project state:** EXISTING  
> **Planning subject:** Ben's Fintastic Sharks debug and repair pass  
> **Plan profile:** software_product  
> **Diagnostics contract:** 2

## 1. Project Identity

```text
Project: Ben's Fintastic Sharks
Requested artifact: authoritative_plan
Repository root: /mnt/hermes/projects/BFSMOD/_qa/windows-startup-fix
Starting branch: envy/rc1-release-evidence
Starting commit: 40207d1b4cbe8db9963f32e43b79bdcfce58918a
Authoritative remote:
origin
https://github.com/bdubsub/BensFintasticSharkMod.git
Remote ref: origin/envy/rc1-release-evidence
Remote commit: 40207d1b4cbe8db9963f32e43b79bdcfce58918a
Canonical default branch: 1.20.1
Approved default at intake: 33f849318b235ecded3012cdb625690096ad6795
Authoritative master: docs/general/plan.md
```

The retained source checkout contains draft pull request 29. Its product source matches the approved default. The project anchor `/mnt/hermes/projects/BFSMOD` is a historical dirty `envy/0.24` checkout, not the source for this pass. Preserve that checkout, owner `Content/`, preexisting line ending and build configuration changes, generated caches, historical branches, and immutable tags `bfs-0.24` and `bfs-0.24-final`. Never stage `AGENTS.md`. A directory name containing `_qa` does not authorize deletion of this active source worktree.

The delivered version is `1.0-rc.1`. Keep Minecraft 1.20.1, Forge 47.2.0, Java 17, Parchment 2023.09.03, GeckoLib 4.4.7 and SmartBrainLib 1.14.2. The unused Fabric directory remains an unsupported template. A new testing artifact must have a distinct source and checksum identity; it does not replace the delivered artifact or rewrite its evidence.

## 2. Planning Subject and Source Roles

The subject is the complete repair and operator tuning experience in the Forge mod. Reports identify defects, examples illustrate commands, and supplied art constrains rendering. None substitutes for the product contract. Current resolved owner choices govern over recommendations in the research brief. The sanitized [research brief](research/brief.md), [repository map](research/repository-map.md), [intake](research/intake.json), and [evidence index](research/evidence.json) preserve exact source locators, fingerprints and observation times.

| ID | Role | Subject | Source | Intended use |
|---|---|---|---|---|
| SRC-001 | owner_request | Original required behavior | Current owner request | Scope authority |
| SRC-002 | review_feedback | Reported bugs and command examples | Report and conversation pasted by owner | Evidence only, subordinate to owner requirements |
| SRC-003 | owner_request | Resolved scope answers | Owner reply resolving five questions | Scope authority |
| SRC-004 | reference | Dive inventory icon dive_helmet.png | Supplied attachment dive_helmet.png | Evidence only, subordinate to owner requirements |
| SRC-005 | reference | Dive inventory icon dive_leggings.png | Supplied attachment dive_leggings.png | Evidence only, subordinate to owner requirements |
| SRC-006 | reference | Dive inventory icon dive_chestplate.png | Supplied attachment dive_chestplate.png | Evidence only, subordinate to owner requirements |
| SRC-007 | reference | Dive inventory icon dive_boots.png | Supplied attachment dive_boots.png | Evidence only, subordinate to owner requirements |
| SRC-008 | repository_evidence | Authorized host capability | Read-only hardware, desktop and private connection checks | Evidence only, subordinate to owner requirements |
| SRC-009 | reference | Dive worn texture atlas | Supplied Photo 1.jpg | Evidence only, subordinate to owner requirements |
| SRC-101 | repository_evidence | Historical anchor a4d4e41 and CodeGraph coverage gap | Historical anchor a4d4e41 and CodeGraph coverage gap | Evidence only, subordinate to owner requirements |
| SRC-102 | repository_evidence | Pinned current repository source at 40207d1 | Pinned current repository source at 40207d1 | Evidence only, subordinate to owner requirements |
| SRC-103 | audit_evidence | https://github.com/bdubsub/BensFintasticSharkMod/pull/29 | https://github.com/bdubsub/BensFintasticSharkMod/pull/29 | Evidence only, subordinate to owner requirements |
| SRC-104 | audit_evidence | https://github.com/bdubsub/BensFintasticSharkMod/issues/28 | https://github.com/bdubsub/BensFintasticSharkMod/issues/28 | Evidence only, subordinate to owner requirements |
| SRC-105 | repository_evidence | Pinned performance and provenance records at 40207d1 | Pinned performance and provenance records at 40207d1 | Evidence only, subordinate to owner requirements |
| SRC-106 | repository_evidence | Pinned configuration, scale and spawning source at 40207d1 | Pinned configuration, scale and spawning source at 40207d1 | Evidence only, subordinate to owner requirements |
| SRC-107 | repository_evidence | Pinned authored assets and generated data at 40207d1 | Pinned authored assets and generated data at 40207d1 | Evidence only, subordinate to owner requirements |
| SRC-108 | reference | https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-47.2.0/forge-1.20.1-47.2.0-sources.jar | https://maven.minecraftforge.net/net/minecraftforge/forge/1.20.1-47.2.0/forge-1.20.1-47.2.0-sources.jar | Evidence only, subordinate to owner requirements |
| SRC-109 | reference | GeckoLib Forge 1.20.1 version4.4.7 exact sources | GeckoLib Forge 1.20.1 version4.4.7 exact sources | Evidence only, subordinate to owner requirements |
| SRC-110 | repository_evidence | Pinned current diagnostic manager, parser and support guide | Pinned current diagnostic manager, parser and support guide | Evidence only, subordinate to owner requirements |

## 3. Purpose and Intended Outcome

An operator can discover a species' actual capabilities, inspect effective settings, change independent movement axes and other existing behavior, observe the result, record values by hand, and restore defaults without rewriting configuration. A marked follow stick provides temporary control for testing mobs. Players encounter safe shark motion, meaningful water disturbances and boat interest, restored Zippy markings, useful underwater algae, and a complete dive suit with predictable movement and oxygen.

The smallest complete outcome includes diagnosis and recovery. A setting that parses but never reaches an entity, a follower that cannot resume normal behavior, a body safe only at its center, or a suit that refills on reconnect does not satisfy the request. The manual workflow deliberately excludes a profile library, subjective final speed balance, public publication, and unrelated ecosystem features. Readback, bounded diagnostics, capability reporting and lifecycle controls are necessary engineering detail within the requested experience.

## 4. Evidence-Based Current State

All observations below are static intake evidence observed September 12, 2026, UTC. No proposed repair is marked verified. Recheck mutable remote and host facts at their use gates.

| Area | Evidence class | Finding and consequence | Evidence |
|---|---|---|---|
| Baseline | OBSERVED | Root is historical. PR29 owns current tooling, corrected provenance and rejected performance evidence. Use its retained source, then sequential approved default lineage. | F001, F002, SRC-101, SRC-102, SRC-103 |
| Performance | OBSERVED | The ordinary candidate is irreversibly rejected at a partial prefix. Doubled cases were not run. Death churn and route simulation are leads, not proven causes. | F003, F004, F015, SRC-104, SRC-105 |
| Diagnostics | OBSERVED | Existing level 2 server on, status and off, bounded `bfs-debug-v2`, client capture, parser and GameTests are reusable. | F005, SRC-110 |
| Movement | OBSERVED | Fixed vertical ratios, several motion writers, center water checks and distinct inheritance/travel paths exist across twenty two species. Their masking of tuning and unsafe body placement are inferred risks awaiting real path proof. | F006, F007, F008, F009, SRC-102, SRC-106 |
| Attributes | INFERRED | Join multiplication can compound health and damage or heal entities on reload. Repeated lifecycle proof is required. | F010, SRC-106 |
| Disturbance | OBSERVED | Boat identity is lost by the living source conversion; no dedicated moving boat or shallow jump producer exists. | F011, SRC-102 |
| Zippy | INFERRED | Exact GeckoLib mask extraction clears base pixels, while the existing glow layer is disabled in daylight. `_glowmask` is already the correct suffix. | F012, SRC-107, SRC-109 |
| Algae | OBSERVED | Three seagrass subclasses and cross models provide single cells, sparse generation and no enclosed cave rejection. Existing animation frames already provide life. | F013, SRC-107 |
| Dive equipment | OBSERVED | Four item icons and a 1280 by 1280 JPEG atlas are supplied. No geometry is supplied, and no dive implementation exists. Gravity alone cannot bypass vanilla water travel. Correct geometry mapping and the final scoped movement hook remain unknown until their named experiment. | F014, FIND-023, SRC-004, SRC-005, SRC-006, SRC-007, SRC-009, SRC-108 |
| Hosts | OBSERVED | Existing laptop connection, Hyprland and NVIDIA capability were found. Actual candidate renderer, isolated paths, endpoint and muted stream remain runtime checks. | SRC-008, EXT-001 |

Consequential alternatives are resolved as follows. Reuse one immutable effective settings service rather than scattered entity mutations, because wildcard atomicity and readback require a single answer. Adapt every actual movement path rather than only `MOVEMENT_SPEED`, because independent axes must reach the final integrator. Use precomputed conservative body envelopes rather than per tick mesh inspection or center only water checks. Use temporary movement leases and controller adapters rather than clearing all goals or teleporting followers. Use a player owned oxygen reservoir rather than armor item charge, preventing swap exploits. Keep single registry IDs for existing algae using explicit segment state, avoiding a new public body block identity. Preserve Zippy's base art and use a nondestructive glow overlay rather than rename a correct mask.

Three technical hypotheses have early resolution tasks. P001-TASK-002 maps all movement writers and proves axis control before bulk tuning acceptance. P002-TASK-001 exercises goal, Brain, SmartBrainLib and special boss or third party controllers before follow acceptance. P006-TASK-001 validates worn atlas mapping and player travel hooks before final suit integration. Their failure keeps their phase incomplete; it does not silently exclude a species or requested behavior.

## 5. Product Contract and Profile Coverage

| Profile area | Status | Source | Contract location | Rationale |
|---|---|---|---|---|
| inputs and outputs | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |
| component architecture | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |
| state and persistence | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |
| failure taxonomy | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |
| versioning | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |
| security | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |
| test system | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |
| release lifecycle | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |
| generalization | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |
| determinism | covered | SRC-001, SRC-003, SRC-102, DEC-001, DEC-003 | Product Contract and Profile Coverage | The requested Forge product requires explicit interfaces, lifecycle, safety, proof and recovery; these are specified as target contracts and not claimed implemented. |

Detailed navigation: command inputs and outputs are in Section 11.1; components in 11.2; state, schemas and determinism in 11.3; failures and trust in 11.4; verification in 14; migration/recovery in 15; and release lifecycle in 16 through 18.

Inputs are authorized commands, actual game interactions, entity registrations, existing server config and saved world/player state. Outputs are effective readback, bounded diagnostics, synchronized gameplay, preserved world data and an exact testing JAR. The logical server owns decisions and durable state. Clients own presentation and validated prediction only. Identical registry, defaults, override revision and command inputs produce identical normalized settings, regardless of entity iteration order. Fixed seeds and tick stimuli drive repeatable tests; wall time and asynchronous file order are not gameplay authority.

## 6. Mandatory Scope

- BFS2-REQ-001: Start from approved current source, reconcile PR29 and issue28 without restoring deleted plans or treating rejection as a pass.
- BFS2-REQ-002: Discoverable /bfs debug controls with species or *, typed bounds, atomic bulk edits, effective readback and reset, with manually recorded session tuning.
- BFS2-REQ-003: `setspeed` independently controls horizontal and vertical base movement for every BFS species. Values are not prescribed balance defaults.
- BFS2-REQ-004: `setsprint` independently multiplies the matching horizontal and vertical base values in declared active states. Prevent duplicate multiplication and hidden override.
- BFS2-REQ-005: `setspawnsize` controls group quantity, with separate physical size controls and explicit natural versus administrative behavior.
- BFS2-REQ-006: Useful comprehensive species controls for spawning, scale, health/damage where supported, hunting, sensing, cooldown, movement and recovery, with safe reload.
- BFS2-REQ-007: Inspect and tune disturbance source, radius, sensitivity, rate and reaction eligibility per species where applicable.
- BFS2-REQ-008: NBT marked follow stick selects and follows all mob families, including bosses and other mods, through temporary movement ownership without a species whitelist.
- BFS2-REQ-009: Release selected followers and restore ordinary behavior on explicit stop and bounded lifecycle exits, without deleting unrelated behavior state.
- BFS2-REQ-010: Sharks remain physically and visually safe at surface, seabed, walls and slopes during pursuit, pitch, turning and recovery, across supported scales and tuning limits.
- BFS2-REQ-011: Real jumping/water entry and occupied moving boats produce bounded, observable shark alerts through real event paths.
- BFS2-REQ-012: Great whites follow eligible boats from behind with body submerged and fin visible where safe, and recover when depth, target or route becomes invalid. No boat damage is implied.
- BFS2-REQ-013: Restore Zippy's existing authored lightning markings and intended glow with correct daytime, darkness and resource reload behavior.
- BFS2-REQ-014: Small algae attaches to supported side/top faces like glow lichen, with underwater placement, water preservation, support and loot behavior.
- BFS2-REQ-015: Large green and red algae support vertical columns and manual stacking like kelp, correct growth/harvest/support transitions, and natural multi block generation.
- BFS2-REQ-016: Increase bounded algae population and retain existing life animation, with natural red algae excluded from enclosed underwater caves.
- BFS2-REQ-017: Four dive armor items preserve supplied item PNGs and use the supplied JPEG worn atlas with verified geometry and UV mapping, with no crafting recipes.
- BFS2-REQ-018: Complete suit underwater enables seabed walking and moonlike buoyant jumping, suppresses swim pose/ascent, and removes only water work penalties.
- BFS2-REQ-019: Full suit supplies 6000 protected submerged ticks of oxygen, refills in real air, persists against swap/reconnect exploits, and retains movement while normal drowning begins when empty.
- BFS2-REQ-020: Resolve measured performance regression by evidence guided work and complete ordinary/doubled installed Forge comparison at the retained threshold.
- BFS2-REQ-021: Preserve existing gameplay and authored assets, integrate every mandatory change and deliver a verified testing JAR with exact provenance, documentation and final regression evidence.
- BFS2-REQ-022: Extend existing diagnostics and support workflows for every new behavior, prove permissions, completeness, overhead and cleanup, and keep visual acceptance on the verified silent laptop.

## 7. Optional / Future Scope

- FUT-001: Respect the Wildlife Common Stingray advancement icon. Disposition: excluded. Owner explicitly says ignore it now.
- FUT-002: Final subjective swim speed balance for each species. Disposition: excluded. The owner performs final tuning and records values manually.
- FUT-003: Named tuning profile library and import/export. Disposition: excluded. Owner chooses manual tuning and handwritten transfer.
- FUT-004: Public release publication. Disposition: excluded. Owner accepts merged testing JAR endpoint.

## 8. Non-Goals

- NG-001: No dive armor crafting recipes in this pass.
- NG-002: No platform upgrade or Fabric artifact.
- NG-003: No boat destruction or unrelated ecosystem redesign.
- NG-004: No redesign of Zippy or supplied authored assets.
- NG-005: No silent persistence of debug overrides, no tuning balance lock based on example numbers.
- NG-006: No player control or nonmob following implied by the mob testing tool.

## 9. Owner Decisions

### DEC-001 — How is species tuning performed and transferred

**Status:** RESOLVED  
**Selected choice:** The owner tunes manually and records values. Provide live session overrides, effective readback and reset. Profile libraries and import/export are excluded.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-002, BFS2-REQ-003, BFS2-REQ-004, BFS2-REQ-006  
**Supersedes:** none

### DEC-002 — Which worn armor art is authoritative

**Status:** RESOLVED  
**Selected choice:** Use the four supplied item PNGs and supplied JPEG worn atlas. Preserve their source identities, create compatible geometry and UV mapping, and validate the rendered result without silently repainting the art.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-017  
**Supersedes:** none

### DEC-003 — What is the completion endpoint

**Status:** RESOLVED  
**Selected choice:** All mandatory repairs and debug controls are verified, integrated through sequential merge commits into 1.20.1, tagged, documented, and delivered as an exact verified testing JAR with checksums and source binding. Public release publication is excluded.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-021  
**Supersedes:** none

### DEC-004 — What happens when suit oxygen is empty

**Status:** RESOLVED  
**Selected choice:** Continue seabed movement and apply ordinary drowning. Refill only in real breathable air, never on equip, swap, disconnect or server restart underwater.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-018, BFS2-REQ-019  
**Supersedes:** none

### DEC-005 — Which mobs can the follow tool control

**Status:** RESOLVED  
**Selected choice:** All mob families, including bosses and other mods, without a BFS or ordinary vanilla whitelist. Preserve movement safety and restore ordinary behavior. Players and nonmob entities do not become followers.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-008, BFS2-REQ-009  
**Supersedes:** none

### DEC-006 — Which report requests are excluded

**Status:** RESOLVED  
**Selected choice:** The Respect the Wildlife Common Stingray advancement icon and subjective final species speed balancing are excluded. Debug tuning does not exclude mechanical movement safety repairs.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-003, BFS2-REQ-010, BFS2-REQ-021  
**Supersedes:** none

### DEC-007 — Where do verification workloads run

**Status:** RESOLVED  
**Selected choice:** Headless checks and disposable dedicated servers run on node-1. Minecraft clients run only on the verified Linux laptop with its discrete GPU and verified silent application stream. No graphical node-1 fallback.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-008, BFS2-REQ-010, BFS2-REQ-012, BFS2-REQ-013, BFS2-REQ-017, BFS2-REQ-018, BFS2-REQ-019, BFS2-REQ-021, BFS2-REQ-022  
**Supersedes:** none

### DEC-008 — How are test servers authorized and cleaned

**Status:** RESOLVED  
**Selected choice:** Configure and read back eula=true under standing authorization. Every check and runtime uses exact owned resources and verifies teardown after its final consumer, preserving owner data and shared caches.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-001, BFS2-REQ-020, BFS2-REQ-021, BFS2-REQ-022  
**Supersedes:** none

### DEC-009 — Which baseline and integration workflow applies

**Status:** RESOLVED  
**Selected choice:** Use current PR29 worktree source matching approved 1.20.1. Preserve the historical anchor and old evidence. Complete existing PR29 work before new sequential phase branches, use checked merge commits and signed phase tags, and never push directly to 1.20.1.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-001, BFS2-REQ-020, BFS2-REQ-021  
**Supersedes:** none

### DEC-010 — How should comprehensive controls and algae scope be bounded

**Status:** RESOLVED  
**Selected choice:** Use distinct group quantity and physical scale controls, tune existing behavior and hunting capabilities, and report effective limits. Restore face and column algae behavior with bounded richer natural generation, preserving authored animation. Do not add boat damage or unrelated ecosystem features.  
**Rationale:** Current owner scope and resolved operating boundaries govern this pass.  
**Affected requirements:** BFS2-REQ-005, BFS2-REQ-006, BFS2-REQ-007, BFS2-REQ-012, BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016  
**Supersedes:** none

Routine engineering defaults in the shared contracts implement these choices. They do not promote optional work. They specify units, safety bounds, oxygen refill and death handling, kelp style growth, and item mechanics so execution does not require another product design pass.

## 10. External Prerequisites

| ID | Prerequisite | Affected requirements | Availability | Authorization | Required external action |
|---|---|---|---|---|---|
| EXT-001 | Existing laptop and private desktop control capability | BFS2-REQ-008, BFS2-REQ-010, BFS2-REQ-012, BFS2-REQ-013, BFS2-REQ-017, BFS2-REQ-018, BFS2-REQ-019, BFS2-REQ-021, BFS2-REQ-022 | available | authorized | Revalidate the specific capability before its dependent gate; restore existing access if it fails. |
| EXT-002 | Existing repository write and registered signing capability | BFS2-REQ-001, BFS2-REQ-020, BFS2-REQ-021 | available | authorized | Revalidate the specific capability before its dependent gate; restore existing access if it fails. |

### EXT-001: Evidence contract

**Kind:** environment  
**Mandatory for endpoint:** true  
**Availability:** available  
**Authorization:** authorized  
**Blocked plan approved:** false

**Required evidence**

- Recheck laptop identity, active desktop and discrete candidate renderer before launch.
- Bind exact owned window/PID and mute only its application stream; verify mute and recreation cleanup.
- Discover its project anchor and verify matching candidate and private dedicated-server connection.

### EXT-002: Evidence contract

**Kind:** credential  
**Mandatory for endpoint:** true  
**Availability:** available  
**Authorization:** authorized  
**Blocked plan approved:** false

**Required evidence**

- Authenticated EnVisione account with repository write permission.
- Registered SSH signing key and EnVy author identity before commit and tag.
- GitHub merge state, required checks, resulting default commit and signed phase tag.

EXT-001 is a capability check, not authority for new connectivity, firewall changes, public ports, credential transfer or another runtime host. Discover the laptop project anchor through the existing connection; never derive it from the node path. EXT-002 binds the authenticated EnVisione account, EnVy author and committer identity, `contact.enviouse@gmail.com`, registered SSH signing key, protected default and actual GitHub checks. Never print key material. A failed signing operation stops the commit. Neither prerequisite authorizes a public release.

No new paid service, production mutation or independently supplied runtime artifact is required by the product. Exact installed Forge and dependency artifacts are pinned from the project and verified by SHA 256 and SHA 512 in every runtime manifest. Temporary compatibility artifacts used by P002-TASK-001 need an official source, exact version, matching Forge target, license/provenance and security inspection before use; they do not become shipped dependencies. Missing required external capabilities leave affected gates unverified while independent headless work continues.

## 11. Architecture and Ownership Boundaries

### 11.1 Public commands and settings

The existing `/bfs` root, level 2 permission and existing species cap/list/info controls remain compatible. All server controls work from the owned console without a player except granting and using a follow stick, which requires an explicit player recipient. Console commands omit the leading slash. Add argument suggestions, field help and localized labels with units, bounds, effective value, source and settings revision. `*` expands only the current BFS species registry, sorted by full registry ID. It must account for all 22 species. A foreign entity ID cannot become a tunable BFS species, while the follow tool has broader scope.

| Command suffix after `/bfs debug` | Contract |
|---|---|
| `help [category]` | Explain commands, units, capability limits and session reset behavior. |
| `list [species]` | List 22 registry IDs or the selected species' fields, defaults, bounds and applicability. |
| `get <species|*> [field]` | Report effective and baseline values, override source, revision and limiting capability reason. Paginate bounded output. |
| `setspeed <species|*> <horizontal> <vertical>` | Set independent base coefficients in blocks per simulation second. |
| `setsprint <species|*> <horizontalMultiplier> <verticalMultiplier>` | Set dimensionless multipliers used once in the declared sprint states. |
| `setspawnsize <species|*> <min> <max>` | Set natural group quantity, not scale. Single quantity shorthand sets both endpoints. |
| `setscale <species|*> <min> <max>` | Set sampled physical scale bounds; a single value sets both. Existing entities preserve their normalized spawn roll. |
| `set <species|*> <field> <value...>` | Typed access to every applicable catalog setting below. Specialized commands delegate to the same transaction. |
| `reset <species|*> [field|all]` | Remove selected session overrides and reapply effective validated server defaults atomically. |
| `reload` | Revalidate existing config into a new baseline snapshot. Preserve explicit session overrides. Invalid config leaves the entire previous snapshot active. |
| `on [category] [ticks] [targets]`, `status`, `off` | Preserve capture syntax and bounded behavior in IFC-001. Capture and gameplay tuning are separate state. |
| `followme [player]`, `followme status [player]`, `followme stop [player]` | Issue marked stick, inspect lease, or release the selected target. Console requires explicit player for issuance and owner selection. |

The setting catalog is the minimum complete surface, not permission to make unsupported combat abilities universal. Defaults labelled `existing` are extracted once from current config and immutable species definitions by P001-TASK-001, normalized and exposed by `list`; they are not guessed numerical balance values. New disturbance source defaults are frozen in IFC-005. No command silently writes TOML, entity base attributes, or a named tuning file.

| Field or family | Type, unit and bound | Default and application |
|---|---|---|
| `movement.horizontal`, `movement.vertical` | Finite double, blocks per simulation second, 0 through 20 each | Existing species cruise coefficients normalized to units. Every movement adapter consumes both; zero disables powered motion on that axis, not gravity or external impulses. |
| `sprint.horizontal`, `sprint.vertical` | Finite double, dimensionless, 0 through 4; each base times multiplier must be at most 20 | Existing effective chase/flee ratio, exposed per species. No example number establishes a default. |
| `movement.acceleration` | Finite double, blocks per simulation second squared, 0.1 through 80 | Existing powered acceleration. |
| `movement.turn_rate`, `movement.pitch_rate` | Finite double, degrees per simulation second, 1 through 360 | Existing steering behavior converted from tick units. |
| `movement.pitch_limit` | Finite double, degrees, 0 through 80 | Existing species limit, constrained by body safety rather than silently restoring fixed axis ratio. |
| `recovery.stall_ticks`, `recovery.retry_ticks`, `recovery.max_attempts` | Integers, ticks 20 through 1200, ticks 1 through 200, count 1 through 16 | Existing supported controller settings. A failed route yields bounded safe recovery. |
| `spawn.enabled`, `spawn.replace_vanilla` | Boolean | Existing policy. Replacement only where current species supports it. |
| `spawn.chance` | Finite double, probability 0 through 1 | Existing natural spawn chance. |
| `spawn.cap`, `spawn.local_cap` | Integers, entities 0 through 512 | Existing cap semantics and scope exposed; zero disables corresponding natural admission. |
| `spawn.spacing` | Finite double, blocks 0 through 64 | Existing spacing, always combined with collision and valid habitat. |
| `spawn.group_min`, `spawn.group_max` | Integers, entities 1 through 32, ordered | Existing effective group defaults for each species; absent extras retain ordinary group behavior rather than assume one common group. |
| `scale.min`, `scale.max` | Finite double, multiplier 0.25 through 2, ordered | Existing range for scaled species, 1 for species with no current variation. |
| `attributes.health_multiplier`, `attributes.damage_multiplier`, `attributes.knockback_multiplier` | Finite double, multiplier 0.1 through 10 for health, 0 through 10 for damage and knockback | Existing config. Damage/knockback only for an actual supported attribute or existing combat path. Preserve current health fraction; no free heal on join. |
| `hunting.enabled`, `hunting.players`, `hunting.prey_enabled` | Boolean | Existing species policy. Preserve existing player hostility permissions and all mode/team rules. |
| `hunting.prey` | Validated set of entity registry IDs or entity type tags, at most 128 entries | Existing diet. Add/remove/replace operations validate the complete set and never target players by inserting their ID. |
| `hunting.range`, `sensing.range`, `sensing.food_range`, `sensing.threat_range` | Finite double, blocks 0 through 64 | Existing supported ranges; unloaded chunks are not force loaded. |
| `hunting.attack_cooldown`, `hunting.retarget_ticks`, `sensing.interval_ticks` | Integers, ticks 1 through 1200 | Existing cadence and cooldown. Runtime clock uses server level time and one owner, not entity age. |
| `hunting.line_of_sight` | Boolean | Existing sensing requirement. Disabling this query cannot bypass reach/collision checks on an actual attack. |
| `disturbance.enabled`, `disturbance.source.<kind>.enabled`, `disturbance.reaction` | Boolean, boolean, enum `ignore|alert|investigate` | Existing species reaction and IFC-005 for new sources. Boat tracking is Great White eligibility, not a new combat target. |
| `disturbance.radius`, `disturbance.sensitivity`, `disturbance.source.<kind>.strength` | Finite doubles, blocks 0 through 64, dimensionless 0 through 4, normalized strength 0 through 1 | Existing values where present; IFC-005 provides new source defaults and threshold rule. |
| `disturbance.interval_ticks`, `disturbance.alert_ticks`, `disturbance.source.<kind>.interval_ticks` | Integers, ticks 1 through 1200, 1 through 2400, 1 through 1200 | Existing cadence and IFC-005 new source defaults. |

Every field includes `supported`, `read_only`, or `not_applicable` plus a concrete reason. All BFS species support the mandatory two axes, sprint coefficients, group quantity and physical scale; inheritance gaps cannot become `not_applicable`. Other controls expose the actual existing behavior capability. Per entity observational state includes target, hunger/feeding state when present, active behavior, current cooldown, health, scale, spawn reason, navigation/recovery state, attack eligibility, sensed candidates and disturbance decision. Observation is not arbitrary NBT mutation. Additional existing behavior knobs discovered in the canonical species/config inventory must be mapped to this same typed catalog with existing semantics, not omitted behind a generic setting claim or converted into new gameplay.

Commands validate all target species, all pair invariants and all capabilities before committing once on the server thread. A wildcard unsupported setting fails with the sorted affected species and no mutation; the operator may target an applicable species. This differs from required universal fields, whose unsupported result is an implementation defect. On a safe scale change that cannot fit a currently loaded entity, reject the entire transaction with `BODY_NO_FIT`; do not crush, relocate or partly resize entities. Existing vanilla cap aliases use the same resolver to prevent competing override stores.

### 11.2 Components and data flow

`BfsDebugCommands` and `BfsCommands` parse into the effective species settings service. That service resolves immutable current defaults, validated existing server config and session overrides, then publishes one revision to server consumers. `BfsAquaticEntity`, `SmartWaterAnimal`, Atlantic fish paths and their renderers receive scale and motion through explicit adapters. `PitchSwimmingMoveControl`, `SharkSwimmingMoveControl`, `AquaticMovement`, `AquaticRoute` and `AquaticDepthGuidance` keep one powered motion owner. `SpeciesBehaviorEngine` reads the same snapshot for sensing, prey and cadence. `MobCapManager` and `BfsSpawnPlacements` consume group and admission values only at actual spawn gates.

Forge disturbance listeners preserve typed source identity through `WaterDisturbanceEvent`, `WaterDisturbanceHandler` and shark intent arbitration. Follow leases integrate with existing goals, vanilla Brain and SmartBrainLib without erasing unrelated state. Survival and collision safety outrank all diagnostic movement intents. Client rendering, HUD and local diagnostic classes remain isolated from common and dedicated server class loading. The existing parser and support guide evolve with emitted records. Data providers own algae blockstate/model/loot and new dive item model/language output.

### 11.3 Shared interface contracts

These signatures are normative version 1 interfaces except the retained version 2 diagnostic format. JSON blocks define field types and interface ownership; subsequent paragraphs define constraints and default semantics. An implementation may use repository conventional class names but must preserve signatures and evidence semantics. All settings and duration calculations use 20 simulation ticks per second, separately from wall clock speed under server lag.

#### IFC-001: Bounded diagnostic record and capture

```json
{
  "id": "IFC-001",
  "version": 2,
  "producer_phase": "BFS2-PHASE-000",
  "consumer_phases": ["BFS2-PHASE-001", "BFS2-PHASE-002", "BFS2-PHASE-003", "BFS2-PHASE-004", "BFS2-PHASE-005", "BFS2-PHASE-006", "BFS2-PHASE-007"],
  "requirement_ids": ["BFS2-REQ-022", "BFS2-REQ-020"],
  "acceptance_ids": ["BFS2-AC-022", "BFS2-AC-020"],
  "signature": {
    "capture": "start(category: Category, ticks: int[20,36000]=1200, targets: EntityRef[0,32]) -> CaptureResult",
    "record": {"format": "bfs-debug-v2", "schemaMinor": "int>=0", "captureId": "opaque string", "sequence": "long>=0", "tick": "long", "side": "server|client", "dimension": "registry ID", "entity": "session pseudonym|null", "entityType": "registry ID|null", "event": "bounded enum", "settingsRevision": "long|null", "intentId": "opaque string|null", "reason": "bounded enum", "data": "typed event payload"},
    "status": "status() -> enabled, side, categories, targets, remainingTicks, wallDeadline, counters, exactOutputPath",
    "stop": "stop(reason: StopReason) -> terminal completeness summary"
  }
}
```

Server categories preserve `all`, `movement`, `brain`, `combat`, `population`, `advancement`, `algae`; add `tuning`, `follow`, `disturbance`, `boat`, `dive`, and client `render` as their owning feature is introduced. Unknown categories fail. Additive fields preserve parsing of historical v2 captures; major incompatibility requires an explicit format version and fixture migration, never relabel old records. Header binds source commit, JAR SHA 256/SHA 512, dependency/config digest, host role and test ID. Unknown/new subsystem values are null with a reason, never zero masquerading as a measurement. Phase 000 instruments current paths directly and does not depend on Phase 001 settings or motion interfaces being implemented.

Default off. Existing hard limits are preserved or tightened: at most 32 targets, 36000 ticks, 30 minutes wall time, 8192 queued records, 100000 emitted records, 64 MiB per capture and 256 MiB per directory, using the smaller bound wherever the current implementation is stricter. Status reports the actual selected numerical bounds. Sample movement at most once per target every five ticks; transition records are event driven and rate limited. A bounded high detail reproduction may sample each tick for at most 200 ticks and eight targets. Queue overflow, dropped records, size stop, I/O failure or missing terminal footer yields `CAPTURE_INCOMPLETE`, never a passing assertion. No tick thread file blocking or background reads of mutable world objects. Empty selection gives a bounded world/category capture only for applicable aggregate categories; entity categories require explicit targets on console.

#### IFC-002: Effective species settings

```json
{
  "id": "IFC-002",
  "version": 1,
  "producer_phase": "BFS2-PHASE-001",
  "consumer_phases": ["BFS2-PHASE-002", "BFS2-PHASE-003", "BFS2-PHASE-007"],
  "requirement_ids": ["BFS2-REQ-002", "BFS2-REQ-003", "BFS2-REQ-004", "BFS2-REQ-005", "BFS2-REQ-006", "BFS2-REQ-007"],
  "acceptance_ids": ["BFS2-AC-002", "BFS2-AC-003", "BFS2-AC-004", "BFS2-AC-005", "BFS2-AC-006", "BFS2-AC-007"],
  "signature": {
    "resolve": "resolve(species: ResourceLocation) -> EffectiveSpeciesSettings",
    "snapshot": {"schema": "int=1", "revision": "long>=0", "species": "BFS registry ID", "fields": "sorted map<FieldId, TypedValue>", "provenance": "map<FieldId, default|server_config|session>", "capabilities": "map<FieldId, CapabilityResult>"},
    "mutate": "apply(expectedRevision: long, targets: sorted SpeciesId[], patch: TypedPatch) -> Applied(newRevision)|Rejected(reason, fields, species)",
    "reset": "reset(targets: SpeciesId[], fields: FieldId[]|all) -> Applied|Rejected"
  }
}
```

Precedence is session override over validated server config over immutable species default. Scope is one server lifetime, across its dimensions. Reload changes the baseline atomically and preserves overrides; restart drops overrides and verbose capture. Newly spawned and already loaded entities read the same effective revision. No per tick parsing or disk I/O. Attributes are recomputed from immutable bases with stable named modifiers, preserving health fraction and never compounding. Scale retains a normalized spawn roll in `[0,1]` and computes `min + roll * (max-min)`; existing saved scales migrate to the corresponding roll once and do not reroll on join. Synchronized dimensions, eye position, render scale and IFC-003 envelope derive from the same scale. Unknown future saved schemas fail safely with an actionable message; no silent destructive downgrade.

Natural group quantity includes the original accepted spawn plus admitted extras. Group selection is an inclusive seeded uniform integer from min through max, bounded by existing cap, spacing, water, collision and placement rules. Administrative `/summon`, spawn eggs and explicit test spawns remain one requested entity and do not produce hidden extras. They still receive scale and effective attributes. Distinguish group request, successful count and each admission rejection. Reload/reset do not spawn or delete existing animals. No group edit changes body size.

#### IFC-003: Motion intent and body safety

```json
{
  "id": "IFC-003",
  "version": 1,
  "producer_phase": "BFS2-PHASE-001",
  "consumer_phases": ["BFS2-PHASE-002", "BFS2-PHASE-003", "BFS2-PHASE-007"],
  "requirement_ids": ["BFS2-REQ-003", "BFS2-REQ-004", "BFS2-REQ-010", "BFS2-REQ-012"],
  "acceptance_ids": ["BFS2-AC-003", "BFS2-AC-004", "BFS2-AC-010", "BFS2-AC-012"],
  "signature": {
    "intent": {"id": "opaque string", "owner": "safety|follow|combat|boat|ordinary", "state": "idle|cruise|pursuit|flee|follow|boat_track|recovery", "target": "dimension plus Vec3 in blocks|null", "settingsRevision": "long", "horizontalBps": "finite double[0,20]", "verticalBps": "finite double[0,20]", "expiresAtTick": "long"},
    "evaluate": "evaluate(entity, proposedPose, sweptSegment, mediumPolicy) -> safePose, safeVelocity, clearance, reason",
    "clearance": {"scale": "finite double[0.25,2]", "bodyWet": "boolean", "solidClear": "boolean", "finExposure": "blocks>=0", "surfaceY": "finite double|null", "limitedAxes": "set<horizontal|vertical>"}
  }
}
```

For normalized steering direction `d`, sprint state flag `S`, base coefficients `Bh,Bv` and multipliers `Mh,Mv`, the requested powered vector in blocks per tick is `(d.x*Bh*(S?Mh:1), d.y*Bv*(S?Mv:1), d.z*Bh*(S?Mh:1))/20`. Sprint states are `pursuit` and `flee`; idle, cruise, diagnostic follow, boat tracking and recovery use base coefficients. Prey pursuit and existing retaliation pursuit count as pursuit. Alert without pursuit does not. A species without an existing pursuit/flee behavior still exposes both sprint coefficients and reports inactive state; do not invent predation to demonstrate multiplication. P001-TASK-002 exercises the adapter's explicit state input as well as natural states where present. No nested sprint multiplication, fixed hidden vertical ratio, post collision upward impulse or independent speed floor may override this formula. Current ratios are default values only.

Arrival, acceleration, turn feasibility and safety may reduce powered velocity and must report the limiting reason. External knockback, currents, gravity and vanilla physiology remain separate; actual measured displacement is never mislabeled as requested speed. A physically blocked route does not guarantee target speed. Extreme valid combinations remain finite and collision safe. The final travel writer runs once per simulation tick. Steering aligns body attitude to the anisotropic requested direction using bounded pitch/yaw changes without changing authored animation curves.

Priority is collision/breathing/survival recovery, then explicit follow lease, then existing combat, then noncombat boat interest, then ordinary navigation. Do not globally erase combat memories; suspended owned intents expire or resume through their normal validity checks. Precompute conservative per species body and dorsal fin envelopes from supplied geometry, pivots and animation extrema. Include scale, belly, snout, tail and turning/pitch sweep; AABB center or static entity box alone cannot prove mesh clearance. Use full body volume against actual fluid shapes and solid shapes, including flowing water, waterlogged obstacles, steps, slopes, ceilings and uneven local surface. Partition the body and dorsal fin so only the fin may break the surface for boat intent. No hot path vertex traversal.

Movement samples/substeps cover at most 0.25 block translation or five degrees of rotation between swept envelope checks, with at most 32 bounded steps per tick. If a proposed motion needs more work, shorten it safely and report `WORK_BUDGET`. A finite solver validates segment geometry and fluid occupancy conservatively between samples. Surface or route failure selects a safe wet waypoint or brakes within the last safe region; it never force teleports through a wall or leaves powered flight. Lack of progress for the configured stall window causes bounded replan attempts then yields ordinary safe recovery, with a terminal reason and no endless oscillation. Client interpolation and scaled render pose must remain within the accepted conservative envelope; corresponding laptop evidence is mandatory.

#### IFC-004: Temporary mob follow lease

```json
{
  "id": "IFC-004",
  "version": 1,
  "producer_phase": "BFS2-PHASE-002",
  "consumer_phases": ["BFS2-PHASE-003", "BFS2-PHASE-007"],
  "requirement_ids": ["BFS2-REQ-008", "BFS2-REQ-009"],
  "acceptance_ids": ["BFS2-AC-008", "BFS2-AC-009"],
  "signature": {
    "lease": {"version": "int=1", "token": "server issued opaque nonce", "owner": "UUID", "mob": "UUID", "dimension": "registry ID", "adapter": "capability ID", "startedTick": "long", "expiresTick": "long", "lastProgressTick": "long", "ownedIntentId": "opaque string"},
    "claim": "claim(serverPlayer, clickedEntity, markedStack, hand, interactionId) -> Lease|Rejected(reason)",
    "tick": "tick(lease, level) -> active|blocked|released(reason)",
    "release": "release(lease, reason) -> restoration result"
  }
}
```

One active follower per operator, one owner per mob, at most 32 concurrent server leases, 2400 tick lifetime, 64 block range and 10 tick route update interval. Follow destination respects a four block arrival distance plus both bodies' dimensions. A second click on the same mob releases it; a valid new selection first validates acquisition, then releases the old selection. Another operator cannot steal a lease. Stop is idempotent. The special stick stores namespaced version/token/owner fields and a clear localized label; server issuance registry and permission checks defeat forged or copied NBT as authority. The player must hold the issued stick in either hand and remain permission level 2. Both Forge interaction callbacks deduplicate the same click. Console issuance uses an explicit online recipient and never requires the owner to join for unrelated diagnostics.

All living mob families are in scope, including ground, flying, swimming, amphibious, vanilla Brain, SmartBrainLib, multipart bosses and third party controllers. A clicked dragon part resolves to its owning mob; a projectile, armor stand, item, vehicle or player is not a follower. Use registry driven capability detection and explicit movement adapters, not namespace or entity type whitelist acceptance. Boss controller arbitration and temporary suppression of owned destructive travel side effects must preserve health, phases and ordinary behavior after release. Do not disable physiology, collide through terrain, drag water breathers onto land, force ground mobs into drowning routes, mount/dismount mobs, or teleport between dimensions. No special mob can be rejected merely to make the all mobs gate pass.

P002-TASK-001 creates the compatibility inventory from the active registry, exercises vanilla goal/Brain/flying/aquatic cases, Ender Dragon and Wither special movement, and a separately loaded Forge compatibility fixture with a non BFS namespace and independent controller. It also tests an actual pinned compatible third party mob artifact selected from the existing authorized test environment or an official redistributable source, recording version, hashes, license and controller path. A test fixture alone does not establish external mod compatibility. Discover each controller's real ownership hook and implement required adapters before the phase closes. An uncooperative controller produces `ADAPTER_CONFLICT`, releases safely and remains an unresolved mandatory defect; it is not an accepted category exclusion or universal compatibility claim. Unknown future third party implementations are covered by the generic contract and tested extension path, not a claim that every future artifact has been run.

Release within one server tick on explicit stop, permission loss, no held marked stick, target/owner death or removal, logout, dimension change, unload or shutdown. Release on timeout, range excess, and after 200 ticks of blocked route without progress. Invalidate nonce on server restart. Store no live entity references across unload and no persistent follow flag that revives a lease on reload. Clear only this lease's intent/goal/modifier/memory; restore original controller ownership and unchanged unrelated brain state. Within 40 ticks after release, ordinary valid goal selection must resume in the fixture; physiology and collision never stopped.

#### IFC-005: Water disturbance and boat interest

```json
{
  "id": "IFC-005",
  "version": 1,
  "producer_phase": "BFS2-PHASE-003",
  "consumer_phases": ["BFS2-PHASE-007"],
  "requirement_ids": ["BFS2-REQ-007", "BFS2-REQ-011", "BFS2-REQ-012"],
  "acceptance_ids": ["BFS2-AC-007", "BFS2-AC-011", "BFS2-AC-012"],
  "signature": {
    "event": {"version": "int=1", "dimension": "registry ID", "worldTick": "long", "sourceId": "UUID", "sourceKind": "swim_sprint|attack|damage|block_break|fall|projectile|water_entry|water_jump|occupied_boat", "position": "Vec3 blocks", "strength": "finite double[0,1]", "boatId": "UUID|null", "riderId": "UUID|null"},
    "react": "react(event, speciesSettings) -> ignored(reason)|alert(untilTick)|investigate(intent)",
    "boatTarget": "boatTarget(boat, sharkEnvelope) -> safeBehindWaypoint|rejected(reason)"
  }
}
```

Retain current existing source defaults. New water entry/jump sources default enabled for existing eligible sharks, strength 0.5, source cooldown 20 ticks. Occupied moving boats default enabled, strength 0.5, source interval 10 ticks, horizontal movement threshold 0.02 block per tick. New source default radius is 24 blocks, sensitivity 1, alert lifetime 100 ticks; acceptance uses `strength*sensitivity >= 0.25` and distance within radius. Existing species reaction distinctions and hostility remain intact. These are bounded diagnostic starting values, not final speed balance.

Real server transitions from air to water and water jump impulse produce one deduplicated event with actual position. A fall callback requiring three blocks is insufficient. A rider out of water still permits the occupied boat source. Empty or stationary boats and motion noise below threshold do not emit occupied movement alerts. Key throttles by dimension, source UUID and source kind using world ticks. Prune on removal, dimension unload and expiry; retain at most 4096 recent source keys per level and inspect only bounded nearby eligible shark candidates, at most 64 per event. Saturation records `SOURCE_BUDGET` without an unbounded global scan. No arbitrary player NBT or projectile payload is logged.

Great Whites may acquire eligible moving occupied boats as noncombat interest. Target the point behind actual horizontal travel direction by at least boat half length plus scaled shark half length plus two blocks, with no more than 20 ticks prediction. Heading turn follows existing safe turn bounds. Body remains fully submerged; dorsal fin exposure is positive only if the current depth and route permit it. No attack, damage, rider ejection or boat destruction follows from this state. Reevaluate each 10 ticks. Within 20 ticks of dismount, removal, wrong dimension or unsafe route, abandon the boat intent and recover to safe water. Stationary boats expire interest after 100 ticks. Multiple eligible boats select nearest safe candidate with stable UUID tie break; retain a valid target to avoid oscillation. Follow and survival intent priority remains IFC-003.

#### IFC-006: Dive equipment state and synchronization

```json
{
  "id": "IFC-006",
  "version": 1,
  "producer_phase": "BFS2-PHASE-006",
  "consumer_phases": ["BFS2-PHASE-007"],
  "requirement_ids": ["BFS2-REQ-017", "BFS2-REQ-018", "BFS2-REQ-019"],
  "acceptance_ids": ["BFS2-AC-017", "BFS2-AC-018", "BFS2-AC-019"],
  "signature": {
    "persistent": {"schema": "int=1", "remainingTicks": "int[0,6000]", "initialized": "boolean"},
    "runtime": {"revision": "long>=0", "fullSuit": "boolean", "submergedEyes": "boolean", "waterContact": "boolean", "movementMode": "vanilla|seabed", "oxygenMode": "air|protected|empty|external_breathing|exempt", "jumpLatched": "boolean", "lastConsumptionTick": "long"},
    "sync": "server -> owning client: schema, revision, remainingTicks, eligibility, movementMode, oxygenMode",
    "apply": "apply(player, equipment, fluid, inputs, tick) -> one movement decision plus oxygen transition"
  }
}
```

Register `bensfintasticsharks:dive_helmet`, `dive_chestplate`, `dive_leggings`, `dive_boots` with supplied item PNG bytes, normal armor slots, natural names, visible creative availability and command access. No crafting or cooking recipe. As conservative equipment defaults, use vanilla leather tier durability and defense for corresponding slots, no armor toughness or knockback bonus, leather equip sound and enchantability, and no new repair ingredient or special combat set bonus. Ordinary same item anvil combination remains permitted; existing Prismarine material and effects are unchanged. The suit benefit belongs to its requested underwater behavior.

The worn source is the supplied 1280 by 1280 JPEG. Preserve original file identity and decoded RGB appearance in a deterministically decoded PNG if required by the renderer. JPEG has no authored alpha. Do not remove a guessed background, hallucinate transparency, repaint pixels, downsample, upscale, or call this a supplied mesh. P006-TASK-001 maps the actual atlas regions to compatible armor geometry, documenting normalized UV rectangles, piece/limb assignments, texture dimensions, seam orientation and unused atlas regions. Validate all UVs within `[0,1]`, no accidental region bleed or mirrored lettering/details, texture and geometry load, matching left/right limbs and slot visibility, and no unintended full opaque sheet on the model. Geometry is newly authored only to carry the supplied atlas on the equipment, not a visual redesign. Verify front/back/side, walk/jump/crouch, all four slots, common player arm variants and reload. Any ambiguous art mapping is resolved by the atlas and worn proof, not invented lost source geometry.

Seabed mode requires all four exact pieces, survival or adventure mode, and body water contact. Dry travel, creative and spectator remain vanilla. Full suit water travel uses one shared client/server eligibility predicate and one scoped movement writer: land style horizontal inputs, real gravity 0.02 block per tick squared, grounded jump impulse 0.24 block per tick, bounded terminal downward speed 0.3 block per tick, normal collision and external impulses. One jump per rising edge while actually grounded; holding jump cannot swim upward, repeat jumps in midair or retrigger on landing until released. No fake `onGround`, flight permission or player teleport. Buoyant apex and landing must be smooth. Partial set, death, creative/spectator transition and dry exit remove owned modifiers immediately. Server authority corrects invalid client states without sustained rubber banding. Forge events and existing hooks are preferred; if they cannot replace the water travel branch exactly once, P006-TASK-001 documents the missing hook and uses the narrowest player only injection, with dedicated server class loading and unchanged nonsuit travel proof.

The reservoir belongs to the player, not armor NBT. A new player initializes to 6000 ticks once; an existing saved missing state initializes once at migration and records the event. A charged full suit protects exactly 6000 submerged simulation ticks at 20 TPS. Consumption occurs once per authoritative tick with eyes submerged and no independent breathing immunity; ticks 1 through 6000 prevent drowning, tick 6001 starts normal drowning countdown from vanilla air zero, not an extra full vanilla air reserve. When empty, seabed movement continues. Partial suits consume no stored reserve and receive ordinary breathing; reequipping neither refills nor resets. Persist remaining ticks through death clone, logout/login, dimension transfer and server restart. Missing or corrupt established state clamps conservatively to zero and reports a repair reason, never grants a new full charge.

Refill is 20 reserve ticks per simulation tick only while eyes are in genuinely breathable air, restoring an empty reservoir in 300 ticks. It occurs regardless of worn pieces so surfacing is the refill action; equip is not. Water Breathing, Respiration and creative/spectator immunity cannot manufacture real air: Water Breathing or equivalent independent immunity pauses reserve consumption and preserves its vanilla effect; Respiration does not extend the suit's exact 6000 protected ticks. Bubble columns may provide their vanilla breathing behavior but do not refill this reserve while eyes remain submerged. At exhaustion, independent active breathing effects retain their ordinary effect. Death copies charge, and respawning into real air begins ordinary refill rather than a death reset.

Use an explicit localized oxygen readout while the suit is relevant, displaying remaining seconds from ticks, empty status and refill status. Preserve vanilla air/drowning feedback and distinguish the reserve from vanilla 300 tick air. Synchronize immediately on mode/equipment transition, login and dimension join, plus at most once every 20 ticks for steady depletion; reject stale revisions on client. This is server to owning client state, with no client writable oxygen payload.

Remove only real water and airborne mining penalties while seabed mode applies. Preserve tool suitability, enchantments, Mining Fatigue, effects, block hardness and protection. Block placement keeps vanilla reach, orientation, collision and permission checks. If vanilla has no water placement delay to remove, demonstrate parity rather than add a placement speed bonus. Land and underwater matched tool/effect/block cases, grounded and during a suit jump, must have equal work rate after removal of only water/airborne factors.

#### IFC-007: Algae state, growth and generation

```json
{
  "id": "IFC-007",
  "version": 1,
  "producer_phase": "BFS2-PHASE-005",
  "consumer_phases": ["BFS2-PHASE-006", "BFS2-PHASE-007"],
  "requirement_ids": ["BFS2-REQ-014", "BFS2-REQ-015", "BFS2-REQ-016"],
  "acceptance_ids": ["BFS2-AC-014", "BFS2-AC-015", "BFS2-AC-016"],
  "signature": {
    "attachment": {"faces": "nonempty set<north|south|east|west|down>", "waterlogged": "boolean=true"},
    "column": {"segment": "single|body|top", "age": "int[0,25]", "waterlogged": "boolean=true"},
    "place": "place(context, existingState) -> validState|rejected(reason)",
    "generate": "generate(seed, oceanFloor, sourceWaterColumn) -> bounded placement summary"
  }
}
```

Face names describe the attachment direction toward support: `down` means attachment to the top face of the block below. No underside/ceiling attachment is requested. The existing small algae ID becomes a waterlogged multiface block restricted to supported side/top placement. Placement requires water, preserves fluid, adds only the clicked valid face and consumes one item only on a new face. Breaking the cell yields one normal algae item, not one per face; removing support removes invalid faces and preserves valid faces, restoring water if the last face is lost.

Large green and red retain their public block/item IDs using `single|body|top` states and one associated item per segment. Existing singleton states map to valid `single` with water preserved. Manual stacking, top random growth and bonemeal extend into a valid source water cell only, maximum total column height eight. Random growth chance is 0.14 per eligible random tick, stops at age 25 or the height cap; bonemeal extends one valid cell and fails without consumption when blocked. Only the top grows. Top/middle/base harvest and support loss preserve lower supported segments, remove invalid upper segments, restore water and yield exactly one allowed item per removed segment, respecting ordinary tool/loot context. No duplicate update drops.

Natural placement uses existing tagged ocean biomes and valid exposed ocean floor, with bounded patch attempts and column heights two through eight limited by actual water depth. At least one source water cell remains above the top. Red algae requires an unbroken source water column from the candidate to the actual open surface, with no solid roof or occupied obstruction above that surface in the heightmap check. Biome membership alone is insufficient. This natural cave predicate does not prohibit manual cave decoration. No existing chunks are retroactively repopulated.

Choose two bounded patch attempts for each existing one attempt placement opportunity, retain existing biome distribution and seed determinism, at most 16 candidate cells per patch and eight vertical cells per column. Fixed seed ordinary open ocean samples must show at least 1.5 times the baseline occupied algae cells and a majority of naturally generated large columns at least two cells tall, while enclosed red cave fixtures produce zero placements. If collisions or depth prevent that evidence, repair placement selection within these resource bounds rather than bypass water or cave rules. Preserve original green ten frame and red nine frame animation metadata, four ticks per frame and no interpolation; do not add particles, light emission or unrelated ecology.

#### IFC-008: Evidence and integration identity

```json
{
  "id": "IFC-008",
  "version": 1,
  "producer_phase": "BFS2-PHASE-000",
  "consumer_phases": ["BFS2-PHASE-001", "BFS2-PHASE-002", "BFS2-PHASE-003", "BFS2-PHASE-004", "BFS2-PHASE-005", "BFS2-PHASE-006", "BFS2-PHASE-007"],
  "requirement_ids": ["BFS2-REQ-001", "BFS2-REQ-020", "BFS2-REQ-021"],
  "acceptance_ids": ["BFS2-AC-001", "BFS2-AC-020", "BFS2-AC-021"],
  "signature": {
    "evidence": {"schema": "int=1", "requirementIds": "stable ID[]", "taskIds": "stable ID[]", "sourceCommit": "git object ID", "artifactSha256": "hex string", "artifactSha512": "hex string", "dependenciesDigest": "hex string", "configDigest": "hex string", "fixtureId": "string", "seed": "long|null", "world": "sanitized instance ID|null", "hostRoles": "headless_server|laptop_client[]", "tickWindow": "start,end|null", "result": "passed|failed|unverified", "cleanup": "complete|incomplete"},
    "integrate": "verified phase branch -> checked GitHub merge commit on 1.20.1 -> verified resulting default -> signed annotated phase tag"
  }
}
```

Preserve historical evidence as history. Baseline source `692962f25b5b5eda65cc41fe1073c316668fd4c0` and JAR SHA 256 `c0c1f50ade56a35ccc7590d8196605467634ae86483987163f2a08f613939ff4` remain separate from rejected source `89a8daf03976cc3005da65e90418a687ccf34b5d` and delivered JAR `ac509f173563582485bb041f08cf75c827200110e2a73fb6b5b214f80906872a`. Original artifact `94bfe2c8b45fc54505d42b63d026e203086959c34bb28325da3cf762c46578bd` retains its original identity. The failed 22400 tick prefix with 1833 exceedances cannot pass a 36000 tick nearest rank p95 gate allowing only 1800 exceedances. Never splice a new candidate into that capture.

Performance acceptance uses the existing installed production Forge harness, seed 240024, all 22 species, ordinary target 63 and doubled target 126, actual predation and identical replenishment policy, no connected player, 2400 warmup and 36000 contiguous measured ticks per case. Run baseline and candidate at both populations with exact environment parity. Candidate p95 must be at most 110 percent of its paired baseline in both populations. The retained ordinary reference p95 is 6.944770 ms and unchanged limit 7.639247 ms; under the retained environment the new candidate must also meet that original limit. Report mean/p50/p95/p99, heap trend, population time series, death/replenishment and work counters. Lower actual workload, missing species, incomplete capture, duplicate/missing ticks, altered observer, warmup mismatch or hidden population churn invalidates comparison. Profiling is separate from acceptance. Preserve workload and behavior rather than disable predation, cap counts or delete expensive safety checks to meet the threshold.

### 11.4 Failures and trust boundaries

| Reason family | Meaning, retry and safe result |
|---|---|
| `PERMISSION_DENIED`, `INVALID_STICK`, `OWNER_CONFLICT` | Reject without mutation. Only authorized command source and server validated interactions can create changes. |
| `INVALID_VALUE`, `UNKNOWN_SPECIES`, `UNKNOWN_FIELD`, `NOT_APPLICABLE`, `REVISION_CONFLICT` | Atomic input rejection with exact field and bounds. Refresh readback before retry. Never silently clamp command input. |
| `BODY_NO_FIT`, `SOLID_BLOCKED`, `WATER_ENVELOPE`, `SURFACE_UNSAFE`, `WORK_BUDGET` | Hold or shorten motion safely, explain actual limitation, retry at bounded cadence. A scale transaction rejects entirely when no fit is possible. |
| `NO_ROUTE`, `ADAPTER_CONFLICT`, `LEASE_EXPIRED`, `OWNER_GONE`, `TARGET_GONE`, `WRONG_DIMENSION` | Stop only owned intent and release lease. Adapter conflict remains a required compatibility defect. |
| `THROTTLED`, `OUT_OF_RANGE`, `REACTION_DISABLED`, `SOURCE_BUDGET`, `NO_SAFE_BOAT_ROUTE` | Observable bounded disturbance decision, no fabricated reaction or unbounded rescan. |
| `SCHEMA_UNSUPPORTED`, `STATE_CORRUPT`, `CONFIG_REJECTED` | Keep previous validated settings; conservatively recover player reserve and report repair. Preserve source data for diagnosis, not silent reset. |
| `CAPTURE_INCOMPLETE`, `IO_FAILURE`, `LIMIT_REACHED` | Stop capture safely, mark evidence unusable for a passing result and retain a minimal sanitized failure. Gameplay continues unchanged. |
| `ENVIRONMENT_UNAVAILABLE`, `ARTIFACT_MISMATCH`, `CHECK_FAILED`, `CLEANUP_INCOMPLETE` | Leave the affected gate open. Recover the exact capability or fixture, then rerun from the earliest invalidated dependency. |

No arbitrary NBT, player names, chat, credentials or private network endpoints enter public captures. Bounds apply before allocation and registry resolution. Follow tokens and owner identity stay server side except the minimum issued marker; logs use per session pseudonyms. Changes are serialized on the logical server. Clients cannot write species settings, targets, charge or test outcomes. Command aliases, packets and Forge callbacks must share permission and validation boundaries. No new network channel for remote administration or automatic support upload is authorized.

## 12. Requirements

Each requirement is declared once here. Its canonical implementation phase is unique; other phases contribute regressions and final evidence without owning a duplicate requirement. All acceptance criteria are target criteria, not executed results.

### BFS2-REQ-001 — Current source and truthful open work

**Behavior:** Establish the retained PR29 source, current default ancestry and protected local state as a trustworthy prerequisite. Reconcile the complete PR29 and issue28 inventory into truthful completed provenance work, failing performance work and exact owned actions; preserve the original evidence.  
**Owner:** Repository integration  
**Canonical phase:** BFS2-PHASE-000  
**Contributors:** Repository integration and final regression verification as referenced in the task IDs.  
**Dependencies:** none  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P000-TASK-001

**Acceptance criteria**

- **BFS2-AC-001:** Baseline, branch, remote, artifact provenance and protected working changes are recorded before edits. Every actionable PR29 and issue28 finding has a verified current disposition and a mandatory Phase 000 work owner. Original failure and artifact identities remain unchanged. This prerequisite is complete after baseline reconciliation, before diagnostics and performance implementation; it does not require their future pass or the phase merge. Old plan deletion is not a performance pass.

**Required evidence**

- Read complete PR, issue and review state, source and artifact manifests, Git ancestry and exact diff. Retain the baseline reconciliation record and owned action mapping. The complete performance pass, PR29 merge, issue closure, resulting default verification and signed tag remain mandatory in BFS2-REQ-020 and P000-TASK-005 at the Phase 000 exit; completing this prerequisite does not waive any of those gates.

### BFS2-REQ-002 — Discoverable atomic session tuning

**Behavior:** Implement IFC-002 and the command surface in Section 11.1 for manual tuning and readback.  
**Owner:** Settings service  
**Canonical phase:** BFS2-PHASE-001  
**Contributors:** Command and settings service and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-001, BFS2-REQ-022  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P001-TASK-001, P001-TASK-003, P001-TASK-005

**Acceptance criteria**

- **BFS2-AC-002:** Help and suggestions expose each ID, supported field, unit, bound and source. Valid wildcard changes affect all 22 species once. Invalid value, unsupported member, stale revision and denied permission change nothing. Reset and config reload produce declared state; restart clears overrides.

**Required evidence**

- Real dispatcher tests from console and authorized/unauthorized sources; unit transaction tests; loaded/new species convergence, reset/reload/restart evidence and readable help capture.

### BFS2-REQ-003 — Independent base movement axes

**Behavior:** Every BFS species receives separate horizontal and vertical base coefficients at the actual powered integrator.  
**Owner:** Species movement  
**Canonical phase:** BFS2-PHASE-001  
**Contributors:** Species movement adapters and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-002  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P001-TASK-002, P001-TASK-004

**Acceptance criteria**

- **BFS2-AC-003:** In sufficiently open water after settling, each axis change modifies its commanded powered coefficient without changing the other coefficient. All 22 registry entries map to exercised movement paths, including sharks, fish, Atlantic fish, stingray, seal and bottom dwellers. Zero, minimum, maximum and anisotropic combinations stay finite. Temporary safety limits are reported rather than hidden fixed ratios.

**Required evidence**

- Adapter inventory, equation tests and real travel/behavior fixtures for each path and all species; paired unobstructed trajectories with desired, powered and external velocity separated. Natural locomotion states and inactive axis intent are distinguished.

### BFS2-REQ-004 — State bounded sprint multiplication

**Behavior:** Apply each sprint multiplier exactly once in pursuit or flee, and base coefficients in other states.  
**Owner:** Species movement  
**Canonical phase:** BFS2-PHASE-001  
**Contributors:** Species movement adapters and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-002, BFS2-REQ-003  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P001-TASK-002, P001-TASK-004

**Acceptance criteria**

- **BFS2-AC-004:** The IFC-003 formula holds before reported acceleration/safety constraints. Enter/exit pursuit, retaliation pursuit, flee and idle/cruise transitions neither stack nor retain a multiplier. Follow, boat and recovery consume base axes. A species with no natural sprint behavior reports inactivity truthfully.

**Required evidence**

- Unit equation and transition tests, actual chase/flee fixtures for existing eligible species, explicit adapter state coverage for every species and reset/reload regression.

### BFS2-REQ-005 — Group quantity distinct from body size

**Behavior:** Implement inclusive min/max natural group controls for every BFS species with IFC-002 spawn semantics.  
**Owner:** Spawn admission  
**Canonical phase:** BFS2-PHASE-001  
**Contributors:** Spawn admission and settings service and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-002  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P001-TASK-003, P001-TASK-004

**Acceptance criteria**

- **BFS2-AC-005:** Requested group counts respect caps, chance, spacing, valid habitat and collision; rejected extras explain their gate. Summon and spawn eggs create one requested mob. Min equals max yields that requested group subject to admission. Reset neither deletes nor creates mobs, and group edits do not change dimensions.

**Required evidence**

- Actual finalize spawn and natural placement entry fixtures, cap/spacing boundary populations, administrative negative cases and source reason capture for all 22 species.

### BFS2-REQ-006 — Comprehensive existing species controls

**Behavior:** Expose existing behavior controls in the complete catalog with per species capability and effective state.  
**Owner:** Species tuning  
**Canonical phase:** BFS2-PHASE-001  
**Contributors:** Settings, species policy, attributes and scale and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-002, BFS2-REQ-003, BFS2-REQ-004, BFS2-REQ-005  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P001-TASK-001, P001-TASK-003, P001-TASK-004

**Acceptance criteria**

- **BFS2-AC-006:** Spawn, scale, health/damage/knockback, hunting/prey, attack cooldown, sensing and recovery settings reach actual owners. Universal scale matches dimensions, eye/render scale and body envelope. Loaded/new entities and repeated join/reload show no attribute multiplication or healing. Unsupported combat abilities remain explicitly unavailable without inventing gameplay. New disturbance source behavior is owned by BFS2-REQ-007.

**Required evidence**

- Capability manifest compared to existing config/species fields, actual policy/cooldown/prey tests, repeated save/load and join with fractional health, scale collision rejection and silent laptop visual dimensions proof.

### BFS2-REQ-007 — Disturbance tuning and explanations

**Behavior:** Extend IFC-002 with every IFC-005 source, radius, sensitivity, rate, eligibility and reaction field.  
**Owner:** Disturbance policy  
**Canonical phase:** BFS2-PHASE-003  
**Contributors:** Disturbance settings and reaction policy and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-002, BFS2-REQ-006  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P003-TASK-001, P003-TASK-002, P003-TASK-004

**Acceptance criteria**

- **BFS2-AC-007:** Each configured field appears in help and effective readback and changes the corresponding decision in a real source fixture. Boundary threshold, disabled source, species ineligibility, cooldown and range have distinct reasons. Wildcard errors remain atomic; reset and reload restore declared behavior.

**Required evidence**

- Dispatcher transaction tests and event-to-reaction server tests around each threshold, with real events as the positive stimulus and explicit negative source/species/cooldown cases.

### BFS2-REQ-008 — All mob temporary following

**Behavior:** The server issued marked stick selects mobs across all families and controls temporary safe movement through IFC-004.  
**Owner:** Follow controller  
**Canonical phase:** BFS2-PHASE-002  
**Contributors:** Follow interactions and controller adapters and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-002, BFS2-REQ-003, BFS2-REQ-010, EXT-001  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P002-TASK-001, P002-TASK-002, P002-TASK-004

**Acceptance criteria**

- **BFS2-AC-008:** Real right click acquires exactly one lease and mob follows a moving owner around a valid obstacle. Goal, Brain, SmartBrainLib, aquatic, flying, ground, amphibious, multipart boss and actual third party paths are exercised. Players and nonmob entities are rejected; a boss part resolves to its mob. Forged NBT, permission loss and duplicate hand callbacks cannot grant or duplicate control. Special controller failure keeps acceptance open.

**Required evidence**

- P002-TASK-001 compatibility experiment and manifest, real server interaction tests with independently controlled non BFS fixture and an actual pinned third party artifact, Ender Dragon/Wither cases, and laptop right click/input proof.

### BFS2-REQ-009 — Follow restoration and lifecycle

**Behavior:** Release only owned state and restore ordinary controller behavior on every IFC-004 termination path.  
**Owner:** Follow controller  
**Canonical phase:** BFS2-PHASE-002  
**Contributors:** Follow lease lifecycle and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-008  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P002-TASK-003, P002-TASK-004

**Acceptance criteria**

- **BFS2-AC-009:** Stop, repeated click, reselection, lost stick, lost permission, death, logout, dimension change, unload, shutdown, timeout, range excess and route failure meet release bounds. Competing owners cannot steal. No stale references or revived lease persists after restart. Normal valid goal selection resumes within 40 ticks and unrelated memories/health remain intact.

**Required evidence**

- Real lifecycle and serialization tests for each adapter family, two operator conflict, blocked route timer, repeated acquire/release and same server tick removal; actual reconnect evidence for client state.

### BFS2-REQ-010 — Whole body shark movement safety

**Behavior:** Use IFC-003 scaled body and fin envelopes for pursuit, turn, pitch, surface, seabed and recovery.  
**Owner:** Aquatic movement  
**Canonical phase:** BFS2-PHASE-001  
**Contributors:** Aquatic movement and renderer contract and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-003, BFS2-REQ-004, BFS2-REQ-006, EXT-001  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P001-TASK-002, P001-TASK-004

**Acceptance criteria**

- **BFS2-AC-010:** No accepted powered route intersects solid geometry or exposes the body outside water. Upward prey pursuit near a real surface either follows safely or yields a bounded recovery, never stalls indefinitely or flies. All supported scale/speed extrema remain finite, preserve ordinary fluid/impulse behavior and match the actual rendered mesh envelope.

**Required evidence**

- Server swept geometry/property tests plus live pursuit fixtures at open surface, shallow seabed, slope, wall, ceiling, flowing fluid and waterlogged obstruction. Silent laptop proof includes snout/tail/belly and interpolated pitch at minimum/default/maximum scales and high valid anisotropic speed.

### BFS2-REQ-011 — Actual jumping and boats alert sharks

**Behavior:** Produce water entry, water jump and occupied moving boat disturbances through their real server paths.  
**Owner:** Water disturbance listeners  
**Canonical phase:** BFS2-PHASE-003  
**Contributors:** Water disturbance listeners and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-007  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P003-TASK-001, P003-TASK-004

**Acceptance criteria**

- **BFS2-AC-011:** A shallow jump or water entry produces an accepted event without a three block fall; a dry seated rider in a moving boat retains boat identity and produces one event per permitted interval. Empty/stationary boats, duplicated callbacks, source removal and different dimensions yield declared negative outcomes. Repeated producers remain within map/work bounds.

**Required evidence**

- Actual server transition and moving boat event fixtures, not only direct handler calls or forced disturbance commands; bounded source/removal/load tests and one actual laptop ride/jump sequence for input integration.

### BFS2-REQ-012 — Safe Great White boat interest

**Behavior:** Track eligible boats from behind under IFC-005 while preserving body submersion and only safe fin exposure.  
**Owner:** Shark intent arbitration  
**Canonical phase:** BFS2-PHASE-003  
**Contributors:** Shark intent arbitration and surface route and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-010, BFS2-REQ-011, BFS2-REQ-009, EXT-001  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P003-TASK-003, P003-TASK-004

**Acceptance criteria**

- **BFS2-AC-012:** Positive deep open water case shows body submerged and fin visible behind the boat. Abrupt turns, shore approach, obstruction, shallow depth, dismount, stop, removal, multiple boats and competing follow/combat intents follow bounded selection/release rules. No boat damage, rider ejection or water safety bypass occurs.

**Required evidence**

- Real event-to-intent server chain, trajectory/clearance/priority assertions and paired laptop fin/body/boat visuals under actual player boat control; recovery and no damage evidence.

### BFS2-REQ-013 — Original Zippy lightning and glow

**Behavior:** Restore existing authored markings at normal light and intended glow in darkness without changing supplied pixels.  
**Owner:** Common Thresher rendering  
**Canonical phase:** BFS2-PHASE-004  
**Contributors:** Common Thresher rendering and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-022, EXT-001  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P004-TASK-001, P004-TASK-002, P004-TASK-003

**Acceptance criteria**

- **BFS2-AC-013:** Zippy retains visible original lightning before darkness, during darkness, after daylight return and after resource reload. No missing texture, opaque mask background or changed variant occurs. Other Thresher skins and existing Great White glow retain behavior.

**Required evidence**

- Exact texture hash and layer/resource tests, diagnostics proving actual variant/layer/brightness choice, and silent laptop targeted daylight/dark/reload screenshots with preserved original art comparison.

### BFS2-REQ-014 — Small underwater face algae

**Behavior:** Implement IFC-007 supported side/top multiface algae with existing ID and water preservation.  
**Owner:** Algae blocks  
**Canonical phase:** BFS2-PHASE-005  
**Contributors:** Algae block state and data providers and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-022  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P005-TASK-001, P005-TASK-004

**Acceptance criteria**

- **BFS2-AC-014:** Actual placement adds valid faces underwater and rejects dry, unsupported or duplicate face placement. Support removal retains valid faces, last face loss restores water, and one normal item drops on cell harvest without per face duplication. Existing singleton save state remains valid.

**Required evidence**

- Real player placement/interaction paths, fluid/support neighbor updates, loot and old state load fixtures, generated blockstate/model consistency and laptop side/top visual check.

### BFS2-REQ-015 — Tall stackable algae columns

**Behavior:** Implement both green and red column growth, manual stacking, harvest and support rules in IFC-007.  
**Owner:** Algae columns  
**Canonical phase:** BFS2-PHASE-005  
**Contributors:** Large algae block lifecycle and data providers and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-014  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P005-TASK-002, P005-TASK-004

**Acceptance criteria**

- **BFS2-AC-015:** Player stacking and natural growth form columns of multiple cells with correct top/body state and cap. Bonemeal, blocked growth, age limit and fluid conditions follow the contract. Breaking top/middle/base or support yields correct supported remainder, water and exact drops after save/load and repeated updates.

**Required evidence**

- Actual use/random tick/bonemeal/neighbor and loot paths, singleton migration, maximum column and obstruction fixtures, generated output checks and laptop stacked rendering.

### BFS2-REQ-016 — Richer animated algae without red cave generation

**Behavior:** Produce bounded richer ocean patches and natural columns while excluding enclosed red caves and preserving authored animation.  
**Owner:** Algae generation  
**Canonical phase:** BFS2-PHASE-005  
**Contributors:** Algae placement feature and generated world data and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-014, BFS2-REQ-015  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P005-TASK-003, P005-TASK-004

**Acceptance criteria**

- **BFS2-AC-016:** Fixed seed comparisons meet IFC-007 density and height criteria with zero natural red placements in enclosed cave fixtures. Only new chunks receive generation. Occupied cells, structures and fluids remain protected. Green/red frame counts, timing and pixels remain unchanged and animate in the client.

**Required evidence**

- Baseline/candidate worldgen counts on matched seeds and biomes, open surface and enclosed/roofed cave counterexamples, bounded work assertions, generated resource drift and silent laptop animation proof.

### BFS2-REQ-017 — Four dive items and faithful worn atlas

**Behavior:** Create the four IFC-006 armor items using exact supplied item PNGs and the supplied JPEG worn art with validated compatible geometry/UVs.  
**Owner:** Dive equipment  
**Canonical phase:** BFS2-PHASE-006  
**Contributors:** Item registration, models and armor renderer and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-022, EXT-001  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P006-TASK-001, P006-TASK-002, P006-TASK-005

**Acceptance criteria**

- **BFS2-AC-017:** Four item bytes and JPEG source hash remain identical. Any decoded PNG matches decoded source RGB exactly. Slot models render mapped atlas regions without fabricated alpha, painted replacement details or missing texture. UV bounds, seams, left/right limb and player arm variants pass, with zero dive crafting recipes.

**Required evidence**

- Source identity manifest, decoded pixel comparison and normalized UV table, model/resource/load tests, package recipe absence and silent laptop front/back/side plus movement/reload rendering proof.

### BFS2-REQ-018 — Seabed walking and work parity

**Behavior:** Full suit water contact enables IFC-006 movement and removes only actual underwater work penalties.  
**Owner:** Player movement  
**Canonical phase:** BFS2-PHASE-006  
**Contributors:** Player movement, mining and placement integration and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-017, EXT-001  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P006-TASK-001, P006-TASK-003, P006-TASK-004, P006-TASK-005

**Acceptance criteria**

- **BFS2-AC-018:** Full suit survival/adventure walks on seabed, jumps with the specified finite buoyant arc, never enters swimming pose or gains held key ascent, and applies travel once. Dry, partial, creative and spectator restore vanilla. Equal tools/blocks/effects show land/submerged work parity while harvest, Mining Fatigue and placement permissions remain intact. Empty oxygen retains movement.

**Required evidence**

- Early real hook experiment followed by actual server player travel/mining/placement tests, protected block negative cases and silent laptop inputs, pose, apex/landing, reconnect and no sustained correction proof.

### BFS2-REQ-019 — Exactly five minutes of protected submerged oxygen

**Behavior:** Implement IFC-006 charge, depletion, empty drowning, real air refill, persistence and synchronized feedback.  
**Owner:** Player reservoir  
**Canonical phase:** BFS2-PHASE-006  
**Contributors:** Player persistent reservoir and client feedback and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-017, BFS2-REQ-018, EXT-001  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P006-TASK-004, P006-TASK-005

**Acceptance criteria**

- **BFS2-AC-019:** A full initial charge supplies exactly 6000 protected submerged ticks, with normal drowning countdown after exhaustion and no extra full vanilla reserve. Tick 5999/6000/6001 boundaries, partial swaps, replacement armor, reconnect, restart, death clone, dimension transfer and corruption do not reset charge. Real air refill is 20 ticks of reserve per simulation tick; bubble columns and equip do not refill. External breathing/Respiration and exempt modes follow declared compatibility.

**Required evidence**

- Real tick and breathe event tests with exact counters, persisted restart/death/dimension fixtures, adversarial equipment cycles and actual laptop HUD/reconnect synchronization. Prove no charge changes from client input.

### BFS2-REQ-020 — Measured performance recovery

**Behavior:** Repair evidenced performance causes and pass the complete installed Forge comparison in IFC-008.  
**Owner:** Performance verification  
**Canonical phase:** BFS2-PHASE-000  
**Contributors:** Performance analysis and affected behavior owners and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-001, BFS2-REQ-022  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P000-TASK-003, P000-TASK-004, P000-TASK-005

**Acceptance criteria**

- **BFS2-AC-020:** Profile navigation, collision/recovery, sensing, species policy, stingray contact, allocation and replenishment separately. Keep predation and actual target populations. Complete four captures with 2400 warmup and 36000 contiguous measured ticks each and unchanged p95 threshold; no partial prefix, altered workload or disabled safety counts as a pass. Issue28 closes only after evidence and approved merge.

**Required evidence**

- Exact original failure identity plus new source/JAR/dependency/config/harness manifests, profiler attribution, ordinary/doubled baseline/candidate raw tick completeness summaries, percentiles, population/death/work data and installed server logs.

### BFS2-REQ-021 — Integrated regression and verified testing artifact

**Behavior:** Close the complete pass at DEC-003 with current source, matched production Forge runtimes and preserved previous content.  
**Owner:** Repository integration  
**Canonical phase:** BFS2-PHASE-007  
**Contributors:** Repository release and verification and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-001, BFS2-REQ-002, BFS2-REQ-003, BFS2-REQ-004, BFS2-REQ-005, BFS2-REQ-006, BFS2-REQ-007, BFS2-REQ-008, BFS2-REQ-009, BFS2-REQ-010, BFS2-REQ-011, BFS2-REQ-012, BFS2-REQ-013, BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016, BFS2-REQ-017, BFS2-REQ-018, BFS2-REQ-019, BFS2-REQ-020, BFS2-REQ-022, EXT-001, EXT-002  
**Lifecycle stage:** post_change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P007-TASK-001, P007-TASK-002, P007-TASK-003, P007-TASK-004, P007-TASK-005, P007-TASK-006

**Acceptance criteria**

- **BFS2-AC-021:** Every mandatory phase is merged and tagged, every cross feature regression passes, performance is remeasured on the final artifact, and the delivered JAR matches the verified merged source and checksums. Fishing, Prismarine armor, advancement behavior, Cod/Salmon repairs, Oceanic fin mapping and authored assets remain intact. Documentation states actual behavior and limits. No public publication or optional scope is required.

**Required evidence**

- Complete evidence index, full affected unit/GameTest/data/resource/package suites, installed server and silent laptop final candidate evidence, default branch verification, signed tags, sanitized support packet, exact delivered JAR SHA 256/SHA 512 and cleanup confirmation.

### BFS2-REQ-022 — Reusable diagnostics before dependent tests

**Behavior:** Deliver IFC-001 core before performance or new feature tests, then extend it locally in each owning feature phase.  
**Owner:** Diagnostics  
**Canonical phase:** BFS2-PHASE-000  
**Contributors:** Diagnostic manager, parser and support guide and final regression verification as referenced in the task IDs.  
**Dependencies:** BFS2-REQ-001  
**Lifecycle stage:** change  
**Production verification:** none  
**Release impact:** stable release  
**Task IDs:** P000-TASK-002, P000-TASK-004, P001-TASK-004, P002-TASK-004, P003-TASK-004, P004-TASK-003, P005-TASK-004, P006-TASK-005, P007-TASK-002

**Acceptance criteria**

- **BFS2-AC-022:** On/status/off works on the owned server console without a player; denial, timeout, removed target, reload, writer overflow, redaction and completeness checks are executable. Disabled capture adds no trace allocations, scans, file writes or packets. Enabled bounded captures preserve gameplay decisions and meet measured overhead. Each feature adds its typed signals before its local proof. Final all feature coverage is a BFS2-REQ-021 contribution, not a prerequisite for Phase 000.

**Required evidence**

- Core real dispatcher/manager/parser tests and off/on behavior parity in Phase 000; local extension and support procedure evidence in Phases 001 through 006; final installed artifact replay in Phase 007.

## 13. Phased Roadmap

The complete global sequence is frozen below. Each linked file owns the sole full phase declaration and execution blueprint. Every phase has the repository maintainer as accountable owner and the named component scope. New phase branches start only after the preceding phase is fully merged on GitHub, resulting `origin/1.20.1` is fetched and verified, and its signed annotated phase tag is pushed. Phase 000 uses the existing PR29 branch; it does not create a competing branch or leave performance open for a new stacked branch.

| Phase ID | Objective | Owner | Dependencies | Canonical requirements | Entry summary | Exit summary | Next transition | Execution blueprint |
|---|---|---|---|---|---|---|---|---|
| BFS2-PHASE-000 | Establish truthful current baseline, reusable diagnostics and measured performance recovery. | Repository maintainer, diagnostics and performance | EXT-002 | BFS2-REQ-001, BFS2-REQ-020, BFS2-REQ-022 | Retained PR29 source and failed evidence identified; no old plan restored. | Core diagnostics verified, full performance matrix passes, PR29 merged, issue28 reconciled, resulting default and signed tag verified. | BFS2-PHASE-001 at P001-TASK-001 | [Phase 000](phases/plan-phase-000.md) |
| BFS2-PHASE-001 | Deliver complete session tuning and safe independent species movement. | Repository maintainer, settings and aquatic behavior | BFS2-PHASE-000, EXT-001, EXT-002 | BFS2-REQ-002, BFS2-REQ-003, BFS2-REQ-004, BFS2-REQ-005, BFS2-REQ-006, BFS2-REQ-010 | Phase 000 integrated and tagged; current source and diagnostics available. | All 22 species controls, effective equations, scale/spawn/attribute lifecycle and full body safety pass; phase integrated and tagged. | BFS2-PHASE-002 at P002-TASK-001 | [Phase 001](phases/plan-phase-001.md) |
| BFS2-PHASE-002 | Deliver broad mob following with reliable restoration. | Repository maintainer, mob controller adapters | BFS2-PHASE-001, EXT-001, EXT-002 | BFS2-REQ-008, BFS2-REQ-009 | Safe movement and settings contracts integrated; no unresolved prerequisite phase defect. | All adapter families, special bosses and actual third party case pass acquisition, safety and release; phase integrated and tagged. | BFS2-PHASE-003 at P003-TASK-001 | [Phase 002](phases/plan-phase-002.md) |
| BFS2-PHASE-003 | Deliver real disturbance controls and safe Great White boat interest. | Repository maintainer, disturbance and shark intent | BFS2-PHASE-002, EXT-001, EXT-002 | BFS2-REQ-007, BFS2-REQ-011, BFS2-REQ-012 | Follow and body safety integrated with no competing movement owner. | Natural source, tuning, throttles, boat trajectory, fin/body and recovery proofs pass; phase integrated and tagged. | BFS2-PHASE-004 at P004-TASK-001 | [Phase 003](phases/plan-phase-003.md) |
| BFS2-PHASE-004 | Restore original Zippy markings through lighting and reload. | Repository maintainer, client rendering | BFS2-PHASE-003, EXT-001, EXT-002 | BFS2-REQ-013 | Current approved render path and exact original art bound. | Day/dark/reload and unaffected variant/glow regressions pass with unchanged authored pixels; phase integrated and tagged. | BFS2-PHASE-005 at P005-TASK-001 | [Phase 004](phases/plan-phase-004.md) |
| BFS2-PHASE-005 | Deliver underwater face algae, tall columns and bounded richer generation. | Repository maintainer, blocks and world generation | BFS2-PHASE-004, EXT-001, EXT-002 | BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016 | Existing state and data identities recorded; current approved source. | Placement/growth/loot/save compatibility, density, cave exclusion and animation pass; phase integrated and tagged. | BFS2-PHASE-006 at P006-TASK-001 | [Phase 005](phases/plan-phase-005.md) |
| BFS2-PHASE-006 | Deliver faithful dive equipment, seabed movement and exact oxygen lifecycle. | Repository maintainer, equipment and player behavior | BFS2-PHASE-005, EXT-001, EXT-002 | BFS2-REQ-017, BFS2-REQ-018, BFS2-REQ-019 | Four item sources/JPEG atlas bound and current approved baseline ready. | Worn geometry/UV, one travel writer, work parity, 6000 tick reservoir and all lifecycle/client proofs pass; phase integrated and tagged. | BFS2-PHASE-007 at P007-TASK-001 | [Phase 006](phases/plan-phase-006.md) |
| BFS2-PHASE-007 | Verify the complete merged product and deliver the exact testing JAR. | Repository maintainer, final verification and delivery | BFS2-PHASE-006, EXT-001, EXT-002 | BFS2-REQ-021 | All feature phases merged, tags and evidence traceable; no known required defect. | Plan wide Definition of Done, final installed artifact regression/performance, resulting default verification, signed tag, documentation and verified delivery complete. | Final completion at P007-TASK-006, no successor phase | [Phase 007](phases/plan-phase-007.md) |

The following IDs reserve concrete work packages for the linked blueprints. Their detailed steps, exact fixture construction and local diagnostic runbooks belong solely to those files. Phase authors must preserve these IDs and objectives; additional local tasks may refine work without changing phase ownership or dependency direction.

| Phase | Reserved tasks in execution order |
|---|---|
| 000 | P000-TASK-001, reconcile source, protections, PR29/issue28, toolchain and owned runtime intake. P000-TASK-002, verify/extend existing diagnostic core and parser before profiling. P000-TASK-003, reproduce and profile actual performance failure, implement only evidenced repairs. P000-TASK-004, execute full matrix, regression, diagnostic bounds and cleanup proof. P000-TASK-005, update evidence/docs, merge PR29, verify default, reconcile issue28 and signed tag. |
| 001 | P001-TASK-001, freeze current effective defaults and complete capability inventory, implement atomic command/settings core. P001-TASK-002, prove all movement paths and implement independent axis/sprint integration. P001-TASK-003, implement scale, spawn, attributes, hunting, sensing, cooldown and recovery consumers with safe lifecycle. P001-TASK-004, complete body envelope safety and local diagnostics before full mechanical/server/client proof. P001-TASK-005, document commands and behaviors, review/integrate/verify/tag. |
| 002 | P002-TASK-001, perform registry/controller compatibility experiment including special bosses and pinned third party artifact. P002-TASK-002, implement issued stick, ownership and every required movement adapter. P002-TASK-003, implement bounded lifecycle restoration and interaction security. P002-TASK-004, deliver local diagnostics, broad real paths, client interaction and recovery proof. P002-TASK-005, document support/capabilities and review/integrate/verify/tag. |
| 003 | P003-TASK-001, extend diagnostic payloads and actual water/boat source events with bounded lifetime. P003-TASK-002, implement complete disturbance catalog and effective reaction policy. P003-TASK-003, implement Great White behind boat intent and safe recovery. P003-TASK-004, prove source-to-reaction and real boat/fin/client behavior, permissions and work bounds. P003-TASK-005, document behavior and review/integrate/verify/tag. |
| 004 | P004-TASK-001, reproduce lighting transition and inspect exact texture/layer mechanism with scoped signals. P004-TASK-002, implement nondestructive original marking/glow rendering. P004-TASK-003, verify day/dark/reload and unrelated variants on the silent laptop. P004-TASK-004, update asset ledger, diagnostic guide and render evidence. P004-TASK-005, review/integrate/verify/tag. |
| 005 | P005-TASK-001, implement face algae state/placement/support and diagnostics. P005-TASK-002, implement both column block state/growth/stack/loot lifecycles. P005-TASK-003, implement bounded exposed floor generation and data providers. P005-TASK-004, verify migration, fluid, drops, density, cave negatives and animated client presentation. P005-TASK-005, document behavior and review/integrate/verify/tag. |
| 006 | P006-TASK-001, validate atlas geometry/UV and real player movement hook experiment. P006-TASK-002, register pieces, faithful worn rendering and item resources. P006-TASK-003, implement shared eligibility and exactly one seabed movement path. P006-TASK-004, implement reservoir persistence/sync/feedback and water work parity. P006-TASK-005, complete local diagnostics, exact timing, lifecycle, compatibility and actual client proof. P006-TASK-006, document equipment and review/integrate/verify/tag. |
| 007 | P007-TASK-001, freeze final candidate and audit all requirements/evidence/default ancestry. P007-TASK-002, run complete feature interactions, old gameplay, diagnostic support and actual client regressions. P007-TASK-003, rerun full installed Forge performance comparison on final product. P007-TASK-004, finish artifact/package/hash/source/license and documentation gates. P007-TASK-005, review/integrate, verify resulting default and final signed tag with exact candidate equivalence. P007-TASK-006, deliver the verified testing JAR/checksums, close satisfied tracking and verify final cleanup. |

Phase 000 owns the reusable diagnostic core. Each feature implements its own new observations before its first dependent assertion. A phase exit evaluates all behavior introduced or changed by that phase at its exact revision, and remains closed for any known owned defect. Final cross feature diagnostic coverage and retained performance are Phase 007 regression gates under BFS2-REQ-021; they do not create a backward dependency or postpone early diagnostics. No future phase implementation begins before current integration and tag gates pass.

## 14. Verification Strategy

### Execution Hosts

| Workload or gate | Execution host | Required capabilities and launch configuration | Candidate identity and runtime directory | Evidence |
|---|---|---|---|---|
| Compilation, unit checks, resource generation, archive checks and audit | `node-1` | Read checked wrapper and task graph first; no client, renderer or display dependency. | Reuse applicable checkout; exact test outputs and preexisting content recorded before run. | Commands, outcomes, revision, generated drift, owned output cleanup. |
| Dedicated GameTests, real server logic and installed Forge performance | `node-1` | Verified dedicated server task graph or installed production Forge no GUI launch, console input, automatic `eula=true` and readback. | Unique owned runtime under verified `/mnt/hermes/projects/BFSMOD` anchor with parent exclusions and active use checked. | Readiness, true server entry paths, bounded tick observations, source/artifact/dependency/config binding and process exit. |
| Input, rendering, synchronization, animation and targeted visuals | Verified Linux laptop | Existing authorized connection, active Hyprland, actual discrete NVIDIA renderer, isolated silent client and exact owned stream mute. | Discover actual laptop anchor and nested isolated instance; match source/artifact/dependency/config hashes to server. | Window/process/renderer identity, muted stream proof, client logs and only necessary visual evidence. |
| Multiplayer acceptance | Laptop client with `node-1` dedicated server | Readiness and existing private reachability, supported automatic connection, correct player joined on both sides and owned console fixture control. | Bind both directories and exact endpoint in private runtime record; redact public address details. | Server plus client correlation and actual named residual client claim. |

No graphical Minecraft client runs on `node-1`. Xvfb, virtual displays, VNC, X11 forwarding, offscreen rendering, software rendering, llvmpipe or an integrated GPU do not satisfy the laptop boundary. Missing laptop capability leaves the named visual/input/sync gate unverified under EXT-001; it does not block independent server tests. No generic test name is proof that the task is headless.

For a client gate, first verify the actual laptop host/session/discrete renderer. Set the disposable instance's pinned version master output to zero before launch. After its window appears, bind exact address, class, title and PID using `hyprctl clients -j`; correlate only that process tree to its PipeWire/PulseAudio playback stream, mute it using `wpctl` or `pactl`, and verify muted before assertions. Recheck and immediately mute replacement streams after reload, reconnect, device changes or stream recreation. Never mute the default sink, global output, microphone, unrelated application or personal Minecraft instance. If identity or mute cannot be verified, stop the owned client. Teardown removes the exact client, watcher, streams and temporary audio state.

For an in world client gate, configure/read back the exact server runtime's `eula=true`, start the owned dedicated server, confirm candidate readiness and existing private reachability, then automatically connect using version supported launch options or authorized controls. Verify intended player/world join on both sides before assertions. Server console may prepare fixtures but never grant progress or bypass the behavior being tested. Do not edit personal server lists, expose ports, change firewall or authentication, or copy credentials. Singleplayer is permitted only for a specifically identified integrated server defect with its requirement ID and rationale; none is currently required. Connectivity failure is not a singleplayer exception.

### Checks and evidence fidelity

Use the checked wrapper with the actual task graph inspected first:

```bash
./gradlew :forge:compileJava :forge:compileTestJava --no-daemon
./gradlew :forge:test --no-daemon
./gradlew :forge:Data --no-daemon
./gradlew :forge:test --no-daemon
./gradlew :forge:GameTestServer --no-daemon --rerun-tasks
./gradlew :forge:build --no-daemon
```

The GameTest task must be verified to start only a dedicated server. Run affected real world suites after entity, block, registration, resource, data or harness changes. Unit helpers prove math, validation and serialization; actual dispatcher, tick, travel, spawn, interaction, neighbor, loot and breathe paths prove server behavior. Neither simulated players nor forced handler inputs prove real client input/rendering or natural disturbance producers. The installed production Forge final gate cannot be replaced by a development classpath server.

| Requirement family | Isolated checks | Real server or artifact proof | Required residual client proof |
|---|---|---|---|
| BFS2-REQ-001, BFS2-REQ-020 | Manifest/parser/comparison rejection boundaries | Four case installed Forge matrix, current source/PR/default/tag identity | None for server performance; no player joins the measurement. |
| BFS2-REQ-002 through BFS2-REQ-006 | Atomic validation, equation, capability and modifier tests | All species actual movement/spawn/policy/lifecycle paths | Readable control feedback and physical scale/render agreement. |
| BFS2-REQ-007, BFS2-REQ-011, BFS2-REQ-012 | Throttle and source/intent validation | Actual water/boat producer to shark decision, bounded state and recovery | Actual jump/boat input and behind boat fin/body presentation. |
| BFS2-REQ-008, BFS2-REQ-009 | Marker/lease validation and state bounds | All controller families, special boss/third party and lifecycle restoration | Actual right click selection, moving owner and reconnect. |
| BFS2-REQ-010 | Finite math and conservative swept envelope properties | Surface/prey/slope/wall/scale/recovery fixtures | Rendered/interpolated whole body and fin clearance. |
| BFS2-REQ-013 | Asset identity, model/layer choice and reload state | Packaged resources and client class separation | Day/dark/day/reload original markings and other glow variants. |
| BFS2-REQ-014 through BFS2-REQ-016 | Blockstate, growth and data invariant tests | Actual placement/support/drop/save/generation and cave counts | Faces, columns and retained animation. |
| BFS2-REQ-017 through BFS2-REQ-019 | Image identity/UV, timing and persistence checks | Actual equipment/travel/work/breathe/clone/restart paths | Worn mapping, one input travel, pose, jump, oxygen feedback and sync. |
| BFS2-REQ-021, BFS2-REQ-022 | Complete evidence/schema/permission/redaction checks | Final archive, installed artifact, support collection and old gameplay regressions | Final exact candidate combined scenarios and required remaining visual gates. |

Boundary matrices include invalid/nonfinite/out of range input, zero speed, maximum valid coefficients and scale, mixed wildcard capabilities, concurrent revisions, absent/removed targets, reload/restart, dimension transfer, death, low/empty air, blocked route and denied permissions. Matched seeded trials normalize tick observations and preserve the actual behavior workload. Source/config/dependency/artifact, fixture or observer changes invalidate dependent evidence from the earliest affected gate. A reported pass from the old plan is historical evidence, not a fresh result.

### Cleanup and evidence retention

Every check, build used for verification and audit records exact owned paths/processes and teardown before execution. Resolve preexisting files, active worktree users, exclusions from Git/build/index/package, and source/data protections. Redirect scratch into unique owned directories; avoid incidental bytecode and duplicate caches. Necessary extra worktrees or actual runtimes stay nested under each verified project anchor. Use the existing applicable checkout, never clone/copy a second project or force remove a historical worktree.

On success, failure, timeout or interruption, preserve only required sanitized evidence and requested deliverables after their final consumer, stop only owned processes and verify exit, remove exact test created worlds/config/logs/reports/traces/screenshots/downloads/test build outputs and audio watcher state, then verify paths and processes absent on every used host. Retain necessary candidates until packaging/comparison consumers finish. Keep source, tracked fixtures, owner files, shared dependency caches, personal saves and unrelated processes. No blanket `git clean`, broad kill, symlink traversal or deletion of an active source worktree. Eligible finished worktrees require Git aware guarded removal while branches/tags remain. A read only audit confirms that it created no resources. Report exact leftovers as `CLEANUP_INCOMPLETE`; a passing test does not waive cleanup.

Required sanitized records go under existing `docs/verification/` conventions, using `docs/verification/bfs2-phase-NNN.md` and minimal referenced entries under `docs/verification/artifacts/bfs2/phase-NNN/`. The retained source plan stays in `docs/general/` and is not a disposable test output. Do not retain entire runtimes or raw private logs. The delivered testing JAR and two checksum files are requested deliverables outside tracked source, at an exact destination recorded in the final evidence index.

## Diagnostics and Debugging

**Requirement IDs:** BFS2-REQ-022, BFS2-REQ-020, BFS2-REQ-002, BFS2-REQ-003, BFS2-REQ-004, BFS2-REQ-005, BFS2-REQ-006, BFS2-REQ-007, BFS2-REQ-008, BFS2-REQ-009, BFS2-REQ-010, BFS2-REQ-011, BFS2-REQ-012, BFS2-REQ-013, BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016, BFS2-REQ-017, BFS2-REQ-018, BFS2-REQ-019, BFS2-REQ-021  
**Task IDs:** P000-TASK-002, P000-TASK-004, P001-TASK-004, P002-TASK-004, P003-TASK-004, P004-TASK-003, P005-TASK-004, P006-TASK-005, P007-TASK-002  
**Controls:** IFC-001 preserves `/bfs debug on`, `/bfs debug on <category> <ticks> [targets]`, `/bfs debug status`, `/bfs debug off`, permission level 2 and explicit console targets. Existing client local controls are discovered from `BfsClientDebugManager` and `docs/test/debug-diagnostics.md`, extended under that existing root without server privilege.  
**Signals:** IFC-001 header/correlation plus typed subsystem table below distinguish desired, actual, authority and rejection. New feature schemas are emitted before local dependent tests.  
**Collection procedure:** Follow the numbered bounded runbook below, using actual status output paths, real entry stimuli, parser queries, stop/completeness checks and exact teardown.  
**Headless verification:** Real dispatcher, manager and world paths in `BfsGameTests`, existing parser tests and installed Forge `tools/performance` harness prove server decisions, permissions, timing, bounds and output integrity without requiring the owner to join. They do not prove rendering or physical input.  
**Client verification:** Silent laptop logs and targeted visuals prove actual input, rendered body/fin/scale, Zippy, algae animation, armor UVs, movement pose and oxygen feedback. Real client receipt and rendering are distinct from server state.  
**Client audio isolation:** Before startup set isolated client master audio to zero, bind exact owned Hyprland address/class/title/PID, correlate its process tree to the application stream, mute and verify with `wpctl`/`pactl`, reapply after every stream recreation, and remove the owned client/watcher/stream/temp state at teardown. No global or unrelated mute.  
**Budgets and privacy:** IFC-001 numerical caps, default off, lazy snapshots, asynchronous bounded output and redacted pseudonyms apply. Measure disabled and enabled overhead separately; incomplete or dropped evidence cannot pass. No secrets, arbitrary NBT, chat, private addresses or automatic external upload.  
**Regression and support:** Maintain `docs/test/debug-diagnostics.md`, `tools/bfs_debug_analyze.py`, its tests and applicable GameTests. Verify the enable/reproduce/disable/collect/redact procedure against the final installed JAR and retain only the minimal sanitized support packet.

| Signal | Source and unit | Expected observation |
|---|---|---|
| `settingsRevision`, field value/source/capability and transaction reason | Server, revision/count and field units from Section 11.1, on changes | One revision per accepted transaction, no partial wildcard state or join compounding. |
| Base/sprint/effective axes, actual displacement, powered/external velocity, intent/state | Server, blocks per simulation second and blocks per tick, at most five tick sampling | Correct equation and active state; external movement and safety limits explain deviations. Teleports/dimension moves split windows. |
| `bodyWet`, `solidClear`, surfaceY, scale, safe/desired pitch, recovery attempt | Server envelope and client pose, blocks/degrees/count | Whole body safe, fin exception only for valid boat intent, bounded recovery and no renderer escape. |
| Lease owner/target pseudonyms, adapter, active writer, progress/release reason | Server interaction/behavior, tick/count/blocks | One owner and writer, real route progress, bounded release and ordinary behavior restoration. |
| Disturbance source kind/strength/radius/throttle/reaction and boat ID correlation | Server real event path, dimensionless/blocks/ticks | Real source reaches eligible sharks once per interval; absence, suppression and unsafe boat route are distinguishable. |
| Variant, selected texture/layer, brightness and reload generation | Client renderer, registry path/brightness level/generation | Authored Zippy markings survive daylight and reload with intended dark glow. |
| Face/segment/age/fluid/support, placement/growth/drop and generation rejection | Server block/data paths, state/count/ticks | Valid water preserved, no duplicate drops, bounded columns and zero enclosed red cave generation. |
| Full suit, eye/body fluid, travel writer, grounded/swim pose, jump, charge and sync revision | Server/player plus owning client, blocks per tick/ticks/revision | Exactly one eligible travel writer, no held ascent, exact reservoir boundaries and no swap/reconnect refill. |
| Break speed factors and placement permission/result | Server real action, work rate/factor/boolean | Only water/airborne factors removed, all other tool/effect/protection decisions unchanged. |
| Tick duration/population/death/replenishment/work counters and capture completeness | Installed server observer, milliseconds/entities/count/ticks | Complete retained workload with unchanged comparison limit and no hidden sample loss. |

1. Identify the owning task and candidate commit/JAR hashes, pinned versions/config, fixture seed/coordinates, host roles, exact isolated paths and registered cleanup targets. For server runs configure/read back `eula=true` and verify readiness/console. For client claims verify laptop renderer, join identity and muted stream before assertions.
2. Read current dispatcher/help and `docs/test/debug-diagnostics.md`. On the server console run `bfs debug status`, then for a tagged owned fixture run `bfs debug on movement 200 @e[tag=bfs2_probe,limit=8]` and `bfs debug status`. Choose the feature category from IFC-001 instead of movement when appropriate. An absent target or unsupported command is a failing setup result, not permission to broaden to unrelated entities. Record the exact output path returned by status. Phase 000 uses current categories until its bounded additions exist.
3. Apply the real stimulus: move actual prey for pursuit, execute the actual spawn/interaction/support/breathe path, or use laptop inputs only for the named residual claim. Do not replace boat movement with a direct disturbance command, natural following with teleportation, or breathing ticks with a helper arithmetic result. Keep deterministic fixture inputs and a bounded tick deadline.
4. Disable using `bfs debug off`, verify `bfs debug status`, and require a complete footer with no dropped/missing records. Discover exact supported parser arguments using `python3 -B tools/bfs_debug_analyze.py --help`, then analyze the status identified capture with the selected requirement's assertions. Compare sorted sequence/tick/intent IDs and the typed fields above; do not assume server and client clocks are synchronized. Query only the selected category/entities. For performance use the existing packaged performance runbook and inspect its harness arguments before launch; it is a separate observer with the IFC-008 exact full window.
5. Record decisive assertion outcomes, negative/recovery cases, source/artifact/config identity and unverified residual claims. Off must stop new matching records by the next tick; repeat off is harmless. Capture timeout, target removal, reload/shutdown, writer backpressure and I/O failure all take the registered stop path. A truncated capture yields `CAPTURE_INCOMPLETE` and requires a fresh bounded run.
6. Build the support packet with project/version/loader, minimal relevant settings, reproduction actions, expected/actual behavior, test/capture ID, sanitized summaries and client evidence only for actual client claims. Remove secrets, chat, player names, private addresses and unrelated paths. Keep the guide linked from user documentation and do not upload the packet automatically.
7. Verify retained evidence is readable, then stop owned test processes/watchers, confirm exit and mute stream disappearance, remove exact disposable paths after the final consumer, and verify cleanup on every host. Preserve the reusable diagnostics, parser and guide in the product. Report failed assertions separately from failed cleanup.

An illustrative record, not an executed trace:

```json
{"format":"bfs-debug-v2","schemaMinor":1,"captureId":"sample01","sequence":42,"tick":120,"side":"server","dimension":"minecraft:overworld","entity":"mob03","entityType":"bensfintasticsharks:great_white_shark","event":"movement","settingsRevision":3,"intentId":"intent07","reason":"WATER_ENVELOPE","data":{"state":"pursuit","horizontalBps":4.0,"verticalBps":2.0,"bodyWet":true,"solidClear":true,"limitedAxes":["vertical"]}}
```

Phase 000 verifies capture off adds no diagnostic formatting, traversal, allocation, packets or file writes in disabled branches. Paired seeded off/on runs must produce the same gameplay decisions and final state. Measured enabled scoped capture p95 tick cost may add no more than 5 percent or 0.25 ms, whichever allowance is larger, over the same candidate with capture off; final gameplay performance remains subject to IFC-008 with diagnostics off. Repeat bounds tests with maximum targets, timeout, queue saturation, target removal, redaction strings and writer failure. No overhead success can excuse changed behavior or incomplete evidence.

## 15. Compatibility, Migration, Rollout, and Recovery

Preserve registry IDs, resources, animation names/bones/curves, entity saves, fishing, Prismarine equipment and advancement behavior. The supplied authored 1.8.0 geometry/animation inputs permit only the documented deterministic GeckoLib compatibility conversion, Cod geometry identifier correction and Oceanic `leftFin` to `Fin`, `rightFin` to `Fin2` mappings. Do not broaden asset repair into redesign. Zippy changes rendering use of existing art, not pixels.

Existing species config remains compatible; missing new fields use declared defaults and invalid reload keeps the old entire snapshot. Session overrides never persist. Scale migration normalizes an existing saved value once and does not reroll after reload. Existing algae singleton saves load as a valid supported singleton, with no chunk repopulation. New player reserve persistence has explicit schema and conservative corrupted-state recovery. Unknown newer schema cannot silently be downgraded or reset. Prove upgrade/load/unload/restart with old fixture worlds in disposable runtimes and preserve source fixtures.

Roll out only through sequential reviewed phase merge commits into `1.20.1`. Keep the preceding approved JAR/source/tag available as identity references while the current bounded comparison needs them. Stop candidate testing on source, artifact, config or dependency mismatch and rebuild the affected evidence. Rollback during testing stops the owned runtime and reopens an untouched disposable baseline world with the preceding candidate; never load a world containing new item/block data into an older JAR and claim lossless downgrade. Do not alter production worlds. After integration, any corrective rollback is a normal reviewed change preserving history, not force push or tag movement.

## 16. Documentation, Operations, and Release Gates

Each feature phase updates root `README.md`, `DOCUMENTATION.md`, `docs/README.md`, existing affected topic guides, the diagnostic support guide and its phase evidence with behavior actually implemented. Add cross links when document locations change. The dive guide must explain full set eligibility, gravity/jump controls, reserve timing/refill/exhaustion, no recipes and no swap refill. The tuning guide must provide discover/list/get/change/observe/record/reset examples, units and capability reasons without requiring profile files. Algae and boat guides distinguish natural rules from manual placement and noncombat interest. Asset ledger records exact supplied source/decoded mapping and unchanged original artwork.

Before every integration, update the corresponding phase milestone and linked issue/PR state, inspect complete diff and required checks, run one private independent review under the standing workflow, and resolve actionable findings. Commit and push completed changes on the current phase branch using EnVy as sole author/committer, registered SSH signatures and no coauthor. Git/GitHub prose uses lowercase and ordinary periods/commas except required literals; documentation uses normal grammar and capitalization. No attribution or private workflow details belong in repository/public output. Do not stage owner instructions or unrelated caches/build files.

PR29 is the Phase 000 integration vehicle. Recheck its live comments/reviews/checks and issue28 before closure rather than assuming the intake absence of review threads remains true. Use GitHub merge commit integration with all required checks and conversations satisfied. If pending checks allow supported auto merge, use merge commit auto merge and wait for actual merged state. If merge commits are disabled, report the restriction rather than substitute a method. Fetch and verify `origin/1.20.1` contains the reported merge commit, rerun required default proof, then create and push a signed annotated phase tag. Tags use unique names `bfs2-phase-000` through `bfs2-phase-007` and never move historical tags. Keep phase branches.

Tracked documentation is canonical. Prepare wiki changes from it and publish only after the relevant approved merge. Then reconcile satisfied issues, milestones and Projects; do not close unsatisfied defects or imply performance passed because plans were deleted. Final package verification includes `unzip -tqq`, archive entry inspection, SHA 256 and SHA 512, correct Forge metadata/dependencies, no Fabric artifact, no source archives, secrets, debug fixtures, runtime config, logs or caches. A testing JAR is not a CurseForge, Modrinth or GitHub release. Public publication remains excluded.

## 17. Risks and Failure Boundaries

| Risk ID and causal scenario | Affected requirements or interfaces | Likelihood and impact rationale | Prevention | Detection signals | Recovery | Owning phase and tasks | Required proof |
|---|---|---|---|---|---|---|---|
| BFS2-RISK-001. Historical root overwrites current fixes. | BFS2-REQ-001, IFC-008 | Observed wrong anchor lineage creates high exposure and broad loss risk. | Pin retained PR29 source and protect dirty owner state. | Source ancestry and complete diff against current default. | Stop mismatched work, preserve owner state, resume applicable checkout. | BFS2-PHASE-000, P000-TASK-001, P000-TASK-005 | Current source/fishing/debug presence, historical evidence unchanged and exact resulting merge/tag. |
| BFS2-RISK-002. Axes are masked, applied twice or bypassed by a movement path. | BFS2-REQ-003, BFS2-REQ-004, IFC-002, IFC-003 | Observed fixed ratios and distinct writers make this likely with high functional impact. | One formula, 22 species adapters and explicit state. | Effective/actual axes, writer and limit reasons. | Atomic reset and safe ordinary movement. | BFS2-PHASE-001, P001-TASK-002, P001-TASK-004 | Orthogonal paired axis changes, cruise/pursuit/flee transitions, all adapters and finite extreme values. |
| BFS2-RISK-003. Reload compounds attributes, rerolls scale or partly applies wildcard. | BFS2-REQ-002, BFS2-REQ-005, BFS2-REQ-006, IFC-002 | Existing join path supports a concrete high impact lifecycle concern. | Immutable bases, stable modifiers, normalized roll and transaction validation. | Revision/source, health fraction, modifier count and scale roll. | Keep previous snapshot on rejection, remove only owned modifiers on reset. | BFS2-PHASE-001, P001-TASK-001, P001-TASK-003, P001-TASK-004 | Repeated join/save/reload, invalid one member transaction, body no fit and no healing/duplication. |
| BFS2-RISK-004. Special mob controller defeats following or restoration. | BFS2-REQ-008, BFS2-REQ-009, IFC-004 | Diverse brains and boss controllers make exposure high; broken safety/normal behavior is severe. | Early registry driven feasibility and adapters, one temporary writer. | Adapter identity, lease/intent, progress and release reasons. | Release owned state safely; unresolved adapter remains a mandatory defect. | BFS2-PHASE-002, P002-TASK-001, P002-TASK-002, P002-TASK-003, P002-TASK-004 | Boss/Brain/third party positive routes, blocked/lifecycle/competing owner negatives and ordinary behavior within 40 ticks. |
| BFS2-RISK-005. Wet center hides body clipping, flight or stalled pursuit. | BFS2-REQ-010, BFS2-REQ-012, IFC-003 | Current center water test directly supports the risk; mechanical and visual impact high. | Scaled swept body/fin envelope and bounded recovery. | BodyWet/solidClear, desired/safe pose and progress counters. | Brake or safe wet replan, no teleport/flight. | BFS2-PHASE-001, P001-TASK-004. BFS2-PHASE-003, P003-TASK-004 | Surface/shore/slope/wall/ceiling and extrema, server shape invariant plus actual laptop mesh. |
| BFS2-RISK-006. Boat events flood work or retain stale dimension/source state. | BFS2-REQ-007, BFS2-REQ-011, BFS2-REQ-012, IFC-005 | Added frequent producer and existing maps create credible performance and wrong target risk. | Typed source identity, bounded query/cadence/maps, lifecycle prune. | Source, throttle, map size, candidate count and intent release. | Expire/release invalid source and safe ordinary state. | BFS2-PHASE-003, P003-TASK-001, P003-TASK-004 | Many sources, empty/stationary boats, source recreation, unload and dimension isolation. |
| BFS2-RISK-007. Glow extraction removes daylight markings. | BFS2-REQ-013 | Exact library mechanism is strong evidence; visual impact localized. | Nondestructive original art overlay and explicit lighting behavior. | Variant/mask/layer/brightness/reload signals. | Restore correct original texture path and invalidate visual evidence. | BFS2-PHASE-004, P004-TASK-001, P004-TASK-002, P004-TASK-003 | Day/dark/day/reload with preserved pixels and unaffected glow variants. |
| BFS2-RISK-008. Algae update loses water, duplicates loot or generates in caves. | BFS2-REQ-014, BFS2-REQ-015, BFS2-REQ-016, IFC-007 | State/growth changes plausibly affect saves and many blocks. | Existing IDs, single state migration, bounded source water placement and top growth. | Fluid/support/drop counts, column transitions and generation rejection. | Remove only invalid supported state, restore fluid; no retroactive generation. | BFS2-PHASE-005, P005-TASK-001, P005-TASK-002, P005-TASK-003, P005-TASK-004 | Old singleton load, middle/base break, repeated neighbors, height cap and roofed red negative fixtures. |
| BFS2-RISK-009. Armor atlas mapping invents alpha or distorts art. | BFS2-REQ-017, IFC-006 | JPEG contains no geometry or alpha, so mapping is uncertain and high visible impact. | Preserve source/decoded pixels and author only compatible geometry with explicit UV map. | Pixel hashes, UV bounds/seams and selected resource. | Repair mapping without repainting or pretending lost geometry exists. | BFS2-PHASE-006, P006-TASK-001, P006-TASK-002, P006-TASK-005 | All pieces, limbs, arm types and poses, front/back/side/reload actual rendering. |
| BFS2-RISK-010. Suit grants infinite air or leaves movement altered. | BFS2-REQ-018, BFS2-REQ-019, IFC-006 | Lifecycle transitions and vanilla travel ownership make high exposure with severe gameplay impact. | Player reservoir, one decrement and writer, precise eligibility and persistence. | Remaining tick/mode/revision, writer, pose, refill reason. | Remove only owned movement state; corrupt established charge becomes zero. | BFS2-PHASE-006, P006-TASK-001, P006-TASK-003, P006-TASK-004, P006-TASK-005 | 5999/6000/6001, swaps/restart/death/dimension, real input, held jump, dry/partial/exempt and work permission cases. |
| BFS2-RISK-011. Optimization hides failure by changing workload. | BFS2-REQ-020, BFS2-REQ-021, IFC-008 | Observed death churn and incomplete failure invite invalid comparisons; evidence impact severe. | Preserve predation, observers, population and threshold; profile separate from acceptance. | Complete tick window, population/death/work data and hashes. | Reject capture and rerun exact full matrix. | BFS2-PHASE-000, P000-TASK-003, P000-TASK-004. BFS2-PHASE-007, P007-TASK-003 | Four full cases and unchanged comparison; no suppressed safety/predation or relabeled original artifact. |
| BFS2-RISK-012. Diagnostics alter behavior or imply proof from missing records. | BFS2-REQ-022, IFC-001 | Instrumentation is frequent and cross cutting, making overhead and false proof consequential. | Default off, bounds, snapshot ownership and strict completeness. | Dropped/footer/counter/overhead and parity outputs. | Stop failed capture, retain sanitized reason and rerun bounded case. | BFS2-PHASE-000, P000-TASK-002, P000-TASK-004. BFS2-PHASE-007, P007-TASK-002 | Unauthorized/removed/overflow/I/O/redaction tests and matched off/on gameplay. |
| BFS2-RISK-013. Wrong runtime host, audible stream or unsafe cleanup damages owner environment. | EXT-001, BFS2-REQ-021, BFS2-REQ-022 | Reused worktrees and transient stream IDs create concrete operational exposure. | Verified host/renderer, prelaunch zero plus exact application mute, owned-resource teardown. | Host/window/PID/stream mute, exact paths/process state and cleanup result. | Stop owned client on ambiguity, preserve user data, reconcile exact leftovers before rerun. | Every phase runtime task, P007-TASK-006 | Both host identity and cleanup records; no client on node-1 and no global mute/deletion. |

These are observed defects or plausible causal risks, not a claim that all future bugs are known. New failures within scope require a real regression and appropriate evidence. A material scope change requires an owner authorized amendment; runtime difficulty or a failed validator does not authorize scope reduction.

## 18. Definition of Done

All mandatory repairs and debug controls are verified, integrated through sequential merge commits into 1.20.1, tagged, documented, and delivered as an exact verified testing JAR with checksums and source binding. Public release publication is excluded.

- BFS2-REQ-001 through BFS2-REQ-022 have their specified acceptance and evidence at the required fidelity. Every phase blueprint exit, local diagnostic contribution and final cross feature check passes, with no known mandatory owned defect remaining.
- Existing PR29 and issue28 are reconciled by actual evidence and merge, all sequential phase PRs are fully merged into `1.20.1`, resulting default checks pass, and signed annotated phase tags identify the exact merged commits.
- The final installed Forge artifact passes ordinary and doubled complete performance comparison with the unchanged threshold. Historical failed evidence and all original artifact identities remain truthful and unchanged.
- All 22 species controls reach actual paths. Whole body motion, broad mob following/restoration, real disturbances/boat behavior, original Zippy art, algae behavior and dive movement/6000 tick oxygen pass their server and named client proof.
- Old fishing, Prismarine, advancements and authored resources retain required behavior. No Fabric artifact, unrequested recipe, boat destruction, profile library, subjective speed balance lock or public release is introduced.
- Diagnostics remain reusable and default off, permissions/budgets/redaction/overhead/completeness pass, and the delivered artifact's support collection procedure is verified.
- Every launched Minecraft client remained inaudible with verified application stream mute. Every check/runtime has complete owned-resource teardown on every used host, preserving required evidence and requested deliverables.
- User/technical documentation and affected wiki/tracking state describe only merged verified behavior. The exact testing JAR, source binding, dependency manifest and SHA 256/SHA 512 files are delivered and readable.
- A missing external prerequisite leaves completion unverified, with its exact affected gate visible. Neither planned tests, a plausible explanation, partial progress nor an enabled auto merge constitutes completion.

## 19. Goal Creator Handoff

```text
Mandatory boundary: BFS2-REQ-001 through BFS2-REQ-022, all eight registered phases and all required evidence.
Optional/future disposition: excluded. FUT-001, FUT-002, FUT-003, FUT-004 remain outside completion.
Locked owner decisions: DEC-001, DEC-002, DEC-003, DEC-004, DEC-005, DEC-006, DEC-007, DEC-008, DEC-009, DEC-010.
Active phase: BFS2-PHASE-000
Active phase plan: phases/plan-phase-000.md
Next executable action: P000-TASK-001. Revalidate the retained PR29 source, current remote/default state, protected working changes, issue28 and exact historical evidence before any implementation or runtime allocation.
Known failing checks: Historical ordinary performance rejection is final. Doubled cases were not run. New feature and final candidate evidence has not been executed.
Known external blockers: none
Completion endpoint: All mandatory repairs and debug controls are verified, integrated through sequential merge commits into 1.20.1, tagged, documented, and delivered as an exact verified testing JAR with checksums and source binding. Public release publication is excluded.
Required evidence gates: Real server paths, complete performance matrix, exact source/artifact/dependency/config identity, named silent laptop client proof, required PR checks and merge/default/tag verification, documentation/support closure, and complete owned-resource cleanup.
```

Revalidate EXT-001 and EXT-002 at their use gates; their intake availability is not a permanent capability guarantee.

This plan does not create a goal or execution cursor and does not authorize another planning pass. If the owner separately creates a saved goal, it remains immutable and the mutable execution position belongs in `docs/plan/active_phase.md`. After all current phase gates pass, execution advances only that cursor by one contiguous registered phase through the deterministic transition helper, rereads the next phase blueprint, and continues under the unchanged goal. Plan progress, evidence or digest changes never authorize goal rewriting, phase stacking or restoration of the retired plan.
