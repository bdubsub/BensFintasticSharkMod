package tfar.bensfintasticsharks.entity;

final class OceanicBiteTiming {
    private static final int AUTHORED_IMPACT_TICKS = 5;

    private OceanicBiteTiming() {
    }

    static int impactDelayTicks() {
        return AUTHORED_IMPACT_TICKS;
    }
}
