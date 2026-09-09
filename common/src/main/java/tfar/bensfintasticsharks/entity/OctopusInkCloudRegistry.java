package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** Bounded server-only ownership for transient octopus ink clouds. */
public final class OctopusInkCloudRegistry {
    public static final int MAX_RADIUS_BLOCKS = 2;
    public static final int LIFETIME_TICKS = 80;
    public static final int MAX_CLOUDS_PER_COLUMN = 8;

    private static final Map<ServerLevel, Map<UUID, Cloud>> CLOUDS = new WeakHashMap<>();

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
        clouds.put(source.getUUID(), new Cloud(origin, level.getGameTime() + LIFETIME_TICKS));
        return true;
    }

    public static boolean active(ServerLevel level, UUID source) {
        Map<UUID, Cloud> clouds = CLOUDS.get(level);
        return clouds != null && clouds.containsKey(source);
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
        if (clouds.isEmpty()) CLOUDS.remove(level);
    }

    private static void expire(ServerLevel level, long gameTime) {
        Map<UUID, Cloud> clouds = CLOUDS.get(level);
        if (clouds == null) return;
        clouds.values().removeIf(cloud -> cloud.expiresAt() <= gameTime);
    }

    private static long columnKey(BlockPos pos) {
        return (((long) (pos.getX() >> 4)) << 32) ^ ((long) (pos.getZ() >> 4) & 0xffffffffL);
    }

    private record Cloud(BlockPos origin, long expiresAt) {}
}
