package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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

public class OceanicWhitetipSharkEntityForge extends OceanicWhitetipSharkEntity implements GeoEntity {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.oceanicwhitetipshark.idle");
    private static final RawAnimation SWIM = RawAnimation.begin().thenLoop("animation.oceanicwhitetipshark.swim");
    private static final RawAnimation BITE = RawAnimation.begin().then("animation.oceanicwhitetipshark.bite", Animation.LoopType.PLAY_ONCE);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public OceanicWhitetipSharkEntityForge(EntityType<OceanicWhitetipSharkEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void onPostDisturbance(BlockPos source, DisturbanceType type, @Nullable LivingEntity sourceEntity) {
        super.onPostDisturbance(source, type, sourceEntity);
        // Convergent feeding: a whitetip that smells blood alerts nearby
        // whitetips and pulls them onto the same target. Radius tightened from
        // 48 to 20 — at 48 every whitetip in the same chunk-load batch would
        // converge on the same prey and create a pack-hunting effect across
        // species (the same blood event also feeds reactToDisturbance on every
        // other shark in range).
        if (type == DisturbanceType.BLOOD && sourceEntity != null && level() instanceof ServerLevel sl) {
            SharkAlertHandler.fire(sl, this, SharkAlertEvent.Type.BLOOD_CONVERGENCE,
                    OceanicWhitetipSharkEntity.class, 20.0, sourceEntity);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, event -> {
            if (!this.isInWaterOrBubble()) {
                return event.setAndContinue(IDLE);
            }
            return event.setAndContinue(SWIM);
        }).triggerableAnim("bite", BITE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void onBiteAttack(LivingEntity target) {
        if (!level().isClientSide) triggerAnim("controller", "bite");
    }
}
