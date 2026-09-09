package tfar.bensfintasticsharks.entity;

final class OceanicPresentationState {
    private OceanicPresentationState() {
    }

    static boolean hasActiveGrab(int grabTimer, boolean hasPassengers) {
        return grabTimer > 0 && hasPassengers;
    }

}
