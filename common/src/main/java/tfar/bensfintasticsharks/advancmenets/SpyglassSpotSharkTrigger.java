package tfar.bensfintasticsharks.advancmenets;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import tfar.bensfintasticsharks.BensFintasticSharks;

public class SpyglassSpotSharkTrigger extends SimpleCriterionTrigger<SpyglassSpotSharkTrigger.TriggerInstance> {

    private static final ResourceLocation ID = BensFintasticSharks.id("spyglass_spotted_shark");

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected TriggerInstance createInstance(JsonObject json, ContextAwarePredicate player,
                                             DeserializationContext context) {
        return new TriggerInstance(player);
    }

    public void trigger(ServerPlayer player) {
        trigger(player, instance -> true);
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        public TriggerInstance(ContextAwarePredicate player) {
            super(ID, player);
        }

        public static TriggerInstance spotted() {
            return new TriggerInstance(ContextAwarePredicate.ANY);
        }
    }
}
