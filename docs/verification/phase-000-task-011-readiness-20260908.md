# Phase 000 task 011 readiness and protected diff evidence

## Binding

This record closes the current candidate readiness portion of `P000-TASK-011`
for the exact Phase 000 branch source at commit
`73a65a8de7b709a3f143fd977e9b4135a3fa9878`. The run used Minecraft `1.20.1`,
Forge `47.2.0`, Java `17.0.19`, GeckoLib `4.4.7`, and SmartBrainLib `1.14.2`.
It ran on headless `node-1` only. No client, renderer, display server, or
virtual display was started.

## Deterministic candidate checks

The clean detached verification tree ran the required command sequence:

```text
./gradlew :forge:clean :forge:test :forge:Data :forge:GameTestServer :forge:build --no-daemon --rerun-tasks
```

The command completed successfully in 1 minute 19 seconds. The dedicated
GameTest server reported `All 22 required tests passed :)`. Data generation
completed without a reported resource failure, and the Forge build completed
successfully.

The candidate Forge artifact is
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar`.

* SHA-256: `87fa9a7188fad3eac0346e36973d0bf5f801cf088eadf2181afb92e99323bf1e`
* SHA-512: `e8942558a57a3f02ed2a25942e1aedb76546b297f009af123bd16b6c882c6caf50d50092ce5d1083c910ce22094b99722dbfbc7124f084018a6100fe2be28f38`
* `unzip -tqq` passed.

`git diff --check` passed. The temporary verification tree contained only the
pre-existing `build.gradle` line-ending difference, generated cache metadata,
and the ignored Forge log directory. No source, resource, plan, goal, or
owner-protected path changed in the verification tree.

## Dedicated server readiness

The exact candidate was started from the disposable runtime
`/tmp/bfsm-p000-task011-server-20260908` with Java 17 and:

```text
./gradlew :forge:Server --no-daemon -PbfsServerRunDir=/tmp/bfsm-p000-task011-server-20260908 --args='--port 25840 --nogui'
```

The runtime `eula.txt` was read back with `eula=true` before launch. The
server reached the Forge readiness marker:

```text
[20:48:49] [Server thread/INFO] [minecraft/DedicatedServer]: Done (13.347s)! For help, type "help"
```

The owned server was then stopped. Port `25840` was closed and the owned
server processes were absent after shutdown. The sanitized readiness log has
SHA-256 `bb86b31aa087f0ec622bf004be00a366962b9372431c600b737a7f903e18155e`.

## Disposition

This record closes the current candidate build, artifact, EULA readback,
dedicated-server readiness, and cleanup evidence for `P000-TASK-011`. It does
not close the Phase 000 integration task. `P000-TASK-012` still requires the
protected-state recheck, pull request integration into `envy/0.24`, fetched
ancestry verification, and the required signed phase tag. Later gameplay and
visual requirements remain assigned to their canonical phases.
