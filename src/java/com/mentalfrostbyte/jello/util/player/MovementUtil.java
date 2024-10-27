package com.mentalfrostbyte.jello.util.player;

import net.minecraft.client.Minecraft;

public class MovementUtil {
    protected static Minecraft mc = Minecraft.getInstance();

    public static boolean isMoving() {
        boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean right = mc.gameSettings.keyBindRight.isKeyDown();
        boolean back = mc.gameSettings.keyBindBack.isKeyDown();
        return forward || left || right || back;
    }
}
