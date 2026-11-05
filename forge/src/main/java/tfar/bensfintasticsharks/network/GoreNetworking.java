package tfar.bensfintasticsharks.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.gore.GoreDebugSettings;
import tfar.bensfintasticsharks.gore.GoreImpactKind;

public final class GoreNetworking {
    private static final String PROTOCOL = "2";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BensFintasticSharks.MOD_ID, "gore"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private static boolean registered;

    private GoreNetworking() {
    }

    public static void register() {
        if (registered) return;
        registered = true;
        CHANNEL.registerMessage(0, GoreImpactPayload.class,
                GoreImpactPayload::encode, GoreImpactPayload::new, GoreImpactPayload::handle);
    }

    public static void sendImpact(ServerLevel level, LivingEntity target, GoreImpactKind kind) {
        GoreDebugSettings.Settings settings = GoreDebugSettings.current();
        GoreImpactPayload payload = new GoreImpactPayload(
                target.getId(), target.getX(), target.getY() + target.getBbHeight() * 0.55D,
                target.getZ(), kind, settings);
        CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                target.getX(), target.getY(), target.getZ(), 48.0D, level.dimension())), payload);
    }
}
