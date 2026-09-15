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
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.gametest.GameTestHolder;
import tfar.bensfintasticsharks.follow.BfsFollowManager;
import tfar.bensfintasticsharks.entity.PitchSwimmingNavigation;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModItems;

/** Server fixtures for the marked follow debug lease. */
@GameTestHolder("bfsfollow")
public final class BfsFollowGameTests {

    private BfsFollowGameTests() {
    }

    @GameTest(template = "empty", batch = "follow_claim", timeoutTicks = 20)
    public static void issuingFollowStickSelectsFreshMarker(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-issue", new BlockPos(2, 2, 2));
        BfsFollowManager.issue(owner);
        helper.assertTrue(owner.getMainHandItem().is(ModItems.FOLLOW_STICK),
                "issuing the follow command must select the fresh marker in the main hand");
        helper.assertTrue(owner.getMainHandItem().hasTag(),
                "the selected follow marker must retain its authorization tag");
        owner.remove(Entity.RemovalReason.DISCARDED);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "follow_claim", timeoutTicks = 20)
    public static void issuingFollowStickPreservesHeldItem(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-issue-held", new BlockPos(2, 2, 2));
        owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND));
        BfsFollowManager.issue(owner);
        helper.assertTrue(owner.getMainHandItem().is(ModItems.FOLLOW_STICK),
                "the fresh marker must become the selected item when the main hand was occupied");
        helper.assertTrue(owner.getInventory().items.stream().anyMatch(stack -> stack.is(Items.DIAMOND)),
                "issuing the follow command must preserve the previously held item");
        owner.remove(Entity.RemovalReason.DISCARDED);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "follow_claim", timeoutTicks = 20)
    public static void unprivilegedIssuedMarkerCannotClaimMob(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-unprivileged", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.COW, new BlockPos(5, 2, 2));
        issueAndHold(owner);
        helper.getLevel().getServer().getPlayerList().deop(owner.getGameProfile());
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(!BfsFollowManager.status(owner).permitted(),
                "a deopped owner must report that the follow permission is unavailable");
        helper.assertTrue(!event.isCanceled() && !BfsFollowManager.status(owner).following(),
                "an issued follow marker must not claim a mob after operator permission is lost");
        owner.remove(Entity.RemovalReason.DISCARDED);
        target.remove(Entity.RemovalReason.DISCARDED);
        helper.succeed();
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

    @GameTest(template = "empty", batch = "follow_claim", timeoutTicks = 100)
    public static void followClickRangeArmsWhenOwnerMoves(GameTestHelper helper) {
        for (int x = 0; x <= 14; x++) {
            for (int z = 0; z <= 4; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
            }
            for (int y = 1; y <= 2; y++) {
                helper.setBlock(new BlockPos(x, y, 0), Blocks.STONE.defaultBlockState());
                helper.setBlock(new BlockPos(x, y, 4), Blocks.STONE.defaultBlockState());
            }
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-click-range", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.COW, new BlockPos(5, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "a real reach distance click must keep the follow lease active");
        owner.setPos(helper.absolutePos(new BlockPos(11, 2, 2)).getCenter());
        double movedOwnerDistance = owner.distanceTo(target);
        helper.runAfterDelay(40, () -> {
            helper.assertTrue(target.isAlive(), "the close click target must remain alive");
            helper.assertTrue(owner.distanceTo(target) < movedOwnerDistance - 0.5D,
                    "the close click target must follow after the owner moves, initial "
                            + movedOwnerDistance + ", final " + owner.distanceTo(target));
            BfsFollowManager.stop(owner, "click_range_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_claim", timeoutTicks = 60)
    public static void followEventBusClaimsMarkedMob(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-event-bus", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(9, 2, 2));
        double initialDistance = owner.distanceTo(target);
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        MinecraftForge.EVENT_BUS.post(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "the registered Forge interaction handler must claim a marked mob");
        helper.runAfterDelay(20, () -> {
            helper.assertTrue(owner.distanceTo(target) < initialDistance,
                    "the event bus claim must produce movement toward the owner");
            BfsFollowManager.stop(owner, "event_bus_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_claim", timeoutTicks = 40)
    public static void heldClickDoesNotReclaimAfterArrival(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-arrival", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(4, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "the close target must begin with an active lease");
        helper.assertTrue(BfsFollowManager.stop(owner, "arrived"),
                "the explicit arrival release must succeed");
        PlayerInteractEvent.EntityInteract heldClick = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(heldClick);
        helper.assertTrue(!heldClick.isCanceled() && !BfsFollowManager.status(owner).following(),
                "a held click must not immediately reclaim an arrived target");
        helper.runAfterDelay(25, () -> {
            PlayerInteractEvent.EntityInteract repeatedHeldClick = new PlayerInteractEvent.EntityInteract(
                    owner, InteractionHand.MAIN_HAND, target);
            BfsFollowManager.onEntityInteract(repeatedHeldClick);
            helper.assertTrue(!repeatedHeldClick.isCanceled() && !BfsFollowManager.status(owner).following(),
                    "a held click must stay latched while the target remains at arrival");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_navigation", timeoutTicks = 100)
    public static void followCowNavigatesToOwner(GameTestHelper helper) {
        for (int x = 0; x <= 12; x++) {
            for (int z = 0; z <= 4; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
            }
            for (int y = 1; y <= 2; y++) {
                helper.setBlock(new BlockPos(x, y, 0), Blocks.STONE.defaultBlockState());
                helper.setBlock(new BlockPos(x, y, 4), Blocks.STONE.defaultBlockState());
            }
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-cow", new BlockPos(2, 1, 2));
        Mob target = helper.spawn(EntityType.COW, new BlockPos(9, 1, 2));
        double initialDistance = owner.distanceTo(target);
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "a normal cow must accept the generic follow lease");
        helper.runAfterDelay(60, () -> {
            helper.assertTrue(target.isAlive(), "the cow follow target must remain alive");
            helper.assertTrue(owner.distanceTo(target) < initialDistance - 0.5D,
                    "the cow must navigate toward the owner, initial " + initialDistance
                            + ", final " + owner.distanceTo(target));
            BfsFollowManager.stop(owner, "cow_navigation_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_navigation", timeoutTicks = 120)
    public static void followSlimeNavigatesToOwner(GameTestHelper helper) {
        for (int x = 0; x <= 12; x++) {
            for (int z = 0; z <= 4; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
            }
            for (int y = 1; y <= 2; y++) {
                helper.setBlock(new BlockPos(x, y, 0), Blocks.STONE.defaultBlockState());
                helper.setBlock(new BlockPos(x, y, 4), Blocks.STONE.defaultBlockState());
            }
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-slime", new BlockPos(2, 1, 2));
        Mob target = helper.spawn(EntityType.SLIME, new BlockPos(9, 1, 2));
        double initialDistance = owner.distanceTo(target);
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "a slime must accept the generic follow lease");
        helper.runAfterDelay(90, () -> {
            helper.assertTrue(target.isAlive(), "the slime follow target must remain alive");
            helper.assertTrue(owner.distanceTo(target) < initialDistance - 0.5D,
                    "a slime must navigate toward the owner, initial " + initialDistance
                            + ", final " + owner.distanceTo(target));
            BfsFollowManager.stop(owner, "slime_navigation_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_navigation", timeoutTicks = 120)
    public static void followSmartBrainAnimalNavigatesToOwner(GameTestHelper helper) {
        for (int x = 0; x <= 12; x++) {
            for (int y = 1; y <= 5; y++) {
                for (int z = 0; z <= 6; z++) {
                    helper.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState());
                }
            }
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-smartbrain", new BlockPos(2, 3, 3));
        Mob target = helper.spawn(ModEntityTypes.HARBOR_SEAL, new BlockPos(9, 3, 3));
        double initialDistance = owner.distanceTo(target);
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "a SmartBrain animal must accept the generic follow lease");
        helper.runAfterDelay(80, () -> {
            helper.assertTrue(target.isAlive(), "the SmartBrain follow target must remain alive");
            helper.assertTrue(owner.distanceTo(target) < initialDistance - 0.5D,
                    "a SmartBrain animal must move toward the owner, initial " + initialDistance
                            + ", final " + owner.distanceTo(target));
            BfsFollowManager.stop(owner, "smartbrain_navigation_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_navigation", timeoutTicks = 140)
    public static void followSharkNavigatesToOwner(GameTestHelper helper) {
        for (int x = 0; x <= 14; x++) {
            for (int y = 1; y <= 7; y++) {
                for (int z = 0; z <= 6; z++) {
                    helper.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState());
                }
            }
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-shark", new BlockPos(2, 3, 3));
        owner.setGameMode(GameType.CREATIVE);
        Mob target = helper.spawn(ModEntityTypes.GREAT_WHITE_SHARK, new BlockPos(10, 3, 3));
        double initialDistance = owner.distanceTo(target);
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "a shark must accept the generic follow lease");
        helper.runAfterDelay(100, () -> {
            helper.assertTrue(target.isAlive(), "the shark follow target must remain alive");
            helper.assertTrue(owner.distanceTo(target) < initialDistance - 0.5D,
                    "a shark must move toward the owner, initial " + initialDistance
                            + ", final " + owner.distanceTo(target));
            BfsFollowManager.stop(owner, "shark_navigation_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_restoration", timeoutTicks = 60)
    public static void followSharkReleaseClearsAquaticDestination(GameTestHelper helper) {
        for (int x = 0; x <= 14; x++) {
            for (int y = 1; y <= 7; y++) {
                for (int z = 0; z <= 6; z++) {
                    helper.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState());
                }
            }
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-shark-release", new BlockPos(2, 3, 3));
        owner.setGameMode(GameType.CREATIVE);
        Mob target = helper.spawn(ModEntityTypes.GREAT_WHITE_SHARK, new BlockPos(10, 3, 3));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "the shark must be claimed before testing controller release");
        String navigationClass = target.getNavigation().getClass().getName();
        boolean capturedDestination = target.getNavigation() instanceof PitchSwimmingNavigation navigation
                && navigation.requestedDestination() != null;
        helper.assertTrue(capturedDestination,
                "the aquatic navigation must capture the follow destination, navigation="
                        + navigationClass + ", following=" + BfsFollowManager.status(owner).following());
        helper.assertTrue(BfsFollowManager.stop(owner, "shark_release_fixture"),
                "the shark follow lease must release explicitly");
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(target.getNavigation() instanceof PitchSwimmingNavigation navigation
                            && navigation.requestedDestination() == null,
                    "releasing a shark lease must clear the captured aquatic destination");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_navigation", timeoutTicks = 140)
    public static void followLeaseNavigatesAroundMovingObstacle(GameTestHelper helper) {
        for (int x = 0; x <= 20; x++) {
            for (int z = 0; z <= 8; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
            }
        }
        for (int y = 1; y <= 2; y++) {
            for (int z = 1; z <= 3; z++) {
                helper.setBlock(new BlockPos(5, y, z), Blocks.STONE.defaultBlockState());
            }
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-navigation", new BlockPos(2, 1, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(9, 1, 2));
        target.setNoAi(false);
        double initialDistance = owner.distanceTo(target);
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "the obstacle fixture must begin with an active lease");
        helper.runAfterDelay(20, () -> {
            owner.setPos(helper.absolutePos(new BlockPos(2, 1, 4)).getCenter());
            helper.runAfterDelay(20, () -> {
                owner.setPos(helper.absolutePos(new BlockPos(9, 1, 4)).getCenter());
                helper.runAfterDelay(20, () -> {
                    owner.setPos(helper.absolutePos(new BlockPos(9, 1, 2)).getCenter());
                });
            });
        });
        helper.runAfterDelay(100, () -> {
            double finalDistance = owner.distanceTo(target);
            helper.assertTrue(finalDistance < initialDistance - 1.0D,
                    "the generic navigation adapter must reduce distance around the obstacle, initial "
                            + initialDistance + ", final " + finalDistance);
            helper.assertTrue(target.isAlive(), "the moving obstacle target must remain alive");
            BfsFollowManager.stop(owner, "navigation_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
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

    @GameTest(template = "empty", batch = "follow_security_lifecycle", timeoutTicks = 40)
    public static void followLeaseReleasesWhenMarkerIsLost(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-marker-loss", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(7, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "marker loss fixture must begin with an active lease");
        owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(!BfsFollowManager.status(owner).following(),
                    "removing the issued marker must release the lease on the next server tick");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_security_lifecycle", timeoutTicks = 40)
    public static void followLeaseReleasesWhenPermissionIsLost(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-permission-loss", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(7, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "permission loss fixture must begin with an active lease");
        helper.getLevel().getServer().getPlayerList().getOps().remove(owner.getGameProfile());
        helper.getLevel().getServer().getPlayerList().sendPlayerPermissionLevel(owner);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(!BfsFollowManager.status(owner).following(),
                    "permission loss must release the lease on the next server tick");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_security_lifecycle", timeoutTicks = 40)
    public static void followLeaseReleasesWhenOwnerLeavesRange(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-range", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(7, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "range fixture must begin with an active lease");
        owner.setPos(helper.absolutePos(new BlockPos(80, 2, 2)).getCenter());
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(!BfsFollowManager.status(owner).following(),
                    "moving beyond the sixty four block range must release the lease");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_security_lifecycle", timeoutTicks = 240)
    public static void followLeaseReleasesAfterBlockedRoute(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-blocked", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(18, 2, 2));
        target.setNoGravity(true);
        target.setNoAi(true);
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "blocked route fixture must begin with an active lease");
        helper.runAfterDelay(BfsFollowManager.BLOCKED_TICKS + 2, () -> {
            helper.assertTrue(!BfsFollowManager.status(owner).following(),
                    "a route with no progress must release after two hundred blocked ticks");
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

    @GameTest(template = "empty", batch = "follow_independent_fixture", timeoutTicks = 40)
    public static void independentlyControlledNonBfsMobUsesGenericLease(GameTestHelper helper) {
        IndependentFixtureMob target = new IndependentFixtureMob(helper.getLevel());
        target.setNoGravity(true);
        target.moveTo(helper.absolutePos(new BlockPos(8, 2, 2)).getCenter());
        helper.getLevel().addFreshEntity(target);
        ServerPlayer owner = makeTestPlayer(helper, "follow-independent", new BlockPos(2, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        BfsFollowManager.onEntityInteract(event);
        helper.assertTrue(event.isCanceled(), "the non BFS fixture must accept the generic lease");
        helper.assertTrue(BfsFollowManager.status(owner).following(),
                "the independent fixture lease must be active");
        helper.assertTrue(BfsFollowManager.stop(owner, "independent_fixture"),
                "the independent fixture lease must release cleanly");
        helper.assertTrue(!BfsFollowManager.status(owner).following(),
                "the independent fixture lease must be absent after release");
        owner.remove(Entity.RemovalReason.DISCARDED);
        target.remove(Entity.RemovalReason.DISCARDED);
        helper.succeed();
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

    private static final class IndependentFixtureMob extends Mob {
        private IndependentFixtureMob(Level level) {
            super(EntityType.COW, level);
        }

        @Override
        protected void registerGoals() {
        }
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
