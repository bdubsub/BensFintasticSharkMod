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

    EntityType<GreatHammerheadSharkEntity> registerGreatHammerheadShark();
    EntityType<GreatWhiteSharkEntity> registerGreatWhite();
    EntityType<HarborSealEntity> registerHarborSeal();
    EntityType<CommonStingrayEntity> registerStingray();
    EntityType<CommonThresherSharkEntity> registerThresherShark();
    EntityType<ShortfinMakoSharkEntity> registerShortfinMakoShark();

    ArmorItem createPrismarineArmor(ArmorMaterial prismarine, ArmorItem.Type helmet, Item.Properties properties);

}