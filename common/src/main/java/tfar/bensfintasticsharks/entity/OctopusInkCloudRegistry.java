package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Optional;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** Bounded server-only ownership for transient octopus ink clouds. */
public final class OctopusInkCloudRegistry {
    public static final int MAX_RADIUS_BLOCKS = 2;
    public static final int LIFETIME_TICKS = 80;
    public static final int MAX_CLOUDS_PER_COLUMN = 8;
    public static final int MAX_PARTICLE_BIRTHS = 32;

    private static final Map<ServerLevel, Map<UUID, Cloud>> CLOUDS = new WeakHashMap<>();
    private static final Map<ServerLevel, Integer> NEXT_EVENT = new WeakHashMap<>();

    private OctopusInkCloudRegistry() {}

    public static boolean tryCreate(ServerLevel level, Entity source) {
        Map<UUID, Cloud> clouds = CLOUDS.computeIfAbsent(level, ignored -> new HashMap<>());
        expire(level, source.level().getGameTime());
        if (clouds.containsKey(source.getUUID())) return false;
        BlockPos origin = source.blockPosition();
        long column = columnKey(origin);
        long activeInColumn = clouds.values().stream()
                .filter(cloud -> columnKey(cloud.origin()) == column)
                .count();
        if (activeInColumn >= MAX_CLOUDS_PER_COLUMN) return false;
        int event = NEXT_EVENT.merge(level, 1, (previous, ignored) -> previous == Integer.MAX_VALUE ? 1 : previous + 1);
        clouds.put(source.getUUID(), new Cloud(source.getUUID(), origin, level.getGameTime(),
                level.getGameTime() + LIFETIME_TICKS, event));
        return true;
    }

    public static boolean active(ServerLevel level, UUID source) {
        Map<UUID, Cloud> clouds = CLOUDS.get(level);
        return clouds != null && clouds.containsKey(source);
    }

    public static Optional<Snapshot> snapshot(ServerLevel level, UUID source) {
        Map<UUID, Cloud> clouds = CLOUDS.get(level);
        if (clouds == null) return Optional.empty();
        expire(level, level.getGameTime());
        Cloud cloud = clouds.get(source);
        return cloud == null ? Optional.empty() : Optional.of(cloud.snapshot());
    }

    /**
     * Returns true only when a short ray to an observer crosses this cloud before the observer
     * and no solid block separates the cloud from the observer. The caller remains responsible
     * for nonvisual cues and close contact.
     */
    public static boolean obscuresVisualRay(ServerLevel level, UUID source, Vec3 observer) {
        Map<UUID, Cloud> clouds = CLOUDS.get(level);
        if (clouds == null) return false;
        expire(level, level.getGameTime());
        Cloud cloud = clouds.get(source);
        if (cloud == null) return false;
        Vec3 center = Vec3.atCenterOf(cloud.origin());
        Vec3 toObserver = observer.subtract(center);
        if (toObserver.lengthSqr() <= MAX_RADIUS_BLOCKS * MAX_RADIUS_BLOCKS) return false;
        Vec3 direction = toObserver.normalize();
        Vec3 cloudEdge = center.add(direction.scale(MAX_RADIUS_BLOCKS));
        if (level.clip(new net.minecraft.world.level.ClipContext(center, observer,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE, null)).getType()
                == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            var hit = level.clip(new net.minecraft.world.level.ClipContext(center, observer,
                    net.minecraft.world.level.ClipContext.Block.COLLIDER,
                    net.minecraft.world.level.ClipContext.Fluid.NONE, null));
            if (center.distanceToSqr(hit.getLocation()) < center.distanceToSqr(cloudEdge)) return false;
        }
        return level.getFluidState(cloud.origin()).is(net.minecraft.tags.FluidTags.WATER)
                && level.getFluidState(BlockPos.containing(cloudEdge)).is(net.minecraft.tags.FluidTags.WATER);
    }

    public static void tick(ServerLevel level, UUID source) {
        Map<UUID, Cloud> clouds = CLOUDS.get(level);
        if (clouds == null) return;
        expire(level, level.getGameTime());
        if (!clouds.containsKey(source) && clouds.isEmpty()) CLOUDS.remove(level);
    }

    public static void remove(ServerLevel level, UUID source) {
        Map<UUID, Cloud> clouds = CLOUDS.get(level);
        if (clouds == null) return;
        clouds.remove(source);
        if (clouds.isEmpty()) {
            CLOUDS.remove(level);
            NEXT_EVENT.remove(level);
        }
    }

    private static void expire(ServerLevel level, long gameTime) {
        Map<UUID, Cloud> clouds = CLOUDS.get(level);
        if (clouds == null) return;
        clouds.values().removeIf(cloud -> cloud.expiresAt() <= gameTime);
    }

    private static long columnKey(BlockPos pos) {
        return (((long) (pos.getX() >> 4)) << 32) ^ ((long) (pos.getZ() >> 4) & 0xffffffffL);
    }

    public record Snapshot(UUID source, BlockPos origin, long createdAt, long expiresAt, int event) {}

    private record Cloud(UUID source, BlockPos origin, long createdAt, long expiresAt, int event) {
        private Snapshot snapshot() {
            return new Snapshot(source, origin, createdAt, expiresAt, event);
        }
    }
}
