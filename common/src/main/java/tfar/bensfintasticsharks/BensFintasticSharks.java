package tfar.bensfintasticsharks;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tfar.bensfintasticsharks.advancmenets.PlayerFoundEntityTrigger;
import tfar.bensfintasticsharks.init.*;
import tfar.bensfintasticsharks.platform.Services;

import java.util.List;
import java.util.stream.Stream;

public class BensFintasticSharks {

    public static final String MOD_ID = "bensfintasticsharks";
    public static final String MOD_NAME = "BensFintasticSharks";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final PlayerFoundEntityTrigger PLAYER_FOUND_ENTITY = CriteriaTriggers.register(new PlayerFoundEntityTrigger());

    public static final int GRAB_TIMER = 100;

    public static void init() {
        // Platform-side hook lets each loader create its custom mob categories before
        // ModEntityTypes' static init runs.
        Services.PLATFORM.initCustomCategories();
        Services.PLATFORM.registerAll(ModEntityTypes.class, BuiltInRegistries.ENTITY_TYPE, EntityType.class);
        Services.PLATFORM.registerAll(ModItems.class, BuiltInRegistries.ITEM, Item.class);
        Services.PLATFORM.registerAll(ModMobEffects.class, BuiltInRegistries.MOB_EFFECT, MobEffect.class);
        Services.PLATFORM.registerAll(ModCreativeTabs.class, BuiltInRegistries.CREATIVE_MODE_TAB, CreativeModeTab.class);
        EntityVariantPredicates.poke();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID,path);
    }

    private static final int PLAYER_SCAN_INTERVAL = 20;

    public static void playerTick(Player player) {

        if (player instanceof ServerPlayer serverPlayer && serverPlayer.tickCount % PLAYER_SCAN_INTERVAL == 0) {
            List<LivingEntity> nearby = serverPlayer.serverLevel().getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, player, player.getBoundingBox().inflate(16));
            for (LivingEntity living : nearby) {
                PLAYER_FOUND_ENTITY.trigger(serverPlayer, living);
            }
        }
    }

    public static Stream<Item> getKnownItems() {
        return getKnown(BuiltInRegistries.ITEM);
    }

    public static <V> Stream<V> getKnown(Registry<V> registry) {
        return registry.stream().filter(o -> registry.getKey(o).getNamespace().equals(MOD_ID));
    }
}