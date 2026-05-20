package tfar.bensfintasticsharks.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.CannonballJellyfishEntityForge;

public class CannonballJellyfishRenderer extends GeoEntityRenderer<CannonballJellyfishEntityForge> {

    private static final ResourceLocation TEXTURE = BensFintasticSharks.id("textures/entity/cannonball_jellyfish/default_1.png");

    public CannonballJellyfishRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("cannonball_jellyfish")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(CannonballJellyfishEntityForge entity) {
        return TEXTURE;
    }
}
