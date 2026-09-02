package tfar.bensfintasticsharks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
 * BFS spawn egg with per-player, left-click variant cycling for species that ship variants.
 *
 * <p>The variant index is stored in the held stack's NBT under {@link #TAG_VARIANT} (0-based
 * internally) and shown to players 1-based ("Variant 2/6") so there is never a confusing
 * "Variant 0". Sneak + right-clicking while holding the egg advances the variant — handled
 * server-side in {@link #use}/{@link #useOn} via {@link #cycleVariant}, with a chat
 * confirmation and a little chime.</p>
 *
 * <p>When the egg spawns a mob — on a block ({@link #useOn}) OR into water/air ({@link #use}) —
 * the stored variant is stamped onto the freshly-spawned entity, overriding the random roll in
 * {@code finalizeSpawn}. (The water/air path previously skipped this, so eggs spawned in water
 * ignored the chosen variant.)</p>
 */
public class BfsSpawnEggItem extends SpawnEggItem {

    public static final String TAG_VARIANT = "BfsVariant";
    private final int variantCount;

    /**
     * @param variantCount number of variants. Pass 0 for species with no variants — cycling is a
     *                     no-op and no variant tooltip line is shown.
     */
    public BfsSpawnEggItem(EntityType<? extends net.minecraft.world.entity.Mob> type,
                           int primary, int secondary,
                           int variantCount,
                           Properties props) {
        super(type, primary, secondary, props);
        this.variantCount = Math.max(0, variantCount);
    }

    public boolean hasVariants() { return variantCount > 0; }
    public int variantCount() { return variantCount; }

    /** Server-side: advance to the next variant (wraps), with a chime + chat confirmation. */
    public void cycleVariant(Player player, ItemStack stack) {
        if (!hasVariants()) return;
        int next = (getVariantId(stack) + 1) % variantCount;
        setVariantId(stack, next);
        Level level = player.level();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7f, 1.4f);
        player.displayClientMessage(
                Component.translatable(getDescriptionId(stack)).withStyle(ChatFormatting.AQUA)
                        .append(Component.literal(" — Variant " + (next + 1) + "/" + variantCount)
                                .withStyle(ChatFormatting.GRAY)),
                false);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // Sneak + right-click cycles the variant instead of spawning a mob. use() runs on both
        // logical sides, so cycle server-side only (no double-advance); the client consumes so
        // the interaction is swallowed and nothing spawns.
        if (hasVariants() && player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                cycleVariant(player, stack);
                return InteractionResultHolder.success(stack);
            }
            return InteractionResultHolder.consume(stack);
        }
        int variant = getVariantId(stack);
        InteractionResultHolder<ItemStack> result = super.use(level, player, hand);
        // Water/air spawn path: vanilla rolls a random variant in finalizeSpawn, so re-apply the
        // egg's chosen variant to the just-spawned mob (the block path is handled in useOn).
        if (hasVariants() && result.getResult().consumesAction() && level instanceof ServerLevel sl) {
            applyVariantToNearestSpawn(sl, player.blockPosition(), super.getType(stack.getTag()), variant);
        }
        return result;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        Player player = ctx.getPlayer();
        // Sneak + right-click on a block cycles the variant instead of spawning. Cycle server-side
        // only (useOn runs on both sides); the client consumes so the click is swallowed. A null
        // player (e.g. dispenser) falls through to a normal spawn.
        if (hasVariants() && player != null && player.isShiftKeyDown()) {
            if (!ctx.getLevel().isClientSide) {
                cycleVariant(player, stack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        int variant = getVariantId(stack);
        InteractionResult result = super.useOn(ctx);
        if (hasVariants() && result.consumesAction() && ctx.getLevel() instanceof ServerLevel sl) {
            applyVariantToNearestSpawn(sl, ctx.getClickedPos(), super.getType(stack.getTag()), variant);
        }
        return result;
    }

    /** Find the just-spawned mob of our type near {@code near} and stamp the chosen variant. */
    private void applyVariantToNearestSpawn(ServerLevel sl, BlockPos near, EntityType<?> spawnType, int variant) {
        // Big sharks have wide hitboxes and the spawn lands at click/look position + offset, so
        // inflate generously. The newest entity (lowest tickCount) is the one we just spawned.
        AABB searchArea = new AABB(near).inflate(8.0);
        List<Entity> candidates = sl.getEntitiesOfClass(Entity.class, searchArea,
                e -> e.getType() == spawnType && e instanceof BfsVariantHolder);
        candidates.stream()
                .min(Comparator.comparingInt(e -> e.tickCount))
                .ifPresent(e -> ((BfsVariantHolder) e).setBfsVariantId(variant));
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
        // Scientific name (italicized) under the item name. Key: item.<ns>.<id>.scientific —
        // silently skipped when absent (getString() returns the key verbatim when missing).
        net.minecraft.resources.ResourceLocation rl =
                net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this);
        if (rl != null) {
            String sciKey = "item." + rl.getNamespace() + "." + rl.getPath() + ".scientific";
            net.minecraft.network.chat.MutableComponent sciLine = Component.translatable(sciKey);
            if (!sciLine.getString().equals(sciKey)) {
                tooltip.add(sciLine.withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
        if (!hasVariants()) return;
        // 1-based display so there's never a "Variant 0".
        int variant = getVariantId(stack);
        tooltip.add(Component.literal("Variant " + (variant + 1) + " / " + variantCount)
                .withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("Sneak + right-click to switch variant")
                .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
