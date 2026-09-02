package tfar.bensfintasticsharks.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.phys.Vec3;
import tfar.bensfintasticsharks.trade.BfsFishermanTrades;
import tfar.bensfintasticsharks.trade.BfsFishermanTrades.TradeDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Builds the loader-neutral {@code /bfs summon villager [trade]} command branch. */
public final class BfsVillagerCommand {

    private static final String RANDOM_TRADE = "random";
    private static final int MASTER_LEVEL_XP = 250;

    /** The same immutable catalogue used to populate natural master fishermen. */
    private static final List<TradeDefinition> BFS_TRADES = BfsFishermanTrades.CATALOGUE;

    private static final SuggestionProvider<CommandSourceStack> ENTITY_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(new String[]{"villager"}, builder);

    private static final SuggestionProvider<CommandSourceStack> TRADE_SUGGESTIONS =
            (context, builder) -> {
                List<String> suggestions = new ArrayList<>(BFS_TRADES.size() + 1);
                suggestions.add(RANDOM_TRADE);
                BFS_TRADES.forEach(trade -> suggestions.add(trade.id()));
                return SharedSuggestionProvider.suggest(suggestions, builder);
            };

    private BfsVillagerCommand() {
    }

    /** Returns the branch attached below the existing {@code /bfs} root. */
    public static LiteralArgumentBuilder<CommandSourceStack> createNode() {
        return Commands.literal("summon")
                .then(Commands.argument("entity", StringArgumentType.word())
                        .suggests(ENTITY_SUGGESTIONS)
                        .executes(context -> summon(context.getSource(),
                                StringArgumentType.getString(context, "entity"), null))
                        .then(Commands.argument("trade", StringArgumentType.word())
                                .suggests(TRADE_SUGGESTIONS)
                                .executes(context -> summon(context.getSource(),
                                        StringArgumentType.getString(context, "entity"),
                                        StringArgumentType.getString(context, "trade")))));
    }

    private static int summon(CommandSourceStack source, String entityName, String requestedTrade) {
        if (!"villager".equalsIgnoreCase(entityName)) {
            source.sendFailure(Component.literal("Unknown BFS showcase entity: " + entityName
                    + ". Expected villager."));
            return 0;
        }

        TradeDefinition selectedTrade = selectTrade(source, requestedTrade);
        if (selectedTrade == null) {
            return 0;
        }

        ServerLevel level = source.getLevel();
        Vec3 position = source.getPosition();
        Villager villager = EntityType.VILLAGER.create(level, null, null,
                BlockPos.containing(position), MobSpawnType.COMMAND, false, false);
        if (villager == null) {
            source.sendFailure(Component.literal("Could not create the BFS showcase villager."));
            return 0;
        }

        // EntityType#create performs vanilla command-spawn initialization. On Forge,
        // its transformed internal call also fires the supported FinalizeSpawn event.
        villager.moveTo(position.x, position.y, position.z, source.getRotation().y, 0.0F);
        villager.setVillagerData(villager.getVillagerData()
                .setProfession(VillagerProfession.FISHERMAN)
                .setLevel(5));
        villager.setVillagerXp(MASTER_LEVEL_XP);
        villager.refreshBrain(level);
        villager.setPersistenceRequired();

        // Generate the same two random listings per tier that a naturally levelled
        // fisherman receives, then guarantee the requested BFS listing is present.
        MerchantOffers offers = new MerchantOffers();
        villager.setOffers(offers);
        for (int levelNumber = 1; levelNumber <= 5; levelNumber++) {
            addNormalOffers(villager, offers, levelNumber);
        }

        // Always put the explicitly selected offer last so showcase players can find
        // it immediately, even if another mod also injected a matching offer.
        offers.removeIf(offer -> offer.getResult().is(selectedTrade.result()));
        MerchantOffer guaranteedOffer = selectedTrade.listing().getOffer(villager, villager.getRandom());
        if (guaranteedOffer == null) {
            source.sendFailure(Component.literal("Could not create BFS trade: " + selectedTrade.id()));
            return 0;
        }
        offers.add(guaranteedOffer);

        if (!level.addFreshEntity(villager)) {
            source.sendFailure(Component.literal("Could not add the BFS showcase villager to the world."));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("Summoned a master fisherman with guaranteed BFS trade: ")
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal(selectedTrade.id()).withStyle(ChatFormatting.AQUA)), true);
        return 1;
    }

    private static TradeDefinition selectTrade(CommandSourceStack source, String requestedTrade) {
        if (requestedTrade == null || RANDOM_TRADE.equalsIgnoreCase(requestedTrade)) {
            return BFS_TRADES.get(source.getLevel().getRandom().nextInt(BFS_TRADES.size()));
        }

        String normalized = requestedTrade.toLowerCase(Locale.ROOT);
        for (TradeDefinition trade : BFS_TRADES) {
            if (trade.id().equals(normalized)) {
                return trade;
            }
        }

        source.sendFailure(Component.literal("Unknown BFS trade: " + requestedTrade + ". Valid trades: "
                + RANDOM_TRADE + ", " + String.join(", ", tradeIds())));
        return null;
    }

    private static void addNormalOffers(Villager villager, MerchantOffers offers, int levelNumber) {
        var professionTrades = VillagerTrades.TRADES.get(VillagerProfession.FISHERMAN);
        if (professionTrades == null) {
            return;
        }

        VillagerTrades.ItemListing[] listings = professionTrades.get(levelNumber);
        if (listings == null || listings.length == 0) {
            return;
        }

        List<MerchantOffer> vanillaCandidates = new ArrayList<>(listings.length);
        for (VillagerTrades.ItemListing listing : listings) {
            MerchantOffer offer = listing.getOffer(villager, villager.getRandom());
            // The Forge trade event adds BFS's weighted listing to the master pool.
            // Leave that out here: this villager gets two normal offers at every tier,
            // followed by the separately guaranteed showcase offer.
            if (offer != null && !isBfsOffer(offer)) {
                vanillaCandidates.add(offer);
            }
        }

        int offerCount = Math.min(2, vanillaCandidates.size());
        Set<Integer> selectedIndexes = new HashSet<>();
        while (selectedIndexes.size() < offerCount) {
            selectedIndexes.add(villager.getRandom().nextInt(vanillaCandidates.size()));
        }

        for (int index : selectedIndexes) {
            offers.add(vanillaCandidates.get(index));
        }
    }

    private static boolean isBfsOffer(MerchantOffer offer) {
        return BFS_TRADES.stream().anyMatch(trade -> offer.getResult().is(trade.result()));
    }

    private static List<String> tradeIds() {
        return BFS_TRADES.stream().map(TradeDefinition::id).toList();
    }
}
