package tfar.bensfintasticsharks.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import tfar.bensfintasticsharks.dive.DiveOxygenManager;

/** Owning player snapshot for the dive suit state. The server remains authoritative. */
public record DiveOxygenPayload(int schema, long revision, int remainingTicks, boolean eligible,
                                String movementMode, String oxygenMode) {

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(schema);
        buffer.writeVarLong(revision);
        buffer.writeVarInt(remainingTicks);
        buffer.writeBoolean(eligible);
        buffer.writeUtf(movementMode, 32);
        buffer.writeUtf(oxygenMode, 32);
    }

    public static DiveOxygenPayload decode(FriendlyByteBuf buffer) {
        return new DiveOxygenPayload(buffer.readVarInt(), buffer.readVarLong(), buffer.readVarInt(),
                buffer.readBoolean(), buffer.readUtf(32), buffer.readUtf(32));
    }

    public static void handle(DiveOxygenPayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> tfar.bensfintasticsharks.client.DiveClientState.accept(payload)));
        context.setPacketHandled(true);
    }

    public boolean isSupported() {
        return schema == DiveOxygenManager.SCHEMA;
    }
}
