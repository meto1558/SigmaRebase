package com.mentalfrostbyte.jello.event.impl.player.movement;

import com.mentalfrostbyte.jello.event.CancellableEvent;

import java.util.ArrayList;
import java.util.List;

public class EventUpdateWalkingPlayer extends CancellableEvent {
    public boolean pre;
    private double x;
    private double y;
    private double z;
    private boolean onGround;
    private boolean moving;
    private final List<Runnable> runnables = new ArrayList<>();

    public EventUpdateWalkingPlayer(double x, double y, double z, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.onGround = onGround;
        this.pre = true;
        this.moving = false;
    }

    public void postUpdate() {
        this.pre = false;
    }

    public boolean isMoving() {
        return this.moving;
    }

    public void setMoving(boolean moving) {
        this.moving = moving;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public boolean isPre() {
        return this.pre;
    }

    public void attackPost(Runnable runnable) {
        this.runnables.add(runnable);
    }

    public List<Runnable> getRunnableList() {
        return this.runnables;
    }
}
