package tfar.bensfintasticsharks.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.bensfintasticsharks.BensFintasticSharks;

/** Small, versioned channel for authoritative dive snapshots. */
public final class DiveNetworking {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BensFintasticSharks.MOD_ID, "dive"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private static boolean registered;

    private DiveNetworking() {
    }

    public static void register() {
        if (registered) return;
        registered = true;
        CHANNEL.registerMessage(0, DiveOxygenPayload.class,
                DiveOxygenPayload::encode, DiveOxygenPayload::decode, DiveOxygenPayload::handle);
    }

    public static void send(ServerPlayer player, int remainingTicks, long revision, boolean eligible,
                            String movementMode, String oxygenMode) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new DiveOxygenPayload(1, revision, remainingTicks, eligible, movementMode, oxygenMode));
    }
}
