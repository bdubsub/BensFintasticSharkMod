package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import java.util.Locale;
import java.util.Map;
import tfar.bensfintasticsharks.entity.GiantMorayEelEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.GiantMorayEelEntityForge;

public class GiantMorayEelRenderer extends GeoEntityRenderer<GiantMorayEelEntityForge> {

    private static final Map<GiantMorayEelEntity.Variant, ResourceLocation> TEXTURE_BY_VARIANT = Util.make(Maps.newHashMap(), m -> {
        for (GiantMorayEelEntity.Variant v : GiantMorayEelEntity.Variant.values()) {
            m.put(v, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/giant_moray_eel/%s.png", v.getName())));
        }
    });

        public GiantMorayEelRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("giant_moray_eel")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(GiantMorayEelEntityForge entity) {
        return TEXTURE_BY_VARIANT.get(entity.getVariant());
    }
    @Override
    public void preRender(com.mojang.blaze3d.vertex.PoseStack poseStack, GiantMorayEelEntityForge animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float s = animatable.getBfsScale();
        if (Math.abs(s - 1.0f) > 0.001f) poseStack.scale(s, s, s);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
