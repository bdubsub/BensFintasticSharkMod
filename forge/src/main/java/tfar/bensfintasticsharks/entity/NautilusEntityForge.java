package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class NautilusEntityForge extends NautilusEntity implements GeoEntity {

    // Hide is a "hold on last frame" pose — once switched to it, it stays. Move is the
    // default loop. Resting flag drives the swap so we don't restart the hide animation
    // every render frame (the previous version did exactly that).
    private static final RawAnimation HIDE = RawAnimation.begin().thenPlayAndHold("animation.nautilus.hide");
    private static final RawAnimation MOVE = RawAnimation.begin().thenLoop("animation.nautilus.move");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public NautilusEntityForge(EntityType<NautilusEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 8, event -> {
            return event.setAndContinue(this.isResting() ? HIDE : MOVE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
