package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.ShortfinMakoSharkEntityForge;
import tfar.bensfintasticsharks.entity.ShortfinMakoSharkEntity;

import java.util.Locale;
import java.util.Map;

public class ShortfinMakoRenderer extends GeoEntityRenderer<ShortfinMakoSharkEntityForge> {

    private static final Map<ShortfinMakoSharkEntity.Variant, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        for(ShortfinMakoSharkEntity.Variant variant : ShortfinMakoSharkEntity.Variant.values()) {
            map.put(variant, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/shortfin_mako/%s.png", variant.getName())));
        }
    });

    public ShortfinMakoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("shortfin_mako")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ShortfinMakoSharkEntityForge animatable) {
        return TEXTURE_BY_TYPE.get(animatable.getVariant());
    }

    @Override
    protected float getDeathMaxRotation(ShortfinMakoSharkEntityForge entityLivingBaseIn) {
        return 0;
    }
    @Override
    public void preRender(com.mojang.blaze3d.vertex.PoseStack poseStack, ShortfinMakoSharkEntityForge animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float s = animatable.getBfsScale();
        if (Math.abs(s - 1.0f) > 0.001f) poseStack.scale(s, s, s);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
