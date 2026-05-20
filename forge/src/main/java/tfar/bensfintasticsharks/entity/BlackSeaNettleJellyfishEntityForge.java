package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BlackSeaNettleJellyfishEntityForge extends BlackSeaNettleJellyfishEntity implements GeoEntity {

    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("animation.medium_jellyfish.move_1");
    private static final RawAnimation BEACHED = RawAnimation.begin().thenLoop("animation.medium_jellyfish.beached");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BlackSeaNettleJellyfishEntityForge(EntityType<BlackSeaNettleJellyfishEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event ->
                event.setAndContinue(isInWaterOrBubble() ? MOVE : BEACHED)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
