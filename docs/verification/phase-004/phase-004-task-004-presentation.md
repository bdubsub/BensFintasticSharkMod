# Phase 004 task 004 presentation evidence

Date: September 11, 2026

The supplied presentation inputs remain byte-identical on the Phase 004
candidate. The green strip is ten authored frames at four ticks each and the
red strip is nine authored frames at four ticks each. Both metadata files keep
`interpolate: false`. The block form uses the supplied 16 by 16 patch texture,
while the two tall forms use their separate animated strips and cutout models.

The exact candidate loaded all three forms on EnVy's Linux laptop through the
packaged dedicated server. The clean capture
`/tmp/bfsm-p004-final-client-clean.png` has SHA 256
`d660dfe719bbd052d51adc0121ec9506951130b555ebb076a7bf550d94b1503f` and was
captured without switching away from the active workspace. The client used the
NVIDIA GeForce RTX 5090 Laptop GPU, Java 17, and an audio-muted disposable
Prism profile. The client log showed no algae model, blockstate, atlas, texture,
or render-layer warning. EnVy approved the exhibit and continuation.

The matching command placed exhibit control captured ten green and nine red
frames at approximately four game tick cadence. The frame hashes and crop
change checks are in `phase-004-task-004-loop-manifest.json`. The red capture
also contains the authored repeated hold frames. This proves the patch and
both animated forms change through the client boundary without an atlas or
render warning.

The current rebuilt candidate is `525b4b75cc139758a3721e859588940528cf00076a9132d1738b3bd85b9f3ce4`.
It was captured at naturally generated coordinates `(104, 47, 187)` for green
and `(106, 18, 104)` for red through the exact packaged server at
`100.76.164.109:25870`. The client window stable id was `18000d91`; the
Hyprland workspace was 2 before and after every capture. The full frame and
cropped frame hashes are recorded in the current candidate section of
`phase-004-task-004-loop-manifest.json`. The current captures show both forms
changing through their authored frame sequences, including the red repeated
holds, without an atlas or render warning.

The current laptop visual gate is awaiting EnVy approval for this exact
rebuilt candidate. The earlier approval remains valid only for the superseded
exhibit hash and is retained as provenance.
