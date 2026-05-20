package tfar.bensfintasticsharks.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Server-side config. Loaded once at server start (or singleplayer world load).
 *
 * <h3>How mob caps work</h3>
 *
 * Vanilla Minecraft uses one cap per {@code MobCategory} (e.g. {@code WATER_CREATURE = 5}).
 * That cap is checked against the count of mobs of that category inside the player's
 * loaded chunks (the spawn area, ~128 blocks around them). When a new mob tries to
 * spawn, if the category count is at the cap, the spawn is skipped.
 *
 * <p>BFS uses a similar but <strong>per-species</strong> system on top of that:
 * <ul>
 *     <li>Each individual species (Great White, Lobster, Cannonball Jellyfish, …) has its
 *         own integer cap.</li>
 *     <li>When a mob of that species tries to spawn naturally, we count how many of that
 *         species already exist within a <strong>64-block radius</strong> (about 4 chunks)
 *         of the spawn point. If the count is at or above the cap, the spawn is cancelled.</li>
 *     <li>The vanilla {@code MobCategory} caps (apex_predator_cap, bfs_water_creature_cap,
 *         bfs_water_ambient_cap) still apply on top — they're the overall ceiling for the
 *         whole category. Per-species caps just let you tune individual animals.</li>
 *     <li>Per-species caps don't affect command spawning ({@code /summon}) or spawn eggs —
 *         only natural / chunk-generation spawns.</li>
 * </ul>
 *
 * <p>You can edit caps in this config file, or live with the {@code /bfs cap} command.
 * Command changes are runtime-only and don't persist across restarts unless you also
 * edit the config.
 */
public class BfsConfig {

    /**
     * Canonical list of BFS species, used to populate per-species config maps.
     * <strong>Must be declared before {@link #SPEC} / {@link #COMMON}</strong> — the
     * static block below triggers {@link Common}'s constructor which iterates this
     * array. Java initializes static fields in textual order, so swapping these
     * lines leaves {@code ALL_SPECIES} null when the constructor runs.
     */
    public static final String[] ALL_SPECIES = new String[] {
            "great_white_shark", "great_hammerhead_shark", "common_thresher_shark",
            "shortfin_mako_shark", "tiger_shark", "oceanic_whitetip_shark",
            "sandtiger_shark", "blacktip_reef_shark", "orca", "bottlenose_dolphin",
            "common_octopus", "caribbean_reef_octopus", "nautilus", "giant_moray_eel",
            "green_sea_turtle", "american_lobster", "common_stingray", "harbor_seal",
            "black_sea_nettle_jellyfish", "cannonball_jellyfish"
    };

    public static final ForgeConfigSpec SPEC;
    public static final Common COMMON;

    static {
        Pair<Common, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = pair.getLeft();
        SPEC = pair.getRight();
    }

    public static void register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SPEC);
    }

    public static class Common {
        // Category-level caps (vanilla-style; max mobs of the whole category per player).
        public final ForgeConfigSpec.IntValue apexPredatorCap;
        public final ForgeConfigSpec.IntValue bfsWaterCreatureCap;
        public final ForgeConfigSpec.IntValue bfsWaterAmbientCap;

        // Per-species caps. Keyed by species path (lowercase, matches the entity registry
        // id minus the namespace). Loaded into a map so MobCapManager can look up by EntityType.
        public final Map<String, ForgeConfigSpec.IntValue> speciesCaps = new LinkedHashMap<>();

        // Per-species natural-spawn probability. 1.0 = vanilla biome-modifier weight,
        // 0.0 = never spawn naturally, 2.0 = roughly twice as often (capped by category cap).
        public final Map<String, ForgeConfigSpec.DoubleValue> speciesSpawnChance = new LinkedHashMap<>();

        // Per-species HP / damage / knockback resistance multipliers (1.0 = vanilla).
        public final Map<String, ForgeConfigSpec.DoubleValue> speciesHpMult = new LinkedHashMap<>();
        public final Map<String, ForgeConfigSpec.DoubleValue> speciesDamageMult = new LinkedHashMap<>();
        public final Map<String, ForgeConfigSpec.DoubleValue> speciesKnockbackResistance = new LinkedHashMap<>();

        // Per-species minimum group size on natural spawn — when the first mob of a group
        // finalizes, we spawn extras nearby to reach this number.
        public final Map<String, ForgeConfigSpec.IntValue> speciesGroupMin = new LinkedHashMap<>();

        public final ForgeConfigSpec.DoubleValue lightSensitivityMult;
        public final ForgeConfigSpec.DoubleValue heavySensitivityMult;
        public final ForgeConfigSpec.DoubleValue bloodSensitivityMult;
        public final ForgeConfigSpec.BooleanValue disturbanceParticlesEnabled;
        public final ForgeConfigSpec.BooleanValue disturbanceAudioEnabled;

        public final ForgeConfigSpec.BooleanValue conservationDebuffEnabled;
        public final ForgeConfigSpec.BooleanValue jellyfishGlowParticles;
        public final ForgeConfigSpec.BooleanValue octopusInkParticles;

        public final ForgeConfigSpec.DoubleValue sharkDetectionRadiusMult;
        public final ForgeConfigSpec.DoubleValue sharkDisengageDistanceMult;

        public final ForgeConfigSpec.DoubleValue sharkHpMult;
        public final ForgeConfigSpec.DoubleValue sharkDamageMult;
        public final ForgeConfigSpec.DoubleValue jellyfishDamageMult;
        public final ForgeConfigSpec.DoubleValue orcaKnockbackResistance;

        public final ForgeConfigSpec.IntValue captainBenHatChestWeight;

        Common(ForgeConfigSpec.Builder b) {
            b.comment("Spawn caps. See class-level Javadoc for an explanation of how caps work.")
                    .push("spawning");
            apexPredatorCap = b.comment("BFS apex predator category cap (per player, per loaded area). " +
                            "Vanilla water_creature is 5 — this is independent.")
                    .defineInRange("apex_predator_cap", 3, 1, 20);
            bfsWaterCreatureCap = b.comment("BFS water creature category cap (per player, per loaded area).")
                    .defineInRange("bfs_water_creature_cap", 10, 1, 50);
            bfsWaterAmbientCap = b.comment("BFS water ambient category cap, used by jellyfish.")
                    .defineInRange("bfs_water_ambient_cap", 15, 1, 100);
            b.pop();

            b.comment(
                    "Per-species caps. Each cap is the max number of mobs of that species allowed",
                    "within a 64-block radius of any natural spawn check (about 4 chunks). The radius",
                    "is intentionally smaller than vanilla's loaded-chunk window so blooms and packs",
                    "stay tight to where the player is. Caps set to 0 disable natural spawning of that",
                    "species entirely. Caps don't restrict /summon, spawn eggs, or convert-style spawns."
            ).push("caps");
            // Sharks
            speciesCaps.put("great_white_shark",          b.defineInRange("great_white_shark",          2, 0, 64));
            speciesCaps.put("great_hammerhead_shark",     b.defineInRange("great_hammerhead_shark",     2, 0, 64));
            speciesCaps.put("common_thresher_shark",      b.defineInRange("common_thresher_shark",      2, 0, 64));
            speciesCaps.put("shortfin_mako_shark",        b.defineInRange("shortfin_mako_shark",        2, 0, 64));
            speciesCaps.put("tiger_shark",                b.defineInRange("tiger_shark",                2, 0, 64));
            speciesCaps.put("oceanic_whitetip_shark",     b.defineInRange("oceanic_whitetip_shark",     2, 0, 64));
            speciesCaps.put("sandtiger_shark",            b.defineInRange("sandtiger_shark",            2, 0, 64));
            speciesCaps.put("blacktip_reef_shark",        b.comment("Pack shark; higher cap so groups stay together.").defineInRange("blacktip_reef_shark", 5, 0, 64));
            // Marine mammals
            speciesCaps.put("orca",                       b.comment("Apex of apex — rare by design.").defineInRange("orca", 1, 0, 64));
            speciesCaps.put("bottlenose_dolphin",         b.defineInRange("bottlenose_dolphin",         4, 0, 64));
            // Cephalopods
            speciesCaps.put("common_octopus",             b.defineInRange("common_octopus",             3, 0, 64));
            speciesCaps.put("caribbean_reef_octopus",     b.defineInRange("caribbean_reef_octopus",     2, 0, 64));
            speciesCaps.put("nautilus",                   b.comment("Living fossil — rare deep-water find.").defineInRange("nautilus", 1, 0, 64));
            // Other fauna
            speciesCaps.put("giant_moray_eel",            b.comment("Reef ambusher — bumped so reefs feel inhabited.").defineInRange("giant_moray_eel", 4, 0, 64));
            speciesCaps.put("green_sea_turtle",           b.defineInRange("green_sea_turtle",           3, 0, 64));
            speciesCaps.put("american_lobster",           b.comment("Food source — high cap so they're easy to find.").defineInRange("american_lobster", 10, 0, 64));
            speciesCaps.put("common_stingray",            b.defineInRange("common_stingray",            3, 0, 64));
            speciesCaps.put("harbor_seal",                b.comment("Pack mammal — higher cap so groups stay together.").defineInRange("harbor_seal", 5, 0, 64));
            // Jellyfish — kept rare. Players asked for fewer; default 1-2 per area.
            speciesCaps.put("black_sea_nettle_jellyfish", b.comment("Deep-water jellyfish — rare cameo, not a swarm.").defineInRange("black_sea_nettle_jellyfish", 1, 0, 64));
            speciesCaps.put("cannonball_jellyfish",       b.comment("Warm-water jellyfish — rare cameo, not a swarm.").defineInRange("cannonball_jellyfish", 2, 0, 64));
            b.pop();

            b.comment(
                    "Per-species natural spawn probability. 1.0 = vanilla biome-modifier weight,",
                    "0.5 = roughly half as often, 2.0 = roughly twice as often, 0.0 = never spawn.",
                    "Applied as a probabilistic gate at the spawn position check — the biome modifier",
                    "JSON still controls *where* a species can spawn at all."
            ).push("spawn_chance");
            for (String s : ALL_SPECIES) {
                speciesSpawnChance.put(s, b.defineInRange(s, 1.0, 0.0, 5.0));
            }
            b.pop();

            b.comment(
                    "Per-species HP multiplier. 1.0 = the species' default health, 2.0 = double, 0.5 = half.",
                    "Applied when an entity joins the world."
            ).push("hp_mult");
            for (String s : ALL_SPECIES) {
                speciesHpMult.put(s, b.defineInRange(s, 1.0, 0.1, 10.0));
            }
            b.pop();

            b.comment(
                    "Per-species ATTACK_DAMAGE multiplier. Applied at entity join. Mobs without an",
                    "ATTACK_DAMAGE attribute (e.g. jellyfish — they apply tick-based contact damage)",
                    "ignore this value."
            ).push("damage_mult");
            for (String s : ALL_SPECIES) {
                speciesDamageMult.put(s, b.defineInRange(s, 1.0, 0.0, 10.0));
            }
            b.pop();

            b.comment(
                    "Per-species KNOCKBACK_RESISTANCE absolute value (0.0 = vanilla pushable,",
                    "1.0 = immune). -1 means leave the default attribute untouched."
            ).push("knockback_resistance");
            for (String s : ALL_SPECIES) {
                // -1 sentinel = no override.
                speciesKnockbackResistance.put(s, b.defineInRange(s, -1.0, -1.0, 1.0));
            }
            b.pop();

            b.comment(
                    "Per-species minimum group size on natural spawn. When the first mob of a",
                    "group finalizes, we spawn extras nearby until reaching this number. Set to 1",
                    "to keep biome-modifier-only groups. Useful for tightening reef shark packs",
                    "or jellyfish blooms."
            ).push("group_size_min");
            speciesGroupMin.put("blacktip_reef_shark", b.defineInRange("blacktip_reef_shark", 3, 1, 16));
            speciesGroupMin.put("harbor_seal",         b.defineInRange("harbor_seal",         3, 1, 16));
            speciesGroupMin.put("bottlenose_dolphin",  b.defineInRange("bottlenose_dolphin",  2, 1, 16));
            speciesGroupMin.put("cannonball_jellyfish",b.defineInRange("cannonball_jellyfish",2, 1, 16));
            speciesGroupMin.put("black_sea_nettle_jellyfish", b.defineInRange("black_sea_nettle_jellyfish", 1, 1, 16));
            speciesGroupMin.put("green_sea_turtle",    b.defineInRange("green_sea_turtle",    1, 1, 16));
            speciesGroupMin.put("american_lobster",    b.defineInRange("american_lobster",    1, 1, 16));
            b.pop();

            b.push("disturbance");
            lightSensitivityMult = b.defineInRange("light_sensitivity_mult", 1.0, 0.0, 3.0);
            heavySensitivityMult = b.defineInRange("heavy_sensitivity_mult", 1.0, 0.0, 3.0);
            bloodSensitivityMult = b.defineInRange("blood_sensitivity_mult", 1.0, 0.0, 3.0);
            disturbanceParticlesEnabled = b.define("particles_enabled", true);
            disturbanceAudioEnabled = b.define("audio_enabled", true);
            b.pop();

            b.push("visuals");
            conservationDebuffEnabled = b.comment("Show the Respect the Ocean effect when killing a protected sea creature.")
                    .define("conservation_debuff_enabled", true);
            jellyfishGlowParticles = b.define("jellyfish_glow_particles", true);
            octopusInkParticles = b.define("octopus_ink_particles", true);
            b.pop();

            b.push("ai");
            sharkDetectionRadiusMult = b.defineInRange("shark_detection_radius_mult", 1.0, 0.1, 3.0);
            sharkDisengageDistanceMult = b.defineInRange("shark_disengage_distance_mult", 1.0, 0.1, 3.0);
            b.pop();

            b.comment("Combat scaling. 1.0 = vanilla spec values; >1 = harder, <1 = easier. Applied at entity construction.")
                    .push("combat");
            sharkHpMult = b.defineInRange("shark_hp_mult", 1.0, 0.1, 5.0);
            sharkDamageMult = b.defineInRange("shark_damage_mult", 1.0, 0.1, 5.0);
            jellyfishDamageMult = b.defineInRange("jellyfish_damage_mult", 1.0, 0.0, 5.0);
            orcaKnockbackResistance = b.defineInRange("orca_knockback_resistance", 0.4, 0.0, 1.0);
            b.pop();

            b.push("structures");
            captainBenHatChestWeight = b.comment("Out of 100. Higher = more common in buried treasure / shipwrecks.")
                    .defineInRange("captain_ben_hat_chest_weight", 5, 0, 100);
            b.pop();
        }
    }
}
