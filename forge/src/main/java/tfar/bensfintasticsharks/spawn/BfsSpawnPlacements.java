package tfar.bensfintasticsharks.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.init.ModEntityTypes;

/**
 * Spawn placement registrations for Legacy 1.0 mobs.
 *
 * Sharks + Orca refuse to spawn within 8 blocks of a beach biome — gives players
 * a safe shore to retreat to. We sample every 4 blocks horizontally instead of
 * every block (289 → 25 lookups per attempt) which is plenty for detection.
 */
public class BfsSpawnPlacements {

    public static void onSpawnPlacementRegister(SpawnPlacementRegisterEvent event) {
        // Apex predators — water + not near beach
        registerPredator(event, ModEntityTypes.GREAT_WHITE_SHARK);
        registerPredator(event, ModEntityTypes.GREAT_HAMMERHEAD_SHARK);
        registerPredator(event, ModEntityTypes.COMMON_THRESHER_SHARK);
        registerPredator(event, ModEntityTypes.SHORTFIN_MAKO_SHARK);
        registerPredator(event, ModEntityTypes.TIGER_SHARK);
        registerPredator(event, ModEntityTypes.OCEANIC_WHITETIP_SHARK);
        registerPredator(event, ModEntityTypes.SANDTIGER_SHARK);
        registerPredator(event, ModEntityTypes.BLACKTIP_REEF_SHARK);
        registerPredator(event, ModEntityTypes.ORCA);

        // Water creatures — straight water rule
        registerWater(event, ModEntityTypes.HARBOR_SEAL);
        registerWater(event, ModEntityTypes.COMMON_STINGRAY);
        registerWater(event, ModEntityTypes.BOTTLENOSE_DOLPHIN);
        registerWater(event, ModEntityTypes.GIANT_MORAY_EEL);
        registerWater(event, ModEntityTypes.GREEN_SEA_TURTLE);
        registerWater(event, ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH);
        registerWater(event, ModEntityTypes.CANNONBALL_JELLYFISH);

        // Seafloor dwellers — must spawn on the bottom, not floating at the surface.
        registerSeafloor(event, ModEntityTypes.COMMON_OCTOPUS);
        registerSeafloor(event, ModEntityTypes.CARIBBEAN_REEF_OCTOPUS);
        registerSeafloor(event, ModEntityTypes.AMERICAN_LOBSTER);

        // Nautilus: deep ravines, mostly at night.
        event.register((EntityType) ModEntityTypes.NAUTILUS, SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.OCEAN_FLOOR,
                BfsSpawnPlacements::checkNautilusSpawn,
                SpawnPlacementRegisterEvent.Operation.OR);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerSeafloor(SpawnPlacementRegisterEvent event, EntityType<?> type) {
        event.register((EntityType) type, SpawnPlacements.Type.IN_WATER, Heightmap.Types.OCEAN_FLOOR,
                BfsSpawnPlacements::checkSeafloorSpawn,
                SpawnPlacementRegisterEvent.Operation.OR);
    }

    /**
     * Seafloor predicate: position must be water, the block below must be solid (so the
     * mob lands on something), and we must be at least a few blocks below sea level so
     * we don't end up at the surface like the vanilla water-animal rule does.
     */
    public static boolean checkSeafloorSpawn(EntityType<? extends WaterAnimal> type,
                                             ServerLevelAccessor level,
                                             MobSpawnType reason,
                                             BlockPos pos,
                                             RandomSource random) {
        if (!level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER)) return false;
        if (pos.getY() > level.getSeaLevel() - 4) return false;
        BlockPos below = pos.below();
        return level.getBlockState(below).isFaceSturdy(level, below, net.minecraft.core.Direction.UP);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerPredator(SpawnPlacementRegisterEvent event, EntityType<?> type) {
        event.register((EntityType) type, SpawnPlacements.Type.IN_WATER, Heightmap.Types.OCEAN_FLOOR,
                BfsSpawnPlacements::checkPredatorSpawn,
                SpawnPlacementRegisterEvent.Operation.OR);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerWater(SpawnPlacementRegisterEvent event, EntityType<?> type) {
        event.register((EntityType) type, SpawnPlacements.Type.IN_WATER, Heightmap.Types.OCEAN_FLOOR,
                BfsSpawnPlacements::checkSimpleWaterSpawn,
                SpawnPlacementRegisterEvent.Operation.OR);
    }

    public static boolean checkPredatorSpawn(EntityType<? extends WaterAnimal> type,
                                              ServerLevelAccessor level,
                                              MobSpawnType reason,
                                              BlockPos pos,
                                              RandomSource random) {
        if (!checkSimpleWaterSpawn(type, level, reason, pos, random)) return false;
        // Beach exclusion (sampled). Check biomes within 8 blocks horizontally every 4 blocks.
        for (int dx = -8; dx <= 8; dx += 4) {
            for (int dz = -8; dz <= 8; dz += 4) {
                Holder<Biome> biome = level.getBiome(pos.offset(dx, 0, dz));
                if (biome.is(Biomes.BEACH) || biome.is(Biomes.SNOWY_BEACH)) return false;
            }
        }
        return true;
    }

    public static boolean checkSimpleWaterSpawn(EntityType<? extends WaterAnimal> type,
                                                 ServerLevelAccessor level,
                                                 MobSpawnType reason,
                                                 BlockPos pos,
                                                 RandomSource random) {
        return WaterAnimal.checkSurfaceWaterAnimalSpawnRules(type, level, reason, pos, random);
    }

    /**
     * Nautilus spawn predicate: must pass the basic seafloor check, must be at least 20
     * blocks below sea level (so it ends up in ravines and deep crevices), and 80% of
     * attempts are night-only. Natural spawns only — spawn eggs bypass via the
     * MobSpawnType check inside the seafloor predicate.
     */
    public static boolean checkNautilusSpawn(EntityType<? extends WaterAnimal> type,
                                             ServerLevelAccessor level,
                                             MobSpawnType reason,
                                             BlockPos pos,
                                             RandomSource random) {
        if (!checkSeafloorSpawn(type, level, reason, pos, random)) return false;
        if (pos.getY() > level.getSeaLevel() - 20) return false;
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION) {
            boolean isNight = !level.getLevel().isDay();
            if (!isNight && random.nextFloat() < 0.8f) return false;
        }
        return true;
    }

    /** Optional helper for sharks themselves to slow down in shallow water, used from travel(). */
    public static float speedScaleForDepth(AbstractSharkEntity<?> shark, LevelReader level) {
        BlockPos pos = shark.blockPosition();
        int depth = 0;
        for (int i = 0; i < 4; i++) {
            if (!level.getBlockState(pos.below(i)).isAir()) break;
            depth++;
        }
        return depth < 3 ? 0.6f : 1.0f;
    }
}
