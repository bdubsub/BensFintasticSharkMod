package tfar.bensfintasticsharks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.bensfintasticsharks.dive.DiveOxygenManager;
import tfar.bensfintasticsharks.dive.DiveSuitEligibility;

/** Draws the local player's full-suit oxygen reserve as a compact, per-tick bar. */
public final class DiveOxygenHud {

    @SubscribeEvent
    public void render(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null
                || !DiveSuitEligibility.evaluate(minecraft.player).fullSuit()) return;

        DiveClientState.Snapshot snapshot = DiveClientState.snapshot();
        int remaining = snapshot.revision() < 0
                ? DiveOxygenManager.MAX_RESERVE_TICKS
                : snapshot.estimatedRemainingTicks(minecraft.level.getGameTime());
        float ratio = remaining / (float) DiveOxygenManager.MAX_RESERVE_TICKS;
        int width = 180;
        int height = 7;
        int x = (event.getWindow().getGuiScaledWidth() - width) / 2;
        int y = event.getWindow().getGuiScaledHeight() - 42;
        var graphics = event.getGuiGraphics();
        graphics.drawCenteredString(minecraft.font, Component.translatable("hud.bensfintasticsharks.oxygen_label"),
                x + width / 2, y - 11, 0xFFE8F5FF);
        graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0xB0000000);
        graphics.fill(x, y, x + width, y + height, 0xFF202830);
        int fillWidth = Math.round(width * ratio);
        if (fillWidth > 0) {
            int red = ratio < 0.5f ? 255 : Math.round(510.0f * (1.0f - ratio));
            int green = ratio < 0.5f ? Math.round(440.0f * ratio) : 220;
            int color = 0xFF000000 | red << 16 | green << 8 | 0x50;
            graphics.fill(x, y, x + fillWidth, y + height, color);
        }
        for (int segment = 1; segment < 20; segment++) {
            int marker = x + segment * width / 20;
            graphics.fill(marker, y, marker + 1, y + height, 0x55202028);
        }
    }

    @SubscribeEvent
    public void onLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        DiveClientState.clear();
    }
}
