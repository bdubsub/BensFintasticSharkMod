package tfar.bensfintasticsharks.datagen;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.loaders.SeparateTransformsModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, BensFintasticSharks.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        spawnEgg(ModItems.GREAT_WHITE_SHARK_SPAWN_EGG);
        spawnEgg(ModItems.GREAT_HAMMERHEAD_SHARK_SPAWN_EGG);
        spawnEgg(ModItems.COMMON_THRESHER_SHARK_SPAWN_EGG);
        spawnEgg(ModItems.HARBOR_SEAL_SPAWN_EGG);
        spawnEgg(ModItems.COMMON_STINGRAY_SPAWN_EGG);
        spawnEgg(ModItems.SHORTFIN_MAKO_SHARK_SPAWN_EGG);

        spawnEgg(ModItems.TIGER_SHARK_SPAWN_EGG);
        spawnEgg(ModItems.OCEANIC_WHITETIP_SHARK_SPAWN_EGG);
        spawnEgg(ModItems.SANDTIGER_SHARK_SPAWN_EGG);
        spawnEgg(ModItems.BLACKTIP_REEF_SHARK_SPAWN_EGG);
        spawnEgg(ModItems.BOTTLENOSE_DOLPHIN_SPAWN_EGG);
        spawnEgg(ModItems.ORCA_SPAWN_EGG);
        spawnEgg(ModItems.COMMON_OCTOPUS_SPAWN_EGG);
        spawnEgg(ModItems.CARIBBEAN_REEF_OCTOPUS_SPAWN_EGG);
        spawnEgg(ModItems.NAUTILUS_SPAWN_EGG);
        spawnEgg(ModItems.GIANT_MORAY_EEL_SPAWN_EGG);
        spawnEgg(ModItems.GREEN_SEA_TURTLE_SPAWN_EGG);
        spawnEgg(ModItems.AMERICAN_LOBSTER_SPAWN_EGG);
        spawnEgg(ModItems.BLACK_SEA_NETTLE_JELLYFISH_SPAWN_EGG);
        spawnEgg(ModItems.CANNONBALL_JELLYFISH_SPAWN_EGG);
        spawnEgg(ModItems.ATLANTIC_COD_SPAWN_EGG);
        spawnEgg(ModItems.ATLANTIC_SALMON_SPAWN_EGG);

        makeOneLayerItem(ModItems.CAPTAIN_BEN_HAT);
        makeOneLayerItem(ModItems.RAW_LOBSTER_CLAW);
        makeOneLayerItem(ModItems.COOKED_LOBSTER_CLAW);
        makeOneLayerItem(ModItems.RAW_LOBSTER_TAIL);
        makeOneLayerItem(ModItems.COOKED_LOBSTER_TAIL);
        makeOneLayerItem(ModItems.RAW_ATLANTIC_COD);
        makeOneLayerItem(ModItems.COOKED_ATLANTIC_COD);
        makeOneLayerItem(ModItems.RAW_ATLANTIC_SALMON);
        makeOneLayerItem(ModItems.COOKED_ATLANTIC_SALMON);

        makeSimpleBlockItem(ModItems.ALGAE_BLOCK);
        makeSimpleBlockItem(ModItems.LARGE_GREEN_ALGAE);
        makeSimpleBlockItem(ModItems.LARGE_RED_ALGAE);

        makeOneLayerItem(ModItems.GREAT_WHITE_SHARK_SKIN);
        makeOneLayerItem(ModItems.GREAT_HAMMERHEAD_SHARK_SKIN);
        makeOneLayerItem(ModItems.COMMON_THRESHER_SHARK_SKIN);

        makeOneLayerItem(ModItems.GREAT_WHITE_SHARK_TOOTH);
        makeOneLayerItem(ModItems.GREAT_HAMMERHEAD_SHARK_TOOTH);
        makeOneLayerItem(ModItems.COMMON_THRESHER_SHARK_TOOTH);
        makeOneLayerItem(ModItems.TIGER_SHARK_TOOTH);
        makeOneLayerItem(ModItems.SHORTFIN_MAKO_SHARK_TOOTH);
        makeOneLayerItem(ModItems.OCEANIC_WHITETIP_SHARK_TOOTH);
        makeOneLayerItem(ModItems.MEGALODON_TOOTH);
        makeOneLayerItem(ModItems.CARTILAGE);
        // The *_SHARK_BLOCK icons use handwritten 3D models in assets/ — no datagen here.

        makeOneLayerItem(ModItems.SHARK_AXE,handheld);
        makeOneLayerItem(ModItems.SHARK_HOE,handheld);
        makeOneLayerItem(ModItems.SHARK_PICKAXE,handheld);
        makeOneLayerItem(ModItems.SHARK_SHOVEL,handheld);
        makeOneLayerItem(ModItems.SHARK_SWORD,handheld);

        makeOneLayerItem(ModItems.PRISMARINE_HELMET);
        makeOneLayerItem(ModItems.PRISMARINE_CHESTPLATE);
        makeOneLayerItem(ModItems.PRISMARINE_LEGGINGS);
        makeOneLayerItem(ModItems.PRISMARINE_BOOTS);

        makeOneLayerItem(ModItems.LOST_MANUSCRIPT);
        makeOneLayerItem(ModItems.SHARK_CODEX);
        makeOneLayerItem(ModItems.CODEX_PAGE);
        makeOneLayerItem(ModItems.CODEX_VOLUME);

        makeOneLayerItem(ModItems.GREAT_WHITE_SHARK_PIXEL_ART);
        makeOneLayerItem(ModItems.GREAT_HAMMERHEAD_SHARK_PIXEL_ART);
        makeOneLayerItem(ModItems.COMMON_THRESHER_SHARK_PIXEL_ART);
        makeOneLayerItem(ModItems.STINGRAY_PIXEL_ART);
        makeOneLayerItem(ModItems.ILLEGAL_POACHING);
        makeOneLayerItem(ModItems.ALBINO);
        makeOneLayerItem(ModItems.ZIPPY_PIXEL_ART);
        makeOneLayerItem(ModItems.JUSTICE_FOR_STEVE);
        makeOneLayerItem(ModItems.SHARKS_GALORE);
        makeOneLayerItem(ModItems.SLEEPING_WITH_THE_FISHES);
        makeOneLayerItem(ModItems.MOMMY_SHARK);
        makeOneLayerItem(ModItems.SPECIMEN_8);
        makeOneLayerItem(ModItems.GREAT_WHITE_SHARK_ADVANCEMENT_ICON);
        makeOneLayerItem(ModItems.GREAT_HAMMERHEAD_SHARK_ADVANCEMENT_ICON);
        makeOneLayerItem(ModItems.COMMON_THRESHER_SHARK_ADVANCEMENT_ICON);
        makeOneLayerItem(ModItems.SHORTFIN_MAKO_SHARK_ADVANCEMENT_ICON);
        makeOneLayerItem(ModItems.TIGER_SHARK_ADVANCEMENT_ICON);
        makeOneLayerItem(ModItems.OCEANIC_WHITETIP_SHARK_ADVANCEMENT_ICON);
        makeOneLayerItem(ModItems.SANDTIGER_SHARK_ADVANCEMENT_ICON);
        makeOneLayerItem(ModItems.BLACKTIP_REEF_SHARK_ADVANCEMENT_ICON);
        makeOneLayerItem(ModItems.SHARK_JAWS);

        trident(ModItems.SHARK_TRIDENT);

        makeOneLayerItem(ModItems.BFS_LOGO);
    }

    protected ModelFile.ExistingModelFile generated = getExistingFile(mcLoc("item/generated"));
    protected ModelFile.ExistingModelFile handheld = getExistingFile(mcLoc("item/handheld"));
    protected ModelFile.ExistingModelFile template_spawn_egg = getExistingFile(mcLoc("item/template_spawn_egg"));



    protected void makeSimpleBlockItem(Item item, ResourceLocation loc) {
        String s = BuiltInRegistries.ITEM.getKey(item).getPath();
        getBuilder(s)
                .parent(getExistingFile(loc));
    }

    protected void makeSimpleBlockItem(Item item) {
        makeSimpleBlockItem(item, BensFintasticSharks.id("block/" + BuiltInRegistries.ITEM.getKey(item).getPath()));
    }

    protected void spawnEgg(Item item) {
        // Custom per-species sprite (textures/item/<id>.png) instead of the vanilla tinted egg.
        makeOneLayerItem(item);
    }

    protected void makeOneLayerItem(Item item, ResourceLocation texture) {
        makeOneLayerItem(item,texture,generated);
    }

    protected void makeOneLayerItem(Item item, ResourceLocation texture,ModelFile parent) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        if (existingFileHelper.exists(texture, PackType.CLIENT_RESOURCES, ".png", "textures")) {
            getBuilder(path).parent(parent)
                    .texture("layer0",texture);
        } else {
            System.out.println("no texture for " + item + " found, skipping");
        }
    }


    protected void makeOneLayerItem(Item item) {
        makeOneLayerItem(item,generated);
    }

    protected void makeOneLayerItem(Item item,ModelFile parent) {
        ResourceLocation texture = BuiltInRegistries.ITEM.getKey(item);
        makeOneLayerItem(item, texture.withPrefix("item/"),parent);
    }

    protected ItemModelBuilder makeSpriteModel(String name) {
        return getBuilder("item/" + name+"_gui")
                .parent(generated)
                .texture("layer0", "item/" + name+"_gui");

    }

    private void trident(Item tieredTridentItem) {

        String name = BuiltInRegistries.ITEM.getKey(tieredTridentItem).getPath();//shark_trident

        //trident in hand model
        ItemModelBuilder r3dFile = nested()
                .parent(getExistingFile(modLoc("item/shark_trident_3d")));

        //trident throwing model

        //trident in hand model
        ItemModelBuilder r3dFileThrowing = nested()
                .parent(getExistingFile(mcLoc("item/trident_in_hand")));

        ItemModelBuilder rSpriteFile = makeSpriteModel(name);

        ItemModelBuilder throwingBuilder = nested()
                .parent(getExistingFile(modLoc("item/shark_trident_3d_throwing")));

      /*  ItemModelBuilder end = getBuilder(name + "_throwing").guiLight(BlockModel.GuiLight.FRONT)
                .customLoader(SeparateTransformsModelBuilder::begin).base(rSpriteFile)
                .perspective(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, throwingBuilder)
                .perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, throwingBuilder)
                .perspective(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, throwingBuilder)
                .perspective(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, throwingBuilder)
                .end();*/

        getBuilder(name+"_throwing").guiLight(BlockModel.GuiLight.FRONT)
                .customLoader(SeparateTransformsModelBuilder::begin).base(rSpriteFile)
                .perspective(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, throwingBuilder)
                .perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, throwingBuilder)
                .perspective(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, throwingBuilder)
                .perspective(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, throwingBuilder)
                .end();

        getBuilder(name).guiLight(BlockModel.GuiLight.FRONT)
                .customLoader(SeparateTransformsModelBuilder::begin).base(rSpriteFile)
                .perspective(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, r3dFile)
                .perspective(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, r3dFile)
                .perspective(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, r3dFile)
                .perspective(ItemDisplayContext.THIRD_PERSON_LEFT_HAND, r3dFile)
                .end()
                .override().model(getExistingFile(modLoc("item/"+name+"_throwing"))).predicate(mcLoc("throwing"), 1).end();


    }


}
