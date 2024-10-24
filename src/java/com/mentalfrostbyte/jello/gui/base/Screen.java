package com.mentalfrostbyte.jello.gui.base;

import totalcross.json.JSONObject;
import net.minecraft.client.Minecraft;

public abstract class Screen
        extends CustomGuiScreen {

    public Screen(String var1) {
        super(null, var1, 0, 0, Minecraft.getInstance().getMainWindow().getWidth(), Minecraft.getInstance().getMainWindow().getHeight());
    }

    public int method13313() {
        return 30;
    }

    @Override
    public void method13161(JSONObject var1) {
        super.method13161(var1);
        this.setWidthA(Minecraft.getInstance().getMainWindow().getWidth());
        this.setHeightA(Minecraft.getInstance().getMainWindow().getHeight());
    }

    @Override
    public void keyPressed(int var1) {
        if (var1 == Minecraft.getInstance().gameSettings.keyBindFullscreen.keyCode.getKeyCode()) {
            Minecraft.getInstance().getMainWindow().toggleFullscreen();
            Minecraft.getInstance().gameSettings.fullscreen = Minecraft.getInstance().getMainWindow().isFullscreen();
        }

        super.keyPressed(var1);
    }
}
