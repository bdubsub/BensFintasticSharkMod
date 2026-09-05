package tfar.bensfintasticsharks.debug;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.SharedConstants;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.ModList;
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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    public static final String SCHEMA_VERSION = "bfs-debug-v2";

    private static final Gson GSON = new Gson();
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss")
            .withLocale(Locale.ROOT).withZone(ZoneOffset.UTC);
    private static final ExecutorService WRITER = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "bfs-debug-writer");
        thread.setDaemon(true);
        return thread;
    });

    private static volatile Session session;
    private static volatile StopSummary lastStop = StopSummary.none();

    private BfsDebugManager() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(BfsDebugManager::onServerTick);
        eventBus.addListener(BfsDebugManager::onEntityJoin);
        eventBus.addListener(BfsDebugManager::onEntityLeave);
        eventBus.addListener(BfsDebugManager::onLivingHurt);
        eventBus.addListener(BfsDebugManager::onAdvancement);
        eventBus.addListener(BfsDebugManager::onBlockBreak);
        eventBus.addListener(BfsDebugManager::onBlockPlace);
        eventBus.addListener(BfsDebugManager::onServerStopping);
    }

    public static StartResult start(CommandSourceStack source, String category, int durationTicks,
                                    Collection<? extends Entity> requestedTargets) {
        if (session != null) {
            return StartResult.existing(session);
        }
        DebugCategory parsedCategory = DebugCategory.parse(category);
        if (parsedCategory == null) {
            return StartResult.failure("Unknown debug category. Use all, movement, brain, combat, population, advancement, or algae.");
        }
        if (durationTicks < MIN_DURATION_TICKS || durationTicks > MAX_DURATION_TICKS) {
            return StartResult.failure("Debug duration must be between " + MIN_DURATION_TICKS + " and " + MAX_DURATION_TICKS + " ticks.");
        }

        Path outputDirectory = outputDirectory();
        try {
            ensureDirectoryBudget(diagnosticRoot());
        } catch (IOException exception) {
            return StartResult.failure("BFS debug capture cannot start because its log budget is unavailable: "
                    + exception.getClass().getSimpleName());
        }

        ServerLevel level = source.getLevel();
        LinkedHashSet<UUID> selected = new LinkedHashSet<>();
        int eligible = 0;
        int excluded = 0;
        if (requestedTargets.isEmpty()) {
            AABB search = new AABB(source.getPosition(), source.getPosition()).inflate(128.0D);
            List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, search, BfsDebugManager::isBfsEntity)
                    .stream().sorted(Comparator.comparing(Entity::getUUID)).toList();
            for (LivingEntity candidate : candidates) {
                eligible++;
                if (selected.size() < MAX_TARGETS) {
                    selected.add(candidate.getUUID());
                } else {
                    excluded++;
                }
            }
        } else {
            List<? extends Entity> candidates = requestedTargets.stream()
                    .sorted(Comparator.comparing(Entity::getUUID)).toList();
            for (Entity candidate : candidates) {
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
        Session created = new Session(UUID.randomUUID(), source.getServer(), parsedCategory, level.dimension(), selected,
                eligible, excluded, startTick, startTick + durationTicks, wallDeadlineMillis, outputDirectory);
        session = created;
        enqueue(created, header(created));
        BensFintasticSharks.LOG.info("BFS debug capture {} started. category={}, targets={}, output={}",
                created.id, created.category.id, created.targetCount(), created.outputPath);
        return StartResult.success(created, requestedTargets.isEmpty());
    }

    public static StopResult stop(String reason) {
        Session active = session;
        if (active == null) {
            return StopResult.noSession();
        }
        finish(active, reason);
        return StopResult.stopped(active);
    }

    public static Status status() {
        Session active = session;
        return active == null ? Status.inactive(lastStop) : Status.active(active);
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
        active.lastTick = tick;
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
        for (UUID targetId : active.trackedTargets()) {
            Entity target = level.getEntity(targetId);
            if (target == null) {
                releaseTarget(active, level, targetId, "entity_unavailable_in_source_dimension");
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

    private static void onEntityLeave(EntityLeaveLevelEvent event) {
        Session active = session;
        if (active == null || event.getLevel().isClientSide || !event.getLevel().dimension().equals(active.dimension)) {
            return;
        }
        Entity entity = event.getEntity();
        releaseTarget(active, event.getLevel(), entity.getUUID(), "entity_left_level", entity);
    }

    private static void onLivingHurt(LivingHurtEvent event) {
        Session active = session;
        if (active == null || !active.category.capturesCombat() || event.getEntity().level().isClientSide) {
            return;
        }
        Entity victim = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (!victim.level().dimension().equals(active.dimension)
                || (!active.tracks(victim.getUUID()) && (attacker == null || !active.tracks(attacker.getUUID())))) {
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

    private static void releaseTarget(Session active, Level level, UUID targetId, String reason) {
        releaseTarget(active, level, targetId, reason, null);
    }

    private static void releaseTarget(Session active, Level level, UUID targetId, String reason, @Nullable Entity entity) {
        if (!active.releaseTarget(targetId)) {
            return;
        }
        active.previousPositions.remove(targetId);
        active.previousSampleNanos.remove(targetId);
        active.previousYaw.remove(targetId);
        active.previousPitch.remove(targetId);
        JsonObject record = baseRecord(active, "target_lifecycle", level.getGameTime());
        record.addProperty("entityUuid", targetId.toString());
        record.addProperty("entityType", entity == null ? "unavailable:entity_not_loaded" : entityId(entity));
        if (entity == null) {
            record.addProperty("runtimeId", "unavailable:entity_not_loaded");
        } else {
            record.addProperty("runtimeId", entity.getId());
        }
        record.addProperty("reason", reason);
        enqueue(active, record);
    }

    private static JsonObject movementRecord(Session active, ServerLevel level, Entity entity, long tick) {
        JsonObject record = baseRecord(active, "movement", tick);
        record.addProperty("entityUuid", entity.getUUID().toString());
        record.addProperty("entityType", entityId(entity));
        record.addProperty("runtimeId", entity.getId());
        record.addProperty("x", entity.getX());
        record.addProperty("y", entity.getY());
        record.addProperty("z", entity.getZ());
        record.addProperty("velocityX", entity.getDeltaMovement().x);
        record.addProperty("velocityY", entity.getDeltaMovement().y);
        record.addProperty("velocityZ", entity.getDeltaMovement().z);
        addPositionDelta(active, record, entity);
        record.addProperty("yaw", entity.getYRot());
        record.addProperty("pitch", entity.getXRot());
        addAngularDeltas(active, record, entity);
        record.addProperty("lookX", entity.getLookAngle().x);
        record.addProperty("lookY", entity.getLookAngle().y);
        record.addProperty("lookZ", entity.getLookAngle().z);
        record.addProperty("inWater", entity.isInWaterOrBubble());
        record.addProperty("onGround", entity.onGround());
        record.addProperty("horizontalCollision", entity.horizontalCollision);
        record.addProperty("verticalCollision", entity.verticalCollision);
        record.addProperty("gravityEnabled", !entity.isNoGravity());
        record.addProperty("alive", entity.isAlive());
        record.addProperty("routeAttemptId", "unavailable:navigation_attempt_identity_not_exposed");
        record.addProperty("desiredPitch", "unavailable:movement_controller_target_pitch_not_exposed");
        record.addProperty("selectedWaypoint", "unavailable:navigation_waypoint_not_exposed");
        record.addProperty("routeProgress", "unavailable:navigation_attempt_identity_not_exposed");
        record.addProperty("arrivalReason", "unavailable:navigation_attempt_identity_not_exposed");
        record.addProperty("locomotionMode", "unavailable:species_locomotion_mode_not_exposed");
        record.addProperty("scalarPropulsionSpeed", "unavailable:movement_controller_scalar_speed_not_exposed");
        record.addProperty("motionWriter", entity instanceof Mob mob ? mob.getMoveControl().getClass().getName()
                : "unavailable:not_a_mob");
        addTrajectoryPitch(record, entity.getDeltaMovement());
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

    private static void addPositionDelta(Session active, JsonObject record, Entity entity) {
        Vec3 current = entity.position();
        Vec3 previous = active.previousPositions.put(entity.getUUID(), current);
        long now = System.nanoTime();
        Long previousNanos = active.previousSampleNanos.put(entity.getUUID(), now);
        if (previous == null) {
            record.addProperty("positionDelta", "unavailable:no_previous_sample");
            record.addProperty("elapsedBlocksPerSecond", "unavailable:no_previous_sample");
            return;
        }
        Vec3 delta = current.subtract(previous);
        double horizontal = Math.hypot(delta.x, delta.z);
        record.addProperty("positionDeltaX", delta.x);
        record.addProperty("positionDeltaY", delta.y);
        record.addProperty("positionDeltaZ", delta.z);
        record.addProperty("horizontalBlocksPerTick", horizontal);
        record.addProperty("signedVerticalBlocksPerTick", delta.y);
        record.addProperty("totalBlocksPerTick", delta.length());
        record.addProperty("nominalBlocksPerSecond", delta.length() * 20.0D);
        if (previousNanos == null || now <= previousNanos) {
            record.addProperty("elapsedBlocksPerSecond", "unavailable:invalid_elapsed_sample_window");
            return;
        }
        record.addProperty("elapsedBlocksPerSecond", delta.length() / ((now - previousNanos) / 1_000_000_000.0D));
    }

    private static void addAngularDeltas(Session active, JsonObject record, Entity entity) {
        Float previousYaw = active.previousYaw.put(entity.getUUID(), entity.getYRot());
        Float previousPitch = active.previousPitch.put(entity.getUUID(), entity.getXRot());
        if (previousYaw == null || previousPitch == null) {
            record.addProperty("yawDeltaDegrees", "unavailable:no_previous_sample");
            record.addProperty("pitchDeltaDegrees", "unavailable:no_previous_sample");
            return;
        }
        record.addProperty("yawDeltaDegrees", net.minecraft.util.Mth.wrapDegrees(entity.getYRot() - previousYaw));
        record.addProperty("pitchDeltaDegrees", net.minecraft.util.Mth.wrapDegrees(entity.getXRot() - previousPitch));
    }

    private static void addTrajectoryPitch(JsonObject record, Vec3 velocity) {
        double horizontal = Math.hypot(velocity.x, velocity.z);
        if (horizontal == 0.0D && velocity.y == 0.0D) {
            record.addProperty("trajectoryPitch", "unavailable:zero_velocity");
            return;
        }
        record.addProperty("trajectoryPitch", -Math.toDegrees(Math.atan2(velocity.y, horizontal)));
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
        record.addProperty("hostRole", hostRole(active.server));
        record.addProperty("scenarioId", "unavailable:provided_by_candidate_manifest");
        record.addProperty("requirementId", "unavailable:provided_by_candidate_manifest");
        record.addProperty("requestedTicks", active.endTick - active.startTick);
        record.addProperty("eligibleTargets", active.eligibleTargets);
        record.addProperty("selectedTargets", active.selectedTargetCount());
        record.addProperty("excludedTargets", active.excludedTargets);
        record.addProperty("targetLimit", MAX_TARGETS);
        record.addProperty("queueLimit", MAX_QUEUE_RECORDS);
        record.addProperty("recordByteLimit", MAX_RECORD_BYTES);
        record.addProperty("sessionByteLimit", MAX_SESSION_BYTES);
        record.addProperty("directoryByteLimit", MAX_DIRECTORY_BYTES);
        record.addProperty("nominalTicksPerSecond", 20);
        record.addProperty("units", "blocks, blocks_per_tick, blocks_per_second, degrees, nanoseconds");
        record.addProperty("modId", BensFintasticSharks.MOD_ID);
        record.addProperty("modVersion", loadedModVersion(BensFintasticSharks.MOD_ID));
        record.addProperty("minecraftVersion", SharedConstants.getCurrentVersion().getName());
        record.addProperty("forgeVersion", loadedModVersion("forge"));
        record.addProperty("javaVersion", System.getProperty("java.version", "unavailable:java_version_property_missing"));
        record.addProperty("geckoLibVersion", loadedModVersion("geckolib"));
        record.addProperty("geckoLibNetworkProtocol", geckoLibNetworkProtocol());
        record.addProperty("smartBrainLibVersion", loadedModVersion("smartbrainlib"));
        record.addProperty("sourceRevision", runtimeBinding("bfs.source.revision",
                "unavailable:development_classpath_revision_not_bound"));
        record.addProperty("motionProfileVersion", runtimeBinding("bfs.motion.profile.version",
                "unavailable:phase_002_profile_not_implemented"));
        record.addProperty("artifactSha256", runtimeBinding("bfs.artifact.sha256",
                "unavailable:not_bound_to_a_packaged_artifact"));
        record.addProperty("configuration", runtimeBinding("bfs.configuration.fingerprint",
                "unavailable:runtime_configuration_snapshot_not_yet_bound"));
        record.addProperty("dataPackFingerprint", runtimeBinding("bfs.datapack.fingerprint",
                "unavailable:runtime_datapack_snapshot_not_yet_bound"));
        return record;
    }

    private static JsonObject endRecord(Session active, String reason) {
        JsonObject record = baseRecord(active, "end", active.lastTick);
        record.addProperty("reason", reason);
        record.addProperty("recordsAccepted", active.accepted.get());
        record.addProperty("recordsDropped", active.dropped.get());
        record.addProperty("incomplete", active.incomplete.get());
        record.addProperty("incompleteReason", active.incompleteReason);
        record.addProperty("missingTargets", active.missingTargets.size());
        record.addProperty("remainingTargets", active.targetCount());
        return record;
    }

    private static JsonObject baseRecord(Session active, String type, long tick) {
        JsonObject record = new JsonObject();
        record.addProperty("schema", SCHEMA_VERSION);
        record.addProperty("sessionId", active.id.toString());
        record.addProperty("side", "server");
        record.addProperty("event", type);
        record.addProperty("sequence", active.sequence.incrementAndGet());
        record.addProperty("tick", tick);
        record.addProperty("dimension", active.dimension.location().toString());
        record.addProperty("timestamp", Instant.now().toString());
        record.addProperty("monotonicElapsedNanos", System.nanoTime() - active.startNanos);
        record.addProperty("samplingPoint", "server_tick_end_or_event_callback");
        return record;
    }

    private static void enqueue(Session active, JsonObject record) {
        if (active.closed.get()) {
            return;
        }
        String line = GSON.toJson(record);
        if (line.getBytes(StandardCharsets.UTF_8).length > MAX_RECORD_BYTES) {
            active.drop("record exceeded the maximum size");
            finish(active, "record_byte_limit");
            return;
        }
        if (!active.records.offer(line)) {
            active.drop("writer queue reached its capacity");
            finish(active, "queue_limit");
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
            Files.createDirectories(active.outputDirectory);
            try (BufferedWriter writer = Files.newBufferedWriter(active.outputPath, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                String line;
                while ((line = active.records.poll()) != null) {
                    byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                    if (active.writtenBytes + bytes.length + 1L > MAX_SESSION_BYTES - MAX_RECORD_BYTES) {
                        active.drop("session output reached the maximum size");
                        finish(active, "session_byte_limit");
                        active.records.clear();
                        break;
                    }
                    writer.write(line);
                    writer.newLine();
                    active.writtenBytes += bytes.length + 1L;
                }
                writeTerminalRecord(writer, active);
            }
        } catch (IOException exception) {
            active.markIncomplete("writer failure: " + exception.getClass().getSimpleName());
            finish(active, "writer_failure");
            active.records.clear();
            BensFintasticSharks.LOG.error("BFS debug capture writer failed for {}", active.id, exception);
        } finally {
            active.writerScheduled.set(false);
            if (!active.closed.get() && !active.records.isEmpty()) {
                scheduleWriter(active);
            }
        }
    }

    private static void writeTerminalRecord(BufferedWriter writer, Session active) throws IOException {
        String terminal = active.terminalRecord;
        if (terminal == null || active.terminalWritten.get()) {
            return;
        }
        byte[] bytes = terminal.getBytes(StandardCharsets.UTF_8);
        if (active.writtenBytes + bytes.length + 1L > MAX_SESSION_BYTES) {
            active.markIncomplete("terminal record exceeds reserved session output budget");
            return;
        }
        writer.write(terminal);
        writer.newLine();
        active.writtenBytes += bytes.length + 1L;
        active.terminalWritten.set(true);
    }

    private static void finish(Session active, String reason) {
        if (!active.closed.compareAndSet(false, true)) {
            return;
        }
        if (session == active) {
            session = null;
        }
        active.stopReason = reason;
        active.terminalRecord = GSON.toJson(endRecord(active, reason));
        lastStop = StopSummary.from(active);
        scheduleWriter(active);
        BensFintasticSharks.LOG.info("BFS debug capture {} stopped. reason={}, records={}, dropped={}, incomplete={}, output={}",
                active.id, reason, active.accepted.get(), active.dropped.get(), active.incomplete.get(), active.outputPath);
    }

    private static void ensureDirectoryBudget(Path directory) throws IOException {
        Files.createDirectories(directory);
        try (var paths = Files.walk(directory)) {
            long total = paths.filter(Files::isRegularFile).mapToLong(BfsDebugManager::fileSize).sum();
            if (total + MAX_SESSION_BYTES > MAX_DIRECTORY_BYTES) {
                throw new IOException("BFS debug log budget is exhausted without deleting existing logs");
            }
        }
    }

    private static long fileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException exception) {
            return 0L;
        }
    }

    private static String loadedModVersion(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getVersion().toString())
                .orElse("unavailable:mod_not_loaded");
    }

    private static String runtimeBinding(String property, String unavailable) {
        String value = System.getProperty(property);
        return value == null || value.isBlank() ? unavailable : value;
    }

    private static String geckoLibNetworkProtocol() {
        try {
            java.lang.reflect.Method channelVersions = net.minecraftforge.network.NetworkRegistry.class
                    .getDeclaredMethod("buildChannelVersions");
            channelVersions.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.Map<net.minecraft.resources.ResourceLocation, String> versions =
                    (java.util.Map<net.minecraft.resources.ResourceLocation, String>) channelVersions.invoke(null);
            return versions.getOrDefault(new net.minecraft.resources.ResourceLocation("geckolib", "main"),
                    "unavailable:channel_not_registered");
        } catch (ReflectiveOperationException exception) {
            return "unavailable:" + exception.getClass().getSimpleName();
        }
    }

    private static String hostRole(net.minecraft.server.MinecraftServer server) {
        if (server instanceof net.minecraft.gametest.framework.GameTestServer) {
            return "gametest_server";
        }
        return server.isDedicatedServer() ? "dedicated_server" : "integrated_server";
    }

    private static Path outputDirectory() {
        Path gameDirectory = FMLPaths.GAMEDIR.get().toAbsolutePath().normalize();
        Path directory = diagnosticRoot();
        if (!directory.startsWith(gameDirectory)) {
            throw new IllegalStateException("BFS debug directory escaped the game directory");
        }
        return directory;
    }

    private static Path diagnosticRoot() {
        Path gameDirectory = FMLPaths.GAMEDIR.get().toAbsolutePath().normalize();
        return gameDirectory.resolve("logs").resolve("bfs-debug").normalize();
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

        private static StartResult existing(Session active) {
            return new StartResult(false, "BFS debug capture is already active.", active, false);
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

    public record Status(boolean active, @Nullable Session session, StopSummary lastStop) {
        private static Status inactive(StopSummary lastStop) {
            return new Status(false, null, lastStop);
        }

        private static Status active(Session session) {
            return new Status(true, session, StopSummary.none());
        }
    }

    public record StopSummary(String reason, long accepted, long dropped, boolean incomplete,
                              String incompleteReason, Path outputPath) {
        private static StopSummary none() {
            return new StopSummary("none", 0L, 0L, false, "none", Path.of("unavailable:no_completed_capture"));
        }

        private static StopSummary from(Session session) {
            return new StopSummary(session.stopReason, session.accepted.get(), session.dropped.get(),
                    session.incomplete.get(), session.incompleteReason, session.outputPath);
        }
    }

    public static final class Session {
        private final UUID id;
        private final MinecraftServer server;
        private final DebugCategory category;
        private final ResourceKey<Level> dimension;
        private final Set<UUID> selectedTargets;
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
        private final AtomicLong sequence = new AtomicLong();
        private final Set<UUID> missingTargets = new LinkedHashSet<>();
        private final Map<UUID, Vec3> previousPositions = new HashMap<>();
        private final Map<UUID, Long> previousSampleNanos = new HashMap<>();
        private final Map<UUID, Float> previousYaw = new HashMap<>();
        private final Map<UUID, Float> previousPitch = new HashMap<>();
        private final long startNanos = System.nanoTime();
        private volatile String incompleteReason = "none";
        private volatile long writtenBytes;
        private volatile long lastTick;
        private volatile String stopReason = "none";
        @Nullable
        private volatile String terminalRecord;
        private final AtomicBoolean terminalWritten = new AtomicBoolean();

        private Session(UUID id, MinecraftServer server, DebugCategory category, ResourceKey<Level> dimension, Set<UUID> targets,
                        int eligibleTargets, int excludedTargets, long startTick, long endTick,
                        long wallDeadlineMillis, Path outputDirectory) {
            this.id = id;
            this.server = server;
            this.category = category;
            this.dimension = dimension;
            this.selectedTargets = java.util.Collections.unmodifiableSet(new LinkedHashSet<>(targets));
            this.targets = new LinkedHashSet<>(targets);
            this.eligibleTargets = eligibleTargets;
            this.excludedTargets = excludedTargets;
            this.startTick = startTick;
            this.endTick = endTick;
            this.lastTick = startTick;
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

        public synchronized int targetCount() {
            return targets.size();
        }

        public int selectedTargetCount() {
            return selectedTargets.size();
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
            return this.server == server;
        }

        private synchronized List<UUID> trackedTargets() {
            return List.copyOf(targets);
        }

        private synchronized boolean tracks(UUID targetId) {
            return targets.contains(targetId);
        }

        private synchronized boolean releaseTarget(UUID targetId) {
            if (!targets.remove(targetId)) {
                return false;
            }
            missingTargets.add(targetId);
            return true;
        }
    }
}
