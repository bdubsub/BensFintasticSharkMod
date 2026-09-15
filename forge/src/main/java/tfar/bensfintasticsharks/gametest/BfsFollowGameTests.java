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
import net.minecraft.world.phys.Vec3;
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
        interactWithinReach(event);
        helper.assertTrue(!BfsFollowManager.status(owner).permitted(),
                "a deopped owner must report that the follow permission is unavailable");
        helper.assertTrue(event.isCanceled() && !BfsFollowManager.status(owner).following(),
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
        interactWithinReach(event);
        helper.assertTrue(event.isCanceled(), "a valid issued marker must claim the mob");
        helper.assertTrue(BfsFollowManager.status(owner).following(), "claimed mob must have an active lease");
        helper.runAfterDelay(1, () -> {
            PlayerInteractEvent.EntityInteract reselect = new PlayerInteractEvent.EntityInteract(
                    owner, InteractionHand.MAIN_HAND, target);
            interactWithinReach(reselect);
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

    @GameTest(template = "empty", batch = "follow_claim", timeoutTicks = 20)
    public static void duplicateInteractionCallbacksStayConsumed(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-duplicate", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.COW, new BlockPos(5, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract first = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        interactWithinReach(first);
        PlayerInteractEvent.EntityInteractSpecific duplicate = new PlayerInteractEvent.EntityInteractSpecific(
                owner, InteractionHand.MAIN_HAND, target, Vec3.ZERO);
        BfsFollowManager.onEntityInteractSpecific(duplicate);
        helper.assertTrue(first.isCanceled(), "the first interaction callback must claim the mob");
        helper.assertTrue(duplicate.isCanceled(), "the duplicate interaction callback must stay consumed");
        helper.assertTrue(BfsFollowManager.status(owner).following(),
                "the duplicate callback must leave the original follow lease active");
        owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        PlayerInteractEvent.EntityInteractSpecific missingMarkerDuplicate = new PlayerInteractEvent.EntityInteractSpecific(
                owner, InteractionHand.MAIN_HAND, target, Vec3.ZERO);
        BfsFollowManager.onEntityInteractSpecific(missingMarkerDuplicate);
        helper.assertTrue(!missingMarkerDuplicate.isCanceled(),
                "an empty hand callback must remain unrelated to the follow stick");
        helper.assertTrue(BfsFollowManager.status(owner).following(),
                "a duplicate callback with an empty stack must not drop the active lease");
        BfsFollowManager.stop(owner, "duplicate_callback_fixture");
        owner.remove(Entity.RemovalReason.DISCARDED);
        target.remove(Entity.RemovalReason.DISCARDED);
        helper.succeed();
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
        interactWithinReach(event);
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
        for (int x = 0; x <= 12; x++) for (int z = 0; z <= 5; z++) {
            helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-event-bus", new BlockPos(2, 1, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(9, 1, 2));
        issueAndHold(owner);
        helper.runAfterDelay(2, () -> {
        helper.assertTrue(target.onGround(), "the event bus navigation fixture must settle on its floor before the click");
        double initialDistance = owner.distanceTo(target);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        Vec3 clickPosition = owner.position();
        owner.setPos(target.position().add(0, 0, 2));
        MinecraftForge.EVENT_BUS.post(event);
        owner.setPos(clickPosition);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "the registered Forge interaction handler must claim a marked mob");
        helper.runAfterDelay(20, () -> {
            helper.assertTrue(owner.distanceTo(target) < initialDistance,
                    "the event bus claim must produce movement, initial " + initialDistance + ", final " + owner.distanceTo(target)
                            + ", state " + BfsFollowManager.status(owner).entries());
            BfsFollowManager.stop(owner, "event_bus_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
        });
    }

    @GameTest(template = "empty", batch = "follow_claim", timeoutTicks = 130)
    public static void heldClickRetainsSelectionUntilFreshPress(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-arrival", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.COW, new BlockPos(4, 2, 2));
        issueAndHold(owner);
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
        for (int tick = 1; tick <= 100; tick++) {
            helper.runAfterDelay(tick, () -> {
                interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
                helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1,
                        "holding use must keep exactly one selected mob across ticks and arrival");
            });
        }
        helper.runAfterDelay(101, () -> {
            owner.releaseUsingItem();
            interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 0,
                    "a new press must release the waiting mob without requiring movement");
            owner.releaseUsingItem();
            interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1,
                    "another deliberate press must select the same nearby mob immediately");
            BfsFollowManager.stop(owner, "command_stop");
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
        interactWithinReach(event);
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
        interactWithinReach(event);
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
        interactWithinReach(event);
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
        interactWithinReach(event);
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

    @GameTest(template = "empty", batch = "follow_brain_ownership", timeoutTicks = 150)
    public static void followSharkOwnsRouteAgainstPreyAndRestoresBrain(GameTestHelper helper) {
        for (int x = 0; x <= 20; x++) {
            for (int y = 1; y <= 7; y++) {
                for (int z = 0; z <= 8; z++) helper.setBlock(new BlockPos(x, y, z), Blocks.WATER);
            }
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-brain-owner", new BlockPos(2, 3, 4));
        owner.setGameMode(GameType.CREATIVE);
        var target = helper.spawn(ModEntityTypes.GREAT_WHITE_SHARK, new BlockPos(12, 3, 4));
        Mob prey = helper.spawn(EntityType.SALMON, new BlockPos(18, 3, 4));
        var brain = (net.tslat.smartbrainlib.api.core.SmartBrain<?>) target.getBrain();
        java.util.List<Object> originalTasks = brain.getBehaviours().map(task -> (Object) task).toList();
        WalkTarget originalRoute = new WalkTarget(prey, 1.0F, 1);
        target.getBrain().setMemory(MemoryModuleType.WALK_TARGET, originalRoute);
        helper.runAfterDelay(5, () -> {
            issueAndHold(owner);
            interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
            target.setTarget(prey);
        });
        for (int tick = 16; tick <= 55; tick++) {
            helper.runAtTickTime(tick, () -> {
                helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1, "the selected shark must retain its owner");
                var status = BfsFollowManager.status(owner);
                helper.assertTrue(status.waitingCount() == 1 || ownsWalkTarget(target, owner),
                        "ordinary prey pursuit and idle tasks must not overwrite the scheduled follow target");
            });
        }
        helper.runAtTickTime(56, () -> {
            BfsFollowManager.stop(owner, "brain_restoration_fixture");
            java.util.List<Object> restored = brain.getBehaviours().map(task -> (Object) task).toList();
            helper.assertTrue(restored.size() == originalTasks.size() && restored.containsAll(originalTasks),
                    "release must restore the same ordinary behavior instances without duplicates");
        });
        helper.runAtTickTime(96, () -> {
            helper.assertTrue(!ownsWalkTarget(target, owner), "the released brain must not retain the follow target");
            helper.assertTrue(!brain.getRunningBehaviors().isEmpty(), "ordinary brain tasks must run within forty ticks");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            prey.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_security_lifecycle", timeoutTicks = 40)
    public static void revokedIssuanceReleasesRetainedSelection(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-revoked", new BlockPos(2, 4, 2));
        owner.setGameMode(GameType.CREATIVE);
        Mob target = helper.spawn(EntityType.COW, new BlockPos(4, 4, 2));
        issueAndHold(owner);
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1, "valid issuance must select the member");
        owner.getPersistentData().remove("BfsFollowIssueId");
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 0, "revoked authorization must release within one tick");
            helper.assertTrue(!tfar.bensfintasticsharks.entity.FollowMovementOwners.selected(target),
                    "revocation must remove common movement ownership");
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
        interactWithinReach(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "the shark must be claimed before testing controller release");
        helper.runAfterDelay(BfsFollowManager.ROUTE_INTERVAL_TICKS + 1, () -> {
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
        interactWithinReach(event);
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
        interactWithinReach(firstEvent);
        PlayerInteractEvent.EntityInteract secondEvent = new PlayerInteractEvent.EntityInteract(
                second, InteractionHand.MAIN_HAND, target);
        interactWithinReach(secondEvent);
        helper.assertTrue(firstEvent.isCanceled(), "first owner must claim the mob");
        helper.assertTrue(secondEvent.isCanceled() && !BfsFollowManager.status(second).following(), "a conflicting owner must receive a consumed rejection without control");
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
        interactWithinReach(event);
        helper.assertTrue(event.isCanceled() && !BfsFollowManager.status(owner).following(), "a marker from a cleared server session must be rejected without an ordinary interaction");
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
        interactWithinReach(event);
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
    public static void followLeasePausesWhenMarkerIsNotHeld(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-marker-loss", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(7, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        interactWithinReach(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "marker loss fixture must begin with an active lease");
        owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1 && BfsFollowManager.status(owner).pausedCount() == 1,
                    "temporarily putting the marker away must pause the selected mob");
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
        interactWithinReach(event);
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
    public static void followLeasePausesWhenOwnerLeavesRange(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-range", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(7, 2, 2));
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        interactWithinReach(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "range fixture must begin with an active lease");
        owner.setPos(helper.absolutePos(new BlockPos(80, 2, 2)).getCenter());
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1 && BfsFollowManager.status(owner).pausedCount() == 1,
                    "moving beyond sixty four blocks must retain selection in a safe pause");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_security_lifecycle", timeoutTicks = 240)
    public static void followLeaseRetainsBlockedSelection(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-blocked", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.ZOMBIE, new BlockPos(18, 2, 2));
        target.setNoGravity(true);
        target.setNoAi(true);
        issueAndHold(owner);
        PlayerInteractEvent.EntityInteract event = new PlayerInteractEvent.EntityInteract(
                owner, InteractionHand.MAIN_HAND, target);
        interactWithinReach(event);
        helper.assertTrue(event.isCanceled() && BfsFollowManager.status(owner).following(),
                "blocked route fixture must begin with an active lease");
        helper.runAfterDelay(BfsFollowManager.BLOCKED_TICKS + 2, () -> {
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1 && BfsFollowManager.status(owner).pausedCount() == 1,
                    "two hundred blocked ticks must retain selection in a stable pause");
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
            interactWithinReach(event);
            helper.assertTrue(event.isCanceled(), "generic Mob lease must accept " + target.getType());
            helper.assertTrue(BfsFollowManager.status(owner).following(), "lease must be active for " + target.getType());
            if (target == targets[5]) {
                helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1,
                        "the SmartBrainLib target must have one selected member before its scheduled route");
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

    @GameTest(template = "empty", batch = "follow_controller_families", timeoutTicks = 180)
    public static void bossFollowAdaptersNavigateAndRestore(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-bosses", new BlockPos(2, 20, 2));
        owner.setGameMode(GameType.CREATIVE);
        EnderDragon dragon = helper.spawn(EntityType.ENDER_DRAGON, new BlockPos(20, 20, 2));
        Mob wither = helper.spawn(EntityType.WITHER, new BlockPos(20, 20, 8));
        dragon.setSilent(true);
        dragon.setInvulnerable(true);
        wither.setInvulnerable(true);
        var previousDragonPhase = dragon.getPhaseManager().getCurrentPhase().getPhase();
        double initialDragonDistance = owner.distanceTo(dragon);
        double initialWitherDistance = owner.distanceTo(wither);

        issueAndHold(owner);
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, dragon.head));
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1,
                "the ender dragon parent must receive one follow lease through its head part");
        issueAndHold(owner);
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, wither));
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 2,
                "the wither must remain independently selected beside the dragon");

        helper.runAtTickTime(120, () -> {
            helper.assertTrue(owner.distanceTo(dragon) < initialDragonDistance - 0.5D,
                    "the ender dragon phase adapter must make progress, initial " + initialDragonDistance
                            + ", final " + owner.distanceTo(dragon) + ", alive " + dragon.isAlive()
                            + ", removed " + dragon.isRemoved() + ", phase "
                            + dragon.getPhaseManager().getCurrentPhase().getPhase() + ", position " + dragon.position()
                            + ", delta " + dragon.getDeltaMovement() + ", state " + BfsFollowManager.status(owner));
            helper.assertTrue(owner.distanceTo(wither) < initialWitherDistance - 0.5D,
                    "the wither navigation adapter must make progress, initial " + initialWitherDistance
                            + ", final " + owner.distanceTo(wither) + ", state " + BfsFollowManager.status(owner));
            owner.releaseUsingItem();
            interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, dragon.head));
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1,
                    "a fresh dragon click must release only the dragon lease");
            owner.releaseUsingItem();
            interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, wither));
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 0,
                    "a fresh wither click must release the remaining lease");
            helper.assertTrue(dragon.getPhaseManager().getCurrentPhase().getPhase() == previousDragonPhase,
                    "releasing the dragon must restore its ordinary phase");
            owner.remove(Entity.RemovalReason.DISCARDED);
            dragon.remove(Entity.RemovalReason.DISCARDED);
            wither.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
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
        interactWithinReach(event);
        helper.assertTrue(event.isCanceled(), "the SmartBrainLib target must be claimed before restoration");
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1, "the SmartBrainLib selection must be retained before its first scheduled route");
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
        interactWithinReach(event);
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

    @GameTest(template = "empty", batch = "follow_moving_group", timeoutTicks = 220)
    public static void twentyFollowersMoveTogetherAndToggleSeparately(GameTestHelper helper) {
        for (int x = 0; x <= 30; x++) {
            for (int z = 0; z <= 25; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-band", new BlockPos(3, 1, 12));
        owner.setGameMode(GameType.CREATIVE);
        issueAndHold(owner);
        java.util.List<Mob> members = new java.util.ArrayList<>();
        java.util.Map<Mob, Double> initialDistances = new java.util.HashMap<>();
        for (int i = 0; i < 20; i++) {
            Mob mob = helper.spawn(EntityType.COW, new BlockPos(15 + i / 5 * 2, 1, 8 + i % 5 * 2));
            members.add(mob);
            initialDistances.put(mob, (double) owner.distanceTo(mob));
        }
        helper.runAfterDelay(2, () -> {
            for (Mob mob : members) {
                owner.stopUsingItem();
                interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, mob));
            }
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 20, "all twenty members must remain selected together");
        });
        helper.runAtTickTime(100, () -> {
            for (Mob mob : members) {
                helper.assertTrue(mob.isAlive() && owner.distanceTo(mob) < initialDistances.get(mob) - 0.5D,
                        "every selected member must make real navigation progress, member " + members.indexOf(mob)
                                + ", alive " + mob.isAlive() + ", position " + mob.position()
                                + ", initial distance " + initialDistances.get(mob) + ", distance " + owner.distanceTo(mob)
                                + ", path " + mob.getNavigation().getPath() + ", group " + BfsFollowManager.status(owner));
            }
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 20, "arrival must not release any group member");
            owner.setPos(helper.absolutePos(new BlockPos(3, 1, 22)).getCenter());
            initialDistances.replaceAll((mob, ignored) -> (double) owner.distanceTo(mob));
        });
        helper.runAtTickTime(190, () -> {
            for (Mob mob : members) {
                helper.assertTrue(owner.distanceTo(mob) < initialDistances.get(mob) - 0.5D,
                        "every selected member must follow the owner after a second move without reclaiming");
            }
            for (int i = 0; i < members.size(); i++) {
                owner.stopUsingItem();
                interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, members.get(i)));
                helper.assertTrue(BfsFollowManager.selectedCount(owner) == 19 - i,
                        "each fresh click must release exactly its own member");
            }
            owner.remove(Entity.RemovalReason.DISCARDED);
            members.forEach(mob -> mob.remove(Entity.RemovalReason.DISCARDED));
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_groups", timeoutTicks = 40)
    public static void thirtyThreeFollowersToggleIndependently(GameTestHelper helper) {
        ServerPlayer owner = makeTestPlayer(helper, "follow-group", new BlockPos(2, 2, 2));
        ServerPlayer other = makeTestPlayer(helper, "follow-other", new BlockPos(3, 2, 2));
        issueAndHold(owner);
        issueAndHold(other);
        java.util.List<Mob> targets = new java.util.ArrayList<>();
        for (int i = 0; i < 33; i++) {
            Mob mob = helper.spawn(EntityType.COW, new BlockPos(3 + i % 6 * 2, 2, 3 + i / 6 * 2));
            targets.add(mob);
            owner.releaseUsingItem();
            interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, mob));
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == i + 1,
                    "every selection must add a member without replacing an earlier member or applying the old cap");
        }
        helper.assertTrue(BfsFollowManager.status(owner, 4).entries().size() == 3,
                "the fourth status page must contain the final three members");
        other.releaseUsingItem();
        interactWithinReach(new PlayerInteractEvent.EntityInteract(other, InteractionHand.MAIN_HAND, targets.get(0)));
        helper.assertTrue(BfsFollowManager.selectedCount(other) == 0 && BfsFollowManager.selectedCount(owner) == 33,
                "another operator must not steal a selected mob");
        owner.releaseUsingItem();
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, targets.get(0)));
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 32, "a fresh click must release only one member");
        helper.assertTrue(BfsFollowManager.stopOne(owner, targets.get(1)), "targeted stop must release a selected member");
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 31, "targeted stop must preserve the rest of the group");
        BfsFollowManager.issue(owner);
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 31, "reissuing the marker must preserve the group");
        helper.assertTrue(BfsFollowManager.stopAll(owner, "command_stop") == 31, "group stop must release every remaining member");
        helper.assertTrue(BfsFollowManager.stopAll(owner, "command_stop") == 0, "group stop must be idempotent");
        owner.remove(Entity.RemovalReason.DISCARDED);
        other.remove(Entity.RemovalReason.DISCARDED);
        targets.forEach(mob -> mob.remove(Entity.RemovalReason.DISCARDED));
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "follow_feedback", timeoutTicks = 40)
    public static void followFeedbackMatchesEveryClickOutcome(GameTestHelper helper) {
        FeedbackPlayer owner = (FeedbackPlayer) makeTestPlayer(helper, "follow-feedback", new BlockPos(2, 2, 2));
        Mob target = helper.spawn(EntityType.COW, new BlockPos(4, 2, 2));
        issueAndHold(owner);
        owner.receipts.clear();
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
        assertReceiptPair(helper, owner, "bfs.follow.selected");
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
        helper.assertTrue(owner.receipts.size() == 2, "held duplicate callbacks must not add feedback");
        owner.releaseUsingItem();
        owner.receipts.clear();
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
        assertReceiptPair(helper, owner, "bfs.follow.released");
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 0, "release feedback must agree with membership");
        owner.releaseUsingItem();
        owner.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WHEAT));
        owner.receipts.clear();
        PlayerInteractEvent.EntityInteract unrelated = new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target);
        interactWithinReach(unrelated);
        helper.assertTrue(!unrelated.isCanceled() && owner.receipts.isEmpty(),
                "ordinary item interactions must not produce follow errors or get consumed");
        issueAndHold(owner);
        helper.getLevel().getServer().getPlayerList().deop(owner.getGameProfile());
        owner.receipts.clear();
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
        assertReceiptPair(helper, owner, "bfs.follow.permission_denied");
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 0, "rejected claims must preserve membership");
        owner.remove(Entity.RemovalReason.DISCARDED);
        target.remove(Entity.RemovalReason.DISCARDED);
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "follow_safety", timeoutTicks = 100)
    public static void followYieldsToNativeFireEscape(GameTestHelper helper) {
        for (int x = 0; x <= 18; x++) {
            for (int z = 0; z <= 10; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
        }
        helper.setBlock(new BlockPos(14, 1, 5), Blocks.WATER);
        ServerPlayer owner = makeTestPlayer(helper, "follow-safety", new BlockPos(2, 1, 5));
        owner.setGameMode(GameType.CREATIVE);
        Mob target = helper.spawn(EntityType.COW, new BlockPos(10, 1, 5));
        issueAndHold(owner);
        helper.runAfterDelay(2, () -> {
            interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
            target.setSecondsOnFire(10);
        });
        helper.runAtTickTime(8, () -> {
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1,
                    "fire escape must retain follow membership");
            helper.assertTrue(BfsFollowManager.status(owner).pausedCount() == 1,
                    "follow movement must pause for fire safety");
            helper.assertTrue(target.goalSelector.getRunningGoals().anyMatch(goal ->
                            goal.getGoal() instanceof net.minecraft.world.entity.ai.goal.PanicGoal),
                    "the native panic goal must run while follow is selected");
            target.clearFire();
        });
        helper.runAtTickTime(40, () -> {
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 1,
                    "safety recovery must not require another selection");
            helper.assertTrue(BfsFollowManager.status(owner).pausedCount() == 0,
                    "the selected member must resume after the fire clears");
            BfsFollowManager.stop(owner, "command_stop");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_scheduler", timeoutTicks = 180)
    public static void threeHundredTwentyOneFollowersUseFairBoundedScheduler(GameTestHelper helper) {
        for (int x = 0; x <= 34; x++) {
            for (int z = 0; z <= 34; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-scheduler", new BlockPos(2, 1, 2));
        owner.setGameMode(GameType.CREATIVE);
        issueAndHold(owner);
        java.util.List<Mob> members = new java.util.ArrayList<>(321);
        for (int i = 0; i < 321; i++) {
            int x = 5 + (i % 18);
            int z = 5 + (i / 18);
            Mob mob = helper.spawn(EntityType.COW, new BlockPos(x, 1, z));
            mob.setInvulnerable(true);
            mob.setSilent(true);
            members.add(mob);
            owner.stopUsingItem();
            interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, mob));
        }
        helper.assertTrue(BfsFollowManager.selectedCount(owner) == 321,
                "the scheduler witness must retain all 321 selected members");
        helper.runAfterDelay(130, () -> {
            BfsFollowManager.SchedulerStats stats = BfsFollowManager.schedulerStats();
            helper.assertTrue(stats.peakPerTick() <= BfsFollowManager.MAX_ROUTES_PER_TICK,
                    "route evaluation must stay within the per tick bound, observed " + stats.peakPerTick());
            helper.assertTrue(stats.evaluations() >= 321,
                    "the fair queue must service every member, observed " + stats.evaluations());
            helper.assertTrue(BfsFollowManager.selectedCount(owner) == 321,
                    "deferring route work must never evict a member, status " + BfsFollowManager.status(owner));
            BfsFollowManager.stop(owner, "scheduler_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            members.forEach(mob -> mob.remove(Entity.RemovalReason.DISCARDED));
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "follow_capture", timeoutTicks = 120)
    public static void followCaptureWritesPrivateGroupTransitions(GameTestHelper helper) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 8; z++) helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE);
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-private-owner", new BlockPos(2, 1, 3));
        owner.setGameMode(GameType.CREATIVE);
        Mob target = helper.spawn(EntityType.COW, new BlockPos(12, 1, 3));
        var started = tfar.bensfintasticsharks.debug.BfsDebugManager.start(
                owner.createCommandSourceStack(), "follow", 100, java.util.List.of(target));
        helper.assertTrue(started.started(), "the real follow capture producer must start");
        issueAndHold(owner);
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
        helper.runAfterDelay(30, () -> {
            BfsFollowManager.stop(owner, "command_stop");
            tfar.bensfintasticsharks.debug.BfsDebugManager.stop("follow_capture_fixture");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeedWhen(() -> {
                try {
                    var path = started.activeSession().outputPath();
                    helper.assertTrue(java.nio.file.Files.exists(path), "the capture writer must create its output");
                    String capture = java.nio.file.Files.readString(path);
                    helper.assertTrue(capture.contains("\"event\":\"end\""), "the capture writer must finish its footer");
                    boolean claim = false, route = false, release = false;
                    long previousTick = -1;
                    for (String line : capture.lines().toList()) {
                        var record = com.google.gson.JsonParser.parseString(line).getAsJsonObject();
                        String event = record.get("event").getAsString();
                        long tick = record.get("tick").getAsLong();
                        helper.assertTrue(tick >= previousTick, "the terminal record must not move capture time backward");
                        previousTick = tick;
                        if (!event.startsWith("follow.")) continue;
                        helper.assertTrue(!line.contains(owner.getUUID().toString())
                                        && !line.contains(target.getUUID().toString())
                                        && !line.contains(owner.getGameProfile().getName()),
                                "follow records must not expose raw player names or entity identities");
                        helper.assertTrue(record.get("followVersion").getAsInt() == 2,
                                "real group transitions must declare version two");
                        helper.assertTrue(record.get("groupRevision").getAsLong() >= 1,
                                "real group transitions must carry a revision");
                        if (event.equals("follow.claim")) {
                            claim = record.get("selectedCount").getAsInt() == 1;
                        }
                        if (event.equals("follow.intent")) route = true;
                        if (event.equals("follow.release")) {
                            release = record.get("selectedCount").getAsInt() == 0;
                        }
                    }
                    helper.assertTrue(claim && route && release, "capture must contain selection, real route and release counts");
                } catch (java.io.IOException exception) {
                    helper.fail("Cannot read the follow capture: " + exception.getMessage());
                }
            });
        });
    }

    @GameTest(template = "empty", batch = "follow_retention", timeoutTicks = 2450)
    public static void followSelectionSurvivesLongWaitingAndUnheldMarker(GameTestHelper helper) {
        for (int x = 0; x < 12; x++) for (int z = 0; z < 8; z++) {
            helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
        }
        for (int x = 0; x < 12; x++) for (int z = 0; z < 8; z++) {
            helper.setBlock(new BlockPos(x, 0, z), Blocks.SEA_LANTERN.defaultBlockState());
            helper.setBlock(new BlockPos(x, 4, z), Blocks.STONE.defaultBlockState());
            if (x == 0 || x == 11 || z == 0 || z == 7) for (int y = 1; y < 4; y++) {
                helper.setBlock(new BlockPos(x, y, z), Blocks.STONE.defaultBlockState());
            }
        }
        ServerPlayer owner = makeTestPlayer(helper, "follow-retention", new BlockPos(2, 1, 3));
        Mob target = helper.spawn(EntityType.COW, new BlockPos(4, 1, 3));
        ItemStack marker = issueAndHold(owner).copy();
        interactWithinReach(new PlayerInteractEvent.EntityInteract(owner, InteractionHand.MAIN_HAND, target));
        owner.releaseUsingItem();
        helper.runAfterDelay(100, () -> owner.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY));
        helper.runAfterDelay(110, () -> {
            helper.assertTrue(BfsFollowManager.status(owner).pausedCount() == 1,
                    "an unheld marker must pause the selected mob");
            owner.setItemInHand(InteractionHand.MAIN_HAND, marker);
        });
        helper.runAfterDelay(120, () -> helper.assertTrue(BfsFollowManager.status(owner).waitingCount() == 1,
                "holding the same authorized marker again must resume nearby waiting without a new selection"));
        helper.runAfterDelay(2401, () -> {
            helper.assertTrue(target.isAlive() && BfsFollowManager.selectedCount(owner) == 1,
                    "arrival and the previous lifetime must preserve selection, target alive " + target.isAlive()
                            + ", owner alive " + owner.isAlive() + ", selected " + BfsFollowManager.selectedCount(owner)
                            + ", recent receipts " + ((FeedbackPlayer) owner).receipts.stream().skip(
                                    Math.max(0, ((FeedbackPlayer) owner).receipts.size() - 6)).toList());
            helper.assertTrue(BfsFollowManager.status(owner).waitingCount() == 1,
                    "the nearby selected mob must still be waiting");
            BfsFollowManager.stopAll(owner, "command_stop");
            owner.remove(Entity.RemovalReason.DISCARDED);
            target.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    private static void assertReceiptPair(GameTestHelper helper, FeedbackPlayer owner, String key) {
        helper.assertTrue(owner.receipts.size() == 2, "one explicit action must emit exactly two channel receipts");
        helper.assertTrue(!owner.receipts.get(0).overlay() && owner.receipts.get(1).overlay(),
                "the result must reach chat and the action bar");
        for (Receipt receipt : owner.receipts) {
            helper.assertTrue(receipt.text().getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents text
                            && text.getKey().equals(key), "feedback must describe the actual outcome using a localized component, expected " + key + ", actual " + receipt.text().getContents());
        }
    }

    private record Receipt(net.minecraft.network.chat.Component text, boolean overlay) {}

    private static final class FeedbackPlayer extends ServerPlayer {
        private final java.util.List<Receipt> receipts = new java.util.ArrayList<>();

        private FeedbackPlayer(GameTestHelper helper, GameProfile profile) {
            super(helper.getLevel().getServer(), helper.getLevel(), profile);
        }

        @Override
        public void sendSystemMessage(net.minecraft.network.chat.Component text) {
            receipts.add(new Receipt(text.copy(), false));
        }

        @Override
        public void displayClientMessage(net.minecraft.network.chat.Component text, boolean overlay) {
            receipts.add(new Receipt(text.copy(), overlay));
        }
    }

    private static void interactWithinReach(PlayerInteractEvent.EntityInteract event) {
        ServerPlayer owner = (ServerPlayer) event.getEntity();
        Vec3 position = owner.position();
        owner.setPos(event.getTarget().position().add(0, 0, 2));
        try {
            BfsFollowManager.onEntityInteract(event);
        } finally {
            owner.setPos(position);
        }
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
        ServerPlayer player = new FeedbackPlayer(helper, new GameProfile(java.util.UUID.randomUUID(), name));
        player.setPos(helper.absolutePos(localPosition).getCenter());
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        helper.getLevel().addNewPlayer(player);
        helper.getLevel().getServer().getPlayerList().getOps().add(
                new net.minecraft.server.players.ServerOpListEntry(player.getGameProfile(), 2, false));
        helper.getLevel().getServer().getPlayerList().sendPlayerPermissionLevel(player);
        return player;
    }
}
