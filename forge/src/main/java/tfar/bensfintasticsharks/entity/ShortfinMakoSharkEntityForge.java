package tfar.bensfintasticsharks.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.util.BrainUtils;
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle_controller", 0, event -> {
            boolean isAttacking = this.swinging;
            boolean isDead = this.dead || this.getHealth() < 0.01 || this.isDeadOrDying();
            boolean isFastMoving = getDeltaMovement().lengthSqr() > .01;
            boolean isBeached = onGround() && !isInWaterOrBubble();
            if (isBeached) {
                return event.setAndContinue(ModAnimations.BEACHED2);
            }

            if (event.isMoving() && !isDead && !isAttacking) {
                return event.setAndContinue(isFastMoving ? ModAnimations.FAST_SWIM : DefaultAnimations.SWIM);
            }
            return event.setAndContinue(DefaultAnimations.IDLE);
        })
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

    @Override
    public BrainActivityGroup<? extends ShortfinMakoSharkEntity> getFightTasks() { // These are the tasks that handle fighting
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>()
                        .invalidateIf((entity, target) -> !isInWaterOrBubble() || target instanceof Player pl && (pl.isCreative() || pl.isSpectator())), // Cancel fighting if the target is no longer valid
                new SetWalkTargetToAttackTarget<>().speedMod((owner, target) -> 1.75f),      // Set the walk target to the attack target


                new OneRandomBehaviour<>(
                        Pair.of(new AnimatableMeleeAttack<>(4) {
                            @Override
                            protected void start(Mob entity) {
                                BehaviorUtils.lookAtEntity(entity, this.target);
                                triggerAnim("idle_controller", "bite");

                            }
                        },9),
                        Pair.of(new AnimatableMeleeAttack<>(4) {
                            @Override
                            protected void start(Mob entity) {
                                BehaviorUtils.lookAtEntity(entity, target);
                                triggerAnim("idle_controller", "bite");
                            }

                            @Override
                            protected void doDelayedAction(Mob entity) {
                                super.doDelayedAction(entity);
                                if (!target.isDeadOrDying()) {
                                    grabMob(target);
                                } else {
                                    target.stopRiding();
                                    BrainUtils.clearMemory(getBrain(), MemoryModuleType.LOOK_TARGET);
                                }
                            }
                        },1))
        ); // Melee attack the target if close enough
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
