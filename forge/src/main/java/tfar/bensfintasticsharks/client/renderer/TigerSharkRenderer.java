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
}
