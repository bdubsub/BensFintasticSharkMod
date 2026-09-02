package tfar.bensfintasticsharks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class OrcaEntity extends BfsAquaticEntity<OrcaEntity> {

    private int airTicks;
    private int breachTicks;

    protected OrcaEntity(EntityType<OrcaEntity> type, Level level) {
        super(type, level);
        this.airTicks = 600 + level.getRandom().nextInt(601);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 130)
                .add(Attributes.MOVEMENT_SPEED, 1.1F)
                .add(Attributes.ATTACK_DAMAGE, 4)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4);
    }

    // Orcas roam huge ranges — give them the widest radius of any BFS mob.
    @Override
    protected float wanderRadiusXZ() { return 80f; }
    @Override
    protected float wanderRadiusY() { return 32f; }
    @Override
    protected int wanderWeight() { return 9; }
    @Override
    protected int idleWeight() { return 1; }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (!(level() instanceof ServerLevel sl)) return;
        if (breachTicks > 0) breachTicks--;
        if (airTicks > 0) { airTicks--; return; }
        BlockPos here = blockPosition();
        int surface = sl.getHeight(Heightmap.Types.WORLD_SURFACE_WG, here.getX(), here.getZ());
        if (here.getY() < surface - 1 && isInWater()) {
            Vec3 v = getDeltaMovement();
            // 0.18 — stronger run-up (0.14 → 0.22) so the orca reaches the surface with speed.
            setDeltaMovement(v.x, Math.max(v.y, 0.22), v.z);
        }
        if (!isInWater() || here.getY() >= surface - 1) {
            // Breach: launch up out of the water and fire the one-shot breach animation
            // (OrcaEntityForge.tick() reads isBreaching() and triggers it for all clients).
            if (isInWater()) {
                Vec3 v = getDeltaMovement();
                // 0.18 — Ben: "it barely breaches the surface." 0.7 b/t apexed ~3 blocks,
                // which an orca-sized body barely clears. 1.05 b/t apexes ~6.5 blocks
                // (height ≈ v²/(2×0.08)) — a proper full-body breach.
                setDeltaMovement(v.x, 1.05, v.z);
            }
            breachTicks = 30;
            sl.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, getX(), getY() + 1.5, getZ(),
                    10, 0.3, 0.3, 0.3, 0.0);
            sl.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 1, getZ(),
                    6, 0.4, 0.1, 0.4, 0.0);
            sl.playSound(null, here, SoundEvents.DOLPHIN_SPLASH, SoundSource.NEUTRAL, 1.5f, 0.5f);
            airTicks = 600 + getRandom().nextInt(601);
        }
    }

    public boolean isBreaching() { return breachTicks > 0; }

    @Override public float bfsScaleMin() { return 0.9f; }
    @Override public float bfsScaleMax() { return 1.05f; }

    /**
     * Marine-mammal land-survival window: ~20s longer than the BfsAquaticEntity default.
     * Parent refills to 300 (≈16s before drown damage); we refill to 700 (≈36s) so a beached
     * orca has time to thrash back into water.
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
            this.setAirSupply(700);
        }
    }
}
