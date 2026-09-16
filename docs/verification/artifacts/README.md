# Release artifact identities

The current debug and content testing candidate is identified by the [Phase 007 manifest](../phase-007/candidate-manifest.json) and [server verification](../phase-007/final-server-verification-20260916.md). Its SHA 256 is `4a8714033063853cc49f33fdb1b221293032416615ce286669025531cfc13606`. It retains version `1.0-rc.1`, so use the checksum to distinguish it from the earlier owner confirmed startup correction below. It is a testing JAR with performance and laptop verification deferred, not a public release.

The current owner-confirmed `bfs-1.0-rc.1.jar` is the Windows startup correction with SHA 256 `ac509f173563582485bb041f08cf75c827200110e2a73fb6b5b214f80906872a`.

Its [source manifest](windows-startup/bfs-1.0-rc.1-source-manifest.txt), [SPDX component inventory](windows-startup/bfs-1.0-rc.1.spdx.json), [SHA 256 checksum](windows-startup/bfs-1.0-rc.1.jar.sha256), and [SHA 512 checksum](windows-startup/bfs-1.0-rc.1.jar.sha512) identify that exact binary and its verified integration into `1.20.1`. See the [startup correction record](../rc1-windows-startup.md) for the cause, regression tests, packaged runtime checks and owner confirmation.

The other files in this directory are historical. In particular, the original RC1 manifest and SPDX identify the replaced `94bfe2c8` artifact. They are preserved to keep earlier phase evidence accurate, not to recommend installing that artifact. A matching filename or embedded version alone does not establish artifact identity.
