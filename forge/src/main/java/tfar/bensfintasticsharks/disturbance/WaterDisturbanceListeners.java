package tfar.bensfintasticsharks.disturbance;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.level.BlockEvent;
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

    /** Per-player, per-type last fire time in ticks. */
    private static final ConcurrentMap<UUID, ConcurrentMap<WaterDisturbanceEvent.Type, Long>> LAST_FIRED =
            new ConcurrentHashMap<>();

    public static void register(IEventBus bus) {
        bus.register(new WaterDisturbanceListeners());
        bus.register(new WaterDisturbanceHandler());
        bus.register(new SharkAlertHandler());
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.side.isClient()) return;
        Player player = event.player;
        if (!player.isInWater()) return;
        if (player.tickCount % 10 != 0) return;
        if (!player.isSprinting()) return;
        if (canFire(player.getUUID(), WaterDisturbanceEvent.Type.LIGHT, player.tickCount, 5)) {
            post(player.level(), player.blockPosition(), player, WaterDisturbanceEvent.Type.LIGHT);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity().level().isClientSide) return;
        // Attacker is the one swinging — we want HEAVY when a player attacks in water.
        if (event.getSource().getEntity() instanceof Player attacker
                && attacker.isInWater()
                && canFire(attacker.getUUID(), WaterDisturbanceEvent.Type.HEAVY, attacker.tickCount, 10)) {
            post(attacker.level(), event.getEntity().blockPosition(), attacker, WaterDisturbanceEvent.Type.HEAVY);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        LivingEntity hurt = event.getEntity();
        if (hurt.level().isClientSide) return;
        // BLOOD: bleeding in water => sharks converge. Skip if shark hurt by shark or out of water.
        if (!hurt.isInWater()) return;
        UUID key = hurt.getUUID();
        if (!canFire(key, WaterDisturbanceEvent.Type.BLOOD, hurt.tickCount, 5)) return;
        post(hurt.level(), hurt.blockPosition(), hurt, WaterDisturbanceEvent.Type.BLOOD);
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
        if (canFire(player.getUUID(), WaterDisturbanceEvent.Type.HEAVY, player.tickCount, 10)) {
            post((Level) event.getLevel(), event.getPos(), player, WaterDisturbanceEvent.Type.HEAVY);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLivingFall(net.minecraftforge.event.entity.living.LivingFallEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getDistance() < 3) return;
        if (!event.getEntity().isInWater()) return;
        UUID key = event.getEntity().getUUID();
        if (canFire(key, WaterDisturbanceEvent.Type.HEAVY, event.getEntity().tickCount, 10)) {
            post(event.getEntity().level(), event.getEntity().blockPosition(), event.getEntity(),
                    WaterDisturbanceEvent.Type.HEAVY);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Entity projectile = event.getProjectile();
        if (projectile.level().isClientSide) return;
        BlockPos hitPos = projectile.blockPosition();
        if (!projectile.level().getFluidState(hitPos).is(FluidTags.WATER)) return;
        Entity owner = projectile instanceof net.minecraft.world.entity.projectile.Projectile p ? p.getOwner() : null;
        UUID key = owner != null ? owner.getUUID() : projectile.getUUID();
        if (canFire(key, WaterDisturbanceEvent.Type.LIGHT, projectile.tickCount, 5)) {
            post(projectile.level(), hitPos, owner, WaterDisturbanceEvent.Type.LIGHT);
        }
    }

    private static boolean canFire(UUID uuid, WaterDisturbanceEvent.Type type, int currentTick, int minInterval) {
        ConcurrentMap<WaterDisturbanceEvent.Type, Long> lastByType =
                LAST_FIRED.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>());
        Long last = lastByType.get(type);
        long now = currentTick;
        if (last != null && (now - last) < minInterval) return false;
        lastByType.put(type, now);
        return true;
    }

    private static void post(Level level, BlockPos source, Entity sourceEntity, WaterDisturbanceEvent.Type type) {
        MinecraftForge.EVENT_BUS.post(new WaterDisturbanceEvent(level, source, sourceEntity, type));
    }
}
