package tfar.bensfintasticsharks;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import tfar.bensfintasticsharks.client.ModClientForge;
import tfar.bensfintasticsharks.datagen.ModDatagen;
import tfar.bensfintasticsharks.disturbance.WaterDisturbanceListeners;
import tfar.bensfintasticsharks.entity.*;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModMobEffects;
import tfar.bensfintasticsharks.init.ModTags;
import tfar.bensfintasticsharks.trade.BfsFishermanTrades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod(BensFintasticSharks.MOD_ID)
public class BensFintasticSharksForge {
    
    public BensFintasticSharksForge() {
    
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
        MinecraftForge.EVENT_BUS.addListener(this::trading);
        MinecraftForge.EVENT_BUS.addListener(this::onLivingDeath);
        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.addListener(this::onEntityJoin);
        MinecraftForge.EVENT_BUS.register(new tfar.bensfintasticsharks.spawn.MobCapManager());
        WaterDisturbanceListeners.register(MinecraftForge.EVENT_BUS);
        tfar.bensfintasticsharks.debug.BfsDebugManager.register(MinecraftForge.EVENT_BUS);
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
            } else {
                event.player.setForcedPose(null);
            }



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
    }

    private void onPlayerRespawn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        releaseGrabbedPlayer(event.getEntity());
    }

    private void releaseGrabbedPlayer(net.minecraft.world.entity.player.Player player) {
        if (player.isPassenger()) {
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
        String speciesPath = id.getPath();
        var cfg = tfar.bensfintasticsharks.config.BfsConfig.COMMON;
        boolean isShark = le.getType().is(ModTags.EntityTypes.SHARKS);

        // Per-species HP. Folds in the global shark_hp_mult for sharks so users can scale
        // every shark with one knob.
        var hpCfg = cfg.speciesHpMult.get(speciesPath);
        if (hpCfg != null) {
            double mult = hpCfg.get();
            if (isShark) mult *= cfg.sharkHpMult.get();
            if (Math.abs(mult - 1.0) > 1e-6) {
                var hp = le.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH);
                if (hp != null) {
                    double newMax = hp.getBaseValue() * mult;
                    hp.setBaseValue(newMax);
                    le.setHealth((float) newMax);
                }
            }
        }

        // Per-species damage. Same shark-rollup.
        var dmgCfg = cfg.speciesDamageMult.get(speciesPath);
        if (dmgCfg != null) {
            double mult = dmgCfg.get();
            if (isShark) mult *= cfg.sharkDamageMult.get();
            if (Math.abs(mult - 1.0) > 1e-6) {
                var dmg = le.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                if (dmg != null) dmg.setBaseValue(dmg.getBaseValue() * mult);
            }
        }

        // Per-species knockback resistance. -1 sentinel = leave default attribute alone,
        // except for orca where the legacy single-value config still applies.
        var kbCfg = cfg.speciesKnockbackResistance.get(speciesPath);
        if (kbCfg != null) {
            double v = kbCfg.get();
            if (v >= 0) {
                var kb = le.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE);
                if (kb != null) kb.setBaseValue(v);
            } else if (le.getType() == ModEntityTypes.ORCA) {
                var kb = le.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE);
                if (kb != null) kb.setBaseValue(cfg.orcaKnockbackResistance.get());
            }
        }
    }

    private void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;
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
    }

}
