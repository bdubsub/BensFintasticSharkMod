package tfar.bensfintasticsharks.gore;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GoreTexturePixelGridTest {
    private static final String TEXTURE_DIRECTORY = "/assets/bensfintasticsharks/textures/particle/";
    private static final Map<String, SourceSprite> SOURCE_SPRITES = Map.of(
            "blood_drop.png", new SourceSprite(7, 7, new String[]{".A", "BC"}, Map.of(
                    'A', 0xFF9E120C, 'B', 0xFFE3D4C5, 'C', 0xFFBD3933)),
            "blood_splatter.png", new SourceSprite(4, 5, new String[]{
                    ".AB....", "ABCDB..", "BCEFDB.", ".ACECCA", "..AC.A."
            }, Map.of(
                    'A', 0xFFBD3933, 'B', 0xFFD9504A, 'C', 0xFF9E120C,
                    'D', 0xFF680414, 'E', 0xFFE3D4C5, 'F', 0xFFEFE8E1)),
            "blood_splatter_dark.png", new SourceSprite(4, 5, new String[]{
                    ".AB....", "ACCAD..", "DACCAD.", ".BBCCCA", "..AC.A."
            }, Map.of(
                    'A', 0xFF680414, 'B', 0xFF87080D, 'C', 0xFF320209, 'D', 0xFF9E120C)),
            "blood_tissue.png", new SourceSprite(4, 5, new String[]{
                    ".AB....", "ACCAD..", "DAEFAD.", ".BAECCA", "..AC.A."
            }, Map.of(
                    'A', 0xFF680414, 'B', 0xFF87080D, 'C', 0xFF320209,
                    'D', 0xFF9E120C, 'E', 0xFFE3D4C5, 'F', 0xFFD1BEAA)),
            "blood_wound.png", new SourceSprite(6, 6, new String[]{
                    "..AB", ".CDC", "ADE.", "E.DD"
            }, Map.of(
                    'A', 0xFF87080D, 'B', 0xFFBD3933, 'C', 0xFF9E120C,
                    'D', 0xFF320209, 'E', 0xFF680414))
    );

    @Test
    void goreTexturesPreserveTheSourcePixelsAndHardTransparency() throws IOException {
        for (Map.Entry<String, SourceSprite> entry : SOURCE_SPRITES.entrySet()) {
            String textureName = entry.getKey();
            SourceSprite source = entry.getValue();
            try (InputStream stream = getClass().getResourceAsStream(TEXTURE_DIRECTORY + textureName)) {
                assertNotNull(stream, textureName + " must be available on the test classpath");
                BufferedImage texture = ImageIO.read(stream);
                assertNotNull(texture, textureName + " must be a readable PNG");
                assertEquals(16, texture.getWidth(), textureName + " must use a 16-pixel canvas");
                assertEquals(16, texture.getHeight(), textureName + " must use a 16-pixel canvas");

                for (int y = 0; y < texture.getHeight(); y++) {
                    for (int x = 0; x < texture.getWidth(); x++) {
                        char expected = source.pixelAt(x, y);
                        int actual = texture.getRGB(x, y);
                        if (expected == '.') {
                            assertEquals(0, actual >>> 24,
                                    textureName + " must keep the checkerboard and canvas padding transparent");
                        } else {
                            assertEquals(source.colors().get(expected).intValue(), actual,
                                    textureName + " must preserve the source texel at " + x + "," + y);
                        }
                    }
                }
            }
        }
    }

    private record SourceSprite(int x, int y, String[] rows, Map<Character, Integer> colors) {
        private char pixelAt(int pixelX, int pixelY) {
            int row = pixelY - y;
            int column = pixelX - x;
            if (row < 0 || row >= rows.length || column < 0 || column >= rows[row].length()) {
                return '.';
            }
            return rows[row].charAt(column);
        }
    }
}
