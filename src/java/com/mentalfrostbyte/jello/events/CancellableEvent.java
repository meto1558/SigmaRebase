package com.mentalfrostbyte.jello.events;

import team.sdhq.eventBus.Event;

public class CancellableEvent extends Event {
    public boolean cancelled;
}
