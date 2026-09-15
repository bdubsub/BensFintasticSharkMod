package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Mob;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/** Server side ownership for the low priority occupied boat movement intent. */
public final class BoatMovementOwners {
    private static final Map<Mob, Lease> LEASES = new WeakHashMap<>();

    private BoatMovementOwners() {}

    public static synchronized void claim(Mob mob, UUID boat, long expiresAt) {
        if (!mob.level().isClientSide) LEASES.put(mob, new Lease(boat, expiresAt));
    }

    public static synchronized void release(Mob mob, UUID boat) {
        Lease lease = LEASES.get(mob);
        if (lease != null && (boat == null || lease.boat().equals(boat))) LEASES.remove(mob);
    }

    public static synchronized boolean active(Mob mob) {
        Lease lease = LEASES.get(mob);
        if (lease == null || mob.level().isClientSide || mob.level().getGameTime() >= lease.expiresAt()) {
            if (lease != null) LEASES.remove(mob);
            return false;
        }
        return true;
    }

    public static synchronized UUID boat(Mob mob) {
        return active(mob) ? LEASES.get(mob).boat() : null;
    }

    public static boolean suppressOrdinary(Mob mob) {
        return active(mob) && !FollowMovementOwners.needsSafety(mob);
    }

    private record Lease(UUID boat, long expiresAt) {}
}
