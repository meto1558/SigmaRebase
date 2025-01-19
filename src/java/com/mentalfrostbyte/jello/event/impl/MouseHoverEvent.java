package com.mentalfrostbyte.jello.event.impl;

import com.mentalfrostbyte.jello.event.CancellableEvent;

public class MouseHoverEvent extends CancellableEvent {
    private final int button;

    public MouseHoverEvent(int button) {
        this.button = button;
    }

    public int getMouseButton() {
        return this.button;
    }
}
