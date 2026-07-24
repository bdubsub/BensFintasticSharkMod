package tfar.bensfintasticsharks.client.model;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.item.PrismarineArmorItem;

public class PrismarineArmorModel extends DefaultedItemGeoModel<PrismarineArmorItem> {
    private static final float FULL_SWIM_BODY_SCALE = 0.9F;

    public PrismarineArmorModel() {
        super(BensFintasticSharks.id("armor/prismarine_armor"));
    }

    @Override
    public void setCustomAnimations(PrismarineArmorItem animatable, long instanceId,
                                    AnimationState<PrismarineArmorItem> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone body = getBone("armorBody").orElse(null);
        Entity entity = animationState.getData(DataTickets.ENTITY);
        float swimAmount = entity instanceof LivingEntity livingEntity
                ? livingEntity.getSwimAmount(animationState.getPartialTick())
                : 0;
        float bodyScale = Mth.lerp(swimAmount, 1, FULL_SWIM_BODY_SCALE);

        if (body != null) {
            body.setScaleX(bodyScale);
            body.setScaleZ(bodyScale);
        }
    }
}
