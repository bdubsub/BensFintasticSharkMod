# Algae and Bounded World Generation

The mod keeps the existing algae registry identities while giving each form a water preserving lifecycle.

## Small algae

Small Algae uses the existing `bensfintasticsharks:algae_block` identity with multiface state. A placement attaches to a supported north, south, east, west, or down face and keeps the cell waterlogged. The up face is not accepted. Removing one support preserves every other supported face. Removing the final face restores source water. The block has no collision shape, and an ordinary break produces one algae item for the removed cell.

## Large algae columns

Large Green Algae and Large Red Algae keep their existing public block and item identities. Each cell stores `single`, `body`, or `top`, an age from zero through 25, and a waterlogged flag. A topmost single or top segment can extend into a source water cell. A successful singleton extension turns the old cell into body and creates a top segment. Total height is capped at eight cells. Manual stacking and bonemeal extend one cell when source water and height remain available. Age 25 stops random ticking only, so manual stacking and bonemeal still work at age 25. Breaking a segment restores water in removed cells, removes invalid upper cells, and uses the ordinary loot context for one item per removed cell.

## Natural generation

Natural algae generation keeps the existing ocean biome inputs and uses two bounded patch attempts per opportunity. Each patch tries at most sixteen candidate positions. A candidate needs an exposed solid ocean floor and a source water column. Large columns are two through eight cells tall and fit the available water depth. Red algae additionally needs a continuous open surface path, so a roofed underwater cave is rejected. Generation does not repopulate existing chunks.

## Diagnostics

Start `/bfs debug on algae <ticks>` before reproducing a placement, support update, save load, growth, harvest, or generation case. The capture records the authoritative server decision and the final state. Use the analyzer documented in [BFS Debug Diagnostics](debug-diagnostics.md) to validate the JSONL footer, event fields, bounds, and additive schema minor version. A capture with dropped records or an incomplete terminal record is not evidence of a passing fixture.

The supplied green and red animation metadata remains unchanged. Green retains ten discrete frames and red retains nine, both at four ticks per frame without interpolation.
