package tfar.bensfintasticsharks.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BfsClientRenderDiagnosticsTest {
    @Test
    void selectsDaylightMarkingsAtTheBrightnessBoundary() {
        assertEquals("glow", BfsClientDebugManager.selectedLayer(true, 7));
        assertEquals("marking", BfsClientDebugManager.selectedLayer(true, 8));
        assertEquals("base", BfsClientDebugManager.selectedLayer(false, 15));
    }
}
