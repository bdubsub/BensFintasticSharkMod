package tfar.bensfintasticsharks.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.AtlanticSalmonEntityForge;

public class AtlanticSalmonRenderer extends GeoEntityRenderer<AtlanticSalmonEntityForge> {

    private static final ResourceLocation TEXTURE = BensFintasticSharks.id("textures/entity/atlantic_salmon.png");

    public AtlanticSalmonRenderer(EntityRendererProvider.Context context) {
        super(context, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("atlantic_salmon")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(AtlanticSalmonEntityForge entity) {
        return TEXTURE;
    }

    @Override
    protected void applyRotations(AtlanticSalmonEntityForge entity, com.mojang.blaze3d.vertex.PoseStack poseStack,
                                  float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(entity, poseStack, ageInTicks, rotationYaw, partialTick);
        AquaticRenderTransforms.applySwimPitch(poseStack, entity, partialTick);
    }
}
