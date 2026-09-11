package tfar.bensfintasticsharks.spawn;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.init.ModEntityTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private static final ThreadLocal<Boolean> SPAWNING_GROUP_EXTRAS =
            ThreadLocal.withInitial(() -> false);
    private static final AtomicBoolean REPLACEMENT_CATEGORY_ERROR_REPORTED = new AtomicBoolean();
    private static final Map<UUID, ServerLevel> PENDING_BUCKET_REPLACEMENTS = new HashMap<>();
    private static final Map<UUID, ServerLevel> PENDING_FINALIZED_FISH_REPLACEMENTS = new HashMap<>();

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
        if (REPLACING_VANILLA_FISH.get()) {
            return;
        }

        MobSpawnType reason = event.getSpawnType();
        boolean naturalSpawn = reason == MobSpawnType.NATURAL
                || reason == MobSpawnType.CHUNK_GENERATION;
        EntityType<?> type = event.getEntity().getType();

        if (BfsConfig.COMMON.replaceVanillaMobs.get()
                && isFinalizedFishJoinSource(reason)
                && replacementTypeFor(type) != null
                && event.getEntity().level() instanceof ServerLevel serverLevel) {
            PENDING_FINALIZED_FISH_REPLACEMENTS.put(event.getEntity().getUUID(), serverLevel);
        }

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
            if (groupMin != null && groupMin.get() > 1 && !SPAWNING_GROUP_EXTRAS.get()) {
                spawnGroupExtras(event, groupMin.get());
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (REPLACING_VANILLA_FISH.get()
                || event.getLevel().isClientSide
                || !BfsConfig.COMMON.replaceVanillaMobs.get()
                || !(event.getEntity() instanceof Mob original)) {
            return;
        }

        MobSpawnType reason = original.getSpawnType();
        EntityType<? extends Mob> replacementType = replacementTypeFor(original.getType());
        if (replacementType == null) {
            return;
        }
        ServerLevel finalizedSpawnLevel = PENDING_FINALIZED_FISH_REPLACEMENTS.remove(original.getUUID());
        boolean finalizedFishSpawn = finalizedSpawnLevel == event.getLevel();
        boolean commandSpawn = reason == null && isSummonCommandSpawn();
        boolean structureSpawn = reason == null && isStructurePlacement();
        if (!VanillaFishReplacementPolicy.replacesEntityJoinSource(reason)
                && !finalizedFishSpawn
                && !commandSpawn
                && !structureSpawn) {
            return;
        }
        if (!usesSameMobCategory(original.getType(), replacementType)) {
            reportReplacementCategoryError(original.getType(), replacementType);
            event.setCanceled(true);
            return;
        }

        if (reason == MobSpawnType.BUCKET) {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                PENDING_BUCKET_REPLACEMENTS.put(original.getUUID(), serverLevel);
            }
            return;
        }

        replaceJoinedFish(original, replacementType);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Iterator<Map.Entry<UUID, ServerLevel>> pending = PENDING_BUCKET_REPLACEMENTS.entrySet().iterator();
        while (pending.hasNext()) {
            Map.Entry<UUID, ServerLevel> entry = pending.next();
            pending.remove();
            Entity entity = entry.getValue().getEntity(entry.getKey());
            if (!(entity instanceof Mob original) || !original.isAlive()) {
                continue;
            }
            EntityType<? extends Mob> replacementType = replacementTypeFor(original.getType());
            if (replacementType == null) {
                continue;
            }
            if (!usesSameMobCategory(original.getType(), replacementType)) {
                reportReplacementCategoryError(original.getType(), replacementType);
                continue;
            }
            replaceJoinedFish(original, replacementType);
            original.discard();
        }
        PENDING_FINALIZED_FISH_REPLACEMENTS.clear();
    }

    private static boolean isFinalizedFishJoinSource(MobSpawnType reason) {
        return reason == MobSpawnType.SPAWNER || reason == MobSpawnType.STRUCTURE;
    }

    private static boolean replaceJoinedFish(Mob original, EntityType<? extends Mob> replacementType) {
        Mob replacement = replacementType.create(original.level());
        if (replacement == null) {
            return false;
        }

        copySafeSpawnState(original, replacement, null);
        replacement.moveTo(original.getX(), original.getY(), original.getZ(), original.getYRot(), original.getXRot());
        replacement.setDeltaMovement(original.getDeltaMovement());
        replacement.yHeadRot = original.yHeadRot;
        replacement.yBodyRot = original.yBodyRot;
        REPLACING_VANILLA_FISH.set(true);
        try {
            return original.level().addFreshEntity(replacement);
        } finally {
            REPLACING_VANILLA_FISH.remove();
        }
    }

    private static boolean isSummonCommandSpawn() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(frames ->
                frames.anyMatch(frame -> frame.getDeclaringClass() == SummonCommand.class));
    }

    private static boolean isStructurePlacement() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).walk(frames ->
                frames.anyMatch(frame -> frame.getDeclaringClass().getName().equals(
                        "net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate")
                        && (frame.getMethodName().equals("placeInWorld")
                        || frame.getMethodName().equals("addEntitiesToWorld"))));
    }

    private static void copySafeSpawnState(Mob original, Mob replacement, CompoundTag spawnTag) {
        CompoundTag data = new CompoundTag();
        original.saveWithoutId(data);
        if (spawnTag != null) {
            data.merge(spawnTag.copy());
        }
        // A replacement must receive authored state such as a custom name and safe spawn
        // data, but never source identity or attachment state. Loading UUID, position,
        // motion, passengers, or leash data would collide with the source or resurrect
        // relationships that were not part of the fish conversion.
        data.remove("id");
        data.remove("UUID");
        data.remove("Pos");
        data.remove("Motion");
        data.remove("Rotation");
        data.remove("Passengers");
        data.remove("Leash");
        replacement.load(data);
    }

    private static boolean replaceNaturalFish(MobSpawnEvent.FinalizeSpawn event) {
        EntityType<?> originalType = event.getEntity().getType();
        EntityType<? extends Mob> replacementType = replacementTypeFor(originalType);
        if (replacementType == null) {
            return false;
        }
        if (!usesSameMobCategory(originalType, replacementType)) {
            reportReplacementCategoryError(originalType, replacementType);
            return cancelVanillaNaturalFish(event);
        }

        Mob original = event.getEntity();
        Mob replacement = replacementType.create(original.level());
        if (replacement == null) {
            return cancelVanillaNaturalFish(event);
        }
        if (!hasNaturalReplacementCapacity(event, replacement)) {
            return cancelVanillaNaturalFish(event);
        }

        copySafeSpawnState(original, replacement, event.getSpawnTag());
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
            sourceGroup = ForgeEventFactory.onFinalizeSpawn(
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
            return cancelVanillaNaturalFish(event);
        }

        if (sourceGroup == null && replacement instanceof AbstractSchoolingFish schoolingFish) {
            event.setSpawnData(new AbstractSchoolingFish.SchoolSpawnGroupData(schoolingFish));
        }
        event.setSpawnCancelled(true);
        return true;
    }

    private static boolean cancelVanillaNaturalFish(MobSpawnEvent.FinalizeSpawn event) {
        event.setSpawnCancelled(true);
        return true;
    }

    private static boolean hasNaturalReplacementCapacity(MobSpawnEvent.FinalizeSpawn event, Mob replacement) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return false;
        }

        int cap = getCap(replacement.getType());
        if (cap < 0) {
            return true;
        }
        if (cap == 0) {
            return false;
        }

        AABB area = new AABB(
                event.getX(), event.getY(), event.getZ(),
                event.getX(), event.getY(), event.getZ()
        ).inflate(COUNT_RADIUS);
        int count = level.getEntitiesOfClass(replacement.getClass(), area, Entity::isAlive).size();
        return count < cap;
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

    private static void reportReplacementCategoryError(EntityType<?> source, EntityType<?> replacement) {
        if (REPLACEMENT_CATEGORY_ERROR_REPORTED.compareAndSet(false, true)) {
            BensFintasticSharks.LOG.error(
                    "Vanilla fish replacement is disabled because {} uses {} instead of {}.",
                    BuiltInRegistries.ENTITY_TYPE.getKey(replacement),
                    replacement.getCategory().getName(),
                    source.getCategory().getName()
            );
        }
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
        Mob primary = event.getEntity();
        EntityType<?> type = primary.getType();
        AABB nearby = primary.getBoundingBox().inflate(COUNT_RADIUS);
        int existing = sl.getEntitiesOfClass(primary.getClass(), nearby, e -> e != primary && e.isAlive()).size();
        int cap = getCap(type);
        int available = cap > 0 ? Math.max(0, cap - existing - 1) : Integer.MAX_VALUE;
        int toSpawn = Math.min(available, Math.max(0, desiredGroupSize - 1 - existing));
        if (toSpawn <= 0) return;
        var rnd = sl.getRandom();
        Boolean previous = SPAWNING_GROUP_EXTRAS.get();
        SPAWNING_GROUP_EXTRAS.set(true);
        try {
            SpawnGroupData groupData = event.getSpawnData();
            for (int i = 0; i < toSpawn; i++) {
                double ox = (rnd.nextDouble() - 0.5) * 6.0;
                double oz = (rnd.nextDouble() - 0.5) * 6.0;
                var pos = new net.minecraft.core.BlockPos((int) (primary.getX() + ox), (int) primary.getY(), (int) (primary.getZ() + oz));
                var fluid = sl.getFluidState(pos);
                if (!fluid.is(net.minecraft.tags.FluidTags.WATER) || !fluid.isSource()) continue;
                Entity created = type.create(sl);
                if (!(created instanceof Mob sibling)) continue;
                sibling.moveTo(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, rnd.nextFloat() * 360f, 0);
                if (!sl.noCollision(sibling, sibling.getBoundingBox())) continue;
                groupData = ForgeEventFactory.onFinalizeSpawn(
                        sibling,
                        event.getLevel(),
                        event.getDifficulty(),
                        event.getSpawnType(),
                        groupData,
                        event.getSpawnTag());
                if (groupData == null) continue;
                if (!sl.addFreshEntity(sibling)) continue;
                event.setSpawnData(groupData);
            }
        } finally {
            SPAWNING_GROUP_EXTRAS.set(previous);
        }
    }
}
