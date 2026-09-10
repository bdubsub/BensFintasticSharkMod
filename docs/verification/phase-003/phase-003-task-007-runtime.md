# Phase 003 task 007 runtime and exact candidate evidence

Date: September 10, 2026

This record binds the Phase 003 runtime and artifact checks to the generated
output on commit `0f2bbe08cf5097e56bc1d2a73bd0a5bb9624878`.

## Environment and candidate

The target is Minecraft 1.20.1 with Forge 47.2.0, Java 17.0.19,
GeckoLib 4.4.7, and SmartBrainLib 1.14.2. The built Forge candidate is
`forge/build/libs/BensFintasticSharks-forge-1.20.1-0.24.jar`.

The first task 007 package was an interim artifact used for the initial
runtime probe. Its SHA-256 was
`b32bc654cbad4163605500a97bf143adcdc8ef33f35764dac569dda25fa3a488` and
its SHA-512 was
`80f7f768586584f7902016c9bc6e912b87a137cedf4350c20e8512000356ed0b0466cb5c42fb76a855516a770bf297d8e5678e718acde71e7927e671a17bae4e`.
The interim probe was superseded before owner approval.

The exact candidate used for the approved rerun has these hashes:

* SHA-256: `8a4127ce5f2b13ae034656fd639b4e3675f40a03e62183852b75a849250536d0`
* SHA-512: `f18257d43122c0e50fba8eab1c2fbe9720239ddf63882c484ef0c666f2436c9a42730ce12053f9cf2643eaed032f4ee122e3413870fb974b548aeb23232b708e`
* `unzip -tqq` passed.
* `META-INF/mods.toml` reports mod version `0.24` and the required Forge,
  Minecraft, GeckoLib, and SmartBrainLib dependencies.

The exact archive contains 49 advancement JSON files, the generated English
language file, all seven remastered icon textures, and one generated flat
model for each mapped icon item. The effective Harbor Seal model is
`assets/bensfintasticsharks/models/item/harbor_seal_block.json` with parent
`minecraft:item/generated` and layer
`bensfintasticsharks:item/harbor_seal_block`. The archive contains no
`shark_whisperer` path or language entry.

## Deterministic checks

The following checks passed against the exact candidate source tree.

* `./gradlew :forge:test --no-daemon --console=plain`, exit 0. Log SHA-256
  `1108644024504dc5706efd0fba8eb7b14cd61cd7fd40549ea8aeb995332289cd`.
* `./gradlew :forge:Data --no-daemon --console=plain`, exit 0. Log SHA-256
  `54c413c6b8ce8d933b3d569a6f3391d9e2b5d9b403613bcdf039ac07f2f8d353`.
  The generated resource tree was deterministic across two runs.
* `./gradlew :forge:GameTestServer --no-daemon --rerun-tasks --console=plain`,
  exit 0. All 82 required tests passed. Log SHA-256
  `e5edb3ff654ccfe5d92593f323245959cb05f8f69c455034402f7a2e502e3e4c`.
* `./gradlew :forge:build --no-daemon --console=plain`, exit 0. Log SHA-256
  `0cfc8a2f27e06b7b62fbddb84887f5f4da4de8140b77ce03d7837a2bd19517d6`.

## Final exact candidate server and laptop client

The exact candidate was copied to the packaged Forge `mods` directory in
node 1 runtime `/tmp/bfsm-p003-prod-final4-20260910`. This is a disposable
dedicated server runtime and not a client. It used Java 17.0.19, Forge
47.2.0, port `25982`, and `eula=true`. The server reached
`Done (14.139s)!` and reported this mod set through `forge mods`:

```text
minecraft 1.20.1
forge 47.2.0
geckolib 4.4.7
smartbrainlib 1.14.2
bensfintasticsharks 0.24
```

The dependency hashes were GeckoLib SHA-256
`6601d1911b80580dd2eb4b0ff754fc0ae2f0a62cfd4dfb6ecd8d145c87a81ec0` and
SmartBrainLib SHA-256
`3f0609e603181acf9006a5f7636dc2d75ab1b19f4a38ac6c6aa4ce548916146b`.
The final server log SHA-256 is
`d20e13749e26095ab121c82e0470193452916d55436047cf1a393e64176d1f4b` and
its SHA-512 is
`3b66447cef307d52a5a1182b34bfdf80cbab01646b6edc605bc1ed8000dfe70634de98abe1ecba67bf428e35cd855f29d800b2740060c1885aec7aad9b5bcc9d`.

The disposable Prism profile on EnVy's Linux laptop was
`/home/envy/.local/share/PrismLauncher/instances/bfsm-p003-runtime-20260910`.
It launched with Java 17.0.15 on the NVIDIA GeForce RTX 5090 Laptop GPU and
connected to `100.76.164.109:25982`. The client reported a modded-server
handshake, loaded 47 advancements, and rendered on workspace `special:chatgpt`
while the active workspace remained 2. The window was Minecraft 1.20.1 with
stable id `18000d5c` and PID `3713154`. The profile master volume was `0.0`
and no owned PipeWire or PulseAudio playback stream existed.

The client log SHA-256 is
`a35ce4126df8d620d2919b241bf626f5c31e8cf44d15da9a7152b02d06778dc`.
The connected world capture is `/tmp/bfsm-p003-final4-connected.png`,
SHA-256
`fc83a9258c2528a58181db27e843240f7cdbbcaa68fc80923fdc601f07551e4a`.
EnVy approved continuation after reviewing the connected exact candidate,
the remastered presentation, and the smooth movement result. This closes the
owner visual gate for task 007.

## Existing profile compatibility fixture

A disposable copy with a completed legacy
`bensfintasticsharks:shark_whisperer` progress entry was tested at
`/tmp/bfsm-p003-old-profile-final-20260910` on a second packaged Forge server
at port `25984`. The server used Java 17.0.19, the exact candidate, and the
same dependencies. It reached `Done (2.250s)!` and the laptop client joined
through the same XWayland RTX 5090 path with Java 17.0.15. The client loaded
50 advancements.

The server ignored the retired `shark_whisperer` progress entry, accepted the
login, and reported no data-pack, registry, or retired-node failure. The
progress file remained byte-identical before and after login at SHA-256
`cc79789df53e5c84a5b78e5b680cad118a2e990c8f916c7deb1e7c6f2ce73df7`.
The old-profile server log SHA-256 is
`5b1b8bd72fbdfa6f152e0f9fd298797c026fc6d5fd6d23d8327654ebe4f47319` and the
client log SHA-256 is
`2e30e2c6dc9cde350e968704fd835cbabb365388982c446cbada69ffbc15c601`.
The earlier Wayland positioning error was an environment launch issue; the
XWayland retry completed the login and compatibility check.

## Disposition

The exact candidate, deterministic checks, packaged server load, fresh
encounter progression, laptop connection, owner visual approval, and
old-profile no-migration behavior are evidenced. Task 007 is complete. The
remaining Phase 003 work is task 008 documentation, review, pull request,
merge, default-branch verification, cleanup, and signed phase tagging.
