package tfar.bensfintasticsharks.config;

import tfar.bensfintasticsharks.entity.SpeciesBehaviorProfile;
import tfar.bensfintasticsharks.entity.SpeciesSettingsService;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** Builds the complete validated baseline consumed by the session settings service. */
public final class SpeciesSettingsConfigBridge {

    private SpeciesSettingsConfigBridge() {
    }

    public static Map<String, Map<SpeciesSettingsService.Field, Double>> read() {
        BfsConfig.Common config = BfsConfig.COMMON;
        Map<String, Map<SpeciesSettingsService.Field, Double>> baseline = new LinkedHashMap<>();
        for (String species : SpeciesSettingsService.speciesIds()) {
            EnumMap<SpeciesSettingsService.Field, Double> values = new EnumMap<>(SpeciesSettingsService.Field.class);
            values.putAll(SpeciesSettingsService.defaultBaseline().get(species));

            var groupMin = config.speciesGroupMin.get(species);
            var cap = config.speciesCaps.get(species);
            if (groupMin != null) values.put(SpeciesSettingsService.Field.SPAWN_GROUP_MIN,
                    (double) groupMin.get());
            if (cap != null) {
                int configuredCap = cap.get();
                double maximum = Math.max(values.get(SpeciesSettingsService.Field.SPAWN_GROUP_MIN),
                        Math.min(32, Math.max(1, configuredCap)));
                values.put(SpeciesSettingsService.Field.SPAWN_GROUP_MAX, maximum);
            }

            var health = config.speciesHpMult.get(species);
            var damage = config.speciesDamageMult.get(species);
            var knockback = config.speciesKnockbackResistance.get(species);
            if (health != null) {
                double value = health.get();
                if (isShark(species)) value *= config.sharkHpMult.get();
                values.put(SpeciesSettingsService.Field.HEALTH_MULTIPLIER, value);
            }
            if (damage != null) {
                double value = damage.get();
                if (isShark(species)) value *= config.sharkDamageMult.get();
                values.put(SpeciesSettingsService.Field.DAMAGE_MULTIPLIER, value);
            }
            if (knockback != null && knockback.get() >= 0) {
                values.put(SpeciesSettingsService.Field.KNOCKBACK_RESISTANCE, knockback.get());
            } else if ("orca".equals(species)) {
                values.put(SpeciesSettingsService.Field.KNOCKBACK_RESISTANCE,
                        config.orcaKnockbackResistance.get());
            }
            if (isShark(species)) {
                values.put(SpeciesSettingsService.Field.DETECTION_RADIUS,
                        values.get(SpeciesSettingsService.Field.DETECTION_RADIUS)
                                * config.sharkDetectionRadiusMult.get());
                values.put(SpeciesSettingsService.Field.DISENGAGE_DISTANCE,
                        values.get(SpeciesSettingsService.Field.DISENGAGE_DISTANCE)
                                * config.sharkDisengageDistanceMult.get());
            }
            values.put(SpeciesSettingsService.Field.DISTURBANCE_ENABLED,
                    config.disturbanceEnabled.get() ? 1.0D : 0.0D);
            values.put(SpeciesSettingsService.Field.DISTURBANCE_REACTION,
                    (double) config.disturbanceReaction.get());
            values.put(SpeciesSettingsService.Field.DISTURBANCE_RADIUS,
                    config.disturbanceRadius.get());
            values.put(SpeciesSettingsService.Field.DISTURBANCE_SENSITIVITY,
                    config.disturbanceSensitivity.get());
            values.put(SpeciesSettingsService.Field.DISTURBANCE_INTERVAL_TICKS,
                    (double) config.disturbanceIntervalTicks.get());
            values.put(SpeciesSettingsService.Field.DISTURBANCE_ALERT_TICKS,
                    (double) config.disturbanceAlertTicks.get());
            values.put(SpeciesSettingsService.Field.DISTURBANCE_BOAT_MOVEMENT_THRESHOLD,
                    config.disturbanceBoatMovementThreshold.get());
            applyLegacyDisturbanceCompatibility(values, config);
            baseline.put(species, values);
        }
        return baseline;
    }

    public static SpeciesSettingsService.ReloadResult reload() {
        return SpeciesSettingsService.reloadBaseline(read());
    }

    private static boolean isShark(String species) {
        return species.endsWith("_shark");
    }

    private static void applyLegacyDisturbanceCompatibility(
            Map<SpeciesSettingsService.Field, Double> values, BfsConfig.Common config) {
        applyLegacyMultiplier(values, "swim_sprint", config.lightSensitivityMult.get());
        applyLegacyMultiplier(values, "attack", config.heavySensitivityMult.get());
        applyLegacyMultiplier(values, "damage", config.bloodSensitivityMult.get());
        applyLegacyMultiplier(values, "block_break", config.heavySensitivityMult.get());
        applyLegacyMultiplier(values, "fall", config.heavySensitivityMult.get());
        applyLegacyMultiplier(values, "projectile", config.lightSensitivityMult.get());
    }

    private static void applyLegacyMultiplier(
            Map<SpeciesSettingsService.Field, Double> values, String kind, double multiplier) {
        SpeciesSettingsService.Field enabled = SpeciesSettingsService.disturbanceField(kind, "enabled");
        SpeciesSettingsService.Field strength = SpeciesSettingsService.disturbanceField(kind, "strength");
        values.put(enabled, multiplier > 0.0D ? 1.0D : 0.0D);
        values.put(strength, Math.max(0.0D, Math.min(1.0D, values.get(strength) * multiplier)));
    }
}
