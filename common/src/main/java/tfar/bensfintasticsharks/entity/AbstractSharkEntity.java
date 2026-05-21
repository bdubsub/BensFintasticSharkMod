package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.jetbrains.annotations.Nullable;
import tfar.bensfintasticsharks.disturbance.DisturbanceType;

/**
 * Base class for Legacy 1.0 sharks. Implements the lightweight state machine
 * described in the spec — Idle → Curious → Hostile → Idle (with Investigating
 * collapsed into Curious for simplicity in Legacy 1.0).
 *
 * Designed to be platform-agnostic; the Forge-side handler hands disturbances in
 * via {@link #reactToDisturbance(BlockPos, DisturbanceType, LivingEntity)}.
 */
public abstract class AbstractSharkEntity<T extends AbstractSharkEntity<T>> extends BfsAquaticEntity<T> {

    public enum SharkState { IDLE, CURIOUS, HOSTILE, BEACHED }

    private static final EntityDataAccessor<Integer> STATE =
            SynchedEntityData.defineId(AbstractSharkEntity.class, EntityDataSerializers.INT);

    private final SharkParams params;
    private int stateTimer; // ticks remaining in the current non-idle state
    private int ticksTargetOutOfWater;
    private int biteCooldown;
    /** Scheduled hit: damage lands a few ticks after the bite animation triggers so the visual matches the hit. */
    private LivingEntity pendingBiteTarget;
    private int pendingBiteTicks;
    /** How many ticks after the swing the actual damage frame lands. Override per-species if needed. */
    protected int biteImpactDelayTicks() { return 5; }

    protected AbstractSharkEntity(EntityType<T> type, Level level, SharkParams params) {
        super(type, level);
        this.params = params;
    }

    /**
     * Platform-set mutators so {@code BfsConfig} values affect AI without the common
     * module needing to import Forge classes. Forge sets these in {@code FMLCommonSetupEvent}.
     */
    public static volatile float globalDetectionMult = 1.0f;
    public static volatile float globalDisengageDistanceMult = 1.0f;
    public static volatile float globalDisengageTimeoutMult = 1.0f;

    protected float detectionRadiusMult() { return globalDetectionMult; }
    protected float disengageDistanceMult() { return globalDisengageDistanceMult; }
    protected float disengageTimeoutMult() { return globalDisengageTimeoutMult; }

    /** Effective detection radius after config scaling. */
    public float effectiveDetectionRadius() { return params.detectionRadius() * detectionRadiusMult(); }
    public float effectiveDisengageDistance() { return params.disengageDistance() * disengageDistanceMult(); }
    public int effectiveDisengageTimeoutTicks() { return (int)(params.disengageTimeoutTicks() * disengageTimeoutMult()); }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STATE, 0);
    }

    public SharkState getSharkState() {
        return SharkState.values()[this.entityData.get(STATE) % SharkState.values().length];
    }

    public void setSharkState(SharkState s) {
        this.entityData.set(STATE, s.ordinal());
    }

    public SharkParams getParams() {
        return params;
    }

    public static AttributeSupplier.Builder createSharkAttributes(double maxHp, double speed, double attack) {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, maxHp)
                .add(Attributes.MOVEMENT_SPEED, speed)
                .add(Attributes.ATTACK_DAMAGE, attack);
    }

    /** Forge-side hook to fire a bite/attack animation when {@link #onSharkTick} swings. */
    protected void onBiteAttack(LivingEntity target) {}

    /**
     * Effective bite distance against a target. Vanilla's getAttackReachSqr would give
     * a 5+ block reach for our 2.5-wide sharks, which felt like a staring contest. We
     * cap it tighter so the bite has to actually contact the prey.
     */
    protected double biteRangeAgainst(LivingEntity target) {
        return (this.getBbWidth() * 0.6F) + 1.0 + (target.getBbWidth() * 0.5F);
    }

    /**
     * Called the tick the bite damage actually lands (after {@link #biteImpactDelayTicks()}
     * has elapsed and the target is still in range). Species override this to apply
     * signature effects like Mako/Great White's grab, Thresher's tail-whip slow,
     * Whitetip's lingering wound, etc. Default does nothing.
     */
    protected void onBiteLanded(LivingEntity target) {}

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (biteCooldown > 0) biteCooldown--;
        // Land the scheduled bite at the visual peak of the animation.
        if (pendingBiteTicks > 0) {
            pendingBiteTicks--;
            if (pendingBiteTicks == 0 && pendingBiteTarget != null && pendingBiteTarget.isAlive()) {
                double reach = biteRangeAgainst(pendingBiteTarget);
                if (this.distanceToSqr(pendingBiteTarget) <= reach * reach) {
                    this.doHurtTarget(pendingBiteTarget);
                    onBiteLanded(pendingBiteTarget);
                }
                pendingBiteTarget = null;
            }
        }
        // Drop out of non-IDLE states when timer runs out.
        if (stateTimer > 0) {
            stateTimer--;
            if (stateTimer == 0 && getSharkState() != SharkState.IDLE) {
                setSharkState(SharkState.IDLE);
                this.setTarget(null);
            }
        }
        if (!this.isInWater() && this.onGround()) {
            if (getSharkState() != SharkState.BEACHED) setSharkState(SharkState.BEACHED);
        } else if (getSharkState() == SharkState.BEACHED) {
            setSharkState(SharkState.IDLE);
        }
        // Target-left-water disengage. Spec 2.1: 60+ ticks out of water → drop target.
        LivingEntity tgt = this.getTarget();
        if (tgt != null) {
            if (!tgt.isAlive() || tgt.isDeadOrDying()) {
                this.setTarget(null);
                setSharkState(SharkState.IDLE);
                ticksTargetOutOfWater = 0;
                tgt = null;
            }
        }
        if (tgt != null) {
            if (!tgt.isInWater()) {
                ticksTargetOutOfWater++;
                if (ticksTargetOutOfWater >= 60) {
                    this.setTarget(null);
                    setSharkState(SharkState.IDLE);
                    ticksTargetOutOfWater = 0;
                }
            } else {
                ticksTargetOutOfWater = 0;
            }
            // Also disengage if target is creative/spectator now.
            if (tgt instanceof Player p && (p.isCreative() || p.isSpectator())) {
                this.setTarget(null);
                setSharkState(SharkState.IDLE);
            }
        } else {
            ticksTargetOutOfWater = 0;
        }
        onSharkTick();
    }

    /** Hook for species-specific tick behaviors (item investigation, hovering, etc). */
    protected void onSharkTick() {
        if (level().isClientSide) return;

        // Prey scan: every 20 ticks (staggered by entity id so they don't all scan
        // the same tick), look for SHARK_PREY-tagged living entities in water and
        // lock onto the closest. Replaces the SBL TargetOrRetaliate behaviour.
        if (this.getTarget() == null && (tickCount % 20) == (this.getId() & 0xF) % 20) {
            float radius = effectiveDetectionRadius();
            var preyArea = this.getBoundingBox().inflate(radius);
            java.util.List<LivingEntity> prey = level().getEntitiesOfClass(LivingEntity.class, preyArea,
                    e -> e.isAlive() && e.isInWater() && e != this
                            && !(e instanceof AbstractSharkEntity<?>)
                            && e.getType().is(tfar.bensfintasticsharks.init.ModTags.EntityTypes.SHARK_PREY));
            if (!prey.isEmpty()) {
                LivingEntity closest = prey.stream()
                        .min(java.util.Comparator.comparingDouble(this::distanceToSqr))
                        .orElseThrow();
                this.setTarget(closest);
                setSharkState(SharkState.HOSTILE);
                stateTimer = effectiveDisengageTimeoutTicks();
            }
        }

        // Active chase: while a target exists, refresh the walk target every 10
        // ticks at aggro speed. Faster than prey base speed so prey can't outrun us.
        // Skip walk-target refresh once we're already in bite range so the move
        // control doesn't keep pushing us forward over the target.
        LivingEntity tgt = this.getTarget();
        if (tgt != null && tgt.isAlive() && tickCount % 10 == 0) {
            double biteRange = biteRangeAgainst(tgt);
            double biteRangeSqr = biteRange * biteRange;
            boolean inBiteRange = this.distanceToSqr(tgt) <= biteRangeSqr;
            if (!inBiteRange) {
                BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET,
                        new WalkTarget(tgt.position(), params.aggroSpeedMult(), 1));
            } else {
                // Drop any active walk target so the brain stops pushing the entity forward.
                BrainUtils.clearMemory(getBrain(), MemoryModuleType.WALK_TARGET);
            }
            // Trigger the bite animation when in range, but defer the actual damage
            // until the visual impact frame so the hit syncs with the model snap.
            if (this.distanceToSqr(tgt) <= biteRangeSqr
                    && biteCooldown <= 0
                    && pendingBiteTarget == null) {
                this.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
                onBiteAttack(tgt);
                pendingBiteTarget = tgt;
                pendingBiteTicks = biteImpactDelayTicks();
                biteCooldown = params.biteCooldownTicks();
            }
        }

        // Beach avoidance: every 40 ticks, nudge sharks away from beach biomes
        // and out of shallow water. Only when not actively chasing.
        if (tgt == null && tickCount % 40 == 0
                && (shallowWaterSpeedScale() < 1.0f || isNearBeach())) {
            Vec3 deepwardsHint = findDeepwaterDirection();
            if (deepwardsHint != null) {
                Vec3 walk = position().add(deepwardsHint.scale(16));
                BrainUtils.setMemory(getBrain(),
                        MemoryModuleType.WALK_TARGET,
                        new WalkTarget(walk, 1.0f, 1));
            }
        }
    }

    /**
     * When a shark is hurt by a living attacker, target them and call up to a few
     * nearby same-species sharks for help (zombie-commander style). Reinforcements
     * only "call" — they never get spawned beyond the per-species cap.
     */
    @Override
    public boolean hurt(@NotNull net.minecraft.world.damagesource.DamageSource source, float amount) {
        boolean wasHurt = super.hurt(source, amount);
        if (!wasHurt || level().isClientSide) return wasHurt;
        if (!(source.getEntity() instanceof LivingEntity attacker) || attacker == this) return wasHurt;
        if (attacker instanceof Player p && (p.isCreative() || p.isSpectator())) return wasHurt;

        // Never retaliate against another shark of the same species. Without this guard a
        // friendly bite (or stray AoE) flips the whole pack into infighting because the
        // attacker becomes the target, the help-call drags in everyone else, and they
        // start attacking the shark that just hit them.
        if (attacker instanceof AbstractSharkEntity<?> otherShark
                && otherShark.getType() == this.getType()) {
            return wasHurt;
        }

        this.setTarget(attacker);
        setSharkState(SharkState.HOSTILE);
        stateTimer = effectiveDisengageTimeoutTicks();

        // Throttle the help-call. Big hits always call; small hits only sometimes
        // so a piranha-style bunch of small ticks doesn't drag every shark in.
        if (amount > 4f || getRandom().nextFloat() < 0.35f) {
            float helpRadius = 32f;
            var helpers = level().getEntitiesOfClass(AbstractSharkEntity.class,
                    this.getBoundingBox().inflate(helpRadius),
                    s -> s != this && s.isAlive() && s.isInWater()
                            && (s.getTarget() == null || s.getTarget() == attacker)
                            && s.getType() == this.getType());
            int called = 0;
            for (AbstractSharkEntity<?> helper : helpers) {
                if (called >= 4) break;
                helper.setTarget(attacker);
                helper.setSharkState(SharkState.HOSTILE);
                helper.setStateTimer(effectiveDisengageTimeoutTicks());
                called++;
            }
        }
        return wasHurt;
    }

    private boolean isNearBeach() {
        for (int dx = -8; dx <= 8; dx += 4) {
            for (int dz = -8; dz <= 8; dz += 4) {
                var biome = level().getBiome(blockPosition().offset(dx, 0, dz));
                if (biome.is(net.minecraft.world.level.biome.Biomes.BEACH)
                        || biome.is(net.minecraft.world.level.biome.Biomes.SNOWY_BEACH)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** Returns a horizontal direction toward deeper water, or null if no sample is deeper. */
    @org.jetbrains.annotations.Nullable
    private net.minecraft.world.phys.Vec3 findDeepwaterDirection() {
        BlockPos here = blockPosition();
        int bestScore = depthAt(here);
        net.minecraft.world.phys.Vec3 bestDir = null;
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int dx = (int) Math.round(Math.cos(angle) * 8);
            int dz = (int) Math.round(Math.sin(angle) * 8);
            BlockPos sample = here.offset(dx, 0, dz);
            int s = depthAt(sample);
            if (s > bestScore) {
                bestScore = s;
                bestDir = new net.minecraft.world.phys.Vec3(dx, 0, dz).normalize();
            }
        }
        return bestDir;
    }

    /** Number of water blocks below this position, capped at 8. */
    private int depthAt(BlockPos pos) {
        int d = 0;
        BlockPos cursor = pos;
        for (int i = 0; i < 8; i++) {
            cursor = cursor.below();
            if (level().getFluidState(cursor).is(net.minecraft.tags.FluidTags.WATER)) {
                d++;
            } else break;
        }
        return d;
    }

    /** Called from {@link #reactToDisturbance} after the default reaction. Subclasses override. */
    protected void onPostDisturbance(BlockPos source, DisturbanceType type, @Nullable LivingEntity sourceEntity) {}

    public int getStateTimer() {
        return stateTimer;
    }

    public void setStateTimer(int ticks) {
        this.stateTimer = ticks;
    }

    /**
     * Hook called by the Forge-side disturbance handler. Default implementation
     * gives every shark a basic reactive profile; subclasses can override to
     * customize per-species behavior (e.g. Oceanic Whitetip more persistent).
     */
    public void reactToDisturbance(BlockPos source, DisturbanceType type, @Nullable LivingEntity sourceEntity) {
        if (!this.isAlive() || !this.isInWater()) return;
        SharkState before = getSharkState();
        switch (type) {
            case LIGHT -> {
                if (before == SharkState.IDLE
                        && getRandom().nextFloat() < 0.15f) {
                    walkToward(source);
                    setSharkState(SharkState.CURIOUS);
                    stateTimer = 300;
                }
            }
            case HEAVY -> {
                float chance = before == SharkState.IDLE ? 0.60f
                        : before == SharkState.CURIOUS ? 1.0f
                        : 0f;
                if (getRandom().nextFloat() < chance) {
                    walkToward(source);
                    setSharkState(SharkState.CURIOUS);
                    stateTimer = 400;
                }
            }
            case BLOOD -> {
                if (sourceEntity != null
                        && !(sourceEntity instanceof Player p && (p.isCreative() || p.isSpectator()))
                        && sourceEntity != this) {
                    setSharkState(SharkState.HOSTILE);
                    this.setTarget(sourceEntity);
                    stateTimer = params.disengageTimeoutTicks();
                }
            }
        }
        // Audio cue when the shark first notices a disturbance.
        if (before == SharkState.IDLE && getSharkState() == SharkState.CURIOUS && !level().isClientSide) {
            level().playSound(null, this.blockPosition(),
                    net.minecraft.sounds.SoundEvents.SAND_PLACE,
                    net.minecraft.sounds.SoundSource.NEUTRAL, 0.3f, 0.6f);
        }
        onPostDisturbance(source, type, sourceEntity);
    }

    /** Public helper for shallow-water speed scaling. */
    public float shallowWaterSpeedScale() {
        BlockPos pos = blockPosition();
        int waterDepth = 0;
        for (int i = 1; i <= 4; i++) {
            if (!level().getFluidState(pos.below(i)).is(net.minecraft.tags.FluidTags.WATER)) break;
            waterDepth++;
        }
        return waterDepth < 3 ? 0.6f : 1.0f;
    }

    @Override
    protected float swimSpeedMultiplier() { return 0.22f; }
    @Override
    protected float maxHorizontalSpeed() {
        // Sharks chasing prey go meaningfully faster than the player's swim (0.13 b/t baseline).
        // Wandering sharks stay slow so they don't blow past the player on idle pathing.
        return this.getTarget() != null ? 0.75f : 0.40f;
    }

    @Override
    public void travel(@NotNull Vec3 movementInput) {
        if (this.isEffectiveAi() && this.isInWater()) {
            // Hard braking when chasing prey and within striking range — without this the
            // shark overshoots, smooth-swim steers it back, and the loop becomes a visual
            // "circle of doom" around the target. We zero the input acceleration AND apply
            // heavier friction so velocity bleeds off in a couple of ticks.
            LivingEntity tgt = this.getTarget();
            double brakeRange = tgt != null ? biteRangeAgainst(tgt) + 1.0 : 0;
            boolean braking = tgt != null && tgt.isAlive()
                    && this.distanceToSqr(tgt) <= brakeRange * brakeRange;

            float scale = shallowWaterSpeedScale();
            Vec3 effectiveInput = braking ? Vec3.ZERO : movementInput;
            this.moveRelative(this.getSpeed() * swimSpeedMultiplier() * scale, effectiveInput);
            this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
            double friction = braking ? 0.30 : (this.wasTouchingWater ? 0.78 : 0.25);
            this.setDeltaMovement(this.getDeltaMovement().scale(friction));
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

    // Minecraft is slow-paced; sharks idle 50% of the time, wander 50%.
    // Wander destinations are still wide so they explore — they just take longer to get there.
    @Override
    protected float wanderRadiusXZ() { return 36f; }
    @Override
    protected float wanderRadiusY() { return 8f; }
    @Override
    protected int idleMinTicks() { return 80; }
    @Override
    protected int idleMaxTicks() { return 160; }
    @Override
    protected int wanderWeight() { return 5; }
    @Override
    protected int idleWeight() { return 5; }

    private void walkToward(BlockPos pos) {
        BrainUtils.setMemory(this.getBrain(), MemoryModuleType.WALK_TARGET,
                new WalkTarget(Vec3.atCenterOf(pos), params.aggroSpeedMult(), 2));
    }

    // NOTE: getIdleTasks, getFightTasks, and getSensors are intentionally NOT overridden
    // here. The new shark species inherit BfsAquaticEntity's simpler defaults, which is
    // safer for SBL initialization. Prey hunting lives in onSharkTick below — a direct
    // entity scan and target set that doesn't need a custom NearbyLivingEntitySensor
    // (which appeared to break entity construction for entities that don't override
    // getSensors). Existing sharks like Mako still have their own overrides, untouched.

    /** Per-species tuning bundle. Sized for Legacy 1.0 simplicity. */
    public record SharkParams(
            float detectionRadius,
            float bloodDetectionRadius,
            int aggressionLevel,
            float aggroSpeedMult,
            float disengageDistance,
            int disengageTimeoutTicks,
            int biteCooldownTicks,
            float biteDamage
    ) {}
}
