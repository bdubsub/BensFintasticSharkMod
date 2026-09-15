package tfar.bensfintasticsharks.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import tfar.bensfintasticsharks.entity.ConditionalGlowing;

/**
 * Renders the authored glow mask as a second pass without replacing the base texture.
 * GeckoLib's automatic glow texture derives a mask by mutating the base image, which
 * removes Zippy's daylight markings when the conditional glow pass is skipped.
 */
public class ConditionalAutoGlowingLayer<T extends GeoAnimatable & ConditionalGlowing> extends GeoRenderLayer<T> {
    public ConditionalAutoGlowingLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        if (animatable.hasGlowingLayer()) {
            ResourceLocation glowMask = glowMask(getTextureResource(animatable));
            RenderType glowRenderType = RenderType.eyes(glowMask);
            VertexConsumer glowBuffer = bufferSource.getBuffer(glowRenderType);
            getRenderer().reRender(bakedModel, poseStack, bufferSource, animatable, glowRenderType, glowBuffer,
                    partialTick, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        }
    }

    static ResourceLocation glowMask(ResourceLocation texture) {
        String path = texture.getPath();
        int extension = path.lastIndexOf('.');
        String maskPath = extension < 0
                ? path + "_glowmask"
                : path.substring(0, extension) + "_glowmask" + path.substring(extension);
        return new ResourceLocation(texture.getNamespace(), maskPath);
    }
}
