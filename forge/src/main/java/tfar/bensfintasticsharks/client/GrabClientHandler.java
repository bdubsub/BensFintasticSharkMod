package tfar.bensfintasticsharks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import tfar.bensfintasticsharks.entity.SharkGrabber;

/**
 * 0.18 — client-side polish for the shark grab/thrash:
 *
 * <ul>
 *   <li><b>Camera/model lock:</b> while the local player is held in a shark's jaws
 *       (their vehicle is a {@link SharkGrabber} with an active grab timer), mouse
 *       look is overridden — every frame the player is forced to face the shark
 *       that's thrashing them, so the camera whips around with the thrash instead
 *       of the player free-looking out of the animation. Movement keys (including
 *       sneak-to-dismount) are zeroed for the duration.</li>
 *   <li><b>Red tint:</b> a translucent red overlay ramps in on grab and fades out
 *       over ~1.5s after the shark lets go. Ben's "would it look cool or cringy"
 *       experiment — intensity lives in {@link #MAX_ALPHA} for easy tuning.</li>
 * </ul>
 *
 * The grab timer is a synced entity-data value, so no extra networking is needed.
 */
public class GrabClientHandler {

    /** Peak overlay opacity (0-1). Kept subtle so the screen stays readable. */
    private static final float MAX_ALPHA = 0.30f;
    /** Overlay strength 0..1 — client-frame state, ramps in fast and fades out slow. */
    private static float redness;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        boolean grabbed = grabbingShark(Minecraft.getInstance().player) != null;
        // ~4 ticks to full red on grab; ~30 ticks (1.5s) to fade after release.
        redness = grabbed ? Math.min(1f, redness + 0.25f) : Math.max(0f, redness - 0.035f);
    }

    @SubscribeEvent
    public void onMovementInput(MovementInputUpdateEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        if (grabbingShark(player) == null) return;
        Input input = event.getInput();
        input.forwardImpulse = 0;
        input.leftImpulse = 0;
        input.up = input.down = input.left = input.right = input.jumping = false;
        // No wriggling out of a shark's jaws by sneaking — the grab timer decides.
        input.shiftKeyDown = false;
    }

    @SubscribeEvent
    public void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;
        Entity shark = grabbingShark(player);
        if (shark == null) return;

        // Face the jaws that hold you. positionRider() swings the victim around the
        // shark, so a look-at heading makes the camera sweep with every thrash.
        double dx = shark.getX() - player.getX();
        double dz = shark.getZ() - player.getZ();
        float yaw = (dx * dx + dz * dz) > 1.0e-6
                ? (float) (Mth.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f
                : player.getYRot();
        float pitch = 15.0f; // slightly down at the water/jaws

        player.setYRot(yaw);
        player.setXRot(pitch);
        player.setYHeadRot(yaw);
        player.setYBodyRot(yaw);
        // Kill interpolation so partial-tick lerping doesn't fight the lock.
        player.yRotO = yaw;
        player.xRotO = pitch;
        player.yHeadRotO = yaw;
        player.yBodyRotO = yaw;

        // Camera.setup() has already run: for the front-facing third-person view it
        // mirrored the angles (yaw+180, -pitch), and this event replaces the ANGLES
        // only, not the camera position. Feed it the same mirrored form or F5-front
        // looks away from the player while grabbed.
        if (mc.options.getCameraType() == net.minecraft.client.CameraType.THIRD_PERSON_FRONT) {
            event.setYaw(yaw + 180.0f);
            event.setPitch(-pitch);
        } else {
            event.setYaw(yaw);
            event.setPitch(pitch);
        }
    }

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        if (redness <= 0f) return;
        GuiGraphics gui = event.getGuiGraphics();
        int alpha = (int) (redness * MAX_ALPHA * 255.0f);
        if (alpha <= 0) return;
        gui.fill(0, 0, gui.guiWidth(), gui.guiHeight(), (alpha << 24) | 0xC00000);
    }

    /**
     * 0.19 — hide "Press SHIFT to dismount" while in a shark's jaws. startRiding() makes the
     * client show the vanilla mount.onboard overlay message, but onMovementInput above zeroes
     * shiftKeyDown, so the advice is a lie — the grab timer decides when you get out. Forge
     * renders the overlay message via the RECORD_OVERLAY element; cancelling it while grabbed
     * is safe because the message expires (60t) well before the grab does (100t), so nothing
     * pops back up on release.
     */
    @SubscribeEvent
    public void onRenderGuiOverlay(net.minecraftforge.client.event.RenderGuiOverlayEvent.Pre event) {
        if (!event.getOverlay().id().equals(
                net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.RECORD_OVERLAY.id())) return;
        if (grabbingShark(Minecraft.getInstance().player) != null) {
            event.setCanceled(true);
        }
    }

    /** The shark currently thrashing this player, or null. */
    private static Entity grabbingShark(LocalPlayer player) {
        if (player == null) return null;
        Entity vehicle = player.getVehicle();
        return vehicle instanceof SharkGrabber grabber && grabber.getGrabTimer() > 0 ? vehicle : null;
    }
}
