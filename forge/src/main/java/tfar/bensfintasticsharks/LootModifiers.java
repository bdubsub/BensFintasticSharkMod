package tfar.bensfintasticsharks;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;

public class LootModifiers {

    public static final Codec<? extends IGlobalLootModifier> ADD_ITEM_CHANCE = AddItemChanceLootModifier.CODEC;
    public static final Codec<? extends IGlobalLootModifier> ADD_ONCE_PER_WORLD = AddOncePerWorldLootModifier.CODEC;

}
