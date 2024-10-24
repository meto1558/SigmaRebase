package com.mentalfrostbyte.jello.events.impl;

import com.mentalfrostbyte.jello.events.CancellableEvent;

public class EventRenderGUI extends CancellableEvent {
    public boolean field21522;

    public EventRenderGUI(boolean var1) {
        this.field21522 = var1;
    }

    public boolean method13939() {
        return this.field21522;
    }
}
