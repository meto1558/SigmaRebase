package com.mentalfrostbyte.jello.gui.unmapped;

import com.mentalfrostbyte.jello.gui.impl.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.theme.ColorHelper;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.FontSizeAdjust;
import org.newdawn.slick.TrueTypeFont;
import org.lwjgl.opengl.GL11;

public class Text extends AnimatedIconPanel {
   public static ColorHelper defaultColorHelper = new ColorHelper(
      ClientColors.DEEP_TEAL.getColor(),
      ClientColors.DEEP_TEAL.getColor(),
      ClientColors.DEEP_TEAL.getColor(),
      ClientColors.DEEP_TEAL.getColor(),
      FontSizeAdjust.field14488,
      FontSizeAdjust.NEGATE_AND_DIVIDE_BY_2
   );
   public boolean field20779 = false;

   public Text(CustomGuiScreen screen, String id, int var3, int var4, int var5, int var6, ColorHelper colorHelper, String var8) {
      super(screen, id, var3, var4, var5, var6, colorHelper, var8, false);
   }

   public Text(CustomGuiScreen screen, String id, int var3, int var4, int var5, int var6, ColorHelper colorHelper, String var8, TrueTypeFont font) {
      super(screen, id, var3, var4, var5, var6, colorHelper, var8, font, false);
   }

   @Override
   public void draw(float partialTicks) {
      if (this.field20779) {
         GL11.glAlphaFunc(518, 0.01F);
         RenderUtil.drawString(
            ResourceRegistry.JelloLightFont18_1,
            (float)this.getXA(),
            (float)this.getYA(),
            this.getTypedText(),
            RenderUtil2.applyAlpha(ClientColors.DEEP_TEAL.getColor(), partialTicks)
         );
         GL11.glAlphaFunc(519, 0.0F);
      }

      if (this.typedText != null) {
         RenderUtil.drawString(
            this.getFont(),
            (float)this.getXA(),
            (float)this.getYA(),
            this.getTypedText(),
            RenderUtil2.applyAlpha(this.textColor.getTextColor(), partialTicks * RenderUtil2.getAlpha(this.textColor.getTextColor()))
         );
      }
   }
}
