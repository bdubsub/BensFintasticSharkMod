package tfar.bensfintasticsharks.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tfar.bensfintasticsharks.entity.ThrownSharkTridentEntity;

import java.util.List;

public class SharkTridentItem extends TridentItem {

    /**
     * Vanilla TridentItem hardcodes a +8 attack damage modifier (9 on the tooltip with the
     * player's base 1). Ben wants the Shark Trident at 11, so we rebuild the modifier map
     * with +10 and keep vanilla's -2.9 attack speed.
     */
    private final com.google.common.collect.Multimap<net.minecraft.world.entity.ai.attributes.Attribute,
            net.minecraft.world.entity.ai.attributes.AttributeModifier> sharkTridentModifiers;

    public SharkTridentItem(Properties $$0) {
        super($$0);
        com.google.common.collect.ImmutableMultimap.Builder<net.minecraft.world.entity.ai.attributes.Attribute,
                net.minecraft.world.entity.ai.attributes.AttributeModifier> builder = com.google.common.collect.ImmutableMultimap.builder();
        builder.put(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                new net.minecraft.world.entity.ai.attributes.AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 10.0,
                        net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION));
        builder.put(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,
                new net.minecraft.world.entity.ai.attributes.AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9,
                        net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION));
        this.sharkTridentModifiers = builder.build();
    }

    @Override
    public com.google.common.collect.Multimap<net.minecraft.world.entity.ai.attributes.Attribute,
            net.minecraft.world.entity.ai.attributes.AttributeModifier> getDefaultAttributeModifiers(net.minecraft.world.entity.EquipmentSlot slot) {
        return slot == net.minecraft.world.entity.EquipmentSlot.MAINHAND ? sharkTridentModifiers : super.getDefaultAttributeModifiers(slot);
    }

    // Aqua-blue display name everywhere (inventory, hand, anvil).
    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack)).withStyle(ChatFormatting.AQUA);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.bensfintasticsharks.shark_trident.flavor")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }

    /**
     * Called when the player stops using an Item (stops holding the right mouse button).
     */
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (pEntityLiving instanceof Player player) {
            int i = this.getUseDuration(pStack) - pTimeLeft;
            if (i >= 10) {
                int riptide = EnchantmentHelper.getRiptide(pStack);
                if (riptide <= 0 || player.isInWaterOrRain()) {
                    if (!pLevel.isClientSide) {
                        pStack.hurtAndBreak(1, player, (p_43388_) -> {
                            p_43388_.broadcastBreakEvent(pEntityLiving.getUsedItemHand());
                        });
                        if (riptide == 0) {
                            ThrownTrident throwntrident = new ThrownSharkTridentEntity(pLevel, player, pStack);
                            throwntrident.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F + (float)riptide * 0.5F, 1.0F);
                            if (player.getAbilities().instabuild) {
                                throwntrident.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                            }

                            pLevel.addFreshEntity(throwntrident);
                            pLevel.playSound(null, throwntrident, SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 1.0F, 1.0F);
                            if (!player.getAbilities().instabuild) {
                                player.getInventory().removeItem(pStack);
                            }
                        }
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    if (riptide > 0) {
                        float f7 = player.getYRot();
                        float f = player.getXRot();
                        float f1 = -Mth.sin(f7 * (float) Math.PI / 180F) * Mth.cos(f * (float) Math.PI / 180F);
                        float f2 = -Mth.sin(f * (float) Math.PI / 180F);
                        float f3 = Mth.cos(f7 * (float) Math.PI / 180F) * Mth.cos(f * (float) Math.PI / 180F);
                        float f4 = Mth.sqrt(f1 * f1 + f2 * f2 + f3 * f3);
                        float f5 = 3.0F * (1.0F + (float) riptide) / 4.0F;
                        f1 *= f5 / f4;
                        f2 *= f5 / f4;
                        f3 *= f5 / f4;
                        player.push(f1, f2, f3);
                        player.startAutoSpinAttack(20);
                        if (player.onGround()) {
                            float f6 = 1.2F;
                            player.move(MoverType.SELF, new Vec3(0, f6, 0));
                        }

                        SoundEvent soundevent;
                        if (riptide >= 3) {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_3;
                        } else if (riptide == 2) {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_2;
                        } else {
                            soundevent = SoundEvents.TRIDENT_RIPTIDE_1;
                        }

                        pLevel.playSound(null, player, soundevent, SoundSource.PLAYERS, 1, 1);
                    }

                }
            }
        }
    }


}
