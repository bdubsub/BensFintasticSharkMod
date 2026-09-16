# phase 007 task 003 performance repair

The follow feedback repair was already committed in `822cdbc046fcc87cc607680d2c3a212f2353f579`. The current phase branch then received two behavior preserving performance repairs in `62d1bda860800451c307928ad87d2542b1f726fe`, which removes unchanged living water state writes and precomputes effective species settings for the hot read path. The affected first party GameTests passed 145 of 145 required tests after those changes. Compile, unit tests and the Forge build also passed.

The complete installed Forge candidate pair was run with seed `240024`, all 22 species, actual predation, ordinary replacement, 2,400 warmup ticks and 36,000 measured ticks at targets 63 and 126. The capture artifact was the source equivalent candidate jar with SHA 256 `7823d3b8ae00ad65e928efb2baaf7a14fc70b5ee8bccc129bbc58968880ed8ba`. The subsequent reproducible Forge build produced SHA 256 `bc193e5127544cf00580378b5591f8457e2a1286a32e873572814ed24e7747d7`; because the jar bytes differ, the capture pair is retained as diagnostic evidence and does not bind the rebuilt delivery artifact.

| Case | Baseline p95 | Candidate p95 | Change | Result |
|---|---:|---:|---:|---|
| Ordinary | 6.666408 ms | 8.178976 ms | 22.689400 percent | blocked |
| Doubled | 8.467315 ms | 12.054813 ms | 42.368779 percent | blocked |

The ordinary candidate also exceeded the absolute 7.639247 ms ceiling. At capture time, unrelated long running Java servers owned by `pterodactyl` consumed the host CPU. The required matching environment was therefore unavailable, and the phase contract records `ENVIRONMENT_UNAVAILABLE` rather than treating either comparison as passed. The raw captures and manifests were complete, emitted 36,000 measured ticks, held the 63 and 126 entity targets, and stopped their owned servers with exit code zero. No client or player joined the performance fixture.

The performance gate remains open. A final candidate jar and both baseline and candidate pairs must be rerun on a matching uncontended host before phase completion. The follow behavior and feedback tests remain passed independently, including unlimited independent membership, targeted release, arrival retention and chat plus action bar receipts.
