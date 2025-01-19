package com.mentalfrostbyte.jello.event;

public interface ICancellableEvent {
    boolean setCancelled(boolean cancelled);

    boolean isCancelled();
}
