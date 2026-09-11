package tfar.bensfintasticsharks.datagen.data.loot;

import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.block.Block;
import tfar.bensfintasticsharks.datagen.ModDatagen;
import tfar.bensfintasticsharks.init.ModBlocks;

public class ModBlockLoot extends VanillaBlockLoot {

    @Override
    protected void generate() {
        add(ModBlocks.ALGAE_BLOCK, createShearsOnlyDrop(ModBlocks.ALGAE_BLOCK));
        add(ModBlocks.LARGE_GREEN_ALGAE, createShearsOnlyDrop(ModBlocks.LARGE_GREEN_ALGAE));
        add(ModBlocks.LARGE_RED_ALGAE, createShearsOnlyDrop(ModBlocks.LARGE_RED_ALGAE));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModDatagen.getKnownBlocks().toList();
    }
}
