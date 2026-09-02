package tfar.bensfintasticsharks.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.bensfintasticsharks.ModAnimations;

public class CommonThresherSharkEntityForge extends CommonThresherSharkEntity implements GeoEntity{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CommonThresherSharkEntityForge(EntityType<CommonThresherSharkEntity> $$0, Level $$1) {
        super($$0, $$1);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle_controller", 5, event -> {
            if (this.onGround() && !this.isInWaterOrBubble()) {
                return event.setAndContinue(ModAnimations.BEACHED);
            }
            // getTarget() is server-only, so FAST_SWIM never played client-side; getSharkState()
            // is synced and flips HOSTILE when hunting (same pattern as the mako/tiger).
            return event.setAndContinue(this.getSharkState() == SharkState.HOSTILE ? ModAnimations.FAST_SWIM : DefaultAnimations.SWIM);
        })
                .setAnimationSpeedHandler(e -> ModAnimations.swimClipSpeed(this))
                .triggerableAnim("bite", RawAnimation.begin().then("attack.bite", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("tail_whip", RawAnimation.begin().then("attack.tail_whip", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("death", ModAnimations.DEATH));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    // Old SBL fight tasks removed. Persistent-pursuer chase lives in
    // AbstractSharkEntity.onSharkTick. The thresher's flavor is occasional
    // tail-whip strikes that slow the target.

    private boolean nextStrikeIsTailWhip = false;

    @Override
    protected void onBiteAttack(net.minecraft.world.entity.LivingEntity target) {
        if (level().isClientSide) return;
        // 1 in 4 attacks is a tail whip instead of a bite.
        nextStrikeIsTailWhip = getRandom().nextFloat() < 0.25f;
        triggerAnim("idle_controller", nextStrikeIsTailWhip ? "tail_whip" : "bite");
    }

    @Override
    protected void onBiteLanded(net.minecraft.world.entity.LivingEntity target) {
        if (level().isClientSide) return;
        if (nextStrikeIsTailWhip) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0));
            // Suggestion (Ben): the whip should launch its victim. knockback() wants the
            // vector TOWARD the attacker; water friction eats impulses fast, hence 1.2
            // instead of vanilla's 0.4. hurtMarked syncs the velocity to player clients.
            target.knockback(1.2D, this.getX() - target.getX(), this.getZ() - target.getZ());
            target.setDeltaMovement(target.getDeltaMovement().add(0.0, 0.4, 0.0));
            target.hurtMarked = true;
            // ...and shove anything else caught in the sweep (no damage, just the wake).
            for (net.minecraft.world.entity.LivingEntity e : level().getEntitiesOfClass(
                    net.minecraft.world.entity.LivingEntity.class, this.getBoundingBox().inflate(3.0),
                    e -> e != this && e != target && e.isAlive() && e.isInWater()
                            && !(e instanceof AbstractSharkEntity<?>)
                            && !(e instanceof net.minecraft.world.entity.player.Player p
                                    && (p.isCreative() || p.isSpectator())))) {
                e.knockback(0.6D, this.getX() - e.getX(), this.getZ() - e.getZ());
                e.setDeltaMovement(e.getDeltaMovement().add(0.0, 0.25, 0.0));
                e.hurtMarked = true;
            }
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
