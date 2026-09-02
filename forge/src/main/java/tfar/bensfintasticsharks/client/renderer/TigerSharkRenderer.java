package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import java.util.Locale;
import java.util.Map;
import tfar.bensfintasticsharks.entity.TigerSharkEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.TigerSharkEntityForge;

public class TigerSharkRenderer extends GeoEntityRenderer<TigerSharkEntityForge> {

    private static final Map<TigerSharkEntity.Variant, ResourceLocation> TEXTURE_BY_VARIANT = Util.make(Maps.newHashMap(), m -> {
        for (TigerSharkEntity.Variant v : TigerSharkEntity.Variant.values()) {
            m.put(v, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/tiger_shark/%s.png", v.getName())));
        }
    });

        public TigerSharkRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("tiger_shark")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(TigerSharkEntityForge entity) {
        return TEXTURE_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    protected float getDeathMaxRotation(TigerSharkEntityForge e) {
        return 0;
    }
    @Override
    public void preRender(com.mojang.blaze3d.vertex.PoseStack poseStack, TigerSharkEntityForge animatable, software.bernie.geckolib.cache.object.BakedGeoModel model, net.minecraft.client.renderer.MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        float s = animatable.getBfsScale();
        if (Math.abs(s - 1.0f) > 0.001f) poseStack.scale(s, s, s);
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    protected void applyRotations(TigerSharkEntityForge animatable, com.mojang.blaze3d.vertex.PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick) {
        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick);
        AquaticRenderTransforms.applySharkPitch(poseStack, animatable, partialTick);
    }
}
