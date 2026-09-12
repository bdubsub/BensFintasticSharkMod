# Phase 004 Execution Plan

> **Plan ID:** PLAN-PHASE-004  
> **Phase ID:** BFS2-PHASE-004  
> **Owner:** Repository maintainer  
> **Classification:** MANDATORY  
> **Master plan:** [plan.md](../plan.md)  
> **Phase sequence:** 004 of 007

## Purpose and Ownership

This phase restores Zippy’s existing pale cyan lightning markings in ordinary daylight and its intended dark glow, without altering the supplied PNG pixels, variant selection, resource identifiers, GeckoLib animation, or unrelated renderers. It owns the detailed repair, diagnostics, visual evidence, documentation, and integration work for `BFS2-REQ-013`. The master remains the authority for product scope, interfaces, phase sequence, acceptance, host policy, and release endpoint.

## Evidence-Based Entry State

| Evidence class | Area | Finding | Source or command | Freshness condition |
|---|---|---|---|---|
| OBSERVED | source lineage | The retained planning baseline is PR29 at `40207d1b4cbe8db9963f32e43b79bdcfce58918a`; it is not a future Phase 004 branch, and the historical root is not this phase’s source. | research brief F001 and F002 | Any ancestor, worktree, or complete diff mismatch invalidates dependent source evidence. |
| OBSERVED | authored art | `zippy.png` and `zippy_glowmask.png` are supplied authored inputs with pinned hashes `cd4352898da93334fb3c313d293ada7f7cae1b7d99729e9b01ce7d10fba4767d` and `cb6035ce2dafe249c020c4e9fa4f2b847119b17470a0bdf8cf41f51af184ff8f`. | repository map F012 fingerprints | Any byte change, resource rename, or changed resource-pack precedence invalidates pixel-preservation evidence. |
| OBSERVED | render path | `CommonThresherSharkEntity.hasGlowingLayer`, `CommonThresherRenderer`, and `ConditionalAutoGlowingLayer` form the current Zippy path. | repository map component table and F012 | Reinspect if those files or their pinned source hashes change. |
| INFERRED | defect mechanism | GeckoLib 4.4.7 `AutoGlowingTexture.loadTexture` uses `_glowmask` and `createImageMask` clears marked base pixels before upload. The brightness gate then omits the glow layer at raw brightness eight or higher. | research brief F012 and exact GeckoLib source record | The hypothesis must be confirmed through scoped render observations and actual daylight reproduction before repair acceptance. |
| OBSERVED | diagnostics | `BfsClientDebugManager` is a separate local client capture path and the server already provides bounded `bfs-debug-v2` capture controls. | research brief F005 and repository map | Reinspect if the Phase 000 `IFC-001` contract, parser, or client manager changes. |
| OBSERVED | client environment | An authorized laptop connection, Hyprland, discrete NVIDIA capability, `wpctl`, and `pactl` were found, but no candidate window, renderer, laptop anchor, or stream has been verified for this phase. | repository map host finding | Revalidate immediately before every client launch under `EXT-001`. |

## Scope Boundaries

### Included Scope

- `BFS2-REQ-013`: preserve Zippy’s original supplied lightning artwork in normal light, show its intended glow in darkness, and prove daylight return and resource reload behavior.
- Local `render` observations in the inherited `IFC-001` diagnostic format, exact-art hashes, source-aware regression coverage, silent laptop presentation evidence, and the documentation and integration evidence that describe only implemented behavior.

### Explicit Exclusions

- `NG-004`: no redraw, recolor, redesign, replacement, or semantic change to supplied Zippy art.
- `BFS2-REQ-010`, `BFS2-REQ-012`, algae, armor, movement, world generation, or unrelated entity behavior. Existing Great White glow and other Thresher skins are regressions, not change targets.
- New diagnostic command roots, unbounded render telemetry, a new network channel, a singleplayer fallback, public release publication, or a new phase branch before Phase 003 is merged and tagged.

## Phase Contract

### BFS2-PHASE-004 — Restore original Zippy markings through lighting and reload

**Objective:** Make the approved Zippy variant visibly retain its unchanged original lightning in daylight, darkness, daylight return, and after resource reload, while retaining intended darkness glow and unchanged unrelated variant and Great White glow behavior.  
**Owner:** Repository maintainer  
**Dependencies:** BFS2-PHASE-003, BFS2-REQ-013, BFS2-REQ-022, EXT-001, EXT-002  
**Canonical requirements:** BFS2-REQ-013  
**Documentation and release impact:** Update `README.md`, `DOCUMENTATION.md`, `docs/README.md`, `docs/test/debug-diagnostics.md`, the existing asset ledger, and the phase render evidence with verified behavior. Prepare matching wiki prose from merged tracked documentation only. No public artifact publication occurs.  
**Next transition:** BFS2-PHASE-005 at its first numbered Work Packages entry  

**Entry criteria**

- Phase 003 pull request is merged through GitHub, `origin/1.20.1` contains its merge commit, and its signed annotated tag is verified. At execution start, record that exact sequential default commit as the Phase 004 source baseline; do not treat the PR29 planning baseline as the phase branch.
- `EXT-001` confirms the laptop capability and `EXT-002` confirms EnVy identity, signing availability, branch protection, and required check visibility.
- Create or update the matching phase milestone before implementation. The phase branch starts from that verified default commit, and the current render-path and two source-art hashes match the recorded inputs before any repair work.
- Phase 000’s `IFC-001` and `IFC-008` interfaces are available at the integrated revision. A missing interface or prerequisite stops this phase at the earliest dependent task.

**Implementation scope**

- `BFS2-REQ-013`: reproduce the daylight transition through the actual renderer, add only the narrow nondestructive renderer or layer behavior needed to preserve markings and dark glow, and keep resources byte-identical.

**Execution order**

1. `P004-TASK-001` executes BFS2-REQ-013 and BFS2-REQ-022 by binding the current resource hashes, inspecting the actual render selection and reproducing the light transition with scoped signals before implementation.
2. `P004-TASK-002` executes BFS2-REQ-013 after P004-TASK-001 confirms the causal path, by implementing and testing a nondestructive marking and glow pass without changing authored pixels.
3. `P004-TASK-003` executes BFS2-REQ-013 and BFS2-REQ-022 after P004-TASK-002, using headless checks first and the required silent-laptop targeted visual workflow for residual presentation claims.
4. `P004-TASK-004` executes BFS2-REQ-013 and BFS2-REQ-022 after P004-TASK-003, by recording exact art, diagnostics, limitations, render proof, and operator support instructions in tracked documentation.
5. `P004-TASK-005` contributes BFS2-REQ-013, BFS2-REQ-021, and BFS2-REQ-022 through IFC-008 after every preceding task passes, through required private independent review, required checks, merge commit integration, resulting default verification, signed phase tag, and postmerge wiki and tracking reconciliation.

**Required evidence**

- Byte-for-byte hashes of both supplied Zippy PNGs before and after repair, source-layer and resource-resolution regression results, and a bounded `render` capture showing variant, texture, mask, layer, raw brightness, selection reason, reload generation, and result.
- A silent laptop sequence showing the same spawned Zippy in daylight, darkness, daylight return, and after the actual resource reload, plus a comparison with an unaffected Common Thresher skin and existing Great White glow.
- `IFC-008` evidence binding source commit, candidate artifact SHA 256 and SHA 512, dependencies/config digests, sanitized fixture/world identity, both host roles, tick windows, result, and cleanup result.

**Exit criteria**

- `BFS2-AC-013` passes. Zippy has visible original lightning before darkness, during darkness, after daylight return, and after resource reload. There is no missing texture, opaque mask background, altered variant, or changed supplied pixel hash.
- The Phase 004 local diagnostic controls pass on, status, off, empty target set, selected-target removal, timeout, reload, bounds, redaction, disabled-overhead, enabled-bound, and completeness checks. Server-originated requests cannot start, inspect, or receive the local capture; inherited server permission evidence remains under `IFC-001`.
- Other Common Thresher variants and existing Great White glow regressions pass at the verified candidate revision.
- The phase branch is reviewed, merged to `1.20.1` with required checks, the resulting default is verified, `bfs2-phase-004` is signed and pushed, documentation and wiki obligations are complete, and no known mandatory phase-owned defect remains.

## Shared Contract Projection

```json
{
  "phase_id": "BFS2-PHASE-004",
  "canonical_requirement_ids": [
    "BFS2-REQ-013"
  ],
  "interfaces": [
    {
      "id": "IFC-001",
      "signature": {
        "capture": "start(category: Category, ticks: int[20,36000]=1200, targets: EntityRef[0,32]) -> CaptureResult",
        "record": {
          "format": "bfs-debug-v2",
          "schemaMinor": "int>=0",
          "captureId": "opaque string",
          "sequence": "long>=0",
          "tick": "long",
          "side": "server|client",
          "dimension": "registry ID",
          "entity": "session pseudonym|null",
          "entityType": "registry ID|null",
          "event": "bounded enum",
          "settingsRevision": "long|null",
          "intentId": "opaque string|null",
          "reason": "bounded enum",
          "data": "typed event payload"
        },
        "status": "status() -> enabled, side, categories, targets, remainingTicks, wallDeadline, counters, exactOutputPath",
        "stop": "stop(reason: StopReason) -> terminal completeness summary"
      },
      "acceptance_ids": [
        "BFS2-AC-022",
        "BFS2-AC-020"
      ]
    },
    {
      "id": "IFC-008",
      "signature": {
        "evidence": {
          "schema": "int=1",
          "requirementIds": "stable ID[]",
          "taskIds": "stable ID[]",
          "sourceCommit": "git object ID",
          "artifactSha256": "hex string",
          "artifactSha512": "hex string",
          "dependenciesDigest": "hex string",
          "configDigest": "hex string",
          "fixtureId": "string",
          "seed": "long|null",
          "world": "sanitized instance ID|null",
          "hostRoles": "headless_server|laptop_client[]",
          "tickWindow": "start,end|null",
          "result": "passed|failed|unverified",
          "cleanup": "complete|incomplete"
        },
        "integrate": "verified phase branch -> checked GitHub merge commit on 1.20.1 -> verified resulting default -> signed annotated phase tag"
      },
      "acceptance_ids": [
        "BFS2-AC-001",
        "BFS2-AC-020",
        "BFS2-AC-021"
      ]
    }
  ]
}
```

`IFC-001` is produced by Phase 000 and consumed here without a competing capture subsystem. P004-TASK-001 adds a bounded client-side `render` observation to the existing `bfs-debug-v2` contract before P004-TASK-003 depends on it. It records actual resource choice and layer decision, not a claim that a screenshot proves renderer state. The client observation remains local and cannot mutate server gameplay. `IFC-008`, also produced by Phase 000, binds the phase’s source, packaged artifact, configuration, fixtures, host roles, result, and cleanup through P004-TASK-005. It is consumed by Phase 005 as provenance only; it does not authorize future work before integration.

## Inputs and Upstream Contracts

| Input or contract | Provider | Required state | Validation | Failure behavior |
|---|---|---|---|---|
| `IFC-001` | BFS2-PHASE-000 | Existing bounded capture is default-off and supports client `render` extension without changing its v2 signature. | Check dispatcher, parser, capture schema, bounds, and local-output behavior at phase branch start. | Stop dependent visual assertion, repair the prerequisite through its owner path, and rerun P004-TASK-001. |
| `IFC-008` | BFS2-PHASE-000 | Candidate identity and integration evidence schema are available. | Validate required evidence fields and source/artifact/config digests. | Mark evidence unusable and rerun from the earliest identity-producing gate. |
| render baseline | BFS2-PHASE-003 | Approved default includes current Common Thresher renderer and exact two resource hashes. | Verify ancestry, paths, hashes, and current layer registration. | Stop. Do not copy code or art from the historical root. |
| laptop client capability | EXT-001 | Actual laptop anchor, desktop session, discrete renderer, disposable instance, window-to-stream binding, and private server reachability can be verified at use time. | Perform the host and audio sequence in the local runbook. | Stop the owned client and leave only the client gate unverified. No node-1 graphics or singleplayer fallback. |
| repository and signing capability | EXT-002 | EnVy identity, registered signing key, required-check access, protected default, and merge capability are current. | Revalidate before commit, PR, merge, and tag. | Do not commit, tag, or claim integration. |

## Outputs and Downstream Contracts

| Output or contract | Consumer | Guaranteed state | Compatibility or versioning | Evidence |
|---|---|---|---|---|
| Zippy render repair | Players and BFS2-PHASE-005 | Existing Zippy resource IDs and supplied bytes remain unchanged while marking and glow visibility follows light and reload behavior. | Forge 47.2.0 and GeckoLib 4.4.7; `_glowmask` remains the executed suffix; no save, network, or public registry change. | Hashes, targeted layer/resource regressions, and silent-laptop sequence. |
| local render diagnostics | Operators and final regression | Existing `bfs-debug-v2` client capture can explain Zippy resource and layer choices with bounded, redacted fields. | Additive phase-local event payload only; no incompatible format change. | Parser and control checks, sanitized capture, support guide. |
| phase evidence packet | BFS2-PHASE-005 and BFS2-PHASE-007 | `IFC-008` record identifies the merged source and tagged phase result with cleanup status. | Schema 1, immutable historical evidence retained. | Merged default and signed `bfs2-phase-004` tag evidence. |

## Work Packages

| Task ID | Requirement IDs | Work | Inputs and dependencies | Outputs | Affected components or interfaces | Verification |
|---|---|---|---|---|---|---|
| P004-TASK-001 | BFS2-REQ-013, BFS2-REQ-022 | Create the phase milestone, reproduce the brightness transition, bind exact art, inspect actual variant, texture, mask, and layer mechanism, and add scoped signals before repair. | BFS2-PHASE-003, IFC-001, EXT-001, pinned render paths and source-art hashes. | Causal reproduction record, art manifest, diagnostic field contract, and current phase milestone. | `CommonThresherSharkEntity`, `CommonThresherRenderer`, `ConditionalAutoGlowingLayer`, `BfsClientDebugManager`, IFC-001. | Static checks followed by one bounded silent-laptop baseline reproduction with real client render capture, then failure classification before code change. |
| P004-TASK-002 | BFS2-REQ-013 | Implement the minimal nondestructive normal-light and glow rendering arrangement that preserves original markings and dark glow. | P004-TASK-001 causal record and exact existing art. | Narrow renderer or layer change, unchanged PNG hashes, focused regression tests. | Existing Common Thresher renderer and conditional glow layer only. | Bright and dark layer selection, reload behavior, texture fallback, transparent-background, other skin, and Great White glow regressions. |
| P004-TASK-003 | BFS2-REQ-013, BFS2-REQ-022 | Run the evidence ladder and final silent-laptop visual sequence for day, dark, return, reload, and unrelated regressions. | P004-TASK-002, IFC-001, IFC-008, EXT-001. | Sanitized headless and client evidence records with complete cleanup status. | Existing GameTest and audit surface, client diagnostics, renderer, dedicated server runtime. | Targeted unit and parser checks, server fixture readiness, client capture and final candidate visuals with no lower-fidelity substitute. |
| P004-TASK-004 | BFS2-REQ-013, BFS2-REQ-022 | Update asset ledger, diagnostics support guide, render evidence, README and documentation cross-links from verified results. | P004-TASK-003 completed packet. | Accurate tracked documentation and prepared postmerge wiki update. | Existing asset ledger, `docs/test/debug-diagnostics.md`, `README.md`, `DOCUMENTATION.md`, `docs/README.md`, verification evidence. | Documentation review against artifact hashes, diagnostics procedure, and evidence limits. |
| P004-TASK-005 | BFS2-REQ-013, BFS2-REQ-021, BFS2-REQ-022 | Review, integrate, verify default, tag, then publish matching wiki content and reconcile phase tracking. | P004-TASK-001 through P004-TASK-004, EXT-002. | Merged default commit, signed `bfs2-phase-004` tag, phase evidence packet, and postmerge documentation state. | GitHub PR, milestone, wiki, IFC-008. | Complete diff, required checks, required private independent review, GitHub merge commit, default ancestry, tag signature, and cleanup audit. |

P004-TASK-001 begins with static inspection and bounded diagnostic self-tests, then runs its required silent-laptop baseline daylight reproduction before any renderer edit. It cannot run that visual assertion before exact client identity and silence are proven. P004-TASK-002 must use the measured selection and mask behavior from P004-TASK-001. If the layer mechanism differs from the hypothesis, it must record the observed path and choose the minimal solution that preserves the declared visual invariant. It must not rename the correct `_glowmask`, modify source PNG pixels, change the selected variant, or add an animated effect. P004-TASK-003 cannot turn a headless source assertion into a visual pass. P004-TASK-004 records results only after proof passes; a failed or unverified client gate is documented as such. P004-TASK-005 runs only after all phase criteria pass.

## Architecture and Implementation Boundaries

The server entity selects whether it has a glowing layer by local raw brightness, while the Forge client renderer registers the Common Thresher’s GeckoLib layer. GeckoLib’s automatic glow-mask loading destructively derives a base texture, so a conditional omission of the glow layer can make supplied markings disappear. The repair belongs at the existing client renderer and layer boundary: compose the normal visible marking pass and the darkness glow pass from the already-authored resources without modifying texture files.

The final implementation must retain the current selected resource identifier and skin routing. It must account for four states: brightness at or above eight, brightness below eight, a daylight return after darkness, and resource manager reload. It must make absence explicit: a missing resource, failed texture resolution, incompatible mask dimensions, or unexpected alpha/background does not silently select another skin or paint a fallback glow. It reports the reason through local render diagnostics and preserves the safe current resource path until corrected.

No server state, NBT schema, entity behavior controller, networking, configuration field, or persisted user data changes. Rendering is client-only, and the client diagnostic capture remains separately authorized and local. The change must avoid repeated image decode, allocation, or resource manager traversal per frame. Cache invalidation must follow actual resource reload semantics so old derived texture state cannot survive reload. Any cache or derived layer created by the candidate is test-owned only during verification and is removed with the disposable instance.

## Failure, Recovery, and Edge Cases

| Scenario | Detection | Required behavior | Recovery or rollback | Regression proof |
|---|---|---|---|---|
| Daylight suppresses glow after base mask extraction. | `render` capture shows Zippy variant, `_glowmask`, raw brightness at least eight, omitted glow layer, and missing normal marking pass. | The normal pass preserves visible authored lightning without forcing emissive glow. | Restore the narrow prior renderer change, retain source art, inspect actual selection, and rerun from P004-TASK-001. | Daylight screenshot and capture, exact pixels, no opaque background. |
| Darkness glow is absent, doubled, or masks all base detail. | Capture records brightness below eight, selected passes, and compositing reason; targeted visual proves presentation. | Exactly intended dark glow appears while original texture remains valid. | Correct only the layer composition or cache lifecycle. | Dark screenshot, layer regression, and other variant control. |
| Daylight return or resource reload leaves stale derived texture state. | Reload generation and resource identity differ from active cached record, or postreload sequence differs. | Clear or rebuild only owned derived state and reproduce current resource selection. | Stop capture, recreate disposable client state, and rerun day, dark, day, reload from the earliest changed resource gate. | Bounded sequence after actual reload and reload-reset diagnostics. |
| Resource missing, malformed, wrong-sized, or opaque background appears. | Explicit resource-resolution failure, dimensions or alpha predicate, or texture fallback record. | Fail safely with an actionable reason; do not silently substitute another variant or claim visual success. | Restore verified resource package and rerun static and client gates. | Negative fixture or focused resolver test plus untouched-hash manifest. |
| Other Common Thresher skin or Great White glow changes. | Variant and entity-type control captures plus comparison visuals. | Their existing selection and glow behavior remain unchanged. | Revert the overbroad conditional or renderer registration. | Regression tests and targeted control visuals. |
| Target set is empty, selected entity is removed, or capture expires during reproduction. | Local status reports selected count, target removal, timeout, or terminal completeness reason. | Do not fabricate a record or pass. Stop cleanly and restore the one-entity fixture. | Reset fixture and capture window, then repeat once exactly one BFS entity is automatically selected. | Empty-set, removal, timeout, status, and off checks. |
| Capture reaches an inherited limit or I/O fails. | `CAPTURE_INCOMPLETE`, counters, footer, or `IO_FAILURE`. | Evidence is unusable for acceptance and gameplay remains unchanged. | Retain minimal sanitized failure, correct bounded fixture or output path, then rerun. | Overflow, output-limit, and disabled-versus-enabled parity checks. |
| Laptop stream identity or mute cannot be proven. | Window PID, descendant-stream correlation, or mute readback is missing. | Stop the owned client before assertions and leave the visual gate unverified. | Reconcile only the owned instance and retry after capability restoration. | Host, renderer, window, stream, and teardown record. |

## Diagnostics and Debugging

**Requirement IDs:** BFS2-REQ-013, BFS2-REQ-022  
**Task IDs:** P004-TASK-001, P004-TASK-002, P004-TASK-003, P004-TASK-004  
**Controls:** Use the existing laptop-local `/bfs debug client on`, `/bfs debug client status`, and `/bfs debug client off` controls. `on` automatically selects up to 32 BFS living entities within 128 blocks and has no target argument, so the fixture contains exactly one BFS living entity within that radius for each capture. The server cannot start, inspect, or receive this local capture. Server console setup establishes only entity identity and state through the real server path. P004-TASK-001 adds bounded render payloads to this existing client manager and records the controls and automatic target selection in the support guide before P004-TASK-003. Unknown or unavailable local client state, target removal, timeout, reload, repeated off, and output failure return bounded reasoned results without changing gameplay.  
**Signals:** Use existing `bfs-debug-v2` header identity plus `render.variantId`, `render.baseResource`, `render.maskResource`, `render.rawBrightness` in local raw-light units, `render.layer` as `base`, `marking`, or `glow`, `render.selected` boolean, `render.reason`, `render.resourceReloadGeneration`, `render.textureHash`, `render.maskHash`, `render.alphaBackgroundCheck`, and client frame/tick sequence. All records include client side, session pseudonym, entity type, capture ID, sequence, tick, and reason. Missing values are null with a reason.  
**Collection procedure:** Follow the local numbered runbook below. It uses default-off automatic capture with a one-entity fixture inside the 128-block selection radius and bounded lighting transitions, preserves exact artifacts, redacts endpoints, paths, chat, and identities, and stops on incompleteness.  
**Headless verification:** Inspect the actual Gradle task graph before running only non-graphical focused tests. Add source or unit coverage for resource identity, hash comparison, brightness boundary on both sides of eight, layer decision, reload invalidation, missing-resource handling, and untouched controls. A dedicated `node-1` server may confirm fixture readiness and server-side entity identity with `eula=true` readback, but it cannot establish rendering or local capture state.  
**Client verification:** The residual claim is visible original lightning and intended dark glow across day, dark, return, and resource reload. The laptop must provide the actual discrete renderer, client render capture, and one targeted visual for each state plus two unaffected controls. If authorized controls cannot capture the needed visual or human presentation judgment is specifically required, request that exact action with expected frame and comparison. A server log never closes this claim.  
**Client audio isolation:** Before launch, discover the isolated pinned-version client instance and set only its master audio option to zero. Verify the laptop desktop session and discrete renderer. After launch, use `hyprctl clients -j` to bind the exact owned window address, class, title, and PID, then correlate only that PID tree to its PipeWire node or PulseAudio sink input. Mute that transient application stream with `wpctl` or `pactl` and read back its muted state before any assertion. After resource reload, reconnect, device change, restart, or stream recreation, repeat correlation and muting. Never touch the default sink, a microphone, a personal instance, or an unrelated application. At teardown stop the owned client and watcher, confirm window, process, and stream exit, and remove the isolated audio state. Failure to prove identity or mute stops the client gate.  
**Budgets and privacy:** The existing local capture is default-off, automatically selects at most 32 BFS living entities within 128 blocks, lasts at most 1200 client ticks or 90 wall seconds, samples presentation every four ticks, limits its queue to 8192 records, record size to 16 KiB, capture size to 32 MiB, and directory size to 256 MiB. This phase creates a one-entity fixture and stops the capture within 200 client ticks after the last state observation, preserving the stricter existing client budgets. Render payloads are event-driven and must not add render-thread I/O or per-frame dumps. Retain only minimal sanitized event excerpts and selected required comparison frames, never private endpoints, chat, player names, credentials, or absolute personal paths.  
**Regression and support:** Test controls, parser handling, disabled overhead, bounded enabled overhead, unchanged selection behavior, and the phase visual sequence. Update `docs/test/debug-diagnostics.md` so operators can enable scoped render capture, reproduce brightness and reload transitions, disable capture, find the output, redact it, and submit the minimum support packet. Keep the reusable diagnostics; remove only test-created captures, runtimes, watchers, routes, and redundant frames after their final consumer.

| Signal | Source and unit | Expected observation |
|---|---|---|
| `render.variantId`, `render.baseResource`, `render.maskResource` | client renderer, registry/resource identifier | Zippy selects existing Zippy base and `_glowmask`; controls select their own unchanged resources. |
| `render.rawBrightness`, `render.layer`, `render.selected`, `render.reason` | client renderer, local raw-light unit and bounded enum | At brightness at least eight, an ordinary marking pass remains selected. Below eight, intended glow is selected without removing base markings. |
| `render.textureHash`, `render.maskHash`, `render.alphaBackgroundCheck` | resource resolution, SHA 256 and boolean | Both authored hashes match the entry manifest and no opaque mask background enters the presentation. |
| `render.resourceReloadGeneration` | client resource manager, monotonic reload generation | Reload changes or revalidates the owned resource generation and yields the same required selection behavior. |
| capture counters and terminal reason | IFC-001 status/footer, records and bounded enum | No dropped records, limit stop, I/O failure, or missing terminal footer is accepted as proof. |
| window PID, renderer, stream mute | laptop host checks, process identifier and mute boolean | Exact owned client uses discrete renderer and its matched application stream is muted before every visual assertion. |

### Local collection and support procedure

1. Resolve the verified sequential default commit for the phase branch, then the baseline or candidate JAR SHA 256 and SHA 512, Forge 47.2.0, GeckoLib 4.4.7, dependency and configuration digests, and exact test-owned scratch paths. Discover the laptop project anchor and isolated client instance through the authorized connection. Create unique disposable runtime directories only beneath the verified project anchor after parent exclusions and active-use checks. Register teardown for the `node-1` server, laptop client, audio watcher, world, temporary configuration, logs, captures, screenshots, and scratch evidence before launch.
2. Rehash the two tracked Zippy resources. Run focused non-graphical resource and layer tests, including raw-light values seven and eight, resource reload invalidation, missing-resource handling, and unchanged controls. During the dedicated-server fixture in the next step, run the local client-control regression before a BFS entity enters the 128-block search volume. Assert `/bfs debug client on` reports zero selected targets, `/bfs debug client status` reports the local session, and `/bfs debug client off` is idempotent. Record the baseline failure or precise evidence gap before P004-TASK-002.
3. For baseline P004-TASK-001 reproduction and final P004-TASK-003 acceptance, create a disposable no-GUI dedicated server on `node-1`, set and read back only that runtime’s `eula=true`, confirm readiness and existing private reachability, and record the sanitized endpoint internally. Build or place the exact baseline or candidate on both hosts only after verifying source, artifact, dependency, and config identity. Use the server console to prepare a bounded water fixture with an open-sky position that reads raw brightness eight or higher and a removable opaque roof position that reads raw brightness seven or lower. Spawn one Common Thresher and use the real lightning strike path that calls `thunderHit` to set its synchronized `Variant.ZIPPY` value. Confirm `isZippy()` before it enters the 128-block search volume. Keep it as the sole BFS living entity in that radius. Hold each brightness state for 20 simulation ticks. For controls, replace the sole selected entity with one unchanged `DEFAULT_1` Common Thresher and then one Great White, still keeping exactly one BFS entity within 128 blocks. Use actual resource reload as the fourth stimulus.
4. Before each P004-TASK-001 baseline or P004-TASK-003 candidate client launch, complete the client audio-isolation sequence and verify the actual discrete renderer. Use a supported version-specific direct-connect or authorized desktop control to join the exact owned server and confirm intended player and world on both sides. Execute `/bfs debug client on`, then `/bfs debug client status`; assert that the local capture selected exactly one entity and no exclusion occurred. The client command owns the capture. Server console setup establishes only fixture/entity state and never starts, inspects, or receives client records.
5. For baseline reproduction, observe the open-sky Zippy state at raw brightness eight or higher and record the actual variant, base resource, mask resource, layer decision, and visible marking result before implementation. Stop with `/bfs debug client off`, verify `/bfs debug client status` shows inactive with a complete terminal summary, retain a sanitized baseline record, and tear down this early owned client/server fixture. For final acceptance, repeat with the candidate through daylight, darkness, daylight return, and postreload states, never exceeding 200 client ticks before `/bfs debug client off`. Recheck stream identity and mute immediately after resource reload.
6. The falsifiable oracle is that every Zippy frame contains the original lightning, every selected base and mask hash matches the entry manifest, daylight at raw brightness eight or higher has the normal marking pass without unintended emissive-only substitution, darkness at raw brightness seven or lower includes intended glow, return restores the daylight result, reload yields the same sequence, and neither one-entity control changes behavior. Do not repeatedly poll screenshots for values present in records. Inspect captures by capture ID, sequence, selected-target count, render layer, raw brightness, reload generation, and reason. A missing record, zero or multiple selected fixture targets, invalid hash, mismatch, missing terminal footer, or incomplete capture is a failure or unverified gate, never a pass.
7. Retain only sanitized decisive excerpts, required targeted visuals, identity manifests, and `IFC-008` evidence. Update the support guide with the real client commands, automatic 128-block target selection, one-entity fixture, raw-light stimuli, and redaction instructions. Gracefully stop exact owned server, client, watcher, and temporary audio route; verify their processes and playback stream are gone; remove only verified test-created runtime, world, logs, crash reports, temporary configuration, downloads, redundant frames, and scratch paths on both hosts. Report any exact leftover as cleanup incomplete and reconcile it before a new disposable run.

## Verification Matrix

| Requirement or task | Static or unit | Integration | Real workflow or runtime | Negative and recovery | Execution host and prerequisites | Evidence artifact |
|---|---|---|---|---|---|---|
| P004-TASK-001, BFS2-REQ-013 | Compare source hashes, resource IDs, suffix, brightness values seven and eight, layer choice, and local control behavior. | Verify selected entity to renderer to conditional-layer path and bounded diagnostic schema. | One silent laptop baseline reproduction after static diagnostics, using the one-entity fixture at raw brightness eight or higher. | Missing mask, wrong dimensions, opaque background, wrong variant, zero or multiple selected targets, client capture failure, mute ambiguity, timeout, and output failure. | `node-1` headless fixture server plus verified Linux laptop client under EXT-001, matched baseline identity, joined-world confirmation, prelaunch zero volume, and verified application mute. | Focused test reports, hash manifest, complete local capture, one baseline visual, host/audio record, and cleanup confirmation. |
| P004-TASK-002, BFS2-REQ-013 | Renderer/layer regression for bright, dark, boundary, return, reload and controls. | Verify resource reload invalidates only owned derived state and keeps saved/resource identifiers intact. | Dedicated server fixture may validate entity identity only. | Cache staleness, resource failure, duplicate glow, missing daylight markings, unaffected variants. | `node-1` only for truly headless tests. If server is used, unique runtime, `eula=true` readback, readiness, and teardown. | Test reports and bounded server fixture record, explicitly limited to nonvisual claims. |
| P004-TASK-003, BFS2-REQ-013, BFS2-REQ-022 | Parser, enable/status/off, permission, limits, redaction, disabled-overhead and capture-completeness tests. | Verify candidate identity on both hosts, real server fixture, and client capture correlation. | Actual silent laptop day, dark, return, reload sequence and two targeted controls. | Removed target, reload stream recreation, failed connection, renderer mismatch, mute ambiguity, incomplete capture, and control regression. | `node-1` headless server plus verified Linux laptop client under EXT-001, matched artifacts, existing private connection, joined-world confirmation, prelaunch zero volume and verified application mute. | Sanitized `bfs-debug-v2` capture, visual comparison set, host/audio record, IFC-008 record, cleanup confirmation. |
| P004-TASK-004, BFS2-REQ-013 | Review all factual claims against evidence and hashes. | Check README and docs cross-links and support instructions. | No runtime claim. | Failed/unverified visual gate remains explicit; do not publish it as completed. | Repository checkout only, no temporary process or files beyond the edited tracked docs. | Reviewed docs and phase evidence references. |
| P004-TASK-005, BFS2-REQ-013, BFS2-REQ-021, BFS2-REQ-022 | `git diff --check`, full diff, source/artifact manifest and documentation review. | Required checks, required private independent review, merge commit, default ancestry, tag signature. | No client. | Signing failure, blocked merge, failed check, stale default, tag mismatch, or incomplete cleanup stops transition. | Authenticated repository capability under EXT-002. No runtime resources are created by this audit. | PR state, resulting default commit, signed `bfs2-phase-004` tag, final evidence packet. |

## Documentation, Operations, and Release

P004-TASK-004 updates the existing asset ledger with both Zippy resource paths, their unchanged hashes, the render-use mapping, and the distinction between normal marking and dark glow. It updates `docs/test/debug-diagnostics.md` with exact scoped render collection, status, stop, redaction, and cleanup behavior, then links verified user-facing behavior and support guidance through `README.md`, `DOCUMENTATION.md`, and `docs/README.md`. It adds a phase-specific sanitized render evidence record that binds the source, artifact, fixture, observations, control regressions, visual limits, and cleanup status.

P004-TASK-001 creates or updates the corresponding milestone before implementation and P004-TASK-005 updates its linked issue or PR status before integration. After an approved merge, it verifies `origin/1.20.1`, creates and pushes the signed annotated `bfs2-phase-004` tag, then publishes the prepared wiki change from merged tracked documentation and reconciles project or milestone state. It does not close unrelated issue28, publish a release, change historical tags, or present a test JAR as a public release.

## Risks and Evidence Invalidation

| Risk ID and owner task | Prevention | Detection | Recovery | Evidence invalidated | Reverification |
|---|---|---|---|---|---|
| BFS2-RISK-007, P004-TASK-001 through P004-TASK-003 | Preserve source PNGs, use nondestructive passes, and inspect real layer choice before repair. | Variant, mask, layer, brightness, reload, pixel-hash, and control signals. | Restore correct resource path or narrow composition and rerun from cause reproduction. | Any art hash, renderer path, GeckoLib version, resource pack precedence, or resource-reload change. | Static and client day, dark, return, reload, and control sequence. |
| BFS2-RISK-012, P004-TASK-001 and P004-TASK-003 | Default-off bounded local capture with a one-entity fixture and no render-thread I/O. | Status, selected-target count, counters, terminal footer, redaction, and off/on parity. | Stop capture, retain minimal sanitized failure, correct the bounded fixture, rerun. | Diagnostic schema, parser, bounds, writer, or client manager change. | Control, parser, limit, completeness, and behavior parity checks. |
| BFS2-RISK-013, P004-TASK-003 and P004-TASK-005 | Verify laptop renderer, prelaunch zero volume, exact stream mute, and resource teardown. | Host, desktop, renderer, window/PID, stream mute, process exit, and path absence. | Stop owned client on ambiguity, preserve user data, reconcile exact leftovers. | Laptop host, client instance, artifact/config identity, stream recreation, or cleanup failure. | Full split-host visual procedure and cleanup audit. |
| BFS2-RISK-001, P004-TASK-001 and P004-TASK-005 | Start from verified sequential default and preserve historical root and PR29 worktree. | Ancestry, complete diff, and source hash mismatch. | Stop mismatched work and recreate phase branch only from verified default. | Any upstream merge, branch, source, or tag change. | Revalidate Phase 003 integration, default ancestry, exact resource hashes, then rerun affected gates. |

## Phase Completion Packet

The closure packet contains the phase branch commit and complete diff review, exact authored-art hashes, focused test and parser outcomes, source and candidate artifact hashes, dependency and config digests, sanitized fixture and host records, complete bounded capture, targeted visual comparison set, renderer and muted-stream proof, cleanup results for each host, documentation changes, support guide verification, milestone and PR state, private review result, required check results, GitHub merge commit, verified resulting `origin/1.20.1` commit, and signed annotated `bfs2-phase-004` tag.

Every test, build, audit, server, client, capture, and visual workflow declares its exact disposable paths and owned processes before it begins. It retains only required sanitized evidence after its final consumer. It verifies graceful process exit, playback-stream disappearance, audio-watcher removal, and exact test-created path removal on every used host. A test result and cleanup result are recorded separately. `CLEANUP_INCOMPLETE` keeps this phase open until the exact leftovers are reconciled; source changes, personal instances, pre-existing worlds, shared caches, active PR29 worktree, owner `Content`, and unrelated resources remain protected.

## Next Transition

After all Phase 004 implementation, evidence, documentation, review, merge, resulting-default verification, signed tag, wiki, tracking, and cleanup gates pass, atomically advance the execution cursor to BFS2-PHASE-005 and begin its first numbered Work Packages entry. Do not create or start the Phase 005 branch before the Phase 004 pull request has merged and the resulting `1.20.1` tag is verified.
