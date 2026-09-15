package tfar.bensfintasticsharks.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.gametest.GameTestHolder;
import tfar.bensfintasticsharks.follow.BfsFollowManager;
import tfar.bensfintasticsharks.init.ModItems;

import java.util.UUID;

/** Server fixture for one pinned non BFS controller artifact. */
@GameTestHolder("bfsfollowthirdparty")
public final class BfsFollowThirdPartyGameTests {

    private BfsFollowThirdPartyGameTests() {
    }

    @GameTest(template = "empty", batch = "follow_third_party", timeoutTicks = 120)
    public static void pinnedAlexsMobsGrizzlyUsesGenericLease(GameTestHelper helper) {
        for (int x = 0; x <= 12; x++) {
            for (int z = 0; z <= 4; z++) {
                helper.setBlock(new BlockPos(x, 1, z), Blocks.STONE.defaultBlockState());
            }
        }
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(
                new ResourceLocation("alexsmobs", "grizzly_bear")).orElseThrow(
                () -> new AssertionError("the pinned alex's mobs artifact did not register grizzly_bear"));
        Entity entity = type.create(helper.getLevel());
        helper.assertTrue(entity instanceof Mob, "the external fixture must be a living mob");
        Mob target = (Mob) entity;
        target.moveTo(helper.absolutePos(new BlockPos(8, 2, 2)).getCenter());
        helper.getLevel().addFreshEntity(target);

        ServerPlayer owner = makeTestPlayer(helper, "follow-alexs-mobs", new BlockPos(2, 2, 2));
        double initialDistance = owner.distanceTo(target);
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled(), "the pinned external mob must accept the generic lease");
        helper.assertTrue(BfsFollowManager.status(owner).following(), "the external lease must be active");
        helper.runAfterDelay(60, () -> {
            helper.assertTrue(target.isAlive(), "the external follow target must remain alive");
            helper.assertTrue(owner.distanceTo(target) < initialDistance - 0.5D,
                    "the external mob must navigate toward the owner, initial " + initialDistance
                            + ", final " + owner.distanceTo(target));
            helper.assertTrue(BfsFollowManager.stop(owner, "third_party_fixture"),
                    "the external lease must release cleanly");
            helper.assertTrue(!BfsFollowManager.status(owner).following(),
                    "the external lease must be absent after release");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    private static void issueAndHold(ServerPlayer player) {
        BfsFollowManager.issue(player);
        String issueId = player.getPersistentData().getString("BfsFollowIssueId");
        ItemStack marker = player.getInventory().items.stream()
                .filter(stack -> stack.is(ModItems.FOLLOW_STICK) && stack.hasTag()
                        && issueId.equals(stack.getTag().getString("bfs_follow_issue_id")))
                .findFirst().orElseThrow();
        player.setItemInHand(InteractionHand.MAIN_HAND, marker);
    }

    private static ServerPlayer makeTestPlayer(GameTestHelper helper, String name, BlockPos localPosition) {
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(UUID.randomUUID(), name));
        player.setPos(helper.absolutePos(localPosition).getCenter());
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        helper.getLevel().addNewPlayer(player);
        helper.getLevel().getServer().getPlayerList().op(player.getGameProfile());
        helper.getLevel().getServer().getPlayerList().sendPlayerPermissionLevel(player);
        return player;
    }
}
