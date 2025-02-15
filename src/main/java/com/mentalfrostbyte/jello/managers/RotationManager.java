package com.mentalfrostbyte.jello.managers;

import com.mentalfrostbyte.jello.util.game.MinecraftUtil;
import com.mentalfrostbyte.jello.util.game.player.constructor.Rotation;

public class RotationManager implements MinecraftUtil {
    public static float yaw, pitch, prevPitch, prevYaw;
    public static boolean rotating = false;
    public static Rotation getRotations() {
        return new Rotation(yaw, pitch);
    }
}
