package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.bensfintasticsharks.ModAnimations;

public class ShortfinMakoSharkEntityForge  extends ShortfinMakoSharkEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ShortfinMakoSharkEntityForge(EntityType<ShortfinMakoSharkEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    private static final RawAnimation FAST_SWIM = RawAnimation.begin().thenLoop("move.fast_swim");
    private static final RawAnimation BEACHED = RawAnimation.begin().thenLoop("misc.beached2");
    private static final RawAnimation BITE = RawAnimation.begin().then("attack.bite", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation THRASH = RawAnimation.begin().thenLoop("attack.thrash");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // FAST_SWIM only while hunting (target acquired). With the 10-minute
        // post-kill cooldown in AbstractSharkEntity, the target lock no longer
        // toggles every prey scan, so this no longer flickers. Outside of a
        // hunt the mako cruises with the normal SWIM loop.
        controllers.add(new AnimationController<>(this, "idle_controller", 5, event -> {
            if (this.onGround() && !this.isInWaterOrBubble()) {
                return event.setAndContinue(BEACHED);
            }
            return event.setAndContinue(this.getTarget() != null ? FAST_SWIM : SWIM);
        }).triggerableAnim("bite", BITE));

        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (!this.getPassengers().isEmpty()) {
                return event.setAndContinue(THRASH);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    protected void onBiteAttack(net.minecraft.world.entity.LivingEntity target) {
        if (!level().isClientSide) triggerAnim("idle_controller", "bite");
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // Old SBL fight tasks removed. Persistent-pursuer chase lives in
    // AbstractSharkEntity.onSharkTick. The Mako's flavor is the trademark grab.

    @Override
    protected void onBiteLanded(net.minecraft.world.entity.LivingEntity target) {
        if (level().isClientSide) return;
        if (target.isDeadOrDying()) {
            target.stopRiding();
            return;
        }
        // Mako is the grabber; ~1 in 8 successful bites pin the prey.
        if (getRandom().nextFloat() < 0.12f) {
            grabMob(target);
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime == 30) {
            this.remove(Entity.RemovalReason.KILLED);
            this.dropExperience();
        }
    }

}
