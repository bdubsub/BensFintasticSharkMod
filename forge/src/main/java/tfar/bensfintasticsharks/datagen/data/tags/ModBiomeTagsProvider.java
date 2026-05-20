package tfar.bensfintasticsharks.datagen.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagsProvider extends BiomeTagsProvider {

    public ModBiomeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, BensFintasticSharks.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        // Existing 1.0 mobs preserved
        tag(ModTags.Biomes.GREAT_WHITE_SHARK_SPAWNS).add(Biomes.OCEAN,Biomes.DEEP_OCEAN);

        tag(ModTags.Biomes.GREAT_HAMMERHEAD_SHARK_SPAWNS).add(Biomes.OCEAN,Biomes.DEEP_OCEAN,Biomes.WARM_OCEAN,Biomes.LUKEWARM_OCEAN,Biomes.DEEP_LUKEWARM_OCEAN);

        tag(ModTags.Biomes.COMMON_THRESHER_SHARK_SPAWNS).add(Biomes.OCEAN,Biomes.DEEP_OCEAN);

        tag(ModTags.Biomes.COMMON_STINGRAY_SPAWNS).add(Biomes.OCEAN,Biomes.LUKEWARM_OCEAN,Biomes.WARM_OCEAN,Biomes.SWAMP,Biomes.MANGROVE_SWAMP);

        tag(ModTags.Biomes.HARBOR_SEAL_SPAWNS).add(Biomes.OCEAN,Biomes.DEEP_OCEAN,Biomes.WARM_OCEAN,
                Biomes.LUKEWARM_OCEAN,Biomes.WARM_OCEAN,Biomes.COLD_OCEAN);

        tag(ModTags.Biomes.SHORTFIN_MAKO_SHARK_SPAWNS).add(Biomes.OCEAN,Biomes.DEEP_OCEAN,Biomes.LUKEWARM_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN,Biomes.WARM_OCEAN);

        // Legacy 1.0 new sharks
        tag(ModTags.Biomes.TIGER_SHARK_SPAWNS).add(Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN,
                Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);

        tag(ModTags.Biomes.OCEANIC_WHITETIP_SHARK_SPAWNS).add(Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN);

        tag(ModTags.Biomes.SANDTIGER_SHARK_SPAWNS).add(Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN);

        tag(ModTags.Biomes.BLACKTIP_REEF_SHARK_SPAWNS).add(Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);

        // Marine mammals
        tag(ModTags.Biomes.BOTTLENOSE_DOLPHIN_SPAWNS).add(Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);

        tag(ModTags.Biomes.ORCA_SPAWNS).add(Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN);

        // Cephalopods
        tag(ModTags.Biomes.COMMON_OCTOPUS_SPAWNS).add(Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN,
                Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN);

        tag(ModTags.Biomes.CARIBBEAN_REEF_OCTOPUS_SPAWNS).add(Biomes.WARM_OCEAN);

        tag(ModTags.Biomes.NAUTILUS_SPAWNS).add(Biomes.DEEP_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);

        // Other fauna
        tag(ModTags.Biomes.GIANT_MORAY_EEL_SPAWNS).add(Biomes.WARM_OCEAN);

        tag(ModTags.Biomes.GREEN_SEA_TURTLE_SPAWNS).add(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN);

        tag(ModTags.Biomes.AMERICAN_LOBSTER_SPAWNS).add(Biomes.OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN, Biomes.WARM_OCEAN);

        // Jellyfish
        tag(ModTags.Biomes.BLACK_SEA_NETTLE_JELLYFISH_SPAWNS).add(Biomes.DEEP_OCEAN, Biomes.DEEP_COLD_OCEAN);

        tag(ModTags.Biomes.CANNONBALL_JELLYFISH_SPAWNS).add(Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.WARM_OCEAN);
    }
}
