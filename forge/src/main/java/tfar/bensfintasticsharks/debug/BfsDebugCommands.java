package tfar.bensfintasticsharks.debug;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import tfar.bensfintasticsharks.config.SpeciesSettingsConfigBridge;
import tfar.bensfintasticsharks.dive.DiveOxygenManager;
import tfar.bensfintasticsharks.entity.SpeciesSettingsService;
import tfar.bensfintasticsharks.follow.BfsFollowManager;
import tfar.bensfintasticsharks.gore.GoreDebugSettings;
import tfar.bensfintasticsharks.spawn.MobCapManager;

import java.util.EnumMap;
import java.time.Instant;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Server command node for bounded debug sessions. */
public final class BfsDebugCommands {

    private static final com.mojang.brigadier.suggestion.SuggestionProvider<CommandSourceStack> CATEGORY_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(
                    new String[]{"all", "movement", "brain", "combat", "population", "advancement", "algae",
                            "dive", "follow", "disturbance", "boat"}, builder);
    private static final com.mojang.brigadier.suggestion.SuggestionProvider<CommandSourceStack> SPECIES_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(
                    java.util.stream.Stream.concat(java.util.stream.Stream.of("*"), MobCapManager.getSpeciesPaths().stream()), builder);
    private static final com.mojang.brigadier.suggestion.SuggestionProvider<CommandSourceStack> FIELD_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(
                    SpeciesSettingsService.fields().stream().map(SpeciesSettingsService.Field::id), builder);

    private BfsDebugCommands() {
    }

    public static LiteralArgumentBuilder<CommandSourceStack> createNode() {
        LiteralArgumentBuilder<CommandSourceStack> on = Commands.literal("on")
                        .executes(context -> start(context, "all", BfsDebugManager.DEFAULT_DURATION_TICKS, List.of()))
                        .then(Commands.argument("category", StringArgumentType.word()).suggests(CATEGORY_SUGGESTIONS)
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
        LiteralArgumentBuilder<CommandSourceStack> debug = Commands.literal("debug")
                .then(on)
                .then(Commands.literal("off").executes(BfsDebugCommands::stop))
                .then(Commands.literal("status").executes(BfsDebugCommands::status))
                .then(algaeNode())
                .then(diveNode())
                .then(goreNode())
                .then(followNode())
                .then(settingsNode())
                .then(speedNode())
                .then(sprintNode())
                .then(tiltNode())
                .then(spawnSizeNode())
                .then(scaleNode())
                .then(attributeNode("sethealth", SpeciesSettingsService.Field.HEALTH_MULTIPLIER))
                .then(attributeNode("setdamage", SpeciesSettingsService.Field.DAMAGE_MULTIPLIER))
                .then(attributeNode("setknockback", SpeciesSettingsService.Field.KNOCKBACK_RESISTANCE))
                .then(behaviorNode());
        for (CommandNode<CommandSourceStack> child : debug.getArguments()) addWildcardTargets(child);
        return debug;
    }

    private static void addWildcardTargets(CommandNode<CommandSourceStack> node) {
        for (CommandNode<CommandSourceStack> child : List.copyOf(node.getChildren())) {
            addWildcardTargets(child);
            if (!"entity".equals(child.getName())) continue;
            var wildcard = Commands.literal("*").requires(child.getRequirement());
            if (child.getCommand() != null) wildcard.executes(child.getCommand());
            for (CommandNode<CommandSourceStack> argument : child.getChildren()) wildcard.then(argument);
            node.addChild(wildcard.build());
        }
    }

    private static String speciesTarget(CommandContext<CommandSourceStack> context) {
        for (var node : context.getNodes()) {
            if ("*".equals(node.getNode().getName())) return "*";
        }
        var id = ResourceLocationArgument.getId(context, "entity");
        return Set.of("minecraft", "bfs", "bensfintasticsharks").contains(id.getNamespace())
                ? id.getPath() : id.toString();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> algaeNode() {
        return Commands.literal("algae").requires(source -> source.hasPermission(2))
                .then(Commands.literal("scan")
                        .then(Commands.argument("minX", IntegerArgumentType.integer())
                                .then(Commands.argument("minZ", IntegerArgumentType.integer())
                                        .then(Commands.argument("size", IntegerArgumentType.integer(1, 64))
                                                .executes(BfsDebugCommands::scanAlgae)))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> diveNode() {
        return Commands.literal("dive").requires(source -> source.hasPermission(2))
                .then(Commands.literal("setoxygen")
                        .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                                .then(Commands.argument("ticks", IntegerArgumentType.integer(
                                                0, DiveOxygenManager.MAX_RESERVE_TICKS))
                                        .executes(context -> {
                                            ServerPlayer target = net.minecraft.commands.arguments.EntityArgument
                                                    .getPlayer(context, "player");
                                            int ticks = IntegerArgumentType.getInteger(context, "ticks");
                                            DiveOxygenManager.setReserve(target, ticks);
                                            context.getSource().sendSuccess(() -> Component.literal(
                                                    "Set " + target.getGameProfile().getName() + "'s dive oxygen to "
                                                            + DiveOxygenManager.storedReserve(target) + "/"
                                                            + DiveOxygenManager.MAX_RESERVE_TICKS + " ticks."), false);
                                            return 1;
                                        }))))
                .then(Commands.literal("setdrain")
                        .then(Commands.argument("ticks_per_second", IntegerArgumentType.integer(
                                        0, DiveOxygenManager.MAX_DRAIN_TICKS_PER_SECOND))
                                .executes(context -> {
                                    int requested = IntegerArgumentType.getInteger(context, "ticks_per_second");
                                    int rate = DiveOxygenManager.setDrainTicksPerSecond(requested);
                                    for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                                        DiveOxygenManager.refreshSync(player);
                                    }
                                    context.getSource().sendSuccess(() -> Component.literal(
                                            "Dive oxygen drain is now " + rate + " reserve ticks per second."), false);
                                    return 1;
                                })))
                .then(Commands.literal("status").executes(context -> {
                    String oxygen = context.getSource().getEntity() instanceof ServerPlayer player
                            ? " Your reserve: " + DiveOxygenManager.storedReserve(player) + "/"
                            + DiveOxygenManager.MAX_RESERVE_TICKS + " ticks."
                            : "";
                    context.getSource().sendSuccess(() -> Component.literal(
                            "Dive oxygen drain: " + DiveOxygenManager.drainTicksPerSecond()
                                    + " ticks per second (default "
                                    + DiveOxygenManager.DEFAULT_DRAIN_TICKS_PER_SECOND + ")." + oxygen), false);
                    return 1;
                }))
                .then(Commands.literal("reset").executes(context -> {
                    DiveOxygenManager.resetDebugSettings();
                    for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                        DiveOxygenManager.refreshSync(player);
                    }
                    context.getSource().sendSuccess(() -> Component.literal(
                            "Dive debug settings reset. Oxygen drain: "
                                    + DiveOxygenManager.DEFAULT_DRAIN_TICKS_PER_SECOND + " ticks per second."), false);
                    return 1;
                }));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> goreNode() {
        return Commands.literal("gore").requires(source -> source.hasPermission(2))
                .then(Commands.literal("get").executes(context -> {
                    context.getSource().sendSuccess(() -> Component.literal(
                            "Gore debug settings: " + GoreDebugSettings.current().describe()), false);
                    return 1;
                }))
                .then(Commands.literal("reset").executes(context -> {
                    GoreDebugSettings.reset();
                    context.getSource().sendSuccess(() -> Component.literal(
                            "Gore debug settings reset: " + GoreDebugSettings.current().describe()), false);
                    return 1;
                }))
                .then(Commands.literal("set")
                        .then(Commands.argument("field", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                                        new String[]{"particle_count", "particle_scale", "particle_lifetime",
                                                "wound_scale", "wound_lifetime", "remains_count",
                                                "remains_scale", "remains_lifetime"}, builder))
                                .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                        .executes(context -> {
                                            String field = StringArgumentType.getString(context, "field");
                                            double value = DoubleArgumentType.getDouble(context, "value");
                                            if (!GoreDebugSettings.set(field, value)) {
                                                context.getSource().sendFailure(Component.literal(
                                                        "Invalid gore setting or value. Check the field bounds and use whole numbers for counts and lifetimes."));
                                                return 0;
                                            }
                                            context.getSource().sendSuccess(() -> Component.literal(
                                                    "Gore setting updated: " + GoreDebugSettings.current().describe()), false);
                                            return 1;
                                        }))));
    }

    private static int scanAlgae(CommandContext<CommandSourceStack> context) {
        int minX = IntegerArgumentType.getInteger(context, "minX");
        int minZ = IntegerArgumentType.getInteger(context, "minZ");
        int size = IntegerArgumentType.getInteger(context, "size");
        ServerLevel level = context.getSource().getLevel();
        BfsDebugManager.AlgaeScan scan = BfsDebugManager.scanAlgae(level, minX, minZ, size);
        context.getSource().sendSuccess(() -> Component.literal(
                "algae scan seed=" + scan.seed() + " region=" + scan.minX() + "," + scan.minZ()
                        + " size=" + scan.size() + " small=" + scan.smallCells()
                        + " greenCells=" + scan.greenCells() + " redCells=" + scan.redCells()
                        + " greenColumns=" + scan.greenColumns() + " redColumns=" + scan.redColumns()
                        + " greenTall=" + scan.greenTallColumns() + " redTall=" + scan.redTallColumns()
                        + " generatedCells=" + scan.generatedCells()).withStyle(ChatFormatting.AQUA), false);
        return scan.generatedCells();
    }

    private static LiteralArgumentBuilder<CommandSourceStack> followNode() {
        return Commands.literal("followme").requires(source -> source.hasPermission(2))
                .executes(context -> issueFollow(context, context.getSource().getPlayerOrException()))
                .then(Commands.argument("recipient", EntityArgument.player())
                        .executes(context -> issueFollow(context, EntityArgument.getPlayer(context, "recipient"))))
                .then(Commands.literal("status")
                        .executes(context -> followStatus(context, context.getSource().getPlayerOrException(), 1))
                        .then(Commands.argument("recipient", EntityArgument.player())
                                .executes(context -> followStatus(context, EntityArgument.getPlayer(context, "recipient"), 1))
                                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(context -> followStatus(context, EntityArgument.getPlayer(context, "recipient"),
                                                IntegerArgumentType.getInteger(context, "page"))))))
                .then(Commands.literal("stopone")
                        .then(Commands.argument("target", EntityArgument.entity())
                                .executes(context -> stopOneFollow(context, context.getSource().getPlayerOrException()))
                                .then(Commands.argument("recipient", EntityArgument.player())
                                        .executes(context -> stopOneFollow(context, EntityArgument.getPlayer(context, "recipient"))))))
                .then(Commands.literal("stop")
                        .executes(context -> stopFollow(context, context.getSource().getPlayerOrException()))
                        .then(Commands.argument("recipient", EntityArgument.player())
                                .executes(context -> stopFollow(context, EntityArgument.getPlayer(context, "recipient")))));
    }

    private static int issueFollow(CommandContext<CommandSourceStack> context, ServerPlayer recipient) {
        BfsFollowManager.issue(recipient);
        if (context.getSource().getEntity() != recipient) {
            context.getSource().sendSuccess(() -> BfsFollowManager.message("issued_to", recipient.getDisplayName()), false);
        }
        return 1;
    }

    private static int followStatus(CommandContext<CommandSourceStack> context, ServerPlayer recipient, int page) {
        BfsFollowManager.Status status = BfsFollowManager.status(recipient, page);
        Component summary = BfsFollowManager.message("status", recipient.getDisplayName(),
                BfsFollowManager.count(status.selectedCount()), status.followingCount(), status.waitingCount(),
                status.pausedCount(), status.page(), status.pages());
        if (context.getSource().getEntity() instanceof ServerPlayer viewer) {
            BfsFollowManager.notifyOwner(viewer, summary);
        } else context.getSource().sendSuccess(() -> summary, false);
        for (BfsFollowManager.MemberStatus member : status.entries()) {
            Component entry = BfsFollowManager.message("status_entry", member.label(),
                    BfsFollowManager.message("state." + member.state()), BfsFollowManager.message("reason." + member.reason()));
            context.getSource().sendSuccess(() -> entry, false);
        }
        return status.selectedCount();
    }

    private static int stopFollow(CommandContext<CommandSourceStack> context, ServerPlayer recipient) {
        int count = BfsFollowManager.stopAll(recipient, "command_stop");
        if (context.getSource().getEntity() != recipient) context.getSource().sendSuccess(
                () -> BfsFollowManager.message("group_released", BfsFollowManager.count(count),
                        BfsFollowManager.count(BfsFollowManager.selectedCount(recipient))), false);
        return count;
    }

    private static int stopOneFollow(CommandContext<CommandSourceStack> context, ServerPlayer recipient)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        boolean released = BfsFollowManager.stopOne(recipient, EntityArgument.getEntity(context, "target"));
        if (context.getSource().getEntity() != recipient) context.getSource().sendSuccess(
                () -> BfsFollowManager.message(released ? "target_released" : "not_selected"), false);
        return released ? 1 : 0;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> settingsNode() {
        return Commands.literal("settings")
                .then(Commands.literal("help").executes(BfsDebugCommands::settingsHelp))
                .then(Commands.literal("list").executes(BfsDebugCommands::settingsList))
                .then(Commands.literal("get")
                        .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                                .executes(context -> settingsGet(context,
                                        speciesTarget(context)))))
                .then(Commands.literal("set")
                        .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                                .then(Commands.argument("field", StringArgumentType.word()).suggests(FIELD_SUGGESTIONS)
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                .executes(context -> settingsSet(context, currentRevision()))
                                                .then(Commands.argument("revision", LongArgumentType.longArg())
                                                        .executes(context -> settingsSet(context,
                                                                LongArgumentType.getLong(context, "revision"))))))))
                .then(Commands.literal("reset")
                        .executes(context -> settingsReset(context, "*", Set.of(), currentRevision()))
                        .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                                .executes(context -> settingsReset(context,
                                        speciesTarget(context), Set.of(), currentRevision()))
                                .then(Commands.argument("field", StringArgumentType.word()).suggests(FIELD_SUGGESTIONS)
                                        .executes(context -> settingsReset(context,
                                                speciesTarget(context),
                                                parseFieldSet(StringArgumentType.getString(context, "field")), currentRevision()))
                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                .executes(context -> settingsReset(context,
                                                        speciesTarget(context),
                                                        parseFieldSet(StringArgumentType.getString(context, "field")),
                                                        LongArgumentType.getLong(context, "revision"))))))
                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                .executes(context -> settingsReset(context, "*", Set.of(),
                                        LongArgumentType.getLong(context, "revision")))))
                .then(Commands.literal("reload").executes(BfsDebugCommands::settingsReload));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> tiltNode() {
        return Commands.literal("settilt")
                .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                        .executes(context -> settingsGet(context, speciesTarget(context)))
                        .then(Commands.literal("reset").executes(context -> settingsReset(context,
                                speciesTarget(context), EnumSet.of(SpeciesSettingsService.Field.TILT_UP,
                                        SpeciesSettingsService.Field.TILT_DOWN, SpeciesSettingsService.Field.TILT_RATE),
                                currentRevision())))
                        .then(Commands.argument("upward_degrees", DoubleArgumentType.doubleArg(0.0D, 90.0D))
                                .then(Commands.argument("downward_degrees", DoubleArgumentType.doubleArg(0.0D, 90.0D))
                                        .then(Commands.argument("degrees_per_second", DoubleArgumentType.doubleArg(0.0D, 1800.0D))
                                                .executes(BfsDebugCommands::setTilt)))));
    }

    private static int setTilt(CommandContext<CommandSourceStack> context) {
        double up = DoubleArgumentType.getDouble(context, "upward_degrees");
        double down = DoubleArgumentType.getDouble(context, "downward_degrees");
        double rate = DoubleArgumentType.getDouble(context, "degrees_per_second");
        var result = SpeciesSettingsService.apply(currentRevision(), speciesTarget(context), Map.of(
                SpeciesSettingsService.Field.TILT_UP, up, SpeciesSettingsService.Field.TILT_DOWN, down,
                SpeciesSettingsService.Field.TILT_RATE, rate));
        int count = report(context.getSource(), result);
        if (result.applied()) {
            context.getSource().sendSuccess(() -> Component.literal("BFS tilt tuning")
                    .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
            context.getSource().sendSuccess(() -> Component.literal("  Upward limit: " + formatValue(up)
                    + " degrees. Downward limit: " + formatValue(down) + " degrees."), false);
            context.getSource().sendSuccess(() -> Component.literal("  Turn rate: " + formatValue(rate)
                    + " degrees per second. Applies during normal swimming and pursuit."), false);
            context.getSource().sendSuccess(() -> Component.literal("  Supported species: BFS sharks, Atlantic Cod, and Atlantic Salmon. Changes last until reset or server restart.")
                    .withStyle(ChatFormatting.GRAY), false);
        }
        return count;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> speedNode() {
        return Commands.literal("setspeed")
                .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("horizontal", DoubleArgumentType.doubleArg(0.0D))
                                .then(Commands.argument("vertical", DoubleArgumentType.doubleArg(0.0D))
                                        .executes(context -> setSpeed(context, currentRevision()))
                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                .executes(context -> setSpeed(context,
                                                        LongArgumentType.getLong(context, "revision")))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> sprintNode() {
        return Commands.literal("setsprint")
                .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("horizontal", DoubleArgumentType.doubleArg(0.0D))
                                .then(Commands.argument("vertical", DoubleArgumentType.doubleArg(0.0D))
                                        .executes(context -> setSprint(context, currentRevision()))
                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                .executes(context -> setSprint(context,
                                                        LongArgumentType.getLong(context, "revision")))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> spawnSizeNode() {
        return Commands.literal("setspawnsize")
                .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("minimum", IntegerArgumentType.integer(1, 32))
                                .then(Commands.argument("maximum", IntegerArgumentType.integer(1, 32))
                                        .executes(context -> setSpawnSize(context, currentRevision()))
                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                .executes(context -> setSpawnSize(context,
                                                        LongArgumentType.getLong(context, "revision")))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> scaleNode() {
        return Commands.literal("setscale")
                .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("minimum", DoubleArgumentType.doubleArg(0.25D, 2.0D))
                                .then(Commands.argument("maximum", DoubleArgumentType.doubleArg(0.25D, 2.0D))
                                        .executes(context -> setScale(context, currentRevision()))
                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                .executes(context -> setScale(context,
                                                        LongArgumentType.getLong(context, "revision")))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> attributeNode(String name,
                                                                              SpeciesSettingsService.Field field) {
        double minimum = field.minimum();
        double maximum = field.maximum();
        return Commands.literal(name)
                .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(minimum, maximum))
                                .executes(context -> setSingle(context, field, currentRevision()))
                                .then(Commands.argument("revision", LongArgumentType.longArg())
                                        .executes(context -> setSingle(context, field,
                                                LongArgumentType.getLong(context, "revision"))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> behaviorNode() {
        return Commands.literal("setbehavior")
                .then(Commands.argument("entity", ResourceLocationArgument.id()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("detection", DoubleArgumentType.doubleArg(0.0D, 256.0D))
                                .then(Commands.argument("disengage", DoubleArgumentType.doubleArg(0.0D, 512.0D))
                                        .then(Commands.argument("action_timeout", IntegerArgumentType.integer(1, 20_000))
                                                .then(Commands.argument("memory_ticks", IntegerArgumentType.integer(0, 20_000))
                                                        .executes(context -> setBehavior(context, currentRevision()))
                                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                                .executes(context -> setBehavior(context,
                                                                        LongArgumentType.getLong(context, "revision")))))))));
    }

    private static long currentRevision() {
        return SpeciesSettingsService.revision();
    }

    private static int settingsHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("BFS session settings")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        source.sendSuccess(() -> Component.literal("  /bfs debug settings list")
                .append(Component.literal("  show fields, units and bounds").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /bfs debug settings get <entity|*>")
                .append(Component.literal("  show values, source, revision and capability").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /bfs debug settings set <entity|*> <field> <value> [revision]")
                .append(Component.literal("  apply one atomic patch").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /bfs debug settings reset [entity|*] [field] [revision]")
                .append(Component.literal("  clear session values").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  /bfs debug settings reload")
                .append(Component.literal("  validate and replace the server baseline").withStyle(ChatFormatting.GRAY)), false);
        source.sendSuccess(() -> Component.literal("  disturbance fields include global enable, reaction, radius, sensitivity, alert ticks, boat threshold and per-source enable, strength and interval ticks.")
                .withStyle(ChatFormatting.GRAY), false);
        source.sendSuccess(() -> Component.literal("Specialized aliases include setspeed, setsprint, settilt, setspawnsize, setscale, sethealth, setdamage, setknockback and setbehavior.")
                .withStyle(ChatFormatting.GRAY), false);
        return 1;
    }

    private static int settingsList(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("BFS settings fields")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        for (SpeciesSettingsService.Field field : SpeciesSettingsService.fields()) {
            source.sendSuccess(() -> Component.literal("  " + field.id() + ": ")
                    .withStyle(ChatFormatting.GREEN)
                    .append(Component.literal(field.unit() + ", " + field.boundsDescription())
                            .withStyle(ChatFormatting.WHITE)), false);
        }
        source.sendSuccess(() -> Component.literal("  Revision: " + SpeciesSettingsService.revision())
                .withStyle(ChatFormatting.GRAY), false);
        return 1;
    }

    private static int settingsGet(CommandContext<CommandSourceStack> context, String target) {
        CommandSourceStack source = context.getSource();
        SpeciesSettingsService.Snapshot snapshot = SpeciesSettingsService.snapshot();
        if ("*".equals(target)) {
            for (SpeciesSettingsService.SpeciesSnapshot species : snapshot.species().values()) {
                sendSpecies(source, species);
            }
            return snapshot.species().size();
        }
        SpeciesSettingsService.SpeciesSnapshot species = snapshot.species().get(target);
        if (species == null) {
            source.sendFailure(Component.literal("Unknown BFS species: " + target));
            return 0;
        }
        sendSpecies(source, species);
        return 1;
    }

    private static void sendSpecies(CommandSourceStack source,
                                    SpeciesSettingsService.SpeciesSnapshot species) {
        source.sendSuccess(() -> Component.literal(species.species() + " settings, revision " + species.revision())
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        for (SpeciesSettingsService.FieldValue value : species.fields().values()) {
            SpeciesSettingsService.CapabilityResult capability = value.capability();
            ChatFormatting color = capability.capability() == SpeciesSettingsService.Capability.SUPPORTED
                    ? ChatFormatting.WHITE : ChatFormatting.YELLOW;
            source.sendSuccess(() -> Component.literal("  " + value.field().id() + ": ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(formatValue(value.value()) + " " + value.field().unit()
                            + " [" + value.source() + "]").withStyle(color))
                    .append(Component.literal(" " + capability.capability().id() + ", " + capability.reason())
                            .withStyle(ChatFormatting.DARK_GRAY)), false);
        }
    }

    private static int settingsSet(CommandContext<CommandSourceStack> context, long expectedRevision) {
        String fieldName = StringArgumentType.getString(context, "field");
        SpeciesSettingsService.Field field = SpeciesSettingsService.Field.parse(fieldName);
        if (field == null) {
            context.getSource().sendFailure(Component.literal("Unknown settings field: " + fieldName));
            return 0;
        }
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(field, DoubleArgumentType.getDouble(context, "value"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                speciesTarget(context), patch));
    }

    private static int settingsReset(CommandContext<CommandSourceStack> context, String target,
                                     Set<SpeciesSettingsService.Field> fields, long expectedRevision) {
        if (fields == null) {
            context.getSource().sendFailure(Component.literal("Unknown settings field. Use /bfs debug settings list."));
            return 0;
        }
        return report(context.getSource(), SpeciesSettingsService.reset(expectedRevision, target, fields));
    }

    private static int settingsReload(CommandContext<CommandSourceStack> context) {
        SpeciesSettingsService.ReloadResult result = SpeciesSettingsConfigBridge.reload();
        CommandSourceStack source = context.getSource();
        if (!result.applied()) {
            source.sendFailure(Component.literal("BFS settings reload rejected. " + result.reason()));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("BFS settings baseline reloaded. Session overrides remain active. Revision "
                + result.revision() + ".").withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int setSpeed(CommandContext<CommandSourceStack> context, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(SpeciesSettingsService.Field.HORIZONTAL_SPEED,
                DoubleArgumentType.getDouble(context, "horizontal"));
        patch.put(SpeciesSettingsService.Field.VERTICAL_SPEED,
                DoubleArgumentType.getDouble(context, "vertical"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                speciesTarget(context), patch));
    }

    private static int setSprint(CommandContext<CommandSourceStack> context, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(SpeciesSettingsService.Field.HORIZONTAL_SPRINT,
                DoubleArgumentType.getDouble(context, "horizontal"));
        patch.put(SpeciesSettingsService.Field.VERTICAL_SPRINT,
                DoubleArgumentType.getDouble(context, "vertical"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                speciesTarget(context), patch));
    }

    private static int setSpawnSize(CommandContext<CommandSourceStack> context, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(SpeciesSettingsService.Field.SPAWN_GROUP_MIN,
                (double) IntegerArgumentType.getInteger(context, "minimum"));
        patch.put(SpeciesSettingsService.Field.SPAWN_GROUP_MAX,
                (double) IntegerArgumentType.getInteger(context, "maximum"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                speciesTarget(context), patch));
    }

    private static int setScale(CommandContext<CommandSourceStack> context, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(SpeciesSettingsService.Field.SCALE_MIN,
                DoubleArgumentType.getDouble(context, "minimum"));
        patch.put(SpeciesSettingsService.Field.SCALE_MAX,
                DoubleArgumentType.getDouble(context, "maximum"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                speciesTarget(context), patch));
    }

    private static int setSingle(CommandContext<CommandSourceStack> context,
                                 SpeciesSettingsService.Field field, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(field, DoubleArgumentType.getDouble(context, "value"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                speciesTarget(context), patch));
    }

    private static int setBehavior(CommandContext<CommandSourceStack> context, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(SpeciesSettingsService.Field.DETECTION_RADIUS,
                DoubleArgumentType.getDouble(context, "detection"));
        patch.put(SpeciesSettingsService.Field.DISENGAGE_DISTANCE,
                DoubleArgumentType.getDouble(context, "disengage"));
        patch.put(SpeciesSettingsService.Field.ACTION_TIMEOUT,
                (double) IntegerArgumentType.getInteger(context, "action_timeout"));
        patch.put(SpeciesSettingsService.Field.MEMORY_TICKS,
                (double) IntegerArgumentType.getInteger(context, "memory_ticks"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                speciesTarget(context), patch));
    }

    private static int report(CommandSourceStack source, SpeciesSettingsService.MutationResult result) {
        if (!result.applied()) {
            source.sendFailure(Component.literal("BFS settings change rejected. " + result.reason()
                    + ". Revision remains " + result.revision() + "."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("BFS settings applied. ")
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal(result.targets().size() + " species, " + result.fields().size()
                        + " fields. Revision " + result.revision() + ".").withStyle(ChatFormatting.WHITE)), true);
        return result.targets().size();
    }

    private static Set<SpeciesSettingsService.Field> parseFieldSet(String value) {
        if (value == null || value.equalsIgnoreCase("all")) return Set.of();
        SpeciesSettingsService.Field field = SpeciesSettingsService.Field.parse(value);
        return field == null ? null : EnumSet.of(field);
    }

    private static String formatValue(double value) {
        if (value >= Long.MAX_VALUE || value <= Long.MIN_VALUE) return Double.toString(value);
        if (value == Math.rint(value)) return Long.toString((long) value);
        return String.format(java.util.Locale.ROOT, "%.3f", value);
    }

    private static int start(CommandContext<CommandSourceStack> context, String category, int ticks,
                             Collection<? extends Entity> targets) {
        CommandSourceStack source = context.getSource();
        BfsDebugManager.StartResult result = BfsDebugManager.start(source, category, ticks, targets);
        if (!result.started()) {
            if (result.activeSession() != null) {
                source.sendSuccess(() -> Component.literal("BFS debug capture is already active. Current session status follows.")
                        .withStyle(ChatFormatting.YELLOW), false);
                return status(context);
            }
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
            context.getSource().sendSuccess(() -> Component.literal("BFS debug capture is already inactive.")
                    .withStyle(ChatFormatting.GRAY), false);
            return 1;
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
            BfsDebugManager.StopSummary lastStop = status.lastStop();
            if (!"none".equals(lastStop.reason())) {
                context.getSource().sendSuccess(() -> Component.literal("  Last stop: " + lastStop.reason() + ". Records: "
                        + lastStop.accepted() + " accepted, " + lastStop.dropped() + " dropped. Incomplete: "
                        + lastStop.incomplete() + ". Reason: " + lastStop.incompleteReason() + ".")
                        .withStyle(lastStop.incomplete() ? ChatFormatting.YELLOW : ChatFormatting.GREEN), false);
                context.getSource().sendSuccess(() -> Component.literal("  Output: " + lastStop.outputPath())
                        .withStyle(ChatFormatting.GRAY), false);
            }
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
        context.getSource().sendSuccess(() -> Component.literal("  Capture tick overhead p95: "
                + (session.traceP95Nanos() / 1_000_000.0D) + " ms across " + session.traceSampleCount() + " ticks.")
                .withStyle(ChatFormatting.GRAY), false);
        context.getSource().sendSuccess(() -> Component.literal("  Wall time remaining: "
                + Math.max(0L, (session.wallDeadlineMillis() - now) / 1_000L) + " seconds. Output: " + session.outputPath())
                .withStyle(ChatFormatting.GRAY), false);
        return 1;
    }
}
