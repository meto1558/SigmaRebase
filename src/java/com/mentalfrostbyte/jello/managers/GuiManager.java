package com.mentalfrostbyte.jello.managers;

import net.minecraft.client.Minecraft;

public class GuiManager {

    public static float scaleFactor = 1.0F;

    public GuiManager() {
        scaleFactor = (float) (Minecraft.getInstance().getMainWindow().getFramebufferHeight() / Minecraft.getInstance().getMainWindow().getHeight());
    }

}
