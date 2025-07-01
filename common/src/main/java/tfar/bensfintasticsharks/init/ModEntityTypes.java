package tfar.bensfintasticsharks.init;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import tfar.bensfintasticsharks.entity.*;
import tfar.bensfintasticsharks.platform.Services;

public class ModEntityTypes {

    public static final EntityType<? extends GreatWhiteSharkEntity> GREAT_WHITE_SHARK = Services.PLATFORM.registerGreatWhite();
    public static final EntityType<? extends GreatHammerheadSharkEntity> GREAT_HAMMERHEAD_SHARK = Services.PLATFORM.registerGreatHammerheadShark();
    public static final EntityType<? extends CommonThresherSharkEntity> COMMON_THRESHER_SHARK = Services.PLATFORM.registerThresherShark();
    public static final EntityType<? extends HarborSealEntity> HARBOR_SEAL = Services.PLATFORM.registerHarborSeal();
    public static final EntityType<? extends CommonStingrayEntity> COMMON_STINGRAY = Services.PLATFORM.registerStingray();
    public static final EntityType<? extends ShortfinMakoSharkEntity> SHORTFIN_MAKO_SHARK = Services.PLATFORM.registerShortfinMakoShark();

    public static final EntityType<ThrownSharkTridentEntity> SHARK_TRIDENT = EntityType.Builder.<ThrownSharkTridentEntity>of(ThrownSharkTridentEntity::new, MobCategory.MISC).build("");
}
