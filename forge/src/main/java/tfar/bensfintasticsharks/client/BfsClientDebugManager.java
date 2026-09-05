package tfar.bensfintasticsharks.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.ModList;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import tfar.bensfintasticsharks.BensFintasticSharks;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** Local client diagnostic capture. The server cannot start, inspect, or receive these records. */
public final class BfsClientDebugManager {

    private static final int MAX_TARGETS = 32;
    private static final int DEFAULT_DURATION_TICKS = 1_200;
    private static final int PRESENTATION_SAMPLE_INTERVAL_TICKS = 4;
    private static final long WALL_DURATION_MILLIS = 90_000L;
    private static final int MAX_QUEUE_RECORDS = 8_192;
    private static final int MAX_RECORD_BYTES = 16 * 1024;
    private static final long MAX_SESSION_BYTES = 32L * 1024L * 1024L;
    private static final long MAX_DIRECTORY_BYTES = 256L * 1024L * 1024L;
    private static final String SCHEMA = "bfs-debug-v2";
    private static final Gson GSON = new Gson();
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss")
            .withZone(ZoneOffset.UTC);
    private static final ExecutorService WRITER = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "bfs-client-debug-writer");
        thread.setDaemon(true);
        return thread;
    });

    private static volatile Session session;
    private static volatile StopSummary lastStop = StopSummary.none();

    private BfsClientDebugManager() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(BfsClientDebugManager::onRegisterCommands);
        eventBus.addListener(BfsClientDebugManager::onClientTick);
        eventBus.addListener(BfsClientDebugManager::onRenderLevelStage);
    }

    private static void onRegisterCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bfs")
                .then(Commands.literal("debug")
                        .then(Commands.literal("client")
                                .then(Commands.literal("on").executes(BfsClientDebugManager::start))
                                .then(Commands.literal("off").executes(BfsClientDebugManager::stop))
                                .then(Commands.literal("status").executes(BfsClientDebugManager::status)))));
    }

    private static int start(CommandContext<CommandSourceStack> context) {
        if (session != null) {
            context.getSource().sendFailure(Component.literal("A local BFS debug capture is already active."));
            return 0;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (player == null || level == null) {
            context.getSource().sendFailure(Component.literal("Join a world before starting local BFS debug capture."));
            return 0;
        }
        LinkedHashMap<UUID, Integer> targets = new LinkedHashMap<>();
        int eligible = 0;
        int excluded = 0;
        Path outputDirectory = outputDirectory();
        try {
            ensureDirectoryBudget(diagnosticRoot());
        } catch (IOException exception) {
            context.getSource().sendFailure(Component.literal("Local BFS debug capture cannot start because its log budget is unavailable."));
            return 0;
        }
        AABB search = player.getBoundingBox().inflate(128.0D);
        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, search, BfsClientDebugManager::isBfsEntity)
                .stream().sorted(java.util.Comparator.comparing(Entity::getUUID)).toList();
        for (LivingEntity candidate : candidates) {
            eligible++;
            if (targets.size() < MAX_TARGETS) {
                targets.put(candidate.getUUID(), candidate.getId());
            } else {
                excluded++;
            }
        }
        long startTick = level.getGameTime();
        Session created = new Session(UUID.randomUUID(), level.dimension().location().toString(), targets, eligible, excluded,
                startTick, startTick + DEFAULT_DURATION_TICKS, System.currentTimeMillis() + WALL_DURATION_MILLIS, outputDirectory);
        session = created;
        enqueue(created, header(created));
        BensFintasticSharks.LOG.info("Local BFS debug capture {} started. targets={}, output={}",
                created.id, created.targetCount(), created.outputPath);
        context.getSource().sendSuccess(() -> Component.literal("Local BFS debug capture started").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        context.getSource().sendSuccess(() -> Component.literal("  Side: client. Targets: " + created.targetCount() + " selected, "
                + created.excludedTargets + " excluded.")
                .withStyle(ChatFormatting.WHITE), false);
        context.getSource().sendSuccess(() -> Component.literal("  Duration: " + DEFAULT_DURATION_TICKS + " client ticks or 90 seconds. Output: "
                + created.outputPath).withStyle(ChatFormatting.GRAY), false);
        return targets.size();
    }

    private static int stop(CommandContext<CommandSourceStack> context) {
        Session active = session;
        if (active == null) {
            context.getSource().sendSuccess(() -> Component.literal("Local BFS debug capture is already inactive.")
                    .withStyle(ChatFormatting.GRAY), false);
            return 1;
        }
        finish(active, "operator_requested");
        context.getSource().sendSuccess(() -> Component.literal("Local BFS debug capture stopped. Output is finalizing at "
                + active.outputPath).withStyle(ChatFormatting.WHITE), false);
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> context) {
        Session active = session;
        if (active == null) {
            context.getSource().sendSuccess(() -> Component.literal("Local BFS debug capture: inactive").withStyle(ChatFormatting.GRAY), false);
            StopSummary completed = lastStop;
            if (!"none".equals(completed.reason)) {
                context.getSource().sendSuccess(() -> Component.literal("  Last stop: " + completed.reason + ". Records: "
                        + completed.accepted + " accepted, " + completed.dropped + " dropped. Incomplete: "
                        + completed.incomplete + ". Reason: " + completed.incompleteReason + ".")
                        .withStyle(completed.incomplete ? ChatFormatting.YELLOW : ChatFormatting.GREEN), false);
                context.getSource().sendSuccess(() -> Component.literal("  Output: " + completed.outputPath)
                        .withStyle(ChatFormatting.GRAY), false);
            }
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal("Local BFS debug capture").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        context.getSource().sendSuccess(() -> Component.literal("  Session: " + active.id + ". Targets: " + active.targetCount()
                + " selected, " + active.excludedTargets + " excluded.").withStyle(ChatFormatting.WHITE), false);
        context.getSource().sendSuccess(() -> Component.literal("  Records: " + active.accepted.get() + " accepted, "
                + active.dropped.get() + " dropped. Incomplete: " + active.incomplete.get() + ". Reason: "
                + active.incompleteReason + ".").withStyle(active.incomplete.get() ? ChatFormatting.YELLOW : ChatFormatting.GREEN), false);
        context.getSource().sendSuccess(() -> Component.literal("  Output: " + active.outputPath).withStyle(ChatFormatting.GRAY), false);
        return 1;
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Session active = session;
        if (active == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || !level.dimension().location().toString().equals(active.dimension)) {
            active.markIncomplete("client level changed before clean completion");
            finish(active, "client_level_changed");
            return;
        }
        long tick = level.getGameTime();
        active.lastTick = tick;
        if (tick >= active.endTick) {
            finish(active, "duration_elapsed");
            return;
        }
        if (System.currentTimeMillis() >= active.wallDeadlineMillis) {
            active.markIncomplete("90 second client wall deadline elapsed");
            finish(active, "wall_deadline_elapsed");
            return;
        }
        for (Map.Entry<UUID, Integer> target : active.trackedTargets()) {
            Entity entity = level.getEntity(target.getValue());
            if (entity == null || !entity.getUUID().equals(target.getKey())) {
                releaseTarget(active, level, target.getKey(), target.getValue(), "entity_unavailable_in_client_level");
                continue;
            }
            enqueue(active, movementRecord(active, entity, tick));
        }
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
        BensFintasticSharks.LOG.info("Local BFS debug capture {} stopped. reason={}, records={}, dropped={}, incomplete={}, output={}",
                active.id, reason, active.accepted.get(), active.dropped.get(), active.incomplete.get(), active.outputPath);
    }

    private static JsonObject header(Session active) {
        JsonObject record = baseRecord(active, "header", active.startTick);
        record.addProperty("schema", SCHEMA);
        record.addProperty("side", "client");
        record.addProperty("hostRole", "laptop_client");
        record.addProperty("scenarioId", "unavailable:provided_by_candidate_manifest");
        record.addProperty("requirementId", "unavailable:provided_by_candidate_manifest");
        record.addProperty("requestedTicks", DEFAULT_DURATION_TICKS);
        record.addProperty("wallDurationSeconds", WALL_DURATION_MILLIS / 1_000L);
        record.addProperty("presentationSampleIntervalTicks", PRESENTATION_SAMPLE_INTERVAL_TICKS);
        record.addProperty("presentationSamplingPoint", "after_entities_render");
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
                "unavailable:client_data_pack_snapshot_not_yet_bound"));
        return record;
    }

    private static JsonObject movementRecord(Session active, Entity entity, long tick) {
        JsonObject record = baseRecord(active, "movement", tick);
        record.addProperty("entityUuid", entity.getUUID().toString());
        record.addProperty("entityType", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
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
        record.addProperty("renderPosition", "unavailable:client_tick_snapshot_has_no_partial_render_frame");
        return record;
    }

    private static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }
        Session active = session;
        if (active == null || active.closed.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || !level.dimension().location().toString().equals(active.dimension)) {
            return;
        }
        long tick = level.getGameTime();
        if (!active.claimPresentationTick(tick)) {
            return;
        }
        for (Map.Entry<UUID, Integer> target : active.trackedTargets()) {
            Entity entity = level.getEntity(target.getValue());
            if (entity == null || !entity.getUUID().equals(target.getKey())) {
                releaseTarget(active, level, target.getKey(), target.getValue(), "entity_unavailable_during_client_render");
                continue;
            }
            enqueue(active, presentationRecord(active, entity, tick, event.getPartialTick()));
        }
    }

    private static JsonObject presentationRecord(Session active, Entity entity, long tick, float partialTick) {
        JsonObject record = baseRecord(active, "presentation", tick);
        record.addProperty("samplingPoint", "after_entities_render");
        record.addProperty("entityUuid", entity.getUUID().toString());
        record.addProperty("entityType", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
        record.addProperty("runtimeId", entity.getId());
        record.addProperty("partialTick", partialTick);
        record.addProperty("interpolatedX", Mth.lerp(partialTick, entity.xo, entity.getX()));
        record.addProperty("interpolatedY", Mth.lerp(partialTick, entity.yo, entity.getY()));
        record.addProperty("interpolatedZ", Mth.lerp(partialTick, entity.zo, entity.getZ()));
        record.addProperty("interpolatedYaw", Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot()));
        record.addProperty("interpolatedPitch", Mth.lerp(partialTick, entity.xRotO, entity.getXRot()));
        record.addProperty("interpolationSource", "client_previous_and_current_entity_state");
        addControllerSnapshot(record, entity);
        return record;
    }

    private static void addControllerSnapshot(JsonObject record, Entity entity) {
        if (!(entity instanceof GeoAnimatable animatable)) {
            record.addProperty("animationControllers", "unavailable:not_a_gecko_animatable");
            return;
        }
        try {
            AnimatableManager<?> manager = animatable.getAnimatableInstanceCache().getManagerForId(entity.getId());
            JsonObject controllers = new JsonObject();
            manager.getAnimationControllers().forEach((name, controller) ->
                    controllers.add(name, controllerSnapshot(controller)));
            record.addProperty("animationControllerCount", controllers.size());
            record.add("animationControllers", controllers);
        } catch (RuntimeException exception) {
            record.addProperty("animationControllers", "unavailable:"
                    + exception.getClass().getSimpleName());
        }
    }

    private static JsonObject controllerSnapshot(AnimationController<?> controller) {
        JsonObject record = new JsonObject();
        record.addProperty("state", controller.getAnimationState().name());
        record.addProperty("speed", controller.getAnimationSpeed());
        record.addProperty("triggered", controller.isPlayingTriggeredAnimation());
        AnimationProcessor.QueuedAnimation current = controller.getCurrentAnimation();
        if (current == null) {
            record.addProperty("currentAnimation", "unavailable:no_current_animation");
        } else {
            record.addProperty("currentAnimation", current.animation().name());
            record.addProperty("loopType", current.loopType().toString());
            record.addProperty("animationLengthSeconds", current.animation().length());
        }
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

    private static JsonObject endRecord(Session active, String reason) {
        JsonObject record = baseRecord(active, "end", active.lastTick);
        record.addProperty("reason", reason);
        record.addProperty("recordsAccepted", active.accepted.get());
        record.addProperty("recordsDropped", active.dropped.get());
        record.addProperty("incomplete", active.incomplete.get());
        record.addProperty("incompleteReason", active.incompleteReason);
        record.addProperty("missingTargets", active.missingTargetCount());
        record.addProperty("remainingTargets", active.targetCount());
        return record;
    }

    private static JsonObject baseRecord(Session active, String event, long tick) {
        JsonObject record = new JsonObject();
        record.addProperty("schema", SCHEMA);
        record.addProperty("sessionId", active.id.toString());
        record.addProperty("side", "client");
        record.addProperty("event", event);
        record.addProperty("sequence", active.sequence.incrementAndGet());
        record.addProperty("tick", tick);
        record.addProperty("dimension", active.dimension);
        record.addProperty("timestamp", Instant.now().toString());
        record.addProperty("monotonicElapsedNanos", System.nanoTime() - active.startNanos);
        record.addProperty("samplingPoint", "client_tick_end");
        return record;
    }

    private static void releaseTarget(Session active, ClientLevel level, UUID targetId, int runtimeId, String reason) {
        if (!active.releaseTarget(targetId)) {
            return;
        }
        active.previousPositions.remove(targetId);
        active.previousSampleNanos.remove(targetId);
        active.previousYaw.remove(targetId);
        active.previousPitch.remove(targetId);
        JsonObject record = baseRecord(active, "target_lifecycle", level.getGameTime());
        record.addProperty("entityUuid", targetId.toString());
        record.addProperty("entityType", "unavailable:entity_not_loaded");
        record.addProperty("runtimeId", runtimeId);
        record.addProperty("reason", reason);
        enqueue(active, record);
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
            BensFintasticSharks.LOG.error("BFS client debug capture writer failed for {}", active.id, exception);
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

    private static void ensureDirectoryBudget(Path directory) throws IOException {
        Files.createDirectories(directory);
        try (var paths = Files.walk(directory)) {
            long total = paths.filter(Files::isRegularFile).mapToLong(BfsClientDebugManager::fileSize).sum();
            if (total + MAX_SESSION_BYTES > MAX_DIRECTORY_BYTES) {
                throw new IOException("BFS client debug log budget is exhausted without deleting existing logs");
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

    private static Path outputDirectory() {
        Path gameDirectory = FMLPaths.GAMEDIR.get().toAbsolutePath().normalize();
        Path directory = diagnosticRoot().resolve("client").normalize();
        if (!directory.startsWith(gameDirectory)) {
            throw new IllegalStateException("BFS client debug directory escaped the game directory");
        }
        return directory;
    }

    private static Path diagnosticRoot() {
        Path gameDirectory = FMLPaths.GAMEDIR.get().toAbsolutePath().normalize();
        return gameDirectory.resolve("logs").resolve("bfs-debug").normalize();
    }

    private static boolean isBfsEntity(Entity entity) {
        return BensFintasticSharks.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace());
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

    private static final class StopSummary {
        private final String reason;
        private final long accepted;
        private final long dropped;
        private final boolean incomplete;
        private final String incompleteReason;
        private final Path outputPath;

        private StopSummary(String reason, long accepted, long dropped, boolean incomplete, String incompleteReason,
                            Path outputPath) {
            this.reason = reason;
            this.accepted = accepted;
            this.dropped = dropped;
            this.incomplete = incomplete;
            this.incompleteReason = incompleteReason;
            this.outputPath = outputPath;
        }

        private static StopSummary none() {
            return new StopSummary("none", 0L, 0L, false, "none", Path.of("unavailable:no_completed_capture"));
        }

        private static StopSummary from(Session session) {
            return new StopSummary(session.stopReason, session.accepted.get(), session.dropped.get(),
                    session.incomplete.get(), session.incompleteReason, session.outputPath);
        }
    }

    private static final class Session {
        private final UUID id;
        private final String dimension;
        private final Map<UUID, Integer> selectedTargets;
        private final Map<UUID, Integer> targets;
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
        private long lastPresentationTick = Long.MIN_VALUE;
        private volatile String stopReason = "none";
        private volatile String terminalRecord;
        private final AtomicBoolean terminalWritten = new AtomicBoolean();

        private Session(UUID id, String dimension, Map<UUID, Integer> targets, int eligibleTargets, int excludedTargets,
                        long startTick, long endTick, long wallDeadlineMillis, Path outputDirectory) {
            this.id = id;
            this.dimension = dimension;
            this.selectedTargets = Map.copyOf(targets);
            this.targets = new LinkedHashMap<>(targets);
            this.eligibleTargets = eligibleTargets;
            this.excludedTargets = excludedTargets;
            this.startTick = startTick;
            this.endTick = endTick;
            this.lastTick = startTick;
            this.wallDeadlineMillis = wallDeadlineMillis;
            this.outputDirectory = outputDirectory;
            this.outputPath = outputDirectory.resolve("client-bfs-debug-" + FILE_TIME.format(Instant.now()) + "-" + id + ".jsonl");
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

        private synchronized int targetCount() {
            return targets.size();
        }

        private int selectedTargetCount() {
            return selectedTargets.size();
        }

        private synchronized List<Map.Entry<UUID, Integer>> trackedTargets() {
            return List.copyOf(targets.entrySet());
        }

        private synchronized boolean claimPresentationTick(long tick) {
            if (lastPresentationTick != Long.MIN_VALUE
                    && tick - lastPresentationTick < PRESENTATION_SAMPLE_INTERVAL_TICKS) {
                return false;
            }
            lastPresentationTick = tick;
            return true;
        }

        private synchronized boolean releaseTarget(UUID targetId) {
            if (targets.remove(targetId) == null) {
                return false;
            }
            missingTargets.add(targetId);
            return true;
        }

        private synchronized int missingTargetCount() {
            return missingTargets.size();
        }
    }
}
