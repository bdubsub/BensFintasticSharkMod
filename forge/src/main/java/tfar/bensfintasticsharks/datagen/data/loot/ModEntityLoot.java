package tfar.bensfintasticsharks.datagen.data.loot;

import net.minecraft.data.loot.packs.VanillaEntityLoot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import tfar.bensfintasticsharks.datagen.ModDatagen;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModItems;

import java.util.stream.Stream;

public class ModEntityLoot extends VanillaEntityLoot {

    @Override
    public void generate() {
        nothing(ModEntityTypes.GREAT_WHITE_SHARK);
        nothing(ModEntityTypes.GREAT_HAMMERHEAD_SHARK);
        nothing(ModEntityTypes.COMMON_STINGRAY);
        nothing(ModEntityTypes.COMMON_THRESHER_SHARK);
        nothing(ModEntityTypes.HARBOR_SEAL);
        nothing(ModEntityTypes.SHORTFIN_MAKO_SHARK);

        // Conservation principle: new sharks, mammals, octopuses, jellyfish, eel, turtle, nautilus drop nothing.
        nothing(ModEntityTypes.TIGER_SHARK);
        nothing(ModEntityTypes.OCEANIC_WHITETIP_SHARK);
        nothing(ModEntityTypes.SANDTIGER_SHARK);
        nothing(ModEntityTypes.BLACKTIP_REEF_SHARK);
        nothing(ModEntityTypes.BOTTLENOSE_DOLPHIN);
        nothing(ModEntityTypes.ORCA);
        nothing(ModEntityTypes.COMMON_OCTOPUS);
        nothing(ModEntityTypes.CARIBBEAN_REEF_OCTOPUS);
        nothing(ModEntityTypes.GIANT_MORAY_EEL);
        nothing(ModEntityTypes.GREEN_SEA_TURTLE);
        nothing(ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH);
        nothing(ModEntityTypes.CANNONBALL_JELLYFISH);

        // 0.18 — Ben: nautilus should drop nautilus shells. The other exception to the
        // conservation principle: it's capped at 1 per 64-block radius and hides in the
        // deep, so a guaranteed shell rewards the effort of actually finding one.
        this.add(ModEntityTypes.NAUTILUS, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(net.minecraft.world.item.Items.NAUTILUS_SHELL))));

        // Lobster is the explicit exception — it's the food source in the design doc.
        // Drop layout (per pool, independent rolls):
        //   - Claw pool: 1 of 4 outcomes — 2 claws (15%), 1 claw (40%), or nothing (45%).
        //   - Tail pool: 1 of 3 outcomes — 1 tail (50%) or nothing (50%).
        // So you might get a full pile (2 claws + 1 tail), only one piece, or
        // occasionally nothing. Roughly balances out to "1.4 items per lobster".
        this.add(ModEntityTypes.AMERICAN_LOBSTER, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.RAW_LOBSTER_CLAW)
                                .setWeight(15)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))))
                        .add(LootItem.lootTableItem(ModItems.RAW_LOBSTER_CLAW)
                                .setWeight(40)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                        .add(net.minecraft.world.level.storage.loot.entries.EmptyLootItem.emptyItem()
                                .setWeight(45)))
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.RAW_LOBSTER_TAIL).setWeight(50))
                        .add(net.minecraft.world.level.storage.loot.entries.EmptyLootItem.emptyItem()
                                .setWeight(50))));
    }

    protected void nothing(EntityType<?> type) {
        this.add(type, LootTable.lootTable());
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModDatagen.getKnownEntityTypes();
    }
}
