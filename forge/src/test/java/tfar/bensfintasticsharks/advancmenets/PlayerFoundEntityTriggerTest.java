package tfar.bensfintasticsharks.advancmenets;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerFoundEntityTriggerTest {

    @Test
    void gazeMustRemainOnTheSameEntityForFortyConsecutiveTicks() {
        assertEquals(1, PlayerFoundEntityTrigger.nextDwellTicks(0, false));
        assertEquals(39, PlayerFoundEntityTrigger.nextDwellTicks(38, true));
        assertEquals(40, PlayerFoundEntityTrigger.nextDwellTicks(39, true));
        assertEquals(1, PlayerFoundEntityTrigger.nextDwellTicks(39, false));
        assertEquals(40, PlayerFoundEntityTrigger.nextDwellTicks(40, true));
    }
}
