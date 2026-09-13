"""Run a bounded packaged Forge population and tick timing fixture on a headless host."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import queue
import re
import shutil
import signal
import subprocess
import threading
import time


SPECIES = {
    "great_white_shark": ("GreatWhiteSharkEntity", 1),
    "great_hammerhead_shark": ("GreatHammerheadSharkEntity", 1),
    "common_thresher_shark": ("CommonThresherSharkEntity", 1),
    "shortfin_mako_shark": ("ShortfinMakoSharkEntity", 1),
    "tiger_shark": ("TigerSharkEntity", 1),
    "oceanic_whitetip_shark": ("OceanicWhitetipSharkEntity", 1),
    "sandtiger_shark": ("SandtigerSharkEntity", 1),
    "blacktip_reef_shark": ("BlacktipReefSharkEntity", 3),
    "orca": ("OrcaEntity", 1),
    "bottlenose_dolphin": ("BottlenoseDolphinEntity", 4),
    "common_octopus": ("CommonOctopusEntity", 3),
    "caribbean_reef_octopus": ("CaribbeanReefOctopusEntity", 2),
    "nautilus": ("NautilusEntity", 1),
    "giant_moray_eel": ("GiantMorayEelEntity", 4),
    "green_sea_turtle": ("GreenSeaTurtleEntity", 3),
    "american_lobster": ("AmericanLobsterEntity", 10),
    "common_stingray": ("CommonStingrayEntity", 3),
    "harbor_seal": ("HarborSealEntity", 3),
    "black_sea_nettle_jellyfish": ("BlackSeaNettleJellyfishEntity", 1),
    "cannonball_jellyfish": ("CannonballJellyfishEntity", 2),
    "atlantic_cod": ("AtlanticCodEntity", 8),
    "atlantic_salmon": ("AtlanticSalmonEntity", 8),
}


def sha256(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def main():
    def interrupted(signum, frame):
        raise KeyboardInterrupt(f"Verification interrupted by signal {signum}")

    signal.signal(signal.SIGTERM, interrupted)
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--suite", type=Path, required=True)
    parser.add_argument("--libraries", type=Path, required=True)
    parser.add_argument("--jar", type=Path, required=True)
    parser.add_argument("--geckolib", type=Path, required=True)
    parser.add_argument("--smartbrainlib", type=Path, required=True)
    parser.add_argument("--java", type=Path, required=True)
    parser.add_argument("--name", required=True)
    parser.add_argument("--scale", type=int, choices=(1, 2), default=1)
    parser.add_argument("--ticks", type=int, default=36000)
    parser.add_argument("--warmup", type=int, default=2400)
    parser.add_argument("--port", type=int, default=25579)
    args = parser.parse_args()
    suite = args.suite.resolve(strict=True)
    if not re.fullmatch(r"[a-z0-9-]+", args.name):
        raise ValueError("Run names must be plain lowercase identifiers")
    runtime = suite / args.name
    runtime.mkdir()
    if runtime.resolve() != runtime or runtime.is_symlink():
        raise ValueError("Runtime must be a new direct child of the suite")
    (runtime / "mods").mkdir()
    for dependency in (args.jar, args.geckolib, args.smartbrainlib):
        shutil.copyfile(dependency, runtime / "mods" / dependency.name)
    (runtime / "libraries").symlink_to(args.libraries.resolve(strict=True), target_is_directory=True)
    settings = {"biome": "minecraft:lukewarm_ocean", "lakes": False, "features": False,
                "layers": [{"block": block, "height": height} for block, height in
                           [("minecraft:bedrock", 1), ("minecraft:stone", 94),
                            ("minecraft:sand", 1), ("minecraft:water", 31)]],
                "structure_overrides": []}
    properties = {"server-ip": "127.0.0.1", "server-port": args.port, "online-mode": "true",
                  "level-seed": 240024, "level-type": "minecraft:flat", "level-name": "world",
                  "generator-settings": json.dumps(settings, separators=(",", ":")),
                  "generate-structures": "false", "view-distance": 6, "simulation-distance": 6,
                  "difficulty": "normal", "gamemode": "creative", "max-tick-time": 60000,
                  "enable-rcon": "false", "enable-query": "false", "max-players": 1}
    (runtime / "server.properties").write_text("".join(f"{k}={v}\n" for k, v in properties.items()))
    (runtime / "eula.txt").write_text("eula=true\n")
    assert (runtime / "eula.txt").read_text() == "eula=true\n"
    command = [str(args.java), "-Xms2G", "-Xmx2G", "-XX:+UseG1GC",
               f"-javaagent:{suite / 'probe-agent.jar'}", f"-Dbfs.probe.ticks={args.ticks}",
               f"-Dbfs.probe.warmup={args.warmup}",
               "@libraries/net/minecraftforge/forge/1.20.1-47.2.0/unix_args.txt", "nogui"]
    manifest = {"run": args.name, "scale": args.scale, "seed": 240024, "ticks": args.ticks,
                "warmup": args.warmup, "jar_sha256": sha256(args.jar),
                "geckolib_sha256": sha256(args.geckolib),
                "smartbrainlib_sha256": sha256(args.smartbrainlib),
                "probe_agent_sha256": sha256(suite / "probe-agent.jar"),
                "probe_data_sha256": sha256(suite / "probe-data.jar"),
                "species_targets": {name: count * args.scale for name, (_, count) in SPECIES.items()},
                "fixture": "Command seeded persistent animals in an enclosed water volume. Natural spawning off.",
                "replenishment": "Missing animals restored every census interval. No forced targets or movement.",
                "start_time": time.time(), "pid": None, "teardown": "pending"}
    manifest_path = suite / f"{args.name}-manifest.json"
    manifest_path.write_text(json.dumps(manifest, indent=2) + "\n")
    messages = queue.Queue()
    console = (runtime / "console.log").open("w")
    process = subprocess.Popen(command, cwd=runtime, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                               stderr=subprocess.STDOUT, text=True, bufsize=1, start_new_session=True)
    manifest["pid"] = process.pid
    manifest_path.write_text(json.dumps(manifest, indent=2) + "\n")

    def read_console():
        for line in process.stdout:
            console.write(line)
            console.flush()
            messages.put(line)

    reader = threading.Thread(target=read_console, daemon=True)
    reader.start()

    def send(command):
        process.stdin.write(command + "\n")
        process.stdin.flush()

    def wait_for(token, timeout):
        deadline = time.monotonic() + timeout
        while time.monotonic() < deadline:
            if process.poll() is not None:
                raise RuntimeError(f"Server exited with {process.returncode} before {token}")
            try:
                line = messages.get(timeout=1)
            except queue.Empty:
                continue
            if "PERF_FAILED" in line:
                raise RuntimeError(line.strip())
            if token in line:
                return line
        raise TimeoutError(token)

    def populate(counts):
        for index, (species, (class_name, target)) in enumerate(SPECIES.items()):
            missing = target * args.scale - counts.get(class_name, 0)
            for ordinal in range(max(0, missing)):
                x = -44 + (index % 6) * 16 + ordinal % 4
                z = -36 + (index // 6) * 22 + ordinal // 4
                y = 34 if species in ("american_lobster", "common_stingray", "common_octopus",
                                     "caribbean_reef_octopus", "giant_moray_eel", "nautilus") else 48
                send(f'summon bensfintasticsharks:{species} {x} {y} {z} '
                     '{PersistenceRequired:1b,Tags:["performance_fixture"]}')

    success = False
    try:
        wait_for('For help, type "help"', 180)
        for cmd in ["gamerule doMobSpawning false", "gamerule doDaylightCycle false",
                    "gamerule doWeatherCycle false", "gamerule logAdminCommands false",
                    "gamerule sendCommandFeedback false", "time set noon", "weather clear",
                    "forceload add -80 -80 79 79",
                    "fill -64 32 -64 -64 66 63 minecraft:glass",
                    "fill 63 32 -64 63 66 63 minecraft:glass",
                    "fill -63 32 -64 62 66 -64 minecraft:glass",
                    "fill -63 32 63 62 66 63 minecraft:glass",
                    "fill -50 32 -50 -45 38 -45 minecraft:stone",
                    "fill 45 32 45 50 38 50 minecraft:stone",
                    "fill -24 32 -24 24 32 24 minecraft:seagrass",
                    "kill @e[type=!minecraft:player]", "save-all flush"]:
            send(cmd)
        send("say PERF_FIXTURE_READY")
        wait_for("[Not Secure] [Server] PERF_FIXTURE_READY", 120)
        populate({})
        send("say PERF_POPULATION_READY")
        wait_for("[Not Secure] [Server] PERF_POPULATION_READY", 120)
        (runtime / "probe.start").touch()
        deadline = time.monotonic() + (args.ticks + args.warmup) / 10 + 180
        last_census = None
        while time.monotonic() < deadline:
            line = wait_for("PERF_", 60)
            print(args.name, line.strip(), flush=True)
            if "PERF_COMPLETE" in line:
                success = True
                break
            census_path = runtime / "probe-census.tsv"
            if census_path.exists():
                rows = census_path.read_text().splitlines()
                if len(rows) > 1 and rows[-1] != last_census:
                    last_census = rows[-1]
                    fields = last_census.split("\t")
                    counts = {name.removesuffix("Forge"): int(count) for name, count in
                              re.findall(r"(\w+)=(\d+)", fields[2])}
                    populate(counts)
        if not success:
            raise TimeoutError("The measured run did not complete within its wall deadline")
        for filename in ("probe-census.tsv", "probe-ticks.csv", "probe.complete"):
            shutil.copyfile(runtime / filename, suite / f"{args.name}-{filename}")
        manifest["result"] = "completed"
    except BaseException as exception:
        manifest["result"] = "failed"
        manifest["failure"] = str(exception)
        raise
    finally:
        if process.poll() is None:
            try:
                send("stop")
                process.wait(timeout=60)
            except (BrokenPipeError, subprocess.TimeoutExpired):
                process.terminate()
                process.wait(timeout=20)
        reader.join(timeout=5)
        console.close()
        manifest["exit_code"] = process.returncode
        manifest["end_time"] = time.time()
        manifest["console_sha256"] = sha256(runtime / "console.log")
        manifest["teardown"] = "process exited. runtime retained for final evidence inspection"
        manifest_path.write_text(json.dumps(manifest, indent=2) + "\n")
        print(args.name, "server stopped", process.returncode, flush=True)


if __name__ == "__main__":
    main()
