package tfar.bensfintasticsharks.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

/** Shared whole-body transforms that GeckoLib does not apply from vanilla entity pitch. */
final class AquaticRenderTransforms {
    private AquaticRenderTransforms() {
    }

    /**
     * Smoothly angles a swimming shark toward its vertical travel direction. Vanilla's
     * {@code SmoothSwimmingMoveControl} already updates entity X rotation, but a GeckoLib
     * model otherwise consumes that value only as head-pitch data and leaves the body level.
     */
    static void applySharkPitch(PoseStack poseStack, LivingEntity shark, float partialTick) {
        if (!shark.isAlive() || !shark.isInWaterOrBubble()) return;

        float pitch = Mth.clamp(Mth.lerp(partialTick, shark.xRotO, shark.getXRot()), -35f, 35f);
        if (Math.abs(pitch) < 0.25f) return;

        // Shark geometry faces -Z, so negate vanilla's look pitch: negative X rotation
        // (target above) becomes a positive model rotation (nose up).
        float pivot = shark.getBbHeight() * 0.5f;
        poseStack.translate(0, pivot, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
        poseStack.translate(0, -pivot, 0);
    }
}
