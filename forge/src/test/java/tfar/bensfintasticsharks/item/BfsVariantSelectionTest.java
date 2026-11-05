package tfar.bensfintasticsharks.item;

import net.minecraft.util.RandomSource;
import org.junit.jupiter.api.Test;
import tfar.bensfintasticsharks.entity.BfsVariantHolder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BfsVariantSelectionTest {
    @Test
    void randomIsTheFinalEggChoiceAndCyclesBackToTheFirstManualVariant() {
        assertEquals(1, BfsSpawnEggItem.nextVariantSelection(0, 3));
        assertEquals(2, BfsSpawnEggItem.nextVariantSelection(1, 3));
        assertEquals(BfsSpawnEggItem.RANDOM_VARIANT, BfsSpawnEggItem.nextVariantSelection(2, 3));
        assertEquals(0, BfsSpawnEggItem.nextVariantSelection(BfsSpawnEggItem.RANDOM_VARIANT, 3));
        assertEquals(0, BfsSpawnEggItem.nextVariantSelection(0, 0));
    }

    @Test
    void randomVariantSelectionExcludesRareChoicesAndSamplesRemainingChoicesUniformly() {
        Set<Integer> common = Set.of(1, 4);
        BfsVariantHolder holder = new BfsVariantHolder() {
            @Override
            public int bfsVariantCount() {
                return 5;
            }

            @Override
            public void setBfsVariantId(int id) {
            }

            @Override
            public boolean isCommonBfsVariant(int id) {
                return common.contains(id);
            }
        };

        RandomSource random = RandomSource.create(0xB5F5L);
        int oneCount = 0;
        int fourCount = 0;
        for (int i = 0; i < 20_000; i++) {
            int selected = holder.randomCommonBfsVariantId(random);
            assertTrue(common.contains(selected), "Random must never choose rare variant " + selected);
            if (selected == 1) oneCount++;
            if (selected == 4) fourCount++;
        }
        assertTrue(Math.abs(oneCount - fourCount) < 500,
                "eligible variants should be sampled evenly: " + oneCount + " vs " + fourCount);
    }
}
