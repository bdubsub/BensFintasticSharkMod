package tfar.bensfintasticsharks.debug;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DebugStopSummaryTest {

    private static final String SERVER_SUMMARY = "tfar.bensfintasticsharks.debug.BfsDebugManager$StopSummary";
    private static final String CLIENT_SUMMARY = "tfar.bensfintasticsharks.client.BfsClientDebugManager$StopSummary";

    @Test
    void serverWithoutCaptureHasNoFilesystemPath() throws ReflectiveOperationException {
        assertNoCapture(SERVER_SUMMARY);
    }

    @Test
    void clientWithoutCaptureHasNoFilesystemPath() throws ReflectiveOperationException {
        assertNoCapture(CLIENT_SUMMARY);
    }

    @Test
    void serverCompletedCaptureRetainsOutputPath() throws ReflectiveOperationException {
        assertCompletedCapture(SERVER_SUMMARY);
    }

    @Test
    void clientCompletedCaptureRetainsOutputPath() throws ReflectiveOperationException {
        assertCompletedCapture(CLIENT_SUMMARY);
    }

    private static void assertNoCapture(String className) throws ReflectiveOperationException {
        Class<?> summaryType = Class.forName(className);
        Method none = summaryType.getDeclaredMethod("none");
        none.setAccessible(true);
        Object summary = none.invoke(null);

        assertEquals("none", field(summary, "reason"));
        assertEquals(0L, field(summary, "accepted"));
        assertEquals(0L, field(summary, "dropped"));
        assertEquals(false, field(summary, "incomplete"));
        assertEquals("none", field(summary, "incompleteReason"));
        assertNull(field(summary, "outputPath"), "No capture means no path, not a platform dependent placeholder.");
    }

    private static void assertCompletedCapture(String className) throws ReflectiveOperationException {
        Class<?> summaryType = Class.forName(className);
        Constructor<?> constructor = summaryType.getDeclaredConstructor(String.class, long.class, long.class,
                boolean.class, String.class, Path.class);
        constructor.setAccessible(true);
        Path outputPath = Path.of("logs", "bfs-debug", "capture.jsonl");
        Object summary = constructor.newInstance("manual", 12L, 0L, false, "none", outputPath);

        assertEquals("manual", field(summary, "reason"));
        assertEquals(12L, field(summary, "accepted"));
        assertEquals(outputPath, field(summary, "outputPath"));
    }

    private static Object field(Object summary, String name) throws ReflectiveOperationException {
        Field field = summary.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(summary);
    }
}
