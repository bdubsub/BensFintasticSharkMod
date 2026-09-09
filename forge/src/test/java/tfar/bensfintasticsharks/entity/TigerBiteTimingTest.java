package tfar.bensfintasticsharks.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TigerBiteTimingTest {
    @Test
    void biteDamageUsesAuthoredJawPeak() {
        assertEquals(5, TigerBiteTiming.impactDelayTicks());
    }
}
