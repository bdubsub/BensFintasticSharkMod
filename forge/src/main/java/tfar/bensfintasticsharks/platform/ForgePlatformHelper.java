package tfar.bensfintasticsharks.platform;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.commons.lang3.tuple.Pair;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.BensFintasticSharksForge;
import tfar.bensfintasticsharks.entity.*;
import tfar.bensfintasticsharks.init.ModMobCategories;
import tfar.bensfintasticsharks.item.CaptainBenHatItem;
import tfar.bensfintasticsharks.item.PrismarineArmorItem;
import tfar.bensfintasticsharks.platform.services.IPlatformHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public void initCustomCategories() {
        // Touch ModMobCategories so its static fields run MobCategory.create(...) before any
        // EntityType.Builder.of(..., category) call references them.
        ModMobCategories.touch();
    }

    // unfreeze() is load-bearing: the first field.get(null) below triggers static init of the
    // target class (e.g. ModEntityTypes), whose <clinit> calls EntityType.Builder.build()
    // → NamespacedWrapper.createIntrusiveHolder() → validateWrite(). Without an unfreeze
    // beforehand, validateWrite throws "Registry is already frozen" and mod loading dies.
    // Actual registry entries are still added later via RegisterEvent.
    @Override
    public <F> void registerAll(Class<?> clazz, Registry<? extends F> registry, Class<F> filter) {
        List<Pair<ResourceLocation, Supplier<?>>> list = BensFintasticSharksForge.registerLater.computeIfAbsent(registry, k -> new ArrayList<>());
        for (Field field : clazz.getFields()) {
            MappedRegistry<? extends F> forgeRegistry = (MappedRegistry<? extends F>) registry;
            forgeRegistry.unfreeze();
            try {
                Object o = field.get(null);
                if (filter.isInstance(o)) {
                    list.add(Pair.of(new ResourceLocation(BensFintasticSharks.MOD_ID, field.getName().toLowerCase(Locale.ROOT)), () -> o));
                }
            } catch (IllegalAccessException illegalAccessException) {
                illegalAccessException.printStackTrace();
            }
        }
    }

    // build("") here is the known-working original. A real id (e.g.
    // "bensfintasticsharks:great_white_shark") would let DFU register the entity tree and
    // silence "no DFU for X" warnings, but it's only safe if registerAll's unfreeze() above
    // is still in place when these run. Don't change one without re-testing the other.
    @Override
    public EntityType<GreatHammerheadSharkEntity> registerGreatHammerheadShark() {
        return EntityType.Builder.of(GreatHammerheadSharkEntityForge::new, ModMobCategories.APEX_PREDATOR).sized(1.75f, 1.4f).build("");
    }

    @Override
    public EntityType<GreatWhiteSharkEntity> registerGreatWhite() {
        return EntityType.Builder.of(GreatWhiteSharkEntityForge::new, ModMobCategories.APEX_PREDATOR).sized(2.5f, 1.75f).build("");
    }

    @Override
    public EntityType<HarborSealEntity> registerHarborSeal() {
        return EntityType.Builder.of(HarborSealEntityForge::new, ModMobCategories.BFS_WATER_CREATURE).sized(1.5f, .75f).build("");
    }

    @Override
    public EntityType<CommonStingrayEntity> registerStingray() {
        return EntityType.Builder.of(CommonStingrayEntityForge::new, ModMobCategories.BFS_WATER_CREATURE).sized(1, .25f).build("");
    }

    @Override
    public EntityType<CommonThresherSharkEntity> registerThresherShark() {
        return EntityType.Builder.of(CommonThresherSharkEntityForge::new, ModMobCategories.APEX_PREDATOR).sized(1.5f, 1).build("");
    }

    @Override
    public EntityType<ShortfinMakoSharkEntity> registerShortfinMakoShark() {
        return EntityType.Builder.of(ShortfinMakoSharkEntityForge::new, ModMobCategories.APEX_PREDATOR).sized(1.5f, 0.75f).build("");
    }

    @Override
    public EntityType<TigerSharkEntity> registerTigerShark() {
        return EntityType.Builder.of(TigerSharkEntityForge::new, ModMobCategories.APEX_PREDATOR).sized(2.0f, 0.9f).build("");
    }

    @Override
    public EntityType<OceanicWhitetipSharkEntity> registerOceanicWhitetipShark() {
        return EntityType.Builder.of(OceanicWhitetipSharkEntityForge::new, ModMobCategories.APEX_PREDATOR).sized(2.0f, 0.85f).build("");
    }

    @Override
    public EntityType<SandtigerSharkEntity> registerSandtigerShark() {
        return EntityType.Builder.of(SandtigerSharkEntityForge::new, ModMobCategories.APEX_PREDATOR).sized(1.75f, 0.85f).build("");
    }

    @Override
    public EntityType<BlacktipReefSharkEntity> registerBlacktipReefShark() {
        return EntityType.Builder.of(BlacktipReefSharkEntityForge::new, ModMobCategories.APEX_PREDATOR).sized(1.2f, 0.6f).build("");
    }

    @Override
    public EntityType<BottlenoseDolphinEntity> registerBottlenoseDolphin() {
        return EntityType.Builder.of(BottlenoseDolphinEntityForge::new, ModMobCategories.BFS_WATER_CREATURE).sized(1.5f, 0.85f).build("");
    }

    @Override
    public EntityType<OrcaEntity> registerOrca() {
        return EntityType.Builder.of(OrcaEntityForge::new, ModMobCategories.APEX_PREDATOR).sized(3.5f, 1.6f).build("");
    }

    @Override
    public EntityType<CommonOctopusEntity> registerCommonOctopus() {
        return EntityType.Builder.of(CommonOctopusEntityForge::new, ModMobCategories.BFS_WATER_CREATURE).sized(0.6f, 0.4f).build("");
    }

    @Override
    public EntityType<CaribbeanReefOctopusEntity> registerCaribbeanReefOctopus() {
        return EntityType.Builder.of(CaribbeanReefOctopusEntityForge::new, ModMobCategories.BFS_WATER_CREATURE).sized(0.5f, 0.3f).build("");
    }

    @Override
    public EntityType<NautilusEntity> registerNautilus() {
        return EntityType.Builder.of(NautilusEntityForge::new, ModMobCategories.BFS_WATER_CREATURE).sized(0.5f, 0.4f).build("");
    }

    @Override
    public EntityType<GiantMorayEelEntity> registerGiantMorayEel() {
        return EntityType.Builder.of(GiantMorayEelEntityForge::new, ModMobCategories.BFS_WATER_CREATURE).sized(1.6f, 0.5f).build("");
    }

    @Override
    public EntityType<GreenSeaTurtleEntity> registerGreenSeaTurtle() {
        return EntityType.Builder.of(GreenSeaTurtleEntityForge::new, ModMobCategories.BFS_WATER_CREATURE).sized(1.2f, 0.5f).build("");
    }

    @Override
    public EntityType<AmericanLobsterEntity> registerAmericanLobster() {
        return EntityType.Builder.of(AmericanLobsterEntityForge::new, ModMobCategories.BFS_WATER_CREATURE).sized(0.55f, 0.3f).build("");
    }

    @Override
    public EntityType<BlackSeaNettleJellyfishEntity> registerBlackSeaNettleJellyfish() {
        return EntityType.Builder.of(BlackSeaNettleJellyfishEntityForge::new, ModMobCategories.BFS_WATER_AMBIENT).sized(1.0f, 1.2f).build("");
    }

    @Override
    public EntityType<CannonballJellyfishEntity> registerCannonballJellyfish() {
        return EntityType.Builder.of(CannonballJellyfishEntityForge::new, ModMobCategories.BFS_WATER_AMBIENT).sized(0.5f, 0.5f).build("");
    }

    @Override
    public ArmorItem createPrismarineArmor(ArmorMaterial prismarine, ArmorItem.Type helmet, Item.Properties properties) {
        return new PrismarineArmorItem(prismarine, helmet,properties);
    }

    @Override
    public ArmorItem createCaptainBenHat(ArmorMaterial material, Item.Properties properties) {
        return new CaptainBenHatItem(material, ArmorItem.Type.HELMET, properties);
    }
}
