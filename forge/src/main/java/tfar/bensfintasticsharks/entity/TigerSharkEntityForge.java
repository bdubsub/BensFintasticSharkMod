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

public class TigerSharkEntityForge extends TigerSharkEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.tigershark.idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.tigershark.swim");
    private static final RawAnimation FAST_SWIM = RawAnimation.begin().thenLoop("animation.tigershark.fast_swim");
    private static final RawAnimation BITE = RawAnimation.begin().then("animation.tigershark.bite", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean itemBiteQueued;

    public TigerSharkEntityForge(EntityType<TigerSharkEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && justBitItem() && !itemBiteQueued) {
            triggerAnim("controller", "bite");
            itemBiteQueued = true;
        } else if (!justBitItem()) {
            itemBiteQueued = false;
        }
    }

    @Override
    protected void onBiteAttack(net.minecraft.world.entity.LivingEntity target) {
        if (!level().isClientSide) triggerAnim("controller", "bite");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // SWIM-only in water (no FAST_SWIM flip on chase). The toggle retriggers
        // the controller every prey scan and stalls both anims on frame 0.
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (!this.isInWaterOrBubble()) {
                return event.setAndContinue(IDLE);
            }
            return event.setAndContinue(SWIM);
        }).triggerableAnim("bite", BITE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
