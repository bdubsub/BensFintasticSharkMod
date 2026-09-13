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
            }
            if (isShark(species)) {
                values.put(SpeciesSettingsService.Field.DETECTION_RADIUS,
                        values.get(SpeciesSettingsService.Field.DETECTION_RADIUS)
                                * config.sharkDetectionRadiusMult.get());
                values.put(SpeciesSettingsService.Field.DISENGAGE_DISTANCE,
                        values.get(SpeciesSettingsService.Field.DISENGAGE_DISTANCE)
                                * config.sharkDisengageDistanceMult.get());
            }
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
}
