package tfar.bensfintasticsharks;

import net.minecraft.resources.ResourceKey;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;

public class BiomeModifiers {
    public static final ResourceKey<BiomeModifier> GREAT_WHITE_SHARK_SPAWNS = create("great_white_shark_spawns");
    public static final ResourceKey<BiomeModifier> GREAT_HAMMERHEAD_SHARK_SPAWNS = create("great_hammerhead_shark_spawns");
    public static final ResourceKey<BiomeModifier> COMMON_THRESHER_SHARK_SPAWNS = create("common_thresher_shark_spawns");
    public static final ResourceKey<BiomeModifier> COMMON_STINGRAY_SHARK_SPAWNS = create("common_stingray_spawns");
    public static final ResourceKey<BiomeModifier> HARBOR_SEAL_SPAWNS = create("harbor_seal_spawns");
    public static final ResourceKey<BiomeModifier> SHORTFIN_MAKO_SHARK_SPAWNS = create("shortfin_mako_shark_spawns");

    public static final ResourceKey<BiomeModifier> TIGER_SHARK_SPAWNS = create("tiger_shark_spawns");
    public static final ResourceKey<BiomeModifier> OCEANIC_WHITETIP_SHARK_SPAWNS = create("oceanic_whitetip_shark_spawns");
    public static final ResourceKey<BiomeModifier> SANDTIGER_SHARK_SPAWNS = create("sandtiger_shark_spawns");
    public static final ResourceKey<BiomeModifier> BLACKTIP_REEF_SHARK_SPAWNS = create("blacktip_reef_shark_spawns");
    public static final ResourceKey<BiomeModifier> BOTTLENOSE_DOLPHIN_SPAWNS = create("bottlenose_dolphin_spawns");
    public static final ResourceKey<BiomeModifier> ORCA_SPAWNS = create("orca_spawns");
    public static final ResourceKey<BiomeModifier> COMMON_OCTOPUS_SPAWNS = create("common_octopus_spawns");
    public static final ResourceKey<BiomeModifier> CARIBBEAN_REEF_OCTOPUS_SPAWNS = create("caribbean_reef_octopus_spawns");
    public static final ResourceKey<BiomeModifier> NAUTILUS_SPAWNS = create("nautilus_spawns");
    public static final ResourceKey<BiomeModifier> NAUTILUS_CAVE_SPAWNS = create("nautilus_cave_spawns");
    public static final ResourceKey<BiomeModifier> GIANT_MORAY_EEL_SPAWNS = create("giant_moray_eel_spawns");
    public static final ResourceKey<BiomeModifier> GREEN_SEA_TURTLE_SPAWNS = create("green_sea_turtle_spawns");
    public static final ResourceKey<BiomeModifier> AMERICAN_LOBSTER_SPAWNS = create("american_lobster_spawns");
    public static final ResourceKey<BiomeModifier> BLACK_SEA_NETTLE_JELLYFISH_SPAWNS = create("black_sea_nettle_jellyfish_spawns");
    public static final ResourceKey<BiomeModifier> CANNONBALL_JELLYFISH_SPAWNS = create("cannonball_jellyfish_spawns");
    public static final ResourceKey<BiomeModifier> ATLANTIC_COD_SPAWNS = create("atlantic_cod_spawns");
    public static final ResourceKey<BiomeModifier> ATLANTIC_SALMON_SPAWNS = create("atlantic_salmon_spawns");

    private static ResourceKey<BiomeModifier> create(String key) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, BensFintasticSharks.id(key));
    }
}
