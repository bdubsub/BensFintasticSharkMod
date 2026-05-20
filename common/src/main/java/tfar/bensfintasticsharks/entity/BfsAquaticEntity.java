package tfar.bensfintasticsharks.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomSwimTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Shared base for the new Legacy 1.0 aquatic mobs.
 *
 * The wander brain leans heavily on {@link SetRandomSwimTarget} with a generous
 * radius and a low-weight idle slot — sea creatures should look like they're
 * exploring, not glued to a 12-block bubble. Subclasses override
 * {@link #wanderRadiusXZ()} / {@link #wanderRadiusY()} for finer tuning.
 */
public abstract class BfsAquaticEntity<T extends BfsAquaticEntity<T>> extends SmartWaterAnimal<T> {

    /** Platform-set jellyfish contact damage multiplier (Forge reads from config). */
    public static volatile float globalJellyfishDamageMult = 1.0f;

    private int fleeCheckCooldown;

    protected BfsAquaticEntity(EntityType<T> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 1f / 8f, 0f, false);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    /**
     * Whether this species flees from {@code apex_predator}-tagged entities. Default
     * is {@code true} for everything except sharks themselves. Override to disable
     * (e.g. for jellyfish which can't realistically flee).
     */
    protected boolean fleesFromApex() {
        return !this.getType().is(tfar.bensfintasticsharks.init.ModTags.EntityTypes.APEX_PREDATOR);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (!fleesFromApex()) return;
        if (fleeCheckCooldown-- > 0) return;
        fleeCheckCooldown = 20;
        // Find the nearest apex predator within 12 blocks and head the other way.
        var area = getBoundingBox().inflate(12.0);
        var preds = level().getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, area,
                e -> e.isAlive() && e.getType().is(tfar.bensfintasticsharks.init.ModTags.EntityTypes.APEX_PREDATOR));
        if (preds.isEmpty()) return;
        var pred = preds.stream()
                .min(java.util.Comparator.comparingDouble(this::distanceToSqr))
                .orElseThrow();
        // Walk target is "current position + away vector × 8". Faster than wandering — prey
        // flees but sharks still catch them since shark base speed > prey base speed.
        Vec3 away = position().subtract(pred.position()).normalize().scale(8.0).add(position());
        net.tslat.smartbrainlib.util.BrainUtils.setMemory(getBrain(),
                net.minecraft.world.entity.ai.memory.MemoryModuleType.WALK_TARGET,
                new net.minecraft.world.entity.ai.memory.WalkTarget(away, 1.4f, 1));
    }

    /** Horizontal wander radius. Override for species (e.g. nautilus is sluggish, dolphin is wide). */
    protected float wanderRadiusXZ() { return 32f; }
    /** Vertical wander radius. */
    protected float wanderRadiusY() { return 16f; }
    /** Min/max idle delay in ticks. */
    protected int idleMinTicks() { return 20; }
    protected int idleMaxTicks() { return 40; }
    /** Higher = more time wandering vs idling. Default 8:2 (80% wander). */
    protected int wanderWeight() { return 8; }
    protected int idleWeight() { return 2; }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    /** Per-species multiplier for the moveRelative acceleration. Smaller = slower. */
    protected float swimSpeedMultiplier() { return 0.18f; }
    /** Hard cap on horizontal velocity in blocks/tick. Catches runaway acceleration. */
    protected float maxHorizontalSpeed() { return 0.45f; }
    /** Lock the entity's visual pitch (xRot) to 0 so it doesn't tilt when swimming up/down. */
    protected boolean lockHorizontalPitch() { return false; }

    @Override
    public void setXRot(float xRot) {
        super.setXRot(lockHorizontalPitch() ? 0f : xRot);
    }

    @Override
    public void travel(@NotNull Vec3 movementInput) {
        if (this.isEffectiveAi() && this.isInWater()) {
            // Scale moveRelative so MOVEMENT_SPEED attributes don't compound into mach-10
            // swimming. Vanilla water mobs effectively run with friction-bounded terminal
            // velocities around 0.3-0.5 b/t — we match that.
            this.moveRelative(this.getSpeed() * swimSpeedMultiplier(), movementInput);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(this.wasTouchingWater ? 0.82 : 0.25));
            // Hard cap on horizontal speed so bad pathing or stacked impulses can't break it.
            Vec3 dm = this.getDeltaMovement();
            double horiz = Math.sqrt(dm.x * dm.x + dm.z * dm.z);
            float cap = maxHorizontalSpeed();
            if (horiz > cap) {
                double s = cap / horiz;
                this.setDeltaMovement(dm.x * s, dm.y, dm.z * s);
            }
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.002, 0.0));
            }
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public List<? extends ExtendedSensor<? extends T>> getSensors() {
        return List.of(new HurtBySensor<>());
    }

    @Override
    public BrainActivityGroup<? extends T> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends T> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<>(
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()
                ),
                new OneRandomBehaviour<>(
                        Pair.of(new SetRandomSwimTarget<T>().setRadius(wanderRadiusXZ(), wanderRadiusY()), wanderWeight()),
                        Pair.of(new Idle<T>().runFor(entity -> entity.getRandom().nextInt(idleMinTicks(), idleMaxTicks() + 1)), idleWeight())
                )
        );
    }

    /**
     * BFS aquatic mobs drown when beached. Vanilla water-mob default ramps air down by 1/tick
     * and hits drown damage when air reaches -20. Mammals (seal, dolphin, orca) can surface so
     * they only drown while submerged out of air, which they handle via their own overrides.
     */
    @Override
    protected void handleAirSupply(int air) {
        if (this.isAlive() && !this.isInWaterOrBubble()) {
            this.setAirSupply(air - 1);
            if (this.getAirSupply() == -20) {
                this.setAirSupply(0);
                this.hurt(this.damageSources().drown(), 2.0F);
            }
        } else {
            this.setAirSupply(300);
        }
    }
}
