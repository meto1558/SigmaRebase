package com.mentalfrostbyte.jello.util.game.player.constructor;

public class Rotation {
    public float yaw, pitch;
    public float lastYaw, lastPitch;

    public Rotation(float yaw, float pitch) {
        this.lastYaw = yaw;
        this.lastPitch = pitch;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}