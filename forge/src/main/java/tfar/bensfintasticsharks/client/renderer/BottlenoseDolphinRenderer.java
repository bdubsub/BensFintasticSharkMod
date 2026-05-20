package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import java.util.Locale;
import java.util.Map;
import tfar.bensfintasticsharks.entity.BottlenoseDolphinEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.BottlenoseDolphinEntityForge;

public class BottlenoseDolphinRenderer extends GeoEntityRenderer<BottlenoseDolphinEntityForge> {

    private static final Map<BottlenoseDolphinEntity.Variant, ResourceLocation> TEXTURE_BY_VARIANT = Util.make(Maps.newHashMap(), m -> {
        for (BottlenoseDolphinEntity.Variant v : BottlenoseDolphinEntity.Variant.values()) {
            m.put(v, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/bottlenose_dolphin/%s.png", v.getName())));
        }
    });

        public BottlenoseDolphinRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("bottlenose_dolphin")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BottlenoseDolphinEntityForge entity) {
        return TEXTURE_BY_VARIANT.get(entity.getVariant());
    }
}
