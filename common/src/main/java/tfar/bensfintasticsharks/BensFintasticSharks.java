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
import tfar.bensfintasticsharks.advancmenets.OctopusInkedTrigger;
import tfar.bensfintasticsharks.advancmenets.PlayerFoundEntityTrigger;
import tfar.bensfintasticsharks.advancmenets.SpyglassSpotSharkTrigger;
import tfar.bensfintasticsharks.init.*;
import tfar.bensfintasticsharks.platform.Services;

import java.util.List;
import java.util.stream.Stream;

public class BensFintasticSharks {

    public static final String MOD_ID = "bensfintasticsharks";
    public static final String MOD_NAME = "BensFintasticSharks";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final PlayerFoundEntityTrigger PLAYER_FOUND_ENTITY = CriteriaTriggers.register(new PlayerFoundEntityTrigger());
    public static final OctopusInkedTrigger OCTOPUS_INKED = CriteriaTriggers.register(new OctopusInkedTrigger());
    public static final SpyglassSpotSharkTrigger SPYGLASS_SPOTTED_SHARK =
            CriteriaTriggers.register(new SpyglassSpotSharkTrigger());

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
        if (player instanceof ServerPlayer serverPlayer
                && serverPlayer.tickCount % 4 == 0
                && serverPlayer.isUsingItem()
                && serverPlayer.getUseItem().is(net.minecraft.world.item.Items.SPYGLASS)
                && isLookingAtShark(serverPlayer)) {
            SPYGLASS_SPOTTED_SHARK.trigger(serverPlayer);
        }
    }

    private static boolean isLookingAtShark(ServerPlayer player) {
        double range = 128;
        net.minecraft.world.phys.Vec3 eye = player.getEyePosition();
        net.minecraft.world.phys.Vec3 view = player.getViewVector(1);
        net.minecraft.world.phys.Vec3 end = eye.add(view.scale(range));
        net.minecraft.world.phys.AABB search = player.getBoundingBox()
                .expandTowards(view.scale(range))
                .inflate(1);
        net.minecraft.world.phys.EntityHitResult entityHit =
                net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                        player, eye, end, search,
                        entity -> entity instanceof LivingEntity living
                                && living.isAlive()
                                && living.getType().is(ModTags.EntityTypes.SHARKS),
                        range * range);
        if (entityHit == null) return false;

        net.minecraft.world.phys.HitResult blockHit = player.level().clip(
                new net.minecraft.world.level.ClipContext(
                        eye, end,
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE,
                        player));
        return blockHit.getType() == net.minecraft.world.phys.HitResult.Type.MISS
                || eye.distanceToSqr(entityHit.getLocation()) < eye.distanceToSqr(blockHit.getLocation());
    }

    public static Stream<Item> getKnownItems() {
        return getKnown(BuiltInRegistries.ITEM);
    }

    public static <V> Stream<V> getKnown(Registry<V> registry) {
        return registry.stream().filter(o -> registry.getKey(o).getNamespace().equals(MOD_ID));
    }
}
