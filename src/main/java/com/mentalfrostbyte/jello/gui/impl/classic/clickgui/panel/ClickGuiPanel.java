package com.mentalfrostbyte.jello.gui.impl.classic.clickgui.panel;

import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.system.math.SmoothInterpolator;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import org.newdawn.slick.TrueTypeFont;
import org.lwjgl.opengl.GL11;

public class ClickGuiPanel extends CustomGuiScreen {
   public Animation field21149 = new Animation(500, 200, Animation.Direction.FORWARDS);

   public ClickGuiPanel(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public void draw(float partialTicks) {
      GL11.glAlphaFunc(518, 0.1F);
      float var4 = SmoothInterpolator.interpolate(1.0F - this.field21149.calcPercent(), 0.9, 0.0, 0.9, 0.0);
      float var5 = (float)this.getWidthA() * var4 / 2.0F;
      float var6 = (float)(this.getHeightA() + 10) * var4 / 2.0F;
      RenderUtil.startScissorScaled(
         (float)this.method13271() + var5,
         (float)this.method13272() + var6,
         (float)(this.method13271() + this.getWidthA()) - var5,
         (float)(this.method13272() + this.getHeightA()) - var6
      );
      if (var4 != 0.0F) {
         RenderUtil.drawRoundedRect2(
            (float)this.xA, (float)this.yA, (float)this.widthA, (float)this.heightA, RenderUtil2.applyAlpha(-2500135, 0.9F)
         );
      } else {
         RenderUtil.drawRoundedRect(
            (float)this.xA,
            (float)this.yA,
            (float)(this.widthA - 1),
            (float)(this.heightA - 1),
            3.0F,
            RenderUtil2.applyAlpha(-2500135, 0.9F)
         );
      }

      TrueTypeFont var7 = !this.name.equals("Sigma") ? Resources.regular25 : Resources.regular28;
      if (!this.name.equals("Sigma")) {
         RenderUtil.drawString(
            var7,
            (float)this.xA + (float)(this.getWidthA() - var7.getWidth(this.name)) / 2.0F,
            (float)(this.yA + 18),
            this.name,
            -16777216
         );
      } else {
         RenderUtil.drawString(
            var7,
            (float)this.xA + (float)(this.getWidthA() - var7.getWidth(this.name)) / 2.0F,
            (float)(this.yA + 10),
            this.name,
            -13619152
         );
      }

      super.draw(partialTicks);
      RenderUtil.endScissor();
   }
}
