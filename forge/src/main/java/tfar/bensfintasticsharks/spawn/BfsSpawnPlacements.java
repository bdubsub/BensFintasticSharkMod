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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.entity.AbstractSharkEntity;
import tfar.bensfintasticsharks.init.ModEntityTypes;
import tfar.bensfintasticsharks.init.ModTags;

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
        registerWater(event, ModEntityTypes.GREEN_SEA_TURTLE);
        registerWater(event, ModEntityTypes.ATLANTIC_COD);
        registerWater(event, ModEntityTypes.ATLANTIC_SALMON);

        // Giant Moray: soft bias toward shipwrecks — always allowed on/at a wreck, rarer in
        // open water, so morays cluster around shipwrecks without vanishing elsewhere.
        event.register((EntityType) ModEntityTypes.GIANT_MORAY_EEL, SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.OCEAN_FLOOR,
                BfsSpawnPlacements::checkMorayShipwreckBias,
                SpawnPlacementRegisterEvent.Operation.OR);
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
        // 0.19 — Ben: "whenever I go near a beach my chat fills with encounter advancements".
        // Two shore guards: (1) predators need real water under them — at least 5 blocks —
        // so they can't spawn in waist-deep shore shallows just past the beach line;
        for (int i = 1; i <= 5; i++) {
            if (!level.getFluidState(pos.below(i)).is(net.minecraft.tags.FluidTags.WATER)) return false;
        }
        // (2) beach exclusion widened 8 -> 24 blocks (sampled every 8). The old 8-block ring
        // left sharks spawning 9 blocks offshore — inside the 16-block encounter radius of
        // anyone walking the sand.
        for (int dx = -24; dx <= 24; dx += 8) {
            for (int dz = -24; dz <= 24; dz += 8) {
                Holder<Biome> biome = level.getBiome(pos.offset(dx, 0, dz));
                if (biome.is(Biomes.BEACH) || biome.is(Biomes.SNOWY_BEACH)) return false;
            }
        }
        // Explicit mixed-species density gate. The custom MobCategory cap is applied via
        // reflection and can be overridden by an old config, so it was possible to see a
        // great white, tiger, hammerhead and thresher all fill separate nearby attempts.
        // Keep one local species at a time: non-blacktips may form a rare pair; blacktips
        // retain their real school spawn, but neither kind stacks beside another species.
        if ((reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION)
                && type.is(ModTags.EntityTypes.SHARKS)) {
            var cfg = BfsConfig.COMMON;
            int radius = cfg.sharkSpacingRadius.get();
            AABB area = new AABB(pos).inflate(radius, radius / 2.0, radius);
            java.util.List<AbstractSharkEntity> nearby = level.getLevel().getEntitiesOfClass(
                    AbstractSharkEntity.class, area, shark -> shark.isAlive() && !shark.isRemoved());
            boolean spawningBlacktip = type == ModEntityTypes.BLACKTIP_REEF_SHARK;
            int localCap = spawningBlacktip
                    ? cfg.nearbyBlacktipSharkCap.get()
                    : cfg.nearbyNonBlacktipSharkCap.get();
            if (nearby.size() >= localCap) return false;
            for (AbstractSharkEntity<?> shark : nearby) {
                boolean nearbyBlacktip = shark.getType() == ModEntityTypes.BLACKTIP_REEF_SHARK;
                if (spawningBlacktip != nearbyBlacktip) return false;
                if (!spawningBlacktip && shark.getType() != type) return false;
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
     *
     * <p>0.18 — Ben: more nautiluses when players go past negative Y. Below y=0 the
     * night-only gate is waived (it's pitch black in flooded caves regardless of the
     * surface clock), so deep aquifer attempts succeed 5× as often as daytime ocean
     * ones. Cave-biome spawn entries live in ModDataPackProvider (nautilus_cave_spawns).</p>
     */
    public static boolean checkNautilusSpawn(EntityType<? extends WaterAnimal> type,
                                             ServerLevelAccessor level,
                                             MobSpawnType reason,
                                             BlockPos pos,
                                             RandomSource random) {
        if (!checkSeafloorSpawn(type, level, reason, pos, random)) return false;
        if (pos.getY() > level.getSeaLevel() - 20) return false;
        if (pos.getY() < 0) return true; // deep flooded caves: always fair game
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION) {
            boolean isNight = !level.getLevel().isDay();
            if (!isNight && random.nextFloat() < 0.8f) return false;
        }
        return true;
    }

    /**
     * Giant Moray spawn predicate: basic water rule, then a soft shipwreck bias. Natural spawns
     * right on a shipwreck piece always pass; elsewhere only ~35% pass, so morays concentrate
     * near wrecks. Spawn eggs / spawners bypass the bias.
     */
    public static boolean checkMorayShipwreckBias(EntityType<? extends WaterAnimal> type,
                                                  ServerLevelAccessor level,
                                                  MobSpawnType reason,
                                                  BlockPos pos,
                                                  RandomSource random) {
        if (!checkSimpleWaterSpawn(type, level, reason, pos, random)) return false;
        if (reason != MobSpawnType.NATURAL && reason != MobSpawnType.CHUNK_GENERATION) return true;
        net.minecraft.server.level.ServerLevel sl = level.getLevel();
        boolean onWreck = sl.structureManager()
                .getStructureWithPieceAt(pos, net.minecraft.world.level.levelgen.structure.BuiltinStructures.SHIPWRECK).isValid()
                || sl.structureManager()
                .getStructureWithPieceAt(pos, net.minecraft.world.level.levelgen.structure.BuiltinStructures.SHIPWRECK_BEACHED).isValid();
        return onWreck || random.nextFloat() < 0.35f;
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
