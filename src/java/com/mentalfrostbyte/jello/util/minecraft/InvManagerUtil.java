package com.mentalfrostbyte.jello.util.minecraft;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

import static com.mentalfrostbyte.jello.util.minecraft.MinecraftUtil.mc;

public class InvManagerUtil {

    public static int getTotalArmorProtection(PlayerEntity player) {
        int totalProtection = 0;

        for (int slot = 5; slot <= 8; slot++) {
            totalProtection += getArmorProtectionValue(player.inventory.getStackInSlot(slot));
        }

        return totalProtection;
    }

    public static int getArmorProtectionValue(ItemStack itemStack) {
        if (itemStack != null) {
            return itemStack.getItem() instanceof ArmorItem ? ((ArmorItem)itemStack.getItem()).getDamageReduceAmount() + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, itemStack) : 0;
        } else {
            return 0;
        }
    }

    public static int isHotbarEmpty() {
        for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(hotbarSlot);
            if (itemStack == null) {
                return hotbarSlot;
            }
        }

        return 0;
    }

    public static void moveItemToHotbar(int var0, int var1) {
        mc.playerController.windowClick(mc.player.container.windowId, var0, var1, ClickType.SWAP, mc.player);
    }
}
