package tfar.bensfintasticsharks.gametest;

import com.google.gson.JsonParser;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.gametest.GameTestHolder;
import tfar.bensfintasticsharks.debug.BfsDebugManager;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.entity.AtlanticCodEntity;
import tfar.bensfintasticsharks.entity.AtlanticSalmonEntity;
import tfar.bensfintasticsharks.entity.BfsAquaticEntity;
import tfar.bensfintasticsharks.entity.MovementIntentOverrides;
import tfar.bensfintasticsharks.entity.PitchSwimmingMoveControl;
import tfar.bensfintasticsharks.entity.SharkSwimmingMoveControl;
import tfar.bensfintasticsharks.entity.SpeciesSettingsService;
import tfar.bensfintasticsharks.entity.SmartWaterAnimal;
import tfar.bensfintasticsharks.init.ModEntityTypes;

import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Numeric movement evidence through every current powered travel owner. */
@GameTestHolder("bensfintasticsharks")
public final class BfsMovementOracleGameTests {
    private static final Vec3 POSITIVE_DIRECTION = new Vec3(0.6D, 0.4D, 0.6928203230275509D).normalize();
    private static final Vec3 NEGATIVE_DIRECTION = new Vec3(-0.6D, -0.4D, 0.6928203230275509D).normalize();
    private static final List<EntityType<? extends Mob>> SPECIES = List.of(
            ModEntityTypes.GREAT_WHITE_SHARK,
            ModEntityTypes.GREAT_HAMMERHEAD_SHARK,
            ModEntityTypes.COMMON_THRESHER_SHARK,
            ModEntityTypes.SHORTFIN_MAKO_SHARK,
            ModEntityTypes.TIGER_SHARK,
            ModEntityTypes.OCEANIC_WHITETIP_SHARK,
            ModEntityTypes.SANDTIGER_SHARK,
            ModEntityTypes.BLACKTIP_REEF_SHARK,
            ModEntityTypes.ORCA,
            ModEntityTypes.BOTTLENOSE_DOLPHIN,
            ModEntityTypes.COMMON_OCTOPUS,
            ModEntityTypes.CARIBBEAN_REEF_OCTOPUS,
            ModEntityTypes.NAUTILUS,
            ModEntityTypes.GIANT_MORAY_EEL,
            ModEntityTypes.GREEN_SEA_TURTLE,
            ModEntityTypes.AMERICAN_LOBSTER,
            ModEntityTypes.COMMON_STINGRAY,
            ModEntityTypes.HARBOR_SEAL,
            ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH,
            ModEntityTypes.CANNONBALL_JELLYFISH,
            ModEntityTypes.ATLANTIC_COD,
            ModEntityTypes.ATLANTIC_SALMON);
    private static final List<Condition> CONDITIONS = List.of(
            new Condition("cruise_2_6", 2.0D, 6.0D, 1.0D, 1.0D, false),
            new Condition("cruise_6_6", 6.0D, 6.0D, 1.0D, 1.0D, false),
            new Condition("cruise_6_2", 6.0D, 2.0D, 1.0D, 1.0D, false),
            new Condition("pursuit_6_6", 6.0D, 6.0D, 1.0D, 1.0D, true),
            new Condition("pursuit_1_2_1", 6.0D, 6.0D, 1.2D, 1.0D, true),
            new Condition("pursuit_1_2_1_5", 6.0D, 6.0D, 1.2D, 1.5D, true),
            new Condition("cruise_1_2_1", 6.0D, 6.0D, 1.2D, 1.0D, false),
            new Condition("cruise_1_2_1_5", 6.0D, 6.0D, 1.2D, 1.5D, false));

    private BfsMovementOracleGameTests() {
    }

    @GameTest(template = "empty", batch = "bfs_movement_oracle", timeoutTicks = 3_000)
    public static void allSpeciesMovementOracle(GameTestHelper helper) {
        prepareOracleWaterVolume(helper);
        List<Probe> probes = new ArrayList<>();
        for (int index = 0; index < SPECIES.size(); index++) {
            Vec3 direction = index % 3 == 0 ? NEGATIVE_DIRECTION : POSITIVE_DIRECTION;
            int column = index % 6;
            int row = index / 6;
            double x = direction.x < 0 ? 116.0D - column * 10.0D : 8.0D + column * 10.0D;
            // GameTest's disposable flat world has a solid floor near y=0. Keep the
            // negative-heading probes high enough that the 200-tick sample cannot hit it.
            double y = 8.0D;
            double z = 8.0D + row * 12.0D;
            Mob mob = helper.spawn(SPECIES.get(index), new BlockPos((int) x, (int) y, (int) z));
            mob.getBrain().removeAllBehaviors();
            mob.goalSelector.removeAllGoals(goal -> true);
            mob.targetSelector.removeAllGoals(goal -> true);
            mob.setNoAi(true);
            mob.setPersistenceRequired();
            mob.setNoGravity(true);
            mob.noPhysics = true;
            Mob target = EntityType.SQUID.create(helper.getLevel());
            helper.assertTrue(target != null, "oracle target must construct");
            Vec3 start = helper.absolutePos(new BlockPos((int) x, (int) y, (int) z)).getCenter();
            mob.setPos(start);
            Vec3 targetPosition = start.add(direction.scale(8.0D));
            target.moveTo(targetPosition.x, targetPosition.y, targetPosition.z, 0.0F, 0.0F);
            target.setNoAi(true);
            target.setNoGravity(true);
            target.setInvulnerable(true);
            target.noPhysics = true;
            target.setPersistenceRequired();
            helper.getLevel().addFreshEntity(target);
            probes.add(new Probe(mob, target, direction, start));
        }
        new Runner(helper, probes).start();
    }

    @GameTest(template = "empty", batch = "bfs_movement_oracle", timeoutTicks = 240)
    public static void movementCaptureCoversEveryWriterAndExternalImpulse(GameTestHelper helper) {
        prepareOracleWaterVolume(helper);
        List<Mob> mobs = new ArrayList<>();
        for (int index = 0; index < SPECIES.size(); index++) {
            int x = 8 + (index % 6) * 10;
            int z = 8 + (index / 6) * 12;
            Mob mob = helper.spawn(SPECIES.get(index), new BlockPos(x, 20, z));
            mob.getBrain().removeAllBehaviors();
            mob.goalSelector.removeAllGoals(goal -> true);
            mob.targetSelector.removeAllGoals(goal -> true);
            mob.setNoAi(true);
            mob.setPersistenceRequired();
            mob.setNoGravity(true);
            mob.noPhysics = true;
            mobs.add(mob);
        }
        Mob cod = mobs.get(SPECIES.indexOf(ModEntityTypes.ATLANTIC_COD));
        MinecraftServer server = helper.getLevel().getServer();
        net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack()
                .withLevel(helper.getLevel())
                .withPosition(helper.absolutePos(new BlockPos(8, 20, 8)).getCenter())
                .withPermission(4);
        BfsDebugManager.stop("gametest_setup");
        helper.runAfterDelay(1, () -> {
            BfsDebugManager.StartResult started = BfsDebugManager.start(source, "movement", 40, mobs);
            helper.assertTrue(started.started() && started.activeSession().targetCount() == SPECIES.size(),
                    "writer capture must select all 22 species");
            helper.runAfterDelay(2, () -> {
                cod.setDeltaMovement(new Vec3(0.03D, 0.01D, 0.0D));
                helper.runAfterDelay(4, () -> {
                    BfsDebugManager.stop("gametest_writer_assertions");
                    verifyWriterCapture(helper, mobs, BfsDebugManager.status().lastStop().outputPath(), 40);
                });
            });
        });
    }

    private static void verifyWriterCapture(GameTestHelper helper, List<Mob> mobs, Path output,
                                            int remainingChecks) {
        helper.runAfterDelay(1, () -> {
            try {
                if (!Files.exists(output)) {
                    if (remainingChecks > 1) {
                        verifyWriterCapture(helper, mobs, output, remainingChecks - 1);
                    } else {
                        helper.fail("movement writer capture output was not written: " + output);
                    }
                    return;
                }
                List<com.google.gson.JsonObject> movement = Files.readAllLines(output).stream()
                        .map(JsonParser::parseString)
                        .map(json -> json.getAsJsonObject())
                        .filter(record -> "movement".equals(record.get("event").getAsString()))
                        .toList();
                Set<String> expectedTypes = new LinkedHashSet<>();
                for (Mob mob : mobs) {
                    expectedTypes.add(mob.getType().builtInRegistryHolder().key().location().toString());
                }
                Set<String> recordedTypes = new LinkedHashSet<>();
                Set<String> writers = new LinkedHashSet<>();
                boolean externalImpulse = false;
                for (com.google.gson.JsonObject record : movement) {
                    recordedTypes.add(record.get("entityType").getAsString());
                    String writer = record.get("movementWriter").getAsString();
                    helper.assertTrue(!writer.startsWith("unavailable:"),
                            "movement capture must expose a writer for " + record.get("entityType"));
                    writers.add(writer);
                    if (record.get("entityType").getAsString().endsWith(":atlantic_cod")) {
                        externalImpulse |= Math.abs(record.get("externalVelocityX").getAsDouble()) > 1.0e-6D
                                || Math.abs(record.get("externalVelocityY").getAsDouble()) > 1.0e-6D
                                || Math.abs(record.get("externalVelocityZ").getAsDouble()) > 1.0e-6D;
                    }
                }
                if (recordedTypes.size() < expectedTypes.size() || !recordedTypes.containsAll(expectedTypes)) {
                    if (remainingChecks > 1) {
                        verifyWriterCapture(helper, mobs, output, remainingChecks - 1);
                        return;
                    }
                    helper.fail("movement capture missed species writers, expected=" + expectedTypes
                            + ", recorded=" + recordedTypes + ", writers=" + writers);
                    return;
                }
                helper.assertTrue(!writers.isEmpty(), "movement capture must record at least one writer");
                helper.assertTrue(externalImpulse,
                        "movement capture must preserve a nonzero external impulse alongside powered motion");
                BfsDebugManager.StopSummary stop = BfsDebugManager.status().lastStop();
                helper.assertTrue(stop.accepted() > 0 && stop.dropped() == 0 && !stop.incomplete(),
                        "writer capture must finish complete, accepted=" + stop.accepted()
                                + ", dropped=" + stop.dropped() + ", reason=" + stop.incompleteReason());
                helper.succeed();
            } catch (Exception exception) {
                helper.fail("unable to verify movement writer capture: " + exception.getMessage());
            } finally {
                try {
                    Files.deleteIfExists(output);
                } catch (Exception ignored) {
                    // Cleanup is checked by the bounded test runner after assertions.
                }
                for (Mob mob : mobs) mob.discard();
            }
        });
    }

    private static void prepareOracleWaterVolume(GameTestHelper helper) {
        // Keep the fixture relative to the GameTest structure. Using absolute positions here
        // makes the water miss when the batch runner places the structure away from world zero.
        for (int x = 0; x <= 128; x++) {
            for (int z = 0; z <= 128; z++) {
                helper.setBlock(new BlockPos(x, 0, z), Blocks.SAND.defaultBlockState());
                for (int y = 1; y <= 20; y++) {
                    helper.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState());
                }
            }
        }
    }

    private static boolean isPitchProbe(Mob mob) {
        return mob instanceof AbstractSharkEntity<?> || mob instanceof AtlanticCodEntity
                || mob instanceof AtlanticSalmonEntity;
    }

    private record Condition(String id, double horizontal, double vertical,
                             double horizontalSprint, double verticalSprint, boolean sprint) {
    }

    private static final class Probe {
        private final Mob mob;
        private final Mob target;
        private final Vec3 direction;
        private final Vec3 origin;
        private final Map<String, Vec3> measured = new LinkedHashMap<>();
        private Vec3 lastPosition;
        private Vec3 displacement = Vec3.ZERO;
        private int samples;
        private float minPitch;
        private float maxPitch;
        private float yaw;
        private float pitch;
        private Vec3 input;
        private boolean sprintActiveFixture;

        private Probe(Mob mob, Mob target, Vec3 direction, Vec3 origin) {
            this.mob = mob;
            this.target = target;
            this.direction = direction;
            this.origin = origin;
        }

        private void reset(Condition condition) {
            yaw = (float) (isPitchProbe(mob)
                    ? Math.toDegrees(Math.atan2(-direction.x, direction.z)) : 0.0D);
            double horizontal = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
            pitch = (float) (isPitchProbe(mob) ? Math.toDegrees(Math.atan2(-direction.y, horizontal)) : 0.0D);
            mob.moveTo(origin.x, origin.y, origin.z, (float) yaw, (float) pitch);
            mob.setYRot(yaw);
            mob.yBodyRot = yaw;
            mob.yHeadRot = yaw;
            mob.setXRot(pitch);
            mob.setDeltaMovement(Vec3.ZERO);
            sprintActiveFixture = condition.sprint;
            mob.setTarget(condition.sprint ? target : null);
            input = isPitchProbe(mob) ? new Vec3(0.0D, 0.0D, 1.0D) : direction;
            MovementIntentOverrides.set(mob, input);
            displacement = Vec3.ZERO;
            samples = 0;
            minPitch = mob.getXRot();
            maxPitch = mob.getXRot();
            lastPosition = mob.position();
        }

        private void anchorForMeasurement() {
            if (mob instanceof BfsAquaticEntity<?> aquatic) aquatic.resetFixtureMovementState();
            if (mob instanceof SmartWaterAnimal<?> aquatic) aquatic.resetFixtureMovementState();
            if (mob.getMoveControl() instanceof SharkSwimmingMoveControl sharkControl) {
                sharkControl.resetFixtureState();
            } else if (mob.getMoveControl() instanceof PitchSwimmingMoveControl pitchControl) {
                pitchControl.resetFixtureState();
            }
            mob.moveTo(origin.x, origin.y, origin.z, yaw, pitch);
            mob.setYRot(yaw);
            mob.yBodyRot = yaw;
            mob.yHeadRot = yaw;
            mob.setXRot(pitch);
            mob.setDeltaMovement(Vec3.ZERO);
            lastPosition = mob.position();
        }

        private void reanchorForNextTick() {
            if (mob instanceof BfsAquaticEntity<?> aquatic) aquatic.resetFixtureMovementState();
            if (mob instanceof SmartWaterAnimal<?> aquatic) aquatic.resetFixtureMovementState();
            if (mob.getMoveControl() instanceof SharkSwimmingMoveControl sharkControl) {
                sharkControl.resetFixtureState();
            } else if (mob.getMoveControl() instanceof PitchSwimmingMoveControl pitchControl) {
                pitchControl.resetFixtureState();
            }
            mob.setPos(origin);
            mob.setDeltaMovement(Vec3.ZERO);
            mob.setYRot(yaw);
            mob.yBodyRot = yaw;
            mob.yHeadRot = yaw;
            mob.setXRot(pitch);
            lastPosition = mob.position();
        }

        private void step() {
            reanchorForNextTick();
            if (sprintActiveFixture && target != null && target.isAlive()) mob.setTarget(target);
            if (mob.getMoveControl() instanceof SharkSwimmingMoveControl control && control.snapshot() != null) {
                control.travel(0.0D, 0.82D, 1.0D, 0.0D, input);
            } else if (mob.getMoveControl() instanceof PitchSwimmingMoveControl control) {
                control.travel(0.0D, 0.9D, 1.0D, 0.0D, input);
            } else {
                mob.travel(input);
            }
        }

        private void sample() {
            Vec3 current = mob.position();
            Vec3 step = current.subtract(lastPosition);
            displacement = displacement.add(step);
            lastPosition = current;
            samples++;
            minPitch = Math.min(minPitch, mob.getXRot());
            maxPitch = Math.max(maxPitch, mob.getXRot());
            mob.setYRot(yaw);
            mob.yBodyRot = yaw;
            mob.yHeadRot = yaw;
            mob.setXRot(pitch);
        }
    }

    private static final class Runner {
        private final GameTestHelper helper;
        private final List<Probe> probes;
        private int conditionIndex;
        private int settling;
        private int measuring;

        private Runner(GameTestHelper helper, List<Probe> probes) {
            this.helper = helper;
            this.probes = probes;
        }

        private void start() {
            MovementIntentOverrides.clearAll();
            SpeciesSettingsService.resetSession();
            beginCondition();
        }

        private void beginCondition() {
            if (conditionIndex >= CONDITIONS.size()) {
                finish();
                return;
            }
            Condition condition = CONDITIONS.get(conditionIndex);
            SpeciesSettingsService.MutationResult mutation = SpeciesSettingsService.apply(
                    SpeciesSettingsService.revision(), "*", Map.of(
                            SpeciesSettingsService.Field.HORIZONTAL_SPEED, condition.horizontal,
                            SpeciesSettingsService.Field.VERTICAL_SPEED, condition.vertical,
                            SpeciesSettingsService.Field.HORIZONTAL_SPRINT, condition.horizontalSprint,
                            SpeciesSettingsService.Field.VERTICAL_SPRINT, condition.verticalSprint));
            helper.assertTrue(mutation.applied(), "oracle settings transaction must apply: " + mutation.reason());
            for (Probe probe : probes) probe.reset(condition);
            settling = 100;
            measuring = 200;
            helper.runAfterDelay(1, this::tick);
        }

        private void tick() {
            if (settling > 0) {
                for (Probe probe : probes) probe.step();
                settling--;
                if (settling == 0) {
                    for (Probe probe : probes) probe.anchorForMeasurement();
                } else {
                    for (Probe probe : probes) probe.reanchorForNextTick();
                }
                helper.runAfterDelay(1, this::tick);
                return;
            }
            if (measuring > 0) {
                for (Probe probe : probes) {
                    probe.step();
                    probe.sample();
                }
                measuring--;
                if (measuring > 0) {
                    for (Probe probe : probes) probe.reanchorForNextTick();
                    helper.runAfterDelay(1, this::tick);
                    return;
                }
                recordCondition(CONDITIONS.get(conditionIndex));
                conditionIndex++;
                beginCondition();
            }
        }

        private void recordCondition(Condition condition) {
            for (Probe probe : probes) {
                helper.assertTrue(probe.samples == 200,
                        "oracle must retain exactly 200 measured ticks for " + probe.mob.getType());
                probe.measured.put(condition.id, probe.displacement.scale(20.0D / probe.samples));
            }
        }

        private void finish() {
            try {
                for (Probe probe : probes) {
                    Vec3 cruise26 = probe.measured.get("cruise_2_6");
                    Vec3 cruise66 = probe.measured.get("cruise_6_6");
                    Vec3 cruise62 = probe.measured.get("cruise_6_2");
                    Vec3 pursuit66 = probe.measured.get("pursuit_6_6");
                    Vec3 pursuit121 = probe.measured.get("pursuit_1_2_1");
                    Vec3 pursuit1215 = probe.measured.get("pursuit_1_2_1_5");
                    Vec3 cruise121 = probe.measured.get("cruise_1_2_1");
                    Vec3 cruise1215 = probe.measured.get("cruise_1_2_1_5");
                    assertVectorClose(helper, probe.mob.getType() + " cruise 2 6", expected(probe.direction, 2, 6), cruise26, probe);
                    assertVectorClose(helper, probe.mob.getType() + " cruise 6 6", expected(probe.direction, 6, 6), cruise66, probe);
                    assertVectorClose(helper, probe.mob.getType() + " cruise 6 2", expected(probe.direction, 6, 2), cruise62, probe);
                    assertVectorClose(helper, probe.mob.getType() + " pursuit 1 2 1", expected(probe.direction, 7.2, 6), pursuit121, probe);
                    assertVectorClose(helper, probe.mob.getType() + " pursuit 1 2 1 5", expected(probe.direction, 7.2, 9), pursuit1215, probe);
                    assertComponentClose(helper, probe.mob.getType() + " horizontal change", cruise26.y, cruise66.y);
                    assertComponentClose(helper, probe.mob.getType() + " vertical change", cruise66.x, cruise62.x);
                    assertComponentClose(helper, probe.mob.getType() + " inactive cruise sprint", cruise66.x, cruise121.x);
                    assertComponentClose(helper, probe.mob.getType() + " inactive cruise vertical sprint", cruise66.y, cruise1215.y);
                    double cruiseHorizontal = Math.sqrt(pursuit66.x * pursuit66.x + pursuit66.z * pursuit66.z);
                    double sprintHorizontal = Math.sqrt(pursuit121.x * pursuit121.x + pursuit121.z * pursuit121.z);
                    helper.assertTrue(Math.abs(sprintHorizontal / cruiseHorizontal - 1.2D) <= 0.02D,
                            "horizontal sprint multiplier must be measured independently for " + probe.mob.getType());
                    assertComponentClose(helper, probe.mob.getType() + " vertical sprint horizontal x",
                            pursuit121.x, pursuit1215.x);
                    assertComponentClose(helper, probe.mob.getType() + " vertical sprint horizontal z",
                            pursuit121.z, pursuit1215.z);
                    helper.assertTrue(cruise1215 != null,
                            "cruise multiplier capture must be retained for " + probe.mob.getType());
                    helper.assertTrue(Float.isFinite(probe.minPitch) && Float.isFinite(probe.maxPitch)
                                    && probe.minPitch >= -90.0F && probe.maxPitch <= 90.0F,
                            "pitch must remain within the physical envelope for " + probe.mob.getType());
                }
                helper.succeed();
            } finally {
                for (Probe probe : probes) probe.target.discard();
                for (Probe probe : probes) probe.mob.discard();
                MovementIntentOverrides.clearAll();
                SpeciesSettingsService.resetSession();
            }
        }
    }

    private static Vec3 expected(Vec3 direction, double horizontal, double vertical) {
        Vec3 normalized = direction.normalize();
        return new Vec3(normalized.x * horizontal, normalized.y * vertical, normalized.z * horizontal);
    }

    private static void assertComponentClose(GameTestHelper helper, String label, double first, double second) {
        helper.assertTrue(Math.abs(first - second) <= 0.05D,
                label + " must remain unchanged, first=" + first + ", second=" + second);
    }

    private static void assertVectorClose(GameTestHelper helper, String label, Vec3 expected, Vec3 actual,
                                           Probe probe) {
                helper.assertTrue(actual != null && Math.abs(expected.x - actual.x) <= 0.05D
                        && Math.abs(expected.y - actual.y) <= 0.05D
                        && Math.abs(expected.z - actual.z) <= 0.05D,
                        label + " must match the independent powered vector, expected=" + expected
                        + ", actual=" + actual + ", pitchRange=" + probe.minPitch + ".." + probe.maxPitch
                        + ", measured=" + probe.measured + ", origin=" + probe.origin
                        + ", position=" + probe.mob.position() + ", inWater=" + probe.mob.isInWater());
    }
}
