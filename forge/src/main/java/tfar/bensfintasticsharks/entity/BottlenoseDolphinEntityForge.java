package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BottlenoseDolphinEntityForge extends BottlenoseDolphinEntity implements GeoEntity {

    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.bottlenose_dolphin.swim");
    private static final RawAnimation SWIM_FAST = RawAnimation.begin().thenLoop("animation.bottlenose_dolphin.swim_fast");
    private static final RawAnimation BEACHED = RawAnimation.begin().thenLoop("animation.bottlenose_dolphin.beached");
    private static final RawAnimation BREACH = RawAnimation.begin().then("animation.bottlenose_dolphin.breach", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean breachQueued;

    public BottlenoseDolphinEntityForge(EntityType<BottlenoseDolphinEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && isBreaching() && !breachQueued) {
            triggerAnim("controller", "breach");
            breachQueued = true;
        } else if (!isBreaching()) {
            breachQueued = false;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // SWIM-only in water; SWIM_FAST removed entirely. With even a hysteresis
        // threshold the dolphin would oscillate near the boundary and lock both
        // animations on frame 0. BREACH remains a one-shot triggerable.
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (this.onGround() && !this.isInWaterOrBubble()) {
                return event.setAndContinue(BEACHED);
            }
            return event.setAndContinue(SWIM);
        }).triggerableAnim("breach", BREACH));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
