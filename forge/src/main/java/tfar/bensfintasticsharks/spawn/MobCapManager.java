package tfar.bensfintasticsharks.spawn;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.init.ModEntityTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private static final ThreadLocal<Boolean> REPLACING_VANILLA_FISH =
            ThreadLocal.withInitial(() -> false);
    private static final AtomicBoolean REPLACEMENT_CATEGORY_ERROR_REPORTED = new AtomicBoolean();

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
        MobSpawnType reason = event.getSpawnType();
        if (REPLACING_VANILLA_FISH.get()) {
            return;
        }

        boolean naturalSpawn = reason == MobSpawnType.NATURAL
                || reason == MobSpawnType.CHUNK_GENERATION;
        EntityType<?> type = event.getEntity().getType();

        if (naturalSpawn && BfsConfig.COMMON.replaceVanillaMobs.get()) {
            if (replacementTypeFor(type) != null && replaceNaturalFish(event)) {
                return;
            }
            if (isAtlanticFish(type)) {
                event.setSpawnCancelled(true);
                return;
            }
        }

        if (naturalSpawn
                && BfsConfig.COMMON.disableVanillaAquaticSpawns.get()
                && isVanillaAquatic(type)) {
            event.setSpawnCancelled(true);
            return;
        }
        if (reason != MobSpawnType.NATURAL
                && reason != MobSpawnType.CHUNK_GENERATION
                && reason != MobSpawnType.SPAWNER
                && reason != MobSpawnType.PATROL
                && reason != MobSpawnType.REINFORCEMENT) {
            return;
        }
        Entity entity = event.getEntity();
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

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide
                || !BfsConfig.COMMON.replaceVanillaMobs.get()
                || !(event.getEntity() instanceof Mob original)) {
            return;
        }

        MobSpawnType reason = original.getSpawnType();
        if (reason != MobSpawnType.SPAWN_EGG && reason != MobSpawnType.DISPENSER) {
            return;
        }

        EntityType<? extends Mob> replacementType = replacementTypeFor(original.getType());
        if (replacementType == null) {
            return;
        }

        Mob replacement = replacementType.create(event.getLevel());
        if (replacement == null) {
            return;
        }

        CompoundTag data = new CompoundTag();
        original.saveWithoutId(data);
        replacement.load(data);
        if (event.getLevel().addFreshEntity(replacement)) {
            event.setCanceled(true);
        }
    }

    private static boolean replaceNaturalFish(MobSpawnEvent.FinalizeSpawn event) {
        EntityType<?> originalType = event.getEntity().getType();
        EntityType<? extends Mob> replacementType = replacementTypeFor(originalType);
        if (replacementType == null) {
            return false;
        }
        if (!usesSameMobCategory(originalType, replacementType)) {
            if (REPLACEMENT_CATEGORY_ERROR_REPORTED.compareAndSet(false, true)) {
                BensFintasticSharks.LOG.error(
                        "Vanilla fish replacement is disabled because {} uses {} instead of {}.",
                        BuiltInRegistries.ENTITY_TYPE.getKey(replacementType),
                        replacementType.getCategory().getName(),
                        originalType.getCategory().getName()
                );
            }
            event.setSpawnCancelled(true);
            return true;
        }

        Mob original = event.getEntity();
        Mob replacement = replacementType.create(original.level());
        if (replacement == null) {
            return false;
        }

        replacement.moveTo(
                original.getX(),
                original.getY(),
                original.getZ(),
                original.getYRot(),
                original.getXRot()
        );
        replacement.yHeadRot = original.yHeadRot;
        replacement.yBodyRot = original.yBodyRot;
        replacement.setDeltaMovement(original.getDeltaMovement());

        SpawnGroupData sourceGroup = event.getSpawnData();
        boolean added;
        REPLACING_VANILLA_FISH.set(true);
        try {
            ForgeEventFactory.onFinalizeSpawn(
                    replacement,
                    event.getLevel(),
                    event.getDifficulty(),
                    event.getSpawnType(),
                    sourceGroup,
                    event.getSpawnTag()
            );
            added = event.getLevel().addFreshEntity(replacement);
        } finally {
            REPLACING_VANILLA_FISH.remove();
        }

        if (!added) {
            return false;
        }

        if (sourceGroup == null && replacement instanceof AbstractSchoolingFish schoolingFish) {
            event.setSpawnData(new AbstractSchoolingFish.SchoolSpawnGroupData(schoolingFish));
        }
        event.setSpawnCancelled(true);
        return true;
    }

    public static void validateVanillaFishReplacementCategories() {
        validateReplacementCategory(EntityType.COD, ModEntityTypes.ATLANTIC_COD);
        validateReplacementCategory(EntityType.SALMON, ModEntityTypes.ATLANTIC_SALMON);
    }

    private static void validateReplacementCategory(EntityType<?> source, EntityType<?> replacement) {
        if (!usesSameMobCategory(source, replacement)) {
            throw new IllegalStateException(
                    BuiltInRegistries.ENTITY_TYPE.getKey(replacement)
                            + " must use the same mob category as "
                            + BuiltInRegistries.ENTITY_TYPE.getKey(source)
            );
        }
    }

    private static boolean usesSameMobCategory(EntityType<?> source, EntityType<?> replacement) {
        return source.getCategory() == replacement.getCategory();
    }

    private static EntityType<? extends Mob> replacementTypeFor(EntityType<?> type) {
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (key == null) {
            return null;
        }
        VanillaFishReplacementPolicy.Replacement replacement =
                VanillaFishReplacementPolicy.replacementFor(key.getNamespace(), key.getPath());
        if (replacement == null) {
            return null;
        }
        return switch (replacement) {
            case ATLANTIC_COD -> ModEntityTypes.ATLANTIC_COD;
            case ATLANTIC_SALMON -> ModEntityTypes.ATLANTIC_SALMON;
        };
    }

    private static boolean isAtlanticFish(EntityType<?> type) {
        return type == ModEntityTypes.ATLANTIC_COD || type == ModEntityTypes.ATLANTIC_SALMON;
    }

    private static boolean isVanillaAquatic(EntityType<?> type) {
        var key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (key == null || !"minecraft".equals(key.getNamespace())) return false;
        var category = type.getCategory();
        return category == net.minecraft.world.entity.MobCategory.WATER_CREATURE
                || category == net.minecraft.world.entity.MobCategory.WATER_AMBIENT
                || category == net.minecraft.world.entity.MobCategory.UNDERGROUND_WATER_CREATURE
                || category == net.minecraft.world.entity.MobCategory.AXOLOTLS
                || type == EntityType.TURTLE;
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
