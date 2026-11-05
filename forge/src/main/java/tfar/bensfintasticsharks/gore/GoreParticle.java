package tfar.bensfintasticsharks.gore;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public final class GoreParticle extends TextureSheetParticle {
    public enum Role { PARTICLE, DROPLET, MIST, WOUND, REMAINS }
    private static final float DROPLET_SCALE_RATIO = 0.18F;
    private static volatile Tuning tuning = new Tuning(0.8F, 24, 0.75F, 80, 1.0F, 120, 0);

    private final SpriteSet sprites;
    private final Entity attachedEntity;
    private final double attachedOffsetX;
    private final double attachedOffsetY;
    private final double attachedOffsetZ;
    private final int startDelayTicks;
    private final float baseAlpha;

    private GoreParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY,
                        double velocityZ, SpriteSet sprites, float size, int lifetime, float gravity,
                        Entity attachedEntity, int startDelayTicks, float baseAlpha) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);
        this.sprites = sprites;
        this.attachedEntity = attachedEntity;
        this.attachedOffsetX = attachedEntity == null ? 0.0D : x - attachedEntity.getX();
        this.attachedOffsetY = attachedEntity == null ? 0.0D : y - attachedEntity.getY();
        this.attachedOffsetZ = attachedEntity == null ? 0.0D : z - attachedEntity.getZ();
        this.startDelayTicks = startDelayTicks;
        this.baseAlpha = baseAlpha;
        this.quadSize = size;
        this.lifetime = lifetime;
        this.gravity = gravity;
        this.hasPhysics = false;
        this.roll = level.random.nextFloat() * (float) (Math.PI * 2.0D);
        this.oRoll = roll;
        this.alpha = baseAlpha;
        if (startDelayTicks > 0) this.alpha = 0.0F;
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
        if (attachedEntity != null && !attachedEntity.isAlive()) {
            remove();
            return;
        }
        if (attachedEntity != null) {
            setPos(attachedEntity.getX() + attachedOffsetX,
                    attachedEntity.getY() + attachedOffsetY,
                    attachedEntity.getZ() + attachedOffsetZ);
        }
        this.setSpriteFromAge(sprites);
        if (age <= startDelayTicks) {
            alpha = 0.0F;
            return;
        }
        int effectAge = age - startDelayTicks;
        int effectLifetime = lifetime - startDelayTicks;
        float fadeStart = effectLifetime > 20 ? effectLifetime - 12.0F : effectLifetime * 0.55F;
        float fade = effectAge > fadeStart
                ? Mth.clamp((effectLifetime - effectAge) / (effectLifetime - fadeStart), 0.0F, 1.0F)
                : 1.0F;
        alpha = baseAlpha * fade;
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
                case MIST -> Math.max(0.35F, current.particleScale * 0.7F);
                case WOUND -> current.woundScale;
                case REMAINS -> current.remainsScale;
            };
            int life = switch (role) {
                case PARTICLE -> current.particleLifetime;
                case DROPLET -> current.particleLifetime;
                case MIST -> Math.min(240, current.particleLifetime);
                case WOUND -> current.woundLifetime;
                case REMAINS -> current.remainsLifetime;
            };
            int delay = role == Role.WOUND ? current.particleLifetime : 0;
            Entity attached = role == Role.WOUND && current.woundTargetId > 0
                    ? level.getEntity(current.woundTargetId) : null;
            float opacity = role == Role.MIST ? 0.42F : 1.0F;
            return new GoreParticle(level, x, y, z, velocityX, velocityY, velocityZ,
                    sprites, size, life + delay, gravity, attached, delay, opacity);
        }
    }

    public record Tuning(float particleScale, int particleLifetime, float woundScale, int woundLifetime,
                         float remainsScale, int remainsLifetime, int woundTargetId) {
    }
}
