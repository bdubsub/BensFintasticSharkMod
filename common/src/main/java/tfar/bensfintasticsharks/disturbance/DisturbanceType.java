package tfar.bensfintasticsharks.disturbance;

/**
 * Platform-neutral disturbance category used by {@code AbstractSharkEntity} and
 * platform-side disturbance handlers. The Forge event class
 * {@code WaterDisturbanceEvent.Type} maps 1:1 to this.
 */
public enum DisturbanceType {
    /** Splashes, sprint-swim, arrows — minor noise. */
    LIGHT,
    /** Attacks underwater, big fall splash, broken blocks — clear signal. */
    HEAVY,
    /** A living entity bled in the water — sharks converge. */
    BLOOD
}
