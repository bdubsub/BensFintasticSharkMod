# Release artifact identities

The current owner-confirmed `bfs-1.0-rc.1.jar` is the Windows startup correction with SHA 256 `ac509f173563582485bb041f08cf75c827200110e2a73fb6b5b214f80906872a`.

Its [source manifest](windows-startup/bfs-1.0-rc.1-source-manifest.txt), [SPDX component inventory](windows-startup/bfs-1.0-rc.1.spdx.json), [SHA 256 checksum](windows-startup/bfs-1.0-rc.1.jar.sha256), and [SHA 512 checksum](windows-startup/bfs-1.0-rc.1.jar.sha512) identify that exact binary and its verified integration into `1.20.1`. See the [startup correction record](../rc1-windows-startup.md) for the cause, regression tests, packaged runtime checks and owner confirmation.

The other files in this directory are historical. In particular, the original RC1 manifest and SPDX identify the replaced `94bfe2c8` artifact. They are preserved to keep earlier phase evidence accurate, not to recommend installing that artifact. A matching filename or embedded version alone does not establish artifact identity.
