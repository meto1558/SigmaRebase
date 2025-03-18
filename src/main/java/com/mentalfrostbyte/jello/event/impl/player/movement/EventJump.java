package com.mentalfrostbyte.jello.event.impl.player.movement;

import com.mentalfrostbyte.jello.event.CancellableEvent;
import com.mentalfrostbyte.jello.util.game.player.MovementUtil;
import net.minecraft.util.math.vector.Vector3d;

public class EventJump extends CancellableEvent {
    private float yaw;
    public Vector3d vector;
    public boolean modified;

    public EventJump(Vector3d vector, float yaw) {
        this.vector = vector;
        this.yaw = yaw;
    }

    public EventJump(float yaw) {
        this.yaw = yaw;
    }

    public boolean isModified() {
        return this.modified;
    }

    public Vector3d getVector() {
        return this.vector;
    }

    public void setY(double y) {
        this.vector.y = y;
    }

    public void setStrafeSpeed(double speed) {
        float[] var3 = MovementUtil.getDirectionArray();
        float forward = var3[1];
        float strafe = var3[2];
        float yaw = var3[0];
        if (forward == 0.0F && strafe == 0.0F) {
            this.vector.x = 0.0;
            this.vector.z = 0.0;
        }

        double cos = Math.cos(Math.toRadians(yaw));
        double sin = Math.sin(Math.toRadians(yaw));
        double x = (forward * cos + strafe * sin) * speed;
        double z = (forward * sin - strafe * cos) * speed;
        this.vector.x = x;
        this.vector.z = z;
        this.modified = true;
    }

    public void setVector(Vector3d vector) {
        this.vector = vector;
        this.modified = true;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
