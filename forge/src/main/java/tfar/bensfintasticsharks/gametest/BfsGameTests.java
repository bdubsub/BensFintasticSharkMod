package tfar.bensfintasticsharks.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Items;
import net.minecraftforge.gametest.GameTestHolder;
import tfar.bensfintasticsharks.entity.BottlenoseDolphinEntity;
import tfar.bensfintasticsharks.entity.AtlanticCodEntity;
import tfar.bensfintasticsharks.entity.AquaticMovement;
import tfar.bensfintasticsharks.entity.OceanicWhitetipSharkEntity;
import tfar.bensfintasticsharks.entity.TigerSharkEntity;
import tfar.bensfintasticsharks.init.ModBlocks;
import tfar.bensfintasticsharks.init.ModEntityTypes;

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

    @GameTest(template = "empty", batch = "bfs_baseline", timeoutTicks = 220)
    public static void tigerSharkPursuesReachableEdibleItem(GameTestHelper helper) {
        prepareWaterVolume(helper);
        ItemEntity item = helper.spawnItem(Items.COD, new BlockPos(8, 3, 3));
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(3, 3, 3));
        Vec3 start = shark.position();
        double startDistance = shark.distanceToSqr(item);

        helper.runAfterDelay(40, () -> {
            helper.assertTrue(shark.getSharkState() == TigerSharkEntity.SharkState.CURIOUS
                            || shark.justBitItem(),
                    "tiger shark must acquire a reachable edible item");
            helper.assertTrue(shark.position().distanceToSqr(start) > 0.25,
                    "tiger shark must leave its spawn position while pursuing an item");
            helper.assertTrue(shark.distanceToSqr(item) < startDistance,
                    "tiger shark must reduce distance to a reachable edible item");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "bfs_combat", timeoutTicks = 100)
    public static void tigerBiteLandsOnceAndRecoversAfterTargetLoss(GameTestHelper helper) {
        prepareWaterVolume(helper);
        TigerSharkEntity shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(4, 3, 3));
        Mob prey = helper.spawn(EntityType.DROWNED, new BlockPos(5, 3, 3));
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

    @GameTest(template = "empty", batch = "bfs_combat", timeoutTicks = 140)
    public static void oceanicWhitetipGrabDamagesAndReleasesPassenger(GameTestHelper helper) {
        prepareWaterVolume(helper);
        OceanicWhitetipSharkEntity shark = helper.spawn(ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                new BlockPos(4, 3, 3));
        Mob prey = helper.spawn(EntityType.DROWNED, new BlockPos(5, 3, 3));
        shark.getBrain().removeAllBehaviors();
        prey.setNoAi(true);
        shark.setTarget(prey);
        helper.runAfterDelay(2, () -> {
            helper.assertTrue(shark.getTarget() == prey,
                    "oceanic whitetip fixture must retain its target identity");
            helper.assertTrue(shark.isInWaterOrBubble(),
                    "oceanic whitetip fixture must place the shark in water");
            helper.assertTrue(!prey.isPassenger(),
                    "oceanic whitetip fixture target must start without a vehicle");
            shark.grabMob(prey);
            shark.setTarget(null);
            float startHealth = prey.getHealth();

            helper.assertTrue(prey.isPassenger() && shark.getPassengers().contains(prey),
                    "oceanic whitetip must attach a live target as a passenger");
            helper.assertTrue(shark.getGrabTimer() > 0,
                    "oceanic whitetip must expose an active grab timer");
            helper.runAfterDelay(12, () -> {
                helper.assertTrue(prey.getHealth() < startHealth,
                        "oceanic whitetip thrash must deal server-authoritative damage");
                helper.runAfterDelay(95, () -> {
                    helper.assertTrue(shark.getGrabTimer() == 0,
                            "oceanic whitetip grab timer must expire");
                    helper.assertTrue(!prey.isPassenger() && shark.getPassengers().isEmpty(),
                            "oceanic whitetip must release its passenger when the grab expires");
                    helper.succeed();
                });
            });
        });
    }

    @GameTest(template = "empty", batch = "bfs_movement", timeoutTicks = 320)
    public static void sharkVerticalRouteFollowsDolphinWithoutOrbit(GameTestHelper helper) {
        runVerticalRoute(helper, new BlockPos(4, 5, 4), new BlockPos(4, 9, 4),
                new BlockPos(4, 5, 20), new BlockPos(4, 9, 20), 1);
    }

    @GameTest(template = "empty", batch = "bfs_movement", timeoutTicks = 320)
    public static void sharkDescendingRouteFollowsDolphinWithoutOrbit(GameTestHelper helper) {
        runVerticalRoute(helper, new BlockPos(4, 9, 4), new BlockPos(4, 5, 4),
                new BlockPos(4, 9, 20), new BlockPos(4, 5, 20), -1);
    }

    @GameTest(template = "empty", batch = "bfs_movement", timeoutTicks = 320)
    public static void atlanticCodVerticalRouteUsesScaledPitch(GameTestHelper helper) {
        prepareVerticalWaterVolume(helper);
        AtlanticCodEntity cod = helper.spawn(ModEntityTypes.ATLANTIC_COD, new BlockPos(4, 5, 4));
        cod.getBrain().removeAllBehaviors();
        Vec3 target = helper.absolutePos(new BlockPos(4, 8, 4)).getCenter();
        java.util.List<Double> heights = new java.util.ArrayList<>();
        java.util.List<Float> pitches = new java.util.ArrayList<>();
        double startY = cod.getY();
        sampleFishVerticalRoute(helper, cod, target, startY, heights, pitches, 0);
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

        sampleVerticalRoute(helper, shark, dolphin, sharkTarget, dolphinTarget,
                sharkStartY, dolphinStartY, sharkStartX, sharkStartZ, dolphinStartX, dolphinStartZ,
                sharkHeights, dolphinHeights, sharkHorizontalOffsets, dolphinHorizontalOffsets,
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
            if (sample < 260) {
                setVerticalTarget(shark, sharkTarget);
                if (arrival[0] < 0) {
                    setVerticalTarget(dolphin, dolphinTarget);
                }
                sampleVerticalRoute(helper, shark, dolphin, sharkTarget, dolphinTarget,
                        sharkStartY, dolphinStartY, sharkStartX, sharkStartZ, dolphinStartX, dolphinStartZ,
                        sharkHeights, dolphinHeights, sharkHorizontalOffsets, dolphinHorizontalOffsets,
                        verticalDirection, sample + 1, arrival[0]);
                return;
            }

            double sharkProgress = shark.getY() - sharkStartY;
            double dolphinProgress = dolphin.getY() - dolphinStartY;
            helper.assertTrue(dolphinProgress * verticalDirection > 0.25,
                    "bottlenose dolphin must complete the vertical reference route");
            helper.assertTrue(sharkProgress * verticalDirection > 0.25,
                    "shark must complete the vertical route, progress=" + sharkProgress
                            + ", dolphinProgress=" + dolphinProgress);
            helper.assertTrue(sharkProgress * verticalDirection <= dolphinProgress * verticalDirection + 0.75,
                    "shark vertical progress must remain below the full dolphin reference, shark=" + sharkProgress
                            + ", dolphin=" + dolphinProgress + ", navDone=" + shark.getNavigation().isDone()
                            + ", position=" + shark.position() + ", delta=" + shark.getDeltaMovement());
            helper.assertTrue(max(sharkHorizontalOffsets) <= max(dolphinHorizontalOffsets) + 0.75,
                    "shark must not orbit horizontally while following a vertical route, sharkMax="
                            + max(sharkHorizontalOffsets) + ", dolphinMax=" + max(dolphinHorizontalOffsets));
            helper.assertTrue(hasNoDirectionReversal(sharkHeights, verticalDirection),
                    "shark vertical travel must not repeatedly reverse direction, reversals="
                            + directionReversals(sharkHeights) + ", heights=" + sharkHeights);
            int dolphinSamples = arrival[0] < 0
                    ? dolphinHeights.size() : arrival[0] + 1;
            helper.assertTrue(hasNoDirectionReversal(dolphinHeights.subList(0, dolphinSamples), verticalDirection),
                    "dolphin reference must remain smooth and monotonic");
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
            helper.assertTrue(maxPitchStep(pitches) <= 5.0001F,
                    "atlantic cod pitch must transition smoothly, maxStep=" + maxPitchStep(pitches));
            helper.assertTrue(AquaticMovement.VERTICAL_SPEED_RATIO == 0.10D,
                    "affected aquatic vertical ratio must remain the approved oracle");
            helper.assertTrue(hasNoDirectionReversal(heights, 1),
                    "atlantic cod vertical travel must not repeatedly reverse direction");
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
