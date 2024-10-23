package com.mentalfrostbyte.jello.utils.render;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageUtil {

    public static BufferedImage method35032(BufferedImage var0, int var1) {
        if (var0 != null) {
            if (var0.getWidth() > var1 + var1) {
                if (var0.getHeight() > var1 + var1) {
                    ConvolveOp var4 = new ConvolveOp(method35035((float) var1));
                    BufferedImage var5 = var4.filter(var0, null);
                    var5 = var4.filter(method35034(var5), null);
                    var5 = method35034(var5);
                    return var5.getSubimage(var1, var1, var0.getWidth() - var1 - var1, var0.getHeight() - var1 - var1);
                } else {
                    return var0;
                }
            } else {
                return var0;
            }
        } else {
            return var0;
        }
    }

    public static BufferedImage method35041(BufferedImage var0, int var1) {
        int var4 = var0.getWidth() + var1 * 2;
        int var5 = var0.getHeight() + var1 * 2;
        BufferedImage var6 = method35043(var0, (float) var4 / (float) var0.getWidth(), (float) var5 / (float) var0.getHeight());

        for (int var7 = 0; var7 < var0.getWidth(); var7++) {
            for (int var8 = 0; var8 < var0.getHeight(); var8++) {
                var6.setRGB(var1 + var7, var1 + var8, var0.getRGB(var7, var8));
            }
        }

        return var6;
    }

    public static BufferedImage method35043(BufferedImage var0, double var1, double var3) {
        BufferedImage var7 = null;
        if (var0 != null) {
            int var8 = (int) ((double) var0.getHeight() * var3);
            int var9 = (int) ((double) var0.getWidth() * var1);
            var7 = new BufferedImage(var9, var8, var0.getType());
            Graphics2D var10 = var7.createGraphics();
            AffineTransform var11 = AffineTransform.getScaleInstance(var1, var3);
            var10.drawRenderedImage(var0, var11);
        }

        return var7;
    }

    public static BufferedImage method35042(BufferedImage var0, float var1, float var2, float var3) {
        int var6 = var0.getWidth();
        int var7 = var0.getHeight();

        for (int var8 = 0; var8 < var7; var8++) {
            for (int var9 = 0; var9 < var6; var9++) {
                int var10 = var0.getRGB(var9, var8);
                int var11 = var10 >> 16 & 0xFF;
                int var12 = var10 >> 8 & 0xFF;
                int var13 = var10 & 0xFF;
                float[] var14 = Color.RGBtoHSB(var11, var12, var13, null);
                float var15 = Math.max(0.0F, Math.min(1.0F, var14[0] + var1));
                float var16 = Math.max(0.0F, Math.min(1.0F, var14[1] * var2));
                float var17 = Math.max(0.0F, Math.min(1.0F, var14[2] + var3));
                int var18 = Color.HSBtoRGB(var15, var16, var17);
                var0.setRGB(var9, var8, var18);
            }
        }

        return var0;
    }

    public static BufferedImage method35034(BufferedImage var0) {
        int var3 = var0.getWidth();
        int var4 = var0.getHeight();
        BufferedImage var5 = new BufferedImage(var4, var3, var0.getType());

        for (int var6 = 0; var6 < var3; var6++) {
            for (int var7 = 0; var7 < var4; var7++) {
                var5.setRGB(var4 - 1 - var7, var3 - 1 - var6, var0.getRGB(var6, var7));
            }
        }

        return var5;
    }

    public static Kernel method35035(float var0) {
        int var3 = (int) Math.ceil(var0);
        int var4 = var3 * 2 + 1;
        float[] var5 = new float[var4];
        float var6 = var0 / 3.0F;
        float var7 = 2.0F * var6 * var6;
        float var8 = (float) ((Math.PI * 2) * (double) var6);
        float var9 = (float) Math.sqrt(var8);
        float var10 = var0 * var0;
        float var11 = 0.0F;
        int var12 = 0;

        for (int var13 = -var3; var13 <= var3; var13++) {
            float var14 = (float) (var13 * var13);
            if (!(var14 > var10)) {
                var5[var12] = (float) Math.exp(-var14 / var7) / var9;
            } else {
                var5[var12] = 0.0F;
            }

            var11 += var5[var12];
            var12++;
        }

        for (int var15 = 0; var15 < var4; var15++) {
            var5[var15] /= var11;
        }

        return new Kernel(var4, 1, var5);
    }

}
