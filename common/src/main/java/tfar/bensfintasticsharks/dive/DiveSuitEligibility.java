package tfar.bensfintasticsharks.dive;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tfar.bensfintasticsharks.init.ModItems;

/** Shared equipment and fluid predicate used by server travel and client prediction. */
public final class DiveSuitEligibility {
    private DiveSuitEligibility() {
    }

    public static Result evaluate(Player player) {
        boolean fullSuit = isDivePiece(player.getItemBySlot(EquipmentSlot.HEAD), ModItems.DIVE_HELMET)
                && isDivePiece(player.getItemBySlot(EquipmentSlot.CHEST), ModItems.DIVE_CHESTPLATE)
                && isDivePiece(player.getItemBySlot(EquipmentSlot.LEGS), ModItems.DIVE_LEGGINGS)
                && isDivePiece(player.getItemBySlot(EquipmentSlot.FEET), ModItems.DIVE_BOOTS);
        boolean submergedEyes = player.isEyeInFluid(FluidTags.WATER);
        boolean waterContact = player.isInWaterOrBubble();
        boolean eligible = fullSuit && !player.isCreative() && !player.isSpectator() && waterContact;
        return new Result(fullSuit, submergedEyes, waterContact, eligible);
    }

    private static boolean isDivePiece(ItemStack stack, net.minecraft.world.item.Item expected) {
        return !stack.isEmpty() && stack.is(expected);
    }

    public record Result(boolean fullSuit, boolean submergedEyes, boolean waterContact, boolean eligible) {
    }
}
