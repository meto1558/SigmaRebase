package com.mentalfrostbyte.jello.util.game.player;

import com.mentalfrostbyte.jello.util.game.MinecraftUtil;

public class ServerUtil implements MinecraftUtil {
    public static boolean onCubeCraft() {
        return mc.getIntegratedServer() == null && mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP.toLowerCase().contains("cubecraft.net");
    }

    public static boolean isHypixel() {
        return mc.getIntegratedServer() == null
                && mc.getCurrentServerData() != null
                && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net");
    }
}
