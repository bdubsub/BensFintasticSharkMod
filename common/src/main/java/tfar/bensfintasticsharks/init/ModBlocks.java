package tfar.bensfintasticsharks.init;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

/** Blocks added by the mod. The three algae forms intentionally share vanilla seagrass state rules. */
public final class ModBlocks {

    public static final Block ALGAE_BLOCK = new AlgaeBlock();
    public static final Block LARGE_GREEN_ALGAE = new AlgaeBlock();
    public static final Block LARGE_RED_ALGAE = new AlgaeBlock();

    private ModBlocks() {
    }

    private static final class AlgaeBlock extends SeagrassBlock {
        private AlgaeBlock() {
            super(BlockBehaviour.Properties.copy(Blocks.SEAGRASS));
        }

        @Override
        public boolean isValidBonemealTarget(net.minecraft.world.level.LevelReader level,
                                             net.minecraft.core.BlockPos pos,
                                             net.minecraft.world.level.block.state.BlockState state,
                                             boolean isClient) {
            return false;
        }

        @Override
        public boolean isBonemealSuccess(net.minecraft.world.level.Level level,
                                         net.minecraft.util.RandomSource random,
                                         net.minecraft.core.BlockPos pos,
                                         net.minecraft.world.level.block.state.BlockState state) {
            return false;
        }
    }
}
