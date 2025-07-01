package tfar.bensfintasticsharks.datagen.data.loot;

import net.minecraft.data.loot.packs.VanillaEntityLoot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootTable;
import tfar.bensfintasticsharks.datagen.ModDatagen;
import tfar.bensfintasticsharks.init.ModEntityTypes;

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
    }

    protected void nothing(EntityType<?> type) {
        this.add(type, LootTable.lootTable());
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModDatagen.getKnownEntityTypes();
    }
}
