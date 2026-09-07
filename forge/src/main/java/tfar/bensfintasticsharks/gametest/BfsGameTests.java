package tfar.bensfintasticsharks.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.gametest.GameTestHolder;
import tfar.bensfintasticsharks.entity.BottlenoseDolphinEntity;
import tfar.bensfintasticsharks.entity.AtlanticCodEntity;
import tfar.bensfintasticsharks.entity.AtlanticSalmonEntity;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.entity.AquaticMovement;
import tfar.bensfintasticsharks.entity.OceanicWhitetipSharkEntity;
import tfar.bensfintasticsharks.entity.BlacktipReefSharkEntity;
import tfar.bensfintasticsharks.entity.SandtigerSharkEntity;
import tfar.bensfintasticsharks.entity.TigerSharkEntity;
import tfar.bensfintasticsharks.debug.BfsDebugManager;
import tfar.bensfintasticsharks.init.ModBlocks;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModItems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    @GameTest(template = "empty", batch = "bfs_baseline", timeoutTicks = 40)
    public static void algaeBreakDropsTheBrokenForm(GameTestHelper helper) {
        prepareWaterColumn(helper);
        helper.setBlock(ALGAE_POS, ModBlocks.ALGAE_BLOCK.defaultBlockState());
        helper.runAfterDelay(1, () -> {
            helper.getLevel().destroyBlock(helper.absolutePos(ALGAE_POS), true, null);
            helper.assertItemEntityPresent(ModBlocks.ALGAE_BLOCK.asItem(), ALGAE_POS, 2.0);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_armor", timeoutTicks = 40)
    public static void prismarineArmorEquipmentSurvivesWaterAndReequip(GameTestHelper helper) {
        prepareWaterVolume(helper);
        Player player = makeSurvivalTestPlayer(helper);
        player.setPos(helper.absolutePos(new BlockPos(3, 3, 3)).getCenter());
        helper.getLevel().addFreshEntity(player);
        setPrismarineArmor(player);
        helper.runAfterDelay(10, () -> {
            helper.assertTrue(player.isInWaterOrBubble(), "armor fixture must keep the player in water");
            assertPrismarineArmor(helper, player, "initial water equipment");
            player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            helper.assertTrue(player.getItemBySlot(EquipmentSlot.CHEST).isEmpty(),
                    "chest slot must empty before re-equipping");
            player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.PRISMARINE_CHESTPLATE));
            assertPrismarineArmor(helper, player, "re-equipped water armor");
            helper.succeed();
        });
    }

    private static void setPrismarineArmor(Player player) {
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.PRISMARINE_HELMET));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.PRISMARINE_CHESTPLATE));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.PRISMARINE_LEGGINGS));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.PRISMARINE_BOOTS));
    }

    private static void assertPrismarineArmor(GameTestHelper helper, Player player, String context) {
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.PRISMARINE_HELMET), context + " helmet");
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.PRISMARINE_CHESTPLATE), context + " chestplate");
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.LEGS).is(ModItems.PRISMARINE_LEGGINGS), context + " leggings");
        helper.assertTrue(player.getItemBySlot(EquipmentSlot.FEET).is(ModItems.PRISMARINE_BOOTS), context + " boots");
    }

    @GameTest(template = "empty", batch = "bfs_baseline", timeoutTicks = 40)
    public static void algaeRejectsDryAndUnsupportedPlacement(GameTestHelper helper) {
        prepareWaterColumn(helper);
        helper.setBlock(ALGAE_POS, ModBlocks.LARGE_RED_ALGAE.defaultBlockState());
        helper.runAfterDelay(1, () -> {
            BlockPos absoluteAlgaePos = helper.absolutePos(ALGAE_POS);
            helper.setBlock(SUPPORT_POS, Blocks.AIR.defaultBlockState());
            helper.assertTrue(!ModBlocks.LARGE_RED_ALGAE.defaultBlockState()
                            .canSurvive(helper.getLevel(), absoluteAlgaePos),
                    "algae must reject placement after its submerged support is removed");

            BlockPos dryPos = new BlockPos(3, 1, 1);
            BlockPos absoluteDryPos = helper.absolutePos(dryPos);
            helper.setBlock(dryPos, Blocks.AIR.defaultBlockState());
            helper.assertTrue(!ModBlocks.ALGAE_BLOCK.defaultBlockState()
                            .canSurvive(helper.getLevel(), absoluteDryPos),
                    "algae must reject placement outside a water source");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_debug_lifecycle", timeoutTicks = 20)
    public static void geckoLibNetworkChannelIsRegistered(GameTestHelper helper) {
        try {
            java.lang.reflect.Method buildChannelVersions = net.minecraftforge.network.NetworkRegistry.class
                    .getDeclaredMethod("buildChannelVersions");
            buildChannelVersions.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<net.minecraft.resources.ResourceLocation, String> channels =
                    (java.util.Map<net.minecraft.resources.ResourceLocation, String>) buildChannelVersions.invoke(null);
            helper.assertTrue("1".equals(channels.get(new net.minecraft.resources.ResourceLocation("geckolib", "main"))),
                    "GeckoLib must register its geckolib:main protocol channel with version 1");
            helper.succeed();
        } catch (ReflectiveOperationException exception) {
            helper.fail("unable to inspect registered Forge network channels: " + exception.getMessage());
        }
    }

    @GameTest(template = "empty", batch = "bfs_debug_lifecycle", timeoutTicks = 80)
    public static void serverDebugCommandStartsAndStopsBoundedCapture(GameTestHelper helper) {
        prepareWaterVolume(helper);
        helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(3, 3, 3));
        BfsDebugManager.stop("gametest_setup");
        net.minecraft.server.MinecraftServer server = helper.getLevel().getServer();
        net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack()
                .withLevel(helper.getLevel())
                .withPosition(helper.absolutePos(new BlockPos(3, 3, 3)).getCenter())
                .withPermission(4);
        try {
            int started = server.getCommands().getDispatcher().execute("bfs debug on movement 20", source);
            helper.assertTrue(started >= 0, "operator command must accept bounded BFS debug capture");
            BfsDebugManager.Status active = BfsDebugManager.status();
            helper.assertTrue(active.active(), "debug command must create one active server session");
            helper.assertTrue(active.session().targetCount() <= BfsDebugManager.MAX_TARGETS,
                    "debug target selection must remain bounded");
            helper.runAfterDelay(3, () -> {
                try {
                    int stopped = server.getCommands().getDispatcher().execute("bfs debug off", source);
                    helper.assertTrue(stopped == 1, "operator command must stop the active BFS debug capture");
                    helper.assertTrue(!BfsDebugManager.status().active(), "debug command must clear the active server session");
                    helper.succeed();
                } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
                    helper.fail("BFS debug stop command failed: " + exception.getMessage());
                }
            });
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            helper.fail("BFS debug start command failed: " + exception.getMessage());
        }
    }

    @GameTest(template = "empty", batch = "bfs_debug_permissions", timeoutTicks = 80)
    public static void serverDebugCommandKeepsOneSessionAndRejectsUntrustedSources(GameTestHelper helper) {
        prepareWaterVolume(helper);
        helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(3, 3, 3));
        BfsDebugManager.stop("gametest_setup");
        net.minecraft.server.MinecraftServer server = helper.getLevel().getServer();
        net.minecraft.commands.CommandSourceStack operator = server.createCommandSourceStack()
                .withLevel(helper.getLevel())
                .withPosition(helper.absolutePos(new BlockPos(3, 3, 3)).getCenter())
                .withPermission(4);
        net.minecraft.commands.CommandSourceStack untrusted = operator.withPermission(0);
        try {
            server.getCommands().getDispatcher().execute("bfs debug on movement 20", operator);
            BfsDebugManager.Session original = BfsDebugManager.status().session();
            helper.assertTrue(original != null, "operator command must create the diagnostic session");
            int repeated = server.getCommands().getDispatcher().execute("bfs debug on movement 20", operator);
            BfsDebugManager.Session current = BfsDebugManager.status().session();
            helper.assertTrue(repeated == 1, "repeated operator command must report the existing session");
            helper.assertTrue(current != null && current.id().equals(original.id())
                            && current.startTick() == original.startTick(),
                    "repeated enable must not replace or reset the active session");
            try {
                server.getCommands().getDispatcher().execute("bfs debug status", untrusted);
                helper.fail("untrusted sources must not access the server debug command");
                return;
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException expected) {
                // Permission-gated literal nodes are intentionally invisible to untrusted sources.
            }
            server.getCommands().getDispatcher().execute("bfs debug off", operator);
            helper.assertTrue(!BfsDebugManager.status().active(), "operator stop must release the session after a repeat");
            int repeatedStop = server.getCommands().getDispatcher().execute("bfs debug off", operator);
            helper.assertTrue(repeatedStop == 1,
                    "operator stop must be idempotent after the diagnostic session is already inactive");
            helper.assertTrue(!BfsDebugManager.status().active(),
                    "idempotent operator stop must leave the diagnostic session inactive");
            helper.succeed();
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            helper.fail("BFS debug command test failed: " + exception.getMessage());
        }
    }

    @GameTest(template = "empty", batch = "bfs_info_cards", timeoutTicks = 60)
    public static void infoCardsExposeAuthoritativeSpeciesData(GameTestHelper helper) {
        net.minecraft.server.MinecraftServer server = helper.getLevel().getServer();
        List<String> success = new ArrayList<>();
        net.minecraft.commands.CommandSource capture = new net.minecraft.commands.CommandSource() {
            @Override
            public void sendSystemMessage(net.minecraft.network.chat.Component message) {
                success.add(message.getString());
            }

            @Override
            public boolean acceptsSuccess() {
                return true;
            }

            @Override
            public boolean acceptsFailure() {
                return true;
            }

            @Override
            public boolean shouldInformAdmins() {
                return false;
            }
        };
        net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack()
                .withLevel(helper.getLevel())
                .withPosition(helper.absolutePos(new BlockPos(1, 1, 1)).getCenter())
                .withPermission(4)
                .withSource(capture);
        List<String> species = List.of(
                "orca", "bottlenose_dolphin", "common_octopus", "caribbean_reef_octopus", "nautilus",
                "giant_moray_eel", "green_sea_turtle", "american_lobster", "common_stingray", "harbor_seal",
                "black_sea_nettle_jellyfish", "cannonball_jellyfish", "oceanic_whitetip_shark",
            "atlantic_cod", "atlantic_salmon");
        try {
            for (String id : species) {
                int before = success.size();
                int result = server.getCommands().getDispatcher().execute("bfs info " + id, source);
                helper.assertTrue(result == 1, "info command must succeed for " + id);
                List<String> card = success.subList(before, success.size());
                if (List.of("orca", "bottlenose_dolphin", "common_octopus", "caribbean_reef_octopus",
                        "nautilus", "giant_moray_eel", "green_sea_turtle", "american_lobster", "common_stingray",
                        "harbor_seal", "black_sea_nettle_jellyfish", "cannonball_jellyfish").contains(id)) {
                    helper.assertTrue(card.stream().anyMatch(line -> line.contains("Diet: TBD")),
                            "retained nonshark card must expose Diet: TBD for " + id);
                }
            }
            helper.assertTrue(success.stream().anyMatch(line -> line.contains("Habitats: Deep Lukewarm Ocean, Deep Ocean")),
                    "Oceanic Whitetip must expose only its generated deep ocean habitats");
            helper.assertTrue(success.stream().anyMatch(line -> line.contains("Natural spawning: Replaces Vanilla Cod spawns")),
                    "replacement enabled Cod card must expose its replacement source");
            helper.assertTrue(success.stream().anyMatch(line -> line.contains("Natural spawning: Replaces Vanilla Salmon spawns")),
                    "replacement enabled Salmon card must expose its replacement source");

            var biomeRegistry = helper.getLevel().registryAccess()
                    .registryOrThrow(net.minecraft.core.registries.Registries.BIOME);
            helper.assertTrue(biomeRegistry.getHolderOrThrow(net.minecraft.world.level.biome.Biomes.DEEP_OCEAN)
                            .is(tfar.bensfintasticsharks.init.ModTags.Biomes.OCEANIC_WHITETIP_SHARK_SPAWNS),
                    "Oceanic Whitetip tag must admit Deep Ocean natural spawning");
            helper.assertTrue(biomeRegistry.getHolderOrThrow(net.minecraft.world.level.biome.Biomes.DEEP_LUKEWARM_OCEAN)
                            .is(tfar.bensfintasticsharks.init.ModTags.Biomes.OCEANIC_WHITETIP_SHARK_SPAWNS),
                    "Oceanic Whitetip tag must admit Deep Lukewarm Ocean natural spawning");
            helper.assertTrue(!biomeRegistry.getHolderOrThrow(net.minecraft.world.level.biome.Biomes.DEEP_COLD_OCEAN)
                            .is(tfar.bensfintasticsharks.init.ModTags.Biomes.OCEANIC_WHITETIP_SHARK_SPAWNS),
                    "Oceanic Whitetip tag must reject Deep Cold Ocean natural spawning");

            int unknown = server.getCommands().getDispatcher().execute("bfs info unknown_species", source);
            helper.assertTrue(unknown == 0 && success.stream().anyMatch(line -> line.contains("Unknown BFS species")),
                    "unknown species must return a structured failure, result=" + unknown + ", messages=" + success);
            try {
                server.getCommands().getDispatcher().execute("bfs info orca", source.withPermission(0));
                helper.fail("untrusted source must not access info cards");
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException expected) {
                // Permission-gated command nodes are intentionally invisible to untrusted sources.
            }
            helper.succeed();
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            helper.fail("BFS info command failed: " + exception.getMessage());
        }
    }

    @GameTest(template = "empty", batch = "bfs_debug_brain", timeoutTicks = 100)
    public static void serverDebugBrainCaptureRecordsSanitizedState(GameTestHelper helper) {
        prepareWaterVolume(helper);
        helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(3, 3, 3));
        BfsDebugManager.stop("gametest_setup");
        net.minecraft.server.MinecraftServer server = helper.getLevel().getServer();
        net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack()
                .withLevel(helper.getLevel())
                .withPosition(helper.absolutePos(new BlockPos(3, 3, 3)).getCenter())
                .withPermission(4);
        try {
            int started = server.getCommands().getDispatcher().execute(
                    "bfs debug on brain 30 @e[type=bensfintasticsharks:atlantic_cod,distance=..4,limit=1]", source);
            helper.assertTrue(started == 1, "brain diagnostic fixture must select exactly one Cod");
            helper.runAfterDelay(10, () -> {
                try {
                    server.getCommands().getDispatcher().execute("bfs debug off", source);
                    Path output = BfsDebugManager.status().lastStop().outputPath();
                    verifyBrainCapture(helper, output, 20);
                } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
                    helper.fail("BFS brain diagnostic stop command failed: " + exception.getMessage());
                }
            });
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            helper.fail("BFS brain diagnostic start command failed: " + exception.getMessage());
        }
    }

    private static void verifyBrainCapture(GameTestHelper helper, Path output, int remainingChecks) {
        helper.runAfterDelay(1, () -> {
            try {
                if (!Files.exists(output)) {
                    if (remainingChecks > 1) {
                        verifyBrainCapture(helper, output, remainingChecks - 1);
                    } else {
                        helper.fail("BFS brain diagnostic output was not written: " + output);
                    }
                    return;
                }
                String contents = Files.readString(output);
                if (!contents.contains("\"event\":\"brain\"")) {
                    if (remainingChecks > 1) {
                        verifyBrainCapture(helper, output, remainingChecks - 1);
                    } else {
                        helper.fail("BFS brain diagnostic output has no brain event: " + output);
                    }
                    return;
                }
                helper.assertTrue(contents.contains("\"activeActivities\":"),
                        "brain records must include active activities");
                helper.assertTrue(contents.contains("\"runningBehaviors\":"),
                        "brain records must include running behavior metadata");
                helper.assertTrue(contents.contains("\"memories\":"),
                        "brain records must include memory presence metadata");
                helper.assertTrue(!contents.contains("debugString"),
                        "brain records must not include behavior debug strings");
                helper.succeed();
            } catch (IOException exception) {
                helper.fail("unable to read BFS brain diagnostic output: " + exception.getMessage());
            } finally {
                if (remainingChecks == 1 || Files.exists(output) &&
                        contentsContainBrainEvent(output)) {
                    try {
                        Files.deleteIfExists(output);
                    } catch (IOException ignored) {
                        // Cleanup is best effort after the evidence assertions have run.
                    }
                }
            }
        });
    }

    private static boolean contentsContainBrainEvent(Path output) {
        try {
            return Files.readString(output).contains("\"event\":\"brain\"");
        } catch (IOException exception) {
            return false;
        }
    }

    @GameTest(template = "empty", batch = "bfs_debug_parity", timeoutTicks = 140)
    public static void serverDebugCaptureLeavesPairedPhysicsUnchanged(GameTestHelper helper) {
        prepareWaterVolume(helper);
        AtlanticCodEntity captured = helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(4, 5, 4));
        AtlanticCodEntity control = helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(7, 5, 4));
        captured.setNoAi(true);
        control.setNoAi(true);
        captured.setNoGravity(true);
        control.setNoGravity(true);
        Vec3 capturedStart = captured.position();
        Vec3 controlStart = control.position();

        BfsDebugManager.stop("gametest_setup");
        net.minecraft.server.MinecraftServer server = helper.getLevel().getServer();
        net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack()
                .withLevel(helper.getLevel())
                .withPosition(capturedStart)
                .withPermission(4);
        helper.runAfterDelay(1, () -> {
            try {
                int started = server.getCommands().getDispatcher().execute(
                        "bfs debug on movement 70 @e[type=bensfintasticsharks:atlantic_cod,distance=..8,sort=nearest,limit=1]", source);
                helper.assertTrue(started == 1, "diagnostic parity fixture must select only its captured Cod");
                sampleDiagnosticParity(helper, server, source, captured, control, capturedStart, controlStart, 20);
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
                helper.fail("BFS diagnostic parity start command failed: " + exception.getMessage());
            }
        });
    }

    private static void sampleDiagnosticParity(GameTestHelper helper, net.minecraft.server.MinecraftServer server,
                                               net.minecraft.commands.CommandSourceStack source,
                                               AtlanticCodEntity captured, AtlanticCodEntity control,
                                               Vec3 capturedStart, Vec3 controlStart, int remainingTicks) {
        helper.runAfterDelay(1, () -> {
            captured.move(net.minecraft.world.entity.MoverType.SELF, new Vec3(0.02D, 0.0D, 0.0D));
            control.move(net.minecraft.world.entity.MoverType.SELF, new Vec3(0.02D, 0.0D, 0.0D));
            if (remainingTicks > 1) {
                sampleDiagnosticParity(helper, server, source, captured, control, capturedStart, controlStart,
                        remainingTicks - 1);
                return;
            }
            try {
                server.getCommands().getDispatcher().execute("bfs debug off", source);
                Vec3 capturedDisplacement = captured.position().subtract(capturedStart);
                Vec3 controlDisplacement = control.position().subtract(controlStart);
                helper.assertTrue(capturedDisplacement.lengthSqr() > 1.0e-4D,
                        "paired physics fixture must apply observable scripted movement");
                helper.assertTrue(capturedDisplacement.distanceTo(controlDisplacement) <= 1.0e-4D,
                        "diagnostic capture must not change paired deterministic physics, captured="
                                + capturedDisplacement + ", control=" + controlDisplacement);
                BfsDebugManager.StopSummary stop = BfsDebugManager.status().lastStop();
                helper.assertTrue(stop.dropped() == 0 && !stop.incomplete(),
                        "diagnostic parity capture must finish without dropped or incomplete records");
                helper.succeed();
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
                helper.fail("BFS diagnostic parity stop command failed: " + exception.getMessage());
            }
        });
    }

    @GameTest(template = "empty", batch = "bfs_debug_lifecycle", timeoutTicks = 40)
    public static void fishFastSwimStateSynchronizesFromServerMotion(GameTestHelper helper) {
        prepareWaterVolume(helper);
        AtlanticCodEntity cod = helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(3, 3, 3));
        AtlanticSalmonEntity salmon = helper.spawn(ModEntityTypes.ATLANTIC_SALMON, new BlockPos(5, 3, 3));
        cod.setNoAi(true);
        salmon.setNoAi(true);
        cod.setNoGravity(true);
        salmon.setNoGravity(true);
        cod.setDeltaMovement(0.2D, 0.0D, 0.0D);
        salmon.setDeltaMovement(0.2D, 0.0D, 0.0D);

        helper.runAfterDelay(1, () -> {
            helper.assertTrue(cod.isFastSwim(),
                    "cod fast swim state must be set from server movement above the fast threshold");
            helper.assertTrue(salmon.isFastSwim(),
                    "salmon fast swim state must be set from server movement above the fast threshold");
            cod.setDeltaMovement(Vec3.ZERO);
            salmon.setDeltaMovement(Vec3.ZERO);
            helper.runAfterDelay(1, () -> {
                helper.assertTrue(!cod.isFastSwim(),
                        "cod fast swim state must clear when server movement returns below the threshold");
                helper.assertTrue(!salmon.isFastSwim(),
                        "salmon fast swim state must clear when server movement returns below the threshold");
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_debug_cod_movement", timeoutTicks = 120)
    public static void serverDebugCaptureCodDepthFixture(GameTestHelper helper) {
        captureAquaticDepthMovement(helper, ModEntityTypes.ATLANTIC_COD,
                "bensfintasticsharks:atlantic_cod");
    }

    @GameTest(template = "empty", batch = "bfs_debug_salmon_movement", timeoutTicks = 120)
    public static void serverDebugCaptureSalmonDepthFixture(GameTestHelper helper) {
        captureAquaticDepthMovement(helper, ModEntityTypes.ATLANTIC_SALMON,
                "bensfintasticsharks:atlantic_salmon");
    }

    @GameTest(template = "empty", batch = "bfs_debug_dolphin_movement", timeoutTicks = 120)
    public static void serverDebugCaptureDolphinDepthFixture(GameTestHelper helper) {
        captureAquaticDepthMovement(helper, ModEntityTypes.BOTTLENOSE_DOLPHIN,
                "bensfintasticsharks:bottlenose_dolphin");
    }

    @GameTest(template = "empty", batch = "bfs_debug_oceanic_movement", timeoutTicks = 120)
    public static void serverDebugCaptureOceanicWhitetipDepthFixture(GameTestHelper helper) {
        captureAquaticDepthMovement(helper, ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                "bensfintasticsharks:oceanic_whitetip_shark");
    }

    @GameTest(template = "empty", batch = "bfs_debug_tiger_movement", timeoutTicks = 120)
    public static void serverDebugCaptureTigerDepthFixture(GameTestHelper helper) {
        captureAquaticDepthMovement(helper, ModEntityTypes.TIGER_SHARK,
                "bensfintasticsharks:tiger_shark");
    }

    private static void captureAquaticDepthMovement(GameTestHelper helper, EntityType<? extends Mob> type,
                                                     String entityId) {
        prepareVerticalWaterVolume(helper);
        Mob aquatic = helper.spawn(type, new BlockPos(11, 3, 11));
        aquatic.getBrain().removeAllBehaviors();
        Vec3 target = aquatic.position().add(0.0D, 9.0D, 0.0D);
        aquatic.getNavigation().moveTo(target.x, target.y, target.z, 1.0D);

        BfsDebugManager.stop("gametest_setup");
        net.minecraft.server.MinecraftServer server = helper.getLevel().getServer();
        net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack()
                .withLevel(helper.getLevel())
                .withPosition(aquatic.position())
                .withPermission(4);
        try {
            String command = "bfs debug on movement 70 @e[type=" + entityId + ",distance=..4,limit=1]";
            server.getCommands().getDispatcher().execute(command, source);
            helper.assertTrue(BfsDebugManager.status().active(),
                    "depth fixture must begin a real server diagnostic capture");
            helper.assertTrue(BfsDebugManager.status().session().targetCount() == 1,
                    "depth fixture must capture only its selected aquatic entity");
            driveAquaticDepthTarget(helper, server, source, aquatic, target, 60);
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            helper.fail("BFS depth diagnostic start command failed: " + exception.getMessage());
        }
    }

    private static void driveAquaticDepthTarget(GameTestHelper helper, net.minecraft.server.MinecraftServer server,
                                                net.minecraft.commands.CommandSourceStack source, Mob aquatic,
                                                Vec3 target, int remainingTicks) {
        helper.runAfterDelay(1, () -> {
            aquatic.getNavigation().moveTo(target.x, target.y, target.z, 1.0D);
            if (aquatic instanceof AbstractSharkEntity<?> || aquatic instanceof AtlanticCodEntity
                    || aquatic instanceof AtlanticSalmonEntity) {
                double verticalLimit = aquatic.getSpeed() * AquaticMovement.VERTICAL_SPEED_RATIO + 1.0e-5D;
                helper.assertTrue(Math.abs(aquatic.getDeltaMovement().y) <= verticalLimit,
                        "depth movement must stay within the approved ten percent vertical profile, "
                                + "entity=" + aquatic.getType() + ", vertical=" + aquatic.getDeltaMovement().y
                                + ", limit=" + verticalLimit);
            }
            if (remainingTicks > 1) {
                driveAquaticDepthTarget(helper, server, source, aquatic, target, remainingTicks - 1);
                return;
            }
            try {
                server.getCommands().getDispatcher().execute("bfs debug off", source);
                helper.assertTrue(!BfsDebugManager.status().active(),
                        "depth fixture must release the diagnostic session after sampling");
                helper.succeed();
            } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
                helper.fail("BFS depth diagnostic stop command failed: " + exception.getMessage());
            }
        });
    }

    @GameTest(template = "empty", batch = "bfs_baseline", timeoutTicks = 220)
    public static void tigerSharkPursuesReachableEdibleItem(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(8, 3, 3));
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        item.setNoGravity(true);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        Vec3 start = shark.position();
        double startDistance = shark.distanceToSqr(item);
        sampleTigerItemPursuit(helper, shark, item, start, startDistance, new boolean[1], 0);
    }

    private static void clearAquaticFixtureEntities(GameTestHelper helper, BlockPos first, BlockPos second) {
        Vec3 firstCenter = helper.absolutePos(first).getCenter();
        Vec3 secondCenter = helper.absolutePos(second).getCenter();
        // Keep cleanup inside this structure. The old 26 block radius crossed adjacent
        // GameTest structures, so parallel fixtures could delete each other's sharks and
        // make combat and curiosity results depend on batch placement.
        AABB fixtureArea = new AABB(firstCenter, secondCenter).inflate(4.0D);
        helper.getLevel().getEntitiesOfClass(Entity.class, fixtureArea,
                entity -> !(entity instanceof Player)).forEach(Entity::discard);
    }

    private static void clearCombatFixtureEntities(GameTestHelper helper, BlockPos first, BlockPos second) {
        Vec3 firstCenter = helper.absolutePos(first).getCenter();
        Vec3 secondCenter = helper.absolutePos(second).getCenter();
        AABB fixtureArea = new AABB(firstCenter, secondCenter).inflate(4.0D);
        helper.getLevel().getEntitiesOfClass(Entity.class, fixtureArea,
                        entity -> !(entity instanceof net.minecraft.server.level.ServerPlayer))
                .forEach(Entity::discard);
    }

    private static void clearLocalAquaticFixtureEntities(GameTestHelper helper, BlockPos first, BlockPos second) {
        Vec3 firstCenter = helper.absolutePos(first).getCenter();
        Vec3 secondCenter = helper.absolutePos(second).getCenter();
        AABB fixtureArea = new AABB(firstCenter, secondCenter).inflate(4.0D);
        helper.getLevel().getEntitiesOfClass(LivingEntity.class, fixtureArea,
                entity -> entity.isInWater() && !(entity instanceof Player)).forEach(LivingEntity::discard);
    }

    private static void clearUnexpectedAquaticFixtureEntities(GameTestHelper helper,
                                                               TigerSharkEntity shark, ItemEntity item) {
        AABB fixtureArea = shark.getBoundingBox().minmax(item.getBoundingBox()).inflate(4.0D);
        helper.getLevel().getEntitiesOfClass(LivingEntity.class, fixtureArea,
                entity -> entity != shark && entity.isInWater() && !(entity instanceof Player))
                .forEach(LivingEntity::discard);
    }

    private static void sampleTigerItemPursuit(GameTestHelper helper, TigerSharkEntity shark,
                                                ItemEntity item, Vec3 start, double startDistance,
                                                boolean[] acquired, int sample) {
        helper.runAfterDelay(1, () -> {
            clearUnexpectedAquaticFixtureEntities(helper, shark, item);
            acquired[0] |= shark.getSharkState() == TigerSharkEntity.SharkState.CURIOUS
                    || shark.justBitItem();
            if (sample < 40) {
                sampleTigerItemPursuit(helper, shark, item, start, startDistance, acquired, sample + 1);
                return;
            }
            helper.assertTrue(acquired[0],
                    "tiger shark must acquire a reachable edible item");
            helper.assertTrue(shark.position().distanceToSqr(start) > 0.25,
                    "tiger shark must leave its spawn position while pursuing an item, state="
                            + shark.getSharkState() + ", position=" + shark.position()
                            + ", item=" + item.position() + ", navDone=" + shark.getNavigation().isDone());
            helper.assertTrue(shark.distanceToSqr(item) < startDistance,
                    "tiger shark must reduce distance to a reachable edible item, state="
                            + shark.getSharkState() + ", startDistance=" + startDistance
                            + ", finalDistance=" + shark.distanceToSqr(item)
                            + ", position=" + shark.position() + ", item=" + item.position()
                            + ", navDone=" + shark.getNavigation().isDone()
                            + ", delta=" + shark.getDeltaMovement());
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_combat_tiger_bite", timeoutTicks = 100)
    public static void tigerBiteLandsOnceAndRecoversAfterTargetLoss(GameTestHelper helper) {
        prepareWaterVolume(helper);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(4, 3, 3));
        Mob prey = helper.spawn(EntityType.DROWNED, new BlockPos(4, 3, 3));
        shark.getBrain().removeAllBehaviors();
        prey.setNoAi(true);
        shark.setTarget(prey);
        shark.setSharkState(TigerSharkEntity.SharkState.HOSTILE);
        shark.setStateTimer(100);
        float startHealth = prey.getHealth();

        helper.runAfterDelay(5, () -> {
            helper.assertTrue(prey.getHealth() == startHealth,
                    "scheduled bite must not damage the target before its impact frame");
            helper.runAfterDelay(10, () -> {
                float afterImpactHealth = prey.getHealth();
                helper.assertTrue(afterImpactHealth < startHealth,
                        "tiger shark must land one server-authoritative bite");
                helper.runAfterDelay(8, () -> {
                    helper.assertTrue(prey.getHealth() == afterImpactHealth,
                            "tiger shark must not apply duplicate damage inside the bite cooldown");
                    prey.kill();
                    helper.runAfterDelay(2, () -> {
                        helper.assertTrue(shark.getTarget() == null,
                                "tiger shark must clear a dead target");
                        helper.assertTrue(shark.getSharkState() == TigerSharkEntity.SharkState.IDLE,
                                "tiger shark must return to idle after target loss");
                        helper.succeed();
                    });
                });
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_curiosity_edible", timeoutTicks = 140)
    public static void tigerCuriosityBitesEdibleWithoutConsumingStack(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(8, 3, 3));
        ItemEntity edible = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        edible.setNoGravity(true);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        runWhenTigerCurious(helper, shark, edible, 120, () -> helper.runAfterDelay(10, () -> {
            helper.assertTrue(edible.isAlive() && edible.getItem().is(Items.COD)
                            && edible.getItem().getCount() == 1,
                    "cosmetic item bite must preserve the edible item stack");
            helper.succeed();
        }));
    }

    @GameTest(template = "empty", batch = "bfs_curiosity_non_edible", timeoutTicks = 140)
    public static void tigerCuriosityIgnoresNonEdibleItem(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(8, 3, 3));
        ItemEntity nonEdible = helper.spawnItem(Items.STONE, new BlockPos(8, 3, 3));
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        helper.runAfterDelay(80, () -> {
            helper.assertTrue(shark.getSharkState() != TigerSharkEntity.SharkState.CURIOUS,
                    "non edible item must not enter curiosity state");
            helper.assertTrue(nonEdible.isAlive() && nonEdible.getItem().is(Items.STONE)
                            && nonEdible.getItem().getCount() == 1,
                    "non edible item must remain untouched");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_curiosity_water_exit", timeoutTicks = 160)
    public static void tigerCuriosityClearsWhenItemLeavesWater(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(8, 3, 3));
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        item.setNoGravity(true);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        runWhenTigerCurious(helper, shark, item, 80, () -> {
            item.setPos(item.getX(), item.getY() + 10.0D, item.getZ());
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(!item.isInWater(), "leaving water fixture must invalidate the item");
                helper.assertTrue(shark.getSharkState() != TigerSharkEntity.SharkState.CURIOUS,
                        "leaving water must clear curiosity state");
                helper.assertTrue(shark.getNavigation().isDone(),
                        "leaving water must stop the curiosity navigation");
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_curiosity_path_failure", timeoutTicks = 300)
    public static void tigerCuriosityPathFailureAppliesRetryCooldown(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(8, 3, 3));
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        item.setNoGravity(true);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        // Item scanning retries after its bounded 100 tick empty-scan cooldown. Allow one
        // complete retry window so fixture startup timing cannot turn path cleanup into a
        // false failure.
        runWhenTigerCurious(helper, shark, item, 180, () -> {
            helper.runAfterDelay(12, () -> {
                shark.getNavigation().stop();
                helper.runAfterDelay(2, () -> {
                    helper.assertTrue(shark.getSharkState() == TigerSharkEntity.SharkState.IDLE,
                            "failed curiosity navigation must return the shark to idle");
                    helper.assertTrue(item.isAlive() && item.getItem().is(Items.COD)
                                    && item.getItem().getCount() == 1,
                            "failed curiosity navigation must preserve the item stack");
                    helper.runAfterDelay(20, () -> {
                        helper.assertTrue(shark.getSharkState() != TigerSharkEntity.SharkState.CURIOUS,
                                "the investigated item identity must remain on retry cooldown");
                        helper.succeed();
                    });
                });
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_curiosity_flee", timeoutTicks = 160)
    public static void tigerCuriosityFleePreemptionClearsInvestigation(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(8, 3, 3));
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        item.setNoGravity(true);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        runWhenTigerCurious(helper, shark, item, 80, () -> {
            AbstractSharkEntity<?> largerShark = helper.spawn(ModEntityTypes.GREAT_WHITE_SHARK,
                    new BlockPos(3, 3, 7));
            largerShark.setNoAi(true);
            shark.setBfsScale(0.85F);
            largerShark.setBfsScale(1.10F);
            shark.hurt(helper.getLevel().damageSources().mobAttack(largerShark), 1.0F);
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(shark.isFleeing(),
                        "a larger shark hit must enter the flee state");
                helper.assertTrue(shark.getTarget() == null,
                        "flee preemption must not retain an investigation target as combat target");
                helper.assertTrue(shark.getSharkState() != TigerSharkEntity.SharkState.CURIOUS,
                        "flee preemption must clear curiosity state");
                helper.assertTrue(item.isAlive() && item.getItem().getCount() == 1,
                        "flee preemption must preserve the item stack");
                helper.succeed();
            });
        });
    }

    private static void runWhenTigerCurious(GameTestHelper helper, TigerSharkEntity shark,
                                             ItemEntity item, int remainingTicks, Runnable action) {
        helper.runAfterDelay(1, () -> {
            if (shark.getSharkState() == TigerSharkEntity.SharkState.CURIOUS) {
                action.run();
                return;
            }
            if (remainingTicks <= 0) {
                helper.fail("tiger did not acquire the edible item, state=" + shark.getSharkState()
                        + ", itemAlive=" + item.isAlive() + ", itemInWater=" + item.isInWater()
                        + ", itemPos=" + item.position() + ", navDone=" + shark.getNavigation().isDone());
                return;
            }
            runWhenTigerCurious(helper, shark, item, remainingTicks - 1, action);
        });
    }

    private static void sampleTigerCuriosity(GameTestHelper helper, TigerSharkEntity shark,
                                             ItemEntity edible, boolean[] acquired, int sample) {
        helper.runAfterDelay(1, () -> {
            acquired[0] |= shark.getSharkState() == TigerSharkEntity.SharkState.CURIOUS
                    || shark.justBitItem();
            if (sample < 80) {
                sampleTigerCuriosity(helper, shark, edible, acquired, sample + 1);
                return;
            }
            helper.assertTrue(acquired[0], "tiger must acquire the reachable edible item, state="
                    + shark.getSharkState() + ", itemAlive=" + edible.isAlive()
                    + ", itemInWater=" + edible.isInWater() + ", itemPos=" + edible.position()
                    + ", navDone=" + shark.getNavigation().isDone());
            helper.assertTrue(edible.isAlive() && edible.getItem().is(Items.COD)
                            && edible.getItem().getCount() == 1,
                    "cosmetic item bite must preserve the edible item stack");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_curiosity_target_loss", timeoutTicks = 100)
    public static void tigerCuriosityClearsOnTargetAndItemLoss(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(8, 3, 3));
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        helper.getLevel().getEntitiesOfClass(LivingEntity.class, shark.getBoundingBox().inflate(64.0D),
                entity -> entity != shark && entity.isInWater() && !(entity instanceof Player))
                .forEach(LivingEntity::discard);
        item.setNoGravity(true);
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(shark.getSharkState() == TigerSharkEntity.SharkState.CURIOUS,
                    "target preemption fixture must begin while the shark is curious, state="
                            + shark.getSharkState() + ", item=" + item.position()
                            + ", navDone=" + shark.getNavigation().isDone());
            Mob prey = helper.spawn(EntityType.DROWNED, new BlockPos(8, 3, 5));
            prey.setNoAi(true);
            shark.setTarget(prey);
            helper.runAfterDelay(5, () -> {
                helper.assertTrue(shark.getTarget() == prey,
                        "combat target must remain authoritative during curiosity preemption");
                helper.assertTrue(shark.getSharkState() != TigerSharkEntity.SharkState.CURIOUS,
                        "combat target must clear curiosity state");
                helper.assertTrue(item.isAlive() && item.getItem().getCount() == 1,
                        "curiosity preemption must not consume the item");
                prey.kill();
                item.discard();
                helper.runAfterDelay(10, () -> {
                    helper.assertTrue(shark.getTarget() == null,
                            "target loss must clear the combat target, target=" + shark.getTarget()
                                    + ", state=" + shark.getSharkState());
                    helper.assertTrue(shark.getSharkState() != TigerSharkEntity.SharkState.CURIOUS,
                            "item loss must leave curiosity idle");
                    helper.succeed();
                });
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_curiosity_timeout", timeoutTicks = 400)
    public static void tigerCuriosityTimeoutRemembersItemWithoutReacquiring(GameTestHelper helper) {
        prepareWaterVolume(helper);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        item.setNoGravity(true);
        helper.runAfterDelay(225, () -> {
            helper.assertTrue(item.isAlive() && item.getItem().getCount() == 1,
                    "curiosity timeout must not consume the item");
            helper.assertTrue(shark.getTarget() == null,
                    "curiosity timeout must not create a combat target");
            helper.assertTrue(shark.getSharkState() == TigerSharkEntity.SharkState.IDLE,
                    "curiosity timeout must return the shark to idle");
            helper.runAfterDelay(50, () -> {
                helper.assertTrue(shark.getSharkState() == TigerSharkEntity.SharkState.IDLE,
                        "recent item cooldown must prevent immediate reacquisition");
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_combat_sandtiger_matrix", timeoutTicks = 720)
    public static void sandtigerBiteMatrixReachesStationaryAndMovingPrey(GameTestHelper helper) {
        prepareWaterVolume(helper);
        runBiteMatrix(helper, false, 0);
    }

    @GameTest(template = "empty", batch = "bfs_combat_blacktip_matrix", timeoutTicks = 720)
    public static void blacktipBiteMatrixReachesStationaryAndMovingPrey(GameTestHelper helper) {
        prepareWaterVolume(helper);
        runBiteMatrix(helper, true, 0);
    }

    private static void runBiteMatrix(GameTestHelper helper, boolean blacktip, int scenario) {
        if (scenario >= 6) {
            helper.succeed();
            return;
        }
        clearLocalAquaticFixtureEntities(helper, new BlockPos(2, 2, 2), new BlockPos(10, 5, 10));
        boolean moving = (scenario & 1) == 1;
        int size = scenario / 2;
        EntityType<? extends Mob> preyType = switch (size) {
            case 0 -> EntityType.TROPICAL_FISH;
            case 1 -> EntityType.COD;
            default -> EntityType.DROWNED;
        };
        AbstractSharkEntity<?> shark = blacktip
                ? helper.spawn(ModEntityTypes.BLACKTIP_REEF_SHARK, new BlockPos(3, 3, 4))
                : helper.spawn(ModEntityTypes.SANDTIGER_SHARK, new BlockPos(3, 3, 4));
        Mob prey = helper.spawn(preyType, new BlockPos(8, 3, 4));
        shark.setInvulnerable(true);
        shark.getBrain().removeAllBehaviors();
        shark.setTarget(prey);
        shark.setSharkState(AbstractSharkEntity.SharkState.HOSTILE);
        shark.setStateTimer(240);
        prey.setNoAi(true);
        prey.setNoGravity(true);
        prey.setDeltaMovement(Vec3.ZERO);
        float startHealth = prey.getHealth();
        runBiteScenario(helper, shark, prey, moving, startHealth, prey.getX() + 1.25D,
                0, -1, Double.POSITIVE_INFINITY, scenario, blacktip);
    }

    private static void runBiteScenario(GameTestHelper helper, AbstractSharkEntity<?> shark, Mob prey,
                                        boolean moving, float startHealth, double movingLimit,
                                        int sample, int impactSample, double nearestDistance,
                                        int scenario, boolean blacktip) {
        helper.runAfterDelay(1, () -> {
            if (moving && prey.isAlive() && prey.getX() < movingLimit) {
                prey.setPos(prey.getX() + 0.015D, prey.getY(), prey.getZ());
            }
            if (!moving && prey.isAlive()) {
                prey.setDeltaMovement(Vec3.ZERO);
            }
            if (prey.isAlive() && shark.getTarget() != prey) {
                shark.setTarget(prey);
                shark.setSharkState(AbstractSharkEntity.SharkState.HOSTILE);
                shark.setStateTimer(240);
            }
            if (shark.getNavigation().isDone() || shark.distanceToSqr(prey) > 9.0D) {
                shark.getNavigation().moveTo(prey, 1.0D);
            }
            double currentDistance = shark.distanceTo(prey);
            double closest = Math.min(nearestDistance, currentDistance);
            int impact = impactSample;
            if (impact < 0 && prey.getHealth() < startHealth) {
                impact = sample;
                prey.kill();
            }
            if (impact >= 0) {
                helper.assertTrue(closest < 3.5D,
                        "bite must enter physical contact range, species=" + (blacktip ? "blacktip" : "sandtiger")
                                + ", size=" + (scenario / 2) + ", moving=" + moving
                                + ", closest=" + closest);
                helper.assertTrue(impact >= 3,
                        "bite damage must be delayed after the trigger, sample=" + impact
                                + ", species=" + (blacktip ? "blacktip" : "sandtiger"));
                if (sample >= impact + 4) {
                    helper.assertTrue(shark.getTarget() == null,
                            "target loss must clear the active bite target after impact, species="
                                    + (blacktip ? "blacktip" : "sandtiger") + ", target=" + shark.getTarget());
                    runBiteMatrix(helper, blacktip, scenario + 1);
                    return;
                }
            }
            if (sample >= 180) {
                helper.fail("shark did not land one bite, species=" + (blacktip ? "blacktip" : "sandtiger")
                        + ", size=" + (scenario / 2) + ", moving=" + moving + ", health=" + prey.getHealth()
                        + ", position=" + shark.position() + ", prey=" + prey.position()
                        + ", distance=" + currentDistance + ", navDone=" + shark.getNavigation().isDone()
                        + ", target=" + shark.getTarget() + ", state=" + shark.getSharkState()
                        + ", sharkWidth=" + shark.getBbWidth() + ", preyWidth=" + prey.getBbWidth()
                        + ", preyAlive=" + prey.isAlive() + ", preyInWater=" + prey.isInWater()
                        + ", delta=" + shark.getDeltaMovement());
                return;
            }
            runBiteScenario(helper, shark, prey, moving, startHealth, movingLimit,
                    sample + 1, impact, closest, scenario, blacktip);
        });
    }

    @GameTest(template = "empty", batch = "bfs_combat_oceanic_grab", timeoutTicks = 1100)
    public static void oceanicWhitetipGrabDamagesAndReleasesPassenger(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearCombatFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(6, 3, 3));
        OceanicWhitetipSharkEntity shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                new BlockPos(4, 3, 3));
        Player prey = makeSurvivalTestPlayer(helper);
        prey.setPos(helper.absolutePos(new BlockPos(5, 3, 3)).getCenter());
        prey.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(1000.0D);
        prey.setHealth(1000.0F);
        helper.getLevel().addFreshEntity(prey);
        shark.getBrain().removeAllBehaviors();
        shark.setTarget(prey);
        float startHealth = prey.getHealth();
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(shark.getTarget() == prey,
                    "oceanic whitetip fixture must retain its target identity");
            helper.assertTrue(shark.isInWaterOrBubble(),
                    "oceanic whitetip fixture must place the shark in water");
            helper.assertTrue(!prey.isPassenger(),
                    "oceanic whitetip fixture target must start without a vehicle");
            runOceanicBiteUntilGrab(helper, shark, prey, startHealth, 0);
        });
    }

    @GameTest(template = "empty", batch = "bfs_combat_blacktip_latch", timeoutTicks = 1400)
    public static void blacktipBiteStartsLatchWithoutPeriodicDamage(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearCombatFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(6, 3, 3));
        Entity[] allowedAttacker = new Entity[1];
        BlacktipReefSharkEntity shark = helper.spawn(ModEntityTypes.BLACKTIP_REEF_SHARK,
                new BlockPos(4, 3, 3));
        Player player = makeSurvivalTestPlayer(helper, allowedAttacker);
        allowedAttacker[0] = shark;
        player.setPos(helper.absolutePos(new BlockPos(5, 3, 3)).getCenter());
        player.setNoGravity(true);
        player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH).setBaseValue(1000.0D);
        player.setHealth(1000.0F);
        helper.getLevel().addFreshEntity(player);
        shark.getBrain().removeAllBehaviors();
        shark.setTarget(player);
        float[] initialHealth = {player.getHealth()};
        runBlacktipBiteUntilLatch(helper, shark, player, initialHealth, 0);
    }

    @GameTest(template = "empty", batch = "bfs_combat_oceanic_invalidation", timeoutTicks = 80)
    public static void oceanicGrabReleasesOnTargetInvalidation(GameTestHelper helper) {
        prepareWaterVolume(helper);
        OceanicWhitetipSharkEntity shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                new BlockPos(4, 3, 3));
        Player player = makeSurvivalTestPlayer(helper);
        player.setPos(helper.absolutePos(new BlockPos(5, 3, 3)).getCenter());
        helper.getLevel().addFreshEntity(player);
        helper.runAfterDelay(2, () -> {
            armOceanicGrab(shark, player);
            helper.assertTrue(player.isPassenger() && shark.getGrabTimer() > 0,
                    "oceanic grab lifecycle fixture must be armed");
            player.stopRiding();
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(shark.getGrabTimer() == 0 && !player.isPassenger()
                                && shark.getPassengers().isEmpty(),
                        "target invalidation must release the oceanic grab");
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_combat_oceanic_water_exit", timeoutTicks = 80)
    public static void oceanicGrabReleasesWhenSharkLeavesWater(GameTestHelper helper) {
        prepareWaterVolume(helper);
        OceanicWhitetipSharkEntity shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                new BlockPos(4, 3, 3));
        Player player = makeSurvivalTestPlayer(helper);
        player.setPos(helper.absolutePos(new BlockPos(5, 3, 3)).getCenter());
        helper.getLevel().addFreshEntity(player);
        helper.runAfterDelay(2, () -> {
            armOceanicGrab(shark, player);
            helper.assertTrue(player.isPassenger() && shark.getGrabTimer() > 0,
                    "oceanic grab water fixture must be armed");
            shark.setPos(shark.getX(), helper.absolutePos(new BlockPos(4, 9, 3)).getY(), shark.getZ());
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(shark.getGrabTimer() == 0 && !player.isPassenger()
                                && shark.getPassengers().isEmpty(),
                        "leaving water must release the oceanic grab");
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_combat_oceanic_player_death", timeoutTicks = 80)
    public static void oceanicGrabReleasesWhenPlayerDies(GameTestHelper helper) {
        prepareWaterVolume(helper);
        OceanicWhitetipSharkEntity shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                new BlockPos(4, 3, 3));
        Player player = makeSurvivalTestPlayer(helper);
        player.setPos(helper.absolutePos(new BlockPos(5, 3, 3)).getCenter());
        helper.getLevel().addFreshEntity(player);
        helper.runAfterDelay(2, () -> {
            armOceanicGrab(shark, player);
            helper.assertTrue(player.isPassenger() && shark.getGrabTimer() > 0,
                    "oceanic grab death fixture must be armed");
            player.kill();
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(shark.getGrabTimer() == 0 && !player.isPassenger()
                                && shark.getPassengers().isEmpty(),
                        "player death must release the oceanic grab");
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_combat_oceanic_removal", timeoutTicks = 80)
    public static void oceanicGrabReleasesWhenSharkIsRemoved(GameTestHelper helper) {
        prepareWaterVolume(helper);
        OceanicWhitetipSharkEntity shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                new BlockPos(4, 3, 3));
        Player player = makeSurvivalTestPlayer(helper);
        player.setPos(helper.absolutePos(new BlockPos(5, 3, 3)).getCenter());
        helper.getLevel().addFreshEntity(player);
        helper.runAfterDelay(2, () -> {
            armOceanicGrab(shark, player);
            helper.assertTrue(player.isPassenger() && shark.getGrabTimer() > 0,
                    "oceanic grab removal fixture must be armed");
            shark.remove(Entity.RemovalReason.DISCARDED);
            helper.runAfterDelay(2, () -> {
                helper.assertTrue(!player.isPassenger(),
                        "shark removal must release the passenger");
                helper.succeed();
            });
        });
    }

    private static void armOceanicGrab(OceanicWhitetipSharkEntity shark, Player player) {
        shark.getBrain().removeAllBehaviors();
        shark.setTarget(player);
        shark.grabMob(player);
    }

    private static void runOceanicBiteUntilGrab(GameTestHelper helper, OceanicWhitetipSharkEntity shark,
                                                 Player prey, float startHealth, int sample) {
        helper.runAfterDelay(1, () -> {
            if (!prey.isAlive()) {
                helper.fail("oceanic whitetip prey died before grab, health=" + prey.getHealth());
                return;
            }
            Vec3 anchor = helper.absolutePos(new BlockPos(4, 3, 3)).getCenter();
            shark.setPos(anchor.x, anchor.y, anchor.z);
            shark.setDeltaMovement(Vec3.ZERO);
            shark.setTarget(prey);
            shark.setSharkState(AbstractSharkEntity.SharkState.HOSTILE);
            shark.setStateTimer(240);
            prey.setPos(shark.getX() + 0.9D, shark.getY(), shark.getZ());
            prey.setDeltaMovement(Vec3.ZERO);
            if (prey.isPassenger() && shark.getGrabTimer() > 0) {
                helper.assertTrue(prey.isPassenger(),
                        "oceanic whitetip real bite must create the passenger relationship");
                helper.runAfterDelay(12, () -> {
                    float damagedHealth = prey.getHealth();
                    helper.assertTrue(shark.getGrabTimer() > 0,
                            "oceanic whitetip must expose an active grab timer");
                    helper.runAfterDelay(10, () -> {
                        helper.assertTrue(prey.getHealth() < damagedHealth,
                                "oceanic whitetip thrash must deal server authoritative damage");
                        helper.runAfterDelay(90, () -> {
                            helper.assertTrue(shark.getGrabTimer() == 0,
                                    "oceanic whitetip grab timer must expire");
                            helper.assertTrue(!prey.isPassenger() && shark.getPassengers().isEmpty(),
                                    "oceanic whitetip must release its passenger when the grab expires");
                            helper.succeed();
                        });
                    });
                });
                return;
            }
            if (sample >= 900) {
                helper.fail("oceanic whitetip did not start a grab after real bites, health="
                        + prey.getHealth() + ", target=" + shark.getTarget());
                return;
            }
            runOceanicBiteUntilGrab(helper, shark, prey, startHealth, sample + 1);
        });
    }

    private static void runBlacktipBiteUntilLatch(GameTestHelper helper, BlacktipReefSharkEntity shark,
                                                   Player player, float[] initialHealth, int sample) {
        helper.runAfterDelay(1, () -> {
            if (!player.isAlive()) {
                helper.fail("blacktip player fixture died before latch");
                return;
            }
            Vec3 anchor = helper.absolutePos(new BlockPos(4, 3, 3)).getCenter();
            shark.setPos(anchor.x, anchor.y, anchor.z);
            shark.setDeltaMovement(Vec3.ZERO);
            shark.setTarget(player);
            shark.setSharkState(AbstractSharkEntity.SharkState.HOSTILE);
            shark.setStateTimer(240);
            player.setPos(shark.getX() + 0.9D, shark.getY(), shark.getZ());
            player.setDeltaMovement(Vec3.ZERO);
            if (player.isPassenger() && shark.getGrabTimer() > 0) {
                helper.assertTrue(player.getHealth() < initialHealth[0],
                        "blacktip real bite must deal initial latch damage");
                initialHealth[0] = player.getHealth();
                shark.getBrain().removeAllBehaviors();
                shark.getNavigation().stop();
                shark.setTarget(null);
                helper.runAfterDelay(10, () -> {
                            helper.assertTrue(player.getHealth() >= initialHealth[0] - 0.001F,
                                    "blacktip latch must not deal periodic damage after the initial bite, health="
                                    + player.getHealth() + ", timer=" + shark.getGrabTimer()
                                    + ", initial=" + initialHealth[0] + ", passenger=" + player.isPassenger());
                    helper.runAfterDelay(25, () -> {
                        helper.assertTrue(shark.getGrabTimer() == 0,
                                "blacktip latch timer must expire");
                        helper.assertTrue(!player.isPassenger() && shark.getPassengers().isEmpty(),
                                "blacktip latch must release its passenger when the timer expires");
                        helper.succeed();
                    });
                });
                return;
            }
            if (player.getHealth() < initialHealth[0]) initialHealth[0] = player.getHealth();
            if (sample >= 1100) {
                helper.fail("blacktip did not start a latch after real bites, health="
                        + player.getHealth() + ", target=" + shark.getTarget());
                return;
            }
            runBlacktipBiteUntilLatch(helper, shark, player, initialHealth, sample + 1);
        });
    }

    private static Player makeSurvivalTestPlayer(GameTestHelper helper) {
        return makeSurvivalTestPlayer(helper, null);
    }

    private static Player makeSurvivalTestPlayer(GameTestHelper helper, Entity[] allowedAttacker) {
        return new Player(helper.getLevel(), BlockPos.ZERO, 0.0F,
                new GameProfile(java.util.UUID.randomUUID(), "test-survival-player")) {
            @Override
            public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
                if (allowedAttacker != null && source.getEntity() instanceof AbstractSharkEntity
                        && source.getEntity() != allowedAttacker[0]) {
                    return false;
                }
                return super.hurt(source, amount);
            }

            @Override
            public boolean isCreative() {
                return false;
            }

            @Override
            public boolean isSpectator() {
                return false;
            }
        };
    }

    @GameTest(template = "empty", batch = "bfs_movement", timeoutTicks = 320)
    public static void sharkVerticalRouteFollowsDolphinWithoutOrbit(GameTestHelper helper) {
        runVerticalRoute(helper, new BlockPos(4, 5, 4), new BlockPos(4, 9, 4),
                new BlockPos(20, 5, 20), new BlockPos(20, 9, 20), 1);
    }

    @GameTest(template = "empty", batch = "bfs_movement", timeoutTicks = 320)
    public static void sharkDescendingRouteFollowsDolphinWithoutOrbit(GameTestHelper helper) {
        runVerticalRoute(helper, new BlockPos(4, 9, 4), new BlockPos(4, 5, 4),
                new BlockPos(20, 9, 20), new BlockPos(20, 5, 20), -1);
    }

    @GameTest(template = "empty", batch = "bfs_movement", timeoutTicks = 320)
    public static void atlanticCodVerticalRouteUsesScaledPitch(GameTestHelper helper) {
        prepareVerticalWaterVolume(helper);
        AtlanticCodEntity cod = helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(4, 5, 4));
        cod.getBrain().removeAllBehaviors();
        cod.goalSelector.removeAllGoals(goal -> true);
        cod.targetSelector.removeAllGoals(goal -> true);
        Vec3 target = helper.absolutePos(new BlockPos(4, 8, 4)).getCenter();
        java.util.List<Double> heights = new java.util.ArrayList<>();
        java.util.List<Float> pitches = new java.util.ArrayList<>();
        double startY = cod.getY();
        cod.getNavigation().moveTo(target.x, target.y, target.z, 1.0D);
        sampleFishVerticalRoute(helper, cod, target, startY, heights, pitches, 0);
    }

    @GameTest(template = "empty", batch = "bfs_movement", timeoutTicks = 320)
    public static void bottlenoseDolphinPitchTransitionsSmoothly(GameTestHelper helper) {
        prepareVerticalWaterVolume(helper);
        BottlenoseDolphinEntity dolphin = helper.spawn(ModEntityTypes.BOTTLENOSE_DOLPHIN, new BlockPos(4, 5, 4));
        dolphin.getBrain().removeAllBehaviors();
        Vec3 target = helper.absolutePos(new BlockPos(4, 8, 4)).getCenter();
        java.util.List<Float> pitches = new java.util.ArrayList<>();
        double startY = dolphin.getY();
        setVerticalTarget(dolphin, target);
        sampleDolphinPitchRoute(helper, dolphin, target, startY, pitches, 0);
    }

    private static void sampleDolphinPitchRoute(GameTestHelper helper, BottlenoseDolphinEntity dolphin,
                                                 Vec3 target, double startY, java.util.List<Float> pitches, int sample) {
        helper.runAfterDelay(1, () -> {
            pitches.add(dolphin.getXRot());
            if (sample < 260 && dolphin.distanceToSqr(target) > 0.36) {
                setVerticalTarget(dolphin, target);
                sampleDolphinPitchRoute(helper, dolphin, target, startY, pitches, sample + 1);
                return;
            }
            helper.assertTrue(dolphin.getY() - startY > 0.25,
                    "bottlenose dolphin must make directed vertical progress, position=" + dolphin.position());
            helper.assertTrue(minimumWrappedPitch(pitches) < -1.0F,
                    "bottlenose dolphin must pitch its nose toward the elevated target, pitches=" + pitches);
            helper.assertTrue(maxPitchStep(pitches) <= AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK + 0.0001F,
                    "bottlenose dolphin pitch must transition smoothly, maxStep=" + maxPitchStep(pitches));
            helper.succeed();
        });
    }

    private static void runVerticalRoute(GameTestHelper helper,
                                         BlockPos sharkStartPos, BlockPos sharkTargetPos,
                                         BlockPos dolphinStartPos, BlockPos dolphinTargetPos,
                                         int verticalDirection) {
        prepareVerticalWaterVolume(helper);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, sharkStartPos);
        BottlenoseDolphinEntity dolphin = helper.spawn(ModEntityTypes.BOTTLENOSE_DOLPHIN, dolphinStartPos);
        shark.getBrain().removeAllBehaviors();
        dolphin.getBrain().removeAllBehaviors();
        Vec3 sharkTarget = helper.absolutePos(sharkTargetPos).getCenter();
        Vec3 dolphinTarget = helper.absolutePos(dolphinTargetPos).getCenter();
        setVerticalTarget(shark, sharkTarget);
        setVerticalTarget(dolphin, dolphinTarget);
        double sharkStartY = shark.getY();
        double dolphinStartY = dolphin.getY();
        double sharkStartX = shark.getX();
        double sharkStartZ = shark.getZ();
        double dolphinStartX = dolphin.getX();
        double dolphinStartZ = dolphin.getZ();
        java.util.List<Double> sharkHeights = new java.util.ArrayList<>();
        java.util.List<Double> dolphinHeights = new java.util.ArrayList<>();
        java.util.List<Double> sharkHorizontalOffsets = new java.util.ArrayList<>();
        java.util.List<Double> dolphinHorizontalOffsets = new java.util.ArrayList<>();
        java.util.List<Float> sharkPitches = new java.util.ArrayList<>();
        java.util.List<Double> sharkVerticalSpeeds = new java.util.ArrayList<>();

        sampleVerticalRoute(helper, shark, dolphin, sharkTarget, dolphinTarget,
                sharkStartY, dolphinStartY, sharkStartX, sharkStartZ, dolphinStartX, dolphinStartZ,
                sharkHeights, dolphinHeights, sharkHorizontalOffsets, dolphinHorizontalOffsets,
                sharkPitches, sharkVerticalSpeeds,
                verticalDirection, 0, -1);
    }

    private static void sampleVerticalRoute(GameTestHelper helper, Mob shark, Mob dolphin,
                                             Vec3 sharkTarget, Vec3 dolphinTarget,
                                             double sharkStartY, double dolphinStartY,
                                             double sharkStartX, double sharkStartZ,
                                             double dolphinStartX, double dolphinStartZ,
                                             java.util.List<Double> sharkHeights,
                                             java.util.List<Double> dolphinHeights,
                                             java.util.List<Double> sharkHorizontalOffsets,
                                             java.util.List<Double> dolphinHorizontalOffsets,
                                             java.util.List<Float> sharkPitches,
                                             java.util.List<Double> sharkVerticalSpeeds,
                                             int verticalDirection,
                                             int sample,
                                             int dolphinArrivalSample) {
        final int[] arrival = {dolphinArrivalSample};
        helper.runAfterDelay(1, () -> {
            if (arrival[0] < 0 && dolphin.distanceToSqr(dolphinTarget) <= 0.36) {
                arrival[0] = sample;
            }
            sharkHeights.add(shark.getY());
            dolphinHeights.add(dolphin.getY());
            sharkHorizontalOffsets.add(Math.hypot(shark.getX() - sharkStartX, shark.getZ() - sharkStartZ));
            dolphinHorizontalOffsets.add(Math.hypot(dolphin.getX() - dolphinStartX, dolphin.getZ() - dolphinStartZ));
            sharkPitches.add(Mth.wrapDegrees(shark.getXRot()));
            sharkVerticalSpeeds.add(Math.abs(shark.getDeltaMovement().y));
            if (sample < 260) {
                setVerticalTarget(shark, sharkTarget);
                if (arrival[0] < 0) {
                    setVerticalTarget(dolphin, dolphinTarget);
                }
                sampleVerticalRoute(helper, shark, dolphin, sharkTarget, dolphinTarget,
                        sharkStartY, dolphinStartY, sharkStartX, sharkStartZ, dolphinStartX, dolphinStartZ,
                        sharkHeights, dolphinHeights, sharkHorizontalOffsets, dolphinHorizontalOffsets,
                        sharkPitches, sharkVerticalSpeeds,
                        verticalDirection, sample + 1, arrival[0]);
                return;
            }

            double sharkProgress = shark.getY() - sharkStartY;
            double dolphinProgress = dolphin.getY() - dolphinStartY;
            helper.assertTrue(dolphinProgress * verticalDirection > 0.25,
                    "bottlenose dolphin must complete the vertical reference route, progress="
                            + dolphinProgress + ", position=" + dolphin.position()
                            + ", target=" + dolphinTarget + ", navDone=" + dolphin.getNavigation().isDone()
                            + ", delta=" + dolphin.getDeltaMovement());
            helper.assertTrue(sharkProgress * verticalDirection > 0.25,
                    "shark must complete the vertical route, progress=" + sharkProgress
                            + ", dolphinProgress=" + dolphinProgress + ", position=" + shark.position()
                            + ", target=" + sharkTarget + ", navTarget=" + shark.getNavigation().getTargetPos()
                            + ", pitch=" + shark.getXRot() + ", delta=" + shark.getDeltaMovement());
            helper.assertTrue(hasSingleFiniteHorizontalArc(sharkHorizontalOffsets),
                    "shark must use one finite entry arc instead of sustained horizontal orbit, reversals="
                            + directionReversals(sharkHorizontalOffsets) + ", offsets=" + sharkHorizontalOffsets
                            + ", position=" + shark.position() + ", target=" + sharkTarget
                            + ", navTarget=" + shark.getNavigation().getTargetPos());
            helper.assertTrue(sharkPitches.stream().mapToDouble(Float::doubleValue)
                            .map(Math::abs).max().orElse(0.0) > 1.0,
                    "shark must pitch toward the vertical target, pitches=" + sharkPitches);
            helper.assertTrue(maxPitchStep(sharkPitches)
                            <= AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK + 0.0001F,
                    "shark pitch must transition smoothly, maxStep=" + maxPitchStep(sharkPitches));
            helper.assertTrue(max(sharkVerticalSpeeds)
                            <= shark.getSpeed() * AquaticMovement.VERTICAL_SPEED_RATIO + 0.0001D,
                    "shark powered vertical speed must remain at the ten percent cap, max="
                            + max(sharkVerticalSpeeds) + ", cap="
                            + shark.getSpeed() * AquaticMovement.VERTICAL_SPEED_RATIO);
            helper.assertTrue(hasNoDirectionReversal(sharkHeights, verticalDirection),
                    "shark vertical travel must not repeatedly reverse direction, reversals="
                            + directionReversals(sharkHeights) + ", heights=" + sharkHeights);
            helper.succeed();
        });
    }

    private static void sampleFishVerticalRoute(GameTestHelper helper, AtlanticCodEntity cod,
                                                 Vec3 target, double startY,
                                                 java.util.List<Double> heights,
                                                 java.util.List<Float> pitches, int sample) {
        helper.runAfterDelay(1, () -> {
            heights.add(cod.getY());
            pitches.add(cod.getXRot());
            if (sample < 260 && cod.distanceToSqr(target) > 0.36) {
                cod.getNavigation().moveTo(target.x, target.y, target.z, 1.0D);
                sampleFishVerticalRoute(helper, cod, target, startY, heights, pitches, sample + 1);
                return;
            }
            double progress = cod.getY() - startY;
            float peakPitch = minimumWrappedPitch(pitches);
            helper.assertTrue(progress > 0.25,
                    "atlantic cod must make directed vertical progress, progress=" + progress);
            helper.assertTrue(peakPitch < -1.0F,
                    "atlantic cod must pitch its nose toward the elevated target, peakPitch=" + peakPitch
                            + ", finalPitch=" + cod.getXRot());
            helper.assertTrue(maxPitchStep(pitches) <= AquaticMovement.MAX_PITCH_STEP_DEGREES_PER_TICK + 0.0001F,
                    "atlantic cod pitch must transition smoothly, maxStep=" + maxPitchStep(pitches));
            helper.assertTrue(AquaticMovement.VERTICAL_SPEED_RATIO == 0.10D,
                    "affected aquatic vertical ratio must remain the approved oracle");
            helper.assertTrue(hasNoDirectionReversal(heights, 1),
                    "atlantic cod vertical travel must not reverse direction, reversals="
                            + directionReversals(heights) + ", heights=" + heights + ", pitches=" + pitches
                            + ", position=" + cod.position() + ", delta=" + cod.getDeltaMovement()
                            + ", target=" + target);
            helper.succeed();
        });
    }

    private static float maxPitchStep(java.util.List<Float> pitches) {
        float max = 0.0F;
        for (int i = 1; i < pitches.size(); i++) {
            max = Math.max(max, Math.abs(Mth.wrapDegrees(pitches.get(i) - pitches.get(i - 1))));
        }
        return max;
    }

    private static float minimumWrappedPitch(java.util.List<Float> pitches) {
        float minimum = 0.0F;
        for (float pitch : pitches) {
            minimum = Math.min(minimum, Mth.wrapDegrees(pitch));
        }
        return minimum;
    }

    private static void setVerticalTarget(Mob mob, Vec3 target) {
        mob.getNavigation().moveTo(target.x, target.y, target.z, 1.0D);
    }

    private static boolean hasNoDirectionReversal(java.util.List<Double> heights, int direction) {
        int forward = 0;
        int negative = 0;
        for (int i = 1; i < heights.size(); i++) {
            double directedDelta = (heights.get(i) - heights.get(i - 1)) * direction;
            if (directedDelta > 1.0e-4) forward++;
            if (directedDelta < -1.0e-4) negative++;
        }
        return forward > 5 && negative == 0;
    }

    private static int directionReversals(java.util.List<Double> heights) {
        int reversals = 0;
        int lastSign = 0;
        for (int i = 1; i < heights.size(); i++) {
            double delta = heights.get(i) - heights.get(i - 1);
            int sign = delta > 1.0e-4 ? 1 : delta < -1.0e-4 ? -1 : 0;
            if (sign != 0 && lastSign != 0 && sign != lastSign) reversals++;
            if (sign != 0) lastSign = sign;
        }
        return reversals;
    }

    private static boolean hasSingleFiniteHorizontalArc(java.util.List<Double> offsets) {
        if (offsets.size() < 12 || significantDirectionReversals(offsets, 0.02D) > 2) return false;
        int tailStart = offsets.size() - 12;
        double tailMin = offsets.subList(tailStart, offsets.size()).stream()
                .mapToDouble(Double::doubleValue).min().orElse(Double.NaN);
        double tailMax = offsets.subList(tailStart, offsets.size()).stream()
                .mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
        return tailMax - tailMin <= 1.0e-6D;
    }

    private static int significantDirectionReversals(java.util.List<Double> values, double minimumDelta) {
        int reversals = 0;
        int lastSign = 0;
        for (int i = 1; i < values.size(); i++) {
            double delta = values.get(i) - values.get(i - 1);
            if (Math.abs(delta) <= minimumDelta) continue;
            int sign = delta > 0.0D ? 1 : -1;
            if (lastSign != 0 && sign != lastSign) reversals++;
            lastSign = sign;
        }
        return reversals;
    }

    private static double max(java.util.List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
    }

    private static void prepareVerticalWaterVolume(GameTestHelper helper) {
        for (int x = 1; x <= 24; x++) {
            for (int z = 1; z <= 24; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.SAND.defaultBlockState());
                for (int y = 1; y <= 20; y++) {
                    helper.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState());
                }
            }
        }
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
