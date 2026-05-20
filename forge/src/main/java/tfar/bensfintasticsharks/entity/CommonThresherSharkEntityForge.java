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
            return event.setAndContinue(this.getTarget() != null ? ModAnimations.FAST_SWIM : DefaultAnimations.SWIM);
        })
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
