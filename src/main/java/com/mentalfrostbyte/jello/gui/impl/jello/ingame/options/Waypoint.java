package com.mentalfrostbyte.jello.gui.impl.jello.ingame.options;

import com.mentalfrostbyte.jello.gui.base.animations.Animation;
import com.mentalfrostbyte.jello.gui.base.elements.Element;
import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mentalfrostbyte.jello.util.system.math.smoothing.QuadraticEasing;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import net.minecraft.util.math.vector.Vector3i;

public class Waypoint extends Element {
   public int field21288;
   public final Animation field21289;
   public final Animation field21290;
   public String field21291;
   public Vector3i field21292;
   public int field21293;
   public int field21294;

   public Waypoint(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, String var7, Vector3i var8, int var9) {
      super(var1, var2, var3, var4, var5, var6, true);
      this.field21288 = var4;
      this.field21289 = new Animation(114, 114);
      this.field21290 = new Animation(200, 200);
      this.field21290.changeDirection(Animation.Direction.BACKWARDS);
      this.field21291 = var7;
      this.field21292 = var8;
      this.field21293 = var9;
      this.field21294 = var6;
      this.field20883 = true;
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      super.updatePanelDimensions(newHeight, newWidth);
      this.field21289.changeDirection(!this.method13216() ? Animation.Direction.BACKWARDS : Animation.Direction.FORWARDS);
      boolean var5 = this.method13216() || newHeight > this.method13271() + this.getWidthA() - 62;
      this.method13215(var5);
      if (this.field21290.getDirection() == Animation.Direction.FORWARDS) {
         this.method13215(false);
         this.setXA(Math.round((float)this.getWidthA() * QuadraticEasing.easeInQuad(this.field21290.calcPercent(), 0.0F, 1.0F, 1.0F)));
         if (this.field21290.calcPercent() == 1.0F) {
            this.callUIHandlers();
         }
      }
   }

   public void method13608() {
      this.field21290.changeDirection(Animation.Direction.FORWARDS);
   }

   @Override
   public void draw(float partialTicks) {
      RenderUtil.drawRoundedRect2(
         (float)this.xA,
         (float)this.yA,
         (float)this.widthA,
         (float)this.heightA,
              MathHelper.applyAlpha2(MathHelper.shiftTowardsBlack(ClientColors.LIGHT_GREYISH_BLUE.getColor(), 0.03F), this.field21289.calcPercent())
      );
      RenderUtil.drawString(
         ResourceRegistry.JelloLightFont20,
         (float)(this.xA + 68),
         (float)(this.yA + 14),
         this.field21291,
              MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), 0.8F)
      );
      RenderUtil.drawString(
         ResourceRegistry.JelloLightFont14,
         (float)(this.xA + 68),
         (float)(this.yA + 38),
         "x:" + this.field21292.getX() + " z:" + this.field21292.getZ(),
              MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), 0.5F)
      );
      int var5 = this.widthA - 43;
      float var6 = !this.method13216() ? 0.2F : 0.4F;
      RenderUtil.drawRoundedRect2(
         (float)(this.xA + var5), (float)(this.yA + 27), 20.0F, 2.0F, MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), var6)
      );
      RenderUtil.drawRoundedRect2(
         (float)(this.xA + var5), (float)(this.yA + 27 + 5), 20.0F, 2.0F, MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), var6)
      );
      RenderUtil.drawRoundedRect2(
         (float)(this.xA + var5), (float)(this.yA + 27 + 10), 20.0F, 2.0F, MathHelper.applyAlpha2(ClientColors.DEEP_TEAL.getColor(), var6)
      );
      RenderUtil.drawCircle(
         (float)(this.xA + 35),
         (float)(this.yA + this.heightA / 2),
         20.0F,
              MathHelper.shiftTowardsOther(this.field21293, ClientColors.DEEP_TEAL.getColor(), 0.9F)
      );
      RenderUtil.drawCircle((float)(this.xA + 35), (float)(this.yA + this.heightA / 2), 17.0F, this.field21293);
      RenderUtil.drawRoundedRect(
         (float)this.xA, (float)this.yA, (float)this.widthA, (float)this.heightA, 14.0F, partialTicks * 0.2F * this.field21289.calcPercent()
      );
      super.draw(partialTicks);
   }
}
