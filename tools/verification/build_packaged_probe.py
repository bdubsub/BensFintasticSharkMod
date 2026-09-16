"""Build the isolated compatibility probe for installed Forge 47.2.0."""

import argparse
from pathlib import Path
import subprocess
import tempfile
import zipfile


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--libraries", type=Path, required=True)
    parser.add_argument("--candidate", type=Path, required=True)
    parser.add_argument("--java-home", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    libraries = args.libraries.resolve(strict=True)
    candidate = args.candidate.resolve(strict=True)
    output = args.output.absolute()
    if output.exists() or output.is_symlink():
        parser.error("The output must be a new file in the owned verification runtime.")
    runtime_server = libraries / (
        "net/minecraft/server/1.20.1-20230612.114412/"
        "server-1.20.1-20230612.114412-srg.jar"
    )
    runtime_server.resolve(strict=True)
    classpath = ":".join(map(str, [runtime_server, *sorted(libraries.rglob("*.jar")), candidate]))
    source = Path(__file__).with_name("PackagedCompatibilityProbe.java").resolve()
    with tempfile.TemporaryDirectory(prefix="probe-classes-", dir=output.parent) as temporary:
        classes = Path(temporary)
        subprocess.run([
            str(args.java_home / "bin/javac"), "-proc:none", "--release", "17",
            "-cp", classpath, "-d", str(classes), str(source),
        ], check=True)
        with zipfile.ZipFile(output, "x", compression=zipfile.ZIP_DEFLATED) as archive:
            for compiled in sorted(classes.rglob("*.class")):
                archive.write(compiled, compiled.relative_to(classes))
            archive.writestr("META-INF/mods.toml", (
                'modLoader="javafml"\nloaderVersion="[47,)"\nlicense="All Rights Reserved"\n'
                '[[mods]]\nmodId="bfs_packaged_verification"\nversion="1"\n'
                'displayName="Packaged Compatibility Verification"\n'
            ))
    print(output)


if __name__ == "__main__":
    main()
