package tfar.bensfintasticsharks.datagen.data.tags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModBlocks;
import tfar.bensfintasticsharks.init.ModTags;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput p_126511_, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126511_,lookupProvider, BensFintasticSharks.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Blocks.ALGAE).add(ModBlocks.ALGAE_BLOCK, ModBlocks.LARGE_GREEN_ALGAE,
                ModBlocks.LARGE_RED_ALGAE);
        tag(BlockTags.MINEABLE_WITH_HOE).add(ModBlocks.ALGAE_BLOCK, ModBlocks.LARGE_GREEN_ALGAE,
                ModBlocks.LARGE_RED_ALGAE);
    }
}
