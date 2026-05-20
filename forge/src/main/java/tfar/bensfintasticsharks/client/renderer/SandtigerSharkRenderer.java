package tfar.bensfintasticsharks.client.renderer;

import com.google.common.collect.Maps;
import net.minecraft.Util;
import java.util.Locale;
import java.util.Map;
import tfar.bensfintasticsharks.entity.SandtigerSharkEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.SandtigerSharkEntityForge;

public class SandtigerSharkRenderer extends GeoEntityRenderer<SandtigerSharkEntityForge> {

    private static final Map<SandtigerSharkEntity.Variant, ResourceLocation> TEXTURE_BY_VARIANT = Util.make(Maps.newHashMap(), m -> {
        for (SandtigerSharkEntity.Variant v : SandtigerSharkEntity.Variant.values()) {
            m.put(v, BensFintasticSharks.id(String.format(Locale.ROOT, "textures/entity/sandtiger_shark/%s.png", v.getName())));
        }
    });

        public SandtigerSharkRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(BensFintasticSharks.id("sandtiger_shark")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(SandtigerSharkEntityForge entity) {
        return TEXTURE_BY_VARIANT.get(entity.getVariant());
    }

    @Override
    protected float getDeathMaxRotation(SandtigerSharkEntityForge e) {
        return 0;
    }
}
