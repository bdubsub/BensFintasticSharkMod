package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CaribbeanReefOctopusEntityForge extends CaribbeanReefOctopusEntity implements GeoEntity {

    private static final RawAnimation IDLE_GROUND = RawAnimation.begin().thenLoop("animation.caribbean_reef_octopus.idle_ground");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.caribbean_reef_octopus.swim");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CaribbeanReefOctopusEntityForge(EntityType<CaribbeanReefOctopusEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            // In water → always SWIM (so idle floating doesn't fall back to the on-ground pose).
            // Out of water or hiding → IDLE_GROUND.
            if (this.isHiding() || !this.isInWaterOrBubble()) {
                return event.setAndContinue(IDLE_GROUND);
            }
            return event.setAndContinue(SWIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
