package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

/** Server side fixture input used to exercise a real travel owner without assigning velocity. */
public final class MovementIntentOverrides {
    private static final Map<Entity, Vec3> OVERRIDES = new WeakHashMap<>();

    private MovementIntentOverrides() {
    }

    public static synchronized void set(Entity entity, Vec3 intent) {
        if (entity == null || intent == null) return;
        OVERRIDES.put(entity, intent);
    }

    public static synchronized void clear(Entity entity) {
        if (entity != null) OVERRIDES.remove(entity);
    }

    public static synchronized void clearAll() {
        OVERRIDES.clear();
    }

    public static synchronized boolean active(Entity entity) {
        return entity != null && OVERRIDES.containsKey(entity);
    }

    public static synchronized Vec3 resolve(Entity entity, Vec3 fallback) {
        Vec3 override = OVERRIDES.get(entity);
        return override == null ? fallback : override;
    }
}
