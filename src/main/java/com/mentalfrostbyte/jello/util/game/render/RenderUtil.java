package com.mentalfrostbyte.jello.util.game.render;

import com.mentalfrostbyte.jello.gui.combined.CustomGuiScreen;
import com.mentalfrostbyte.jello.gui.impl.jello.ingame.buttons.keybind.Keys;
import com.mentalfrostbyte.jello.managers.GuiManager;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.data.ModuleCategory;
import com.mentalfrostbyte.jello.module.impl.render.jello.esp.util.StencilFunctionType;
import com.mentalfrostbyte.jello.util.client.render.FontSizeAdjust;
import com.mentalfrostbyte.jello.util.client.render.ResourceRegistry;
import com.mentalfrostbyte.jello.util.client.render.Resources;
import com.mentalfrostbyte.jello.util.client.render.theme.ClientColors;
import com.mentalfrostbyte.jello.util.game.MinecraftUtil;
import com.mentalfrostbyte.jello.util.game.world.BoundingBox;
import com.mentalfrostbyte.jello.util.system.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Stack;

import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

public class RenderUtil implements MinecraftUtil {
    public static boolean stencilOpInProgress = false;

    private static final Stack<IntBuffer> buffer = new Stack<>();

    public static void endScissor() {
        if (buffer.isEmpty()) {
            GL11.glDisable(GL_SCISSOR_TEST);
        } else {
            IntBuffer buffer = RenderUtil.buffer.pop();
            GL11.glScissor(buffer.get(0), buffer.get(1), buffer.get(2), buffer.get(3));
        }
    }

    public static void startScissorUnscaled(int x, int y, int width, int height) {
        startScissor(x, y, width, height, false);
    }

    public static float getGuiScaleFactor() {
        return (float) mc.mainWindow.getGuiScaleFactor();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        drawRect((float) left, (float) top, (float) right, (float) bottom, color);
    }

    public static void drawRectNormalised(float x, float y, float width, float height, int color) {
        drawRect(x, y, x + width, y + height, color);
    }

    public static void drawImage2(float x, float y, float width, float height, Texture texture, int color, boolean filtering) {
        drawImage(x, y, width, height, texture, color, 0.0F, 0.0F, (float) texture.getImageWidth(),
                (float) texture.getImageHeight(), filtering);
    }

    public static void renderWireframeBox(BoundingBox boxIn, int color) {
        renderWireframeBox(boxIn, 2.8F, color);
    }

    public static void renderWireframeBox(BoundingBox boxIn, float width, int color) {
        if (boxIn != null) {
            float var5 = (float) (color >> 24 & 0xFF) / 255.0F;
            float var6 = (float) (color >> 16 & 0xFF) / 255.0F;
            float var7 = (float) (color >> 8 & 0xFF) / 255.0F;
            float var8 = (float) (color & 0xFF) / 255.0F;
            GL11.glColor4f(var6, var7, var8, var5);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glLineWidth(width);
            GL11.glEnable(2848);
            GL11.glEnable(3042);
            GL11.glBegin(3);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.minZ);
            GL11.glEnd();
            GL11.glBegin(3);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.minZ);
            GL11.glEnd();
            GL11.glBegin(1);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.maxZ);
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glEnable(2896);
            GL11.glDisable(2848);
            GL11.glDisable(3042);
        }
    }

    public static boolean isStencilEnabled = false;

    public static void enableStencilBuffer() {
        GL11.glPushMatrix();
        resetDepthBuffer();
        GL11.glEnable(2960); // GL_STENCIL_TEST
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(false);
        GL11.glStencilFunc(512, 1, 1); // GL_EQUAL, ref=1, mask=1
        GL11.glStencilOp(7681, 7680, 7680); // GL_KEEP, GL_KEEP, GL_KEEP
        GL11.glStencilMask(1);
        GL11.glClear(1024); // GL_STENCIL_BUFFER_BIT
        isStencilEnabled = true;
    }

    public static void disableStencilBuffer() {
        GL11.glStencilMask(-1);
        GL11.glDisable(2960); // GL_STENCIL_TEST
        GL11.glPopMatrix();
        isStencilEnabled = false;
    }

    public static void setStencilFunction(StencilFunctionType type) {
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glStencilMask(0);
        GL11.glStencilFunc(type != StencilFunctionType.EQUAL ? 517 : 514, 1, 1);
        // 517 = GL_NOTEQUAL, 514 = GL_EQUAL
    }

    public static void resetColors() {
        RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public static void render3DColoredBox(BoundingBox boxIn, int color) {
        if (boxIn != null) {
            float alpha = (float) (color >> 24 & 0xFF) / 255.0F;
            float red = (float) (color >> 16 & 0xFF) / 255.0F;
            float green = (float) (color >> 8 & 0xFF) / 255.0F;
            float blue = (float) (color & 0xFF) / 255.0F;
            GL11.glColor4f(red, green, blue, alpha);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glLineWidth(1.8F * GuiManager.scaleFactor);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.minZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.maxY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.maxY, boxIn.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.maxZ);
            GL11.glEnd();
            GL11.glBegin(7);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.minZ);
            GL11.glVertex3d(boxIn.minX, boxIn.minY, boxIn.maxZ);
            GL11.glVertex3d(boxIn.maxX, boxIn.minY, boxIn.maxZ);
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glEnable(2896);
            GL11.glDisable(2848);
            GL11.glDisable(3042);
        }
    }

    /**
     * Transforms 2D coordinates using the current OpenGL model view matrix and applies scaling.
     * This method is typically used for converting screen coordinates to scaled OpenGL coordinates.
     *
     * @param x The x-coordinate to transform.
     * @param y The y-coordinate to transform.
     * @return A float array containing two elements:
     * [0] The transformed and scaled x-coordinate.
     * [1] The transformed and scaled y-coordinate.
     */
    public static float[] screenCoordinatesToOpenGLCoordinates(int x, int y) {
        FloatBuffer modelViewMatrix = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelViewMatrix);

        float glX = modelViewMatrix.get(0) * x + modelViewMatrix.get(4) * y + modelViewMatrix.get(8) * 0.0F + modelViewMatrix.get(12);
        float glY = modelViewMatrix.get(1) * x + modelViewMatrix.get(5) * y + modelViewMatrix.get(9) * 0.0F + modelViewMatrix.get(13);
        float glW = modelViewMatrix.get(3) * x + modelViewMatrix.get(7) * y + modelViewMatrix.get(11) * 0.0F + modelViewMatrix.get(15);

        glX /= glW;
        glY /= glW;

        float guiScale = getGuiScaleFactor();
        return new float[]{Math.round(glX * guiScale), Math.round(glY * guiScale)};
    }

    public static void drawHollowRect(double left, double top, double right, double bottom, double borderWidth, int fillColor, int borderColor) {
        drawRect(left + borderWidth, top + borderWidth, right - borderWidth, bottom - borderWidth, fillColor);  // Fill inner rect
        drawRect(left + borderWidth, top, right - borderWidth, top + borderWidth, borderColor);                  // Top border
        drawRect(left, top, left + borderWidth, bottom, borderColor);                                            // Left border
        drawRect(right - borderWidth, top, right, bottom, borderColor);                                          // Right border
        drawRect(left + borderWidth, bottom - borderWidth, right - borderWidth, bottom, borderColor);            // Bottom border
    }

    public static void startScissor(int x, int y, int width, int height, boolean alreadyScaled) {
        if (!alreadyScaled) {
            x = (int) (x * GuiManager.scaleFactor);
            y = (int) (y * GuiManager.scaleFactor);
            width = (int) (width * GuiManager.scaleFactor);
            height = (int) (height * GuiManager.scaleFactor);
        } else {
            float[] glCoordsXY = screenCoordinatesToOpenGLCoordinates(x, y);
            x = (int) glCoordsXY[0];
            y = (int) glCoordsXY[1];
            float[] glCoordsWH = screenCoordinatesToOpenGLCoordinates(width, height);
            width = (int) glCoordsWH[0];
            height = (int) glCoordsWH[1];
        }

        if (GL11.glIsEnabled(GL_SCISSOR_TEST)) {
            IntBuffer currentScissor = BufferUtils.createIntBuffer(16);
            GL11.glGetIntegerv(GL11.GL_SCISSOR_BOX, currentScissor);
            buffer.push(currentScissor);

            int scissorX = currentScissor.get(0);
            int scissorY = mc.getMainWindow().getFramebufferHeight() - currentScissor.get(1) - currentScissor.get(3);
            int scissorRight = scissorX + currentScissor.get(2);
            int scissorBottom = scissorY + currentScissor.get(3);

            if (x < scissorX) x = scissorX;
            if (y < scissorY) y = scissorY;
            if (width > scissorRight) width = scissorRight;
            if (height > scissorBottom) height = scissorBottom;
            if (y > height) height = y;
            if (x > width) width = x;
        }

        int adjustedY = mc.getMainWindow().getFramebufferHeight() - height;
        int scissorWidth = width - x;
        int scissorHeight = height - y;

        GL11.glEnable(GL_SCISSOR_TEST);
        if (scissorWidth >= 0 && scissorHeight >= 0) {
            GL11.glScissor(x, adjustedY, scissorWidth, scissorHeight);
        }
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        if (left > right) {
            float temp = left;
            left = right;
            right = temp;
        }

        if (top > bottom) {
            float temp = top;
            top = bottom;
            bottom = temp;
        }

        float alpha = (float) (color >> 24 & 0xFF) / 255.0F;
        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        RenderSystem.color4f(red, green, blue, alpha);

        buffer.begin(7, DefaultVertexFormats.POSITION);
        buffer.pos(left, bottom, 0.0).endVertex();
        buffer.pos(right, bottom, 0.0).endVertex();
        buffer.pos(right, top, 0.0).endVertex();
        buffer.pos(left, top, 0.0).endVertex();

        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawImage(float x, float y, float width, float height, Texture tex, float alphaValue) {
        drawImage(x, y, width, height, tex, MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), alphaValue));
    }

    public static void drawImage(float x, float y, float width, float height, Texture texture) {
        drawImage(x, y, width, height, texture, -1);
    }

    public static void drawImage(float x, float y, float width, float height, Texture texture, int color) {
        drawImage(x, y, width, height, texture, color, 0.0F, 0.0F, (float) texture.getImageWidth(), (float) texture.getImageHeight(), true);
    }

    public static void drawImage(float x, float y, float width, float height, Texture texture, int color, boolean linearFiltering) {
        drawImage(x, y, width, height, texture, color, 0.0F, 0.0F, (float) texture.getImageWidth(), (float) texture.getImageHeight(), linearFiltering);
    }

    public static void drawImage(float x, float y, float width, float height, Texture texture, int color, float tlX, float tlY, float siW, float siH) {
        drawImage(x, y, width, height, texture, color, tlX, tlY, siW, siH, true);
    }

    /**
     * Draws a sub-image of a texture to the screen.
     *
     * @param x               The x-coordinate of the top-left corner of the image.
     * @param y               The y-coordinate of the top-left corner of the image.
     * @param width           The width of the image.
     * @param height          The height of the image.
     * @param texture         The texture to draw from.
     * @param color           The color to draw the image in, represented as an integer.
     * @param tlX             The x-coordinate of the top-left corner of the sub-image within the texture.
     * @param tlY             The y-coordinate of the top-left corner of the sub-image within the texture.
     * @param siW             The width of the sub-image.
     * @param siH             The height of the sub-image.
     * @param linearFiltering Whether to use linear filtering for the texture.
     */
    public static void drawImage(float x, float y, float width, float height, Texture texture, int color, float tlX, float tlY, float siW, float siH, boolean linearFiltering) {
        if (texture != null) {
            RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
            x = (float) Math.round(x);
            width = (float) Math.round(width);
            y = (float) Math.round(y);
            height = (float) Math.round(height);
            float a = (float) (color >> 24 & 0xFF) / 255.0F;
            float r = (float) (color >> 16 & 0xFF) / 255.0F;
            float g = (float) (color >> 8 & 0xFF) / 255.0F;
            float b = (float) (color & 0xFF) / 255.0F;
            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.blendFuncSeparate(770, 771, 1, 0);
            RenderSystem.color4f(r, g, b, a);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            texture.bind();
            float var17 = width / (float) texture.getTextureWidth() / (width / (float) texture.getImageWidth());
            float var18 = height / (float) texture.getTextureHeight() / (height / (float) texture.getImageHeight());
            float var19 = siW / (float) texture.getImageWidth() * var17;
            float var20 = siH / (float) texture.getImageHeight() * var18;
            float var21 = tlX / (float) texture.getImageWidth() * var17;
            float var22 = tlY / (float) texture.getImageHeight() * var18;
            if (!linearFiltering) {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            } else {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            }

            GL11.glBegin(7);
            GL11.glTexCoord2f(var21, var22);
            GL11.glVertex2f(x, y);
            GL11.glTexCoord2f(var21, var22 + var20);
            GL11.glVertex2f(x, y + height);
            GL11.glTexCoord2f(var21 + var19, var22 + var20);
            GL11.glVertex2f(x + width, y + height);
            GL11.glTexCoord2f(var21 + var19, var22);
            GL11.glVertex2f(x + width, y);
            GL11.glEnd();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    public static void drawCircle(boolean isFadingOut, float circleHeight, float radius, float glowStrength, float glowOpacity) {
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBegin(GL11.GL_QUAD_STRIP);

        int angleStep = (int) (360.0F / (40.0F * radius));

        Module moduleInstance = new Module(ModuleCategory.PLAYER, "ESP COLOR", "");
        java.awt.Color espColor = new java.awt.Color(moduleInstance.parseSettingValueToIntBySettingName("ESP Color"));
        float red = (float) espColor.getRed() / 255.0F;
        float green = (float) espColor.getGreen() / 255.0F;
        float blue = (float) espColor.getBlue() / 255.0F;

        for (int angle = 0; angle <= 360 + angleStep; angle += angleStep) {
            int effectiveAngle = angle > 360 ? 0 : angle;

            double x = Math.sin(Math.toRadians(effectiveAngle)) * radius;
            double z = Math.cos(Math.toRadians(effectiveAngle)) * radius;

            GL11.glColor4f(red, green, blue, isFadingOut ? 0.0F : glowStrength * glowOpacity);
            GL11.glVertex3d(x, 0.0, z);

            GL11.glColor4f(red, green, blue, isFadingOut ? glowStrength * glowOpacity : 0.0F);
            GL11.glVertex3d(x, circleHeight, z);
        }

        GL11.glEnd();

        GL11.glLineWidth(2.2F);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int angle = 0; angle <= 360 + angleStep; angle += angleStep) {
            int effectiveAngle = angle > 360 ? 0 : angle;

            double x = Math.sin(Math.toRadians(effectiveAngle)) * radius;
            double z = Math.cos(Math.toRadians(effectiveAngle)) * radius;

            GL11.glColor4f(red, green, blue, (0.5F + 0.5F * glowStrength) * glowOpacity);
            GL11.glVertex3d(x, isFadingOut ? 0.0 : circleHeight, z);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderSystem.shadeModel(GL11.GL_FLAT);
    }

    public static void drawRoundedRect2(float x, float y, float width, float height, int color) {
        drawColoredRect(x, y, x + width, y + height, color);
    }

    public static void drawRoundedRect(float x, float y, float width, float height, float size, int color) {
        drawColoredRect(x, y + size, x + width, y + height - size, color);
        drawColoredRect(x + size, y, x + width - size, y + size, color);
        drawColoredRect(x + size, y + height - size, x + width - size, y + height, color);
        startScissorScaled(x, y, x + size, y + size);
        drawCircle(x + size, y + size, size * 2.0F, color);
        endScissor();
        startScissorScaled(x + width - size, y, x + width, y + size);
        drawCircle(x - size + width, y + size, size * 2.0F, color);
        endScissor();
        startScissorScaled(x, y + height - size, x + size, y + height);
        drawCircle(x + size, y - size + height, size * 2.0F, color);
        endScissor();
        startScissorScaled(x + width - size, y + height - size, x + width, y + height);
        drawCircle(x - size + width, y - size + height, size * 2.0F, color);
        endScissor();
    }

    public static void startScissorScaled(float x, float y, float width, float height) {
        startScissor((int) x, (int) y, (int) width, (int) height, true);
    }

    public static void drawCircle(float centerX, float centerY, float size, int color) {
        RenderSystem.color4f(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
        float a = (float) (color >> 24 & 0xFF) / 255.0F;
        float r = (float) (color >> 16 & 0xFF) / 255.0F;
        float g = (float) (color >> 8 & 0xFF) / 255.0F;
        float b = (float) (color & 0xFF) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.color4f(r, g, b, a);
        GL11.glEnable(GL11.GL_POINT_SMOOTH);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glPointSize(size * GuiManager.scaleFactor);
        GL11.glBegin(0);
        GL11.glVertex2f(centerX, centerY);
        GL11.glEnd();
        GL11.glDisable(GL11.GL_POINT_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawColoredRect(float x1, float y1, float x2, float y2, int color) {
        if (x1 < x2) {
            int tempX = (int) x1;
            x1 = x2;
            x2 = (float) tempX;
        }

        if (y1 < y2) {
            int tempY = (int) y1;
            y1 = y2;
            y2 = (float) tempY;
        }

        float alpha = (float) (color >> 24 & 0xFF) / 255.0F;
        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        RenderSystem.color4f(red, green, blue, alpha);

        buffer.begin(7, DefaultVertexFormats.POSITION); // 7 = GL_QUADS
        buffer.pos(x1, y2, 0.0).endVertex();
        buffer.pos(x2, y2, 0.0).endVertex();
        buffer.pos(x2, y1, 0.0).endVertex();
        buffer.pos(x1, y1, 0.0).endVertex();

        tessellator.draw();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawString(TrueTypeFont font, float x, float y, String text, int color, FontSizeAdjust widthAdjust, FontSizeAdjust heightAdjust) {
        drawString(font, x, y, text, color, widthAdjust, heightAdjust, false);
    }

    public static void drawString(TrueTypeFont font, float x, float y, String text, int color, FontSizeAdjust widthAdjust, FontSizeAdjust heightAdjust, boolean shadow) {
        resetColors();

        int adjustedWidth = 0;
        int adjustedHeight = 0;

        adjustedWidth = switch (widthAdjust) {
            case NEGATE_AND_DIVIDE_BY_2 -> -font.getWidth(text) / 2;
            case WIDTH_NEGATE -> -font.getWidth(text);
            default -> adjustedWidth;
        };

        adjustedHeight = switch (heightAdjust) {
            case NEGATE_AND_DIVIDE_BY_2 -> -font.getHeight(text) / 2;
            case HEIGHT_NEGATE -> -font.getHeight(text);
            default -> adjustedHeight;
        };

        float alpha = (float) (color >> 24 & 0xFF) / 255.0F;
        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;
        GL11.glPushMatrix();

        boolean var16 = false;
        if ((double) GuiManager.scaleFactor == 2.0) {
            if (font == ResourceRegistry.JelloLightFont20) {
                font = ResourceRegistry.JelloLightFont40;
            } else if (font == ResourceRegistry.JelloLightFont25) {
                font = ResourceRegistry.JelloLightFont50;
            } else if (font == ResourceRegistry.JelloLightFont12) {
                font = ResourceRegistry.JelloLightFont24;
            } else if (font == ResourceRegistry.JelloLightFont14) {
                font = ResourceRegistry.JelloLightFont28;
            } else if (font == ResourceRegistry.JelloLightFont18) {
                font = ResourceRegistry.JelloLightFont36;
            } else if (font == ResourceRegistry.RegularFont20) {
                font = ResourceRegistry.RegularFont40;
            } else if (font == ResourceRegistry.JelloMediumFont20) {
                font = ResourceRegistry.JelloMediumFont40;
            } else if (font == ResourceRegistry.JelloMediumFont25) {
                font = ResourceRegistry.JelloMediumFont50;
            } else {
                var16 = true;
            }

            if (!var16) {
                GL11.glTranslatef(x, y, 0.0F);
                GL11.glScalef(1.0F / GuiManager.scaleFactor, 1.0F / GuiManager.scaleFactor, 1.0F / GuiManager.scaleFactor);
                GL11.glTranslatef(-x, -y, 0.0F);
                adjustedWidth = (int) ((float) adjustedWidth * GuiManager.scaleFactor);
                adjustedHeight = (int) ((float) adjustedHeight * GuiManager.scaleFactor);
            }
        }

        RenderSystem.enableBlend();
        GL11.glBlendFunc(770, 771);

        if (shadow) {
            font.drawString((float) Math.round(x + (float) adjustedWidth), (float) (Math.round(y + (float) adjustedHeight) + 2), text, new Color(0.0F, 0.0F, 0.0F, 0.35F));
        }

        if (text != null) {
            font.drawString((float) Math.round(x + (float) adjustedWidth), (float) Math.round(y + (float) adjustedHeight), text, new Color(red, green, blue, alpha));
        }

        RenderSystem.disableBlend();
        GL11.glPopMatrix();
    }

    public static void startScissor(CustomGuiScreen screen) {
        startScissor(screen.getXA(), screen.getYA(), screen.getWidthA() + screen.getXA(), screen.getHeightA() + screen.getYA(), true);
    }

    public static void drawRoundedButton(float x, float y, float width, float height, float radius, int color) {
        drawColoredRect(x, y + radius, x + width, y + height - radius, color);
        drawColoredRect(x + radius, y, x + width - radius, y + height, color);

        FloatBuffer modelViewMatrixBuffer = BufferUtils.createFloatBuffer(16);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelViewMatrixBuffer);

        float scale = 1.0F;

        drawCircle(x + radius, y + radius, radius * 2.0F * scale, color);
        drawCircle(x + width - radius, y + radius, radius * 2.0F * scale, color);
        drawCircle(x + radius, y + height - radius, radius * 2.0F * scale, color);
        drawCircle(x + width - radius, y + height - radius, radius * 2.0F * scale, color);
    }

    public static void drawString(TrueTypeFont font, float x, float y, String text, int color) {
        drawString(font, x, y, text, color, FontSizeAdjust.field14488, FontSizeAdjust.field14489, false);
    }

    public static void drawRoundedRect(
            float x, float y, float width, float height, float cornerSize, float alpha
    ) {
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

        int shadowColorWithAlpha = MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), alpha);

        // Draw corners
        drawImage(x - cornerSize, y - cornerSize, cornerSize, cornerSize, Resources.shadowCorner1PNG, shadowColorWithAlpha);
        drawImage(x + width, y - cornerSize, cornerSize, cornerSize, Resources.shadowCorner2PNG, shadowColorWithAlpha);
        drawImage(x - cornerSize, y + height, cornerSize, cornerSize, Resources.shadowCorner3PNG, shadowColorWithAlpha);
        drawImage(x + width, y + height, cornerSize, cornerSize, Resources.shadowCorner4PNG, shadowColorWithAlpha);

        // Draw edges (no repeat)
        drawImage(x - cornerSize, y, cornerSize, height, Resources.shadowLeftPNG, shadowColorWithAlpha, false);
        drawImage(x + width, y, cornerSize, height, Resources.shadowRightPNG, shadowColorWithAlpha, false);
        drawImage(x, y - cornerSize, width, cornerSize, Resources.shadowTopPNG, shadowColorWithAlpha, false);
        drawImage(x, y + height, width, cornerSize, Resources.shadowBottomPNG, shadowColorWithAlpha, false);
    }

    public static void startScissor(float x, float y, float width, float height) {
        startScissor((int) x, (int) y, (int) x + (int) width, (int) y + (int) height, true);
    }

    public static void drawFloatingFrame(int x, int y, int width, int height, int color) {
        drawFloatingFrame(x, y, width, height, color, x, y);
    }

    public static void drawFloatingFrame(int x, int y, int width, int height, int color, int blurX, int blurY) {
        int cornerSize = 36;
        int offset = 10;
        int innerOffset = cornerSize - offset;

        // Draw rounded rectangle base
        drawColoredRect(
                (float) (x + offset),
                (float) (y + offset),
                (float) (x + width - offset),
                (float) (y + height - offset),
                color
        );

        // Draw top-left corner image
        drawImage((float) (x - innerOffset), (float) (y - innerOffset), (float) cornerSize, (float) cornerSize, Resources.floatingCornerPNG, color);

        // Draw top-right corner with rotation
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (x + width - cornerSize / 2), (float) (y + cornerSize / 2), 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef((float) (-x - width - cornerSize / 2), (float) (-y - cornerSize / 2), 0.0F);
        drawImage((float) (x + width - innerOffset), (float) (y - innerOffset), (float) cornerSize, (float) cornerSize, Resources.floatingCornerPNG, color);
        GL11.glPopMatrix();

        // Draw bottom-right corner with rotation
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (x + width - cornerSize / 2), (float) (y + height + cornerSize / 2), 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef((float) (-x - width - cornerSize / 2), (float) (-y - height - cornerSize / 2), 0.0F);
        drawImage((float) (x + width - innerOffset), (float) (y + offset + height), (float) cornerSize, (float) cornerSize, Resources.floatingCornerPNG, color);
        GL11.glPopMatrix();

        // Draw bottom-left corner with rotation
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (x - cornerSize / 2), (float) (y + height + cornerSize / 2), 0.0F);
        GL11.glRotatef(270.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef((float) (-x - cornerSize / 2), (float) (-y - height - cornerSize / 2), 0.0F);
        drawImage((float) (x + offset), (float) (y + offset + height), (float) cornerSize, (float) cornerSize, Resources.floatingCornerPNG, color);
        GL11.glPopMatrix();

        // Draw blurred background behind left border
        startScissorUnscaled(blurX - cornerSize, blurY + offset, blurX - innerOffset + cornerSize, blurY - offset + height);

        // Draw left border repeated vertically
        for (int i = 0; i < height; i += cornerSize) {
            drawImage((float) (x - innerOffset), (float) (y + offset + i), (float) cornerSize, (float) cornerSize, Resources.floatingBorderPNG, color);
        }
        endScissor();

        // Draw blurred background behind top border
        startScissorUnscaled(blurX, blurY - innerOffset, blurX + width - offset, blurY + offset);

        // Draw top border repeated horizontally with rotation
        for (int i = 0; i < width; i += cornerSize) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) (x + cornerSize / 2), (float) (y + cornerSize / 2), 0.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef((float) (-x - cornerSize / 2), (float) (-y - cornerSize / 2), 0.0F);
            drawImage((float) (x - innerOffset), (float) (y - offset - i), (float) cornerSize, (float) cornerSize, Resources.floatingBorderPNG, color);
            GL11.glPopMatrix();
        }
        endScissor();

        // Draw blurred background behind right border
        startScissorUnscaled(blurX + width - offset, blurY - innerOffset, x + width + innerOffset, blurY + height - offset);

        // Draw right border repeated vertically with 180 rotation
        for (int i = 0; i < height; i += cornerSize) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) (x + cornerSize / 2), (float) (y + cornerSize / 2), 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef((float) (-x - cornerSize / 2), (float) (-y - cornerSize / 2), 0.0F);
            drawImage((float) (x - width + offset), (float) (y - offset - i), (float) cornerSize, (float) cornerSize, Resources.floatingBorderPNG, color);
            GL11.glPopMatrix();
        }
        endScissor();

        // Draw blurred background behind bottom border
        startScissorUnscaled(blurX - offset, blurY - innerOffset + height - cornerSize, blurX + width - offset, blurY + height + offset * 2);

        // Draw bottom border repeated horizontally with 270 rotation
        for (int i = 0; i < width; i += cornerSize) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) (x + cornerSize / 2), (float) (y + cornerSize / 2), 0.0F);
            GL11.glRotatef(270.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef((float) (-x - cornerSize / 2), (float) (-y - cornerSize / 2), 0.0F);
            drawImage((float) (x - height + offset), (float) (y + offset + i), (float) cornerSize, (float) cornerSize, Resources.floatingBorderPNG, color);
            GL11.glPopMatrix();
        }
        endScissor();
    }

    public static void drawTexture(float x, float y, float width, float height, Texture texture, int color) {
        if (texture == null) {
            return;
        }

        drawImage(x, y, width, height, texture, color, 0.0F, 0.0F, (float) texture.getImageWidth(), (float) texture.getImageHeight(), true);
        drawImage(x, y, width, height, texture, color, 0.0F, 0.0F, (float) texture.getImageWidth(), (float) texture.getImageHeight(), false);
    }

    public static void drawTexturedRect(
            float x, float y, float width, float height,
            int color,
            float textureX, float textureY, float textureWidth, float textureHeight
    ) {
        RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);

        x = (float) Math.round(x);
        width = (float) Math.round(width);
        y = (float) Math.round(y);
        height = (float) Math.round(height);

        float alpha = (float) (color >> 24 & 0xFF) / 255.0F;
        float red = (float) (color >> 16 & 0xFF) / 255.0F;
        float green = (float) (color >> 8 & 0xFF) / 255.0F;
        float blue = (float) (color & 0xFF) / 255.0F;

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0); // GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO
        RenderSystem.color4f(red, green, blue, alpha);

        GL11.glEnable(3042);  // GL_BLEND
        GL11.glEnable(3553);  // GL_TEXTURE_2D

        // Reset pixel store parameters
        GL11.glPixelStorei(3312, 0);
        GL11.glPixelStorei(3313, 0);
        GL11.glPixelStorei(3314, 0);
        GL11.glPixelStorei(3315, 0);
        GL11.glPixelStorei(3316, 0);
        GL11.glPixelStorei(3317, 4);

        float texCoordWidth = 1.0f;
        float texCoordHeight = 1.0f;

        float uStart = textureX / textureWidth;
        float vStart = textureY / textureHeight;

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(uStart, vStart);
        GL11.glVertex2f(x, y);

        GL11.glTexCoord2f(uStart, vStart + texCoordHeight);
        GL11.glVertex2f(x, y + height);

        GL11.glTexCoord2f(uStart + texCoordWidth, vStart + texCoordHeight);
        GL11.glVertex2f(x + width, y + height);

        GL11.glTexCoord2f(uStart + texCoordWidth, vStart);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();

        GL11.glDisable(3553);
        GL11.glDisable(3042);

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawShadowedBorder(float x, float y, float width, float height, float borderThickness, float alpha) {
        int shadowColor = MathHelper.applyAlpha2(ClientColors.LIGHT_GREYISH_BLUE.getColor(), alpha);

        drawImage(x, y, borderThickness, height, Resources.shadowRightPNG, shadowColor, false);
        drawImage(x + width - borderThickness, y, borderThickness, height, Resources.shadowLeftPNG, shadowColor, false);
        drawImage(x, y, width, borderThickness, Resources.shadowBottomPNG, shadowColor, false);
        drawImage(x, y + height - borderThickness, width, borderThickness, Resources.shadowTopPNG, shadowColor, false);
    }

    public static void drawFloatingFrame2(int x, int y, int width, int height, int color) {
        int cornerSize = 36;
        int padding = 10;
        int offset = cornerSize - padding;

        // Draw inner colored rectangle (frame background)
        drawColoredRect(
                (float) (x + padding),
                (float) (y + padding),
                (float) (x + width - padding),
                (float) (y + height - padding),
                color
        );

        // Draw top-left corner image
        drawImage((float) (x - offset), (float) (y - offset), (float) cornerSize, (float) cornerSize, Resources.floatingCornerPNG, color);

        // Draw top-right corner with rotation
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (x + width - cornerSize / 2), (float) (y + cornerSize / 2), 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef((float) (-x - width - cornerSize / 2), (float) (-y - cornerSize / 2), 0.0F);
        drawImage((float) (x + width - offset), (float) (y - offset), (float) cornerSize, (float) cornerSize, Resources.floatingCornerPNG, color);
        GL11.glPopMatrix();

        // Draw bottom-right corner with rotation
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (x + width - cornerSize / 2), (float) (y + height + cornerSize / 2), 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef((float) (-x - width - cornerSize / 2), (float) (-y - height - cornerSize / 2), 0.0F);
        drawImage((float) (x + width - offset), (float) (y + padding + height), (float) cornerSize, (float) cornerSize, Resources.floatingCornerPNG, color);
        GL11.glPopMatrix();

        // Draw bottom-left corner with rotation
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (x - cornerSize / 2), (float) (y + height + cornerSize / 2), 0.0F);
        GL11.glRotatef(270.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef((float) (-x - cornerSize / 2), (float) (-y - height - cornerSize / 2), 0.0F);
        drawImage((float) (x + padding), (float) (y + padding + height), (float) cornerSize, (float) cornerSize, Resources.floatingCornerPNG, color);
        GL11.glPopMatrix();

        // Left vertical border with scissor and repeated images
        startScissor(x - cornerSize, y + padding, x - offset + cornerSize, y - padding + height, true);
        for (int i = 0; i < height; i += cornerSize) {
            drawImage((float) (x - offset), (float) (y + padding + i) - 0.4F, (float) cornerSize, (float) cornerSize + 0.4F, Resources.floatingBorderPNG, color);
        }
        endScissor();

        // Top horizontal border with scissor and repeated rotated images
        startScissor(x, y - offset, x + width - padding, y + padding, true);
        for (int i = 0; i < width; i += cornerSize) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) (x + cornerSize / 2), (float) (y + cornerSize / 2), 0.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef((float) (-x - cornerSize / 2), (float) (-y - cornerSize / 2), 0.0F);
            drawImage((float) (x - offset), (float) (y - padding - i) - 0.4F, (float) cornerSize, (float) cornerSize + 0.4F, Resources.floatingBorderPNG, color);
            GL11.glPopMatrix();
        }
        endScissor();

        // Right vertical border with scissor and repeated rotated images
        startScissor(x + width - padding, y - offset, x + width + offset, y + height - padding, true);
        for (int i = 0; i < height; i += cornerSize) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) (x + cornerSize / 2), (float) (y + cornerSize / 2), 0.0F);
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef((float) (-x - cornerSize / 2), (float) (-y - cornerSize / 2), 0.0F);
            drawImage((float) (x - width + padding), (float) (y - padding - i) - 0.4F, (float) cornerSize, (float) cornerSize + 0.4F, Resources.floatingBorderPNG, color);
            GL11.glPopMatrix();
        }
        endScissor();

        // Bottom horizontal border with scissor and repeated rotated images
        startScissor(x - padding, y - offset + height - cornerSize, x + width - padding, y + height + padding * 2, true);
        for (int i = 0; i < width; i += cornerSize) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) (x + cornerSize / 2), (float) (y + cornerSize / 2), 0.0F);
            GL11.glRotatef(270.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef((float) (-x - cornerSize / 2), (float) (-y - cornerSize / 2), 0.0F);
            drawImage((float) (x - height + padding), (float) (y + padding + i) - 0.4F, (float) cornerSize, (float) cornerSize + 0.4F, Resources.floatingBorderPNG, color);
            GL11.glPopMatrix();
        }
        endScissor();
    }

    public static void drawFilledArc(float centerX, float centerY, float radius, int color) {
        // Draws a full filled circle with radius slightly less by 1
        drawFilledArc(centerX, centerY, 0.0F, 360.0F, radius - 1.0F, color);
    }

    public static void drawFilledArc(float centerX, float centerY, float startAngle, float endAngle, float radius, int color) {
        // Draws an arc with equal horizontal and vertical radii (circle segment)
        drawFilledArc(centerX, centerY, startAngle, endAngle, radius, radius, color);
    }

    public static void drawFilledArc(float centerX, float centerY, float startAngle, float endAngle, float hRadius, float vRadius, int color) {
        resetColors();

        if (startAngle > endAngle) {
            float temp = endAngle;
            endAngle = startAngle;
            startAngle = temp;
        }

        float alpha = ((color >> 24) & 0xFF) / 255f;
        float red = ((color >> 16) & 0xFF) / 255f;
        float green = ((color >> 8) & 0xFF) / 255f;
        float blue = (color & 0xFF) / 255f;


        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.color4f(red, green, blue, alpha);

        // Draw arc outline if sufficiently opaque
        if (alpha > 0.5f) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH); // 2848
            GL11.glLineWidth(2.0f);
            GL11.glBegin(GL11.GL_LINE_STRIP); // 3

            for (float angle = endAngle; angle >= startAngle; angle -= 4.0f) {
                float rad = (float) Math.toRadians(angle);
                float x = (float) Math.cos(rad) * hRadius * 1.001f;
                float y = (float) Math.sin(rad) * vRadius * 1.001f;
                GL11.glVertex2f(centerX + x, centerY + y);
            }

            GL11.glEnd();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        // Draw filled arc as triangle fan (GL_TRIANGLE_FAN = 6)
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        // Center vertex for triangle fan
        GL11.glVertex2f(centerX, centerY);

        for (float angle = endAngle; angle >= startAngle; angle -= 4.0f) {
            float rad = (float) Math.toRadians(angle);
            float x = (float) Math.cos(rad) * hRadius;
            float y = (float) Math.sin(rad) * vRadius;
            GL11.glVertex2f(centerX + x, centerY + y);
        }

        GL11.glEnd();

        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static String getKeyName(int keyCode) {
        // Check if keyCode matches any custom Keys enum entry
        for (Keys key : Keys.values()) {
            if (key.row == keyCode) {
                return key.name;
            }
        }

        // Get input mapping for the keyCode (assuming 0 is the default device)
        InputMappings.Input input = InputMappings.getInputByCode(keyCode, 0);
        String[] parts = input.getTranslationKey().split("\\.");

        if (parts.length != 0) {
            String lastPart = parts[parts.length - 1];
            if (!lastPart.isEmpty()) {
                String prefix = "";
                // If keyCode <= 4, consider it a mouse button
                if (keyCode <= 4) {
                    prefix = "Mouse ";
                }
                // Capitalize first letter and concatenate with prefix
                return prefix + lastPart.substring(0, 1).toUpperCase() + lastPart.substring(1);
            } else {
                return "Unknown";
            }
        } else {
            return "Unknown";
        }
    }

    public static void drawBorder(float x1, float y1, float x2, float y2, int thickness, int color) {
        // Bottom border
        drawColoredRect(x1, y2 - thickness, x2 - thickness, y2, color);

        // Top border
        drawColoredRect(x1, y1, x2 - thickness, y1 + thickness, color);

        // Left border
        drawColoredRect(x1, y1 + thickness, x1 + thickness, y2 - thickness, color);

        // Right border
        drawColoredRect(x2 - thickness, y1, x2, y2, color);
    }

    public static void drawFilledTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int color) {
        // Set OpenGL and RenderSystem state for transparent colored drawing
        RenderSystem.color4f(0f, 0f, 0f, 1f);
        GL11.glColor4f(0f, 0f, 0f, 0f);

        // Extract color components (ARGB)
        float alpha = (float) (color >> 24 & 0xFF) / 255f;
        float red   = (float) (color >> 16 & 0xFF) / 255f;
        float green = (float) (color >> 8 & 0xFF) / 255f;
        float blue  = (float) (color & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        RenderSystem.color4f(red, green, blue, alpha);

        // Begin drawing a triangle (GL_TRIANGLE_FAN = 6)
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glVertex2f(x3, y3);
        GL11.glVertex2f(x1, y1); // Closing the fan, optional for triangles but harmless
        GL11.glEnd();

        // Restore OpenGL state
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawBorder(float x, float y, float width, float height, int color) {
        drawBorder(x, y, width, height, 1, color);
    }

    public static void drawVerticalGradientRect(int left, int top, int right, int bottom, int startColorARGB, int endColorARGB) {
        float startAlpha = ((startColorARGB >> 24) & 0xFF) / 255.0F;
        float startRed = ((startColorARGB >> 16) & 0xFF) / 255.0F;
        float startGreen = ((startColorARGB >> 8) & 0xFF) / 255.0F;
        float startBlue = (startColorARGB & 0xFF) / 255.0F;

        float endAlpha = ((endColorARGB >> 24) & 0xFF) / 255.0F;
        float endRed = ((endColorARGB >> 16) & 0xFF) / 255.0F;
        float endGreen = ((endColorARGB >> 8) & 0xFF) / 255.0F;
        float endBlue = (endColorARGB & 0xFF) / 255.0F;

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Top-right vertex (start color)
        buffer.pos(right, top, 0.0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        // Top-left vertex (start color)
        buffer.pos(left, top, 0.0).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        // Bottom-left vertex (end color)
        buffer.pos(left, bottom, 0.0).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        // Bottom-right vertex (end color)
        buffer.pos(right, bottom, 0.0).color(endRed, endGreen, endBlue, endAlpha).endVertex();

        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    /**
     * Renders a quad with the specified parameters.
     *
     * @param x1     The x-coordinate of the first vertex.
     * @param y1     The y-coordinate of the first vertex.
     * @param x2     The x-coordinate of the second vertex.
     * @param y2     The y-coordinate of the second vertex.
     * @param color3 The x-coordinate of the fourth vertex.
     * @param color  The color of the quad in integer format.
     */
    public static void drawQuad(int x1, int y1, int x2, int y2, int color, int color2, int color3, int color4) {
        float a1 = (float) (color >> 24 & 0xFF) / 255.0F;
        float r1 = (float) (color >> 16 & 0xFF) / 255.0F;
        float g1 = (float) (color >> 8 & 0xFF) / 255.0F;
        float b1 = (float) (color & 0xFF) / 255.0F;
        float a2 = (float) (color2 >> 24 & 0xFF) / 255.0F;
        float r2 = (float) (color2 >> 16 & 0xFF) / 255.0F;
        float g2 = (float) (color2 >> 8 & 0xFF) / 255.0F;
        float b2 = (float) (color2 & 0xFF) / 255.0F;
        float a3 = (float) (color3 >> 24 & 0xFF) / 255.0F;
        float r3 = (float) (color3 >> 16 & 0xFF) / 255.0F;
        float g3 = (float) (color3 >> 8 & 0xFF) / 255.0F;
        float b3 = (float) (color3 & 0xFF) / 255.0F;
        float a4 = (float) (color4 >> 24 & 0xFF) / 255.0F;
        float r4 = (float) (color4 >> 16 & 0xFF) / 255.0F;
        float g4 = (float) (color4 >> 8 & 0xFF) / 255.0F;
        float b4 = (float) (color4 & 0xFF) / 255.0F;
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder var27 = tessellator.getBuffer();
        var27.begin(7, DefaultVertexFormats.POSITION_COLOR);
        var27.pos(x2, y1, 0.0).color(r2, g2, b2, a2).endVertex();
        var27.pos(x1, y1, 0.0).color(r1, g1, b1, a1).endVertex();
        var27.pos(x1, y2, 0.0).color(r4, g4, b4, a4).endVertex();
        var27.pos(x2, y2, 0.0).color(r3, g3, b3, a3).endVertex();
        tessellator.draw();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    /**
     * Renders a category box with the specified parameters.
     *
     * @param x            The x-coordinate of the top-left corner of the box.
     * @param y            The y-coordinate of the top-left corner of the box.
     * @param size         The size of the box.
     * @param color        The color of the box in integer format.
     * @param outlineColor The color of the box outline in integer format.
     */
    public static void renderCategoryBox(float x, float y, float size, int color, int outlineColor) {
        RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4fv(MathHelper.intColorToFloatArrayColor(color));
        GL11.glEnable(2881);
        GL11.glBegin(4);
        GL11.glVertex2f(x + size / 2.0F, y + size / 2.0F);
        GL11.glVertex2f(x + size / 2.0F, y - size / 2.0F);
        GL11.glVertex2f(x - size / 2.0F, y);
        GL11.glEnd();
        GL11.glLineWidth(2.0F);
        GL11.glColor4fv(MathHelper.intColorToFloatArrayColor(outlineColor));
        GL11.glBegin(3);
        GL11.glVertex2f(x + size / 2.0F, y + size / 2.0F);
        GL11.glVertex2f(x + size / 2.0F, y - size / 2.0F);
        GL11.glEnd();
        GL11.glBegin(3);
        GL11.glVertex2f(x - size / 2.0F, y);
        GL11.glVertex2f(x + size / 2.0F, y - size / 2.0F);
        GL11.glEnd();
        GL11.glBegin(3);
        GL11.glVertex2f(x + size / 2.0F, y + size / 2.0F);
        GL11.glVertex2f(x - size / 2.0F, y);
        GL11.glEnd();
        GL11.glDisable(2881);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void recreateDepthBuffer(Framebuffer frameBuffer) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(frameBuffer.depthBuffer);
        int newDepthBuffer = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(36161, newDepthBuffer);
        EXTFramebufferObject.glRenderbufferStorageEXT(36161, 34041, Minecraft.getInstance().getMainWindow().getFramebufferWidth(), Minecraft.getInstance().getMainWindow().getFramebufferHeight());
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36128, 36161, newDepthBuffer);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(36160, 36096, 36161, newDepthBuffer);
    }

    public static void resetDepthBuffer() {
        Framebuffer currentFramebuffer = Minecraft.getInstance().getFramebuffer();

        if (currentFramebuffer != null && currentFramebuffer.depthBuffer > -1) {
            recreateDepthBuffer(currentFramebuffer);
            currentFramebuffer.depthBuffer = -1;
        }
    }

    /**
     * Initializes the stencil buffer for rendering operations.
     * This method sets up the OpenGL state for stencil testing, disables color and depth writing,
     * configures the stencil function and operation, and clears the stencil buffer.
     * It's typically used before performing stencil-based rendering techniques.
     * <p>
     * The method performs the following operations:
     * 1. Pushes the current matrix onto the stack.
     * 2. Resets the depth buffer.
     * 3. Enables stencil testing.
     * 4. Disables color and depth writing.
     * 5. Sets up the stencil function and operation.
     * 6. Clears the stencil buffer.
     * 7. Sets a flag indicating that stencil operations are in progress.
     * <p>
     * Note: This method doesn't take any parameters and doesn't return any value.
     * It modifies the OpenGL state and should be used in conjunction with method11478()
     * to restore the previous state after stencil operations are complete.
     */
    public static void initStencilBuffer() {
        GL11.glPushMatrix();
        resetDepthBuffer();
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glColorMask(false, false, false, false);
        GL11.glDepthMask(false);
        GL11.glStencilFunc(GL11.GL_ACCUM_BUFFER_BIT, 1, 1);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glStencilMask(1);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        stencilOpInProgress = true;
    }

    /**
     * Restores the previous stencil buffer state after stencil operations.
     * This method resets the OpenGL state modified by initStencilBuffer().
     * It disables stencil testing, restores the previous matrix state,
     * and marks stencil operations as no longer in progress.
     * <p>
     * The method performs the following operations:
     * 1. Resets the stencil mask to allow writing to all bits.
     * 2. Disables stencil testing.
     * 3. Pops the matrix stack, restoring the previous transformation state.
     * 4. Sets the stencil operation flag to false.
     * <p>
     * This method should be called after completing stencil-based rendering
     * to restore the OpenGL state to its previous condition.
     */
    public static void restorePreviousStencilBuffer() {
        GL11.glStencilMask(-1);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glPopMatrix();
        stencilOpInProgress = false;
    }

    public static void configureStencilTest() {
        GL11.glColorMask(true, true, true, true);
        GL11.glDepthMask(true);
        GL11.glStencilMask(0);
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 1);
    }

    public static void drawTexturedQuad(
            float x, float y, float width, float height, ByteBuffer pixelData,
            int rgbaColor, float textureU, float textureV, float textureWidth, float textureHeight,
            boolean flipU, boolean flipV) {

        if (pixelData != null) {
            RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.0F);

            x = (float) Math.round(x);
            width = (float) Math.round(width);
            y = (float) Math.round(y);
            height = (float) Math.round(height);

            float alpha = (float) ((rgbaColor >> 24) & 0xFF) / 255.0F;
            float red = (float) ((rgbaColor >> 16) & 0xFF) / 255.0F;
            float green = (float) ((rgbaColor >> 8) & 0xFF) / 255.0F;
            float blue = (float) (rgbaColor & 0xFF) / 255.0F;

            RenderSystem.enableBlend();
            RenderSystem.disableTexture();
            RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            RenderSystem.color4f(red, green, blue, alpha);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);

            GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, (int) textureWidth, (int) textureHeight, 0,
                    GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixelData);

            float one = 1.0f;
            float texU = textureU / textureWidth;
            float texV = textureV / textureHeight;

            GL11.glBegin(GL11.GL_QUADS);
            GL11.glTexCoord2f(texU + (!flipU ? 0.0F : one), texV + (!flipV ? 0.0F : one));
            GL11.glVertex2f(x, y);

            GL11.glTexCoord2f(texU + (!flipU ? 0.0F : one), texV + (!flipV ? one : 0.0F));
            GL11.glVertex2f(x, y + height);

            GL11.glTexCoord2f(texU + (!flipU ? one : 0.0F), texV + (!flipV ? one : 0.0F));
            GL11.glVertex2f(x + width, y + height);

            GL11.glTexCoord2f(texU + (!flipU ? one : 0.0F), texV + (!flipV ? 0.0F : one));
            GL11.glVertex2f(x + width, y);
            GL11.glEnd();

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }

    public static void renderGuiItem(ItemStack itemStack, int x, int y, int width, int height) {
        if (itemStack != null) {
            mc.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);

            GL11.glPushMatrix();
            GL11.glTranslatef((float) x, (float) y, 0.0F);
            GL11.glScalef((float) width / 16.0F, (float) height / 16.0F, 0.0F);

            ItemRenderer itemRenderer = mc.getItemRenderer();

            if (itemStack.getCount() == 0) {
                itemStack = new ItemStack(itemStack.getItem());
            }

            RenderHelper.setupGuiFlatDiffuseLighting();
            GL11.glLightModelfv(GL11.GL_LIGHT_MODEL_TWO_SIDE, new float[]{0.4F, 0.4F, 0.4F, 1.0F});

            RenderSystem.enableColorMaterial();
            RenderSystem.disableLighting();
            RenderSystem.enableBlend();

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDepthFunc(GL11.GL_LEQUAL);

            itemRenderer.renderItemIntoGUI(itemStack, 0, 0);

            GL11.glDepthFunc(GL11.GL_LESS);

            RenderSystem.popMatrix();

            GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

            RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);

            RenderSystem.disableDepthTest();

            TextureImpl.unbind();

            mc.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);

            RenderHelper.setupGui3DDiffuseLighting();
        }
    }

    public static double[] worldToScreen(double x, double y, double z) {
        FloatBuffer screenCoords = BufferUtils.createFloatBuffer(3);
        IntBuffer viewport = BufferUtils.createIntBuffer(16);
        FloatBuffer modelViewMatrix = BufferUtils.createFloatBuffer(16);
        FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);

        // Get the current OpenGL matrices and viewport
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelViewMatrix);
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

        // Project the world coordinates to screen coordinates
        boolean isSuccessful = projectToScreen((float) x, (float) y, (float) z, modelViewMatrix, projectionMatrix, viewport, screenCoords);

        // Return the screen coordinates if successful, otherwise return null
        return !isSuccessful
                ? null
                : new double[]{
                (double) (screenCoords.get(0) / GuiManager.scaleFactor),
                (double) (((float) mc.getFramebuffer().framebufferHeight - screenCoords.get(1)) / GuiManager.scaleFactor),
                (double) screenCoords.get(2)
        };
    }

    public static java.awt.Color getScreenPixelColor(int screenX, int screenY) {
        screenX = (int) (screenX * GuiManager.scaleFactor);
        screenY = (int) (screenY * GuiManager.scaleFactor);

        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(3);

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glReadPixels(
                screenX,
                Minecraft.getInstance().getMainWindow().getFramebufferHeight() - screenY,
                1, 1,
                GL11.GL_RGB,
                GL11.GL_BYTE,
                pixelBuffer
        );

        return new java.awt.Color(
                pixelBuffer.get(0) * 2,
                pixelBuffer.get(1) * 2,
                pixelBuffer.get(2) * 2,
                1
        );
    }

    private static final float[] tempVec1 = new float[4];
    private static final float[] tempVec2 = new float[4];

    public static boolean projectToScreen(float x, float y, float z, FloatBuffer modelMatrix, FloatBuffer projectionMatrix, IntBuffer viewport, FloatBuffer screenCoords) {
        float[] inVector = tempVec1;
        float[] outVector = tempVec2;

        // Load input coordinates into the vector
        inVector[0] = x;
        inVector[1] = y;
        inVector[2] = z;
        inVector[3] = 1.0F;

        // Apply the model and projection transformations
        MathHelper.transformVector(modelMatrix, inVector, outVector);
        MathHelper.transformVector(projectionMatrix, outVector, inVector);

        // Perform perspective division if the w-component is non-zero
        if ((double) inVector[3] != 0.0) {
            inVector[3] = 1.0F / inVector[3] * 0.5F;
            inVector[0] = inVector[0] * inVector[3] + 0.5F;
            inVector[1] = inVector[1] * inVector[3] + 0.5F;
            inVector[2] = inVector[2] * inVector[3] + 0.5F;

            // Map to screen coordinates using the viewport
            screenCoords.put(0, inVector[0] * (float) viewport.get(viewport.position() + 2) + (float) viewport.get(viewport.position()));
            screenCoords.put(1, inVector[1] * (float) viewport.get(viewport.position() + 3) + (float) viewport.get(viewport.position() + 1));
            screenCoords.put(2, inVector[2]);

            return true;
        } else {
            return false;
        }
    }
}