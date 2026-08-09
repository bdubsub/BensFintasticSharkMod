package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.bensfintasticsharks.disturbance.DisturbanceType;
import tfar.bensfintasticsharks.disturbance.SharkAlertEvent;
import tfar.bensfintasticsharks.disturbance.SharkAlertHandler;

public class BlacktipReefSharkEntityForge extends BlacktipReefSharkEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("swim");
    private static final RawAnimation FAST_SWIM = RawAnimation.begin().thenLoop("fast_swim");
    private static final RawAnimation BEACHED = RawAnimation.begin().thenLoop("beached2");
    private static final RawAnimation BITE = RawAnimation.begin().then("bite", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean wasHostile;

    public BlacktipReefSharkEntityForge(EntityType<BlacktipReefSharkEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean hurt(@org.jetbrains.annotations.NotNull net.minecraft.world.damagesource.DamageSource source, float amount) {
        boolean took = super.hurt(source, amount);
        // Pack-alert when ANY blacktip is hurt by a living attacker, not just on
        // disturbance ticks. AbstractSharkEntity.hurt already sets HOSTILE on this
        // shark; we mirror that to the pack so the entire group joins the fight.
        // 0.19 — gated on this shark actually retaliating (getTarget() == attacker):
        // a lone timid blacktip flees instead, and summoning a 32-block posse here
        // would silently defeat the timidity.
        if (took && !level().isClientSide
                && source.getEntity() instanceof net.minecraft.world.entity.LivingEntity attacker
                && !(attacker instanceof BlacktipReefSharkEntity)
                && this.getTarget() == attacker
                && level() instanceof ServerLevel sl) {
            SharkAlertHandler.fire(sl, this, SharkAlertEvent.Type.PACK_ALERT,
                    BlacktipReefSharkEntity.class, 32.0, attacker);
        }
        return took;
    }

    @Override
    protected void onPostDisturbance(BlockPos source, DisturbanceType type, @Nullable LivingEntity sourceEntity) {
        super.onPostDisturbance(source, type, sourceEntity);
        // When a blacktip enters HOSTILE (which happens on BLOOD if a target is in range,
        // or via being attacked), alert other blacktips in 24 blocks.
        boolean hostileNow = getSharkState() == SharkState.HOSTILE;
        if (hostileNow && !wasHostile && level() instanceof ServerLevel sl) {
            SharkAlertHandler.fire(sl, this, SharkAlertEvent.Type.PACK_ALERT,
                    BlacktipReefSharkEntity.class, 24.0, sourceEntity);
        }
        wasHostile = hostileNow;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (!this.isInWaterOrBubble()) {
                return event.setAndContinue(this.onGround() ? BEACHED : IDLE);
            }
            // 0.16: FAST_SWIM (time-scaled swim clip) while locked onto prey, mako-style —
            // the synced SharkState only flips at hunt start/end so this doesn't retrigger.
            if (this.getSharkState() == SharkState.HOSTILE) {
                return event.setAndContinue(FAST_SWIM);
            }
            // 0.19 EXPERIMENT (Ben): no idle clip in water — always SWIM. The isMoving()
            // velocity gate (threshold 0.015 b/t) flickered across the AI's swim/stop duty
            // cycle, each flip crossfading over 5 ticks = the "jittery" look. Blacktip is
            // the test species; Oceanic Whitetip keeps its idle gate as the side-by-side
            // control. Revert = restore `event.isMoving() ? SWIM : IDLE`.
            return event.setAndContinue(SWIM);
        })
                .setAnimationSpeedHandler(e -> tfar.bensfintasticsharks.ModAnimations.swimClipSpeed(this))
                .triggerableAnim("bite", BITE)
                .triggerableAnim("death", DEATH));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void onBiteAttack(net.minecraft.world.entity.LivingEntity target) {
        if (!level().isClientSide) triggerAnim("controller", "bite");
    }

    @Override
    protected void onBiteLanded(net.minecraft.world.entity.LivingEntity target) {
        if (!level().isClientSide
                && target instanceof net.minecraft.world.entity.player.Player
                && !target.isDeadOrDying()
                && getRandom().nextFloat() < 0.2f) {
            latchMob(target);
        }
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
