package tfar.bensfintasticsharks.datagen.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModItems;
import tfar.bensfintasticsharks.init.ModTags;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput dataGenerator, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator,lookupProvider, blockTagProvider, BensFintasticSharks.MOD_ID, existingFileHelper);
    }
    @SuppressWarnings("unchecked")
    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Items.SHARK_TEETH).add(ModItems.GREAT_WHITE_SHARK_TOOTH,ModItems.GREAT_HAMMERHEAD_SHARK_TOOTH,ModItems.COMMON_THRESHER_SHARK_TOOTH,
                ModItems.TIGER_SHARK_TOOTH,ModItems.SHORTFIN_MAKO_SHARK_TOOTH,ModItems.OCEANIC_WHITETIP_SHARK_TOOTH);
        // MEGALODON_TOOTH stays out of SHARK_TEETH on purpose — it's a collectible gag,
        // not a crafting substitute for real teeth.
        tag(ItemTags.AXES).add(ModItems.SHARK_AXE);
        tag(ItemTags.HOES).add(ModItems.SHARK_HOE);
        tag(ItemTags.PICKAXES).add(ModItems.SHARK_PICKAXE);
        tag(ItemTags.SWORDS).add(ModItems.SHARK_SWORD);
        tag(ItemTags.SHOVELS).add(ModItems.SHARK_SHOVEL);

    }
}
