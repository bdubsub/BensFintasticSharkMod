package tfar.bensfintasticsharks.client;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tfar.bensfintasticsharks.client.renderer.*;
import tfar.bensfintasticsharks.entity.*;
import tfar.bensfintasticsharks.init.ModEntityTypes;

public class ModClientForge {

    public static void init(IEventBus bus) {
        bus.addListener(ModClientForge::renderers);
        bus.addListener(ModClientForge::setup);
    }

    static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(ModClient::registerRenderers);
    }

    static void renderers(final EntityRenderersEvent.RegisterRenderers event) {
        EntityRenderers.register((EntityType<GreatWhiteSharkEntityForge>) ModEntityTypes.GREAT_WHITE_SHARK, GreatWhiteRenderer::new);
        EntityRenderers.register((EntityType<GreatHammerheadSharkEntityForge>) ModEntityTypes.GREAT_HAMMERHEAD_SHARK, GreatHammerheadRenderer::new);
        EntityRenderers.register((EntityType<CommonThresherSharkEntityForge>) ModEntityTypes.COMMON_THRESHER_SHARK, CommonThresherRenderer::new);
        EntityRenderers.register((EntityType<HarborSealEntityForge>) ModEntityTypes.HARBOR_SEAL, HarborSealRenderer::new);
        EntityRenderers.register((EntityType<CommonStingrayEntityForge>) ModEntityTypes.COMMON_STINGRAY, CommonStingrayRenderer::new);
        EntityRenderers.register((EntityType<ShortfinMakoSharkEntityForge>) ModEntityTypes.SHORTFIN_MAKO_SHARK, ShortfinMakoRenderer::new);
        EntityRenderers.register(ModEntityTypes.SHARK_TRIDENT,ThrownSharkTridentRenderer::new);
    }

}
