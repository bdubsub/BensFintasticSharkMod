package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AtlanticCodEntityForge extends AtlanticCodEntity implements GeoEntity {

    private static final double SWIM_MOVEMENT_EPSILON = 1.0e-4;
    private static final double VISUAL_MOVEMENT_EPSILON = 4.0e-4;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.atlantic_cod.idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.atlantic_cod.swim");
    private static final RawAnimation FAST_SWIM = RawAnimation.begin().thenLoop("animation.atlantic_cod.swim_fast");
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("animation.atlantic_cod.flop");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int visuallyStillTicks;

    public AtlanticCodEntityForge(EntityType<AtlanticCodEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) {
            double dx = getX() - xo;
            double dy = getY() - yo;
            double dz = getZ() - zo;
            visuallyStillTicks = dx * dx + dy * dy + dz * dz < VISUAL_MOVEMENT_EPSILON
                    ? Math.min(visuallyStillTicks + 1, 40)
                    : 0;
        }
    }

    private boolean isVisuallyMoving() {
        return getDeltaMovement().lengthSqr() > SWIM_MOVEMENT_EPSILON
                || level().isClientSide && visuallyStillTicks == 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            return switch (FishPresentationState.cod(
                    isDeadOrDying(),
                    isInWaterOrBubble(),
                    isFastSwim(),
                    getDeltaMovement().lengthSqr() > SWIM_MOVEMENT_EPSILON || isVisuallyMoving())) {
                case STOP -> PlayState.STOP;
                case FLOP -> event.setAndContinue(FLOP);
                case FAST_SWIM -> event.setAndContinue(FAST_SWIM);
                case SWIM -> event.setAndContinue(SWIM);
                case IDLE -> event.setAndContinue(IDLE);
                case SPIN -> throw new IllegalStateException("Cod cannot select Spin");
            };
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
