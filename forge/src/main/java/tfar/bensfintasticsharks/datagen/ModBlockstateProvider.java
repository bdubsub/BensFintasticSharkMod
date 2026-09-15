package tfar.bensfintasticsharks.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModBlocks;

public class ModBlockstateProvider extends BlockStateProvider {
    public ModBlockstateProvider(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, BensFintasticSharks.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        ModelFile algaeFace = models().withExistingParent("algae_block", mcLoc("block/glow_lichen"))
                .texture("glow_lichen", modLoc("block/algae_block"))
                .texture("particle", modLoc("block/algae_block"))
                .renderType("cutout");
        getMultipartBuilder(ModBlocks.ALGAE_BLOCK)
                .part().modelFile(algaeFace).addModel().condition(BlockStateProperties.NORTH, true).end()
                .part().modelFile(algaeFace).rotationY(90).uvLock(true).addModel()
                .condition(BlockStateProperties.EAST, true).end()
                .part().modelFile(algaeFace).rotationY(180).uvLock(true).addModel()
                .condition(BlockStateProperties.SOUTH, true).end()
                .part().modelFile(algaeFace).rotationY(270).uvLock(true).addModel()
                .condition(BlockStateProperties.WEST, true).end()
                .part().modelFile(algaeFace).rotationX(90).uvLock(true).addModel()
                .condition(BlockStateProperties.DOWN, true).end();

        ModelFile greenAlgae = models().cross("large_green_algae", modLoc("block/large_green_algae"))
                .renderType("cutout");
        ModelFile redAlgae = models().cross("large_red_algae", modLoc("block/large_red_algae"))
                .renderType("cutout");
        getVariantBuilder(ModBlocks.LARGE_GREEN_ALGAE).forAllStates(state ->
                ConfiguredModel.builder().modelFile(greenAlgae).build());
        getVariantBuilder(ModBlocks.LARGE_RED_ALGAE).forAllStates(state ->
                ConfiguredModel.builder().modelFile(redAlgae).build());
    }

    protected void blockstateFromExistingModel(Block block) {
        String s = BuiltInRegistries.BLOCK.getKey(block).getPath();
        ModelFile modelFile = models().getExistingFile(BensFintasticSharks.id("block/" + s));
        getVariantBuilder(block).forAllStates(state -> {
            ConfiguredModel.Builder<?> builder = ConfiguredModel.builder().modelFile(modelFile);
            if (state.hasProperty(HorizontalDirectionalBlock.FACING)) {
                switch (state.getValue(HorizontalDirectionalBlock.FACING)) {
                    case EAST -> builder.rotationY(90);
                    case SOUTH -> builder.rotationY(180);
                    case WEST -> builder.rotationY(270);
                    case NORTH -> builder.rotationY(0);
                }
            }
            return builder.build();
        });
    }
}
