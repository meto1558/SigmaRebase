package com.mentalfrostbyte.jello.util.game.render;

import com.google.gson.JsonSyntaxException;
import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.event.impl.game.network.EventReceivePacket;
import com.mentalfrostbyte.jello.event.impl.game.render.EventRender3D;
import com.mentalfrostbyte.jello.managers.GuiManager;
import com.mentalfrostbyte.jello.util.game.render.shader.SigmaBlurShader;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.network.play.server.SCloseWindowPacket;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import team.sdhq.eventBus.EventBus;
import team.sdhq.eventBus.annotations.EventTarget;
import team.sdhq.eventBus.annotations.priority.LowestPriority;

import java.io.IOException;

public class BlurEngine {
    private static final Minecraft mc = Minecraft.getInstance();

    private static ShaderGroup blurShader;
    public static Framebuffer primaryFramebuffer;
    public static Framebuffer secondaryFramebuffer;

    public static int framebufferWidth = mc.getFramebuffer().framebufferWidth;
    public static int framebufferHeight = mc.getFramebuffer().framebufferHeight;

    public static int screenWidth = 0;
    public static int screenHeight = 0;

    public void init() {
        EventBus.register(this);
    }

    public static void updateRenderBounds(int x, int y, int width, int height) {
        framebufferWidth = Math.min(x, framebufferWidth);
        framebufferHeight = Math.min(y, framebufferHeight);
        screenWidth = Math.max(x + width, screenWidth);
        screenHeight = Math.max(y + height, screenHeight);
    }

    @EventTarget
    public void onReceivePacket(EventReceivePacket event) {
        if (event.packet instanceof SCloseWindowPacket) {
            RenderUtil2.resetShaders();
        }
    }

    @EventTarget
    @LowestPriority
    public void onRender3D(EventRender3D event) {
        if (Client.getInstance().guiManager.getHqIngameBlur()
                && framebufferWidth < screenWidth
                && framebufferHeight < screenHeight) {

            if (primaryFramebuffer == null) {
                try {
                    blurShader = new ShaderGroup(
                            mc.getTextureManager(),
                            new SigmaBlurShader(),
                            mc.getFramebuffer(),
                            new ResourceLocation("jelloblur")
                    );
                    blurShader.createBindFramebuffers(
                            mc.getFramebuffer().framebufferWidth,
                            mc.getFramebuffer().framebufferHeight
                    );

                    blurShader.listShaders.get(0).getShaderManager()
                            .getShaderUniform("Radius").set(35.0F);
                    blurShader.listShaders.get(1).getShaderManager()
                            .getShaderUniform("Radius").set(35.0F);

                    primaryFramebuffer = blurShader.getFramebuffer("jello");
                    secondaryFramebuffer = blurShader.getFramebuffer("jelloswap");

                } catch (IOException | JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }

            if (primaryFramebuffer.framebufferHeight != mc.getFramebuffer().framebufferHeight
                    || primaryFramebuffer.framebufferWidth != mc.getFramebuffer().framebufferWidth) {
                blurShader.createBindFramebuffers(
                        mc.getFramebuffer().framebufferWidth,
                        mc.getFramebuffer().framebufferHeight
                );
            }

            RenderSystem.blendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );
            RenderSystem.enableBlend();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            RenderSystem.disableBlend();

            primaryFramebuffer.framebufferClear(true);
            secondaryFramebuffer.framebufferClear(true);

            RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);

            RenderSystem.matrixMode(GL11.GL_PROJECTION);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(
                    0.0,
                    (double) mc.mainWindow.getFramebufferWidth() / mc.mainWindow.getGuiScaleFactor(),
                    (double) mc.mainWindow.getFramebufferHeight() / mc.mainWindow.getGuiScaleFactor(),
                    0.0,
                    1000.0,
                    3000.0
            );

            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, 0.0F, -2000.0F);

            GL11.glScaled(
                    1.0 / mc.mainWindow.getGuiScaleFactor() * GuiManager.scaleFactor,
                    1.0 / mc.mainWindow.getGuiScaleFactor() * GuiManager.scaleFactor,
                    1.0
            );

            int blurMargin = 35;
            RenderUtil.startScissorUnscaled(
                    framebufferWidth,
                    framebufferHeight - blurMargin,
                    screenWidth,
                    screenHeight + blurMargin
            );

            blurShader.render(mc.timer.renderPartialTicks);
            RenderUtil.endScissor();

            GL11.glEnable(GL11.GL_ALPHA_TEST);

            primaryFramebuffer.bindFramebuffer(true);
            mc.getFramebuffer().bindFramebuffer(true);
        }

        framebufferWidth = mc.getFramebuffer().framebufferWidth;
        framebufferHeight = mc.getFramebuffer().framebufferHeight;
        screenWidth = 0;
        screenHeight = 0;
    }


    public static void renderFramebufferToScreen() {
        if (primaryFramebuffer != null) {
            GL11.glPushMatrix();

            // Bind and render the custom framebuffer to the screen
            primaryFramebuffer.bindFramebufferTexture();
            primaryFramebuffer.framebufferRender(
                    mc.getFramebuffer().framebufferWidth,
                    mc.getFramebuffer().framebufferHeight
            );

            GL11.glPopMatrix();

            // Clear depth buffer
            RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, Minecraft.IS_RUNNING_ON_MAC);

            // Set up orthographic projection for rendering to screen space
            RenderSystem.matrixMode(GL11.GL_PROJECTION);
            RenderSystem.loadIdentity();
            RenderSystem.ortho(
                    0.0,
                    mc.mainWindow.getFramebufferWidth() / mc.mainWindow.getGuiScaleFactor(),
                    mc.mainWindow.getFramebufferHeight() / mc.mainWindow.getGuiScaleFactor(),
                    0.0,
                    1000.0,
                    3000.0
            );

            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, 0.0F, -2000.0F);

            // Apply scaling based on GUI and custom scale factor
            GL11.glScaled(
                    1.0 / mc.mainWindow.getGuiScaleFactor() * GuiManager.scaleFactor,
                    1.0 / mc.mainWindow.getGuiScaleFactor() * GuiManager.scaleFactor,
                    1.0
            );

            // Rebind the main framebuffer
            mc.getFramebuffer().bindFramebuffer(true);
        }
    }

}
