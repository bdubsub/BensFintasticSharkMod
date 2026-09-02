package tfar.bensfintasticsharks.spawn;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class VanillaFishReplacementPolicyTest {

    @Test
    void replacesOnlyVanillaCodAndSalmon() {
        assertEquals(
                VanillaFishReplacementPolicy.Replacement.ATLANTIC_COD,
                VanillaFishReplacementPolicy.replacementFor("minecraft", "cod")
        );
        assertEquals(
                VanillaFishReplacementPolicy.Replacement.ATLANTIC_SALMON,
                VanillaFishReplacementPolicy.replacementFor("minecraft", "salmon")
        );

        assertNull(VanillaFishReplacementPolicy.replacementFor("minecraft", "tropical_fish"));
        assertNull(VanillaFishReplacementPolicy.replacementFor("minecraft", "pufferfish"));
        assertNull(VanillaFishReplacementPolicy.replacementFor("minecraft", "squid"));
        assertNull(VanillaFishReplacementPolicy.replacementFor("minecraft", "dolphin"));
        assertNull(VanillaFishReplacementPolicy.replacementFor("bensfintasticsharks", "atlantic_cod"));
        assertNull(VanillaFishReplacementPolicy.replacementFor("bensfintasticsharks", "atlantic_salmon"));
        assertNull(VanillaFishReplacementPolicy.replacementFor("another_mod", "cod"));
        assertNull(VanillaFishReplacementPolicy.replacementFor("another_mod", "salmon"));
    }
}
