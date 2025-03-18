package com.mentalfrostbyte.jello.event.impl.player.movement;

import team.sdhq.eventBus.Event;

public class EventMoveFlying extends Event {
    private float yaw;
    private double strafe, forward, friction;

    public EventMoveFlying(float yaw, double strafe, double forward, double friction) {
        this.yaw = yaw;
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public double getStrafe() {
        return strafe;
    }

    public void setStrafe(double strafe) {
        this.strafe = strafe;
    }

    public double getForward() {
        return forward;
    }

    public void setForward(double forward) {
        this.forward = forward;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }
}