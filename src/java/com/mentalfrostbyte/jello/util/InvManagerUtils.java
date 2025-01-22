package com.mentalfrostbyte.jello.util;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import com.mentalfrostbyte.jello.misc.Class3280;


import static com.mentalfrostbyte.jello.module.Module.mc;

public class InvManagerUtils {

    public static boolean method25819(int var0) {
        ItemStack var3 = mc.player.container.getSlot(var0).getStack();
        return var3 == null || var3.getItem() instanceof Class3280;
    }

    public static int findBestToolFromHotbarSlotForBlock(BlockState state) {
        int slot = -1;
        float dmg = 1.0F;

        for (int hotbarSlot = 44; hotbarSlot >= 9; hotbarSlot--) {
            ItemStack item = mc.player.container.getSlot(hotbarSlot).getStack();
            if (item != null) {
                float damage;
                if (state == null) {
                    if (!(item.getItem() instanceof SwordItem)) {
                        continue;
                    }

                    damage = ((SwordItem)item.getItem()).getAttackDamage();
                } else {
                    damage = item.getDestroySpeed(state);
                }

                if (damage > dmg) {
                    slot = hotbarSlot;
                    dmg = damage;
                }
            }
        }

        return slot;
    }
    public static int findItemSlot(Item var0) {
        int var3 = 0;
        int var4 = -1;

        for (int var5 = 44; var5 >= 9; var5--) {
            ItemStack var6 = mc.player.container.getSlot(var5).getStack();
            if (!method25819(var5) && var6.getItem() == var0) {
                int var7 = var6.getCount();
                if (var7 > var3) {
                    var4 = var5;
                    var3 = var7;
                }
            }
        }

        return var4;
    }
}
