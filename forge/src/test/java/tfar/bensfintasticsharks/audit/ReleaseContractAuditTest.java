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

    @Test
    void suppliedAdvancementCopyAndPunctuationAreStable() throws IOException {
        JsonObject language = readJson(GENERATED.resolve("assets/bensfintasticsharks/lang/en_us.json"));
        assertEquals("Obtain Capitán Ben's Hat.", language.get("advancements.bensfintasticsharks.captains_heir.description").getAsString());

        Map<String, String> titles = Map.of(
                "harbor_seal_encounter", "Awkward...",
                "albino_encounter", "It's a shiny!",
                "sharks_galore", "Sharks Galore!",
                "sleeping_with_the_fishes", "Sleeping with the fishes.",
                "specimen_8_encounter", "I'll be back",
                "deep_blue_encounter", "Mommy Shark.",
                "zippy_encounter", "THUNDER BRINGER!"
        );
        for (Map.Entry<String, String> entry : titles.entrySet()) {
            String key = "advancements.bensfintasticsharks." + entry.getKey() + ".title";
            assertEquals(entry.getValue(), language.get(key).getAsString(), entry.getKey());
        }
        assertFalse(language.has("advancements.bensfintasticsharks.shark_whisperer.title"));
        assertFalse(language.has("advancements.bensfintasticsharks.shark_whisperer.description"));

        Path advancementDir = GENERATED.resolve("data/bensfintasticsharks/advancements");
        try (var paths = Files.list(advancementDir)) {
            for (Path path : paths.filter(p -> p.getFileName().toString().endsWith(".json")).toList()) {
                JsonObject advancement = readJson(path);
                JsonObject description = advancement.getAsJsonObject("display").getAsJsonObject("description");
                String text = language.get(description.get("translate").getAsString()).getAsString();
                assertEquals(text.trim(), text, path.getFileName().toString());
                assertTrue(text.matches(".*[.!?]$"), path.getFileName().toString());
                assertFalse(text.matches(".*[.!?]{2,}$"), path.getFileName().toString());
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
        assertTrue(forgeSource.contains("if (!this.getPassengers().isEmpty())"));
    }

    @Test
    void retainedEntityAndSpawnContractsAreExplicit() throws IOException {
        String cod = Files.readString(ROOT.resolve(
                "common/src/main/java/tfar/bensfintasticsharks/entity/AtlanticCodEntity.java"));
        String salmon = Files.readString(ROOT.resolve(
                "common/src/main/java/tfar/bensfintasticsharks/entity/AtlanticSalmonEntity.java"));
        String salmonForge = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/entity/AtlanticSalmonEntityForge.java"));
        assertTrue(cod.contains("extends Cod"));
        assertTrue(salmon.contains("extends Salmon"));
        assertTrue(salmon.contains("\"Spin\".equals(getCustomName().getString())"));
        assertTrue(salmonForge.contains("if (isNamedSpin())"));
        assertTrue(salmonForge.contains("animation.atlantic_salmon.spin"));

        Path resources = GENERATED.resolve("data/bensfintasticsharks");
        for (String fish : List.of("atlantic_cod", "atlantic_salmon")) {
            assertTrue(Files.exists(resources.resolve("loot_tables/entities/" + fish + ".json")), fish);
            assertTrue(Files.exists(resources.resolve("recipes/cooked_" + fish + "_from_smelting.json")), fish);
            assertTrue(Files.exists(resources.resolve("recipes/cooked_" + fish + "_from_smoking.json")), fish);
            assertTrue(Files.exists(resources.resolve("loot_modifiers/add_" + fish + "_fishing.json")), fish);
        }
        String speciesInfo = Files.readString(ROOT.resolve(
                "forge/src/main/java/tfar/bensfintasticsharks/command/BfsSpeciesInfo.java"));
        assertTrue(speciesInfo.contains("atlantic_cod\", species(\"Gadus morhua\""));
        assertTrue(speciesInfo.contains("atlantic_salmon\", species(\"Salmo salar\""));
        assertFalse(speciesInfo.contains("atlantic_cod\", species(\"Gadus morhua\", \"Passive schooling fish\",\n                    \"TBD\""));
        assertFalse(speciesInfo.contains("atlantic_salmon\", species(\"Salmo salar\", \"Passive schooling fish\",\n                    \"TBD\""));
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
        assertEquals(2, config.split("\\.worldRestart\\(\\)", -1).length - 1);
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
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(path));
            StringBuilder result = new StringBuilder(digest.length * 2);
            for (byte value : digest) result.append(String.format("%02x", value));
            return result.toString();
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
