package tfar.bensfintasticsharks.spawn;

import net.minecraft.world.entity.MobSpawnType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void replacesOnlyApprovedNewEntitySources() {
        assertTrue(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.SPAWN_EGG));
        assertTrue(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.COMMAND));
        assertTrue(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.BUCKET));
        assertTrue(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.DISPENSER));
        assertTrue(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.SPAWNER));
        assertTrue(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.STRUCTURE));

        assertFalse(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.NATURAL));
        assertFalse(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.CHUNK_GENERATION));
        assertFalse(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.BREEDING));
        assertFalse(VanillaFishReplacementPolicy.replacesEntityJoinSource(MobSpawnType.EVENT));
        assertFalse(VanillaFishReplacementPolicy.replacesEntityJoinSource(null));
    }
}
