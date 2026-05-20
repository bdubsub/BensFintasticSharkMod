package tfar.bensfintasticsharks.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.CaribbeanReefOctopusEntityForge;

public class CaribbeanReefOctopusRenderer extends GeoEntityRenderer<CaribbeanReefOctopusEntityForge> {

    private static final ResourceLocation TEXTURE = BensFintasticSharks.id("textures/entity/caribbean_reef_octopus/default_1.png");

    public CaribbeanReefOctopusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("caribbean_reef_octopus")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(CaribbeanReefOctopusEntityForge entity) {
        return TEXTURE;
    }
}
