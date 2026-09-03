package tfar.bensfintasticsharks.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import tfar.bensfintasticsharks.entity.TigerSharkEntity;
import tfar.bensfintasticsharks.init.ModBlocks;
import tfar.bensfintasticsharks.init.ModEntityTypes;

/** Server safe smoke fixtures for the shared Phase 000 harness. */
@GameTestHolder("bensfintasticsharks")
public final class BfsGameTests {

    private static final BlockPos ALGAE_POS = new BlockPos(1, 1, 1);
    private static final BlockPos SUPPORT_POS = new BlockPos(1, 0, 1);

    private BfsGameTests() {
    }

    @GameTest(template = "empty", batch = "bfs_baseline", timeoutTicks = 20)
    public static void algaePlacementPreservesWaterAndHasNoCollision(GameTestHelper helper) {
        prepareWaterColumn(helper);
        helper.setBlock(ALGAE_POS, ModBlocks.ALGAE_BLOCK.defaultBlockState());

        helper.assertBlockPresent(ModBlocks.ALGAE_BLOCK, ALGAE_POS);
        BlockPos absoluteAlgaePos = helper.absolutePos(ALGAE_POS);
        helper.assertTrue(helper.getLevel().getFluidState(absoluteAlgaePos).is(FluidTags.WATER),
                "algae placement must retain a water fluid state");
        helper.assertTrue(ModBlocks.ALGAE_BLOCK.defaultBlockState().getCollisionShape(helper.getLevel(), absoluteAlgaePos).isEmpty(),
                "algae must not create a collision barrier");
        helper.assertTrue(ModBlocks.ALGAE_BLOCK.defaultBlockState().canSurvive(helper.getLevel(), absoluteAlgaePos),
                "algae must survive while its water support is present");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "bfs_baseline", timeoutTicks = 20)
    public static void algaeRemovalRestoresWater(GameTestHelper helper) {
        prepareWaterColumn(helper);
        helper.setBlock(ALGAE_POS, ModBlocks.LARGE_GREEN_ALGAE.defaultBlockState());
        helper.runAfterDelay(1, () -> {
            helper.setBlock(ALGAE_POS, Blocks.WATER.defaultBlockState());
            helper.assertBlockPresent(Blocks.WATER, ALGAE_POS);
            helper.assertTrue(helper.getLevel().getFluidState(helper.absolutePos(ALGAE_POS)).is(FluidTags.WATER),
                    "removing algae must leave water in the source position");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_baseline", timeoutTicks = 220)
    public static void tigerSharkPursuesReachableEdibleItem(GameTestHelper helper) {
        prepareWaterVolume(helper);
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        Vec3 start = shark.position();
        double startDistance = shark.distanceToSqr(item);

        helper.runAfterDelay(40, () -> {
            helper.assertTrue(shark.getSharkState() == TigerSharkEntity.SharkState.CURIOUS
                            || shark.justBitItem(),
                    "tiger shark must acquire a reachable edible item");
            helper.assertTrue(shark.position().distanceToSqr(start) > 0.25,
                    "tiger shark must leave its spawn position while pursuing an item");
            helper.assertTrue(shark.distanceToSqr(item) < startDistance,
                    "tiger shark must reduce distance to a reachable edible item");
            helper.succeed();
        });
    }

    private static void prepareWaterVolume(GameTestHelper helper) {
        for (int x = 1; x <= 10; x++) {
            for (int z = 1; z <= 10; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.SAND.defaultBlockState());
                for (int y = 1; y <= 5; y++) {
                    helper.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState());
                }
            }
        }
    }

    private static void prepareWaterColumn(GameTestHelper helper) {
        helper.setBlock(SUPPORT_POS, Blocks.SAND.defaultBlockState());
        helper.setBlock(ALGAE_POS, Blocks.WATER.defaultBlockState());
    }
}
