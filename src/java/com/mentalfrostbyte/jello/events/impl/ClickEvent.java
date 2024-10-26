package com.mentalfrostbyte.jello.events.impl;

import com.mentalfrostbyte.jello.events.CancellableEvent;

public class ClickEvent extends CancellableEvent {
    private final Button butoon;

    public ClickEvent(Button var1) {
        this.butoon = var1;
    }

    public Button getButton() {
        return this.butoon;
    }

    public enum Button {
        LEFT,
        RIGHT,
        MID;
    }
}
