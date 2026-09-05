package tfar.bensfintasticsharks.debug;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModBlocks;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Bounded server diagnostic capture for reproducing aquatic behaviour without a client.
 * Captured snapshots are copied on the logical server thread and written by one background worker.
 */
public final class BfsDebugManager {

    public static final int MIN_DURATION_TICKS = 20;
    public static final int MAX_DURATION_TICKS = 36_000;
    public static final int DEFAULT_DURATION_TICKS = 1_200;
    public static final int MAX_TARGETS = 32;
    public static final int MAX_QUEUE_RECORDS = 8_192;
    public static final int MAX_RECORD_BYTES = 16 * 1024;
    public static final long MAX_SESSION_BYTES = 32L * 1024L * 1024L;
    public static final long MAX_DIRECTORY_BYTES = 256L * 1024L * 1024L;
    public static final String SCHEMA_VERSION = "bfs-debug-v1";

    private static final Gson GSON = new Gson();
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss")
            .withLocale(Locale.ROOT).withZone(ZoneOffset.UTC);
    private static final ExecutorService WRITER = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "bfs-debug-writer");
        thread.setDaemon(true);
        return thread;
    });

    private static volatile Session session;

    private BfsDebugManager() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(BfsDebugManager::onServerTick);
        eventBus.addListener(BfsDebugManager::onEntityJoin);
        eventBus.addListener(BfsDebugManager::onLivingHurt);
        eventBus.addListener(BfsDebugManager::onAdvancement);
        eventBus.addListener(BfsDebugManager::onBlockBreak);
        eventBus.addListener(BfsDebugManager::onBlockPlace);
        eventBus.addListener(BfsDebugManager::onServerStopping);
    }

    public static StartResult start(CommandSourceStack source, String category, int durationTicks,
                                    Collection<? extends Entity> requestedTargets) {
        if (session != null) {
            return StartResult.failure("A BFS debug session is already active. Use /bfs debug status or /bfs debug off.");
        }
        DebugCategory parsedCategory = DebugCategory.parse(category);
        if (parsedCategory == null) {
            return StartResult.failure("Unknown debug category. Use all, movement, brain, combat, population, advancement, or algae.");
        }
        if (durationTicks < MIN_DURATION_TICKS || durationTicks > MAX_DURATION_TICKS) {
            return StartResult.failure("Debug duration must be between " + MIN_DURATION_TICKS + " and " + MAX_DURATION_TICKS + " ticks.");
        }

        ServerLevel level = source.getLevel();
        LinkedHashSet<UUID> selected = new LinkedHashSet<>();
        int eligible = 0;
        int excluded = 0;
        if (requestedTargets.isEmpty()) {
            AABB search = new AABB(source.getPosition(), source.getPosition()).inflate(128.0D);
            for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, search, BfsDebugManager::isBfsEntity)) {
                eligible++;
                if (selected.size() < MAX_TARGETS) {
                    selected.add(candidate.getUUID());
                } else {
                    excluded++;
                }
            }
        } else {
            for (Entity candidate : requestedTargets) {
                eligible++;
                if (!candidate.level().dimension().equals(level.dimension())) {
                    excluded++;
                    continue;
                }
                if (selected.size() < MAX_TARGETS) {
                    selected.add(candidate.getUUID());
                } else {
                    excluded++;
                }
            }
        }

        long startTick = level.getGameTime();
        long wallDeadlineMillis = System.currentTimeMillis() + durationTicks * 100L + 30_000L;
        Session created = new Session(UUID.randomUUID(), parsedCategory, level.dimension(), selected, eligible, excluded,
                startTick, startTick + durationTicks, wallDeadlineMillis, outputDirectory());
        session = created;
        enqueue(created, header(created));
        return StartResult.success(created, requestedTargets.isEmpty());
    }

    public static StopResult stop(String reason) {
        Session active = session;
        if (active == null) {
            return StopResult.noSession();
        }
        if (session == active) {
            session = null;
        }
        active.closed.set(true);
        enqueue(active, endRecord(active, reason));
        return StopResult.stopped(active);
    }

    public static Status status() {
        Session active = session;
        return active == null ? Status.inactive() : Status.active(active);
    }

    private static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Session active = session;
        if (active == null) {
            return;
        }
        long now = System.currentTimeMillis();
        MinecraftServer server = event.getServer();
        ServerLevel level = server.getLevel(active.dimension);
        if (level == null) {
            active.markIncomplete("source dimension is unavailable");
            stop("source_dimension_unavailable");
            return;
        }
        long tick = level.getGameTime();
        if (tick >= active.endTick) {
            stop("duration_elapsed");
            return;
        }
        if (now >= active.wallDeadlineMillis) {
            active.markIncomplete("wall deadline elapsed before the requested tick duration");
            stop("wall_deadline_elapsed");
            return;
        }
        if (!active.category.capturesMovement()) {
            return;
        }
        for (UUID targetId : active.targets) {
            Entity target = level.getEntity(targetId);
            if (target == null) {
                active.missingTargets.add(targetId);
                continue;
            }
            enqueue(active, movementRecord(active, level, target, tick));
        }
    }

    private static void onEntityJoin(EntityJoinLevelEvent event) {
        Session active = session;
        if (active == null || event.getLevel().isClientSide || !active.category.capturesPopulation()) {
            return;
        }
        if (event.getLevel().dimension().equals(active.dimension) && isBfsEntity(event.getEntity())) {
            enqueue(active, entityRecord(active, "spawn", event.getEntity(), "joined_level"));
        }
    }

    private static void onLivingHurt(LivingHurtEvent event) {
        Session active = session;
        if (active == null || !active.category.capturesCombat() || event.getEntity().level().isClientSide) {
            return;
        }
        Entity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (!victim.level().dimension().equals(active.dimension)
                || (!active.targets.contains(victim.getUUID()) && (attacker == null || !active.targets.contains(attacker.getUUID())))) {
            return;
        }
        JsonObject record = baseRecord(active, "combat", victim.level().getGameTime());
        record.addProperty("victimUuid", victim.getUUID().toString());
        record.addProperty("victimType", entityId(victim));
        record.addProperty("attackerUuid", attacker == null ? "unavailable" : attacker.getUUID().toString());
        record.addProperty("attackerType", attacker == null ? "unavailable" : entityId(attacker));
        record.addProperty("damageType", event.getSource().getMsgId());
        record.addProperty("amount", event.getAmount());
        enqueue(active, record);
    }

    private static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        Session active = session;
        if (active == null || !active.category.capturesAdvancement()) {
            return;
        }
        if (!event.getEntity().level().dimension().equals(active.dimension)) {
            return;
        }
        JsonObject record = baseRecord(active, "advancement", event.getEntity().level().getGameTime());
        record.addProperty("playerUuid", event.getEntity().getUUID().toString());
        record.addProperty("advancement", event.getAdvancement().getId().toString());
        enqueue(active, record);
    }

    private static void onBlockBreak(BlockEvent.BreakEvent event) {
        Session active = session;
        if (active == null || !active.category.capturesAlgae() || event.getPlayer().level().isClientSide) {
            return;
        }
        if (isAlgae(event.getState())) {
            enqueue(active, blockRecord(active, "algae_break", event.getPlayer(), event.getPos()));
        }
    }

    private static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        Session active = session;
        if (active == null || !active.category.capturesAlgae() || event.getEntity().level().isClientSide) {
            return;
        }
        if (isAlgae(event.getPlacedBlock())) {
            enqueue(active, blockRecord(active, "algae_place", event.getEntity(), event.getPos()));
        }
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        Session active = session;
        if (active != null && active.serverMatches(event.getServer())) {
            active.markIncomplete("server stopped before a clean capture completion");
            stop("server_stopping");
        }
    }

    private static JsonObject movementRecord(Session active, ServerLevel level, Entity entity, long tick) {
        JsonObject record = baseRecord(active, "movement", tick);
        record.addProperty("entityUuid", entity.getUUID().toString());
        record.addProperty("entityType", entityId(entity));
        record.addProperty("x", entity.getX());
        record.addProperty("y", entity.getY());
        record.addProperty("z", entity.getZ());
        record.addProperty("velocityX", entity.getDeltaMovement().x);
        record.addProperty("velocityY", entity.getDeltaMovement().y);
        record.addProperty("velocityZ", entity.getDeltaMovement().z);
        record.addProperty("yaw", entity.getYRot());
        record.addProperty("pitch", entity.getXRot());
        record.addProperty("lookX", entity.getLookAngle().x);
        record.addProperty("lookY", entity.getLookAngle().y);
        record.addProperty("lookZ", entity.getLookAngle().z);
        record.addProperty("inWater", entity.isInWaterOrBubble());
        record.addProperty("onGround", entity.onGround());
        record.addProperty("alive", entity.isAlive());
        if (entity instanceof Mob mob) {
            record.addProperty("moveControl", mob.getMoveControl().getClass().getName());
            record.addProperty("navigationDone", mob.getNavigation().isDone());
            record.addProperty("targetUuid", mob.getTarget() == null ? "none" : mob.getTarget().getUUID().toString());
            record.addProperty("targetType", mob.getTarget() == null ? "none" : entityId(mob.getTarget()));
        } else {
            record.addProperty("moveControl", "unavailable:not_a_mob");
            record.addProperty("navigationDone", "unavailable:not_a_mob");
            record.addProperty("targetUuid", "unavailable:not_a_mob");
            record.addProperty("targetType", "unavailable:not_a_mob");
        }
        record.addProperty("brainState", "unavailable:controller_specific_state_is_not_exposed_by_the_base_entity_api");
        return record;
    }

    private static JsonObject entityRecord(Session active, String event, Entity entity, String reason) {
        JsonObject record = baseRecord(active, event, entity.level().getGameTime());
        record.addProperty("entityUuid", entity.getUUID().toString());
        record.addProperty("entityType", entityId(entity));
        record.addProperty("reason", reason);
        return record;
    }

    private static JsonObject blockRecord(Session active, String event, Entity actor, net.minecraft.core.BlockPos pos) {
        JsonObject record = baseRecord(active, event, actor.level().getGameTime());
        record.addProperty("actorUuid", actor.getUUID().toString());
        record.addProperty("x", pos.getX());
        record.addProperty("y", pos.getY());
        record.addProperty("z", pos.getZ());
        return record;
    }

    private static JsonObject header(Session active) {
        JsonObject record = baseRecord(active, "header", active.startTick);
        record.addProperty("schema", SCHEMA_VERSION);
        record.addProperty("side", "server");
        record.addProperty("category", active.category.id);
        record.addProperty("requestedTicks", active.endTick - active.startTick);
        record.addProperty("eligibleTargets", active.eligibleTargets);
        record.addProperty("selectedTargets", active.targets.size());
        record.addProperty("excludedTargets", active.excludedTargets);
        record.addProperty("targetLimit", MAX_TARGETS);
        record.addProperty("artifactSha256", "unavailable:not_bound_to_a_packaged_artifact");
        record.addProperty("configuration", "unavailable:runtime_configuration_snapshot_not_yet_bound");
        return record;
    }

    private static JsonObject endRecord(Session active, String reason) {
        JsonObject record = baseRecord(active, "end", active.endTick);
        record.addProperty("reason", reason);
        record.addProperty("recordsAccepted", active.accepted.get());
        record.addProperty("recordsDropped", active.dropped.get());
        record.addProperty("incomplete", active.incomplete.get());
        record.addProperty("incompleteReason", active.incompleteReason);
        record.addProperty("missingTargets", active.missingTargets.size());
        return record;
    }

    private static JsonObject baseRecord(Session active, String type, long tick) {
        JsonObject record = new JsonObject();
        record.addProperty("schema", SCHEMA_VERSION);
        record.addProperty("sessionId", active.id.toString());
        record.addProperty("side", "server");
        record.addProperty("event", type);
        record.addProperty("tick", tick);
        record.addProperty("dimension", active.dimension.location().toString());
        record.addProperty("timestamp", Instant.now().toString());
        return record;
    }

    private static void enqueue(Session active, JsonObject record) {
        String line = GSON.toJson(record);
        if (line.getBytes(StandardCharsets.UTF_8).length > MAX_RECORD_BYTES) {
            active.drop("record exceeded the maximum size");
            return;
        }
        if (!active.records.offer(line)) {
            active.drop("writer queue reached its capacity");
            return;
        }
        active.accepted.incrementAndGet();
        scheduleWriter(active);
    }

    private static void scheduleWriter(Session active) {
        if (active.writerScheduled.compareAndSet(false, true)) {
            WRITER.execute(() -> drain(active));
        }
    }

    private static void drain(Session active) {
        try {
            prepareOutputDirectory(active.outputDirectory);
            Files.createDirectories(active.outputDirectory);
            try (BufferedWriter writer = Files.newBufferedWriter(active.outputPath, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                String line;
                while ((line = active.records.poll()) != null) {
                    byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                    if (active.writtenBytes + bytes.length + 1L > MAX_SESSION_BYTES) {
                        active.drop("session output reached the maximum size");
                        continue;
                    }
                    writer.write(line);
                    writer.newLine();
                    active.writtenBytes += bytes.length + 1L;
                }
            }
        } catch (IOException exception) {
            active.markIncomplete("writer failure: " + exception.getClass().getSimpleName());
            BensFintasticSharks.LOG.error("BFS debug capture writer failed for {}", active.id, exception);
        } finally {
            active.writerScheduled.set(false);
            if (!active.records.isEmpty()) {
                scheduleWriter(active);
            }
        }
    }

    private static void prepareOutputDirectory(Path directory) throws IOException {
        Files.createDirectories(directory);
        List<Path> files;
        try (var paths = Files.list(directory)) {
            files = paths.filter(path -> path.getFileName().toString().startsWith("bfs-debug-")
                            && path.getFileName().toString().endsWith(".jsonl"))
                    .sorted(Comparator.comparingLong(BfsDebugManager::lastModified))
                    .toList();
        }
        long total = 0L;
        for (Path file : files) {
            total += Files.size(file);
        }
        for (Path file : files) {
            if (total + MAX_SESSION_BYTES <= MAX_DIRECTORY_BYTES) {
                break;
            }
            long size = Files.size(file);
            Files.deleteIfExists(file);
            total -= size;
        }
    }

    private static long lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException exception) {
            return Long.MIN_VALUE;
        }
    }

    private static Path outputDirectory() {
        Path gameDirectory = FMLPaths.GAMEDIR.get().toAbsolutePath().normalize();
        Path directory = gameDirectory.resolve("logs").resolve("bfs-debug").normalize();
        if (!directory.startsWith(gameDirectory)) {
            throw new IllegalStateException("BFS debug directory escaped the game directory");
        }
        return directory;
    }

    private static boolean isBfsEntity(Entity entity) {
        return entity.getType() != null
                && BensFintasticSharks.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace());
    }

    private static boolean isAlgae(net.minecraft.world.level.block.state.BlockState state) {
        return state.is(ModBlocks.ALGAE_BLOCK) || state.is(ModBlocks.LARGE_GREEN_ALGAE)
                || state.is(ModBlocks.LARGE_RED_ALGAE);
    }

    private static String entityId(Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }

    public enum DebugCategory {
        ALL("all"),
        MOVEMENT("movement"),
        BRAIN("brain"),
        COMBAT("combat"),
        POPULATION("population"),
        ADVANCEMENT("advancement"),
        ALGAE("algae");

        private final String id;

        DebugCategory(String id) {
            this.id = id;
        }

        @Nullable
        public static DebugCategory parse(String value) {
            for (DebugCategory category : values()) {
                if (category.id.equals(value.toLowerCase(Locale.ROOT))) {
                    return category;
                }
            }
            return null;
        }

        private boolean capturesMovement() {
            return this == ALL || this == MOVEMENT || this == BRAIN;
        }

        private boolean capturesCombat() {
            return this == ALL || this == COMBAT;
        }

        private boolean capturesPopulation() {
            return this == ALL || this == POPULATION;
        }

        private boolean capturesAdvancement() {
            return this == ALL || this == ADVANCEMENT;
        }

        private boolean capturesAlgae() {
            return this == ALL || this == ALGAE;
        }
    }

    public record StartResult(boolean started, String message, @Nullable Session activeSession, boolean defaultTargets) {
        private static StartResult success(Session active, boolean defaultTargets) {
            return new StartResult(true, "BFS debug capture started.", active, defaultTargets);
        }

        private static StartResult failure(String message) {
            return new StartResult(false, message, null, false);
        }
    }

    public record StopResult(boolean stopped, @Nullable Session stoppedSession) {
        private static StopResult stopped(Session stopped) {
            return new StopResult(true, stopped);
        }

        private static StopResult noSession() {
            return new StopResult(false, null);
        }
    }

    public record Status(boolean active, @Nullable Session session) {
        private static Status inactive() {
            return new Status(false, null);
        }

        private static Status active(Session session) {
            return new Status(true, session);
        }
    }

    public static final class Session {
        private final UUID id;
        private final DebugCategory category;
        private final ResourceKey<Level> dimension;
        private final Set<UUID> targets;
        private final int eligibleTargets;
        private final int excludedTargets;
        private final long startTick;
        private final long endTick;
        private final long wallDeadlineMillis;
        private final Path outputDirectory;
        private final Path outputPath;
        private final ArrayBlockingQueue<String> records = new ArrayBlockingQueue<>(MAX_QUEUE_RECORDS);
        private final AtomicBoolean writerScheduled = new AtomicBoolean();
        private final AtomicBoolean closed = new AtomicBoolean();
        private final AtomicBoolean incomplete = new AtomicBoolean();
        private final AtomicLong accepted = new AtomicLong();
        private final AtomicLong dropped = new AtomicLong();
        private final Set<UUID> missingTargets = new LinkedHashSet<>();
        private volatile String incompleteReason = "none";
        private volatile long writtenBytes;

        private Session(UUID id, DebugCategory category, ResourceKey<Level> dimension, Set<UUID> targets,
                        int eligibleTargets, int excludedTargets, long startTick, long endTick,
                        long wallDeadlineMillis, Path outputDirectory) {
            this.id = id;
            this.category = category;
            this.dimension = dimension;
            this.targets = Set.copyOf(targets);
            this.eligibleTargets = eligibleTargets;
            this.excludedTargets = excludedTargets;
            this.startTick = startTick;
            this.endTick = endTick;
            this.wallDeadlineMillis = wallDeadlineMillis;
            this.outputDirectory = outputDirectory;
            this.outputPath = outputDirectory.resolve("bfs-debug-" + FILE_TIME.format(Instant.now()) + "-" + id + ".jsonl");
        }

        public UUID id() {
            return id;
        }

        public String category() {
            return category.id;
        }

        public int targetCount() {
            return targets.size();
        }

        public int eligibleTargets() {
            return eligibleTargets;
        }

        public int excludedTargets() {
            return excludedTargets;
        }

        public long startTick() {
            return startTick;
        }

        public long endTick() {
            return endTick;
        }

        public long wallDeadlineMillis() {
            return wallDeadlineMillis;
        }

        public Path outputPath() {
            return outputPath;
        }

        public long accepted() {
            return accepted.get();
        }

        public long dropped() {
            return dropped.get();
        }

        public boolean incomplete() {
            return incomplete.get();
        }

        public String incompleteReason() {
            return incompleteReason;
        }

        private void drop(String reason) {
            dropped.incrementAndGet();
            markIncomplete(reason);
        }

        private void markIncomplete(String reason) {
            incomplete.set(true);
            if ("none".equals(incompleteReason)) {
                incompleteReason = reason;
            }
        }

        private boolean serverMatches(MinecraftServer server) {
            return server.getLevel(dimension) != null;
        }
    }
}
