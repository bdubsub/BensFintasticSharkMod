package tfar.bensfintasticsharks.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.gametest.GameTestHolder;
import tfar.bensfintasticsharks.follow.BfsFollowManager;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModItems;

/** Server fixtures for the marked follow debug lease. */
@GameTestHolder("bfsfollow")
public final class BfsFollowGameTests {

    private BfsFollowGameTests() {
    }

    @GameTest(template = "empty", batch = "follow_claim", timeoutTicks = 40)
    public static void followStickClaimsAndKeepsMob(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-owner", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(7, 2, 2));
        ItemStack marker = issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled(), "a valid issued marker must claim the mob");
        helper.assertTrue(BfsFollowManager.status(owner).following(), "claimed mob must have an active lease");
        helper.runAfterDelay(1, () -> {
            PlayerInteractEvent.EntityInteract reselect = new PlayerInteractEvent.EntityInteract(
                    owner, InteractionHand.MAIN_HAND, target);
            BfsFollowManager.onEntityInteract(reselect);
            helper.assertTrue(reselect.isCanceled(), "repeated target selection must remain consumed");
            helper.assertTrue(BfsFollowManager.status(owner).following(), "repeated target selection must keep the lease");
            helper.runAfterDelay(1, () -> {
                helper.assertTrue(BfsFollowManager.stop(owner, "test_stop"), "test stop must release the lease");
                helper.assertTrue(!BfsFollowManager.status(owner).following(), "released lease must be absent");
                owner.remove(Entity.RemovalReason.DISCARDED);
                target.remove(Entity.RemovalReason.DISCARDED);
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", batch = "follow_arbitration", timeoutTicks = 40)
    public static void followStickArbitratesOneOwnerPerMob(GameTestHelper helper) {
        ServerPlayer first = makeTestPlayer(helper, "follow-first", new BlockPos(2, 2, 2));
        ServerPlayer second = makeTestPlayer(helper, "follow-second", new BlockPos(3, 2, 2));
        Mob target = helper.spawn(EntityType.VILLAGER, new BlockPos(6, 2, 2));
        issueAndHold(first);
        issueAndHold(second);
        PlayerInteractEvent.EntityInteract firstEvent = new PlayerInteractEvent.EntityInteract(
                first, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(firstEvent);
        PlayerInteractEvent.EntityInteract secondEvent = new PlayerInteractEvent.EntityInteract(
                second, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(secondEvent);
        helper.assertTrue(firstEvent.isCanceled(), "first owner must claim the mob");
        helper.assertTrue(!secondEvent.isCanceled(), "second owner must be rejected");
        helper.assertTrue(BfsFollowManager.status(first).following(), "first owner lease must remain active");
        helper.assertTrue(!BfsFollowManager.status(second).following(), "second owner must have no lease");
        BfsFollowManager.stop(first, "test_stop");
        first.remove(Entity.RemovalReason.DISCARDED);
        second.remove(Entity.RemovalReason.DISCARDED);
        target.remove(Entity.RemovalReason.DISCARDED);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "follow_session_reset", timeoutTicks = 20)
    public static void staleMarkerIsRejectedAfterAuthorizationReset(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-restart", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.PHANTOM, new BlockPos(4, 5, 2));
        issueAndHold(owner);
        owner.getPersistentData().remove("BfsFollowIssueId");
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(!event.isCanceled(), "a marker from a cleared server session must be rejected");
        owner.remove(Entity.RemovalReason.DISCARDED);
        target.remove(Entity.RemovalReason.DISCARDED);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "follow_lifecycle", timeoutTicks = 80)
    public static void followLeaseReleasesWhenTargetDies(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-death", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(7, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled(), "a valid marker must claim the target before its death");
        helper.assertTrue(BfsFollowManager.status(owner).following(), "the target death fixture must begin with a lease");

        target.hurt(helper.getLevel().damageSources().generic(), Float.MAX_VALUE);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(!target.isAlive(), "the lifecycle fixture must actually kill the target");
            helper.assertTrue(!BfsFollowManager.status(owner).following(),
                    "target death must release the follow lease on the next server tick");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_controller_families", timeoutTicks = 80)
    public static void representativeMobFamiliesUseTheGenericLease(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-families", new BlockPos(2, 8, 2));
        Mob[] targets = {
                helper.spawn(EntityType.ZOMBIE, new BlockPos(8, 8, 2)),
                helper.spawn(EntityType.SALMON, new BlockPos(8, 8, 5)),
                helper.spawn(EntityType.PHANTOM, new BlockPos(8, 12, 8)),
                helper.spawn(EntityType.FROG, new BlockPos(8, 8, 11)),
                helper.spawn(EntityType.VILLAGER, new BlockPos(8, 8, 14)),
                helper.spawn(ModEntityTypes.HARBOR_SEAL, new BlockPos(8, 8, 17)),
                helper.spawn(EntityType.ENDER_DRAGON, new BlockPos(8, 16, 20)),
                helper.spawn(EntityType.WITHER, new BlockPos(8, 8, 23)),
        };
        for (Mob target : targets) {
            issueAndHold(owner);
            Entity clicked = target instanceof EnderDragon dragon ? dragon.head : target;
            PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                    owner, InteractionHand.MAIN_HAND, clicked);
            BfsFollowManager.onEntityInteract(event);
            helper.assertTrue(event.isCanceled(), "generic Mob lease must accept " + target.getType());
            helper.assertTrue(BfsFollowManager.status(owner).following(), "lease must be active for " + target.getType());
            if (target == targets[5]) {
                helper.assertTrue(ownsWalkTarget(target, owner),
                        "SmartBrainLib target memory must be owned by the follow lease");
            }
            helper.assertTrue(BfsFollowManager.stop(owner, "family_fixture"), "family lease must stop for " + target.getType());
            if (target == targets[5]) {
                helper.assertTrue(!ownsWalkTarget(target, owner),
                        "SmartBrainLib target memory must be restored after release");
            }
        }
        owner.remove(Entity.RemovalReason.DISCARDED);
        for (Mob target : targets) {
            target.remove(Entity.RemovalReason.DISCARDED);
        }
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "follow_restoration", timeoutTicks = 80)
    public static void smartBrainLeaseRestoresWithinFortyTicks(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-restore", new BlockPos(2, 8, 2));
        Mob target = helper.spawn(ModEntityTypes.HARBOR_SEAL, new BlockPos(8, 8, 8));
        target.setNoGravity(true);
        float healthBefore = target.getHealth();
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled(), "the SmartBrainLib target must be claimed before restoration");
        helper.assertTrue(ownsWalkTarget(target, owner), "the follow lease must own the SmartBrainLib walk target");
        helper.assertTrue(BfsFollowManager.stop(owner, "restoration_fixture"),
                "explicit stop must release the SmartBrainLib lease");
        helper.runAfterDelay(40, () -> {
            helper.assertTrue(!BfsFollowManager.status(owner).following(),
                    "the SmartBrainLib lease must remain released after forty ticks");
            helper.assertTrue(!ownsWalkTarget(target, owner),
                    "ordinary SmartBrainLib scheduling must not retain the follow owner target");
            helper.assertTrue(target.isAlive() && target.getHealth() == healthBefore,
                    "restoration must preserve the target health and entity");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    private static ItemStack issueAndHold(ServerPlayer player) {
        BfsFollowManager.issue(player);
        String issueId = player.getPersistentData().getString("BfsFollowIssueId");
        ItemStack marker = player.getInventory().items.stream()
                .filter(stack -> stack.is(ModItems.FOLLOW_STICK) && stack.hasTag()
                        && issueId.equals(stack.getTag().getString("bfs_follow_issue_id")))
                .findFirst().orElseThrow();
        player.setItemInHand(InteractionHand.MAIN_HAND, marker);
        return marker;
    }

    private static boolean ownsWalkTarget(Mob mob, ServerPlayer owner) {
        WalkTarget walkTarget = mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        return walkTarget != null && walkTarget.getTarget() instanceof EntityTracker tracker
                && tracker.getEntity().getUUID().equals(owner.getUUID());
    }

    private static ServerPlayer makeTestPlayer(GameTestHelper helper, String name, BlockPos localPosition) {
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(java.util.UUID.randomUUID(), name));
        player.setPos(helper.absolutePos(localPosition).getCenter());
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        helper.getLevel().addNewPlayer(player);
        helper.getLevel().getServer().getPlayerList().op(player.getGameProfile());
        helper.getLevel().getServer().getPlayerList().sendPlayerPermissionLevel(player);
        return player;
    }
}
