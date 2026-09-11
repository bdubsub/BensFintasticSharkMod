# Release candidate installation and rollback

This procedure applies to Release Candidate 1.0, version `1.0-rc.1`, for
Minecraft 1.20.1 with Forge 47.2.0, Java 17, GeckoLib 4.4.7, and
SmartBrainLib 1.14.2. It is an installation and recovery guide, not a stable
1.0 publication notice.

## Install the candidate

1. Stop the client or dedicated server.
2. Back up the world, player data, server configuration, and the current mod
   JAR before changing anything.
3. Install the exact
   `BensFintasticSharks-forge-1.20.1-1.0-rc.1.jar` and matching dependency
   versions in the `mods` directory.
4. Verify the JAR with the SHA 256 and SHA 512 records in
   `docs/verification/artifacts/`.
5. Keep Minecraft, Forge, the mod, GeckoLib, and SmartBrainLib matched on every
   client and server.
6. Restart after changing either `[spawning] replace_vanilla_mobs` or
   `[spawning] fish_entities`. The default for both is `true`.

The distributable is the reobfuscated Forge JAR only. Do not install the
sources JAR, a Fabric artifact, a test runtime, a world, a log bundle, or a
private configuration file as a mod.

## Diagnostics before requesting a client

Use the trusted server console or a level 2 operator:

```text
/bfs debug on <all|movement|brain|combat|population|advancement|algae> <20-36000> [targets]
/bfs debug status
/bfs debug off
```

Capture files are bounded JSONL under the runtime's `logs/bfs-debug/` directory.
Use the procedure and strict parser in
`docs/test/debug-diagnostics.md`. Capture the exact candidate hash, runtime,
configuration, data pack, seed, coordinates, and start and end conditions.
An incomplete or unbound capture is evidence of a failed procedure, not a
passing behavior result. Use `/bfs debug client on`, `status`, and `off` only
on the graphics-capable laptop for rendering or input claims.

## Roll back safely

1. Stop the server and make a copy of the current logs and configuration.
2. Restore the backed-up world, player data, configuration, and previously
   verified Forge JAR as one matched set.
3. If only future algae generation is being disabled, use the documented data
   pack or biome modifier override. Existing algae blocks remain in the world.
4. Do not downgrade a world containing registered BFS algae blocks to a build
   that does not register those IDs. Restore the pre-upgrade backup first.
5. If the earlier Atlantic fish population fix is needed, use the verified
   `0.23-emergency-fix` artifact and follow the cleanup warning in
   `DOCUMENTATION.md`. It prevents new population overflow but does not delete
   existing fish automatically.
6. Restart the restored server, verify the matching dependency versions, and
   run a bounded readiness check before allowing players to reconnect.

Never move or recreate `bfs-0.24` or `bfs-0.24-final`. A failed candidate is
replaced by a forward phase change or a normal reviewed revert, not by rewriting
history or replacing an immutable tag.
