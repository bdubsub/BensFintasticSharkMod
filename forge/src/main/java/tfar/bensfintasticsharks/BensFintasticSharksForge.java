package tfar.bensfintasticsharks;

import net.minecraft.core.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.bensfintasticsharks.client.ModClientForge;
import tfar.bensfintasticsharks.datagen.ModDatagen;
import tfar.bensfintasticsharks.dive.DiveOxygenManager;
import tfar.bensfintasticsharks.dive.DiveSuitEligibility;
import tfar.bensfintasticsharks.disturbance.WaterDisturbanceListeners;
import tfar.bensfintasticsharks.entity.*;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModMobEffects;
import tfar.bensfintasticsharks.init.ModTags;
import tfar.bensfintasticsharks.trade.BfsFishermanTrades;
import tfar.bensfintasticsharks.network.DiveNetworking;
import tfar.bensfintasticsharks.debug.BfsDebugManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod(BensFintasticSharks.MOD_ID)
public class BensFintasticSharksForge {
    
    public BensFintasticSharksForge() {

        GeckoLib.initialize();
        DiveNetworking.register();
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your mod. You can access Forge and Common code in this
        // project.

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::register);
        bus.addListener(this::attributes);
        bus.addListener(tfar.bensfintasticsharks.spawn.BfsSpawnPlacements::onSpawnPlacementRegister);
        bus.addListener(ModDatagen::start);
        MinecraftForge.EVENT_BUS.addListener(this::playerTick);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedOut);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerChangedDimension);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerRespawn);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerClone);
        MinecraftForge.EVENT_BUS.addListener(this::onBreakSpeed);
        MinecraftForge.EVENT_BUS.addListener(this::onBlockBreak);
        MinecraftForge.EVENT_BUS.addListener(this::onBlockPlace);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityLeaveLevel);
        MinecraftForge.EVENT_BUS.addListener(this::onServerStopping);
        MinecraftForge.EVENT_BUS.addListener(this::trading);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityJoin);
        MinecraftForge.EVENT_BUS.addListener(net.minecraftforge.eventbus.api.EventPriority.LOWEST, true,
                tfar.bensfintasticsharks.fishing.FishingCatchDelivery::onItemFished);
        MinecraftForge.EVENT_BUS.addListener(tfar.bensfintasticsharks.fishing.FishingCatchDelivery::onServerTick);
        MinecraftForge.EVENT_BUS.addListener(tfar.bensfintasticsharks.fishing.FishingCatchDelivery::onServerStopping);
        MinecraftForge.EVENT_BUS.register(new tfar.bensfintasticsharks.spawn.MobCapManager());
        WaterDisturbanceListeners.register(MinecraftForge.EVENT_BUS);
        tfar.bensfintasticsharks.debug.BfsDebugManager.register(MinecraftForge.EVENT_BUS);
        tfar.bensfintasticsharks.follow.BfsFollowManager.register(MinecraftForge.EVENT_BUS);
        bus.addListener(this::onCommonSetup);
        tfar.bensfintasticsharks.config.BfsConfig.register();
        tfar.bensfintasticsharks.worldgen.ModFeatures.register(bus);

        if (FMLEnvironment.dist.isClient()) {
            ModClientForge.init(bus);
        }

        // Use Forge to bootstrap the Common mod.
        BensFintasticSharks.init();
    }

    private void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {

            Entity vehicle = event.player.getVehicle();

            if (vehicle !=  null &&vehicle.getType().is(ModTags.EntityTypes.SHARKS)) {
                event.player.setForcedPose(Pose.SWIMMING);
            } else if (DiveSuitEligibility.evaluate(event.player).eligible()) {
                event.player.setForcedPose(event.player.isCrouching() ? Pose.CROUCHING : Pose.STANDING);
            } else {
                event.player.setForcedPose(null);
            }

            DiveOxygenManager.tick(event.player);

            BensFintasticSharks.playerTick(event.player);

            // QoL: a trail of bubbles behind the player while swimming through water.
            if (tfar.bensfintasticsharks.config.BfsConfig.COMMON.swimBubbles.get()
                    && event.player.isInWater()
                    && event.player.getDeltaMovement().lengthSqr() > 0.01
                    && event.player.tickCount % 3 == 0
                    && event.player.level() instanceof net.minecraft.server.level.ServerLevel sl) {
                net.minecraft.world.phys.Vec3 look = event.player.getLookAngle();
                sl.sendParticles(net.minecraft.core.particles.ParticleTypes.BUBBLE,
                        event.player.getX() - look.x * 0.4,
                        event.player.getY() + 0.4,
                        event.player.getZ() - look.z * 0.4,
                        2, 0.15, 0.15, 0.15, 0.0);
            }
        }
    }

    private void onPlayerLoggedOut(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        releaseGrabbedPlayer(event.getEntity());
    }

    private void onPlayerChangedDimension(net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent event) {
        releaseGrabbedPlayer(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer player) DiveOxygenManager.syncNow(player);
    }

    private void onPlayerRespawn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        releaseGrabbedPlayer(event.getEntity());
        if (event.getEntity() instanceof ServerPlayer player) DiveOxygenManager.syncNow(player);
    }

    private void onPlayerLoggedIn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) DiveOxygenManager.syncNow(player);
    }

    private void onPlayerClone(PlayerEvent.Clone event) {
        DiveOxygenManager.copy(event.getOriginal(), event.getEntity());
    }

    private void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        DiveSuitEligibility.Result eligibility = DiveSuitEligibility.evaluate(player);
        float before = event.getNewSpeed();
        float after = before;
        String reason = "ineligible";
        if (eligibility.eligible() && player.isEyeInFluid(FluidTags.WATER)
                && !EnchantmentHelper.hasAquaAffinity(player)) {
            after = before * 5.0F;
            event.setNewSpeed(after);
            reason = "removed_underwater_penalty";
        }
        if (player instanceof ServerPlayer serverPlayer) {
            BlockState state = event.getState();
            BlockPos pos = event.getPosition().orElse(serverPlayer.blockPosition());
            BfsDebugManager.recordDiveWork(serverPlayer, "break_speed", state, pos, before, after,
                    "observed", reason);
        }
    }

    private void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer serverPlayer) {
            BlockState state = event.getState();
            BfsDebugManager.recordDiveWork(serverPlayer, "break", state, event.getPos(),
                    0.0F, 0.0F, event.isCanceled() ? "denied" : "accepted",
                    event.isCanceled() ? "event_cancelled" : "break_requested");
        }
    }

    private void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            BfsDebugManager.recordDiveWork(serverPlayer, "place", event.getPlacedBlock(), event.getPos(),
                    0.0F, 0.0F, event.isCanceled() ? "denied" : "accepted",
                    event.isCanceled() ? "event_cancelled" : "placed");
        }
    }

    private void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof SharkGrabber grabber) {
            grabber.releaseGrabPassengers();
        } else if (entity.isPassenger() && event.getLevel() instanceof net.minecraft.server.level.ServerLevel level) {
            // EntityLeaveLevelEvent can fire while the chunk distance manager is iterating its
            // tracking set. Dismount after that callback so the relationship is cleaned without
            // mutating the set that is currently being traversed.
            level.getServer().execute(() -> {
                if (entity.isPassenger()) entity.stopRiding();
            });
        }
    }

    private void onServerStopping(ServerStoppingEvent event) {
        for (net.minecraft.server.level.ServerLevel level : event.getServer().getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof SharkGrabber grabber) {
                    grabber.releaseGrabPassengers();
                }
            }
        }
        tfar.bensfintasticsharks.entity.SpeciesSettingsService.resetSession();
    }

    private void releaseGrabbedPlayer(net.minecraft.world.entity.player.Player player) {
        Entity vehicle = player.getVehicle();
        if (vehicle instanceof SharkGrabber grabber) {
            grabber.releaseGrabPassengers();
        } else if (player.isPassenger()) {
            player.stopRiding();
        }
    }

    private void onRegisterCommands(net.minecraftforge.event.RegisterCommandsEvent event) {
        tfar.bensfintasticsharks.command.BfsCommands.register(event.getDispatcher());
    }

    private void onEntityJoin(net.minecraftforge.event.entity.EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof net.minecraft.world.entity.LivingEntity le)) return;
        var id = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(le.getType());
        if (id == null || !BensFintasticSharks.MOD_ID.equals(id.getNamespace())) return;
        applySpeciesAttributes(le);
    }

    private void applySpeciesAttributes(net.minecraft.world.entity.LivingEntity entity) {
        var data = entity.getPersistentData();
        var health = entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
        if (health != null) {
            double base = data.contains("BfsBaseMaxHealth")
                    ? data.getDouble("BfsBaseMaxHealth") : health.getBaseValue();
            data.putDouble("BfsBaseMaxHealth", base);
            double previousMax = health.getValue();
            double fraction = previousMax <= 0 ? 1.0 : entity.getHealth() / previousMax;
            double multiplier = tfar.bensfintasticsharks.entity.SpeciesSettingsService.valueFor(entity,
                    tfar.bensfintasticsharks.entity.SpeciesSettingsService.Field.HEALTH_MULTIPLIER, 1.0D);
            health.setBaseValue(base * multiplier);
            entity.setHealth((float) Math.max(0.0D, Math.min(health.getValue(), health.getValue() * fraction)));
        }

        var damage = entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            double base = data.contains("BfsBaseAttackDamage")
                    ? data.getDouble("BfsBaseAttackDamage") : damage.getBaseValue();
            data.putDouble("BfsBaseAttackDamage", base);
            double multiplier = tfar.bensfintasticsharks.entity.SpeciesSettingsService.valueFor(entity,
                    tfar.bensfintasticsharks.entity.SpeciesSettingsService.Field.DAMAGE_MULTIPLIER, 1.0D);
            damage.setBaseValue(base * multiplier);
        }

        var knockback = entity.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE);
        if (knockback != null) {
            double base = data.contains("BfsBaseKnockbackResistance")
                    ? data.getDouble("BfsBaseKnockbackResistance") : knockback.getBaseValue();
            data.putDouble("BfsBaseKnockbackResistance", base);
            double configured = tfar.bensfintasticsharks.entity.SpeciesSettingsService.valueFor(entity,
                    tfar.bensfintasticsharks.entity.SpeciesSettingsService.Field.KNOCKBACK_RESISTANCE, base);
            knockback.setBaseValue(Math.max(0.0D, Math.min(1.0D, configured)));
        }
    }

    private void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (event.getEntity() instanceof SharkGrabber grabber) {
            grabber.releaseGrabPassengers();
        } else if (event.getEntity().isPassenger()) {
            event.getEntity().stopRiding();
        }
        if (!event.getEntity().getType().is(ModTags.EntityTypes.CONSERVATION_PROTECTED)) return;
        if (!tfar.bensfintasticsharks.config.BfsConfig.COMMON.conservationDebuffEnabled.get()) return;
        if (event.getSource().getEntity() instanceof Player player && !player.isCreative() && !player.isSpectator()) {
            player.addEffect(new MobEffectInstance(
                    ModMobEffects.RESPECT_THE_OCEAN,
                    2400, // 2 minutes
                    0,
                    false, // ambient
                    false, // visible particles
                    true   // show icon
            ));
        }
    }

    private void onCommonSetup(net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            tfar.bensfintasticsharks.spawn.MobCapManager.validateVanillaFishReplacementCategories();
            applyCategoryCapsFromConfig();
            syncSharkMultsFromConfig();
            tfar.bensfintasticsharks.config.SpeciesSettingsConfigBridge.reload();
        });
    }

    private void syncSharkMultsFromConfig() {
        var cfg = tfar.bensfintasticsharks.config.BfsConfig.COMMON;
        tfar.bensfintasticsharks.entity.AbstractSharkEntity.globalDetectionMult = cfg.sharkDetectionRadiusMult.get().floatValue();
        tfar.bensfintasticsharks.entity.AbstractSharkEntity.globalDisengageDistanceMult = cfg.sharkDisengageDistanceMult.get().floatValue();
        tfar.bensfintasticsharks.entity.BfsAquaticEntity.globalJellyfishDamageMult = cfg.jellyfishDamageMult.get().floatValue();
    }

    private void applyCategoryCapsFromConfig() {
        try {
            java.lang.reflect.Field f = findMobCategoryMaxField();
            if (f == null) {
                BensFintasticSharks.LOG.warn("Could not locate MobCategory#max via any mapping; BFS category caps will use creation-time defaults (5/15/25). Per-species caps still apply via MobCapManager.");
                return;
            }
            f.setAccessible(true);
            f.setInt(tfar.bensfintasticsharks.init.ModMobCategories.APEX_PREDATOR,
                    tfar.bensfintasticsharks.config.BfsConfig.COMMON.apexPredatorCap.get());
            f.setInt(tfar.bensfintasticsharks.init.ModMobCategories.BFS_WATER_CREATURE,
                    tfar.bensfintasticsharks.config.BfsConfig.COMMON.bfsWaterCreatureCap.get());
            f.setInt(tfar.bensfintasticsharks.init.ModMobCategories.BFS_WATER_AMBIENT,
                    tfar.bensfintasticsharks.config.BfsConfig.COMMON.bfsWaterAmbientCap.get());
        } catch (Throwable t) {
            BensFintasticSharks.LOG.warn("Could not apply config caps to BFS mob categories", t);
        }
    }

    /**
     * Resolves the {@link net.minecraft.world.entity.MobCategory} max-count field across
     * mapping configurations. In dev/parchment the field is {@code max}; in production
     * the runtime jar exposes it under its SRG name {@code f_21422_}. As a last resort
     * we walk declared int fields and pick the first one (it's the first int declared
     * on the enum), so the lookup keeps working even if SRG names shift in the future.
     */
    private static java.lang.reflect.Field findMobCategoryMaxField() {
        Class<?> cls = net.minecraft.world.entity.MobCategory.class;
        // Try mojmap / parchment name first.
        try { return cls.getDeclaredField("max"); } catch (NoSuchFieldException ignored) {}
        // Forge's runtime uses SRG names for vanilla classes.
        try { return cls.getDeclaredField("f_21422_"); } catch (NoSuchFieldException ignored) {}
        // Last resort: the max field is the first declared int on the enum.
        for (java.lang.reflect.Field candidate : cls.getDeclaredFields()) {
            if (candidate.getType() == int.class && !java.lang.reflect.Modifier.isStatic(candidate.getModifiers())) {
                return candidate;
            }
        }
        return null;
    }

    private void trading(VillagerTradesEvent event) {
        VillagerProfession type = event.getType();
        if (type == VillagerProfession.FISHERMAN) {
            // Shark byproducts are rare MASTER-tier (level 5) goods. We MERGE into the
            // existing master pool instead of overwriting level 1 (the old code wiped the
            // vanilla novice trades).
            //
            // 0.18 review fix: vanilla draws exactly TWO listings from this pool and does
            // NOT redraw when a listing's getOffer returns null. The previous shape — a
            // dozen independent chance-gated listings — meant BFS entries crowded out the
            // two vanilla master trades and then usually rolled null, leaving most master
            // fishermen with zero level-5 trades and making every BFS item several times
            // rarer than intended. One composite listing that ALWAYS yields exactly one
            // weighted offer keeps the pool at three: ~2/3 of master fishermen carry one
            // shark byproduct next to their vanilla trades, ~1/3 carry none.
            List<VillagerTrades.ItemListing> master = new ArrayList<>(event.getTrades().getOrDefault(5, List.of()));
            master.add(BfsFishermanTrades.WEIGHTED_LISTING);
            event.getTrades().put(5, master);
        }
    }

    private void attributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.GREAT_WHITE_SHARK, GreatWhiteSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.GREAT_HAMMERHEAD_SHARK, GreatHammerheadSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.COMMON_THRESHER_SHARK, CommonThresherSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.HARBOR_SEAL, HarborSealEntity.createAttributes().build());
        event.put(ModEntityTypes.COMMON_STINGRAY, CommonStingrayEntityForge.createAttributes().build());
        event.put(ModEntityTypes.SHORTFIN_MAKO_SHARK, ShortfinMakoSharkEntityForge.createAttributes().build());

        // Legacy 1.0 new mobs
        event.put(ModEntityTypes.TIGER_SHARK, TigerSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.OCEANIC_WHITETIP_SHARK, OceanicWhitetipSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.SANDTIGER_SHARK, SandtigerSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.BLACKTIP_REEF_SHARK, BlacktipReefSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.BOTTLENOSE_DOLPHIN, BottlenoseDolphinEntity.createAttributes().build());
        event.put(ModEntityTypes.ORCA, OrcaEntity.createAttributes().build());
        event.put(ModEntityTypes.COMMON_OCTOPUS, CommonOctopusEntity.createAttributes().build());
        event.put(ModEntityTypes.CARIBBEAN_REEF_OCTOPUS, CaribbeanReefOctopusEntity.createAttributes().build());
        event.put(ModEntityTypes.NAUTILUS, NautilusEntity.createAttributes().build());
        event.put(ModEntityTypes.GIANT_MORAY_EEL, GiantMorayEelEntity.createAttributes().build());
        event.put(ModEntityTypes.GREEN_SEA_TURTLE, GreenSeaTurtleEntity.createAttributes().build());
        event.put(ModEntityTypes.AMERICAN_LOBSTER, AmericanLobsterEntity.createAttributes().build());
        event.put(ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH, BlackSeaNettleJellyfishEntity.createAttributes().build());
        event.put(ModEntityTypes.CANNONBALL_JELLYFISH, CannonballJellyfishEntity.createAttributes().build());
        event.put(ModEntityTypes.ATLANTIC_COD, AtlanticCodEntity.createAttributes().build());
        event.put(ModEntityTypes.ATLANTIC_SALMON, AtlanticSalmonEntity.createAttributes().build());
    }

    public static Map<Registry<?>, List<Pair<ResourceLocation, Supplier<?>>>> registerLater = new HashMap<>();
    private void register(RegisterEvent e) {
        for (Map.Entry<Registry<?>,List<Pair<ResourceLocation, Supplier<?>>>> entry : registerLater.entrySet()) {
            Registry<?> registry = entry.getKey();
            List<Pair<ResourceLocation, Supplier<?>>> toRegister = entry.getValue();
            for (Pair<ResourceLocation,Supplier<?>> pair : toRegister) {
                e.register((ResourceKey<? extends Registry<Object>>)registry.key(),pair.getLeft(),(Supplier<Object>)pair.getValue());
            }
        }
        e.register(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BensFintasticSharks.id("add_item_chance"),() -> LootModifiers.ADD_ITEM_CHANCE);
        e.register(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BensFintasticSharks.id("add_once_per_world"),() -> LootModifiers.ADD_ONCE_PER_WORLD);
        e.register(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, BensFintasticSharks.id("replace_fishing_fish"),() -> LootModifiers.REPLACE_FISHING_FISH);
    }

}
