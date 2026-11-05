package tfar.bensfintasticsharks.advancmenets;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.storage.loot.LootContext;
import tfar.bensfintasticsharks.BensFintasticSharks;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlayerFoundEntityTrigger extends SimpleCriterionTrigger<PlayerFoundEntityTrigger.TriggerInstance> {
    static final ResourceLocation ID = BensFintasticSharks.id("player_found_entity");
    public static final int REQUIRED_GAZE_TICKS = 40;
    private static final double LOOK_RANGE = 32.0D;
    private final ConcurrentMap<UUID, GazeProgress> gazeProgress = new ConcurrentHashMap<>();


    public PlayerFoundEntityTrigger() {
    }

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject pJson, ContextAwarePredicate pPredicate, DeserializationContext pDeserializationContext) {
        ContextAwarePredicate contextawarepredicate = EntityPredicate.fromJson(pJson, "entity", pDeserializationContext);
        return new TriggerInstance(ID, pPredicate,contextawarepredicate);
    }

    public void trigger(ServerPlayer pPlayer, LivingEntity entity) {
        LootContext lootcontext = EntityPredicate.createContext(pPlayer, entity);
        this.trigger(pPlayer, instance -> instance.matches(pPlayer,lootcontext));
    }

    /** Tracks one directly targeted living entity and triggers its encounter after two seconds. */
    public void tickLook(ServerPlayer player) {
        LivingEntity target = lookedAtLivingEntity(player);
        if (target == null) {
            clearLook(player);
            return;
        }

        long tick = player.level().getGameTime();
        String dimension = player.level().dimension().location().toString();
        GazeProgress previous = gazeProgress.get(player.getUUID());
        boolean continuing = previous != null && previous.target().equals(target.getUUID())
                && dimension.equals(previous.dimension()) && previous.lastTick() == tick - 1L;
        int previousTicks = continuing ? previous.ticks() : 0;
        int dwellTicks = nextDwellTicks(previousTicks, continuing);

        gazeProgress.put(player.getUUID(), new GazeProgress(target.getUUID(), dimension, tick, dwellTicks));
        if (dwellTicks == REQUIRED_GAZE_TICKS && previousTicks < REQUIRED_GAZE_TICKS) {
            trigger(player, target);
        }
    }

    static int nextDwellTicks(int previousTicks, boolean continuing) {
        if (!continuing) return 1;
        return Math.min(REQUIRED_GAZE_TICKS, Math.max(0, previousTicks) + 1);
    }

    private static LivingEntity lookedAtLivingEntity(ServerPlayer player) {
        Vec3 eye = player.getEyePosition();
        Vec3 view = player.getViewVector(1.0F);
        Vec3 end = eye.add(view.scale(LOOK_RANGE));
        BlockHitResult blockHit = player.level().clip(new ClipContext(eye, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double maxDistanceSqr = blockHit.getType() == HitResult.Type.MISS
                ? LOOK_RANGE * LOOK_RANGE : eye.distanceToSqr(blockHit.getLocation());
        AABB search = player.getBoundingBox().expandTowards(view.scale(LOOK_RANGE)).inflate(0.25D);
        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player, eye, end, search,
                entity -> entity instanceof LivingEntity living && living.isAlive(), maxDistanceSqr);
        return entityHit != null && entityHit.getEntity() instanceof LivingEntity living ? living : null;
    }

    public void clearLook(ServerPlayer player) {
        gazeProgress.remove(player.getUUID());
    }

    public void clearAllLookProgress() {
        gazeProgress.clear();
    }

    private record GazeProgress(UUID target, String dimension, long lastTick, int ticks) {}

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final ContextAwarePredicate entity;


        public TriggerInstance(ResourceLocation pCriterion, ContextAwarePredicate pPlayer, ContextAwarePredicate entity) {
            super(pCriterion, pPlayer);
            this.entity = entity;
        }

        public boolean matches(ServerPlayer pPlayer, LootContext pContext) {
            boolean matches = this.entity.matches(pContext);
            return matches;
        }

        public static TriggerInstance located(EntityPredicate entityPredicate) {
            return new TriggerInstance(BensFintasticSharks.PLAYER_FOUND_ENTITY.getId(),ContextAwarePredicate.ANY, EntityPredicate.wrap(entityPredicate));
        }
        public JsonObject serializeToJson(SerializationContext pConditions) {
            JsonObject jsonobject = super.serializeToJson(pConditions);
            jsonobject.add("entity", this.entity.toJson(pConditions));
            return jsonobject;
        }
    }
}
