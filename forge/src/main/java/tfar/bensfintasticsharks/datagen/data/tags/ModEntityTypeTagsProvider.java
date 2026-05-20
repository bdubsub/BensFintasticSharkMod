package tfar.bensfintasticsharks.datagen.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput p_126511_, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126511_,lookupProvider, BensFintasticSharks.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        tag(ModTags.EntityTypes.GREAT_WHITE_SHARK_ALWAYS_ATTACKS)
                .add(EntityType.COW,EntityType.DOLPHIN,EntityType.GLOW_SQUID,EntityType.PIG,EntityType.SHEEP,EntityType.SQUID,
                        EntityType.TURTLE,
                        ModEntityTypes.HARBOR_SEAL);

        tag(ModTags.EntityTypes.GREAT_HAMMERHEAD_SHARK_ALWAYS_ATTACKS)
                .add(EntityType.COW,EntityType.DOLPHIN,EntityType.GLOW_SQUID,EntityType.PIG,EntityType.SHEEP,EntityType.SQUID,
                        EntityType.TURTLE,
                        ModEntityTypes.HARBOR_SEAL);

        tag(ModTags.EntityTypes.COMMON_THRESHER_SHARK_ALWAYS_ATTACKS)
                .add(EntityType.COD,EntityType.GLOW_SQUID,EntityType.SALMON,EntityType.SHEEP,EntityType.SQUID,
                        EntityType.TURTLE);

        tag(ModTags.EntityTypes.SHORTFIN_MAKO_SHARK_ALWAYS_ATTACKS)
                .add(EntityType.COW,EntityType.DOLPHIN,EntityType.GLOW_SQUID,EntityType.PIG,EntityType.SHEEP,EntityType.SQUID,
                        EntityType.TURTLE,
                        ModEntityTypes.HARBOR_SEAL);

        tag(ModTags.EntityTypes.SHARKS)
                .add(ModEntityTypes.COMMON_THRESHER_SHARK,ModEntityTypes.GREAT_HAMMERHEAD_SHARK,ModEntityTypes.GREAT_WHITE_SHARK,
                        ModEntityTypes.SHORTFIN_MAKO_SHARK,
                        ModEntityTypes.TIGER_SHARK, ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                        ModEntityTypes.SANDTIGER_SHARK, ModEntityTypes.BLACKTIP_REEF_SHARK);

        // General prey list used by AbstractSharkEntity targeting. Vanilla water mobs
        // plus our own peaceful BFS species. Players are NOT in this list — sharks
        // only target players via disturbance reactions (which is intentional).
        tag(ModTags.EntityTypes.SHARK_PREY)
                .add(EntityType.COD, EntityType.SALMON, EntityType.TROPICAL_FISH, EntityType.PUFFERFISH,
                        EntityType.SQUID, EntityType.GLOW_SQUID,
                        EntityType.DOLPHIN, EntityType.TURTLE,
                        ModEntityTypes.HARBOR_SEAL, ModEntityTypes.COMMON_STINGRAY,
                        ModEntityTypes.AMERICAN_LOBSTER, ModEntityTypes.COMMON_OCTOPUS,
                        ModEntityTypes.CARIBBEAN_REEF_OCTOPUS, ModEntityTypes.NAUTILUS,
                        ModEntityTypes.GIANT_MORAY_EEL, ModEntityTypes.GREEN_SEA_TURTLE,
                        ModEntityTypes.BOTTLENOSE_DOLPHIN);

        tag(ModTags.EntityTypes.APEX_PREDATOR)
                .add(ModEntityTypes.COMMON_THRESHER_SHARK, ModEntityTypes.GREAT_HAMMERHEAD_SHARK, ModEntityTypes.GREAT_WHITE_SHARK,
                        ModEntityTypes.SHORTFIN_MAKO_SHARK,
                        ModEntityTypes.TIGER_SHARK, ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                        ModEntityTypes.SANDTIGER_SHARK, ModEntityTypes.BLACKTIP_REEF_SHARK,
                        ModEntityTypes.ORCA);

        tag(ModTags.EntityTypes.CONSERVATION_PROTECTED)
                .add(ModEntityTypes.COMMON_THRESHER_SHARK, ModEntityTypes.GREAT_HAMMERHEAD_SHARK, ModEntityTypes.GREAT_WHITE_SHARK,
                        ModEntityTypes.SHORTFIN_MAKO_SHARK,
                        ModEntityTypes.TIGER_SHARK, ModEntityTypes.OCEANIC_WHITETIP_SHARK,
                        ModEntityTypes.SANDTIGER_SHARK, ModEntityTypes.BLACKTIP_REEF_SHARK,
                        ModEntityTypes.ORCA, ModEntityTypes.BOTTLENOSE_DOLPHIN,
                        ModEntityTypes.COMMON_OCTOPUS, ModEntityTypes.CARIBBEAN_REEF_OCTOPUS,
                        ModEntityTypes.NAUTILUS, ModEntityTypes.GIANT_MORAY_EEL,
                        ModEntityTypes.GREEN_SEA_TURTLE, ModEntityTypes.COMMON_STINGRAY,
                        ModEntityTypes.HARBOR_SEAL,
                        ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH, ModEntityTypes.CANNONBALL_JELLYFISH);
    }
}
