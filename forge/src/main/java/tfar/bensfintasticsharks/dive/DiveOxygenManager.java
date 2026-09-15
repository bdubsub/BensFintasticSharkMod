package tfar.bensfintasticsharks.dive;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import tfar.bensfintasticsharks.network.DiveNetworking;
import tfar.bensfintasticsharks.debug.BfsDebugManager;

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
        Initialization initialization = eligibility.fullSuit() ? ensureInitialized(data) : inspect(data);
        int before = data.contains(RESERVE_KEY) ? readReserve(data) : 0;
        int reserve = initialization.supported() ? before : 0;
        if (eligibility.fullSuit() && initialization.supported() && !initialization.corrupt()) {
            boolean submerged = eligibility.submergedEyes();
            boolean externalBreathing = player.hasEffect(MobEffects.WATER_BREATHING);
            String transition = "steady";
            boolean consumed = false;
            boolean refilled = false;
            if (submerged && !externalBreathing) {
                if (reserve > 0) {
                    reserve--;
                    writeReserve(data, reserve);
                    consumed = true;
                    transition = reserve == 0 ? "reserve_empty" : "consumed";
                    player.setAirSupply(reserve > 0 ? player.getMaxAirSupply() : 0);
                } else {
                    player.setAirSupply(0);
                    transition = "empty";
                }
            } else if (!submerged && reserve < MAX_RESERVE_TICKS) {
                reserve = Math.min(MAX_RESERVE_TICKS, reserve + REAL_AIR_REFILL_TICKS);
                writeReserve(data, reserve);
                refilled = true;
                transition = reserve == MAX_RESERVE_TICKS ? "refill_complete" : "refilled";
                player.setAirSupply(player.getMaxAirSupply());
            } else if (!submerged) {
                player.setAirSupply(player.getMaxAirSupply());
                transition = "air_full";
            } else if (externalBreathing) {
                transition = "external_breathing";
            }

            if (player instanceof ServerPlayer serverPlayer
                    && (serverPlayer.tickCount % 20 == 0 || reserve == 0)) {
                serverPlayer.displayClientMessage(Component.translatable(
                        "hud.bensfintasticsharks.dive_oxygen", reserve, MAX_RESERVE_TICKS), true);
            }
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                BfsDebugManager.recordDiveOxygen(serverPlayer, eligibility, before, reserve,
                        transition, consumed, refilled, externalBreathing ? "water_breathing" : "none");
            }
        } else if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer
                && (eligibility.fullSuit() || initialization.unsupported() || initialization.corrupt())) {
            BfsDebugManager.recordDiveOxygen(serverPlayer, eligibility, before, reserve,
                    initialization.reason(), false, false, initialization.reason());
        }
        sync(player, eligibility, reserve, initialization);
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
        if (source.contains(REVISION_KEY)) {
            target.putLong(REVISION_KEY, source.getLong(REVISION_KEY));
        }
        target.remove(SYNC_INITIALIZED_KEY);
    }

    public static int readReserve(Player player) {
        Initialization initialization = ensureInitialized(player.getPersistentData());
        return initialization.supported() ? readReserve(player.getPersistentData()) : 0;
    }

    public static int schema(Player player) {
        return player.getPersistentData().getInt(SCHEMA_KEY);
    }

    public static long revision(Player player) {
        return player.getPersistentData().getLong(REVISION_KEY);
    }

    public static boolean supportsSchema(Player player) {
        return ensureInitialized(player.getPersistentData()).supported();
    }

    private static Initialization inspect(CompoundTag data) {
        if (data.contains(SCHEMA_KEY) && data.getInt(SCHEMA_KEY) > SCHEMA) {
            return new Initialization(false, false, true, "schema_unsupported");
        }
        if (data.getInt(SCHEMA_KEY) == SCHEMA && data.getBoolean(INITIALIZED_KEY)
                && data.contains(RESERVE_KEY) && data.getInt(RESERVE_KEY) >= 0
                && data.getInt(RESERVE_KEY) <= MAX_RESERVE_TICKS) {
            return new Initialization(true, false, false, "supported");
        }
        return new Initialization(true, false, false, "uninitialized");
    }

    private static Initialization ensureInitialized(CompoundTag data) {
        boolean hasSchema = data.contains(SCHEMA_KEY);
        boolean hasInitialized = data.contains(INITIALIZED_KEY);
        boolean hasReserve = data.contains(RESERVE_KEY);
        int schema = data.getInt(SCHEMA_KEY);
        if (hasSchema && schema > SCHEMA) {
            return new Initialization(false, false, true, "schema_unsupported");
        }
        if (!hasSchema && !hasInitialized && !hasReserve) {
            data.putInt(SCHEMA_KEY, SCHEMA);
            data.putBoolean(INITIALIZED_KEY, true);
            data.putInt(RESERVE_KEY, MAX_RESERVE_TICKS);
            return new Initialization(true, false, false, "initialized");
        }
        if (schema == SCHEMA && data.getBoolean(INITIALIZED_KEY)) {
            int reserve = data.getInt(RESERVE_KEY);
            if (!hasReserve || reserve < 0 || reserve > MAX_RESERVE_TICKS) {
                writeReserve(data, 0);
                return new Initialization(true, true, false, "state_corrupt");
            }
            return new Initialization(true, false, false, "supported");
        }
        if (schema == SCHEMA && !hasInitialized && !hasReserve) {
            data.putBoolean(INITIALIZED_KEY, true);
            data.putInt(RESERVE_KEY, MAX_RESERVE_TICKS);
            return new Initialization(true, false, false, "initialized");
        }
        if (schema == SCHEMA) {
            writeReserve(data, 0);
            return new Initialization(true, true, false, "state_corrupt");
        }
        return new Initialization(false, true, false, "state_corrupt");
    }

    private static int readReserve(CompoundTag data) {
        return Math.max(0, Math.min(MAX_RESERVE_TICKS, data.getInt(RESERVE_KEY)));
    }

    private static void writeReserve(CompoundTag data, int reserve) {
        data.putInt(SCHEMA_KEY, SCHEMA);
        data.putBoolean(INITIALIZED_KEY, true);
        data.putInt(RESERVE_KEY, Math.max(0, Math.min(MAX_RESERVE_TICKS, reserve)));
    }

    private static void sync(Player player, DiveSuitEligibility.Result eligibility, int reserve,
                             Initialization initialization) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        CompoundTag data = player.getPersistentData();
        String movementMode = eligibility.eligible() && initialization.supported() ? "seabed" : "vanilla";
        boolean externalBreathing = player.hasEffect(MobEffects.WATER_BREATHING);
        String oxygenMode = !eligibility.fullSuit() || !initialization.supported() ? "vanilla"
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
        DiveNetworking.send(serverPlayer, reserve, revision,
                eligibility.eligible() && initialization.supported(), movementMode, oxygenMode);
    }

    private record Initialization(boolean supported, boolean corrupt, boolean unsupported, String reason) {
    }
}
