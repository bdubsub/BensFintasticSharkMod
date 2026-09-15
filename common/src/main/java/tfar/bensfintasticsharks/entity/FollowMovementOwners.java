package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** Session scoped ownership shared with the ordinary aquatic controllers. */
public final class FollowMovementOwners {
    private static final Map<Mob, UUID> OWNERS = new WeakHashMap<>();

    private FollowMovementOwners() {}

    public static void claim(Mob mob, UUID nonce) {
        OWNERS.put(mob, nonce);
    }

    public static void release(Mob mob, UUID nonce) {
        OWNERS.remove(mob, nonce);
    }

    public static boolean selected(Mob mob) {
        return !mob.level().isClientSide && OWNERS.containsKey(mob);
    }

    public static boolean needsSafety(Mob mob) {
        if (mob.isOnFire() || mob.isFreezing()
                || mob.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING)
                && mob.getBrain().getMemory(MemoryModuleType.IS_PANICKING).orElse(false)) return true;
        return mob instanceof SmartWaterAnimal<?> animal && animal.getBfsBehaviorProfile() != null
                && animal.getBfsBehaviorProfile().needsSurface() && animal.getAirSupply() < 120;
    }

    public static boolean suppressOrdinary(Mob mob) {
        return selected(mob) && !needsSafety(mob);
    }
}
