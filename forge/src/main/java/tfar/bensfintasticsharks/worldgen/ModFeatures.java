package tfar.bensfintasticsharks.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import tfar.bensfintasticsharks.BensFintasticSharks;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, BensFintasticSharks.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SUNKEN_TROVE =
            FEATURES.register("sunken_trove", () -> new SunkenTroveFeature(NoneFeatureConfiguration.CODEC));

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
