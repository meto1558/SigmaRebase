package com.mentalfrostbyte.jello.module.impl.misc;

import com.mentalfrostbyte.jello.event.impl.EventRender;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import team.sdhq.eventBus.annotations.EventTarget;

public class GameIdler extends Module {
    public GameIdler() {
        super(ModuleCategory.MISC, "GameIdler", "Lowers your fps when the game is idle to increase performance");
    }

    @EventTarget
    public void method16941(EventRender var1) {
        if (this.isEnabled()) {
            boolean var4 = GLFW.glfwGetWindowAttrib(mc.getMainWindow().getHandle(), 131073) == 1;
            if (var4) {
                Minecraft.getInstance().getMainWindow().setFramerateLimit(mc.gameSettings.framerateLimit);
            } else {
                Minecraft.getInstance().getMainWindow().setFramerateLimit(5);
            }
        }
    }

    @Override
    public void onDisable() {
        Minecraft.getInstance().getMainWindow().setFramerateLimit(mc.gameSettings.framerateLimit);
    }
}
