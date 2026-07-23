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

public class GreatHammerheadSharkEntityForge extends GreatHammerheadSharkEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GreatHammerheadSharkEntityForge(EntityType<GreatHammerheadSharkEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(this, "idle_controller", 5, event -> {
            if (this.onGround() && !this.isInWaterOrBubble()) {
                return event.setAndContinue(ModAnimations.BEACHED2);
            }
            // getTarget() is server-only, so FAST_SWIM never played client-side; getSharkState()
            // is synced and flips HOSTILE when hunting (same pattern as the mako/tiger).
            return event.setAndContinue(this.getSharkState() == SharkState.HOSTILE ? ModAnimations.FAST_SWIM : DefaultAnimations.SWIM);
        })
                .setAnimationSpeedHandler(e -> ModAnimations.swimClipSpeed(this))
                .triggerableAnim("bite_right", RawAnimation.begin().then("attack.bite_right", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("bite_left", RawAnimation.begin().then("attack.bite_left", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("death", ModAnimations.DEATH));

        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (!this.getPassengers().isEmpty()) {
                return event.setAndContinue(ModAnimations.THRASH);
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


    // Old SBL fight tasks removed. Persistent-pursuer chase lives in
    // AbstractSharkEntity.onSharkTick. The hammerhead's flavor is two-sided
    // biting plus an occasional grab.

    private boolean nextBiteIsLeft = false;

    @Override
    protected void onBiteAttack(net.minecraft.world.entity.LivingEntity target) {
        if (level().isClientSide) return;
        // Alternate left and right bite swings each strike.
        triggerAnim("idle_controller", nextBiteIsLeft ? "bite_left" : "bite_right");
        nextBiteIsLeft = !nextBiteIsLeft;
    }

    @Override
    protected void onBiteLanded(net.minecraft.world.entity.LivingEntity target) {
        if (level().isClientSide) return;
        if (target.isDeadOrDying()) {
            target.stopRiding();
            return;
        }
        // Small chance to grab on the left-swing bite (the "head-pin").
        if (!nextBiteIsLeft && getRandom().nextFloat() < 0.08f) {
            grabMob(target);
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.triggerAnim("idle_controller", "death");
        if (this.deathTime == 30) {
            this.remove(Entity.RemovalReason.KILLED);
            this.dropExperience();
        }
    }
}
