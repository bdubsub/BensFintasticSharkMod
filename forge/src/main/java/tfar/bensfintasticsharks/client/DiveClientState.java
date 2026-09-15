package tfar.bensfintasticsharks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import tfar.bensfintasticsharks.dive.DiveOxygenManager;
import tfar.bensfintasticsharks.network.DiveOxygenPayload;

/** Client read model for the latest server owned dive snapshot. */
public final class DiveClientState {
    private static volatile Snapshot snapshot = Snapshot.empty();

    private DiveClientState() {
    }

    public static void accept(DiveOxygenPayload payload) {
        if (!payload.isSupported() || payload.remainingTicks() < 0
                || payload.remainingTicks() > DiveOxygenManager.MAX_RESERVE_TICKS
                || payload.revision() <= snapshot.revision()) return;
        snapshot = new Snapshot(payload.revision(), payload.remainingTicks(), payload.eligible(),
                payload.movementMode(), payload.oxygenMode());
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(Component.translatable(
                    "hud.bensfintasticsharks.dive_oxygen", payload.remainingTicks(),
                    DiveOxygenManager.MAX_RESERVE_TICKS), true);
        }
    }

    public static Snapshot snapshot() {
        return snapshot;
    }

    public record Snapshot(long revision, int remainingTicks, boolean eligible,
                           String movementMode, String oxygenMode) {
        private static Snapshot empty() {
            return new Snapshot(-1L, 0, false, "vanilla", "vanilla");
        }
    }
}
