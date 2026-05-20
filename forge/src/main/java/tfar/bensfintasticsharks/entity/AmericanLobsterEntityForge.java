package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class AmericanLobsterEntityForge extends AmericanLobsterEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.american_lobster.idle");
    private static final RawAnimation CRAWL = RawAnimation.begin().thenLoop("animation.american_lobster.crawl");
    private static final RawAnimation SNIP = RawAnimation.begin().then("animation.american_lobster.snip", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.american_lobster.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean snipQueued;

    public AmericanLobsterEntityForge(EntityType<AmericanLobsterEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && isSnipping() && !snipQueued) {
            triggerAnim("controller", "snip");
            snipQueued = true;
        } else if (!isSnipping()) {
            snipQueued = false;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            // Resting lobster plays idle, otherwise crawl. isMoving() returns false too
            // often between brain ticks to drive this cleanly, so we use the entity's
            // own resting flag instead.
            return event.setAndContinue(isResting() ? IDLE : CRAWL);
        })
                .triggerableAnim("snip", SNIP)
                .triggerableAnim("death", DEATH));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.triggerAnim("controller", "death");
        if (this.deathTime == 30) {
            this.remove(Entity.RemovalReason.KILLED);
            this.dropExperience();
        }
    }
}
