package com.mentalfrostbyte.jello.events.impl;

import com.mentalfrostbyte.jello.events.CancellableEvent;

public class EventFOV extends CancellableEvent {
    public float fovModifier;

    public EventFOV(float fovModifier) {
        this.fovModifier = fovModifier;
    }
}
