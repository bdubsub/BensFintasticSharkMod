package tfar.bensfintasticsharks.entity;

/**
 * Pure presentation decisions shared by the Forge fish adapters and their
 * deterministic boundary tests.
 */
final class FishPresentationState {
    enum State {
        STOP,
        FLOP,
        FAST_SWIM,
        SWIM,
        IDLE,
        SPIN
    }

    private FishPresentationState() {
    }

    static State cod(boolean deadOrDying, boolean inWater, boolean fastSwim, boolean moving) {
        if (deadOrDying) return State.STOP;
        if (!inWater) return State.FLOP;
        if (fastSwim) return State.FAST_SWIM;
        return moving ? State.SWIM : State.IDLE;
    }

    static State salmon(boolean deadOrDying, boolean namedSpin, boolean inWater,
                       boolean fastSwim, boolean moving) {
        if (deadOrDying) return State.STOP;
        if (namedSpin) return State.SPIN;
        if (!inWater) return State.FLOP;
        if (fastSwim) return State.FAST_SWIM;
        return moving ? State.SWIM : State.IDLE;
    }
}
