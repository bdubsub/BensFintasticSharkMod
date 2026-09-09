package tfar.bensfintasticsharks.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.Direction;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.network.chat.Component;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import tfar.bensfintasticsharks.entity.BottlenoseDolphinEntity;
import tfar.bensfintasticsharks.entity.AtlanticCodEntity;
import tfar.bensfintasticsharks.entity.AtlanticSalmonEntity;
import tfar.bensfintasticsharks.BensFintasticSharks;
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
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.fishing.FishingCatchPolicy;
import tfar.bensfintasticsharks.spawn.MobCapManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Server safe smoke fixtures for the shared Phase 000 harness. */
@GameTestHolder("bensfintasticsharks")
public final class BfsGameTests {

    private static final BlockPos ALGAE_POS = new BlockPos(1, 1, 1);
    private static final BlockPos SUPPORT_POS = new BlockPos(1, 0, 1);
    private static final int POPULATION_SOAK_TICKS = 24_000;

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
            finishAfterRemovingTestPlayer(helper, player);
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

    @GameTest(template = "empty", batch = "bfs_debug_population", timeoutTicks = 1_300)
    public static void serverDebugPopulationCaptureRecordsLoadedCounts(GameTestHelper helper) {
        prepareWaterVolume(helper);
        helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(3, 3, 3));
        BfsDebugManager.stop("gametest_setup");
        net.minecraft.server.MinecraftServer server = helper.getLevel().getServer();
        net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack()
                .withLevel(helper.getLevel())
                .withPosition(helper.absolutePos(new BlockPos(3, 3, 3)).getCenter())
                .withPermission(4);
        try {
            int started = server.getCommands().getDispatcher().execute("bfs debug on population 1200", source);
            helper.assertTrue(started >= 0, "population capture must start through the operator debug command");
            helper.runAfterDelay(1_204, () -> {
                helper.assertTrue(!BfsDebugManager.status().active(),
                        "population capture must stop after its requested tick duration");
                verifyPopulationCapture(helper, BfsDebugManager.status().lastStop().outputPath(), 20, 1_200);
            });
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            helper.fail("BFS population diagnostic start command failed: " + exception.getMessage());
        }
    }

    private static void verifyPopulationCapture(GameTestHelper helper, Path output, int remainingChecks,
                                                int expectedFinalOffsetTicks) {
        helper.runAfterDelay(1, () -> {
            try {
                if (!Files.exists(output)) {
                    retryPopulationCapture(helper, output, remainingChecks, expectedFinalOffsetTicks);
                    return;
                }
                String contents = Files.readString(output);
                if (!contents.contains("\"event\":\"population_sample\"")) {
                    retryPopulationCapture(helper, output, remainingChecks, expectedFinalOffsetTicks);
                    return;
                }
                helper.assertTrue(contents.contains("\"sampleIntervalTicks\":1200"),
                        "population records must declare the fixed 1,200 tick sample interval");
                helper.assertTrue(contents.contains("\"loadedWaterAmbientEntities\":"),
                        "population records must report loaded water ambient counts");
                helper.assertTrue(contents.contains("\"loadedAtlanticCodEntities\":"),
                        "population records must report Atlantic Cod counts");
                helper.assertTrue(contents.contains("\"replaceVanillaMobs\":"),
                        "population records must identify the active replacement mode");
                helper.assertTrue(contents.contains("\"sampleOffsetTicks\":" + expectedFinalOffsetTicks),
                        "population records must include the final requested tick sample");
                Files.deleteIfExists(output);
                helper.succeed();
            } catch (IOException exception) {
                helper.fail("unable to read BFS population diagnostic output: " + exception.getMessage());
            }
        });
    }

    private static void retryPopulationCapture(GameTestHelper helper, Path output, int remainingChecks,
                                               int expectedFinalOffsetTicks) {
        if (remainingChecks > 1) {
            verifyPopulationCapture(helper, output, remainingChecks - 1, expectedFinalOffsetTicks);
            return;
        }
        helper.fail("BFS population diagnostic output has no population sample: " + output);
    }

    @GameTest(template = "empty", batch = "bfs_population_soak", timeoutTicks = 50_000)
    public static void naturalFishReplacementPopulationRemainsBoundedInBothModes(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearPopulationSoakFish(helper);
        boolean previousReplacement = BfsConfig.COMMON.replaceVanillaMobs.get();
        runPopulationSoak(helper, true, previousReplacement);
    }

    private static void runPopulationSoak(GameTestHelper helper, boolean replacementEnabled,
                                          boolean previousReplacement) {
        BfsConfig.COMMON.replaceVanillaMobs.set(replacementEnabled);
        BfsDebugManager.stop("population_soak_setup");
        net.minecraft.server.MinecraftServer server = helper.getLevel().getServer();
        net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack()
                .withLevel(helper.getLevel())
                .withPosition(helper.absolutePos(new BlockPos(4, 3, 4)).getCenter())
                .withPermission(4);
        try {
            int started = server.getCommands().getDispatcher().execute(
                    "bfs debug on population " + POPULATION_SOAK_TICKS, source);
            helper.assertTrue(started >= 0, "population soak must start through the operator debug command");
            for (int wave = 0; wave < 8; wave++) {
                int scheduledWave = wave;
                helper.runAfterDelay(1 + wave * BfsDebugManager.POPULATION_SAMPLE_INTERVAL_TICKS,
                        () -> spawnPopulationSoakWave(helper, scheduledWave));
            }
            helper.runAfterDelay(POPULATION_SOAK_TICKS + 4,
                    () -> verifyPopulationSoak(helper, BfsDebugManager.status().lastStop().outputPath(),
                            replacementEnabled, previousReplacement, 20));
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException exception) {
            BfsConfig.COMMON.replaceVanillaMobs.set(previousReplacement);
            helper.fail("BFS population soak command failed: " + exception.getMessage());
        }
    }

    private static void spawnPopulationSoakWave(GameTestHelper helper, int wave) {
        BlockPos codPosition = helper.absolutePos(new BlockPos(2 + (wave % 4) * 2, 3, 2 + (wave / 4) * 4));
        BlockPos salmonPosition = codPosition.east();
        spawnVanillaFishForWorldGeneration(helper, EntityType.COD, codPosition, MobSpawnType.NATURAL);
        preservePopulationSoakFish(helper, codPosition);
        spawnVanillaFishForWorldGeneration(helper, EntityType.SALMON, salmonPosition, MobSpawnType.NATURAL);
        preservePopulationSoakFish(helper, salmonPosition);
    }

    private static void preservePopulationSoakFish(GameTestHelper helper, BlockPos position) {
        helper.getLevel().getEntitiesOfClass(Mob.class, new AABB(position).inflate(0.25D),
                        BfsGameTests::isPopulationSoakFish)
                .forEach(fish -> {
                    fish.setPersistenceRequired();
                    fish.setNoAi(true);
                });
    }

    private static void verifyPopulationSoak(GameTestHelper helper, Path output, boolean replacementEnabled,
                                             boolean previousReplacement, int remainingChecks) {
        helper.runAfterDelay(1, () -> {
            try {
                if (!Files.exists(output)) {
                    retryPopulationSoak(helper, output, replacementEnabled, previousReplacement, remainingChecks);
                    return;
                }
                List<JsonObject> samples = Files.readAllLines(output).stream()
                        .map(JsonParser::parseString)
                        .map(json -> json.getAsJsonObject())
                        .filter(record -> "population_sample".equals(record.get("event").getAsString()))
                        .toList();
                assertPopulationSoak(helper, samples, replacementEnabled);
                assertControlledPopulationSoakFish(helper, replacementEnabled);
                Files.deleteIfExists(output);
                clearPopulationSoakFish(helper);
                if (replacementEnabled) {
                    helper.runAfterDelay(2, () -> runPopulationSoak(helper, false, previousReplacement));
                } else {
                    BfsConfig.COMMON.replaceVanillaMobs.set(previousReplacement);
                    helper.succeed();
                }
            } catch (IOException | RuntimeException exception) {
                BfsConfig.COMMON.replaceVanillaMobs.set(previousReplacement);
                helper.fail("unable to verify BFS population soak: " + exception.getMessage());
            }
        });
    }

    private static void retryPopulationSoak(GameTestHelper helper, Path output, boolean replacementEnabled,
                                            boolean previousReplacement, int remainingChecks) {
        if (remainingChecks > 1) {
            verifyPopulationSoak(helper, output, replacementEnabled, previousReplacement, remainingChecks - 1);
            return;
        }
        BfsConfig.COMMON.replaceVanillaMobs.set(previousReplacement);
        helper.fail("BFS population soak output has no final record: " + output);
    }

    private static void assertPopulationSoak(GameTestHelper helper, List<JsonObject> samples,
                                             boolean replacementEnabled) {
        helper.assertTrue(samples.size() == 21,
                "population soak must record a start sample and every 1,200 tick sample through 24,000 ticks");

        for (int index = 0; index < samples.size(); index++) {
            JsonObject sample = samples.get(index);
            helper.assertTrue(sample.get("sampleOffsetTicks").getAsInt()
                            == index * BfsDebugManager.POPULATION_SAMPLE_INTERVAL_TICKS,
                    "population soak must preserve fixed 1,200 tick sample offsets");
            helper.assertTrue(sample.get("replaceVanillaMobs").getAsBoolean() == replacementEnabled,
                    "population soak record must preserve its active replacement mode");
            int waterAmbient = sample.get("loadedWaterAmbientEntities").getAsInt();
            int bfsWaterAmbient = sample.get("loadedBfsWaterAmbientEntities").getAsInt();
            helper.assertTrue(waterAmbient >= 0 && bfsWaterAmbient >= 0 && bfsWaterAmbient <= waterAmbient,
                    "population soak records must report valid ambient population counts");
            helper.assertTrue(sample.get("waterAmbientCategoryCapPerChunk").getAsInt() > 0,
                    "population soak records must report the configured ambient category cap");
        }
    }

    private static void assertControlledPopulationSoakFish(GameTestHelper helper, boolean replacementEnabled) {
        AABB bounds = new AABB(helper.absolutePos(new BlockPos(1, 2, 1)),
                helper.absolutePos(new BlockPos(10, 4, 8))).inflate(0.25D);
        List<Mob> fish = helper.getLevel().getEntitiesOfClass(Mob.class, bounds, BfsGameTests::isPopulationSoakFish);
        int atlanticCod = countPopulationSoakFish(fish, ModEntityTypes.ATLANTIC_COD);
        int atlanticSalmon = countPopulationSoakFish(fish, ModEntityTypes.ATLANTIC_SALMON);
        int vanillaCod = countPopulationSoakFish(fish, EntityType.COD);
        int vanillaSalmon = countPopulationSoakFish(fish, EntityType.SALMON);
        if (replacementEnabled) {
            helper.assertTrue(atlanticCod == 8 && atlanticSalmon == 8 && vanillaCod == 0 && vanillaSalmon == 0,
                    "replacement-enabled natural sources must remain one-for-one and bounded at eight Atlantic Cod and eight Atlantic Salmon");
        } else {
            helper.assertTrue(atlanticCod == 0 && atlanticSalmon == 0 && vanillaCod == 8 && vanillaSalmon == 8,
                    "replacement-disabled natural sources must remain one-for-one and bounded at eight vanilla Cod and eight vanilla Salmon");
        }
    }

    private static int countPopulationSoakFish(List<Mob> fish, EntityType<?> type) {
        return (int) fish.stream().filter(entity -> entity.getType() == type).count();
    }

    private static void clearPopulationSoakFish(GameTestHelper helper) {
        AABB bounds = new AABB(helper.absolutePos(new BlockPos(1, 2, 1)),
                helper.absolutePos(new BlockPos(10, 4, 8))).inflate(1.0D);
        helper.getLevel().getEntitiesOfClass(Mob.class, bounds, BfsGameTests::isPopulationSoakFish)
                .forEach(Mob::discard);
    }

    private static boolean isPopulationSoakFish(Mob fish) {
        EntityType<?> type = fish.getType();
        return type == EntityType.COD
                || type == EntityType.SALMON
                || type == ModEntityTypes.ATLANTIC_COD
                || type == ModEntityTypes.ATLANTIC_SALMON;
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

    @GameTest(template = "empty", batch = "bfs_fish_parity", timeoutTicks = 40)
    public static void atlanticFishMatchVanillaParityAndPlacement(GameTestHelper helper) {
        prepareWaterVolume(helper);
        AtlanticCodEntity cod = helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(3, 3, 3));
        AtlanticSalmonEntity salmon = helper.spawn(ModEntityTypes.ATLANTIC_SALMON, new BlockPos(6, 3, 3));
        Cod vanillaCod = EntityType.COD.create(helper.getLevel());
        Salmon vanillaSalmon = EntityType.SALMON.create(helper.getLevel());
        helper.assertTrue(vanillaCod != null && vanillaSalmon != null,
                "vanilla fish controls must be constructible in the parity fixture");
        helper.assertTrue(ModEntityTypes.ATLANTIC_COD.getCategory() == EntityType.COD.getCategory()
                        && ModEntityTypes.ATLANTIC_SALMON.getCategory() == EntityType.SALMON.getCategory(),
                "Atlantic fish must retain the vanilla water ambient category");
        assertEntityTypeParity(helper, ModEntityTypes.ATLANTIC_COD, EntityType.COD, "Cod");
        assertEntityTypeParity(helper, ModEntityTypes.ATLANTIC_SALMON, EntityType.SALMON, "Salmon");
        helper.assertTrue(cod.getMaxSchoolSize() == vanillaCod.getMaxSchoolSize(),
                "Atlantic Cod must retain vanilla schooling size");
        helper.assertTrue(salmon.getMaxSchoolSize() == vanillaSalmon.getMaxSchoolSize(),
                "Atlantic Salmon must retain vanilla schooling size");
        helper.assertTrue(cod.getNavigation() instanceof WaterBoundPathNavigation
                        && salmon.getNavigation() instanceof WaterBoundPathNavigation,
                "Atlantic fish must use vanilla water navigation");
        helper.assertTrue(cod.getTarget() == null && salmon.getTarget() == null,
                "Atlantic fish must remain passive without attack targets");
        helper.assertTrue(net.minecraft.world.entity.SpawnPlacements.getPlacementType(ModEntityTypes.ATLANTIC_COD)
                        == net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER
                        && net.minecraft.world.entity.SpawnPlacements.getPlacementType(ModEntityTypes.ATLANTIC_SALMON)
                        == net.minecraft.world.entity.SpawnPlacements.Type.IN_WATER,
                "Atlantic fish placement must remain in water");
        helper.assertTrue(net.minecraft.world.entity.SpawnPlacements.getHeightmapType(ModEntityTypes.ATLANTIC_COD)
                        == net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES
                        && net.minecraft.world.entity.SpawnPlacements.getHeightmapType(ModEntityTypes.ATLANTIC_SALMON)
                        == net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                "Atlantic fish placement must use the vanilla heightmap");
        helper.succeed();
    }

    private static void assertEntityTypeParity(GameTestHelper helper, EntityType<?> actual,
                                                EntityType<?> vanilla, String species) {
        helper.assertTrue(Math.abs(actual.getWidth() - vanilla.getWidth()) < 0.0001F
                        && Math.abs(actual.getHeight() - vanilla.getHeight()) < 0.0001F,
                species + " hitbox must match vanilla dimensions, actual=" + actual.getWidth() + "x"
                        + actual.getHeight() + ", vanilla=" + vanilla.getWidth() + "x" + vanilla.getHeight());
        helper.assertTrue(actual.clientTrackingRange() == vanilla.clientTrackingRange(),
                species + " tracking range must match vanilla");
    }

    @GameTest(template = "empty", batch = "bfs_fish_parity", timeoutTicks = 40)
    public static void atlanticSalmonSpinNameIsExactAndReversible(GameTestHelper helper) {
        prepareWaterVolume(helper);
        AtlanticSalmonEntity salmon = helper.spawn(ModEntityTypes.ATLANTIC_SALMON, new BlockPos(4, 3, 3));
        helper.assertTrue(!salmon.isNamedSpin(), "unnamed Salmon must not enter Spin");
        salmon.setCustomName(Component.literal("spin"));
        helper.assertTrue(!salmon.isNamedSpin(), "lowercase spin must not enter Spin");
        salmon.setCustomName(Component.literal("Spin "));
        helper.assertTrue(!salmon.isNamedSpin(), "space suffixed Spin must not enter Spin");
        salmon.setCustomName(Component.literal("Spin"));
        helper.assertTrue(salmon.isNamedSpin(), "exact case sensitive Spin must enter Spin immediately");
        salmon.setCustomName(Component.literal("Other"));
        helper.assertTrue(!salmon.isNamedSpin(), "renaming must exit Spin without reloading the entity");
        salmon.setCustomName(null);
        helper.assertTrue(!salmon.isNamedSpin(), "removing the name must keep Spin inactive");
        helper.succeed();
    }

    @GameTest(template = "empty", batch = "bfs_fish_items", timeoutTicks = 120)
    public static void atlanticFishItemsLootRecipesAndFishingRoundTrip(GameTestHelper helper) {
        prepareWaterVolume(helper);
        helper.assertTrue(BfsConfig.COMMON.fishEntities.get(),
                "the dedicated server fixture must use the default live fish fishing delivery");
        assertFishRecipe(helper, ModItems.RAW_ATLANTIC_COD, ModItems.COOKED_ATLANTIC_COD, "atlantic cod");
        assertFishRecipe(helper, ModItems.RAW_ATLANTIC_SALMON, ModItems.COOKED_ATLANTIC_SALMON,
                "atlantic salmon");

        AtlanticCodEntity cod = helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(3, 3, 3));
        AtlanticSalmonEntity salmon = helper.spawn(ModEntityTypes.ATLANTIC_SALMON, new BlockPos(6, 3, 3));
        assertFishLoot(helper, cod, ModItems.RAW_ATLANTIC_COD, ModItems.COOKED_ATLANTIC_COD, "atlantic cod");
        assertFishLoot(helper, salmon, ModItems.RAW_ATLANTIC_SALMON, ModItems.COOKED_ATLANTIC_SALMON,
                "atlantic salmon");
        cod.discard();
        salmon.discard();

        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(java.util.UUID.randomUUID(), "fish-loot-player"));
        player.setPos(helper.absolutePos(new BlockPos(5, 3, 5)).getCenter());
        player.setYRot(0.0F);
        player.setXRot(0.0F);
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        fishFromRealRodCast(helper, player);

        net.minecraft.server.ServerAdvancementManager advancements = helper.getLevel().getServer().getAdvancements();
        net.minecraft.advancements.Advancement codCatch =
                advancements.getAdvancement(BensFintasticSharks.id("oh_my_cod"));
        net.minecraft.advancements.Advancement salmonCatch =
                advancements.getAdvancement(BensFintasticSharks.id("why_arent_you_red"));
        helper.assertTrue(codCatch != null && salmonCatch != null,
                "Atlantic fishing advancements must be loaded before the real rod assertion");
        helper.assertTrue(player.getAdvancements().getOrStartProgress(codCatch).isDone(),
                "real fishing must complete the Atlantic Cod catch advancement");
        helper.assertTrue(player.getAdvancements().getOrStartProgress(salmonCatch).isDone(),
                "real fishing must complete the Atlantic Salmon catch advancement");
    }

    @GameTest(template = "empty", batch = "bfs_advancements", timeoutTicks = 80)
    public static void sharkSpotterAndAtlanticAdvancementsRequireTheirGameplaySignals(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(1, 1, 1), new BlockPos(10, 5, 10));
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(java.util.UUID.randomUUID(), "advancement-fixture-player"));
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        player.setPos(helper.absolutePos(new BlockPos(5, 2, 2)).getX() + 0.5D,
                helper.absolutePos(new BlockPos(5, 2, 2)).getY(),
                helper.absolutePos(new BlockPos(5, 2, 2)).getZ() + 0.5D);
        player.setYRot(0.0F);
        player.setXRot(0.0F);

        net.minecraft.server.ServerAdvancementManager manager = helper.getLevel().getServer().getAdvancements();
        net.minecraft.advancements.Advancement sharkSpotter = advancement(helper, manager, "shark_spotter");
        net.minecraft.advancements.Advancement codCatch = advancement(helper, manager, "oh_my_cod");
        net.minecraft.advancements.Advancement salmonCatch = advancement(helper, manager, "why_arent_you_red");
        net.minecraft.advancements.Advancement codEncounter = advancement(helper, manager, "gadus_morhua");
        net.minecraft.advancements.Advancement salmonEncounter = advancement(helper, manager, "salmo_salar");
        assertAdvancementIcon(helper, codCatch, ModItems.COOKED_ATLANTIC_COD, "oh_my_cod");
        assertAdvancementIcon(helper, salmonCatch, ModItems.COOKED_ATLANTIC_SALMON, "why_arent_you_red");
        assertAdvancementIcon(helper, codEncounter, ModItems.RAW_ATLANTIC_COD, "gadus_morhua");
        assertAdvancementIcon(helper, salmonEncounter, ModItems.RAW_ATLANTIC_SALMON, "salmo_salar");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SPYGLASS));
        BensFintasticSharks.playerTick(player);
        helper.assertTrue(!player.getAdvancements().getOrStartProgress(sharkSpotter).isDone(),
                "holding a Spyglass without using it must not grant Shark Spotter");
        player.startUsingItem(InteractionHand.MAIN_HAND);
        BensFintasticSharks.playerTick(player);
        helper.assertTrue(!player.getAdvancements().getOrStartProgress(sharkSpotter).isDone(),
                "an active Spyglass with no shark must not grant Shark Spotter");

        helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(5, 3, 7));
        helper.spawn(ModEntityTypes.ATLANTIC_SALMON, new BlockPos(6, 3, 7));
        BensFintasticSharks.playerTick(player);
        helper.assertTrue(player.getAdvancements().getOrStartProgress(codEncounter).isDone()
                        && player.getAdvancements().getOrStartProgress(salmonEncounter).isDone(),
                "nearby Atlantic fish must grant their encounter advancements through the production player found trigger");
        helper.assertTrue(!player.getAdvancements().getOrStartProgress(sharkSpotter).isDone(),
                "an active Spyglass looking only at nonshark BFS entities must not grant Shark Spotter");

        AbstractSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(5, 3, 8));
        shark.setNoAi(true);
        BlockPos obstruction = new BlockPos(5, 3, 5);
        helper.setBlock(obstruction, Blocks.STONE.defaultBlockState());
        BensFintasticSharks.playerTick(player);
        helper.assertTrue(!player.getAdvancements().getOrStartProgress(sharkSpotter).isDone(),
                "a solid obstruction before the viewed shark must prevent Shark Spotter");
        helper.setBlock(obstruction, Blocks.WATER.defaultBlockState());
        BensFintasticSharks.playerTick(player);
        helper.assertTrue(player.getAdvancements().getOrStartProgress(sharkSpotter).isDone(),
                "an actively used Spyglass with an unobstructed BFS shark must grant Shark Spotter");
        player.stopUsingItem();
        helper.succeed();
    }

    private static net.minecraft.advancements.Advancement advancement(GameTestHelper helper,
                                                                        net.minecraft.server.ServerAdvancementManager manager,
                                                                        String id) {
        net.minecraft.advancements.Advancement advancement = manager.getAdvancement(BensFintasticSharks.id(id));
        helper.assertTrue(advancement != null, "generated advancement must load: " + id);
        return advancement;
    }

    private static void assertAdvancementIcon(GameTestHelper helper, net.minecraft.advancements.Advancement advancement,
                                              net.minecraft.world.item.Item expectedIcon, String id) {
        helper.assertTrue(advancement.getDisplay().getIcon().is(expectedIcon),
                "generated advancement must retain its required display item: " + id);
    }

    private static void assertFishRecipe(GameTestHelper helper, net.minecraft.world.item.Item raw,
                                         net.minecraft.world.item.Item cooked, String species) {
        SimpleContainer input = new SimpleContainer(new ItemStack(raw));
        var manager = helper.getLevel().getServer().getRecipeManager();
        var smelting = manager.getRecipeFor(RecipeType.SMELTING, input, helper.getLevel());
        var smoking = manager.getRecipeFor(RecipeType.SMOKING, input, helper.getLevel());
        helper.assertTrue(smelting.isPresent() && smelting.get().getResultItem(helper.getLevel().registryAccess())
                        .is(cooked), species + " furnace recipe must return its matching cooked item");
        helper.assertTrue(smoking.isPresent() && smoking.get().getResultItem(helper.getLevel().registryAccess())
                        .is(cooked), species + " smoker recipe must return its matching cooked item");
        helper.assertTrue(!manager.getRecipeFor(RecipeType.SMELTING,
                        new SimpleContainer(new ItemStack(cooked)), helper.getLevel()).isPresent(),
                species + " cooked item must not be accepted as the raw furnace input");
    }

    private static void assertFishLoot(GameTestHelper helper, net.minecraft.world.entity.LivingEntity fish,
                                       net.minecraft.world.item.Item raw, net.minecraft.world.item.Item cooked,
                                       String species) {
        net.minecraft.server.level.ServerLevel level = (net.minecraft.server.level.ServerLevel) helper.getLevel();
        LootTable table = level.getServer().getLootData().getLootTable(fish.getLootTable());
        LootParams ordinaryParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, fish.position())
                .withParameter(LootContextParams.THIS_ENTITY, fish)
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                .create(LootContextParamSets.ENTITY);
        helper.assertTrue(table.getRandomItems(ordinaryParams).stream().anyMatch(stack -> stack.is(raw)),
                species + " ordinary death must yield its raw fish");
        fish.setSecondsOnFire(20);
        LootParams fireParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, fish.position())
                .withParameter(LootContextParams.THIS_ENTITY, fish)
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                .create(LootContextParamSets.ENTITY);
        helper.assertTrue(table.getRandomItems(fireParams).stream().anyMatch(stack -> stack.is(cooked)),
                species + " fire death must yield its cooked fish");
    }

    private static void fishFromRealRodCast(GameTestHelper helper, ServerPlayer player) {
        FishingRodItem rod = (FishingRodItem) Items.FISHING_ROD;
        boolean caughtLiveFishingEntity = false;
        try {
            java.lang.reflect.Field nibble = FishingHook.class.getDeclaredField("nibble");
            nibble.setAccessible(true);
            for (int attempt = 0; attempt < 128; attempt++) {
                Set<java.util.UUID> existingSupportedFish = helper.getLevel()
                        .getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(32.0D))
                        .stream()
                        .filter(BfsGameTests::isSupportedFishingEntity)
                        .map(Entity::getUUID)
                        .collect(java.util.stream.Collectors.toCollection(HashSet::new));
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.FISHING_ROD));
                rod.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
                FishingHook hook = player.fishing;
                helper.assertTrue(hook != null, "a real rod use must create a fishing hook");
                nibble.setInt(hook, 1);
                Vec3 catchPosition = hook.position();
                int rodDamageBefore = player.getMainHandItem().getDamageValue();
                rod.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);
                helper.assertTrue(player.getMainHandItem().getDamageValue() == rodDamageBefore + 1,
                        "a live fish catch must preserve the normal fishing rod durability cost once");
                List<Mob> caughtFish = helper.getLevel().getEntitiesOfClass(Mob.class,
                        player.getBoundingBox().inflate(32.0D))
                        .stream()
                        .filter(BfsGameTests::isSupportedFishingEntity)
                        .filter(fish -> !existingSupportedFish.contains(fish.getUUID()))
                        .toList();
                helper.assertTrue(caughtFish.size() <= 1,
                        "one fishing resolution must create at most one newly created supported fish entity, actual="
                                + caughtFish.stream().map(fish -> fish.getType() + "@" + fish.position()
                                + "/removed=" + fish.isRemoved()).toList());
                if (!caughtFish.isEmpty()) {
                    Mob fish = caughtFish.get(0);
                    helper.assertTrue(fish.position().distanceToSqr(catchPosition) < 0.0001D,
                            "the live fishing catch must spawn at the hook catch position");
                    caughtLiveFishingEntity = true;
                    fish.discard();
                }
                for (ItemEntity item : helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                        player.getBoundingBox().inflate(32.0D))) {
                    helper.assertTrue(!FishingCatchPolicy.isSupportedFishingFish(item.getItem()),
                            "a supported fishing catch must not also create an immediate fish item");
                    item.discard();
                }
            }
        } catch (ReflectiveOperationException exception) {
            helper.fail("unable to arm the real fishing bite fixture: " + exception.getMessage());
            return;
        }
        helper.assertTrue(caughtLiveFishingEntity, "real fishing casts must resolve at least one live fish entity");
        assertFishingPolicyMatrix(helper);
        helper.succeed();
    }

    private static boolean isSupportedFishingEntity(Mob fish) {
        EntityType<?> type = fish.getType();
        return type == EntityType.COD
                || type == EntityType.SALMON
                || type == EntityType.TROPICAL_FISH
                || type == EntityType.PUFFERFISH
                || type == ModEntityTypes.ATLANTIC_COD
                || type == ModEntityTypes.ATLANTIC_SALMON;
    }

    private static void assertFishingPolicyMatrix(GameTestHelper helper) {
        helper.assertTrue(FishingCatchPolicy.selectFishingItem(new ItemStack(Items.COD), true, 0.99F, true)
                        .is(ModItems.RAW_ATLANTIC_COD),
                "replacement enabled Cod fishing must select Atlantic Cod");
        helper.assertTrue(FishingCatchPolicy.selectFishingItem(new ItemStack(Items.SALMON), true, 0.99F, false)
                        .is(ModItems.RAW_ATLANTIC_SALMON),
                "replacement enabled Salmon fishing must select Atlantic Salmon");
        helper.assertTrue(FishingCatchPolicy.selectFishingItem(new ItemStack(Items.COD), false, 0.99F, false)
                        .is(Items.COD),
                "replacement disabled fishing must retain vanilla fish outside the Atlantic selection");
        helper.assertTrue(FishingCatchPolicy.selectFishingItem(new ItemStack(Items.COD), false, 0.0F, false)
                        .is(ModItems.RAW_ATLANTIC_COD),
                "one Atlantic selection must replace the vanilla fish instead of adding a second fish");
        helper.assertTrue(FishingCatchPolicy.selectFishingItem(new ItemStack(Items.TROPICAL_FISH), false, 0.0F, false)
                        .is(Items.TROPICAL_FISH),
                "Atlantic selection must preserve Tropical Fish species");
        helper.assertTrue(FishingCatchPolicy.onlySupportedFishingFish(List.of(
                        new ItemStack(Items.COD), new ItemStack(Items.SALMON))) == null,
                "mixed drops must never be converted into a duplicate fishing catch");
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
            String command = "bfs debug on movement 70 @e[type=" + entityId + ",sort=nearest,limit=1]";
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
        freezeCuriosityItem(item);
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

    private static void freezeCuriosityItem(ItemEntity item) {
        item.setNoGravity(true);
        item.setDeltaMovement(Vec3.ZERO);
        item.noPhysics = true;
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
                                "tiger shark must return to idle after target loss, state="
                                        + shark.getSharkState() + ", target=" + shark.getTarget()
                                        + ", position=" + shark.position() + ", navDone="
                                        + shark.getNavigation().isDone() + ", delta="
                                        + shark.getDeltaMovement());
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
        freezeCuriosityItem(edible);
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
        freezeCuriosityItem(nonEdible);
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

    @GameTest(template = "empty", batch = "bfs_curiosity_water_exit", timeoutTicks = 260)
    public static void tigerCuriosityClearsWhenItemLeavesWater(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(8, 3, 3));
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        freezeCuriosityItem(item);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        // The scan has a bounded empty-result cooldown and path discovery can begin after the
        // fixture's first tick. Allow one full retry window before testing the water exit.
        runWhenTigerCurious(helper, shark, item, 180, () -> {
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
        freezeCuriosityItem(item);
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

    @GameTest(template = "empty", batch = "bfs_curiosity_flee", timeoutTicks = 260)
    public static void tigerCuriosityFleePreemptionClearsInvestigation(GameTestHelper helper) {
        prepareWaterVolume(helper);
        clearAquaticFixtureEntities(helper, new BlockPos(3, 3, 3), new BlockPos(8, 3, 3));
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        freezeCuriosityItem(item);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        // Allow one complete bounded scan and path acquisition window before asserting
        // preemption. Fixture startup and parallel batch placement can delay the first
        // reachable path without changing the curiosity contract.
        runWhenTigerCurious(helper, shark, item, 180, () -> {
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
        freezeCuriosityItem(item);
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(item.isInWater(), "The edible item must tick in water before the shark can scan it, position="
                    + item.position() + ", block=" + item.blockPosition() + ", fluid="
                    + helper.getLevel().getFluidState(item.blockPosition()));
            TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
            helper.getLevel().getEntitiesOfClass(LivingEntity.class, shark.getBoundingBox().inflate(64.0D),
                    entity -> entity != shark && entity.isInWater() && !(entity instanceof Player))
                    .forEach(LivingEntity::discard);
            runWhenTigerCurious(helper, shark, item, 80, () -> {
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
        });
    }

    @GameTest(template = "empty", batch = "bfs_curiosity_timeout", timeoutTicks = 400)
    public static void tigerCuriosityTimeoutRemembersItemWithoutReacquiring(GameTestHelper helper) {
        prepareWaterVolume(helper);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        freezeCuriosityItem(item);
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
                finishAfterRemovingTestPlayer(helper, player);
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
                finishAfterRemovingTestPlayer(helper, player);
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
                finishAfterRemovingTestPlayer(helper, player);
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
                finishAfterRemovingTestPlayer(helper, player);
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
                            finishAfterRemovingTestPlayer(helper, prey);
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
                        finishAfterRemovingTestPlayer(helper, player);
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

    private static void finishAfterRemovingTestPlayer(GameTestHelper helper, Player player) {
        if (!player.isRemoved()) {
            player.stopRiding();
            player.remove(Entity.RemovalReason.DISCARDED);
        }
        helper.runAfterDelay(1, helper::succeed);
    }

    @GameTest(template = "empty", batch = "bfs_spawn_controls", timeoutTicks = 40)
    public static void vanillaFishReplacementHonorsOneForOneAndModes(GameTestHelper helper) {
        prepareWaterVolume(helper);
        boolean previousReplacement = BfsConfig.COMMON.replaceVanillaMobs.get();
        boolean previousSuppression = BfsConfig.COMMON.disableVanillaAquaticSpawns.get();
        MobCapManager manager = new MobCapManager();
        BlockPos local = new BlockPos(4, 3, 4);
        BlockPos absolute = helper.absolutePos(local);
        try {
            BfsConfig.COMMON.replaceVanillaMobs.set(true);
            BfsConfig.COMMON.disableVanillaAquaticSpawns.set(false);

            Cod source = EntityType.COD.create(helper.getLevel());
            helper.assertTrue(source != null, "vanilla Cod fixture must construct");
            source.moveTo(absolute.getX() + 0.5D, absolute.getY() + 0.5D, absolute.getZ() + 0.5D,
                    37.0F, -12.0F);
            source.yHeadRot = 51.0F;
            source.yBodyRot = 29.0F;
            source.setDeltaMovement(new Vec3(0.12D, 0.03D, -0.08D));
            source.setCustomName(Component.literal("named source cod"));
            source.setCustomNameVisible(true);

            MobSpawnEvent.FinalizeSpawn replacementEvent = newFinalizeSpawn(helper, source, absolute,
                    MobSpawnType.NATURAL, null);
            manager.onFinalizeSpawn(replacementEvent);
            helper.assertTrue(replacementEvent.isSpawnCancelled(),
                    "natural vanilla Cod must be replaced before suppression");
            List<AtlanticCodEntity> cods = helper.getLevel().getEntitiesOfClass(AtlanticCodEntity.class,
                    new AABB(absolute).inflate(2.0D));
            helper.assertTrue(cods.size() == 1,
                    "one accepted Cod attempt must produce at most one Atlantic Cod, count=" + cods.size());
            AtlanticCodEntity replacement = cods.get(0);
            helper.assertTrue(replacement.hasCustomName()
                            && "named source cod".equals(replacement.getCustomName().getString()),
                    "replacement must preserve custom name");
            helper.assertTrue(replacement.isCustomNameVisible(),
                    "replacement must preserve custom name visibility");
            helper.assertTrue(replacement.getYRot() == source.getYRot()
                            && replacement.getXRot() == source.getXRot()
                            && replacement.yHeadRot == source.yHeadRot
                            && replacement.yBodyRot == source.yBodyRot,
                    "replacement must preserve body and head rotation");
            helper.assertTrue(replacement.getDeltaMovement().equals(source.getDeltaMovement()),
                    "replacement must preserve motion");
            helper.assertTrue(replacementEvent.getSpawnData() != null,
                    "replacement must preserve schooling group continuity");

            TropicalFish tropical = EntityType.TROPICAL_FISH.create(helper.getLevel());
            helper.assertTrue(tropical != null, "Tropical Fish fixture must construct");
            tropical.moveTo(absolute.getX() + 0.5D, absolute.getY() + 0.5D, absolute.getZ() + 0.5D,
                    0.0F, 0.0F);
            MobSpawnEvent.FinalizeSpawn excludedEvent = newFinalizeSpawn(helper, tropical, absolute,
                    MobSpawnType.NATURAL, null);
            manager.onFinalizeSpawn(excludedEvent);
            helper.assertTrue(!excludedEvent.isSpawnCancelled(),
                    "unsupported vanilla aquatic species must remain unchanged");

            Pufferfish pufferfish = EntityType.PUFFERFISH.create(helper.getLevel());
            helper.assertTrue(pufferfish != null, "Pufferfish fixture must construct");
            pufferfish.moveTo(absolute.getX() + 0.5D, absolute.getY() + 0.5D, absolute.getZ() + 4.5D,
                    0.0F, 0.0F);
            MobSpawnEvent.FinalizeSpawn pufferfishEvent = newFinalizeSpawn(helper, pufferfish,
                    absolute.offset(0, 0, 4), MobSpawnType.NATURAL, null);
            manager.onFinalizeSpawn(pufferfishEvent);
            helper.assertTrue(!pufferfishEvent.isSpawnCancelled(),
                    "Pufferfish natural spawn must remain unchanged");

            List<MobSpawnType> joinSources = List.of(MobSpawnType.SPAWN_EGG, MobSpawnType.COMMAND,
                    MobSpawnType.BUCKET, MobSpawnType.DISPENSER, MobSpawnType.SPAWNER, MobSpawnType.STRUCTURE);
            BlockPos bucketPosition = helper.absolutePos(joinSourcePosition(2));
            for (int index = 0; index < joinSources.size(); index++) {
                MobSpawnType reason = joinSources.get(index);
                boolean salmonSource = index % 2 == 1;
                EntityType<? extends Mob> sourceType = salmonSource ? EntityType.SALMON : EntityType.COD;
                EntityType<?> replacementType = salmonSource
                        ? ModEntityTypes.ATLANTIC_SALMON
                        : ModEntityTypes.ATLANTIC_COD;
                BlockPos sourcePosition = helper.absolutePos(joinSourcePosition(index));
                String sourceName = "join source " + reason;
                spawnVanillaFishForJoinSource(helper, sourceType, sourcePosition, reason, sourceName);

                if (reason == MobSpawnType.BUCKET) {
                    continue;
                }
                List<Mob> joinedFish = helper.getLevel().getEntitiesOfClass(Mob.class,
                        new AABB(sourcePosition).inflate(0.25D));
                helper.assertTrue(joinedFish.size() == 1 && joinedFish.get(0).getType() == replacementType,
                        "new vanilla fish from " + reason + " must become exactly one matching Atlantic fish, actual="
                                + joinedFish.stream().map(mob -> String.valueOf(mob.getType())).toList());
                helper.assertTrue(joinedFish.get(0).hasCustomName()
                                && sourceName.equals(joinedFish.get(0).getCustomName().getString()),
                        "new vanilla fish from " + reason + " must preserve its safe custom name");
            }

            Cod existing = EntityType.COD.create(helper.getLevel());
            helper.assertTrue(existing != null, "existing vanilla Cod fixture must construct");
            existing.moveTo(absolute.getX() + 0.5D, absolute.getY() + 0.5D, absolute.getZ() + 6.5D,
                    0.0F, 0.0F);
            helper.getLevel().addFreshEntity(existing);
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(Cod.class,
                            new AABB(absolute.offset(0, 0, 6)).inflate(1.0D)).contains(existing),
                    "existing loaded vanilla Cod must remain unchanged by the real entity join path");

            AtlanticCodEntity bfsEgg = ModEntityTypes.ATLANTIC_COD.create(helper.getLevel());
            helper.assertTrue(bfsEgg != null, "BFS Cod spawn egg fixture must construct");
            bfsEgg.moveTo(absolute.getX() + 0.5D, absolute.getY() + 0.5D, absolute.getZ() + 7.5D,
                    0.0F, 0.0F);
            bfsEgg.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(absolute),
                    MobSpawnType.SPAWN_EGG, null, null);
            helper.getLevel().addFreshEntity(bfsEgg);
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(AtlanticCodEntity.class,
                            new AABB(absolute.offset(0, 0, 7)).inflate(1.0D)).contains(bfsEgg),
                    "BFS Cod spawn egg must remain unchanged by the real entity join path");

            BfsConfig.COMMON.replaceVanillaMobs.set(false);
            Salmon unchanged = EntityType.SALMON.create(helper.getLevel());
            helper.assertTrue(unchanged != null, "vanilla Salmon fixture must construct");
            unchanged.moveTo(absolute.getX() + 0.5D, absolute.getY() + 0.5D, absolute.getZ() + 1.5D,
                    0.0F, 0.0F);
            MobSpawnEvent.FinalizeSpawn disabledEvent = newFinalizeSpawn(helper, unchanged,
                    absolute.offset(0, 0, 1), MobSpawnType.CHUNK_GENERATION, null);
            manager.onFinalizeSpawn(disabledEvent);
            helper.assertTrue(!disabledEvent.isSpawnCancelled(),
                    "replacement disabled must preserve vanilla Salmon");
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(AtlanticSalmonEntity.class,
                    new AABB(absolute).inflate(2.0D)).isEmpty(),
                    "replacement disabled must not add Atlantic Salmon");

            Cod disabledEggSource = EntityType.COD.create(helper.getLevel());
            helper.assertTrue(disabledEggSource != null, "replacement disabled egg fixture must construct");
            disabledEggSource.moveTo(absolute.getX() + 0.5D, absolute.getY() + 0.5D,
                    absolute.getZ() + 8.5D, 0.0F, 0.0F);
            disabledEggSource.finalizeSpawn(helper.getLevel(), helper.getLevel().getCurrentDifficultyAt(
                    absolute.offset(0, 0, 8)), MobSpawnType.SPAWN_EGG, null, null);
            helper.getLevel().addFreshEntity(disabledEggSource);
            helper.assertTrue(helper.getLevel().getEntitiesOfClass(Cod.class,
                            new AABB(absolute.offset(0, 0, 8)).inflate(1.0D)).contains(disabledEggSource),
                    "replacement disabled must preserve vanilla Cod spawn eggs through the real entity join path");
            helper.runAfterDelay(2, () -> {
                List<Mob> bucketReplacement = helper.getLevel().getEntitiesOfClass(Mob.class,
                        new AABB(bucketPosition).inflate(0.25D));
                helper.assertTrue(bucketReplacement.size() == 1
                                && bucketReplacement.get(0).getType() == ModEntityTypes.ATLANTIC_COD,
                        "real bucket release must become exactly one Atlantic Cod after vanilla applies bucket state");
                Mob bucketFish = bucketReplacement.get(0);
                helper.assertTrue(bucketFish.hasCustomName()
                                && "join source BUCKET".equals(bucketFish.getCustomName().getString())
                                && bucketFish.isNoAi()
                                && bucketFish.isSilent()
                                && bucketFish.isNoGravity()
                                && bucketFish.isCurrentlyGlowing()
                                && bucketFish.isInvulnerable()
                                && bucketFish.getHealth() == 1.0F,
                        "real bucket release must preserve compatible bucket state on its replacement");
                helper.succeed();
            });
        } finally {
            BfsConfig.COMMON.replaceVanillaMobs.set(previousReplacement);
            BfsConfig.COMMON.disableVanillaAquaticSpawns.set(previousSuppression);
        }
    }

    private static void spawnVanillaFishForJoinSource(GameTestHelper helper, EntityType<? extends Mob> sourceType,
                                                       BlockPos position, MobSpawnType reason, String sourceName) {
        if (reason == MobSpawnType.SPAWN_EGG) {
            ItemStack spawnEgg = new ItemStack(sourceType == EntityType.SALMON
                    ? Items.SALMON_SPAWN_EGG : Items.COD_SPAWN_EGG);
            spawnEgg.setHoverName(Component.literal(sourceName));
            helper.assertTrue(spawnEgg.getItem() instanceof SpawnEggItem,
                    "vanilla fish spawn egg fixture must use the production SpawnEggItem path");
            Player eggUser = makeSurvivalTestPlayer(helper);
            eggUser.setItemInHand(InteractionHand.MAIN_HAND, spawnEgg);
            InteractionResult result = ((SpawnEggItem) spawnEgg.getItem()).useOn(new UseOnContext(
                    eggUser,
                    InteractionHand.MAIN_HAND,
                    new net.minecraft.world.phys.BlockHitResult(position.getCenter(), Direction.UP, position, false)
            ));
            helper.assertTrue(result.consumesAction(),
                    "vanilla fish spawn egg fixture must use the production item-use path");
            return;
        }

        if (reason == MobSpawnType.COMMAND) {
            String entityId = sourceType == EntityType.SALMON ? "minecraft:salmon" : "minecraft:cod";
            String command = "summon " + entityId + " " + position.getX() + " " + position.getY() + " "
                    + position.getZ() + " {CustomName:'{\"text\":\"" + sourceName + "\"}'}";
            int result = helper.getLevel().getServer().getCommands().performPrefixedCommand(
                    helper.getLevel().getServer().createCommandSourceStack()
                            .withLevel(helper.getLevel())
                            .withPermission(4),
                    command);
            helper.assertTrue(result > 0,
                    "vanilla fish command fixture must dispatch the production summon command");
            return;
        }

        if (reason == MobSpawnType.BUCKET) {
            ItemStack bucket = new ItemStack(sourceType == EntityType.SALMON
                    ? Items.SALMON_BUCKET : Items.COD_BUCKET);
            bucket.setHoverName(Component.literal(sourceName));
            bucket.getOrCreateTag().putBoolean("NoAI", true);
            bucket.getOrCreateTag().putBoolean("Silent", true);
            bucket.getOrCreateTag().putBoolean("NoGravity", true);
            bucket.getOrCreateTag().putBoolean("Glowing", true);
            bucket.getOrCreateTag().putBoolean("Invulnerable", true);
            bucket.getOrCreateTag().putFloat("Health", 1.0F);
            helper.assertTrue(bucket.getItem() instanceof MobBucketItem,
                    "vanilla fish bucket fixture must use the production MobBucketItem path");
            ((MobBucketItem) bucket.getItem()).checkExtraContent(null, helper.getLevel(), bucket, position);
            return;
        }

        Mob sourceFish = sourceType.spawn(helper.getLevel(), (net.minecraft.nbt.CompoundTag) null,
                fish -> fish.setCustomName(Component.literal(sourceName)), position, reason, true, false);
        helper.assertTrue(sourceFish != null,
                "vanilla fish fixture must construct through the production spawn path for " + reason);
    }

    private static BlockPos joinSourcePosition(int index) {
        return new BlockPos(2 + index % 3 * 3, 3, 7 + index / 3 * 2);
    }

    @GameTest(template = "empty", batch = "bfs_spawn_controls", timeoutTicks = 120)
    public static void vanillaFishReplacementUsesActualCreationSources(GameTestHelper helper) {
        prepareActualSourceWaterVolume(helper);
        boolean previousReplacement = BfsConfig.COMMON.replaceVanillaMobs.get();
        boolean previousSuppression = BfsConfig.COMMON.disableVanillaAquaticSpawns.get();
        BfsConfig.COMMON.replaceVanillaMobs.set(true);
        BfsConfig.COMMON.disableVanillaAquaticSpawns.set(false);
        ServerPlayer spawnerPlayer = makeSpawnerTestPlayer(helper);
        try {
            for (int fishIndex = 0; fishIndex < 2; fishIndex++) {
                EntityType<? extends Mob> sourceType = sourceFishType(fishIndex);
                EntityType<? extends Mob> replacementType = replacementFishType(fishIndex);
                BlockPos naturalPosition = helper.absolutePos(actualSourcePosition(0, fishIndex));
                BlockPos chunkPosition = helper.absolutePos(actualSourcePosition(1, fishIndex));
                spawnVanillaFishForWorldGeneration(helper, sourceType, naturalPosition, MobSpawnType.NATURAL);
                assertSingleReplacement(helper, naturalPosition, replacementType, "natural spawn");
                spawnVanillaFishForWorldGeneration(helper, sourceType, chunkPosition, MobSpawnType.CHUNK_GENERATION);
                assertSingleReplacement(helper, chunkPosition, replacementType, "chunk generation");
            }

            for (int fishIndex = 0; fishIndex < 2; fishIndex++) {
                EntityType<? extends Mob> sourceType = sourceFishType(fishIndex);
                EntityType<? extends Mob> replacementType = replacementFishType(fishIndex);

                BlockPos spawnEggPosition = helper.absolutePos(actualSourcePosition(2, fishIndex));
                String spawnEggName = "actual spawn egg " + sourceFishName(fishIndex);
                spawnVanillaFishForJoinSource(helper, sourceType, spawnEggPosition, MobSpawnType.SPAWN_EGG,
                        spawnEggName);
                assertSingleReplacement(helper, spawnEggPosition, replacementType, spawnEggName);

                BlockPos commandPosition = helper.absolutePos(actualSourcePosition(3, fishIndex));
                String commandName = "actual summon " + sourceFishName(fishIndex);
                spawnVanillaFishForJoinSource(helper, sourceType, commandPosition, MobSpawnType.COMMAND,
                        commandName);
                assertSingleReplacement(helper, commandPosition, replacementType, commandName);

                String spawnerName = "actual spawner " + sourceFishName(fishIndex);
                BlockPos spawnerPosition = spawnVanillaFishWithSpawner(helper, sourceType, spawnerPlayer,
                        fishIndex, spawnerName);
                assertSingleReplacement(helper, spawnerPosition, replacementType, spawnerName);

                BlockPos structurePosition = helper.absolutePos(actualSourcePosition(7, fishIndex));
                String structureName = "actual structure " + sourceFishName(fishIndex);
                spawnVanillaFishWithStructure(helper, sourceType, structurePosition, structureName);
                assertSingleReplacement(helper, structurePosition, replacementType, structureName);

                BlockPos savedPosition = helper.absolutePos(actualSourcePosition(8, fishIndex));
                String savedName = "actual saved " + sourceFishName(fishIndex);
                loadSavedVanillaFish(helper, sourceType, savedPosition, savedName);
                List<Mob> loadedFish = helper.getLevel().getEntitiesOfClass(Mob.class,
                        new AABB(savedPosition).inflate(0.25D));
                helper.assertTrue(loadedFish.size() == 1 && loadedFish.get(0).getType() == sourceType,
                        "saved vanilla " + sourceFishName(fishIndex) + " must remain unchanged, actual="
                                + loadedFish.stream().map(mob -> String.valueOf(mob.getType())).toList());
                helper.assertTrue(loadedFish.get(0).hasCustomName()
                                && savedName.equals(loadedFish.get(0).getCustomName().getString()),
                        "saved vanilla " + sourceFishName(fishIndex) + " must preserve its saved name");

                BlockPos bucketPosition = helper.absolutePos(actualSourcePosition(4, fishIndex));
                String bucketName = "actual player bucket " + sourceFishName(fishIndex);
                releaseVanillaFishFromPlayerBucket(helper, sourceType, bucketPosition, bucketName);

                BlockPos dispenserPosition = helper.absolutePos(actualSourcePosition(5, fishIndex));
                String dispenserName = "actual dispenser bucket " + sourceFishName(fishIndex);
                releaseVanillaFishFromDispenser(helper, sourceType, dispenserPosition, dispenserName);
            }
            helper.runAfterDelay(4, () -> {
                try {
                    for (int fishIndex = 0; fishIndex < 2; fishIndex++) {
                        EntityType<? extends Mob> replacementType = replacementFishType(fishIndex);
                        BlockPos bucketPosition = helper.absolutePos(actualSourcePosition(4, fishIndex));
                        String bucketName = "actual player bucket " + sourceFishName(fishIndex);
                        assertStatefulBucketReplacement(helper, bucketPosition, replacementType, bucketName);

                        BlockPos dispenserPosition = helper.absolutePos(actualSourcePosition(5, fishIndex));
                        String dispenserName = "actual dispenser bucket " + sourceFishName(fishIndex);
                        assertStatefulBucketReplacement(helper, dispenserPosition, replacementType, dispenserName);
                    }
                } finally {
                    try {
                        BfsConfig.COMMON.replaceVanillaMobs.set(previousReplacement);
                        BfsConfig.COMMON.disableVanillaAquaticSpawns.set(previousSuppression);
                    } finally {
                        helper.getLevel().removePlayerImmediately(spawnerPlayer, Entity.RemovalReason.DISCARDED);
                    }
                }
                helper.succeed();
            });
        } catch (RuntimeException exception) {
            try {
                BfsConfig.COMMON.replaceVanillaMobs.set(previousReplacement);
                BfsConfig.COMMON.disableVanillaAquaticSpawns.set(previousSuppression);
            } finally {
                helper.getLevel().removePlayerImmediately(spawnerPlayer, Entity.RemovalReason.DISCARDED);
            }
            throw exception;
        }
    }

    @GameTest(template = "empty", batch = "bfs_spawn_controls", timeoutTicks = 120)
    public static void naturalVanillaFishReplacementHonorsPopulationCap(GameTestHelper helper) {
        prepareActualSourceWaterVolume(helper);
        boolean previousReplacement = BfsConfig.COMMON.replaceVanillaMobs.get();
        BfsConfig.COMMON.replaceVanillaMobs.set(true);
        MobCapManager.setRuntimeCap(ModEntityTypes.ATLANTIC_COD, 1);
        try {
            for (int attempt = 0; attempt < 6; attempt++) {
                BlockPos position = helper.absolutePos(new BlockPos(2 + attempt * 2, 3, 2));
                EntityType.COD.spawn(helper.getLevel(), (CompoundTag) null, fish -> fish.setNoAi(true), position,
                        MobSpawnType.NATURAL, true, false);
            }
            List<AtlanticCodEntity> atlanticCod = helper.getLevel().getEntitiesOfClass(AtlanticCodEntity.class,
                    new AABB(helper.absolutePos(new BlockPos(7, 3, 2))).inflate(8.0D));
            List<Cod> vanillaCod = helper.getLevel().getEntitiesOfClass(Cod.class,
                    new AABB(helper.absolutePos(new BlockPos(7, 3, 2))).inflate(8.0D),
                    cod -> cod.getType() == EntityType.COD);
            helper.assertTrue(atlanticCod.size() == 1,
                    "natural vanilla Cod replacement must honor the configured Atlantic Cod population cap");
            helper.assertTrue(vanillaCod.isEmpty(),
                    "a rejected natural vanilla Cod replacement must not leak a vanilla Cod");
            helper.succeed();
        } finally {
            MobCapManager.resetRuntimeCap(ModEntityTypes.ATLANTIC_COD);
            BfsConfig.COMMON.replaceVanillaMobs.set(previousReplacement);
        }
    }

    private static EntityType<? extends Mob> sourceFishType(int fishIndex) {
        return fishIndex == 0 ? EntityType.COD : EntityType.SALMON;
    }

    private static EntityType<? extends Mob> replacementFishType(int fishIndex) {
        return fishIndex == 0 ? ModEntityTypes.ATLANTIC_COD : ModEntityTypes.ATLANTIC_SALMON;
    }

    private static String sourceFishName(int fishIndex) {
        return fishIndex == 0 ? "cod" : "salmon";
    }

    private static BlockPos actualSourcePosition(int sourceIndex, int fishIndex) {
        return new BlockPos(2 + (sourceIndex % 3 + fishIndex * 3) * 2, 3,
                2 + sourceIndex / 3 * 3);
    }

    private static void spawnVanillaFishForWorldGeneration(GameTestHelper helper, EntityType<? extends Mob> sourceType,
                                                            BlockPos position, MobSpawnType reason) {
        Mob sourceFish = sourceType.spawn(helper.getLevel(), (CompoundTag) null,
                fish -> fish.setNoAi(true), position, reason, true, false);
        helper.assertTrue(sourceFish != null,
                "vanilla fish fixture must construct through the production " + reason + " path");
    }

    private static void assertSingleReplacement(GameTestHelper helper, BlockPos position,
                                                EntityType<? extends Mob> replacementType, String sourceName) {
        List<Mob> replacement = helper.getLevel().getEntitiesOfClass(Mob.class,
                new AABB(position).inflate(0.25D));
        helper.assertTrue(replacement.size() == 1 && replacement.get(0).getType() == replacementType,
                sourceName + " must produce exactly one matching Atlantic fish, actual="
                        + replacement.stream().map(mob -> String.valueOf(mob.getType())).toList());
        if (!sourceName.startsWith("natural") && !sourceName.startsWith("chunk")) {
            helper.assertTrue(replacement.get(0).hasCustomName()
                            && sourceName.equals(replacement.get(0).getCustomName().getString()),
                    sourceName + " must preserve its safe custom name");
        }
    }

    private static void releaseVanillaFishFromPlayerBucket(GameTestHelper helper, EntityType<? extends Mob> sourceType,
                                                           BlockPos position, String sourceName) {
        ItemStack bucket = statefulVanillaFishBucket(sourceType, sourceName);
        ((MobBucketItem) bucket.getItem()).checkExtraContent(null, helper.getLevel(), bucket, position);
    }

    private static void releaseVanillaFishFromDispenser(GameTestHelper helper, EntityType<? extends Mob> sourceType,
                                                        BlockPos position, String sourceName) {
        BlockPos dispenserPosition = position.relative(Direction.WEST);
        var dispenserState = Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, Direction.EAST);
        helper.getLevel().setBlock(dispenserPosition, dispenserState, 3);
        helper.getLevel().setBlock(dispenserPosition.relative(Direction.WEST), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        helper.assertTrue(helper.getLevel().getBlockEntity(dispenserPosition) instanceof DispenserBlockEntity,
                "actual dispenser bucket fixture must create a dispenser block entity");
        DispenserBlockEntity dispenser = (DispenserBlockEntity) helper.getLevel().getBlockEntity(dispenserPosition);
        dispenser.setItem(0, statefulVanillaFishBucket(sourceType, sourceName));
        ((DispenserBlock) dispenserState.getBlock()).tick(dispenserState, helper.getLevel(), dispenserPosition,
                helper.getLevel().getRandom());
    }

    private static ItemStack statefulVanillaFishBucket(EntityType<? extends Mob> sourceType, String sourceName) {
        ItemStack bucket = new ItemStack(sourceType == EntityType.SALMON ? Items.SALMON_BUCKET : Items.COD_BUCKET);
        bucket.setHoverName(Component.literal(sourceName));
        CompoundTag tag = bucket.getOrCreateTag();
        tag.putBoolean("NoAI", true);
        tag.putBoolean("Silent", true);
        tag.putBoolean("NoGravity", true);
        tag.putBoolean("Glowing", true);
        tag.putBoolean("Invulnerable", true);
        tag.putFloat("Health", 1.0F);
        return bucket;
    }

    private static void assertStatefulBucketReplacement(GameTestHelper helper, BlockPos position,
                                                        EntityType<? extends Mob> replacementType, String sourceName) {
        List<Mob> replacement = helper.getLevel().getEntitiesOfClass(Mob.class,
                new AABB(position).inflate(0.5D));
        helper.assertTrue(replacement.size() == 1 && replacement.get(0).getType() == replacementType,
                sourceName + " must become exactly one matching Atlantic fish, actual="
                        + replacement.stream().map(mob -> String.valueOf(mob.getType())).toList());
        Mob fish = replacement.get(0);
        helper.assertTrue(fish.hasCustomName()
                        && sourceName.equals(fish.getCustomName().getString())
                        && fish.isNoAi()
                        && fish.isSilent()
                        && fish.isNoGravity()
                        && fish.isCurrentlyGlowing()
                        && fish.isInvulnerable()
                        && fish.getHealth() == 1.0F,
                sourceName + " must preserve compatible bucket state on its replacement");
    }

    private static BlockPos spawnVanillaFishWithSpawner(GameTestHelper helper, EntityType<? extends Mob> sourceType,
                                                        ServerPlayer nearbyPlayer, int fishIndex, String sourceName) {
        BlockPos playerPosition = nearbyPlayer.blockPosition();
        BlockPos position = new BlockPos(playerPosition.getX() + 4 + fishIndex * 4,
                helper.getLevel().getSeaLevel() - 2, playerPosition.getZ());
        prepareSpawnerWaterFixture(helper, position);
        BlockPos spawnerPosition = position.below(2);
        helper.getLevel().setBlock(spawnerPosition, Blocks.SPAWNER.defaultBlockState(), 3);
        helper.assertTrue(helper.getLevel().getBlockEntity(spawnerPosition) instanceof SpawnerBlockEntity,
                "actual spawner fixture must create a spawner block entity");
        SpawnerBlockEntity spawner = (SpawnerBlockEntity) helper.getLevel().getBlockEntity(spawnerPosition);
        spawner.setEntityId(sourceType, helper.getLevel().getRandom());
        CompoundTag settings = spawner.getSpawner().save(new CompoundTag());
        settings.putShort("Delay", (short) 0);
        settings.putShort("MinSpawnDelay", (short) 200);
        settings.putShort("MaxSpawnDelay", (short) 200);
        settings.putShort("SpawnCount", (short) 1);
        settings.putShort("MaxNearbyEntities", (short) 8);
        settings.putShort("RequiredPlayerRange", (short) 16);
        settings.putShort("SpawnRange", (short) 0);
        CompoundTag spawnData = settings.getCompound("SpawnData");
        CompoundTag entityData = spawnData.getCompound("entity");
        entityData.putString("id", sourceType == EntityType.SALMON ? "minecraft:salmon" : "minecraft:cod");
        entityData.putString("CustomName", Component.Serializer.toJson(Component.literal(sourceName)));
        entityData.putBoolean("NoAI", true);
        ListTag positionTag = new ListTag();
        positionTag.add(DoubleTag.valueOf(position.getX() + 0.5D));
        positionTag.add(DoubleTag.valueOf(position.getY() + 0.5D));
        positionTag.add(DoubleTag.valueOf(position.getZ() + 0.5D));
        entityData.put("Pos", positionTag);
        spawnData.put("entity", entityData);
        settings.put("SpawnData", spawnData);
        spawner.getSpawner().load(helper.getLevel(), spawnerPosition, settings);

        helper.assertTrue(helper.getLevel().hasNearbyAlivePlayer(position.getX() + 0.5D,
                        position.getY() + 0.5D, position.getZ() + 0.5D, 16.0D),
                "actual spawner fixture must have a nearby player");
        spawner.getSpawner().serverTick(helper.getLevel(), spawnerPosition);
        return position;
    }

    private static ServerPlayer makeSpawnerTestPlayer(GameTestHelper helper) {
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(java.util.UUID.randomUUID(), "spawner-fixture-player"));
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        player.setPos(0.5D, helper.getLevel().getSeaLevel() - 1.0D, 0.5D);
        helper.getLevel().addNewPlayer(player);
        return player;
    }

    private static void prepareSpawnerWaterFixture(GameTestHelper helper, BlockPos position) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -2; y <= 1; y++) {
                    helper.getLevel().setBlock(position.offset(x, y, z), Blocks.WATER.defaultBlockState(), 3);
                }
            }
        }
    }

    private static void spawnVanillaFishWithStructure(GameTestHelper helper, EntityType<? extends Mob> sourceType,
                                                      BlockPos position, String sourceName) {
        BlockPos capturePosition = position.above();
        Mob sourceFish = sourceType.create(helper.getLevel());
        helper.assertTrue(sourceFish != null, "actual structure fixture must construct a vanilla fish source");
        sourceFish.moveTo(capturePosition.getX() + 0.5D, capturePosition.getY() + 0.5D,
                capturePosition.getZ() + 0.5D);
        sourceFish.setCustomName(Component.literal(sourceName));
        sourceFish.setNoAi(true);
        helper.getLevel().addFreshEntity(sourceFish);

        StructureTemplate template = new StructureTemplate();
        template.fillFromWorld(helper.getLevel(), capturePosition, new net.minecraft.core.Vec3i(1, 1, 1),
                true, Blocks.AIR);
        sourceFish.discard();
        boolean placed = template.placeInWorld(helper.getLevel(), position, position,
                new StructurePlaceSettings().setFinalizeEntities(true), helper.getLevel().getRandom(), 2);
        helper.assertTrue(placed, "actual structure fixture must place captured vanilla fish data");
    }

    private static void loadSavedVanillaFish(GameTestHelper helper, EntityType<? extends Mob> sourceType,
                                             BlockPos position, String sourceName) {
        Mob sourceFish = sourceType.create(helper.getLevel());
        helper.assertTrue(sourceFish != null, "saved fish fixture must construct a vanilla fish source");
        sourceFish.moveTo(position.getX() + 0.5D, position.getY() + 0.5D, position.getZ() + 0.5D);
        sourceFish.setCustomName(Component.literal(sourceName));
        sourceFish.setNoAi(true);
        CompoundTag savedData = new CompoundTag();
        sourceFish.saveWithoutId(savedData);
        helper.assertTrue(savedData.contains("Pos"), "saved fish fixture must serialize vanilla fish data");
        savedData.putString("id", sourceType == EntityType.SALMON ? "minecraft:salmon" : "minecraft:cod");
        Entity loadedFish = EntityType.loadEntityRecursive(savedData, helper.getLevel(), entity -> entity);
        helper.assertTrue(loadedFish instanceof Mob, "saved fish fixture must deserialize a vanilla fish");
        helper.getLevel().addFreshEntity(loadedFish);
    }

    private static MobSpawnEvent.FinalizeSpawn newFinalizeSpawn(GameTestHelper helper, Mob mob,
                                                                  BlockPos position, MobSpawnType reason,
                                                                  net.minecraft.nbt.CompoundTag spawnTag) {
        return new MobSpawnEvent.FinalizeSpawn(
                mob,
                helper.getLevel(),
                position.getX(),
                position.getY(),
                position.getZ(),
                helper.getLevel().getCurrentDifficultyAt(position),
                reason,
                null,
                spawnTag,
                null
        );
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

    private static void prepareActualSourceWaterVolume(GameTestHelper helper) {
        prepareWaterVolume(helper);
        for (int x = 11; x <= 12; x++) {
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
