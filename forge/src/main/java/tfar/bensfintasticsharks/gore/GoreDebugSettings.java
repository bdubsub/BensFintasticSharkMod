package tfar.bensfintasticsharks.gore;

/** Bounded server-session tuning for temporary gore particles. */
public final class GoreDebugSettings {
    private static final Settings DEFAULTS = new Settings(8, 0.8F, 24, 0.75F, 80, 2, 1.0F, 120);
    private static volatile Settings current = DEFAULTS;

    private GoreDebugSettings() {
    }

    public static Settings current() {
        return current;
    }

    public static void reset() {
        current = DEFAULTS;
    }

    public static boolean set(String field, double value) {
        Settings old = current;
        if (!Double.isFinite(value)) return false;
        switch (field) {
            case "particle_count" -> {
                if (value != Math.rint(value) || value < 0 || value > 64) return false;
                current = new Settings((int) value, old.particleScale, old.particleLifetime,
                        old.woundScale, old.woundLifetime, old.remainsCount, old.remainsScale,
                        old.remainsLifetime);
            }
            case "particle_scale" -> {
                if (value < 0.1 || value > 4.0) return false;
                current = new Settings(old.particleCount, (float) value, old.particleLifetime,
                        old.woundScale, old.woundLifetime, old.remainsCount, old.remainsScale,
                        old.remainsLifetime);
            }
            case "particle_lifetime" -> {
                if (value != Math.rint(value) || value < 1 || value > 1200) return false;
                current = new Settings(old.particleCount, old.particleScale, (int) value,
                        old.woundScale, old.woundLifetime, old.remainsCount, old.remainsScale,
                        old.remainsLifetime);
            }
            case "wound_scale" -> {
                if (value < 0.1 || value > 4.0) return false;
                current = new Settings(old.particleCount, old.particleScale, old.particleLifetime,
                        (float) value, old.woundLifetime, old.remainsCount, old.remainsScale,
                        old.remainsLifetime);
            }
            case "wound_lifetime" -> {
                if (value != Math.rint(value) || value < 1 || value > 1200) return false;
                current = new Settings(old.particleCount, old.particleScale, old.particleLifetime,
                        old.woundScale, (int) value, old.remainsCount, old.remainsScale,
                        old.remainsLifetime);
            }
            case "remains_count" -> {
                if (value != Math.rint(value) || value < 0 || value > 32) return false;
                current = new Settings(old.particleCount, old.particleScale, old.particleLifetime,
                        old.woundScale, old.woundLifetime, (int) value, old.remainsScale,
                        old.remainsLifetime);
            }
            case "remains_scale" -> {
                if (value < 0.1 || value > 4.0) return false;
                current = new Settings(old.particleCount, old.particleScale, old.particleLifetime,
                        old.woundScale, old.woundLifetime, old.remainsCount, (float) value,
                        old.remainsLifetime);
            }
            case "remains_lifetime" -> {
                if (value != Math.rint(value) || value < 1 || value > 6000) return false;
                current = new Settings(old.particleCount, old.particleScale, old.particleLifetime,
                        old.woundScale, old.woundLifetime, old.remainsCount, old.remainsScale,
                        (int) value);
            }
            default -> { return false; }
        }
        return true;
    }

    public record Settings(int particleCount, float particleScale, int particleLifetime,
                           float woundScale, int woundLifetime, int remainsCount,
                           float remainsScale, int remainsLifetime) {
        public String describe() {
            return "particles=" + particleCount + " x" + particleScale + " " + particleLifetime + "t, "
                    + "wound=x" + woundScale + " " + woundLifetime + "t, remains=" + remainsCount
                    + " x" + remainsScale + " " + remainsLifetime + "t";
        }
    }
}
