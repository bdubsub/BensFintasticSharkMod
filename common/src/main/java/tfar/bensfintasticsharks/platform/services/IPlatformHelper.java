package tfar.bensfintasticsharks.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import tfar.bensfintasticsharks.entity.*;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    <F> void registerAll(Class<?> clazz, Registry<? extends F> registry, Class<F> filter);

    /** Platform-specific custom mob category registration, called before any entity types are built. */
    default void initCustomCategories() {}

    EntityType<GreatHammerheadSharkEntity> registerGreatHammerheadShark();
    EntityType<GreatWhiteSharkEntity> registerGreatWhite();
    EntityType<HarborSealEntity> registerHarborSeal();
    EntityType<CommonStingrayEntity> registerStingray();
    EntityType<CommonThresherSharkEntity> registerThresherShark();
    EntityType<ShortfinMakoSharkEntity> registerShortfinMakoShark();

    // New Legacy 1.0 mobs.
    EntityType<TigerSharkEntity> registerTigerShark();
    EntityType<OceanicWhitetipSharkEntity> registerOceanicWhitetipShark();
    EntityType<SandtigerSharkEntity> registerSandtigerShark();
    EntityType<BlacktipReefSharkEntity> registerBlacktipReefShark();
    EntityType<BottlenoseDolphinEntity> registerBottlenoseDolphin();
    EntityType<OrcaEntity> registerOrca();
    EntityType<CommonOctopusEntity> registerCommonOctopus();
    EntityType<CaribbeanReefOctopusEntity> registerCaribbeanReefOctopus();
    EntityType<NautilusEntity> registerNautilus();
    EntityType<GiantMorayEelEntity> registerGiantMorayEel();
    EntityType<GreenSeaTurtleEntity> registerGreenSeaTurtle();
    EntityType<AmericanLobsterEntity> registerAmericanLobster();
    EntityType<BlackSeaNettleJellyfishEntity> registerBlackSeaNettleJellyfish();
    EntityType<CannonballJellyfishEntity> registerCannonballJellyfish();
    EntityType<AtlanticCodEntity> registerAtlanticCod();
    EntityType<AtlanticSalmonEntity> registerAtlanticSalmon();

    ArmorItem createPrismarineArmor(ArmorMaterial prismarine, ArmorItem.Type helmet, Item.Properties properties);

    /** Platform helper for the GeckoLib-rendered captain's hat. */
    ArmorItem createCaptainBenHat(ArmorMaterial material, Item.Properties properties);

}
