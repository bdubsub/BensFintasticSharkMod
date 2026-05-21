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
            // Hiding or beached → ground pose.
            if (this.isHiding() || !this.isInWaterOrBubble()) {
                return event.setAndContinue(IDLE_GROUND);
            }
            // Sitting on the sea floor → ground pose too (matches the Common Octopus behaviour).
            net.minecraft.core.BlockPos below = this.blockPosition().below();
            boolean nearFloor = !this.level().getBlockState(below).isAir()
                    && this.getDeltaMovement().horizontalDistanceSqr() < 0.0025;
            if (nearFloor) return event.setAndContinue(IDLE_GROUND);
            return event.setAndContinue(SWIM);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
