package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.*;
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

public class GreatWhiteSharkEntityForge extends GreatWhiteSharkEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GreatWhiteSharkEntityForge(EntityType<GreatWhiteSharkEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle_controller", 5, event -> {
            if (this.onGround() && !this.isInWaterOrBubble()) {
                return event.setAndContinue(ModAnimations.BEACHED2);
            }
            // getTarget() is server-only (Mob targets aren't synced to the client), so the old
            // predicate always saw null and FAST_SWIM never played. getSharkState() IS synced
            // and flips to HOSTILE exactly when a hunt is on — same pattern as the mako/tiger.
            return event.setAndContinue(this.getSharkState() == SharkState.HOSTILE ? ModAnimations.FAST_SWIM : DefaultAnimations.SWIM);
        })
                .setAnimationSpeedHandler(e -> ModAnimations.swimClipSpeed(this))
                .triggerableAnim("bite", RawAnimation.begin().then("attack.bite", Animation.LoopType.PLAY_ONCE))
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

    // Old SBL fight tasks removed. Persistent-pursuer chase now lives in
    // AbstractSharkEntity.onSharkTick, and the species-specific grab is wired
    // through onBiteAttack / onBiteLanded below.

    @Override
    protected void onBiteAttack(net.minecraft.world.entity.LivingEntity target) {
        if (!level().isClientSide) triggerAnim("idle_controller", "bite");
    }

    @Override
    protected void onBiteLanded(net.minecraft.world.entity.LivingEntity target) {
        // Roughly 1-in-10 bites grab the target like the alpha SBL version. If the
        // target's already dying, just release any rider so corpses don't dangle.
        if (level().isClientSide) return;
        if (target.isDeadOrDying()) {
            target.stopRiding();
            return;
        }
        if (getRandom().nextFloat() < 0.10f) {
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
