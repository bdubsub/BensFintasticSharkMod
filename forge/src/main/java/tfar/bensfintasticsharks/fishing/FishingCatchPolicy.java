package tfar.bensfintasticsharks.fishing;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModItems;

import javax.annotation.Nullable;
import java.util.List;

public final class FishingCatchPolicy {

    public static final float ATLANTIC_SELECTION_SHARE = 0.25F;

    private FishingCatchPolicy() {
    }

    public static ItemStack selectFishingItem(ItemStack original, boolean replaceVanillaMobs,
                                              float atlanticRoll, boolean selectAtlanticSalmon) {
        if (!original.is(Items.COD) && !original.is(Items.SALMON)) {
            return original.copy();
        }

        Item mappedReplacement = matchingAtlanticItem(original);
        if (replaceVanillaMobs && mappedReplacement != null) {
            return copyWithItem(original, mappedReplacement);
        }
        if (atlanticRoll < ATLANTIC_SELECTION_SHARE) {
            return copyWithItem(original, selectAtlanticSalmon
                    ? ModItems.RAW_ATLANTIC_SALMON
                    : ModItems.RAW_ATLANTIC_COD);
        }
        return original.copy();
    }

    @Nullable
    public static ItemStack onlySupportedFishingFish(List<ItemStack> drops) {
        if (drops.size() != 1 || !isSupportedFishingFish(drops.get(0))) {
            return null;
        }
        return drops.get(0);
    }

    @Nullable
    public static EntityType<? extends Mob> entityTypeFor(ItemStack stack) {
        Item item = stack.getItem();
        if (item == Items.COD) {
            return EntityType.COD;
        }
        if (item == Items.SALMON) {
            return EntityType.SALMON;
        }
        if (item == Items.TROPICAL_FISH) {
            return EntityType.TROPICAL_FISH;
        }
        if (item == Items.PUFFERFISH) {
            return EntityType.PUFFERFISH;
        }
        if (item == ModItems.RAW_ATLANTIC_COD) {
            return ModEntityTypes.ATLANTIC_COD;
        }
        if (item == ModItems.RAW_ATLANTIC_SALMON) {
            return ModEntityTypes.ATLANTIC_SALMON;
        }
        return null;
    }

    public static boolean isSupportedFishingFish(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.COD
                || item == Items.SALMON
                || item == Items.TROPICAL_FISH
                || item == Items.PUFFERFISH
                || item == ModItems.RAW_ATLANTIC_COD
                || item == ModItems.RAW_ATLANTIC_SALMON;
    }

    @Nullable
    private static Item matchingAtlanticItem(ItemStack stack) {
        if (stack.is(Items.COD)) {
            return ModItems.RAW_ATLANTIC_COD;
        }
        if (stack.is(Items.SALMON)) {
            return ModItems.RAW_ATLANTIC_SALMON;
        }
        return null;
    }

    private static ItemStack copyWithItem(ItemStack original, Item item) {
        ItemStack result = new ItemStack(item, original.getCount());
        if (original.hasTag()) {
            result.setTag(original.getTag().copy());
        }
        return result;
    }
}
