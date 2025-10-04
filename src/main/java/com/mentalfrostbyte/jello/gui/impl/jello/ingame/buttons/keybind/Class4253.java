package com.mentalfrostbyte.jello.gui.impl.jello.ingame.buttons.keybind;


import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.system.math.smoothing.QuadraticEasing;
import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;

import java.util.Date;

public class Class4253 extends Element {
   public float field20623;
   public Class6984 field20624;
   public Date field20625;
   public int field20626;
   public Date field20627;
   public Class4263 field20628;

   public Class4253(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Class6984 var7, int var8) {
      super(var1, var2, var3, var4, var5, var6, false);
      this.addToList(this.field20628 = new Class4263(this, "delete", 200, 20, 20, 20));
      this.field20628.onClick((var1x, var2x) -> {
         this.field20625 = new Date();
         this.callUIHandlers();
      });
      this.field20624 = var7;
      this.field20626 = var8;
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      super.updatePanelDimensions(newHeight, newWidth);
   }

   public void method13056() {
      this.setHeightA(0);
      this.field20627 = new Date();
   }

   @Override
   public void draw(float partialTicks) {
      if (this.field20627 != null) {
         float var4 = Animation.calculateProgress(this.field20627, 150.0F);
         var4 = QuadraticEasing.easeOutQuad(var4, 0.0F, 1.0F, 1.0F);
         this.setHeightA((int)(55.0F * var4));
         if (var4 == 1.0F) {
            this.field20627 = null;
         }
      }

      if (this.field20625 != null) {
         float var6 = Animation.calculateProgress(this.field20625, 180.0F);
         var6 = QuadraticEasing.easeOutQuad(var6, 0.0F, 1.0F, 1.0F);
         this.setHeightA((int)(55.0F * (1.0F - var6)));
         if (var6 == 1.0F) {
            this.field20625 = null;
         }
      }

      RenderUtil.startScissor(this.xA, this.yA, this.xA + this.widthA, this.yA + this.heightA, true);
      RenderUtil.drawString(
         ResourceRegistry.RegularFont20,
         (float)(this.xA + 25),
         (float)this.yA + (float)this.heightA / 2.0F - 17.5F,
         this.field20624.method21596(),
         RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.6F * partialTicks)
      );
      RenderUtil.drawString(
         ResourceRegistry.JelloLightFont12,
         (float)(this.xA + 25),
         (float)this.yA + (float)this.heightA / 2.0F + 7.5F,
         this.field20624.method21597(),
              RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), 0.6F * partialTicks)
      );
      this.field20628.setYA((int)((float)this.heightA / 2.0F - 7.5F));
      super.draw(partialTicks);
      RenderUtil.endScissor();
   }
}
