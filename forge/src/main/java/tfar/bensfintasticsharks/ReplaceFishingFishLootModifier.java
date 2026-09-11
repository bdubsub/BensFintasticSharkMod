package tfar.bensfintasticsharks;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import tfar.bensfintasticsharks.config.BfsConfig;
import tfar.bensfintasticsharks.fishing.FishingCatchPolicy;
import tfar.bensfintasticsharks.fishing.FishingCatchDelivery;

public final class ReplaceFishingFishLootModifier extends LootModifier {

    public static final Codec<ReplaceFishingFishLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(modifier -> modifier.conditions))
                    .apply(instance, ReplaceFishingFishLootModifier::new));

    public ReplaceFishingFishLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
                                                           LootContext context) {
        ItemStack original = FishingCatchPolicy.onlySupportedFishingFish(generatedLoot);
        boolean replace = BfsConfig.COMMON.replaceVanillaMobs.get();
        if (original != null) {
            generatedLoot.set(0, FishingCatchPolicy.selectFishingItem(
                    original, replace, context.getRandom().nextFloat(), context.getRandom().nextBoolean()));
        }
        if (context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof FishingHook hook) {
            ItemStack rod = context.getParamOrNull(LootContextParams.TOOL);
            if (rod != null) {
                FishingCatchDelivery.rememberAttempt(hook, rod, BfsConfig.COMMON.fishEntities.get(), replace,
                        original == null ? "unavailable:not_one_supported_fish"
                                : BuiltInRegistries.ITEM.getKey(original.getItem()).toString(),
                        context.getQueriedLootTableId().toString());
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
