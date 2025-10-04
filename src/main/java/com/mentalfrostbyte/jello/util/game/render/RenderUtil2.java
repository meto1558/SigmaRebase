package com.mentalfrostbyte.jello.util.game.render;

import com.mentalfrostbyte.Client;
import com.mentalfrostbyte.jello.util.game.MinecraftUtil;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;

public class RenderUtil2 implements MinecraftUtil {
    public static final ResourceLocation BLUR_SHADER = new ResourceLocation("shaders/post/blur.json");

    public static void blur() {
        if (mc.getRenderViewEntity() instanceof PlayerEntity && Client.getInstance().guiManager.getGuiBlur()) {
            if (mc.gameRenderer.shaderGroup != null) {
                mc.gameRenderer.shaderGroup.close();
            }

            mc.gameRenderer.loadShader(BLUR_SHADER);
        }

        setShaderParams(20);
    }

    public static void setShaderParams(int radius) {
        if (mc.gameRenderer.shaderGroup != null) {
            mc.gameRenderer.shaderGroup.listShaders.get(0).getShaderManager().getShaderUniform("Radius").set((float) radius);
            mc.gameRenderer.shaderGroup.listShaders.get(1).getShaderManager().getShaderUniform("Radius").set((float) radius);
        }
    }

    /**
     * Resets the current shader to its default state or loads a specific shader based on the current shader index.
     * If the shader index is equal to the total number of shaders, it sets the shader group to null, effectively
     * disabling any active shaders. Otherwise, it loads the shader corresponding to the current shader index.
     */
    public static void resetShaders() {
        if (mc.gameRenderer.shaderIndex == GameRenderer.SHADER_COUNT) {
            mc.gameRenderer.shaderGroup = null;
        } else {
            mc.gameRenderer.loadShader(GameRenderer.SHADERS_TEXTURES[mc.gameRenderer.shaderIndex]);
        }
    }

    public static void setShaderParamsRounded(float radius) {
        setShaderParams(Math.round(radius * 20.0F));
    }
}
