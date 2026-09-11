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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.fishing.FishingCatchPolicy;
import tfar.bensfintasticsharks.init.ModItems;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@GameTestHolder("bensfintasticsharks")
@PrefixGameTestTemplate(false)
public final class BfsFishingModeGameTests {

    private BfsFishingModeGameTests() {
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_selection", timeoutTicks = 20)
    public static void fishingSelectionPreservesUnmappedSpecies(GameTestHelper helper) {
        for (boolean replacement : new boolean[]{false, true}) {
            for (Item item : List.of(Items.TROPICAL_FISH, Items.PUFFERFISH,
                    ModItems.RAW_ATLANTIC_COD, ModItems.RAW_ATLANTIC_SALMON, Items.STICK)) {
                for (float roll : new float[]{0.0F, Math.nextDown(0.25F), 0.25F, 0.99F}) {
                    for (boolean salmon : new boolean[]{false, true}) {
                        ItemStack original = new ItemStack(item);
                        original.getOrCreateTag().putString("fixture", "preserved");
                        ItemStack selected = FishingCatchPolicy.selectFishingItem(original, replacement, roll, salmon);
                        helper.assertTrue(ItemStack.matches(original, selected),
                                "Only an eligible vanilla Cod or Salmon outcome may enter Atlantic selection. " + item);
                        helper.assertTrue(selected != original && selected.getTag() != original.getTag(),
                                "Selection must preserve independent item data.");
                    }
                }
            }
        }
        for (Item item : List.of(Items.COD, Items.SALMON)) {
            helper.assertTrue(FishingCatchPolicy.selectFishingItem(new ItemStack(item), false,
                            Math.nextDown(0.25F), false).is(ModItems.RAW_ATLANTIC_COD),
                    "The lower selection boundary must admit Atlantic Cod.");
            helper.assertTrue(FishingCatchPolicy.selectFishingItem(new ItemStack(item), false,
                            Math.nextDown(0.25F), true).is(ModItems.RAW_ATLANTIC_SALMON),
                    "The lower selection boundary must admit Atlantic Salmon.");
            helper.assertTrue(FishingCatchPolicy.selectFishingItem(new ItemStack(item), false, 0.25F, true).is(item),
                    "The excluded boundary must retain the original vanilla fish.");
        }
        helper.succeed();
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_loaded_table", timeoutTicks = 20)
    public static void loadedFishingTablePreservesCategoriesAndSpecies(GameTestHelper helper) {
        boolean originalReplacement = BfsConfig.COMMON.replaceVanillaMobs.get();
        ServerPlayer player = newPlayer(helper);
        FishingHook hook = null;
        try {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FISHING_ROD));
            Items.FISHING_ROD.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
            hook = player.fishing;
            helper.assertTrue(hook != null, "The loot context requires a real fishing hook.");
            var table = helper.getLevel().getServer().getLootData().getLootTable(BuiltInLootTables.FISHING);
            for (boolean replacement : new boolean[]{false, true}) {
                BfsConfig.COMMON.replaceVanillaMobs.set(replacement);
                Set<Item> outcomes = new HashSet<>();
                int ordinaryItems = 0;
                for (int luck : new int[]{0, 3}) {
                    ItemStack tool = new ItemStack(Items.FISHING_ROD);
                    if (luck != 0) {
                        tool.enchant(Enchantments.FISHING_LUCK, luck);
                    }
                    LootParams params = new LootParams.Builder(helper.getLevel())
                            .withParameter(LootContextParams.ORIGIN, hook.position())
                            .withParameter(LootContextParams.THIS_ENTITY, hook)
                            .withParameter(LootContextParams.KILLER_ENTITY, player)
                            .withParameter(LootContextParams.TOOL, tool)
                            .withLuck(luck).create(LootContextParamSets.FISHING);
                    for (long seed = 1; seed <= 2048; seed++) {
                        List<ItemStack> original = new ArrayList<>();
                        table.getRandomItemsRaw(new LootContext.Builder(params).withOptionalRandomSeed(seed)
                                .create(null), original::add);
                        List<ItemStack> selected = table.getRandomItems(params, seed);
                        helper.assertTrue(original.size() == 1 && selected.size() == 1,
                                "The loaded root table must retain one selected loot result.");
                        ItemStack before = original.get(0);
                        ItemStack after = selected.get(0);
                        outcomes.add(after.getItem());
                        if (!before.is(Items.COD) && !before.is(Items.SALMON)) {
                            helper.assertTrue(ItemStack.matches(before, after),
                                    "Seeded nonreplacement fish, junk and treasure must remain unchanged. " + before);
                            if (!FishingCatchPolicy.isSupportedFishingFish(before)) {
                                ordinaryItems++;
                            }
                        } else {
                            helper.assertTrue(after.getCount() == 1 && FishingCatchPolicy.isSupportedFishingFish(after),
                                    "A fish category result must not append another fish or become a nonfish item.");
                            if (replacement) {
                                helper.assertTrue(after.is(before.is(Items.COD)
                                                ? ModItems.RAW_ATLANTIC_COD : ModItems.RAW_ATLANTIC_SALMON),
                                        "Replacement must preserve the selected Cod or Salmon species.");
                            }
                        }
                    }
                }
                helper.assertTrue(outcomes.containsAll(List.of(ModItems.RAW_ATLANTIC_COD,
                                ModItems.RAW_ATLANTIC_SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH)),
                        "The seeded root table must reach both Atlantic and both unreplaced fish species.");
                helper.assertTrue(outcomes.contains(Items.COD) == !replacement
                                && outcomes.contains(Items.SALMON) == !replacement,
                        "Vanilla Cod and Salmon must occur exactly in replacement disabled mode.");
                helper.assertTrue(ordinaryItems > 0, "The seeded matrix must exercise ordinary item categories.");
            }
            helper.succeed();
        } finally {
            BfsConfig.COMMON.replaceVanillaMobs.set(originalReplacement);
            if (hook != null) {
                hook.discard();
            }
            player.getAdvancements().stopListening();
            player.discard();
        }
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_replacement_live", timeoutTicks = 20)
    public static void replacementLiveCatchesMatchRealRodResults(GameTestHelper helper) {
        verifyRealRodMode(helper, true, true, new BlockPos(12, 3, 12));
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_replacement_item", timeoutTicks = 20)
    public static void replacementItemCatchesMatchRealRodResults(GameTestHelper helper) {
        verifyRealRodMode(helper, true, false, new BlockPos(12, 3, 28));
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_mixed_live", timeoutTicks = 20)
    public static void mixedLiveCatchesMatchRealRodResults(GameTestHelper helper) {
        verifyRealRodMode(helper, false, true, new BlockPos(28, 3, 12));
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_mixed_item", timeoutTicks = 20)
    public static void mixedItemCatchesMatchRealRodResults(GameTestHelper helper) {
        verifyRealRodMode(helper, false, false, new BlockPos(28, 3, 28));
    }

    private static void verifyRealRodMode(GameTestHelper helper, boolean replacement, boolean live,
                                          BlockPos playerPosition) {
        boolean originalReplacement = BfsConfig.COMMON.replaceVanillaMobs.get();
        boolean originalLive = BfsConfig.COMMON.fishEntities.get();
        ServerPlayer player = newPlayer(helper, playerPosition);
        CatchObserver observer = new CatchObserver(player);
        MinecraftForge.EVENT_BUS.register(observer);
        try {
            BfsConfig.COMMON.replaceVanillaMobs.set(replacement);
            BfsConfig.COMMON.fishEntities.set(live);
            helper.assertTrue(BfsConfig.COMMON.fishEntities.get() == live,
                    "The fishing entity delivery toggle must apply before the real rod cast. expected=" + live
                            + ", actual=" + BfsConfig.COMMON.fishEntities.get());
            var nibble = FishingHook.class.getDeclaredField("nibble");
            nibble.setAccessible(true);
            int fishCatches = 0;
            for (int attempt = 0; attempt < 64; attempt++) {
                resetCatchProgress(helper, player);
                observer.clear();
                InteractionHand hand = attempt % 2 == 0 ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
                player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
                ItemStack rod = new ItemStack(Items.FISHING_ROD);
                if (attempt % 3 == 0) {
                    rod.enchant(Enchantments.FISHING_LUCK, 3);
                }
                player.setItemInHand(hand, rod);
                Items.FISHING_ROD.use(helper.getLevel(), player, hand);
                observer.hook = player.fishing;
                helper.assertTrue(observer.hook != null, "The real rod must cast a hook.");
                var catchPosition = observer.hook.position();
                nibble.setInt(observer.hook, 1);
                int statsBefore = player.getStats().getValue(Stats.CUSTOM, Stats.FISH_CAUGHT);
                Items.FISHING_ROD.use(helper.getLevel(), player, hand);
                helper.assertTrue(observer.results.size() == 1 && observer.events == 1,
                        "The actual loaded loot pipeline must resolve exactly one event and result.");
                ItemStack selected = observer.results.get(0);
                var fishType = FishingCatchPolicy.entityTypeFor(selected);
                boolean fish = fishType != null;
                List<Entity> accepted = observer.spawned.stream()
                        .filter(entity -> !entity.isRemoved()).toList();
                List<Entity> mobs = accepted.stream().filter(Mob.class::isInstance).toList();
                List<ItemEntity> items = accepted.stream().filter(ItemEntity.class::isInstance)
                        .map(ItemEntity.class::cast).toList();
                if (live && fish) {
                    helper.assertTrue(mobs.size() == 1 && mobs.get(0).getType() == fishType && items.isEmpty(),
                            "Live mode must deliver one matching fish and no immediate item, selected="
                                    + selected + ", expected=" + fishType + ", accepted="
                                    + accepted.stream().map(entity -> entity.getType().toString()
                                    + ":removed=" + entity.isRemoved()).toList());
                    Entity caught = mobs.get(0);
                    helper.assertTrue(caught.isAlive() && caught.position().distanceToSqr(catchPosition) < 0.000001D,
                            "The fish must be alive at the actual hook location.");
                    helper.assertTrue(Double.isFinite(caught.getDeltaMovement().length())
                                    && caught.getDeltaMovement().dot(player.position().subtract(catchPosition)) > 0.0D,
                            "The live reel impulse must be finite and point toward the angler.");
                } else {
                            helper.assertTrue(mobs.isEmpty() && items.size() == 1
                                    && ItemStack.matches(selected, items.get(0).getItem()),
                            "Item delivery must retain the exact real loot result and create no fish, selected="
                                    + selected + ", accepted=" + accepted.stream().map(entity -> entity.getType()
                                    + ":removed=" + entity.isRemoved()).toList() + ", observed="
                                    + observer.spawned.stream().map(entity -> entity.getType() + ":removed="
                                    + entity.isRemoved() + ":pos=" + entity.position()).toList()
                                    + ", configuredLive=" + BfsConfig.COMMON.fishEntities.get()
                                    + ", requestedLive=" + live);
                }
                helper.assertTrue(!replacement || (!selected.is(Items.COD) && !selected.is(Items.SALMON)),
                        "Replacement enabled fishing must not leak vanilla Cod or Salmon.");
                helper.assertTrue(accepted.stream().filter(ExperienceOrb.class::isInstance).count() == 1,
                        "A successful catch must deliver XP once.");
                helper.assertTrue(player.getStats().getValue(Stats.CUSTOM, Stats.FISH_CAUGHT) - statsBefore == (fish ? 1 : 0),
                        "The fish statistic must increase exactly once for a fish and never for junk or treasure.");
                assertCatchProgress(helper, player, "oh_my_cod", selected.is(ModItems.RAW_ATLANTIC_COD));
                assertCatchProgress(helper, player, "why_arent_you_red", selected.is(ModItems.RAW_ATLANTIC_SALMON));
                helper.assertTrue(rod.getDamageValue() == 1 && observer.hook.isRemoved() && player.fishing == null,
                        "Each real cast and reel must consume one durability and clear its hook.");
                if (fish) {
                    fishCatches++;
                }
            }
            helper.assertTrue(fishCatches > 0, "Each delivery mode must exercise actual fish catches.");
            helper.succeed();
        } catch (ReflectiveOperationException exception) {
            helper.fail("Could not arm the real fishing mode fixture. " + exception.getMessage());
        } finally {
            MinecraftForge.EVENT_BUS.unregister(observer);
            observer.clear();
            BfsConfig.COMMON.replaceVanillaMobs.set(originalReplacement);
            BfsConfig.COMMON.fishEntities.set(originalLive);
            player.getAdvancements().stopListening();
            player.discard();
        }
    }

    private static ServerPlayer newPlayer(GameTestHelper helper) {
        return newPlayer(helper, new BlockPos(20, 3, 20));
    }

    private static ServerPlayer newPlayer(GameTestHelper helper, BlockPos position) {
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(UUID.randomUUID(), "fish-mode"));
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        player.setPos(helper.absolutePos(position).getCenter());
        return player;
    }

    private static void resetCatchProgress(GameTestHelper helper, ServerPlayer player) {
        for (String path : List.of("oh_my_cod", "why_arent_you_red")) {
            var advancement = helper.getLevel().getServer().getAdvancements().getAdvancement(BensFintasticSharks.id(path));
            helper.assertTrue(advancement != null, "The matching advancement must be loaded.");
            for (String criterion : advancement.getCriteria().keySet()) {
                player.getAdvancements().revoke(advancement, criterion);
            }
        }
    }

    private static void assertCatchProgress(GameTestHelper helper, ServerPlayer player, String path, boolean expected) {
        var advancement = helper.getLevel().getServer().getAdvancements().getAdvancement(BensFintasticSharks.id(path));
        helper.assertTrue(player.getAdvancements().getOrStartProgress(advancement).isDone() == expected,
                "Only the committed matching species may complete the catch advancement. " + path);
    }

    public static final class CatchObserver {
        private final ServerPlayer player;
        private final List<Entity> spawned = new ArrayList<>();
        private List<ItemStack> results = List.of();
        private FishingHook hook;
        private int events;

        private CatchObserver(ServerPlayer player) {
            this.player = player;
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void observeCatch(ItemFishedEvent event) {
            if (event.getHookEntity() == hook) {
                events++;
                results = event.getDrops().stream().map(ItemStack::copy).toList();
            }
        }

        @SubscribeEvent
        public void observeInsertion(EntityJoinLevelEvent event) {
            Entity entity = event.getEntity();
            boolean delivery = hook != null && entity != hook && entity != player && entity.level() == player.level()
                    && entity.position().distanceToSqr(hook.position()) < 16.0D;
            boolean experience = entity instanceof ExperienceOrb && entity.level() == player.level()
                    && entity.position().distanceToSqr(player.position()) < 4.0D;
            if (delivery || experience) {
                spawned.add(entity);
            }
        }

        private void clear() {
            spawned.forEach(Entity::discard);
            spawned.clear();
            if (hook != null) {
                hook.discard();
                hook = null;
            }
            results = List.of();
            events = 0;
        }
    }
}
