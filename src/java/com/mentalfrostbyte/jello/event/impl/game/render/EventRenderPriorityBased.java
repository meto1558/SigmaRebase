package com.mentalfrostbyte.jello.event.impl.game.render;

import com.mentalfrostbyte.jello.event.CancellableEvent;

public class EventRenderPriorityBased extends CancellableEvent {
    private int priority = 99;

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void addPriority(int priority) {
        this.priority += priority;
    }
}
