package tfar.bensfintasticsharks.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

/** Leather tier material for the non recipe dive suit. */
public final class DiveArmorMaterial implements ArmorMaterial {
    public static final ArmorMaterial DIVE = new DiveArmorMaterial();

    private DiveArmorMaterial() {
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return ArmorMaterials.LEATHER.getDurabilityForType(type);
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return ArmorMaterials.LEATHER.getDefenseForType(type);
    }

    @Override
    public int getEnchantmentValue() {
        return ArmorMaterials.LEATHER.getEnchantmentValue();
    }

    @Override
    public SoundEvent getEquipSound() {
        return ArmorMaterials.LEATHER.getEquipSound();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return ArmorMaterials.LEATHER.getRepairIngredient();
    }

    @Override
    public String getName() {
        return "bensfintasticsharks:dive";
    }

    @Override
    public float getToughness() {
        return 0.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
