package com.titanmodpack.titanores.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

// Armor of the mod. Each piece has a passive ability (see event/ArmorEvents):
// helmet = aqua affinity, chestplate = wither immunity, leggings = fire immunity, boots = no fall damage.
public class ModArmorItem extends ArmorItem {
    public ModArmorItem(ModArmorMaterial material, EquipmentSlotType slot, Properties properties) {
        super(material, slot, properties);
    }

    // True if the entity wears a piece of this mod's armor in the given slot.
    public static boolean isWorn(LivingEntity entity, EquipmentSlotType slot) {
        return entity.getItemBySlot(slot).getItem() instanceof ModArmorItem;
    }

    // The material of the full set, or null if the four pieces are not all the same mod material.
    @Nullable
    public static ModArmorMaterial fullSet(LivingEntity entity) {
        ModArmorMaterial material = null;
        for (EquipmentSlotType slot : new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET}) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!(stack.getItem() instanceof ModArmorItem)) {
                return null;
            }
            ModArmorMaterial pieceMaterial = (ModArmorMaterial) ((ModArmorItem) stack.getItem()).getMaterial();
            if (material != null && material != pieceMaterial) {
                return null;
            }
            material = pieceMaterial;
        }
        return material;
    }

    // Unbreakable armor (titanium) is not enchantable by default; all of the mod's armor should be.
    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("tooltip.titanores.armor." + getSlot().getName()).withStyle(TextFormatting.GOLD));
    }
}
