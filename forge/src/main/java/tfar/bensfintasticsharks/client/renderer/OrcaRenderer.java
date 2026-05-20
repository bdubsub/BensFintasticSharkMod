package tfar.bensfintasticsharks.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.OrcaEntityForge;

public class OrcaRenderer extends GeoEntityRenderer<OrcaEntityForge> {

    private static final ResourceLocation TEXTURE = BensFintasticSharks.id("textures/entity/orca/default_1.png");

    public OrcaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("orca")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(OrcaEntityForge entity) {
        return TEXTURE;
    }
}
