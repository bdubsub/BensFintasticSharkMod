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

public class OrcaEntityForge extends OrcaEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swim");
    private static final RawAnimation BREACH = RawAnimation.begin().then("breach", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean breachQueued;
    private int visuallyStillTicks;

    public OrcaEntityForge(EntityType<OrcaEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        visuallyStillTicks = getDeltaMovement().lengthSqr() < 4.0e-4
                ? Math.min(visuallyStillTicks + 1, 40)
                : 0;
        if (!level().isClientSide && isBreaching() && !breachQueued) {
            triggerAnim("controller", "breach");
            breachQueued = true;
        } else if (!isBreaching()) {
            breachQueued = false;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (!this.isInWaterOrBubble()) {
                return event.setAndContinue(IDLE);
            }
            return event.setAndContinue(visuallyStillTicks >= 20 ? IDLE : SWIM);
        }).triggerableAnim("breach", BREACH));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
