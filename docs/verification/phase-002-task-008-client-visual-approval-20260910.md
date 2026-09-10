# Phase 002 client visual approval

Date: September 10, 2026

EnVy approved the complete Phase 002 visual checklist after connecting the
exact current candidate to the disposable production Forge server. The owner
approval closes the DEC-015 visual gate for the Phase 002 candidate.

## Bound candidate

* Source revision: `7aefa29`.
* Documentation and evidence revision: `0b54102`.
* Minecraft: 1.20.1.
* Forge: 47.2.0.
* Java: 17.
* Candidate: `forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar`.
* Candidate SHA-256: `fd6bce814e8d472273532fbb2b33304523fd54fb7ffe849a7e7b37fa7083fd1f`.
* Candidate SHA-512: `69fc2c75dd3d57fd06f4da5a873fd61f362564d59df02ed77f3d48861b193a9d926e00189520acf2c670ff0f48780c84f1e0d75ce34ce32eebccf9f2cde1df6d`.
* GeckoLib Forge 4.4.7 SHA-256: `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0`.
* SmartBrainLib Forge 1.14.2 SHA-256: `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b`.

## Runtime and connection

The server ran on node 1 as a headless production Forge server with Java
17.0.19. It used the private Tailscale endpoint `100.76.164.109:25973`, the
matching candidate and dependencies, and an exact disposable runtime with
`eula=true` verified before launch. The laptop client ran on `envision` with
the RTX 5090 Laptop GPU and Java 17.0.15. The client joined the server as
`EnVyOnMyMind`; the final server log records the login and join at
`(0.5,66.0,18.5)`.

The client was launched from the disposable Prism instance
`/home/envy/.local/share/PrismLauncher/instances/bfsm-p002-visual-20260910`
with these identity properties:

```text
-Dbfs.source.revision=7aefa29
-Dbfs.artifact.sha256=fd6bce814e8d472273532fbb2b33304523fd54fb7ffe849a7e7b37fa7083fd1f
-Dbfs.motion.profile.version=phase-002-pitched-propulsion
-Dbfs.configuration.fingerprint=p002-visual-20260910
-Dbfs.datapack.fingerprint=phase-002-generated
```

The owned window was verified as `Minecraft 1.20.1` with Hyprland stable ID
`18000ce6` and process ID `2597573`. Its PipeWire sink input was `389922`,
with process ID `2597573` and `Mute: yes` throughout the approval run.

## Owner checklist

EnVy approved each item below on the bound laptop client:

1. Cod ascends and descends with forward propulsion, without lateral wobble,
   circles, or a stationary stall.
2. Salmon uses the same smooth pitched propulsion, including fast swimming.
3. Tiger Shark uses smooth body pitch and forward propulsion without circles or
   stuck routes.
4. Oceanic Whitetip and the other sharks move and animate without frozen or
   missing presentation.
5. Pitch transitions are smooth, without snapping to the sky or ground while
   stationary.
6. Idle, swim, fast swim, hit, death, Cod flop, Salmon spin and flop, Oceanic
   bite and thrash, beached, and death presentations are present and coherent.
7. The scene has no wrong textures, missing bones, rendering errors, registry
   warnings, or disconnects.

The screenshot retained for window and rendered-scene identity showed the
client window and the disposable test tank. The direct owner approval is the
evidence for the movement and animation appearance; the screenshot alone is
not treated as a motion trace.

## Retained hashes and cleanup

Sanitized evidence was retained outside the repository until phase closure:

| Evidence | SHA-256 | SHA-512 |
| --- | --- | --- |
| Client log | `1547054bc4edfc99b6a7981267281e3178f2b9114c97559fa56e2bbe9815c2ae` | `812f4ffa9bcbca3e609accb9c5b32e18fcd7e63bdf8f25948214aa95f454f3848d6f09264b6ff904e101f5749dd491acafad4d97c4f80f896a450cd72a16e213` |
| Server console log | `35ccc19b871bd8943e4b7d113cc9dc6fb795d904990a66ccf1b1b827871105b8` | `1dcb05dc0e4bd0760478baa79aee6cc15ffd84063b2b02aeb82c1c1897f7c03215722e51d816d63834e15ae1adccfb9f01f3c17c08b6d3a99f3dd36ba13e9006` |
| Approval screenshot | `3d1717dbc5647814d9f2c241639157e94314c44fd4b500f180f4312462f54e11` | `6193ce0c5523299c7b8d126c1489ae7ca3e277a1dbceb2aeeb0cd3b278578fc27dad253f7fab892160fe79e5b47a2a019dd4430b291902b6e06eda81641c6ff3` |
The exact disposable server runtime, generated world, client clone, owned
processes, sockets, launcher log, and temporary screenshot were removed after
their final consumers completed. Cleanup was verified. The sanitized copies
used for this evidence remain under `/tmp/bfsm-p002-visual-*` outside Git and
are not release artifacts.
