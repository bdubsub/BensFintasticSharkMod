package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import java.util.Locale;
import java.util.Map;
import tfar.bensfintasticsharks.entity.BlacktipReefSharkEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.BlacktipReefSharkEntityForge;

public class BlacktipReefSharkRenderer extends GeoEntityRenderer<BlacktipReefSharkEntityForge> {

    private static final Map<BlacktipReefSharkEntity.Variant, ResourceLocation> TEXTURE_BY_VARIANT = Util.make(Maps.newHashMap(), m -> {
        for (BlacktipReefSharkEntity.Variant v : BlacktipReefSharkEntity.Variant.values()) {
            m.put(v, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/blacktip_reef_shark/%s.png", v.getName())));
        }
    });

        public BlacktipReefSharkRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("blacktip_reef_shark")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(BlacktipReefSharkEntityForge entity) {
        return TEXTURE_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    protected float getDeathMaxRotation(BlacktipReefSharkEntityForge e) {
        return 0;
    }
}
