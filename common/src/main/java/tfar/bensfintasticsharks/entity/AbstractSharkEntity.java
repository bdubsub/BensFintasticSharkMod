package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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
    /**
     * Hunt cooldown countdown. While > 0, the shark will not scan for prey,
     * will not retaliate against attackers (except players, who are always
     * fair game in self-defense), and stays in IDLE/CURIOUS. Decremented every
     * server tick. Set to {@link #HUNT_COOLDOWN_TICKS} the moment a hunt ends
     * (target dies or escapes). Persisted in NBT so it survives saves.
     *
     * <p>This is the "one prey per hunting cycle" rule — a satiated shark
     * cruises lazily and doesn't murder everything in sight.</p>
     */
    private int huntCooldown;
    /** 10 real-time minutes = 12000 ticks at 20 t/s. */
    public static final int HUNT_COOLDOWN_TICKS = 12_000;
    /** Last target we were locked onto. Used to detect "target just died this tick" so we can start the cooldown. */
    private LivingEntity lastHuntTarget;
    /** Scheduled hit: damage lands a few ticks after the bite animation triggers so the visual matches the hit. */
    private LivingEntity pendingBiteTarget;
    private int pendingBiteTicks;
    /**
     * Sustained flight from a larger shark (Ben 0.19 suggestion). While {@code > 0} the
     * shark does not hunt, does not retaliate, and keeps re-pathing away from
     * {@link #fleeFrom}. Transient — deliberately not persisted (a 6s panic, not a mood).
     */
    private int fleeTicks;
    @Nullable
    private LivingEntity fleeFrom;
    /** How many ticks after the swing the actual damage frame lands. Override per-species if needed. */
    protected int biteImpactDelayTicks() { return 5; }

    protected AbstractSharkEntity(EntityType<T> type, Level level, SharkParams params) {
        super(type, level);
        this.params = params;
        this.moveControl = new SharkSwimmingMoveControl(this, 1f / 8f);
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

    // Bug 3: sharks are a protected/conservation species and must drop nothing, including XP.
    // WaterAnimal.getExperienceReward() otherwise returns 1-3 XP unconditionally. Returning 0
    // here (shared by all 8 sharks) zeroes both the vanilla LivingEntity death path and the
    // explicit dropExperience() calls in the Forge subclasses' tickDeath() animation hooks.
    @Override
    public int getExperienceReward() {
        return 0;
    }

    /** Forge-side hook to fire a bite/attack animation when {@link #onSharkTick} swings. */
    protected void onBiteAttack(LivingEntity target) {}

    /**
     * Effective bite distance against a target. Vanilla's getAttackReachSqr would give
     * a 5+ block reach for our 2.5-wide sharks, which felt like a staring contest. We
     * cap it tighter so the bite has to actually contact the prey.
     */
    protected double biteRangeAgainst(LivingEntity target) {
        // Give the visible snout a little more room than the compact collision box. This is
        // still far below vanilla's width-based reach for large mobs, but no longer asks a
        // great white's nose to overlap the victim before an animated bite can connect.
        return (this.getBbWidth() * 0.65F) + 1.25 + (target.getBbWidth() * 0.5F);
    }

    protected double closeBiteRangeAgainst(LivingEntity target, double contactPadding) {
        return this.getBbWidth() * 0.5F + target.getBbWidth() * 0.5F + contactPadding;
    }

    /**
     * Called the tick the bite damage actually lands (after {@link #biteImpactDelayTicks()}
     * has elapsed and the target is still in range). Species override this to apply
     * signature effects like Mako/Great White's grab, Thresher's tail-whip slow,
     * Whitetip's lingering wound, etc. Default does nothing.
     */
    protected void onBiteLanded(LivingEntity target) {}

    /**
     * This species' prey list (Part II hunger spec). Each of the 8 sharks overrides with
     * its own {@code bensfintasticsharks:prey/<species>} tag; the broad legacy tag is only
     * the fallback for any future AbstractSharkEntity subclass that hasn't picked one.
     */
    protected net.minecraft.tags.TagKey<EntityType<?>> preyTag() {
        return tfar.bensfintasticsharks.init.ModTags.EntityTypes.SHARK_PREY;
    }

    /**
     * Single hunt-target predicate shared by the base prey scan AND the legacy species'
     * SBL sensors/behaviours (which previously each had their own canTarget and were the
     * feeding-frenzy loophole — "any mob under 50% health" with no hunger check).
     *
     * <p>Rules: non-player prey requires an empty hunger clock and membership in this
     * species' {@link #preyTag()}. Wounded players are an opportunistic target only for
     * species that opt into {@link #canJoinPlayerFeedingFrenzy()} (blacktips); every shark
     * can still target a player through its own disturbance or retaliation.</p>
     */
    public boolean canHuntTarget(LivingEntity target) {
        if (target == this || target.getType() == this.getType()) return false;
        if (!this.isInWater()) return false;
        if (!target.isAlive() || target.isDeadOrDying()) return false;
        if (target.getVehicle() == this) return false;
        if (target instanceof Player player) {
            if (player.isCreative() || player.isSpectator()) return false;
            // Only schooling blacktips opportunistically join another shark's attack on a
            // wounded swimmer. Other species still attack players that disturb or hurt them,
            // but do not pile into a cross-species feeding frenzy just because blood exists.
            return canJoinPlayerFeedingFrenzy()
                    && target.getHealth() / target.getMaxHealth() <= 0.5f;
        }
        if (isOnHuntCooldown()) return false;
        return target.getType().is(preyTag());
    }

    /** Whether this species may acquire a wounded player from blood/nearby-prey scans. */
    public boolean canJoinPlayerFeedingFrenzy() {
        return false;
    }

    /**
     * Selects one deterministic non-schooling shark to investigate fresh player blood.
     * If a non-blacktip is already attacking that player it remains the sole responder;
     * otherwise the closest eligible non-blacktip starts the encounter. Blacktips are
     * handled separately by {@link #canJoinPlayerFeedingFrenzy()} and may school.
     */
    private boolean isPrimaryPlayerBloodResponder(LivingEntity bleedingPlayer) {
        if (canJoinPlayerFeedingFrenzy()) return false;
        var area = bleedingPlayer.getBoundingBox().inflate(24.0);
        java.util.List<AbstractSharkEntity> nearby = level().getEntitiesOfClass(
                AbstractSharkEntity.class, area,
                shark -> shark.isAlive() && shark.isInWater()
                        && !shark.canJoinPlayerFeedingFrenzy()
                        && (shark.getTarget() == null || shark.getTarget() == bleedingPlayer)
                        && (!shark.isOnHuntCooldown() || shark.getTarget() == bleedingPlayer));
        boolean encounterInProgress = nearby.stream()
                .anyMatch(shark -> shark.getTarget() == bleedingPlayer);
        if (encounterInProgress) return this.getTarget() == bleedingPlayer;
        return nearby.stream()
                .min(java.util.Comparator
                        .comparingDouble((AbstractSharkEntity shark) -> shark.distanceToSqr(bleedingPlayer))
                        .thenComparingInt(net.minecraft.world.entity.Entity::getId))
                .orElse(null) == this;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (biteCooldown > 0) biteCooldown--;
        if (huntCooldown > 0) huntCooldown--;
        // Land the scheduled bite at the visual peak of the animation.
        if (pendingBiteTicks > 0) {
            pendingBiteTicks--;
            if (pendingBiteTicks == 0) {
                // Release the slot UNCONDITIONALLY (0.19 review): the old code only nulled
                // the target inside the isAlive() branch, so a victim dying during the
                // impact delay (packmate kill, thrash tick, /kill, chunk unload) wedged
                // pendingBiteTarget forever and the shark could never bite again.
                LivingEntity victim = pendingBiteTarget;
                pendingBiteTarget = null;
                if (victim != null && victim.isAlive()) {
                    double reach = biteRangeAgainst(victim);
                    if (this.distanceToSqr(victim) <= reach * reach) {
                        this.doHurtTarget(victim);
                        onBiteLanded(victim);
                    }
                }
            }
        }
        // Persistent pursuit: the timeout is now time spent outside the species' disengage
        // radius, not a fixed lifetime for every attack. Previously every shark forgot a live,
        // nearby target after exactly 15-20 seconds — often just after ejecting a thrash victim.
        LivingEntity timedTarget = this.getTarget();
        // The four older SBL brains can assign Mob#setTarget before the shared prey scan runs.
        // Normalize that path into the synced state machine so animation and timeout behavior
        // are identical regardless of which sensor noticed the prey first.
        if (timedTarget != null && timedTarget.isAlive() && this.isInWater()
                && getSharkState() != SharkState.HOSTILE) {
            setSharkState(SharkState.HOSTILE);
            stateTimer = effectiveDisengageTimeoutTicks();
        }
        boolean activelyTracking = getSharkState() == SharkState.HOSTILE
                && timedTarget != null && timedTarget.isAlive()
                && this.distanceToSqr(timedTarget)
                    <= effectiveDisengageDistance() * effectiveDisengageDistance();
        if (activelyTracking) {
            stateTimer = effectiveDisengageTimeoutTicks();
        }
        // Drop out of non-IDLE states when the target has remained lost/far away long enough.
        if (stateTimer > 0) {
            if (!activelyTracking) stateTimer--;
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
        // Track the active hunt target so we can detect a successful kill.
        if (tgt != null && tgt != lastHuntTarget) {
            lastHuntTarget = tgt;
        }
        // Our last hunt target just died. Only credit a successful hunt — and the 10-minute
        // satiation cooldown — if WE actually killed it (0.19 fix). The old code started the
        // cooldown on ANY death of the last target, which produced two reported bugs:
        //   • Blacktips "never attack anything": reef blacktips school and BLOOD scans latch
        //     several onto the same fish; whichever one lands the kill satiates the rest, so
        //     the whole school goes docile for 10 minutes and re-satiates before it wears off.
        //   • Big shark "gives up the chase": while a great white/tiger chases a blacktip, the
        //     blacktip eats a cod; the cod's death (or a stray retarget onto it) credited the
        //     big shark with a kill it never made, dropping it into IDLE + cooldown.
        // getKillCredit() returns the entity the combat tracker blames for the kill, so
        // "== this" is true only for our own bite/thrash/tail-whip. getKillCredit() prefers a
        // recent player attacker over the mob, so a player softening the prey before our fatal
        // bite would otherwise leave us un-satiated (perpetual hunting near players) — fall back
        // to getLastHurtByMob(), which is the last MOB to hit it (= us, when we land the kill).
        // A schooling packmate that merely targeted but never bit the prey is still not the last
        // mob hitter, so this does not reopen the whole-school satiation bug.
        if (lastHuntTarget != null && !lastHuntTarget.isAlive()) {
            boolean ourKill = lastHuntTarget.getKillCredit() == this
                    || lastHuntTarget.getLastHurtByMob() == this;
            lastHuntTarget = null;
            if (ourKill) {
                huntCooldown = HUNT_COOLDOWN_TICKS;
            }
            this.setTarget(null);
            setSharkState(SharkState.IDLE);
            ticksTargetOutOfWater = 0;
            tgt = null;
        }
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

        // Sustained flight from a larger shark (Ben 0.19). While fleeing the shark neither
        // hunts nor chases — it just keeps re-pathing away every half-second until the timer
        // runs out or it has opened up a safe gap.
        if (fleeTicks > 0) {
            fleeTicks--;
            LivingEntity from = fleeFrom;
            if (from == null || !from.isAlive() || from.isRemoved()
                    || this.distanceToSqr(from) > 40.0 * 40.0) {
                fleeTicks = 0;
                fleeFrom = null;
            } else {
                if (tickCount % 10 == 0) {
                    Vec3 away = position().subtract(from.position()).normalize().scale(12).add(position());
                    BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET, new WalkTarget(away, 1.5f, 1));
                }
                return;
            }
        }

        // Prey scan: every 20 ticks (staggered by entity id so they don't all scan
        // the same tick), look for huntable living entities in water and lock onto
        // the closest. Replaces the SBL TargetOrRetaliate behaviour. canHuntTarget is
        // the single authority (Part II spec): per-species preyTag() for mobs, gated
        // by the hunt cooldown. Only blacktips opt into opportunistically acquiring a
        // wounded player, preventing blood from pulling every nearby species into the same
        // frenzy. Shark-on-shark predation is allowed when the tag says so; same-species never.
        if (this.getTarget() == null && (tickCount % 20) == (this.getId() & 0xF) % 20) {
            float radius = effectiveDetectionRadius();
            var preyArea = this.getBoundingBox().inflate(radius);
            java.util.List<LivingEntity> prey = level().getEntitiesOfClass(LivingEntity.class, preyArea,
                    e -> e.isInWater() && canHuntTarget(e));
            if (!prey.isEmpty()) {
                LivingEntity closest = prey.stream()
                        .min(java.util.Comparator.comparingDouble(this::distanceToSqr))
                        .orElseThrow();
                this.setTarget(closest);
                setSharkState(SharkState.HOSTILE);
                stateTimer = effectiveDisengageTimeoutTicks();
            }
        }

        // Active chase: while a target exists, refresh the walk target every 4
        // ticks at aggro speed. The former 10-tick snapshot lagged behind fast or vertically
        // moving prey and made sharks orbit yesterday's path node in a figure eight.
        // Skip walk-target refresh once we're already in bite range so the move
        // control doesn't keep pushing us forward over the target.
        LivingEntity tgt = this.getTarget();
        if (tgt != null && tgt.isAlive()) {
            double biteRange = biteRangeAgainst(tgt);
            double biteRangeSqr = biteRange * biteRange;
            boolean inBiteRange = this.distanceToSqr(tgt) <= biteRangeSqr;
            if ((tickCount + getId()) % 4 == 0) {
                if (!inBiteRange) {
                    Vec3 targetCenter = new Vec3(tgt.getX(), tgt.getY(0.5), tgt.getZ());
                    // A short lead keeps the waypoint ahead of lateral prey without making
                    // erratic targets yank the path around. Four ticks is the refresh interval.
                    Vec3 chasePoint = targetCenter.add(tgt.getDeltaMovement().scale(2.0));
                    BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET,
                            new WalkTarget(chasePoint, params.aggroSpeedMult(), 1));
                } else {
                    // Drop any active walk target so the brain stops pushing the entity forward.
                    BrainUtils.clearMemory(getBrain(), MemoryModuleType.WALK_TARGET);
                }
            }
            // Trigger the bite animation when in range, but defer the actual damage
            // until the visual impact frame so the hit syncs with the model snap.
            // Checked EVERY tick (0.19): behind the old %10 gate a fast shark could
            // cross the whole bite disc between checks and never fire, which fed the
            // overshoot-orbit loop.
            if (inBiteRange
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

        // Ben 0.19 suggestion: a smaller shark bolts when a meaningfully larger shark bites
        // it rather than turning to fight (and dragging its packmates into a losing brawl).
        // Runs before the cooldown/retaliation logic so a satiated small shark still flees,
        // and returns here so the help-call never fires for the fleeing shark.
        if (attacker instanceof AbstractSharkEntity<?> bigger && isSmallerThan(bigger)) {
            startFleeing(attacker);
            return wasHurt;
        }

        // Species veto on fighting back (e.g. a lone blacktip is too timid to retaliate).
        if (!retaliatesAgainst(attacker)) {
            // Drop any current aggro first (0.19 review): a timid shark that was already
            // hunting keeps chasing otherwise — onSharkTick's chase overwrites the flee
            // walk-target every 10t while a target is set, and the blacktip Forge
            // pack-alert gate (getTarget() == attacker) would fire despite the timidity.
            this.setTarget(null);
            setSharkState(SharkState.IDLE);
            onRetaliationDeclined(attacker);
            return wasHurt;
        }

        this.setTarget(attacker);
        setSharkState(SharkState.HOSTILE);
        stateTimer = effectiveDisengageTimeoutTicks();

        // Throttle the help-call. Big hits always call; small hits only sometimes
        // so a piranha-style bunch of small ticks doesn't drag every shark in.
        // Tightened from 4 helpers / 32-block radius to 2 helpers / 16 blocks so
        // a single shark attack doesn't pull the entire local population.
        if (alertsPackmatesWhenAttacked()
                && (amount > 4f || getRandom().nextFloat() < 0.20f)) {
            float helpRadius = 16f;
            var helpers = level().getEntitiesOfClass(AbstractSharkEntity.class,
                    this.getBoundingBox().inflate(helpRadius),
                    s -> s != this && s.isAlive() && s.isInWater()
                            && (s.getTarget() == null || s.getTarget() == attacker)
                            && s.getType() == this.getType()
                            // Satiated packmates ignore mob-on-shark scuffles; a player
                            // attack is pack defense and always brings help.
                            && (attacker instanceof Player || !s.isOnHuntCooldown()));
            int called = 0;
            for (AbstractSharkEntity<?> helper : helpers) {
                if (called >= 2) break;
                helper.setTarget(attacker);
                helper.setSharkState(SharkState.HOSTILE);
                helper.setStateTimer(effectiveDisengageTimeoutTicks());
                called++;
            }
        }
        return wasHurt;
    }

    /**
     * Whether this shark fights back when hurt by {@code attacker}. Default: always.
     * Species override for temperament (blacktip reef sharks are timid when alone).
     */
    protected boolean retaliatesAgainst(LivingEntity attacker) {
        return true;
    }

    /** Only true pack species should turn a player's fight with one shark into a group fight. */
    protected boolean alertsPackmatesWhenAttacked() {
        return false;
    }

    /** Called instead of targeting when {@link #retaliatesAgainst} vetoes. Default: bolt away. */
    protected void onRetaliationDeclined(LivingEntity attacker) {
        Vec3 away = position().subtract(attacker.position()).normalize().scale(12).add(position());
        BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET, new WalkTarget(away, 1.4f, 1));
    }

    /** Begin ~6s of sustained flight away from {@code from}; drops any current target/hunt. */
    protected void startFleeing(LivingEntity from) {
        this.fleeFrom = from;
        this.fleeTicks = 120;
        this.setTarget(null);
        setSharkState(SharkState.IDLE);
        Vec3 away = position().subtract(from.position()).normalize().scale(12).add(position());
        BrainUtils.setMemory(getBrain(), MemoryModuleType.WALK_TARGET, new WalkTarget(away, 1.5f, 1));
    }

    /** True while this shark is actively fleeing a larger one (used by species tick hooks). */
    public boolean isFleeing() { return fleeTicks > 0; }

    /**
     * Whether {@code other} is a meaningfully larger shark (≥25% wider at effective, scaled
     * size). Uses the registered base width × per-entity {@link #getBfsScale()} so it's correct
     * even for the species that don't call {@code refreshDimensions()} each tick.
     */
    private boolean isSmallerThan(AbstractSharkEntity<?> other) {
        float mine = this.getType().getWidth() * this.getBfsScale();
        float theirs = other.getType().getWidth() * other.getBfsScale();
        return theirs > mine * 1.25f;
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
                        && sourceEntity != this
                        // Don't pack-target other sharks just because they're
                        // bleeding. Same-species was already blocked elsewhere,
                        // but cross-species (e.g. great white bleeding from a
                        // jellyfish sting) used to drag every other shark in
                        // the 56-block radius into the fight.
                        && !(sourceEntity instanceof AbstractSharkEntity<?>)
                        // Satiated sharks ignore blood — one prey per cycle.
                        && huntCooldown <= 0
                        // 0.19 review: bleeding MOBS must still be on this species'
                        // prey list — otherwise "hunt any wounded mob" survives via
                        // the blood path. Bleeding players stay fair game (that's
                        // the intended player-aggression channel).
                        && (sourceEntity instanceof Player
                                ? (this.getTarget() == sourceEntity
                                    || canJoinPlayerFeedingFrenzy()
                                    || isPrimaryPlayerBloodResponder(sourceEntity))
                                : canHuntTarget(sourceEntity))
                        // 0.20 — don't abandon an ACTIVE chase for a bystander's blood. A great
                        // white already HOSTILE-locked onto a live blacktip used to get yanked
                        // onto whatever the blacktip was biting; the fish then died to the blacktip
                        // and the great white was (falsely) satiated. Gate on "hostile with a live
                        // target" specifically — NOT "has any target" — so the whitetip's blood
                        // convergence (SharkAlertHandler pre-sets the target on an IDLE/CURIOUS
                        // shark before calling this) can still flip it to HOSTILE.
                        && (getSharkState() != SharkState.HOSTILE
                                || this.getTarget() == null || !this.getTarget().isAlive())) {
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
        // Wandering sharks stay slow so they don't blow past the player on idle pathing. A shark
        // fleeing a bigger shark gets the chase cap too (it has no target, so it would otherwise
        // be pinned to the slow wander cap and never open the gap the flee expects).
        return (this.getTarget() != null || isFleeing()) ? 0.75f : 0.40f;
    }

    /**
     * Extra thrust while locked onto prey. SmoothSwimmingMoveControl hands travel() a
     * forward input of only speed×0.125, so the friction equilibrium for an attacking
     * shark settles at ~0.14-0.17 b/t (≈3 m/s) — slower than a sprint-swimming player
     * (~0.28 b/t / 5.6 m/s), which is how Ben could outswim every species. The boost
     * roughly doubles chase acceleration; species keep their relative speed ordering.
     */
    protected float chaseAccelBoost() { return 2.2f; }

    /**
     * Hard lower bound on horizontal speed while chasing (outside the brake zone).
     * Catches the slower-attribute species (hammerhead, sandtiger, mako's low base
     * speed) whose boosted equilibrium still lands under the player's sprint-swim.
     * 0.32 b/t ≈ 6.4 m/s vs the player's ~5.6 m/s. Mako overrides higher — fastest
     * shark in the ocean, fastest in the mod.
     */
    protected float chaseSpeedFloor() { return 0.32f; }

    /**
     * Enforces {@link #chaseSpeedFloor()} on the current delta movement. Shared by the
     * base {@link #travel} and the four species with bespoke travel() overrides (Great
     * White, Great Hammerhead, Common Thresher, Shortfin Mako) — the first cut of the
     * 0.18 speed fix only patched the base method, which those four never reach in water.
     *
     * <p>Guards (review round): the floor only applies while locked on, outside the brake
     * zone, and while the move control is actually steering ({@code movementInput} check —
     * otherwise it would amplify residual drift up the floor in a stale direction whenever
     * the navigation idles). It also respects the shallow-water slowdown so a chase can't
     * bypass {@link #shallowWaterSpeedScale()}.</p>
     */
    protected void applyChaseFloor(Vec3 movementInput) {
        LivingEntity tgt = this.getTarget();
        if (tgt == null || !tgt.isAlive()) return;
        double brakeRange = biteRangeAgainst(tgt);
        double distSqr = this.distanceToSqr(tgt);
        // Inside the bite range travel()'s hard brake owns the velocity — hands off.
        if (distSqr <= brakeRange * brakeRange) return;
        if (movementInput.lengthSqr() < 1.0e-4) return;
        // 0.19 — approach band (biteRange + 2.0): taper the floor near the bite disc so an
        // overshooting shark doesn't carry full speed through the final turn. Not dropped —
        // a floor-free band let sprint-swimming players hold a fixed gap forever. 0.30 b/t
        // still outruns the player and its ~1.7-block turn radius fits every bite range.
        double band = brakeRange + 2.0;
        boolean inBand = distSqr <= band * band;
        double floor = (inBand ? Math.min(chaseSpeedFloor(), 0.30f) : chaseSpeedFloor())
                * shallowWaterSpeedScale();

        // 0.19 — the floor now drives velocity straight along the shark's BODY-FORWARD axis
        // instead of a velocity/target blend. The old blend could point the delta-movement in
        // a direction the body hadn't turned to yet (the move control slews yaw only 10°/tick),
        // so the shark translated sideways or backwards while facing elsewhere — Ben's "gliding
        // backwards while chasing". Pinning speed to the facing direction makes velocity ≡
        // heading, so there is no sideways/backward glide during a chase, and it can never
        // shove the shark in reverse.
        float yawRad = this.yBodyRot * ((float) Math.PI / 180f);
        double fx = -Mth.sin(yawRad);
        double fz = Mth.cos(yawRad);
        // Only boost when the body is actually pointed at the prey (within ~60°). While
        // mis-aimed the floor stays OFF: friction bleeds the speed, the turn radius collapses,
        // the move control re-points the body, and the floor re-engages once aligned. Gating on
        // FACING (not velocity) is what both kills the old orbiting "spin of doom" and guarantees
        // we never floor a heading the body isn't holding.
        Vec3 toTgt = new Vec3(tgt.getX() - getX(), 0, tgt.getZ() - getZ());
        double toLen = toTgt.length();
        if (toLen < 1.0e-4) return;
        // When prey is mostly above/below us, horizontal flooring creates an artificial
        // orbit while the move controller is trying to climb or dive. Let vertical steering
        // own that approach until the horizontal and vertical gaps are comparable again.
        double verticalGap = Math.abs(tgt.getY(0.5) - this.getY(0.5));
        if (verticalGap > toLen * 1.5) return;
        double facingDot = (fx * toTgt.x + fz * toTgt.z) / toLen;
        if (facingDot <= 0.5) return;
        Vec3 dm = getDeltaMovement();
        double horiz = Math.sqrt(dm.x * dm.x + dm.z * dm.z);
        if (horiz >= floor) return;
        setDeltaMovement(fx * floor, dm.y, fz * floor);
    }

    /**
     * Damps any horizontal velocity component pointing OPPOSITE the body's facing, so a
     * coasting/turning shark never visibly slides tail-first (Ben 0.19: "sharks can't swim
     * backwards"). Purely lateral (banking) velocity is untouched, so turns still read as
     * fluid — only true reverse motion is bled off. A knockback shove still lands; it's just
     * curbed to a brief nudge instead of a sustained backslide.
     */
    protected void dampBackslide() {
        Vec3 dm = getDeltaMovement();
        if (dm.x * dm.x + dm.z * dm.z < 1.0e-6) return;
        float yawRad = this.yBodyRot * ((float) Math.PI / 180f);
        double fx = -Mth.sin(yawRad);
        double fz = Mth.cos(yawRad);
        double along = dm.x * fx + dm.z * fz; // signed speed along body-forward
        if (along >= 0) return;               // moving forward or purely sideways — fine
        double remove = 0.85;                 // strip 85% of the backward component
        setDeltaMovement(dm.x - fx * along * remove, dm.y, dm.z - fz * along * remove);
    }

    /**
     * Shared in-water swim step for the base shark and the four species with bespoke
     * {@code travel()} overrides (Great White, Great Hammerhead, Common Thresher, Shortfin
     * Mako). Applies the chase-acceleration burst, hard braking inside bite range, friction,
     * the horizontal speed cap, the chase floor, and backslide damping in one place so all
     * five paths stay in lock-step.
     *
     * @param useSwimMultiplier true for the base tuning (accel scaled by {@link #swimSpeedMultiplier()},
     *        friction {@code waterFriction}); false for the grabber species which accelerate on
     *        raw {@link #getSpeed()} with their own lower friction.
     */
    protected void swimInWater(Vec3 movementInput, double waterFriction, double idleSink, boolean useSwimMultiplier) {
        LivingEntity tgt = this.getTarget();
        // While a victim is grabbed (grabber species) the prey is already caught — don't brake
        // or burst, just cruise so the grab/thrash keeps the exact feel it had before 0.19.
        boolean hasPassenger = !this.getPassengers().isEmpty();
        double brakeRange = tgt != null ? biteRangeAgainst(tgt) : 0;
        boolean braking = !hasPassenger && tgt != null && tgt.isAlive()
                && this.distanceToSqr(tgt) <= brakeRange * brakeRange;
        // 0.19 — the burst that was missing on the four bespoke overrides (Ben: "Great White
        // doesn't increase speed when chasing"). Chasing multiplies acceleration so the shark
        // visibly lunges instead of coasting at the flat floor speed.
        boolean chasing = !hasPassenger && tgt != null && tgt.isAlive() && !braking;
        // 0.20 — a shark fleeing a bigger shark (no target) gets the same burst so it can
        // actually pull away, not just walk off at wander speed while the hunter closes.
        boolean fleeing = !hasPassenger && isFleeing();
        float scale = shallowWaterSpeedScale();
        if (chasing || fleeing) scale *= chaseAccelBoost();
        Vec3 effectiveInput = braking ? Vec3.ZERO : movementInput;
        float accel = useSwimMultiplier
                ? this.getSpeed() * swimSpeedMultiplier() * scale
                : this.getSpeed() * scale;
        this.moveRelative(accel, effectiveInput);
        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());
        double friction = braking ? 0.30 : (this.wasTouchingWater ? waterFriction : 0.25);
        this.setDeltaMovement(this.getDeltaMovement().scale(friction));
        Vec3 dm = this.getDeltaMovement();
        double horiz = Math.sqrt(dm.x * dm.x + dm.z * dm.z);
        float cap = maxHorizontalSpeed();
        if (horiz > cap) {
            double s = cap / horiz;
            this.setDeltaMovement(dm.x * s, dm.y, dm.z * s);
        } else {
            applyChaseFloor(movementInput);
        }
        if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -idleSink, 0.0));
        }
        dampBackslide();
    }

    @Override
    public void travel(@NotNull Vec3 movementInput) {
        if (this.isEffectiveAi() && this.isInWater()) {
            // Braking, chase burst, cap, chase floor and backslide damping all live in the
            // shared helper so this path and the four bespoke species overrides can't drift
            // apart. Braking (inside bite range) zeroes input and applies heavy friction so
            // the shark doesn't overshoot into the old "circle of doom"; the brake zone is
            // kept strictly inside the bite range so slower species never stall in a dead
            // annulus one block short of the target.
            swimInWater(movementInput, 0.78, 0.002, true);
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

    /** Ticks remaining until this shark will hunt again. 0 means ready to hunt. */
    public int getHuntCooldown() { return huntCooldown; }
    /** True when the shark is satiated and won't actively hunt new prey. */
    public boolean isOnHuntCooldown() { return huntCooldown > 0; }
    /** Force-set the hunt cooldown. Mostly useful for debug / commands. */
    public void setHuntCooldown(int ticks) { this.huntCooldown = Math.max(0, ticks); }

    @Override
    public void addAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("HuntCooldown", huntCooldown);
    }

    @Override
    public void readAdditionalSaveData(@NotNull net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("HuntCooldown")) {
            huntCooldown = tag.getInt("HuntCooldown");
        }
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
