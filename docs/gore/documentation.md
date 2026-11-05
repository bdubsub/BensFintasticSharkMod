# Gore effects and controls

Gore adds short-lived blood effects when a BFS shark damages a living target. Every hit creates an animated blood mist, and underwater hits briefly tint the nearby player's underwater view red. When the local player is bitten, a fading red vignette appears around the screen edges. Nonfatal hits may leave a temporary wound mark attached to the target. Fatal hits create a wider, slower-fading spray and may leave temporary visual remains. These effects do not alter damage, prey loot, entity persistence, or server gameplay.

The event changes the amount of blood: a normal bite makes a small effect, thrash damage makes a heavier one, release makes a larger burst, and a kill sprays blood through a wider area. With the default tuning these tiers use 2, 6, 16, and 24 splatter particles, respectively; the animated mist count follows the same tiers and spreads farther for releases and kills. Fatal effects fade over 96 ticks by default. Water hits make the mist denser and activate the temporary red underwater tint. A wound mark appears at the hit point after its splash fades, avoiding stacked sprite layers. Fatal remains use a spaced layout.

`particle_count` is the base count used to size all four tiers. Zero suppresses splatter, droplets, and mist. `particle_scale` and `particle_lifetime` also scale each tier, while the wound and remains settings continue to tune those effects independently.

Each client controls whether it sees these effects. Gore is enabled by default and saved in the client's `bfs-client.toml` configuration.

```text
/bfs gore on
/bfs gore off
/bfs gore status
```

These are client commands, require no server operator permission, and change only the local player's display. Turning gore off removes currently visible gore particles as they tick.

## Operator tuning

Operators can inspect and change temporary visual parameters during the current server session:

```text
/bfs debug gore get
/bfs debug gore set <field> <value>
/bfs debug gore reset
```

Counts and lifetimes require whole numbers. A rejected value leaves all settings unchanged. Settings reset to their defaults when the server stops.

| Field | Default | Allowed range |
|---|---:|---:|
| `particle_count` | 8 | 0–64 |
| `particle_scale` | 0.8 | 0.1–4.0 |
| `particle_lifetime` | 24 ticks | 1–1,200 ticks |
| `wound_scale` | 0.75 | 0.1–4.0 |
| `wound_lifetime` | 80 ticks | 1–1,200 ticks |
| `remains_count` | 2 | 0–32 |
| `remains_scale` | 1.0 | 0.1–4.0 |
| `remains_lifetime` | 120 ticks | 1–6,000 ticks |

The server sends these visual parameters with nearby hit cues. Each receiving client then applies its own saved gore preference. Server tuning never enables effects for a client that has turned them off.

## Pixel textures

The 24 supplied blood-mist frames are preserved as separate 16×16 RGBA textures and animated by the particle sprite set. Existing splatter, droplet, wound, and tissue textures remain centered on 16×16 canvases with square texels:

| Texture | Opaque source bounds | Opaque texels |
|---|---:|---:|
| `blood_drop.png` | 2×2 | 3 |
| `blood_wound.png` | 4×4 | 11 |
| `blood_splatter.png` | 7×5 | 22 |
| `blood_splatter_dark.png` | 7×5 | 22 |
| `blood_tissue.png` | 7×5 | 22 |

The tissue texture is used for remains. Pixel-grid regression checks still protect the extracted static sprites against stretched texels, new shades, and checkerboard pixels.
