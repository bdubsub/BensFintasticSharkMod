package tfar.bensfintasticsharks.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import tfar.bensfintasticsharks.entity.MovementIntentOverrides;
import tfar.bensfintasticsharks.entity.SharkSwimmingMoveControl;
import tfar.bensfintasticsharks.entity.SpeciesSettingsService;
import tfar.bensfintasticsharks.init.ModEntityTypes;

import java.util.Map;

@GameTestHolder("bfstuning")
@PrefixGameTestTemplate(false)
public final class BfsTuningGameTests {
    @GameTest(template = "bfsmovementoraclegametests.empty", timeoutTicks = 60)
    public static void commandsAcceptLargeSpeedAndSprintValues(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        var source = server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
        try {
            for (String command : new String[] {"bfs debug setspeed * 100 250",
                    "bfs debug setsprint * 100 1000",
                    "bfs debug setspeed bensfintasticsharks:oceanic_whitetip_shark 100 250",
                    "bfs debug setsprint bfs:oceanic_whitetip_shark 100 1000",
                    "bfs debug settings set oceanic_whitetip_shark vertical_speed 250"}) {
                int result = server.getCommands().getDispatcher().execute(command, source);
                helper.assertTrue(result > 0, "Large tuning values must apply through the command dispatcher.");
            }
            var shark = SpeciesSettingsService.resolve("oceanic_whitetip_shark");
            helper.assertTrue(shark.field(SpeciesSettingsService.Field.VERTICAL_SPEED).value() == 250.0D
                            && shark.field(SpeciesSettingsService.Field.VERTICAL_SPRINT).value() == 1000.0D,
                    "Command readback must retain values above the former limits.");
            helper.succeed();
        } catch (Exception exception) {
            helper.fail("Tuning command failed: " + exception.getMessage());
        } finally {
            SpeciesSettingsService.resetSession();
        }
    }

    @GameTest(template = "bfsmovementoraclegametests.empty", timeoutTicks = 60)
    public static void ordinaryPursuitUsesIndependentAxisValues(GameTestHelper helper) {
        prepareWater(helper);
        Mob shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK, new BlockPos(10, 8, 10));
        Mob prey = helper.spawn(EntityType.COD, new BlockPos(10, 14, 24));
        shark.setNoAi(true);
        prey.setNoAi(true);
        helper.runAfterDelay(4, () -> {
            try {
                Vec3 normal = step(helper, shark, prey, -30.0F, 5.0D, false);
                Vec3 faster = step(helper, shark, prey, -30.0F, 15.0D, false);
                helper.assertTrue(Math.abs(normal.y - 0.2D) < 0.001D,
                        "Ordinary upward pursuit must use 5 times 1.6 vertical speed. Actual " + normal);
                helper.assertTrue(Math.abs(faster.y - 0.6D) < 0.001D,
                        "Increasing vertical speed must change real travel. Actual " + faster);
                helper.assertTrue(Math.abs(normal.z - faster.z) < 0.001D,
                        "Vertical tuning must not change horizontal propulsion.");
                Vec3 descending = step(helper, shark, prey, 30.0F, 15.0D, false);
                helper.assertTrue(Math.abs(descending.y + 0.6D) < 0.001D,
                        "Ordinary downward pursuit must use the same independent settings.");
                helper.succeed();
            } finally {
                shark.discard();
                prey.discard();
                SpeciesSettingsService.resetSession();
            }
        });
    }

    @GameTest(template = "bfsmovementoraclegametests.empty", timeoutTicks = 60)
    public static void alignedVerticalRouteReleasesTurningThrottle(GameTestHelper helper) {
        prepareWater(helper);
        Mob shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK, new BlockPos(10, 10, 10));
        Mob prey = helper.spawn(EntityType.COD, new BlockPos(10, 18, 10));
        shark.setNoAi(true);
        prey.setNoAi(true);
        helper.runAfterDelay(4, () -> {
            try {
                Vec3 upward = step(helper, shark, prey, -90.0F, 5.0D, true);
                Vec3 downward = step(helper, shark, prey, 90.0F, 5.0D, true);
                helper.assertTrue(upward.y > 0.3D && downward.y < -0.3D,
                        "An aligned vertical route must not retain the turning throttle. Up "
                                + upward + ", down " + downward);
                helper.succeed();
            } finally {
                shark.discard();
                prey.discard();
                SpeciesSettingsService.resetSession();
            }
        });
    }

    private static Vec3 step(GameTestHelper helper, Mob shark, Mob prey, float pitch,
                             double vertical, boolean route) {
        var result = SpeciesSettingsService.apply(SpeciesSettingsService.revision(), "oceanic_whitetip_shark",
                Map.of(SpeciesSettingsService.Field.HORIZONTAL_SPEED, 4.0D,
                        SpeciesSettingsService.Field.VERTICAL_SPEED, vertical,
                        SpeciesSettingsService.Field.HORIZONTAL_SPRINT, 0.8D,
                        SpeciesSettingsService.Field.VERTICAL_SPRINT, 1.6D));
        helper.assertTrue(result.applied(), "Pursuit settings must apply.");
        helper.assertTrue(SpeciesSettingsService.hasMovementOverrides(shark),
                "A real settings override must select configured movement.");
        helper.assertTrue(!MovementIntentOverrides.active(shark), "Use ordinary production movement.");
        var control = (SharkSwimmingMoveControl) shark.getMoveControl();
        control.resetFixtureState();
        Vec3 origin = Vec3.atCenterOf(helper.absolutePos(new BlockPos(10, 10, 10)));
        shark.moveTo(origin.x, origin.y, origin.z, 0.0F, pitch);
        shark.setDeltaMovement(Vec3.ZERO);
        shark.setTarget(prey);
        if (route) {
            control.setWantedPosition(origin.x, origin.y + (pitch < 0 ? 8 : -8), origin.z, 1.0D);
            control.tick();
        }
        control.travel(1.0D, 0.82D, 0.75D, 0.0D, new Vec3(0, 0, 1));
        return shark.position().subtract(origin);
    }

    private static void prepareWater(GameTestHelper helper) {
        for (int x = 0; x <= 30; x++) {
            for (int y = 0; y <= 22; y++) {
                for (int z = 0; z <= 30; z++) {
                    helper.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState());
                }
            }
        }
    }
}
