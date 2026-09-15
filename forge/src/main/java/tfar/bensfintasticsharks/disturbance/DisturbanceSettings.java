package tfar.bensfintasticsharks.disturbance;

import net.minecraft.world.entity.Entity;
import tfar.bensfintasticsharks.entity.SpeciesBehaviorProfile;
import tfar.bensfintasticsharks.entity.SpeciesSettingsService;

/** Resolves one immutable disturbance policy from the shared species settings snapshot. */
public final class DisturbanceSettings {

    public enum Reaction {
        IGNORE(0),
        ALERT(1),
        INVESTIGATE(2);

        private final int value;

        Reaction(int value) {
            this.value = value;
        }

        public static Reaction from(double value) {
            int rounded = (int) Math.round(value);
            return switch (rounded) {
                case 2 -> INVESTIGATE;
                case 1 -> ALERT;
                default -> IGNORE;
            };
        }
    }

    public record Effective(String species, long revision, boolean applicable,
                            String capabilityReason, boolean enabled, boolean sourceEnabled,
                            Reaction reaction, double radius, double sensitivity, double strength,
                            int sourceIntervalTicks, int globalIntervalTicks, int alertTicks,
                            double boatMovementThreshold) {
        public double effectiveStrength(double eventStrength) {
            return Math.max(0.0D, Math.min(1.0D, eventStrength * strength * sensitivity));
        }

        public int effectiveIntervalTicks() {
            return Math.max(sourceIntervalTicks, globalIntervalTicks);
        }
    }

    public record Decision(String outcome, String reason, boolean accepted,
                           boolean thresholdAccepted, double effectiveStrength) {
    }

    public static Decision evaluate(Effective settings, double eventStrength,
                                   double distance, long tick, Long previousTick) {
        double effectiveStrength = settings.effectiveStrength(eventStrength);
        if (!settings.applicable()) return ignored("not_applicable_species", effectiveStrength);
        if (!settings.enabled()) return ignored("global_disabled", effectiveStrength);
        if (!settings.sourceEnabled()) return ignored("source_disabled", effectiveStrength);
        if (settings.reaction() == Reaction.IGNORE) return ignored("reaction_ignored", effectiveStrength);
        if (effectiveStrength < 0.25D) return ignored("low_threshold", effectiveStrength);
        if (distance > settings.radius()) return ignored("outside_radius", effectiveStrength);
        if (previousTick != null && tick - previousTick < settings.effectiveIntervalTicks()) {
            return ignored("cooldown", effectiveStrength);
        }
        return new Decision(settings.reaction() == Reaction.INVESTIGATE ? "investigate" : "alert",
                "eligible_sharks", true, true, effectiveStrength);
    }

    private static Decision ignored(String reason, double effectiveStrength) {
        return new Decision("ignored", reason, false, false, effectiveStrength);
    }

    private DisturbanceSettings() {
    }

    public static Effective resolve(Entity entity, WaterDisturbanceEvent.SourceKind sourceKind) {
        SpeciesBehaviorProfile.Profile profile = entity == null
                ? null : SpeciesBehaviorProfile.forEntity(entity);
        String species = profile == null ? null : profile.id();
        SpeciesSettingsService.SpeciesSnapshot snapshot = species == null
                ? null : SpeciesSettingsService.resolve(species);
        if (snapshot == null) {
            return new Effective(species == null ? "unregistered" : species,
                    SpeciesSettingsService.revision(), false,
                    "species is not registered in the BFS settings catalog",
                    false, false, Reaction.IGNORE, 0.0D, 0.0D, 0.0D, 1, 20, 1, 0.02D);
        }
        SpeciesSettingsService.Field enabledField = SpeciesSettingsService.Field.DISTURBANCE_ENABLED;
        SpeciesSettingsService.CapabilityResult capability = snapshot.field(enabledField).capability();
        boolean applicable = capability.capability() == SpeciesSettingsService.Capability.SUPPORTED;
        return new Effective(species, snapshot.revision(), applicable, capability.reason(),
                value(snapshot, enabledField, 1.0D) >= 0.5D,
                value(snapshot, SpeciesSettingsService.disturbanceField(sourceKind.id(), "enabled"), 1.0D) >= 0.5D,
                Reaction.from(value(snapshot, SpeciesSettingsService.Field.DISTURBANCE_REACTION, 1.0D)),
                value(snapshot, SpeciesSettingsService.Field.DISTURBANCE_RADIUS, 24.0D),
                value(snapshot, SpeciesSettingsService.Field.DISTURBANCE_SENSITIVITY, 1.0D),
                value(snapshot, SpeciesSettingsService.disturbanceField(sourceKind.id(), "strength"), 1.0D),
                (int) Math.round(value(snapshot,
                        SpeciesSettingsService.disturbanceField(sourceKind.id(), "interval_ticks"), 20.0D)),
                (int) Math.round(value(snapshot, SpeciesSettingsService.Field.DISTURBANCE_INTERVAL_TICKS, 20.0D)),
                (int) Math.round(value(snapshot, SpeciesSettingsService.Field.DISTURBANCE_ALERT_TICKS, 100.0D)),
                value(snapshot, SpeciesSettingsService.Field.DISTURBANCE_BOAT_MOVEMENT_THRESHOLD, 0.02D));
    }

    private static double value(SpeciesSettingsService.SpeciesSnapshot snapshot,
                                SpeciesSettingsService.Field field, double fallback) {
        SpeciesSettingsService.FieldValue value = field == null ? null : snapshot.field(field);
        return value == null ? fallback : value.value();
    }
}
