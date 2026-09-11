package bfs.verification;

import java.io.BufferedWriter;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Fixed size tick timing and periodic census recorder for a disposable server. */
public final class TickProbe {
    private static final int LIMIT = Integer.getInteger("bfs.probe.ticks", 36000);
    private static final int WARMUP = Integer.getInteger("bfs.probe.warmup", 2400);
    private static final Map<String, Long> COUNTERS = new TreeMap<>();
    private static final Map<Class<?>, Map<String, Method>> METHODS = new HashMap<>();
    private static Map<Object, double[]> positions = new HashMap<>();
    private static long started;
    private static long wallStarted;
    private static int observed;
    private static int armedAt = -1;
    private static int measured;
    private static boolean failed;
    private static BufferedWriter ticks;
    private static BufferedWriter census;

    public static void beforeTick() {
        started = System.nanoTime();
    }

    public static void count(String name) {
        if (armedAt >= 0 && observed - armedAt > WARMUP && measured < LIMIT) {
            COUNTERS.merge(name, 1L, Long::sum);
        }
    }

    public static void action(String action) {
        count("action_" + action);
    }

    public static void death(Object victim, Object source) {
        if (armedAt < 0 || observed - armedAt <= WARMUP || measured >= LIMIT) return;
        try {
            String cause = String.valueOf(call(source, "m_19385_"));
            Object attacker = call(source, "m_7639_");
            String attackerClass = attacker == null ? "none" : attacker.getClass().getSimpleName();
            count("death_" + victim.getClass().getSimpleName() + "_" + cause + "_by_" + attackerClass);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Death cause telemetry failed", exception);
        }
    }

    public static void afterTick(Object server) {
        long duration = System.nanoTime() - started;
        if (failed || measured >= LIMIT) return;
        observed++;
        try {
            if (armedAt < 0) {
                if (observed % 20 == 0 && Files.exists(Path.of("probe.start"))) {
                    armedAt = observed;
                    census = Files.newBufferedWriter(Path.of("probe-census.tsv"));
                    census.write("tick\theap_used_bytes\tspecies_counts\tminimum_air\tmaximum_brain_memories\tcounters\tmoving_entities\tposition_samples\n");
                    sample(server, -WARMUP);
                    System.out.println("PERF_WARMUP " + WARMUP);
                }
                return;
            }
            int elapsed = observed - armedAt;
            if (elapsed <= WARMUP) {
                if (elapsed % 200 == 0) sample(server, elapsed - WARMUP);
                return;
            }
            if (ticks == null) {
                ticks = Files.newBufferedWriter(Path.of("probe-ticks.csv"));
                ticks.write("tick,duration_ns\n");
                wallStarted = System.nanoTime();
                COUNTERS.clear();
                sample(server, 0);
                System.out.println("PERF_MEASUREMENT_STARTED " + LIMIT);
            }
            measured++;
            ticks.write(measured + "," + duration + "\n");
            if (measured % 200 == 0 || measured == LIMIT) {
                sample(server, measured);
                ticks.flush();
            }
            if (measured == LIMIT) {
                ticks.close();
                census.close();
                Files.writeString(Path.of("probe.complete"), "ticks=" + measured + "\nwall_ns="
                        + (System.nanoTime() - wallStarted) + "\ncounters=" + COUNTERS + "\n");
                System.out.println("PERF_COMPLETE " + LIMIT);
            }
        } catch (Throwable exception) {
            failed = true;
            exception.printStackTrace();
            System.out.println("PERF_FAILED " + exception.getClass().getName());
        }
    }

    private static Object call(Object target, String method) throws ReflectiveOperationException {
        Map<String, Method> methods = METHODS.computeIfAbsent(target.getClass(), ignored -> new HashMap<>());
        Method cached = methods.get(method);
        if (cached == null) {
            cached = target.getClass().getMethod(method);
            methods.put(method, cached);
        }
        return cached.invoke(target);
    }

    private static void sample(Object server, int tick) throws Exception {
        Map<String, Integer> counts = new TreeMap<>();
        Map<String, Integer> air = new TreeMap<>();
        int memoryMaximum = 0;
        int moving = 0;
        Map<Object, double[]> currentPositions = new HashMap<>();
        for (Object level : (Iterable<?>) call(server, "m_129785_")) {
            for (Object entity : (Iterable<?>) call(level, "m_8583_")) {
                String name = entity.getClass().getName();
                if (!name.startsWith("tfar.bensfintasticsharks.entity.")) continue;
                String species = name.substring(name.lastIndexOf('.') + 1);
                counts.merge(species, 1, Integer::sum);
                Object id = call(entity, "m_20148_");
                double[] position = {(Double) call(entity, "m_20185_"), (Double) call(entity, "m_20186_"),
                        (Double) call(entity, "m_20189_")};
                double[] previous = positions.get(id);
                if (previous != null && Math.abs(previous[0] - position[0])
                        + Math.abs(previous[1] - position[1]) + Math.abs(previous[2] - position[2]) > 0.01) moving++;
                currentPositions.put(id, position);
                air.merge(species, (Integer) call(entity, "m_20146_"), Math::min);
                Object brain = call(entity, "m_6274_");
                Map<?, ?> memories = (Map<?, ?>) call(brain, "m_147339_");
                memoryMaximum = Math.max(memoryMaximum, memories.size());
            }
        }
        positions = currentPositions;
        long heap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
        census.write(tick + "\t" + heap + "\t" + counts + "\t" + air + "\t" + memoryMaximum
                + "\t" + COUNTERS + "\t" + moving + "\t" + positions.size() + "\n");
        census.flush();
        System.out.println("PERF_SAMPLE tick=" + tick + " entities="
                + counts.values().stream().mapToInt(Integer::intValue).sum() + " heap=" + heap);
    }
}
