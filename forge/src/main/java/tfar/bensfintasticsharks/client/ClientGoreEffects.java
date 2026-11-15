package tfar.bensfintasticsharks.client;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.event.TickEvent;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Applies a received visual cue only on this client and only while its local gore option is on. */
public final class ClientGoreEffects {
    private static final double PARTICLE_GAP = 0.04D;
    private static final Map<Integer, BloodTrail> TRAILS = new LinkedHashMap<>();
    private static ClientLevel trailLevel;

    private ClientGoreEffects() {
    }

    public static void handle(GoreImpactPayload impact) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (!impact.isValid() || !BfsClientGoreConfig.enabled() || level == null || minecraft.player == null) return;
        if (minecraft.player.distanceToSqr(impact.x(), impact.y(), impact.z()) > 48.0D * 48.0D) return;

        Entity target = level.getEntity(impact.targetEntityId());
        boolean waterImpact = BfsBloodOverlay.recordImpact(level, impact.targetEntityId(),
                impact.x(), impact.y(), impact.z()) || target != null && target.isInWater();
        GoreImpactProfile profile = GoreImpactProfile.forKind(impact.kind(), impact.settings());
        if (target != null) profile = profile.forTarget(target.getBbWidth(), target.getBbHeight());
        RandomSource random = level.random;
        GoreParticle.configure(new GoreParticle.Tuning(profile.particleScale(), profile.particleLifetime(),
                profile.woundScale(), profile.woundLifetime(), profile.remainsScale(), profile.remainsLifetime(),
                waterImpact));
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
        if (trailLevel != level) {
            TRAILS.clear();
            trailLevel = minecraft.level;
        }
        if (!impact.fatal() && target != null && profile.woundLifetime() > 0 && profile.particleCount() > 0) {
            if (TRAILS.size() >= 128 && !TRAILS.containsKey(target.getId())) {
                TRAILS.remove(TRAILS.keySet().iterator().next());
            }
            TRAILS.put(target.getId(), new BloodTrail(target, target.position(), profile,
                    level.getGameTime() + Math.min(200, profile.woundLifetime())));
        } else {
            TRAILS.remove(impact.targetEntityId());
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

    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || level != trailLevel || !BfsClientGoreConfig.enabled()) {
            TRAILS.clear();
            trailLevel = level;
            return;
        }
        var iterator = TRAILS.values().iterator();
        while (iterator.hasNext()) {
            BloodTrail trail = iterator.next();
            Entity target = trail.target;
            if (!target.isAlive() || target.isRemoved() || level.getGameTime() >= trail.endsAt) {
                iterator.remove();
                continue;
            }
            Vec3 current = target.position();
            double distance = trail.previous.distanceTo(current);
            if (distance > 8.0D) {
                iterator.remove();
                continue;
            }
            if (level.getGameTime() % 2 == 0) {
                GoreImpactProfile profile = trail.profile;
                int count = Math.min(8, Math.max(1, (int) Math.ceil(distance / 0.15D)));
                for (int index = 1; index <= count; index++) {
                    Vec3 point = trail.previous.lerp(current, index / (double) count)
                            .add(0.0D, target.getBbHeight() * 0.4D, 0.0D);
                    boolean water = target.isInWater() || level.getFluidState(BlockPos.containing(point)).is(FluidTags.WATER);
                    GoreParticle.configure(new GoreParticle.Tuning(profile.woundScale() * 0.35F,
                            Math.min(30, profile.particleLifetime()), profile.woundScale(), profile.woundLifetime(),
                            profile.remainsScale(), profile.remainsLifetime(), water));
                    level.addParticle(ModGoreParticles.BLOOD_MIST.get(), point.x, point.y, point.z,
                            0.0D, water ? 0.0D : -0.015D, 0.0D);
                }
                trail.previous = current;
            }
        }
    }

    private static final class BloodTrail {
        private final Entity target;
        private Vec3 previous;
        private final GoreImpactProfile profile;
        private final long endsAt;

        private BloodTrail(Entity target, Vec3 previous, GoreImpactProfile profile, long endsAt) {
            this.target = target;
            this.previous = previous;
            this.profile = profile;
            this.endsAt = endsAt;
        }
    }

    private static Vec3 fromVector(Vector3f vector) {
        return new Vec3(vector.x(), vector.y(), vector.z());
    }
}
