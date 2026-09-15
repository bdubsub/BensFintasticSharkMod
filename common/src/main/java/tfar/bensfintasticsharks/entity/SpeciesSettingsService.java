package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Server owned, session only species settings.
 *
 * <p>The service deliberately has no Forge or filesystem dependency. The logical server publishes
 * one immutable snapshot after every accepted transaction, while command code supplies the
 * platform configuration baseline. The snapshot is safe to hand to movement and spawn consumers
 * without exposing a mutable map or a partially applied wildcard update.</p>
 */
public final class SpeciesSettingsService {

    public enum Field {
        HORIZONTAL_SPEED("horizontal_speed", "blocks_per_second", 0.0D, 20.0D),
        VERTICAL_SPEED("vertical_speed", "blocks_per_second", 0.0D, 20.0D),
        HORIZONTAL_SPRINT("horizontal_sprint", "multiplier", 0.0D, 4.0D),
        VERTICAL_SPRINT("vertical_sprint", "multiplier", 0.0D, 4.0D),
        SPAWN_GROUP_MIN("spawn_group_min", "entities", 1.0D, 32.0D),
        SPAWN_GROUP_MAX("spawn_group_max", "entities", 1.0D, 32.0D),
        SCALE_MIN("scale_min", "multiplier", 0.25D, 2.0D),
        SCALE_MAX("scale_max", "multiplier", 0.25D, 2.0D),
        HEALTH_MULTIPLIER("health_multiplier", "multiplier", 0.1D, 10.0D),
        DAMAGE_MULTIPLIER("damage_multiplier", "multiplier", 0.0D, 10.0D),
        KNOCKBACK_RESISTANCE("knockback_resistance", "value", 0.0D, 1.0D),
        DETECTION_RADIUS("detection_radius", "blocks", 0.0D, 256.0D),
        DISENGAGE_DISTANCE("disengage_distance", "blocks", 0.0D, 512.0D),
        ACTION_TIMEOUT("action_timeout", "ticks", 1.0D, 20_000.0D),
        MEMORY_TICKS("memory_ticks", "ticks", 0.0D, 20_000.0D),
        SCAN_RADIUS("scan_radius", "blocks", 1.0D, 256.0D),
        DISTURBANCE_ENABLED("disturbance_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_REACTION("disturbance_reaction", "reaction", 0.0D, 2.0D),
        DISTURBANCE_RADIUS("disturbance_radius", "blocks", 0.0D, 256.0D),
        DISTURBANCE_SENSITIVITY("disturbance_sensitivity", "multiplier", 0.0D, 3.0D),
        DISTURBANCE_INTERVAL_TICKS("disturbance_interval_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_ALERT_TICKS("disturbance_alert_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_BOAT_MOVEMENT_THRESHOLD("disturbance_boat_movement_threshold", "blocks_per_tick", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_SWIM_SPRINT_ENABLED("disturbance_source_swim_sprint_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_SWIM_SPRINT_STRENGTH("disturbance_source_swim_sprint_strength", "normalized", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_SWIM_SPRINT_INTERVAL_TICKS("disturbance_source_swim_sprint_interval_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_SOURCE_ATTACK_ENABLED("disturbance_source_attack_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_ATTACK_STRENGTH("disturbance_source_attack_strength", "normalized", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_ATTACK_INTERVAL_TICKS("disturbance_source_attack_interval_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_SOURCE_DAMAGE_ENABLED("disturbance_source_damage_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_DAMAGE_STRENGTH("disturbance_source_damage_strength", "normalized", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_DAMAGE_INTERVAL_TICKS("disturbance_source_damage_interval_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_SOURCE_BLOCK_BREAK_ENABLED("disturbance_source_block_break_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_BLOCK_BREAK_STRENGTH("disturbance_source_block_break_strength", "normalized", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_BLOCK_BREAK_INTERVAL_TICKS("disturbance_source_block_break_interval_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_SOURCE_FALL_ENABLED("disturbance_source_fall_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_FALL_STRENGTH("disturbance_source_fall_strength", "normalized", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_FALL_INTERVAL_TICKS("disturbance_source_fall_interval_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_SOURCE_PROJECTILE_ENABLED("disturbance_source_projectile_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_PROJECTILE_STRENGTH("disturbance_source_projectile_strength", "normalized", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_PROJECTILE_INTERVAL_TICKS("disturbance_source_projectile_interval_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_SOURCE_WATER_ENTRY_ENABLED("disturbance_source_water_entry_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_WATER_ENTRY_STRENGTH("disturbance_source_water_entry_strength", "normalized", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_WATER_ENTRY_INTERVAL_TICKS("disturbance_source_water_entry_interval_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_SOURCE_WATER_JUMP_ENABLED("disturbance_source_water_jump_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_WATER_JUMP_STRENGTH("disturbance_source_water_jump_strength", "normalized", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_WATER_JUMP_INTERVAL_TICKS("disturbance_source_water_jump_interval_ticks", "ticks", 1.0D, 20_000.0D),
        DISTURBANCE_SOURCE_OCCUPIED_BOAT_ENABLED("disturbance_source_occupied_boat_enabled", "boolean", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_OCCUPIED_BOAT_STRENGTH("disturbance_source_occupied_boat_strength", "normalized", 0.0D, 1.0D),
        DISTURBANCE_SOURCE_OCCUPIED_BOAT_INTERVAL_TICKS("disturbance_source_occupied_boat_interval_ticks", "ticks", 1.0D, 20_000.0D);

        private final String id;
        private final String unit;
        private final double minimum;
        private final double maximum;

        Field(String id, String unit, double minimum, double maximum) {
            this.id = id;
            this.unit = unit;
            this.minimum = minimum;
            this.maximum = maximum;
        }

        public String id() {
            return id;
        }

        public String unit() {
            return unit;
        }

        public double minimum() {
            return minimum;
        }

        public double maximum() {
            return maximum;
        }

        public boolean accepts(double value) {
            return Double.isFinite(value) && value >= minimum && value <= maximum
                    && (!"boolean".equals(unit) && !"reaction".equals(unit) || value == Math.rint(value));
        }

        public static Field parse(String value) {
            if (value == null) return null;
            String normalized = value.trim().toLowerCase(Locale.ROOT);
            for (Field field : values()) {
                if (field.id.equals(normalized) || field.name().toLowerCase(Locale.ROOT).equals(normalized)) {
                    return field;
                }
            }
            return null;
        }
    }

    public enum Capability {
        SUPPORTED("supported"),
        READ_ONLY("read_only"),
        NOT_APPLICABLE("not_applicable");

        private final String id;

        Capability(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }
    }

    public record CapabilityResult(Capability capability, String reason) {
        public CapabilityResult {
            Objects.requireNonNull(capability, "capability");
            reason = reason == null || reason.isBlank() ? "declared by the species adapter" : reason;
        }
    }

    public record FieldValue(Field field, double value, String source, CapabilityResult capability) {
        public FieldValue {
            Objects.requireNonNull(field, "field");
            Objects.requireNonNull(source, "source");
            Objects.requireNonNull(capability, "capability");
        }
    }

    public record SpeciesSnapshot(String species, long revision, Map<Field, FieldValue> fields) {
        public SpeciesSnapshot {
            Objects.requireNonNull(species, "species");
            fields = immutableFields(fields);
        }

        private static Map<Field, FieldValue> immutableFields(Map<Field, FieldValue> values) {
            EnumMap<Field, FieldValue> copy = new EnumMap<>(Field.class);
            copy.putAll(values);
            return Collections.unmodifiableMap(copy);
        }

        public FieldValue field(Field field) {
            return fields.get(field);
        }
    }

    public record Snapshot(long revision, Map<String, SpeciesSnapshot> species) {
        public Snapshot {
            TreeMap<String, SpeciesSnapshot> copy = new TreeMap<>();
            copy.putAll(species);
            species = Collections.unmodifiableMap(copy);
        }

        public SpeciesSnapshot species(String id) {
            return species.get(id);
        }
    }

    public record MutationResult(boolean applied, long previousRevision, long revision,
                                 String reason, List<String> targets, List<Field> fields,
                                 Snapshot snapshot) {
        public MutationResult {
            targets = List.copyOf(targets);
            fields = List.copyOf(fields);
        }
    }

    public record ReloadResult(boolean applied, long previousRevision, long revision,
                               String reason, Snapshot snapshot) {
    }

    private record State(long revision, Map<String, Map<Field, Double>> baseline,
                         Map<String, Map<Field, Double>> overrides) {
    }

    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final List<String> SPECIES = createSpeciesList();
    private static volatile State state = new State(0L, createDefaultBaseline(), Map.of());

    private SpeciesSettingsService() {
    }

    public static List<String> speciesIds() {
        return SPECIES;
    }

    public static List<Field> fields() {
        return List.of(Field.values());
    }

    public static Field disturbanceField(String sourceKind, String property) {
        if (sourceKind == null || property == null) return null;
        return Field.parse("disturbance_source_" + sourceKind.trim().toLowerCase(Locale.ROOT)
                + "_" + property.trim().toLowerCase(Locale.ROOT));
    }

    public static long revision() {
        return state.revision();
    }

    public static Snapshot snapshot() {
        LOCK.readLock().lock();
        try {
            return snapshotOf(state);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public static SpeciesSnapshot resolve(String species) {
        LOCK.readLock().lock();
        try {
            if (!SPECIES.contains(species)) return null;
            return speciesSnapshot(state, species);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    /** Returns an effective field value or the supplied fallback for an unregistered entity. */
    public static double valueFor(Entity entity, Field field, double fallback) {
        if (entity == null || field == null) return fallback;
        SpeciesBehaviorProfile.Profile profile = SpeciesBehaviorProfile.forEntity(entity);
        String species = profile == null ? null : profile.id();
        if (species == null) return fallback;
        SpeciesSnapshot snapshot = resolve(species);
        FieldValue value = snapshot == null ? null : snapshot.field(field);
        return value == null ? fallback : value.value();
    }

    public static int intValue(Entity entity, Field field, int fallback) {
        return (int) Math.round(valueFor(entity, field, fallback));
    }

    /** Pursuit and escape are the only states that activate sprint multipliers. */
    public static boolean sprintActive(Entity entity) {
        if (!(entity instanceof Mob mob)) return false;
        if (MovementIntentOverrides.active(entity)) {
            return mob.getTarget() != null && mob.getTarget().isAlive();
        }
        if (mob.getTarget() != null && mob.getTarget().isAlive()) return true;
        return entity instanceof SmartWaterAnimal<?> animal
                && "escape".equals(animal.getBfsBehaviorAction());
    }

    /**
     * Calculates the requested powered vector in blocks per tick from a normalized world intent.
     * Collision, acceleration, route and external-force handling remain the caller's concern.
     */
    public static Vec3 requestedVelocity(Entity entity, Vec3 worldIntent,
                                         double fallbackHorizontalBps, double fallbackVerticalBps) {
        if (worldIntent == null || worldIntent.lengthSqr() <= 1.0e-10) return Vec3.ZERO;
        Vec3 direction = worldIntent.normalize();
        boolean sprint = sprintActive(entity);
        double horizontal = valueFor(entity, Field.HORIZONTAL_SPEED, fallbackHorizontalBps);
        double vertical = valueFor(entity, Field.VERTICAL_SPEED, fallbackVerticalBps);
        if (sprint) {
            horizontal *= valueFor(entity, Field.HORIZONTAL_SPRINT, 1.0D);
            vertical *= valueFor(entity, Field.VERTICAL_SPRINT, 1.0D);
        }
        return composeVelocity(direction, horizontal, vertical);
    }

    /** Independent IFC 003 equation, with speeds expressed in blocks per second. */
    public static Vec3 composeVelocity(Vec3 normalizedIntent, double horizontalBps,
                                       double verticalBps) {
        if (normalizedIntent == null || normalizedIntent.lengthSqr() <= 1.0e-10) return Vec3.ZERO;
        Vec3 direction = normalizedIntent.normalize();
        return new Vec3(direction.x * horizontalBps / 20.0D,
                direction.y * verticalBps / 20.0D,
                direction.z * horizontalBps / 20.0D);
    }

    public static MutationResult apply(long expectedRevision, String target,
                                       Map<Field, Double> patch) {
        Objects.requireNonNull(patch, "patch");
        LOCK.writeLock().lock();
        try {
            State current = state;
            List<String> targets = expandTargets(target);
            if (targets.isEmpty()) {
                return rejected(current, "unknown species target", targets, patch.keySet());
            }
            if (expectedRevision != current.revision()) {
                return rejected(current, "stale revision. expected " + expectedRevision
                        + ", observed " + current.revision(), targets, patch.keySet());
            }
            String validation = validatePatch(current, targets, patch);
            if (validation != null) {
                return rejected(current, validation, targets, patch.keySet());
            }
            Map<String, Map<Field, Double>> nextOverrides = deepCopy(current.overrides());
            for (String species : targets) {
                Map<Field, Double> values = nextOverrides.computeIfAbsent(species,
                        ignored -> new EnumMap<>(Field.class));
                values.putAll(patch);
            }
            State next = new State(current.revision() + 1L, current.baseline(), immutableOverrides(nextOverrides));
            state = next;
            return new MutationResult(true, current.revision(), next.revision(), "applied",
                    targets, sortedFields(patch.keySet()), snapshotOf(next));
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public static MutationResult reset(long expectedRevision, String target, Set<Field> requestedFields) {
        LOCK.writeLock().lock();
        try {
            State current = state;
            List<String> targets = expandTargets(target);
            List<Field> fields = requestedFields == null || requestedFields.isEmpty()
                    ? fields() : sortedFields(requestedFields);
            if (targets.isEmpty()) return rejected(current, "unknown species target", targets, fields);
            if (expectedRevision != current.revision()) {
                return rejected(current, "stale revision. expected " + expectedRevision
                        + ", observed " + current.revision(), targets, fields);
            }
            Map<String, Map<Field, Double>> nextOverrides = deepCopy(current.overrides());
            for (String species : targets) {
                Map<Field, Double> values = nextOverrides.get(species);
                if (values == null) continue;
                if (requestedFields == null || requestedFields.isEmpty()) {
                    nextOverrides.remove(species);
                } else {
                    values.keySet().removeAll(requestedFields);
                    if (values.isEmpty()) nextOverrides.remove(species);
                }
            }
            State next = new State(current.revision() + 1L, current.baseline(), immutableOverrides(nextOverrides));
            state = next;
            return new MutationResult(true, current.revision(), next.revision(), "reset",
                    targets, fields, snapshotOf(next));
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    /**
     * Replaces the complete validated server baseline and keeps valid session overrides. A
     * malformed or incompatible candidate leaves the entire previous state untouched.
     */
    public static ReloadResult reloadBaseline(Map<String, Map<Field, Double>> candidate) {
        LOCK.writeLock().lock();
        try {
            State current = state;
            Map<String, Map<Field, Double>> normalized = normalizeBaseline(candidate);
            if (normalized == null) {
                return new ReloadResult(false, current.revision(), current.revision(),
                        "invalid baseline. previous snapshot retained", snapshotOf(current));
            }
            if (validateEffective(normalized, current.overrides()) != null) {
                return new ReloadResult(false, current.revision(), current.revision(),
                        "baseline would invalidate a session override. previous snapshot retained",
                        snapshotOf(current));
            }
            State next = new State(current.revision() + 1L, normalized, current.overrides());
            state = next;
            return new ReloadResult(true, current.revision(), next.revision(), "baseline reloaded",
                    snapshotOf(next));
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    /** Clears all session values at a server restart. */
    public static void resetSession() {
        LOCK.writeLock().lock();
        try {
            State current = state;
            state = new State(current.revision() + 1L, current.baseline(), Map.of());
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    /** Returns an independent baseline suitable for a platform configuration bridge. */
    public static Map<String, Map<Field, Double>> defaultBaseline() {
        return deepImmutableBaseline(createDefaultBaseline());
    }

    private static MutationResult rejected(State current, String reason, List<String> targets,
                                           java.util.Collection<Field> fields) {
        return new MutationResult(false, current.revision(), current.revision(), reason,
                targets, sortedFields(fields), snapshotOf(current));
    }

    private static String validatePatch(State current, List<String> targets,
                                        Map<Field, Double> patch) {
        if (patch.isEmpty()) return "no fields were supplied";
        for (Field field : patch.keySet()) {
            if (field == null) return "unknown field";
            if (!field.accepts(patch.get(field))) {
                return field.id() + " must be between " + field.minimum() + " and " + field.maximum()
                        + " " + field.unit();
            }
        }
        for (String species : targets) {
            Map<Field, Double> values = effectiveValues(current, species);
            for (Field field : patch.keySet()) {
                CapabilityResult capability = capability(species, field);
                if (capability.capability() != Capability.SUPPORTED) {
                    return species + ": " + field.id() + " is " + capability.capability().id()
                            + " because " + capability.reason();
                }
            }
            values.putAll(patch);
            String pairError = validatePairs(values);
            if (pairError != null) return species + ": " + pairError;
        }
        return null;
    }

    private static String validateEffective(Map<String, Map<Field, Double>> baseline,
                                            Map<String, Map<Field, Double>> overrides) {
        for (String species : SPECIES) {
            Map<Field, Double> values = new EnumMap<>(baseline.get(species));
            values.putAll(overrides.getOrDefault(species, Map.of()));
            String error = validatePairs(values);
            if (error != null) return species + ": " + error;
        }
        return null;
    }

    private static String validatePairs(Map<Field, Double> values) {
        if (values.get(Field.SCALE_MIN) > values.get(Field.SCALE_MAX)) {
            return "scale_min cannot exceed scale_max";
        }
        if (values.get(Field.SPAWN_GROUP_MIN) > values.get(Field.SPAWN_GROUP_MAX)) {
            return "spawn_group_min cannot exceed spawn_group_max";
        }
        return null;
    }

    private static List<String> expandTargets(String target) {
        if (target == null || target.isBlank()) return List.of();
        if ("*".equals(target)) return SPECIES;
        return SPECIES.contains(target) ? List.of(target) : List.of();
    }

    private static List<Field> sortedFields(java.util.Collection<Field> values) {
        List<Field> fields = new ArrayList<>(values);
        fields.sort(Comparator.comparing(Field::id));
        return fields;
    }

    private static Snapshot snapshotOf(State source) {
        Map<String, SpeciesSnapshot> values = new TreeMap<>();
        for (String species : SPECIES) values.put(species, speciesSnapshot(source, species));
        return new Snapshot(source.revision(), values);
    }

    private static SpeciesSnapshot speciesSnapshot(State source, String species) {
        Map<Field, Double> values = effectiveValues(source, species);
        Map<Field, FieldValue> fields = new EnumMap<>(Field.class);
        Map<Field, Double> overrides = source.overrides().getOrDefault(species, Map.of());
        for (Field field : Field.values()) {
            CapabilityResult capability = capability(species, field);
            String origin = overrides.containsKey(field) ? "session" : "server_config";
            fields.put(field, new FieldValue(field, values.get(field), origin, capability));
        }
        return new SpeciesSnapshot(species, source.revision(), fields);
    }

    private static CapabilityResult capability(String species, Field field) {
        if (field.id().startsWith("disturbance_")
                && SpeciesBehaviorProfile.forId(species).family() != SpeciesBehaviorProfile.Family.SHARK) {
            return new CapabilityResult(Capability.NOT_APPLICABLE,
                    "disturbance reaction policy is currently shark-only");
        }
        return new CapabilityResult(Capability.SUPPORTED, "owned by the BFS species settings adapter");
    }

    private static Map<Field, Double> effectiveValues(State source, String species) {
        EnumMap<Field, Double> values = new EnumMap<>(Field.class);
        values.putAll(source.baseline().get(species));
        values.putAll(source.overrides().getOrDefault(species, Map.of()));
        return values;
    }

    private static Map<String, Map<Field, Double>> normalizeBaseline(Map<String, Map<Field, Double>> candidate) {
        if (candidate == null) return null;
        Map<String, Map<Field, Double>> normalized = new TreeMap<>();
        for (String species : SPECIES) {
            Map<Field, Double> provided = candidate.get(species);
            if (provided == null) return null;
            EnumMap<Field, Double> values = new EnumMap<>(Field.class);
            for (Field field : Field.values()) {
                Double value = provided.get(field);
                if (value == null || !field.accepts(value)) return null;
                values.put(field, value);
            }
            normalized.put(species, values);
        }
        return deepImmutableBaseline(normalized);
    }

    private static Map<String, Map<Field, Double>> createDefaultBaseline() {
        Map<String, Map<Field, Double>> baseline = new TreeMap<>();
        for (String species : SPECIES) {
            SpeciesBehaviorProfile.Profile profile = SpeciesBehaviorProfile.forId(species);
            EnumMap<Field, Double> values = new EnumMap<>(Field.class);
            double horizontal = switch (profile.family()) {
                case SHARK -> 5.0D;
                case MAMMAL -> 4.0D;
                case FISH -> 3.0D;
                case TURTLE -> 2.0D;
                case OCTOPUS, BENTHIC -> 1.8D;
                case JELLYFISH -> 0.5D;
            };
            double vertical = switch (profile.family()) {
                case SHARK -> 3.5D;
                case FISH -> 2.0D;
                case MAMMAL -> 2.5D;
                case TURTLE, OCTOPUS, BENTHIC -> 1.5D;
                case JELLYFISH -> 0.5D;
            };
            values.put(Field.HORIZONTAL_SPEED, horizontal);
            values.put(Field.VERTICAL_SPEED, vertical);
            values.put(Field.HORIZONTAL_SPRINT, 1.2D);
            values.put(Field.VERTICAL_SPRINT, 1.5D);
            values.put(Field.SPAWN_GROUP_MIN, profile.social() ? 2.0D : 1.0D);
            values.put(Field.SPAWN_GROUP_MAX, profile.social() ? 4.0D : 1.0D);
            double[] scaleRange = defaultScaleRange(species);
            values.put(Field.SCALE_MIN, scaleRange[0]);
            values.put(Field.SCALE_MAX, scaleRange[1]);
            values.put(Field.HEALTH_MULTIPLIER, 1.0D);
            values.put(Field.DAMAGE_MULTIPLIER, 1.0D);
            values.put(Field.KNOCKBACK_RESISTANCE, 0.0D);
            values.put(Field.DETECTION_RADIUS, (double) profile.scanRadius());
            values.put(Field.DISENGAGE_DISTANCE, 64.0D);
            values.put(Field.ACTION_TIMEOUT, (double) profile.actionTimeoutTicks());
            values.put(Field.MEMORY_TICKS, (double) profile.memoryTicks());
            values.put(Field.SCAN_RADIUS, (double) profile.scanRadius());
            values.put(Field.DISTURBANCE_ENABLED, 1.0D);
            values.put(Field.DISTURBANCE_REACTION, 1.0D);
            values.put(Field.DISTURBANCE_RADIUS, 24.0D);
            values.put(Field.DISTURBANCE_SENSITIVITY, 1.0D);
            values.put(Field.DISTURBANCE_INTERVAL_TICKS, 20.0D);
            values.put(Field.DISTURBANCE_ALERT_TICKS, 100.0D);
            values.put(Field.DISTURBANCE_BOAT_MOVEMENT_THRESHOLD, 0.02D);
            putDisturbanceDefaults(values);
            baseline.put(species, values);
        }
        return baseline;
    }

    private static void putDisturbanceDefaults(EnumMap<Field, Double> values) {
        putSourceDefaults(values, "swim_sprint", 1.0D, 0.5D, 5.0D);
        putSourceDefaults(values, "attack", 1.0D, 1.0D, 10.0D);
        putSourceDefaults(values, "damage", 1.0D, 1.0D, 5.0D);
        putSourceDefaults(values, "block_break", 1.0D, 0.75D, 10.0D);
        putSourceDefaults(values, "fall", 1.0D, 1.0D, 10.0D);
        putSourceDefaults(values, "projectile", 1.0D, 0.5D, 5.0D);
        putSourceDefaults(values, "water_entry", 1.0D, 0.5D, 20.0D);
        putSourceDefaults(values, "water_jump", 1.0D, 0.5D, 20.0D);
        putSourceDefaults(values, "occupied_boat", 1.0D, 0.5D, 10.0D);
    }

    private static void putSourceDefaults(EnumMap<Field, Double> values, String kind,
                                          double enabled, double strength, double interval) {
        values.put(disturbanceField(kind, "enabled"), enabled);
        values.put(disturbanceField(kind, "strength"), strength);
        values.put(disturbanceField(kind, "interval_ticks"), interval);
    }

    private static double[] defaultScaleRange(String species) {
        return switch (species) {
            case "great_white_shark" -> new double[]{0.90D, 1.10D};
            case "great_hammerhead_shark", "common_thresher_shark", "oceanic_whitetip_shark",
                    "orca" -> new double[]{0.90D, 1.05D};
            case "shortfin_mako_shark" -> new double[]{0.75D, 1.25D};
            case "tiger_shark" -> new double[]{0.85D, 1.00D};
            case "sandtiger_shark" -> new double[]{0.72D, 1.45D};
            case "blacktip_reef_shark" -> new double[]{0.90D, 1.05D};
            case "bottlenose_dolphin" -> new double[]{0.50D, 2.00D};
            case "green_sea_turtle" -> new double[]{0.85D, 1.15D};
            case "common_octopus" -> new double[]{0.45D, 0.90D};
            case "caribbean_reef_octopus" -> new double[]{0.40D, 0.85D};
            case "nautilus" -> new double[]{0.85D, 1.10D};
            case "giant_moray_eel" -> new double[]{0.55D, 1.00D};
            case "american_lobster" -> new double[]{0.50D, 1.00D};
            case "black_sea_nettle_jellyfish" -> new double[]{0.80D, 1.15D};
            case "cannonball_jellyfish" -> new double[]{0.40D, 0.65D};
            default -> new double[]{1.00D, 1.00D};
        };
    }

    private static List<String> createSpeciesList() {
        List<String> ids = new ArrayList<>();
        for (SpeciesBehaviorProfile.Profile profile : SpeciesBehaviorProfile.all()) ids.add(profile.id());
        ids.sort(String::compareTo);
        return List.copyOf(ids);
    }

    private static Map<String, Map<Field, Double>> deepCopy(Map<String, Map<Field, Double>> source) {
        Map<String, Map<Field, Double>> copy = new TreeMap<>();
        for (Map.Entry<String, Map<Field, Double>> entry : source.entrySet()) {
            copy.put(entry.getKey(), new EnumMap<>(entry.getValue()));
        }
        return copy;
    }

    private static Map<String, Map<Field, Double>> immutableOverrides(Map<String, Map<Field, Double>> source) {
        Map<String, Map<Field, Double>> copy = new TreeMap<>();
        for (Map.Entry<String, Map<Field, Double>> entry : source.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                copy.put(entry.getKey(), Collections.unmodifiableMap(new EnumMap<>(entry.getValue())));
            }
        }
        return Collections.unmodifiableMap(copy);
    }

    private static Map<String, Map<Field, Double>> deepImmutableBaseline(Map<String, Map<Field, Double>> source) {
        Map<String, Map<Field, Double>> copy = new TreeMap<>();
        for (Map.Entry<String, Map<Field, Double>> entry : source.entrySet()) {
            copy.put(entry.getKey(), Collections.unmodifiableMap(new EnumMap<>(entry.getValue())));
        }
        return Collections.unmodifiableMap(copy);
    }
}
