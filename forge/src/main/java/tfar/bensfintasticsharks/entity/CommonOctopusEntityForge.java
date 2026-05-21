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
            // Ground idle when:
            //  - explicitly hiding,
            //  - sitting on a solid block in water (sea floor),
            //  - or beached / out of water.
            if (this.isHiding() || (this.onGround() && !this.isInWaterOrBubble())) {
                return event.setAndContinue(IDLE_GROUND);
            }
            if (this.isInWaterOrBubble()) {
                net.minecraft.core.BlockPos below = this.blockPosition().below();
                boolean nearFloor = !this.level().getBlockState(below).isAir()
                        && this.getDeltaMovement().horizontalDistanceSqr() < 0.0025;
                if (nearFloor) return event.setAndContinue(IDLE_GROUND);
                return event.setAndContinue(SWIM);
            }
            return event.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
