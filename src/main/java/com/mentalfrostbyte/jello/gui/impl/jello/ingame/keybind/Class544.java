package com.mentalfrostbyte.jello.gui.impl.jello.ingame.keybind;

import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.KeyboardScreen;
import com.mentalfrostbyte.jello.gui.unmapped.Class4375;

public class Class544 implements Runnable {
   private static String[] field2605;
   public final KeyboardScreen field2606;
   public final KeyboardScreen field2607;

   public Class544(KeyboardScreen var1, KeyboardScreen var2) {
      this.field2607 = var1;
      this.field2606 = var2;
   }

   @Override
   public void run() {
      for (CustomGuiScreen var4 : this.field2606.getChildren()) {
         if (var4 instanceof Class4375) {
            Class4375 var5 = (Class4375)var4;
            var5.method13712();
         }
      }
   }
}
