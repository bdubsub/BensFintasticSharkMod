# Phase 002 packaged server verification

Date: September 10, 2026

Commit: `7aefa29`

The exact Forge candidate was copied into a disposable production-style server
runtime with the matching dependency jars. The server started and reached the
ready marker under Java 17 without an error or exception, then shut down cleanly.

Environment:

* Host: node 1, headless dedicated server.
* Minecraft: 1.20.1.
* Forge: 47.2.0.
* Java: Eclipse Adoptium 17.0.19.
* Forge runtime: production `libraries` from the checked in server package.
* Server address: `127.0.0.1:25951`.
* EULA: `eula=true`, verified before launch.

Candidate artifact:

```text
forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar
sha256 fd6bce814e8d472273532fbb2b33304523fd54fb7ffe849a7e7b37fa7083fd1f
sha512 69fc2c75dd3d57fd06f4da5a873fd61f362564d59df02ed77f3d48861b193a9d926e00189520acf2c670ff0f48780c84f1e0d75ce34ce32eebccf9f2cde1df6d
```

Dependency hashes:

| Dependency | SHA-256 |
| --- | --- |
| GeckoLib Forge 4.4.7 | `6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0` |
| SmartBrainLib Forge 1.14.2 | `3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b` |

The candidate passed `unzip -tqq`. The jar contains the Forge metadata, mixin
metadata, and compiled pitched movement controller. No duplicate archive
entries were found.

The production log recorded Java 17, Forge 47.2.0, Minecraft 1.20.1, the
loopback bind, and `Done (14.061s)!`. The log contained no `ERROR`, `Exception`,
or `Caused by:` markers.

| File | SHA-256 | SHA-512 |
| --- | --- | --- |
| `logs/latest.log` | `c377f0eba0c1abd75990f5d2a67ca654fc2ca7ed4838fc8f039a51711d493530` | `1aa92d1817e90eb28d4ceb4f35ce0db3987dfe42da73c8646a92c6958931f6465b6e2f174348098905a8fd09925938a193c5aac17e340c6d485e44279d14e0d1` |

The disposable runtime and its generated world, configuration, and logs were
removed after the hashes were recorded. Cleanup was verified. Laptop visual
approval and multiplayer client acceptance remain open.
