package tfar.bensfintasticsharks.client;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraft.client.resources.model.ModelResourceLocation;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.client.renderer.*;
import tfar.bensfintasticsharks.entity.*;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.item.SharkCodexItem;

public class ModClientForge {

    public static void init(IEventBus bus) {
        bus.addListener(ModClientForge::renderers);
        bus.addListener(ModClientForge::setup);
        bus.addListener(ModClientForge::registerAdditionalModels);
        // 0.18 — grab/thrash screen effects (camera lock + red tint) live on the
        // runtime Forge bus; init() is only reached on the client dist.
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new GrabClientHandler());
    }

    static void registerAdditionalModels(final ModelEvent.RegisterAdditional event) {
        // The thrown-trident renderer fetches this 3D model by name. It's only referenced
        // as a perspective parent in the item model, so it isn't baked as a standalone
        // top-level model and getModel() returned the missing-texture placeholder (the
        // "missing texture block" seen on a stuck trident). Register it so it's baked.
        event.register(new ModelResourceLocation(BensFintasticSharks.id("shark_trident_3d"), "inventory"));
    }

    static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(ModClient::registerRenderers);
        // Wire the Shark Codex book-reader opener. The common SharkCodexItem keeps a
        // no-op Consumer by default so the dedicated server can load that class
        // without touching client-only GUI types.
        SharkCodexItem.CLIENT_OPENER = SharkCodexClient::openReader;
    }

    static void renderers(final EntityRenderersEvent.RegisterRenderers event) {
        EntityRenderers.register((EntityType<GreatWhiteSharkEntityForge>) ModEntityTypes.GREAT_WHITE_SHARK, GreatWhiteRenderer::new);
        EntityRenderers.register((EntityType<GreatHammerheadSharkEntityForge>) ModEntityTypes.GREAT_HAMMERHEAD_SHARK, GreatHammerheadRenderer::new);
        EntityRenderers.register((EntityType<CommonThresherSharkEntityForge>) ModEntityTypes.COMMON_THRESHER_SHARK, CommonThresherRenderer::new);
        EntityRenderers.register((EntityType<HarborSealEntityForge>) ModEntityTypes.HARBOR_SEAL, HarborSealRenderer::new);
        EntityRenderers.register((EntityType<CommonStingrayEntityForge>) ModEntityTypes.COMMON_STINGRAY, CommonStingrayRenderer::new);
        EntityRenderers.register((EntityType<ShortfinMakoSharkEntityForge>) ModEntityTypes.SHORTFIN_MAKO_SHARK, ShortfinMakoRenderer::new);
        EntityRenderers.register(ModEntityTypes.SHARK_TRIDENT,ThrownSharkTridentRenderer::new);

        // Legacy 1.0 new mob renderers
        EntityRenderers.register((EntityType<TigerSharkEntityForge>) ModEntityTypes.TIGER_SHARK, TigerSharkRenderer::new);
        EntityRenderers.register((EntityType<OceanicWhitetipSharkEntityForge>) ModEntityTypes.OCEANIC_WHITETIP_SHARK, OceanicWhitetipSharkRenderer::new);
        EntityRenderers.register((EntityType<SandtigerSharkEntityForge>) ModEntityTypes.SANDTIGER_SHARK, SandtigerSharkRenderer::new);
        EntityRenderers.register((EntityType<BlacktipReefSharkEntityForge>) ModEntityTypes.BLACKTIP_REEF_SHARK, BlacktipReefSharkRenderer::new);
        EntityRenderers.register((EntityType<BottlenoseDolphinEntityForge>) ModEntityTypes.BOTTLENOSE_DOLPHIN, BottlenoseDolphinRenderer::new);
        EntityRenderers.register((EntityType<OrcaEntityForge>) ModEntityTypes.ORCA, OrcaRenderer::new);
        EntityRenderers.register((EntityType<CommonOctopusEntityForge>) ModEntityTypes.COMMON_OCTOPUS, CommonOctopusRenderer::new);
        EntityRenderers.register((EntityType<CaribbeanReefOctopusEntityForge>) ModEntityTypes.CARIBBEAN_REEF_OCTOPUS, CaribbeanReefOctopusRenderer::new);
        EntityRenderers.register((EntityType<NautilusEntityForge>) ModEntityTypes.NAUTILUS, NautilusRenderer::new);
        EntityRenderers.register((EntityType<GiantMorayEelEntityForge>) ModEntityTypes.GIANT_MORAY_EEL, GiantMorayEelRenderer::new);
        EntityRenderers.register((EntityType<GreenSeaTurtleEntityForge>) ModEntityTypes.GREEN_SEA_TURTLE, GreenSeaTurtleRenderer::new);
        EntityRenderers.register((EntityType<AmericanLobsterEntityForge>) ModEntityTypes.AMERICAN_LOBSTER, AmericanLobsterRenderer::new);
        EntityRenderers.register((EntityType<BlackSeaNettleJellyfishEntityForge>) ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH, BlackSeaNettleJellyfishRenderer::new);
        EntityRenderers.register((EntityType<CannonballJellyfishEntityForge>) ModEntityTypes.CANNONBALL_JELLYFISH, CannonballJellyfishRenderer::new);
        EntityRenderers.register((EntityType<AtlanticCodEntityForge>) ModEntityTypes.ATLANTIC_COD, AtlanticCodRenderer::new);
        EntityRenderers.register((EntityType<AtlanticSalmonEntityForge>) ModEntityTypes.ATLANTIC_SALMON, AtlanticSalmonRenderer::new);
    }

}
