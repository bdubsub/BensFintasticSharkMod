package tfar.bensfintasticsharks.gametest;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import tfar.bensfintasticsharks.disturbance.WaterDisturbanceEvent;
import tfar.bensfintasticsharks.disturbance.GreatWhiteBoatInterest;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.entity.BoatMovementOwners;
import tfar.bensfintasticsharks.entity.SpeciesSettingsService;
import tfar.bensfintasticsharks.init.ModEntityTypes;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Real transition and moving occupied boat fixtures for BFS2 REQ 011. */
@GameTestHolder("bfsdisturbance")
public final class BfsDisturbanceGameTests {

    private BfsDisturbanceGameTests() {
    }

    @GameTest(template = "empty", batch = "disturbance_sources", timeoutTicks = 100)
    public static void realWaterEntryAndJumpProduceTypedEvents(GameTestHelper helper) {
        BlockPos water = new BlockPos(5, 2, 2);
        BlockPos waterAbsolute = helper.absolutePos(water);
        helper.setBlock(water, Blocks.WATER.defaultBlockState());
        helper.setBlock(water.above(), Blocks.WATER.defaultBlockState());
        Mob mob = helper.spawn(EntityType.COW, new BlockPos(5, 5, 2));
        mob.setNoGravity(true);
        Collector collector = new Collector();
        MinecraftForge.EVENT_BUS.register(collector);
        helper.runAfterDelay(5, () -> {
            mob.setPos(Vec3.atCenterOf(waterAbsolute));
            helper.runAfterDelay(5, () -> {
                helper.assertTrue(mob.isInWaterOrBubble(),
                        "the fixture must keep the mob in water before the upward exit");
                mob.setDeltaMovement(0.0D, 0.2D, 0.0D);
                mob.setPos(Vec3.atCenterOf(helper.absolutePos(water.above().above())));
                helper.runAfterDelay(5, () -> {
                    helper.assertFalse(mob.isInWaterOrBubble(),
                            "the fixture must move the mob fully out of water");
                    try {
                        helper.assertTrue(collector.has(WaterDisturbanceEvent.SourceKind.WATER_ENTRY),
                                "an actual air to water transition must emit one water_entry source");
                        helper.assertTrue(collector.has(WaterDisturbanceEvent.SourceKind.WATER_JUMP),
                                "an actual upward water exit must emit one water_jump source");
                    } finally {
                        MinecraftForge.EVENT_BUS.unregister(collector);
                        mob.remove(Entity.RemovalReason.DISCARDED);
                    }
                    helper.succeed();
                });
            });
        });
    }

    @GameTest(template = "empty", batch = "disturbance_sources", timeoutTicks = 100)
    public static void occupiedMovingBoatProducesOneTypedEventPerInterval(GameTestHelper helper) {
        ServerPlayer rider = makeTestPlayer(helper, "boat-rider", new BlockPos(2, 2, 2));
        Boat boat = helper.spawn(EntityType.BOAT, new BlockPos(5, 2, 2));
        rider.startRiding(boat, true);
        Collector collector = new Collector();
        MinecraftForge.EVENT_BUS.register(collector);
        moveBoat(helper, boat, 0);
        helper.runAfterDelay(45, () -> {
            try {
                List<WaterDisturbanceEvent> events = collector.events.stream()
                        .filter(event -> event.getSourceKind() == WaterDisturbanceEvent.SourceKind.OCCUPIED_BOAT)
                        .toList();
                helper.assertTrue(events.size() >= 2,
                        "a moving occupied boat must produce repeated events after the ten tick interval, got " + events.size());
                List<Long> boatTicks = new ArrayList<>();
                for (int index = 0; index < collector.events.size(); index++) {
                    if (collector.events.get(index).getSourceKind() == WaterDisturbanceEvent.SourceKind.OCCUPIED_BOAT) {
                        boatTicks.add(collector.ticks.get(index));
                    }
                }
                for (int index = 1; index < boatTicks.size(); index++) {
                    helper.assertTrue(boatTicks.get(index) - boatTicks.get(index - 1) >= 10,
                            "occupied boat events must remain at least ten server ticks apart");
                }
                WaterDisturbanceEvent event = events.get(0);
                helper.assertTrue(event.getBoat() == boat && event.getRider() == rider,
                        "boat events must retain the actual boat and dry seated rider identity");
                helper.assertTrue(event.getStrength() > 0.0D && event.getStrength() <= 1.0D,
                        "boat event strength must be bounded");
            } finally {
                MinecraftForge.EVENT_BUS.unregister(collector);
                rider.stopRiding();
                rider.remove(Entity.RemovalReason.DISCARDED);
                boat.remove(Entity.RemovalReason.DISCARDED);
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "disturbance_settings", timeoutTicks = 140)
    public static void realWaterEntryUsesEffectiveSessionPolicy(GameTestHelper helper) {
        BlockPos water = new BlockPos(5, 2, 2);
        helper.setBlock(water.below(), Blocks.WATER.defaultBlockState());
        helper.setBlock(water, Blocks.WATER.defaultBlockState());
        helper.setBlock(water.above(), Blocks.WATER.defaultBlockState());
        helper.setBlock(water.above().above(), Blocks.WATER.defaultBlockState());
        AbstractSharkEntity<?> shark = helper.spawn(ModEntityTypes.TIGER_SHARK, new BlockPos(5, 2, 2));
        shark.setNoAi(true);
        shark.setPos(helper.absolutePos(water).getCenter());
        Mob source = helper.spawn(EntityType.COW, new BlockPos(8, 5, 2));
        source.setNoAi(true);
        source.setNoGravity(true);
        long baselineRevision = SpeciesSettingsService.revision();
        SpeciesSettingsService.MutationResult disabled = SpeciesSettingsService.apply(baselineRevision,
                "tiger_shark", Map.of(SpeciesSettingsService.Field.DISTURBANCE_ENABLED, 0.0D));
        helper.assertTrue(disabled.applied(), "the disabled policy fixture must apply atomically");
        Collector collector = new Collector();
        MinecraftForge.EVENT_BUS.register(collector);
        helper.runAfterDelay(5, () -> {
            helper.assertTrue(shark.isInWaterOrBubble(), "the shark policy fixture must keep the shark in water");
            source.setPos(helper.absolutePos(water).getCenter());
            helper.runAfterDelay(8, () -> {
                helper.assertTrue(collector.has(WaterDisturbanceEvent.SourceKind.WATER_ENTRY),
                        "the real water entry must still emit its typed source while reaction is disabled");
                helper.assertTrue(shark.getSharkState() == AbstractSharkEntity.SharkState.IDLE,
                        "a disabled species policy must not react to the real source, state=" + shark.getSharkState());
                long enabledRevision = SpeciesSettingsService.revision();
                SpeciesSettingsService.MutationResult enabled = SpeciesSettingsService.apply(enabledRevision,
                        "tiger_shark", Map.of(SpeciesSettingsService.Field.DISTURBANCE_ENABLED, 1.0D,
                                SpeciesSettingsService.Field.DISTURBANCE_REACTION, 2.0D,
                                SpeciesSettingsService.disturbanceField("water_entry", "strength"), 1.0D));
                helper.assertTrue(enabled.applied(), "the enabled policy fixture must apply atomically");
                shark.setSharkState(AbstractSharkEntity.SharkState.IDLE);
                WaterDisturbanceEvent recorded = collector.events.stream()
                        .filter(event -> event.getSourceKind() == WaterDisturbanceEvent.SourceKind.WATER_ENTRY)
                        .findFirst().orElseThrow();
                WaterDisturbanceEvent replay = new WaterDisturbanceEvent(recorded.getLevel(), recorded.getSource(),
                        recorded.getSourceEntity(), recorded.getType(), recorded.getSourceKind(), recorded.getStrength(),
                        recorded.getBoat(), recorded.getRider());
                MinecraftForge.EVENT_BUS.post(replay);
                try {
                    helper.assertTrue(shark.getSharkState() == AbstractSharkEntity.SharkState.CURIOUS,
                            "an investigate policy must make the same real water entry visible, state="
                                    + shark.getSharkState());
                } finally {
                    MinecraftForge.EVENT_BUS.unregister(collector);
                    source.remove(Entity.RemovalReason.DISCARDED);
                    shark.remove(Entity.RemovalReason.DISCARDED);
                    SpeciesSettingsService.reset(SpeciesSettingsService.revision(), "tiger_shark",
                            EnumSet.of(SpeciesSettingsService.Field.DISTURBANCE_ENABLED,
                                    SpeciesSettingsService.Field.DISTURBANCE_REACTION,
                                    SpeciesSettingsService.disturbanceField("water_entry", "strength")));
                }
                helper.succeed();
            });
        });
    }

    @GameTest(template = "empty", batch = "disturbance_boat_interest", timeoutTicks = 100)
    public static void greatWhiteTracksMovingBoatFromSafeBehindOffset(GameTestHelper helper) {
        fillWater(helper, 0, 0, 0, 14, 8, 14);
        AbstractSharkEntity<?> shark = helper.spawn(ModEntityTypes.GREAT_WHITE_SHARK, new BlockPos(3, 1, 7));
        shark.setNoAi(true);
        shark.setNoGravity(true);
        Boat boat = helper.spawn(EntityType.BOAT, new BlockPos(8, 3, 7));
        ServerPlayer rider = makeTestPlayer(helper, "boat-interest-rider", new BlockPos(8, 10, 7));
        rider.startRiding(boat, true);
        WaterDisturbanceEvent event = new WaterDisturbanceEvent(helper.getLevel(), boat.blockPosition(), boat,
                WaterDisturbanceEvent.Type.LIGHT, WaterDisturbanceEvent.SourceKind.OCCUPIED_BOAT, 1.0D,
                boat, rider, new Vec3(0.5D, 0.0D, 0.0D));
        GreatWhiteBoatInterest.Decision decision = new GreatWhiteBoatInterest().acquire(
                helper.getLevel(), (tfar.bensfintasticsharks.entity.GreatWhiteSharkEntity) shark, event);
        helper.assertTrue(decision.accepted(), "the direct boat lease decision must be accepted, reason=" + decision.reason());
        MinecraftForge.EVENT_BUS.post(event);
        helper.runAfterDelay(3, () -> {
            try {
                helper.assertTrue(BoatMovementOwners.active(shark),
                        "a safe moving occupied boat must acquire the great white boat_track lease");
                WalkTarget target = shark.getBrain().getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
                helper.assertTrue(target != null && target.getTarget().currentPosition().x < boat.getX(),
                        "the tracked waypoint must stay behind the boat travel direction");
            } finally {
                rider.stopRiding();
                rider.remove(Entity.RemovalReason.DISCARDED);
                boat.remove(Entity.RemovalReason.DISCARDED);
                shark.remove(Entity.RemovalReason.DISCARDED);
            }
            helper.succeed();
        });
    }

    @GameTest(template = "empty", batch = "disturbance_boat_interest", timeoutTicks = 100)
    public static void higherPriorityCombatBlocksBoatInterest(GameTestHelper helper) {
        fillWater(helper, 0, 0, 0, 14, 8, 14);
        AbstractSharkEntity<?> shark = helper.spawn(ModEntityTypes.GREAT_WHITE_SHARK, new BlockPos(3, 1, 7));
        shark.setNoAi(true);
        shark.setNoGravity(true);
        Mob prey = helper.spawn(EntityType.COD, new BlockPos(4, 1, 7));
        prey.setNoAi(true);
        shark.setTarget(prey);
        Boat boat = helper.spawn(EntityType.BOAT, new BlockPos(8, 3, 7));
        ServerPlayer rider = makeTestPlayer(helper, "boat-combat-rider", new BlockPos(8, 10, 7));
        rider.startRiding(boat, true);
        MinecraftForge.EVENT_BUS.post(new WaterDisturbanceEvent(helper.getLevel(), boat.blockPosition(), boat,
                WaterDisturbanceEvent.Type.LIGHT, WaterDisturbanceEvent.SourceKind.OCCUPIED_BOAT, 1.0D,
                boat, rider, new Vec3(0.5D, 0.0D, 0.0D)));
        helper.runAfterDelay(3, () -> {
            try {
                helper.assertFalse(BoatMovementOwners.active(shark),
                        "combat ownership must outrank occupied boat interest");
            } finally {
                rider.stopRiding();
                rider.remove(Entity.RemovalReason.DISCARDED);
                boat.remove(Entity.RemovalReason.DISCARDED);
                prey.remove(Entity.RemovalReason.DISCARDED);
                shark.remove(Entity.RemovalReason.DISCARDED);
            }
            helper.succeed();
        });
    }

    private static void fillWater(GameTestHelper helper, int minX, int minY, int minZ,
                                  int maxX, int maxY, int maxZ) {
        for (int x = minX; x <= maxX; x++) for (int y = minY; y <= maxY; y++)
            for (int z = minZ; z <= maxZ; z++) helper.setBlock(new BlockPos(x, y, z), Blocks.WATER.defaultBlockState());
    }

    private static void moveBoat(GameTestHelper helper, Boat boat, int step) {
        if (step >= 40) return;
        boat.setPos(5.0D + step * 0.1D, 2.0D, 2.0D);
        helper.runAfterDelay(1, () -> moveBoat(helper, boat, step + 1));
    }

    private static ServerPlayer makeTestPlayer(GameTestHelper helper, String name, BlockPos localPosition) {
        ServerPlayer player = new ServerPlayer(helper.getLevel().getServer(), helper.getLevel(),
                new GameProfile(UUID.randomUUID(), name));
        player.setPos(helper.absolutePos(localPosition).getCenter());
        player.connection = new ServerGamePacketListenerImpl(helper.getLevel().getServer(),
                new Connection(PacketFlow.SERVERBOUND), player);
        helper.getLevel().addNewPlayer(player);
        helper.getLevel().getServer().getPlayerList().getOps().add(
                new net.minecraft.server.players.ServerOpListEntry(player.getGameProfile(), 2, false));
        return player;
    }

    private static final class Collector {
        private final List<WaterDisturbanceEvent> events = new ArrayList<>();
        private final List<Long> ticks = new ArrayList<>();

        @SubscribeEvent
        public void capture(WaterDisturbanceEvent event) {
            if (!event.getLevel().isClientSide) {
                events.add(event);
                ticks.add(event.getLevel().getGameTime());
            }
        }

        private boolean has(WaterDisturbanceEvent.SourceKind kind) {
            return events.stream().anyMatch(event -> event.getSourceKind() == kind);
        }
    }
}
