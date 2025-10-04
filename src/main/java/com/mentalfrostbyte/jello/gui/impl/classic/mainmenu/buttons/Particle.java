package com.mentalfrostbyte.jello.gui.impl.classic.mainmenu.buttons;

import java.awt.Color;
import java.util.Random;

import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class Particle {
   private final float field45021;
   private final float field45022;
   public float field45023;
   public float field45024;
   public float field45025;
   private float field45026;
   private float field45027;
   private float field45028;
   private boolean field45029;
   private final float field45030 = 1.0F;
   private final Random random = new Random();
   private float field45032 = 1.0F;
   public float field45033;
   public Color field45034 = new Color(1.0F, 1.0F, 1.0F, 0.5F);

   public Particle(float var1, float var2) {
      this.field45023 = this.field45021 = var1;
      this.field45024 = this.field45022 = var2;
      this.field45025 = (float)this.random.nextInt(4) + this.random.nextFloat();
      this.field45027 = 0.5F - this.random.nextFloat();
      this.field45028 = 0.5F - this.random.nextFloat();
      this.field45032 = this.random.nextFloat();
      this.field45027 *= 0.7F;
      this.field45028 *= 0.7F;
   }

   public void method37519(float var1) {
      method37520(
         this.field45023 * 2.0F, this.field45024 * 2.0F, this.field45025 * 2.0F, MathHelper.applyAlpha2(this.field45034.getRGB(), var1 * this.field45032)
      );
   }

   public static void method37520(float var0, float var1, float var2, int var3) {
      var2 *= 1.0F;
      var0 *= 2.0F;
      var1 *= 2.0F;
      float var6 = 0.19634953F;
      float var7 = (float)Math.cos(var6);
      float var8 = (float)Math.sin(var6);
      float var9 = var2;
      float var10 = 0.0F;
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      GL11.glBegin(2);
      GL11.glColor4fv(MathHelper.intColorToFloatArrayColor(var3));

      for (int var11 = 0; var11 < 32; var11++) {
         GL11.glVertex2f(var9 + var0, var10 + var1);
         GL11.glVertex2f(var0, var1);
         float var12 = var9;
         var9 = var7 * var9 - var8 * var10;
         var10 = var8 * var12 + var7 * var10;
      }

      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPopMatrix();
      GL11.glDisable(3042);
      GL11.glEnable(3553);
   }

   public void method37521() {
      this.field45023 = this.field45023 + this.field45027;
      this.field45024 = this.field45024 + this.field45028;
      this.field45032 = Math.max(0.0F, Math.min(1.0F, this.field45032 - 0.003F + 5.0E-4F * this.field45025));
   }

   // $VF: synthetic method
   public static float method37522(Particle var0) {
      return var0.field45032;
   }
}
