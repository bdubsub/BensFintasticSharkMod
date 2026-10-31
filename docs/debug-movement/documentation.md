# Swimming speed and tilt tuning

Use these operator commands together when tuning Oceanic Whitetip movement. The values below are an example, not a required preset.

```text
/bfs debug setspeed oceanic_whitetip_shark 4 5
/bfs debug setsprint oceanic_whitetip_shark 1.2 1.6
/bfs debug settilt oceanic_whitetip_shark 45 45 60
```

`setspeed` supplies horizontal and vertical coefficients in blocks per second. `setsprint` multiplies each coefficient during pursuit or escape. A multiplier below 1 slows that axis. For example, horizontal speed 4 with sprint 0.8 requests 3.2, while sprint 1.2 requests 4.8.

`settilt` supplies maximum upward angle, maximum downward angle, and angular speed in degrees per second. The final value already controls how quickly the body tilts. These are session overrides, so reapply them after a server restart. Use the same species identifier for all three commands. An asterisk applies settings to the supported species.

Configured movement uses the body's direction to distribute horizontal and vertical propulsion. The axis coefficients are not a promise that both measured components equal those values simultaneously. Acceleration, turning, collision clearance, and arrival still affect motion. Record all three commands when reporting a preferred configuration.

The pursuit correction removes the artificial tenfold waypoint approach limit and the fixed creep speed during tuned vertical turns. Reaching the requested depth limits the vertical component without braking forward movement. Bite range braking and obstacle clearance remain active. Untuned movement keeps its existing behavior.

Replace the previous BFS JAR on both client and server and keep only one BFS JAR installed. Test in open water first, with prey outside bite range, so obstacles and deliberate contact braking do not obscure the speed comparison. Compare level pursuit, ascent, and descent using the same settings.

The `bfstuning` server GameTest namespace covers these commands, independent axis settings, sustained pitched pursuit, vertical turns, depth transitions, bite braking, and obstacle clearance. The Forge build also runs the existing unit tests. See the [technical reference](../../DOCUMENTATION.md) for the rest of the mod.
