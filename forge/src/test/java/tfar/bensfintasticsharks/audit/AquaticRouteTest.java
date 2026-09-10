package tfar.bensfintasticsharks.audit;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;
import tfar.bensfintasticsharks.entity.AquaticRoute;

import static org.junit.jupiter.api.Assertions.*;

class AquaticRouteTest {

    @Test
    void matchingHeightDoesNotCompleteTheDestination() {
        AquaticRoute route = new AquaticRoute(new Vec3(0, 4, 0), 0.5, null);
        assertFalse(route.arrived(new Vec3(5, 4, 0)));
        assertTrue(route.arrived(new Vec3(0.1, 4, 0.1)));
    }

    @Test
    void steepAscentHasOneClearanceLegThenTheOriginalDestination() {
        Vec3 goal = new Vec3(0, 4, 0);
        Vec3 clearance = AquaticRoute.clearancePoint(Vec3.ZERO, goal, 0, 45, 1);
        assertNotNull(clearance);
        assertTrue(clearance.z >= 5);
        assertEquals(0, clearance.y);
        AquaticRoute route = new AquaticRoute(goal, 0.5, clearance);
        assertEquals(clearance, route.target(Vec3.ZERO));
        assertEquals(goal, route.target(clearance));
        assertFalse(route.arrived(clearance));
        assertEquals(goal, route.target(Vec3.ZERO));
        assertTrue(route.arrived(goal));
    }

    @Test
    void ordinaryDiagonalTravelNeedsNoClearanceLeg() {
        assertNull(AquaticRoute.clearancePoint(Vec3.ZERO, new Vec3(10, 2, 0), 0, 45, 1));
    }

    @Test
    void bodyClearanceDoesNotEraseSmallButSteepDepthChanges() {
        assertNotNull(AquaticRoute.clearancePoint(Vec3.ZERO, new Vec3(0, -3, 0), 0, 60, 5));
        assertNull(AquaticRoute.clearancePoint(Vec3.ZERO, new Vec3(4, 0.5, 0), 0, 45, 5));
    }
}
