package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import java.util.Locale;
import java.util.Map;
import tfar.bensfintasticsharks.entity.AmericanLobsterEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.AmericanLobsterEntityForge;

public class AmericanLobsterRenderer extends GeoEntityRenderer<AmericanLobsterEntityForge> {

    private static final Map<AmericanLobsterEntity.Variant, ResourceLocation> TEXTURE_BY_VARIANT = Util.make(Maps.newHashMap(), m -> {
        for (AmericanLobsterEntity.Variant v : AmericanLobsterEntity.Variant.values()) {
            m.put(v, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/american_lobster/%s.png", v.getName())));
        }
    });

        public AmericanLobsterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("american_lobster")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(AmericanLobsterEntityForge entity) {
        return TEXTURE_BY_VARIANT.get(entity.getVariant());
    }
}
