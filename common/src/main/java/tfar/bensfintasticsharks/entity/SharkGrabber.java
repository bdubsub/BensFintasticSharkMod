package tfar.bensfintasticsharks.entity;

/**
 * Implemented by shark species that can grab a victim and thrash it
 * (Great White, Great Hammerhead, Shortfin Mako). The grab timer is a synced
 * entity-data value, so clients can read it too — the 0.18 client-side grab
 * effects (camera lock + red tint) key off the local player's vehicle
 * implementing this interface.
 */
public interface SharkGrabber {

    /** Ticks remaining in the current grab/thrash. 0 = not grabbing. */
    int getGrabTimer();
}
