package tfar.bensfintasticsharks.entity;

import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.bensfintasticsharks.init.ModTags;

/** Removes prey loot when a BFS shark has eaten a tagged prey animal. */
public final class SharkPreyDropHandler {

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        if (!event.getEntity().getType().is(ModTags.EntityTypes.SHARK_PREY)) return;
        if (event.getSource().getEntity() instanceof AbstractSharkEntity<?>
                || event.getEntity().getKillCredit() instanceof AbstractSharkEntity<?>) {
            event.getDrops().clear();
        }
    }
}
