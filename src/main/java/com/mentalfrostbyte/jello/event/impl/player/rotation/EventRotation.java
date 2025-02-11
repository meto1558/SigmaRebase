package com.mentalfrostbyte.jello.event.impl.player.rotation;

import com.mentalfrostbyte.jello.event.CancellableEvent;

public class EventRotation extends CancellableEvent {
    public float yaw, pitch;

    public EventRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
