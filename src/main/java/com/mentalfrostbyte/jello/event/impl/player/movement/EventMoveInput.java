package com.mentalfrostbyte.jello.event.impl.player.movement;


import team.sdhq.eventBus.Event;

public class EventMoveInput extends Event {
    private float forward, strafe;
    private boolean jumping, sneaking;
    private float sneakFactor;

    public EventMoveInput(float forward, float strafe, boolean jumping, boolean sneaking, float sneakFactor) {
        this.forward = forward;
        this.strafe = strafe;
        this.jumping = jumping;
        this.sneaking = sneaking;
        this.sneakFactor = sneakFactor;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public float getSneakFactor() {
        return sneakFactor;
    }

    public void setSneakFactor(float sneakFactor) {
        this.sneakFactor = sneakFactor;
    }
}
