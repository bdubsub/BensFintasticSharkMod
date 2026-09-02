package tfar.bensfintasticsharks.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tfar.bensfintasticsharks.entity.*;
import tfar.bensfintasticsharks.platform.Services;

public class ModEntityTypes {

    // Existing 1.0 mobs
    public static final EntityType<? extends GreatWhiteSharkEntity> GREAT_WHITE_SHARK = Services.PLATFORM.registerGreatWhite();
    public static final EntityType<? extends GreatHammerheadSharkEntity> GREAT_HAMMERHEAD_SHARK = Services.PLATFORM.registerGreatHammerheadShark();
    public static final EntityType<? extends CommonThresherSharkEntity> COMMON_THRESHER_SHARK = Services.PLATFORM.registerThresherShark();
    public static final EntityType<? extends HarborSealEntity> HARBOR_SEAL = Services.PLATFORM.registerHarborSeal();
    public static final EntityType<? extends CommonStingrayEntity> COMMON_STINGRAY = Services.PLATFORM.registerStingray();
    public static final EntityType<? extends ShortfinMakoSharkEntity> SHORTFIN_MAKO_SHARK = Services.PLATFORM.registerShortfinMakoShark();

    // Legacy 1.0 — new sharks
    public static final EntityType<? extends TigerSharkEntity> TIGER_SHARK = Services.PLATFORM.registerTigerShark();
    public static final EntityType<? extends OceanicWhitetipSharkEntity> OCEANIC_WHITETIP_SHARK = Services.PLATFORM.registerOceanicWhitetipShark();
    public static final EntityType<? extends SandtigerSharkEntity> SANDTIGER_SHARK = Services.PLATFORM.registerSandtigerShark();
    public static final EntityType<? extends BlacktipReefSharkEntity> BLACKTIP_REEF_SHARK = Services.PLATFORM.registerBlacktipReefShark();

    // Legacy 1.0 — marine mammals
    public static final EntityType<? extends BottlenoseDolphinEntity> BOTTLENOSE_DOLPHIN = Services.PLATFORM.registerBottlenoseDolphin();
    public static final EntityType<? extends OrcaEntity> ORCA = Services.PLATFORM.registerOrca();

    // Legacy 1.0 — cephalopods
    public static final EntityType<? extends CommonOctopusEntity> COMMON_OCTOPUS = Services.PLATFORM.registerCommonOctopus();
    public static final EntityType<? extends CaribbeanReefOctopusEntity> CARIBBEAN_REEF_OCTOPUS = Services.PLATFORM.registerCaribbeanReefOctopus();
    public static final EntityType<? extends NautilusEntity> NAUTILUS = Services.PLATFORM.registerNautilus();

    // Legacy 1.0 — other fauna
    public static final EntityType<? extends GiantMorayEelEntity> GIANT_MORAY_EEL = Services.PLATFORM.registerGiantMorayEel();
    public static final EntityType<? extends GreenSeaTurtleEntity> GREEN_SEA_TURTLE = Services.PLATFORM.registerGreenSeaTurtle();
    public static final EntityType<? extends AmericanLobsterEntity> AMERICAN_LOBSTER = Services.PLATFORM.registerAmericanLobster();

    // Legacy 1.0 — jellyfish
    public static final EntityType<? extends BlackSeaNettleJellyfishEntity> BLACK_SEA_NETTLE_JELLYFISH = Services.PLATFORM.registerBlackSeaNettleJellyfish();
    public static final EntityType<? extends CannonballJellyfishEntity> CANNONBALL_JELLYFISH = Services.PLATFORM.registerCannonballJellyfish();

    public static final EntityType<? extends AtlanticCodEntity> ATLANTIC_COD = Services.PLATFORM.registerAtlanticCod();
    public static final EntityType<? extends AtlanticSalmonEntity> ATLANTIC_SALMON = Services.PLATFORM.registerAtlanticSalmon();

    // build("") matches the pattern used in ForgePlatformHelper — see the note there.
    public static final EntityType<ThrownSharkTridentEntity> SHARK_TRIDENT = EntityType.Builder.<ThrownSharkTridentEntity>of(ThrownSharkTridentEntity::new, MobCategory.MISC).build("");
}
