package tfar.bensfintasticsharks.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import tfar.bensfintasticsharks.client.renderer.DiveArmorRenderer;

import java.util.function.Consumer;

/** Forge client bridge for the atlas backed dive armor. */
public final class DiveArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DiveArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity,
                                                                    ItemStack stack,
                                                                    EquipmentSlot slot,
                                                                    HumanoidModel<?> original) {
                if (renderer == null) {
                    renderer = new DiveArmorRenderer();
                }
                HumanoidModel<?> poseSource = original;
                var entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity);
                if (entityRenderer instanceof LivingEntityRenderer<?, ?> livingRenderer
                        && livingRenderer.getModel() instanceof HumanoidModel<?> humanoidModel) {
                    poseSource = humanoidModel;
                }
                renderer.prepForRender(entity, stack, slot, poseSource);
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
