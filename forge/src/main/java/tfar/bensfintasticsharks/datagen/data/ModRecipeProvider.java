package tfar.bensfintasticsharks.datagen.data;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModItems;
import tfar.bensfintasticsharks.init.ModTags;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput generatorIn) {
        super(generatorIn);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SHARK_AXE)
                .define('s', ModTags.Items.SHARK_TEETH)
                .define('c',ModItems.CARTILAGE)
                .pattern("ss")
                .pattern("sc")
                .pattern(" c")
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SHARK_HOE)
                .define('s', ModTags.Items.SHARK_TEETH)
                .define('c',ModItems.CARTILAGE)
                .pattern("ss")
                .pattern(" c")
                .pattern(" c")
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SHARK_PICKAXE)
                .define('s', ModTags.Items.SHARK_TEETH)
                .define('c',ModItems.CARTILAGE)
                .pattern("sss")
                .pattern(" c ")
                .pattern(" c ")
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SHARK_SHOVEL)
                .define('s', ModTags.Items.SHARK_TEETH)
                .define('c',ModItems.CARTILAGE)
                .pattern("s")
                .pattern("c")
                .pattern("c")
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SHARK_SWORD)
                .define('s', ModTags.Items.SHARK_TEETH)
                .define('c',ModItems.CARTILAGE)
                .pattern("s")
                .pattern("s")
                .pattern("c")
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.PRISMARINE_HELMET)
                .define('d', Items.DIAMOND)
                .define('p',Items.PRISMARINE_SHARD)
                .define('s', ModTags.Items.SHARK_TEETH)
                .pattern("pdp")
                .pattern("s s")
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.PRISMARINE_CHESTPLATE)
                .define('d', Items.DIAMOND)
                .define('p',Items.PRISMARINE_SHARD)
                .define('s', ModTags.Items.SHARK_TEETH)
                .pattern("p p")
                .pattern("ddd")
                .pattern("sds")
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.PRISMARINE_LEGGINGS)
                .define('d', Items.DIAMOND)
                .define('p',Items.PRISMARINE_SHARD)
                .define('s', ModTags.Items.SHARK_TEETH)
                .pattern("ppp")
                .pattern("d d")
                .pattern("s s")
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.PRISMARINE_BOOTS)
                .define('d', Items.DIAMOND)
                .define('p',Items.PRISMARINE_SHARD)
                .define('s', ModTags.Items.SHARK_TEETH)
                .pattern("p p")
                .pattern("d d")
                .pattern("s s")
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CODEX_PAGE)
                .define('p',ModItems.LOST_MANUSCRIPT)
                .pattern("ppp")
                .pattern("ppp")
                .pattern("ppp")
                .unlockedBy("has_lost_manuscript",has(ModItems.LOST_MANUSCRIPT))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CODEX_VOLUME)
                .define('p',ModItems.CODEX_PAGE)
                .pattern("ppp")
                .pattern("ppp")
                .pattern("ppp")
                .unlockedBy(getHasName(ModItems.CODEX_PAGE),has(ModItems.CODEX_PAGE))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC,ModItems.SHARK_CODEX)
                .requires(Items.BOOK).requires(ModTags.Items.SHARK_TEETH)
                .requires(Items.BLUE_DYE).requires(Items.TROPICAL_FISH_BUCKET)
                .unlockedBy("has_shark_teeth",has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        // Shark Trident (Ben's corrected recipe): a vanilla Trident centered, with the three
        // shark teeth around it — Common Thresher + Great White on the top row, Great Hammerhead
        // to its right — Cartilage on the left/bottom, and a Prismarine Shard bottom-left.
        //   row 1:  _  thresher  white
        //   row 2:  cartilage  trident  hammerhead
        //   row 3:  prismarine  cartilage  _
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.SHARK_TRIDENT)
                .define('T', Items.TRIDENT)
                .define('C', ModItems.COMMON_THRESHER_SHARK_TOOTH)
                .define('W', ModItems.GREAT_WHITE_SHARK_TOOTH)
                .define('H', ModItems.GREAT_HAMMERHEAD_SHARK_TOOTH)
                .define('R', ModItems.CARTILAGE)
                .define('P', Items.PRISMARINE_SHARD)
                .pattern(" CW")
                .pattern("RTH")
                .pattern("PR ")
                .unlockedBy("has_shark_teeth", has(ModTags.Items.SHARK_TEETH))
                .save(consumer);

        // Lobster meat smelting (Legacy 1.0)
        SimpleCookingRecipeBuilder.smelting(net.minecraft.world.item.crafting.Ingredient.of(ModItems.RAW_LOBSTER_CLAW),
                        RecipeCategory.FOOD, ModItems.COOKED_LOBSTER_CLAW, 0.35F, 200)
                .unlockedBy("has_raw_lobster_claw", has(ModItems.RAW_LOBSTER_CLAW))
                .save(consumer, "cooked_lobster_claw_from_smelting");

        SimpleCookingRecipeBuilder.smelting(net.minecraft.world.item.crafting.Ingredient.of(ModItems.RAW_LOBSTER_TAIL),
                        RecipeCategory.FOOD, ModItems.COOKED_LOBSTER_TAIL, 0.35F, 200)
                .unlockedBy("has_raw_lobster_tail", has(ModItems.RAW_LOBSTER_TAIL))
                .save(consumer, "cooked_lobster_tail_from_smelting");

        SimpleCookingRecipeBuilder.campfireCooking(net.minecraft.world.item.crafting.Ingredient.of(ModItems.RAW_LOBSTER_CLAW),
                        RecipeCategory.FOOD, ModItems.COOKED_LOBSTER_CLAW, 0.35F, 600)
                .unlockedBy("has_raw_lobster_claw", has(ModItems.RAW_LOBSTER_CLAW))
                .save(consumer, "cooked_lobster_claw_from_campfire");

        SimpleCookingRecipeBuilder.campfireCooking(net.minecraft.world.item.crafting.Ingredient.of(ModItems.RAW_LOBSTER_TAIL),
                        RecipeCategory.FOOD, ModItems.COOKED_LOBSTER_TAIL, 0.35F, 600)
                .unlockedBy("has_raw_lobster_tail", has(ModItems.RAW_LOBSTER_TAIL))
                .save(consumer, "cooked_lobster_tail_from_campfire");

        addFishCookingRecipes(consumer, ModItems.RAW_ATLANTIC_COD, ModItems.COOKED_ATLANTIC_COD,
                "atlantic_cod");
        addFishCookingRecipes(consumer, ModItems.RAW_ATLANTIC_SALMON, ModItems.COOKED_ATLANTIC_SALMON,
                "atlantic_salmon");
    }

    private static void addFishCookingRecipes(Consumer<FinishedRecipe> consumer, net.minecraft.world.item.Item raw,
                                              net.minecraft.world.item.Item cooked, String name) {
        var ingredient = net.minecraft.world.item.crafting.Ingredient.of(raw);
        SimpleCookingRecipeBuilder.smelting(ingredient, RecipeCategory.FOOD, cooked, 0.35F, 200)
                .unlockedBy("has_raw_" + name, has(raw))
                .save(consumer, BensFintasticSharks.id("cooked_" + name + "_from_smelting"));
        SimpleCookingRecipeBuilder.smoking(ingredient, RecipeCategory.FOOD, cooked, 0.35F, 100)
                .unlockedBy("has_raw_" + name, has(raw))
                .save(consumer, BensFintasticSharks.id("cooked_" + name + "_from_smoking"));
        SimpleCookingRecipeBuilder.campfireCooking(ingredient, RecipeCategory.FOOD, cooked, 0.35F, 600)
                .unlockedBy("has_raw_" + name, has(raw))
                .save(consumer, BensFintasticSharks.id("cooked_" + name + "_from_campfire"));
    }
}
