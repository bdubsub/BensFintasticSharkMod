package tfar.bensfintasticsharks.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.init.ModEntityTypes;

@GameTestHolder("bfspredation")
@PrefixGameTestTemplate(false)
public final class BfsPredationGameTests {

    private BfsPredationGameTests() {
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
