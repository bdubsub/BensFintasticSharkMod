package tfar.bensfintasticsharks.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import tfar.bensfintasticsharks.BensFintasticSharks;

public class ModTags {

    public static class EntityTypes {

        public static final TagKey<EntityType<?>> GREAT_WHITE_SHARK_ALWAYS_ATTACKS = create("great_white_shark/always_attacks");
        public static final TagKey<EntityType<?>> GREAT_HAMMERHEAD_SHARK_ALWAYS_ATTACKS = create("great_hammerhead_shark/always_attacks");
        public static final TagKey<EntityType<?>> COMMON_THRESHER_SHARK_ALWAYS_ATTACKS = create("common_thresher_shark/always_attacks");
        public static final TagKey<EntityType<?>> SHORTFIN_MAKO_SHARK_ALWAYS_ATTACKS = create("shortfin_mako_shark/always_attacks");

        /** Broad prey list used by all 8 sharks for general targeting. */
        public static final TagKey<EntityType<?>> SHARK_PREY = create("shark_prey");

        // 0.19 — per-species prey lists (Ben's Part II hunger spec). Each shark only hunts
        // its own list; AbstractSharkEntity#preyTag() picks the right one. Datapack-tunable.
        public static final TagKey<EntityType<?>> GREAT_WHITE_SHARK_PREY = create("prey/great_white_shark");
        public static final TagKey<EntityType<?>> GREAT_HAMMERHEAD_SHARK_PREY = create("prey/great_hammerhead_shark");
        public static final TagKey<EntityType<?>> COMMON_THRESHER_SHARK_PREY = create("prey/common_thresher_shark");
        public static final TagKey<EntityType<?>> SHORTFIN_MAKO_SHARK_PREY = create("prey/shortfin_mako_shark");
        public static final TagKey<EntityType<?>> TIGER_SHARK_PREY = create("prey/tiger_shark");
        public static final TagKey<EntityType<?>> SANDTIGER_SHARK_PREY = create("prey/sandtiger_shark");
        public static final TagKey<EntityType<?>> OCEANIC_WHITETIP_SHARK_PREY = create("prey/oceanic_whitetip_shark");
        public static final TagKey<EntityType<?>> BLACKTIP_REEF_SHARK_PREY = create("prey/blacktip_reef_shark");

        public static final TagKey<EntityType<?>> SHARKS = create("sharks");
        public static final TagKey<EntityType<?>> APEX_PREDATOR = create("apex_predator");
        public static final TagKey<EntityType<?>> CONSERVATION_PROTECTED = create("conservation_protected");

        private static TagKey<EntityType<?>> create(String pName) {
            return TagKey.create(Registries.ENTITY_TYPE, BensFintasticSharks.id(pName));
        }
    }

    public static class Items {
        public static final TagKey<Item> SHARK_TEETH = create("shark_teeth");

        private static TagKey<Item> create(String pName) {
            return TagKey.create(Registries.ITEM, BensFintasticSharks.id(pName));
        }
    }

    public static class Biomes {

        public static TagKey<Biome> GREAT_WHITE_SHARK_SPAWNS = create("great_white_shark_spawns");
        public static TagKey<Biome> GREAT_HAMMERHEAD_SHARK_SPAWNS = create("great_hammerhead_shark_spawns");
        public static TagKey<Biome> COMMON_THRESHER_SHARK_SPAWNS = create("common_thresher_shark_spawns");
        public static TagKey<Biome> COMMON_STINGRAY_SPAWNS = create("common_stingray_spawns");
        public static TagKey<Biome> HARBOR_SEAL_SPAWNS = create("harbor_seal_spawns");
        public static TagKey<Biome> SHORTFIN_MAKO_SHARK_SPAWNS = create("shortfin_mako_shark_spawns");

        // Legacy 1.0 new mob spawn biomes
        public static TagKey<Biome> TIGER_SHARK_SPAWNS = create("tiger_shark_spawns");
        public static TagKey<Biome> OCEANIC_WHITETIP_SHARK_SPAWNS = create("oceanic_whitetip_shark_spawns");
        public static TagKey<Biome> SANDTIGER_SHARK_SPAWNS = create("sandtiger_shark_spawns");
        public static TagKey<Biome> BLACKTIP_REEF_SHARK_SPAWNS = create("blacktip_reef_shark_spawns");
        public static TagKey<Biome> BOTTLENOSE_DOLPHIN_SPAWNS = create("bottlenose_dolphin_spawns");
        public static TagKey<Biome> ORCA_SPAWNS = create("orca_spawns");
        public static TagKey<Biome> COMMON_OCTOPUS_SPAWNS = create("common_octopus_spawns");
        public static TagKey<Biome> CARIBBEAN_REEF_OCTOPUS_SPAWNS = create("caribbean_reef_octopus_spawns");
        public static TagKey<Biome> NAUTILUS_SPAWNS = create("nautilus_spawns");
        // 0.18 — flooded caves below y=0 (dripstone/lush/deep dark aquifers).
        public static TagKey<Biome> NAUTILUS_CAVE_SPAWNS = create("nautilus_cave_spawns");
        public static TagKey<Biome> GIANT_MORAY_EEL_SPAWNS = create("giant_moray_eel_spawns");
        public static TagKey<Biome> GREEN_SEA_TURTLE_SPAWNS = create("green_sea_turtle_spawns");
        public static TagKey<Biome> AMERICAN_LOBSTER_SPAWNS = create("american_lobster_spawns");
        public static TagKey<Biome> BLACK_SEA_NETTLE_JELLYFISH_SPAWNS = create("black_sea_nettle_jellyfish_spawns");
        public static TagKey<Biome> CANNONBALL_JELLYFISH_SPAWNS = create("cannonball_jellyfish_spawns");

        private static TagKey<Biome> create(String pName) {
            return TagKey.create(Registries.BIOME, BensFintasticSharks.id(pName));
        }
    }

}
