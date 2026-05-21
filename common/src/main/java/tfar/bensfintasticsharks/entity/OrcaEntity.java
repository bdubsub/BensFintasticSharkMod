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

    protected OrcaEntity(EntityType<OrcaEntity> type, Level level) {
        super(type, level);
        this.airTicks = 600 + level.getRandom().nextInt(601);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 120)
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
        if (airTicks > 0) { airTicks--; return; }
        BlockPos here = blockPosition();
        int surface = sl.getHeight(Heightmap.Types.WORLD_SURFACE_WG, here.getX(), here.getZ());
        if (here.getY() < surface - 1 && isInWater()) {
            Vec3 v = getDeltaMovement();
            setDeltaMovement(v.x, Math.max(v.y, 0.14), v.z);
        }
        if (!isInWater() || here.getY() >= surface - 1) {
            sl.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, getX(), getY() + 1.5, getZ(),
                    10, 0.3, 0.3, 0.3, 0.0);
            sl.sendParticles(ParticleTypes.SPLASH, getX(), getY() + 1, getZ(),
                    6, 0.4, 0.1, 0.4, 0.0);
            sl.playSound(null, here, SoundEvents.DOLPHIN_SPLASH, SoundSource.NEUTRAL, 1.5f, 0.5f);
            airTicks = 600 + getRandom().nextInt(601);
        }
    }

    @Override public float bfsScaleMin() { return 0.9f; }
    @Override public float bfsScaleMax() { return 1.05f; }
}
