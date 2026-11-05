package tfar.bensfintasticsharks.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import tfar.bensfintasticsharks.gore.GoreDebugSettings;
import tfar.bensfintasticsharks.gore.GoreImpactKind;

import java.util.function.Supplier;

/** A visual-only hit cue. It contains no authoritative entity or damage state. */
public record GoreImpactPayload(int targetEntityId, double x, double y, double z, GoreImpactKind kind,
                                int particleCount, float particleScale, int particleLifetime,
                                float woundScale, int woundLifetime,
                                int remainsCount, float remainsScale, int remainsLifetime) {
    public GoreImpactPayload(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                GoreImpactKind.fromWireId(buffer.readVarInt()),
                buffer.readVarInt(), buffer.readFloat(), buffer.readVarInt(), buffer.readFloat(),
                buffer.readVarInt(), buffer.readVarInt(), buffer.readFloat(), buffer.readVarInt());
    }

    public GoreImpactPayload(int targetEntityId, double x, double y, double z, GoreImpactKind kind,
                             GoreDebugSettings.Settings settings) {
        this(targetEntityId, x, y, z, kind, settings.particleCount(), settings.particleScale(), settings.particleLifetime(),
                settings.woundScale(), settings.woundLifetime(), settings.remainsCount(),
                settings.remainsScale(), settings.remainsLifetime());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(targetEntityId);
        buffer.writeDouble(x);
        buffer.writeDouble(y);
        buffer.writeDouble(z);
        buffer.writeVarInt(kind == null ? -1 : kind.wireId());
        buffer.writeVarInt(particleCount);
        buffer.writeFloat(particleScale);
        buffer.writeVarInt(particleLifetime);
        buffer.writeFloat(woundScale);
        buffer.writeVarInt(woundLifetime);
        buffer.writeVarInt(remainsCount);
        buffer.writeFloat(remainsScale);
        buffer.writeVarInt(remainsLifetime);
    }

    public boolean isValid() {
        return targetEntityId > 0 && kind != null && Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z)
                && Math.abs(x) <= 30_000_000.0D && Math.abs(z) <= 30_000_000.0D
                && Math.abs(y) <= 4_096.0D
                && particleCount >= 0 && particleCount <= 64
                && validScale(particleScale) && particleLifetime >= 1 && particleLifetime <= 1_200
                && validScale(woundScale) && woundLifetime >= 1 && woundLifetime <= 1_200
                && remainsCount >= 0 && remainsCount <= 32
                && validScale(remainsScale) && remainsLifetime >= 1 && remainsLifetime <= 6_000;
    }

    private static boolean validScale(float scale) {
        return Float.isFinite(scale) && scale >= 0.1F && scale <= 4.0F;
    }

    public boolean fatal() {
        return kind != null && kind.isFatal();
    }

    public GoreDebugSettings.Settings settings() {
        return new GoreDebugSettings.Settings(particleCount, particleScale, particleLifetime,
                woundScale, woundLifetime, remainsCount, remainsScale, remainsLifetime);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> tfar.bensfintasticsharks.client.ClientGoreEffects.handle(this)));
        context.setPacketHandled(true);
    }
}
