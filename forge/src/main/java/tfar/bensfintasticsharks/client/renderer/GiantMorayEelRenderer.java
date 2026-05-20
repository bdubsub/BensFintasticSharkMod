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
}
