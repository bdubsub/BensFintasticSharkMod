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
     * Applies the entity pitch to the whole GeckoLib model. The pitch is authored by the logical
     * movement controller and interpolated here between server-synchronized entity rotations.
     */
    static void applySwimPitch(PoseStack poseStack, LivingEntity swimmer, float partialTick) {
        if (!swimmer.isAlive() || !swimmer.isInWaterOrBubble()) return;

        float pitch = Mth.lerp(partialTick, swimmer.xRotO, swimmer.getXRot());
        if (pitch == 0.0F) return;

        // Aquatic models face -Z, so negate vanilla's look pitch: negative X rotation
        // (target above) becomes a positive model rotation (nose up).
        float pivot = swimmer.getBbHeight() * 0.5f;
        poseStack.translate(0, pivot, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
        poseStack.translate(0, -pivot, 0);
    }

    /** Compatibility name retained for existing shark renderers. */
    static void applySharkPitch(PoseStack poseStack, LivingEntity shark, float partialTick) {
        applySwimPitch(poseStack, shark, partialTick);
    }
}
