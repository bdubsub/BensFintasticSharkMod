package tfar.bensfintasticsharks.entity;

final class TigerBiteTiming {
    private static final int AUTHORED_IMPACT_TICKS = 5;

    private TigerBiteTiming() {
    }

    static int impactDelayTicks() {
        return AUTHORED_IMPACT_TICKS;
    }
}
