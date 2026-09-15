package tfar.bensfintasticsharks.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import tfar.bensfintasticsharks.dive.DiveOxygenManager;
import tfar.bensfintasticsharks.dive.DiveSuitEligibility;
import tfar.bensfintasticsharks.dive.DiveTravelController;
import tfar.bensfintasticsharks.init.ModItems;
import tfar.bensfintasticsharks.mixin.LivingEntityJumpingAccessor;

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

    private static void equipFullSuit(Player player) {
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.DIVE_HELMET));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.DIVE_CHESTPLATE));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.DIVE_LEGGINGS));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.DIVE_BOOTS));
    }
}
