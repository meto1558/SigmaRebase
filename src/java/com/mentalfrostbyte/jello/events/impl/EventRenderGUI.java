package com.mentalfrostbyte.jello.events.impl;

import com.mentalfrostbyte.jello.events.CancellableEvent;

public class EventRenderGUI extends CancellableEvent {
    public boolean isRendering;

    public EventRenderGUI(boolean isRendering) {
        this.isRendering = isRendering;
    }
}
