package com.mentalfrostbyte.jello.event.impl.game.render;

import com.mentalfrostbyte.jello.event.CancellableEvent;

public class EventRenderInGameGui extends CancellableEvent {
    public boolean isRendering;

    public EventRenderInGameGui(boolean isRendering) {
        this.isRendering = isRendering;
    }
}
