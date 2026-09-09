package tfar.bensfintasticsharks.entity;

final class OceanicPresentationState {
    private static final int AUTHORED_BITE_IMPACT_TICKS = 5;

    private OceanicPresentationState() {
    }

    static boolean hasActiveGrab(int grabTimer, boolean hasPassengers) {
        return grabTimer > 0 && hasPassengers;
    }

    static int biteImpactDelayTicks() {
        return AUTHORED_BITE_IMPACT_TICKS;
    }
}
