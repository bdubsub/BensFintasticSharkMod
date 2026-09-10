package tfar.bensfintasticsharks.entity;

import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OctopusEscapePolicyTest {
    @Test
    void jetAccelerationIsFiniteAndBounded() {
        Vec3 direction = new Vec3(1.0, 0.2, 0.0).normalize();
        Vec3 velocity = Vec3.ZERO;
        for (int tick = 0; tick < OctopusEscapePolicy.JET_DURATION_TICKS; tick++) {
            velocity = OctopusEscapePolicy.applyJet(velocity, direction);
            assertTrue(velocity.length() <= OctopusEscapePolicy.MAX_JET_SPEED + 1.0e-9);
        }
        assertTrue(velocity.dot(direction) > 0.0);
    }

    @Test
    void escapeEnvelopeUsesFiniteActionAndCooldown() {
        assertEquals(20, OctopusEscapePolicy.JET_DURATION_TICKS);
        assertEquals(1_200, OctopusEscapePolicy.EMISSION_COOLDOWN_TICKS);
        assertEquals(4.0, OctopusEscapePolicy.RETREAT_DISTANCE);
    }
}
