package tfar.bensfintasticsharks.audit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Deterministic checks for the supplied 0.24 release contract. */
class ReleaseContractAuditTest {

    private static final double TRANSFORM_TOLERANCE = 0.0001D;
    private static final List<Double> NORMALIZED_SAMPLE_POINTS = List.of(0.0D, 0.25D, 0.5D, 0.75D, 1.0D);
    private static final Path ROOT = findProjectRoot();
    private static final Path GENERATED = ROOT.resolve("common/src/generated/resources");
    private static final Path SOURCE_ASSETS = ROOT.resolve("common/src/main/resources/assets/bensfintasticsharks");

    private static final Map<String, String> ICON_HASHES = new LinkedHashMap<>(Map.ofEntries(
            Map.entry("albino.png", "e0638a22ee40480f03a7e4361ff324e4a8d9b1cb93590a2ec695b2830f79ab76"),
            Map.entry("harbor_seal_block.png", "e5e531f62b458fea3ab50b1e61abc4504c75453fa7cd06c8640aabf794b0abd1"),
            Map.entry("mommy_shark.png", "58fb46c5267ea9b6e027fdaf4532ad2d76de7dcf75aa046367f3720f18c00cde"),
            Map.entry("sharks_galore.png", "11f66a72567bb8ae16aa2022ca21778d43eb0d0fcbb531b21d34e03fa4e020f6"),
            Map.entry("sleeping_with_the_fishes.png", "6c01085423275d602fbad91924a8772249c1a8dbeee749f5e1e9907df0995977"),
            Map.entry("specimen_8.png", "88f6dc345a7c399034142cf2bebc54fdd488e41b9f36f19d470806d393c1fc65"),
            Map.entry("zippy_pixel_art.png", "4f54793625dc71ab456ca58de55b6bfe015f586c81930d11b72d5a3d6942595d")
    ));

    private static final Map<String, String> EXPECTED_ADVANCEMENT_COPY = Map.ofEntries(
            Map.entry("advancements.bensfintasticsharks.albino_encounter.title", "It's a shiny!"),
            Map.entry("advancements.bensfintasticsharks.albino_encounter.description", "Encounter an albino variant."),
            Map.entry("advancements.bensfintasticsharks.american_lobster_encounter.title", "Pincers Out"),
            Map.entry("advancements.bensfintasticsharks.american_lobster_encounter.description", "Encounter an American Lobster."),
            Map.entry("advancements.bensfintasticsharks.apex_awareness.title", "Apex Awareness"),
            Map.entry("advancements.bensfintasticsharks.apex_awareness.description", "Survive a shark attack without killing the shark."),
            Map.entry("advancements.bensfintasticsharks.apex_of_apex.title", "Ocean Sovereign"),
            Map.entry("advancements.bensfintasticsharks.apex_of_apex.description", "Encounter an Orca in the wild."),
            Map.entry("advancements.bensfintasticsharks.black_sea_nettle_encounter.title", "Drift Sting"),
            Map.entry("advancements.bensfintasticsharks.black_sea_nettle_encounter.description", "Encounter a Black Sea Nettle Jellyfish."),
            Map.entry("advancements.bensfintasticsharks.blacktip_reef_encounter.title", "Reef Predator"),
            Map.entry("advancements.bensfintasticsharks.blacktip_reef_encounter.description", "Encounter a Blacktip Reef Shark."),
            Map.entry("advancements.bensfintasticsharks.cannonball_jellyfish_encounter.title", "Round Drifter"),
            Map.entry("advancements.bensfintasticsharks.cannonball_jellyfish_encounter.description", "Encounter a Cannonball Jellyfish."),
            Map.entry("advancements.bensfintasticsharks.captains_heir.title", "El Capitán's Legacy"),
            Map.entry("advancements.bensfintasticsharks.captains_heir.description", "Obtain Capitán Ben's Hat."),
            Map.entry("advancements.bensfintasticsharks.caribbean_reef_octopus_encounter.title", "Coral Camouflaged"),
            Map.entry("advancements.bensfintasticsharks.caribbean_reef_octopus_encounter.description", "Encounter a Caribbean Reef Octopus."),
            Map.entry("advancements.bensfintasticsharks.common_octopus_encounter.title", "Octopus vulgaris"),
            Map.entry("advancements.bensfintasticsharks.common_octopus_encounter.description", "Encounter a Common Octopus."),
            Map.entry("advancements.bensfintasticsharks.common_thresher_encounter.title", "Whiplash!"),
            Map.entry("advancements.bensfintasticsharks.common_thresher_encounter.description", "Encounter a Common Thresher Shark."),
            Map.entry("advancements.bensfintasticsharks.conservationist.title", "Conservationist"),
            Map.entry("advancements.bensfintasticsharks.conservationist.description", "Earn the Respect the Ocean effect... actually wait, don't. Encounter sharks, let them live."),
            Map.entry("advancements.bensfintasticsharks.deep_blue_encounter.title", "Mommy Shark."),
            Map.entry("advancements.bensfintasticsharks.deep_blue_encounter.description", "Discover Deep Blue."),
            Map.entry("advancements.bensfintasticsharks.dolphin_friend.title", "Dolphin Tale"),
            Map.entry("advancements.bensfintasticsharks.dolphin_friend.description", "Encounter a Common Bottlenose Dolphin in the wild."),
            Map.entry("advancements.bensfintasticsharks.fancy_fork.title", "Fancy Fork"),
            Map.entry("advancements.bensfintasticsharks.fancy_fork.description", "Obtain a Shark Trident."),
            Map.entry("advancements.bensfintasticsharks.fresh_catch.title", "Fresh Catch"),
            Map.entry("advancements.bensfintasticsharks.fresh_catch.description", "Cook a lobster."),
            Map.entry("advancements.bensfintasticsharks.gadus_morhua.title", "Gadus morhua"),
            Map.entry("advancements.bensfintasticsharks.gadus_morhua.description", "Encounter an Atlantic Cod."),
            Map.entry("advancements.bensfintasticsharks.giant_moray_eel_encounter.title", "Crevice Lurker"),
            Map.entry("advancements.bensfintasticsharks.giant_moray_eel_encounter.description", "Encounter a Giant Moray Eel."),
            Map.entry("advancements.bensfintasticsharks.great_hammerhead_encounter.title", "Stop! Hammer Time!"),
            Map.entry("advancements.bensfintasticsharks.great_hammerhead_encounter.description", "Encounter a Great Hammerhead Shark."),
            Map.entry("advancements.bensfintasticsharks.great_white_encounter.title", "King of the Seas"),
            Map.entry("advancements.bensfintasticsharks.great_white_encounter.description", "Encounter a Great White Shark."),
            Map.entry("advancements.bensfintasticsharks.green_sea_turtle_encounter.title", "Duuuude."),
            Map.entry("advancements.bensfintasticsharks.green_sea_turtle_encounter.description", "Encounter a Green Sea Turtle."),
            Map.entry("advancements.bensfintasticsharks.harbor_seal_encounter.title", "Awkward..."),
            Map.entry("advancements.bensfintasticsharks.harbor_seal_encounter.description", "Encounter a Harbor Seal."),
            Map.entry("advancements.bensfintasticsharks.hidden_trove.title", "Hidden Trove"),
            Map.entry("advancements.bensfintasticsharks.hidden_trove.description", "Find a Sunken Trove."),
            Map.entry("advancements.bensfintasticsharks.illegal_poaching.title", "Conservation Violation"),
            Map.entry("advancements.bensfintasticsharks.illegal_poaching.description", "Kill a shark."),
            Map.entry("advancements.bensfintasticsharks.inked.title", "Aw, you made me ink! >:("),
            Map.entry("advancements.bensfintasticsharks.inked.description", "Anger an octopus."),
            Map.entry("advancements.bensfintasticsharks.justice_for_steve.title", "Crikey! Respect the wildlife!"),
            Map.entry("advancements.bensfintasticsharks.justice_for_steve.description", "Encounter and get stung by a Common Stingray."),
            Map.entry("advancements.bensfintasticsharks.level_shark_codex.title", "Level Up!"),
            Map.entry("advancements.bensfintasticsharks.level_shark_codex.description", "Combine 9 Codex Pages with Capitán Ben's Codex."),
            Map.entry("advancements.bensfintasticsharks.lost_manuscript.title", "Lost beneath the waves"),
            Map.entry("advancements.bensfintasticsharks.lost_manuscript.description", "Find a lost manuscript."),
            Map.entry("advancements.bensfintasticsharks.marine_biologist.title", "Marine Biologist"),
            Map.entry("advancements.bensfintasticsharks.marine_biologist.description", "Encounter every BFS species."),
            Map.entry("advancements.bensfintasticsharks.marine_curious.title", "Marine Curious"),
            Map.entry("advancements.bensfintasticsharks.marine_curious.description", "Encounter your first BFS creature."),
            Map.entry("advancements.bensfintasticsharks.nautilus_encounter.title", "Living Fossil"),
            Map.entry("advancements.bensfintasticsharks.nautilus_encounter.description", "Encounter a Nautilus."),
            Map.entry("advancements.bensfintasticsharks.oceanic_whitetip_encounter.title", "Pelagic Nightmare"),
            Map.entry("advancements.bensfintasticsharks.oceanic_whitetip_encounter.description", "Encounter an Oceanic Whitetip Shark."),
            Map.entry("advancements.bensfintasticsharks.oh_my_cod.title", "Oh My Cod"),
            Map.entry("advancements.bensfintasticsharks.oh_my_cod.description", "Catch an Atlantic Cod."),
            Map.entry("advancements.bensfintasticsharks.prismarine_armor.title", "The Sea Dwelling Knight"),
            Map.entry("advancements.bensfintasticsharks.prismarine_armor.description", "Obtain a full set of Prismarine Armor."),
            Map.entry("advancements.bensfintasticsharks.root.title", "Ben’s Fintastic Sharks!"),
            Map.entry("advancements.bensfintasticsharks.root.description", "Achievement granted when logging in with the mod."),
            Map.entry("advancements.bensfintasticsharks.salmo_salar.title", "Salmo salar"),
            Map.entry("advancements.bensfintasticsharks.salmo_salar.description", "Encounter an Atlantic Salmon."),
            Map.entry("advancements.bensfintasticsharks.sandtiger_encounter.title", "OOOOH HOOHOHOHOO!"),
            Map.entry("advancements.bensfintasticsharks.sandtiger_encounter.description", "Encounter a Sandtiger Shark."),
            Map.entry("advancements.bensfintasticsharks.shark_codex.title", "Knowledge is power…"),
            Map.entry("advancements.bensfintasticsharks.shark_codex.description", "Craft Capitán Ben's Codex."),
            Map.entry("advancements.bensfintasticsharks.shark_spotter.title", "Shark Spotter"),
            Map.entry("advancements.bensfintasticsharks.shark_spotter.description", "Spot a shark using a Spyglass."),
            Map.entry("advancements.bensfintasticsharks.sharks_galore.title", "Sharks Galore!"),
            Map.entry("advancements.bensfintasticsharks.sharks_galore.description", "Discover every species of sharks."),
            Map.entry("advancements.bensfintasticsharks.shortfin_mako_encounter.title", "Fast as hell, twice as mean."),
            Map.entry("advancements.bensfintasticsharks.shortfin_mako_encounter.description", "Encounter a Shortfin Mako Shark."),
            Map.entry("advancements.bensfintasticsharks.sleeping_with_the_fishes.title", "Sleeping with the fishes."),
            Map.entry("advancements.bensfintasticsharks.sleeping_with_the_fishes.description", "Killed by a shark."),
            Map.entry("advancements.bensfintasticsharks.source_trust_me_bro.title", "Source: trust me bro"),
            Map.entry("advancements.bensfintasticsharks.source_trust_me_bro.description", "Obtain a Megalodon Tooth."),
            Map.entry("advancements.bensfintasticsharks.specimen_8_encounter.title", "I'll be back"),
            Map.entry("advancements.bensfintasticsharks.specimen_8_encounter.description", "Discover Specimen-8."),
            Map.entry("advancements.bensfintasticsharks.stung.title", "Stung!"),
            Map.entry("advancements.bensfintasticsharks.stung.description", "Take damage from a jellyfish."),
            Map.entry("advancements.bensfintasticsharks.tiger_shark_encounter.title", "Striped Garbage Can!"),
            Map.entry("advancements.bensfintasticsharks.tiger_shark_encounter.description", "Encounter a Tiger Shark."),
            Map.entry("advancements.bensfintasticsharks.why_arent_you_red.title", "Why aren't you red?"),
            Map.entry("advancements.bensfintasticsharks.why_arent_you_red.description", "Catch an Atlantic Salmon."),
            Map.entry("advancements.bensfintasticsharks.wrong_place_wrong_time.title", "Wrong Place, Wrong Time"),
            Map.entry("advancements.bensfintasticsharks.wrong_place_wrong_time.description", "Be attacked by a shark shortly after entering the water."),
            Map.entry("advancements.bensfintasticsharks.zippy_encounter.title", "THUNDER BRINGER!"),
            Map.entry("advancements.bensfintasticsharks.zippy_encounter.description", "Discover Zippy.")
    );

    private static final List<SpeciesPresentation> LIVING_SPECIES = List.of(
            new SpeciesPresentation("great_white_shark", "GREAT_WHITE_SHARK"),
            new SpeciesPresentation("great_hammerhead_shark", "GREAT_HAMMERHEAD_SHARK"),
            new SpeciesPresentation("common_thresher_shark", "COMMON_THRESHER_SHARK"),
            new SpeciesPresentation("shortfin_mako_shark", "SHORTFIN_MAKO_SHARK"),
            new SpeciesPresentation("tiger_shark", "TIGER_SHARK"),
            new SpeciesPresentation("oceanic_whitetip_shark", "OCEANIC_WHITETIP_SHARK"),
            new SpeciesPresentation("sandtiger_shark", "SANDTIGER_SHARK"),
            new SpeciesPresentation("blacktip_reef_shark", "BLACKTIP_REEF_SHARK"),
            new SpeciesPresentation("orca", "ORCA"),
            new SpeciesPresentation("bottlenose_dolphin", "BOTTLENOSE_DOLPHIN"),
            new SpeciesPresentation("common_octopus", "COMMON_OCTOPUS"),
            new SpeciesPresentation("caribbean_reef_octopus", "CARIBBEAN_REEF_OCTOPUS"),
            new SpeciesPresentation("nautilus", "NAUTILUS"),
            new SpeciesPresentation("giant_moray_eel", "GIANT_MORAY_EEL"),
            new SpeciesPresentation("green_sea_turtle", "GREEN_SEA_TURTLE"),
            new SpeciesPresentation("american_lobster", "AMERICAN_LOBSTER"),
            new SpeciesPresentation("common_stingray", "COMMON_STINGRAY"),
            new SpeciesPresentation("harbor_seal", "HARBOR_SEAL"),
            new SpeciesPresentation("black_sea_nettle_jellyfish", "BLACK_SEA_NETTLE_JELLYFISH"),
            new SpeciesPresentation("cannonball_jellyfish", "CANNONBALL_JELLYFISH"),
            new SpeciesPresentation("atlantic_cod", "ATLANTIC_COD"),
            new SpeciesPresentation("atlantic_salmon", "ATLANTIC_SALMON")
    );

    private static final Map<String, String> SUPPLIED_ICON_MODELS = Map.ofEntries(
            Map.entry("albino", "albino"),
            Map.entry("harbor_seal_block", "harbor_seal_block"),
            Map.entry("sharks_galore", "sharks_galore"),
            Map.entry("sleeping_with_the_fishes", "sleeping_with_the_fishes"),
            Map.entry("specimen_8", "specimen_8"),
            Map.entry("mommy_shark", "mommy_shark"),
            Map.entry("zippy_pixel_art", "zippy_pixel_art")
    );

    private static final Map<String, String> CHILD_SEMANTIC_HASHES = Map.of(
            "marine_biologist", "50948053b89d56628b1fc3ed9fa4727daf4a2b7b415e1c601664fd74aaad2807",
            "apex_of_apex", "6c52dc0444a1038a51fd24b609795647108a85753caeb9b5f106e1454a04a443"
    );

    @Test
    void suppliedAdvancementCopyAndPunctuationAreStable() throws IOException {
        JsonObject language = readJson(GENERATED.resolve("assets/bensfintasticsharks/lang/en_us.json"));
        Path advancementDir = GENERATED.resolve("data/bensfintasticsharks/advancements");
        Map<String, String> referencedCopy = new LinkedHashMap<>();
        try (var paths = Files.list(advancementDir)) {
            for (Path path : paths.filter(p -> p.getFileName().toString().endsWith(".json")).toList()) {
                JsonObject advancement = readJson(path);
                JsonObject display = advancement.getAsJsonObject("display");
                String titleKey = display.getAsJsonObject("title").get("translate").getAsString();
                String descriptionKey = display.getAsJsonObject("description").get("translate").getAsString();
                assertTrue(language.has(titleKey), path.getFileName().toString());
                assertTrue(language.has(descriptionKey), path.getFileName().toString());
                assertTrue(referencedCopy.put(titleKey, language.get(titleKey).getAsString()) == null, titleKey);
                assertTrue(referencedCopy.put(descriptionKey, language.get(descriptionKey).getAsString()) == null, descriptionKey);
            }
        }

        assertEquals(EXPECTED_ADVANCEMENT_COPY, referencedCopy);
        assertEquals(EXPECTED_ADVANCEMENT_COPY.keySet(), referencedCopy.keySet());
        assertEquals("Obtain Capitán Ben's Hat.", language.get("advancements.bensfintasticsharks.captains_heir.description").getAsString());
        assertFalse(language.has("advancements.bensfintasticsharks.shark_whisperer.title"));
        assertFalse(language.has("advancements.bensfintasticsharks.shark_whisperer.description"));

        for (Map.Entry<String, String> entry : EXPECTED_ADVANCEMENT_COPY.entrySet()) {
            String text = entry.getValue();
            assertEquals(text.trim(), text, entry.getKey());
            assertFalse(text.contains("'") && text.contains("’"), entry.getKey());
            if (entry.getKey().endsWith(".description")) {
                assertTrue(text.matches(".*[.!?]$"), entry.getKey());
                assertFalse(text.matches(".*[.!?]{2,}$"), entry.getKey());
            }
        }
    }

    @Test
    void advancementGraphRetiresDuplicateAndPreservesChildren() throws IOException {
        Path advancementDir = GENERATED.resolve("data/bensfintasticsharks/advancements");
        Map<String, JsonObject> advancements = new HashMap<>();
        try (var paths = Files.list(advancementDir)) {
            for (Path path : paths.filter(p -> p.getFileName().toString().endsWith(".json")).toList()) {
                String id = path.getFileName().toString().replaceFirst("\\.json$", "");
                advancements.put(id, readJson(path));
            }
        }
        assertFalse(advancements.containsKey("shark_whisperer"));
        assertEquals("bensfintasticsharks:sharks_galore", advancements.get("marine_biologist").get("parent").getAsString());
        assertEquals("bensfintasticsharks:sharks_galore", advancements.get("apex_of_apex").get("parent").getAsString());

        for (Map.Entry<String, JsonObject> entry : advancements.entrySet()) {
            JsonElement parent = entry.getValue().get("parent");
            if (parent != null && parent.isJsonPrimitive() && parent.getAsString().startsWith("bensfintasticsharks:")) {
                String parentId = parent.getAsString().substring("bensfintasticsharks:".length());
                assertTrue(advancements.containsKey(parentId), entry.getKey() + " has a missing parent");
            }
        }

        Set<String> visiting = new HashSet<>();
        Set<String> visited = new HashSet<>();
        for (String id : advancements.keySet()) {
            assertFalse(hasCycle(id, advancements, visiting, visited), id);
        }

        Set<String> roots = advancements.entrySet().stream()
                .filter(entry -> !entry.getValue().has("parent"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        assertEquals(Set.of("root"), roots);
        for (String id : advancements.keySet()) {
            assertTrue(reachesRoot(id, advancements, new HashSet<>()), id + " is not rooted");
        }

        String provider = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/datagen/data/BensFintasticSharksAdvancements.java"));
        String languageProvider = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/datagen/ModLangProvider.java"));
        assertFalse(provider.contains("shark_whisperer"));
        assertFalse(languageProvider.contains("shark_whisperer"));
        assertFalse(Files.readString(GENERATED.resolve("assets/bensfintasticsharks/lang/en_us.json"))
                .contains("shark_whisperer"));
    }

    @Test
    void advancementChildSemanticsAndIconModelsAreDeterministic() throws IOException {
        Path advancementDir = GENERATED.resolve("data/bensfintasticsharks/advancements");
        for (Map.Entry<String, String> entry : CHILD_SEMANTIC_HASHES.entrySet()) {
            JsonObject child = readJson(advancementDir.resolve(entry.getKey() + ".json"));
            assertEquals("bensfintasticsharks:sharks_galore", child.get("parent").getAsString(), entry.getKey());
            assertEquals(entry.getValue(), semanticDigest(child), entry.getKey());
        }

        Path modelDir = GENERATED.resolve("assets/bensfintasticsharks/models/item");
        Path sourceModelDir = SOURCE_ASSETS.resolve("models/item");
        Set<String> modelPaths = new HashSet<>();
        for (Map.Entry<String, String> entry : SUPPLIED_ICON_MODELS.entrySet()) {
            Path modelPath = modelDir.resolve(entry.getKey() + ".json");
            assertTrue(modelPaths.add(modelPath.toString()), modelPath.toString());
            assertTrue(Files.exists(modelPath), entry.getKey());
            JsonObject model = readJson(modelPath);
            assertEquals("minecraft:item/generated", model.get("parent").getAsString(), entry.getKey());
            assertEquals("bensfintasticsharks:item/" + entry.getValue(),
                    model.getAsJsonObject("textures").get("layer0").getAsString(), entry.getKey());
            assertFalse(Files.exists(sourceModelDir.resolve(entry.getKey() + ".json")), entry.getKey());
        }
    }

    @Test
    void advancementMutationFixturesAreRejected() throws IOException {
        JsonObject child = readJson(GENERATED.resolve("data/bensfintasticsharks/advancements/marine_biologist.json"));
        JsonObject changedCriteria = JsonParser.parseString(child.toString()).getAsJsonObject();
        changedCriteria.getAsJsonObject("criteria").remove("atlantic_cod");
        assertNotEquals(CHILD_SEMANTIC_HASHES.get("marine_biologist"), semanticDigest(changedCriteria));

        JsonObject wrongParent = JsonParser.parseString(child.toString()).getAsJsonObject();
        wrongParent.addProperty("parent", "bensfintasticsharks:root");
        assertEquals("bensfintasticsharks:root", wrongParent.get("parent").getAsString());
        assertNotEquals("bensfintasticsharks:sharks_galore", wrongParent.get("parent").getAsString());

        Map<String, String> missingCopy = new LinkedHashMap<>(EXPECTED_ADVANCEMENT_COPY);
        missingCopy.remove("advancements.bensfintasticsharks.captains_heir.description");
        assertNotEquals(EXPECTED_ADVANCEMENT_COPY, missingCopy);

        JsonObject swappedModel = readJson(GENERATED.resolve(
                "assets/bensfintasticsharks/models/item/albino.json"));
        swappedModel.getAsJsonObject("textures").addProperty("layer0", "bensfintasticsharks:item/zippy_pixel_art");
        assertNotEquals("bensfintasticsharks:item/albino",
                swappedModel.getAsJsonObject("textures").get("layer0").getAsString());
    }

    @Test
    void suppliedAdvancementIconsAreExact16PixelCopies() throws IOException {
        Path itemTextures = SOURCE_ASSETS.resolve("textures/item");
        for (Map.Entry<String, String> entry : ICON_HASHES.entrySet()) {
            Path path = itemTextures.resolve(entry.getKey());
            BufferedImage image = ImageIO.read(path.toFile());
            assertNotNull(image, entry.getKey());
            assertEquals(16, image.getWidth(), entry.getKey());
            assertEquals(16, image.getHeight(), entry.getKey());
            assertEquals(entry.getValue(), sha256(path), entry.getKey());
        }
    }

    @Test
    void fishAndWhitetipClipsHaveDistinctFivePointTransformSamples() throws IOException {
        assertClipHasFivePointMotion("atlantic_cod.animation.json", "animation.atlantic_cod.idle");
        assertClipHasFivePointMotion("atlantic_cod.animation.json", "animation.atlantic_cod.swim");
        assertClipHasFivePointMotion("atlantic_cod.animation.json", "animation.atlantic_cod.swim_fast");
        assertClipHasFivePointMotion("atlantic_salmon.animation.json", "animation.atlantic_salmon.idle");
        assertClipHasFivePointMotion("atlantic_salmon.animation.json", "animation.atlantic_salmon.swim");
        assertClipHasFivePointMotion("atlantic_salmon.animation.json", "animation.atlantic_salmon.swim_fast");
        assertClipHasFivePointMotion("oceanic_whitetip_shark.animation.json", "animation.oceanicwhitetipshark.idle");
        assertClipHasFivePointMotion("oceanic_whitetip_shark.animation.json", "animation.oceanicwhitetipshark.swim_new");
        assertClipHasFivePointMotion("oceanic_whitetip_shark.animation.json", "animation.oceanicwhitetipshark.swim_fast_new");
        assertClipHasFivePointMotion("oceanic_whitetip_shark.animation.json", "animation.oceanicwhitetipshark.bite_new");
        assertClipHasFivePointMotion("oceanic_whitetip_shark.animation.json", "animation.oceanicwhitetipshark.death");
        assertClipHasFivePointMotion("oceanic_whitetip_shark.animation.json", "animation.oceanicwhitetipshark.beached");
        assertClipHasFivePointMotion("oceanic_whitetip_shark.animation.json", "animation.oceanicwhitetipshark.thrash");
    }

    @Test
    void oceanicClipBonesAndControllerStatesAreComplete() throws IOException {
        Path geometryPath = SOURCE_ASSETS.resolve("geo/entity/oceanic_whitetip_shark.geo.json");
        JsonObject geometry = readJson(geometryPath);
        Set<String> bones = new HashSet<>();
        for (JsonElement geometryEntry : geometry.getAsJsonArray("minecraft:geometry")) {
            for (JsonElement bone : geometryEntry.getAsJsonObject().getAsJsonArray("bones")) {
                bones.add(bone.getAsJsonObject().get("name").getAsString());
            }
        }

        JsonObject animations = readJson(SOURCE_ASSETS.resolve("animations/entity/oceanic_whitetip_shark.animation.json"))
                .getAsJsonObject("animations");
        Map<String, Set<String>> expected = Map.of(
                "animation.oceanicwhitetipshark.idle", Set.of("Body", "Head", "Jaw", "Tail", "Tail2", "Tail3", "Tail4", "Tail5", "Fin", "Fin2"),
                "animation.oceanicwhitetipshark.swim_new", Set.of("Body", "Head", "Tail", "Tail2", "Tail3", "Tail4", "Tail5", "Fin", "Fin2"),
                "animation.oceanicwhitetipshark.swim_fast_new", Set.of("Body", "Head", "Jaw", "Tail", "Tail2", "Tail3", "Tail4", "Tail5", "Fin", "Fin2"),
                "animation.oceanicwhitetipshark.bite_new", Set.of("Body", "Head", "Jaw", "JawBase", "Tail", "Tail2", "Tail3", "Tail4", "Tail5", "Fin", "Fin2"),
                "animation.oceanicwhitetipshark.death", Set.of("Body", "Head", "Jaw", "JawBase", "Tail", "Tail2", "Tail3", "Tail4", "Tail5", "Fin", "Fin2"),
                "animation.oceanicwhitetipshark.beached", Set.of("Body", "Head", "Jaw", "JawBase", "Tail", "Tail2", "Tail3", "Tail4", "Tail5", "Fin", "Fin2"),
                "animation.oceanicwhitetipshark.thrash", Set.of("Body", "Head", "Jaw", "JawBase", "Tail", "Tail2", "Tail3", "Tail4", "Tail5", "Fin", "Fin2")
        );
        assertEquals(expected.keySet(), animations.keySet());
        for (Map.Entry<String, Set<String>> entry : expected.entrySet()) {
            Set<String> animatedBones = animations.getAsJsonObject(entry.getKey()).getAsJsonObject("bones").keySet();
            assertEquals(entry.getValue(), animatedBones, entry.getKey());
            assertTrue(bones.containsAll(animatedBones), entry.getKey());
        }

        String forgeSource = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/entity/OceanicWhitetipSharkEntityForge.java"));
        for (String clip : List.of("idle", "swim_new", "swim_fast_new", "beached", "thrash", "bite_new", "death")) {
            assertTrue(forgeSource.contains("animation.oceanicwhitetipshark." + clip), clip);
        }
        assertTrue(forgeSource.indexOf("DEATH") < forgeSource.indexOf("THRASH"));
        assertTrue(forgeSource.contains("if (this.onGround() && !this.isInWaterOrBubble())"));
        assertTrue(forgeSource.contains(".triggerableAnim(\"bite\", BITE)"));
        assertTrue(forgeSource.contains(".triggerableAnim(\"death\", DEATH)"));
        assertTrue(forgeSource.contains("if (!this.isDeadOrDying() && this.isInWaterOrBubble()"));
        assertTrue(forgeSource.contains("&& this.getGrabTimer() > 0 && !this.getPassengers().isEmpty()"));
        assertTrue(forgeSource.indexOf("if (this.isDeadOrDying())")
                < forgeSource.indexOf("if (this.getSharkState() == SharkState.HOSTILE)"));
    }

    @Test
    void retainedEntityAndSpawnContractsAreExplicit() throws IOException {
        String cod = Files.readString(ROOT.resolve(
                "common/src/main/java/tfar/bensfintasticsharks/entity/AtlanticCodEntity.java"));
        String salmon = Files.readString(ROOT.resolve(
                "common/src/main/java/tfar/bensfintasticsharks/entity/AtlanticSalmonEntity.java"));
        String salmonForge = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/entity/AtlanticSalmonEntityForge.java"));
        String platform = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/platform/ForgePlatformHelper.java"));
        String spawnPlacements = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/spawn/BfsSpawnPlacements.java"));
        assertTrue(cod.contains("extends Cod"));
        assertTrue(salmon.contains("extends Salmon"));
        assertTrue(salmon.contains("\"Spin\".equals(getCustomName().getString())"));
        assertTrue(salmonForge.contains("if (isNamedSpin())"));
        assertTrue(salmonForge.contains("animation.atlantic_salmon.spin"));
        assertTrue(platform.contains(".sized(0.5f, 0.3f)"));
        assertTrue(platform.contains(".sized(0.7f, 0.4f)"));
        assertEquals(2, countOccurrences(platform, ".clientTrackingRange(4)"));
        assertTrue(spawnPlacements.contains("registerFish(event, ModEntityTypes.ATLANTIC_COD)"));
        assertTrue(spawnPlacements.contains("registerFish(event, ModEntityTypes.ATLANTIC_SALMON)"));
        assertTrue(spawnPlacements.contains("Heightmap.Types.MOTION_BLOCKING_NO_LEAVES"));

        Path resources = GENERATED.resolve("data/bensfintasticsharks");
        for (String fish : List.of("atlantic_cod", "atlantic_salmon")) {
            assertTrue(Files.exists(resources.resolve("loot_tables/entities/" + fish + ".json")), fish);
            assertTrue(Files.exists(resources.resolve("recipes/cooked_" + fish + "_from_smelting.json")), fish);
            assertTrue(Files.exists(resources.resolve("recipes/cooked_" + fish + "_from_smoking.json")), fish);
        }
        assertTrue(Files.exists(resources.resolve("loot_modifiers/replace_fishing_fish.json")));
        assertFalse(Files.exists(resources.resolve("loot_modifiers/add_atlantic_cod_fishing.json")));
        assertFalse(Files.exists(resources.resolve("loot_modifiers/add_atlantic_salmon_fishing.json")));
        String speciesInfo = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/command/BfsSpeciesInfo.java"));
        assertTrue(speciesInfo.contains("atlantic_cod\", species(\"Gadus morhua\""));
        assertTrue(speciesInfo.contains("atlantic_salmon\", species(\"Salmo salar\""));
        assertFalse(speciesInfo.contains("atlantic_cod\", species(\"Gadus morhua\", \"Passive schooling fish\",\n                    \"TBD\""));
        assertFalse(speciesInfo.contains("atlantic_salmon\", species(\"Salmo salar\", \"Passive schooling fish\",\n                    \"TBD\""));
    }

    @Test
    void vanillaFishReplacementContractIsExplicit() throws IOException {
        String config = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/config/BfsConfig.java"));
        String manager = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/spawn/MobCapManager.java"));
        String policy = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/spawn/VanillaFishReplacementPolicy.java"));
        String categories = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/init/ModMobCategories.java"));
        String platform = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/platform/ForgePlatformHelper.java"));

        assertTrue(config.contains(".worldRestart()\n                    .define(\"replace_vanilla_mobs\", true)"));
        assertTrue(config.contains(".worldRestart()\n                    .define(\"fish_entities\", true)"));
        assertTrue(config.contains(".worldRestart()\n                    .define(\"disable_vanilla_aquatic_spawns\", false)"));
        assertTrue(policy.contains("if (!\"minecraft\".equals(namespace)) {\n            return null;\n        }"));
        assertTrue(policy.contains("case \"cod\" -> Replacement.ATLANTIC_COD"));
        assertTrue(policy.contains("case \"salmon\" -> Replacement.ATLANTIC_SALMON"));
        assertTrue(policy.contains("case SPAWN_EGG, COMMAND, BUCKET, DISPENSER, SPAWNER, STRUCTURE -> true"));
        assertTrue(manager.contains("MobSpawnType.NATURAL"));
        assertTrue(manager.contains("MobSpawnType.CHUNK_GENERATION"));
        assertTrue(manager.contains("replacesEntityJoinSource(reason)"));
        assertTrue(manager.contains("REPLACING_VANILLA_FISH.get()"));
        assertTrue(manager.contains("copySafeSpawnState(original, replacement, event.getSpawnTag())"));
        assertTrue(manager.contains("data.remove(\"Passengers\")"));
        assertTrue(manager.contains("data.remove(\"Leash\")"));
        assertTrue(manager.indexOf("replaceNaturalFish(event)")
                < manager.indexOf("disableVanillaAquaticSpawns.get()"));
        assertTrue(manager.contains("REPLACEMENT_CATEGORY_ERROR_REPORTED.compareAndSet(false, true)"));
        assertTrue(manager.contains("event.setSpawnCancelled(true)"));
        assertTrue(categories.contains("BFS_WATER_AMBIENT"));
        assertTrue(platform.contains("MobCategory.WATER_AMBIENT"));
        assertTrue(platform.contains("registerAtlanticCod"));
        assertTrue(platform.contains("registerAtlanticSalmon"));

        Path modifierDir = GENERATED.resolve("data/bensfintasticsharks/forge/biome_modifier");
        assertTrue(Files.exists(modifierDir.resolve("atlantic_cod_spawns.json")));
        assertTrue(Files.exists(modifierDir.resolve("atlantic_salmon_spawns.json")));
    }

    @Test
    void fishItemRecipeLootAndCreativeContractsAreComplete() throws IOException {
        String items = Files.readString(ROOT.resolve(
                "common/src/main/java/tfar/bensfintasticsharks/init/ModItems.java"));
        String models = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/datagen/ModItemModelProvider.java"));
        String creative = Files.readString(ROOT.resolve(
                "common/src/main/java/tfar/bensfintasticsharks/init/ModCreativeTabs.java"));
        String entityLoot = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/datagen/data/loot/ModEntityLoot.java"));
        String fishingLoot = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/datagen/data/ModGlobalLootModifierProvider.java"));
        String fishingPolicy = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/fishing/FishingCatchPolicy.java"));
        String recipes = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/datagen/data/ModRecipeProvider.java"));

        Path itemTextures = SOURCE_ASSETS.resolve("textures/item");
        for (String item : List.of("atlantic_cod_spawn_egg", "atlantic_salmon_spawn_egg",
                "raw_atlantic_cod", "cooked_atlantic_cod", "raw_atlantic_salmon", "cooked_atlantic_salmon")) {
            assertTexture(itemTextures.resolve(item + ".png"), 16, 16);
            assertTrue(Files.exists(GENERATED.resolve("assets/bensfintasticsharks/models/item/" + item + ".json")), item);
            String field = switch (item) {
                case "atlantic_cod_spawn_egg" -> "ATLANTIC_COD_SPAWN_EGG";
                case "atlantic_salmon_spawn_egg" -> "ATLANTIC_SALMON_SPAWN_EGG";
                case "raw_atlantic_cod" -> "RAW_ATLANTIC_COD";
                case "cooked_atlantic_cod" -> "COOKED_ATLANTIC_COD";
                case "raw_atlantic_salmon" -> "RAW_ATLANTIC_SALMON";
                case "cooked_atlantic_salmon" -> "COOKED_ATLANTIC_SALMON";
                default -> throw new IllegalStateException(item);
            };
            assertTrue(models.contains("ModItems." + field), item);
        }
        for (String item : List.of("ATLANTIC_COD_SPAWN_EGG", "ATLANTIC_SALMON_SPAWN_EGG",
                "RAW_ATLANTIC_COD", "COOKED_ATLANTIC_COD", "RAW_ATLANTIC_SALMON", "COOKED_ATLANTIC_SALMON")) {
            assertTrue(items.contains("public static final Item " + item), item);
        }
        assertTrue(creative.contains("!(item instanceof HiddenItem)"));
        assertTrue(entityLoot.contains("SmeltItemFunction.smelted().when(onFire())"));
        assertTrue(entityLoot.contains("ModItems.RAW_ATLANTIC_COD"));
        assertTrue(entityLoot.contains("ModItems.RAW_ATLANTIC_SALMON"));
        assertTrue(fishingLoot.contains("replace_fishing_fish"));
        assertFalse(fishingLoot.contains("add_atlantic_cod_fishing"));
        assertFalse(fishingLoot.contains("add_atlantic_salmon_fishing"));
        assertTrue(fishingPolicy.contains("ATLANTIC_SELECTION_SHARE = 0.25F"));
        assertTrue(fishingPolicy.contains("drops.size() != 1"));
        assertTrue(recipes.contains("ModItems.RAW_ATLANTIC_COD"));
        assertTrue(recipes.contains("ModItems.COOKED_ATLANTIC_COD"));
        assertTrue(recipes.contains("ModItems.RAW_ATLANTIC_SALMON"));
        assertTrue(recipes.contains("ModItems.COOKED_ATLANTIC_SALMON"));
        assertTrue(recipes.contains("SimpleCookingRecipeBuilder.smelting"));
        assertTrue(recipes.contains("SimpleCookingRecipeBuilder.smoking"));

        JsonObject fishingModifier = readJson(GENERATED.resolve(
                "data/bensfintasticsharks/loot_modifiers/replace_fishing_fish.json"));
        assertEquals("bensfintasticsharks:replace_fishing_fish", fishingModifier.get("type").getAsString());

        for (String fish : List.of("atlantic_cod", "atlantic_salmon")) {
            String entityLootJson = Files.readString(GENERATED.resolve(
                    "data/bensfintasticsharks/loot_tables/entities/" + fish + ".json"));
            assertTrue(entityLootJson.contains("raw_" + fish), fish);
            assertTrue(entityLootJson.contains("minecraft:furnace_smelt"), fish);
            for (String station : List.of("smelting", "smoking")) {
                JsonObject recipe = readJson(GENERATED.resolve(
                        "data/bensfintasticsharks/recipes/cooked_" + fish + "_from_" + station + ".json"));
                assertEquals("bensfintasticsharks:raw_" + fish,
                        recipe.getAsJsonObject("ingredient").get("item").getAsString(), fish);
                assertEquals("bensfintasticsharks:cooked_" + fish, recipe.get("result").getAsString(), fish);
                assertTrue(recipe.get("cookingtime").getAsInt() > 0, fish);
            }
        }
    }

    private static int countOccurrences(String text, String needle) {
        int count = 0;
        int offset = 0;
        while ((offset = text.indexOf(needle, offset)) >= 0) {
            count++;
            offset += needle.length();
        }
        return count;
    }

    @Test
    void showcaseCardsAndOceanicHabitatBindToAuthoritativeSources() throws IOException {
        String speciesInfo = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/command/BfsSpeciesInfo.java"));
        String commands = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/command/BfsCommands.java"));
        String biomeProvider = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/datagen/data/tags/ModBiomeTagsProvider.java"));

        List<String> retainedTbd = List.of(
                "orca", "bottlenose_dolphin", "common_octopus", "caribbean_reef_octopus", "nautilus",
                "giant_moray_eel", "green_sea_turtle", "american_lobster", "common_stingray", "harbor_seal",
                "black_sea_nettle_jellyfish", "cannonball_jellyfish");
        for (String id : retainedTbd) {
            assertTrue(speciesInfo.contains("Map.entry(\"" + id + "\", species("), id);
        }
        assertEquals(12, retainedTbd.size());
        assertTrue(speciesInfo.contains("\"TBD\""));
        assertFalse(speciesInfo.contains("VANILLA_REPLACEMENT_HABITATS"));
        assertTrue(speciesInfo.contains("getMobSettings().getMobs"));
        assertTrue(speciesInfo.contains("EntityType.COD"));
        assertTrue(speciesInfo.contains("EntityType.SALMON"));

        assertTrue(commands.contains("\"Scientific name\""));
        assertTrue(commands.contains("\"Habitats\""));
        assertTrue(commands.contains("\"Behavior\""));
        assertTrue(commands.contains("\"Diet\""));
        assertTrue(commands.contains("\"Health\""));
        assertTrue(commands.contains("\"Variants\""));
        assertTrue(commands.contains("\"Registry ID\""));
        assertTrue(commands.contains("\"Spawn category\""));
        assertTrue(commands.contains("\"Natural spawning\""));
        assertTrue(commands.contains("\"Natural cap\""));

        assertTrue(biomeProvider.contains(
                "tag(ModTags.Biomes.OCEANIC_WHITETIP_SHARK_SPAWNS).add(Biomes.DEEP_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN)"));
        assertFalse(biomeProvider.contains("OCEANIC_WHITETIP_SHARK_SPAWNS).add(Biomes.DEEP_COLD_OCEAN"));

        JsonObject oceanic = readJson(GENERATED.resolve(
                "data/bensfintasticsharks/tags/worldgen/biome/oceanic_whitetip_shark_spawns.json"));
        assertEquals(List.of("minecraft:deep_ocean", "minecraft:deep_lukewarm_ocean"),
                stringValues(oceanic.getAsJsonArray("values")));
    }

    @Test
    void everyLivingSpeciesHasActionAndPresentationInventoryEntries() throws IOException {
        assertEquals(22, LIVING_SPECIES.size());
        String entityTypes = Files.readString(ROOT.resolve(
                "common/src/main/java/tfar/bensfintasticsharks/init/ModEntityTypes.java"));
        String speciesInfo = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/command/BfsSpeciesInfo.java"));
        String rendererRegistration = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/client/ModClientForge.java"));

        for (SpeciesPresentation species : LIVING_SPECIES) {
            assertTrue(entityTypes.contains(" " + species.entityField() + " ="), species.id());
            assertTrue(speciesInfo.contains("Map.entry(\"" + species.id() + "\""), species.id());
            assertTrue(rendererRegistration.contains("ModEntityTypes." + species.entityField()), species.id());
        }
    }

    @Test
    void grabbersUseAuthoritativeBoundedCleanup() throws IOException {
        for (String file : List.of("GreatWhiteSharkEntity.java", "GreatHammerheadSharkEntity.java",
                "ShortfinMakoSharkEntity.java", "OceanicWhitetipSharkEntity.java",
                "BlacktipReefSharkEntity.java")) {
            String source = Files.readString(ROOT.resolve("common/src/main/java/tfar/bensfintasticsharks/entity/" + file));
            assertTrue(source.contains("startRiding(this, true)"), file);
            assertTrue(source.contains("isInWaterOrBubble()"), file);
            assertTrue(source.contains("new ClientboundSetPassengersPacket(this)"), file);
            assertTrue(source.contains("public void remove(RemovalReason reason)"), file);
            assertTrue(source.contains("ejectPassengers()"), file);
        }
        String blacktip = Files.readString(ROOT.resolve(
                "common/src/main/java/tfar/bensfintasticsharks/entity/BlacktipReefSharkEntity.java"));
        assertTrue(blacktip.contains("implements BfsVariantHolder, SharkGrabber"));
        String config = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/config/BfsConfig.java"));
        assertEquals(3, config.split("\\.worldRestart\\(\\)", -1).length - 1);
    }

    @Test
    void permanentAlgaeResourcesAreCompleteAndBounded() throws IOException {
        Path textures = SOURCE_ASSETS.resolve("textures/block");
        assertTexture(textures.resolve("algae_block.png"), 16, 16);
        assertTexture(textures.resolve("large_green_algae.png"), 16, 160);
        assertTexture(textures.resolve("large_red_algae.png"), 16, 144);

        assertAnimationMetadata(textures.resolve("large_green_algae.png.mcmeta"), 10);
        assertAnimationMetadata(textures.resolve("large_red_algae.png.mcmeta"), 9);

        for (String id : List.of("algae_block", "large_green_algae", "large_red_algae")) {
            assertTrue(Files.exists(GENERATED.resolve("assets/bensfintasticsharks/blockstates/" + id + ".json")), id);
            JsonObject model = readJson(GENERATED.resolve("assets/bensfintasticsharks/models/block/" + id + ".json"));
            assertEquals("minecraft:cutout", model.get("render_type").getAsString(), id);
            assertTrue(Files.exists(GENERATED.resolve("data/bensfintasticsharks/loot_tables/blocks/" + id + ".json")), id);
            assertTrue(Files.exists(GENERATED.resolve("data/bensfintasticsharks/worldgen/configured_feature/" + id + ".json")), id);
            assertTrue(Files.exists(GENERATED.resolve("data/bensfintasticsharks/worldgen/placed_feature/" + id + ".json")), id);
            JsonObject placement = readJson(GENERATED.resolve("data/bensfintasticsharks/worldgen/placed_feature/" + id + ".json"));
            JsonArray placements = placement.getAsJsonArray("placement");
            assertEquals(4, placements.size(), id);
            assertEquals(3, placements.get(0).getAsJsonObject().get("chance").getAsInt(), id);
            JsonObject height = placements.get(2).getAsJsonObject().getAsJsonObject("height");
            assertEquals(20, height.getAsJsonObject("min_inclusive").get("absolute").getAsInt(), id);
            assertEquals(62, height.getAsJsonObject("max_inclusive").get("absolute").getAsInt(), id);
        }

        JsonObject blocks = readJson(GENERATED.resolve("data/bensfintasticsharks/tags/blocks/algae.json"));
        assertEquals(List.of(
                "bensfintasticsharks:algae_block",
                "bensfintasticsharks:large_green_algae",
                "bensfintasticsharks:large_red_algae"
        ), stringValues(blocks.getAsJsonArray("values")));
        JsonObject biomes = readJson(GENERATED.resolve("data/bensfintasticsharks/tags/worldgen/biome/algae_spawns.json"));
        assertEquals(9, biomes.getAsJsonArray("values").size());
    }

    @Test
    void prismarineArmorGeometryAndLivePoseBindingAreComplete() throws IOException {
        JsonObject geometry = readJson(SOURCE_ASSETS.resolve("geo/item/armor/prismarine_armor.geo.json"));
        Map<String, Integer> cubeCounts = new LinkedHashMap<>();
        Set<String> bones = new HashSet<>();
        for (JsonElement geometryEntry : geometry.getAsJsonArray("minecraft:geometry")) {
            for (JsonElement boneElement : geometryEntry.getAsJsonObject().getAsJsonArray("bones")) {
                JsonObject bone = boneElement.getAsJsonObject();
                String name = bone.get("name").getAsString();
                bones.add(name);
                cubeCounts.put(name, bone.has("cubes") ? bone.getAsJsonArray("cubes").size() : 0);
            }
        }
        assertTrue(bones.containsAll(Set.of("bipedHead", "armorHead", "bipedBody", "armorBody",
                "bipedRightArm", "armorRightArm", "bipedLeftArm", "armorLeftArm", "bipedLeftLeg",
                "armorLeftLeg", "armorLeftBoot", "bipedRightLeg", "armorRightLeg", "armorRightBoot")));
        assertEquals(5, cubeCounts.get("armorHead"));
        assertEquals(5, cubeCounts.get("armorBody"));
        assertEquals(3, cubeCounts.get("armorRightArm"));
        assertEquals(3, cubeCounts.get("armorLeftArm"));
        assertEquals(2, cubeCounts.get("armorLeftLeg"));
        assertEquals(2, cubeCounts.get("armorRightLeg"));
        assertEquals(3, cubeCounts.get("armorLeftBoot"));
        assertEquals(3, cubeCounts.get("armorRightBoot"));

        String armorItem = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/item/PrismarineArmorItem.java"));
        String armorModel = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/client/model/PrismarineArmorModel.java"));
        String armorRenderer = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/client/renderer/PrismarineArmorRenderer.java"));
        assertTrue(armorItem.contains("livingRenderer.getModel()"));
        assertTrue(armorItem.contains("this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, poseSource)"));
        assertTrue(armorModel.contains("armor/prismarine_armor"));
        assertTrue(armorRenderer.contains("PrismarineArmorModel"));
    }

    private static void assertClipHasFivePointMotion(String fileName, String clipName) throws IOException {
        JsonObject animations = readJson(SOURCE_ASSETS.resolve("animations/entity/" + fileName)).getAsJsonObject("animations");
        assertTrue(animations.has(clipName), clipName);
        JsonObject clip = animations.getAsJsonObject(clipName);
        double length = clip.get("animation_length").getAsDouble();
        assertTrue(length > 0.0D, clipName);

        List<String> sampledChannels = new ArrayList<>();
        for (Map.Entry<String, JsonElement> boneEntry : clip.getAsJsonObject("bones").entrySet()) {
            for (Map.Entry<String, JsonElement> channelEntry : boneEntry.getValue().getAsJsonObject().entrySet()) {
                List<List<Double>> samples = new ArrayList<>();
                boolean complete = true;
                for (double normalizedTime : NORMALIZED_SAMPLE_POINTS) {
                    Optional<List<Double>> sample = sampleKeyframedVector(channelEntry.getValue(), length * normalizedTime);
                    if (sample.isEmpty()) {
                        complete = false;
                        break;
                    }
                    samples.add(sample.get());
                }
                if (complete && hasDistinctVectors(samples)) {
                    sampledChannels.add(boneEntry.getKey() + "." + channelEntry.getKey() + "=" + samples);
                }
            }
        }
        assertFalse(sampledChannels.isEmpty(), clipName + " has no moving transform at start, quarter, half, three-quarter, and end");
    }

    private static Optional<List<Double>> sampleKeyframedVector(JsonElement channel, double time) {
        if (!channel.isJsonObject()) return Optional.empty();
        List<Keyframe> keyframes = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : channel.getAsJsonObject().entrySet()) {
            try {
                Optional<List<Double>> vector = readVector(entry.getValue());
                if (vector.isPresent()) keyframes.add(new Keyframe(Double.parseDouble(entry.getKey()), vector.get()));
            } catch (NumberFormatException ignored) {
            }
        }
        keyframes.sort(Comparator.comparingDouble(Keyframe::time));
        if (keyframes.isEmpty() || time < keyframes.get(0).time() - TRANSFORM_TOLERANCE
                || time > keyframes.get(keyframes.size() - 1).time() + TRANSFORM_TOLERANCE) return Optional.empty();

        Keyframe previous = keyframes.get(0);
        for (Keyframe current : keyframes) {
            if (Math.abs(time - current.time()) <= TRANSFORM_TOLERANCE) return Optional.of(current.vector());
            if (current.time() > time) return Optional.of(interpolate(previous, current, time));
            previous = current;
        }
        return Optional.of(previous.vector());
    }

    private static Optional<List<Double>> readVector(JsonElement frame) {
        JsonElement vector = frame;
        if (frame.isJsonObject()) {
            JsonObject object = frame.getAsJsonObject();
            vector = object.has("post") ? object.getAsJsonObject("post").get("vector") : object.get("vector");
        }
        if (vector == null || !vector.isJsonArray()) return Optional.empty();
        List<Double> values = new ArrayList<>();
        for (JsonElement component : vector.getAsJsonArray()) {
            if (!component.isJsonPrimitive() || !component.getAsJsonPrimitive().isNumber()) return Optional.empty();
            values.add(component.getAsDouble());
        }
        return values.isEmpty() ? Optional.empty() : Optional.of(values);
    }

    private static List<Double> interpolate(Keyframe start, Keyframe end, double time) {
        assertEquals(start.vector().size(), end.vector().size());
        double fraction = (time - start.time()) / (end.time() - start.time());
        List<Double> result = new ArrayList<>();
        for (int index = 0; index < start.vector().size(); index++) {
            result.add(start.vector().get(index) + (end.vector().get(index) - start.vector().get(index)) * fraction);
        }
        return result;
    }

    private static boolean hasDistinctVectors(List<List<Double>> samples) {
        for (List<Double> first : samples) {
            for (List<Double> second : samples) {
                if (first.size() != second.size()) continue;
                for (int index = 0; index < first.size(); index++) {
                    if (Math.abs(first.get(index) - second.get(index)) > TRANSFORM_TOLERANCE) return true;
                }
            }
        }
        return false;
    }

    private record Keyframe(double time, List<Double> vector) {
    }

    private record SpeciesPresentation(String id, String entityField) {
    }

    private static void assertTexture(Path path, int width, int height) throws IOException {
        BufferedImage image = ImageIO.read(path.toFile());
        assertNotNull(image, path.toString());
        assertEquals(width, image.getWidth(), path.toString());
        assertEquals(height, image.getHeight(), path.toString());
    }

    private static void assertAnimationMetadata(Path path, int frameCount) throws IOException {
        JsonObject animation = readJson(path).getAsJsonObject("animation");
        assertEquals(4, animation.get("frametime").getAsInt());
        JsonArray frames = animation.getAsJsonArray("frames");
        assertEquals(frameCount, frames.size());
        for (int index = 0; index < frameCount; index++) assertEquals(index, frames.get(index).getAsInt());
    }

    private static List<String> stringValues(JsonArray array) {
        return array.asList().stream().map(JsonElement::getAsString).collect(Collectors.toList());
    }

    private static boolean reachesRoot(String id, Map<String, JsonObject> advancements, Set<String> visited) {
        if (!visited.add(id)) return false;
        JsonElement parent = advancements.get(id).get("parent");
        if (parent == null) return "root".equals(id);
        if (!parent.isJsonPrimitive() || !parent.getAsString().startsWith("bensfintasticsharks:")) return false;
        String parentId = parent.getAsString().substring("bensfintasticsharks:".length());
        return advancements.containsKey(parentId) && reachesRoot(parentId, advancements, visited);
    }

    private static String semanticDigest(JsonObject advancement) {
        JsonObject semantic = JsonParser.parseString(advancement.toString()).getAsJsonObject();
        semantic.remove("parent");
        return sha256Bytes(canonicalJson(semantic).getBytes(StandardCharsets.UTF_8));
    }

    private static String canonicalJson(JsonElement element) {
        if (element.isJsonObject()) {
            return element.getAsJsonObject().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> quote(entry.getKey()) + ":" + canonicalJson(entry.getValue()))
                    .collect(Collectors.joining(",", "{", "}"));
        }
        if (element.isJsonArray()) {
            return element.getAsJsonArray().asList().stream()
                    .map(ReleaseContractAuditTest::canonicalJson)
                    .collect(Collectors.joining(",", "[", "]"));
        }
        return element.toString();
    }

    private static String quote(String value) {
        return new com.google.gson.JsonPrimitive(value).toString();
    }

    private static boolean hasCycle(String id, Map<String, JsonObject> advancements, Set<String> visiting, Set<String> visited) {
        if (visited.contains(id)) return false;
        if (!visiting.add(id)) return true;
        JsonElement parent = advancements.get(id).get("parent");
        if (parent != null && parent.isJsonPrimitive() && parent.getAsString().startsWith("bensfintasticsharks:")) {
            String parentId = parent.getAsString().substring("bensfintasticsharks:".length());
            if (hasCycle(parentId, advancements, visiting, visited)) return true;
        }
        visiting.remove(id);
        visited.add(id);
        return false;
    }

    private static JsonObject readJson(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static String sha256(Path path) throws IOException {
        try {
            return digestHex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(path)));
        } catch (NoSuchAlgorithmException exception) {
            throw new AssertionError(exception);
        }
    }

    private static String digestHex(byte[] digest) {
        StringBuilder result = new StringBuilder(digest.length * 2);
        for (byte value : digest) result.append(String.format("%02x", value));
        return result.toString();
    }

    private static String sha256Bytes(byte[] bytes) {
        try {
            return digestHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException exception) {
            throw new AssertionError(exception);
        }
    }

    private static Path findProjectRoot() {
        Path candidate = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        for (int depth = 0; depth < 8 && candidate != null; depth++, candidate = candidate.getParent()) {
            if (Files.isDirectory(candidate.resolve("common/src/generated/resources"))) return candidate;
        }
        throw new IllegalStateException("could not locate the project root");
    }
}
