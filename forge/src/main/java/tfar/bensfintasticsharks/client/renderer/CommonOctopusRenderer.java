package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import java.util.Locale;
import java.util.Map;
import tfar.bensfintasticsharks.entity.CommonOctopusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.CommonOctopusEntityForge;

public class CommonOctopusRenderer extends GeoEntityRenderer<CommonOctopusEntityForge> {

    private static final Map<CommonOctopusEntity.Variant, ResourceLocation> TEXTURE_BY_VARIANT = Util.make(Maps.newHashMap(), m -> {
        for (CommonOctopusEntity.Variant v : CommonOctopusEntity.Variant.values()) {
            m.put(v, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/common_octopus/%s.png", v.getName())));
        }
    });

        public CommonOctopusRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("common_octopus")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(CommonOctopusEntityForge entity) {
        return TEXTURE_BY_VARIANT.get(entity.getVariant());
    }
}
