package tfar.bensfintasticsharks.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import tfar.bensfintasticsharks.debug.BfsDebugManager;
import tfar.bensfintasticsharks.dive.DiveOxygenManager;
import tfar.bensfintasticsharks.dive.DiveSuitEligibility;
import tfar.bensfintasticsharks.dive.DiveTravelController;
import tfar.bensfintasticsharks.init.ModItems;
import tfar.bensfintasticsharks.mixin.LivingEntityJumpingAccessor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@GameTestHolder("bfsdive")
public final class BfsDiveGameTests {

    private BfsDiveGameTests() {
    }

    @GameTest(template = "empty", batch = "bfs_dive", timeoutTicks = 40)
    public static void fullSuitEligibilityAndLandlikeTravel(GameTestHelper helper) {
        prepareWater(helper);
        Player player = makePlayer(helper, new BlockPos(3, 3, 3));
        equipFullSuit(player);
        helper.runAfterDelay(10, () -> {
            player.tick();
            DiveSuitEligibility.Result result = DiveSuitEligibility.evaluate(player);
            helper.assertTrue(result.eligible(),
                    "a full suit in water must activate dive eligibility");

            player.setDeltaMovement(Vec3.ZERO);
            ((LivingEntityJumpingAccessor) player).bfs$setJumping(false);
            helper.assertTrue(DiveTravelController.apply(player, Vec3.ZERO),
                    "eligible player travel must be owned by the dive controller");
            helper.assertTrue(player.getDeltaMovement().y <= 0.0D,
                    "landlike travel must apply downward gravity when no jump is pressed");

            player.setOnGround(true);
            ((LivingEntityJumpingAccessor) player).bfs$setJumping(true);
            player.setDeltaMovement(Vec3.ZERO);
            DiveTravelController.apply(player, Vec3.ZERO);
            helper.assertTrue(player.getDeltaMovement().y > 0.20D,
                    "one grounded jump edge must use the moonlike impulse");
            ((LivingEntityJumpingAccessor) player).bfs$setJumping(false);
            player.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_dive", timeoutTicks = 40)
    public static void oxygenReserveUsesOnlySubmergedTicksAndRealAirRefill(GameTestHelper helper) {
        prepareWater(helper);
        Player player = makePlayer(helper, new BlockPos(3, 3, 3));
        equipFullSuit(player);
        helper.runAfterDelay(10, () -> {
            player.tick();
            for (int tick = 0; tick < DiveOxygenManager.MAX_RESERVE_TICKS; tick++) {
                player.tickCount = tick + 1;
                DiveOxygenManager.tick(player);
            }
            helper.assertTrue(DiveOxygenManager.readReserve(player) == 0,
                    "the protected reserve must end after exactly 6000 submerged ticks");
            helper.assertTrue(player.getAirSupply() == 0,
                    "normal drowning air must begin when the custom reserve is empty");

            player.setPos(3.5D, 100.0D, 3.5D);
            player.tick();
            for (int tick = 0; tick < 300; tick++) {
                player.tickCount = DiveOxygenManager.MAX_RESERVE_TICKS + tick + 1;
                DiveOxygenManager.tick(player);
            }
            helper.assertTrue(DiveOxygenManager.readReserve(player) == DiveOxygenManager.MAX_RESERVE_TICKS,
                    "real air must refill the reserve by 20 ticks for 300 ticks");

            player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
            int retained = DiveOxygenManager.readReserve(player);
            DiveOxygenManager.tick(player);
            helper.assertTrue(DiveOxygenManager.readReserve(player) == retained,
                    "removing one piece must not refill or reset the reserve");
            player.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_dive", timeoutTicks = 40)
    public static void oxygenSchemaRepairsCorruptionAndPreservesNewerState(GameTestHelper helper) {
        prepareWater(helper);
        Player missing = makePlayer(helper, new BlockPos(2, 3, 2));
        Player corrupt = makePlayer(helper, new BlockPos(4, 3, 2));
        Player newer = makePlayer(helper, new BlockPos(6, 3, 2));
        equipFullSuit(missing);
        equipFullSuit(corrupt);
        equipFullSuit(newer);
        corrupt.getPersistentData().putInt("bfs_dive_oxygen_schema", DiveOxygenManager.SCHEMA);
        corrupt.getPersistentData().putBoolean("bfs_dive_oxygen_initialized", true);
        corrupt.getPersistentData().putInt("bfs_dive_oxygen_ticks", -7);
        newer.getPersistentData().putInt("bfs_dive_oxygen_schema", DiveOxygenManager.SCHEMA + 1);
        newer.getPersistentData().putBoolean("bfs_dive_oxygen_initialized", true);
        newer.getPersistentData().putInt("bfs_dive_oxygen_ticks", 1234);
        helper.runAfterDelay(10, () -> {
            missing.setPos(helper.absolutePos(new BlockPos(2, 2, 2)).getCenter());
            corrupt.setPos(helper.absolutePos(new BlockPos(4, 2, 2)).getCenter());
            newer.setPos(helper.absolutePos(new BlockPos(6, 2, 2)).getCenter());
            missing.setDeltaMovement(Vec3.ZERO);
            corrupt.setDeltaMovement(Vec3.ZERO);
            newer.setDeltaMovement(Vec3.ZERO);
            DiveOxygenManager.tick(missing);
            helper.assertTrue(missing.getPersistentData().getBoolean("bfs_dive_oxygen_initialized")
                            && missing.getPersistentData().getInt("bfs_dive_oxygen_ticks") >= 0
                            && missing.getPersistentData().getInt("bfs_dive_oxygen_ticks")
                            <= DiveOxygenManager.MAX_RESERVE_TICKS,
                    "missing state must initialize once within the supported reserve range");
            DiveOxygenManager.tick(corrupt);
            helper.assertTrue(DiveOxygenManager.readReserve(corrupt) == 0,
                    "established supported corruption must repair to zero, value="
                            + DiveOxygenManager.readReserve(corrupt));
            DiveOxygenManager.tick(newer);
            helper.assertTrue(newer.getPersistentData().getInt("bfs_dive_oxygen_schema")
                            == DiveOxygenManager.SCHEMA + 1
                            && newer.getPersistentData().getInt("bfs_dive_oxygen_ticks") == 1234,
                    "newer schema state must remain opaque and unchanged");
            helper.assertTrue(!DiveOxygenManager.supportsSchema(newer),
                    "newer schema must be rejected without downgrade");
            missing.remove(Entity.RemovalReason.DISCARDED);
            corrupt.remove(Entity.RemovalReason.DISCARDED);
            newer.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_dive", timeoutTicks = 40)
    public static void waterWorkKeepsMatchingBreakSpeed(GameTestHelper helper) {
        prepareWater(helper);
        Player player = makePlayer(helper, new BlockPos(3, 2, 3));
        equipFullSuit(player);
        BlockPos target = helper.absolutePos(new BlockPos(3, 2, 3));
        helper.runAfterDelay(10, () -> {
            player.setOnGround(true);
            float submerged = player.getDigSpeed(Blocks.STONE.defaultBlockState(), target);
            player.setPos(helper.absolutePos(new BlockPos(3, 20, 3)).getCenter());
            float dry = player.getDigSpeed(Blocks.STONE.defaultBlockState(), target);
            helper.assertTrue(Math.abs(submerged - dry) <= 0.0001F,
                    "a full dive suit must preserve the same break speed in water and air");
            player.remove(Entity.RemovalReason.DISCARDED);
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_dive_lifecycle", timeoutTicks = 7600)
    public static void continuousReserveIntervalUsesRealAirRefill(GameTestHelper helper) {
        prepareWater(helper);
        helper.runAfterDelay(10, () -> {
            ServerPlayer player = makeServerPlayer(helper, new BlockPos(3, 3, 3));
            equipFullSuit(player);
            player.setNoGravity(true);
            player.setOnGround(true);
            player.doTick();
            DiveSuitEligibility.Result initialEligibility = DiveSuitEligibility.evaluate(player);
            helper.assertTrue(initialEligibility.fullSuit() && initialEligibility.submergedEyes(),
                    "server player must start fully suited with submerged eyes");
            BfsDebugManager.stop("dive_lifecycle_setup");
            CommandSourceStack source = helper.getLevel().getServer().createCommandSourceStack()
                    .withLevel(helper.getLevel())
                    .withPosition(player.position())
                    .withPermission(4);
            BfsDebugManager.StartResult started = BfsDebugManager.start(source, "dive", 7200, java.util.List.of(player));
            helper.assertTrue(started.started(), "the continuous dive capture must start for one explicit player");
            LifecycleWitness witness = new LifecycleWitness(helper, player,
                    started.activeSession().outputPath(), helper.getLevel().getGameTime());
            helper.runAfterDelay(20, witness::step);
        });
    }

    private static void prepareWater(GameTestHelper helper) {
        for (int x = 0; x < 8; x++) {
            for (int z = 0; z < 8; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.STONE.defaultBlockState());
                for (int y = 1; y < 11; y++) {
                    helper.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState());
                }
            }
        }
    }

    private static Player makePlayer(GameTestHelper helper, BlockPos localPosition) {
        Player player = new Player(helper.getLevel(), BlockPos.ZERO, 0.0F,
                new GameProfile(UUID.randomUUID(), "dive-fixture")) {
            @Override
            public boolean isCreative() {
                return false;
            }

            @Override
            public boolean isSpectator() {
                return false;
            }
        };
        player.setPos(helper.absolutePos(localPosition).getCenter());
        helper.getLevel().addFreshEntity(player);
        return player;
    }

    private static ServerPlayer makeServerPlayer(GameTestHelper helper, BlockPos localPosition) {
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(UUID.randomUUID(), "dive-lifecycle"));
        player.setPos(helper.absolutePos(localPosition).getCenter());
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        helper.getLevel().addNewPlayer(player);
        return player;
    }

    private static void equipFullSuit(Player player) {
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.DIVE_HELMET));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.DIVE_CHESTPLATE));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.DIVE_LEGGINGS));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.DIVE_BOOTS));
    }

    private static final class LifecycleWitness {
        private enum Stage {
            SUBMERGED,
            LEAVING,
            REFILLING,
            HOLDING_AIR,
            COMPLETE
        }

        private final GameTestHelper helper;
        private final ServerPlayer player;
        private final Path output;
        private final long captureStart;
        private Stage stage = Stage.SUBMERGED;
        private int protectedTicks;
        private int emptyTicks;
        private int refillTicks;
        private int previousReserve = -1;
        private boolean sawFirstEmpty;

        private LifecycleWitness(GameTestHelper helper, ServerPlayer player, Path output, long captureStart) {
            this.helper = helper;
            this.player = player;
            this.output = output;
            this.captureStart = captureStart;
        }

        private void step() {
            try {
                if (stage == Stage.SUBMERGED) {
                    player.setDeltaMovement(Vec3.ZERO);
                    player.doTick();
                    if (DiveOxygenManager.readReserve(player) == 0) {
                        DiveOxygenManager.tick(player);
                    }
                    observeSubmerged();
                } else if (stage == Stage.LEAVING) {
                    player.setPos(player.getX() + 8.0D, player.getY(), player.getZ());
                    player.doTick();
                    player.doTick();
                    if (!DiveSuitEligibility.evaluate(player).submergedEyes()) {
                        stage = Stage.REFILLING;
                        previousReserve = 0;
                        observeRefill();
                    }
                } else if (stage == Stage.REFILLING) {
                    player.setDeltaMovement(Vec3.ZERO);
                    player.doTick();
                    observeRefill();
                } else if (stage == Stage.HOLDING_AIR) {
                    player.setDeltaMovement(Vec3.ZERO);
                }

                if (helper.getLevel().getGameTime() >= captureStart + 7200L) {
                    finish();
                    return;
                }
                helper.runAfterDelay(1, this::step);
            } catch (RuntimeException failure) {
                if (BfsDebugManager.status().active()) {
                    BfsDebugManager.stop("dive_lifecycle_failure");
                }
                if (!player.isRemoved()) {
                    player.remove(Entity.RemovalReason.DISCARDED);
                }
                helper.fail("continuous dive lifecycle witness failed: " + failure.getMessage());
            }
        }

        private void observeSubmerged() {
            DiveSuitEligibility.Result eligibility = DiveSuitEligibility.evaluate(player);
            helper.assertTrue(eligibility.fullSuit() && eligibility.submergedEyes(),
                    "reserve depletion must remain fully suited and eye submerged");
            int reserve = DiveOxygenManager.readReserve(player);
            if (previousReserve < 0) {
                previousReserve = reserve;
            } else if (reserve == previousReserve) {
                return;
            } else if (reserve == previousReserve - 1) {
                previousReserve = reserve;
            } else if (reserve == 0 && previousReserve == 0) {
                emptyTicks++;
                if (!sawFirstEmpty) {
                    sawFirstEmpty = true;
                    helper.assertTrue(protectedTicks == DiveOxygenManager.MAX_RESERVE_TICKS,
                            "the first empty submerged tick must follow exactly 6000 protected ticks");
                    helper.assertTrue(player.getAirSupply() == 0,
                            "vanilla air must be empty on the first custom reserve empty tick");
                    stage = Stage.LEAVING;
                }
                return;
            } else {
                helper.fail("reserve changed by more than one tick during continuous depletion, previous="
                        + previousReserve + ", current=" + reserve);
            }
            protectedTicks++;
            helper.assertTrue(reserve >= 0 && reserve <= DiveOxygenManager.MAX_RESERVE_TICKS,
                    "reserve must remain within schema bounds during depletion");
            if (reserve == 0) {
                sawFirstEmpty = true;
                helper.assertTrue(protectedTicks == DiveOxygenManager.MAX_RESERVE_TICKS,
                        "reserve must reach zero after exactly 6000 protected ticks");
                helper.assertTrue(player.getAirSupply() == 0,
                        "vanilla air must be empty when the custom reserve reaches zero");
                emptyTicks++;
                stage = Stage.LEAVING;
            }
        }

        private void observeRefill() {
            DiveSuitEligibility.Result eligibility = DiveSuitEligibility.evaluate(player);
            helper.assertTrue(eligibility.fullSuit() && !eligibility.submergedEyes(),
                    "reserve refill must occur only in real air");
            int reserve = DiveOxygenManager.readReserve(player);
            if (reserve == previousReserve + DiveOxygenManager.REAL_AIR_REFILL_TICKS) {
                refillTicks++;
                previousReserve = reserve;
            } else if (reserve == previousReserve && reserve == DiveOxygenManager.MAX_RESERVE_TICKS) {
                stage = Stage.HOLDING_AIR;
                helper.assertTrue(refillTicks == 300,
                        "real air must refill exactly 300 ticks at 20 reserve ticks per tick");
                return;
            } else {
                helper.fail("reserve refill did not advance by exactly 20 ticks, previous="
                        + previousReserve + ", current=" + reserve);
            }
            helper.assertTrue(reserve <= DiveOxygenManager.MAX_RESERVE_TICKS,
                    "reserve refill must remain capped at 6000 ticks");
            if (reserve == DiveOxygenManager.MAX_RESERVE_TICKS) {
                helper.assertTrue(refillTicks == 300,
                        "real air must reach full reserve after exactly 300 refill ticks");
                stage = Stage.HOLDING_AIR;
            }
        }

        private void finish() {
            helper.assertTrue(stage == Stage.HOLDING_AIR,
                    "the complete capture must finish after the reserve has refilled in real air");
            helper.assertTrue(protectedTicks == DiveOxygenManager.MAX_RESERVE_TICKS
                            && emptyTicks >= 1 && refillTicks == 300,
                    "the continuous witness must include 6000 protected, empty, and 300 refill ticks");
            BfsDebugManager.stop("dive_lifecycle_complete");
            BfsDebugManager.StopSummary summary = BfsDebugManager.status().lastStop();
            helper.assertTrue(!summary.incomplete() && summary.dropped() == 0,
                    "the continuous dive capture must finish complete without dropped records");
            helper.assertTrue(Files.exists(output), "the continuous dive capture must write its output file");
            if (!player.isRemoved()) {
                player.remove(Entity.RemovalReason.DISCARDED);
            }
            stage = Stage.COMPLETE;
            helper.succeed();
        }
    }
}
