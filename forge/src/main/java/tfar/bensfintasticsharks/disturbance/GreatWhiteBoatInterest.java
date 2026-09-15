package tfar.bensfintasticsharks.disturbance;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.tslat.smartbrainlib.util.BrainUtils;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.entity.BoatMovementOwners;
import tfar.bensfintasticsharks.entity.FollowMovementOwners;
import tfar.bensfintasticsharks.entity.GreatWhiteSharkEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Great white occupied boat interest with a conservative, expiring movement lease. */
public final class GreatWhiteBoatInterest {
    private static final double MIN_MOTION = 0.02D;
    private static final double BEHIND_CLEARANCE = 2.0D;
    private static final long STATIONARY_EXPIRY = 100L;
    private static final long LEASE_TICKS = 25L;
    private static final double MAX_TURN_COSINE = Math.cos(Math.toRadians(75.0D));
    private static final Map<UUID, Lease> LEASES = new ConcurrentHashMap<>();

    public record Decision(boolean accepted, String reason, Vec3 target, int predictionTicks, double offset) {
        static Decision reject(String reason) {
            return new Decision(false, reason, Vec3.ZERO, 0, 0.0D);
        }
    }

    public Decision acquire(ServerLevel level, GreatWhiteSharkEntity shark, WaterDisturbanceEvent event) {
        if (event.getBoat() instanceof Boat boat && event.getRider() != null) {
            return acquire(level, shark, boat, event.getRider(), event.getBoatMovement(), level.getGameTime());
        }
        return Decision.reject("boat_missing_occupant");
    }

    private Decision acquire(ServerLevel level, GreatWhiteSharkEntity shark, Boat boat, Entity rider,
                             Vec3 motion, long tick) {
        if (!eligible(shark, boat, rider)) return Decision.reject(eligibilityReason(shark, boat, rider));
        Vec3 direction = horizontalDirection(motion);
        if (direction.lengthSqr() < MIN_MOTION * MIN_MOTION) return Decision.reject("stationary_boat");
        Lease previous = LEASES.get(shark.getUUID());
        if (previous != null && !previous.boat().equals(boat.getUUID())
                && boat.getUUID().toString().compareTo(previous.boat().toString()) >= 0) {
            return Decision.reject("boat_tie_break");
        }
        if (previous != null && previous.boat().equals(boat.getUUID())
                && previous.direction().lengthSqr() > 0.0D
                && previous.direction().dot(direction) < MAX_TURN_COSINE) {
            release(shark, "turn_exceeds_bound");
            return Decision.reject("turn_exceeds_bound");
        }
        double offset = behindOffset(boat.getBbWidth(), shark.getBbWidth());
        Vec3 target = behindTarget(boat.position(), direction, offset, shark.getY());
        if (!safeWaterDestination(level, shark, shark.position())) return Decision.reject("shark_not_submerged");
        if (!safeWaterDestination(level, shark, target)) return Decision.reject("unsafe_route");
        int predictionTicks = predictionTicks(shark.position(), target, shark.getSpeed());
        Lease lease = new Lease(boat.getUUID(), direction, boat.position(), tick, tick, target,
                predictionTicks, offset);
        LEASES.put(shark.getUUID(), lease);
        BoatMovementOwners.claim(shark, boat.getUUID(), tick + LEASE_TICKS);
        BrainUtils.setMemory(shark.getBrain(), MemoryModuleType.WALK_TARGET,
                new WalkTarget(target, 1.0F, 1));
        if (shark.getSharkState() == AbstractSharkEntity.SharkState.IDLE) {
            shark.setSharkState(AbstractSharkEntity.SharkState.CURIOUS);
        }
        return new Decision(true, "boat_track", target, predictionTicks, offset);
    }

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide
                || !(event.level instanceof ServerLevel level)) return;
        long tick = level.getGameTime();
        for (var entry : LEASES.entrySet()) {
            Lease lease = entry.getValue();
            Entity sharkEntity = level.getEntity(entry.getKey());
            Entity boatEntity = level.getEntity(lease.boat());
            if (!(sharkEntity instanceof GreatWhiteSharkEntity shark) || !(boatEntity instanceof Boat boat)
                    || !eligible(shark, boat, boat.getPassengers().isEmpty() ? null : boat.getPassengers().get(0))) {
                if (sharkEntity instanceof GreatWhiteSharkEntity shark) release(shark, "boat_unavailable");
                else LEASES.remove(entry.getKey(), lease);
                continue;
            }
            Vec3 delta = boat.position().subtract(lease.lastPosition());
            long lastMotion = lease.lastMotionTick();
            Vec3 direction = lease.direction();
            if (delta.horizontalDistanceSqr() >= MIN_MOTION * MIN_MOTION) {
                direction = horizontalDirection(delta);
                lastMotion = tick;
            }
            if (tick - lastMotion > STATIONARY_EXPIRY) {
                release(shark, "stationary_expired");
                continue;
            }
            if (direction.lengthSqr() < MIN_MOTION * MIN_MOTION || direction.dot(lease.direction()) < MAX_TURN_COSINE) {
                release(shark, "turn_exceeds_bound");
                continue;
            }
            if (FollowMovementOwners.selected(shark) || FollowMovementOwners.needsSafety(shark)
                    || shark.getTarget() != null || shark.getSharkState() == AbstractSharkEntity.SharkState.HOSTILE) {
                release(shark, "higher_priority_intent");
                continue;
            }
            if (tick % 10 == 0) {
                double offset = behindOffset(boat.getBbWidth(), shark.getBbWidth());
                Vec3 target = behindTarget(boat.position(), direction, offset, shark.getY());
                if (!safeWaterDestination(level, shark, target)) {
                    release(shark, "unsafe_route");
                    continue;
                }
                int predictionTicks = predictionTicks(shark.position(), target, shark.getSpeed());
                BrainUtils.setMemory(shark.getBrain(), MemoryModuleType.WALK_TARGET,
                        new WalkTarget(target, 1.0F, 1));
                BoatMovementOwners.claim(shark, boat.getUUID(), tick + LEASE_TICKS);
                LEASES.replace(entry.getKey(), lease,
                        new Lease(boat.getUUID(), direction, boat.position(), lease.lastEventTick(), lastMotion,
                                target, predictionTicks, offset));
            } else {
                BoatMovementOwners.claim(shark, boat.getUUID(), tick + LEASE_TICKS);
                LEASES.replace(entry.getKey(), lease,
                        new Lease(boat.getUUID(), direction, boat.position(), lease.lastEventTick(), lastMotion,
                                lease.target(), lease.predictionTicks(), lease.offset()));
            }
        }
    }

    private static boolean eligible(GreatWhiteSharkEntity shark, Boat boat, Entity rider) {
        return shark.isAlive() && !shark.isRemoved()
                && boat != null && boat.isAlive() && boat.level() == shark.level()
                && rider != null && boat.getPassengers().contains(rider)
                && rider.level() == shark.level()
                && !FollowMovementOwners.selected(shark) && !FollowMovementOwners.needsSafety(shark)
                && shark.getTarget() == null && shark.getSharkState() != AbstractSharkEntity.SharkState.HOSTILE;
    }

    private static String eligibilityReason(GreatWhiteSharkEntity shark, Boat boat, Entity rider) {
        if (!shark.isAlive() || shark.isRemoved()) return "shark_unavailable";
        if (shark.getTarget() != null || shark.getSharkState() == AbstractSharkEntity.SharkState.HOSTILE) return "combat_priority";
        if (FollowMovementOwners.selected(shark)) return "follow_priority";
        if (FollowMovementOwners.needsSafety(shark)) return "safety_priority";
        if (boat == null || rider == null || !boat.getPassengers().contains(rider)) return "boat_missing_occupant";
        if (!boat.isAlive() || boat.level() != shark.level() || rider.level() != shark.level()) return "boat_wrong_dimension";
        return "great_white_not_ready";
    }

    private static Vec3 horizontalDirection(Vec3 motion) {
        Vec3 horizontal = new Vec3(motion.x, 0.0D, motion.z);
        double length = horizontal.length();
        return length < 1.0e-8 ? Vec3.ZERO : horizontal.scale(1.0D / length);
    }

    static double behindOffset(double boatWidth, double sharkWidth) {
        return Math.max(0.0D, boatWidth) * 0.5D + Math.max(0.0D, sharkWidth) * 0.5D + BEHIND_CLEARANCE;
    }

    static Vec3 behindTarget(Vec3 boatPosition, Vec3 direction, double offset, double y) {
        Vec3 target = boatPosition.subtract(direction.scale(Math.max(0.0D, offset)));
        return new Vec3(target.x, y, target.z);
    }

    static int predictionTicks(Vec3 from, Vec3 to, double speed) {
        return Math.max(1, Math.min(20,
                (int) Math.ceil(from.distanceTo(to) / Math.max(0.05D, speed))));
    }

    private static boolean safeWaterDestination(ServerLevel level, GreatWhiteSharkEntity shark, Vec3 target) {
        if (!finite(target)) return false;
        Vec3 delta = target.subtract(shark.position());
        AABB box = shark.getBoundingBox().move(delta).inflate(0.02D);
        double[] xs = {box.minX + 0.05D, (box.minX + box.maxX) * 0.5D, box.maxX - 0.05D};
        double[] ys = {box.minY + 0.05D, (box.minY + box.maxY) * 0.5D, box.maxY - 0.05D};
        double[] zs = {box.minZ + 0.05D, (box.minZ + box.maxZ) * 0.5D, box.maxZ - 0.05D};
        for (double x : xs) for (double y : ys) for (double z : zs) {
            BlockState state = level.getBlockState(net.minecraft.core.BlockPos.containing(x, y, z));
            if (!level.getFluidState(net.minecraft.core.BlockPos.containing(x, y, z)).is(FluidTags.WATER)
                    || !state.getCollisionShape(level, net.minecraft.core.BlockPos.containing(x, y, z)).isEmpty()) return false;
        }
        return true;
    }

    private static boolean finite(Vec3 value) {
        return Double.isFinite(value.x) && Double.isFinite(value.y) && Double.isFinite(value.z);
    }

    private static void release(GreatWhiteSharkEntity shark, String reason) {
        Lease lease = LEASES.remove(shark.getUUID());
        if (lease != null) {
            BoatMovementOwners.release(shark, lease.boat());
            if (shark.getBrain().getMemory(MemoryModuleType.WALK_TARGET).isPresent()) {
                BrainUtils.clearMemory(shark.getBrain(), MemoryModuleType.WALK_TARGET);
            }
        }
    }

    static void clearEntity(ServerLevel level, UUID entity) {
        LEASES.remove(entity);
        LEASES.entrySet().removeIf(entry -> entry.getValue().boat().equals(entity));
    }

    static void clearLevel(ServerLevel level) {
        LEASES.entrySet().removeIf(entry -> {
            Entity shark = level.getEntity(entry.getKey());
            return shark == null || shark.level() == level;
        });
    }

    private record Lease(UUID boat, Vec3 direction, Vec3 lastPosition, long lastEventTick,
                         long lastMotionTick, Vec3 target, int predictionTicks, double offset) {}
}
