package tfar.bensfintasticsharks.advancmenets;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import tfar.bensfintasticsharks.BensFintasticSharks;

/**
 * Fires when a player provokes an octopus into inking by hurting it. Unlike the
 * old {@link PlayerFoundEntityTrigger} placeholder this only triggers for the
 * attacking player, never on a passive encounter or the proximity-panic ink.
 */
public class OctopusInkedTrigger extends SimpleCriterionTrigger<OctopusInkedTrigger.TriggerInstance> {
    static final ResourceLocation ID = BensFintasticSharks.id("octopus_inked");

    public ResourceLocation getId() {
        return ID;
    }

    public TriggerInstance createInstance(JsonObject pJson, ContextAwarePredicate pPredicate, DeserializationContext pDeserializationContext) {
        ContextAwarePredicate entity = EntityPredicate.fromJson(pJson, "entity", pDeserializationContext);
        return new TriggerInstance(pPredicate, entity);
    }

    public void trigger(ServerPlayer pPlayer, LivingEntity octopus) {
        LootContext lootcontext = EntityPredicate.createContext(pPlayer, octopus);
        this.trigger(pPlayer, instance -> instance.matches(lootcontext));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final ContextAwarePredicate entity;

        public TriggerInstance(ContextAwarePredicate pPlayer, ContextAwarePredicate entity) {
            super(ID, pPlayer);
            this.entity = entity;
        }

        public static TriggerInstance inked(EntityPredicate entityPredicate) {
            return new TriggerInstance(ContextAwarePredicate.ANY, EntityPredicate.wrap(entityPredicate));
        }

        public boolean matches(LootContext pContext) {
            return this.entity.matches(pContext);
        }

        public JsonObject serializeToJson(SerializationContext pConditions) {
            JsonObject jsonobject = super.serializeToJson(pConditions);
            jsonobject.add("entity", this.entity.toJson(pConditions));
            return jsonobject;
        }
    }
}
