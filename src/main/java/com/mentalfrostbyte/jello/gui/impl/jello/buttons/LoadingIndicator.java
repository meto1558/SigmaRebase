package com.mentalfrostbyte.jello.gui.impl.jello.buttons;

import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.combined.AnimatedIconPanel;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mentalfrostbyte.jello.util.system.math.counter.TimerUtil;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import org.lwjgl.opengl.GL11;

public class LoadingIndicator extends AnimatedIconPanel {
   private static String[] field20736;
   public TimerUtil field20769 = new TimerUtil();
   public float field20770 = 0.0F;

   public LoadingIndicator(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6) {
      super(var1, var2, var3, var4, var5, var6, false);
      this.field20769.start();
   }

   @Override
   public void draw(float partialTicks) {
      this.field20770 = this.field20770 + (this.isHovered() ? 0.2F : -0.2F);
      this.field20770 = Math.min(1.0F, Math.max(0.0F, this.field20770));
      float var4 = (float)(this.field20769.getElapsedTime() / 75L % 12L);
      if (this.field20770 != 0.0F) {
         GL11.glPushMatrix();
         GL11.glTranslatef((float)(this.xA + this.widthA / 2), (float)(this.yA + this.heightA / 2), 0.0F);
         GL11.glRotatef(var4 * 30.0F, 0.0F, 0.0F, 1.0F);
         GL11.glTranslatef((float)(-this.xA - this.widthA / 2), (float)(-this.yA - this.heightA / 2), 0.0F);
         RenderUtil.drawImage(
            (float)this.xA,
            (float)this.yA,
            (float)this.widthA,
            (float)this.heightA,
            Resources.loadingIndicatorPNG,
            MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), this.field20770 * partialTicks)
         );
         GL11.glPopMatrix();
      }
   }
}
