package com.mentalfrostbyte.jello.gui.base.elements.impl.button.types;

import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import net.minecraft.item.ItemStack;

public class BlockButton extends Element {
   public ItemStack field21366;
   public boolean field21367;

   public BlockButton(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, ItemStack var7) {
      super(var1, var2, var3, var4, var5, var6, false);
      this.field21366 = var7;
   }

   @Override
   public void draw(float partialTicks) {
      byte var4 = 5;
      if (this.method13700() || this.method13298()) {
         RenderUtil.drawShadowedBorder(
            (float)this.xA,
            (float)this.yA,
            (float)this.widthA,
            (float)this.heightA,
            14.0F,
            !this.method13700() ? 0.3F * partialTicks : 0.8F * partialTicks
         );
      }

      RenderUtil.renderGuiItem(this.field21366, this.xA + var4, this.yA + var4, this.widthA - var4 * 2, this.heightA - var4 * 2);
      super.draw(partialTicks);
   }

   public boolean method13700() {
      return this.field21367;
   }

   public void method13702(boolean var1, boolean var2) {
      if (var1 != this.method13700()) {
         this.field21367 = var1;
         this.callUIHandlers();
      }
   }

   @Override
   public void onClick3(int mouseX, int mouseY, int mouseButton) {
      this.method13702(!this.field21367, true);
   }
}
