package tfar.bensfintasticsharks.disturbance;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;

import java.util.List;

public class SharkAlertHandler {

    /** Rate-limit per source position to avoid event spam. Key = source long hash. */
    private static final java.util.concurrent.ConcurrentMap<Long, Long> LAST_FIRED = new java.util.concurrent.ConcurrentHashMap<>();

    /** Public API for shark entities to fire a convergence/alert. */
    public static void fire(ServerLevel level, AbstractSharkEntity<?> source, SharkAlertEvent.Type type,
                            Class<?> alertedSpecies, double radius, net.minecraft.world.entity.LivingEntity target) {
        long key = source.blockPosition().asLong();
        long now = level.getGameTime();
        Long last = LAST_FIRED.get(key);
        if (last != null && now - last < 100) return;
        LAST_FIRED.put(key, now);
        MinecraftForge.EVENT_BUS.post(new SharkAlertEvent(level, source.blockPosition(), target,
                alertedSpecies, radius, type));
    }

    @SubscribeEvent
    public void onAlert(SharkAlertEvent event) {
        if (event.getLevel().isClientSide) return;
        ServerLevel level = (ServerLevel) event.getLevel();
        AABB area = new AABB(event.getSource()).inflate(event.getRadius());
        List<AbstractSharkEntity> sharks = level.getEntitiesOfClass(AbstractSharkEntity.class, area,
                shark -> event.getAlertedSpecies().isInstance(shark));
        for (AbstractSharkEntity shark : sharks) {
            if (shark.getSharkState() != AbstractSharkEntity.SharkState.IDLE
                    && shark.getSharkState() != AbstractSharkEntity.SharkState.CURIOUS) continue;
            // Satiated sharks ignore pack alerts and blood convergence — one
            // prey per cycle. They still react to disturbances cosmetically.
            if (shark.isOnHuntCooldown()) continue;
            switch (event.getType()) {
                case BLOOD_CONVERGENCE -> {
                    // 0.19 review: the raw setTarget bypassed every prey/species guard —
                    // whitetips converged on their own bleeding packmates (who can't even
                    // retaliate, thanks to the same-species hurt() guard) and on non-prey
                    // like bleeding great whites. Same rule as the base BLOOD path now:
                    // players always draw the convergence, mobs must pass canHuntTarget.
                    net.minecraft.world.entity.LivingEntity target = event.getTarget();
                    boolean valid = target != null
                            && (shark.getTarget() == target || shark.canHuntTarget(target));
                    if (valid) {
                        shark.setTarget(target);
                        shark.reactToDisturbance(event.getSource(),
                                tfar.bensfintasticsharks.disturbance.DisturbanceType.BLOOD, target);
                    }
                }
                case PACK_ALERT -> {
                    // Pack alert: only ~30% of same-species sharks in radius go
                    // HOSTILE on the attacker, and only if they aren't already
                    // tracking something. Dropped from 80% after playtests showed
                    // entire packs piling onto a single trigger; the rest now
                    // become CURIOUS via the disturbance fallback, which looks
                    // alert without dogpiling.
                    if (shark.getRandom().nextFloat() < 0.30f && event.getTarget() != null
                            && shark.getTarget() == null) {
                        shark.setTarget(event.getTarget());
                        shark.setSharkState(AbstractSharkEntity.SharkState.HOSTILE);
                        shark.setStateTimer(400);
                    } else {
                        shark.reactToDisturbance(event.getSource(),
                                tfar.bensfintasticsharks.disturbance.DisturbanceType.HEAVY, null);
                    }
                }
            }
        }
    }
}
