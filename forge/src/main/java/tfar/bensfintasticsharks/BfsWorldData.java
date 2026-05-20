package tfar.bensfintasticsharks;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

/**
 * Per-world flag tracking whether Captain Ben's Hat has already been generated
 * by a natural loot source. Once true, subsequent natural rolls are suppressed
 * so the hat can only appear once per world from world generation / loot tables.
 *
 * <p>Players can still receive duplicates via creative mode or /give — those
 * paths do not consult this flag.</p>
 */
public class BfsWorldData extends SavedData {

    private static final String ID = "bensfintasticsharks_world";
    private boolean captainBenHatClaimed;

    public boolean isCaptainBenHatClaimed() { return captainBenHatClaimed; }

    public void setCaptainBenHatClaimed(boolean v) {
        if (this.captainBenHatClaimed != v) {
            this.captainBenHatClaimed = v;
            this.setDirty();
        }
    }

    public static BfsWorldData get(ServerLevel level) {
        // Always stored on the overworld so all dimensions share one flag.
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
                BfsWorldData::load, BfsWorldData::new, ID);
    }

    public static BfsWorldData get(Level level) {
        if (level instanceof ServerLevel sl) return get(sl);
        throw new IllegalStateException("BfsWorldData must be accessed on the server side");
    }

    public static BfsWorldData load(CompoundTag tag) {
        BfsWorldData d = new BfsWorldData();
        d.captainBenHatClaimed = tag.getBoolean("CaptainBenHatClaimed");
        return d;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        tag.putBoolean("CaptainBenHatClaimed", captainBenHatClaimed);
        return tag;
    }
}
