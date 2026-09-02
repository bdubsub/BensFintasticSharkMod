package tfar.bensfintasticsharks.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class TigerSharkEntityForge extends TigerSharkEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.tigershark.idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.tigershark.swim");
    private static final RawAnimation FAST_SWIM = RawAnimation.begin().thenLoop("animation.tigershark.fast_swim");
    private static final RawAnimation BITE = RawAnimation.begin().then("animation.tigershark.bite", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.tigershark.death");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean itemBiteQueued;

    public TigerSharkEntityForge(EntityType<TigerSharkEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && justBitItem() && !itemBiteQueued) {
            triggerAnim("controller", "bite");
            itemBiteQueued = true;
        } else if (!justBitItem()) {
            itemBiteQueued = false;
        }
    }

    @Override
    protected void onBiteAttack(net.minecraft.world.entity.LivingEntity target) {
        if (!level().isClientSide) triggerAnim("controller", "bite");
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // FAST_SWIM while locked onto prey, SWIM otherwise — same pattern as the mako.
        // getSharkState() is synced entity data (getTarget() is server-only), and with
        // the post-kill hunt cooldown the state only flips at hunt start/end, so the
        // controller no longer retriggers every prey scan.
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (!this.isInWaterOrBubble()) {
                return event.setAndContinue(IDLE);
            }
            return event.setAndContinue(getSharkState() == SharkState.HOSTILE ? FAST_SWIM : SWIM);
        }).setAnimationSpeedHandler(e -> tfar.bensfintasticsharks.ModAnimations.swimClipSpeed(this))
                .triggerableAnim("bite", BITE)
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
