package com.mentalfrostbyte.jello.util.player;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

public class InvManagerUtil {

    public static int getArmorProtectionValue(ItemStack itemStack) {
        if (itemStack != null) {
            return itemStack.getItem() instanceof ArmorItem ? ((ArmorItem)itemStack.getItem()).getDamageReduceAmount() + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, itemStack) : 0;
        } else {
            return 0;
        }
    }

    public static int getTotalArmorProtection(PlayerEntity player) {
        int totalProtection = 0;

        for (int slot = 5; slot <= 8; slot++) {
            totalProtection += getArmorProtectionValue(player.inventory.getStackInSlot(slot));
        }

        return totalProtection;
    }

}
