package tfar.bensfintasticsharks;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
import tfar.bensfintasticsharks.entity.*;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModItems;
import tfar.bensfintasticsharks.init.ModTags;

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
        bus.addListener(ModDatagen::start);
        MinecraftForge.EVENT_BUS.addListener(this::playerTick);
        MinecraftForge.EVENT_BUS.addListener(this::trading);

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
        }
    }

    private void trading(VillagerTradesEvent event) {
        VillagerProfession type = event.getType();
        if (type == VillagerProfession.FISHERMAN) {
            event.getTrades().put(1,List.of(
                    new VillagerTrades.ItemsForEmeralds(ModItems.GREAT_WHITE_SHARK_TOOTH,1,4,10),
                    new VillagerTrades.ItemsForEmeralds(ModItems.GREAT_HAMMERHEAD_SHARK_TOOTH,1,4,10),
                    new VillagerTrades.ItemsForEmeralds(ModItems.COMMON_THRESHER_SHARK_TOOTH,1,4,10),

                    new VillagerTrades.ItemsForEmeralds(ModItems.GREAT_WHITE_SHARK_SKIN,1,4,10),
                    new VillagerTrades.ItemsForEmeralds(ModItems.GREAT_HAMMERHEAD_SHARK_SKIN,1,4,10),
                    new VillagerTrades.ItemsForEmeralds(ModItems.COMMON_THRESHER_SHARK_SKIN,1,4,10),

                    new VillagerTrades.ItemsForEmeralds(ModItems.CARTILAGE,1,6,10)

                    ));
        }
    }

    private void attributes(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.GREAT_WHITE_SHARK, GreatWhiteSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.GREAT_HAMMERHEAD_SHARK, GreatHammerheadSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.COMMON_THRESHER_SHARK, CommonThresherSharkEntity.createAttributes().build());
        event.put(ModEntityTypes.HARBOR_SEAL, HarborSealEntity.createAttributes().build());
        event.put(ModEntityTypes.COMMON_STINGRAY, CommonStingrayEntityForge.createAttributes().build());
        event.put(ModEntityTypes.SHORTFIN_MAKO_SHARK, ShortfinMakoSharkEntityForge.createAttributes().build());
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
    }

}