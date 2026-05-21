package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.client.renderer.layer.ConditionalAutoGlowingLayer;
import tfar.bensfintasticsharks.entity.GreatWhiteSharkEntity;
import tfar.bensfintasticsharks.entity.GreatWhiteSharkEntityForge;

import java.util.Locale;
import java.util.Map;

public class GreatWhiteRenderer extends GeoEntityRenderer<GreatWhiteSharkEntityForge> {

    private static final Map<GreatWhiteSharkEntity.Variant, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        for(GreatWhiteSharkEntity.Variant variant : GreatWhiteSharkEntity.Variant.values()) {
            map.put(variant, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/great_white/%s.png", variant.getName())));
        }
    });


    public GreatWhiteRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("great_white")));
        addRenderLayer(new ConditionalAutoGlowingLayer<>(this));
    }

    @Override
    public void actuallyRender(PoseStack poseStack, GreatWhiteSharkEntityForge animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
      //  if (animatable.isSharkinator()) {
      //      packedLight = 0xffffff;
      //  }
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ResourceLocation getTextureLocation(GreatWhiteSharkEntityForge animatable) {
        return TEXTURE_BY_TYPE.get(animatable.getVariant());
    }


    @Override
    protected float getDeathMaxRotation(GreatWhiteSharkEntityForge entityLivingBaseIn) {
        return 0;
    }

    @Override
    public void preRender(com.mojang.blaze3d.vertex.PoseStack poseStack, GreatWhiteSharkEntityForge animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float s = animatable.getBfsScale();
        if (Math.abs(s - 1.0f) > 0.001f) poseStack.scale(s, s, s);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
