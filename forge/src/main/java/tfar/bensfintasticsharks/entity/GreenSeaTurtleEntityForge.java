package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GreenSeaTurtleEntityForge extends GreenSeaTurtleEntity implements GeoEntity {

    private static final RawAnimation IDLE_SWIM = RawAnimation.begin().thenLoop("animation.green_sea_turtle.idle_swim");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.green_sea_turtle.swim");
    private static final RawAnimation CRAWL = RawAnimation.begin().thenLoop("animation.green_sea_turtle.crawl");
    private static final RawAnimation CRAWL_IDLE = RawAnimation.begin().thenLoop("animation.green_sea_turtle.crawl_idle");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GreenSeaTurtleEntityForge(EntityType<GreenSeaTurtleEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 8, event -> {
            boolean inWater = this.isInWaterOrBubble();
            // Higher threshold so idle_swim and crawl_idle actually get a chance to play.
            // The old 0.001 fired immediately even when the entity was effectively still.
            double sq = this.getDeltaMovement().horizontalDistanceSqr();
            boolean moving = sq > 0.008;
            if (moving) {
                return event.setAndContinue(inWater ? SWIM : CRAWL);
            }
            return event.setAndContinue(inWater ? IDLE_SWIM : CRAWL_IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
