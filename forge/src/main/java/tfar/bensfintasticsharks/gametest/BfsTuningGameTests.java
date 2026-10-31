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
import tfar.bensfintasticsharks.entity.AquaticMovement;
import tfar.bensfintasticsharks.entity.MovementIntentOverrides;
import tfar.bensfintasticsharks.entity.SharkSwimmingMoveControl;
import tfar.bensfintasticsharks.entity.SpeciesSettingsService;
import tfar.bensfintasticsharks.init.ModEntityTypes;

import java.util.Map;

@GameTestHolder("bfstuning")
@PrefixGameTestTemplate(false)
public final class BfsTuningGameTests {
    @GameTest(template = "bfsmovementoraclegametests.empty", batch = "pursuit_speed", timeoutTicks = 60)
    public static void tunedPursuitRetainsSpeedDuringLevelAscentAndDescent(GameTestHelper helper) {
        prepareWater(helper);
        Mob shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK, new BlockPos(10, 10, 10));
        Mob prey = helper.spawn(EntityType.COD, new BlockPos(10, 10, 24));
        shark.setNoAi(true);
        prey.setNoAi(true);
        helper.runAfterDelay(4, () -> {
            try {
                applyPursuitSettings(helper, 8.0D, 8.0D);
                for (float pitch : new float[] {0, -45, 45}) {
                    Vec3 direction = AquaticMovement.forwardVector(0, pitch);
                    Vec3 moved = configuredRouteStep(helper, shark, prey, pitch, direction.scale(3.0D));
                    helper.assertTrue(Math.abs(moved.length() - 0.4D) < 0.002D,
                            "An unobstructed pursuit must retain eight blocks per second at pitch "
                                    + pitch + ". Actual " + moved.length() * 20.0D);
                }
                helper.succeed();
            } finally {
                shark.discard();
                prey.discard();
                SpeciesSettingsService.resetSession();
            }
        });
    }

    @GameTest(template = "bfsmovementoraclegametests.empty", batch = "pursuit_vertical_turn", timeoutTicks = 60)
    public static void tunedVerticalPursuitKeepsMovingWhileTilting(GameTestHelper helper) {
        prepareWater(helper);
        Mob shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK, new BlockPos(10, 10, 10));
        Mob prey = helper.spawn(EntityType.COD, new BlockPos(10, 18, 10));
        shark.setNoAi(true);
        prey.setNoAi(true);
        helper.runAfterDelay(4, () -> {
            try {
                applyPursuitSettings(helper, 4.0D, 8.0D);
                for (int sign : new int[] {-1, 1}) {
                    Vec3 moved = configuredRouteStep(helper, shark, prey, sign * 20.0F,
                            new Vec3(0, -sign * 6.0D, 0));
                    helper.assertTrue(moved.y * -sign > 0.1D,
                            "Vertical pursuit must not creep while the pitch approaches its limit. Actual " + moved
                                    + ", controller " + ((SharkSwimmingMoveControl) shark.getMoveControl()).snapshot());
                    helper.assertTrue(moved.horizontalDistance() < 0.001D,
                            "A directly vertical pursuit must not drift sideways.");
                }
                helper.succeed();
            } finally {
                shark.discard();
                prey.discard();
                SpeciesSettingsService.resetSession();
            }
        });
    }

    @GameTest(template = "bfsmovementoraclegametests.empty", batch = "pursuit_depth_exit", timeoutTicks = 60)
    public static void reachingPreyDepthDoesNotBrakeHorizontalPursuit(GameTestHelper helper) {
        prepareWater(helper);
        Mob shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK, new BlockPos(10, 10, 10));
        Mob prey = helper.spawn(EntityType.COD, new BlockPos(10, 10, 24));
        shark.setNoAi(true);
        prey.setNoAi(true);
        helper.runAfterDelay(4, () -> {
            try {
                applyPursuitSettings(helper, 8.0D, 12.0D);
                for (int sign : new int[] {-1, 1}) {
                    Vec3 moved = configuredRouteStep(helper, shark, prey, sign * 45.0F,
                            new Vec3(0, -sign * 0.1D, 8));
                    helper.assertTrue(moved.horizontalDistance() > 0.27D,
                            "Reaching the target depth must preserve horizontal pursuit. Actual " + moved);
                    helper.assertTrue(moved.y * -sign > 0 && Math.abs(moved.y) <= 0.10001D,
                            "Vertical motion must stop at the requested depth without overshooting.");
                }
                helper.succeed();
            } finally {
                shark.discard();
                prey.discard();
                SpeciesSettingsService.resetSession();
            }
        });
    }

    private static void applyPursuitSettings(GameTestHelper helper, double horizontal, double vertical) {
        var result = SpeciesSettingsService.apply(SpeciesSettingsService.revision(), "oceanic_whitetip_shark",
                Map.of(SpeciesSettingsService.Field.HORIZONTAL_SPEED, horizontal / 2.0D,
                        SpeciesSettingsService.Field.VERTICAL_SPEED, vertical / 2.0D,
                        SpeciesSettingsService.Field.HORIZONTAL_SPRINT, 2.0D,
                        SpeciesSettingsService.Field.VERTICAL_SPRINT, 2.0D,
                        SpeciesSettingsService.Field.TILT_UP, 45.0D,
                        SpeciesSettingsService.Field.TILT_DOWN, 45.0D,
                        SpeciesSettingsService.Field.TILT_RATE, 60.0D));
        helper.assertTrue(result.applied(), "Pursuit fixture settings must apply.");
    }

    @GameTest(template = "bfsmovementoraclegametests.empty", batch = "pursuit_live_travel", timeoutTicks = 60)
    public static void sharkTravelMaintainsPursuitAndStillBrakesForPreyAndWalls(GameTestHelper helper) {
        prepareWater(helper);
        Mob shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK, new BlockPos(10, 10, 6));
        Mob prey = helper.spawn(EntityType.COD, new BlockPos(10, 10, 24));
        shark.setNoAi(true);
        prey.setNoAi(true);
        helper.runAfterDelay(4, () -> {
            try {
                applyPursuitSettings(helper, 8.0D, 8.0D);
                var control = (SharkSwimmingMoveControl) shark.getMoveControl();
                Vec3 origin = Vec3.atCenterOf(helper.absolutePos(new BlockPos(10, 10, 6)));
                for (float pitch : new float[] {0, -45, 45}) {
                    shark.getNavigation().stop();
                    control.resetFixtureState();
                    shark.moveTo(origin.x, origin.y, origin.z, 0, pitch);
                    shark.setDeltaMovement(Vec3.ZERO);
                    shark.setTarget(prey);
                    shark.setNoAi(false);
                    Vec3 direction = AquaticMovement.forwardVector(0, pitch);
                    for (int tick = 0; tick < 15; tick++) {
                        Vec3 destination = shark.position().add(direction.scale(5.0D));
                        prey.moveTo(destination.x, destination.y, destination.z);
                        control.setWantedPosition(destination.x, destination.y, destination.z, 1.0D);
                        control.tick();
                        Vec3 before = shark.position();
                        shark.travel(new Vec3(0, 0, 1));
                        if (tick >= 5) {
                            double speed = shark.position().distanceTo(before) * 20.0D;
                            helper.assertTrue(Math.abs(speed - 8.0D) < 0.05D,
                                    "Real shark travel must sustain configured pursuit at pitch " + pitch
                                            + ". Actual speed " + speed);
                        }
                    }
                }
                shark.getNavigation().stop();
                control.resetFixtureState();
                shark.moveTo(origin.x, origin.y, origin.z, 0, 0);
                shark.setDeltaMovement(Vec3.ZERO);
                prey.moveTo(origin.x, origin.y, origin.z + 1);
                control.setWantedPosition(prey.getX(), prey.getY(), prey.getZ(), 1.0D);
                control.tick();
                shark.travel(new Vec3(0, 0, 1));
                helper.assertTrue(shark.position().distanceTo(origin) < 0.01D,
                        "A fresh pursuit inside bite range must not accelerate into prey.");
                prey.moveTo(origin.x, origin.y, origin.z + 8);
                helper.setBlock(new BlockPos(10, 10, 7), Blocks.STONE.defaultBlockState());
                shark.getNavigation().stop();
                control.resetFixtureState();
                control.setWantedPosition(prey.getX(), prey.getY(), prey.getZ(), 1.0D);
                control.tick();
                shark.travel(new Vec3(0, 0, 1));
                helper.assertTrue(shark.position().distanceTo(origin) < 0.01D,
                        "Configured pursuit must preserve the obstacle clearance check.");
                helper.succeed();
            } finally {
                shark.discard();
                prey.discard();
                SpeciesSettingsService.resetSession();
            }
        });
    }

    private static Vec3 configuredRouteStep(GameTestHelper helper, Mob shark, Mob prey,
                                             float pitch, Vec3 destinationOffset) {
        var control = (SharkSwimmingMoveControl) shark.getMoveControl();
        shark.getNavigation().stop();
        control.resetFixtureState();
        Vec3 origin = Vec3.atCenterOf(helper.absolutePos(new BlockPos(10, 10, 10)));
        shark.moveTo(origin.x, origin.y, origin.z, 0, pitch);
        shark.setDeltaMovement(Vec3.ZERO);
        shark.setTarget(prey);
        Vec3 destination = origin.add(destinationOffset);
        control.setWantedPosition(destination.x, destination.y, destination.z, 1.0D);
        control.tick();
        control.travel(1.0D, 0.82D, 0.75D, 0.0D, new Vec3(0, 0, 1));
        return shark.position().subtract(origin);
    }

    @GameTest(template = "bfsmovementoraclegametests.empty", batch = "tilt_commands", timeoutTicks = 60)
    public static void tiltCommandSupportsWildcardNamespacedTargetsAndReset(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        var source = server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
        try {
            helper.assertTrue(server.getCommands().getDispatcher().execute("bfs debug settilt * 45 35 60", source) == 10,
                    "Tilt wildcard must apply to all ten supported swimmers.");
            helper.assertTrue(server.getCommands().getDispatcher().execute(
                    "bfs debug settilt bfs:oceanic_whitetip_shark 30 50 120", source) == 1,
                    "Namespaced tilt command must accept the species.");
            helper.assertTrue(SpeciesSettingsService.resolve("oceanic_whitetip_shark")
                            .field(SpeciesSettingsService.Field.TILT_RATE).value() == 120.0D,
                    "Effective tilt rate must match command input.");
            helper.assertTrue(server.getCommands().getDispatcher().execute(
                    "bfs debug settilt oceanic_whitetip_shark reset", source) == 1,
                    "Tilt reset must restore the species defaults.");
            helper.assertTrue("server_config".equals(SpeciesSettingsService.resolve("oceanic_whitetip_shark")
                            .field(SpeciesSettingsService.Field.TILT_RATE).source()),
                    "Reset must remove session tilt settings.");
            helper.succeed();
        } catch (Exception exception) {
            helper.fail("Tilt command failed: " + exception.getMessage());
        } finally {
            SpeciesSettingsService.resetSession();
        }
    }

    @GameTest(template = "bfsmovementoraclegametests.empty", batch = "tilt_travel", timeoutTicks = 60)
    public static void tiltChangesRealUpwardAndDownwardSwimmingAndPursuit(GameTestHelper helper) {
        prepareWater(helper);
        Mob shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK, new BlockPos(10, 10, 10));
        Mob prey = helper.spawn(EntityType.COD, new BlockPos(10, 16, 24));
        shark.setNoAi(true);
        prey.setNoAi(true);
        helper.runAfterDelay(4, () -> {
            try {
                for (boolean sprint : new boolean[] {false, true}) {
                    for (int direction : new int[] {-1, 1}) {
                        float peak = tiltRoute(helper, shark, prey, sprint, direction, 60.0D);
                        helper.assertTrue(peak > 12.0F && peak <= 35.001F,
                                "Tilt must respond during upward and downward travel. Sprint " + sprint
                                        + ", direction " + direction + ", peak " + peak
                                        + ", controller " + ((SharkSwimmingMoveControl) shark.getMoveControl()).snapshot());
                    }
                }
                helper.assertTrue(tiltRoute(helper, shark, prey, true, -1, 0.0D) < 0.001F,
                        "Zero tilt rate must hold the starting pitch.");
                helper.succeed();
            } finally {
                shark.discard();
                prey.discard();
                SpeciesSettingsService.resetSession();
            }
        });
    }

    private static float tiltRoute(GameTestHelper helper, Mob shark, Mob prey, boolean sprint,
                                   int direction, double rate) {
        var result = SpeciesSettingsService.apply(SpeciesSettingsService.revision(), "oceanic_whitetip_shark", Map.of(
                SpeciesSettingsService.Field.HORIZONTAL_SPEED, 2.0D,
                SpeciesSettingsService.Field.VERTICAL_SPEED, 2.0D,
                SpeciesSettingsService.Field.HORIZONTAL_SPRINT, 2.0D,
                SpeciesSettingsService.Field.VERTICAL_SPRINT, 6.0D,
                SpeciesSettingsService.Field.TILT_UP, 35.0D,
                SpeciesSettingsService.Field.TILT_DOWN, 35.0D,
                SpeciesSettingsService.Field.TILT_RATE, rate));
        helper.assertTrue(result.applied(), "Tilt fixture settings must apply.");
        var control = (SharkSwimmingMoveControl) shark.getMoveControl();
        shark.getNavigation().stop();
        control.resetFixtureState();
        Vec3 origin = Vec3.atCenterOf(helper.absolutePos(new BlockPos(10, 10, 10)));
        shark.moveTo(origin.x, origin.y, origin.z, 0.0F, 0.0F);
        shark.setDeltaMovement(Vec3.ZERO);
        shark.setTarget(sprint ? prey : null);
        control.setWantedPosition(origin.x, origin.y - direction * 6.0D, origin.z + 12.0D, 1.0D);
        float peak = 0;
        for (int tick = 0; tick < 20; tick++) {
            control.tick();
            control.travel(0.02D, 0.82D, 0.75D, 0.0D, new Vec3(0, 0, 1));
            peak = Math.max(peak, shark.getXRot() * direction);
        }
        return peak;
    }

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
        shark.getNavigation().stop();
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
