package tfar.bensfintasticsharks.init;

import net.minecraft.world.item.CreativeModeTab;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.TextComponents;
import tfar.bensfintasticsharks.item.HiddenItem;

public class ModCreativeTabs {

    public static final CreativeModeTab TAB = CreativeModeTab.builder(null,-1)
            .title(TextComponents.TAB_TITLE)
            .displayItems((itemDisplayParameters, output) -> {
                BensFintasticSharks.getKnownItems().filter(item -> !(item instanceof HiddenItem)).forEach(output::accept);
            })
            .icon(ModItems.BFS_LOGO::getDefaultInstance).build();

}
