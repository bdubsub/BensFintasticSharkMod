package tfar.bensfintasticsharks.client.renderer;

import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.item.CaptainBenHatItem;

public class CaptainBenHatRenderer extends GeoArmorRenderer<CaptainBenHatItem> {
    public CaptainBenHatRenderer() {
        super(new DefaultedItemGeoModel<>(BensFintasticSharks.id("armor/captain_ben_hat")));
    }
}
