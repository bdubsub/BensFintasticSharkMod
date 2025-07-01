package tfar.bensfintasticsharks.init;

import com.google.common.collect.HashBiMap;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.EntityVariantPredicate;
import net.minecraft.world.entity.animal.Parrot;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.*;

import java.util.Optional;

public class EntityVariantPredicates {

    public static final EntityVariantPredicate<GreatWhiteSharkEntity.Variant> GREAT_WHITE_SHARK = EntityVariantPredicate.create(GreatWhiteSharkEntity.Variant.CODEC, entity -> entity instanceof GreatWhiteSharkEntity shark ? Optional.of(shark.getVariant()) : Optional.empty());
    public static final EntityVariantPredicate<GreatHammerheadSharkEntity.Variant> GREAT_HAMMERHEAD_SHARK = EntityVariantPredicate.create(GreatHammerheadSharkEntity.Variant.CODEC, entity -> entity instanceof GreatHammerheadSharkEntity shark ? Optional.of(shark.getVariant()) : Optional.empty());
    public static final EntityVariantPredicate<CommonThresherSharkEntity.Variant> COMMON_THRESHER_SHARK = EntityVariantPredicate.create(CommonThresherSharkEntity.Variant.CODEC, entity -> entity instanceof CommonThresherSharkEntity greatWhiteShark ? Optional.of(greatWhiteShark.getVariant()) : Optional.empty());
    public static final EntityVariantPredicate<HarborSealEntity.Variant> HARBOR_SEAL = EntityVariantPredicate.create(HarborSealEntity.Variant.CODEC, entity -> entity instanceof HarborSealEntity greatWhiteShark ? Optional.of(greatWhiteShark.getVariant()) : Optional.empty());
    public static final EntityVariantPredicate<CommonStingrayEntity.Variant> COMMON_STINGRAY = EntityVariantPredicate.create(CommonStingrayEntity.Variant.CODEC, entity -> entity instanceof CommonStingrayEntity greatWhiteShark ? Optional.of(greatWhiteShark.getVariant()) : Optional.empty());
    public static final EntityVariantPredicate<ShortfinMakoSharkEntity.Variant> SHORTFIN_MAKO_SHARK = EntityVariantPredicate.create(ShortfinMakoSharkEntity.Variant.CODEC, entity -> entity instanceof ShortfinMakoSharkEntity greatWhiteShark ? Optional.of(greatWhiteShark.getVariant()) : Optional.empty());

    static {
        EntitySubPredicate.Types.TYPES = HashBiMap.create(EntitySubPredicate.Types.TYPES);
        EntitySubPredicate.Types.TYPES.put(BensFintasticSharks.id("great_white_shark").toString(),GREAT_WHITE_SHARK.type());
        EntitySubPredicate.Types.TYPES.put(BensFintasticSharks.id("great_hammerhead_shark").toString(),GREAT_HAMMERHEAD_SHARK.type());
        EntitySubPredicate.Types.TYPES.put(BensFintasticSharks.id("common_thresher_shark").toString(),COMMON_THRESHER_SHARK.type());
        EntitySubPredicate.Types.TYPES.put(BensFintasticSharks.id("harbor_seal").toString(),HARBOR_SEAL.type());
        EntitySubPredicate.Types.TYPES.put(BensFintasticSharks.id("common_stingray").toString(),COMMON_STINGRAY.type());
        EntitySubPredicate.Types.TYPES.put(BensFintasticSharks.id("shortfin_mako_shark").toString(),SHORTFIN_MAKO_SHARK.type());
    }

    public static void poke(){}

}
