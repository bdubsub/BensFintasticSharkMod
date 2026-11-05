# Dive Suit and oxygen

The Dive Suit has four pieces: Dive Helmet, Dive Chestplate, Dive Leggings, and Dive Boots. Wearing all four pieces grants eligible survival and adventure players landlike movement while they are in water. Normal gravity and collision still apply. Space cannot propel a player upward through open water; a normal jump impulse is available only from solid footing, and holding Space does not repeat it. Creative, spectator, partial-suit, and out-of-water travel keep their normal movement. The suit currently has no crafting recipe.

The full suit carries a saved reserve of 6,000 oxygen ticks, equal to five minutes at the default drain rate. The server drains the reserve while the player's eyes are underwater without Water Breathing. When the reserve reaches zero, vanilla drowning applies. The reserve refills by 20 ticks per game tick when the player's eyes are in breathable air. Equipping a partial suit does not reset the reserve.

The color-changing oxygen bar appears only while all four pieces are equipped. It renders above the experience bar and hotbar. Its displayed amount is predicted from the latest server snapshot so the bar moves smoothly between network updates. It disappears when the full suit is removed or the player logs out.

## Operator controls

These server commands require permission level 2:

```text
/bfs debug dive setoxygen <player> <ticks>
/bfs debug dive setdrain <ticks_per_second>
/bfs debug dive status
/bfs debug dive reset
```

`setoxygen` accepts 0–6,000 and changes the selected player's saved reserve. `setdrain` accepts 0–1,200 reserve ticks per second. The default is 20, which consumes one oxygen tick per game tick; zero pauses reserve drain. The drain override applies to the whole server session and resets when the server stops. `status` reports the current drain rate and, when run by a player, that player's reserve. `reset` restores the default drain rate without changing saved player reserves.

## Armor model

The worn model uses the supplied Dive Armor 2.0 128×128 atlas and its matching 128×128 helmet, chest, leg, and boot layer textures. The geometry maps to GeckoLib’s head, body, arm, leg, and boot slot bones so each piece follows the wearer's current humanoid pose. The lower leg meshes are split into leggings and boot segments for their separate equipment slots.
