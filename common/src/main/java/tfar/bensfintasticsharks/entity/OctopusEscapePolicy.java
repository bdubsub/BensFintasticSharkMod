package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.Optional;

/** Selects a short, water-only retreat without forcing an octopus through a wall. */
public final class OctopusEscapePolicy {
    public static final int JET_DURATION_TICKS = 20;
    public static final int EMISSION_COOLDOWN_TICKS = 1_200;
    public static final double RETREAT_DISTANCE = 4.0;
    public static final double MAX_JET_SPEED = 0.32;
    public static final double JET_ACCELERATION = 0.045;

    private OctopusEscapePolicy() {}

    public record Route(Vec3 target, Vec3 direction) {}

    /**
     * Tests a bounded fan of away-biased routes. A route is valid only when every sample stays
     * in water and the entity's translated collision box remains clear.
     */
    public static Optional<Route> findRoute(Entity octopus, Entity threat) {
        Vec3 away = octopus.position().subtract(threat.position());
        Vec3 horizontal = new Vec3(away.x, 0.0, away.z);
        if (horizontal.lengthSqr() < 1.0e-6) {
            horizontal = octopus.getLookAngle();
            horizontal = new Vec3(horizontal.x, 0.0, horizontal.z);
        }
        if (horizontal.lengthSqr() < 1.0e-6) horizontal = new Vec3(1.0, 0.0, 0.0);
        horizontal = horizontal.normalize();
        double vertical = Math.max(-0.75, Math.min(0.75, away.y * 0.2));
        double[] turns = {0.0, Math.PI / 6.0, -Math.PI / 6.0, Math.PI / 3.0,
                -Math.PI / 3.0, Math.PI / 2.0, -Math.PI / 2.0};
        for (double turn : turns) {
            double cos = Math.cos(turn);
            double sin = Math.sin(turn);
            Vec3 direction = new Vec3(
                    horizontal.x * cos - horizontal.z * sin,
                    vertical / RETREAT_DISTANCE,
                    horizontal.x * sin + horizontal.z * cos).normalize();
            Vec3 target = octopus.position().add(direction.scale(RETREAT_DISTANCE));
            if (isSafeRoute(octopus, target)) return Optional.of(new Route(target, direction));
        }
        return Optional.empty();
    }

    public static boolean isSafeRoute(Entity entity, Vec3 target) {
        Level level = entity.level();
        Vec3 start = entity.position();
        Vec3 delta = target.subtract(start);
        int samples = 8;
        AABB startBox = entity.getBoundingBox();
        for (int i = 1; i <= samples; i++) {
            double fraction = (double) i / samples;
            Vec3 sample = start.add(delta.scale(fraction));
            BlockPos fluidPos = BlockPos.containing(sample);
            if (!level.getFluidState(fluidPos).is(FluidTags.WATER)) return false;
            AABB translated = startBox.move(delta.scale(fraction));
            if (!level.noCollision(entity, translated)) return false;
        }
        return true;
    }

    /** Applies one bounded jet acceleration step while preserving an existing route component. */
    public static Vec3 applyJet(Vec3 current, Vec3 direction) {
        Vec3 next = current.add(direction.scale(JET_ACCELERATION));
        double speed = next.length();
        return speed <= MAX_JET_SPEED ? next : next.scale(MAX_JET_SPEED / speed);
    }
}
