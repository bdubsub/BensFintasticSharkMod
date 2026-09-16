# Dive equipment

The Dive Suit is registered as four ordinary armor items.

```text
/give @s bensfintasticsharks:dive_helmet
/give @s bensfintasticsharks:dive_chestplate
/give @s bensfintasticsharks:dive_leggings
/give @s bensfintasticsharks:dive_boots
```

The set has no crafting recipe. Equip all four pieces before entering water. In survival or adventure, body water contact enables landlike underwater travel. Horizontal movement uses ordinary player input and collision. Gravity is 0.02 blocks per tick squared, the downward speed is bounded at 0.30 blocks per tick, and a grounded jump uses a 0.24 block per tick impulse. Holding jump does not repeatedly rearm the jump until the key is released.

The suit stores a schema 1 oxygen reserve on the player. A full reserve is 6,000 submerged ticks, or five minutes at 20 ticks per second. The server decrements it once per submerged tick without Water Breathing. While the eyes are in breathable air, it refills by 20 ticks per tick to the 6,000 tick maximum. When the reserve reaches zero, vanilla air is set to zero so normal drowning rules apply. The action bar reports the reserve at bounded intervals and while empty. The server also sends a revisioned dive snapshot to the owning client when the equipment or mode changes and at most once per 20 steady state ticks. Stale or unsupported revisions are ignored on the client.

Creative and spectator players, partial suits, and dry players retain vanilla travel. Removing a piece preserves the stored reserve and does not create a refill. Respawn and player replacement copy the schema and remaining value. A corrupt or out of range value is recovered conservatively as an empty reserve before normal air refill.

For a bounded server check, run the isolated dive GameTests.

```text
./gradlew :forge:GameTestServer --no-daemon -PbfsGameTestNamespaces=bfsdive
```

The tests cover full suit eligibility, gravity, one grounded jump edge, exact 6,000 submerged ticks, air refill, and partial suit retention. A client visual review is still required for worn atlas seams, arm and leg pivots, HUD rendering, resource reload, and reconnect behavior. Keep the owned client inaudible and use the laptop renderer for that gate.
