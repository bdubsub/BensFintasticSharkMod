package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.GreatHammerheadSharkEntity;
import tfar.bensfintasticsharks.entity.GreatHammerheadSharkEntityForge;

import java.util.Locale;
import java.util.Map;

public class GreatHammerheadRenderer extends GeoEntityRenderer<GreatHammerheadSharkEntityForge> {

    private static final Map<GreatHammerheadSharkEntity.Variant, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        for(GreatHammerheadSharkEntity.Variant variant : GreatHammerheadSharkEntity.Variant.values()) {
            map.put(variant, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/great_hammerhead_shark/%s.png", variant.getName())));
        }
    });


    public GreatHammerheadRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("great_hammerhead_shark")));
    }

    @Override
    public ResourceLocation getTextureLocation(GreatHammerheadSharkEntityForge animatable) {
        return TEXTURE_BY_TYPE.get(animatable.getVariant());
    }

    @Override
    protected float getDeathMaxRotation(GreatHammerheadSharkEntityForge animatable) {
        return 0;
    }
    @Override
    public void preRender(com.mojang.blaze3d.vertex.PoseStack poseStack, GreatHammerheadSharkEntityForge animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float s = animatable.getBfsScale();
        if (Math.abs(s - 1.0f) > 0.001f) poseStack.scale(s, s, s);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
