package com.mentalfrostbyte.jello.event.impl.world.render;

import com.mentalfrostbyte.jello.event.CancellableEvent;
import net.minecraft.entity.Entity;

public class EventRenderNameTag extends CancellableEvent {
    private final Entity entity;

    public EventRenderNameTag(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}

