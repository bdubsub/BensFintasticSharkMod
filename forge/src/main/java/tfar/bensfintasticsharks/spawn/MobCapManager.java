package tfar.bensfintasticsharks.spawn;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.config.BfsConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-species mob cap enforcement.
 *
 * <p>How it works:
 * <ul>
 *     <li>Each BFS species has a configured cap (see {@link BfsConfig}).</li>
 *     <li>On a natural spawn attempt, this listener counts existing entities of the same
 *         species within {@link #COUNT_RADIUS} blocks of the spawn position.</li>
 *     <li>If the count is at or above the cap, the spawn is cancelled.</li>
 *     <li>{@code /summon}, spawn eggs, and structure spawns are excluded — only natural and
 *         chunk-generation spawns are gated.</li>
 *     <li>Caps can be overridden at runtime via {@code /bfs cap set …} (non-persistent).</li>
 * </ul>
 */
public class MobCapManager {

    /** Radius in blocks used to count "nearby" entities of the same species. */
    public static final int COUNT_RADIUS = 64;

    /**
     * Runtime overrides for caps. Persists for the server lifetime (until shutdown or
     * reset via {@code /bfs cap reset}). Not written to disk — to persist changes,
     * edit the config file.
     */
    private static final Map<EntityType<?>, Integer> RUNTIME_OVERRIDES = new ConcurrentHashMap<>();

    /** Lookup-table: species path → EntityType, populated lazily. */
    private static final Map<String, EntityType<?>> SPECIES_BY_PATH = new HashMap<>();

    /** Returns the current cap for an EntityType, or {@code -1} if uncapped. */
    public static int getCap(EntityType<?> type) {
        Integer override = RUNTIME_OVERRIDES.get(type);
        if (override != null) return override;
        return getConfigCap(type);
    }

    /** Returns the config-defined cap for the species, or {@code -1} if none defined. */
    public static int getConfigCap(EntityType<?> type) {
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (key == null || !BensFintasticSharks.MOD_ID.equals(key.getNamespace())) return -1;
        var cfg = BfsConfig.COMMON.speciesCaps.get(key.getPath());
        if (cfg == null) return -1;
        return cfg.get();
    }

    public static void setRuntimeCap(EntityType<?> type, int cap) {
        RUNTIME_OVERRIDES.put(type, cap);
    }

    public static void resetRuntimeCap(EntityType<?> type) {
        RUNTIME_OVERRIDES.remove(type);
    }

    public static void resetAllRuntimeCaps() {
        RUNTIME_OVERRIDES.clear();
    }

    /** All BFS species we track (those that have a config cap). */
    public static List<EntityType<?>> getTrackedSpecies() {
        ensureSpeciesIndexed();
        return new ArrayList<>(SPECIES_BY_PATH.values());
    }

    /** Lookup a species by its registry path (e.g. "great_white_shark"). */
    public static EntityType<?> getSpeciesByPath(String path) {
        ensureSpeciesIndexed();
        return SPECIES_BY_PATH.get(path);
    }

    /** Returns all known species paths for command tab-completion. */
    public static List<String> getSpeciesPaths() {
        ensureSpeciesIndexed();
        return new ArrayList<>(SPECIES_BY_PATH.keySet());
    }

    private static void ensureSpeciesIndexed() {
        if (!SPECIES_BY_PATH.isEmpty()) return;
        for (String path : BfsConfig.COMMON.speciesCaps.keySet()) {
            var id = BensFintasticSharks.id(path);
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
            if (type != null) SPECIES_BY_PATH.put(path, type);
        }
    }

    @SubscribeEvent
    public void onFinalizeSpawn(MobSpawnEvent.FinalizeSpawn event) {
        // Only gate natural-style spawns. /summon, spawn eggs, structure placements should pass.
        MobSpawnType reason = event.getSpawnType();
        if (reason != MobSpawnType.NATURAL
                && reason != MobSpawnType.CHUNK_GENERATION
                && reason != MobSpawnType.SPAWNER
                && reason != MobSpawnType.PATROL
                && reason != MobSpawnType.REINFORCEMENT) {
            return;
        }
        Entity entity = event.getEntity();
        EntityType<?> type = entity.getType();
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (key == null || !BensFintasticSharks.MOD_ID.equals(key.getNamespace())) return;
        String speciesPath = key.getPath();

        // 1) Per-species cap
        int cap = getCap(type);
        if (cap == 0) {
            event.setSpawnCancelled(true);
            return;
        }
        if (cap > 0) {
            Level level = entity.level();
            AABB area = new AABB(event.getX(), event.getY(), event.getZ(), event.getX(), event.getY(), event.getZ()).inflate(COUNT_RADIUS);
            int count = level.getEntitiesOfClass(entity.getClass(), area, e -> e != entity && e.isAlive()).size();
            if (count >= cap) {
                event.setSpawnCancelled(true);
                return;
            }
        }

        // 2) Per-species spawn-chance multiplier
        var chanceCfg = BfsConfig.COMMON.speciesSpawnChance.get(speciesPath);
        if (chanceCfg != null) {
            double chance = chanceCfg.get();
            if (chance <= 0.0) {
                event.setSpawnCancelled(true);
                return;
            }
            if (chance < 1.0 && entity.level().getRandom().nextDouble() > chance) {
                event.setSpawnCancelled(true);
                return;
            }
            // chance > 1 is allowed but doesn't help here — the spawn already passed; extra weight
            // comes via the biome modifier or via spawning extras as group-min (handled elsewhere).
        }

        // 3) Per-species group minimum — spawn extras nearby when first mob of a group
        // finalizes. Only on NATURAL/CHUNK_GENERATION to avoid spawner/patrol stacking.
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION) {
            var groupMin = BfsConfig.COMMON.speciesGroupMin.get(speciesPath);
            if (groupMin != null && groupMin.get() > 1) {
                spawnGroupExtras(event, groupMin.get());
            }
        }
    }

    private void spawnGroupExtras(MobSpawnEvent.FinalizeSpawn event, int desiredGroupSize) {
        if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl)) return;
        Entity primary = event.getEntity();
        EntityType<?> type = primary.getType();
        // Count siblings already present nearby — bail if we have enough.
        AABB nearby = primary.getBoundingBox().inflate(16);
        int existing = sl.getEntitiesOfClass(primary.getClass(), nearby, e -> e != primary && e.isAlive()).size();
        int toSpawn = Math.max(0, desiredGroupSize - 1 - existing);
        if (toSpawn <= 0) return;
        var rnd = sl.getRandom();
        for (int i = 0; i < toSpawn; i++) {
            double ox = (rnd.nextDouble() - 0.5) * 6.0;
            double oz = (rnd.nextDouble() - 0.5) * 6.0;
            var pos = new net.minecraft.core.BlockPos((int) (primary.getX() + ox), (int) primary.getY(), (int) (primary.getZ() + oz));
            if (!sl.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) continue;
            Entity sibling = type.create(sl);
            if (sibling == null) continue;
            sibling.moveTo(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, rnd.nextFloat() * 360f, 0);
            sl.addFreshEntity(sibling);
        }
    }
}
