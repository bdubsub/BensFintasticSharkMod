package tfar.bensfintasticsharks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class AddItemChanceLootModifier extends LootModifier {
    public static final Codec<AddItemChanceLootModifier> CODEC =
            RecordCodecBuilder.create(inst -> inst.group(LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions))
                    .and(BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(m -> m.item))
                    .and(ExtraCodecs.POSITIVE_INT.fieldOf("min").forGetter(m -> m.min))
                    .and(ExtraCodecs.POSITIVE_INT.fieldOf("max").forGetter(m -> m.max))
                    .and(ExtraCodecs.floatRangeMinExclusiveWithMessage(0,1,e -> "Value must be within (0,1] :" + e).fieldOf("chance").forGetter(m -> m.chance))

                    .apply(inst, AddItemChanceLootModifier::new));
    private final Item item;
    private final int min;
    private final int max;
    private final float chance;

    public AddItemChanceLootModifier(LootItemCondition[] conditionsIn, Item item, int min, int max, float chance) {
        super(conditionsIn);
        if (max < min) {
            throw new IllegalArgumentException("AddItemChanceLootModifier: max (" + max + ") must be >= min (" + min + ")");
        }
        this.item = item;
        this.min = min;
        this.max = max;
        this.chance = chance;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(context.getRandom().nextFloat() <= chance) {
            int amount = min + context.getRandom().nextInt(max - min + 1);
            generatedLoot.add(new ItemStack(item,amount));
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
