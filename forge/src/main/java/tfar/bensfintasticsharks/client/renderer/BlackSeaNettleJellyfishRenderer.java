package tfar.bensfintasticsharks.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.BlackSeaNettleJellyfishEntityForge;

public class BlackSeaNettleJellyfishRenderer extends GeoEntityRenderer<BlackSeaNettleJellyfishEntityForge> {

    private static final ResourceLocation TEXTURE = BensFintasticSharks.id("textures/entity/black_sea_nettle_jellyfish/default_1.png");

    public BlackSeaNettleJellyfishRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("black_sea_nettle_jellyfish")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BlackSeaNettleJellyfishEntityForge entity) {
        return TEXTURE;
    }
    @Override
    public void preRender(com.mojang.blaze3d.vertex.PoseStack poseStack, BlackSeaNettleJellyfishEntityForge animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float s = animatable.getBfsScale();
        if (Math.abs(s - 1.0f) > 0.001f) poseStack.scale(s, s, s);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
