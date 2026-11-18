package tfar.bensfintasticsharks.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiveArmorGeometryTest {
    @Test
    void helmetEnclosesHeadAndBootsRotateAtTheKnee() throws Exception {
        Path geometry = Path.of("../common/src/main/resources/assets/bensfintasticsharks/geo/item/armor/dive_armor.geo.json");
        JsonObject model = JsonParser.parseString(Files.readString(geometry)).getAsJsonObject()
                .getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject();
        int boots = 0;
        boolean helmet = false;
        for (var element : model.getAsJsonArray("bones")) {
            JsonObject bone = element.getAsJsonObject();
            String name = bone.get("name").getAsString();
            if (name.endsWith("Boot")) {
                assertEquals(12, bone.getAsJsonArray("pivot").get(1).getAsInt());
                boots++;
            }
            if (name.equals("armorHead")) {
                JsonObject shell = bone.getAsJsonArray("cubes").get(0).getAsJsonObject();
                JsonArray origin = shell.getAsJsonArray("origin");
                double bottom = origin.get(1).getAsDouble();
                double top = bottom + shell.getAsJsonArray("size").get(1).getAsDouble();
                assertTrue(bottom < 24 && bottom >= 23.5);
                assertTrue(top > 32 && top <= 32.5);
                helmet = true;
            }
        }
        assertEquals(2, boots);
        assertTrue(helmet);
    }
}
