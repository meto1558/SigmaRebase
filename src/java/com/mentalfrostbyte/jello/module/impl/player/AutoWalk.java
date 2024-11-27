package com.mentalfrostbyte.jello.module.impl.player;

import com.mentalfrostbyte.jello.event.EventTarget;
import com.mentalfrostbyte.jello.event.impl.TickEvent;
import com.mentalfrostbyte.jello.module.Module;
import com.mentalfrostbyte.jello.module.ModuleCategory;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class AutoWalk extends Module {
    public AutoWalk() {
        super(ModuleCategory.PLAYER, "AutoWalk", "Automatically walks forward");
    }

    @EventTarget
    private void onTickEvent(TickEvent event) {
        if (this.isEnabled()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true);
        }
    }

    @Override
    public void onDisable() {
        boolean isKeyPressed = GLFW.glfwGetKey(mc.getMainWindow().getHandle(), mc.gameSettings.keyBindForward.keyCode.getKeyCode()) == GLFW.GLFW_PRESS;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, isKeyPressed);
    }
}
