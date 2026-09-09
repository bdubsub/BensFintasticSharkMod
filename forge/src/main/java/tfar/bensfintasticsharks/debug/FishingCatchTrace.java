package tfar.bensfintasticsharks.debug;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.fishing.FishingCatchPolicy;

import java.lang.ref.WeakReference;
import java.util.UUID;

/** A bounded observation of one real fishing callback and its subsequent rod cleanup. */
public final class FishingCatchTrace {
    private final UUID sessionId;
    private final UUID attemptId;
    private final UUID playerId;
    private final WeakReference<ServerPlayer> player;
    private final WeakReference<FishingHook> hook;
    private final ItemStack rod;
    private ItemFishedEvent event;
    private final JsonObject data = new JsonObject();
    private final int statisticBefore;
    private boolean closed;
    private boolean ambiguousSettlement;

    FishingCatchTrace(UUID sessionId, String angler, ItemFishedEvent event, ItemStack rod,
                      String originalItem, String lootTable, boolean replace, boolean live) {
        ServerPlayer owner = (ServerPlayer) event.getEntity();
        FishingHook fishingHook = event.getHookEntity();
        this.sessionId = sessionId;
        this.attemptId = fishingHook.getUUID();
        this.playerId = owner.getUUID();
        this.player = new WeakReference<>(owner);
        this.hook = new WeakReference<>(fishingHook);
        this.rod = rod;
        this.event = event;
        this.statisticBefore = owner.getStats().getValue(Stats.CUSTOM, Stats.FISH_CAUGHT);
        data.addProperty("attemptId", attemptId.toString());
        data.addProperty("hookRuntimeId", fishingHook.getId());
        data.addProperty("hookType", BuiltInRegistries.ENTITY_TYPE.getKey(fishingHook.getType()).toString());
        data.addProperty("angler", angler);
        data.addProperty("replaceVanillaMobs", replace);
        data.addProperty("fishEntities", live);
        data.addProperty("originalItem", originalItem);
        data.addProperty("lootTable", lootTable);
        ItemStack selected = FishingCatchPolicy.onlySupportedFishingFish(event.getDrops());
        data.addProperty("lootCategory", selected == null
                ? "unavailable:custom_junk_or_treasure_pool_not_exposed" : "fish");
        data.addProperty("selectedItem", selected == null ? "unavailable:not_one_supported_fish" : itemId(selected));
        data.addProperty("selectedCount", selected == null ? 0 : selected.getCount());
        data.addProperty("selectedSpecies", selected == null ? "unavailable:not_one_supported_fish"
                : BuiltInRegistries.ENTITY_TYPE.getKey(FishingCatchPolicy.entityTypeFor(selected)).toString());
        JsonArray drops = new JsonArray();
        event.getDrops().stream().limit(16).forEach(stack -> {
            JsonObject item = new JsonObject();
            item.addProperty("item", itemId(stack));
            item.addProperty("count", stack.getCount());
            drops.add(item);
        });
        data.add("observedDrops", drops);
        data.addProperty("dropListTruncated", event.getDrops().size() > 16);
        data.addProperty("replacementApplied", selected != null && !originalItem.startsWith("unavailable:")
                && !itemId(selected).equals(originalItem));
        data.addProperty("rodItem", itemId(rod));
        data.addProperty("rodHand", originalItem.equals("unavailable:no_loot_context") ? "unavailable:tool_not_observed"
                : rod == owner.getMainHandItem() ? "main_hand"
                : rod == owner.getOffhandItem() ? "off_hand" : "unavailable:tool_not_held");
        data.addProperty("rodDamageBefore", rod.getDamageValue());
        data.addProperty("rodCountBefore", rod.getCount());
        JsonObject enchantments = new JsonObject();
        var toolEnchantments = EnchantmentHelper.getEnchantments(rod);
        toolEnchantments.entrySet().stream().limit(16).forEach(entry -> enchantments.addProperty(
                BuiltInRegistries.ENCHANTMENT.getKey(entry.getKey()).toString(), entry.getValue()));
        data.add("rodEnchantments", enchantments);
        data.addProperty("rodEnchantmentsTruncated", toolEnchantments.size() > 16);
        data.addProperty("rodCostRequested", event.getRodDamage());
        data.addProperty("cancelledBefore", event.isCanceled());
        data.addProperty("fishStatisticBefore", statisticBefore);
        data.add("advancementsBefore", advancements(owner));
        outcome("interrupted_before_result");
        data.addProperty("deliveryKind", "none");
        data.addProperty("deliveryCount", 0);
        data.addProperty("insertionAccepted", false);
        data.addProperty("xpRequested", 0);
        data.addProperty("xpAccepted", 0);
    }

    public void outcome(String outcome) {
        data.addProperty("outcome", outcome);
    }

    public void delivery(Entity entity, boolean live, boolean accepted) {
        data.addProperty("insertionAccepted", accepted);
        data.addProperty("deliveryKind", live ? "live" : "item");
        data.addProperty("deliveryUuid", entity.getUUID().toString());
        data.addProperty("deliveryType", BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
        Vec3 impulse = entity.getDeltaMovement();
        JsonObject vector = new JsonObject();
        vector.addProperty("x", impulse.x);
        vector.addProperty("y", impulse.y);
        vector.addProperty("z", impulse.z);
        data.add("reelImpulse", vector);
    }

    public void committed(int requestedXp, boolean xpAccepted) {
        outcome("committed");
        data.addProperty("deliveryCount", 1);
        data.addProperty("xpRequested", requestedXp);
        data.addProperty("xpAccepted", xpAccepted ? requestedXp : 0);
    }

    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        ServerPlayer owner = player.get();
        data.addProperty("stage", "delivery");
        data.addProperty("cancelledAfter", event.isCanceled());
        data.addProperty("rodCostRequested", event.getRodDamage());
        event = null;
        if (owner != null) {
            data.addProperty("fishStatisticAfter", owner.getStats().getValue(Stats.CUSTOM, Stats.FISH_CAUGHT));
            data.addProperty("fishStatisticDelta", owner.getStats().getValue(Stats.CUSTOM, Stats.FISH_CAUGHT) - statisticBefore);
            data.add("advancementsAfter", advancements(owner));
            BfsDebugManager.recordFishing(sessionId, owner.level().getGameTime(), data);
        }
    }

    JsonObject settlement() {
        JsonObject record = new JsonObject();
        record.addProperty("attemptId", attemptId.toString());
        record.addProperty("angler", data.get("angler").getAsString());
        record.addProperty("stage", "settled");
        record.addProperty("ambiguousSettlement", ambiguousSettlement);
        record.addProperty("rodDamageBefore", data.get("rodDamageBefore").getAsInt());
        record.addProperty("rodDamageAfter", rod.getDamageValue());
        record.addProperty("rodCountAfter", rod.getCount());
        FishingHook fishingHook = hook.get();
        record.addProperty("hookRemoved", fishingHook == null || fishingHook.isRemoved());
        ServerPlayer owner = player.get();
        if (owner == null) {
            record.addProperty("playerState", "unavailable:owner_released");
        } else {
            record.addProperty("playerState", owner.isRemoved() ? "removed" : "available");
            record.addProperty("hookStillOwned", owner.fishing != null && owner.fishing.getUUID().equals(attemptId));
            record.addProperty("fishStatisticAfter", owner.getStats().getValue(Stats.CUSTOM, Stats.FISH_CAUGHT));
            record.add("advancementsAfter", advancements(owner));
        }
        return record;
    }

    UUID attemptId() {
        return attemptId;
    }

    UUID playerId() {
        return playerId;
    }

    boolean isResolving() {
        return !closed;
    }

    void markAmbiguousSettlement() {
        ambiguousSettlement = true;
    }

    static String itemId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }

    private static JsonObject advancements(ServerPlayer player) {
        JsonObject progress = new JsonObject();
        for (String path : new String[]{"oh_my_cod", "why_arent_you_red"}) {
            var advancement = player.server.getAdvancements().getAdvancement(BensFintasticSharks.id(path));
            if (advancement == null) {
                progress.addProperty(path, "unavailable:advancement_not_loaded");
            } else {
                progress.addProperty(path, player.getAdvancements().getOrStartProgress(advancement).isDone());
            }
        }
        return progress;
    }
}
