package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CommonOctopusEntityForge extends CommonOctopusEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.common_octopus.idle");
    private static final RawAnimation IDLE_GROUND = RawAnimation.begin().thenLoop("animation.common_octopus.idle_on_ground");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.common_octopus.swim");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CommonOctopusEntityForge(EntityType<CommonOctopusEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            // Out of water OR explicitly hiding → ground pose. Otherwise SWIM.
            // The old "near floor" predicate fired almost any time underwater
            // (block-below is rarely air) which locked the octopus into the
            // ground_idle while it was actually swimming.
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
