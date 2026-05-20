package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import java.util.Locale;
import java.util.Map;
import tfar.bensfintasticsharks.entity.GreenSeaTurtleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.GreenSeaTurtleEntityForge;

public class GreenSeaTurtleRenderer extends GeoEntityRenderer<GreenSeaTurtleEntityForge> {

    private static final Map<GreenSeaTurtleEntity.Variant, ResourceLocation> TEXTURE_BY_VARIANT = Util.make(Maps.newHashMap(), m -> {
        for (GreenSeaTurtleEntity.Variant v : GreenSeaTurtleEntity.Variant.values()) {
            m.put(v, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/green_sea_turtle/%s.png", v.getName())));
        }
    });

        public GreenSeaTurtleRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("green_sea_turtle")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(GreenSeaTurtleEntityForge entity) {
        return TEXTURE_BY_VARIANT.get(entity.getVariant());
    }
}
