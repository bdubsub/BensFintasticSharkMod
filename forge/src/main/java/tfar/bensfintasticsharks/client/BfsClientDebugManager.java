package tfar.bensfintasticsharks.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    private static final long WALL_DURATION_MILLIS = 90_000L;
    private static final int MAX_QUEUE_RECORDS = 8_192;
    private static final int MAX_RECORD_BYTES = 16 * 1024;
    private static final long MAX_SESSION_BYTES = 32L * 1024L * 1024L;
    private static final long MAX_DIRECTORY_BYTES = 256L * 1024L * 1024L;
    private static final String SCHEMA = "bfs-debug-v1";
    private static final Gson GSON = new Gson();
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("uuuuMMdd-HHmmss")
            .withZone(ZoneOffset.UTC);
    private static final ExecutorService WRITER = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "bfs-client-debug-writer");
        thread.setDaemon(true);
        return thread;
    });

    private static volatile Session session;

    private BfsClientDebugManager() {
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(BfsClientDebugManager::onRegisterCommands);
        eventBus.addListener(BfsClientDebugManager::onClientTick);
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
        AABB search = player.getBoundingBox().inflate(128.0D);
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, search, BfsClientDebugManager::isBfsEntity)) {
            eligible++;
            if (targets.size() < MAX_TARGETS) {
                targets.put(candidate.getUUID(), candidate.getId());
            } else {
                excluded++;
            }
        }
        long startTick = level.getGameTime();
        Session created = new Session(UUID.randomUUID(), level.dimension().location().toString(), targets, eligible, excluded,
                startTick, startTick + DEFAULT_DURATION_TICKS, System.currentTimeMillis() + WALL_DURATION_MILLIS, outputDirectory());
        session = created;
        enqueue(created, header(created));
        context.getSource().sendSuccess(() -> Component.literal("Local BFS debug capture started").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        context.getSource().sendSuccess(() -> Component.literal("  Side: client. Targets: " + created.targets.size() + " selected, "
                + created.excludedTargets + " excluded.")
                .withStyle(ChatFormatting.WHITE), false);
        context.getSource().sendSuccess(() -> Component.literal("  Duration: " + DEFAULT_DURATION_TICKS + " client ticks or 90 seconds. Output: "
                + created.outputPath).withStyle(ChatFormatting.GRAY), false);
        return targets.size();
    }

    private static int stop(CommandContext<CommandSourceStack> context) {
        Session active = session;
        if (active == null) {
            context.getSource().sendFailure(Component.literal("No local BFS debug capture is active."));
            return 0;
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
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.literal("Local BFS debug capture").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        context.getSource().sendSuccess(() -> Component.literal("  Session: " + active.id + ". Targets: " + active.targets.size()
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
        if (tick >= active.endTick) {
            finish(active, "duration_elapsed");
            return;
        }
        if (System.currentTimeMillis() >= active.wallDeadlineMillis) {
            active.markIncomplete("90 second client wall deadline elapsed");
            finish(active, "wall_deadline_elapsed");
            return;
        }
        for (Map.Entry<UUID, Integer> target : active.targets.entrySet()) {
            Entity entity = level.getEntity(target.getValue());
            if (entity == null || !entity.getUUID().equals(target.getKey())) {
                active.missingTargets.add(target.getKey());
                continue;
            }
            enqueue(active, movementRecord(active, entity, tick));
        }
    }

    private static void finish(Session active, String reason) {
        if (session == active) {
            session = null;
        }
        if (active.closed.compareAndSet(false, true)) {
            enqueue(active, endRecord(active, reason));
        }
    }

    private static JsonObject header(Session active) {
        JsonObject record = baseRecord(active, "header", active.startTick);
        record.addProperty("schema", SCHEMA);
        record.addProperty("side", "client");
        record.addProperty("requestedTicks", DEFAULT_DURATION_TICKS);
        record.addProperty("wallDurationSeconds", WALL_DURATION_MILLIS / 1_000L);
        record.addProperty("eligibleTargets", active.eligibleTargets);
        record.addProperty("selectedTargets", active.targets.size());
        record.addProperty("excludedTargets", active.excludedTargets);
        record.addProperty("targetLimit", MAX_TARGETS);
        record.addProperty("artifactSha256", "unavailable:not_bound_to_a_packaged_artifact");
        record.addProperty("configuration", "unavailable:runtime_configuration_snapshot_not_yet_bound");
        return record;
    }

    private static JsonObject movementRecord(Session active, Entity entity, long tick) {
        JsonObject record = baseRecord(active, "movement", tick);
        record.addProperty("entityUuid", entity.getUUID().toString());
        record.addProperty("entityType", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
        record.addProperty("x", entity.getX());
        record.addProperty("y", entity.getY());
        record.addProperty("z", entity.getZ());
        record.addProperty("velocityX", entity.getDeltaMovement().x);
        record.addProperty("velocityY", entity.getDeltaMovement().y);
        record.addProperty("velocityZ", entity.getDeltaMovement().z);
        record.addProperty("yaw", entity.getYRot());
        record.addProperty("pitch", entity.getXRot());
        record.addProperty("renderPosition", "unavailable:client_tick_snapshot_has_no_partial_render_frame");
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

    private static JsonObject baseRecord(Session active, String event, long tick) {
        JsonObject record = new JsonObject();
        record.addProperty("schema", SCHEMA);
        record.addProperty("sessionId", active.id.toString());
        record.addProperty("side", "client");
        record.addProperty("event", event);
        record.addProperty("tick", tick);
        record.addProperty("dimension", active.dimension);
        record.addProperty("timestamp", Instant.now().toString());
        return record;
    }

    private static void enqueue(Session active, JsonObject record) {
        String line = GSON.toJson(record);
        if (line.getBytes(StandardCharsets.UTF_8).length > MAX_RECORD_BYTES || !active.records.offer(line)) {
            active.drop(line.getBytes(StandardCharsets.UTF_8).length > MAX_RECORD_BYTES
                    ? "record exceeded the maximum size" : "writer queue reached its capacity");
            return;
        }
        active.accepted.incrementAndGet();
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
            BensFintasticSharks.LOG.error("BFS client debug capture writer failed for {}", active.id, exception);
        } finally {
            active.writerScheduled.set(false);
            if (!active.records.isEmpty() && active.writerScheduled.compareAndSet(false, true)) {
                WRITER.execute(() -> drain(active));
            }
        }
    }

    private static void prepareOutputDirectory(Path directory) throws IOException {
        Files.createDirectories(directory);
        List<Path> files;
        try (var paths = Files.list(directory)) {
            files = paths.filter(path -> path.getFileName().toString().startsWith("client-bfs-debug-")
                            && path.getFileName().toString().endsWith(".jsonl"))
                    .sorted(Comparator.comparingLong(BfsClientDebugManager::lastModified))
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
        Path directory = gameDirectory.resolve("logs").resolve("bfs-debug").resolve("client").normalize();
        if (!directory.startsWith(gameDirectory)) {
            throw new IllegalStateException("BFS client debug directory escaped the game directory");
        }
        return directory;
    }

    private static boolean isBfsEntity(Entity entity) {
        return BensFintasticSharks.MOD_ID.equals(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace());
    }

    private static final class Session {
        private final UUID id;
        private final String dimension;
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
        private final Set<UUID> missingTargets = new LinkedHashSet<>();
        private volatile String incompleteReason = "none";
        private volatile long writtenBytes;

        private Session(UUID id, String dimension, Map<UUID, Integer> targets, int eligibleTargets, int excludedTargets,
                        long startTick, long endTick, long wallDeadlineMillis, Path outputDirectory) {
            this.id = id;
            this.dimension = dimension;
            this.targets = Map.copyOf(targets);
            this.eligibleTargets = eligibleTargets;
            this.excludedTargets = excludedTargets;
            this.startTick = startTick;
            this.endTick = endTick;
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
    }
}
