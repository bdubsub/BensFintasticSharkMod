package tfar.bensfintasticsharks.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.gore.GoreWaterBoundary;
import tfar.bensfintasticsharks.init.ModEntityTypes;

@GameTestHolder("bfspredation")
@PrefixGameTestTemplate(false)
public final class BfsPredationGameTests {

    private BfsPredationGameTests() {
    }

    @GameTest(template = "bfsgametests.empty", batch = "gore_water_boundary", timeoutTicks = 30)
    public static void oversizedBloodParticlesCannotSpawnInShallowWater(GameTestHelper helper) {
        BlockPos shallow = new BlockPos(3, 2, 3);
        helper.setBlock(shallow.below(), Blocks.STONE);
        helper.setBlock(shallow, Blocks.WATER);
        helper.setBlock(shallow.above(), Blocks.AIR);
        BlockPos water = helper.absolutePos(shallow);
        double x = water.getX() + 0.5D;
        double y = water.getY() + 0.5D;
        double z = water.getZ() + 0.5D;

        double rejected = GoreWaterBoundary.submergedCenter(helper.getLevel(), x, y, z, 1.0F);
        helper.assertTrue(Double.isNaN(rejected), "an oversized kill particle must be rejected before rendering");
        double accepted = GoreWaterBoundary.submergedCenter(helper.getLevel(), x, y + 0.5D, z, 0.1F);
        helper.assertTrue(accepted >= water.getY() && accepted < y + 0.5D,
                "a smaller particle above the surface must be lowered into water");
        helper.succeed();
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_predation", timeoutTicks = 30)
    public static void sharkEatenPreyDoesNotDropItsItem(GameTestHelper helper) {
        AbstractSharkEntity<?> shark = helper.spawn(ModEntityTypes.GREAT_WHITE_SHARK, new BlockPos(3, 2, 3));
        shark.setNoAi(true);
        Mob cod = helper.spawn(EntityType.COD, new BlockPos(4, 2, 3));
        cod.setNoAi(true);
        AABB area = new AABB(0, 0, 0, 8, 6, 8).move(helper.absolutePos(BlockPos.ZERO));

        helper.runAfterDelay(2, () -> {
            try {
                cod.hurt(helper.getLevel().damageSources().mobAttack(shark), cod.getHealth() + 1.0F);
                helper.assertTrue(cod.isDeadOrDying(), "the shark must kill the prey before loot is checked");
                helper.assertTrue(helper.getLevel().getEntitiesOfClass(ItemEntity.class, area).isEmpty(),
                        "fish killed by a BFS shark must be fully eaten and leave no item drops");
            } finally {
                shark.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
            }
            helper.succeed();
        });
    }
}
