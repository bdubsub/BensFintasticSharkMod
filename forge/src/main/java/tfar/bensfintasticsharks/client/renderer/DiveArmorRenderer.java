package tfar.bensfintasticsharks.client.renderer;

import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tfar.bensfintasticsharks.client.model.DiveArmorModel;
import tfar.bensfintasticsharks.item.DiveArmorItem;

public final class DiveArmorRenderer extends GeoArmorRenderer<DiveArmorItem> {
    public DiveArmorRenderer() {
        super(new DiveArmorModel());
    }
}
