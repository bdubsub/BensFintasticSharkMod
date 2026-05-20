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
 * Layout (~5×3×5 footprint):
 *  - Sand/gravel/sandstone ring at floor level
 *  - Half-buried chest in the centre with our loot table
 *  - 1-2 sandstone "spoil pile" blocks beside the chest
 *  - 4-6 seagrass clumps inside the dig
 *  - Optional kelp stalk (50% chance, 2-4 high)
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

        // 2. Sand/gravel/sandstone ring at floor level (replaces the floor blocks one below).
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                int dist = Math.abs(dx) + Math.abs(dz);
                if (dist < 2 || dist > 3) continue;
                BlockPos pos = floor.offset(dx, -1, dz);
                if (level.getBlockState(pos).isSolid()) {
                    level.setBlock(pos, randomFloorBlock(random), 2);
                }
            }
        }

        // 3. Chest at the centre, waterlogged.
        BlockPos chestPos = floor;
        Direction face = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        BlockState chestState = Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, face)
                .setValue(ChestBlock.WATERLOGGED, true);
        level.setBlock(chestPos, chestState, 2);
        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
            chest.setLootTable(LOOT, random.nextLong());
        }

        // 4. Spoil pile — one or two sandstone blocks beside the chest at the same
        // level, like the excavation result of digging into the seafloor.
        for (int i = 0; i < 1 + random.nextInt(2); i++) {
            int dx = random.nextInt(3) - 1;
            int dz = random.nextInt(3) - 1;
            if (dx == 0 && dz == 0) continue;
            BlockPos pos = floor.offset(dx, 0, dz);
            if (canReplaceWithDecor(level, pos)) {
                level.setBlock(pos, Blocks.SANDSTONE.defaultBlockState(), 2);
            }
        }

        // 5. Seagrass decoration around the trove.
        for (int i = 0; i < 4 + random.nextInt(3); i++) {
            int dx = random.nextInt(5) - 2;
            int dz = random.nextInt(5) - 2;
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
            if (canReplaceWithDecor(level, kelpBase) && level.getFluidState(kelpBase.above()).is(FluidTags.WATER)) {
                int kelpHeight = 2 + random.nextInt(3);
                for (int k = 0; k < kelpHeight; k++) {
                    BlockPos kelpPos = kelpBase.above(k);
                    if (!level.getFluidState(kelpPos).is(FluidTags.WATER)) break;
                    level.setBlock(kelpPos, Blocks.KELP_PLANT.defaultBlockState(), 2);
                }
            }
        }
        return true;
    }

    private static boolean canReplaceWithDecor(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.CHEST)) return false;
        return state.isAir() || level.getFluidState(pos).is(FluidTags.WATER);
    }

    private static BlockState randomFloorBlock(RandomSource random) {
        return switch (random.nextInt(4)) {
            case 0 -> Blocks.SAND.defaultBlockState();
            case 1 -> Blocks.GRAVEL.defaultBlockState();
            case 2 -> Blocks.SANDSTONE.defaultBlockState();
            default -> Blocks.SAND.defaultBlockState();
        };
    }
}
