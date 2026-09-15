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

    private void addFollowTranslations() {
        add("bfs.follow.reason.safety", "Safety behavior has priority");
        add("bfs.follow.tooltip", "Right click a mob to select it. Release the button, then click it again to stop following.");
        add("bfs.follow.issued", "Follow Debug Stick issued. Click mobs to add them, then click each again to release it. %s selected.");
        add("bfs.follow.issued_to", "Follow Debug Stick issued to %s.");
        add("bfs.follow.target", "%s [%s]");
        add("bfs.follow.count.one", "1 mob");
        add("bfs.follow.count.many", "%s mobs");
        add("bfs.follow.selected", "Selected: %s. %s selected.");
        add("bfs.follow.waiting", "Arrived nearby: %1$s. %3$s still selected. Click this mob again to release only it.");
        add("bfs.follow.resumed", "Following again: %1$s. %3$s selected.");
        add("bfs.follow.paused", "Paused: %s. %s. %s still selected.");
        add("bfs.follow.released", "Released: %s. %s. %s selected.");
        add("bfs.follow.group_released", "Released %s. %s selected.");
        add("bfs.follow.target_released", "The selected mob was released.");
        add("bfs.follow.not_selected", "That mob is not selected in this follow group.");
        add("bfs.follow.offline_cleanup", "Your previous follow group was cleared when you left. Released %s.");
        add("bfs.follow.permission_denied", "The Follow Debug Stick requires operator permission level 2.");
        add("bfs.follow.invalid_stick", "This Follow Debug Stick is invalid. Issue a fresh stick with /bfs debug followme.");
        add("bfs.follow.unsupported_target", "Select a living mob with the Follow Debug Stick.");
        add("bfs.follow.target_out_of_range", "Move within normal interaction reach of that mob.");
        add("bfs.follow.target_already_claimed", "That mob is selected by another operator.");
        add("bfs.follow.status", "Follow group for %s. Selected: %s. Following: %s. Waiting: %s. Paused: %s. Page %s of %s.");
        add("bfs.follow.status_entry", "%s. %s. %s.");
        add("bfs.follow.state.following", "Following");
        add("bfs.follow.state.waiting", "Waiting");
        add("bfs.follow.state.paused", "Paused");
        add("bfs.follow.reason.tracking", "Moving toward you");
        add("bfs.follow.reason.nearby", "Still selected nearby");
        add("bfs.follow.reason.marker_not_held", "Hold your valid Follow Debug Stick to resume");
        add("bfs.follow.reason.range_exceeded", "Return within 64 blocks to resume");
        add("bfs.follow.reason.no_route", "Waiting for a safe reachable route");
        add("bfs.follow.reason.clicked_again", "You clicked this mob again");
        add("bfs.follow.reason.command_stop", "Stopped by command");
        add("bfs.follow.reason.lifecycle", "The owner or mob is no longer available");
        add("bfs.follow.reason.permission_lost", "Follow authorization was lost");
        add("bfs.follow.reason.dimension_changed", "The owner and mob are in different dimensions");
        add("bfs.follow.reason.entity_left_level", "The mob left the loaded world");
        add("bfs.follow.reason.owner_left_level", "The owner left the loaded world");
        add("bfs.follow.reason.target_died", "The mob died");
        add("bfs.follow.reason.owner_died", "The owner died");
        add("bfs.follow.reason.owner_changed_dimension", "The owner changed dimension");
        add("bfs.follow.reason.owner_respawned", "The owner respawned");
        add("bfs.follow.reason.owner_logged_out", "The owner disconnected");
        add("bfs.follow.reason.server_stopping", "The server is stopping");
    }

    protected final Set<Item> exclude_items = new HashSet<>();
    @Override
    protected void addTranslations() {
        addFollowTranslations();
        BensFintasticSharks.getKnownItems().filter(item -> item instanceof BlockItem).forEach(exclude_items::add);
        // Bug 12: exclude the hat from auto-naming ("Captain Ben Hat") so the explicit
        // "Capitán" override below is the single source of truth (LanguageProvider.add throws
        // on a duplicate key, so we must not let addDefaultItem add it too).
        exclude_items.add(tfar.bensfintasticsharks.init.ModItems.CAPTAIN_BEN_HAT);
        // 0.16: explicit overrides below — the auto-names would be "Illegal Poaching"
        // (stale pre-rename advancement title) and "Shark Codex" (pre-rebrand).
        exclude_items.add(tfar.bensfintasticsharks.init.ModItems.ILLEGAL_POACHING);
        exclude_items.add(tfar.bensfintasticsharks.init.ModItems.SHARK_CODEX);
        // 0.18 — Ben: the egg should say "Common Bottlenose Dolphin" (auto-name drops "Common").
        exclude_items.add(tfar.bensfintasticsharks.init.ModItems.BOTTLENOSE_DOLPHIN_SPAWN_EGG);
        // 0.19 — these two were hand-renamed in the shipped 0.12 en_us.json and got wiped by the
        // next datagen run ("Nautilus"/"Sandtiger Shark" regressions). Overrides below.
        exclude_items.add(tfar.bensfintasticsharks.init.ModItems.NAUTILUS_SPAWN_EGG);
        exclude_items.add(tfar.bensfintasticsharks.init.ModItems.SANDTIGER_SHARK_SPAWN_EGG);
        exclude_items.add(tfar.bensfintasticsharks.init.ModItems.FOLLOW_STICK);
        BensFintasticSharks.getKnownItems().toList().stream().filter(item -> !exclude_items.contains(item)).<Supplier<? extends Item>>map(item -> () -> item).forEach(this::addDefaultItem);
        BensFintasticSharks.getKnownBlocks().<Supplier<? extends Block>>map(block -> () -> block).forEach(this::addDefaultBlock);

        for (EntityType<?> type : ModDatagen.getKnownEntityTypes().toList()) {
            // 0.18 — explicit "Common Bottlenose Dolphin" override below.
            if (type == tfar.bensfintasticsharks.init.ModEntityTypes.BOTTLENOSE_DOLPHIN) continue;
            // 0.19 — explicit "Chambered Nautilus" / "Sand Tiger Shark" overrides below.
            if (type == tfar.bensfintasticsharks.init.ModEntityTypes.NAUTILUS) continue;
            if (type == tfar.bensfintasticsharks.init.ModEntityTypes.SANDTIGER_SHARK) continue;
            addDefaultEntityType(() -> type);
        }
        add("entity.bensfintasticsharks.bottlenose_dolphin", "Common Bottlenose Dolphin");
        add("item.bensfintasticsharks.bottlenose_dolphin_spawn_egg", "Common Bottlenose Dolphin Spawn Egg");
        add("entity.bensfintasticsharks.nautilus", "Chambered Nautilus");
        add("item.bensfintasticsharks.nautilus_spawn_egg", "Chambered Nautilus Spawn Egg");
        add("entity.bensfintasticsharks.sandtiger_shark", "Sand Tiger Shark");
        add("item.bensfintasticsharks.sandtiger_shark_spawn_egg", "Sand Tiger Shark Spawn Egg");

        // 0.19 — spawn egg scientific-name tooltips (BfsSpawnEggItem.appendHoverText reads
        // `item...<egg>.scientific`; missing key = silently no tooltip). These were introduced
        // in the 0.12 release by hand-editing the GENERATED en_us.json, so the next :forge:Data
        // run deleted all 20. Datagen is the single source of truth — they live here now.
        add("item.bensfintasticsharks.american_lobster_spawn_egg.scientific", "Homarus americanus");
        add("item.bensfintasticsharks.black_sea_nettle_jellyfish_spawn_egg.scientific", "Chrysaora achlyos");
        add("item.bensfintasticsharks.blacktip_reef_shark_spawn_egg.scientific", "Carcharhinus melanopterus");
        add("item.bensfintasticsharks.bottlenose_dolphin_spawn_egg.scientific", "Tursiops truncatus");
        add("item.bensfintasticsharks.cannonball_jellyfish_spawn_egg.scientific", "Stomolophus meleagris");
        add("item.bensfintasticsharks.caribbean_reef_octopus_spawn_egg.scientific", "Octopus briareus");
        add("item.bensfintasticsharks.common_octopus_spawn_egg.scientific", "Octopus vulgaris");
        add("item.bensfintasticsharks.common_stingray_spawn_egg.scientific", "Dasyatis pastinaca");
        add("item.bensfintasticsharks.common_thresher_shark_spawn_egg.scientific", "Alopias vulpinus");
        add("item.bensfintasticsharks.giant_moray_eel_spawn_egg.scientific", "Gymnothorax javanicus");
        add("item.bensfintasticsharks.great_hammerhead_shark_spawn_egg.scientific", "Sphyrna mokarran");
        add("item.bensfintasticsharks.great_white_shark_spawn_egg.scientific", "Carcharodon carcharias");
        add("item.bensfintasticsharks.green_sea_turtle_spawn_egg.scientific", "Chelonia mydas");
        add("item.bensfintasticsharks.harbor_seal_spawn_egg.scientific", "Phoca vitulina");
        add("item.bensfintasticsharks.nautilus_spawn_egg.scientific", "Nautilus pompilius");
        add("item.bensfintasticsharks.oceanic_whitetip_shark_spawn_egg.scientific", "Carcharhinus longimanus");
        add("item.bensfintasticsharks.orca_spawn_egg.scientific", "Orcinus orca");
        add("item.bensfintasticsharks.sandtiger_shark_spawn_egg.scientific", "Carcharias taurus");
        add("item.bensfintasticsharks.shortfin_mako_shark_spawn_egg.scientific", "Isurus oxyrinchus");
        add("item.bensfintasticsharks.tiger_shark_spawn_egg.scientific", "Galeocerdo cuvier");
        add("item.bensfintasticsharks.atlantic_cod_spawn_egg.scientific", "Gadus morhua");
        add("item.bensfintasticsharks.atlantic_salmon_spawn_egg.scientific", "Salmo salar");

        addTextComponent(TextComponents.ROOT,"Ben’s Fintastic Sharks!");
        addTextComponent(TextComponents.ROOT_DESC,"Achievement granted when logging in with the mod.");

        addTextComponent(TextComponents.GREAT_WHITE_ENCOUNTER,"King of the Seas");
        addTextComponent(TextComponents.GREAT_WHITE_ENCOUNTER_DESC,"Encounter a Great White Shark.");

        addTextComponent(TextComponents.GREAT_HAMMERHEAD_ENCOUNTER,"Stop! Hammer Time!");
        addTextComponent(TextComponents.GREAT_HAMMERHEAD_ENCOUNTER_DESC,"Encounter a Great Hammerhead Shark.");

        addTextComponent(TextComponents.COMMON_THRESHER_ENCOUNTER,"Whiplash!");
        addTextComponent(TextComponents.COMMON_THRESHER_ENCOUNTER_DESC,"Encounter a Common Thresher Shark.");

        addTextComponent(TextComponents.SHORTFIN_MAKO_ENCOUNTER,"Fast as hell, twice as mean.");
        addTextComponent(TextComponents.SHORTFIN_MAKO_ENCOUNTER_DESC,"Encounter a Shortfin Mako Shark.");

        addTextComponent(TextComponents.HARBOR_SEAL_ENCOUNTER,"Awkward...");
        addTextComponent(TextComponents.HARBOR_SEAL_ENCOUNTER_DESC,"Encounter a Harbor Seal.");

        addTextComponent(TextComponents.SLEEPING_WITH_THE_FISHES,"Sleeping with the fishes.");
        addTextComponent(TextComponents.SLEEPING_WITH_THE_FISHES_DESC,"Killed by a shark.");

        addTextComponent(TextComponents.ALBINO_ENCOUNTER,"It's a shiny!");
        addTextComponent(TextComponents.ALBINO_ENCOUNTER_DESC,"Encounter an albino variant.");

        addTextComponent(TextComponents.ILLEGAL_POACHING,"Conservation Violation");
        addTextComponent(TextComponents.ILLEGAL_POACHING_DESC,"Kill a shark.");

        addTextComponent(TextComponents.JUSTICE_FOR_STEVE,"Crikey! Respect the wildlife!");
        addTextComponent(TextComponents.JUSTICE_FOR_STEVE_DESC,"Encounter and get stung by a Common Stingray.");

        addTextComponent(TextComponents.PRISMARINE_ARMOR,"The Sea Dwelling Knight");
        addTextComponent(TextComponents.PRISMARINE_ARMOR_DESC,"Obtain a full set of Prismarine Armor.");

        addTextComponent(TextComponents.ZIPPY_ENCOUNTER,"THUNDER BRINGER!");
        addTextComponent(TextComponents.ZIPPY_ENCOUNTER_DESC,"Discover Zippy.");

        addTextComponent(TextComponents.SPECIMEN_8_ENCOUNTER,"I'll be back");
        addTextComponent(TextComponents.SPECIMEN_8_ENCOUNTER_DESC,"Discover Specimen-8.");

        addTextComponent(TextComponents.DEEP_BLUE_ENCOUNTER,"Mommy Shark.");
        addTextComponent(TextComponents.DEEP_BLUE_ENCOUNTER_DESC,"Discover Deep Blue.");

        addTextComponent(TextComponents.SHARK_CODEX,"Knowledge is power…");
        addTextComponent(TextComponents.SHARK_CODEX_DESC,"Craft Capitán Ben's Codex.");

        addTextComponent(TextComponents.LOST_MANUSCRIPT,"Lost beneath the waves");
        addTextComponent(TextComponents.LOST_MANUSCRIPT_DESC,"Find a lost manuscript.");

        addTextComponent(TextComponents.LEVEL_SHARK_CODEX,"Level Up!");
        addTextComponent(TextComponents.LEVEL_SHARK_CODEX_DESC,"Combine 9 Codex Pages with Capitán Ben's Codex.");

        addTextComponent(TextComponents.SHARKS_GALORE,"Sharks Galore!");
        addTextComponent(TextComponents.SHARKS_GALORE_DESC,"Discover every species of sharks.");

        addTextComponent(TextComponents.TAB_TITLE,"Ben's Fintastic Sharks");

        // Bug 12 — explicit display-name override (auto-gen would yield "Captain Ben Hat").
        add("item.bensfintasticsharks.captain_ben_hat", "Capitán Ben's Hat");

        // 0.16 — the icon item still showed the advancement's pre-rename title on /give.
        add("item.bensfintasticsharks.illegal_poaching", "Conservation Violation");

        // 0.16 — codex rebrand: "Capitán Ben's Codex: A Compendium of Marine Wildlife".
        add("item.bensfintasticsharks.shark_codex", "Capitán Ben's Codex");
        add("item.bensfintasticsharks.shark_codex.flavor", "A Compendium of Marine Wildlife");

        // Legacy 1.0 flavor tooltips
        // Capitán Ben's quote — split across lines since item tooltips don't word-wrap.
        add("item.bensfintasticsharks.captain_ben_hat.flavor", "“Whosoever wears this hat inherits the sea");
        add("item.bensfintasticsharks.captain_ben_hat.flavor2", "and all its claims—to rule it, and be ruled");
        add("item.bensfintasticsharks.captain_ben_hat.flavor3", "by it; her favor and fury alike.”");
        add("item.bensfintasticsharks.captain_ben_hat.attribution", "— Capitán Ben");
        add("item.bensfintasticsharks.shark_trident.flavor", "Best used for show. Sharks are friends.");

        // 0.18 — Megalodon Tooth gag subtext (split so the tooltip doesn't run long).
        add("item.bensfintasticsharks.megalodon_tooth.flavor", "100% authentic Otodus megalodon");
        add("item.bensfintasticsharks.megalodon_tooth.flavor2", "specimen tooth!...Probably.");
        add("item.bensfintasticsharks.follow_stick", "Follow Debug Stick");

        // Conservation effect
        add("effect.bensfintasticsharks.respect_the_ocean", "Respect the Ocean");
        add("effect.bensfintasticsharks.respect_the_ocean.description", "The ocean is watching…");

        // Legacy 1.0 advancement strings (see BensFintasticSharksAdvancements)
        add("advancements.bensfintasticsharks.marine_curious.title", "Marine Curious");
        add("advancements.bensfintasticsharks.marine_curious.description", "Encounter your first BFS creature.");
        add("advancements.bensfintasticsharks.shark_spotter.title", "Shark Spotter");
        add("advancements.bensfintasticsharks.shark_spotter.description", "Spot a shark using a Spyglass.");
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
        add("advancements.bensfintasticsharks.inked.title", "Aw, you made me ink! >:(");
        add("advancements.bensfintasticsharks.inked.description", "Anger an octopus.");
        add("advancements.bensfintasticsharks.dolphin_friend.title", "Dolphin Tale");
        add("advancements.bensfintasticsharks.dolphin_friend.description", "Encounter a Common Bottlenose Dolphin in the wild.");
        add("advancements.bensfintasticsharks.apex_of_apex.title", "Ocean Sovereign");
        add("advancements.bensfintasticsharks.apex_of_apex.description", "Encounter an Orca in the wild.");
        add("advancements.bensfintasticsharks.fresh_catch.title", "Fresh Catch");
        add("advancements.bensfintasticsharks.fresh_catch.description", "Cook a lobster.");
        add("advancements.bensfintasticsharks.hidden_trove.title", "Hidden Trove");
        add("advancements.bensfintasticsharks.hidden_trove.description", "Find a Sunken Trove.");
        add("advancements.bensfintasticsharks.captains_heir.title", "El Capit\u00E1n's Legacy");
        add("advancements.bensfintasticsharks.captains_heir.description", "Obtain Capitán Ben's Hat.");
        add("advancements.bensfintasticsharks.fancy_fork.title", "Fancy Fork");
        add("advancements.bensfintasticsharks.fancy_fork.description", "Obtain a Shark Trident.");
        // 0.18 — Megalodon Tooth gag advancement.
        add("advancements.bensfintasticsharks.source_trust_me_bro.title", "Source: trust me bro");
        add("advancements.bensfintasticsharks.source_trust_me_bro.description", "Obtain a Megalodon Tooth.");

        // Per-species encounter advancements (added alongside the Legacy 1.0 mobs).
        add("advancements.bensfintasticsharks.tiger_shark_encounter.title", "Striped Garbage Can!");
        add("advancements.bensfintasticsharks.tiger_shark_encounter.description", "Encounter a Tiger Shark.");
        add("advancements.bensfintasticsharks.oceanic_whitetip_encounter.title", "Pelagic Nightmare");
        add("advancements.bensfintasticsharks.oceanic_whitetip_encounter.description", "Encounter an Oceanic Whitetip Shark.");
        add("advancements.bensfintasticsharks.sandtiger_encounter.title", "OOOOH HOOHOHOHOO!");
        add("advancements.bensfintasticsharks.sandtiger_encounter.description", "Encounter a Sandtiger Shark.");
        add("advancements.bensfintasticsharks.blacktip_reef_encounter.title", "Reef Predator");
        add("advancements.bensfintasticsharks.blacktip_reef_encounter.description", "Encounter a Blacktip Reef Shark.");
        add("advancements.bensfintasticsharks.common_octopus_encounter.title", "Octopus vulgaris");
        add("advancements.bensfintasticsharks.common_octopus_encounter.description", "Encounter a Common Octopus.");
        add("advancements.bensfintasticsharks.caribbean_reef_octopus_encounter.title", "Coral Camouflaged");
        add("advancements.bensfintasticsharks.caribbean_reef_octopus_encounter.description", "Encounter a Caribbean Reef Octopus.");
        add("advancements.bensfintasticsharks.nautilus_encounter.title", "Living Fossil");
        add("advancements.bensfintasticsharks.nautilus_encounter.description", "Encounter a Nautilus.");
        add("advancements.bensfintasticsharks.giant_moray_eel_encounter.title", "Crevice Lurker");
        add("advancements.bensfintasticsharks.giant_moray_eel_encounter.description", "Encounter a Giant Moray Eel.");
        add("advancements.bensfintasticsharks.green_sea_turtle_encounter.title", "Duuuude.");
        add("advancements.bensfintasticsharks.green_sea_turtle_encounter.description", "Encounter a Green Sea Turtle.");
        add("advancements.bensfintasticsharks.american_lobster_encounter.title", "Pincers Out");
        add("advancements.bensfintasticsharks.american_lobster_encounter.description", "Encounter an American Lobster.");
        add("advancements.bensfintasticsharks.black_sea_nettle_encounter.title", "Drift Sting");
        add("advancements.bensfintasticsharks.black_sea_nettle_encounter.description", "Encounter a Black Sea Nettle Jellyfish.");
        add("advancements.bensfintasticsharks.cannonball_jellyfish_encounter.title", "Round Drifter");
        add("advancements.bensfintasticsharks.cannonball_jellyfish_encounter.description", "Encounter a Cannonball Jellyfish.");
        add("advancements.bensfintasticsharks.oh_my_cod.title", "Oh My Cod");
        add("advancements.bensfintasticsharks.oh_my_cod.description", "Catch an Atlantic Cod.");
        add("advancements.bensfintasticsharks.why_arent_you_red.title", "Why aren't you red?");
        add("advancements.bensfintasticsharks.why_arent_you_red.description", "Catch an Atlantic Salmon.");
        add("advancements.bensfintasticsharks.gadus_morhua.title", "Gadus morhua");
        add("advancements.bensfintasticsharks.gadus_morhua.description", "Encounter an Atlantic Cod.");
        add("advancements.bensfintasticsharks.salmo_salar.title", "Salmo salar");
        add("advancements.bensfintasticsharks.salmo_salar.description", "Encounter an Atlantic Salmon.");
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
