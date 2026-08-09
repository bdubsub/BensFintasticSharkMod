package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AtlanticCodEntityForge extends AtlanticCodEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.atlantic_cod.idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.atlantic_cod.swim");
    private static final RawAnimation FAST_SWIM = RawAnimation.begin().thenLoop("animation.atlantic_cod.swim_fast");
    private static final RawAnimation FLOP = RawAnimation.begin().thenLoop("animation.atlantic_cod.flop");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public AtlanticCodEntityForge(EntityType<AtlanticCodEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (!isInWaterOrBubble()) return event.setAndContinue(FLOP);
            if (getDeltaMovement().horizontalDistanceSqr() > 0.0225) return event.setAndContinue(FAST_SWIM);
            return event.setAndContinue(event.isMoving() ? SWIM : IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
