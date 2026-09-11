package tfar.bensfintasticsharks.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FishPresentationStateTest {
    @Test
    void codMatrixUsesDeadFlopFastSwimAndIdlePriority() {
        assertEquals(FishPresentationState.State.STOP,
                FishPresentationState.cod(true, false, true, true));
        assertEquals(FishPresentationState.State.FLOP,
                FishPresentationState.cod(false, false, true, true));
        assertEquals(FishPresentationState.State.FAST_SWIM,
                FishPresentationState.cod(false, true, true, true));
        assertEquals(FishPresentationState.State.SWIM,
                FishPresentationState.cod(false, true, false, true));
        assertEquals(FishPresentationState.State.IDLE,
                FishPresentationState.cod(false, true, false, false));
    }

    @Test
    void salmonMatrixKeepsExactSpinAboveEveryLiveState() {
        assertEquals(FishPresentationState.State.STOP,
                FishPresentationState.salmon(true, true, false, true, true));
        assertEquals(FishPresentationState.State.SPIN,
                FishPresentationState.salmon(false, true, false, true, true));
        assertEquals(FishPresentationState.State.FLOP,
                FishPresentationState.salmon(false, false, false, true, true));
        assertEquals(FishPresentationState.State.FAST_SWIM,
                FishPresentationState.salmon(false, false, true, true, true));
        assertEquals(FishPresentationState.State.SWIM,
                FishPresentationState.salmon(false, false, true, false, true));
        assertEquals(FishPresentationState.State.IDLE,
                FishPresentationState.salmon(false, false, true, false, false));
    }

    @Test
    void thresholdFlagsAreConsumedWithoutAnImplicitBoundaryShift() {
        assertEquals(FishPresentationState.State.SWIM,
                FishPresentationState.cod(false, true, false, true));
        assertEquals(FishPresentationState.State.FAST_SWIM,
                FishPresentationState.cod(false, true, true, true));
    }
}
