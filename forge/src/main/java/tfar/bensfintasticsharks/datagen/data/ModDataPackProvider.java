package tfar.bensfintasticsharks.datagen.data;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.BiomeModifiers;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModTags;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
public class ModDataPackProvider extends DatapackBuiltinEntriesProvider {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModDataPackProvider::biomeModifiers);


    public ModDataPackProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(BensFintasticSharks.MOD_ID));
    }

    private static MobSpawnSettings.SpawnerData data(EntityType<?> type, int weight, int min, int max) {
        return new MobSpawnSettings.SpawnerData(type, weight, min, max);
    }

    private static void registerSpawn(BootstapContext<BiomeModifier> ctx, java.util.function.Function<TagKey<Biome>, HolderSet.Named<Biome>> lookup,
                                       net.minecraft.resources.ResourceKey<BiomeModifier> key,
                                       TagKey<Biome> biomeTag,
                                       List<MobSpawnSettings.SpawnerData> spawners) {
        ctx.register(key, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(lookup.apply(biomeTag), spawners));
    }

    public static void biomeModifiers(BootstapContext<BiomeModifier> context) {
        var biomes = context.lookup(Registries.BIOME);

        // Existing modifiers preserved
        context.register(BiomeModifiers.GREAT_WHITE_SHARK_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.GREAT_WHITE_SHARK_SPAWNS),
                        List.of(data(ModEntityTypes.GREAT_WHITE_SHARK, 50, 1, 1))));
        context.register(BiomeModifiers.GREAT_HAMMERHEAD_SHARK_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.GREAT_HAMMERHEAD_SHARK_SPAWNS),
                        List.of(data(ModEntityTypes.GREAT_HAMMERHEAD_SHARK, 50, 1, 1))));
        context.register(BiomeModifiers.COMMON_THRESHER_SHARK_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.COMMON_THRESHER_SHARK_SPAWNS),
                        List.of(data(ModEntityTypes.COMMON_THRESHER_SHARK, 50, 1, 1))));
        context.register(BiomeModifiers.COMMON_STINGRAY_SHARK_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.COMMON_STINGRAY_SPAWNS),
                        List.of(data(ModEntityTypes.COMMON_STINGRAY, 50, 1, 3))));
        context.register(BiomeModifiers.HARBOR_SEAL_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.HARBOR_SEAL_SPAWNS),
                        List.of(data(ModEntityTypes.HARBOR_SEAL, 50, 1, 3))));
        context.register(BiomeModifiers.SHORTFIN_MAKO_SHARK_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.SHORTFIN_MAKO_SHARK_SPAWNS),
                        List.of(data(ModEntityTypes.SHORTFIN_MAKO_SHARK, 50, 1, 1))));

        // Legacy 1.0 — new sharks. Weights halved from the original tuning to keep the
        // ocean from feeling crowded by default; server owners can raise via config.
        context.register(BiomeModifiers.TIGER_SHARK_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.TIGER_SHARK_SPAWNS),
                        List.of(data(ModEntityTypes.TIGER_SHARK, 25, 1, 1))));
        context.register(BiomeModifiers.OCEANIC_WHITETIP_SHARK_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.OCEANIC_WHITETIP_SHARK_SPAWNS),
                        List.of(data(ModEntityTypes.OCEANIC_WHITETIP_SHARK, 15, 1, 1))));
        context.register(BiomeModifiers.SANDTIGER_SHARK_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.SANDTIGER_SHARK_SPAWNS),
                        List.of(data(ModEntityTypes.SANDTIGER_SHARK, 25, 1, 1))));
        context.register(BiomeModifiers.BLACKTIP_REEF_SHARK_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.BLACKTIP_REEF_SHARK_SPAWNS),
                        List.of(data(ModEntityTypes.BLACKTIP_REEF_SHARK, 30, 2, 3))));

        // Legacy 1.0 — marine mammals
        context.register(BiomeModifiers.BOTTLENOSE_DOLPHIN_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.BOTTLENOSE_DOLPHIN_SPAWNS),
                        List.of(data(ModEntityTypes.BOTTLENOSE_DOLPHIN, 40, 1, 2))));
        context.register(BiomeModifiers.ORCA_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.ORCA_SPAWNS),
                        List.of(data(ModEntityTypes.ORCA, 10, 1, 1))));

        // Legacy 1.0 — cephalopods
        context.register(BiomeModifiers.COMMON_OCTOPUS_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.COMMON_OCTOPUS_SPAWNS),
                        List.of(data(ModEntityTypes.COMMON_OCTOPUS, 30, 1, 1))));
        context.register(BiomeModifiers.CARIBBEAN_REEF_OCTOPUS_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.CARIBBEAN_REEF_OCTOPUS_SPAWNS),
                        List.of(data(ModEntityTypes.CARIBBEAN_REEF_OCTOPUS, 20, 1, 1))));
        context.register(BiomeModifiers.NAUTILUS_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.NAUTILUS_SPAWNS),
                        List.of(data(ModEntityTypes.NAUTILUS, 10, 1, 1))));

        // Legacy 1.0 — other fauna.
        context.register(BiomeModifiers.GIANT_MORAY_EEL_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.GIANT_MORAY_EEL_SPAWNS),
                        List.of(data(ModEntityTypes.GIANT_MORAY_EEL, 40, 1, 2))));
        context.register(BiomeModifiers.GREEN_SEA_TURTLE_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.GREEN_SEA_TURTLE_SPAWNS),
                        List.of(data(ModEntityTypes.GREEN_SEA_TURTLE, 25, 1, 2))));
        context.register(BiomeModifiers.AMERICAN_LOBSTER_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.AMERICAN_LOBSTER_SPAWNS),
                        List.of(data(ModEntityTypes.AMERICAN_LOBSTER, 100, 1, 3))));

        // Legacy 1.0 — jellyfish. Spawn weight kept very low so they're rare visitors.
        context.register(BiomeModifiers.BLACK_SEA_NETTLE_JELLYFISH_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.BLACK_SEA_NETTLE_JELLYFISH_SPAWNS),
                        List.of(data(ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH, 3, 1, 1))));
        context.register(BiomeModifiers.CANNONBALL_JELLYFISH_SPAWNS,
                new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes.getOrThrow(ModTags.Biomes.CANNONBALL_JELLYFISH_SPAWNS),
                        List.of(data(ModEntityTypes.CANNONBALL_JELLYFISH, 4, 1, 1))));
    }
}
