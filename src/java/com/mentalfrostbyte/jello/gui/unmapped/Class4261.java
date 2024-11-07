package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.ColorHelper;
import org.newdawn.slick.TrueTypeFont;

public class Class4261 extends Class4247 {
   public Class7312 field20673;

   public Class4261(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, boolean var7, Class7312 var8) {
      super(var1, var2, var3, var4, var5, var6, var7);
      this.field20673 = var8;
   }

   @Override
   public void method13028(int var1, int var2) {
      this.field20673.method23108();
   }

   @Override
   public void draw(float var1) {
      this.method13086().method23109(this.getXA(), this.getYA(), this.getWidthA(), this.getHeightA(), var1);
      super.draw(var1);
   }

   public Class7312 method13086() {
      return this.field20673;
   }

   public void method13087(Class7312 var1) {
      this.field20673 = var1;
   }
}
