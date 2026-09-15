package tfar.bensfintasticsharks.dive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import tfar.bensfintasticsharks.network.DiveNetworking;

/** Owns the full suit's persistent oxygen reserve on the logical server. */
public final class DiveOxygenManager {

    public static final int MAX_RESERVE_TICKS = 6000;
    public static final int REAL_AIR_REFILL_TICKS = 20;
    public static final int SCHEMA = 1;
    private static final String SCHEMA_KEY = "bfs_dive_oxygen_schema";
    private static final String INITIALIZED_KEY = "bfs_dive_oxygen_initialized";
    private static final String RESERVE_KEY = "bfs_dive_oxygen_ticks";
    private static final String REVISION_KEY = "bfs_dive_oxygen_revision";
    private static final String SYNC_INITIALIZED_KEY = "bfs_dive_oxygen_sync_initialized";
    private static final String SYNC_FULL_KEY = "bfs_dive_oxygen_sync_full";
    private static final String SYNC_ELIGIBLE_KEY = "bfs_dive_oxygen_sync_eligible";
    private static final String SYNC_MOVEMENT_KEY = "bfs_dive_oxygen_sync_movement";
    private static final String SYNC_OXYGEN_KEY = "bfs_dive_oxygen_sync_oxygen";

    private DiveOxygenManager() {
    }

    public static void tick(Player player) {
        DiveSuitEligibility.Result eligibility = DiveSuitEligibility.evaluate(player);
        CompoundTag data = player.getPersistentData();
        int reserve = readReserve(data);
        if (eligibility.fullSuit()) {
            ensureInitialized(data);
            reserve = readReserve(data);
            boolean submerged = eligibility.submergedEyes();
            boolean externalBreathing = player.hasEffect(MobEffects.WATER_BREATHING);
            if (submerged && !externalBreathing) {
                if (reserve > 0) {
                    reserve--;
                    writeReserve(data, reserve);
                    player.setAirSupply(reserve > 0 ? player.getMaxAirSupply() : 0);
                } else {
                    player.setAirSupply(0);
                }
            } else if (!submerged && reserve < MAX_RESERVE_TICKS) {
                reserve = Math.min(MAX_RESERVE_TICKS, reserve + REAL_AIR_REFILL_TICKS);
                writeReserve(data, reserve);
                player.setAirSupply(player.getMaxAirSupply());
            } else if (!submerged) {
                player.setAirSupply(player.getMaxAirSupply());
            }

            if (player instanceof ServerPlayer serverPlayer
                    && (serverPlayer.tickCount % 20 == 0 || reserve == 0)) {
                serverPlayer.displayClientMessage(Component.translatable(
                        "hud.bensfintasticsharks.dive_oxygen", reserve, MAX_RESERVE_TICKS), true);
            }
        }
        sync(player, eligibility, reserve);
    }

    public static void syncNow(ServerPlayer player) {
        player.getPersistentData().remove(SYNC_INITIALIZED_KEY);
        tick(player);
    }

    public static void copy(Player original, Player replacement) {
        CompoundTag source = original.getPersistentData();
        if (!source.contains(SCHEMA_KEY) && !source.contains(INITIALIZED_KEY) && !source.contains(RESERVE_KEY)) return;
        CompoundTag target = replacement.getPersistentData();
        target.putInt(SCHEMA_KEY, source.getInt(SCHEMA_KEY));
        target.putBoolean(INITIALIZED_KEY, source.getBoolean(INITIALIZED_KEY));
        target.putInt(RESERVE_KEY, source.getInt(RESERVE_KEY));
        target.remove(SYNC_INITIALIZED_KEY);
    }

    public static int readReserve(Player player) {
        return readReserve(player.getPersistentData());
    }

    private static void ensureInitialized(CompoundTag data) {
        if (!data.getBoolean(INITIALIZED_KEY) || data.getInt(SCHEMA_KEY) != SCHEMA) {
            data.putInt(SCHEMA_KEY, SCHEMA);
            data.putBoolean(INITIALIZED_KEY, true);
            data.putInt(RESERVE_KEY, MAX_RESERVE_TICKS);
            return;
        }
        int reserve = data.getInt(RESERVE_KEY);
        if (reserve < 0 || reserve > MAX_RESERVE_TICKS) writeReserve(data, 0);
    }

    private static int readReserve(CompoundTag data) {
        return Math.max(0, Math.min(MAX_RESERVE_TICKS, data.getInt(RESERVE_KEY)));
    }

    private static void writeReserve(CompoundTag data, int reserve) {
        data.putInt(SCHEMA_KEY, SCHEMA);
        data.putBoolean(INITIALIZED_KEY, true);
        data.putInt(RESERVE_KEY, Math.max(0, Math.min(MAX_RESERVE_TICKS, reserve)));
    }

    private static void sync(Player player, DiveSuitEligibility.Result eligibility, int reserve) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        CompoundTag data = player.getPersistentData();
        String movementMode = eligibility.eligible() ? "seabed" : "vanilla";
        boolean externalBreathing = player.hasEffect(MobEffects.WATER_BREATHING);
        String oxygenMode = !eligibility.fullSuit() ? "vanilla"
                : externalBreathing ? "external_breathing"
                : eligibility.submergedEyes() ? reserve > 0 ? "protected" : "empty" : "air";
        boolean initialized = data.getBoolean(SYNC_INITIALIZED_KEY);
        boolean modeChanged = !initialized
                || data.getBoolean(SYNC_FULL_KEY) != eligibility.fullSuit()
                || data.getBoolean(SYNC_ELIGIBLE_KEY) != eligibility.eligible()
                || !movementMode.equals(data.getString(SYNC_MOVEMENT_KEY))
                || !oxygenMode.equals(data.getString(SYNC_OXYGEN_KEY));
        if (!modeChanged && serverPlayer.tickCount % 20 != 0) return;
        long revision = data.getLong(REVISION_KEY) + 1L;
        data.putLong(REVISION_KEY, revision);
        data.putBoolean(SYNC_INITIALIZED_KEY, true);
        data.putBoolean(SYNC_FULL_KEY, eligibility.fullSuit());
        data.putBoolean(SYNC_ELIGIBLE_KEY, eligibility.eligible());
        data.putString(SYNC_MOVEMENT_KEY, movementMode);
        data.putString(SYNC_OXYGEN_KEY, oxygenMode);
        DiveNetworking.send(serverPlayer, reserve, revision, eligibility.eligible(), movementMode, oxygenMode);
    }
}
