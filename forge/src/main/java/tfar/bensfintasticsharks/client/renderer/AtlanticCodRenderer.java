package tfar.bensfintasticsharks.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.AtlanticCodEntityForge;

public class AtlanticCodRenderer extends GeoEntityRenderer<AtlanticCodEntityForge> {

    private static final ResourceLocation TEXTURE = BensFintasticSharks.id("textures/entity/atlantic_cod.png");

    public AtlanticCodRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("atlantic_cod")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(AtlanticCodEntityForge entity) {
        return TEXTURE;
    }
}
