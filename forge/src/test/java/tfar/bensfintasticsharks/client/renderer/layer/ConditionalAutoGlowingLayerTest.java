package tfar.bensfintasticsharks.client.renderer.layer;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConditionalAutoGlowingLayerTest {
    @Test
    void keepsTheAuthoredBasePathAndAddsTheGlowmaskSuffix() {
        ResourceLocation base = new ResourceLocation("bensfintasticsharks",
                "textures/entity/common_thresher_shark/zippy.png");
        assertEquals(new ResourceLocation("bensfintasticsharks",
                        "textures/entity/common_thresher_shark/zippy_glowmask.png"),
                ConditionalAutoGlowingLayer.glowMask(base));
    }
}
