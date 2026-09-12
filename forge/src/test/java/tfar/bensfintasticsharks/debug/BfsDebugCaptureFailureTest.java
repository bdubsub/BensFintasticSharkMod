package tfar.bensfintasticsharks.debug;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BfsDebugCaptureFailureTest {

    @Test
    void oversizedRecordStopsWithIncompleteFooter() throws Exception {
        Path directory = Files.createTempDirectory("bfs-debug-oversized-");
        try {
            BfsDebugManager.Session session = newSession(directory);
            JsonObject record = new JsonObject();
            record.addProperty("payload", "x".repeat(BfsDebugManager.MAX_RECORD_BYTES));

            enqueue().invoke(null, session, record);

            assertTrue(session.incomplete());
            assertEquals(1L, session.dropped());
            assertEquals("record_byte_limit", field(session, "stopReason"));
            waitForTerminal(session.outputPath());
            assertTrue(Files.readString(session.outputPath()).contains("\"event\":\"end\""));
        } finally {
            delete(directory);
        }
    }

    @Test
    void queueOverflowStopsWithoutAcceptingMoreRecords() throws Exception {
        Path directory = Files.createTempDirectory("bfs-debug-queue-");
        try {
            BfsDebugManager.Session session = newSession(directory);
            AtomicBoolean writerScheduled = (AtomicBoolean) fieldObject(session, "writerScheduled");
            writerScheduled.set(true);
            Method enqueue = enqueue();
            for (int index = 0; index <= BfsDebugManager.MAX_QUEUE_RECORDS; index++) {
                JsonObject record = new JsonObject();
                record.addProperty("index", index);
                enqueue.invoke(null, session, record);
            }

            assertTrue(session.incomplete());
            assertEquals(1L, session.dropped());
            assertEquals(BfsDebugManager.MAX_QUEUE_RECORDS, session.accepted());
            assertEquals("queue_limit", field(session, "stopReason"));
            ((java.util.concurrent.BlockingQueue<?>) fieldObject(session, "records")).clear();
        } finally {
            delete(directory);
        }
    }

    @Test
    void writerFailureStopsWithExplicitReason() throws Exception {
        Path parent = Files.createTempDirectory("bfs-debug-writer-");
        try {
            Path blockedDirectory = parent.resolve("occupied");
            Files.writeString(blockedDirectory, "file");
            BfsDebugManager.Session session = newSession(blockedDirectory);
            JsonObject record = new JsonObject();
            record.addProperty("event", "probe");

            enqueue().invoke(null, session, record);

            waitUntilClosed(session);
            assertTrue(session.incomplete());
            assertTrue(session.incompleteReason().startsWith("writer failure:"));
            assertEquals("writer_failure", field(session, "stopReason"));
        } finally {
            delete(parent);
        }
    }

    private static Method enqueue() throws ReflectiveOperationException {
        Method method = BfsDebugManager.class.getDeclaredMethod("enqueue", BfsDebugManager.Session.class,
                JsonObject.class);
        method.setAccessible(true);
        return method;
    }

    private static BfsDebugManager.Session newSession(Path directory) throws ReflectiveOperationException {
        net.minecraft.SharedConstants.tryDetectVersion();
        net.minecraft.server.Bootstrap.bootStrap();
        Constructor<BfsDebugManager.Session> constructor = BfsDebugManager.Session.class.getDeclaredConstructor(
                UUID.class, net.minecraft.server.MinecraftServer.class, BfsDebugManager.DebugCategory.class,
                ResourceKey.class, Set.class, int.class, int.class, long.class, long.class, long.class, Path.class);
        constructor.setAccessible(true);
        return constructor.newInstance(UUID.randomUUID(), null, BfsDebugManager.DebugCategory.MOVEMENT,
                Level.OVERWORLD, new LinkedHashSet<>(), 0, 0, 0L, 20L,
                System.currentTimeMillis() + 60_000L, directory);
    }

    private static Object field(BfsDebugManager.Session session, String name) throws ReflectiveOperationException {
        return fieldObject(session, name);
    }

    private static Object fieldObject(BfsDebugManager.Session session, String name) throws ReflectiveOperationException {
        Field field = BfsDebugManager.Session.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(session);
    }

    private static void waitForTerminal(Path output) throws Exception {
        for (int attempt = 0; attempt < 100; attempt++) {
            if (Files.exists(output) && Files.readString(output).contains("\"event\":\"end\"")) {
                return;
            }
            Thread.sleep(10L);
        }
        throw new AssertionError("debug writer did not finish terminal record");
    }

    private static void waitUntilClosed(BfsDebugManager.Session session) throws Exception {
        for (int attempt = 0; attempt < 100; attempt++) {
            AtomicBoolean closed = (AtomicBoolean) fieldObject(session, "closed");
            if (closed.get()) {
                return;
            }
            Thread.sleep(10L);
        }
        throw new AssertionError("debug writer did not close after failure");
    }

    private static void delete(Path path) throws Exception {
        try (var paths = Files.walk(path)) {
            paths.sorted(java.util.Comparator.reverseOrder()).forEach(candidate -> {
                try {
                    Files.deleteIfExists(candidate);
                } catch (Exception exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
    }
}
