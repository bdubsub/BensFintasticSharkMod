package tfar.bensfintasticsharks.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable inventory of the final powered movement owner for each BFS species. */
public final class SpeciesMovementAdapterCatalog {

    public record Adapter(String species, String adapterId, String writerId, String stateSource) {}

    private static final Map<String, Adapter> ADAPTERS = create();

    private SpeciesMovementAdapterCatalog() {}

    public static Collection<Adapter> all() {
        return ADAPTERS.values();
    }

    public static Adapter forSpecies(String species) {
        return ADAPTERS.get(species);
    }

    public static Adapter forEntity(Entity entity) {
        if (entity == null) return null;
        ResourceLocation id = entity.getType().builtInRegistryHolder().key().location();
        return forSpecies(id.getPath());
    }

    private static Map<String, Adapter> create() {
        Map<String, Adapter> adapters = new LinkedHashMap<>();
        add(adapters, "great_white_shark", "pitch_shark", "AbstractSharkEntity.swimInWater", "target_or_flee");
        add(adapters, "great_hammerhead_shark", "pitch_shark", "AbstractSharkEntity.swimInWater", "target_or_flee");
        add(adapters, "common_thresher_shark", "pitch_shark", "AbstractSharkEntity.swimInWater", "target_or_flee");
        add(adapters, "shortfin_mako_shark", "pitch_shark", "AbstractSharkEntity.swimInWater", "target_or_flee");
        add(adapters, "tiger_shark", "pitch_shark", "AbstractSharkEntity.swimInWater", "target_or_flee");
        add(adapters, "oceanic_whitetip_shark", "pitch_shark", "AbstractSharkEntity.swimInWater", "target_or_flee");
        add(adapters, "sandtiger_shark", "pitch_shark", "AbstractSharkEntity.swimInWater", "target_or_flee");
        add(adapters, "blacktip_reef_shark", "pitch_shark", "AbstractSharkEntity.swimInWater", "target_or_flee");
        add(adapters, "orca", "aquatic", "BfsAquaticEntity.travel", "behavior_action");
        add(adapters, "bottlenose_dolphin", "aquatic", "BfsAquaticEntity.travel", "behavior_action");
        add(adapters, "common_octopus", "aquatic", "BfsAquaticEntity.travel", "behavior_action");
        add(adapters, "caribbean_reef_octopus", "aquatic", "BfsAquaticEntity.travel", "behavior_action");
        add(adapters, "nautilus", "aquatic", "BfsAquaticEntity.travel", "behavior_action");
        add(adapters, "giant_moray_eel", "aquatic", "BfsAquaticEntity.travel", "behavior_action");
        add(adapters, "green_sea_turtle", "turtle", "GreenSeaTurtleEntity.travel", "behavior_action");
        add(adapters, "american_lobster", "lobster", "AmericanLobsterEntity.travel", "behavior_action");
        add(adapters, "common_stingray", "stingray", "CommonStingrayEntity.travel", "behavior_action");
        add(adapters, "harbor_seal", "seal", "HarborSealEntity.travel", "behavior_action");
        add(adapters, "black_sea_nettle_jellyfish", "jellyfish", "BlackSeaNettleJellyfishEntity.travel", "drift");
        add(adapters, "cannonball_jellyfish", "jellyfish", "CannonballJellyfishEntity.travel", "drift");
        add(adapters, "atlantic_cod", "fish", "BfsFishMoveControl.travel", "behavior_engine");
        add(adapters, "atlantic_salmon", "fish", "BfsFishMoveControl.travel", "behavior_engine");
        return Map.copyOf(adapters);
    }

    private static void add(Map<String, Adapter> adapters, String species, String adapter,
                            String writer, String state) {
        adapters.put(species, new Adapter(species, adapter, writer, state));
    }
}
