package tfar.bensfintasticsharks.gore;

/** Resolves the session tuning into the amount and spread for one hit event. */
public record GoreImpactProfile(int particleCount, float particleScale, int particleLifetime,
                                float woundScale, int woundLifetime, int remainsCount,
                                float remainsScale, int remainsLifetime, int mistCount,
                                double mistSpreadRadius, double mistSpeed) {
    private static final int MAX_PARTICLES = 64;
    private static final int MAX_PARTICLE_LIFETIME = 1_200;

    public static GoreImpactProfile forKind(GoreImpactKind kind, GoreDebugSettings.Settings tuning) {
        return switch (kind) {
            case BITE -> resolve(tuning, 0.25D, 0.65D, 0.75D,
                    0.25D, 0.08D, 0.025D, 0.8D, 0.8D, false);
            case THRASH -> resolve(tuning, 0.75D, 1.0D, 1.25D,
                    0.75D, 0.35D, 0.04D, 1.0D, 1.25D, false);
            case THRASH_RELEASE -> resolve(tuning, 2.0D, 1.25D, 2.0D,
                    2.0D, 0.7D, 0.07D, 1.35D, 2.0D, false);
            case FATAL -> resolve(tuning, 3.0D, 1.35D, 4.0D,
                    3.0D, 1.0D, 0.11D, 1.0D, 1.0D, true);
        };
    }

    public GoreImpactProfile forTarget(float width, float height) {
        double size = targetScale(width, height);
        return new GoreImpactProfile(particleCount, (float) (particleScale * size), particleLifetime,
                (float) (woundScale * size), woundLifetime, remainsCount,
                (float) (remainsScale * size), remainsLifetime, mistCount,
                mistSpreadRadius * size, mistSpeed * Math.sqrt(size));
    }

    public static double targetScale(float width, float height) {
        if (!Float.isFinite(width) || !Float.isFinite(height) || width <= 0 || height <= 0) return 1.0D;
        return Math.max(0.15D, Math.min(3.0D, Math.cbrt(width * (double) width * height / (0.6D * 0.6D * 1.8D))));
    }

    private static GoreImpactProfile resolve(GoreDebugSettings.Settings tuning,
                                             double particleCountMultiplier, double scaleMultiplier,
                                             double lifetimeMultiplier, double mistCountMultiplier,
                                             double mistSpreadRadius, double mistSpeed,
                                             double woundScaleMultiplier, double woundLifetimeMultiplier,
                                             boolean fatal) {
        return new GoreImpactProfile(
                scaledCount(tuning.particleCount(), particleCountMultiplier, MAX_PARTICLES),
                scaledScale(tuning.particleScale(), scaleMultiplier),
                scaledLifetime(tuning.particleLifetime(), lifetimeMultiplier),
                scaledScale(tuning.woundScale(), woundScaleMultiplier),
                scaledLifetime(tuning.woundLifetime(), woundLifetimeMultiplier),
                fatal ? tuning.remainsCount() : 0,
                tuning.remainsScale(),
                tuning.remainsLifetime(),
                scaledCount(tuning.particleCount(), mistCountMultiplier, MAX_PARTICLES),
                mistSpreadRadius,
                mistSpeed);
    }

    private static int scaledCount(int count, double multiplier, int maximum) {
        if (count == 0) return 0;
        return Math.min(maximum, Math.max(1, (int) Math.ceil(count * multiplier)));
    }

    private static float scaledScale(float scale, double multiplier) {
        return (float) Math.min(4.0D, Math.max(0.1D, scale * multiplier));
    }

    private static int scaledLifetime(int lifetime, double multiplier) {
        return Math.min(MAX_PARTICLE_LIFETIME, Math.max(1, (int) Math.ceil(lifetime * multiplier)));
    }
}
