package tfar.bensfintasticsharks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Plain item with gray italic flavor lines pulled from lang, following the
 * Captain Ben Hat convention: {@code <descriptionId>.flavor}, {@code .flavor2}, …
 * Tooltips don't word-wrap, so multi-line flavor is split across numbered keys.
 */
public class FlavorItem extends Item {

    private final int flavorLines;

    public FlavorItem(Properties properties, int flavorLines) {
        super(properties);
        this.flavorLines = flavorLines;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        for (int i = 1; i <= flavorLines; i++) {
            String key = getDescriptionId() + ".flavor" + (i == 1 ? "" : String.valueOf(i));
            tooltip.add(Component.translatable(key).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}
