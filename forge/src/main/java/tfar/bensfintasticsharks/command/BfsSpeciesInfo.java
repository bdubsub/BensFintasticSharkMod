package tfar.bensfintasticsharks.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.init.ModTags;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class BfsSpeciesInfo {

    private static final Map<String, String> VANILLA_REPLACEMENT_HABITATS = Map.of(
            "atlantic_cod", "Cold Ocean, Deep Cold Ocean, Deep Lukewarm Ocean, Deep Ocean, Lukewarm Ocean, Ocean",
            "atlantic_salmon", "Cold Ocean, Deep Cold Ocean, Deep Frozen Ocean, Frozen Ocean, Frozen River, River"
    );

    private static final Map<String, Entry> ENTRIES = Map.ofEntries(
            Map.entry("great_white_shark", shark("Carcharodon carcharias", "Neutral apex predator",
                    ModTags.Biomes.GREAT_WHITE_SHARK_SPAWNS, ModTags.EntityTypes.GREAT_WHITE_SHARK_PREY)),
            Map.entry("great_hammerhead_shark", shark("Sphyrna mokarran", "Neutral apex predator",
                    ModTags.Biomes.GREAT_HAMMERHEAD_SHARK_SPAWNS, ModTags.EntityTypes.GREAT_HAMMERHEAD_SHARK_PREY)),
            Map.entry("common_thresher_shark", shark("Alopias vulpinus", "Neutral predator",
                    ModTags.Biomes.COMMON_THRESHER_SHARK_SPAWNS, ModTags.EntityTypes.COMMON_THRESHER_SHARK_PREY)),
            Map.entry("shortfin_mako_shark", shark("Isurus oxyrinchus", "Neutral pursuit predator",
                    ModTags.Biomes.SHORTFIN_MAKO_SHARK_SPAWNS, ModTags.EntityTypes.SHORTFIN_MAKO_SHARK_PREY)),
            Map.entry("tiger_shark", shark("Galeocerdo cuvier", "Neutral and curious predator",
                    ModTags.Biomes.TIGER_SHARK_SPAWNS, ModTags.EntityTypes.TIGER_SHARK_PREY)),
            Map.entry("oceanic_whitetip_shark", shark("Carcharhinus longimanus", "Neutral persistent predator",
                    ModTags.Biomes.OCEANIC_WHITETIP_SHARK_SPAWNS, ModTags.EntityTypes.OCEANIC_WHITETIP_SHARK_PREY)),
            Map.entry("sandtiger_shark", shark("Carcharias taurus", "Neutral ambush predator",
                    ModTags.Biomes.SANDTIGER_SHARK_SPAWNS, ModTags.EntityTypes.SANDTIGER_SHARK_PREY)),
            Map.entry("blacktip_reef_shark", shark("Carcharhinus melanopterus", "Skittish schooling predator",
                    ModTags.Biomes.BLACKTIP_REEF_SHARK_SPAWNS, ModTags.EntityTypes.BLACKTIP_REEF_SHARK_PREY)),
            Map.entry("orca", species("Orcinus orca", "Passive marine mammal",
                    "TBD", ModTags.Biomes.ORCA_SPAWNS)),
            Map.entry("bottlenose_dolphin", species("Tursiops truncatus", "Passive and playful",
                    "TBD", ModTags.Biomes.BOTTLENOSE_DOLPHIN_SPAWNS)),
            Map.entry("common_octopus", species("Octopus vulgaris", "Passive and evasive",
                    "TBD", ModTags.Biomes.COMMON_OCTOPUS_SPAWNS)),
            Map.entry("caribbean_reef_octopus", species("Octopus briareus", "Passive and evasive",
                    "TBD", ModTags.Biomes.CARIBBEAN_REEF_OCTOPUS_SPAWNS)),
            Map.entry("nautilus", species("Nautilus pompilius", "Passive",
                    "TBD", ModTags.Biomes.NAUTILUS_SPAWNS, ModTags.Biomes.NAUTILUS_CAVE_SPAWNS)),
            Map.entry("giant_moray_eel", species("Gymnothorax javanicus", "Defensive reef predator",
                    "TBD", ModTags.Biomes.GIANT_MORAY_EEL_SPAWNS)),
            Map.entry("green_sea_turtle", species("Chelonia mydas", "Passive",
                    "TBD", ModTags.Biomes.GREEN_SEA_TURTLE_SPAWNS)),
            Map.entry("american_lobster", species("Homarus americanus", "Passive",
                    "TBD", ModTags.Biomes.AMERICAN_LOBSTER_SPAWNS)),
            Map.entry("common_stingray", species("Dasyatis pastinaca", "Defensive when threatened",
                    "TBD", ModTags.Biomes.COMMON_STINGRAY_SPAWNS)),
            Map.entry("harbor_seal", species("Phoca vitulina", "Passive marine mammal",
                    "TBD", ModTags.Biomes.HARBOR_SEAL_SPAWNS)),
            Map.entry("black_sea_nettle_jellyfish", species("Chrysaora achlyos", "Passive contact hazard",
                    "TBD", ModTags.Biomes.BLACK_SEA_NETTLE_JELLYFISH_SPAWNS)),
            Map.entry("cannonball_jellyfish", species("Stomolophus meleagris", "Passive contact hazard",
                    "TBD", ModTags.Biomes.CANNONBALL_JELLYFISH_SPAWNS)),
            Map.entry("atlantic_cod", species("Gadus morhua", "Passive schooling fish",
                    "TBD", ModTags.Biomes.ATLANTIC_COD_SPAWNS)),
            Map.entry("atlantic_salmon", species("Salmo salar", "Passive schooling fish",
                    "TBD", ModTags.Biomes.ATLANTIC_SALMON_SPAWNS))
    );

    private BfsSpeciesInfo() {
    }

    static Entry get(String species) {
        return ENTRIES.get(species);
    }

    static String habitats(CommandSourceStack source, String species, Entry entry) {
        if (BfsConfig.COMMON.replaceVanillaMobs.get()) {
            String replacementHabitats = VANILLA_REPLACEMENT_HABITATS.get(species);
            if (replacementHabitats != null) {
                return replacementHabitats;
            }
        }

        var registry = source.getLevel().registryAccess().registryOrThrow(Registries.BIOME);
        List<String> names = entry.habitats().stream()
                .flatMap(tag -> registry.getTag(tag).stream())
                .flatMap(named -> named.stream())
                .flatMap(holder -> holder.unwrapKey().stream())
                .map(key -> prettyName(key.location()))
                .sorted()
                .toList();
        return names.isEmpty() ? "No natural habitat" : String.join(", ", names);
    }

    static String diet(Entry entry) {
        if (entry.preyTag() == null) {
            return entry.diet();
        }

        List<String> prey = BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(type -> type.is(entry.preyTag()))
                .map(EntityType::getDescription)
                .map(component -> component.getString())
                .sorted()
                .collect(Collectors.toList());
        return prey.isEmpty() ? entry.diet() : String.join(", ", prey);
    }

    private static Entry shark(String scientificName, String behavior, TagKey<Biome> habitats,
                               TagKey<EntityType<?>> preyTag) {
        return new Entry(scientificName, behavior, "Datapack prey list", List.of(habitats), preyTag);
    }

    @SafeVarargs
    private static Entry species(String scientificName, String behavior, String diet,
                                 TagKey<Biome>... habitats) {
        return new Entry(scientificName, behavior, diet, List.of(habitats), null);
    }

    private static String prettyName(ResourceLocation id) {
        String[] words = id.getPath().split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!result.isEmpty()) {
                result.append(' ');
            }
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }

    record Entry(String scientificName, String behavior, String diet, List<TagKey<Biome>> habitats,
                 TagKey<EntityType<?>> preyTag) {
    }
}
