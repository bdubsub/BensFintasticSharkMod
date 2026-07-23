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
    @Override
    public void preRender(com.mojang.blaze3d.vertex.PoseStack poseStack, CaribbeanReefOctopusEntityForge animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float s = animatable.getBfsScale();
        if (Math.abs(s - 1.0f) > 0.001f) poseStack.scale(s, s, s);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected void applyRotations(CaribbeanReefOctopusEntityForge animatable, com.mojang.blaze3d.vertex.PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        // Squid-style lean: tilt the upright-authored model toward the swim direction
        // (after the body yaw, around mid-height) so horizontal travel reads as
        // swimming instead of an upright vertical drift.
        float pitch = net.minecraft.util.Mth.lerp(partialTick, animatable.xBodyRotO, animatable.xBodyRot);
        if (Math.abs(pitch) > 0.01f) {
            float pivot = animatable.getType().getHeight() * 0.5f;
            poseStack.translate(0, pivot, 0);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(pitch));
            poseStack.translate(0, -pivot, 0);
        }
    }
}
