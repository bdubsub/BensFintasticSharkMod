package tfar.bensfintasticsharks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import tfar.bensfintasticsharks.entity.BfsVariantHolder;

import java.util.Comparator;
import java.util.List;

/**
 * BFS spawn egg with shift+right-click variant cycling, for species that ship
 * variants. The variant count is supplied at registration so eggs for entities
 * without variants (jellyfish, lobster, etc.) don't pretend to cycle anything.
 *
 * <p>The cycle index is stored in the item NBT under {@link #TAG_VARIANT}. When
 * the egg actually spawns an entity (regular right-click on a block), we search
 * a small radius for the just-spawned entity of our type and apply the variant
 * via {@link BfsVariantHolder#setBfsVariantId(int)} — this overrides any random
 * variant selection that ran in {@code finalizeSpawn}.</p>
 */
public class BfsSpawnEggItem extends SpawnEggItem {

    public static final String TAG_VARIANT = "BfsVariant";
    private final int variantCount;

    /**
     * @param variantCount number of cycle slots. Pass 0 for species with no variants —
     *                     shift+right-click will be a no-op and no tooltip line is added.
     */
    public BfsSpawnEggItem(EntityType<? extends net.minecraft.world.entity.Mob> type,
                           int primary, int secondary,
                           int variantCount,
                           Properties props) {
        super(type, primary, secondary, props);
        this.variantCount = Math.max(0, variantCount);
    }

    public boolean hasVariants() { return variantCount > 0; }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hasVariants() && player.isShiftKeyDown()) {
            int next = (getVariantId(stack) + 1) % variantCount;
            setVariantId(stack, next);
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.literal("Variant: " + next + " / " + (variantCount - 1))
                                .withStyle(ChatFormatting.AQUA),
                        true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        return super.use(level, player, hand);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        InteractionResult result = super.useOn(ctx);
        if (!hasVariants()) return result;
        if (!(result.consumesAction()) || !(ctx.getLevel() instanceof ServerLevel sl)) return result;
        ItemStack stack = ctx.getItemInHand();
        if (!stack.hasTag() || !stack.getTag().contains(TAG_VARIANT)) return result;
        int variant = getVariantId(stack);

        EntityType<?> spawnType = super.getType(stack.getTag());
        AABB searchArea = new AABB(ctx.getClickedPos()).inflate(4.0);
        List<Entity> candidates = sl.getEntitiesOfClass(Entity.class, searchArea,
                e -> e.getType() == spawnType && e instanceof BfsVariantHolder);
        candidates.stream()
                .min(Comparator.comparingInt(e -> e.tickCount))
                .ifPresent(e -> ((BfsVariantHolder) e).setBfsVariantId(variant));
        return result;
    }

    public static int getVariantId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(TAG_VARIANT) ? tag.getInt(TAG_VARIANT) : 0;
    }

    public static void setVariantId(ItemStack stack, int id) {
        stack.getOrCreateTag().putInt(TAG_VARIANT, id);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Level level,
                                @NotNull List<Component> tooltip,
                                @NotNull net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        // Scientific name (italicized) right under the item name. Translation key is
        // item.bensfintasticsharks.<id>_spawn_egg.scientific — silently skipped if absent.
        net.minecraft.resources.ResourceLocation rl =
                net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this);
        if (rl != null) {
            String sciKey = "item." + rl.getNamespace() + "." + rl.getPath() + ".scientific";
            net.minecraft.network.chat.MutableComponent sciLine =
                    Component.translatable(sciKey);
            // Only show when the key actually resolves to something different from itself
            // (i.e. lang has it). getString() returns the key verbatim when missing.
            if (!sciLine.getString().equals(sciKey)) {
                tooltip.add(sciLine.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
        if (!hasVariants()) return;
        int variant = getVariantId(stack);
        tooltip.add(Component.literal("Variant " + variant + " / " + (variantCount - 1))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("Shift + right-click to cycle")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
