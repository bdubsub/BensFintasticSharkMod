package tfar.bensfintasticsharks.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import tfar.bensfintasticsharks.BensFintasticSharks;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, BensFintasticSharks.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SUNKEN_TROVE =
            FEATURES.register("sunken_trove", () -> new SunkenTroveFeature(NoneFeatureConfiguration.CODEC));

    public static final ResourceKey<Feature<?>> ALGAE_BLOCK_FEATURE_KEY =
            ResourceKey.create(Registries.FEATURE, BensFintasticSharks.id("algae_block"));
    public static final ResourceKey<Feature<?>> LARGE_GREEN_ALGAE_FEATURE_KEY =
            ResourceKey.create(Registries.FEATURE, BensFintasticSharks.id("large_green_algae"));
    public static final ResourceKey<Feature<?>> LARGE_RED_ALGAE_FEATURE_KEY =
            ResourceKey.create(Registries.FEATURE, BensFintasticSharks.id("large_red_algae"));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ALGAE_BLOCK =
            FEATURES.register("algae_block", () -> new AlgaePatchFeature(NoneFeatureConfiguration.CODEC,
                    tfar.bensfintasticsharks.init.ModBlocks.ALGAE_BLOCK));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> LARGE_GREEN_ALGAE =
            FEATURES.register("large_green_algae", () -> new AlgaePatchFeature(NoneFeatureConfiguration.CODEC,
                    tfar.bensfintasticsharks.init.ModBlocks.LARGE_GREEN_ALGAE));
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> LARGE_RED_ALGAE =
            FEATURES.register("large_red_algae", () -> new AlgaePatchFeature(NoneFeatureConfiguration.CODEC,
                    tfar.bensfintasticsharks.init.ModBlocks.LARGE_RED_ALGAE));

    public static final ResourceKey<ConfiguredFeature<?, ?>> ALGAE_BLOCK_CONFIGURED =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, BensFintasticSharks.id("algae_block"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> LARGE_GREEN_ALGAE_CONFIGURED =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, BensFintasticSharks.id("large_green_algae"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> LARGE_RED_ALGAE_CONFIGURED =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, BensFintasticSharks.id("large_red_algae"));

    public static final ResourceKey<PlacedFeature> ALGAE_BLOCK_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, BensFintasticSharks.id("algae_block"));
    public static final ResourceKey<PlacedFeature> LARGE_GREEN_ALGAE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, BensFintasticSharks.id("large_green_algae"));
    public static final ResourceKey<PlacedFeature> LARGE_RED_ALGAE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, BensFintasticSharks.id("large_red_algae"));

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
