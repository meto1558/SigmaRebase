package com.mentalfrostbyte.jello.gui.base.elements.impl;

import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.gui.base.elements.impl.button.types.BezierButton;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mentalfrostbyte.jello.util.system.math.SmoothInterpolator;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.system.math.vector.Vector2d;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Bezier extends Element {
   private final BezierButton field20608;
   private final BezierButton field20609;
   public int field20610 = 30;
   private Animation field20611 = new Animation(300, 300);

   public Bezier(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10, float var11) {
      super(var1, var2, var3, var4, var5, var6, false);
      this.field20610 = var7;
      this.addToList(this.field20608 = new BezierButton(this, 10, "pos1"));
      this.addToList(this.field20609 = new BezierButton(this, 10, "pos2"));
      this.method13249((var1x, var2x) -> this.callUIHandlers());
      this.method13041(var8, var9, var10, var11);
   }

   public float[] method13040() {
      int var3 = this.field20610;
      float var4 = (float)(this.widthA - var3 * 2);
      float var5 = (float)(this.field20608.getXA() - var3) / var4;
      float var6 = 1.0F - (float)(this.field20608.getYA() - var3) / var4;
      float var7 = (float)(this.field20609.getXA() - var3) / var4;
      float var8 = 1.0F - (float)(this.field20609.getYA() - var3) / var4;
      return new float[]{var5, var6, var7, var8};
   }

   public void method13041(float var1, float var2, float var3, float var4) {
      float var7 = (float)(this.widthA - this.field20610 * 2);
      this.field20608.method13144((float)this.field20610 + var7 * var1, (float)this.field20610 + var7 * (1.0F - var2));
      this.field20609.method13144((float)this.field20610 + var7 * var3, (float)this.field20610 + var7 * (1.0F - var4));
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      super.updatePanelDimensions(newHeight, newWidth);
   }

   @Override
   public void draw(float partialTicks) {
      this.field20611.changeDirection(Animation.Direction.FORWARDS);
      if (this.field20611.calcPercent() == 1.0F) {
         this.field20611 = new Animation(1500, 0);
      }

      float[] var4 = this.method13040();
      int var5 = this.field20610;
      float var6 = (float)(this.widthA - var5 * 2);
      float var7 = var4[0];
      float var8 = var4[1];
      float var9 = var4[2];
      float var10 = var4[3];
      RenderUtil.drawRoundedRect(
         (float)(this.xA + var5),
         (float)(this.yA + var5),
         (float)(this.widthA - var5 * 2),
         (float)(this.heightA - var5 * 2),
         3.0F,
         MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), 0.05F * partialTicks)
      );
      ArrayList var11 = new ArrayList();
      var11.add(new Vector2d(0.0, 0.0));
      var11.add(new Vector2d(var7, var8));
      var11.add(new Vector2d(var9, var10));
      var11.add(new Vector2d(1.0, 1.0));
      SmoothInterpolator var12 = new SmoothInterpolator(1.0F / var6 * 2.0F);
      double var13 = var12.calculateInterpolatedValue(var11, Math.min(0.8F, this.field20611.calcPercent()) * 1.25F);
      RenderUtil.drawCircle(
         (float)((double)this.xA + (double)var6 * var13 + (double)var5),
         (float)(this.yA - var5 / 2 + this.heightA),
         14.0F,
              MathHelper.applyAlpha2(ClientColors.DARK_BLUE_GREY.getColor(), partialTicks)
      );
      List<Vector2d> var15 = var12.generateInterpolatedPoints(var11);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)(this.xA + var5), (float)(this.yA + var5), 0.0F);
      GL11.glLineWidth(1.0F);
      GL11.glColor4d(0.0, 0.0, 0.0, 0.6F * partialTicks);
      GL11.glAlphaFunc(519, 0.0F);
      RenderSystem.enableBlend();
      RenderSystem.disableTexture();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GL11.glEnable(2848);
      GL11.glBegin(3);
      GL11.glVertex2f(0.0F, var6);

      for (Vector2d var17 : var15) {
         GL11.glVertex2d(var17.x() * (double)var6, (1.0 - var17.y()) * (double)var6);
      }

      GL11.glVertex2f(var6, 0.0F);
      GL11.glEnd();
      GL11.glLineWidth(3.0F);
      GL11.glColor4d(0.0, 0.2F, 0.4F, 0.2F);
      GL11.glBegin(3);
      GL11.glVertex2f(0.0F, var6);
      GL11.glVertex2f((float)(this.field20608.getXA() - var5 + 5), (float)(this.field20608.getYA() - var5 + 5));
      GL11.glEnd();
      GL11.glBegin(3);
      GL11.glVertex2f(var6, 0.0F);
      GL11.glVertex2f((float)(this.field20609.getXA() - var5 + 5), (float)(this.field20609.getYA() - var5 + 5));
      GL11.glEnd();
      RenderSystem.disableBlend();
      RenderSystem.enableTexture();
      GL11.glPopMatrix();
      super.draw(partialTicks);
   }
}
