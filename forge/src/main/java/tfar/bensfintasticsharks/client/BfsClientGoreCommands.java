package tfar.bensfintasticsharks.client;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import tfar.bensfintasticsharks.gore.BfsClientGoreConfig;

/** Client-local gore preference; these commands are never sent to the server dispatcher. */
public final class BfsClientGoreCommands {
    private BfsClientGoreCommands() {
    }

    public static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bfs")
                .then(Commands.literal("gore")
                        .then(Commands.literal("on").executes(context -> set(context, true)))
                        .then(Commands.literal("off").executes(context -> set(context, false)))
                        .then(Commands.literal("status").executes(BfsClientGoreCommands::status))));
    }

    private static int set(CommandContext<CommandSourceStack> context, boolean enabled) {
        BfsClientGoreConfig.setEnabled(enabled);
        context.getSource().sendSuccess(() -> Component.literal("Gore visuals are "
                + (enabled ? "on" : "off") + " for this client."), false);
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> context) {
        boolean enabled = BfsClientGoreConfig.enabled();
        context.getSource().sendSuccess(() -> Component.literal("Gore visuals are "
                + (enabled ? "on" : "off") + " for this client."), false);
        return 1;
    }
}
