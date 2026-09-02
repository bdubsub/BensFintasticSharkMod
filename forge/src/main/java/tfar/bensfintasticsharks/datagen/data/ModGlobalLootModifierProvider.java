package tfar.bensfintasticsharks.datagen.data;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import tfar.bensfintasticsharks.AddItemChanceLootModifier;
import tfar.bensfintasticsharks.AddOncePerWorldLootModifier;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.init.ModItems;

import java.util.HashSet;
import java.util.Set;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output) {
        super(output, BensFintasticSharks.MOD_ID);
    }

    // Obtainable through dungeons, shipwrecks, buried treasure, ocean ruins. (Low
    // chance of spawning in them)

    final Set<ResourceLocation> tables = new HashSet<>();

    @Override
    protected void start() {
        tables.add(BuiltInLootTables.SIMPLE_DUNGEON);
        tables.add(BuiltInLootTables.SHIPWRECK_MAP);
        tables.add(BuiltInLootTables.BURIED_TREASURE);
        tables.add(BuiltInLootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY);
        tables.add(BuiltInLootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY);

        for (ResourceLocation table : tables) {
            addModifier(table);
        }

        // Captain Ben's Hat appears in buried-treasure and shipwreck loot at ~5%.
        // Once-per-world: after the first natural spawn the world flag flips and
        // further rolls are suppressed. Creative / give bypass this entirely.
        add("add_captain_ben_hat_buried_treasure", new AddOncePerWorldLootModifier(
                new LootItemCondition[]{ LootTableIdCondition.builder(BuiltInLootTables.BURIED_TREASURE).build() },
                ModItems.CAPTAIN_BEN_HAT, 0.05f));
        add("add_captain_ben_hat_shipwreck_map", new AddOncePerWorldLootModifier(
                new LootItemCondition[]{ LootTableIdCondition.builder(BuiltInLootTables.SHIPWRECK_MAP).build() },
                ModItems.CAPTAIN_BEN_HAT, 0.03f));

        add("add_atlantic_cod_fishing", new AddItemChanceLootModifier(
                new LootItemCondition[]{LootTableIdCondition.builder(BuiltInLootTables.FISHING_FISH).build()},
                ModItems.RAW_ATLANTIC_COD, 1, 1, 0.125f));
        add("add_atlantic_salmon_fishing", new AddItemChanceLootModifier(
                new LootItemCondition[]{LootTableIdCondition.builder(BuiltInLootTables.FISHING_FISH).build()},
                ModItems.RAW_ATLANTIC_SALMON, 1, 1, 0.125f));
    }

    void addModifier(ResourceLocation table) {
        String[] s = table.getPath().split("/");
        add("add_item_chance_" + s[s.length - 1], new AddItemChanceLootModifier(new LootItemCondition[]{
                LootTableIdCondition.builder(table).build()
        }, ModItems.LOST_MANUSCRIPT, 1, 3, .05f));
    }
}
