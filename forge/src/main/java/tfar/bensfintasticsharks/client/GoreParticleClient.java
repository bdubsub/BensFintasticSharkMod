package tfar.bensfintasticsharks.client;

import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import tfar.bensfintasticsharks.gore.GoreParticle;
import tfar.bensfintasticsharks.gore.ModGoreParticles;

public final class GoreParticleClient {
    private GoreParticleClient() {
    }

    public static void registerProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModGoreParticles.BLOOD_SPLATTER.get(),
                sprites -> new GoreParticle.Provider(sprites, GoreParticle.Role.PARTICLE, 0.03F));
        event.registerSpriteSet(ModGoreParticles.BLOOD_SPLATTER_DARK.get(),
                sprites -> new GoreParticle.Provider(sprites, GoreParticle.Role.PARTICLE, 0.03F));
        event.registerSpriteSet(ModGoreParticles.BLOOD_MIST.get(),
                sprites -> new GoreParticle.Provider(sprites, GoreParticle.Role.MIST, -0.002F));
        event.registerSpriteSet(ModGoreParticles.BLOOD_WOUND.get(),
                sprites -> new GoreParticle.Provider(sprites, GoreParticle.Role.WOUND, 0.0F));
        event.registerSpriteSet(ModGoreParticles.BLOOD_DROP.get(),
                sprites -> new GoreParticle.Provider(sprites, GoreParticle.Role.DROPLET, 0.08F));
        event.registerSpriteSet(ModGoreParticles.BLOOD_REMAINS.get(),
                sprites -> new GoreParticle.Provider(sprites, GoreParticle.Role.REMAINS, 0.0F));
    }
}
