package tfar.bensfintasticsharks.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.server.level.ServerLevel;
import tfar.bensfintasticsharks.debug.BfsDebugManager;
import tfar.bensfintasticsharks.init.ModBlocks;

/** Places bounded, water preserving algae patches on exposed ocean floors. */
public final class AlgaePatchFeature extends Feature<NoneFeatureConfiguration> {

    private static final int PATCH_ATTEMPTS = 2;
    private static final int CANDIDATE_ATTEMPTS = 16;
    private static final int SEARCH_DOWN = 16;
    private static final int MAX_COLUMN_HEIGHT = 8;
    private final Block algae;

    public AlgaePatchFeature(Codec<NoneFeatureConfiguration> codec, Block algae) {
        super(codec);
        this.algae = algae;
    }

    public static boolean isUnoccupiedSourceWater(BlockState state) {
        return state.is(Blocks.WATER);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        boolean placed = false;
        for (int patch = 0; patch < PATCH_ATTEMPTS; patch++) {
            for (int attempt = 0; attempt < CANDIDATE_ATTEMPTS; attempt++) {
                int x = origin.getX() + random.nextInt(17) - 8;
                int z = origin.getZ() + random.nextInt(17) - 8;
                BlockPos candidate = new BlockPos(x, origin.getY(), z);
                BlockPos floor = findExposedFloor(level, candidate);
                if (floor == null) {
                    recordGeneration(level, candidate, "floor_rejected", attempt, 1, 0,
                            false, level.canSeeSky(candidate));
                    continue;
                }
                if (!placeCandidate(level, floor, random, attempt)) {
                    continue;
                }
                placed = true;
                break;
            }
        }
        return placed;
    }

    private BlockPos findExposedFloor(WorldGenLevel level, BlockPos start) {
        for (int offset = 0; offset <= SEARCH_DOWN; offset++) {
            BlockPos water = start.below(offset);
            BlockPos floor = water.below();
            if (isSourceWater(level, water)
                    && !level.getFluidState(floor).is(FluidTags.WATER)
                    && Block.canSupportCenter(level, floor, Direction.UP)) {
                return floor;
            }
        }
        return null;
    }

    private boolean placeCandidate(WorldGenLevel level, BlockPos floor, RandomSource random, int attempt) {
        BlockPos first = floor.above();
        if (!isSourceWater(level, first)) {
            recordGeneration(level, first, "source_water_rejected", attempt, 0, 0, false, false);
            return false;
        }
        if (algae == ModBlocks.ALGAE_BLOCK) {
            BlockState state = algae.defaultBlockState()
                    .setValue(BlockStateProperties.DOWN, true)
                    .setValue(BlockStateProperties.WATERLOGGED, true);
            boolean placed = level.setBlock(first, state, Block.UPDATE_ALL);
            recordGeneration(level, first, placed ? "small_placed" : "small_rejected",
                    attempt, 1, placed ? 1 : 0, true, level.canSeeSky(first));
            return placed;
        }

        int available = 0;
        BlockPos cursor = first;
        while (available < MAX_COLUMN_HEIGHT && isSourceWater(level, cursor)) {
            available++;
            cursor = cursor.above();
        }
        if (available < 2) {
            recordGeneration(level, first, "height_rejected", attempt, 1, 0, true, level.canSeeSky(first));
            return false;
        }
        int height = 2 + random.nextInt(Math.min(MAX_COLUMN_HEIGHT, available) - 1);
        boolean surfaceVisible = hasOpenWaterSurface(level, first);
        if (algae == ModBlocks.LARGE_RED_ALGAE && !surfaceVisible) {
            recordGeneration(level, first, "red_surface_rejected", attempt, 1, 0, true, false);
            return false;
        }
        ((ModBlocks.LargeAlgaeBlock) algae).placeNaturalColumn(level, first, height);
        recordGeneration(level, first, "column_placed", attempt, 1, height, true, surfaceVisible);
        return true;
    }

    /** Returns whether a source water column has an unobstructed, sky-visible surface. */
    public static boolean hasOpenWaterSurface(WorldGenLevel level, BlockPos first) {
        BlockPos cursor = first;
        int limit = level.getMaxBuildHeight() - first.getY();
        for (int offset = 0; offset < limit && isSourceWater(level, cursor); offset++) {
            cursor = cursor.above();
        }
        if (cursor.getY() >= level.getMaxBuildHeight() || !level.getFluidState(cursor).isEmpty()) {
            return false;
        }
        for (int y = cursor.getY(); y < level.getMaxBuildHeight(); y++) {
            if (!level.getBlockState(new BlockPos(cursor.getX(), y, cursor.getZ())).isAir()) {
                return false;
            }
        }
        return true;
    }

    private static void recordGeneration(WorldGenLevel level, BlockPos pos, String reason,
                                         int attempt, int candidateAttempts, int placedCells,
                                         boolean sourceWater, boolean surfaceVisible) {
        if (level instanceof ServerLevel server) {
            BfsDebugManager.recordAlgaeGeneration(server, pos, reason, attempt, candidateAttempts,
                    placedCells, sourceWater, surfaceVisible, server.getBlockState(pos));
        }
    }

    private static boolean isSourceWater(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.WATER)
                && level.getFluidState(pos).is(FluidTags.WATER)
                && level.getFluidState(pos).isSource();
    }
}
