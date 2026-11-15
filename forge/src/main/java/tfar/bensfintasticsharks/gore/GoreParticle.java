package tfar.bensfintasticsharks.gore;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;

public final class GoreParticle extends TextureSheetParticle {
    public enum Role { PARTICLE, DROPLET, MIST, WOUND, REMAINS }
    private static final float DROPLET_SCALE_RATIO = 0.18F;
    private static volatile Tuning tuning = new Tuning(0.8F, 24, 0.75F, 80, 1.0F, 120, false);

    private final SpriteSet sprites;
    private final float baseAlpha;
    private final boolean underwater;

    private GoreParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY,
                         double velocityZ, SpriteSet sprites, float size, int lifetime, float gravity,
                         float baseAlpha, boolean underwater) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.baseAlpha = baseAlpha;
        this.underwater = underwater;
        this.quadSize = size;
        this.lifetime = lifetime;
        this.gravity = underwater ? 0.0F : gravity;
        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;
        this.hasPhysics = false;
        this.roll = level.random.nextFloat() * (float) (Math.PI * 2.0D);
        this.oRoll = roll;
        this.alpha = baseAlpha;
        containUnderwater();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.setSpriteFromAge(sprites);
    }

    public static void configure(Tuning updated) {
        tuning = updated;
    }

    public static float dropletSize(float particleScale) {
        return particleScale * DROPLET_SCALE_RATIO;
    }

    @Override
    public void tick() {
        if (!BfsClientGoreConfig.enabled()) {
            remove();
            return;
        }
        super.tick();
        containUnderwater();
        this.setSpriteFromAge(sprites);
        float fadeStart = lifetime > 20 ? lifetime - 12.0F : lifetime * 0.55F;
        float fade = age > fadeStart
                ? Mth.clamp((lifetime - age) / (lifetime - fadeStart), 0.0F, 1.0F)
                : 1.0F;
        alpha = baseAlpha * fade;
    }

    private void containUnderwater() {
        if (!underwater) return;
        BlockPos.MutableBlockPos pos = BlockPos.containing(x, y, z).mutable();
        int search = (int) Math.ceil(quadSize * Math.sqrt(2.0D)) + 2;
        while (!level.getFluidState(pos).is(FluidTags.WATER) && search-- > 0) pos.move(0, -1, 0);
        if (!level.getFluidState(pos).is(FluidTags.WATER)) {
            remove();
            return;
        }
        // Search only as far as the billboard can reach above its center.
        int top = pos.getY() + (int) Math.ceil(quadSize * Math.sqrt(2.0D)) + 2;
        while (pos.getY() < top && level.getFluidState(pos.above()).is(FluidTags.WATER)) pos.move(0, 1, 0);
        double surface = pos.getY() + level.getFluidState(pos).getHeight(level, pos);
        double boundedY = GoreWaterBoundary.centerBelowSurface(y, surface, quadSize);
        if (boundedY < y) {
            setPos(x, boundedY, z);
            yd = Math.min(0.0D, yd);
        }
        if (!level.getFluidState(BlockPos.containing(x, this.y, z)).is(FluidTags.WATER)) remove();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final Role role;
        private final float gravity;

        public Provider(SpriteSet sprites, Role role, float gravity) {
            this.sprites = sprites;
            this.role = role;
            this.gravity = gravity;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double velocityX, double velocityY, double velocityZ) {
            Tuning current = tuning;
            float size = switch (role) {
                case PARTICLE -> current.particleScale;
                case DROPLET -> dropletSize(current.particleScale);
                case MIST -> current.particleScale * 0.7F;
                case WOUND -> dropletSize(current.woundScale);
                case REMAINS -> current.remainsScale;
            };
            int life = switch (role) {
                case PARTICLE, DROPLET -> current.particleLifetime;
                case MIST -> Math.min(240, current.particleLifetime);
                case WOUND -> Math.min(30, current.woundLifetime);
                case REMAINS -> current.remainsLifetime;
            };
            float opacity = role == Role.MIST ? 0.42F : 1.0F;
            return new GoreParticle(level, x, y, z, velocityX, velocityY, velocityZ,
                    sprites, size, life, gravity, opacity, current.underwater);
        }
    }

    public record Tuning(float particleScale, int particleLifetime, float woundScale, int woundLifetime,
                         float remainsScale, int remainsLifetime, boolean underwater) {
    }
}
