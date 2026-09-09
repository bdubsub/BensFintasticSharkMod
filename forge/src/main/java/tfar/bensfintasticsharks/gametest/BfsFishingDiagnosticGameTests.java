package tfar.bensfintasticsharks.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.debug.BfsDebugManager;
import tfar.bensfintasticsharks.init.ModItems;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@GameTestHolder("bensfintasticsharks")
@PrefixGameTestTemplate(false)
public final class BfsFishingDiagnosticGameTests {
    private BfsFishingDiagnosticGameTests() {
    }

    @GameTest(template = "bfsgametests.empty", batch = "bfs_fishing_diagnostics", timeoutTicks = 240)
    public static void fishingDiagnosticsObserveRealRodWithoutChangingRewards(GameTestHelper helper) {
        Probe probe = new Probe(helper);
        MinecraftForge.EVENT_BUS.register(probe);
        try {
            BfsDebugManager.stop("gametest_setup");
            probe.fish(true, true, false, false);
            helper.assertTrue(!BfsDebugManager.status().active(), "Fishing must not enable diagnostics.");
            var server = helper.getLevel().getServer();
            var source = server.createCommandSourceStack().withLevel(helper.getLevel()).withPermission(4);
            server.getCommands().getDispatcher().execute("bfs debug on advancement 80", source);
            Path output = BfsDebugManager.status().session().outputPath();
            for (int index = 0; index < 5; index++) {
                int mode = index;
                helper.runAtTickTime(2 + index * 4, () -> {
                    try {
                        probe.fish(mode < 2, mode % 2 == 0, mode == 4, mode % 2 != 0);
                    } catch (Exception failure) {
                        probe.cleanup();
                        helper.fail("Fishing diagnostic fixture failed. " + failure.getMessage());
                    }
                });
            }
            helper.runAtTickTime(22, () -> BfsDebugManager.stop("gametest_complete"));
            verifyCapture(helper, probe, output, 160);
        } catch (Exception failure) {
            probe.cleanup();
            helper.fail("Could not start the fishing diagnostic fixture. " + failure.getMessage());
        }
    }

    private static void verifyCapture(GameTestHelper helper, Probe probe, Path output, int remaining) {
        helper.runAfterDelay(2, () -> {
            try {
                if (!Files.exists(output) || !Files.readString(output).contains("\"event\":\"end\"")) {
                    if (remaining <= 0) {
                        throw new IllegalStateException("The bounded writer did not finish its capture.");
                    }
                    verifyCapture(helper, probe, output, remaining - 2);
                    return;
                }
                String text = Files.readString(output);
                List<JsonObject> rows = text.lines().map(line -> JsonParser.parseString(line).getAsJsonObject()).toList();
                List<JsonObject> deliveries = rows.stream().filter(row -> "fishing".equals(row.get("event").getAsString()))
                        .filter(row -> "delivery".equals(row.get("stage").getAsString())).toList();
                List<JsonObject> settled = rows.stream().filter(row -> "fishing".equals(row.get("event").getAsString()))
                        .filter(row -> "settled".equals(row.get("stage").getAsString())).toList();
                helper.assertTrue(deliveries.size() == 5 && settled.size() == 5,
                        "Every captured rod use must have one delivery and one post-reel record.");
                helper.assertTrue(deliveries.stream().filter(row -> "committed".equals(row.get("outcome").getAsString())).count() == 4,
                        "All four delivery modes must report their real committed result.");
                helper.assertTrue(deliveries.stream().filter(row -> "previously_cancelled".equals(row.get("outcome").getAsString())).count() == 1,
                        "The cancelled catch must be observed without rewards.");
                helper.assertTrue(deliveries.stream().anyMatch(row -> "off_hand".equals(row.get("rodHand").getAsString())),
                        "The captured tool must preserve offhand identity.");
                for (JsonObject row : settled) {
                    helper.assertTrue(row.get("hookRemoved").getAsBoolean() && !row.get("hookStillOwned").getAsBoolean(),
                            "Post-reel evidence must observe actual hook removal.");
                    helper.assertTrue(row.get("rodDamageAfter").getAsInt() - row.get("rodDamageBefore").getAsInt() == 1,
                            "Post-reel evidence must observe actual rod damage.");
                    helper.assertTrue(!row.get("ambiguousSettlement").getAsBoolean(), "Each fixture uses a distinct angler.");
                }
                for (ServerPlayer player : probe.players) {
                    helper.assertTrue(!text.contains(player.getUUID().toString()), "The capture must pseudonymize anglers.");
                }
                helper.assertTrue(!text.contains("private_rod_probe") && !text.contains("fishing-private-player"),
                        "Rod NBT and player names must not enter fishing support evidence.");
                helper.assertTrue(!rows.get(rows.size() - 1).get("incomplete").getAsBoolean(),
                        "A completed capture cannot conceal missing settlements.");
                verifyCaptureLimits(helper, probe);
                probe.cleanup();
                helper.succeed();
            } catch (Throwable failure) {
                probe.cleanup();
                helper.fail("Fishing capture verification failed. " + failure.getMessage());
            }
        });
    }

    private static void verifyCaptureLimits(GameTestHelper helper, Probe probe) throws Exception {
        var server = helper.getLevel().getServer();
        var source = server.createCommandSourceStack().withLevel(helper.getLevel()).withPermission(4);
        server.getCommands().getDispatcher().execute("bfs debug on advancement 80", source);
        for (int attempt = 0; attempt <= BfsDebugManager.MAX_TARGETS; attempt++) {
            probe.fish(true, true, false, false);
        }
        var limited = BfsDebugManager.status();
        helper.assertTrue(!limited.active() && limited.lastStop().incomplete()
                        && limited.lastStop().reason().equals("fishing_settlement_limit"),
                "Excess pending fishing observations must stop incomplete without preventing rod use.");
        server.getCommands().getDispatcher().execute("bfs debug on advancement 80", source);
        probe.fish(true, false, true, false);
        server.getCommands().getDispatcher().execute("bfs debug off", source);
        helper.assertTrue(!BfsDebugManager.status().active() && BfsDebugManager.status().lastStop().incomplete(),
                "Stopping before actual rod settlement must mark the capture incomplete.");
    }

    public static final class Probe {
        private final GameTestHelper helper;
        private final List<ServerPlayer> players = new ArrayList<>();
        private final List<Entity> entities = new ArrayList<>();
        private ServerPlayer current;
        private boolean cancel;

        private Probe(GameTestHelper helper) {
            this.helper = helper;
        }

        private void fish(boolean replace, boolean live, boolean cancel, boolean offhand) throws ReflectiveOperationException {
            boolean oldReplace = BfsConfig.COMMON.replaceVanillaMobs.get();
            boolean oldLive = BfsConfig.COMMON.fishEntities.get();
            BfsConfig.COMMON.replaceVanillaMobs.set(replace);
            BfsConfig.COMMON.fishEntities.set(live);
            current = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                    new GameProfile(UUID.randomUUID(), "fishing-private-player"));
            players.add(current);
            current.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                    new Connection(PacketFlow.SERVERBOUND), current);
            current.setPos(helper.absolutePos(new BlockPos(4, 3, 4)).getCenter());
            InteractionHand hand = offhand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack rod = new ItemStack(Items.FISHING_ROD);
            rod.enchant(Enchantments.FISHING_LUCK, 3);
            rod.getOrCreateTag().putString("private_rod_probe", "do_not_capture");
            current.setItemInHand(hand, rod);
            this.cancel = cancel;
            try {
                Items.FISHING_ROD.use(helper.getLevel(), current, hand);
                FishingHook hook = current.fishing;
                helper.assertTrue(hook != null, "The real cast must create its hook.");
                var nibble = FishingHook.class.getDeclaredField("nibble");
                nibble.setAccessible(true);
                nibble.setInt(hook, 1);
                Items.FISHING_ROD.use(helper.getLevel(), current, hand);
                helper.assertTrue(current.getStats().getValue(Stats.CUSTOM, Stats.FISH_CAUGHT) == (cancel ? 0 : 1),
                        "Enabling observation must not alter the catch statistic.");
                helper.assertTrue(rod.getDamageValue() == 1 && current.fishing == null,
                        "Observed and unobserved rod use must have the same cleanup and durability.");
            } finally {
                current = null;
                BfsConfig.COMMON.replaceVanillaMobs.set(oldReplace);
                BfsConfig.COMMON.fishEntities.set(oldLive);
            }
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public void select(ItemFishedEvent event) {
            if (event.getEntity() == current) {
                event.getDrops().clear();
                event.getDrops().add(new ItemStack(ModItems.RAW_ATLANTIC_COD));
            }
        }

        @SubscribeEvent(priority = EventPriority.LOW)
        public void cancel(ItemFishedEvent event) {
            if (event.getEntity() == current && cancel) event.setCanceled(true);
        }

        @SubscribeEvent
        public void inserted(EntityJoinLevelEvent event) {
            if (current != null && event.getLevel() == helper.getLevel()) entities.add(event.getEntity());
        }

        private void cleanup() {
            BfsDebugManager.stop("gametest_cleanup");
            MinecraftForge.EVENT_BUS.unregister(this);
            entities.forEach(Entity::discard);
            players.forEach(player -> {
                player.getAdvancements().stopListening();
                player.discard();
            });
        }
    }
}
