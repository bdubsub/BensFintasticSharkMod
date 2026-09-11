package tfar.bensfintasticsharks.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.fishing.FishingCatchDelivery;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GameTestHolder("bensfintasticsharks")
@PrefixGameTestTemplate(false)
public final class BfsFishingGameTests {

    private BfsFishingGameTests() {
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_nested_live", timeoutTicks = 20)
    public static void nestedFishInsertionCommitsOneCatch(GameTestHelper helper) {
        verifyDelivery(helper, FailureMode.REENTER, true);
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_rejected_live", timeoutTicks = 20)
    public static void rejectedFishInsertionCannotRetryOrAwardCatch(GameTestHelper helper) {
        verifyDelivery(helper, FailureMode.REJECT, false);
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_cancelled_live", timeoutTicks = 20)
    public static void cancelledFishingCannotCreateLiveRewards(GameTestHelper helper) {
        verifyDelivery(helper, FailureMode.CANCEL, false);
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_replay_live", timeoutTicks = 20)
    public static void committedFishingCannotReplayAfterHookRemoval(GameTestHelper helper) {
        verifyDelivery(helper, FailureMode.NONE, true);
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_removed_live", timeoutTicks = 20)
    public static void removedHookDuringInsertionCannotAwardCatch(GameTestHelper helper) {
        verifyDelivery(helper, FailureMode.REMOVE_HOOK, false);
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_nested_item", timeoutTicks = 20)
    public static void nestedItemInsertionCommitsOneCatch(GameTestHelper helper) {
        verifyDelivery(helper, FailureMode.REENTER, true, false);
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_rejected_item", timeoutTicks = 20)
    public static void rejectedItemInsertionCannotRetryOrAwardCatch(GameTestHelper helper) {
        verifyDelivery(helper, FailureMode.REJECT, false, false);
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_cancelled_item", timeoutTicks = 20)
    public static void cancelledItemFishingCannotCreateRewards(GameTestHelper helper) {
        verifyDelivery(helper, FailureMode.CANCEL, false, false);
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_removed_item", timeoutTicks = 20)
    public static void removedHookDuringItemInsertionCannotAwardCatch(GameTestHelper helper) {
        verifyDelivery(helper, FailureMode.REMOVE_HOOK, false, false);
    }

    private static void verifyDelivery(GameTestHelper helper, FailureMode mode, boolean succeeds) {
        verifyDelivery(helper, mode, succeeds, true);
    }

    private static void verifyDelivery(GameTestHelper helper, FailureMode mode, boolean succeeds, boolean live) {
        boolean originalLive = BfsConfig.COMMON.fishEntities.get();
        BfsConfig.COMMON.fishEntities.set(live);
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(UUID.randomUUID(), "fish-transaction"));
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        player.setPos(helper.absolutePos(playerPosition(mode, live)).getCenter());
        ItemStack rod = new ItemStack(Items.FISHING_ROD);
        player.setItemInHand(InteractionHand.MAIN_HAND, rod);
        FishingEvents events = new FishingEvents(player, mode);
        MinecraftForge.EVENT_BUS.register(events);
        try {
            Items.FISHING_ROD.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            events.hook = player.fishing;
            helper.assertTrue(events.hook != null, "Using the rod must create the real hook.");
            var nibble = FishingHook.class.getDeclaredField("nibble");
            nibble.setAccessible(true);
            nibble.setInt(events.hook, 1);
            Items.FISHING_ROD.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);

            int fishCount = (int) events.spawned.stream()
                    .filter(entity -> entity.getType() == ModEntityTypes.ATLANTIC_COD)
                    .filter(entity -> entity.isAlive() && !entity.isRemoved()).count();
            int orbCount = (int) events.spawned.stream().filter(ExperienceOrb.class::isInstance).count();
            helper.assertTrue(events.fishingEvents == 1, "The rod must resolve one real fishing event.");
            helper.assertTrue(fishCount == (succeeds && live ? 1 : 0),
                    "One attempt must deliver exactly its accepted live fish count. expected="
                            + (succeeds && live ? 1 : 0) + ", actual=" + fishCount + ", spawned="
                            + events.spawned.stream().map(entity -> entity.getType() + "@" + entity.position()
                            + "/removed=" + entity.isRemoved()).toList());
            helper.assertTrue(events.fishInsertions == (mode == FailureMode.CANCEL ? 0 : 1),
                    "Nested or rejected insertion must not start another fish insertion.");
            long itemCount = events.spawned.stream().filter(ItemEntity.class::isInstance)
                    .filter(entity -> entity.isAlive() && !entity.isRemoved()).count();
            helper.assertTrue(itemCount == (succeeds && !live ? 1 : 0),
                    "Only successful item delivery may create one item reward.");
            helper.assertTrue(orbCount == (succeeds ? 1 : 0), "Fishing XP must follow one committed delivery only.");
            helper.assertTrue(player.getStats().getValue(Stats.CUSTOM, Stats.FISH_CAUGHT) == (succeeds ? 1 : 0),
                    "The catch statistic must follow one committed delivery only.");
            var cod = helper.getLevel().getServer().getAdvancements()
                    .getAdvancement(BensFintasticSharks.id("oh_my_cod"));
            var salmon = helper.getLevel().getServer().getAdvancements()
                    .getAdvancement(BensFintasticSharks.id("why_arent_you_red"));
            helper.assertTrue(cod != null && salmon != null, "Both catch advancements must be loaded.");
            helper.assertTrue(player.getAdvancements().getOrStartProgress(cod).isDone() == succeeds,
                    "Only committed Cod delivery may grant the Cod catch advancement.");
            helper.assertTrue(!player.getAdvancements().getOrStartProgress(salmon).isDone(),
                    "A Cod transaction must not grant the Salmon catch advancement.");
            helper.assertTrue(rod.getDamageValue() == 1, "The real reel must charge rod durability once.");
            helper.assertTrue(events.hook.isRemoved() && player.fishing == null,
                    "The real reel must remove its hook on success or failure.");

            int attemptsBeforeReplay = events.fishInsertions;
            int spawnedBeforeReplay = events.spawned.size();
            FishingCatchDelivery.onItemFished(events.replay());
            helper.assertTrue(events.fishInsertions == attemptsBeforeReplay
                            && events.spawned.size() == spawnedBeforeReplay,
                    "A removed hook must not create another fish or success reward.");
            helper.succeed();
        } catch (ReflectiveOperationException exception) {
            helper.fail("Could not arm the real rod fixture. " + exception.getMessage());
        } finally {
            MinecraftForge.EVENT_BUS.unregister(events);
            if (events.hook != null) {
                events.hook.discard();
            }
            events.spawned.forEach(Entity::discard);
            events.observed.forEach(Entity::discard);
            player.getAdvancements().stopListening();
            player.discard();
            BfsConfig.COMMON.fishEntities.set(originalLive);
        }
    }

    private static BlockPos playerPosition(FailureMode mode, boolean live) {
        return switch (mode) {
            case NONE -> new BlockPos(20, 3, 20);
            case REENTER -> live ? new BlockPos(4, 3, 4) : new BlockPos(4, 3, 8);
            case REJECT -> live ? new BlockPos(4, 3, 35) : new BlockPos(8, 3, 35);
            case CANCEL -> live ? new BlockPos(35, 3, 4) : new BlockPos(35, 3, 8);
            case REMOVE_HOOK -> live ? new BlockPos(35, 3, 35) : new BlockPos(31, 3, 35);
        };
    }

    private enum FailureMode {
        NONE, REENTER, REJECT, CANCEL, REMOVE_HOOK
    }

    public static final class FishingEvents {
        private final ServerPlayer player;
        private final FailureMode mode;
        private final List<Entity> spawned = new ArrayList<>();
        private final List<Entity> observed = new ArrayList<>();
        private FishingHook hook;
        private int fishInsertions;
        private int fishingEvents;
        private boolean nested;

        private FishingEvents(ServerPlayer player, FailureMode mode) {
            this.player = player;
            this.mode = mode;
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void selectKnownCatch(ItemFishedEvent event) {
            if (event.getHookEntity() != hook) {
                return;
            }
            fishingEvents++;
            event.getDrops().clear();
            event.getDrops().add(new ItemStack(ModItems.RAW_ATLANTIC_COD));
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public void cancelCatch(ItemFishedEvent event) {
            if (event.getHookEntity() == hook && mode == FailureMode.CANCEL) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public void interceptInsertion(EntityJoinLevelEvent event) {
            Entity entity = event.getEntity();
            if (hook == null || entity.level() != player.level() || entity == hook || entity == player) {
                return;
            }
            boolean nearHook = entity.position().distanceToSqr(hook.position()) <= 1.0D;
            boolean experience = entity instanceof ExperienceOrb
                    && entity.position().distanceToSqr(player.position()) <= 4.0D;
            if (!nearHook && !experience) return;
            observed.add(entity);
            if (experience) {
                spawned.add(entity);
            }
            if (!nearHook) return;
            boolean matchingItem = entity instanceof ItemEntity item && item.getItem().is(ModItems.RAW_ATLANTIC_COD);
            if (entity.getType() != ModEntityTypes.ATLANTIC_COD && !matchingItem) {
                return;
            }
            fishInsertions++;
            if (mode == FailureMode.REJECT) {
                event.setCanceled(true);
                return;
            }
            spawned.add(entity);
            if (mode == FailureMode.REMOVE_HOOK) {
                hook.discard();
            }
            if (!nested && (mode == FailureMode.REENTER || mode == FailureMode.REJECT)) {
                nested = true;
                FishingCatchDelivery.onItemFished(replay());
            }
        }

        private ItemFishedEvent replay() {
            return new ItemFishedEvent(List.of(new ItemStack(ModItems.RAW_ATLANTIC_COD)), 1, hook);
        }
    }
}
