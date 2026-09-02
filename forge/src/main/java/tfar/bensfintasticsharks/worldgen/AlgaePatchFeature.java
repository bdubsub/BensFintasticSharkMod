package tfar.bensfintasticsharks.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/** Places one algae unit on the first valid submerged floor near a feature candidate. */
public final class AlgaePatchFeature extends Feature<NoneFeatureConfiguration> {

    private static final int SEARCH_DOWN = 16;
    private static final int SEARCH_UP = 4;
    private final Block algae;

    public AlgaePatchFeature(Codec<NoneFeatureConfiguration> codec, Block algae) {
        super(codec);
        this.algae = algae;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        for (int offset = 0; offset <= SEARCH_DOWN; offset++) {
            if (placeAt(level, origin.below(offset))) return true;
        }
        for (int offset = 1; offset <= SEARCH_UP; offset++) {
            if (placeAt(level, origin.above(offset))) return true;
        }
        return false;
    }

    private boolean placeAt(WorldGenLevel level, BlockPos pos) {
        if (!level.getFluidState(pos).is(FluidTags.WATER)
                || level.getFluidState(pos).getAmount() != 8) {
            return false;
        }

        BlockState state = algae.defaultBlockState();
        if (!state.canSurvive(level, pos) || !state.getCollisionShape(level, pos).isEmpty()) {
            return false;
        }
        return level.setBlock(pos, state, 2);
    }
}
