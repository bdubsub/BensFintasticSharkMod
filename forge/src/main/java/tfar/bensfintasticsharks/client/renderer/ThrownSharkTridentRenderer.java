package tfar.bensfintasticsharks.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import tfar.bensfintasticsharks.BensFintasticSharks;
import tfar.bensfintasticsharks.entity.ThrownSharkTridentEntity;
import tfar.bensfintasticsharks.init.ModItems;

/**
 * Thrown trident renderer modelled on vanilla {@code TridentRenderer}.
 *
 * <p>Three things differ from vanilla and we have to compensate:
 * <ul>
 *     <li>Vanilla uses a hand-built {@code TridentModel} that points at -Y. Our model
 *         is the item's 3D model which points at +Y (handle at the bottom, tip at the
 *         top). We add a 180 degree rotation around Z so the tip lines up with the
 *         direction vanilla's rotation chain expects.</li>
 *     <li>{@code ItemDisplayContext.GROUND} (the previous renderer used it) applies a
 *         90 degree X rotation to render the trident lying on its side, which compounds
 *         with our yaw/pitch rotation and makes the trident look tilted in flight. We
 *         use {@code NONE} so the renderer applies no extra transform.</li>
 *     <li>The model origin is at the handle base. Vanilla puts the tip near the model
 *         origin so when the trident sticks, the entity position lines up with the
 *         spike tip. We translate down two blocks to do the same thing.</li>
 * </ul>
 */
public class ThrownSharkTridentRenderer extends EntityRenderer<ThrownSharkTridentEntity> {
    private final ItemRenderer itemRenderer;

    /** Direct handle to the 3D trident model so we render it instead of the 2D inventory sprite. */
    private static final ModelResourceLocation THREE_D_MODEL =
            new ModelResourceLocation(BensFintasticSharks.id("shark_trident_3d"), "inventory");

    public ThrownSharkTridentRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }

    @Override
    public void render(ThrownSharkTridentEntity entity, float entityYaw, float partialTick,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, pose, buffer, packedLight);

        pose.pushPose();

        // Match vanilla TridentRenderer's rotation chain exactly. The model already
        // ships with the tip oriented along +Y, so the standard yaw/pitch chain
        // aligns the tip with the flight direction with no extra Z flip needed.
        pose.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        pose.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 90.0F));

        // Render the 3D model directly so we don't fall back to the 2D GUI sprite.
        ItemStack stack = new ItemStack(ModItems.SHARK_TRIDENT);
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(THREE_D_MODEL);
        this.itemRenderer.render(stack, ItemDisplayContext.NONE, false,
                pose, buffer, packedLight, OverlayTexture.NO_OVERLAY, model);

        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownSharkTridentEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
