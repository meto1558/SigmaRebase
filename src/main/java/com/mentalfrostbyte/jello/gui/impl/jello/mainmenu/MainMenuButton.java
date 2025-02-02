package com.mentalfrostbyte.jello.gui.impl.jello.mainmenu;

import com.mentalfrostbyte.jello.gui.base.Animation;
import com.mentalfrostbyte.jello.gui.base.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.unmapped.Class4238;
import com.mentalfrostbyte.jello.gui.unmapped.PNGIconButton;
import com.mentalfrostbyte.jello.util.client.ClientColors;
import com.mentalfrostbyte.jello.util.client.ColorHelper;
import com.mentalfrostbyte.jello.util.system.math.MathUtils;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil2;
import com.mentalfrostbyte.jello.util.game.render.RenderUtil;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.TrueTypeFont;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class MainMenuButton extends PNGIconButton implements Class4238 {
   public boolean field20577 = false;
   public Animation field20578 = new Animation(160, 140, Animation.Direction.BACKWARDS);

   public MainMenuButton(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture var7, ColorHelper var8, String var9, TrueTypeFont var10) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public MainMenuButton(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture var7, ColorHelper var8, String var9) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public MainMenuButton(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture var7, ColorHelper var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public MainMenuButton(CustomGuiScreen var1, String var2, int var3, int var4, int var5, int var6, Texture var7) {
      super(var1, var2, var3, var4, var5, var6, var7, new ColorHelper(ClientColors.LIGHT_GREYISH_BLUE.getColor(), ClientColors.LIGHT_GREYISH_BLUE.getColor()));
   }

   @Override
   public void updatePanelDimensions(int newHeight, int newWidth) {
      super.updatePanelDimensions(newHeight, newWidth);
      this.field20577 = this.method13298();
      if (!this.field20577) {
         if (this.method13029()) {
            this.field20578.changeDirection(Animation.Direction.BACKWARDS);
            this.method13292(false);
         }
      } else {
         this.field20578.changeDirection(Animation.Direction.FORWARDS);
         this.method13292(true);
      }
   }

   public boolean method13029() {
      return Math.abs(this.method13030() - this.method13031()) < 0.6F;
   }

   public float method13030() {
      return MathUtils.lerp(this.field20578.calcPercent(), 0.24, 0.88, 0.3, 1.0);
   }

   public float method13031() {
      return MathUtils.lerp(this.field20578.calcPercent(), 0.45, 0.02, 0.59, 0.28);
   }

   @Override
   public void draw(float partialTicks) {
      float var4 = !this.method13212() ? 0.0F : 0.1F;
      float var5 = this.method13030();
      if (this.field20578.getDirection() == Animation.Direction.BACKWARDS) {
         var5 = this.method13031();
      }

      int var6 = (int)((double)this.getWidthA() * (1.0 + (double)var5 * 0.2));
      int var7 = (int)((double)this.getHeightA() * (1.0 + (double)var5 * 0.2));
      int var8 = this.getXA() - (var6 - this.getWidthA()) / 2;
      int var9 = (int)((double)(this.getYA() - (var7 - this.getHeightA()) / 2) - (double)((float)(this.getHeightA() / 2) * var5) * 0.2);
      float[] var10 = RenderUtil2.method17701(this.getTexture().getWidth(), this.getTexture().getHeight(), (float)var6, (float)var7);
      float var11 = 85;
      RenderUtil.drawImage(
         (float)var8 + var10[0] - var11,
         (float)var9 + var10[1] - var11,
         var10[2] + (var11 * 2),
         var10[3] + (var11 * 2),
         Resources.shadowPNG,
              RenderUtil2.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor(), this.field20578.calcPercent() * 0.7F * partialTicks)
      );
      RenderUtil.drawImage(
         (float)var8 + var10[0],
         (float)var9 + var10[1],
         var10[2],
         var10[3],
         this.getTexture(),
         RenderUtil2.applyAlpha(RenderUtil2.shiftTowardsOther(this.textColor.getPrimaryColor(), this.textColor.getSecondaryColor(), 1.0F - var4), partialTicks)
      );
      if (this.getTypedText() != null) {
         RenderUtil.drawString(
            this.getFont(),
            (float)(var8 + var6 / 2),
            (float)(var9 + var7 / 2),
            this.getTypedText(),
                 RenderUtil2.applyAlpha(this.textColor.getTextColor(), partialTicks),
            this.textColor.method19411(),
            this.textColor.method19413()
         );
      }

      TrueTypeFont font = this.getFont();
      float var13 = 0.8F + var5 * 0.2F;
      if (var5 > 0.0F) {
         GL11.glPushMatrix();
         String var14 = this.getTypedText() != null ? this.getTypedText() : this.name;
         GL11.glTranslatef(
            (float)(this.getXA() + this.getWidthA() / 2 - font.getWidth(var14) / 2), (float)(this.getYA() + this.getHeightA() - 40), 0.0F
         );
         GL11.glScalef(var13, var13, var13);
         GL11.glAlphaFunc(519, 0.0F);
         RenderUtil.drawImage(
            (1.0F - var13) * (float)font.getWidth(var14) / 2.0F + 1.0F - (float)font.getWidth(var14) / 2.0F,
            (float)font.getHeight(var14) / 3.0F,
            (float)(font.getWidth(var14) * 2),
            (float)font.getHeight(var14) * 3.0F,
            Resources.shadowPNG,
            var5 * 0.6F * partialTicks
         );
         RenderUtil.drawString(
            font,
            (1.0F - var13) * (float)font.getWidth(var14) / 2.0F + 1.0F,
            40.0F,
            var14,
                 RenderUtil2.applyAlpha(this.getTextColor().getPrimaryColor(), var5 * 0.6F * partialTicks)
         );
         GL11.glPopMatrix();
      }

      super.method13226(partialTicks);
   }

   @Override
   public float method13032() {
      return 1.2F;
   }

   @Override
   public float method13033() {
      return 0.07F * (30.0F / (float) Minecraft.getFps());
   }
}
