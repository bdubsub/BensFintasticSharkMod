package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import java.util.Locale;
import java.util.Map;
import tfar.bensfintasticsharks.entity.NautilusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.NautilusEntityForge;

public class NautilusRenderer extends GeoEntityRenderer<NautilusEntityForge> {

    private static final Map<NautilusEntity.Variant, ResourceLocation> TEXTURE_BY_VARIANT = Util.make(Maps.newHashMap(), m -> {
        for (NautilusEntity.Variant v : NautilusEntity.Variant.values()) {
            m.put(v, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/nautilus/%s.png", v.getName())));
        }
    });

        public NautilusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("nautilus")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(NautilusEntityForge entity) {
        return TEXTURE_BY_VARIANT.get(entity.getVariant());
    }
    @Override
    public void preRender(com.mojang.blaze3d.vertex.PoseStack poseStack, NautilusEntityForge animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float s = animatable.getBfsScale();
        if (Math.abs(s - 1.0f) > 0.001f) poseStack.scale(s, s, s);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
