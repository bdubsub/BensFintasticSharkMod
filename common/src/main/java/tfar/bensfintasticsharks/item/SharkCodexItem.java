package tfar.bensfintasticsharks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

/**
 * Right-clicked Shark Codex opens a vanilla-style written-book reader populated
 * with the species pages baked into {@link #PAGES}. The codex item itself stays
 * in the player's hand; the reader just shows a virtual book.
 *
 * <p>This class lives in the common module and must not reference any
 * {@code net.minecraft.client.*} types directly or the JVM will fail to load
 * it on a dedicated server (it would crash during {@code ModItems.<clinit>}).
 * The actual GUI is opened through the {@link #CLIENT_OPENER} consumer, which
 * the Forge-side client setup wires up to a class that does live in the client
 * runtime.</p>
 */
public class SharkCodexItem extends Item {

    /**
     * Set by the Forge client init to a function that opens the book reader.
     * Default is a no-op so the dedicated server can load this class fine.
     */
    public static Consumer<Player> CLIENT_OPENER = player -> { };

    public SharkCodexItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack codex = player.getItemInHand(hand);
        if (!level.isClientSide) {
            return InteractionResultHolder.success(codex);
        }
        CLIENT_OPENER.accept(player);
        return InteractionResultHolder.sidedSuccess(codex, true);
    }

    private static Component page(String title, String body) {
        return Component.empty()
                .append(Component.literal(title).withStyle(ChatFormatting.BOLD, ChatFormatting.DARK_BLUE))
                .append(Component.literal("\n\n"))
                .append(Component.literal(body));
    }

    /** Pages baked into the codex. */
    public static final List<Component> PAGES = List.of(
            page("Ben's Shark Codex",
                    "A field journal for the sharks of these seas. Find one species per page. Each entry covers its temperament, habitat, and the bare minimum you need to come home with your toes."),
            page("Great White",
                    "Largest of the predatory sharks. Patrols cool open ocean. Curious enough to investigate a thrown bait, hostile enough to remember if you bleed. Disengages when targets leave the water for long. Treat as the apex of any reef."),
            page("Great Hammerhead",
                    "Hunts coastal flats and warm shallows. Strong sense for blood; it will arrive faster than its size suggests. Less persistent than the white but harder to spot until it is already on you."),
            page("Common Thresher",
                    "Tall tail, slim body, deep water dweller. Targets schooling fish more than divers. Will hold a grudge once damaged. Watch for the tail strike when threatened."),
            page("Shortfin Mako",
                    "Fast, lean, blue-grey. Lives offshore in temperate water. Pursues prey relentlessly once committed and refuses to give ground; small pods will close on a wounded mako together."),
            page("Tiger Shark",
                    "Reef and lagoon resident, opportunistic to a fault. Will swallow anything that looks like food. Aggressive at night and during slack tide."),
            page("Oceanic Whitetip",
                    "Open-water shark with stark white fin tips. Slow approach, persistent presence. Famous for hanging on long after every other shark has lost interest."),
            page("Sandtiger",
                    "Sandy-floored bays and wrecks. Bristled jaw makes it look more dangerous than it is, but the bite still reaches the bone. Often docile until pressed."),
            page("Blacktip Reef",
                    "Black-tipped fins, light belly, shallow reef shark. Skittish in calm water, bolder in groups. Watches divers more than it bites them."),
            page("On Reading the Sharks",
                    "Look at the state of the water. Calm water and a curious shark is a warning. Bloody water and a hostile shark is a sentence. The mod's disturbance system treats your splashes and bait as one or both of those."),
            page("Disturbances",
                    "Drop a fish in the water and nearby sharks investigate. Drop a bleeding mob and they hunt. Throw the Shark Trident to call a heavy disturbance. The radius is roughly thirty blocks for light, sixty for blood."),
            page("Conservation",
                    "Some species are tagged protected. Killing them without need raises no banner but it does invite the wrath of the rest of the pod. The codex notes which species fall under this tag."),
            page("Prey",
                    "Sharks chase any aquatic mob in the prey list. The list spans cod, squid, seals, stingrays, lobsters, octopuses, nautilus, eels, sea turtles, and dolphins. Stay clear of the trail."),
            page("Lost Manuscript",
                    "Loose pages found in sunken loot. Nine pages make a codex page, nine of those make a volume, and a volume bound with shark teeth and a tropical fish bucket gives you this codex. Keep yours close."),
            page("Spawn Rules",
                    "Each species has a soft cap per chunk and a hard cap per area. Edit the values from the BFS config or with the in-game commands. Use /bfs cap get and /bfs cap set to inspect and change them."),
            page("Final Notes",
                    "Treat the ocean as a place to learn from. Bait carefully, log every encounter, and never assume a calm fin means a calm shark."));
}
