package tfar.bensfintasticsharks.client.renderer;

import software.bernie.geckolib.renderer.GeoArmorRenderer;
import tfar.bensfintasticsharks.client.model.PrismarineArmorModel;
import tfar.bensfintasticsharks.item.PrismarineArmorItem;

public class PrismarineArmorRenderer extends GeoArmorRenderer<PrismarineArmorItem> {
    public PrismarineArmorRenderer() {
        super(new PrismarineArmorModel());
    }
}
