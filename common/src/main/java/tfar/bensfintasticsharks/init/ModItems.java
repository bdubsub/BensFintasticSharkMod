package tfar.bensfintasticsharks.init;

import net.minecraft.world.item.*;
import tfar.bensfintasticsharks.item.HiddenItem;
import tfar.bensfintasticsharks.item.PrismarineArmorMaterial;
import tfar.bensfintasticsharks.item.SharkTridentItem;
import tfar.bensfintasticsharks.platform.Services;

public class ModItems {

    static final int CLEAR = 0x00ffffff;

    public static final Item GREAT_WHITE_SHARK_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.GREAT_WHITE_SHARK,CLEAR,CLEAR,new Item.Properties());
    public static final Item GREAT_HAMMERHEAD_SHARK_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.GREAT_HAMMERHEAD_SHARK,CLEAR,CLEAR,new Item.Properties());
    public static final Item COMMON_THRESHER_SHARK_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.COMMON_THRESHER_SHARK,CLEAR,CLEAR,new Item.Properties());
    public static final Item HARBOR_SEAL_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.HARBOR_SEAL,CLEAR,CLEAR,new Item.Properties());
    public static final Item COMMON_STINGRAY_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.COMMON_STINGRAY,CLEAR,CLEAR,new Item.Properties());
    public static final Item SHORTFIN_MAKO_SHARK_SPAWN_EGG = new SpawnEggItem(ModEntityTypes.SHORTFIN_MAKO_SHARK,CLEAR,CLEAR,new Item.Properties());

    public static final Item GREAT_WHITE_SHARK_SKIN = new Item(new Item.Properties());
    public static final Item GREAT_HAMMERHEAD_SHARK_SKIN = new Item(new Item.Properties());
    public static final Item COMMON_THRESHER_SHARK_SKIN = new Item(new Item.Properties());

    public static final Item GREAT_WHITE_SHARK_TOOTH = new Item(new Item.Properties());
    public static final Item GREAT_HAMMERHEAD_SHARK_TOOTH = new Item(new Item.Properties());
    public static final Item COMMON_THRESHER_SHARK_TOOTH = new Item(new Item.Properties());

    public static final Item CARTILAGE = new Item(new Item.Properties());

    public static final Item SHARK_SWORD = new SwordItem(ModTiers.SHARK, 3, -2.4F, new Item.Properties());
    public static final Item SHARK_SHOVEL =  new ShovelItem(ModTiers.SHARK, 1.5F, -3.0F, new Item.Properties());
    public static final Item SHARK_PICKAXE = new PickaxeItem(ModTiers.SHARK, 1, -2.8F, new Item.Properties());
    public static final Item SHARK_AXE = new AxeItem(ModTiers.SHARK, 6.0F, -3.1F, new Item.Properties());
    public static final Item SHARK_HOE =  new HoeItem(ModTiers.SHARK, -2, -1.0F, new Item.Properties());

    public static final Item PRISMARINE_HELMET = Services.PLATFORM.createPrismarineArmor(PrismarineArmorMaterial.PRISMARINE,ArmorItem.Type.HELMET,new Item.Properties());
    public static final Item PRISMARINE_CHESTPLATE = Services.PLATFORM.createPrismarineArmor(PrismarineArmorMaterial.PRISMARINE,ArmorItem.Type.CHESTPLATE,new Item.Properties());
    public static final Item PRISMARINE_LEGGINGS = Services.PLATFORM.createPrismarineArmor(PrismarineArmorMaterial.PRISMARINE,ArmorItem.Type.LEGGINGS,new Item.Properties());
    public static final Item PRISMARINE_BOOTS = Services.PLATFORM.createPrismarineArmor(PrismarineArmorMaterial.PRISMARINE,ArmorItem.Type.BOOTS,new Item.Properties());

    public static final Item SHARK_TRIDENT = new SharkTridentItem(new Item.Properties().durability(500));

    public static final Item SHARK_CODEX = new Item(new Item.Properties());
    public static final Item LOST_MANUSCRIPT = new Item(new Item.Properties());
    public static final Item CODEX_PAGE = new Item(new Item.Properties());
    public static final Item CODEX_VOLUME = new Item(new Item.Properties());

    //these are only used for advancements
    public static final Item GREAT_WHITE_SHARK_PIXEL_ART = new HiddenItem(new Item.Properties());
    public static final Item GREAT_HAMMERHEAD_SHARK_PIXEL_ART = new HiddenItem(new Item.Properties());
    public static final Item COMMON_THRESHER_SHARK_PIXEL_ART = new HiddenItem(new Item.Properties());
    public static final Item SHARKS_GALORE = new HiddenItem(new Item.Properties());
    public static final Item HARBOR_SEAL_BLOCK = new HiddenItem(new Item.Properties());
    public static final Item STINGRAY_PIXEL_ART = new HiddenItem(new Item.Properties());
    public static final Item ILLEGAL_POACHING = new HiddenItem(new Item.Properties());
    public static final Item ALBINO = new HiddenItem(new Item.Properties());
    public static final Item ZIPPY_PIXEL_ART = new HiddenItem(new Item.Properties());
    public static final Item JUSTICE_FOR_STEVE = new HiddenItem(new Item.Properties());
    public static final Item UNETHICAL = new HiddenItem(new Item.Properties());
    public static final Item SLEEPING_WITH_THE_FISHES = new HiddenItem(new Item.Properties());
    public static final Item MOMMY_SHARK = new HiddenItem(new Item.Properties());
    public static final Item SPECIMEN_8 = new HiddenItem(new Item.Properties());
}
