package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.render.RenderUtil;

public class Class4361 extends PanelBase {
   public Class4361(CustomGuiScreen var1, String var2, int var3, int var4) {
      super(var1, var2, var3, var4, 30, 30, false);
   }

   @Override
   public void draw(float var1) {
      RenderUtil.method11455((float)this.xA, (float)this.yA, 30.0F, 30.0F, !this.method13298() ? Class4334.field21177 : Class4334.field21178);
      super.draw(var1);
   }
}
