package tfar.bensfintasticsharks.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import tfar.bensfintasticsharks.gore.BfsClientGoreConfig;
import tfar.bensfintasticsharks.gore.GoreImpactProfile;
import tfar.bensfintasticsharks.gore.GoreParticle;
import tfar.bensfintasticsharks.gore.GoreParticleLayout;
import tfar.bensfintasticsharks.gore.ModGoreParticles;
import tfar.bensfintasticsharks.network.GoreImpactPayload;

import java.util.List;

/** Applies a received visual cue only on this client and only while its local gore option is on. */
public final class ClientGoreEffects {
    private static final double PARTICLE_GAP = 0.04D;

    private ClientGoreEffects() {
    }

    public static void handle(GoreImpactPayload impact) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (!impact.isValid() || !BfsClientGoreConfig.enabled() || level == null || minecraft.player == null) return;
        if (minecraft.player.distanceToSqr(impact.x(), impact.y(), impact.z()) > 48.0D * 48.0D) return;

        boolean waterImpact = BfsBloodOverlay.recordImpact(level, impact.targetEntityId(),
                impact.x(), impact.y(), impact.z());
        GoreImpactProfile profile = GoreImpactProfile.forKind(impact.kind(), impact.settings());
        RandomSource random = level.random;
        GoreParticle.configure(new GoreParticle.Tuning(profile.particleScale(), profile.particleLifetime(),
                profile.woundScale(), profile.woundLifetime(), profile.remainsScale(), profile.remainsLifetime(),
                impact.targetEntityId()));
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Vec3 cameraRight = fromVector(camera.getLeftVector()).scale(-1.0D);
        Vec3 cameraUp = fromVector(camera.getUpVector());
        double phase = random.nextDouble() * Math.PI * 2.0D;
        double centerSize = profile.particleCount() > 0 ? profile.particleScale() : 0.0D;
        if (profile.particleCount() > 0) {
            SimpleParticleType splatter = random.nextBoolean()
                    ? ModGoreParticles.BLOOD_SPLATTER.get() : ModGoreParticles.BLOOD_SPLATTER_DARK.get();
            level.addParticle(splatter, impact.x(), impact.y(), impact.z(), 0.0D, 0.0D, 0.0D);

            float dropletSize = GoreParticle.dropletSize(profile.particleScale());
            List<GoreParticleLayout.Offset> droplets = GoreParticleLayout.separatedOffsets(
                    profile.particleCount() - 1, centerSize, dropletSize, PARTICLE_GAP, phase);
            for (GoreParticleLayout.Offset offset : droplets) {
                Vec3 displacement = cameraRight.scale(offset.right()).add(cameraUp.scale(offset.up()));
                Vec3 outwardVelocity = displacement.normalize().scale(0.02D);
                level.addParticle(ModGoreParticles.BLOOD_DROP.get(), impact.x() + displacement.x,
                        impact.y() + displacement.y, impact.z() + displacement.z,
                        outwardVelocity.x, outwardVelocity.y, outwardVelocity.z);
            }
        }
        int mistCount = Math.min(48, (int) Math.ceil(profile.mistCount() * (waterImpact ? 1.35D : 1.0D)));
        for (int index = 0; index < mistCount; index++) {
            Vec3 direction = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
            if (direction.lengthSqr() < 1.0E-8D) direction = new Vec3(0.0D, 1.0D, 0.0D);
            direction = direction.normalize();
            Vec3 offset = direction.scale(random.nextDouble() * profile.mistSpreadRadius());
            Vec3 velocity = direction.scale(profile.mistSpeed() * (0.65D + random.nextDouble() * 0.75D));
            level.addParticle(ModGoreParticles.BLOOD_MIST.get(),
                    impact.x() + offset.x, impact.y() + offset.y, impact.z() + offset.z,
                    velocity.x, velocity.y, velocity.z);
        }
        Entity woundTarget = level.getEntity(impact.targetEntityId());
        if (!impact.fatal() && profile.woundLifetime() > 0 && woundTarget != null) {
            level.addParticle(ModGoreParticles.BLOOD_WOUND.get(), impact.x(), impact.y(), impact.z(),
                    0.0D, 0.0D, 0.0D);
        }
        if (impact.fatal()) {
            List<GoreParticleLayout.Offset> remains = GoreParticleLayout.separatedOffsets(profile.remainsCount(),
                    centerSize, profile.remainsScale(), PARTICLE_GAP, phase + Math.PI);
            for (GoreParticleLayout.Offset offset : remains) {
                Vec3 displacement = cameraRight.scale(offset.right()).add(cameraUp.scale(offset.up()));
                level.addParticle(ModGoreParticles.BLOOD_REMAINS.get(), impact.x() + displacement.x,
                        impact.y() + displacement.y, impact.z() + displacement.z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private static Vec3 fromVector(Vector3f vector) {
        return new Vec3(vector.x(), vector.y(), vector.z());
    }
}
