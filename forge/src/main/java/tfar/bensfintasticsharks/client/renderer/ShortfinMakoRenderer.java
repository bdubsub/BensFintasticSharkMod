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
}
