package com.mentalfrostbyte.jello.event;

import team.sdhq.eventBus.Event;

public class CancellableEvent extends Event {

    public boolean cancelled;

    public boolean setCancelled(boolean cancelled) {
        return this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }
}
