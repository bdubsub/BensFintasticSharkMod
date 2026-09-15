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
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import tfar.bensfintasticsharks.disturbance.WaterDisturbanceEvent;

import java.util.ArrayList;
import java.util.List;
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
