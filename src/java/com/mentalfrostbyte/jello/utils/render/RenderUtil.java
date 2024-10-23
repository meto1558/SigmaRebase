package com.mentalfrostbyte.jello.utils.render;

import com.mentalfrostbyte.jello.managers.GuiManager;
import net.minecraft.client.Minecraft;
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

}
