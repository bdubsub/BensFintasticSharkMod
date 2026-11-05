package tfar.bensfintasticsharks.gore;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tfar.bensfintasticsharks.BensFintasticSharks;

public final class ModGoreParticles {
    private static final DeferredRegister<net.minecraft.core.particles.ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BensFintasticSharks.MOD_ID);

    public static final RegistryObject<SimpleParticleType> BLOOD_SPLATTER = register("blood_splatter");
    public static final RegistryObject<SimpleParticleType> BLOOD_SPLATTER_DARK = register("blood_splatter_dark");
    public static final RegistryObject<SimpleParticleType> BLOOD_MIST = register("blood_mist");
    public static final RegistryObject<SimpleParticleType> BLOOD_WOUND = register("blood_wound");
    public static final RegistryObject<SimpleParticleType> BLOOD_DROP = register("blood_drop");
    public static final RegistryObject<SimpleParticleType> BLOOD_REMAINS = register("blood_remains");

    private ModGoreParticles() {
    }

    private static RegistryObject<SimpleParticleType> register(String name) {
        return PARTICLES.register(name, () -> new SimpleParticleType(false));
    }

    public static void register(IEventBus modBus) {
        PARTICLES.register(modBus);
    }

}
