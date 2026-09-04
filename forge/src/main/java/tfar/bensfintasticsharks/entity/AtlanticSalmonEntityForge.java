package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AtlanticSalmonEntityForge extends AtlanticSalmonEntity implements GeoEntity {

    private static final double SWIM_MOVEMENT_EPSILON = 1.0e-4;

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.atlantic_salmon.idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.atlantic_salmon.swim");
    private static final RawAnimation FAST_SWIM = RawAnimation.begin().thenLoop("animation.atlantic_salmon.swim_fast");
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("animation.atlantic_salmon.flop");
    private static final RawAnimation SPIN = RawAnimation.begin().thenLoop("animation.atlantic_salmon.spin");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AtlanticSalmonEntityForge(EntityType<AtlanticSalmonEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (isNamedSpin()) return event.setAndContinue(SPIN);
            if (!isInWaterOrBubble()) return event.setAndContinue(FLOP);
            double movement = getDeltaMovement().lengthSqr();
            if (movement > 0.0225) return event.setAndContinue(FAST_SWIM);
            return event.setAndContinue(movement > SWIM_MOVEMENT_EPSILON ? SWIM : IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
