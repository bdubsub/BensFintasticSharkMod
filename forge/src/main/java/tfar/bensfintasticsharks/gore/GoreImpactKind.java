package tfar.bensfintasticsharks.gore;

/** Distinguishes a light bite cue from grab, release, and fatal effects. */
public enum GoreImpactKind {
    BITE,
    THRASH,
    THRASH_RELEASE,
    FATAL;

    public static GoreImpactKind forDamage(boolean fatal, boolean victimHeldByAttacker) {
        if (fatal) return FATAL;
        return victimHeldByAttacker ? THRASH : BITE;
    }

    public static GoreImpactKind fromWireId(int id) {
        GoreImpactKind[] values = values();
        return id >= 0 && id < values.length ? values[id] : null;
    }

    public int wireId() {
        return ordinal();
    }

    public boolean isFatal() {
        return this == FATAL;
    }
}
