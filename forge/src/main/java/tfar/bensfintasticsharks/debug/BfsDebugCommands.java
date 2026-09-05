package tfar.bensfintasticsharks.debug;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/** Server command node for bounded debug sessions. */
public final class BfsDebugCommands {

    private BfsDebugCommands() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> createNode() {
        LiteralArgumentBuilder<CommandSourceStack> on = Commands.literal("on")
                        .executes(context -> start(context, "all", BfsDebugManager.DEFAULT_DURATION_TICKS, List.of()))
                        .then(Commands.argument("category", StringArgumentType.word())
                                .then(Commands.argument("ticks", IntegerArgumentType.integer(
                                                BfsDebugManager.MIN_DURATION_TICKS, BfsDebugManager.MAX_DURATION_TICKS))
                                        .executes(context -> start(context,
                                                StringArgumentType.getString(context, "category"),
                                                IntegerArgumentType.getInteger(context, "ticks"), List.of()))
                                        .then(Commands.argument("targets", EntityArgument.entities())
                                                .executes(context -> start(context,
                                                        StringArgumentType.getString(context, "category"),
                                                        IntegerArgumentType.getInteger(context, "ticks"),
                                                        EntityArgument.getEntities(context, "targets"))))));
        return Commands.literal("debug")
                .then(on)
                .then(Commands.literal("off").executes(BfsDebugCommands::stop))
                .then(Commands.literal("status").executes(BfsDebugCommands::status));
    }

    private static int start(CommandContext<CommandSourceStack> context, String category, int ticks,
                             Collection<? extends Entity> targets) {
        CommandSourceStack source = context.getSource();
        BfsDebugManager.StartResult result = BfsDebugManager.start(source, category, ticks, targets);
        if (!result.started()) {
            source.sendFailure(Component.literal(result.message()));
            return 0;
        }
        BfsDebugManager.Session session = result.activeSession();
        source.sendSuccess(() -> Component.literal("BFS debug capture started").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        source.sendSuccess(() -> Component.literal("  Session: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(session.id().toString()).withStyle(ChatFormatting.WHITE)), false);
        source.sendSuccess(() -> Component.literal("  Category: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(session.category()).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(". Duration: " + ticks + " ticks.").withStyle(ChatFormatting.WHITE)), false);
        source.sendSuccess(() -> Component.literal("  Targets: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(session.targetCount() + " selected, " + session.excludedTargets() + " excluded")
                        .withStyle(ChatFormatting.WHITE)), false);
        if (result.defaultTargets()) {
            source.sendSuccess(() -> Component.literal("  Default target selection uses nearby loaded BFS entities only. Empty selection is recorded, not a passing entity test.")
                    .withStyle(ChatFormatting.YELLOW), false);
        }
        source.sendSuccess(() -> Component.literal("  Output: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(session.outputPath().toString()).withStyle(ChatFormatting.WHITE)), false);
        return session.targetCount();
    }

    private static int stop(CommandContext<CommandSourceStack> context) {
        BfsDebugManager.StopResult result = BfsDebugManager.stop("operator_requested");
        if (!result.stopped()) {
            context.getSource().sendFailure(Component.literal("No BFS debug capture is active."));
            return 0;
        }
        BfsDebugManager.Session session = result.stoppedSession();
        context.getSource().sendSuccess(() -> Component.literal("BFS debug capture stopped. Output is finalizing at ")
                .append(Component.literal(session.outputPath().toString()).withStyle(ChatFormatting.WHITE)), false);
        return 1;
    }

    private static int status(CommandContext<CommandSourceStack> context) {
        BfsDebugManager.Status status = BfsDebugManager.status();
        if (!status.active()) {
            context.getSource().sendSuccess(() -> Component.literal("BFS debug capture: inactive").withStyle(ChatFormatting.GRAY), false);
            return 0;
        }
        BfsDebugManager.Session session = status.session();
        long now = System.currentTimeMillis();
        context.getSource().sendSuccess(() -> Component.literal("BFS debug capture").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        context.getSource().sendSuccess(() -> Component.literal("  Side: server. Session: " + session.id()).withStyle(ChatFormatting.WHITE), false);
        context.getSource().sendSuccess(() -> Component.literal("  Category: " + session.category() + ". Targets: "
                + session.targetCount() + " selected, " + session.excludedTargets() + " excluded.").withStyle(ChatFormatting.WHITE), false);
        context.getSource().sendSuccess(() -> Component.literal("  Tick window: " + session.startTick() + " to " + session.endTick()
                + ". Wall deadline: " + Instant.ofEpochMilli(session.wallDeadlineMillis()) + ".").withStyle(ChatFormatting.WHITE), false);
        context.getSource().sendSuccess(() -> Component.literal("  Records: " + session.accepted() + " accepted, " + session.dropped()
                + " dropped. Incomplete: " + session.incomplete() + ". Reason: " + session.incompleteReason() + ".")
                .withStyle(session.incomplete() ? ChatFormatting.YELLOW : ChatFormatting.GREEN), false);
        context.getSource().sendSuccess(() -> Component.literal("  Wall time remaining: "
                + Math.max(0L, (session.wallDeadlineMillis() - now) / 1_000L) + " seconds. Output: " + session.outputPath())
                .withStyle(ChatFormatting.GRAY), false);
        return 1;
    }
}
