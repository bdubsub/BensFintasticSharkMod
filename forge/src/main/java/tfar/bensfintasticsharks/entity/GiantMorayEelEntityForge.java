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

public class GiantMorayEelEntityForge extends GiantMorayEelEntity implements GeoEntity {

    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.giant_moray_eel.swim");
    private static final RawAnimation SWIM_FAST = RawAnimation.begin().thenLoop("animation.giant_moray_eel.swim_fast");
    private static final RawAnimation BITE = RawAnimation.begin().then("animation.giant_moray_eel.bite", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean lungeQueued;

    public GiantMorayEelEntityForge(EntityType<GiantMorayEelEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && isLunging() && !lungeQueued) {
            triggerAnim("controller", "bite");
            lungeQueued = true;
        } else if (!isLunging()) {
            lungeQueued = false;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // The eel's horizontal speed sits right around 0.04 lengthSqr at normal swim,
        // so a single threshold flips SWIM/SWIM_FAST every other tick and stalls both.
        // SWIM is the safe default; SWIM_FAST kicks in only when clearly above cruise.
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            double sq = this.getDeltaMovement().lengthSqr();
            return event.setAndContinue(sq > 0.12 ? SWIM_FAST : SWIM);
        }).triggerableAnim("bite", BITE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
