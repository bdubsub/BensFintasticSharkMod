package tfar.bensfintasticsharks.datagen.data.loot;

import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import tfar.bensfintasticsharks.datagen.ModDatagen;
import tfar.bensfintasticsharks.init.ModBlocks;

public class ModBlockLoot extends VanillaBlockLoot {

    @Override
    protected void generate() {
        dropSelf(ModBlocks.ALGAE_BLOCK);
        dropSelf(ModBlocks.LARGE_GREEN_ALGAE);
        dropSelf(ModBlocks.LARGE_RED_ALGAE);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModDatagen.getKnownBlocks().toList();
    }
}
