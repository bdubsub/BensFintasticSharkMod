package tfar.bensfintasticsharks.client.model;

import software.bernie.geckolib.model.DefaultedItemGeoModel;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.item.PrismarineArmorItem;

public class PrismarineArmorModel extends DefaultedItemGeoModel<PrismarineArmorItem> {
    public PrismarineArmorModel() {
        super(BensFintasticSharks.id("armor/prismarine_armor"));
    }
}
