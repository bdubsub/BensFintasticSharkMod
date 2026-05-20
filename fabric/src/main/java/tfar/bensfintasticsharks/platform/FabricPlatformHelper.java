package tfar.bensfintasticsharks.platform;

import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import tfar.bensfintasticsharks.entity.*;
import tfar.bensfintasticsharks.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <F> void registerAll(Class<?> clazz, Registry<? extends F> registry, Class<F> filter) {
    }

    @Override
    public EntityType<GreatHammerheadSharkEntity> registerGreatHammerheadShark() {
        return null;
    }

    @Override
    public EntityType<GreatWhiteSharkEntity> registerGreatWhite() {
        return null;
    }

    @Override
    public EntityType<HarborSealEntity> registerHarborSeal() {
        return null;
    }

    @Override
    public EntityType<CommonStingrayEntity> registerStingray() {
        return null;
    }

    @Override
    public EntityType<CommonThresherSharkEntity> registerThresherShark() {
        return null;
    }

    @Override
    public EntityType<ShortfinMakoSharkEntity> registerShortfinMakoShark() {
        return null;
    }

    @Override
    public EntityType<TigerSharkEntity> registerTigerShark() {
        return null;
    }

    @Override
    public EntityType<OceanicWhitetipSharkEntity> registerOceanicWhitetipShark() {
        return null;
    }

    @Override
    public EntityType<SandtigerSharkEntity> registerSandtigerShark() {
        return null;
    }

    @Override
    public EntityType<BlacktipReefSharkEntity> registerBlacktipReefShark() {
        return null;
    }

    @Override
    public EntityType<BottlenoseDolphinEntity> registerBottlenoseDolphin() {
        return null;
    }

    @Override
    public EntityType<OrcaEntity> registerOrca() {
        return null;
    }

    @Override
    public EntityType<CommonOctopusEntity> registerCommonOctopus() {
        return null;
    }

    @Override
    public EntityType<CaribbeanReefOctopusEntity> registerCaribbeanReefOctopus() {
        return null;
    }

    @Override
    public EntityType<NautilusEntity> registerNautilus() {
        return null;
    }

    @Override
    public EntityType<GiantMorayEelEntity> registerGiantMorayEel() {
        return null;
    }

    @Override
    public EntityType<GreenSeaTurtleEntity> registerGreenSeaTurtle() {
        return null;
    }

    @Override
    public EntityType<AmericanLobsterEntity> registerAmericanLobster() {
        return null;
    }

    @Override
    public EntityType<BlackSeaNettleJellyfishEntity> registerBlackSeaNettleJellyfish() {
        return null;
    }

    @Override
    public EntityType<CannonballJellyfishEntity> registerCannonballJellyfish() {
        return null;
    }

    @Override
    public ArmorItem createPrismarineArmor(ArmorMaterial prismarine, ArmorItem.Type helmet, Item.Properties properties) {
        return null;
    }

    @Override
    public ArmorItem createCaptainBenHat(ArmorMaterial material, Item.Properties properties) {
        return null;
    }
}
