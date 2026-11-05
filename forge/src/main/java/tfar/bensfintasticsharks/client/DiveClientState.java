package tfar.bensfintasticsharks.client;

import net.minecraft.client.Minecraft;
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
                || payload.drainTicksPerSecond() < 0
                || payload.drainTicksPerSecond() > DiveOxygenManager.MAX_DRAIN_TICKS_PER_SECOND
                || payload.revision() <= snapshot.revision()) return;
        Minecraft minecraft = Minecraft.getInstance();
        long clientTick = minecraft.level == null ? 0L : minecraft.level.getGameTime();
        snapshot = new Snapshot(payload.revision(), payload.remainingTicks(), payload.eligible(),
                payload.movementMode(), payload.oxygenMode(), payload.drainTicksPerSecond(), clientTick);
    }

    public static Snapshot snapshot() {
        return snapshot;
    }

    public static void clear() {
        snapshot = Snapshot.empty();
    }

    public record Snapshot(long revision, int remainingTicks, boolean eligible,
                           String movementMode, String oxygenMode, int drainTicksPerSecond,
                           long receivedAtClientTick) {
        public int estimatedRemainingTicks(long clientTick) {
            long elapsed = Math.max(0L, clientTick - receivedAtClientTick);
            long predictedDrain = elapsed * drainTicksPerSecond / 20L;
            return (int) Math.max(0L, remainingTicks - predictedDrain);
        }

        private static Snapshot empty() {
            return new Snapshot(-1L, 0, false, "vanilla", "vanilla", 0, 0L);
        }
    }
}
