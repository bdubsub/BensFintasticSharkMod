package tfar.bensfintasticsharks.disturbance;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GreatWhiteBoatInterestTest {
    @Test
    void behindOffsetKeepsBothCollisionMarginsAndClearance() {
        assertEquals(4.5D, GreatWhiteBoatInterest.behindOffset(3.0D, 2.0D), 1.0e-9);
    }

    @Test
    void behindTargetOpposesTravelDirectionAndPreservesDepth() {
        Vec3 target = GreatWhiteBoatInterest.behindTarget(new Vec3(10, 8, 10), new Vec3(1, 0, 0),
                4.5D, 3.25D);
        assertEquals(5.5D, target.x, 1.0e-9);
        assertEquals(3.25D, target.y, 1.0e-9);
        assertEquals(10.0D, target.z, 1.0e-9);
    }

    @Test
    void predictionIsFiniteAndCappedAtTwentyTicks() {
        assertEquals(1, GreatWhiteBoatInterest.predictionTicks(Vec3.ZERO, new Vec3(1, 0, 0), 2.0D));
        assertEquals(20, GreatWhiteBoatInterest.predictionTicks(Vec3.ZERO, new Vec3(200, 0, 0), 0.1D));
        assertTrue(GreatWhiteBoatInterest.predictionTicks(Vec3.ZERO, new Vec3(5, 0, 0), 1.0D) <= 20);
    }
}
