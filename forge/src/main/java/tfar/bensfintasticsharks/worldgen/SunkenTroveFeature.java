package tfar.bensfintasticsharks.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import tfar.bensfintasticsharks.BensFintasticSharks;

/**
 * Underwater "trove" feature.
 *
 * Vanilla underwater structures (ocean_ruin, buried_treasure) use custom Structure
 * subclasses with their own placement logic. A Feature is simpler and equally robust:
 * we scan DOWN from the jigsaw-provided origin to find the actual ocean floor, then
 * build a small dig-site there. This makes the feature immune to jigsaw mis-positioning
 * that placed the trove above water in earlier builds.
 *
 * Layout (~7×3×7 footprint):
 *  - Irregular sand/gravel/sandstone excavation patch
 *  - Half-buried chest in the centre with our loot table
 *  - A broken two-pillar shrine behind the chest, plus waterlogged rubble
 *  - A mostly-buried sea lantern so the site is readable in deep water
 *  - Seagrass and an optional kelp stalk reclaiming the edges
 */
public class SunkenTroveFeature extends Feature<NoneFeatureConfiguration> {

    public static final ResourceLocation LOOT = BensFintasticSharks.id("chests/sunken_trove");

    public SunkenTroveFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();
        RandomSource random = ctx.random();

        // 1. Find the actual ocean floor by scanning DOWN through water until we hit
        // a solid block. Robust to jigsaw mis-positioning that might place the
        // origin above the water surface.
        BlockPos.MutableBlockPos cursor = origin.mutable();
        int minY = level.getMinBuildHeight() + 1;
        // First, descend until we're inside water (skips above-surface bug).
        while (cursor.getY() > minY && !level.getFluidState(cursor).is(FluidTags.WATER)) {
            cursor.move(0, -1, 0);
        }
        // Then descend through water to the seafloor.
        while (cursor.getY() > minY && level.getFluidState(cursor.below()).is(FluidTags.WATER)) {
            cursor.move(0, -1, 0);
        }
        BlockPos floor = cursor.immutable();
        // Bail out if there's no water at the chest level or no solid floor under it.
        if (!level.getFluidState(floor).is(FluidTags.WATER)) return false;
        if (!level.getBlockState(floor.below()).isSolid()) return false;

        // 2. Place the chest first. If worldgen cannot create its block entity, this
        // is not a trove and we should not leave a decorated-but-empty dig site behind.
        BlockPos chestPos = floor;
        Direction face = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState chestState = Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, face)
                .setValue(ChestBlock.WATERLOGGED, true);
        if (!level.setBlock(chestPos, chestState, 2)) return false;
        if (!(level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest)) {
            level.setBlock(chestPos, Blocks.WATER.defaultBlockState(), 2);
            return false;
        }
        chest.setLootTable(LOOT, random.nextLong());

        // 3. Make the site read as an intentional excavation rather than a loose chest.
        // The irregular circular patch keeps the edge organic while the cut sandstone
        // immediately around the chest hints that a small shrine used to stand here.
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                int distSqr = dx * dx + dz * dz;
                if (distSqr > 12 || (distSqr > 8 && random.nextBoolean())) continue;
                BlockPos pos = floor.offset(dx, -1, dz);
                if (level.getBlockState(pos).isSolid()) {
                    boolean centralTile = Math.abs(dx) <= 1 && Math.abs(dz) <= 1;
                    level.setBlock(pos, centralTile
                            ? Blocks.CUT_SANDSTONE.defaultBlockState()
                            : randomFloorBlock(random), 2);
                }
            }
        }

        // 4. Broken shrine: two uneven pillars frame the chest from behind. Their
        // orientation follows the chest so every rotation still has a clear "front".
        Direction right = face.getClockWise();
        Direction back = face.getOpposite();
        BlockPos leftPillar = floor.relative(back, 2).relative(right, -2);
        BlockPos rightPillar = floor.relative(back, 2).relative(right, 2);
        buildBrokenPillar(level, leftPillar, 2 + random.nextInt(2), face);
        buildBrokenPillar(level, rightPillar, 1 + random.nextInt(2), face.getOpposite());

        // Rubble makes the silhouette less boxy. Slabs and stairs remain waterlogged,
        // avoiding the temporary air pockets that used to make underwater features pop.
        for (int i = 0; i < 5 + random.nextInt(3); i++) {
            int dx = random.nextInt(7) - 3;
            int dz = random.nextInt(7) - 3;
            if (Math.abs(dx) <= 1 && Math.abs(dz) <= 1) continue;
            placeRubble(level, floor.offset(dx, 0, dz), Direction.Plane.HORIZONTAL.getRandomDirection(random), random);
        }

        // A recessed light makes the trove discoverable without turning it into a beacon.
        // Only the top face is exposed at seafloor level and the side is randomized.
        BlockPos lightPos = floor.relative(right, random.nextBoolean() ? 2 : -2).relative(back).below();
        if (level.getBlockState(lightPos).isSolid()) {
            level.setBlock(lightPos, Blocks.SEA_LANTERN.defaultBlockState(), 2);
        }

        // 5. Seagrass decoration around the trove.
        for (int i = 0; i < 7 + random.nextInt(4); i++) {
            int dx = random.nextInt(7) - 3;
            int dz = random.nextInt(7) - 3;
            BlockPos pos = floor.offset(dx, 0, dz);
            if (canReplaceWithDecor(level, pos)) {
                level.setBlock(pos, Blocks.SEAGRASS.defaultBlockState(), 2);
            }
        }

        // 6. Optional kelp stalk (50% chance, 2-4 high).
        if (random.nextFloat() < 0.5f) {
            int dx = random.nextInt(3) - 1;
            int dz = random.nextInt(3) - 1;
            BlockPos kelpBase = floor.offset(dx, 0, dz);
            if (canReplaceWithDecor(level, kelpBase)
                    && Blocks.KELP.defaultBlockState().canSurvive(level, kelpBase)) {
                int desiredHeight = 2 + random.nextInt(3);
                int availableHeight = 0;
                while (availableHeight < desiredHeight) {
                    BlockPos kelpPos = kelpBase.above(availableHeight);
                    if (!canReplaceWithDecor(level, kelpPos)
                            || !level.getFluidState(kelpPos).is(FluidTags.WATER)) break;
                    availableHeight++;
                }
                for (int k = 0; k < availableHeight; k++) {
                    BlockPos kelpPos = kelpBase.above(k);
                    BlockState kelpState = k == availableHeight - 1
                            ? Blocks.KELP.defaultBlockState()
                            : Blocks.KELP_PLANT.defaultBlockState();
                    level.setBlock(kelpPos, kelpState, 2);
                }
            }
        }
        return true;
    }

    private static void buildBrokenPillar(WorldGenLevel level, BlockPos base, int height, Direction capFacing) {
        int segmentsPlaced = 0;
        for (int y = 0; y < height; y++) {
            BlockPos pos = base.above(y);
            if (!canReplaceWithDecor(level, pos)) break;
            BlockState state = y == 0
                    ? Blocks.CHISELED_SANDSTONE.defaultBlockState()
                    : Blocks.CUT_SANDSTONE.defaultBlockState();
            if (!level.setBlock(pos, state, 2)) break;
            segmentsPlaced++;
        }
        BlockPos cap = base.above(segmentsPlaced);
        if (segmentsPlaced > 0 && canReplaceWithDecor(level, cap)) {
            level.setBlock(cap, Blocks.SANDSTONE_STAIRS.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, capFacing)
                    .setValue(BlockStateProperties.WATERLOGGED, true), 2);
        }
    }

    private static void placeRubble(WorldGenLevel level, BlockPos pos, Direction facing, RandomSource random) {
        if (!canReplaceWithDecor(level, pos)) return;
        BlockState state = switch (random.nextInt(4)) {
            case 0 -> Blocks.SANDSTONE.defaultBlockState();
            case 1 -> Blocks.CUT_SANDSTONE.defaultBlockState();
            case 2 -> Blocks.SANDSTONE_SLAB.defaultBlockState()
                    .setValue(BlockStateProperties.WATERLOGGED, true);
            default -> Blocks.SANDSTONE_STAIRS.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                    .setValue(BlockStateProperties.WATERLOGGED, true);
        };
        level.setBlock(pos, state, 2);
    }

    private static boolean canReplaceWithDecor(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.CHEST)) return false;
        return state.isAir() || level.getFluidState(pos).is(FluidTags.WATER);
    }

    private static BlockState randomFloorBlock(RandomSource random) {
        return switch (random.nextInt(7)) {
            case 0 -> Blocks.SAND.defaultBlockState();
            case 1 -> Blocks.GRAVEL.defaultBlockState();
            case 2, 3 -> Blocks.SANDSTONE.defaultBlockState();
            case 4 -> Blocks.CUT_SANDSTONE.defaultBlockState();
            default -> Blocks.SAND.defaultBlockState();
        };
    }
}
