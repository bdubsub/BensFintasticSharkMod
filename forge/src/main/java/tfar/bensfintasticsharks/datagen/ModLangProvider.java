package tfar.bensfintasticsharks.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import org.codehaus.plexus.util.StringUtils;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.TextComponents;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput gen) {
        super(gen, BensFintasticSharks.MOD_ID, "en_us");
    }

    protected final Set<Item> exclude_items = new HashSet<>();
    @Override
    protected void addTranslations() {
        BensFintasticSharks.getKnownItems().filter(item -> item instanceof BlockItem).forEach(exclude_items::add);
        BensFintasticSharks.getKnownItems().toList().stream().filter(item -> !exclude_items.contains(item)).<Supplier<? extends Item>>map(item -> () -> item).forEach(this::addDefaultItem);

        for (EntityType<?> type : ModDatagen.getKnownEntityTypes().toList()) {
            addDefaultEntityType(() -> type);
        }

        addTextComponent(TextComponents.ROOT,"Ben’s Fintastic Sharks!");
        addTextComponent(TextComponents.ROOT_DESC,"Achievement granted when logging in with the mod");

        addTextComponent(TextComponents.GREAT_WHITE_ENCOUNTER,"You’re gonna need a bigger boat…");
        addTextComponent(TextComponents.GREAT_WHITE_ENCOUNTER_DESC,"Encounter a Great White Shark");

        addTextComponent(TextComponents.GREAT_HAMMERHEAD_ENCOUNTER,"Stop! Hammer Time!");
        addTextComponent(TextComponents.GREAT_HAMMERHEAD_ENCOUNTER_DESC,"Encounter a Great Hammerhead Shark");

        addTextComponent(TextComponents.COMMON_THRESHER_ENCOUNTER,"Whiplash!");
        addTextComponent(TextComponents.COMMON_THRESHER_ENCOUNTER_DESC,"Encounter a Common Thresher Shark");

        addTextComponent(TextComponents.SHORTFIN_MAKO_ENCOUNTER,"Speed Demon");
        addTextComponent(TextComponents.SHORTFIN_MAKO_ENCOUNTER_DESC,"Encounter a Shortfin Mako Shark");

        addTextComponent(TextComponents.HARBOR_SEAL_ENCOUNTER,"Awkward.");
        addTextComponent(TextComponents.HARBOR_SEAL_ENCOUNTER_DESC,"Encounter a Harbor Seal");

        addTextComponent(TextComponents.SLEEPING_WITH_THE_FISHES,"Sleeping with the fishes.");
        addTextComponent(TextComponents.SLEEPING_WITH_THE_FISHES_DESC,"Killed by a shark");

        addTextComponent(TextComponents.STINGRAY_ATTACKS_PLAYER,"Crankey!");
        addTextComponent(TextComponents.STINGRAY_ATTACKS_PLAYER_DESC,"Get stung by a Common Stingray");

        addTextComponent(TextComponents.ALBINO_ENCOUNTER,"It's a shiny!");
        addTextComponent(TextComponents.ALBINO_ENCOUNTER_DESC,"Encounter an albino variant");

        addTextComponent(TextComponents.ILLEGAL_POACHING,"Illegal Poaching");
        addTextComponent(TextComponents.ILLEGAL_POACHING_DESC,"Kill a shark");

        addTextComponent(TextComponents.UNETHICAL,"Unethical");
        addTextComponent(TextComponents.UNETHICAL_DESC,"Kill a Harbor Seal");

        addTextComponent(TextComponents.JUSTICE_FOR_STEVE,"Justice for Steve");
        addTextComponent(TextComponents.JUSTICE_FOR_STEVE_DESC,"Kill a Common Stingray");

        addTextComponent(TextComponents.PRISMARINE_ARMOR,"The Sea Dwelling Knight");
        addTextComponent(TextComponents.PRISMARINE_ARMOR_DESC,"Obtain a full set of Prismarine Armor");

        addTextComponent(TextComponents.ZIPPY_ENCOUNTER,"Shark of Zeus");
        addTextComponent(TextComponents.ZIPPY_ENCOUNTER_DESC,"Discover Zippy");

        addTextComponent(TextComponents.SPECIMEN_8_ENCOUNTER,"I’ll be back.");
        addTextComponent(TextComponents.SPECIMEN_8_ENCOUNTER_DESC,"Discover Specimen-8");

        addTextComponent(TextComponents.DEEP_BLUE_ENCOUNTER,"Mommy Shark");
        addTextComponent(TextComponents.DEEP_BLUE_ENCOUNTER_DESC,"Discover Deep Blue");

        addTextComponent(TextComponents.SHARK_CODEX,"Knowledge is power…");
        addTextComponent(TextComponents.SHARK_CODEX_DESC,"Craft a Shark Codex");

        addTextComponent(TextComponents.LOST_MANUSCRIPT,"Lost beneath the waves");
        addTextComponent(TextComponents.LOST_MANUSCRIPT_DESC,"Find a lost manuscript");

        addTextComponent(TextComponents.LEVEL_SHARK_CODEX,"Level Up!");
        addTextComponent(TextComponents.LEVEL_SHARK_CODEX_DESC,"Combine 9 shark Codex Pages with a Shark Codex");

        addTextComponent(TextComponents.SHARKS_GALORE,"Shark Galore!");
        addTextComponent(TextComponents.SHARKS_GALORE_DESC,"Discover every species of sharks");

        addTextComponent(TextComponents.TAB_TITLE,"Ben's Fintastic Sharks");

        // Legacy 1.0 flavor tooltips
        add("item.bensfintasticsharks.captain_ben_hat.flavor", "Once worn by a captain who respected the sea.");
        add("item.bensfintasticsharks.shark_trident.flavor", "Best used for show. Sharks are friends.");

        // Conservation effect
        add("effect.bensfintasticsharks.respect_the_ocean", "Respect the Ocean");
        add("effect.bensfintasticsharks.respect_the_ocean.description", "The ocean is watching…");

        // Legacy 1.0 advancement strings (see BensFintasticSharksAdvancements)
        add("advancements.bensfintasticsharks.marine_curious.title", "Marine Curious");
        add("advancements.bensfintasticsharks.marine_curious.description", "Encounter your first BFS creature.");
        add("advancements.bensfintasticsharks.shark_spotter.title", "Shark Spotter");
        add("advancements.bensfintasticsharks.shark_spotter.description", "Encounter three shark species.");
        add("advancements.bensfintasticsharks.shark_whisperer.title", "Shark Whisperer");
        add("advancements.bensfintasticsharks.shark_whisperer.description", "Encounter all eight shark species.");
        add("advancements.bensfintasticsharks.apex_awareness.title", "Apex Awareness");
        add("advancements.bensfintasticsharks.apex_awareness.description", "Survive a shark attack without killing the shark.");
        add("advancements.bensfintasticsharks.conservationist.title", "Conservationist");
        add("advancements.bensfintasticsharks.conservationist.description", "Earn the Respect the Ocean effect... actually wait, don't. Encounter sharks, let them live.");
        add("advancements.bensfintasticsharks.marine_biologist.title", "Marine Biologist");
        add("advancements.bensfintasticsharks.marine_biologist.description", "Encounter every BFS species.");
        add("advancements.bensfintasticsharks.wrong_place_wrong_time.title", "Wrong Place, Wrong Time");
        add("advancements.bensfintasticsharks.wrong_place_wrong_time.description", "Be attacked by a shark shortly after entering the water.");
        add("advancements.bensfintasticsharks.stung.title", "Stung!");
        add("advancements.bensfintasticsharks.stung.description", "Take damage from a jellyfish.");
        add("advancements.bensfintasticsharks.inked.title", "Inked");
        add("advancements.bensfintasticsharks.inked.description", "Anger an octopus.");
        add("advancements.bensfintasticsharks.dolphin_friend.title", "Dolphin Friend");
        add("advancements.bensfintasticsharks.dolphin_friend.description", "Encounter a Bottlenose Dolphin in the wild.");
        add("advancements.bensfintasticsharks.apex_of_apex.title", "Apex of Apex");
        add("advancements.bensfintasticsharks.apex_of_apex.description", "Encounter an Orca in the wild.");
        add("advancements.bensfintasticsharks.fresh_catch.title", "Fresh Catch");
        add("advancements.bensfintasticsharks.fresh_catch.description", "Cook a lobster.");
        add("advancements.bensfintasticsharks.hidden_trove.title", "Hidden Trove");
        add("advancements.bensfintasticsharks.hidden_trove.description", "Find a Sunken Trove.");
        add("advancements.bensfintasticsharks.captains_heir.title", "Captain's Heir");
        add("advancements.bensfintasticsharks.captains_heir.description", "Obtain Captain Ben's Hat.");

        // Per-species encounter advancements (added alongside the Legacy 1.0 mobs).
        add("advancements.bensfintasticsharks.tiger_shark_encounter.title", "Striped Hunter");
        add("advancements.bensfintasticsharks.tiger_shark_encounter.description", "Encounter a Tiger Shark.");
        add("advancements.bensfintasticsharks.oceanic_whitetip_encounter.title", "Open Water Visitor");
        add("advancements.bensfintasticsharks.oceanic_whitetip_encounter.description", "Encounter an Oceanic Whitetip Shark.");
        add("advancements.bensfintasticsharks.sandtiger_encounter.title", "Toothy Smile");
        add("advancements.bensfintasticsharks.sandtiger_encounter.description", "Encounter a Sandtiger Shark.");
        add("advancements.bensfintasticsharks.blacktip_reef_encounter.title", "Reef Patrol");
        add("advancements.bensfintasticsharks.blacktip_reef_encounter.description", "Encounter a Blacktip Reef Shark.");
        add("advancements.bensfintasticsharks.common_octopus_encounter.title", "Eight Arms");
        add("advancements.bensfintasticsharks.common_octopus_encounter.description", "Encounter a Common Octopus.");
        add("advancements.bensfintasticsharks.caribbean_reef_octopus_encounter.title", "Reef Resident");
        add("advancements.bensfintasticsharks.caribbean_reef_octopus_encounter.description", "Encounter a Caribbean Reef Octopus.");
        add("advancements.bensfintasticsharks.nautilus_encounter.title", "Living Fossil");
        add("advancements.bensfintasticsharks.nautilus_encounter.description", "Encounter a Nautilus.");
        add("advancements.bensfintasticsharks.giant_moray_eel_encounter.title", "Crevice Lurker");
        add("advancements.bensfintasticsharks.giant_moray_eel_encounter.description", "Encounter a Giant Moray Eel.");
        add("advancements.bensfintasticsharks.green_sea_turtle_encounter.title", "Slow and Steady");
        add("advancements.bensfintasticsharks.green_sea_turtle_encounter.description", "Encounter a Green Sea Turtle.");
        add("advancements.bensfintasticsharks.american_lobster_encounter.title", "Pincers Out");
        add("advancements.bensfintasticsharks.american_lobster_encounter.description", "Encounter an American Lobster.");
        add("advancements.bensfintasticsharks.black_sea_nettle_encounter.title", "Drift Sting");
        add("advancements.bensfintasticsharks.black_sea_nettle_encounter.description", "Encounter a Black Sea Nettle Jellyfish.");
        add("advancements.bensfintasticsharks.cannonball_jellyfish_encounter.title", "Round Drifter");
        add("advancements.bensfintasticsharks.cannonball_jellyfish_encounter.description", "Encounter a Cannonball Jellyfish.");
    }


    protected void addDefaultItem(Supplier<? extends Item> supplier) {
        addItem(supplier,getNameFromItem(supplier.get()));
    }

    protected void addDefaultBlock(Supplier<? extends Block> supplier) {
        addBlock(supplier,getNameFromBlock(supplier.get()));
    }

    protected void addDefaultEnchantment(Supplier<? extends Enchantment> supplier) {
        addEnchantment(supplier,getNameFromEnchantment(supplier.get()));
    }

    protected void addDefaultEntityType(Supplier<EntityType<?>> supplier) {
        addEntityType(supplier,getNameFromEntity(supplier.get()));
    }

    public static String getNameFromItem(Item item) {
        return StringUtils.capitaliseAllWords(item.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromBlock(Block block) {
        return StringUtils.capitaliseAllWords(block.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromEnchantment(Enchantment enchantment) {
        return StringUtils.capitaliseAllWords(enchantment.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    public static String getNameFromEntity(EntityType<?> entity) {
        return StringUtils.capitaliseAllWords(entity.getDescriptionId().split("\\.")[2].replace("_", " "));
    }

    protected void addTextComponent(MutableComponent component, String text) {
        ComponentContents contents = component.getContents();
        if (contents instanceof TranslatableContents translatableContents) {
            add(translatableContents.getKey(),text);
        } else {
            throw new UnsupportedOperationException(component +" is not translatable");
        }
    }

}
