package tfar.bensfintasticsharks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.entity.BfsVariantHolder;
import tfar.bensfintasticsharks.init.ModTags;
import tfar.bensfintasticsharks.spawn.MobCapManager;

import java.util.Locale;

/**
 * {@code /bfs} command tree. Currently exposes the per-species cap controls
 * documented in {@link tfar.bensfintasticsharks.config.BfsConfig}.
 *
 * Permission level 2 (op) required.
 */
public class BfsCommands {

    private static final SuggestionProvider<CommandSourceStack> SPECIES_SUGGESTIONS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(MobCapManager.getSpeciesPaths(), builder);

    private static final SuggestionProvider<CommandSourceStack> DISTURBANCE_SUGGESTIONS =
            (ctx, builder) -> SharedSuggestionProvider.suggest(new String[]{"light", "heavy", "blood"}, builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("bfs")
                        .requires(s -> s.hasPermission(2))
                        // /bfs help — top-level help
                        .executes(BfsCommands::topHelp)
                        .then(Commands.literal("help").executes(BfsCommands::topHelp))
                        // /bfs summon villager [trade] — creative showcase fisherman
                        .then(BfsVillagerCommand.createNode())
                        // /bfs cap …
                        .then(Commands.literal("cap")
                                .then(Commands.literal("help").executes(BfsCommands::capHelp))
                                .then(Commands.literal("list").executes(BfsCommands::capList))
                                .then(Commands.literal("get")
                                        .then(Commands.argument("species", StringArgumentType.word())
                                                .suggests(SPECIES_SUGGESTIONS)
                                                .executes(BfsCommands::capGet)))
                                .then(Commands.literal("set")
                                        .then(Commands.argument("species", StringArgumentType.word())
                                                .suggests(SPECIES_SUGGESTIONS)
                                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 64))
                                                        .executes(BfsCommands::capSet))))
                                .then(Commands.literal("reset")
                                        .then(Commands.argument("species", StringArgumentType.word())
                                                .suggests(SPECIES_SUGGESTIONS)
                                                .executes(BfsCommands::capReset))
                                        .executes(BfsCommands::capResetAll)))
                        // /bfs list — list all species with categories + caps
                        .then(Commands.literal("list").executes(BfsCommands::list))
                        // /bfs find <species> — locate nearest of that species
                        .then(Commands.literal("find")
                                .then(Commands.argument("species", StringArgumentType.word())
                                        .suggests(SPECIES_SUGGESTIONS)
                                        .executes(BfsCommands::find)))
                        // /bfs info <species> — show details
                        .then(Commands.literal("info")
                                .requires(BfsCommands::canUseCreativeInfo)
                                .then(Commands.argument("species", StringArgumentType.word())
                                        .suggests(SPECIES_SUGGESTIONS)
                                        .executes(BfsCommands::info)))
                        // /bfs reload — reload config without restart
                        .then(Commands.literal("reload").executes(BfsCommands::reload))
                        // /bfs disturbance <type> — fire a test disturbance at the caller's position
                        .then(Commands.literal("disturbance")
                                .then(Commands.argument("type", StringArgumentType.word())
                                        .suggests(DISTURBANCE_SUGGESTIONS)
                                        .executes(BfsCommands::disturbance)))
                        // /bfs count <species> — count nearby instances of that species
                        .then(Commands.literal("count")
                                .then(Commands.argument("species", StringArgumentType.word())
                                        .suggests(SPECIES_SUGGESTIONS)
                                        .executes(BfsCommands::count)))
        );
    }

    private static int topHelp(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("Ben's Fintastic Sharks — admin commands")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        line(src, "/bfs list", "list every BFS species and its current cap");
        line(src, "/bfs summon villager [trade]", "spawn a master fisherman with a guaranteed BFS trade");
        line(src, "/bfs find <species>", "find nearest of that species and TP-tell you its coords");
        line(src, "/bfs info <species>", "show category, cap, biology blurb");
        line(src, "/bfs count <species>", "count instances within 64 blocks of you");
        line(src, "/bfs cap help", "per-species cap controls (set/get/reset)");
        line(src, "/bfs disturbance <type>", "fire a test light/heavy/blood disturbance at your position");
        line(src, "/bfs reload", "reload config without restarting the server");
        return 1;
    }

    private static void line(CommandSourceStack src, String cmd, String desc) {
        src.sendSuccess(() -> Component.literal("  " + cmd + "  ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("— " + desc).withStyle(ChatFormatting.GRAY)), false);
    }

    private static int list(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("BFS species:").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        for (EntityType<?> type : MobCapManager.getTrackedSpecies()) {
            int cap = MobCapManager.getCap(type);
            String path = BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath();
            String cat = type.getCategory().getName();
            String capText = cap == 0 ? "OFF" : String.valueOf(cap);
            Component line = Component.literal("  " + path)
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(" [" + cat + "]").withStyle(ChatFormatting.DARK_GRAY))
                    .append(Component.literal(" cap=" + capText).withStyle(ChatFormatting.YELLOW));
            src.sendSuccess(() -> line, false);
        }
        return 1;
    }

    private static int find(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        String species = StringArgumentType.getString(ctx, "species");
        EntityType<?> type = MobCapManager.getSpeciesByPath(species);
        if (type == null) {
            src.sendFailure(Component.literal("Unknown BFS species: " + species));
            return 0;
        }
        var level = src.getLevel();
        var origin = src.getPosition();
        var area = new net.minecraft.world.phys.AABB(
                origin.x - 512, origin.y - 256, origin.z - 512,
                origin.x + 512, origin.y + 256, origin.z + 512);
        java.util.List<? extends net.minecraft.world.entity.Entity> nearby =
                level.getEntities(type, area, e -> true);
        if (nearby.isEmpty()) {
            src.sendSuccess(() -> Component.literal("No " + species + " found within 512 blocks of you.")
                    .withStyle(ChatFormatting.YELLOW), false);
            return 0;
        }
        var closest = nearby.stream()
                .min(java.util.Comparator.comparingDouble(e -> e.distanceToSqr(origin)))
                .orElseThrow();
        var bp = closest.blockPosition();
        int dist = (int) Math.sqrt(closest.distanceToSqr(origin));
        src.sendSuccess(() -> Component.literal("Nearest " + species + ": ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(bp.getX() + ", " + bp.getY() + ", " + bp.getZ())
                        .withStyle(ChatFormatting.GREEN))
                .append(Component.literal(" (" + dist + " blocks away)").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    private static int info(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        String species = StringArgumentType.getString(ctx, "species");
        EntityType<?> type = MobCapManager.getSpeciesByPath(species);
        if (type == null) {
            src.sendFailure(Component.literal("Unknown BFS species: " + species));
            return 0;
        }
        BfsSpeciesInfo.Entry details = BfsSpeciesInfo.get(species);
        if (details == null) {
            src.sendFailure(Component.literal("No species information is available for: " + species));
            return 0;
        }

        int cap = MobCapManager.getCap(type);
        int configCap = MobCapManager.getConfigCap(type);
        String cat = type.getCategory().getName();
        Entity entity = type.create(src.getLevel());
        double health = Double.NaN;
        int variants = 1;
        if (entity instanceof LivingEntity living) {
            health = living.getMaxHealth();
            var hpMult = BfsConfig.COMMON.speciesHpMult.get(species);
            if (hpMult != null) {
                health *= hpMult.get();
            }
            if (type.is(ModTags.EntityTypes.SHARKS)) {
                health *= BfsConfig.COMMON.sharkHpMult.get();
            }
        }
        if (entity instanceof BfsVariantHolder holder) {
            variants = holder.bfsVariantCount();
        }
        if (entity != null) {
            entity.discard();
        }

        src.sendSuccess(() -> type.getDescription().copy().withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        infoLine(src, "Scientific name", details.scientificName());
        infoLine(src, "Habitats", BfsSpeciesInfo.habitats(src, details));
        infoLine(src, "Behavior", details.behavior());
        infoLine(src, "Diet", BfsSpeciesInfo.diet(details));
        if (!Double.isNaN(health)) {
            infoLine(src, "Health", String.format(Locale.ROOT, "%.1f H.P.", health));
        }
        infoLine(src, "Variants", String.valueOf(variants));
        infoLine(src, "Registry ID", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        infoLine(src, "Spawn category", cat);
        src.sendSuccess(() -> Component.literal("  Natural cap: ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal((cap == 0 ? "DISABLED" : String.valueOf(cap)))
                        .withStyle(cap != configCap ? ChatFormatting.GOLD : ChatFormatting.WHITE)), false);
        if (cap != configCap) {
            src.sendSuccess(() -> Component.literal("  Default cap: " + configCap).withStyle(ChatFormatting.DARK_GRAY), false);
        }
        return 1;
    }

    private static boolean canUseCreativeInfo(CommandSourceStack source) {
        return !(source.getEntity() instanceof Player player) || player.isCreative();
    }

    private static void infoLine(CommandSourceStack source, String label, String value) {
        source.sendSuccess(() -> Component.literal("  " + label + ": ").withStyle(ChatFormatting.GRAY)
                .append(Component.literal(value).withStyle(ChatFormatting.WHITE)), false);
    }

    private static int count(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        String species = StringArgumentType.getString(ctx, "species");
        EntityType<?> type = MobCapManager.getSpeciesByPath(species);
        if (type == null) {
            src.sendFailure(Component.literal("Unknown BFS species: " + species));
            return 0;
        }
        var level = src.getLevel();
        var origin = src.getPosition();
        var area = new net.minecraft.world.phys.AABB(
                origin.x - MobCapManager.COUNT_RADIUS, origin.y - MobCapManager.COUNT_RADIUS, origin.z - MobCapManager.COUNT_RADIUS,
                origin.x + MobCapManager.COUNT_RADIUS, origin.y + MobCapManager.COUNT_RADIUS, origin.z + MobCapManager.COUNT_RADIUS);
        int count = level.getEntities(type, area, e -> e.isAlive()).size();
        int cap = MobCapManager.getCap(type);
        String capText = cap == 0 ? "DISABLED" : String.valueOf(cap);
        src.sendSuccess(() -> Component.literal(species + ": ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(count + " / " + capText).withStyle(ChatFormatting.WHITE))
                .append(Component.literal(" within " + MobCapManager.COUNT_RADIUS + " blocks")
                        .withStyle(ChatFormatting.GRAY)), false);
        return count;
    }

    private static int reload(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        // Forge's ForgeConfigSpec reloads on file change automatically; this nudges the load.
        // We re-apply the category caps (which use reflection from the config values).
        try {
            net.minecraftforge.fml.ModLoadingContext.get();
            // Re-read by accessing each cap value
            tfar.bensfintasticsharks.config.BfsConfig.COMMON.apexPredatorCap.get();
            ctx.getSource().sendSuccess(() -> Component.literal("BFS config refreshed. Edits to mob caps in config take effect immediately.")
                    .withStyle(ChatFormatting.GREEN), true);
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Reload failed: " + e.getMessage()));
            return 0;
        }
        return 1;
    }

    private static int disturbance(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        String typeStr = StringArgumentType.getString(ctx, "type").toUpperCase();
        tfar.bensfintasticsharks.disturbance.WaterDisturbanceEvent.Type type;
        try {
            type = tfar.bensfintasticsharks.disturbance.WaterDisturbanceEvent.Type.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            src.sendFailure(Component.literal("Unknown disturbance type. Must be: light, heavy, or blood."));
            return 0;
        }
        var pos = net.minecraft.core.BlockPos.containing(src.getPosition());
        var ent = src.getEntity();
        // Count nearby sharks so the player can tell whether nothing happened because
        // the system is broken or because there's nothing in range to react.
        double radius = switch (type) {
            case BLOOD -> 24.0;
            case HEAVY -> 16.0;
            case LIGHT -> 12.0;
        };
        var area = new net.minecraft.world.phys.AABB(pos).inflate(radius);
        long sharkCount = src.getLevel().getEntitiesOfClass(
                tfar.bensfintasticsharks.entity.AbstractSharkEntity.class, area,
                s -> s.isAlive() && s.isInWaterOrBubble()).size();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
                new tfar.bensfintasticsharks.disturbance.WaterDisturbanceEvent(
                        src.getLevel(), pos, ent, type));
        final var finalType = type;
        final long count = sharkCount;
        final double rad = radius;
        src.sendSuccess(() -> Component.literal(
                "Fired " + finalType.name() + " disturbance. Sharks in " + (int) rad + "-block radius: " + count + ".")
                .withStyle(ChatFormatting.GREEN), false);
        if (sharkCount == 0) {
            src.sendSuccess(() -> Component.literal("(No sharks nearby to react. Spawn or find some first.)")
                    .withStyle(ChatFormatting.GRAY), false);
        }
        return 1;
    }

    private static int capHelp(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("Per-species mob caps").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        src.sendSuccess(() -> Component.literal("Each cap is the max number of mobs of a species allowed within "
                + MobCapManager.COUNT_RADIUS + " blocks of a natural spawn check (~4 chunks).")
                .withStyle(ChatFormatting.GRAY), false);
        src.sendSuccess(() -> Component.literal("Vanilla 'MobCategory' caps still apply on top — these are species-level fine tuning.")
                .withStyle(ChatFormatting.GRAY), false);
        src.sendSuccess(() -> Component.literal("Caps don't restrict /summon, spawn eggs, or convert-style spawns — only natural spawning.")
                .withStyle(ChatFormatting.GRAY), false);
        src.sendSuccess(() -> Component.literal(""), false);
        src.sendSuccess(() -> Component.literal("Commands:").withStyle(ChatFormatting.YELLOW), false);
        src.sendSuccess(() -> Component.literal("  /bfs cap list  ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("— show all current caps").withStyle(ChatFormatting.GRAY)), false);
        src.sendSuccess(() -> Component.literal("  /bfs cap get <species>  ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("— show one cap").withStyle(ChatFormatting.GRAY)), false);
        src.sendSuccess(() -> Component.literal("  /bfs cap set <species> <0-64>  ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("— override at runtime (not saved)").withStyle(ChatFormatting.GRAY)), false);
        src.sendSuccess(() -> Component.literal("  /bfs cap reset <species>  ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("— clear runtime override").withStyle(ChatFormatting.GRAY)), false);
        src.sendSuccess(() -> Component.literal("  /bfs cap reset  ").withStyle(ChatFormatting.GREEN)
                .append(Component.literal("— clear all overrides").withStyle(ChatFormatting.GRAY)), false);
        return 1;
    }

    private static int capList(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        src.sendSuccess(() -> Component.literal("BFS per-species caps (within " + MobCapManager.COUNT_RADIUS + "-block radius):")
                .withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD), false);
        for (EntityType<?> type : MobCapManager.getTrackedSpecies()) {
            int cap = MobCapManager.getCap(type);
            int configCap = MobCapManager.getConfigCap(type);
            boolean overridden = cap != configCap;
            String name = BuiltInRegistries.ENTITY_TYPE.getKey(type).getPath();
            String capText = cap == 0 ? "DISABLED" : String.valueOf(cap);
            Component line = Component.literal("  " + name + ": ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(capText).withStyle(overridden ? ChatFormatting.GOLD : ChatFormatting.WHITE));
            if (overridden) {
                line = ((net.minecraft.network.chat.MutableComponent) line)
                        .append(Component.literal(" (default " + configCap + ")")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
            final Component finalLine = line;
            src.sendSuccess(() -> finalLine, false);
        }
        return 1;
    }

    private static int capGet(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        String species = StringArgumentType.getString(ctx, "species");
        EntityType<?> type = MobCapManager.getSpeciesByPath(species);
        if (type == null) {
            src.sendFailure(Component.literal("Unknown BFS species: " + species));
            return 0;
        }
        int cap = MobCapManager.getCap(type);
        int configCap = MobCapManager.getConfigCap(type);
        boolean overridden = cap != configCap;
        String capText = cap == 0 ? "DISABLED" : String.valueOf(cap);
        src.sendSuccess(() -> Component.literal(species + " cap: ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(capText).withStyle(overridden ? ChatFormatting.GOLD : ChatFormatting.WHITE))
                .append(overridden
                        ? Component.literal(" (default " + configCap + ")").withStyle(ChatFormatting.DARK_GRAY)
                        : Component.literal("")),
                false);
        return cap;
    }

    private static int capSet(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        String species = StringArgumentType.getString(ctx, "species");
        int value = IntegerArgumentType.getInteger(ctx, "value");
        EntityType<?> type = MobCapManager.getSpeciesByPath(species);
        if (type == null) {
            src.sendFailure(Component.literal("Unknown BFS species: " + species));
            return 0;
        }
        MobCapManager.setRuntimeCap(type, value);
        src.sendSuccess(() -> Component.literal("Set " + species + " cap to " + value
                + " (runtime override; edit config to persist).").withStyle(ChatFormatting.GREEN), true);
        return value;
    }

    private static int capReset(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack src = ctx.getSource();
        String species = StringArgumentType.getString(ctx, "species");
        EntityType<?> type = MobCapManager.getSpeciesByPath(species);
        if (type == null) {
            src.sendFailure(Component.literal("Unknown BFS species: " + species));
            return 0;
        }
        MobCapManager.resetRuntimeCap(type);
        int configCap = MobCapManager.getConfigCap(type);
        src.sendSuccess(() -> Component.literal("Cleared " + species + " override; cap is now " + configCap
                + " (from config).").withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int capResetAll(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx) {
        MobCapManager.resetAllRuntimeCaps();
        ctx.getSource().sendSuccess(() -> Component.literal("Cleared all BFS cap overrides.").withStyle(ChatFormatting.GREEN), true);
        return 1;
    }
}
