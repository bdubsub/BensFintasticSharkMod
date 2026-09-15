package tfar.bensfintasticsharks.debug;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import tfar.bensfintasticsharks.config.SpeciesSettingsConfigBridge;
import tfar.bensfintasticsharks.entity.SpeciesSettingsService;
import tfar.bensfintasticsharks.follow.BfsFollowManager;
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
                            "follow", "disturbance", "boat"}, builder);
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
        return Commands.literal("debug")
                .then(on)
                .then(Commands.literal("off").executes(BfsDebugCommands::stop))
                .then(Commands.literal("status").executes(BfsDebugCommands::status))
                .then(algaeNode())
                .then(followNode())
                .then(settingsNode())
                .then(speedNode())
                .then(sprintNode())
                .then(spawnSizeNode())
                .then(scaleNode())
                .then(attributeNode("sethealth", SpeciesSettingsService.Field.HEALTH_MULTIPLIER))
                .then(attributeNode("setdamage", SpeciesSettingsService.Field.DAMAGE_MULTIPLIER))
                .then(attributeNode("setknockback", SpeciesSettingsService.Field.KNOCKBACK_RESISTANCE))
                .then(behaviorNode());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> algaeNode() {
        return Commands.literal("algae").requires(source -> source.hasPermission(2))
                .then(Commands.literal("scan")
                        .then(Commands.argument("minX", IntegerArgumentType.integer())
                                .then(Commands.argument("minZ", IntegerArgumentType.integer())
                                        .then(Commands.argument("size", IntegerArgumentType.integer(1, 64))
                                                .executes(BfsDebugCommands::scanAlgae)))));
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
                        .then(Commands.argument("entity", StringArgumentType.word()).suggests(SPECIES_SUGGESTIONS)
                                .executes(context -> settingsGet(context,
                                        StringArgumentType.getString(context, "entity")))))
                .then(Commands.literal("set")
                        .then(Commands.argument("entity", StringArgumentType.word()).suggests(SPECIES_SUGGESTIONS)
                                .then(Commands.argument("field", StringArgumentType.word()).suggests(FIELD_SUGGESTIONS)
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg())
                                                .executes(context -> settingsSet(context, currentRevision()))
                                                .then(Commands.argument("revision", LongArgumentType.longArg())
                                                        .executes(context -> settingsSet(context,
                                                                LongArgumentType.getLong(context, "revision"))))))))
                .then(Commands.literal("reset")
                        .executes(context -> settingsReset(context, "*", Set.of(), currentRevision()))
                        .then(Commands.argument("entity", StringArgumentType.word()).suggests(SPECIES_SUGGESTIONS)
                                .executes(context -> settingsReset(context,
                                        StringArgumentType.getString(context, "entity"), Set.of(), currentRevision()))
                                .then(Commands.argument("field", StringArgumentType.word()).suggests(FIELD_SUGGESTIONS)
                                        .executes(context -> settingsReset(context,
                                                StringArgumentType.getString(context, "entity"),
                                                parseFieldSet(StringArgumentType.getString(context, "field")), currentRevision()))
                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                .executes(context -> settingsReset(context,
                                                        StringArgumentType.getString(context, "entity"),
                                                        parseFieldSet(StringArgumentType.getString(context, "field")),
                                                        LongArgumentType.getLong(context, "revision"))))))
                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                .executes(context -> settingsReset(context, "*", Set.of(),
                                        LongArgumentType.getLong(context, "revision")))))
                .then(Commands.literal("reload").executes(BfsDebugCommands::settingsReload));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> speedNode() {
        return Commands.literal("setspeed")
                .then(Commands.argument("entity", StringArgumentType.word()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("horizontal", DoubleArgumentType.doubleArg(0.0D, 20.0D))
                                .then(Commands.argument("vertical", DoubleArgumentType.doubleArg(0.0D, 20.0D))
                                        .executes(context -> setSpeed(context, currentRevision()))
                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                .executes(context -> setSpeed(context,
                                                        LongArgumentType.getLong(context, "revision")))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> sprintNode() {
        return Commands.literal("setsprint")
                .then(Commands.argument("entity", StringArgumentType.word()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("horizontal", DoubleArgumentType.doubleArg(0.0D, 4.0D))
                                .then(Commands.argument("vertical", DoubleArgumentType.doubleArg(0.0D, 4.0D))
                                        .executes(context -> setSprint(context, currentRevision()))
                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                .executes(context -> setSprint(context,
                                                        LongArgumentType.getLong(context, "revision")))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> spawnSizeNode() {
        return Commands.literal("setspawnsize")
                .then(Commands.argument("entity", StringArgumentType.word()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("minimum", IntegerArgumentType.integer(1, 32))
                                .then(Commands.argument("maximum", IntegerArgumentType.integer(1, 32))
                                        .executes(context -> setSpawnSize(context, currentRevision()))
                                        .then(Commands.argument("revision", LongArgumentType.longArg())
                                                .executes(context -> setSpawnSize(context,
                                                        LongArgumentType.getLong(context, "revision")))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> scaleNode() {
        return Commands.literal("setscale")
                .then(Commands.argument("entity", StringArgumentType.word()).suggests(SPECIES_SUGGESTIONS)
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
                .then(Commands.argument("entity", StringArgumentType.word()).suggests(SPECIES_SUGGESTIONS)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(minimum, maximum))
                                .executes(context -> setSingle(context, field, currentRevision()))
                                .then(Commands.argument("revision", LongArgumentType.longArg())
                                        .executes(context -> setSingle(context, field,
                                                LongArgumentType.getLong(context, "revision"))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> behaviorNode() {
        return Commands.literal("setbehavior")
                .then(Commands.argument("entity", StringArgumentType.word()).suggests(SPECIES_SUGGESTIONS)
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
        source.sendSuccess(() -> Component.literal("Specialized aliases include setspeed, setsprint, setspawnsize, setscale, sethealth, setdamage, setknockback and setbehavior.")
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
                    .append(Component.literal(field.unit() + ", " + field.minimum() + " to "
                            + field.maximum()).withStyle(ChatFormatting.WHITE)), false);
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
                StringArgumentType.getString(context, "entity"), patch));
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
                StringArgumentType.getString(context, "entity"), patch));
    }

    private static int setSprint(CommandContext<CommandSourceStack> context, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(SpeciesSettingsService.Field.HORIZONTAL_SPRINT,
                DoubleArgumentType.getDouble(context, "horizontal"));
        patch.put(SpeciesSettingsService.Field.VERTICAL_SPRINT,
                DoubleArgumentType.getDouble(context, "vertical"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                StringArgumentType.getString(context, "entity"), patch));
    }

    private static int setSpawnSize(CommandContext<CommandSourceStack> context, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(SpeciesSettingsService.Field.SPAWN_GROUP_MIN,
                (double) IntegerArgumentType.getInteger(context, "minimum"));
        patch.put(SpeciesSettingsService.Field.SPAWN_GROUP_MAX,
                (double) IntegerArgumentType.getInteger(context, "maximum"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                StringArgumentType.getString(context, "entity"), patch));
    }

    private static int setScale(CommandContext<CommandSourceStack> context, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(SpeciesSettingsService.Field.SCALE_MIN,
                DoubleArgumentType.getDouble(context, "minimum"));
        patch.put(SpeciesSettingsService.Field.SCALE_MAX,
                DoubleArgumentType.getDouble(context, "maximum"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                StringArgumentType.getString(context, "entity"), patch));
    }

    private static int setSingle(CommandContext<CommandSourceStack> context,
                                 SpeciesSettingsService.Field field, long expectedRevision) {
        Map<SpeciesSettingsService.Field, Double> patch = new EnumMap<>(SpeciesSettingsService.Field.class);
        patch.put(field, DoubleArgumentType.getDouble(context, "value"));
        return report(context.getSource(), SpeciesSettingsService.apply(expectedRevision,
                StringArgumentType.getString(context, "entity"), patch));
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
                StringArgumentType.getString(context, "entity"), patch));
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
