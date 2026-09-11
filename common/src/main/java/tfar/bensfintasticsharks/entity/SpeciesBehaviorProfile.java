package tfar.bensfintasticsharks.entity;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Server-owned behavior contract for every supported living species.
 *
 * <p>The values describe bounded engineering choices for the game simulation. They are not
 * claims about universal biological measurements. Species with a dedicated combat state machine
 * keep that state machine as their action owner; this profile supplies the shared sensor and
 * priority metadata consumed by non-combat aquatic animals.</p>
 */
public final class SpeciesBehaviorProfile {

    public enum Family {
        SHARK, FISH, MAMMAL, TURTLE, OCTOPUS, BENTHIC, JELLYFISH
    }

    public enum Locomotion {
        PELAGIC_FORWARD,
        PELAGIC_BURST,
        POD_TRAVEL,
        HAULOUT_SWIM,
        BOTTOM_GRAZE,
        BENTHIC_CRAWL,
        BENTHIC_SHELTER,
        JET_DRIFT,
        BOTTOM_WALK,
        BOTTOM_GLIDE,
        PULSE_DRIFT
    }

    public enum FoodMode {
        PASSIVE,
        SMALL_FISH,
        FISH_AND_INVERTEBRATE,
        ALGAE,
        BENTHIC_INVERTEBRATE,
        PLANKTON
    }

    public enum ThreatResponse {
        HUNT,
        FLEE,
        INK_ESCAPE,
        DEFENSIVE_STING,
        NONE
    }

    public enum Habitat {
        OPEN_WATER,
        SURFACE_ACCESS,
        SEAFLOOR,
        SHELTER,
        CURRENT_DRIFT
    }

    public record Profile(String id, Family family, Locomotion locomotion, FoodMode foodMode,
                          ThreatResponse threatResponse, Habitat habitat, boolean social,
                          boolean needsSurface, int scanRadius, int actionTimeoutTicks,
                          int memoryTicks) {
        public Profile {
            if (id == null || id.isBlank()) throw new IllegalArgumentException("species id is required");
            if (scanRadius < 1 || actionTimeoutTicks < 1 || memoryTicks < 0) {
                throw new IllegalArgumentException("species bounds must be positive");
            }
        }
    }

    private static final Map<String, Profile> PROFILES = createProfiles();

    private SpeciesBehaviorProfile() {}

    public static Profile forId(String id) {
        return PROFILES.get(id);
    }

    public static Profile forEntity(net.minecraft.world.entity.Entity entity) {
        ResourceLocation id = entity.getType().builtInRegistryHolder().key().location();
        return forId(id.getPath());
    }

    public static Collection<Profile> all() {
        return PROFILES.values();
    }

    public static boolean covers(String id) {
        return PROFILES.containsKey(id);
    }

    private static Map<String, Profile> createProfiles() {
        Map<String, Profile> profiles = new LinkedHashMap<>();
        add(profiles, "great_white_shark", Family.SHARK, Locomotion.PELAGIC_FORWARD,
                FoodMode.FISH_AND_INVERTEBRATE, ThreatResponse.HUNT, Habitat.OPEN_WATER, false, false, 28, 400, 100);
        add(profiles, "great_hammerhead_shark", Family.SHARK, Locomotion.PELAGIC_FORWARD,
                FoodMode.FISH_AND_INVERTEBRATE, ThreatResponse.HUNT, Habitat.SEAFLOOR, false, false, 28, 400, 100);
        add(profiles, "common_thresher_shark", Family.SHARK, Locomotion.PELAGIC_FORWARD,
                FoodMode.SMALL_FISH, ThreatResponse.HUNT, Habitat.OPEN_WATER, false, false, 28, 400, 100);
        add(profiles, "shortfin_mako_shark", Family.SHARK, Locomotion.PELAGIC_BURST,
                FoodMode.SMALL_FISH, ThreatResponse.HUNT, Habitat.OPEN_WATER, false, false, 28, 400, 100);
        add(profiles, "tiger_shark", Family.SHARK, Locomotion.PELAGIC_FORWARD,
                FoodMode.FISH_AND_INVERTEBRATE, ThreatResponse.HUNT, Habitat.OPEN_WATER, false, false, 26, 400, 100);
        add(profiles, "oceanic_whitetip_shark", Family.SHARK, Locomotion.PELAGIC_FORWARD,
                FoodMode.FISH_AND_INVERTEBRATE, ThreatResponse.HUNT, Habitat.OPEN_WATER, false, false, 26, 400, 100);
        add(profiles, "sandtiger_shark", Family.SHARK, Locomotion.PELAGIC_FORWARD,
                FoodMode.FISH_AND_INVERTEBRATE, ThreatResponse.HUNT, Habitat.OPEN_WATER, false, false, 26, 400, 100);
        add(profiles, "blacktip_reef_shark", Family.SHARK, Locomotion.PELAGIC_FORWARD,
                FoodMode.FISH_AND_INVERTEBRATE, ThreatResponse.HUNT, Habitat.SEAFLOOR, false, false, 24, 400, 100);

        add(profiles, "orca", Family.MAMMAL, Locomotion.POD_TRAVEL,
                FoodMode.FISH_AND_INVERTEBRATE, ThreatResponse.FLEE, Habitat.SURFACE_ACCESS, true, true, 20, 600, 200);
        add(profiles, "bottlenose_dolphin", Family.MAMMAL, Locomotion.POD_TRAVEL,
                FoodMode.SMALL_FISH, ThreatResponse.FLEE, Habitat.SURFACE_ACCESS, true, true, 16, 400, 200);
        add(profiles, "harbor_seal", Family.MAMMAL, Locomotion.HAULOUT_SWIM,
                FoodMode.SMALL_FISH, ThreatResponse.FLEE, Habitat.SURFACE_ACCESS, true, true, 14, 400, 200);
        add(profiles, "green_sea_turtle", Family.TURTLE, Locomotion.BOTTOM_GRAZE,
                FoodMode.ALGAE, ThreatResponse.FLEE, Habitat.SEAFLOOR, false, true, 12, 600, 200);
        add(profiles, "common_octopus", Family.OCTOPUS, Locomotion.BENTHIC_CRAWL,
                FoodMode.BENTHIC_INVERTEBRATE, ThreatResponse.INK_ESCAPE, Habitat.SHELTER, false, false, 12, 400, 300);
        add(profiles, "caribbean_reef_octopus", Family.OCTOPUS, Locomotion.BENTHIC_SHELTER,
                FoodMode.BENTHIC_INVERTEBRATE, ThreatResponse.INK_ESCAPE, Habitat.SHELTER, false, false, 12, 400, 300);
        add(profiles, "nautilus", Family.BENTHIC, Locomotion.JET_DRIFT,
                FoodMode.BENTHIC_INVERTEBRATE, ThreatResponse.FLEE, Habitat.SEAFLOOR, false, false, 10, 500, 300);
        add(profiles, "giant_moray_eel", Family.BENTHIC, Locomotion.BENTHIC_SHELTER,
                FoodMode.SMALL_FISH, ThreatResponse.FLEE, Habitat.SHELTER, false, false, 10, 400, 300);
        add(profiles, "american_lobster", Family.BENTHIC, Locomotion.BOTTOM_WALK,
                FoodMode.BENTHIC_INVERTEBRATE, ThreatResponse.DEFENSIVE_STING, Habitat.SEAFLOOR, false, false, 10, 400, 300);
        add(profiles, "common_stingray", Family.BENTHIC, Locomotion.BOTTOM_GLIDE,
                FoodMode.BENTHIC_INVERTEBRATE, ThreatResponse.DEFENSIVE_STING, Habitat.SEAFLOOR, false, false, 10, 400, 300);
        add(profiles, "black_sea_nettle_jellyfish", Family.JELLYFISH, Locomotion.PULSE_DRIFT,
                FoodMode.PLANKTON, ThreatResponse.NONE, Habitat.CURRENT_DRIFT, false, false, 8, 200, 0);
        add(profiles, "cannonball_jellyfish", Family.JELLYFISH, Locomotion.PULSE_DRIFT,
                FoodMode.PLANKTON, ThreatResponse.NONE, Habitat.CURRENT_DRIFT, false, false, 8, 200, 0);
        add(profiles, "atlantic_cod", Family.FISH, Locomotion.PELAGIC_FORWARD,
                FoodMode.SMALL_FISH, ThreatResponse.FLEE, Habitat.SEAFLOOR, true, false, 12, 300, 100);
        add(profiles, "atlantic_salmon", Family.FISH, Locomotion.PELAGIC_FORWARD,
                FoodMode.SMALL_FISH, ThreatResponse.FLEE, Habitat.OPEN_WATER, true, false, 12, 300, 100);
        return Map.copyOf(profiles);
    }

    private static void add(Map<String, Profile> profiles, String id, Family family,
                            Locomotion locomotion, FoodMode foodMode, ThreatResponse threatResponse,
                            Habitat habitat, boolean social, boolean needsSurface, int scanRadius,
                            int actionTimeoutTicks, int memoryTicks) {
        profiles.put(id, new Profile(id, family, locomotion, foodMode, threatResponse, habitat,
                social, needsSurface, scanRadius, actionTimeoutTicks, memoryTicks));
    }
}
