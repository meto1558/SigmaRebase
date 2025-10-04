package com.mentalfrostbyte.jello.gui.impl.jello.ingame.buttons.keybind;

import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;

public class Class4263 extends Element {
   private static String[] field20677;
   public float field20678;

   public Class4263(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6, false);
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      super.updatePanelDimensions(newHeight, newWidth);
      this.field20678 = this.field20678 + (!this.method13298() ? -0.14F : 0.14F);
      this.field20678 = Math.min(Math.max(0.0F, this.field20678), 1.0F);
   }

   @Override
   public void draw(float partialTicks) {
      RenderUtil.drawCircle(
         (float)(this.xA + this.widthA / 2),
         (float)(this.yA + this.heightA / 2),
         (float)this.widthA,
         MathHelper.applyAlpha2(ClientColors.PALE_YELLOW.getColor(), (0.5F + this.field20678 * 0.3F + (!this.field20909 ? 0.0F : 0.2F)) * partialTicks)
      );
      RenderUtil.drawRoundedRect2(
         (float)(this.xA + (this.widthA - 10) / 2),
         (float)(this.yA + this.heightA / 2 - 1),
         10.0F,
         2.0F,
              MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.75F * partialTicks)
      );
      super.draw(partialTicks);
   }
}
