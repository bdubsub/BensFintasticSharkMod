package tfar.bensfintasticsharks.init;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import tfar.bensfintasticsharks.item.BfsSpawnEggItem;
import tfar.bensfintasticsharks.item.HiddenItem;
import tfar.bensfintasticsharks.item.PrismarineArmorMaterial;
import tfar.bensfintasticsharks.item.SharkCodexItem;
import tfar.bensfintasticsharks.item.SharkTridentItem;
import tfar.bensfintasticsharks.platform.Services;

public class ModItems {

    static final int CLEAR = 0x00ffffff;

    public static final Item GREAT_WHITE_SHARK_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.GREAT_WHITE_SHARK,CLEAR,CLEAR, 7, new Item.Properties());
    public static final Item GREAT_HAMMERHEAD_SHARK_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.GREAT_HAMMERHEAD_SHARK,CLEAR,CLEAR, 6, new Item.Properties());
    public static final Item COMMON_THRESHER_SHARK_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.COMMON_THRESHER_SHARK,CLEAR,CLEAR, 4, new Item.Properties());
    public static final Item HARBOR_SEAL_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.HARBOR_SEAL,CLEAR,CLEAR, 4, new Item.Properties());
    public static final Item COMMON_STINGRAY_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.COMMON_STINGRAY,CLEAR,CLEAR, 4, new Item.Properties());
    public static final Item SHORTFIN_MAKO_SHARK_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.SHORTFIN_MAKO_SHARK,CLEAR,CLEAR, 5, new Item.Properties());

    // Legacy 1.0 new mob spawn eggs — variant counts match each entity's Variant enum.
    public static final Item TIGER_SHARK_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.TIGER_SHARK, 0x5C5043, 0xE8D294, 4, new Item.Properties());
    public static final Item OCEANIC_WHITETIP_SHARK_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.OCEANIC_WHITETIP_SHARK, 0x6B7A8C, 0xE6E6E6, 6, new Item.Properties());
    public static final Item SANDTIGER_SHARK_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.SANDTIGER_SHARK, 0x8E7A5C, 0xD8C098, 3, new Item.Properties());
    public static final Item BLACKTIP_REEF_SHARK_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.BLACKTIP_REEF_SHARK, 0x4A6072, 0x1F1F1F, 4, new Item.Properties());
    public static final Item BOTTLENOSE_DOLPHIN_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.BOTTLENOSE_DOLPHIN, 0x7BB2C8, 0xE0E8EC, 5, new Item.Properties());
    public static final Item ORCA_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.ORCA, 0x121212, 0xF5F5F5, 0, new Item.Properties());
    public static final Item COMMON_OCTOPUS_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.COMMON_OCTOPUS, 0x8C5A3C, 0xC79A6F, 4, new Item.Properties());
    public static final Item CARIBBEAN_REEF_OCTOPUS_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.CARIBBEAN_REEF_OCTOPUS, 0x2E8B8B, 0x4DD2C7, 0, new Item.Properties());
    public static final Item NAUTILUS_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.NAUTILUS, 0xA67451, 0xF0E6D2, 2, new Item.Properties());
    public static final Item GIANT_MORAY_EEL_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.GIANT_MORAY_EEL, 0x4C5C40, 0xC9C49E, 3, new Item.Properties());
    public static final Item GREEN_SEA_TURTLE_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.GREEN_SEA_TURTLE, 0x4C7A3E, 0xD4C68C, 3, new Item.Properties());
    public static final Item AMERICAN_LOBSTER_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.AMERICAN_LOBSTER, 0x3B4F70, 0xC23B2F, 5, new Item.Properties());
    public static final Item BLACK_SEA_NETTLE_JELLYFISH_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.BLACK_SEA_NETTLE_JELLYFISH, 0x2A1A30, 0x6F4F8C, 0, new Item.Properties());
    public static final Item CANNONBALL_JELLYFISH_SPAWN_EGG = new BfsSpawnEggItem(ModEntityTypes.CANNONBALL_JELLYFISH, 0xB48C5C, 0xE6D4A8, 0, new Item.Properties());

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

    public static final Item SHARK_CODEX = new SharkCodexItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
    public static final Item LOST_MANUSCRIPT = new Item(new Item.Properties());
    public static final Item CODEX_PAGE = new Item(new Item.Properties());
    public static final Item CODEX_VOLUME = new Item(new Item.Properties());

    // Legacy 1.0 — Captain Ben's Hat (cosmetic helmet, leather-tier stats, GeckoLib worn rendering).
    public static final Item CAPTAIN_BEN_HAT = Services.PLATFORM.createCaptainBenHat(ArmorMaterials.LEATHER,
            new Item.Properties().rarity(Rarity.RARE));

    // Legacy 1.0 — Lobster meat (raw + cooked).
    public static final FoodProperties RAW_LOBSTER_MEAT = new FoodProperties.Builder().nutrition(2).saturationMod(0.2F).build();
    public static final FoodProperties COOKED_LOBSTER_MEAT = new FoodProperties.Builder().nutrition(6).saturationMod(0.8F).meat().build();

    public static final Item RAW_LOBSTER_CLAW = new Item(new Item.Properties().food(RAW_LOBSTER_MEAT));
    public static final Item COOKED_LOBSTER_CLAW = new Item(new Item.Properties().food(COOKED_LOBSTER_MEAT));
    public static final Item RAW_LOBSTER_TAIL = new Item(new Item.Properties().food(RAW_LOBSTER_MEAT));
    public static final Item COOKED_LOBSTER_TAIL = new Item(new Item.Properties().food(COOKED_LOBSTER_MEAT));

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
