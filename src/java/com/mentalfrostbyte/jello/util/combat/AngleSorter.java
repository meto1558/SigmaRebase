package com.mentalfrostbyte.jello.util.combat;

import net.minecraft.entity.Entity;

import java.util.Comparator;

public class AngleSorter implements Comparator<TimedEntity> {
   public final InteractAutoBlock mc;

   public AngleSorter(InteractAutoBlock var1) {
      this.mc = var1;
   }

   public int compare(TimedEntity var1, TimedEntity var2) {
      Entity var5 = var1.getEntity();
      Entity var6 = var2.getEntity();
      float var7 = RotationUtil.angleDiff(RotationUtil.method34147(var5).yaw, this.mc.mc.player.rotationYaw);
      float var8 = RotationUtil.angleDiff(RotationUtil.method34147(var6).yaw, this.mc.mc.player.rotationYaw);
      if (!(var7 - var8 < 0.0F)) {
         if (var7 - var8 != 0.0F) {
            return 1;
         } else {
            float var9 = this.mc.mc.player.getDistance(var5);
            float var10 = this.mc.mc.player.getDistance(var6);
            if (!(var9 - var10 < 0.0F)) {
               return var9 - var10 != 0.0F ? 1 : 0;
            } else {
               return -1;
            }
         }
      } else {
         return -1;
      }
   }
}
