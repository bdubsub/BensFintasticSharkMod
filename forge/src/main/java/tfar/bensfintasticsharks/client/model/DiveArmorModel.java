package tfar.bensfintasticsharks.client.model;

import software.bernie.geckolib.model.DefaultedItemGeoModel;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.item.DiveArmorItem;

public final class DiveArmorModel extends DefaultedItemGeoModel<DiveArmorItem> {
    public DiveArmorModel() {
        super(BensFintasticSharks.id("armor/dive_armor"));
    }
}
