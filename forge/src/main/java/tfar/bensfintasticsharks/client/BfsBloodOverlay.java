package tfar.bensfintasticsharks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.bensfintasticsharks.gore.BfsClientGoreConfig;

/** Client-only short-lived water blood tint and bite vignette. */
public final class BfsBloodOverlay {
    private static final long WATER_TINT_TICKS = 80L;
    private static final long BITE_TINT_TICKS = 40L;
    private static final double WATER_TINT_RADIUS_SQR = 14.0D * 14.0D;

    private static ResourceKey<Level> dimension;
    private static Vec3 waterBloodPosition;
    private static long waterTintEndsAt;
    private static long biteTintEndsAt;

    public static boolean recordImpact(Level level, int targetEntityId, double x, double y, double z) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!BfsClientGoreConfig.enabled() || minecraft.player == null) return false;
        long tick = level.getGameTime();
        if (minecraft.player.getId() == targetEntityId) {
            biteTintEndsAt = tick + BITE_TINT_TICKS;
            dimension = level.dimension();
        }

        BlockPos impact = BlockPos.containing(x, y, z);
        if (!level.getFluidState(impact).is(FluidTags.WATER)) return false;
        waterBloodPosition = new Vec3(x, y, z);
        waterTintEndsAt = tick + WATER_TINT_TICKS;
        dimension = level.dimension();
        return true;
    }

    @SubscribeEvent
    public void render(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            clear();
            return;
        }
        if (!BfsClientGoreConfig.enabled()) {
            clear();
            return;
        }

        long tick = minecraft.level.getGameTime();
        if (dimension != null && !dimension.equals(minecraft.level.dimension())) {
            clear();
            return;
        }
        var graphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        if (waterBloodPosition != null && minecraft.player.isInWater()
                && tick < waterTintEndsAt
                && minecraft.player.position().distanceToSqr(waterBloodPosition) <= WATER_TINT_RADIUS_SQR) {
            float remaining = (waterTintEndsAt - tick) / (float) WATER_TINT_TICKS;
            int alpha = Math.round(30.0F * remaining);
            graphics.fill(0, 0, width, height, color(alpha, 0xB60D20));
        }

        if (tick < biteTintEndsAt) {
            float remaining = (biteTintEndsAt - tick) / (float) BITE_TINT_TICKS;
            drawEdgeVignette(graphics, width, height, remaining);
        }
    }

    private static void drawEdgeVignette(net.minecraft.client.gui.GuiGraphics graphics,
                                          int width, int height, float intensity) {
        int bands = 14;
        int thickness = Math.min(128, Math.min(width, height) / 7);
        int bandWidth = Math.max(1, thickness / bands);
        for (int band = 0; band < bands; band++) {
            float edge = 1.0F - band / (float) bands;
            int alpha = Math.round(150.0F * intensity * edge * edge);
            int inset = band * bandWidth;
            int outer = Math.min(thickness, inset + bandWidth + 1);
            int red = color(alpha, 0xA90019);
            graphics.fill(inset, 0, outer, height, red);
            graphics.fill(width - outer, 0, width - inset, height, red);
            graphics.fill(0, inset, width, outer, red);
            graphics.fill(0, height - outer, width, height - inset, red);
        }
    }

    private static int color(int alpha, int rgb) {
        return (Math.max(0, Math.min(255, alpha)) << 24) | (rgb & 0x00FFFFFF);
    }

    private static void clear() {
        dimension = null;
        waterBloodPosition = null;
        waterTintEndsAt = 0L;
        biteTintEndsAt = 0L;
    }

    @SubscribeEvent
    public void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        clear();
    }
}
