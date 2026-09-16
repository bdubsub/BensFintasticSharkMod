package tfar.bensfintasticsharks.disturbance;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Posts {@link WaterDisturbanceEvent}s in response to vanilla forge events.
 *
 * Rate-limited per-player-per-type so a sprinting player doesn't flood the bus.
 * See the spec's "Water Disturbance System" section.
 */
public class WaterDisturbanceListeners {

    private static final int MAX_THROTTLE_KEYS_PER_LEVEL = 4096;
    private static final long THROTTLE_RETENTION_TICKS = 200L;
    private static final ConcurrentMap<ThrottleKey, Long> LAST_FIRED = new ConcurrentHashMap<>();
    private static final ConcurrentMap<LivingEntity, WaterState> WAS_IN_WATER = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, BoatState> BOATS = new ConcurrentHashMap<>();

    public static void register(IEventBus bus) {
        bus.register(new WaterDisturbanceListeners());
        bus.register(new WaterDisturbanceHandler());
        bus.register(new GreatWhiteBoatInterest());
        bus.register(new SharkAlertHandler());
        bus.register(new PreyFleeHandler());
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side.isClient()) return;
        Player player = event.player;
        if (!player.isInWater()) return;
        if (!player.isSprinting()) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        long tick = level.getGameTime();
        if (tick % 10 != 0) return;
        if (canFire(level, player.getUUID(), WaterDisturbanceEvent.SourceKind.SWIM_SPRINT, tick, 5,
                player.blockPosition(), player, WaterDisturbanceEvent.Type.LIGHT, null, null)) {
            post(player.level(), player.blockPosition(), player, WaterDisturbanceEvent.Type.LIGHT,
                    WaterDisturbanceEvent.SourceKind.SWIM_SPRINT, 0.5D, null, null);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;
        boolean inWater = entity.isInWaterOrBubble();
        WaterState previous = WAS_IN_WATER.get(entity);
        if (previous == null) {
            WAS_IN_WATER.put(entity, new WaterState(entity.level().dimension(), inWater));
            return;
        }
        if (previous.dimension().equals(entity.level().dimension()) && previous.inWater() == inWater) return;
        WAS_IN_WATER.put(entity, new WaterState(entity.level().dimension(), inWater));
        ServerLevel level = (ServerLevel) entity.level();
        long tick = level.getGameTime();
        if (!previous.inWater() && inWater
                && canFire(level, entity.getUUID(), WaterDisturbanceEvent.SourceKind.WATER_ENTRY, tick, 20,
                entity.blockPosition(), entity, WaterDisturbanceEvent.Type.LIGHT, null, null)) {
            post(entity.level(), entity.blockPosition(), entity, WaterDisturbanceEvent.Type.LIGHT,
                    WaterDisturbanceEvent.SourceKind.WATER_ENTRY, 0.5D, null, null);
        } else if (previous.inWater() && !inWater && entity.getDeltaMovement().y > 0.08D
                && canFire(level, entity.getUUID(), WaterDisturbanceEvent.SourceKind.WATER_JUMP, tick, 20,
                entity.blockPosition(), entity, WaterDisturbanceEvent.Type.HEAVY, null, null)) {
            post(entity.level(), entity.blockPosition(), entity, WaterDisturbanceEvent.Type.HEAVY,
                    WaterDisturbanceEvent.SourceKind.WATER_JUMP, 0.5D, null, null);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        Entity entity = event.getEntity();
        if (entity instanceof LivingEntity) {
            WAS_IN_WATER.put((LivingEntity) entity, new WaterState(event.getLevel().dimension(), false));
        }
        if (entity instanceof Boat boat) {
            ServerLevel level = (ServerLevel) event.getLevel();
            BOATS.put(entity.getUUID(), new BoatState(level.dimension(), boat.position(), level.getGameTime()));
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onEntityLeave(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        ResourceKey<Level> dimension = event.getLevel().dimension();
        if (entity instanceof LivingEntity living) WAS_IN_WATER.remove(living);
        if (entity instanceof Boat) BOATS.remove(entity.getUUID());
        if (event.getLevel() instanceof ServerLevel level) {
            WaterDisturbanceHandler.clearEntity(level, entity.getUUID());
            GreatWhiteBoatInterest.clearEntity(level, entity.getUUID());
        }
        LAST_FIRED.keySet().removeIf(key -> key.dimension().equals(dimension) && key.source().equals(entity.getUUID()));
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        ResourceKey<Level> dimension = level.dimension();
        WAS_IN_WATER.entrySet().removeIf(entry -> entry.getValue().dimension().equals(dimension));
        BOATS.entrySet().removeIf(entry -> entry.getValue().dimension().equals(dimension));
        LAST_FIRED.keySet().removeIf(key -> key.dimension().equals(dimension));
        WaterDisturbanceHandler.clearLevel(level);
        GreatWhiteBoatInterest.clearLevel(level);
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide
                || !(event.level instanceof ServerLevel level)) return;
        for (var entry : BOATS.entrySet()) {
            BoatState state = entry.getValue();
            if (!state.dimension().equals(level.dimension())) continue;
            Entity entity = level.getEntity(entry.getKey());
            if (!(entity instanceof Boat boat) || !boat.isAlive()) {
                BOATS.remove(entry.getKey(), state);
                continue;
            }
            net.minecraft.world.phys.Vec3 delta = boat.position().subtract(state.position());
            BoatState updated = new BoatState(state.dimension(), boat.position(), state.lastEventTick());
            BOATS.replace(entry.getKey(), state, updated);
            long tick = level.getGameTime();
            if (boat.getPassengers().isEmpty()) {
                if (tick % 10 == 0) {
                    WaterDisturbanceEvent empty = new WaterDisturbanceEvent(level, boat.blockPosition(), boat,
                            WaterDisturbanceEvent.Type.LIGHT,
                            WaterDisturbanceEvent.SourceKind.OCCUPIED_BOAT, 0.0D, boat, null);
                    tfar.bensfintasticsharks.debug.BfsDebugManager.recordDisturbanceDecision(level, empty,
                            "ignored", "empty_boat", 0, sourceKeyCount(level));
                }
                continue;
            }
            Entity rider = boat.getPassengers().get(0);
            if (rider.isInWaterOrBubble()) continue;
            double horizontal = Math.hypot(delta.x, delta.z);
            double movementThreshold = tfar.bensfintasticsharks.config.BfsConfig.COMMON
                    .disturbanceBoatMovementThreshold.get();
            if (horizontal < movementThreshold) {
                if (tick % 10 == 0) {
                    WaterDisturbanceEvent stationary = new WaterDisturbanceEvent(level, boat.blockPosition(), boat,
                            WaterDisturbanceEvent.Type.LIGHT,
                            WaterDisturbanceEvent.SourceKind.OCCUPIED_BOAT, 0.0D, boat, rider);
                    tfar.bensfintasticsharks.debug.BfsDebugManager.recordDisturbanceDecision(level, stationary,
                            "ignored", "stationary_boat", 0, sourceKeyCount(level));
                }
                continue;
            }
            if (tick - state.lastEventTick() < 10) continue;
            if (!canFire(level, boat.getUUID(), WaterDisturbanceEvent.SourceKind.OCCUPIED_BOAT, tick, 10,
                    boat.blockPosition(), boat, WaterDisturbanceEvent.Type.LIGHT, boat, rider)) continue;
            double strength = Math.min(1.0D, horizontal / Math.max(0.25D, movementThreshold * 12.5D));
            post(level, boat.blockPosition(), boat, WaterDisturbanceEvent.Type.LIGHT,
                    WaterDisturbanceEvent.SourceKind.OCCUPIED_BOAT, strength, boat, rider, delta);
            BOATS.replace(entry.getKey(), updated,
                    new BoatState(updated.dimension(), updated.position(), tick));
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity().level().isClientSide) return;
        // Attacker is the one swinging — we want HEAVY when a player attacks in water.
        if (event.getSource().getEntity() instanceof Player attacker
                && attacker.isInWater()
                && attacker.level() instanceof ServerLevel level
                && canFire(level, attacker.getUUID(), WaterDisturbanceEvent.SourceKind.ATTACK,
                level.getGameTime(), 10, event.getEntity().blockPosition(), attacker,
                WaterDisturbanceEvent.Type.HEAVY, null, null)) {
            post(attacker.level(), event.getEntity().blockPosition(), attacker, WaterDisturbanceEvent.Type.HEAVY,
                    WaterDisturbanceEvent.SourceKind.ATTACK, 1.0D, null, null);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity hurt = event.getEntity();
        if (hurt.level().isClientSide) return;
        // BLOOD: bleeding in water => sharks converge. Skip if shark hurt by shark or out of water.
        if (!hurt.isInWater()) return;
        UUID key = hurt.getUUID();
        if (!(hurt.level() instanceof ServerLevel level)
                || !canFire(level, key, WaterDisturbanceEvent.SourceKind.DAMAGE, level.getGameTime(), 5,
                hurt.blockPosition(), hurt, WaterDisturbanceEvent.Type.BLOOD, null, null)) return;
        post(hurt.level(), hurt.blockPosition(), hurt, WaterDisturbanceEvent.Type.BLOOD,
                WaterDisturbanceEvent.SourceKind.DAMAGE, 1.0D, null, null);
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        if (event.getLevel().isClientSide()) return;
        if (!player.isInWater()) return;
        if (!event.getLevel().getFluidState(event.getPos()).is(FluidTags.WATER)) {
            // Adjacent water — still a disturbance if there's water nearby.
            boolean nearWater = false;
            BlockPos pos = event.getPos();
            for (BlockPos around : new BlockPos[]{pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west()}) {
                if (event.getLevel().getFluidState(around).is(FluidTags.WATER)) {
                    nearWater = true;
                    break;
                }
            }
            if (!nearWater) return;
        }
        if (!(player.level() instanceof ServerLevel level)) return;
        if (canFire(level, player.getUUID(), WaterDisturbanceEvent.SourceKind.BLOCK_BREAK,
                level.getGameTime(), 10, event.getPos(), player, WaterDisturbanceEvent.Type.HEAVY, null, null)) {
            post((Level) event.getLevel(), event.getPos(), player, WaterDisturbanceEvent.Type.HEAVY,
                    WaterDisturbanceEvent.SourceKind.BLOCK_BREAK, 0.75D, null, null);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLivingFall(net.minecraftforge.event.entity.living.LivingFallEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getDistance() < 3) return;
        if (!event.getEntity().isInWater()) return;
        UUID key = event.getEntity().getUUID();
        if (!(event.getEntity().level() instanceof ServerLevel level)
                || !canFire(level, key, WaterDisturbanceEvent.SourceKind.FALL, level.getGameTime(), 10,
                event.getEntity().blockPosition(), event.getEntity(), WaterDisturbanceEvent.Type.HEAVY, null, null)) {
            return;
        }
        post(event.getEntity().level(), event.getEntity().blockPosition(), event.getEntity(),
                WaterDisturbanceEvent.Type.HEAVY, WaterDisturbanceEvent.SourceKind.FALL,
                1.0D, null, null);
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getProjectile();
        if (projectile.level().isClientSide) return;
        BlockPos hitPos = projectile.blockPosition();
        if (!projectile.level().getFluidState(hitPos).is(FluidTags.WATER)) return;
        Entity owner = projectile instanceof net.minecraft.world.entity.projectile.Projectile p ? p.getOwner() : null;
        UUID key = owner != null ? owner.getUUID() : projectile.getUUID();
        if (!(projectile.level() instanceof ServerLevel level)) return;
        if (canFire(level, key, WaterDisturbanceEvent.SourceKind.PROJECTILE, level.getGameTime(), 5,
                hitPos, owner, WaterDisturbanceEvent.Type.LIGHT, null, null)) {
            post(projectile.level(), hitPos, owner, WaterDisturbanceEvent.Type.LIGHT,
                    WaterDisturbanceEvent.SourceKind.PROJECTILE, 0.5D, null, null);
        }
    }

    private static boolean canFire(ServerLevel level, UUID uuid, WaterDisturbanceEvent.SourceKind sourceKind,
                                   long now, int minInterval, BlockPos source, Entity sourceEntity,
                                   WaterDisturbanceEvent.Type type, Entity boat, Entity rider) {
        prune(level, now);
        ThrottleKey key = new ThrottleKey(level.dimension(), uuid, sourceKind);
        Long last = LAST_FIRED.get(key);
        if (last != null && now - last < minInterval) {
            tfar.bensfintasticsharks.debug.BfsDebugManager.recordDisturbanceThrottle(level,
                    new WaterDisturbanceEvent(level, source, sourceEntity, type, sourceKind, 0.0D, boat, rider),
                    "duplicate", now - last, sourceKeyCount(level));
            return false;
        }
        if (last == null && sourceKeyCount(level) >= MAX_THROTTLE_KEYS_PER_LEVEL) {
            tfar.bensfintasticsharks.debug.BfsDebugManager.recordDisturbanceThrottle(level,
                    new WaterDisturbanceEvent(level, source, sourceEntity, type, sourceKind, 0.0D, boat, rider),
                    "source_budget", 0L, sourceKeyCount(level));
            return false;
        }
        LAST_FIRED.put(key, now);
        return true;
    }

    private static void prune(ServerLevel level, long now) {
        LAST_FIRED.entrySet().removeIf(entry -> entry.getKey().dimension().equals(level.dimension())
                && now - entry.getValue() > THROTTLE_RETENTION_TICKS);
    }

    static int sourceKeyCount(ServerLevel level) {
        int count = 0;
        for (ThrottleKey key : LAST_FIRED.keySet()) {
            if (key.dimension().equals(level.dimension())) count++;
        }
        return count;
    }

    private static void post(Level level, BlockPos source, Entity sourceEntity, WaterDisturbanceEvent.Type type,
                             WaterDisturbanceEvent.SourceKind sourceKind, double strength,
                             Entity boat, Entity rider) {
        post(level, source, sourceEntity, type, sourceKind, strength, boat, rider, net.minecraft.world.phys.Vec3.ZERO);
    }

    private static void post(Level level, BlockPos source, Entity sourceEntity, WaterDisturbanceEvent.Type type,
                             WaterDisturbanceEvent.SourceKind sourceKind, double strength,
                             Entity boat, Entity rider, net.minecraft.world.phys.Vec3 boatMovement) {
        WaterDisturbanceEvent event = new WaterDisturbanceEvent(level, source, sourceEntity, type,
                sourceKind, strength, boat, rider, boatMovement);
        if (level instanceof ServerLevel serverLevel) {
            tfar.bensfintasticsharks.debug.BfsDebugManager.recordDisturbanceEvent(serverLevel, event,
                    "emitted", "producer");
        }
        MinecraftForge.EVENT_BUS.post(event);
    }

    private record WaterState(ResourceKey<Level> dimension, boolean inWater) {}

    private record ThrottleKey(ResourceKey<Level> dimension, UUID source,
                                WaterDisturbanceEvent.SourceKind sourceKind) {}

    private record BoatState(ResourceKey<Level> dimension, net.minecraft.world.phys.Vec3 position,
                             long lastEventTick) {}
}
