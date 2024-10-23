package com.mentalfrostbyte.jello.utils.render;

import com.mentalfrostbyte.jello.managers.GuiManager;
import com.mentalfrostbyte.jello.utils.ClientColors;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

public class RenderUtil {

    private static final Minecraft mc = Minecraft.getInstance();

    private static final Stack<IntBuffer> buffer = new Stack<>();

    public static void endScissor() {
        if (buffer.isEmpty()) {
            GL11.glDisable(GL_SCISSOR_TEST);
        } else {
            IntBuffer var2 = buffer.pop();
            GL11.glScissor(var2.get(0), var2.get(1), var2.get(2), var2.get(3));
        }
    }

    public static void drawPortalBackground(int var0, int var1, int var2, int var3) {
        method11421(var0, var1, var2, var3, false);
    }

    public static float method11417() {
        return (float) mc.getMainWindow().getGuiScaleFactor();
    }

    public static float[] method11416(int var0, int var1) {
        FloatBuffer var4 = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloatv(2982, var4);
        float var5 = var4.get(0) * (float)var0 + var4.get(4) * (float)var1 + var4.get(8) * 0.0F + var4.get(12);
        float var6 = var4.get(1) * (float)var0 + var4.get(5) * (float)var1 + var4.get(9) * 0.0F + var4.get(13);
        float var7 = var4.get(3) * (float)var0 + var4.get(7) * (float)var1 + var4.get(11) * 0.0F + var4.get(15);
        var5 /= var7;
        var6 /= var7;
        return new float[]{(float)Math.round(var5 * method11417()), (float)Math.round(var6 * method11417())};
    }

    public static void method11421(int var0, int var1, int var2, int var3, boolean var4) {
        if (!var4) {
            var0 = (int)((float)var0 * GuiManager.scaleFactor);
            var1 = (int)((float)var1 * GuiManager.scaleFactor);
            var2 = (int)((float)var2 * GuiManager.scaleFactor);
            var3 = (int)((float)var3 * GuiManager.scaleFactor);
        } else {
            float[] var7 = method11416(var0, var1);
            var0 = (int)var7[0];
            var1 = (int)var7[1];
            float[] var8 = method11416(var2, var3);
            var2 = (int)var8[0];
            var3 = (int)var8[1];
        }

        if (GL11.glIsEnabled(3089)) {
            IntBuffer var17 = BufferUtils.createIntBuffer(16);
            GL11.glGetIntegerv(3088, var17);
            buffer.push(var17);
            int var18 = var17.get(0);
            int var9 = mc.getMainWindow().getFramebufferHeight() - var17.get(1) - var17.get(3);
            int var10 = var18 + var17.get(2);
            int var11 = var9 + var17.get(3);
            if (var0 < var18) {
                var0 = var18;
            }

            if (var1 < var9) {
                var1 = var9;
            }

            if (var2 > var10) {
                var2 = var10;
            }

            if (var3 > var11) {
                var3 = var11;
            }

            if (var1 > var3) {
                var3 = var1;
            }

            if (var0 > var2) {
                var2 = var0;
            }
        }

        int var19 = mc.getMainWindow().getFramebufferHeight() - var3;
        int var20 = var2 - var0;
        int var21 = var3 - var1;
        GL11.glEnable(3089);
        if (var20 >= 0 && var21 >= 0) {
            GL11.glScissor(var0, var19, var20, var21);
        }
    }

    public static void drawImage(float x, float y, float var2, float var3, Texture tex, float alphaValue) {
        drawImage(x, y, var2, var3, tex, ColorUtils.applyAlpha(ClientColors.LIGHT_GREYISH_BLUE.getColor, alphaValue));
    }

    public static void method11455(float var0, float var1, float var2, float var3, Texture var4) {
        drawImage(var0, var1, var2, var3, var4, -1);
    }

    public static void drawImage(float var0, float var1, float var2, float var3, Texture var4, int var5) {
        drawImage(var0, var1, var2, var3, var4, var5, 0.0F, 0.0F, (float)var4.getImageWidth(), (float)var4.getImageHeight(), true);
    }

    public static void method11450(float var0, float var1, float var2, float var3, Texture var4, int var5, boolean var6) {
        drawImage(var0, var1, var2, var3, var4, var5, 0.0F, 0.0F, (float)var4.getImageWidth(), (float)var4.getImageHeight(), var6);
    }

    public static void method11451(float var0, float var1, float var2, float var3, Texture var4, int var5, float var6, float var7, float var8, float var9) {
        drawImage(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, true);
    }

    public static void drawImage(float var0, float var1, float var2, float var3, Texture var4, int var5, float var6, float var7, float var8, float var9, boolean var10) {
        if (var4 != null) {
            RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
            var0 = (float)Math.round(var0);
            var2 = (float)Math.round(var2);
            var1 = (float)Math.round(var1);
            var3 = (float)Math.round(var3);
            float var13 = (float)(var5 >> 24 & 0xFF) / 255.0F;
            float var14 = (float)(var5 >> 16 & 0xFF) / 255.0F;
            float var15 = (float)(var5 >> 8 & 0xFF) / 255.0F;
            float var16 = (float)(var5 & 0xFF) / 255.0F;
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.color4f(var14, var15, var16, var13);
            GL11.glEnable(3042);
            GL11.glEnable(3553);
            var4.bind();
            float var17 = var2 / (float)var4.getTextureWidth() / (var2 / (float)var4.getImageWidth());
            float var18 = var3 / (float)var4.getTextureHeight() / (var3 / (float)var4.getImageHeight());
            float var19 = var8 / (float)var4.getImageWidth() * var17;
            float var20 = var9 / (float)var4.getImageHeight() * var18;
            float var21 = var6 / (float)var4.getImageWidth() * var17;
            float var22 = var7 / (float)var4.getImageHeight() * var18;
            if (!var10) {
                GL11.glTexParameteri(3553, 10240, 9729);
            } else {
                GL11.glTexParameteri(3553, 10240, 9728);
            }

            GL11.glBegin(7);
            GL11.glTexCoord2f(var21, var22);
            GL11.glVertex2f(var0, var1);
            GL11.glTexCoord2f(var21, var22 + var20);
            GL11.glVertex2f(var0, var1 + var3);
            GL11.glTexCoord2f(var21 + var19, var22 + var20);
            GL11.glVertex2f(var0 + var2, var1 + var3);
            GL11.glTexCoord2f(var21 + var19, var22);
            GL11.glVertex2f(var0 + var2, var1);
            GL11.glEnd();
            GL11.glDisable(3553);
            GL11.glDisable(3042);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    public static void renderBackgroundBox(float var0, float var1, float var2, float var3, int var4) {
        drawRect(var0, var1, var0 + var2, var1 + var3, var4);
    }

    public static void drawRect(float var0, float var1, float var2, float var3, float var4, int var5) {
        drawRect(var0, var1 + var4, var0 + var2, var1 + var3 - var4, var5);
        drawRect(var0 + var4, var1, var0 + var2 - var4, var1 + var4, var5);
        drawRect(var0 + var4, var1 + var3 - var4, var0 + var2 - var4, var1 + var3, var5);
        method11418(var0, var1, var0 + var4, var1 + var4);
        method11438(var0 + var4, var1 + var4, var4 * 2.0F, var5);
        endScissor();
        method11418(var0 + var2 - var4, var1, var0 + var2, var1 + var4);
        method11438(var0 - var4 + var2, var1 + var4, var4 * 2.0F, var5);
        endScissor();
        method11418(var0, var1 + var3 - var4, var0 + var4, var1 + var3);
        method11438(var0 + var4, var1 - var4 + var3, var4 * 2.0F, var5);
        endScissor();
        method11418(var0 + var2 - var4, var1 + var3 - var4, var0 + var2, var1 + var3);
        method11438(var0 - var4 + var2, var1 - var4 + var3, var4 * 2.0F, var5);
        endScissor();
    }

    public static void method11418(float var0, float var1, float var2, float var3) {
        method11421((int)var0, (int)var1, (int)var2, (int)var3, true);
    }

    public static void method11438(float var0, float var1, float var2, int var3) {
        RenderSystem.color4f(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
        float var6 = (float)(var3 >> 24 & 0xFF) / 255.0F;
        float var7 = (float)(var3 >> 16 & 0xFF) / 255.0F;
        float var8 = (float)(var3 >> 8 & 0xFF) / 255.0F;
        float var9 = (float)(var3 & 0xFF) / 255.0F;
        Tessellator var10 = Tessellator.getInstance();
        BufferBuilder var11 = var10.getBuffer();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.color4f(var7, var8, var9, var6);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glPointSize(var2 * GuiManager.scaleFactor);
        GL11.glBegin(0);
        GL11.glVertex2f(var0, var1);
        GL11.glEnd();
        GL11.glDisable(2832);
        GL11.glDisable(3042);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawRect(float var0, float var1, float var2, float var3, int var4) {
        if (var0 < var2) {
            int var7 = (int)var0;
            var0 = var2;
            var2 = (float)var7;
        }

        if (var1 < var3) {
            int var13 = (int)var1;
            var1 = var3;
            var3 = (float)var13;
        }

        float var14 = (float)(var4 >> 24 & 0xFF) / 255.0F;
        float var8 = (float)(var4 >> 16 & 0xFF) / 255.0F;
        float var9 = (float)(var4 >> 8 & 0xFF) / 255.0F;
        float var10 = (float)(var4 & 0xFF) / 255.0F;
        Tessellator var11 = Tessellator.getInstance();
        BufferBuilder var12 = var11.getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.color4f(var8, var9, var10, var14);
        var12.begin(7, DefaultVertexFormats.POSITION);
        var12.pos((double)var0, (double)var3, 0.0).endVertex();
        var12.pos((double)var2, (double)var3, 0.0).endVertex();
        var12.pos((double)var2, (double)var1, 0.0).endVertex();
        var12.pos((double)var0, (double)var1, 0.0).endVertex();
        var11.draw();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

}
