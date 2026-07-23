package tfar.bensfintasticsharks.trade;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.util.RandomSource;
import tfar.bensfintasticsharks.init.ModItems;

import java.util.List;

/**
 * Canonical catalogue for BFS master-fisherman offers.
 *
 * <p>The Forge trade event uses {@link #WEIGHTED_LISTING} for naturally levelled
 * fishermen, while the showcase command reads {@link #CATALOGUE} directly to
 * guarantee a requested offer. Keeping both consumers here prevents their prices,
 * weights, and use limits from drifting apart.</p>
 */
public final class BfsFishermanTrades {

    public static final List<TradeDefinition> CATALOGUE = List.of(
            emeraldTrade("great_white_shark_tooth", ModItems.GREAT_WHITE_SHARK_TOOTH, 20, 12, 3),
            emeraldTrade("great_hammerhead_shark_tooth", ModItems.GREAT_HAMMERHEAD_SHARK_TOOTH, 20, 12, 3),
            emeraldTrade("common_thresher_shark_tooth", ModItems.COMMON_THRESHER_SHARK_TOOTH, 20, 12, 3),
            emeraldTrade("tiger_shark_tooth", ModItems.TIGER_SHARK_TOOTH, 20, 12, 3),
            emeraldTrade("shortfin_mako_shark_tooth", ModItems.SHORTFIN_MAKO_SHARK_TOOTH, 20, 12, 3),
            emeraldTrade("oceanic_whitetip_shark_tooth", ModItems.OCEANIC_WHITETIP_SHARK_TOOTH, 20, 12, 3),
            emeraldTrade("great_white_shark_skin", ModItems.GREAT_WHITE_SHARK_SKIN, 20, 16, 3),
            emeraldTrade("great_hammerhead_shark_skin", ModItems.GREAT_HAMMERHEAD_SHARK_SKIN, 20, 16, 3),
            emeraldTrade("common_thresher_shark_skin", ModItems.COMMON_THRESHER_SHARK_SKIN, 20, 16, 3),
            emeraldTrade("cartilage", ModItems.CARTILAGE, 20, 10, 4),
            emeraldTrade("shark_jaws", ModItems.SHARK_JAWS, 8, 48, 1),
            new TradeDefinition("megalodon_tooth", ModItems.MEGALODON_TOOTH, 3,
                    (trader, random) -> new MerchantOffer(
                            new ItemStack(Items.EMERALD_BLOCK, 64),
                            new ItemStack(ModItems.MEGALODON_TOOTH),
                            1, 50, 0.0F))
    );

    /** One weighted composite occupies a single slot in the master trade pool. */
    public static final VillagerTrades.ItemListing WEIGHTED_LISTING = new WeightedListing(CATALOGUE);

    private BfsFishermanTrades() {
    }

    private static TradeDefinition emeraldTrade(String id, Item result, int weight,
                                                 int emeraldCost, int maxUses) {
        return new TradeDefinition(id, result, weight,
                (trader, random) -> new MerchantOffer(
                        new ItemStack(Items.EMERALD, emeraldCost),
                        new ItemStack(result),
                        maxUses, 30, 0.05F));
    }

    public record TradeDefinition(String id, Item result, int weight,
                                  VillagerTrades.ItemListing listing) {
    }

    /** Always resolves the composite pool to exactly one BFS offer. */
    private record WeightedListing(List<TradeDefinition> entries) implements VillagerTrades.ItemListing {
        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            int totalWeight = 0;
            for (TradeDefinition entry : entries) {
                totalWeight += entry.weight();
            }

            int roll = random.nextInt(totalWeight);
            for (TradeDefinition entry : entries) {
                roll -= entry.weight();
                if (roll < 0) {
                    return entry.listing().getOffer(trader, random);
                }
            }

            return null; // Unreachable while the catalogue contains positive weights.
        }
    }
}
