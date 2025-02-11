package com.mentalfrostbyte.jello.util.game.player.constructor;

import net.minecraft.entity.Entity;

public class Rotation {
    public float yaw, pitch;
    public float lastYaw, lastPitch;

    public Rotation(float yaw, float pitch) {
        this.lastYaw = yaw;
        this.lastPitch = pitch;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    public Rotation(float yaw, float pitch, float lastYaw, float lastPitch) {
        this.lastYaw = lastYaw;
        this.lastPitch = lastPitch;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    public static Rotation of(Entity entity) {
        return new Rotation(entity.rotationYaw, entity.rotationPitch, entity.prevRotationYaw, entity.prevRotationPitch);
    }
}