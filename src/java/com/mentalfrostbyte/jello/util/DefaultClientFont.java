package com.mentalfrostbyte.jello.util;

import java.awt.Font;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public class DefaultClientFont extends TrueTypeFont {
   public final int fontSize;
   public Minecraft mc = Minecraft.getInstance();

   public DefaultClientFont(int fontSize) {
      super(new Font("Arial", Font.PLAIN, fontSize), false);
      this.fontSize = fontSize;
   }

   public int getWidth(String var1) {
      return this.mc.fontRenderer.getStringWidth(var1) * this.fontSize;
   }

   public int getHeight() {
      return 9 * this.fontSize;
   }

   public int getHeight(String var1) {
      return 9 * this.fontSize;
   }

   public int getLineHeight() {
      return 9 * this.fontSize;
   }

   public void drawString(float var1, float var2, String var3, Color var4) {
      this.drawString(var1, var2, var3, var4, 0, var3.length() - 1);
   }

   public void drawString(float var1, float var2, String var3, Color var4, int var5, int var6) {
      GL11.glPushMatrix();
      GL11.glScalef((float)this.fontSize, (float)this.fontSize, 0.0F);
      GL11.glTranslatef(-var1 / (float)this.fontSize, -var2 / (float)this.fontSize + 1.0F, 0.0F);
      this.mc
         .fontRenderer
         .renderString(
            var3,
            var1,
            var2,
            new java.awt.Color(var4.r, var4.g, var4.b, var4.a).getRGB(),
            new MatrixStack().getLast().getMatrix(),
            false,
            false
         );
      GL11.glPopMatrix();
   }

   public void drawString(float var1, float var2, String var3) {
      this.drawString(var1, var2, var3, Color.white);
   }
}
