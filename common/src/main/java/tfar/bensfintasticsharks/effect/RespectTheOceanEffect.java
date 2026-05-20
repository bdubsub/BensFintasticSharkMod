package tfar.bensfintasticsharks.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Cosmetic effect granted to players who kill a conservation-protected sea creature.
 *
 * Why: design philosophy of the mod is to gently discourage shark/marine-mammal
 * killing without being preachy. This effect has no gameplay impact — purely a
 * silent reminder visible in the player's effect list.
 */
public class RespectTheOceanEffect extends MobEffect {
    public RespectTheOceanEffect() {
        super(MobEffectCategory.NEUTRAL, 0x4A6E89);
    }
}
