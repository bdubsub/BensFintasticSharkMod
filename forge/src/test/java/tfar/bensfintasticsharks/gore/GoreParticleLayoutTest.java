package tfar.bensfintasticsharks.gore;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoreParticleLayoutTest {
    private static final double GAP = 0.04D;

    @Test
    void zeroCountProducesNoParticlesAndSingleSatelliteClearsCenter() {
        assertTrue(GoreParticleLayout.separatedOffsets(0, 0.8D, 0.14D, GAP, 0.0D).isEmpty());

        List<GoreParticleLayout.Offset> offsets = GoreParticleLayout.separatedOffsets(1, 0.8D, 0.14D, GAP, 0.0D);
        assertEquals(1, offsets.size());
        assertTrue(Math.max(Math.abs(offsets.get(0).right()), Math.abs(offsets.get(0).up()))
                >= 0.8D + 0.14D + GAP);
    }

    @Test
    void maximumDropletCountKeepsEverySquareQuadSeparated() {
        int count = 63;
        double centerSize = 0.8D;
        double dropletSize = 0.144D;
        List<GoreParticleLayout.Offset> offsets = GoreParticleLayout.separatedOffsets(
                count, centerSize, dropletSize, GAP, 0.73D);

        assertEquals(count, offsets.size());
        for (GoreParticleLayout.Offset offset : offsets) {
            assertTrue(Math.max(Math.abs(offset.right()), Math.abs(offset.up()))
                            >= centerSize + dropletSize + GAP - 1.0e-9D,
                    "each droplet must clear the central splatter");
        }
        for (int first = 0; first < offsets.size(); first++) {
            for (int second = first + 1; second < offsets.size(); second++) {
                double deltaRight = offsets.get(first).right() - offsets.get(second).right();
                double deltaUp = offsets.get(first).up() - offsets.get(second).up();
                assertTrue(Math.max(Math.abs(deltaRight), Math.abs(deltaUp))
                                >= 2.0D * dropletSize + GAP - 1.0e-9D,
                        "droplet quads must not overlap each other");
            }
        }
    }

    @Test
    void aRingWithoutCenterStillSeparatesMultipleRemains() {
        List<GoreParticleLayout.Offset> offsets = GoreParticleLayout.separatedOffsets(4, 0.0D, 1.0D, GAP, 0.0D);

        assertEquals(4, offsets.size());
        for (int first = 0; first < offsets.size(); first++) {
            for (int second = first + 1; second < offsets.size(); second++) {
                double deltaRight = offsets.get(first).right() - offsets.get(second).right();
                double deltaUp = offsets.get(first).up() - offsets.get(second).up();
                assertTrue(Math.max(Math.abs(deltaRight), Math.abs(deltaUp)) >= 2.0D + GAP - 1.0e-9D);
            }
        }
    }

    @Test
    void rejectsNonFiniteOrNegativeLayoutInputs() {
        assertThrows(IllegalArgumentException.class,
                () -> GoreParticleLayout.separatedOffsets(-1, 0.8D, 0.1D, GAP, 0.0D));
        assertThrows(IllegalArgumentException.class,
                () -> GoreParticleLayout.separatedOffsets(1, Double.NaN, 0.1D, GAP, 0.0D));
        assertThrows(IllegalArgumentException.class,
                () -> GoreParticleLayout.separatedOffsets(1, 0.8D, -0.1D, GAP, 0.0D));
        assertThrows(IllegalArgumentException.class,
                () -> GoreParticleLayout.separatedOffsets(1, 0.8D, 0.1D, GAP, Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class,
                () -> GoreParticleLayout.separatedOffsets(1, 0.8D, 0.0D, 0.0D, 0.0D));
        assertThrows(IllegalArgumentException.class,
                () -> GoreParticleLayout.separatedOffsets(2, 0.0D, 0.0D, 0.0D, 0.0D));
    }
}
