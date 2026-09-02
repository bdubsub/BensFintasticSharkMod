package tfar.bensfintasticsharks.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import tfar.bensfintasticsharks.item.SharkCodexItem;

/**
 * Client-only handler that actually opens the Shark Codex book reader.
 *
 * <p>Lives in the Forge module's client package so its class references to
 * {@code BookViewScreen} only get resolved on the physical client. The
 * Forge mod-setup wires this class's {@link #openReader(Player)} method
 * into {@link SharkCodexItem#CLIENT_OPENER}.</p>
 */
public final class SharkCodexClient {

    private SharkCodexClient() {}

    public static void openReader(Player player) {
        ItemStack virtualBook = new ItemStack(Items.WRITTEN_BOOK);
        virtualBook.addTagElement("title", StringTag.valueOf("Capitán Ben's Codex"));
        virtualBook.addTagElement("author", StringTag.valueOf("Capitán Ben"));
        ListTag pageList = new ListTag();
        for (Component page : SharkCodexItem.PAGES) {
            pageList.add(StringTag.valueOf(Component.Serializer.toJson(page)));
        }
        virtualBook.addTagElement("pages", pageList);
        virtualBook.addTagElement("resolved", ByteTag.valueOf(true));
        WrittenBookItem.resolveBookComponents(virtualBook, player.createCommandSourceStack(), player);
        Minecraft.getInstance().setScreen(new BookViewScreen(new BookViewScreen.WrittenBookAccess(virtualBook)));
    }
}
