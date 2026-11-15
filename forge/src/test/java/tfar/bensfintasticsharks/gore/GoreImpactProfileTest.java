package tfar.bensfintasticsharks.gore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoreImpactProfileTest {
    private static final GoreDebugSettings.Settings DEFAULTS =
            new GoreDebugSettings.Settings(8, 0.8F, 24, 0.75F, 80, 2, 1.0F, 120);

    @Test
    void damageKindDistinguishesBitesThrashAndKills() {
        assertEquals(GoreImpactKind.BITE, GoreImpactKind.forDamage(false, false));
        assertEquals(GoreImpactKind.THRASH, GoreImpactKind.forDamage(false, true));
        assertEquals(GoreImpactKind.FATAL, GoreImpactKind.forDamage(true, true));
    }

    @Test
    void bloodIntensityIncreasesFromBiteThroughReleaseAndKill() {
        GoreImpactProfile bite = GoreImpactProfile.forKind(GoreImpactKind.BITE, DEFAULTS);
        GoreImpactProfile thrash = GoreImpactProfile.forKind(GoreImpactKind.THRASH, DEFAULTS);
        GoreImpactProfile release = GoreImpactProfile.forKind(GoreImpactKind.THRASH_RELEASE, DEFAULTS);
        GoreImpactProfile fatal = GoreImpactProfile.forKind(GoreImpactKind.FATAL, DEFAULTS);

        assertEquals(2, bite.particleCount());
        assertEquals(2, bite.mistCount());
        assertEquals(6, thrash.particleCount());
        assertEquals(16, release.particleCount());
        assertEquals(16, release.mistCount());
        assertEquals(24, fatal.particleCount());
        assertEquals(24, fatal.mistCount());
        assertEquals(96, fatal.particleLifetime());
        assertEquals(DEFAULTS.remainsCount(), fatal.remainsCount());
        assertEquals(0, release.remainsCount());
    }

    @Test
    void victimsScaleBloodSizeAndSpreadWithoutChangingHitIntensity() {
        GoreImpactProfile base = GoreImpactProfile.forKind(GoreImpactKind.THRASH_RELEASE, DEFAULTS);
        GoreImpactProfile salmon = base.forTarget(0.7F, 0.4F);
        GoreImpactProfile player = base.forTarget(0.6F, 1.8F);
        GoreImpactProfile largeMob = base.forTarget(2.0F, 2.0F);
        assertTrue(salmon.particleScale() < player.particleScale());
        assertTrue(player.particleScale() < largeMob.particleScale());
        assertTrue(salmon.mistSpreadRadius() < player.mistSpreadRadius());
        assertTrue(salmon.remainsScale() < player.remainsScale());
        assertEquals(base.particleCount(), salmon.particleCount());
        assertEquals(base.particleLifetime(), salmon.particleLifetime());
        assertEquals(base.particleScale(), player.particleScale(), 0.00001F);
        assertEquals(1.0D, GoreImpactProfile.targetScale(Float.NaN, 1));
        assertEquals(1.0D, GoreImpactProfile.targetScale(0, 1));
        assertEquals(0.15D, GoreImpactProfile.targetScale(0.001F, 0.001F));
        assertEquals(3.0D, GoreImpactProfile.targetScale(Float.MAX_VALUE, Float.MAX_VALUE));
    }

    @Test
    void zeroParticlesAndMaximumDebugValuesStayBounded() {
        GoreDebugSettings.Settings disabled =
                new GoreDebugSettings.Settings(0, 4.0F, 1_200, 4.0F, 1_200, 32, 4.0F, 6_000);
        GoreImpactProfile noBlood = GoreImpactProfile.forKind(GoreImpactKind.FATAL, disabled);
        assertEquals(0, noBlood.particleCount());
        assertEquals(0, noBlood.mistCount());

        GoreDebugSettings.Settings maximum =
                new GoreDebugSettings.Settings(64, 4.0F, 1_200, 4.0F, 1_200, 32, 4.0F, 6_000);
        GoreImpactProfile maximumFatal = GoreImpactProfile.forKind(GoreImpactKind.FATAL, maximum);
        assertEquals(64, maximumFatal.particleCount());
        assertEquals(64, maximumFatal.mistCount());
        assertEquals(4.0F, maximumFatal.particleScale());
        assertEquals(1_200, maximumFatal.particleLifetime());
        assertEquals(32, maximumFatal.remainsCount());
    }
}
