package tfar.bensfintasticsharks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

/**
 * Like {@link AddItemChanceLootModifier} but consults the world-scoped
 * {@link BfsWorldData} flag for Captain Ben's Hat. Once the hat has been
 * generated naturally in this world, future rolls are suppressed so the hat
 * stays a once-per-world collector's piece.
 */
public class AddOncePerWorldLootModifier extends LootModifier {
    public static final Codec<AddOncePerWorldLootModifier> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions))
                    .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
                    .and(ExtraCodecs.floatRangeMinExclusiveWithMessage(0, 1, e -> "Value must be within (0,1] :" + e)
                            .fieldOf("chance").forGetter(m -> m.chance))
                    .apply(inst, AddOncePerWorldLootModifier::new));

    private final Item item;
    private final float chance;

    public AddOncePerWorldLootModifier(LootItemCondition[] conditionsIn, Item item, float chance) {
        super(conditionsIn);
        this.item = item;
        this.chance = chance;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!(context.getLevel() instanceof ServerLevel sl)) return generatedLoot;
        BfsWorldData data = BfsWorldData.get(sl);
        if (data.isCaptainBenHatClaimed()) return generatedLoot;
        if (context.getRandom().nextFloat() <= chance) {
            generatedLoot.add(new ItemStack(item, 1));
            data.setCaptainBenHatClaimed(true);
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
